package view.Ban;

import dao.DatBan_DAO;
import entity.Ban;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDatBan;
import entity.ChiTietPhieuDatBan;
import entity.MonAn;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChiTietPhieuDatBan_View extends JPanel {
    private JTable table;
    private PhieuDatBan phieu;
    private DatBan_DAO daoDatBan = new DatBan_DAO();
    
    private static final Color PRIMARY_COLOR = new Color(37, 99, 235); // Blue
    private static final Color SECONDARY_COLOR = new Color(59, 130, 246);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color ACCENT_COLOR = new Color(249, 115, 22);

    public ChiTietPhieuDatBan_View(PhieuDatBan phieu) {
        this.phieu = phieu;
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_COLOR);

        // Header
        add(createModernHeader(), BorderLayout.NORTH);

        // Main content with scroll
        JScrollPane mainScroll = new JScrollPane(createMainContent());
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    private JPanel createModernHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(20, 30, 20, 30)
        ));

        // Left side - Title and breadcrumb
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel breadcrumb = new JLabel("Quản lý đặt bàn / Chi tiết phiếu");
        breadcrumb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        breadcrumb.setForeground(TEXT_SECONDARY);

        JLabel title = new JLabel("Chi tiết phiếu đặt bàn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT_PRIMARY);

        leftPanel.add(breadcrumb);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(title);

        // Right side - Action buttons
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        JButton btnBack = createModernButton("Quay lại", SECONDARY_COLOR, false);
        btnBack.addActionListener(e -> ((JFrame) SwingUtilities.getWindowAncestor(this)).dispose());

        JButton btnPrint = createModernButton("In phiếu", SUCCESS_COLOR, true);
        btnPrint.addActionListener(e -> JOptionPane.showMessageDialog(this, "Chức năng in phiếu đang được phát triển"));

        rightPanel.add(btnBack);
        rightPanel.add(btnPrint);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        // Top section with info cards in grid
        JPanel topSection = new JPanel(new GridLayout(1, 2, 20, 0));
        topSection.setOpaque(false);
        topSection.add(createInfoCard());
        topSection.add(createStatusCard());

        mainPanel.add(topSection, BorderLayout.NORTH);
        mainPanel.add(createOrderDetailsCard(), BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createInfoCard() {
        JPanel card = createCard("Thông tin phiếu đặt");
        card.setLayout(new GridLayout(5, 1, 0, 15));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String thoiGianStr = phieu.getThoiGianDat().format(formatter);
        String[] parts = thoiGianStr.split(" ");

        card.add(createInfoRow("Mã phiếu", phieu.getMaPhieu()));
        card.add(createInfoRow("Khách hàng", phieu.getKhachHang().getTenKhachHang()));
        card.add(createInfoRow("Số bàn", phieu.getBan().getMaBan()));
        card.add(createInfoRow("Ngày đặt", parts[0]));
        card.add(createInfoRow("Giờ đặt", parts[1]));

        return card;
    }

    private JPanel createStatusCard() {
        JPanel card = createCard("Trạng thái & Thông tin thêm");
        card.setLayout(new BorderLayout(0, 20));

        // Status badge
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);
        
        JLabel statusBadge = new JLabel("Đã xác nhận");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusBadge.setForeground(Color.WHITE);
        statusBadge.setBackground(SUCCESS_COLOR);
        statusBadge.setOpaque(true);
        statusBadge.setBorder(new EmptyBorder(8, 16, 8, 16));
        
        statusPanel.add(statusBadge);

        // Additional info
        JPanel infoGrid = new JPanel(new GridLayout(3, 1, 0, 15));
        infoGrid.setOpaque(false);
        infoGrid.add(createInfoRow("Số người", "N/A"));
        infoGrid.add(createInfoRow("Ghi chú", "Không có ghi chú"));
        
        // Summary info without icon
        JPanel summaryPanel = new JPanel(new BorderLayout(10, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(12, 15, 12, 15)
        ));
        summaryPanel.setBackground(new Color(239, 246, 255));
        
        List<ChiTietPhieuDatBan> chiTietList = daoDatBan.getChiTietByPhieuId(phieu.getMaPhieu());
        int totalItems = chiTietList.size();
        
        JLabel summaryText = new JLabel("<html><b>" + totalItems + "</b> món ăn đã đặt</html>");
        summaryText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        summaryText.setForeground(TEXT_PRIMARY);
        
        summaryPanel.add(summaryText, BorderLayout.CENTER);

        card.add(statusPanel, BorderLayout.NORTH);
        card.add(infoGrid, BorderLayout.CENTER);
        card.add(summaryPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createOrderDetailsCard() {
        JPanel card = createCard("Danh sách món ăn");
        card.setLayout(new BorderLayout(0, 15));

        // Create table
        List<ChiTietPhieuDatBan> chiTietList = daoDatBan.getChiTietByPhieuId(phieu.getMaPhieu());
        String[] cols = {"STT", "Tên món ăn", "Đơn giá", "Số lượng", "Thành tiền"};
        Object[][] data = new Object[chiTietList.size()][5];
        double tongTien = 0.0;
        
        for (int i = 0; i < chiTietList.size(); i++) {
            ChiTietPhieuDatBan ct = chiTietList.get(i);
            MonAn mon = ct.getMonAn() != null ? ct.getMonAn() : new MonAn();
            data[i][0] = i + 1;
            data[i][1] = mon.getTenMonAn() != null ? mon.getTenMonAn() : "N/A";
            data[i][2] = String.format("%,d", (int)ct.getDonGia());
            data[i][3] = ct.getSoLuongMonAn();
            double thanhTien = ct.tinhTongTien();
            data[i][4] = String.format("%,d", (int)thanhTien);
            tongTien += thanhTien;
        }

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TEXT_PRIMARY);

        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_SECONDARY);
        header.setBorder(new MatteBorder(0, 0, 2, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));

        // Column alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Total panel
        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);
        totalPanel.setBorder(new CompoundBorder(
            new MatteBorder(2, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(20, 0, 0, 0)
        ));

        JPanel totalRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        totalRight.setOpaque(false);

        JLabel lblTotalLabel = new JLabel("Tổng cộng:");
        lblTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalLabel.setForeground(TEXT_SECONDARY);

        JLabel lblTotal = new JLabel(String.format("%,d VNĐ", (int)tongTien));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotal.setForeground(ACCENT_COLOR);

        totalRight.add(lblTotalLabel);
        totalRight.add(lblTotal);
        totalPanel.add(totalRight, BorderLayout.EAST);

        card.add(scrollPane, BorderLayout.CENTER);
        card.add(totalPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(CARD_COLOR);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

        card.add(titlePanel, BorderLayout.NORTH);

        return card;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        textPanel.setOpaque(false);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLabel.setForeground(TEXT_SECONDARY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblValue.setForeground(TEXT_PRIMARY);

        textPanel.add(lblLabel);
        textPanel.add(lblValue);

        row.add(textPanel, BorderLayout.CENTER);

        return row;
    }

    private JButton createModernButton(String text, Color bgColor, boolean isPrimary) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }
}