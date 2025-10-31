package view.HoaDon;

import entity.Thue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.Thue_DAO; // SỬA: Import đúng DAO

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

public class Thue_TraCuu_View extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cboFilter;
    private Thue_DAO thueDAO = new Thue_DAO(); // SỬA: Khởi tạo đúng DAO

    // Stats Labels
    private JLabel lblTotal; 
    private JLabel lblHoatDong; 
    private JLabel lblKhongHoatDong;  

    // Màu sắc và Styles chung
    private static final Color PRIMARY_BLUE = new Color(41, 128, 185); 
    private static final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private static final Color WARNING_ORANGE = new Color(243, 156, 18);
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    private static final Color BG_COLOR = new Color(251, 248, 241); 
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(220, 221, 225);
    private static final Color RED_ORANGE = new Color(255, 99, 71); 

    public Thue_TraCuu_View() {
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
        loadThueData();
    }

    private JPanel createHeaderSection() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Tra cứu thuế");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Tìm kiếm, lọc thông tin và quản lý thuế");
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

        JLabel lblSearch = new JLabel("Từ khóa:");
        lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        txtSearch = new JTextField(25);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                loadThueData();
            }
        });

        JLabel lblFilter = new JLabel("Trạng thái:");
        lblFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // ComboBox lọc theo Trạng thái
        cboFilter = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Không hoạt động"});
        cboFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboFilter.setBackground(Color.WHITE);
        cboFilter.setPreferredSize(new Dimension(150, 38));
        cboFilter.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        cboFilter.addActionListener(e -> loadThueData());

        JButton btnSearch = createStyledButton("Tìm kiếm", PRIMARY_BLUE);
        btnSearch.addActionListener(e -> loadThueData());

        JButton btnReset = createStyledButton("Làm mới", TEXT_SECONDARY);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cboFilter.setSelectedIndex(0);
            loadThueData();
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
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // 1. Tổng số thuế
        lblTotal = new JLabel("0");
        JPanel totalCard = createStatCard("0", "Tổng số thuế", PRIMARY_BLUE, lblTotal);

        // 2. Hoạt động (SUCCESS_GREEN)
        lblHoatDong = new JLabel("0");
        JPanel hoatDongCard = createStatCard("0", "Hoạt động", SUCCESS_GREEN, lblHoatDong);

        // 3. Không hoạt động (WARNING_ORANGE)
        lblKhongHoatDong = new JLabel("0");
        JPanel khongHoatDongCard = createStatCard("0", "Không hoạt động", WARNING_ORANGE, lblKhongHoatDong);

        statsPanel.add(totalCard);
        statsPanel.add(hoatDongCard);
        statsPanel.add(khongHoatDongCard);

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
        // Cột: Mã thuế, Tên thuế, Tỷ lệ (%), Mô tả, Trạng thái
        String[] cols = {"Mã thuế", "Tên thuế", "Tỷ lệ (%)", "Mô tả", "Trạng thái"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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

        JLabel tableTitle = new JLabel("Danh sách thuế");
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

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        // Áp dụng Cell Renderer
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã thuế
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer); // Tỷ lệ
        table.getColumnModel().getColumn(3).setCellRenderer(leftRenderer); // Mô tả
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Trạng thái
        
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
                    
                    if (column == 4) { // Cột Trạng thái
                        if (text.equals("Hoạt động")) {
                            c.setForeground(SUCCESS_GREEN.darker());
                        } else if (text.equals("Không hoạt động")) {
                            c.setForeground(WARNING_ORANGE.darker());
                        } 
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                }
                return c;
            }
        };
        
        table.getColumnModel().getColumn(4).setCellRenderer(statusRenderer); // Trạng thái

        // Điều chỉnh độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(80); // Mã thuế
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Tên thuế
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Tỷ lệ
        table.getColumnModel().getColumn(3).setPreferredWidth(200); // Mô tả
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Trạng thái
    }

    // ====== Load + filter dữ liệu ======
    private void loadThueData() {
        model.setRowCount(0);
        List<Thue> dsThue = thueDAO.getAllThue();

        String keyword = txtSearch.getText().trim().toLowerCase();
        String filter = (String) cboFilter.getSelectedItem();
        
        // --- KHỞI TẠO VÀ TÍNH TOÁN THỐNG KÊ (Tính trên toàn bộ dữ liệu gốc) ---
        int total = dsThue.size();
        int countHoatDong = 0;

        for (Thue thue : dsThue) {
            if (thue.isTrangThai()) countHoatDong++;
        }
        
        int countKhongHoatDong = total - countHoatDong;
        
        // Cập nhật thống kê
        lblTotal.setText(String.valueOf(total));
        lblHoatDong.setText(String.valueOf(countHoatDong));
        lblKhongHoatDong.setText(String.valueOf(countKhongHoatDong));
        
        // --- LỌC DỮ LIỆU ĐỂ HIỂN THỊ (Logic chính) ---
        List<Thue> filtered = dsThue.stream().filter(thue -> {
            String ma = safeLower(thue.getMaSoThue());
            String ten = safeLower(thue.getTenThue());
            String moTa = safeLower(thue.getMoTa());

            boolean matchKeyword = keyword.isEmpty()
                    || ma.contains(keyword)
                    || ten.contains(keyword)
                    || moTa.contains(keyword);

            boolean matchFilter = filter.equals("Tất cả") 
                    || (filter.equals("Hoạt động") && thue.isTrangThai())
                    || (filter.equals("Không hoạt động") && !thue.isTrangThai());

            return matchKeyword && matchFilter;
        }).collect(Collectors.toList());

        for (Thue thue : filtered) {
            String statusStr = thue.isTrangThai() ? "Hoạt động" : "Không hoạt động";
            // SỬA: Chuyển đổi double (ví dụ: 0.08) sang hiển thị phần trăm (ví dụ: 8.0%)
            String tyLeStr = String.format("%.1f%%", thue.getTyLeThue()); 
            model.addRow(new Object[]{
                    thue.getMaSoThue(),
                    thue.getTenThue(),
                    tyLeStr,
                    thue.getMoTa(),
                    statusStr
            });
        }
    }

    private String safeLower(String s) {
        return (s == null) ? "" : s.toLowerCase();
    }

    // ===== UI Helpers (Copy từ KhachHang_TraCuu_View) =====
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
}