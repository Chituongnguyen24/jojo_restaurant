package view.Ban;


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

    //DAO
    private Ban_DAO banDAO;
    private DatBan_DAO datBanDAO;

    //Dữ liệu
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
    
    //UI
    private WrappingFlowPanel pnlLuoiBan; // Panel lưới bàn (Center)
    private JTable tblDanhSachDatBan; // Bảng danh sách đặt bàn (West)
    private DefaultTableModel modelDanhSachDatBan;
    private JTextField txtTimSDT;
    private TableRowSorter<DefaultTableModel> boLocSapXep;
    private Map<String, JButton> cacNutChonKhuVuc; // Nút chọn khu vực (East)
    private JPanel pnlChuaNutKhuVuc;
    private JPanel pnlSidebarPhai; // Sidebar chọn khu vực (East)

    //Màu và Font
    private static final Color MAU_NEN = new Color(245, 245, 240); // Nền chính
    private static final Color MAU_TRANG = Color.WHITE; // Nền panel, thẻ bàn
    private static final Color MAU_CHU_CHINH = new Color(60, 60, 60);
    private static final Color MAU_CHU_PHU = new Color(120, 120, 120);
    private static final Color MAU_VIEN = new Color(230, 230, 230); // Viền chung
    private static final Color MAU_CAM_CHINH = new Color(255, 152, 0); // Nút sidebar active
    private static final Color MAU_CAM_DAM = new Color(220, 120, 0); // Badge sidebar active
    private static final Color MAU_NHAN_MACDINH = new Color(76, 175, 80); // Badge sidebar inactive
    //Màu trạng thái bàn
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

    public DatBan_View() {
        banDAO = new Ban_DAO();
        datBanDAO = new DatBan_DAO();
        danhSachBanTheoKhuVuc = new LinkedHashMap<>();
        tenKhuVuc = new ArrayList<>();
        cacNutChonKhuVuc = new LinkedHashMap<>();
        danhSachPhieuDatDangHoatDong = new ArrayList<>();
        areaIcons = new LinkedHashMap<>(); // Khởi tạo  icons

        taiTatCaIconKhuVuc(); //hàm tải icon

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
            if (o.getIconWidth() <= 0) { //kiểm tra ảnh đã load thành công hay chưa
                System.err.println("Warning: Icon not found or invalid: " + imagePath);
                return null;
            }
            // Scale icon (80x80)
            Image s = o.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            return new ImageIcon(s);
        } catch (Exception e) {
            System.err.println("Error loading icon: " + imagePath);
            return null;
        }
    }

    private ImageIcon layIconChoKhuVucHienTai() {
        ImageIcon icon = areaIcons.get(khuVucHienTai);
        return icon != null ? icon : areaIcons.getOrDefault("DEFAULT", null);
    }

    private void thietLapGiaoDien() {
        setLayout(new BorderLayout(10, 10)); //Khoảng cách giữa các component
        setBackground(MAU_NEN);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); //Padding cho toàn bộ view

        //Phần Chú thích 
        JPanel pnlChuThich = taoPanelChuThich();
        add(pnlChuThich, BorderLayout.NORTH);

        //Danh sách đặt bàn
        JPanel pnlDanhSach = taoPanelDanhSachDatBan();
        add(pnlDanhSach, BorderLayout.WEST);

        //Lưới bàn
        pnlLuoiBan = new WrappingFlowPanel();
        pnlLuoiBan.setBackground(MAU_TRANG);
        pnlLuoiBan.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollLuoiBan = new JScrollPane(pnlLuoiBan);
        scrollLuoiBan.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        add(scrollLuoiBan, BorderLayout.CENTER);

        //Sidebar chọn khu vực
        pnlSidebarPhai = taoSidebarChonKhuVuc();
        add(pnlSidebarPhai, BorderLayout.EAST);
    }

    private void taiDuLieuVaHienThiBanDau() {
        taiDuLieuKhuVuc(); //Load khu vực
        
 
        // Khởi động danh sách đặt bàn 
        taiDuLieuDatBan(); 
        capNhatNoiDungSidebar(); 
        capNhatLuaChonSidebar(); 
        capNhatHienThiLuoiBan(); 
    }

    //Hàm tải dữ liệu
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
        capNhatBangDatBan(); //cập nhật jtable
    }

    //hàm tạo thành phần giao diện
    private JPanel taoSidebarChonKhuVuc() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(MAU_TRANG);
        sidebar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true), BorderFactory.createEmptyBorder(25, 20, 25, 20)));
        sidebar.setPreferredSize(new Dimension(280, 0));
        JLabel aL = new JLabel("Khu vực");
        aL.setFont(FONT_TIEUDE_CHINH);
        aL.setForeground(MAU_CHU_CHINH);
        aL.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel aSL = new JLabel("Chọn khu vực");
        aSL.setFont(FONT_CHU);
        aSL.setForeground(MAU_CHU_PHU);
        aSL.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(aL);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(aSL);
        sidebar.add(Box.createVerticalStrut(20));
        pnlChuaNutKhuVuc = new JPanel();
        pnlChuaNutKhuVuc.setLayout(new BoxLayout(pnlChuaNutKhuVuc, BoxLayout.Y_AXIS));
        pnlChuaNutKhuVuc.setOpaque(false);
        sidebar.add(pnlChuaNutKhuVuc);
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

     private JPanel taoPanelDanhSachDatBan() {
        JPanel panel = new JPanel(new BorderLayout(0, 10)); // Tăng khoảng cách
        panel.setBackground(MAU_TRANG);
        panel.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        panel.setPreferredSize(new Dimension(800, 0)); //chiều rộng panel trái

        //tiêu đề
        JLabel titleLabel = new JLabel("Danh sách đặt bàn");
        titleLabel.setFont(FONT_TIEUDE_CHINH);
        titleLabel.setForeground(MAU_CHU_CHINH);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        panel.add(titleLabel, BorderLayout.NORTH);

        //nội dung (tìm kiếm + bảng)
        JPanel pnlContent = new JPanel(new BorderLayout(0, 5));
        pnlContent.setBackground(MAU_TRANG);
        pnlContent.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10)); //padding

        //tìm kiếm
        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlTimKiem.setBackground(MAU_TRANG);
        JLabel lblTim = new JLabel("Tìm SĐT:"); lblTim.setFont(FONT_CHU);
        txtTimSDT = new JTextField(15); txtTimSDT.setFont(FONT_CHU);
        pnlTimKiem.add(lblTim); pnlTimKiem.add(txtTimSDT);
        pnlContent.add(pnlTimKiem, BorderLayout.NORTH);

        //bảng
        modelDanhSachDatBan = new DefaultTableModel( new String[]{"Mã phiếu", "Số bàn", "SĐT", "Thời gian khách tới"}, 0) { 
        	@Override public boolean isCellEditable(int r, int c) { 
        		return false; 
        	} 
        };
        tblDanhSachDatBan = new JTable(modelDanhSachDatBan);
        tblDanhSachDatBan.setFont(FONT_O_BANG);
        tblDanhSachDatBan.setRowHeight(25);
        tblDanhSachDatBan.getTableHeader().setFont(FONT_TIEUDE_BANG);
        tblDanhSachDatBan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        boLocSapXep = new TableRowSorter<>(modelDanhSachDatBan);
        tblDanhSachDatBan.setRowSorter(boLocSapXep);

        JScrollPane sP = new JScrollPane(tblDanhSachDatBan);
        pnlContent.add(sP, BorderLayout.CENTER);

        panel.add(pnlContent, BorderLayout.CENTER);

        return panel;
    }

    private JPanel taoPanelChuThich() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        p.setBackground(MAU_NEN);
        p.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10)); // bỏ viền dưới, padding
        p.add(taoMucChuThich("Đang phục vụ", MAU_TRANGTHAI_CO_KHACH));
        p.add(taoMucChuThich("Đã đặt", MAU_TRANGTHAI_DA_DAT));
        p.add(taoMucChuThich("Trống", MAU_TRANGTHAI_TRONG));
        return p;
    }

    //hàm cập nhật nội dung giao diện
    private void capNhatNoiDungSidebar() {
         pnlChuaNutKhuVuc.removeAll();
         cacNutChonKhuVuc.clear();
         Map<String, Integer> sl = banDAO.getSoBanTheoKhuVuc();
         for (String n : tenKhuVuc) { 
        	 int c = sl.getOrDefault(n, 0);
        	 JButton fBtn = taoNutChonKhuVuc(n, c + " bàn");
        	 fBtn.addActionListener(e -> chuyenKhuVuc(n));
        	 cacNutChonKhuVuc.put(n, fBtn);
        	 pnlChuaNutKhuVuc.add(fBtn);
        	 pnlChuaNutKhuVuc.add(Box.createVerticalStrut(10));
         } 
         pnlChuaNutKhuVuc.revalidate();
         pnlChuaNutKhuVuc.repaint();
     }

    private void chuyenKhuVuc(String tenKV) {
         khuVucHienTai = tenKV;
         capNhatLuaChonSidebar();
         capNhatHienThiLuoiBan();
     }

    private void capNhatLuaChonSidebar() {
         for (Map.Entry<String, JButton> entry : cacNutChonKhuVuc.entrySet()) { 
        	 dinhDangNutKhuVuc(entry.getValue(), entry.getKey().equals(khuVucHienTai)); 
         }
     }

    private JButton taoNutChonKhuVuc(String n, String c) {
         JButton btn = new JButton();
         btn.setLayout(new BorderLayout(10, 0));
         btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
         btn.setPreferredSize(new Dimension(240, 45));
         btn.setFocusPainted(false);
         btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
         JLabel nL = new JLabel(n);
         nL.setFont(FONT_NUT);
         JLabel cL = new JLabel(c);
         cL.setFont(new Font("Segoe UI", Font.BOLD, 12));
         cL.setOpaque(true);
         cL.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
         cL.setHorizontalAlignment(SwingConstants.CENTER);
         btn.add(nL, BorderLayout.WEST);
         btn.add(cL, BorderLayout.EAST);
         dinhDangNutKhuVuc(btn, false);
         return btn;
     }

    private void dinhDangNutKhuVuc(JButton btn, boolean sel) {
         if (btn.getComponentCount() < 2) return;
         JLabel nL = (JLabel) btn.getComponent(0);
         JLabel cL = (JLabel) btn.getComponent(1);
         Border b;
         if (sel) { 
        	 btn.setBackground(MAU_CAM_CHINH);
        	 b = BorderFactory.createLineBorder(MAU_CAM_CHINH, 1, true);
        	 nL.setForeground(MAU_TRANG);
        	 cL.setForeground(MAU_TRANG);
        	 cL.setBackground(MAU_CAM_DAM);
         } else {
        	 btn.setBackground(MAU_TRANG);
        	 b = BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true);
        	 nL.setForeground(MAU_CHU_CHINH);
        	 cL.setForeground(MAU_TRANG);
        	 cL.setBackground(MAU_NHAN_MACDINH);
         } btn.setBorder(BorderFactory.createCompoundBorder(b, BorderFactory.createEmptyBorder(10, 15, 10, 15)));
     }

    //cập nhật lưới bàn ,hiển thị tất cả bàn
    void capNhatHienThiLuoiBan() {
        pnlLuoiBan.removeAll();

        List<Ban> dsBanTrongKV = danhSachBanTheoKhuVuc.get(khuVucHienTai);

        if (dsBanTrongKV != null && !dsBanTrongKV.isEmpty()) {
            for (Ban ban : dsBanTrongKV) {
                //tạo thẻ bàn
                JPanel card = taoTheBan(ban);
                //thêm thẻ bàn vào FlowLayout
                pnlLuoiBan.add(card);
            }
        } else {
            //xử lý khi không có bàn giữ nguyên
            JLabel lbl = new JLabel("Khu vực này chưa có bàn.");
            lbl.setFont(FONT_CHU);
            lbl.setForeground(MAU_CHU_PHU);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            pnlLuoiBan.add(lbl);
        }
        pnlLuoiBan.revalidate();
        pnlLuoiBan.repaint();
    }

    private void capNhatBangDatBan() {
        modelDanhSachDatBan.setRowCount(0);
        if (danhSachPhieuDatDangHoatDong != null) {
            for (PhieuDatBan p : danhSachPhieuDatDangHoatDong) {
                
                //kiểm tra p.getBan(), p.getBan().getMaBan()
                String mb = (p.getBan() != null && p.getBan().getMaBan() != null) 
                            ? p.getBan().getMaBan().trim() : "N/A";
                
                //kiểm tra p.getKhachHang(), p.getKhachHang().getSdt()
                String s = (p.getKhachHang() != null && p.getKhachHang().getSdt() != null) 
                            ? p.getKhachHang().getSdt().trim() : "N/A";
                
                String t = p.getThoiGianDatFormatted();
                
                //kiểm tra getMaPhieu()
                String maPhieu = (p.getMaPhieu() != null) ? p.getMaPhieu().trim() : "N/A";
                
                modelDanhSachDatBan.addRow(new Object[]{maPhieu, mb, s, t});
            }
        }
        locBang();
    }

    private void locBang() {
         String sT = txtTimSDT.getText().trim();
         if (sT.isEmpty()) { 
        	 boLocSapXep.setRowFilter(null);
         } else { 
        	 try { 
        		 boLocSapXep.setRowFilter(RowFilter.regexFilter("(?i)" + sT, 2));
        	 } catch (java.util.regex.PatternSyntaxException e) {
        		 boLocSapXep.setRowFilter(null); 
        	 }
         }
     }

    //Tạo thẻ bàn (Chỉ hiển thị, không có sự kiện)
    private JPanel taoTheBan(Ban ban) {
        RoundedPanel card = new RoundedPanel(null, 18); 
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MAU_TRANG);

        //xác định màu viền dựa trên trạng thái bàn
        Color mauVienTrangThai;
        String trangThaiText;
        Color trangThaiTextColor;

        if (ban.getTrangThai() == TrangThaiBan.TRONG) {
            mauVienTrangThai = MAU_TRANGTHAI_TRONG;
            trangThaiText = "Trống";
            trangThaiTextColor = mauVienTrangThai.darker();
        } else if (ban.getTrangThai() == TrangThaiBan.CO_KHACH) {
            mauVienTrangThai = MAU_TRANGTHAI_CO_KHACH;
            trangThaiText = "Có khách";
            trangThaiTextColor = mauVienTrangThai.darker();
        } else { // DA_DAT
            mauVienTrangThai = MAU_TRANGTHAI_DA_DAT;
            trangThaiText = "Đã đặt";
            trangThaiTextColor = mauVienTrangThai.darker();
        }
        card.setBorderColor(mauVienTrangThai);
        card.setBorderThickness(4);

        card.setBorder(BorderFactory.createEmptyBorder(20, 18, 20, 18));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        //thêm Icon
        ImageIcon currentIcon = layIconChoKhuVucHienTai();
        JLabel lblIcon;

        if (currentIcon != null) {
            lblIcon = new JLabel(currentIcon);
        } else {
            lblIcon = new JLabel("No Icon");
            lblIcon.setPreferredSize(new Dimension(80, 80));
            lblIcon.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }

        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        //tên bàn và loại bàn
        String loaiBanTen = ban.getLoaiBan().getTenHienThi();
        if (loaiBanTen.equalsIgnoreCase("Bàn VIP")) loaiBanTen = "VIP";
        JLabel lblName = new JLabel(String.format("%s (%s)", ban.getMaBan().trim(), loaiBanTen));
        lblName.setFont(FONT_TIEUDE_CHINH);
        lblName.setForeground(MAU_CHU_CHINH);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        //số chỗ
        JLabel lblCapacity = new JLabel(ban.getSoCho() + " chỗ");
        lblCapacity.setFont(FONT_CHU);
        lblCapacity.setForeground(MAU_CHU_PHU);
        lblCapacity.setAlignmentX(Component.CENTER_ALIGNMENT);

        //trạng thái
        JLabel lblStatusText = new JLabel(trangThaiText);
        lblStatusText.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblStatusText.setForeground(trangThaiTextColor);
        lblStatusText.setAlignmentX(Component.CENTER_ALIGNMENT);

        //thêm các thành phần vào card
        card.add(lblIcon);
        card.add(Box.createVerticalStrut(12));
        card.add(lblName);
        card.add(Box.createVerticalStrut(6));
        card.add(lblCapacity);
        card.add(Box.createVerticalStrut(8));
        card.add(lblStatusText);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 251, 252)); //hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(MAU_TRANG);
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) return;
                TrangThaiBan trangThai = ban.getTrangThai();
                if (trangThai == TrangThaiBan.TRONG) {
                    moDialogDatBan(ban); //dialog đặt bàn
                } else if (trangThai == TrangThaiBan.DA_DAT) {
                    xuLyBanDaDat(ban); //hỏi nhận bàn/hủy
                } else if (trangThai == TrangThaiBan.CO_KHACH) {
                    xuLyBanCoKhach(ban); //hỏi trả bàn
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

        if (result == 0) {
            //chuyển bàn sang trạng thái "Có khách"
            ban.setTrangThai(TrangThaiBan.CO_KHACH);
            boolean success = banDAO.capNhatBan(ban);
            if (success) {
                capNhatHienThiLuoiBan(); //cập nhật lưới bàn
            } else {
                JOptionPane.showMessageDialog(pnlLuoiBan, "Lỗi khi cập nhật trạng thái bàn!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                ban.setTrangThai(TrangThaiBan.DA_DAT);
            }

        } else if (result == 1) {
            int confirmHuy = JOptionPane.showConfirmDialog(
                pnlLuoiBan, 
                "Bạn có chắc chắn muốn HỦY đặt bàn này không?", 
                "Xác nhận hủy", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirmHuy == JOptionPane.YES_OPTION) {
                //lấy phiếu đặt bàn tương ứng
                PhieuDatBan pdb = datBanDAO.getPhieuByBan(ban.getMaBan().trim());
                
                //xóa phiếu đặt
                if (pdb != null) {
                    //xóa khỏi
                    datBanDAO.deletePhieuDatBan(pdb.getMaPhieu());
                    
                    //xóa khỏi danh sách session, dùng .trim() để khớp với mã phiếu sạch trong danh sách
                    final String maPhieuCanXoa = pdb.getMaPhieu().trim();
                    danhSachPhieuDatDangHoatDong.removeIf(
                        p -> p.getMaPhieu().equals(maPhieuCanXoa)
                    );
                }
                
                //chuyển bàn về "Trống"
                ban.setTrangThai(TrangThaiBan.TRONG);
                banDAO.capNhatBan(ban);
                
                //tải lại cả lưới bàn và danh sách đặt
                taiDuLieuKhuVuc(); 
                capNhatBangDatBan(); 
                capNhatHienThiLuoiBan(); 
                JOptionPane.showMessageDialog(pnlLuoiBan, "Đã hủy đặt bàn " + ban.getMaBan().trim());
            }
        }
    }

    //xử lý bàn "Có Khách"
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

        if (result == 0) { //trả bàn
            //tìm phiếu đặt bàn
            PhieuDatBan pdb = datBanDAO.getPhieuByBan(ban.getMaBan().trim());
            
            if (pdb != null) {
            	final String maPhieuCanXoa = pdb.getMaPhieu().trim();
                danhSachPhieuDatDangHoatDong.removeIf(
                    p -> p.getMaPhieu().equals(maPhieuCanXoa)
                );
                
                System.out.println("Đã ẩn phiếu đặt khỏi danh sách: " + maPhieuCanXoa);
            }
        
            //chuyển bàn về "Trống"
            ban.setTrangThai(TrangThaiBan.TRONG);
            boolean success = banDAO.capNhatBan(ban);
            
            if (success) {
                //tải lại lưới bàn và danh sách
                taiDuLieuVaHienThiBanDau();
            } else {
                 JOptionPane.showMessageDialog(pnlLuoiBan, "Lỗi khi cập nhật trạng thái bàn!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                 ban.setTrangThai(TrangThaiBan.CO_KHACH); // Rollback
            }
        }

    }
    
   //mở dialog đặt bàn và xử lý callback
   private void moDialogDatBan(Ban ban) {
       JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);

       //callback để tải lại view khi đặt bàn thành công
       Consumer<PhieuDatBan> refreshCallback = (phieuMoi) -> {
           System.out.println("Callback: Đã nhận phiếu mới " + phieuMoi.getMaPhieu());

           //thêm phiếu mới vào danh sách
           danhSachPhieuDatDangHoatDong.add(phieuMoi);

           taiDuLieuKhuVuc();
           capNhatBangDatBan();
           capNhatHienThiLuoiBan(); 
       };
       DatBan_Dialog dialog = new DatBan_Dialog(parentFrame, ban, refreshCallback);
       dialog.setVisible(true);
   }
   
    //taoMucChuThich
    private JPanel taoMucChuThich(String text, Color color) { 
    	JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
    	item.setOpaque(false);
    	JPanel hopMau = new JPanel() { 
    		@Override 
    		protected void paintComponent(Graphics g) {
    			Graphics2D g2 = (Graphics2D) g.create();
    			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    			g2.setColor(getBackground());
    			g2.fillOval(0, 0, getWidth(), getHeight());
    			g2.setColor(getBackground().darker());
    			g2.drawOval(0, 0, getWidth()-1, getHeight()-1);
    			g2.dispose();
    		}
    	}; 
    	hopMau.setBackground(color);
    	hopMau.setPreferredSize(new Dimension(18, 18));
    	hopMau.setOpaque(false);
    	JLabel label = new JLabel(text);
    	label.setFont(FONT_CHU);
    	label.setForeground(MAU_CHU_PHU);
    	item.add(hopMau);
    	item.add(label);
    	return item;
    }


    //LỚP NỘI BỘ
    
    //lớp Panel bo tròn
    private class RoundedPanel extends JPanel {
        private int cornerRadius = 18; //độ bo góc mặc định
        private Color borderColor = MAU_VIEN;
        private int borderThickness = 1; 

        public RoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            this.cornerRadius = radius;
            setOpaque(false);
        }

        public RoundedPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        public void setBorderColor(Color color) {
            this.borderColor = color;
            repaint();
        }

        public void setBorderThickness(int thickness) {
            this.borderThickness = Math.max(1, thickness);
            repaint(); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.borderColor);
            //đặt độ dày viền
            g2.setStroke(new BasicStroke(this.borderThickness));
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
    
    private class WrappingFlowPanel extends JPanel implements Scrollable {

        private FlowLayout layout;

        public WrappingFlowPanel() {
            //flowLayout căn trái với khoảng cách
            layout = new FlowLayout(FlowLayout.LEFT, 10, 10);
            setLayout(layout);
        }


        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize(); //kích thước viewport ưu tiên = kích thước panel
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            //tốc độ cuộn khi nhấn mũi tên (ví dụ: 16 pixels)
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            //tốc độ cuộn khi nhấn page up/down (ví dụ: chiều cao/rộng của viewport)
            return (orientation == SwingConstants.VERTICAL) ? visibleRect.height : visibleRect.width;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }

        @Override
        public Dimension getPreferredSize() {
            int width = getParent().getWidth() - getInsets().left - getInsets().right; 
            if (width <= 0) {
                 width = Integer.MAX_VALUE; 
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
                        if (currentWidth == 0 || currentWidth + hgap + preferred.width <= width) {
                            if (currentWidth > 0) {
                                currentWidth += hgap;
                            }
                            currentWidth += preferred.width;
                            rowHeight = Math.max(rowHeight, preferred.height);
                        } else {
                            height += rowHeight + vgap;
                            currentWidth = preferred.width;
                            rowHeight = preferred.height;
                        }
                    }
                }
                height += rowHeight; 
            }
            return new Dimension(width, height + getInsets().top + getInsets().bottom);
        }
    }
}