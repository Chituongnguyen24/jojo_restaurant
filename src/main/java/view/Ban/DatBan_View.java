package view.Ban;

// DAO cần thiết (Chỉ để tải dữ liệu hiển thị ban đầu)
import dao.Ban_DAO;
import dao.DatBan_DAO;
import entity.Ban;
import entity.KhachHang; // Needed for PhieuDatBan
import entity.NhanVien; // Needed for PhieuDatBan
import entity.PhieuDatBan;
import enums.TrangThaiBan;
import enums.LoaiBan;

// Import cho Graphics
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
// Import cho JTable và Filtering
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
// Import khác
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent; // For search listener
import javax.swing.event.DocumentListener; // For search listener
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime; // Needed for PhieuDatBan
import java.time.format.DateTimeFormatter; // Thêm import
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Vector; // For table data
import java.util.function.Consumer;

public class DatBan_View extends JPanel {

    // --- DAOs ---
    private Ban_DAO banDAO;
    private DatBan_DAO datBanDAO;

    // --- Dữ liệu ---
    private Map<String, List<Ban>> danhSachBanTheoKhuVuc;
    private List<String> tenKhuVuc;
    private String khuVucHienTai;
    private List<PhieuDatBan> danhSachPhieuDatDangHoatDong;

    private Map<String, String> areaImagePaths = new LinkedHashMap<String, String>() {{
        put("Sân thượng", "images/icon/bannho.png");
        put("Sân vườn", "images/icon/thongthuong.png");
        put("Tầng 2", "images/icon/bannho.png");
        put("Tầng trệt", "images/icon/thongthuong.png");
        put("Phòng VIP", "images/icon/vip.png");
    }};
    private static final String DEFAULT_TABLE_IMAGE = "images/icon/bansanthuong.png";
    private Map<String, ImageIcon> areaIcons;
    
    // --- Giao diện ---
    private WrappingFlowPanel pnlLuoiBan; // Panel lưới bàn (Center)
    private JTable tblDanhSachDatBan; // Bảng danh sách đặt bàn (West)
    private DefaultTableModel modelDanhSachDatBan;
    private JTextField txtTimSDT;
    private TableRowSorter<DefaultTableModel> boLocSapXep;
    private Map<String, JButton> cacNutChonKhuVuc; // Nút chọn khu vực (East)
    private JPanel pnlChuaNutKhuVuc;
    private JPanel pnlSidebarPhai; // Sidebar chọn khu vực (East)

    // === Các hằng số Màu sắc và Font (Giữ nguyên) ===
    private static final Color MAU_NEN = new Color(245, 245, 240); // Nền chính
    private static final Color MAU_TRANG = Color.WHITE; // Nền panel, thẻ bàn
    private static final Color MAU_CHU_CHINH = new Color(60, 60, 60);
    private static final Color MAU_CHU_PHU = new Color(120, 120, 120);
    private static final Color MAU_VIEN = new Color(230, 230, 230); // Viền chung
    private static final Color MAU_CAM_CHINH = new Color(255, 152, 0); // Nút sidebar active
    private static final Color MAU_CAM_DAM = new Color(220, 120, 0); // Badge sidebar active
    private static final Color MAU_NHAN_MACDINH = new Color(76, 175, 80); // Badge sidebar inactive
    // Màu trạng thái
    private static final Color MAU_TRANGTHAI_TRONG = TrangThaiBan.TRONG.getColor();
    private static final Color MAU_TRANGTHAI_CO_KHACH = TrangThaiBan.CO_KHACH.getColor(); // Đỏ (Đang phục vụ)
    private static final Color MAU_TRANGTHAI_DA_DAT = TrangThaiBan.DA_DAT.getColor();     // Vàng
    private static final Color MAU_BAO_TRI = Color.GRAY; // Màu cho Bảo trì (nếu có)
    private static final Color MAU_VIEN_SANG = new Color(233, 236, 239); // Viền legend

