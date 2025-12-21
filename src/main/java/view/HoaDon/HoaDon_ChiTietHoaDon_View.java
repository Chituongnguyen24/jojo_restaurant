package view.HoaDon;

import dao.Ban_DAO;
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.KhuyenMai_DAO;
import dao.PhieuDatBan_DAO;
import dao.Thue_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.PhieuDatBan;
import entity.Thue;
import enums.TrangThaiBan;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import connectDB.ConnectDB;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

public class HoaDon_ChiTietHoaDon_View extends JDialog {

    private JTable tableChiTiet;
    private DefaultTableModel modelChiTiet;
    
    // DAOs
    private HoaDon_DAO hoaDonDAO;
    private KhachHang_DAO khachHangDAO;
    private Ban_DAO banDAO;
    private PhieuDatBan_DAO pbdDAO;
    private KhuyenMai_DAO khuyenMaiDAO;
    private Thue_DAO thueDAO;
    
    // Data
    private HoaDon hoaDonHienTai;

    // Components
    private JButton btnThanhToan, btnDong;
    private JComboBox<KhuyenMai> cbxKhuyenMai;
    private JComboBox<String> cbxPhuongThucTT;
    private JLabel lblTongTienMon, lblTongGiamGia, lblTongThue, lblTongThanhToan;

    private static final DecimalFormat CURRENCY_FORMAT;

