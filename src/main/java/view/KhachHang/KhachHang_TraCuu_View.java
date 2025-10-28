package view.KhachHang;

import dao.KhachHang_DAO;
import entity.KhachHang;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.stream.Collectors;

public class KhachHang_TraCuu_View extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cboFilter;
    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();

    // Stats Labels
    private JLabel lblTotal; 
    private JLabel lblHangDong; 
    private JLabel lblHangBac;  
    private JLabel lblHangVang;  

    // Màu sắc và Styles chung (ĐỒNG BỘ VỚI NHÂN VIÊN)
    private static final Color PRIMARY_BLUE = new Color(41, 128, 185); 
    private static final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private static final Color WARNING_ORANGE = new Color(243, 156, 18);
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    private static final Color BG_COLOR = new Color(251, 248, 241); 
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(220, 221, 225);
    private static final Color RED_ORANGE = new Color(255, 99, 71); 

    public KhachHang_TraCuu_View() {
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
        loadKhachHangData();
    }

    private JPanel createHeaderSection() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Tra cứu khách hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Tìm kiếm, lọc thông tin và quản lý thẻ thành viên");
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
                loadKhachHangData();
            }
        });

        JLabel lblFilter = new JLabel("Phân loại:");
        lblFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // CHỈNH SỬA: ComboBox chỉ lọc theo Hạng và Khách thường
        cboFilter = new JComboBox<>(new String[]{"Tất cả", "Đồng", "Bạc", "Vàng"});
        cboFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboFilter.setBackground(Color.WHITE);
        cboFilter.setPreferredSize(new Dimension(150, 38));
        cboFilter.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        cboFilter.addActionListener(e -> loadKhachHangData());

        JButton btnSearch = createStyledButton("Tìm kiếm", PRIMARY_BLUE);
        btnSearch.addActionListener(e -> loadKhachHangData());

        JButton btnReset = createStyledButton("Làm mới", TEXT_SECONDARY);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cboFilter.setSelectedIndex(0);
            loadKhachHangData();
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
        // Tương tự NhanVien_TraCuu_View
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // 1. Tổng số khách hàng
        lblTotal = new JLabel("0");
        JPanel totalCard = createStatCard("0", "Tổng số KH", PRIMARY_BLUE, lblTotal);

        // 2. Hạng Đồng (SUCCESS_GREEN)
        lblHangDong = new JLabel("0");
        JPanel dongCard = createStatCard("0", "Hạng Đồng", SUCCESS_GREEN, lblHangDong);

        // 3. Hạng Bạc (WARNING_ORANGE)
        lblHangBac = new JLabel("0");
        JPanel bacCard = createStatCard("0", "Hạng Bạc", WARNING_ORANGE, lblHangBac);

        // 4. Hạng Vàng (RED_ORANGE)
        lblHangVang = new JLabel("0");
        JPanel vangCard = createStatCard("0", "Hạng Vàng", RED_ORANGE, lblHangVang);


        statsPanel.add(totalCard);
        statsPanel.add(dongCard);
        statsPanel.add(bacCard);
        statsPanel.add(vangCard);

        return statsPanel;
    }
    
    private JPanel createStatCard(String defaultValue, String label, Color bgColor, JLabel valueLabel) {
        // Helper Card (Copy logic từ NhanVien_TraCuu_View)
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
        // Cột: Mã KH, Tên KH, SĐT, Email, Điểm TL, Hạng, Là TV
        String[] cols = {"Mã KH", "Tên khách hàng", "SĐT", "Email", "Điểm tích lũy", "Hạng", "Phân loại"};
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

        JLabel tableTitle = new JLabel("Danh sách khách hàng");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_PRIMARY);

        tableHeader.add(tableTitle, BorderLayout.WEST);
        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        return tableCard;
    }

    private void setupTableStyle() {
        // Đồng bộ style với NhanVien_TraCuu_View
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

        // Áp dụng Cell Renderer
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã KH
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer); // Điểm tích lũy
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Hạng
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Phân loại (Thành viên/Thường)
        
        // Renderer đặc biệt cho cột Hạng và Phân loại
        DefaultTableCellRenderer roleRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    setHorizontalAlignment(CENTER);
                    String text = value.toString();
                    
                    if (column == 5) { // Cột Hạng
                        // Logic màu cho Hạng (Đồng, Bạc, Vàng)
                        if (text.equals("Vàng")) {
                             c.setForeground(RED_ORANGE.darker());
                        } else if (text.equals("Bạc")) {
                             c.setForeground(WARNING_ORANGE.darker());
                        } else if (text.equals("Đồng")) {
                             c.setForeground(SUCCESS_GREEN.darker());
                        } 
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else if (column == 6) { // Cột Phân loại
                        if (text.equals("Thành viên")) {
                            c.setForeground(PRIMARY_BLUE.darker()); // Màu xanh dương cho thành viên
                        } else {
                            c.setForeground(TEXT_SECONDARY.darker()); // Màu xám cho khách thường
                        }
                         c.setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                }
                return c;
            }
        };
        
        table.getColumnModel().getColumn(5).setCellRenderer(roleRenderer); // Hạng
        table.getColumnModel().getColumn(6).setCellRenderer(roleRenderer); // Phân loại

        // Điều chỉnh độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(80); // Mã KH
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // SĐT
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Điểm TL
        table.getColumnModel().getColumn(5).setPreferredWidth(80); // Hạng
        table.getColumnModel().getColumn(6).setPreferredWidth(120); // Phân loại
    }

    // ====== Load + filter dữ liệu ======
    private void loadKhachHangData() {
        model.setRowCount(0);
        List<KhachHang> dsKH = khachHangDAO.getAllKhachHang();

        String keyword = txtSearch.getText().trim().toLowerCase();
        String filter = (String) cboFilter.getSelectedItem();
        
        // --- KHỞI TẠO VÀ TÍNH TOÁN THỐNG KÊ (Tính trên toàn bộ dữ liệu gốc) ---
        int total = dsKH.size();
        int countDong = 0;
        int countBac = 0;
        int countVang = 0;

        for (KhachHang kh : dsKH) {
            String hang = khachHangDAO.xepHangKhachHang(kh.getDiemTichLuy());
            // Chỉ đếm hạng nếu họ là thành viên (giả định Khách thường không có hạng cụ thể)
            if (kh.isLaThanhVien()) { 
                if (hang.equals("Vàng")) countVang++;
                else if (hang.equals("Bạc")) countBac++;
                else if (hang.equals("Đồng")) countDong++;
            }
        }
        
        // Cập nhật thống kê
        lblTotal.setText(String.valueOf(total));
        lblHangDong.setText(String.valueOf(countDong));
        lblHangBac.setText(String.valueOf(countBac));
        lblHangVang.setText(String.valueOf(countVang));
        
        // --- LỌC DỮ LIỆU ĐỂ HIỂN THỊ (Logic chính) ---
        List<KhachHang> filtered = dsKH.stream().filter(kh -> {
            String ten = safeLower(kh.getTenKhachHang());
            String sdt = safeLower(kh.getSdt());
            String email = safeLower(kh.getEmail());
            String ma = safeLower(kh.getMaKhachHang());
            String hang = khachHangDAO.xepHangKhachHang(kh.getDiemTichLuy()); 

            boolean matchKeyword = keyword.isEmpty()
                    || ten.contains(keyword)
                    || sdt.contains(keyword)
                    || email.contains(keyword)
                    || ma.contains(keyword);

            // BƯỚC SỬA LỖI: Lọc dựa trên giá trị Hạng được chọn từ ComboBox
            boolean matchFilter = filter.equals("Tất cả") 
                    || (filter.equals("Đồng") && kh.isLaThanhVien() && hang.equals("Đồng")) // Lọc hạng Đồng
                    || (filter.equals("Bạc") && kh.isLaThanhVien() && hang.equals("Bạc")) // Lọc hạng Bạc
                    || (filter.equals("Vàng") && kh.isLaThanhVien() && hang.equals("Vàng")); // Lọc hạng Vàng

            return matchKeyword && matchFilter;
        }).collect(Collectors.toList());

        for (KhachHang kh : filtered) {
            String loai = kh.isLaThanhVien() ? "Thành viên" : "Khách thường";
            String hang = khachHangDAO.xepHangKhachHang(kh.getDiemTichLuy()); 
            
            model.addRow(new Object[]{
                    kh.getMaKhachHang(),
                    kh.getTenKhachHang(),
                    kh.getSdt(),
                    kh.getEmail(),
                    kh.getDiemTichLuy(),
                    hang,  
                    loai
            });
        }
    }

    private String safeLower(String s) {
        return (s == null) ? "" : s.toLowerCase();
    }

    // ===== UI Helpers (Copy từ NhanVien_TraCuu_View) =====
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