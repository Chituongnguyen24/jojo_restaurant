package view.HoaDon;

import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import entity.HoaDon;
import entity.KhachHang;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

public class HoaDon_TraCuu_View extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cboFilter;
    private HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();

    // Stats Labels
    private JLabel lblTotal;
    private JLabel lblDaThanhToan;
    private JLabel lblChuaThanhToan;
    private JLabel lblTongDoanhThu;

    // Màu sắc và Styles chung (ĐỒNG BỘ VỚI KHÁCH HÀNG VÀ NHÂN VIÊN)
    private static final Color PRIMARY_BLUE = new Color(41, 128, 185);
    private static final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private static final Color WARNING_ORANGE = new Color(243, 156, 18);
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    private static final Color BG_COLOR = new Color(251, 248, 241);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(220, 221, 225);
    private static final Color RED_ORANGE = new Color(255, 99, 71);

    public HoaDon_TraCuu_View() {
        setLayout(new BorderLayout());
        setBackground(BG_COLOR);

        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(25, 30, 30, 30));

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);

        topSection.add(createHeaderSection());
        topSection.add(Box.createVerticalStrut(20));
        topSection.add(createSearchSection());
        topSection.add(Box.createVerticalStrut(20));
        topSection.add(createStatsPanel());

        mainContent.add(topSection, BorderLayout.NORTH);
        mainContent.add(createTableSection(), BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // Load lần đầu
        loadHoaDonData();
    }

    private JPanel createHeaderSection() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Tra cứu hóa đơn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Tìm kiếm và lọc hóa đơn theo trạng thái hoặc thông tin khách hàng");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(TEXT_SECONDARY);

        titlePanel.add(title);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.WEST);
        return header;
    }

    private JPanel createSearchSection() {
        JPanel searchCard = new RoundedPanel(12, CARD_BG);
        searchCard.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        searchCard.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        searchCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel lblSearch = new JLabel("Từ khóa (Mã HD/Tên KH/SĐT):");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                loadHoaDonData();
            }
        });

        JLabel lblFilter = new JLabel("Trạng thái:");
        lblFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cboFilter = new JComboBox<>(new String[]{"Tất cả", "Đã thanh toán", "Chưa thanh toán"});
        cboFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboFilter.setBackground(Color.WHITE);
        cboFilter.setPreferredSize(new Dimension(150, 38));
        cboFilter.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        cboFilter.addActionListener(e -> loadHoaDonData());

        JButton btnSearch = createStyledButton("Tìm kiếm", PRIMARY_BLUE);
        btnSearch.addActionListener(e -> loadHoaDonData());

        JButton btnReset = createStyledButton("Làm mới", TEXT_SECONDARY);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cboFilter.setSelectedIndex(0);
            loadHoaDonData();
        });

        searchCard.add(lblSearch);
        searchCard.add(txtSearch);
        searchCard.add(lblFilter);
        searchCard.add(cboFilter);
        searchCard.add(btnSearch);
        searchCard.add(btnReset);

        return searchCard;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // 1. Tổng số hóa đơn
        lblTotal = new JLabel("0");
        JPanel totalCard = createStatCard("0", "Tổng số HD", PRIMARY_BLUE, lblTotal);

        // 2. Đã thanh toán (SUCCESS_GREEN)
        lblDaThanhToan = new JLabel("0");
        JPanel daTTCard = createStatCard("0", "Đã thanh toán", SUCCESS_GREEN, lblDaThanhToan);

        // 3. Chưa thanh toán (WARNING_ORANGE)
        lblChuaThanhToan = new JLabel("0");
        JPanel chuaTTCard = createStatCard("0", "Chưa thanh toán", WARNING_ORANGE, lblChuaThanhToan);

        // 4. Tổng doanh thu (RED_ORANGE) - Tính tổng tiền của tất cả HD đã thanh toán
        lblTongDoanhThu = new JLabel("0 VNĐ");
        JPanel doanhThuCard = createStatCard("0 VNĐ", "Tổng doanh thu", RED_ORANGE, lblTongDoanhThu);

        statsPanel.add(totalCard);
        statsPanel.add(daTTCard);
        statsPanel.add(chuaTTCard);
        statsPanel.add(doanhThuCard);

        return statsPanel;
    }

    private JPanel createStatCard(String defaultValue, String label, Color bgColor, JLabel valueLabel) {
        JPanel card = new RoundedPanel(12, bgColor);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(25, 25, 25, 25));
        card.setPreferredSize(new Dimension(0, 120));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setText(defaultValue);

        JLabel titleLabel = new JLabel(label);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(255, 255, 255, 220));

        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(titleLabel);

        card.add(textPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createTableSection() {
        String[] cols = {"Mã HD", "Khách hàng", "Ngày lập", "Tổng tiền", "Phương thức", "Trạng thái", "Chi tiết"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Chỉ cho nhấn nút Chi tiết
            }
        };

        table = new JTable(model);
        setupTableStyle();

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tableCard = new RoundedPanel(12, CARD_BG);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(new EmptyBorder(20, 25, 15, 25));

        JLabel tableTitle = new JLabel("Danh sách hóa đơn tra cứu được");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_PRIMARY);

        tableHeader.add(tableTitle, BorderLayout.WEST);
        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        return tableCard;
    }

    private void setupTableStyle() {
        table.setRowHeight(48);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(PRIMARY_BLUE.getRed(), PRIMARY_BLUE.getGreen(), PRIMARY_BLUE.getBlue(), 30));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 48));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));

        // Căn chỉnh nội dung cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);

        // Áp dụng Cell Renderer cho các cột
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã HD
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Tổng tiền
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Phương thức
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Trạng thái

        // Renderer đặc biệt cho cột Trạng thái
        DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    setHorizontalAlignment(CENTER);
                    String text = value.toString();
                    if (text.equals("Đã thanh toán")) {
                        c.setForeground(SUCCESS_GREEN.darker());
                    } else if (text.equals("Chưa thanh toán")) {
                        c.setForeground(RED_ORANGE.darker());
                    }
                    c.setFont(c.getFont().deriveFont(Font.BOLD));
                }
                return c;
            }
        };
        table.getColumnModel().getColumn(5).setCellRenderer(statusRenderer);

        // Thêm nút xem chi tiết (giữ nguyên logic cũ nhưng style mới)
        table.getColumn("Chi tiết").setCellRenderer(new ButtonRenderer("Xem", new Color(0, 120, 215), Color.WHITE));
        table.getColumn("Chi tiết").setCellEditor(new ButtonEditor(new JCheckBox(), "Xem"));

        // Điều chỉnh độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(80); // Mã HD
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Khách hàng
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Ngày lập
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // Tổng tiền
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Phương thức
        table.getColumnModel().getColumn(5).setPreferredWidth(120); // Trạng thái
        table.getColumnModel().getColumn(6).setPreferredWidth(80); // Chi tiết
    }

    private void loadHoaDonData() {
        model.setRowCount(0);
        List<HoaDon> dsHD = hoaDonDAO.getAllHoaDon();
        String keyword = txtSearch.getText().trim().toLowerCase();
        String filter = (String) cboFilter.getSelectedItem();

        // --- KHỞI TẠO VÀ TÍNH TOÁN THỐNG KÊ (Tính trên toàn bộ dữ liệu gốc) ---
        int total = dsHD.size();
        int countDaTT = 0;
        int countChuaTT = 0;
        double tongDoanhThu = 0.0;
        for (HoaDon hd : dsHD) {
            if (hd.isDaThanhToan()) {
                countDaTT++;
                tongDoanhThu += hoaDonDAO.tinhTongTienHoaDon(hd.getMaHoaDon());
            } else {
                countChuaTT++;
            }
        }

        // Cập nhật thống kê
        lblTotal.setText(String.valueOf(total));
        lblDaThanhToan.setText(String.valueOf(countDaTT));
        lblChuaThanhToan.setText(String.valueOf(countChuaTT));
        lblTongDoanhThu.setText(String.format("%,.0f VNĐ", tongDoanhThu));

        // --- LỌC DỮ LIỆU ĐỂ HIỂN THỊ (Logic chính) ---
        List<HoaDon> filtered = dsHD.stream().filter(hd -> {
            KhachHang kh = null;
            String tenKH = "";
            String sdtKH = "";
            // Lấy thông tin KH nếu có mã hợp lệ
            if (hd.getKhachHang() != null && hd.getKhachHang().getMaKhachHang() != null) {
                kh = khachHangDAO.getKhachHangById(hd.getKhachHang().getMaKhachHang());
                if (kh != null) {
                    tenKH = safeLower(kh.getTenKhachHang());
                    sdtKH = safeLower(kh.getSdt());
                }
            }
            String maHD = safeLower(hd.getMaHoaDon());
            // Kiểm tra keyword khớp với mã HD, tên KH hoặc SĐT KH
            boolean matchKeyword = keyword.isEmpty()
                    || maHD.contains(keyword)
                    || tenKH.contains(keyword)
                    || sdtKH.contains(keyword);
            // Lọc theo trạng thái
            boolean matchFilter = filter.equals("Tất cả")
                    || (filter.equals("Đã thanh toán") && hd.isDaThanhToan())
                    || (filter.equals("Chưa thanh toán") && !hd.isDaThanhToan());
            return matchKeyword && matchFilter;
        }).collect(Collectors.toList());

        for (HoaDon hd : filtered) {
            KhachHang kh = null;
            if (hd.getKhachHang() != null && hd.getKhachHang().getMaKhachHang() != null) {
                kh = khachHangDAO.getKhachHangById(hd.getKhachHang().getMaKhachHang());
            }
            String tenKHDisplay = (kh != null) ? kh.getTenKhachHang() : "Khách lẻ";
            double tongTien = hoaDonDAO.tinhTongTienHoaDon(hd.getMaHoaDon());
            String trangThaiDisplay = hd.isDaThanhToan() ? "Đã thanh toán" : "Chưa thanh toán";
            model.addRow(new Object[]{
                    hd.getMaHoaDon(), tenKHDisplay, hd.getNgayLap().toString(),
                    String.format("%,.0f VNĐ", tongTien), hd.getPhuongThuc(), trangThaiDisplay,
                    "Xem" // Nút xem chi tiết
            });
        }
    }

    private String safeLower(String s) {
        return (s == null) ? "" : s.toLowerCase();
    }

    // ===== UI Helpers =====
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color color = bgColor;
                if (getModel().isPressed()) {
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

        btn.setPreferredSize(new Dimension(110, 38));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color bgColor;

        public RoundedPanel(int radius, Color color) {
            super();
            cornerRadius = radius;
            bgColor = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            g2.dispose();
        }
    }

    // --- Renderer và Editor cho nút trong bảng (Giữ nguyên nhưng điều chỉnh style nếu cần) ---
    class ButtonRenderer extends JButton implements TableCellRenderer {
        private final Color bgColor;
        private final Color fgColor;

        public ButtonRenderer(String text, Color bg, Color fg) {
            setText(text);
            this.bgColor = bg;
            this.fgColor = fg;
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setForeground(fgColor);
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private final String type;

        public ButtonEditor(JCheckBox checkBox, String type) {
            super(checkBox);
            this.type = type;
            button = new JButton();
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText(type);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setForeground(Color.WHITE);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow < 0 || selectedRow >= table.getRowCount()) {
                    isPushed = false;
                    return type;
                }
                String maHD = table.getValueAt(selectedRow, 0).toString();
                if (type.equals("Xem")) {
                    SwingUtilities.invokeLater(() -> {
                        HoaDon hd = hoaDonDAO.findByMaHD(maHD);
                        if (hd != null) {
                            KhachHang kh = null;
                            if (hd.getKhachHang() != null && hd.getKhachHang().getMaKhachHang() != null) {
                                kh = khachHangDAO.getKhachHangById(hd.getKhachHang().getMaKhachHang());
                            }
                            hd.setKhachHang(kh); // Gán KH đầy đủ
                            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(HoaDon_TraCuu_View.this);
                            HoaDon_ChiTietHoaDon_View detailDialog = new HoaDon_ChiTietHoaDon_View(parentFrame, hd);
                            detailDialog.setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(table, "Không tìm thấy thông tin hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                }
            }
            isPushed = false;
            return type;
        }
    }
}