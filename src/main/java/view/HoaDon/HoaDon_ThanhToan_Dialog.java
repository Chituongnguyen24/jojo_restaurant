package view.HoaDon;

import dao.HoaDon_DAO;
import dao.Ban_DAO;
import dao.PhieuDatBan_DAO;
import entity.*;
import enums.TrangThaiBan;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class HoaDon_ThanhToan_Dialog extends JDialog {
    
    // DAOs
    private HoaDon_DAO hoaDonDAO;
    private Ban_DAO banDAO;
    private PhieuDatBan_DAO phieuDatBanDAO;
    
    // Entities
    private HoaDon hoaDon;
    private List<ChiTietHoaDon> chiTietList;
    
    // Components
    private JTable tblChiTiet;
    private DefaultTableModel modelChiTiet;
    
    private JTextField txtMaKM;
    private JLabel lblTongTien, lblGiamGia, lblThue, lblThanhToan;
    private JComboBox<String> cboPhuongThuc;
    private JButton btnApMaKM, btnHuyMaKM, btnThanhToan, btnHuy;
    
    // Data
    private double tongTienMonAn;    // Tổng tiền món (trước KM và Thuế)
    private double tienGiam;         // Tiền giảm từ KM
    private double tienThue;         // Tiền thuế
    private double tongThanhToan;    // Tổng cuối cùng
    
    // Colors & Fonts
    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color WARNING_COLOR = new Color(251, 146, 60);
    private static final Color BG_COLOR = new Color(248, 250, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_MONEY = new Font("Segoe UI", Font.BOLD, 16);
    
    private static final DecimalFormat CURRENCY_FORMAT;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        CURRENCY_FORMAT = new DecimalFormat("#,##0", symbols);
    }
    
    /**
     * Constructor đầy đủ với các tham số tính toán
     */
    public HoaDon_ThanhToan_Dialog(Frame owner, HoaDon hoaDon, HoaDon_DAO hoaDonDAO,
                                     double tongTienMonAn, double tienGiam, double tienThue, double tongThanhToan,
                                     List<ChiTietHoaDon> chiTietList) {
        super(owner, "Thanh toán hóa đơn - " + hoaDon.getMaHD(), true);
        
        // Khởi tạo
        this.hoaDon = hoaDon;
        this.hoaDonDAO = hoaDonDAO;
        this.banDAO = new Ban_DAO();
        this.phieuDatBanDAO = new PhieuDatBan_DAO();
        this.chiTietList = chiTietList;
        
        // Dữ liệu tính toán
        this.tongTienMonAn = tongTienMonAn;
        this.tienGiam = tienGiam;
        this.tienThue = tienThue;
        this.tongThanhToan = tongThanhToan;
        
        // Thiết lập UI
        setSize(900, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(BG_COLOR);
        
        add(createHeader(), BorderLayout.NORTH);
        add(createContent(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);
        
        setupEvents();
        loadData();
    }
    
    // ===== UI CREATION =====
    
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_COLOR);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(20, 30, 20, 30)
        ));
        
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Thanh toán hóa đơn");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);
        
        JLabel subtitleLabel = new JLabel("Mã hóa đơn: " + hoaDon.getMaHD() + " | Bàn: " + hoaDon.getBan().getMaBan());
        subtitleLabel.setFont(FONT_NORMAL);
        subtitleLabel.setForeground(TEXT_SECONDARY);
        
        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(subtitleLabel);
        
        header.add(leftPanel, BorderLayout.WEST);
        
        return header;
    }
    
    private JPanel createContent() {
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(BG_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Top: Chi tiết món
        JPanel tablePanel = createTablePanel();
        
        // Bottom: Tính tiền & Khuyến mãi
        JPanel calculationPanel = createCalculationPanel();
        
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        contentPanel.add(calculationPanel, BorderLayout.SOUTH);
        
        return contentPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Chi tiết món ăn");
        lblTitle.setFont(FONT_HEADER);
        lblTitle.setForeground(TEXT_PRIMARY);
        lblTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        String[] cols = {"STT", "Tên món", "Đơn giá", "SL", "Thành tiền"};
        modelChiTiet = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tblChiTiet = new JTable(modelChiTiet);
        tblChiTiet.setRowHeight(40);
        tblChiTiet.setFont(FONT_NORMAL);
        tblChiTiet.setSelectionBackground(new Color(219, 234, 254));
        tblChiTiet.setGridColor(BORDER_COLOR);
        
        tblChiTiet.getTableHeader().setFont(FONT_HEADER);
        tblChiTiet.getTableHeader().setBackground(new Color(241, 245, 249));
        tblChiTiet.getTableHeader().setForeground(TEXT_PRIMARY);
        tblChiTiet.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        // Căn giữa các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        
        tblChiTiet.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(300);
        tblChiTiet.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblChiTiet.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(60);
        tblChiTiet.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        tblChiTiet.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        JScrollPane scroll = new JScrollPane(tblChiTiet);
        scroll.setBorder(new LineBorder(BORDER_COLOR));
        scroll.getViewport().setBackground(CARD_COLOR);
        
        panel.add(lblTitle, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createCalculationPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setOpaque(false);
        
        // Left: Khuyến mãi
        JPanel leftPanel = createKhuyenMaiPanel();
        leftPanel.setPreferredSize(new Dimension(350, 200));
        
        // Right: Tính tiền
        JPanel rightPanel = createTinhTienPanel();
        
        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createKhuyenMaiPanel() {
        JPanel card = createCard("Mã khuyến mãi");
        card.setLayout(new BorderLayout(10, 10));
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        inputPanel.setOpaque(false);
        
        txtMaKM = new JTextField(15);
        txtMaKM.setFont(FONT_NORMAL);
        txtMaKM.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR),
            new EmptyBorder(8, 12, 8, 12)
        ));
        
        btnApMaKM = createButton("Áp dụng", SUCCESS_COLOR);
        btnHuyMaKM = createButton("Hủy mã", DANGER_COLOR);
        btnHuyMaKM.setEnabled(false);
        
        inputPanel.add(new JLabel("Mã KM:"));
        inputPanel.add(txtMaKM);
        inputPanel.add(btnApMaKM);
        inputPanel.add(btnHuyMaKM);
        
        card.add(inputPanel, BorderLayout.NORTH);
        
        return card;
    }
    
    private JPanel createTinhTienPanel() {
        JPanel card = createCard("Tổng cộng");
        card.setLayout(new GridLayout(5, 2, 10, 12));
        
        lblTongTien = new JLabel(CURRENCY_FORMAT.format(tongTienMonAn) + " VNĐ");
        lblGiamGia = new JLabel(CURRENCY_FORMAT.format(tienGiam) + " VNĐ");
        lblThue = new JLabel(CURRENCY_FORMAT.format(tienThue) + " VNĐ");
        lblThanhToan = new JLabel(CURRENCY_FORMAT.format(tongThanhToan) + " VNĐ");
        
        lblTongTien.setFont(FONT_MONEY);
        lblGiamGia.setFont(FONT_MONEY);
        lblThue.setFont(FONT_MONEY);
        lblThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 20));
        
        lblTongTien.setHorizontalAlignment(JLabel.RIGHT);
        lblGiamGia.setHorizontalAlignment(JLabel.RIGHT);
        lblThue.setHorizontalAlignment(JLabel.RIGHT);
        lblThanhToan.setHorizontalAlignment(JLabel.RIGHT);
        
        lblGiamGia.setForeground(DANGER_COLOR);
        lblThanhToan.setForeground(SUCCESS_COLOR);
        
        card.add(createLabel("Tổng tiền món:"));
        card.add(lblTongTien);
        
        card.add(createLabel("Giảm giá:"));
        card.add(lblGiamGia);
        
        card.add(createLabel("Thuế VAT:"));
        card.add(lblThue);
        
        card.add(new JSeparator());
        card.add(new JSeparator());
        
        card.add(createLabel("TỔNG THANH TOÁN:"));
        card.add(lblThanhToan);
        
        return card;
    }
    
    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(CARD_COLOR);
        footer.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(20, 30, 20, 30)
        ));
        
        // Left: Phương thức thanh toán
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        JLabel lblPhuongThuc = new JLabel("Phương thức:");
        lblPhuongThuc.setFont(FONT_HEADER);
        
        cboPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản", "Thẻ"});
        cboPhuongThuc.setFont(FONT_NORMAL);
        cboPhuongThuc.setPreferredSize(new Dimension(150, 35));
        
        leftPanel.add(lblPhuongThuc);
        leftPanel.add(cboPhuongThuc);
        
        // Right: Buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        btnHuy = createButton("Hủy", new Color(148, 163, 184));
        btnThanhToan = createButton("Thanh toán", SUCCESS_COLOR);
        
        btnHuy.setPreferredSize(new Dimension(120, 45));
        btnThanhToan.setPreferredSize(new Dimension(150, 45));
        
        rightPanel.add(btnHuy);
        rightPanel.add(btnThanhToan);
        
        footer.add(leftPanel, BorderLayout.WEST);
        footer.add(rightPanel, BorderLayout.EAST);
        
        return footer;
    }
    
    // ===== UTILITY METHODS =====
    
    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setBackground(CARD_COLOR);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        if (title != null) {
            card.setLayout(new BorderLayout(0, 15));
            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(FONT_HEADER);
            lblTitle.setForeground(TEXT_PRIMARY);
            card.add(lblTitle, BorderLayout.NORTH);
        }
        
        return card;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_NORMAL);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color color = bgColor;
                if (!isEnabled()) {
                    color = new Color(203, 213, 225);
                } else if (getModel().isPressed()) {
                    color = bgColor.darker();
                } else if (getModel().isRollover()) {
                    color = bgColor.brighter();
                }
                
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        btn.setForeground(Color.WHITE);
        btn.setFont(FONT_HEADER);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 35));
        
        return btn;
    }
    
    // ===== EVENT HANDLERS =====
    
    private void setupEvents() {
        btnApMaKM.addActionListener(e -> apDungMaKhuyenMai());
        btnHuyMaKM.addActionListener(e -> huyMaKhuyenMai());
        btnThanhToan.addActionListener(e -> thanhToan());
        btnHuy.addActionListener(e -> dispose());
    }
    
    // ===== DATA METHODS =====
    
    private void loadData() {
        modelChiTiet.setRowCount(0);
        
        int stt = 1;
        for (ChiTietHoaDon ct : chiTietList) {
            double donGia = ct.getDonGiaBan();
            int soLuong = ct.getSoLuong();
            double thanhTien = donGia * soLuong;
            
            modelChiTiet.addRow(new Object[]{
                stt++,
                ct.getMonAn() != null ? ct.getMonAn().getTenMonAn() : "N/A",
                CURRENCY_FORMAT.format(donGia),
                soLuong,
                CURRENCY_FORMAT.format(thanhTien)
            });
        }
        
        // Hiển thị mã KM nếu đã có
        if (hoaDon.getKhuyenMai() != null && hoaDon.getKhuyenMai().getMaKM() != null) {
            txtMaKM.setText(hoaDon.getKhuyenMai().getMaKM());
            txtMaKM.setEditable(false);
            btnApMaKM.setEnabled(false);
            btnHuyMaKM.setEnabled(true);
        }
    }
    
    private void apDungMaKhuyenMai() {
        String maKM = txtMaKM.getText().trim();
        if (maKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập mã khuyến mãi!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Gọi hàm trong DAO
        String ketQua = hoaDonDAO.apDungMaKhuyenMai(hoaDon.getMaHD(), maKM);
        
        if ("OK".equals(ketQua)) {
            JOptionPane.showMessageDialog(this, 
                "Áp dụng mã khuyến mãi thành công!", 
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            // Reload dữ liệu
            reloadThanhToan();
            
            txtMaKM.setEditable(false);
            btnApMaKM.setEnabled(false);
            btnHuyMaKM.setEnabled(true);
        } else {
            JOptionPane.showMessageDialog(this, ketQua, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void huyMaKhuyenMai() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn hủy mã khuyến mãi?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = hoaDonDAO.huyMaKhuyenMai(hoaDon.getMaHD());
            
            if (success) {
                JOptionPane.showMessageDialog(this, "Đã hủy mã khuyến mãi!");
                
                // Reload
                reloadThanhToan();
                
                txtMaKM.setText("");
                txtMaKM.setEditable(true);
                btnApMaKM.setEnabled(true);
                btnHuyMaKM.setEnabled(false);
            }
        }
    }
    
    private void reloadThanhToan() {
        // Lấy lại hóa đơn từ DB
        hoaDon = hoaDonDAO.findByMaHD(hoaDon.getMaHD());
        
        // Tính lại các giá trị
        tongTienMonAn = hoaDon.getTongTienTruocThue();
        tienGiam = hoaDon.getTongGiamGia();
        tongThanhToan = hoaDonDAO.tinhTongTienHoaDon(hoaDon.getMaHD());
        tienThue = tongThanhToan - (tongTienMonAn - tienGiam);
        
        // Cập nhật UI
        lblTongTien.setText(CURRENCY_FORMAT.format(tongTienMonAn) + " VNĐ");
        lblGiamGia.setText(CURRENCY_FORMAT.format(tienGiam) + " VNĐ");
        lblThue.setText(CURRENCY_FORMAT.format(tienThue) + " VNĐ");
        lblThanhToan.setText(CURRENCY_FORMAT.format(tongThanhToan) + " VNĐ");
    }
    
    private void thanhToan() {
        String phuongThuc = (String) cboPhuongThuc.getSelectedItem();
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            String.format("Xác nhận thanh toán %.0f VNĐ bằng %s?", tongThanhToan, phuongThuc), 
            "Xác nhận thanh toán", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = hoaDonDAO.thanhToanHoaDon(hoaDon.getMaHD(), phuongThuc);
            
            if (success) {
                // Cập nhật trạng thái bàn về TRỐNG
                capNhatTrangThaiBan();
                
                // Cập nhật phiếu đặt (nếu có)
                capNhatPhieuDatBan();
                
                JOptionPane.showMessageDialog(this, 
                    "Thanh toán thành công!\nIn hóa đơn...", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                // TODO: Mở dialog in hóa đơn
                
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi thanh toán! Vui lòng thử lại.", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void capNhatTrangThaiBan() {
        Ban ban = hoaDon.getBan();
        if (ban != null) {
            ban.setTrangThai(TrangThaiBan.TRONG.name());
            banDAO.capNhatBan(ban);
        }
    }
    
    private void capNhatPhieuDatBan() {
        if (hoaDon.getPhieuDatBan() != null && hoaDon.getPhieuDatBan().getMaPhieu() != null) {
            PhieuDatBan phieu = phieuDatBanDAO.getPhieuDatBanById(hoaDon.getPhieuDatBan().getMaPhieu());
            if (phieu != null) {
                phieu.setTrangThaiPhieu("Đã thanh toán");
                phieuDatBanDAO.updatePhieuDatBan(phieu);
            }
        }
    }
}