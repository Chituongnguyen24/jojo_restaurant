package view.HoaDon;

import entity.KhuyenMai;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.KhuyenMai_DAO;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class KhuyenMai_TraCuu_View extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cboFilter;
    private KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO();

    // Stats Labels
    private JLabel lblTotal; 
    private JLabel lblHoatDong; 
    private JLabel lblSapBatDau;  
    private JLabel lblHetHan;  

    // Màu sắc và Styles chung (ĐỒNG BỘ VỚI KHÁCH HÀNG)
    private static final Color PRIMARY_BLUE = new Color(41, 128, 185); 
    private static final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private static final Color WARNING_ORANGE = new Color(243, 156, 18);
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    private static final Color BG_COLOR = new Color(251, 248, 241); 
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(220, 221, 225);
    private static final Color RED_ORANGE = new Color(255, 99, 71); 

    public KhuyenMai_TraCuu_View() {
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
        loadKhuyenMaiData();
    }

    private JPanel createHeaderSection() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Tra cứu khuyến mãi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Tìm kiếm, lọc thông tin và quản lý khuyến mãi");
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
                loadKhuyenMaiData();
            }
        });

        JLabel lblFilter = new JLabel("Trạng thái:");
        lblFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // ComboBox lọc theo Trạng thái
        cboFilter = new JComboBox<>(new String[]{"Tất cả", "Hoạt động", "Sắp bắt đầu", "Hết hạn"});
        cboFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboFilter.setBackground(Color.WHITE);
        cboFilter.setPreferredSize(new Dimension(150, 38));
        cboFilter.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        cboFilter.addActionListener(e -> loadKhuyenMaiData());

        JButton btnSearch = createStyledButton("Tìm kiếm", PRIMARY_BLUE);
        btnSearch.addActionListener(e -> loadKhuyenMaiData());

        JButton btnReset = createStyledButton("Làm mới", TEXT_SECONDARY);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cboFilter.setSelectedIndex(0);
            loadKhuyenMaiData();
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

        // 1. Tổng số khuyến mãi
        lblTotal = new JLabel("0");
        JPanel totalCard = createStatCard("0", "Tổng số KM", PRIMARY_BLUE, lblTotal);

        // 2. Hoạt động (SUCCESS_GREEN)
        lblHoatDong = new JLabel("0");
        JPanel hoatDongCard = createStatCard("0", "Hoạt động", SUCCESS_GREEN, lblHoatDong);

        // 3. Sắp bắt đầu (WARNING_ORANGE)
        lblSapBatDau = new JLabel("0");
        JPanel sapBatDauCard = createStatCard("0", "Sắp bắt đầu", WARNING_ORANGE, lblSapBatDau);

        // 4. Hết hạn (RED_ORANGE)
        lblHetHan = new JLabel("0");
        JPanel hetHanCard = createStatCard("0", "Hết hạn", RED_ORANGE, lblHetHan);

        statsPanel.add(totalCard);
        statsPanel.add(hoatDongCard);
        statsPanel.add(sapBatDauCard);
        statsPanel.add(hetHanCard);

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
        // SỬA: Thêm cột Loại KM
        String[] cols = {"Mã KM", "Tên KM", "Giá trị", "Loại KM", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái"};
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

        JLabel tableTitle = new JLabel("Danh sách khuyến mãi");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_PRIMARY);

        tableHeader.add(tableTitle, BorderLayout.WEST);
        tableCard.add(tableHeader, BorderLayout.CENTER);
        tableCard.add(scrollPane, BorderLayout.SOUTH); // SỬA: Dùng CENTER

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

        // Áp dụng Cell Renderer
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã KM
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer); // Giá trị
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Ngày bắt đầu
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer); // Ngày kết thúc
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Trạng thái
        
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
                    
                    if (column == 6) { // Cột Trạng thái
                        if (text.equals("Hoạt động") || text.equals("Luôn áp dụng")) {
                            c.setForeground(SUCCESS_GREEN.darker());
                        } else if (text.equals("Hết hạn")) {
                            c.setForeground(RED_ORANGE.darker());
                        } else if (text.equals("Sắp bắt đầu")) {
                            c.setForeground(WARNING_ORANGE.darker());
                        } 
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    }
                }
                return c;
            }
        };
        
        table.getColumnModel().getColumn(6).setCellRenderer(statusRenderer); // Trạng thái

        // Điều chỉnh độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(80); // Mã KM
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Tên KM
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Giá trị
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Loại KM
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Ngày bắt đầu
        table.getColumnModel().getColumn(5).setPreferredWidth(120); // Ngày kết thúc
        table.getColumnModel().getColumn(6).setPreferredWidth(120); // Trạng thái
    }

    private String getCurrentTrangThai(KhuyenMai km) {
        if (km.getMaKM().equals("KM00000000")) return "Luôn áp dụng";

        LocalDate now = LocalDate.now();
        if (now.isBefore(km.getNgayApDung())) {
            return "Sắp bắt đầu";
        } else if (now.isAfter(km.getNgayHetHan())) {
            return "Hết hạn";
        } else {
            return "Hoạt động";
        }
    }

    // ====== Load + filter dữ liệu ======
    private void loadKhuyenMaiData() {
        model.setRowCount(0);
        List<KhuyenMai> dsKM = khuyenMaiDAO.getAllKhuyenMai();

        String keyword = txtSearch.getText().trim().toLowerCase();
        String filter = (String) cboFilter.getSelectedItem();
        
        // --- KHỞI TẠO VÀ TÍNH TOÁN THỐNG KÊ (Tính trên toàn bộ dữ liệu gốc) ---
        int total = dsKM.size();
        int countHoatDong = 0;
        int countSapBatDau = 0;
        int countHetHan = 0;

        for (KhuyenMai km : dsKM) {
            String trangThai = getCurrentTrangThai(km);
            if ("Hoạt động".equals(trangThai) || "Luôn áp dụng".equals(trangThai)) countHoatDong++;
            else if ("Sắp bắt đầu".equals(trangThai)) countSapBatDau++;
            else if ("Hết hạn".equals(trangThai)) countHetHan++;
        }
        
        // Cập nhật thống kê
        lblTotal.setText(String.valueOf(total));
        lblHoatDong.setText(String.valueOf(countHoatDong));
        lblSapBatDau.setText(String.valueOf(countSapBatDau));
        lblHetHan.setText(String.valueOf(countHetHan));
        
        // --- LỌC DỮ LIỆU ĐỂ HIỂN THỊ (Logic chính) ---
        List<KhuyenMai> filtered = dsKM.stream().filter(km -> {
            String ten = safeLower(km.getMoTa());
            String ma = safeLower(km.getMaKM());
            String trangThai = getCurrentTrangThai(km); 

            boolean matchKeyword = keyword.isEmpty()
                    || ten.contains(keyword)
                    || ma.contains(keyword);

            boolean matchFilter = filter.equals("Tất cả") 
                    || (filter.equals("Hoạt động") && (trangThai.equals("Hoạt động") || trangThai.equals("Luôn áp dụng")))
                    || (filter.equals("Sắp bắt đầu") && trangThai.equals("Sắp bắt đầu"))
                    || (filter.equals("Hết hạn") && trangThai.equals("Hết hạn"));

            return matchKeyword && matchFilter;
        }).collect(Collectors.toList());

        for (KhuyenMai km : filtered) {
            String trangThai = getCurrentTrangThai(km);
            String giaTriHienThi;
            if (km.getMucKM() < 1.0) {
                 giaTriHienThi = String.format("%.0f%%", km.getMucKM() * 100);
            } else {
                 giaTriHienThi = String.format("%,.0f VNĐ", km.getMucKM());
            }

            model.addRow(new Object[]{
                    km.getMaKM(),
                    km.getMoTa(),
                    giaTriHienThi,
                    km.getLoaiKM(), // MỚI
                    km.getNgayApDung().toString(),
                    km.getNgayHetHan().toString(),
                    trangThai
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