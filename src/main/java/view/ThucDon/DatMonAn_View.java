package view.ThucDon;

import dao.Ban_DAO;
import dao.HoaDon_DAO; // SỬA: Thêm DAO Hóa Đơn
import dao.PhieuDatBan_DAO;
import entity.Ban;
import entity.ChiTietHoaDon; // SỬA: Thêm
import entity.HoaDon; // SỬA: Thêm
import entity.PhieuDatBan;
import enums.TrangThaiBan;
import view.HoaDon.HoaDon_ThanhToan_Dialog; // SỬA: Thêm import

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import dao.MonAn_DAO;
import entity.MonAn;
import java.util.EventObject;
import java.awt.geom.RoundRectangle2D;

public class DatMonAn_View extends JPanel { 
    
    // DAOs
    private Ban_DAO banDAO;
    private PhieuDatBan_DAO datBanDAO;
    private MonAn_DAO monAnDAO; 
    private HoaDon_DAO hoaDonDAO; // SỬA: Thêm

    // Data
    private PhieuDatBan phieuDatBanHienTai; 

    // Components
    private JTable tblPhieuDat; 
    private JTable tblChiTietMon; 
    private DefaultTableModel modelPhieuDat;
    private DefaultTableModel modelChiTietMon;
    
    // SỬA: Đổi tên/Thêm nút
    private JButton btnGoiThemMon, btnHuyMon, btnLamMoi, btnThanhToan;
    private JLabel lblThongTinPhieu;

