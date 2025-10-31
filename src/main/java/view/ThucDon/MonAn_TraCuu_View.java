package view.ThucDon;

import dao.MonAn_DAO;
import entity.MonAn;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MonAn_TraCuu_View extends JPanel {

    private JTextField txtSearch;
    private JComboBox<String> cboFilterTrangThai;
    private JComboBox<String> cboFilterLoaiMon; // THÊM
    private DefaultTableModel model;
    private JTable table;
    private MonAn_DAO monAnDAO;

    // Stats Labels
    private JLabel lblTotalMonAn, lblConBan, lblHetHang;
	private JTable tblMonAn;

    // Màu sắc & Style
    private static final Color PRIMARY_BLUE = new Color(41, 128, 185);
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    private static final Color BG_COLOR = new Color(251, 248, 241); 
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(220, 221, 225);

    public MonAn_TraCuu_View() {
        monAnDAO = new MonAn_DAO();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 20));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(25, 30, 30, 30));

        // ===== TOP SECTION (HEADER, SEARCH, STATS) =====
        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        
        topSection.add(createHeaderSection());
        topSection.add(Box.createVerticalStrut(20));
        topSection.add(createSearchSection());
        topSection.add(Box.createVerticalStrut(20));
        topSection.add(createStatsPanel());

        add(topSection, BorderLayout.NORTH);

        // ===== TABLE SECTION (CENTER) =====
        add(createTableSection(), BorderLayout.CENTER);
    }
    
    private void setupTableStyle() {
        // Code này giữ nguyên style cho bảng
        tblMonAn.setRowHeight(48);
        tblMonAn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblMonAn.setSelectionBackground(new Color(PRIMARY_BLUE.getRed(), PRIMARY_BLUE.getGreen(), PRIMARY_BLUE.getBlue(), 30));
        tblMonAn.setSelectionForeground(TEXT_PRIMARY);
        tblMonAn.setGridColor(BORDER_COLOR);
        tblMonAn.setShowGrid(true);
        tblMonAn.setIntercellSpacing(new Dimension(1, 1));
        
        // Căn giữa và định dạng cột (Cần kiểm tra lại chỉ số cột sau khi xóa cột Sửa/Xóa)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        // Cột 0: Mã Món
        tblMonAn.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); 
        
        // Cột 2: Đơn giá
        tblMonAn.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() { 
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Double) {
                    setText(currencyFormatter.format(value));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                } else if (value instanceof Number) {
                    setText(currencyFormatter.format(((Number) value).doubleValue()));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                }
                return c;
            }
        });
        // Cột 3: Trạng Thái
        tblMonAn.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); 
        
        // Renderer cho cột Trạng Thái (hiển thị màu)
        tblMonAn.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
             @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    setHorizontalAlignment(CENTER);
                    String status = value.toString();
                    if (status.equals("Còn bán")) {
                        c.setForeground(new Color(46, 204, 113).darker());
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else if (status.equals("Hết")) {
                        c.setForeground(new Color(231, 76, 60).darker());
                        c.setFont(c.getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(TEXT_PRIMARY);
                        c.setFont(c.getFont().deriveFont(Font.PLAIN));
                    }
                }
                return c;
            }
        });
    }

    private JPanel createHeaderSection() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Tra cứu Thực đơn"); 
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Tìm kiếm và lọc thông tin các món ăn");
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

        txtSearch = new JTextField(20);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchData(); 
            }
        });

        // 1. Lọc Trạng thái
        JLabel lblFilterTrangThai = new JLabel("Trạng thái:");
        lblFilterTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cboFilterTrangThai = new JComboBox<>(new String[]{"Tất cả", "Còn bán", "Hết"});
        cboFilterTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboFilterTrangThai.setBackground(Color.WHITE);
        cboFilterTrangThai.setPreferredSize(new Dimension(100, 38));
        cboFilterTrangThai.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        cboFilterTrangThai.addActionListener(e -> searchData());
        
        // 2. Lọc Loại món ăn (THÊM)
        JLabel lblFilterLoaiMon = new JLabel("Loại món:");
        lblFilterLoaiMon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        List<String> loaiMonList = monAnDAO.getUniqueLoaiMonAn();
        loaiMonList.add(0, "Tất cả"); // Thêm tùy chọn "Tất cả"
        cboFilterLoaiMon = new JComboBox<>(loaiMonList.toArray(new String[0]));
        cboFilterLoaiMon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboFilterLoaiMon.setBackground(Color.WHITE);
        cboFilterLoaiMon.setPreferredSize(new Dimension(120, 38));
        cboFilterLoaiMon.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        cboFilterLoaiMon.addActionListener(e -> searchData());


        JButton btnSearch = createStyledButton("Tìm", PRIMARY_BLUE);
        btnSearch.addActionListener(e -> searchData());

        JButton btnReset = createStyledButton("Làm mới", TEXT_SECONDARY);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cboFilterTrangThai.setSelectedIndex(0);
            cboFilterLoaiMon.setSelectedIndex(0);
            loadData();
        });

        searchCard.add(lblSearch);
        searchCard.add(txtSearch);
        searchCard.add(lblFilterTrangThai);
        searchCard.add(cboFilterTrangThai);
        searchCard.add(lblFilterLoaiMon); // THÊM
        searchCard.add(cboFilterLoaiMon); // THÊM
        searchCard.add(btnSearch);
        searchCard.add(btnReset);

        return searchCard;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        lblTotalMonAn = new JLabel("0");
        JPanel totalCard = createStatCard("0", "Tổng số món ăn", PRIMARY_BLUE, lblTotalMonAn);

        lblConBan = new JLabel("0");
        JPanel activeCard = createStatCard("0", "Món đang bán", new Color(46, 204, 113), lblConBan);

        lblHetHang = new JLabel("0");
        JPanel outOfStockCard = createStatCard("0", "Món hết hàng/ngừng bán", new Color(243, 156, 18), lblHetHang);

        statsPanel.add(totalCard);
        statsPanel.add(activeCard);
        statsPanel.add(outOfStockCard);

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
        // THAY ĐỔI: Thêm cột "Loại món ăn"
        String[] cols = {"Mã Món", "Tên Món Ăn", "Loại món ăn", "Đơn Giá", "Trạng Thái", "Đường dẫn ảnh"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        tblMonAn = new JTable(model);
        table = tblMonAn;
        setupTableStyle(); 
        
        // Điều chỉnh độ rộng cột
        table.getColumnModel().getColumn(2).setPreferredWidth(120); // Loại món ăn
        table.getColumnModel().getColumn(5).setPreferredWidth(150); // Đường dẫn ảnh

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tableCard = new RoundedPanel(12, CARD_BG);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(new EmptyBorder(20, 25, 15, 25));

        JLabel tableTitle = new JLabel("Danh sách món ăn");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_PRIMARY);

        tableHeader.add(tableTitle, BorderLayout.WEST);
        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        return tableCard;
    }

    // --- LOGIC TẢI VÀ THỐNG KÊ DỮ LIỆU ---

    public void loadData() {
        model.setRowCount(0);
        List<MonAn> dsMonAn = monAnDAO.getAllMonAn();
        
        int total = dsMonAn.size();
        int conBan = 0;
        int hetHang = 0;

        populateTable(dsMonAn);
        
        // Tính thống kê
        for(MonAn mon : dsMonAn) {
            if(mon.isTrangThai()) {
                conBan++;
            } else {
                hetHang++;
            }
        }

        // Cập nhật thống kê
        lblTotalMonAn.setText(String.valueOf(total));
        lblConBan.setText(String.valueOf(conBan));
        lblHetHang.setText(String.valueOf(hetHang));
        
        // Cập nhật ComboBox Loại món ăn
        List<String> loaiMonList = monAnDAO.getUniqueLoaiMonAn();
        cboFilterLoaiMon.removeAllItems();
        cboFilterLoaiMon.addItem("Tất cả");
        for (String loai : loaiMonList) {
            cboFilterLoaiMon.addItem(loai);
        }
    }

    private void searchData() {
    	
    	String keyword = txtSearch.getText().trim();
    	
    	Object selectedStatusObj = cboFilterTrangThai.getSelectedItem();
        String statusFilter = (selectedStatusObj != null) ? selectedStatusObj.toString() : "Tất cả";
        
        Object selectedLoaiMonObj = cboFilterLoaiMon.getSelectedItem();
        String loaiMonFilter = (selectedLoaiMonObj != null) ? selectedLoaiMonObj.toString() : "Tất cả";
        
        model.setRowCount(0);
        
        // SỬA: Truyền thêm tham số loại món ăn
        List<MonAn> dsMonAn = monAnDAO.searchMonAn(keyword, statusFilter, loaiMonFilter); 
        
        populateTable(dsMonAn);
    }

    private void populateTable(List<MonAn> dsMonAn) {
        for (MonAn mon : dsMonAn) {
            model.addRow(new Object[]{
                mon.getMaMonAn().trim(),
                mon.getTenMonAn(),
                mon.getLoaiMonAn(), // HIỂN THỊ LOẠI MÓN
                mon.getDonGia(),
                mon.isTrangThai() ? "Còn bán" : "Hết",
                mon.getImagePath() 
            });
        }
    }
    
 
    
    
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
        // Code này giữ nguyên
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