    // COLORS
    private static final Color COLOR_PRIMARY = new Color(59, 130, 246); // Xanh dương
    private static final Color COLOR_SUCCESS = new Color(34, 197, 94);  // Xanh lá
    private static final Color COLOR_DANGER = new Color(239, 68, 68);   // Đỏ
    private static final Color COLOR_WARNING = new Color(251, 146, 60); // Cam
    private static final Color COLOR_BG = new Color(248, 250, 252);     // Xám nhạt
    private static final Color COLOR_BORDER = new Color(226, 232, 240); // Viền nhạt
    private static final Color COLOR_TEXT = new Color(51, 65, 85);      // Chữ đen xám
    private static final Color COLOR_TEXT_LIGHT = new Color(100, 116, 139); // Chữ nhạt

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        CURRENCY_FORMAT = new DecimalFormat("###,### VNĐ", symbols);
    }

    public HoaDon_ChiTietHoaDon_View(Frame owner, HoaDon hoaDon) {
        super(owner, "Chi Tiết Hóa Đơn: " + (hoaDon != null ? hoaDon.getMaHD() : "N/A"), true);
        this.hoaDonHienTai = hoaDon;
        
        if (this.hoaDonHienTai == null) {
            JOptionPane.showMessageDialog(owner, "Không tìm thấy thông tin hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            dispose();
            return;
        }

        initializeDAOs();
        initializeUI(owner);
        setupEventListeners();
        loadInitialData();
        checkAndDisableIfPaid();
    }

    private void initializeDAOs() {
        this.hoaDonDAO = new HoaDon_DAO();
        this.khachHangDAO = new KhachHang_DAO();
        this.banDAO = new Ban_DAO();
        this.pbdDAO = new PhieuDatBan_DAO();
        this.khuyenMaiDAO = new KhuyenMai_DAO();
        this.thueDAO = new Thue_DAO();
    }

    private void initializeUI(Frame owner) {
        setSize(1000, 750);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(COLOR_BG);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(COLOR_BG);
        
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    // --- PANEL HEADER: Thông tin chung & Thông tin Khách/NV ---
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, COLOR_PRIMARY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Panel Thông tin hóa đơn (Trái)
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.add(createHeaderLabel("Mã HĐ: " + hoaDonHienTai.getMaHD(), true));
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(createHeaderLabel("Giờ vào: " + hoaDonHienTai.getGioVao(), false));
        infoPanel.add(createHeaderLabel("Bàn: " + (hoaDonHienTai.getBan() != null ? hoaDonHienTai.getBan().getMaBan() : "Mang về"), false));

        // Panel Thông tin Khách hàng & Nhân viên (Phải) - Dùng Label tĩnh
        JPanel peoplePanel = new JPanel();
        peoplePanel.setLayout(new BoxLayout(peoplePanel, BoxLayout.Y_AXIS));
        peoplePanel.setOpaque(false);

        // Lấy tên khách
        String tenKhach = "Khách vãng lai";
        String maKhach = "";
        
        KhachHang kh = hoaDonHienTai.getKhachHang();
        if (kh != null) {
            maKhach = kh.getMaKH().trim();
            // Nếu là khách vãng lai (KH00000000), thử lấy tên từ ghi chú
            if ("KH00000000".equalsIgnoreCase(maKhach)) {
                String ghiChu = "";
                if (hoaDonHienTai.getPhieuDatBan() != null) ghiChu = hoaDonHienTai.getPhieuDatBan().getGhiChu();
                // Giả định HoaDon không có getGhiChu() như bạn nói, ta chỉ lấy từ PhieuDatBan
                
                if (ghiChu != null && ghiChu.contains("Khách: ")) {
                    try {
                        int start = ghiChu.indexOf("Khách: ") + 7;
                        int end = ghiChu.indexOf(" - SĐT:");
                        if (end > start) tenKhach = ghiChu.substring(start, end).trim();
                        else tenKhach = ghiChu.substring(start).trim();
                    } catch (Exception e) {}
                }
            } else {
                tenKhach = kh.getTenKH();
            }
        }
        
        // Lấy tên nhân viên
        String tenNV = "N/A";
        String maNV = "";
        if (hoaDonHienTai.getNhanVien() != null) {
            // Giả sử có getHoTen(), nếu không thì dùng getMaNhanVien()
            // tenNV = hoaDonHienTai.getNhanVien().getHoTen(); 
            maNV = hoaDonHienTai.getNhanVien().getMaNhanVien();
            tenNV = maNV; // Tạm thời hiện mã nếu chưa lấy được tên
        }

        JLabel lblKhachTitle = new JLabel("Khách hàng:");
        lblKhachTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblKhachTitle.setForeground(COLOR_TEXT_LIGHT);

        JLabel lblTenKhach = new JLabel(tenKhach);
        lblTenKhach.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTenKhach.setForeground(COLOR_PRIMARY);
        
        JLabel lblMaKH = null;
        if (!maKhach.isEmpty() && !"KH00000000".equalsIgnoreCase(maKhach)) {
             lblMaKH = new JLabel("Mã TV: " + maKhach);
             lblMaKH.setFont(new Font("Segoe UI", Font.PLAIN, 12));
             lblMaKH.setForeground(COLOR_TEXT_LIGHT);
        }

        JLabel lblNhanVien = new JLabel("Nhân viên: " + tenNV);
        lblNhanVien.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblNhanVien.setForeground(COLOR_TEXT);

        peoplePanel.add(lblKhachTitle);
        peoplePanel.add(Box.createVerticalStrut(2));
        peoplePanel.add(lblTenKhach);
        if(lblMaKH != null) peoplePanel.add(lblMaKH);
        peoplePanel.add(Box.createVerticalStrut(8));
        peoplePanel.add(lblNhanVien);

        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(peoplePanel, BorderLayout.CENTER);
        return panel;
    }

    // --- PANEL TABLE: Danh sách món ăn ---
    private JScrollPane createTablePanel() {
        modelChiTiet = new DefaultTableModel(new String[]{"STT", "Tên món", "SL", "Đơn giá", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableChiTiet = new JTable(modelChiTiet);
        tableChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableChiTiet.setRowHeight(40);
        tableChiTiet.setGridColor(COLOR_BORDER);
        tableChiTiet.setShowGrid(true);
        tableChiTiet.setIntercellSpacing(new Dimension(1, 1));
        tableChiTiet.setSelectionBackground(new Color(219, 234, 254));
        tableChiTiet.setSelectionForeground(COLOR_TEXT);

        tableChiTiet.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableChiTiet.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Căn chỉnh tiền tệ
        CurrencyRenderer currencyRenderer = new CurrencyRenderer(CURRENCY_FORMAT);
        tableChiTiet.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);
        tableChiTiet.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);

        // Căn giữa STT và SL
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableChiTiet.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableChiTiet.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Độ rộng cột
        tableChiTiet.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableChiTiet.getColumnModel().getColumn(1).setPreferredWidth(350);
        tableChiTiet.getColumnModel().getColumn(2).setPreferredWidth(60);
        tableChiTiet.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableChiTiet.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(tableChiTiet);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new MatteBorder(1, 1, 1, 1, COLOR_BORDER));
        return scrollPane;
    }

    // --- PANEL FOOTER: Tổng tiền và Khuyến mãi ---
    private JPanel createFooterPanel() {
        JPanel footer = new JPanel(new GridBagLayout());
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, COLOR_BORDER),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.EAST;

        // Combobox Khuyến mãi
        cbxKhuyenMai = new JComboBox<>();
        cbxKhuyenMai.setRenderer(new KhuyenMaiRenderer());
        cbxKhuyenMai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbxKhuyenMai.setPreferredSize(new Dimension(250, 35));
        loadDataKhuyenMai(); // Load dữ liệu KM

        gbc.gridx = 0; gbc.gridy = 0;
        footer.add(createTotalLabel("Áp dụng Khuyến mãi:", false, COLOR_TEXT), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_START;
        footer.add(cbxKhuyenMai, gbc);
        gbc.anchor = GridBagConstraints.EAST;

        // Các Label Tổng tiền
        gbc.gridx = 0; gbc.gridy = 1;
        footer.add(createTotalLabel("Tổng tiền món:", false, COLOR_TEXT), gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        footer.add(createTotalLabel("Giảm giá (KM):", false, COLOR_WARNING), gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        footer.add(createTotalLabel("Thuế (VAT):", false, COLOR_TEXT), gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        footer.add(createTotalLabel("THÀNH TIỀN:", true, COLOR_DANGER), gbc);

        gbc.anchor = GridBagConstraints.LINE_END;
        lblTongTienMon = createTotalLabel("0 VNĐ", false, COLOR_TEXT);
        lblTongGiamGia = createTotalLabel("0 VNĐ", false, COLOR_WARNING);
        lblTongThue = createTotalLabel("0 VNĐ", false, COLOR_TEXT);
        lblTongThanhToan = createTotalLabel("0 VNĐ", true, COLOR_DANGER);

        gbc.gridx = 1; gbc.gridy = 1; footer.add(lblTongTienMon, gbc);
        gbc.gridx = 1; gbc.gridy = 2; footer.add(lblTongGiamGia, gbc);
        gbc.gridx = 1; gbc.gridy = 3; footer.add(lblTongThue, gbc);
        gbc.gridx = 1; gbc.gridy = 4; footer.add(lblTongThanhToan, gbc);

        return footer;
    }

    // --- PANEL BUTTON: Nút Thanh toán và Đóng ---
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new MatteBorder(1, 0, 0, 0, COLOR_BORDER));

        // Trái: Chọn phương thức thanh toán
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        JLabel lblPTTT = new JLabel("Phương thức:");
        lblPTTT.setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        cbxPhuongThucTT = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ"});
        cbxPhuongThucTT.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbxPhuongThucTT.setPreferredSize(new Dimension(150, 35));
        
        leftPanel.add(lblPTTT);
        leftPanel.add(cbxPhuongThucTT);

        // Phải: Nút bấm
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        btnDong = new ModernButton("Đóng", COLOR_DANGER);
        btnDong.setPreferredSize(new Dimension(100, 40));

        btnThanhToan = new ModernButton("Xác nhận Thanh toán", COLOR_SUCCESS);
        btnThanhToan.setPreferredSize(new Dimension(200, 40));

        rightPanel.add(btnDong);
        rightPanel.add(btnThanhToan);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        return panel;
    }

    // --- LOGIC XỬ LÝ DỮ LIỆU ---

    private void loadInitialData() {
        loadChiTietData(hoaDonHienTai.getMaHD());
        tinhVaHienThiTongTien(hoaDonHienTai);
    }

    private void loadChiTietData(String maHoaDon) {
        modelChiTiet.setRowCount(0);
        List<ChiTietHoaDon> chiTietList = hoaDonDAO.getChiTietHoaDonForPrint(maHoaDon);
        int stt = 1;
        for (ChiTietHoaDon ct : chiTietList) {
            double donGia = ct.getDonGiaBan();
            double thanhTien = ct.getSoLuong() * donGia;
            modelChiTiet.addRow(new Object[]{
                    stt++,
                    ct.getMonAn() != null ? ct.getMonAn().getTenMonAn() : "N/A",
                    ct.getSoLuong(),
                    donGia,
                    thanhTien
            });
        }
    }

    private void loadDataKhuyenMai() {
        cbxKhuyenMai.removeAllItems();
        try {
            List<KhuyenMai> dsKM = khuyenMaiDAO.getAllActiveKhuyenMai();
            for (KhuyenMai km : dsKM) {
                cbxKhuyenMai.addItem(km);
                // Chọn lại khuyến mãi đang có trong hóa đơn
                if (hoaDonHienTai.getKhuyenMai() != null && 
                    km.getMaKM().equals(hoaDonHienTai.getKhuyenMai().getMaKM())) {
                    cbxKhuyenMai.setSelectedItem(km);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void capNhatGiaTienXemTruoc() {
        if (hoaDonHienTai == null) return;

        double tongTienMon = hoaDonHienTai.getTongTienTruocThue();
        KhuyenMai km = (KhuyenMai) cbxKhuyenMai.getSelectedItem();
        double tienGiamMoi = 0;
        
        if (km != null && !km.getMaKM().equalsIgnoreCase("KM00000000")) {
            tienGiamMoi = tongTienMon * km.getMucKM();
            if (tienGiamMoi > tongTienMon) tienGiamMoi = tongTienMon;
        }

        double tienSauGiam = Math.max(0, tongTienMon - tienGiamMoi);
        
        // Tính thuế (Giả sử VAT 8%)
        double tyLeVAT = getTyLeThue("VAT08", 0.08);
        BigDecimal bdSauGiam = BigDecimal.valueOf(tienSauGiam);
        BigDecimal bdTyLeVAT = BigDecimal.valueOf(tyLeVAT);
        
        BigDecimal tienVAT = bdSauGiam.multiply(bdTyLeVAT).setScale(0, RoundingMode.HALF_UP);
        BigDecimal tongThanhToan = bdSauGiam.add(tienVAT).setScale(0, RoundingMode.HALF_UP);

        updateLabels(
            CURRENCY_FORMAT.format(tongTienMon),
            CURRENCY_FORMAT.format(tienGiamMoi),
            CURRENCY_FORMAT.format(tienVAT),
            CURRENCY_FORMAT.format(tongThanhToan)
        );
    }

    private double getTyLeThue(String maThue, double defaultValue) {
        try {
            List<Thue> taxes = thueDAO.getAllActiveTaxes();
            for (Thue t : taxes) {
                if (t.getMaSoThue().equals(maThue)) return t.getTyLeThue();
            }
        } catch (Exception e) { }
        return defaultValue;
    }

    private void tinhVaHienThiTongTien(HoaDon hd) {
        // Hàm này gọi lần đầu khi mở form
        capNhatGiaTienXemTruoc(); 
    }

    // --- LOGIC THANH TOÁN ---

    private void xuLyThanhToan() {
        try {
            // 1. Kiểm tra chi tiết hóa đơn
            List<ChiTietHoaDon> listCT = hoaDonDAO.getChiTietHoaDonForPrint(hoaDonHienTai.getMaHD());
            if (listCT == null || listCT.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Hóa đơn trống, không thể thanh toán!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. Kiểm tra phương thức thanh toán
            String pttt = (String) cbxPhuongThucTT.getSelectedItem();
            if (pttt == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phương thức thanh toán.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 3. Tự động cập nhật Khuyến mãi nếu thay đổi
            if (!updatePromotionIfChanged()) {
                return; // Nếu cập nhật lỗi thì dừng
            }

            // 4. Xác nhận
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Xác nhận thanh toán hóa đơn " + hoaDonHienTai.getMaHD() + "?\nTổng tiền: " + lblTongThanhToan.getText(),
                "Xác nhận", JOptionPane.YES_NO_OPTION);
            
            if (confirm != JOptionPane.YES_OPTION) return;

            // 5. Thực hiện thanh toán (Transaction)
            performPaymentTransaction(pttt, listCT);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xử lý thanh toán: " + e.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean updatePromotionIfChanged() {
        try {
            KhuyenMai kmMoi = (KhuyenMai) cbxKhuyenMai.getSelectedItem();
            KhuyenMai kmCu = hoaDonHienTai.getKhuyenMai();
            
            String maKMMoi = (kmMoi != null) ? kmMoi.getMaKM() : "KM00000000";
            String maKMCu = (kmCu != null) ? kmCu.getMaKM() : "KM00000000";

            if (!maKMMoi.equalsIgnoreCase(maKMCu)) {
                // Chỉ cập nhật mã khuyến mãi, giữ nguyên mã khách hàng cũ
                String maKH = (hoaDonHienTai.getKhachHang() != null) ? hoaDonHienTai.getKhachHang().getMaKH() : "KH00000000";
                
                boolean ok = hoaDonDAO.updateKhachHangVaKhuyenMai(hoaDonHienTai.getMaHD(), maKH, maKMMoi);
                if (ok) {
                    // Reload object hóa đơn trong bộ nhớ
                    this.hoaDonHienTai = hoaDonDAO.findByMaHD(hoaDonHienTai.getMaHD());
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể cập nhật mã khuyến mãi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void performPaymentTransaction(String phuongThuc, List<ChiTietHoaDon> listCT) {
        Connection conn = null;
        try {
            conn = ConnectDB.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Cập nhật trạng thái Hóa đơn -> Đã thanh toán
            boolean hdOk = hoaDonDAO.thanhToanHoaDon(hoaDonHienTai.getMaHD(), phuongThuc);
            if (!hdOk) throw new SQLException("Lỗi cập nhật trạng thái Hóa đơn.");

            // 2. Cập nhật trạng thái Bàn -> Trống
            if (hoaDonHienTai.getBan() != null) {
                boolean banOk = banDAO.capNhatTrangThaiBan(hoaDonHienTai.getBan().getMaBan(), TrangThaiBan.TRONG);
                if (!banOk) throw new SQLException("Lỗi cập nhật trạng thái Bàn.");
            }

            // 3. Cập nhật Phiếu đặt bàn -> Hoàn thành (nếu có)
            if (hoaDonHienTai.getPhieuDatBan() != null) {
                PhieuDatBan pdb = pbdDAO.getPhieuDatBanById(hoaDonHienTai.getPhieuDatBan().getMaPhieu());
                if (pdb != null) {
                    pdb.setTrangThaiPhieu("Hoàn thành");
                    pdb.setThoiGianTraBan(LocalDateTime.now());
                    boolean pdbOk = pbdDAO.updatePhieuDatBan(pdb);
                    if (!pdbOk) throw new SQLException("Lỗi cập nhật Phiếu đặt bàn.");
                }
            }

            conn.commit(); // Thành công -> Lưu vào DB

            // 4. Cộng điểm tích lũy (không cần trong transaction)
            updateLoyaltyPoints();

            // 5. In hóa đơn / Xem trước
            printReceipt(listCT);

            JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Đóng cửa sổ

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Thanh toán thất bại: " + e.getMessage(), "Lỗi Transaction", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    private void updateLoyaltyPoints() {
        try {
            KhachHang kh = hoaDonHienTai.getKhachHang();
            if (kh != null && !kh.getMaKH().trim().equalsIgnoreCase("KH00000000")) {
                double total = hoaDonDAO.tinhTongTienHoaDon(hoaDonHienTai.getMaHD());
                int points = (int) (total / 10000); // 10k = 1 điểm
                if (points > 0) {
                    khachHangDAO.congDiemTichLuy(kh.getMaKH(), points);
                }
            }
        } catch (Exception e) {
            System.err.println("Lỗi cộng điểm: " + e.getMessage());
        }
    }

    private void printReceipt(List<ChiTietHoaDon> listCT) {
        try {
            Frame owner = (Frame) this.getOwner();
            // Lấy lại hóa đơn mới nhất từ DB để in cho chính xác
            HoaDon hdIn = hoaDonDAO.findByMaHD(hoaDonHienTai.getMaHD());
            HoaDon_Printer.showPreview(owner, hdIn, listCT);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi xem trước in ấn: " + e.getMessage());
        }
    }

    private void checkAndDisableIfPaid() {
        if (hoaDonHienTai.isDaThanhToan()) {
            btnThanhToan.setEnabled(false);
            btnThanhToan.setText("Đã Thanh Toán");
            cbxPhuongThucTT.setEnabled(false);
            cbxKhuyenMai.setEnabled(false);
            if (hoaDonHienTai.getPhuongThucThanhToan() != null) {
                cbxPhuongThucTT.setSelectedItem(hoaDonHienTai.getPhuongThucThanhToan());
            }
        }
    }

    // --- HELPER UI METHODS ---

    private void setupEventListeners() {
        cbxKhuyenMai.addActionListener(e -> capNhatGiaTienXemTruoc());
        btnDong.addActionListener(e -> dispose());
        btnThanhToan.addActionListener(e -> xuLyThanhToan());
    }

    private JLabel createHeaderLabel(String text, boolean isBold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, isBold ? 16 : 14));
        label.setForeground(isBold ? COLOR_TEXT : COLOR_TEXT_LIGHT);
        return label;
    }

    private JLabel createTotalLabel(String text, boolean isBold, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, isBold ? 18 : 14));
        label.setForeground(color);
        return label;
    }

    private void updateLabels(String tienMon, String giamGia, String thue, String thanhTien) {
        lblTongTienMon.setText(tienMon);
        lblTongGiamGia.setText(giamGia);
        lblTongThue.setText(thue);
        lblTongThanhToan.setText(thanhTien);
    }

    // --- RENDERERS & COMPONENTS ---

    // Nút bấm hiện đại
    private class ModernButton extends JButton {
        private Color bgColor;
        public ModernButton(String text, Color bgColor) {
            super(text);
            this.bgColor = bgColor;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(isEnabled() ? bgColor : Color.GRAY);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
            g2.setColor(Color.WHITE);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    // Hiển thị tiền tệ trong bảng
    static class CurrencyRenderer extends DefaultTableCellRenderer {
        private DecimalFormat formatter;
        public CurrencyRenderer(DecimalFormat formatter) {
            this.formatter = formatter;
            setHorizontalAlignment(JLabel.RIGHT);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Number) setText(formatter.format(value));
            return this;
        }
    }


    class KhuyenMaiRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof KhuyenMai) {
                KhuyenMai km = (KhuyenMai) value;
                String text = km.getMaKM().equals("KM00000000") ? "Không áp dụng" : km.getMoTa() + " (" + (int)(km.getMucKM()*100) + "%)";
                setText(text);
            }
            if (isSelected) {
                setBackground(COLOR_PRIMARY);
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                setForeground(COLOR_TEXT);
            }
            return this;
        }
    }
}