    // Colors
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color WARNING_COLOR = new Color(251, 146, 60);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);


    public DatMonAn_View() {
        this.banDAO = new Ban_DAO();
        this.datBanDAO = new PhieuDatBan_DAO(); 
        this.monAnDAO = new MonAn_DAO(); 
        this.hoaDonDAO = new HoaDon_DAO(); // SỬA: Khởi tạo

        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_COLOR);

        JPanel mainContentPanel = new JPanel(new BorderLayout(0, 0));
        mainContentPanel.setBackground(BACKGROUND_COLOR);
        
        mainContentPanel.add(createHeader(), BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            createPhieuDatPanel(), // Panel bên trái
            createChiTietPanel() // Panel bên phải
        );
        splitPane.setDividerLocation(500); 
        splitPane.setBorder(new EmptyBorder(20, 30, 20, 30)); 
        splitPane.setOpaque(false);
        
        mainContentPanel.add(splitPane, BorderLayout.CENTER);
        mainContentPanel.add(createFooter(), BorderLayout.SOUTH);

        JScrollPane masterScroll = new JScrollPane(mainContentPanel);
        masterScroll.setBorder(null);
        masterScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        masterScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); 
        masterScroll.getVerticalScrollBar().setUnitIncrement(16);

        add(masterScroll, BorderLayout.CENTER); 

        setupEvents();
        taiDanhSachPhieuDat();
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(20, 30, 20, 30)
        ));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Quản lý Đặt món (Cho phiếu đặt trước)");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Chọn phiếu đặt bàn để thêm/hủy món");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(subtitleLabel);

        btnLamMoi = createButton("Làm mới", PRIMARY_COLOR);
        btnLamMoi.setPreferredSize(new Dimension(130, 45));

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(btnLamMoi, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createPhieuDatPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JPanel headerCard = createCardHeader("Danh sách Phiếu Đặt Bàn", "Chọn phiếu để xem chi tiết");
        
        modelPhieuDat = new DefaultTableModel(new String[]{"Mã Phiếu", "Bàn", "Khách Hàng", "Giờ Hẹn", "Trạng Thái"}, 0) { 
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return false;
            }
        };
        tblPhieuDat = createStyledTable(modelPhieuDat);
        tblPhieuDat.setRowHeight(50);
        
        // Custom renderer
        tblPhieuDat.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setHorizontalAlignment(LEFT);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                if (column == 2) { // Tên khách
                    setFont(new Font("Segoe UI", Font.BOLD, 13));
                }
                
                if (column == 4) { // Trạng thái
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    if (value.equals("Đã đến")) {
                        setForeground(SUCCESS_COLOR);
                    } else if (value.equals("Chưa đến")) {
                        setForeground(WARNING_COLOR);
                    } else {
                        setForeground(DANGER_COLOR);
                    }
                }
                
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground()); 
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    if (column != 4) setForeground(TEXT_PRIMARY); // Giữ màu trạng thái
                }
                
                setBorder(new EmptyBorder(10, 15, 10, 15));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblPhieuDat);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel cardPanel = createCard();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(headerCard, BorderLayout.NORTH);
        panel.add(cardPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createChiTietPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JPanel infoCard = createInfoCard();
        JPanel tableCard = createDonDatTableCard();

        panel.add(infoCard, BorderLayout.NORTH);
        panel.add(tableCard, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createInfoCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(15, 0));
        card.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel leftInfo = new JPanel();
        leftInfo.setLayout(new BoxLayout(leftInfo, BoxLayout.Y_AXIS));
        leftInfo.setOpaque(false);

        lblThongTinPhieu = new JLabel("Chưa chọn phiếu đặt");
        lblThongTinPhieu.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblThongTinPhieu.setForeground(TEXT_PRIMARY);

        JLabel lblStatus = new JLabel("Vui lòng chọn 1 phiếu từ danh sách bên trái");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStatus.setForeground(TEXT_SECONDARY);

        leftInfo.add(lblThongTinPhieu);
        leftInfo.add(Box.createVerticalStrut(5));
        leftInfo.add(lblStatus);

        card.add(leftInfo, BorderLayout.WEST);

        return card;
    }

    private JPanel createDonDatTableCard() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        JPanel header = createCardHeader("Chi tiết món đặt trước", "Danh sách món trong phiếu");

        modelChiTietMon = new DefaultTableModel(
            new String[]{"Mã món", "Tên món", "SL", "Đơn Giá", "Ghi chú"}, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return false;
            }
        };
        tblChiTietMon = createStyledTable(modelChiTietMon);
        tblChiTietMon.setRowHeight(50);
        
        tblChiTietMon.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblChiTietMon.getColumnModel().getColumn(1).setPreferredWidth(250);
        tblChiTietMon.getColumnModel().getColumn(2).setPreferredWidth(40);
        tblChiTietMon.getColumnModel().getColumn(3).setPreferredWidth(100);
        tblChiTietMon.getColumnModel().getColumn(4).setPreferredWidth(150);

        tblChiTietMon.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                if (column == 1) { // Tên món
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else if (column == 2) { // Số lượng
                    setHorizontalAlignment(CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else if (column == 3) { // Đơn giá
                    setHorizontalAlignment(RIGHT);
                } else {
                    setHorizontalAlignment(LEFT);
                }
                
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    setForeground(TEXT_PRIMARY);
                }
                
                setBorder(new EmptyBorder(10, 15, 10, 15));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblChiTietMon);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel cardPanel = createCard();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(header, BorderLayout.NORTH);
        panel.add(cardPanel, BorderLayout.CENTER);

        return panel;
    }

    // ===== HÀM ĐÃ SỬA (Thêm 3 nút) =====
    private JPanel createFooter() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footerPanel.setBackground(CARD_COLOR);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(20, 30, 20, 30)
        ));

        Dimension btnSize = new Dimension(170, 45);

        btnGoiThemMon = createButton("Gọi thêm món", PRIMARY_COLOR);
        btnGoiThemMon.setPreferredSize(btnSize);

        btnHuyMon = createButton("Hủy món đã chọn", DANGER_COLOR);
        btnHuyMon.setPreferredSize(btnSize);
        
        btnThanhToan = createButton("Thanh toán", SUCCESS_COLOR);
        btnThanhToan.setPreferredSize(btnSize);
        
        btnGoiThemMon.setEnabled(false);
        btnHuyMon.setEnabled(false);
        btnThanhToan.setEnabled(false); // Tắt ban đầu

        footerPanel.add(btnGoiThemMon);
        footerPanel.add(btnHuyMon);
        footerPanel.add(btnThanhToan);

        return footerPanel;
    }

    // ===== UTILITY METHODS =====

    private JPanel createCard() {
        JPanel card = new JPanel();
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(0, 0, 0, 0)
        ));
        return card;
    }

    private JPanel createCardHeader(String title, String subtitle) {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(CARD_COLOR);
        header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(20, 25, 15, 25)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(TEXT_PRIMARY);

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(TEXT_SECONDARY);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(3));
        header.add(lblSubtitle);

        return header;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(50);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(TEXT_PRIMARY); // SỬA: Giữ màu chữ đen
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(new Color(241,245,249));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 45));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        return table;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (!isEnabled()) {
                    g2.setColor(new Color(203, 213, 225));
                } else if (getModel().isPressed()) {
                    g2.setColor(darkerColor(bgColor));
                } else if (getModel().isRollover()) {
                    g2.setColor(brighterColor(bgColor));
                } else {
                    g2.setColor(bgColor);
                }
                
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }

    private Color darkerColor(Color c) {
        return new Color(
            Math.max((int)(c.getRed() * 0.8), 0),
            Math.max((int)(c.getGreen() * 0.8), 0),
            Math.max((int)(c.getBlue() * 0.8), 0)
        );
    }

    private Color brighterColor(Color c) {
        return new Color(
            Math.min((int)(c.getRed() * 1.1), 255),
            Math.min((int)(c.getGreen() * 1.1), 255),
            Math.min((int)(c.getBlue() * 1.1), 255)
        );
    }

    // ===== EVENT HANDLERS =====

    private void setupEvents() {
        tblPhieuDat.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblPhieuDat.getSelectedRow() != -1) {
                xuLyChonPhieu();
            }
        });
        
        btnGoiThemMon.addActionListener(e -> moDialogDatMon());
        btnHuyMon.addActionListener(e -> xuLyHuyMon());
        btnThanhToan.addActionListener(e -> xuLyThanhToan()); // SỬA: Thêm sự kiện
        btnLamMoi.addActionListener(e -> taiDanhSachPhieuDat());
    }

    // ===== DATA METHODS =====
    
    // ===== HÀM ĐÃ SỬA (Load tất cả) =====
    private void taiDanhSachPhieuDat() {
        modelPhieuDat.setRowCount(0);
        modelChiTietMon.setRowCount(0);
        phieuDatBanHienTai = null;
        lblThongTinPhieu.setText("Chưa chọn phiếu đặt");
        btnGoiThemMon.setEnabled(false);
        btnHuyMon.setEnabled(false);
        btnThanhToan.setEnabled(false); // SỬA: Tắt nút
        
        List<PhieuDatBan> dsPhieu = datBanDAO.getAllPhieuDatBan(); 
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yy");
        
        for (PhieuDatBan phieu : dsPhieu) {
            // SỬA: Bỏ IF, load tất cả
            modelPhieuDat.addRow(new Object[]{
                phieu.getMaPhieu().trim(),
                phieu.getBan().getMaBan().trim(),
                phieu.getKhachHang().getTenKH(),
                phieu.getThoiGianDenHen().format(formatter),
                phieu.getTrangThaiPhieu() // Thêm trạng thái
            });
        }
    }

    // ===== HÀM ĐÃ SỬA (Logic bật/tắt nút) =====
    private void xuLyChonPhieu() {
        int row = tblPhieuDat.getSelectedRow();
        if (row == -1) return;
        
        String maPhieu = (String) modelPhieuDat.getValueAt(row, 0);
        String tenBan = (String) modelPhieuDat.getValueAt(row, 1);
        String tenKhach = (String) modelPhieuDat.getValueAt(row, 2);

        this.phieuDatBanHienTai = datBanDAO.getPhieuDatBanById(maPhieu);
        
        if (this.phieuDatBanHienTai != null) {
            lblThongTinPhieu.setText("Phiếu: " + maPhieu.trim() + " (Bàn: " + tenBan + " - Khách: " + tenKhach + ")");
            taiDonDatMon(maPhieu);
            
            // SỬA: Logic bật/tắt nút
            String trangThai = this.phieuDatBanHienTai.getTrangThaiPhieu();
            
            if (trangThai.equals("Chưa đến")) {
                 btnGoiThemMon.setEnabled(true);
                 btnHuyMon.setEnabled(true);
                 btnThanhToan.setEnabled(false); // Không thể thanh toán khi chưa đến
            } else if (trangThai.equals("Đã đến")) {
                 btnGoiThemMon.setEnabled(true);
                 btnHuyMon.setEnabled(true);
                 btnThanhToan.setEnabled(true); // Có thể thanh toán
            } else {
                 // Đã thanh toán, Không đến, v.v.
                 btnGoiThemMon.setEnabled(false);
                 btnHuyMon.setEnabled(false);
                 btnThanhToan.setEnabled(false);
            }
        } else {
            lblThongTinPhieu.setText("Lỗi: Không tìm thấy phiếu " + maPhieu);
            modelChiTietMon.setRowCount(0);
            btnGoiThemMon.setEnabled(false);
            btnHuyMon.setEnabled(false);
            btnThanhToan.setEnabled(false);
        }
    }

    private void taiDonDatMon(String maPhieu) {
        modelChiTietMon.setRowCount(0);
        List<Object[]> dsMon = datBanDAO.getChiTietTheoMaPhieu(maPhieu); 
        
        for (Object[] row : dsMon) {
            MonAn monAn = monAnDAO.getMonAnTheoMa((String) row[0]);
            double donGia = (monAn != null) ? monAn.getDonGia() : 0.0;
            
            modelChiTietMon.addRow(new Object[]{
                row[0], // MaMon
                row[1], // TenMon
                row[2], // SoLuong
                String.format("%,.0f", donGia), // Đơn giá
                row[3]  // GhiChu
            });
        }
    }

    private void moDialogDatMon() {
        if (phieuDatBanHienTai == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một phiếu đặt bàn từ danh sách!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ChonMon_Dialog dialog = new ChonMon_Dialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            phieuDatBanHienTai
        );
        dialog.setVisible(true);
        
        taiDonDatMon(phieuDatBanHienTai.getMaPhieu());
    }

    private void xuLyHuyMon() {
        int selectedRow = tblChiTietMon.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một món trong 'Chi tiết món' để hủy!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (phieuDatBanHienTai == null) return;

        String maMonAn = (String) modelChiTietMon.getValueAt(selectedRow, 0);
        String tenMonAn = (String) modelChiTietMon.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn HỦY món: " + tenMonAn + "?", 
            "Xác nhận hủy", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = datBanDAO.deleteChiTiet(phieuDatBanHienTai.getMaPhieu(), maMonAn);
            if (success) {
                JOptionPane.showMessageDialog(this, "Hủy món thành công!");
                taiDonDatMon(phieuDatBanHienTai.getMaPhieu());
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi hủy món!", 
                    "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // ===== HÀM MỚI (Xử lý thanh toán) =====
    private void xuLyThanhToan() {
        if (phieuDatBanHienTai == null || !phieuDatBanHienTai.getTrangThaiPhieu().equals("Đã đến")) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể thanh toán các phiếu 'Đã đến'!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Ban ban = phieuDatBanHienTai.getBan();
        if (ban == null) {
             JOptionPane.showMessageDialog(this, "Lỗi: Phiếu này không liên kết với bàn nào.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Frame mainFrame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, this);

        try {
            // 1. LẤY HOẶC TẠO HÓA ĐƠN
            HoaDon hoaDonHienTai = hoaDonDAO.getHoaDonByBanChuaThanhToan(ban.getMaBan());

            if (hoaDonHienTai == null) {
                // Nếu chưa có HĐ, tạo HĐ từ PĐB
                // TODO: Cần lấy mã NV đăng nhập thay vì "NVTT001"
                String maNV = phieuDatBanHienTai.getNhanVien() != null ? phieuDatBanHienTai.getNhanVien().getMaNhanVien() : "NVTT001";
                
                boolean taoHoaDonOK = hoaDonDAO.taoHoaDonTuPhieuDat(phieuDatBanHienTai, maNV);
                if (!taoHoaDonOK) {
                    JOptionPane.showMessageDialog(this, "Lỗi khi tạo hóa đơn từ phiếu đặt bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                hoaDonHienTai = hoaDonDAO.getHoaDonByBanChuaThanhToan(ban.getMaBan());
            }

            if (hoaDonHienTai == null) {
                JOptionPane.showMessageDialog(this, "Không thể lấy thông tin hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 2. TÍNH TOÁN CÁC GIÁ TRỊ
            double tongTienMonAn = hoaDonHienTai.getTongTienTruocThue();
            double tienGiam = hoaDonHienTai.getTongGiamGia();
            double tongThanhToan = hoaDonDAO.tinhTongTienHoaDon(hoaDonHienTai.getMaHD());
            double tienThue = tongThanhToan - (tongTienMonAn - tienGiam);

            // 3. LẤY CHI TIẾT MÓN ĂN (TỪ HÓA ĐƠN)
            List<ChiTietHoaDon> chiTietList = hoaDonDAO.getChiTietHoaDonForPrint(hoaDonHienTai.getMaHD());
            if (chiTietList == null) chiTietList = new ArrayList<>();

            // 4. MỞ DIALOG THANH TOÁN
            HoaDon_ThanhToan_Dialog thanhToanDialog = new HoaDon_ThanhToan_Dialog(
                    mainFrame,
                    hoaDonHienTai,
                    hoaDonDAO,
                    tongTienMonAn,
                    tienGiam,
                    tienThue,
                    tongThanhToan,
                    chiTietList
            );
            thanhToanDialog.setVisible(true);

            // 5. SAU KHI ĐÓNG DIALOG -> Tải lại danh sách PĐB
            taiDanhSachPhieuDat();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Đã xảy ra lỗi khi chuẩn bị thanh toán: " + e.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}

// ===== CÁC CLASS HỖ TRỢ GIAO DIỆN (Giữ nguyên) =====

class RoundedPanel extends JPanel {
    private final int cornerRadius;
    private final Color bgColor;

    public RoundedPanel(int radius, Color color) {
        this.cornerRadius = radius;
        this.bgColor = color;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color color = (bgColor != null) ? bgColor : getBackground();
        g2.setColor(color);
        g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
        g2.dispose();
    }
}

class SmallRoundedButton extends JButton {
    Color bgColor;
    private Color fgColor;
    private int arc = 10;

    public SmallRoundedButton(String text, Color bg, Color fg) {
        super(text);
        this.bgColor = bg;
        this.fgColor = fg;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(fgColor);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(72, 28)); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color fill = bgColor;
        if (getModel().isPressed()) fill = bgColor.darker();
        else if (getModel().isRollover()) fill = bgColor.brighter();
        g2.setColor(fill);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(getText());
        int textHeight = fm.getAscent();
        g2.setColor(getForeground());
        g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 2);
        g2.dispose();
    }
}

class SmallButtonCellRenderer implements javax.swing.table.TableCellRenderer {
    private final JPanel panel;
    private final SmallRoundedButton button;

    public SmallButtonCellRenderer() {
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8)); 
        panel.setOpaque(true); 
        button = new SmallRoundedButton("+ Đặt", new Color(40, 167, 69), Color.WHITE);
        button.setFocusable(false);
        panel.add(button);
    }

    @Override
    public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        String text = (value == null) ? "" : value.toString();
        button.setText(text);
        if (isSelected) { 
            panel.setBackground(table.getSelectionBackground());
            button.bgColor = new Color(40, 167, 69).darker();
        } else {
            panel.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
            button.bgColor = new Color(40, 167, 69);
        }
        button.repaint();
        return panel;
    }
}

class SmallButtonCellEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
    private final JPanel panel;
    private final SmallRoundedButton button;
    private int editingRow = -1;
    private ChonMon_Dialog dialog; // Thêm tham chiếu

    // SỬA: Thêm dialog vào constructor
    public SmallButtonCellEditor(ChonMon_Dialog dialog) {
        this.dialog = dialog; 
        panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8)); 
        panel.setOpaque(true); 
        button = new SmallRoundedButton("+ Đặt", new Color(40, 167, 69), Color.WHITE);
        button.setFocusable(false);
        button.addActionListener(e -> {
            final int row = editingRow;
            SwingUtilities.invokeLater(() -> {
                fireEditingStopped();
                if (row >= 0) {
                    // Gọi hàm datMon của dialog cha
                    dialog.datMon(row);
                }
            });
        });
        panel.add(button);
    }

    @Override
    public Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {
        this.editingRow = row;
        String text = (value == null) ? "" : value.toString();
        button.setText(text);
        panel.setBackground(table.getSelectionBackground()); 
        button.bgColor = new Color(40, 167, 69).darker();
        button.repaint();
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return "+ Đặt";
    }

    @Override
    public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            return ((MouseEvent) e).getClickCount() >= 1;
        }
        return false;
    }
}