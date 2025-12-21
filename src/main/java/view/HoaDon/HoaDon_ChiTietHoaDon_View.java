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
import java.util.Objects;

public class HoaDon_ChiTietHoaDon_View extends JDialog {

    private JTable tableChiTiet;
    private DefaultTableModel modelChiTiet;
    private HoaDon_DAO hoaDonDAO;
    private KhachHang_DAO khachHangDAO;
    private Ban_DAO banDAO;
    private PhieuDatBan_DAO pbdDAO;
    private KhuyenMai_DAO khuyenMaiDAO;
    private Thue_DAO thueDAO;
    private HoaDon hoaDonHienTai;

    private JButton btnThanhToan, btnDong;
    private JComboBox<KhachHang> cbxKhachHang;
    private JComboBox<KhuyenMai> cbxKhuyenMai;
    private JComboBox<String> cbxPhuongThucTT;
    private JLabel lblTongTienMon, lblTongGiamGia, lblTongThue, lblTongThanhToan;

    private static final DecimalFormat CURRENCY_FORMAT;

    // MÀU SẮC GIAO DIỆN
    private static final Color COLOR_PRIMARY = new Color(59, 130, 246);
    private static final Color COLOR_SUCCESS = new Color(34, 197, 94);
    private static final Color COLOR_DANGER = new Color(239, 68, 68);
    private static final Color COLOR_WARNING = new Color(251, 146, 60);
    private static final Color COLOR_BG = new Color(248, 250, 252);
    private static final Color COLOR_BORDER = new Color(226, 232, 240);
    private static final Color COLOR_TEXT = new Color(51, 65, 85);
    private static final Color COLOR_TEXT_LIGHT = new Color(100, 116, 139);

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        CURRENCY_FORMAT = new DecimalFormat("###,### VNĐ", symbols);
    }

    public HoaDon_ChiTietHoaDon_View(Frame owner, HoaDon hoaDon) {
        super(owner, "Chi Tiết Hóa Đơn: " + hoaDon.getMaHD(), true);
        this.hoaDonHienTai = hoaDon;
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

        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        mainPanel.setBackground(COLOR_BG);
        mainPanel.add(createHeader(), BorderLayout.NORTH);
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);
        return mainPanel;
    }

    private void setupEventListeners() {
        cbxKhachHang.addActionListener(e -> capNhatGiaTienXemTruoc());
        cbxKhuyenMai.addActionListener(e -> capNhatGiaTienXemTruoc());
        btnDong.addActionListener(e -> dispose());
        btnThanhToan.addActionListener(e -> xuLyThanhToan());
    }

    private void loadInitialData() {
        loadChiTietData(hoaDonHienTai.getMaHD());
        tinhVaHienThiTongTien(hoaDonHienTai);
    }

    private void checkAndDisableIfPaid() {
        if (hoaDonHienTai.isDaThanhToan()) {
            btnThanhToan.setEnabled(false);
            btnThanhToan.setText("Đã Thanh Toán");
            cbxPhuongThucTT.setEnabled(false);
            cbxKhachHang.setEnabled(false);
            cbxKhuyenMai.setEnabled(false);
            if (hoaDonHienTai.getPhuongThucThanhToan() != null) {
                cbxPhuongThucTT.setSelectedItem(hoaDonHienTai.getPhuongThucThanhToan());
            }
        }
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 0, 0, 0, COLOR_BORDER),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JPanel leftPanel = createLeftButtonPanel();
        JPanel rightPanel = createRightButtonPanel();

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createLeftButtonPanel() {
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);

        JLabel lblPTTT = new JLabel("Phương thức thanh toán:");
        lblPTTT.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPTTT.setForeground(COLOR_TEXT);

        cbxPhuongThucTT = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ"});
        cbxPhuongThucTT.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbxPhuongThucTT.setPreferredSize(new Dimension(150, 35));
        cbxPhuongThucTT.setBackground(Color.WHITE);
        cbxPhuongThucTT.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, COLOR_BORDER),
                new EmptyBorder(5, 10, 5, 10)
        ));

        leftPanel.add(lblPTTT);
        leftPanel.add(cbxPhuongThucTT);
        return leftPanel;
    }

    private JPanel createRightButtonPanel() {
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        btnDong = new ModernButton("Đóng", COLOR_DANGER);
        btnDong.setPreferredSize(new Dimension(120, 40));

        btnThanhToan = new ModernButton("Xác nhận Thanh toán", COLOR_SUCCESS);
        btnThanhToan.setPreferredSize(new Dimension(200, 40));

        rightPanel.add(btnDong);
        rightPanel.add(btnThanhToan);
        return rightPanel;
    }

    private void xuLyThanhToan() {
        List<ChiTietHoaDon> chiTietList = hoaDonDAO.getChiTietHoaDonForPrint(hoaDonHienTai.getMaHD());
        if (chiTietList == null || chiTietList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có món ăn để tạo hóa đơn", "Hóa Đơn Rỗng", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String phuongThuc = (String) cbxPhuongThucTT.getSelectedItem();
        if (phuongThuc == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phương thức thanh toán.", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // --- TỰ ĐỘNG CẬP NHẬT NẾU CÓ THAY ĐỔI ---
        boolean updateSuccess = autoUpdateInvoiceInfo();
        if (!updateSuccess) {
            return; // Nếu cập nhật lỗi thì dừng lại
        }
        // ----------------------------------------

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận thanh toán cho hóa đơn " + hoaDonHienTai.getMaHD() + "?\nTổng tiền: " + lblTongThanhToan.getText(),
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        performPayment(phuongThuc, chiTietList);
    }

    // Hàm mới: Tự động kiểm tra và cập nhật thông tin nếu khác biệt
    private boolean autoUpdateInvoiceInfo() {
        KhachHang khachHangMoi = (KhachHang) cbxKhachHang.getSelectedItem();
        KhuyenMai khuyenMaiMoi = (KhuyenMai) cbxKhuyenMai.getSelectedItem();
        KhachHang khachHangCu = hoaDonHienTai.getKhachHang();
        KhuyenMai khuyenMaiCu = hoaDonHienTai.getKhuyenMai();

        boolean canUpdate = false;
        
        // Kiểm tra Khách hàng
        String idKHCu = (khachHangCu != null) ? khachHangCu.getMaKH() : "";
        String idKHMoi = (khachHangMoi != null) ? khachHangMoi.getMaKH() : "";
        if (!idKHCu.equals(idKHMoi)) {
            canUpdate = true;
        }

        // Kiểm tra Khuyến mãi
        String maKMCu = (khuyenMaiCu != null && khuyenMaiCu.getMaKM() != null) ? khuyenMaiCu.getMaKM().trim() : "KM00000000";
        String maKMMoi = (khuyenMaiMoi != null && khuyenMaiMoi.getMaKM() != null) ? khuyenMaiMoi.getMaKM().trim() : "KM00000000";
        
        if (!maKMCu.equalsIgnoreCase(maKMMoi)) {
            canUpdate = true;
        }

        if (canUpdate) {
            // Thực hiện cập nhật thầm lặng (Silent Update)
            boolean updateOK = hoaDonDAO.updateKhachHangVaKhuyenMai(
                    hoaDonHienTai.getMaHD(),
                    (khachHangMoi != null) ? khachHangMoi.getMaKH() : "KH00000000",
                    (maKMMoi != null) ? maKMMoi : "KM00000000"
            );
            
            if (updateOK) {
                // Cập nhật lại đối tượng hóa đơn hiện tại trong bộ nhớ
                this.hoaDonHienTai = hoaDonDAO.findByMaHD(hoaDonHienTai.getMaHD());
                tinhVaHienThiTongTien(this.hoaDonHienTai);
                return true; 
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật thông tin Khuyến mãi/Khách hàng vào hệ thống.", "Lỗi Cập Nhật", JOptionPane.ERROR_MESSAGE);
                return false; 
            }
        }
        return true; // Không có gì thay đổi
    }

    private void performPayment(String phuongThuc, List<ChiTietHoaDon> chiTietList) {
        Connection conn = null;
        try {
            conn = ConnectDB.getConnection();
            conn.setAutoCommit(false);

            boolean successHD = hoaDonDAO.thanhToanHoaDon(hoaDonHienTai.getMaHD(), phuongThuc);
            if (!successHD) {
                throw new SQLException("Thanh toán HD thất bại");
            }

            updateBanStatus(conn);

            updatePhieuDatBan(conn);

            conn.commit();

            updateDiemTichLuy();

            printHoaDon(chiTietList);

            JOptionPane.showMessageDialog(this, "Thanh toán thành công! Hóa đơn đã hoàn tất.");
            dispose();

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thanh toán: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateBanStatus(Connection conn) throws SQLException {
        if (hoaDonHienTai.getBan() != null) {
            boolean successBan = banDAO.capNhatTrangThaiBan(hoaDonHienTai.getBan().getMaBan(), TrangThaiBan.TRONG);
            if (!successBan) {
                throw new SQLException("Cập nhật trạng thái bàn thất bại");
            }
        }
    }

    private void updatePhieuDatBan(Connection conn) throws SQLException {
        PhieuDatBan pdb = hoaDonHienTai.getPhieuDatBan();
        if (pdb != null) {
            PhieuDatBan pdbFull = pbdDAO.getPhieuDatBanById(pdb.getMaPhieu());
            if (pdbFull != null) {
                pdbFull.setTrangThaiPhieu("Hoàn thành");
                pdbFull.setThoiGianTraBan(LocalDateTime.now());
                boolean successPDB = pbdDAO.updatePhieuDatBan(pdbFull);
                if (!successPDB) {
                    throw new SQLException("Cập nhật PDB thất bại");
                }
            } else {
                throw new SQLException("Không tìm thấy PDB liên kết");
            }
        }
    }

    private void updateDiemTichLuy() {
        try {
            KhachHang kh = hoaDonHienTai.getKhachHang();
            if (kh != null && !kh.getMaKH().trim().equalsIgnoreCase("KH00000000")) {
                double tongThanhToan = hoaDonDAO.tinhTongTienHoaDon(hoaDonHienTai.getMaHD());
                int diemCongThem = (int) (tongThanhToan / 10000);
                if (diemCongThem > 0) {
                    khachHangDAO.congDiemTichLuy(kh.getMaKH(), diemCongThem);
                }
            }
        } catch (Exception e_diem) {
            System.err.println("Lỗi: Không thể cộng điểm tích lũy. " + e_diem.getMessage());
        }
    }

    private void printHoaDon(List<ChiTietHoaDon> chiTietList) {
        try {
            Frame owner = (Frame) this.getOwner();
            HoaDon hoaDonDaThanhToan = hoaDonDAO.findByMaHD(hoaDonHienTai.getMaHD());
            HoaDon_Printer.showPreview(owner, hoaDonDaThanhToan, chiTietList);
        } catch (Exception e_print) {
            e_print.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi mở bản xem trước: " + e_print.getMessage());
        }
    }

    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout(20, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 0, 2, 0, COLOR_PRIMARY),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel infoPanel = createInfoPanel();
        JPanel khachPanel = createKhachHangPanel();

        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(khachPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        infoPanel.add(createHeaderLabel("Mã HĐ: " + hoaDonHienTai.getMaHD(), true));
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(createHeaderLabel("Giờ vào: " + hoaDonHienTai.getGioVao(), false));
        infoPanel.add(createHeaderLabel("Bàn: " + (hoaDonHienTai.getBan() != null ? hoaDonHienTai.getBan().getMaBan() : "N/A"), false));
        return infoPanel;
    }

    private JPanel createKhachHangPanel() {
        JPanel khachPanel = new JPanel();
        khachPanel.setLayout(new BoxLayout(khachPanel, BoxLayout.Y_AXIS));
        khachPanel.setOpaque(false);

        cbxKhachHang = createKhachHangComboBox();

        khachPanel.add(createHeaderLabel("Khách hàng:", true));
        khachPanel.add(Box.createVerticalStrut(5));
        khachPanel.add(cbxKhachHang);
        khachPanel.add(Box.createVerticalStrut(10));
        khachPanel.add(createHeaderLabel("Nhân viên: " + (hoaDonHienTai.getNhanVien() != null ? hoaDonHienTai.getNhanVien().getMaNhanVien() : "N/A"), false));
        return khachPanel;
    }

    private JComboBox<KhachHang> createKhachHangComboBox() {
        JComboBox<KhachHang> comboBox = new JComboBox<>();
        comboBox.setRenderer(new KhachHangRenderer());
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(250, 35));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, COLOR_BORDER),
                new EmptyBorder(5, 10, 5, 10)
        ));

        KhachHang khachHangDaChon = null;
        try {
            List<KhachHang> dsKH = khachHangDAO.getAllKhachHang();
            for (KhachHang kh : dsKH) {
                comboBox.addItem(kh);
                if (kh.getMaKH().equals(hoaDonHienTai.getKhachHang().getMaKH())) {
                    khachHangDaChon = kh;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (khachHangDaChon != null) {
            comboBox.setSelectedItem(khachHangDaChon);
        } else if (comboBox.getItemCount() > 0) { // Fix lỗi setSelectedIndex
            comboBox.setSelectedIndex(0);
        }
        return comboBox;
    }

    private void capNhatGiaTienXemTruoc() {
        if (hoaDonHienTai == null) return;

        double tongTienMon = hoaDonHienTai.getTongTienTruocThue();
        KhuyenMai km = (KhuyenMai) cbxKhuyenMai.getSelectedItem();
        double tienGiamMoi = 0;
        if (km != null && !km.getMaKM().equals("KM00000000")) {
            tienGiamMoi = tongTienMon * km.getMucKM();
            if (tienGiamMoi > tongTienMon) tienGiamMoi = tongTienMon;
        }

        double tienSauGiamPreview = Math.max(0, tongTienMon - tienGiamMoi);

        double tyLePhi = getTyLeThue("PHIPK5", 0.05);
        double tyLeVAT = getTyLeThue("VAT08", 0.08);

        BigDecimal bdSauGiam = BigDecimal.valueOf(tienSauGiamPreview);
        BigDecimal bdTyLePhi = BigDecimal.valueOf(tyLePhi);
        BigDecimal bdTyLeVAT = BigDecimal.valueOf(tyLeVAT);

        BigDecimal tienPhiPreview = bdSauGiam.multiply(bdTyLePhi).setScale(0, RoundingMode.HALF_UP);
        BigDecimal coSoVATPreview = bdSauGiam.add(tienPhiPreview);
        BigDecimal tienVATPreview = coSoVATPreview.multiply(bdTyLeVAT).setScale(0, RoundingMode.HALF_UP);
        BigDecimal tongThueVaPhiPreview = tienPhiPreview.add(tienVATPreview);
        BigDecimal tongThanhToanPreview = bdSauGiam.add(tongThueVaPhiPreview).setScale(0, RoundingMode.HALF_UP);

        updateLabels(
                CURRENCY_FORMAT.format(tongTienMon),
                CURRENCY_FORMAT.format(tienGiamMoi),
                CURRENCY_FORMAT.format(tongThueVaPhiPreview.doubleValue()),
                CURRENCY_FORMAT.format(tongThanhToanPreview.doubleValue())
        );
    }

    private double getTyLeThue(String maThue, double defaultValue) {
        try {
            List<Thue> taxes = thueDAO.getAllActiveTaxes();
            for (Thue t : taxes) {
                if (t.getMaSoThue().equals(maThue)) {
                    return t.getTyLeThue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    private void updateLabels(String tongTienMonText, String tongGiamGiaText, String tongThueText, String tongThanhToanText) {
        lblTongTienMon.setText(tongTienMonText);
        lblTongGiamGia.setText(tongGiamGiaText);
        lblTongThue.setText(tongThueText);
        lblTongThanhToan.setText(tongThanhToanText);
    }

    private JLabel createHeaderLabel(String text, boolean isBold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, isBold ? 16 : 14));
        label.setForeground(isBold ? COLOR_TEXT : COLOR_TEXT_LIGHT);
        label.setBorder(new EmptyBorder(2, 0, 2, 0));
        return label;
    }

    private JScrollPane createTablePanel() {
        modelChiTiet = new DefaultTableModel(new String[]{"STT", "Tên món", "SL", "Đơn giá", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableChiTiet = new JTable(modelChiTiet);
        customizeTable();

        JScrollPane scrollPane = new JScrollPane(tableChiTiet);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(new MatteBorder(1, 1, 1, 1, COLOR_BORDER));
        return scrollPane;
    }

    private void customizeTable() {
        tableChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableChiTiet.setRowHeight(45);
        tableChiTiet.setGridColor(COLOR_BORDER);
        tableChiTiet.setShowGrid(true);
        tableChiTiet.setIntercellSpacing(new Dimension(1, 1));
        tableChiTiet.setSelectionBackground(new Color(219, 234, 254));
        tableChiTiet.setSelectionForeground(COLOR_TEXT);

        // Header styling
        tableChiTiet.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableChiTiet.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // Renderers
        CurrencyRenderer currencyRenderer = new CurrencyRenderer(CURRENCY_FORMAT);
        tableChiTiet.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);
        tableChiTiet.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableChiTiet.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableChiTiet.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Column widths
        tableChiTiet.getColumnModel().getColumn(0).setPreferredWidth(50);
        tableChiTiet.getColumnModel().getColumn(1).setPreferredWidth(350);
        tableChiTiet.getColumnModel().getColumn(2).setPreferredWidth(60);
        tableChiTiet.getColumnModel().getColumn(3).setPreferredWidth(130);
        tableChiTiet.getColumnModel().getColumn(4).setPreferredWidth(130);
    }

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

        cbxKhuyenMai = createKhuyenMaiComboBox();

        gbc.gridx = 0; gbc.gridy = 0;
        footer.add(createTotalLabel("Áp dụng Khuyến mãi:", false, COLOR_TEXT), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.anchor = GridBagConstraints.LINE_START;
        footer.add(cbxKhuyenMai, gbc);
        gbc.anchor = GridBagConstraints.EAST;

        gbc.gridx = 0; gbc.gridy = 1;
        footer.add(createTotalLabel("Tổng tiền món:", false, COLOR_TEXT), gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        footer.add(createTotalLabel("Tổng giảm giá (KM):", false, COLOR_WARNING), gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        footer.add(createTotalLabel("Thuế & Phí (VAT, P.vụ):", false, COLOR_TEXT), gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        footer.add(createTotalLabel("THÀNH TIỀN (Tổng phải trả):", true, COLOR_DANGER), gbc);

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

    private JComboBox<KhuyenMai> createKhuyenMaiComboBox() {
        JComboBox<KhuyenMai> comboBox = new JComboBox<>();
        comboBox.setRenderer(new KhuyenMaiRenderer());
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(250, 35));
        comboBox.setBackground(Color.WHITE);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(1, 1, 1, 1, COLOR_BORDER),
                new EmptyBorder(5, 10, 5, 10)
        ));

        KhuyenMai kmDaChon = null;
        try {
            List<KhuyenMai> dsKM = khuyenMaiDAO.getAllActiveKhuyenMai();
            for (KhuyenMai km : dsKM) {
                comboBox.addItem(km);
                if (hoaDonHienTai.getKhuyenMai() != null && hoaDonHienTai.getKhuyenMai().getMaKM() != null &&
                        km.getMaKM() != null && km.getMaKM().equals(hoaDonHienTai.getKhuyenMai().getMaKM())) {
                    kmDaChon = km;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (kmDaChon != null) {
            comboBox.setSelectedItem(kmDaChon);
        } else if (comboBox.getItemCount() > 0) { // Fix lỗi setSelectedIndex
            comboBox.setSelectedIndex(0);
        }
        return comboBox;
    }

    private JLabel createTotalLabel(String text, boolean isBold, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", isBold ? Font.BOLD : Font.PLAIN, isBold ? 18 : 14));
        label.setForeground(color);
        label.setBorder(new EmptyBorder(3, 10, 3, 10));
        return label;
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

    private void tinhVaHienThiTongTien(HoaDon hd) {
        double tongTienMon = hd.getTongTienTruocThue();
        double giamGia = hd.getTongGiamGia();
        double tongThanhToan = hoaDonDAO.tinhTongTienHoaDon(hd.getMaHD());
        double tongThueVaPhi = hoaDonDAO.tinhTongThueVaPhi(hd.getMaHD());

        updateLabels(
                CURRENCY_FORMAT.format(tongTienMon),
                CURRENCY_FORMAT.format(giamGia),
                CURRENCY_FORMAT.format(tongThueVaPhi),
                CURRENCY_FORMAT.format(tongThanhToan)
        );
    }

    // MODERN BUTTON CLASS
    private class ModernButton extends JButton {
        private Color bgColor;
        private int cornerRadius = 10;

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

            Color currentColor = getButtonColor();

            g2.setColor(currentColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

            // Vẽ chữ
            g2.setColor(isEnabled() ? Color.WHITE : Color.LIGHT_GRAY);
            FontMetrics fm = g2.getFontMetrics();
            int textX = (getWidth() - fm.stringWidth(getText())) / 2;
            int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(getText(), textX, textY);

            g2.dispose();
        }

        private Color getButtonColor() {
            if (!isEnabled()) {
                return new Color(180, 180, 180);
            } else if (getModel().isPressed()) {
                return bgColor.darker();
            } else if (getModel().isRollover()) {
                return bgColor.brighter();
            }
            return bgColor;
        }
    }

    static class CurrencyRenderer extends DefaultTableCellRenderer {
        private DecimalFormat formatter;

        public CurrencyRenderer(DecimalFormat formatter) {
            this.formatter = formatter;
            setHorizontalAlignment(JLabel.RIGHT);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Number) {
                setText(formatter.format(value));
            }
            if (isSelected) {
                cell.setBackground(new Color(219, 234, 254));
                cell.setForeground(COLOR_TEXT);
            } else {
                cell.setBackground(row % 2 == 0 ? Color.WHITE : COLOR_BG);
                cell.setForeground(COLOR_TEXT);
            }
            return cell;
        }
    }

    class KhachHangRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof KhachHang) {
                KhachHang kh = (KhachHang) value;
                setText(kh.getTenKH() + " (" + kh.getMaKH().trim() + ")");
            }
            if (isSelected) {
                setBackground(COLOR_PRIMARY);
                setForeground(Color.WHITE);
            }
            return this;
        }
    }

    class KhuyenMaiRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof KhuyenMai) {
                KhuyenMai km = (KhuyenMai) value;
                if (km.getMaKM() == null || km.getMaKM().trim().isEmpty() || km.getMaKM().trim().equalsIgnoreCase("KM00000000")) {
                    setText("Không áp dụng");
                } else {
                    setText(km.getMoTa());
                }
            }
            if (isSelected) {
                setBackground(COLOR_PRIMARY);
                setForeground(Color.WHITE);
            }
            return this;
        }
    }
}