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
import java.text.SimpleDateFormat;

// Import cho JTable và Filtering
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;
// Import khác
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent; // For search listener
import javax.swing.event.DocumentListener; // For search listener
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime; // Needed for PhieuDatBan
import java.time.format.DateTimeFormatter; // Thêm import
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
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
    
    private JSpinner spnDateFrom;         
    private JSpinner spnDateTo;          
    private JButton btnLoc;

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
    	danhSachPhieuDatDangHoatDong = datBanDAO.getAllPhieuDatBan();
        capNhatBangDatBan(); //cập nhật jtable
    }

    //hàm tạo thành phần giao diện
    private JPanel taoSidebarChonKhuVuc() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(MAU_TRANG);
        sidebar.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(MAU_VIEN, 1, true), BorderFactory.createEmptyBorder(25, 20, 25, 20)));
        sidebar.setPreferredSize(new Dimension(230, 0));
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
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(MAU_TRANG);
        panel.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        panel.setPreferredSize(new Dimension(700, 0));

        JLabel titleLabel = new JLabel("Danh sách đặt bàn");
        titleLabel.setFont(FONT_TIEUDE_CHINH);
        titleLabel.setForeground(MAU_CHU_CHINH);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel pnlContent = new JPanel(new BorderLayout(0, 10)); // Tăng khoảng cách
        pnlContent.setBackground(MAU_TRANG);
        pnlContent.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        // === PANEL TÌM KIẾM VÀ LỌC (SỬA LẠI) ===
        JPanel pnlFilterSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Tăng khoảng cách ngang
        pnlFilterSearch.setBackground(MAU_TRANG);

        // -- Tìm SĐT --
        JLabel lblTim = new JLabel("Tìm SĐT:"); lblTim.setFont(FONT_CHU);
        txtTimSDT = new JTextField(15); txtTimSDT.setFont(FONT_CHU);
        pnlFilterSearch.add(lblTim);
        pnlFilterSearch.add(txtTimSDT);

     // -- Lọc Ngày Từ (DÙNG JSpinner) --
        JLabel lblDateFrom = new JLabel("Từ ngày:"); lblDateFrom.setFont(FONT_CHU);
        // Tạo SpinnerDateModel: giá trị hiện tại là hôm nay, không giới hạn ngày min/max, step là từng ngày
        SpinnerDateModel dateFromModel = new SpinnerDateModel(new java.util.Date(), null, null, Calendar.DAY_OF_MONTH);
        spnDateFrom = new JSpinner(dateFromModel);
        // Định dạng hiển thị ngày
        JSpinner.DateEditor dateFromEditor = new JSpinner.DateEditor(spnDateFrom, "dd/MM/yyyy");
        spnDateFrom.setEditor(dateFromEditor);
        // Đặt kích thước
        spnDateFrom.setPreferredSize(new Dimension(120, txtTimSDT.getPreferredSize().height));
        spnDateFrom.setFont(FONT_CHU);
        pnlFilterSearch.add(lblDateFrom);
        pnlFilterSearch.add(spnDateFrom);

        // -- Lọc Ngày Đến (DÙNG JSpinner) --
        JLabel lblDateTo = new JLabel("Đến ngày:"); lblDateTo.setFont(FONT_CHU);
        SpinnerDateModel dateToModel = new SpinnerDateModel(new java.util.Date(), null, null, Calendar.DAY_OF_MONTH);
        spnDateTo = new JSpinner(dateToModel);
        JSpinner.DateEditor dateToEditor = new JSpinner.DateEditor(spnDateTo, "dd/MM/yyyy");
        spnDateTo.setEditor(dateToEditor);
        spnDateTo.setPreferredSize(new Dimension(120, txtTimSDT.getPreferredSize().height));
        spnDateTo.setFont(FONT_CHU);
        pnlFilterSearch.add(lblDateTo);
        pnlFilterSearch.add(spnDateTo);

        // -- Nút Lọc --
        btnLoc = new JButton("Lọc");
        btnLoc.setFont(FONT_CHU);
        pnlFilterSearch.add(btnLoc);
        // ===================================

        pnlContent.add(pnlFilterSearch, BorderLayout.NORTH);

        // --- Panel Bảng (Giữ nguyên) ---
        modelDanhSachDatBan = new DefaultTableModel(
                new String[]{"Mã phiếu", "Số bàn", "SĐT", "Thời gian khách tới", ""}, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    // Chỉ cho phép edit cột "Chi tiết" (index 4)
                    return c == 4;
                }
            };
        tblDanhSachDatBan = new JTable(modelDanhSachDatBan);
        tblDanhSachDatBan.setFont(FONT_O_BANG);
        tblDanhSachDatBan.setRowHeight(25);
        tblDanhSachDatBan.getTableHeader().setFont(FONT_TIEUDE_BANG);
        tblDanhSachDatBan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        boLocSapXep = new TableRowSorter<>(modelDanhSachDatBan);
        tblDanhSachDatBan.setRowSorter(boLocSapXep);
        Color chiTietButtonColor = new Color(0, 123, 255); // Màu xanh dương
        tblDanhSachDatBan.getColumn("Chi tiết").setCellRenderer(new ButtonRenderer("Chi tiết", chiTietButtonColor, Color.WHITE));
        tblDanhSachDatBan.getColumn("Chi tiết").setCellEditor(new ButtonEditor(new JCheckBox(), "Chi tiết"));
        // Đặt độ rộng cột nút
        tblDanhSachDatBan.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblDanhSachDatBan.getColumnModel().getColumn(4).setMaxWidth(90);
        
        JScrollPane sP = new JScrollPane(tblDanhSachDatBan);
        pnlContent.add(sP, BorderLayout.CENTER);

        panel.add(pnlContent, BorderLayout.CENTER);

        //NÚT LỌC VÀ Ô TÌM SĐT
        btnLoc.addActionListener(e -> locBang());
        txtTimSDT.addActionListener(e -> locBang()); // Lọc luôn khi nhấn Enter ở ô SĐT

        return panel;
    }
    
    private void locBang() {
        if (boLocSapXep == null) return; // Đảm bảo sorter đã được tạo

        String sdtFilter = txtTimSDT.getText().trim();
        java.util.Date dateFrom = (java.util.Date) spnDateFrom.getValue();
        java.util.Date dateTo = (java.util.Date) spnDateTo.getValue();

        // Tạo danh sách các bộ lọc con
        List<RowFilter<Object, Object>> filters = new ArrayList<>();

        // 1. Lọc theo SĐT (nếu có nhập)
        if (!sdtFilter.isEmpty()) {
            try {
                // Lọc cột SĐT (index 2) - không phân biệt hoa thường
                filters.add(RowFilter.regexFilter("(?i)" + sdtFilter, 2));
            } catch (java.util.regex.PatternSyntaxException e) {
                // Xử lý nếu regex không hợp lệ (ví dụ: người dùng nhập ký tự đặc biệt)
                System.err.println("Lỗi regex filter SĐT: " + e.getMessage());
            }
        }

        // 2. Lọc theo Ngày (cột "Thời gian khách tới", index 3)
        if (dateFrom != null || dateTo != null) {
            filters.add(new RowFilter<Object, Object>() {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                @Override
                public boolean include(Entry<? extends Object, ? extends Object> entry) {
                    try {
                        // Lấy giá trị ngày giờ từ bảng (cột 3)
                        String dateString = entry.getStringValue(3);
                        java.util.Date entryDate = sdf.parse(dateString);

                        // So sánh với ngày bắt đầu (nếu có)
                        // (So sánh chỉ phần ngày, bỏ qua giờ phút)
                        if (dateFrom != null) {
                            Calendar calEntry = Calendar.getInstance();
                            calEntry.setTime(entryDate);
                            Calendar calFrom = Calendar.getInstance();
                            calFrom.setTime(dateFrom);
                            // Đặt giờ phút giây milli về 0 để so sánh ngày
                            setZeroTime(calEntry);
                            setZeroTime(calFrom);
                            if (calEntry.before(calFrom)) {
                                return false; // Nếu ngày trong bảng < ngày bắt đầu -> ẩn
                            }
                        }

                        // So sánh với ngày kết thúc (nếu có)
                        if (dateTo != null) {
                             Calendar calEntry = Calendar.getInstance();
                            calEntry.setTime(entryDate);
                            Calendar calTo = Calendar.getInstance();
                            calTo.setTime(dateTo);
                            setZeroTime(calEntry);
                            setZeroTime(calTo);
                             if (calEntry.after(calTo)) {
                                return false; // Nếu ngày trong bảng > ngày kết thúc -> ẩn
                            }
                        }
                        return true; // Nếu qua hết các kiểm tra -> hiện
                    } catch (Exception e) {
                        // Nếu parse lỗi, ẩn dòng đó đi
                        System.err.println("Lỗi parse ngày khi lọc: " + entry.getStringValue(3));
                        return false;
                    }
                }
                 // Hàm trợ giúp đặt giờ phút giây về 0
                private void setZeroTime(Calendar cal) {
                    cal.set(Calendar.HOUR_OF_DAY, 0);
                    cal.set(Calendar.MINUTE, 0);
                    cal.set(Calendar.SECOND, 0);
                    cal.set(Calendar.MILLISECOND, 0);
                }
            });
        }
        // Kết hợp tất cả bộ lọc con bằng AND
        if (filters.isEmpty()) {
            boLocSapXep.setRowFilter(null); // Không có bộ lọc nào -> hiển thị tất cả
        } else {
            boLocSapXep.setRowFilter(RowFilter.andFilter(filters));
        }
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
         btn.setPreferredSize(new Dimension(190, 45));
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
                
                modelDanhSachDatBan.addRow(new Object[]{maPhieu, mb, s, t, "Chi tiết"});
            }
        }
        locBang();
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
            	PhieuDatBan pdb = datBanDAO.getPhieuByBan(ban.getMaBan().trim());
                if (pdb != null) {
                    datBanDAO.deletePhieuDatBan(pdb.getMaPhieu());
                }
                ban.setTrangThai(TrangThaiBan.TRONG);
                banDAO.capNhatBan(ban);


                taiDuLieuVaHienThiBanDau();

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
       Runnable refreshCallback = () -> {
           System.out.println("Callback: Đặt bàn thành công, đang tải lại dữ liệu...");
           // Tải lại toàn bộ dữ liệu để cập nhật cả lưới bàn và danh sách
           // (Hàm này đã bao gồm taiDuLieuDatBan())
           taiDuLieuVaHienThiBanDau();
       };

       // Khởi tạo và hiển thị dialog với Runnable
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
    
    class ButtonRenderer extends JButton implements TableCellRenderer {
        private Color bgColor;
        private Color fgColor;

        public ButtonRenderer(String text, Color bg, Color fg) {
            setText(text);
            this.bgColor = bg;
            this.fgColor = fg;
            setOpaque(true); // Quan trọng để nền hiển thị
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setFocusPainted(false);
            setBorderPainted(false); // Không vẽ viền mặc định
            setBorder(new EmptyBorder(5, 5, 5, 5)); // Padding nhỏ
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,boolean isSelected, boolean hasFocus,int row, int column) {
            // Đặt màu nền và màu chữ
            setBackground(bgColor);
            setForeground(fgColor);
            return this;
        }
    }

    /**
     * Lớp Editor để xử lý sự kiện click nút trong JTable
     */
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private String label;
        private String currentMaPhieu; // Lưu mã phiếu của hàng đang click

        public ButtonEditor(JCheckBox checkBox, String type) {
            super(checkBox);
            this.label = type;
            button = new JButton();
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setBorder(new EmptyBorder(5, 5, 5, 5));
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            button.setText(label);
            // Lấy mã phiếu từ cột 0
            currentMaPhieu = table.getValueAt(row, 0).toString();

            // Đặt màu nút "Chi tiết"
            if ("Chi tiết".equals(label)) {
                button.setBackground(new Color(0, 123, 255)); 
                button.setForeground(Color.WHITE);
            }
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && currentMaPhieu != null && "Chi tiết".equals(label)) {
                SwingUtilities.invokeLater(() -> {
                    PhieuDatBan selectedPDB = null;
                    for (PhieuDatBan pdb : danhSachPhieuDatDangHoatDong) {
                        if (pdb.getMaPhieu() != null && pdb.getMaPhieu().trim().equals(currentMaPhieu.trim())) {
                            selectedPDB = pdb;
                            break;
                        }
                    }

                    if (selectedPDB != null) {
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(DatBan_View.this);
                        ChiTietPhieu_Dialog detailDialog = new ChiTietPhieu_Dialog(parentFrame, selectedPDB);
                        detailDialog.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(button, "Không tìm thấy thông tin chi tiết cho phiếu: " + currentMaPhieu, "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
            isPushed = false;
            currentMaPhieu = null;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        public void cancelCellEditing() {
            isPushed = false;
            super.cancelCellEditing();
        }
    }
}