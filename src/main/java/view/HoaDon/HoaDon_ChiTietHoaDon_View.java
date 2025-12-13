package view.HoaDon;

import dao.*;
import entity.*;
import enums.TrangThaiBan;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
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

    /* ===================== PALETTE NHÀ HÀNG SANG ===================== */
    private static final Color COLOR_PRIMARY = new Color(30, 41, 59);      // Navy
    private static final Color COLOR_ACCENT = new Color(202, 138, 4);      // Gold
    private static final Color COLOR_SUCCESS = new Color(21, 128, 61);     // Green
    private static final Color COLOR_DANGER = new Color(185, 28, 28);      // Red trầm
    private static final Color COLOR_BG = new Color(248, 250, 252);        // Xám rất nhạt
    private static final Color COLOR_CARD = Color.WHITE;
    private static final Color COLOR_BORDER = new Color(226, 232, 240);
    private static final Color COLOR_TEXT = new Color(30, 41, 59);
    private static final Color COLOR_TEXT_LIGHT = new Color(100, 116, 139);

    private JTable tableChiTiet;
    private DefaultTableModel modelChiTiet;

    private HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    private Ban_DAO banDAO = new Ban_DAO();
    private PhieuDatBan_DAO pbdDAO = new PhieuDatBan_DAO();
    private KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO();
    private Thue_DAO thueDAO = new Thue_DAO();

    private HoaDon hoaDonHienTai;

    private JComboBox<KhachHang> cbxKhachHang;
    private JComboBox<KhuyenMai> cbxKhuyenMai;
    private JComboBox<String> cbxPhuongThucTT;

    private JLabel lblTongTienMon, lblTongGiamGia, lblTongThue, lblTongThanhToan;
    private JButton btnThanhToan, btnDong;

    private static final DecimalFormat CURRENCY_FORMAT;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        CURRENCY_FORMAT = new DecimalFormat("###,### VNĐ", symbols);
    }

    public HoaDon_ChiTietHoaDon_View(Frame owner, HoaDon hoaDon) {
        super(owner, "Chi tiết hóa đơn • " + hoaDon.getMaHD(), true);
        this.hoaDonHienTai = hoaDon;

        setSize(1050, 760);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_BG);

        JPanel main = new JPanel(new BorderLayout(15, 15));
        main.setBorder(new EmptyBorder(20, 25, 20, 25));
        main.setBackground(COLOR_BG);

        main.add(createHeader(), BorderLayout.NORTH);
        main.add(createTablePanel(), BorderLayout.CENTER);
        main.add(createFooter(), BorderLayout.SOUTH);

        add(main, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadChiTietData(hoaDon.getMaHD());
        tinhVaHienThiTongTien(hoaDon);
    }

    /* ===================== HEADER ===================== */
    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout(20, 10));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 3, 0, COLOR_ACCENT),
                new EmptyBorder(15, 20, 15, 20)));

        JLabel lblTitle = new JLabel("HÓA ĐƠN THANH TOÁN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(COLOR_PRIMARY);

        JLabel lblSub = new JLabel("Mã HĐ: " + hoaDonHienTai.getMaHD());
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(COLOR_TEXT_LIGHT);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(lblTitle);
        left.add(Box.createVerticalStrut(5));
        left.add(lblSub);

        cbxKhachHang = new JComboBox<>();
        cbxKhachHang.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbxKhachHang.setPreferredSize(new Dimension(260, 36));
        cbxKhachHang.setRenderer(new KhachHangRenderer());
        cbxKhachHang.setBorder(new LineBorder(COLOR_BORDER));
        khachHangDAO.getAllKhachHang().forEach(cbxKhachHang::addItem);
        cbxKhachHang.setSelectedItem(hoaDonHienTai.getKhachHang());

        JPanel right = new JPanel(new BorderLayout(5, 5));
        right.setOpaque(false);
        right.add(new JLabel("Khách hàng:"), BorderLayout.NORTH);
        right.add(cbxKhachHang, BorderLayout.CENTER);

        panel.add(left, BorderLayout.WEST);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    /* ===================== TABLE ===================== */
    private JScrollPane createTablePanel() {
        modelChiTiet = new DefaultTableModel(
                new String[]{"STT", "Món ăn", "SL", "Đơn giá", "Thành tiền"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tableChiTiet = new JTable(modelChiTiet);
        tableChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableChiTiet.setRowHeight(44);
        tableChiTiet.setShowVerticalLines(false);
        tableChiTiet.setGridColor(COLOR_BORDER);

        JTableHeader h = tableChiTiet.getTableHeader();
        h.setFont(new Font("Segoe UI", Font.BOLD, 14));
        h.setBackground(new Color(241, 245, 249));
        h.setForeground(COLOR_TEXT);
        h.setBorder(new MatteBorder(0, 0, 1, 0, COLOR_BORDER));

        CurrencyRenderer cr = new CurrencyRenderer(CURRENCY_FORMAT);
        tableChiTiet.getColumnModel().getColumn(3).setCellRenderer(cr);
        tableChiTiet.getColumnModel().getColumn(4).setCellRenderer(cr);

        JScrollPane sp = new JScrollPane(tableChiTiet);
        sp.setBorder(new LineBorder(COLOR_BORDER));
        return sp;
    }

    /* ===================== FOOTER ===================== */
    private JPanel createFooter() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new CompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(20, 20, 20, 20)));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 10, 6, 10);
        g.anchor = GridBagConstraints.EAST;

        lblTongTienMon = createMoneyLabel(false, COLOR_TEXT);
        lblTongGiamGia = createMoneyLabel(false, COLOR_ACCENT);
        lblTongThue = createMoneyLabel(false, COLOR_TEXT);
        lblTongThanhToan = createMoneyLabel(true, COLOR_ACCENT);

        addRow(panel, g, 0, "Tổng tiền món:", lblTongTienMon);
        addRow(panel, g, 1, "Giảm giá:", lblTongGiamGia);
        addRow(panel, g, 2, "Thuế & Phí:", lblTongThue);
        addRow(panel, g, 3, "TỔNG THANH TOÁN:", lblTongThanhToan);

        return panel;
    }

    private void addRow(JPanel p, GridBagConstraints g, int y, String label, JLabel value) {
        g.gridx = 0; g.gridy = y;
        p.add(new JLabel(label), g);
        g.gridx = 1;
        p.add(value, g);
    }

    private JLabel createMoneyLabel(boolean big, Color c) {
        JLabel l = new JLabel("0 VNĐ");
        l.setFont(new Font("Segoe UI", big ? Font.BOLD : Font.PLAIN, big ? 22 : 14));
        l.setForeground(c);
        return l;
    }

    /* ===================== BUTTON ===================== */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 15));
        panel.setBackground(COLOR_CARD);
        panel.setBorder(new MatteBorder(1, 0, 0, 0, COLOR_BORDER));

        btnDong = new ModernButton("Đóng", COLOR_DANGER);
        btnThanhToan = new ModernButton("Xác nhận thanh toán", COLOR_SUCCESS);

        panel.add(btnDong);
        panel.add(btnThanhToan);
        btnDong.addActionListener(e -> dispose());
        return panel;
    }

    /* ===================== DATA ===================== */
    private void loadChiTietData(String maHD) {
        modelChiTiet.setRowCount(0);
        int i = 1;
        for (ChiTietHoaDon ct : hoaDonDAO.getChiTietHoaDonForPrint(maHD)) {
            double tt = ct.getSoLuong() * ct.getDonGiaBan();
            modelChiTiet.addRow(new Object[]{
                    i++, ct.getMonAn().getTenMonAn(), ct.getSoLuong(),
                    ct.getDonGiaBan(), tt
            });
        }
    }

    private void tinhVaHienThiTongTien(HoaDon hd) {
        lblTongTienMon.setText(CURRENCY_FORMAT.format(hd.getTongTienTruocThue()));
        lblTongGiamGia.setText(CURRENCY_FORMAT.format(hd.getTongGiamGia()));
        lblTongThue.setText(CURRENCY_FORMAT.format(
                hoaDonDAO.tinhTongThueVaPhi(hd.getMaHD())));
        lblTongThanhToan.setText(CURRENCY_FORMAT.format(
                hoaDonDAO.tinhTongTienHoaDon(hd.getMaHD())));
    }

    /* ===================== CUSTOM ===================== */
    private class ModernButton extends JButton {
        private Color color;
        public ModernButton(String text, Color c) {
            super(text);
            this.color = c;
            setPreferredSize(new Dimension(180, 42));
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setContentAreaFilled(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
            super.paintComponent(g);
        }
    }

    static class CurrencyRenderer extends DefaultTableCellRenderer {
        DecimalFormat f;
        CurrencyRenderer(DecimalFormat f) {
            this.f = f;
            setHorizontalAlignment(RIGHT);
        }
        public Component getTableCellRendererComponent(
                JTable t, Object v, boolean s, boolean fcs, int r, int c) {
            super.getTableCellRendererComponent(t, v, s, fcs, r, c);
            setText(v instanceof Number ? f.format(v) : "");
            return this;
        }
    }

    class KhachHangRenderer extends DefaultListCellRenderer {
        public Component getListCellRendererComponent(
                JList<?> l, Object v, int i, boolean s, boolean f) {
            super.getListCellRendererComponent(l, v, i, s, f);
            if (v instanceof KhachHang kh)
                setText(kh.getTenKH() + " (" + kh.getMaKH() + ")");
            return this;
        }
    }
}