    private static final Font FONT_TIEUDE_CHINH = new Font("Segoe UI", Font.BOLD, 16); // Header sidebar
    private static final Font FONT_NUT = new Font("Segoe UI", Font.PLAIN, 14); // Nút sidebar
    private static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 12); // Chữ phụ, legend
    private static final Font FONT_TIEUDE_BANG = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_O_BANG = new Font("Segoe UI", Font.PLAIN, 12);
    // =============================

    public DatBan_View() {
        banDAO = new Ban_DAO();
        datBanDAO = new DatBan_DAO();
        danhSachBanTheoKhuVuc = new LinkedHashMap<>();
        tenKhuVuc = new ArrayList<>();
        cacNutChonKhuVuc = new LinkedHashMap<>();
        danhSachPhieuDatDangHoatDong = new ArrayList<>();
        areaIcons = new LinkedHashMap<>(); // <<< THÊM: Khởi tạo map icons

        taiTatCaIconKhuVuc(); // <<< THÊM: Gọi hàm tải icon

        thietLapGiaoDien();
        taiDuLieuVaHienThiBanDau();
    }
    
    private void taiTatCaIconKhuVuc() {
        for (Map.Entry<String, String> entry : areaImagePaths.entrySet()) {
            ImageIcon icon = taiImageIcon(entry.getValue());
            if (icon != null) {
                areaIcons.put(entry.getKey(), icon);
            }
        }
        ImageIcon defaultIcon = taiImageIcon(DEFAULT_TABLE_IMAGE);
        if (defaultIcon != null) {
            areaIcons.put("DEFAULT", defaultIcon);
        }
    }

    private ImageIcon taiImageIcon(String imagePath) {
        try {
            ImageIcon o = new ImageIcon(imagePath);
            if (o.getIconWidth() <= 0) { // Check if image loaded successfully
                System.err.println("Warning: Icon not found or invalid: " + imagePath);
                return null;
            }
            // Scale icon to a fixed size (e.g., 80x80)
            Image s = o.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            return new ImageIcon(s);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + imagePath);
            // e.printStackTrace(); // Optional: print stack trace for debugging
            return null;
        }
    }

    private ImageIcon layIconChoKhuVucHienTai() {
        ImageIcon icon = areaIcons.get(khuVucHienTai); // khuVucHienTai needs to be set correctly
        // Fallback if area icon not found or khuVucHienTai is null
        return icon != null ? icon : areaIcons.getOrDefault("DEFAULT", null);
    }

    private void thietLapGiaoDien() {
        setLayout(new BorderLayout(10, 10)); // Khoảng cách giữa các component
        setBackground(MAU_NEN);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding cho toàn bộ view

        // 1. Chú thích (NORTH)
        JPanel pnlChuThich = taoPanelChuThich();
        add(pnlChuThich, BorderLayout.NORTH);

        // 2. Danh sách đặt bàn (WEST)
        JPanel pnlDanhSach = taoPanelDanhSachDatBan();
        add(pnlDanhSach, BorderLayout.WEST);

        // 3. Lưới bàn (CENTER)
        pnlLuoiBan = new WrappingFlowPanel(); // <<< Thay bằng dòng này
        pnlLuoiBan.setBackground(MAU_TRANG);
        pnlLuoiBan.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollLuoiBan = new JScrollPane(pnlLuoiBan);
        scrollLuoiBan.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        add(scrollLuoiBan, BorderLayout.CENTER);

        // 4. Sidebar chọn khu vực (EAST)
        pnlSidebarPhai = taoSidebarChonKhuVuc();
        add(pnlSidebarPhai, BorderLayout.EAST);
    }

    private void taiDuLieuVaHienThiBanDau() {
        taiDuLieuKhuVuc(); // Load khu vực
        
        // SỬA LẠI HÀM NÀY:
        // Khởi động danh sách đặt bàn (trống)
        taiDuLieuDatBan(); 
        
        capNhatNoiDungSidebar(); 
        capNhatLuaChonSidebar(); 
        capNhatHienThiLuoiBan(); 
    }

    // --- Các hàm tải dữ liệu ---
    private void taiDuLieuKhuVuc() {
        Map<String, Integer> sl = banDAO.getSoBanTheoKhuVuc(); tenKhuVuc = new ArrayList<>(sl.keySet());
        if (khuVucHienTai == null && !tenKhuVuc.isEmpty()) { 
        	khuVucHienTai = tenKhuVuc.get(0);
        }
        else if (tenKhuVuc.isEmpty()) {
        	khuVucHienTai = "Không có dữ liệu";
        }
        danhSachBanTheoKhuVuc.clear(); 
        Map<String, String> kvMap = banDAO.getDanhSachKhuVuc();
        for (String n : tenKhuVuc) {
            String m = kvMap.entrySet().stream().filter(entry -> entry.getValue().equals(n)).map(Map.Entry::getKey).findFirst().orElse(null);
            if (m != null) { 
            	danhSachBanTheoKhuVuc.put(n, banDAO.getBanTheoKhuVuc(m)); 
            }
        }
    }

    private void taiDuLieuDatBan() {
        capNhatBangDatBan(); // Cập nhật JTable
    }

    // --- Các hàm tạo thành phần giao diện ---
    private JPanel taoSidebarChonKhuVuc() {
        // (Giống các phiên bản trước)
        JPanel sidebar = new JPanel(); sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS)); sidebar.setBackground(MAU_TRANG); sidebar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true), BorderFactory.createEmptyBorder(25, 20, 25, 20))); sidebar.setPreferredSize(new Dimension(280, 0));
        JLabel aL = new JLabel("Khu vực"); aL.setFont(FONT_TIEUDE_CHINH); aL.setForeground(MAU_CHU_CHINH); aL.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel aSL = new JLabel("Chọn khu vực"); aSL.setFont(FONT_CHU); aSL.setForeground(MAU_CHU_PHU); aSL.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(aL); sidebar.add(Box.createVerticalStrut(5)); sidebar.add(aSL); sidebar.add(Box.createVerticalStrut(20));
        pnlChuaNutKhuVuc = new JPanel(); pnlChuaNutKhuVuc.setLayout(new BoxLayout(pnlChuaNutKhuVuc, BoxLayout.Y_AXIS)); pnlChuaNutKhuVuc.setOpaque(false); sidebar.add(pnlChuaNutKhuVuc); sidebar.add(Box.createVerticalGlue()); return sidebar;
    }

     private JPanel taoPanelDanhSachDatBan() {
        // (Giống phiên bản trước, nhưng bỏ chú thích ở đây)
        JPanel panel = new JPanel(new BorderLayout(0, 10)); // Tăng khoảng cách
        panel.setBackground(MAU_TRANG);
        panel.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        panel.setPreferredSize(new Dimension(800, 0)); // Đặt chiều rộng cố định cho panel trái

        // Tiêu đề
        JLabel titleLabel = new JLabel("Danh sách đặt bàn");
        titleLabel.setFont(FONT_TIEUDE_CHINH);
        titleLabel.setForeground(MAU_CHU_CHINH);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Panel Nội dung (Tìm kiếm + Bảng)
        JPanel pnlContent = new JPanel(new BorderLayout(0, 5));
        pnlContent.setBackground(MAU_TRANG);
        pnlContent.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); // Padding

        // Panel Tìm kiếm
        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlTimKiem.setBackground(MAU_TRANG);
        JLabel lblTim = new JLabel("Tìm SĐT:"); lblTim.setFont(FONT_CHU);
        txtTimSDT = new JTextField(15); txtTimSDT.setFont(FONT_CHU);
        pnlTimKiem.add(lblTim); pnlTimKiem.add(txtTimSDT);
        pnlContent.add(pnlTimKiem, BorderLayout.NORTH);

        // Panel Bảng
        modelDanhSachDatBan = new DefaultTableModel( new String[]{"Mã phiếu", "Số bàn", "SĐT", "Thời gian khách tới"}, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        tblDanhSachDatBan = new JTable(modelDanhSachDatBan); tblDanhSachDatBan.setFont(FONT_O_BANG); tblDanhSachDatBan.setRowHeight(25); tblDanhSachDatBan.getTableHeader().setFont(FONT_TIEUDE_BANG); tblDanhSachDatBan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        boLocSapXep = new TableRowSorter<>(modelDanhSachDatBan); tblDanhSachDatBan.setRowSorter(boLocSapXep);
        // *** BỎ LISTENER TÌM KIẾM TẠM THỜI ***
        // txtTimSDT.getDocument().addDocumentListener(new DocumentListener() { ... });
        JScrollPane sP = new JScrollPane(tblDanhSachDatBan);
        pnlContent.add(sP, BorderLayout.CENTER);

        panel.add(pnlContent, BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoPanelChuThich() {
        // (Giống phiên bản trước)
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5)); p.setBackground(MAU_NEN); p.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10)); // Bỏ viền dưới, padding
        p.add(taoMucChuThich("Đang phục vụ", MAU_TRANGTHAI_CO_KHACH)); p.add(taoMucChuThich("Đã đặt", MAU_TRANGTHAI_DA_DAT)); p.add(taoMucChuThich("Trống", MAU_TRANGTHAI_TRONG));
        // Thêm Bảo trì nếu cần
        // p.add(taoMucChuThich("Bảo trì", MAU_BAO_TRI));
        return p;
    }

    // --- Các hàm cập nhật nội dung giao diện ---
    private void capNhatNoiDungSidebar() {
        // (Giữ nguyên)
         pnlChuaNutKhuVuc.removeAll(); cacNutChonKhuVuc.clear(); Map<String, Integer> sl = banDAO.getSoBanTheoKhuVuc(); for (String n : tenKhuVuc) { int c = sl.getOrDefault(n, 0); JButton fBtn = taoNutChonKhuVuc(n, c + " bàn"); fBtn.addActionListener(e -> chuyenKhuVuc(n)); cacNutChonKhuVuc.put(n, fBtn); pnlChuaNutKhuVuc.add(fBtn); pnlChuaNutKhuVuc.add(Box.createVerticalStrut(10)); } pnlChuaNutKhuVuc.revalidate(); pnlChuaNutKhuVuc.repaint();
     }

    private void chuyenKhuVuc(String tenKV) {
        // (Giữ nguyên)
         khuVucHienTai = tenKV; capNhatLuaChonSidebar(); capNhatHienThiLuoiBan();
     }

    private void capNhatLuaChonSidebar() {
        // (Giữ nguyên)
         for (Map.Entry<String, JButton> entry : cacNutChonKhuVuc.entrySet()) { dinhDangNutKhuVuc(entry.getValue(), entry.getKey().equals(khuVucHienTai)); }
     }

    private JButton taoNutChonKhuVuc(String n, String c) {
        // (Giữ nguyên)
         JButton btn = new JButton(); btn.setLayout(new BorderLayout(10, 0)); btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45)); btn.setPreferredSize(new Dimension(240, 45)); btn.setFocusPainted(false); btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); JLabel nL = new JLabel(n); nL.setFont(FONT_NUT); JLabel cL = new JLabel(c); cL.setFont(new Font("Segoe UI", Font.BOLD, 12)); cL.setOpaque(true); cL.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10)); cL.setHorizontalAlignment(SwingConstants.CENTER); btn.add(nL, BorderLayout.WEST); btn.add(cL, BorderLayout.EAST); dinhDangNutKhuVuc(btn, false); return btn;
     }

    private void dinhDangNutKhuVuc(JButton btn, boolean sel) {
        // (Giữ nguyên)
         if (btn.getComponentCount() < 2) return; JLabel nL = (JLabel) btn.getComponent(0); JLabel cL = (JLabel) btn.getComponent(1); Border b; if (sel) { btn.setBackground(MAU_CAM_CHINH); b = BorderFactory.createLineBorder(MAU_CAM_CHINH, 1, true); nL.setForeground(MAU_TRANG); cL.setForeground(MAU_TRANG); cL.setBackground(MAU_CAM_DAM); } else { btn.setBackground(MAU_TRANG); b = BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true); nL.setForeground(MAU_CHU_CHINH); cL.setForeground(MAU_TRANG); cL.setBackground(MAU_NHAN_MACDINH); } btn.setBorder(BorderFactory.createCompoundBorder(b, BorderFactory.createEmptyBorder(10, 15, 10, 15)));
     }

    /**
     * Cập nhật lưới bàn (Hiển thị tất cả bàn)
     */
    void capNhatHienThiLuoiBan() {
        pnlLuoiBan.removeAll();

        List<Ban> dsBanTrongKV = danhSachBanTheoKhuVuc.get(khuVucHienTai);

        if (dsBanTrongKV != null && !dsBanTrongKV.isEmpty()) {
            for (Ban ban : dsBanTrongKV) {
                // Tạo thẻ bàn như cũ
                JPanel card = taoTheBan(ban);

                // KHÔNG CẦN WRAPPER NỮA KHI DÙNG FLOWLAYOUT
                // JPanel wrapper = new JPanel(new BorderLayout());
                // wrapper.setOpaque(false);
                // wrapper.add(card, BorderLayout.NORTH);

                // Thêm trực tiếp thẻ bàn vào FlowLayout
                pnlLuoiBan.add(card);
            }
        } else {
            // Phần xử lý khi không có bàn giữ nguyên
            JLabel lbl = new JLabel("Khu vực này chưa có bàn.");
            lbl.setFont(FONT_CHU);
            lbl.setForeground(MAU_CHU_PHU);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            // FlowLayout cũng hoạt động tốt cho trường hợp này
            pnlLuoiBan.add(lbl);
        }
        pnlLuoiBan.revalidate();
        pnlLuoiBan.repaint();
    }

    private void capNhatBangDatBan() {
        modelDanhSachDatBan.setRowCount(0);
        if (danhSachPhieuDatDangHoatDong != null) {
            for (PhieuDatBan p : danhSachPhieuDatDangHoatDong) {
                
                // Sửa dòng 1: Kiểm tra cả p.getBan() và p.getBan().getMaBan()
                String mb = (p.getBan() != null && p.getBan().getMaBan() != null) 
                            ? p.getBan().getMaBan().trim() : "N/A";
                
                // Sửa dòng 2: Kiểm tra cả p.getKhachHang() và p.getKhachHang().getSdt()
                String s = (p.getKhachHang() != null && p.getKhachHang().getSdt() != null) 
                            ? p.getKhachHang().getSdt().trim() : "N/A";
                
                String t = p.getThoiGianDatFormatted();
                
                // Kiểm tra p.getMaPhieu() để đảm bảo an toàn
                String maPhieu = (p.getMaPhieu() != null) ? p.getMaPhieu().trim() : "N/A";
                
                modelDanhSachDatBan.addRow(new Object[]{maPhieu, mb, s, t});
            }
        }
        locBang();
    }

    private void locBang() {
        // (Giữ nguyên)
         String sT = txtTimSDT.getText().trim(); if (sT.isEmpty()) { boLocSapXep.setRowFilter(null); } else { try { boLocSapXep.setRowFilter(RowFilter.regexFilter("(?i)" + sT, 2)); } catch (java.util.regex.PatternSyntaxException e) { boLocSapXep.setRowFilter(null); } }
     }

    /**
     * Tạo thẻ bàn (Chỉ hiển thị, không có sự kiện)
     */
    private JPanel taoTheBan(Ban ban) {
        // Sử dụng RoundedPanel hiện có, nhưng tùy chỉnh lại
        RoundedPanel card = new RoundedPanel(null, 18); // Layout null để dùng BoxLayout, bo góc 18
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS)); // Dùng BoxLayout
        card.setBackground(MAU_TRANG); // Nền luôn trắng

        // Xác định màu viền dựa trên trạng thái
        Color mauVienTrangThai;
        String trangThaiText;
        Color trangThaiTextColor;

        if (ban.getTrangThai() == TrangThaiBan.TRONG) {
            mauVienTrangThai = MAU_TRANGTHAI_TRONG; // Xanh lá
            trangThaiText = "Trống";
            trangThaiTextColor = mauVienTrangThai.darker();
        } else if (ban.getTrangThai() == TrangThaiBan.CO_KHACH) {
            mauVienTrangThai = MAU_TRANGTHAI_CO_KHACH; // Đỏ
            trangThaiText = "Có khách";
            trangThaiTextColor = mauVienTrangThai.darker();
        } else { // DA_DAT
            mauVienTrangThai = MAU_TRANGTHAI_DA_DAT; // Vàng
            trangThaiText = "Đã đặt";
            trangThaiTextColor = mauVienTrangThai.darker();
        }
        card.setBorderColor(mauVienTrangThai); // Set màu viền cho RoundedPanel
        card.setBorderThickness(2); // Làm viền dày hơn (Thêm hàm này vào RoundedPanel - xem Bước 3)

        card.setBorder(BorderFactory.createEmptyBorder(20, 18, 20, 18)); // Padding
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.CENTER_ALIGNMENT); // Căn giữa card nếu layout cha cho phép

        // Lấy và thêm Icon
        ImageIcon currentIcon = layIconChoKhuVucHienTai();
        JLabel lblIcon; // Declare the label first

        if (currentIcon != null) {
            // If icon exists, use the JLabel(Icon) constructor
            lblIcon = new JLabel(currentIcon);
        } else {
            // If icon is null, use the JLabel(String) constructor
            lblIcon = new JLabel("No Icon");
            // Set preferred size and maybe a border for the placeholder text
            lblIcon.setPreferredSize(new Dimension(80, 80));
            lblIcon.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); // Optional: add border
        }

        // Set alignment AFTER creating the label
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tên bàn và loại bàn
        String loaiBanTen = ban.getLoaiBan().getTenHienThi();
        if (loaiBanTen.equalsIgnoreCase("Bàn VIP")) loaiBanTen = "VIP";
        JLabel lblName = new JLabel(String.format("%s (%s)", ban.getMaBan().trim(), loaiBanTen));
        lblName.setFont(FONT_TIEUDE_CHINH); // Dùng font từ Ban_View
        lblName.setForeground(MAU_CHU_CHINH);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Số chỗ
        JLabel lblCapacity = new JLabel(ban.getSoCho() + " chỗ");
        lblCapacity.setFont(FONT_CHU); // Dùng font từ Ban_View
        lblCapacity.setForeground(MAU_CHU_PHU);
        lblCapacity.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Trạng thái (Text)
        JLabel lblStatusText = new JLabel(trangThaiText);
        lblStatusText.setFont(new Font("Segoe UI", Font.ITALIC, 11)); // Font trạng thái
        lblStatusText.setForeground(trangThaiTextColor); // Màu chữ trạng thái
        lblStatusText.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Thêm các thành phần vào card
        card.add(lblIcon);
        card.add(Box.createVerticalStrut(12));
        card.add(lblName);
        card.add(Box.createVerticalStrut(6));
        card.add(lblCapacity);
        card.add(Box.createVerticalStrut(8));
        card.add(lblStatusText);

        // *** GIỮ NGUYÊN LOGIC CLICK CỦA DatBan_View ***
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 251, 252)); // Hiệu ứng hover nhẹ
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(MAU_TRANG); // Trở về nền trắng
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                TrangThaiBan trangThai = ban.getTrangThai();
                if (trangThai == TrangThaiBan.TRONG) {
                    moDialogDatBan(ban); // Mở dialog đặt bàn
                } else if (trangThai == TrangThaiBan.DA_DAT) {
                    xuLyBanDaDat(ban); // Hỏi nhận bàn/hủy
                } else if (trangThai == TrangThaiBan.CO_KHACH) {
                    xuLyBanCoKhach(ban); // Hỏi trả bàn
                } else {
                    JOptionPane.showMessageDialog(pnlLuoiBan, "Bàn này hiện không khả dụng.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        return card;
    }
    
    
    private void xuLyBanDaDat(Ban ban) {
        String[] options = {"Sử dụng bàn", "Hủy đặt", "Đóng"};
        int result = JOptionPane.showOptionDialog(
            pnlLuoiBan,
            "Khách đã đến nhận bàn " + ban.getMaBan().trim() + "?",
            "Xác nhận nhận bàn",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (result == 0) { // 0. Sử dụng bàn
            // Chuyển bàn sang trạng thái "Có khách"
            ban.setTrangThai(TrangThaiBan.CO_KHACH);
            boolean success = banDAO.capNhatBan(ban);
            if (success) {
                capNhatHienThiLuoiBan(); // Chỉ cần cập nhật lưới bàn
            } else {
                JOptionPane.showMessageDialog(pnlLuoiBan, "Lỗi khi cập nhật trạng thái bàn!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                ban.setTrangThai(TrangThaiBan.DA_DAT); // Rollback
            }

        } else if (result == 1) { // 1. Hủy đặt
            int confirmHuy = JOptionPane.showConfirmDialog(
                pnlLuoiBan, 
                "Bạn có chắc chắn muốn HỦY đặt bàn này không?", 
                "Xác nhận hủy", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirmHuy == JOptionPane.YES_OPTION) {
                // Lấy phiếu đặt bàn tương ứng
                PhieuDatBan pdb = datBanDAO.getPhieuByBan(ban.getMaBan().trim());
                
                // Xóa phiếu đặt (bao gồm cả chi tiết)
                if (pdb != null) {
                    // 1. Xóa khỏi CSDL
                    datBanDAO.deletePhieuDatBan(pdb.getMaPhieu());
                    
                    // 2. <<< THÊM MỚI: Xóa khỏi danh sách session >>>
                    // Dùng .trim() để khớp với mã phiếu sạch trong danh sách
                    final String maPhieuCanXoa = pdb.getMaPhieu().trim();
                    danhSachPhieuDatDangHoatDong.removeIf(
                        p -> p.getMaPhieu().equals(maPhieuCanXoa)
                    );
                }
                
                // Chuyển bàn về "Trống"
                ban.setTrangThai(TrangThaiBan.TRONG);
                banDAO.capNhatBan(ban);
                
                // Tải lại cả lưới bàn và danh sách đặt
                taiDuLieuKhuVuc(); // Tải lại trạng thái bàn
                capNhatBangDatBan(); // Cập nhật JTable (vẽ lại list)
                capNhatHienThiLuoiBan(); // Cập nhật lưới bàn
                JOptionPane.showMessageDialog(pnlLuoiBan, "Đã hủy đặt bàn " + ban.getMaBan().trim());
            }
        }
        // result == 2 (Đóng) hoặc các trường hợp khác: Không làm gì
    }

    /**
     * Xử lý khi click vào bàn "Có Khách" (Step 3)
     */
    private void xuLyBanCoKhach(Ban ban) {
        String[] options = {"Xác nhận trả bàn", "Hủy"};
        int result = JOptionPane.showOptionDialog(
            pnlLuoiBan,
            "Khách muốn trả bàn " + ban.getMaBan().trim() + "?\n(Thao tác này sẽ hoàn tất phiếu đặt bàn)",
            "Xác nhận trả bàn",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (result == 0) { // 0. Xác nhận trả bàn
            // Tìm phiếu đặt bàn (nếu có)
            PhieuDatBan pdb = datBanDAO.getPhieuByBan(ban.getMaBan().trim());
            
            if (pdb != null) {
            	final String maPhieuCanXoa = pdb.getMaPhieu().trim();
                danhSachPhieuDatDangHoatDong.removeIf(
                    p -> p.getMaPhieu().equals(maPhieuCanXoa)
                );
                
                System.out.println("Đã ẩn phiếu đặt khỏi danh sách: " + maPhieuCanXoa);
            }
        
            // Chuyển bàn về "Trống"
            ban.setTrangThai(TrangThaiBan.TRONG);
            boolean success = banDAO.capNhatBan(ban);
            
            if (success) {
                // Tải lại cả lưới bàn và danh sách (vì phiếu đã bị ẩn)
                taiDuLieuVaHienThiBanDau();
            } else {
                 JOptionPane.showMessageDialog(pnlLuoiBan, "Lỗi khi cập nhật trạng thái bàn!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                 ban.setTrangThai(TrangThaiBan.CO_KHACH); // Rollback
            }
            
            // GHI CHÚ:
            // Trong hệ thống thực tế, đây là lúc bạn sẽ chuyển sang
            // màn hình TÍNH TIỀN và tạo HÓA ĐƠN (thay vì xóa phiếu đặt).
        }
        // result == 1 (Hủy): Không làm gì
    }
   /**
    * Phương thức mới: Mở dialog đặt bàn và xử lý callback
    */
   private void moDialogDatBan(Ban ban) {
       JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

       // Callback để tải lại view khi đặt bàn thành công
       Consumer<PhieuDatBan> refreshCallback = (phieuMoi) -> {
           System.out.println("Callback: Đã nhận phiếu mới " + phieuMoi.getMaPhieu());

           // 1. Thêm phiếu mới vào danh sách của phiên này
           danhSachPhieuDatDangHoatDong.add(phieuMoi);

           // 2. Tải lại trạng thái bàn (vì bàn đã bị đặt)
           taiDuLieuKhuVuc();

           // 3. Cập nhật cả 2 phần UI
           capNhatBangDatBan(); // Cập nhật JTable (hiện phiếu mới)
           capNhatHienThiLuoiBan(); // Cập nhật lưới bàn (đổi màu bàn)
       };

       DatBan_Dialog dialog = new DatBan_Dialog(parentFrame, ban, refreshCallback);
       dialog.setVisible(true);
   }
    // (taoMucChuThich giữ nguyên)
    private JPanel taoMucChuThich(String text, Color color) { JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); item.setOpaque(false); JPanel hopMau = new JPanel() { @Override protected void paintComponent(Graphics g) { Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(getBackground()); g2.fillOval(0, 0, getWidth(), getHeight()); g2.setColor(getBackground().darker()); g2.drawOval(0, 0, getWidth()-1, getHeight()-1); g2.dispose(); } }; hopMau.setBackground(color); hopMau.setPreferredSize(new Dimension(18, 18)); hopMau.setOpaque(false); JLabel label = new JLabel(text); label.setFont(FONT_CHU); label.setForeground(MAU_CHU_PHU); item.add(hopMau); item.add(label); return item; }


    // =================================================================
    // === CÁC LỚP NỘI BỘ ===
    // =================================================================
    /** Lớp Panel bo tròn (Đổi tên thành RoundedPanel) */
    private class RoundedPanel extends JPanel {
        private int cornerRadius = 18; // Độ bo góc mặc định
        private Color borderColor = MAU_VIEN; // Màu viền mặc định
        private int borderThickness = 1; // Độ dày viền mặc định

        // Constructor mới cho phép set bo góc
        public RoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            this.cornerRadius = radius;
            setOpaque(false);
        }

        // Giữ lại constructor cũ nếu cần
        public RoundedPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }


        // Hàm set màu viền (giữ nguyên)
        public void setBorderColor(Color color) {
            this.borderColor = color;
            repaint(); // Vẽ lại khi đổi màu
        }

        // === HÀM MỚI: Set độ dày viền ===
        public void setBorderThickness(int thickness) {
            this.borderThickness = Math.max(1, thickness); // Đảm bảo độ dày ít nhất là 1
            repaint(); // Vẽ lại khi đổi độ dày
        }

        @Override
        protected void paintComponent(Graphics g) {
            // Chỉ vẽ nền
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            g2.dispose();
            super.paintComponent(g); // Gọi super để các component con được vẽ
        }

        @Override
        protected void paintBorder(Graphics g) {
            // Vẽ viền bo tròn
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.borderColor);
            // Đặt độ dày viền
            g2.setStroke(new BasicStroke(this.borderThickness));
            // Vẽ hình chữ nhật bo tròn (vẽ bên trong 1 chút để stroke không bị cắt)
            // Tính toán offset dựa trên độ dày
            int offset = borderThickness / 2;
            g2.draw(new RoundRectangle2D.Double(
                offset, // x + offset
                offset, // y + offset
                getWidth() - borderThickness, // width - thickness
                getHeight() - borderThickness, // height - thickness
                cornerRadius, cornerRadius
            ));
            g2.dispose();
        }
    }
 // === LỚP NỘI BỘ MỚI: Panel tự động xuống dòng ===
    private class WrappingFlowPanel extends JPanel implements Scrollable {

        private FlowLayout layout;

        public WrappingFlowPanel() {
            // Sử dụng FlowLayout căn trái với khoảng cách
            layout = new FlowLayout(FlowLayout.LEFT, 10, 10);
            setLayout(layout);
        }

        // --- Triển khai Interface Scrollable ---

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize(); // Kích thước viewport ưu tiên = kích thước panel
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            // Tốc độ cuộn khi nhấn mũi tên (ví dụ: 16 pixels)
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            // Tốc độ cuộn khi nhấn page up/down (ví dụ: chiều cao/rộng của viewport)
            return (orientation == SwingConstants.VERTICAL) ? visibleRect.height : visibleRect.width;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            // *** QUAN TRỌNG: Buộc chiều rộng khớp với viewport ***
            // Điều này làm cho FlowLayout xuống dòng
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            // Không buộc chiều cao khớp viewport, để scroll dọc hoạt động
            return false;
        }

        // --- Tùy chỉnh tính toán chiều cao cho FlowLayout (Cần thiết) ---
        // Ghi đè getPreferredSize để tính chiều cao chính xác khi xuống dòng
        @Override
        public Dimension getPreferredSize() {
            int width = getParent().getWidth() - getInsets().left - getInsets().right; // Lấy chiều rộng khả dụng
            if (width <= 0) {
                 width = Integer.MAX_VALUE; // Nếu chưa có parent, giả định rộng vô hạn
            }
            int height = 0;
            int rowHeight = 0;
            int currentWidth = 0;
            int hgap = layout.getHgap();
            int vgap = layout.getVgap();

            synchronized (getTreeLock()) {
                for (Component comp : getComponents()) {
                    if (comp.isVisible()) {
                        Dimension preferred = comp.getPreferredSize();
                        // Kiểm tra xem component có vừa hàng hiện tại không
                        if (currentWidth == 0 || currentWidth + hgap + preferred.width <= width) {
                            if (currentWidth > 0) {
                                currentWidth += hgap;
                            }
                            currentWidth += preferred.width;
                            rowHeight = Math.max(rowHeight, preferred.height);
                        } else {
                            // Bắt đầu hàng mới
                            height += rowHeight + vgap;
                            currentWidth = preferred.width;
                            rowHeight = preferred.height;
                        }
                    }
                }
                height += rowHeight; // Thêm chiều cao hàng cuối cùng
            }
            return new Dimension(width, height + getInsets().top + getInsets().bottom);
        }
    }
} // Kết thúc lớp DatBan_View