package view.ThucDon;

import dao.Ban_DAO;
import dao.PhieuDatBan_DAO;
import entity.Ban;
import entity.PhieuDatBan;
import enums.TrangThaiBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.List;

public class DatMonAn_View extends JPanel {
    
    // DAOs
    private Ban_DAO banDAO;
    private PhieuDatBan_DAO datBanDAO;

    // Data
    private PhieuDatBan phieuDatBanHienTai; 

    // Components
    private JTable tblBan;
    private JTable tblDonDatMon;
    private DefaultTableModel modelBan;
    private DefaultTableModel modelDonDatMon;
    
    private JButton btnDatMon, btnHoanThanhMon, btnHuyMon, btnLamMoi;
    private JLabel lblThongTinBan;

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
        this.datBanDAO = new PhieuDatBan_DAO(); // Khởi tạo đúng DAO

        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_COLOR);

        // === HEADER ===
        add(createHeader(), BorderLayout.NORTH);

        // === CONTENT ===
        add(createContent(), BorderLayout.CENTER);

        // === FOOTER ===
        add(createFooter(), BorderLayout.SOUTH);

        // Events
        setupEvents();

        // Load data
        taiDanhSachBan();
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(20, 30, 20, 30)
        ));

        // Left side - Title
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Quản lý Đặt món");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Chọn bàn và gọi món cho khách hàng");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        leftPanel.add(titleLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(subtitleLabel);

        // Right side - Refresh button (no icon)
        btnLamMoi = createButton("Làm mới", PRIMARY_COLOR);
        btnLamMoi.setPreferredSize(new Dimension(130, 45));

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(btnLamMoi, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContent() {
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Left panel - Danh sách bàn
        JPanel leftPanel = createBanPanel();
        leftPanel.setPreferredSize(new Dimension(350, 0));

        // Right panel - Chi tiết đơn
        JPanel rightPanel = createDonDatPanel();

        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(rightPanel, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createBanPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        // Card header 
        JPanel headerCard = createCardHeader("Danh sách bàn", "Chọn bàn để xem chi tiết");
        
        // Table
        modelBan = new DefaultTableModel(new String[]{"Mã bàn", "Số chỗ", "Trạng thái"}, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return false;
            }
        };
        tblBan = createStyledTable(modelBan);
        tblBan.setRowHeight(60);
        
        // Custom renderer cho bảng bàn
        tblBan.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setHorizontalAlignment(column == 1 ? CENTER : LEFT);
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                
                if (column == 2) { // Cột trạng thái
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    TrangThaiBan status = TrangThaiBan.fromString(value.toString());
                    
                    if (status == TrangThaiBan.CO_KHACH) {
                        setForeground(DANGER_COLOR);
                    } else if (status == TrangThaiBan.DA_DAT) {
                        setForeground(WARNING_COLOR);
                    } else {
                        setForeground(SUCCESS_COLOR);
                    }
                    setText(value != null ? value.toString() : "");
                }
                
                if (isSelected) {
                    setBackground(new Color(219, 234, 254));
                    setForeground(TEXT_PRIMARY); // Màu chữ chính
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    if (column != 2) setForeground(TEXT_PRIMARY);
                }
                
                setBorder(new EmptyBorder(10, 15, 10, 15));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblBan);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel cardPanel = createCard();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(headerCard, BorderLayout.NORTH);
        panel.add(cardPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDonDatPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        // Info card - Thông tin bàn đang chọn
        JPanel infoCard = createInfoCard();
        
        // Table card
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

        lblThongTinBan = new JLabel("Chưa chọn bàn");
        lblThongTinBan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblThongTinBan.setForeground(TEXT_PRIMARY);

        JLabel lblStatus = new JLabel("Vui lòng chọn bàn từ danh sách");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStatus.setForeground(TEXT_SECONDARY);

        leftInfo.add(lblThongTinBan);
        leftInfo.add(Box.createVerticalStrut(5));
        leftInfo.add(lblStatus);

        card.add(leftInfo, BorderLayout.WEST);

        return card;
    }

    private JPanel createDonDatTableCard() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setOpaque(false);

        // Header
        JPanel header = createCardHeader("Chi tiết đơn đặt món", "Danh sách món đã gọi");

        // Table
        modelDonDatMon = new DefaultTableModel(
            new String[]{"Mã món", "Tên món", "SL", "Ghi chú", "Trạng thái", "Thời gian"}, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return false;
            }
        };
        tblDonDatMon = createStyledTable(modelDonDatMon);
        tblDonDatMon.setRowHeight(55);
        
        // Set column widths
        tblDonDatMon.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblDonDatMon.getColumnModel().getColumn(1).setPreferredWidth(200);
        tblDonDatMon.getColumnModel().getColumn(2).setPreferredWidth(50);
        tblDonDatMon.getColumnModel().getColumn(3).setPreferredWidth(150);
        tblDonDatMon.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblDonDatMon.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Custom renderer
        tblDonDatMon.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
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
                } else if (column == 4) { // Trạng thái
                    setFont(new Font("Segoe UI", Font.BOLD, 12));
                    if (value != null) {
                        String s = value.toString();
                        if (s.contains("Hoàn thành")) {
                            setForeground(SUCCESS_COLOR);
                        } else {
                            setForeground(WARNING_COLOR);
                        }
                    } 
                } else {
                    setHorizontalAlignment(LEFT);
                }
                
                if (isSelected) {
                    setBackground(new Color(219, 234, 254));
                    if (column != 4) setForeground(TEXT_PRIMARY);
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    if (column != 4) setForeground(TEXT_PRIMARY);
                }
                
                setBorder(new EmptyBorder(10, 15, 10, 15));
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblDonDatMon);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel cardPanel = createCard();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(header, BorderLayout.NORTH);
        panel.add(cardPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooter() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        footerPanel.setBackground(CARD_COLOR);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(20, 30, 20, 30)
        ));

        Dimension btnSize = new Dimension(170, 45);

        btnDatMon = createButton("Đặt món", SUCCESS_COLOR);
        btnDatMon.setPreferredSize(btnSize);

        btnHoanThanhMon = createButton("Hoàn thành", PRIMARY_COLOR);
        btnHoanThanhMon.setPreferredSize(btnSize);
        btnHoanThanhMon.setEnabled(false);

        btnHuyMon = createButton("Hủy món", DANGER_COLOR);
        btnHuyMon.setPreferredSize(btnSize);

        footerPanel.add(btnDatMon);
        footerPanel.add(btnHoanThanhMon);
        footerPanel.add(btnHuyMon);

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
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

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
        tblBan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tblBan.getSelectedRow() != -1) {
                xuLyChonBan();
            }
        });
        
        btnDatMon.addActionListener(e -> moDialogDatMon());
        btnHuyMon.addActionListener(e -> xuLyHuyMon());
        btnLamMoi.addActionListener(e -> taiDanhSachBan());
    }

    // ===== DATA METHODS =====

    private void taiDanhSachBan() {
        modelBan.setRowCount(0);
        modelDonDatMon.setRowCount(0);
        phieuDatBanHienTai = null;
        lblThongTinBan.setText("Chưa chọn bàn");
        
        // Lấy danh sách bàn đang ĐÃ ĐẶT hoặc CÓ KHÁCH
        List<Ban> dsBan = banDAO.getBanDangHoatDong();
        for (Ban ban : dsBan) {
            modelBan.addRow(new Object[]{
                ban.getMaBan().trim(),
                ban.getSoCho(),
                ban.getTrangThai() // Trạng thái String raw DB
            });
        }
    }

    private void xuLyChonBan() {
        int row = tblBan.getSelectedRow();
        if (row == -1) return;
        
        String maBan = (String) modelBan.getValueAt(row, 0);
        int soCho = (int) modelBan.getValueAt(row, 1);
        String trangThaiStr = (String) modelBan.getValueAt(row, 2);
        
        lblThongTinBan.setText("Bàn " + maBan + " - " + soCho + " chỗ ngồi");
        
        // 1. Lấy phiếu đặt bàn đang CHƯA ĐẾN (nếu có)
        this.phieuDatBanHienTai = datBanDAO.getPhieuByBan(maBan);
        
        if (this.phieuDatBanHienTai != null && this.phieuDatBanHienTai.getTrangThaiPhieu().equals("Chưa đến")) {
             // Bàn đang ở trạng thái ĐÃ ĐẶT 
             taiDonDatMon(this.phieuDatBanHienTai.getMaPhieu());
             btnDatMon.setEnabled(true);
             btnHuyMon.setEnabled(true);
        } else {
            // 2. Nếu không có phiếu CHƯA ĐẾN, kiểm tra Hóa đơn (CO_KHACH)
            // Logic cho bàn CO_KHACH (đã có hóa đơn)
            if (TrangThaiBan.fromString(trangThaiStr) == TrangThaiBan.CO_KHACH) {
                 // Cần code để lấy chi tiết món ăn từ bảng CTHOADON
                 // Hiện tại, ta chỉ hiển thị cảnh báo và tắt nút
                 modelDonDatMon.setRowCount(0);
                 JOptionPane.showMessageDialog(this, 
                    "Bàn đang phục vụ (CO_KHACH). Vui lòng dùng menu Thanh Toán/Gọi món ở DatBan_View.", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                 // Tắt nút
                 btnDatMon.setEnabled(false);
                 btnHuyMon.setEnabled(false);
            } else {
                 modelDonDatMon.setRowCount(0);
                 btnDatMon.setEnabled(false);
                 btnHuyMon.setEnabled(false);
            }
        }
    }

    private void taiDonDatMon(String maPhieu) {
        modelDonDatMon.setRowCount(0);
        // Dùng hàm getChiTietTheoMaPhieu của PhieuDatBan_DAO
        List<Object[]> dsMon = datBanDAO.getChiTietTheoMaPhieu(maPhieu); 
        
        String thoiDiemGia = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
        String trangThaiGia = "Chưa hoàn thành";

        for (Object[] row : dsMon) {
            modelDonDatMon.addRow(new Object[]{
                row[0],
                row[1],
                row[2],
                row[3],
                trangThaiGia,
                thoiDiemGia
            });
        }
    }

    private void moDialogDatMon() {
        if (phieuDatBanHienTai == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một bàn đang có phiếu đặt!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ChonMon_Dialog dialog = new ChonMon_Dialog(
            (Frame) SwingUtilities.getWindowAncestor(this), 
            phieuDatBanHienTai
        );
        dialog.setVisible(true);
        
        // Sau khi đóng dialog, refresh lại đơn đặt món
        taiDonDatMon(phieuDatBanHienTai.getMaPhieu());
    }

    private void xuLyHuyMon() {
        int selectedRow = tblDonDatMon.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một món trong 'Đơn đặt món' để hủy!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (phieuDatBanHienTai == null) return;

        String maMonAn = (String) modelDonDatMon.getValueAt(selectedRow, 0);
        String tenMonAn = (String) modelDonDatMon.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn HỦY món: " + tenMonAn + "?", 
            "Xác nhận hủy", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            // Dùng hàm deleteChiTiet của PhieuDatBan_DAO
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
}