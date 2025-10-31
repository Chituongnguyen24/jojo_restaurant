package view.NhanVien;

import dao.NhanVien_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NhanVien_TraCuu_View extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cboFilter;
    private JLabel lblTotalActive, lblTotalManager, lblTotalReceptionist;
    private NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Formatter for displaying date

    private static final Color PRIMARY_BLUE = new Color(41, 128, 185);
    private static final Color SUCCESS_GREEN = new Color(46, 204, 113);
    private static final Color WARNING_ORANGE = new Color(243, 156, 18);
    private static final Color DANGER_RED = new Color(231, 76, 60);
    private static final Color BG_COLOR = new Color(251, 248, 241);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(44, 62, 80);
    private static final Color TEXT_SECONDARY = new Color(127, 140, 141);
    private static final Color BORDER_COLOR = new Color(220, 221, 225);

    public NhanVien_TraCuu_View() {
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

        loadNhanVienData();
    }

    private JPanel createHeaderSection() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Tra cứu nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Tìm kiếm và lọc thông tin nhân viên theo nhiều tiêu chí");
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
                loadNhanVienData();
            }
        });

        JLabel lblFilter = new JLabel("Vai trò:");
        lblFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        cboFilter = new JComboBox<>(new String[]{"Tất cả", "NVQL", "NVTT"}); // SỬA: Dùng mã vai trò DB để lọc
        cboFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cboFilter.setBackground(Color.WHITE);
        cboFilter.setPreferredSize(new Dimension(150, 38));
        cboFilter.setBorder(new LineBorder(BORDER_COLOR, 1, true));
        cboFilter.addActionListener(e -> loadNhanVienData());

        JButton btnSearch = createStyledButton("Tìm kiếm", PRIMARY_BLUE);
        btnSearch.addActionListener(e -> loadNhanVienData());

        JButton btnReset = createStyledButton("Làm mới", TEXT_SECONDARY);
        btnReset.addActionListener(e -> {
            txtSearch.setText("");
            cboFilter.setSelectedIndex(0);
            loadNhanVienData();
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
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        lblTotalActive = new JLabel("0");
        JPanel activeCard = createStatCard("0", "Nhân viên hoạt động", PRIMARY_BLUE, lblTotalActive);

        lblTotalManager = new JLabel("0");
        JPanel managerCard = createStatCard("0", "Quản lý", SUCCESS_GREEN, lblTotalManager);

        lblTotalReceptionist = new JLabel("0");
        JPanel receptionistCard = createStatCard("0", "Tiếp tân", WARNING_ORANGE, lblTotalReceptionist);

        statsPanel.add(activeCard);
        statsPanel.add(managerCard);
        statsPanel.add(receptionistCard);

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
        // SỬA: Thêm các cột mới
        String[] cols = {"Mã NV", "Tên NV", "Giới tính", "Ngày sinh", "SĐT", "CCCD", "Chức vụ", "Trạng thái", "Tài khoản", "Vai trò TK"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(48);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(PRIMARY_BLUE.getRed(), PRIMARY_BLUE.getGreen(), PRIMARY_BLUE.getBlue(), 30));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER_COLOR);
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                }
                
                setBorder(new EmptyBorder(5, 10, 5, 10));
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                // Center align specific columns
                if (column == 0 || column == 2 || column == 3 || column == 7) { 
                    setHorizontalAlignment(CENTER);
                } else {
                    setHorizontalAlignment(LEFT);
                }
                
                // Color for role column (Vai trò TK)
                int roleColumnIndex = 9; 
                if (column == roleColumnIndex && value != null) {
                    if (value.equals("NVQL")) {
                        setForeground(SUCCESS_GREEN);
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if (value.equals("NVTT")) {
                        setForeground(WARNING_ORANGE);
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                } else if (!isSelected) {
                     setForeground(TEXT_PRIMARY); 
                     setFont(getFont().deriveFont(Font.PLAIN)); 
                }
                
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 48));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT); 

        // SỬA: Điều chỉnh độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(70);  // Mã NV
        table.getColumnModel().getColumn(1).setPreferredWidth(130); // Tên NV
        table.getColumnModel().getColumn(2).setPreferredWidth(60);  // Giới Tinh
        table.getColumnModel().getColumn(3).setPreferredWidth(90); // Ngày Sinh
        table.getColumnModel().getColumn(4).setPreferredWidth(90); // SĐT
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // CCCD
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Chức vụ
        table.getColumnModel().getColumn(7).setPreferredWidth(90); // Trạng thái
        table.getColumnModel().getColumn(8).setPreferredWidth(100); // Tài khoản
        table.getColumnModel().getColumn(9).setPreferredWidth(80); // Vai trò TK

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel tableCard = new RoundedPanel(12, CARD_BG);
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new LineBorder(BORDER_COLOR, 1, true));

        JPanel tableHeader = new JPanel(new BorderLayout());
        tableHeader.setOpaque(false);
        tableHeader.setBorder(new EmptyBorder(20, 25, 15, 25));

        JLabel tableTitle = new JLabel("Danh sách nhân viên");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_PRIMARY);

        tableHeader.add(tableTitle, BorderLayout.WEST);

        tableCard.add(tableHeader, BorderLayout.NORTH);
        tableCard.add(scrollPane, BorderLayout.CENTER);

        return tableCard;
    }

    private void loadNhanVienData() {
        model.setRowCount(0);
        String keyword = txtSearch.getText().trim();
        String filterRoleCode = (String) cboFilter.getSelectedItem();
        
        // SỬA: Cần mapping lại chuỗi hiển thị 'Quản lý'/'Tiếp tân' sang mã DB 'NVQL'/'NVTT' cho DAO
        String daoFilterRoleCode = filterRoleCode.equals("Quản lý") ? "NVQL" : (filterRoleCode.equals("Tiếp tân") ? "NVTT" : filterRoleCode);
        
        List<NhanVien> dsNV = nhanVienDAO.timKiemVaLocNhanVien(keyword, daoFilterRoleCode); 

        int totalActive = 0;
        int totalManager = 0;
        int totalReceptionist = 0;

        for (NhanVien nv : dsNV) {
            TaiKhoan tk = nv.getTaiKhoan();
            String ngaySinhStr = nv.getNgaySinh() != null ? nv.getNgaySinh().format(DATE_FORMATTER) : "-"; 
            String vaiTroTK = tk != null && tk.getVaiTro() != null ? tk.getVaiTro() : "-";
            
            model.addRow(new Object[]{
                    nv.getMaNhanVien(),
                    nv.getHoTen(),
                    !nv.getGioiTinh() ? "Nam" : "Nữ",
                    ngaySinhStr, 
                    nv.getSoDienThoai(),
                    nv.getSoCCCD() != null ? nv.getSoCCCD() : "-", 
                    nv.getChucVu() != null ? nv.getChucVu() : "-", 
                    nv.getTrangThai() != null ? nv.getTrangThai() : "-", 
                    tk != null ? tk.getTenDangNhap() : "Chưa có",
                    vaiTroTK 
            });

            if (nv.getTrangThai() != null && nv.getTrangThai().equals("Đang làm")) {
                totalActive++;
                if (nv.getChucVu() != null) {
                    if (nv.getChucVu().contains("quản lý")) totalManager++;
                    else if (nv.getChucVu().contains("tiếp tân")) totalReceptionist++;
                }
            }
        }

        lblTotalActive.setText(String.valueOf(totalActive));
        lblTotalManager.setText(String.valueOf(totalManager));
        lblTotalReceptionist.setText(String.valueOf(totalReceptionist));
    }

    private String safeLower(String s) {
        return (s == null) ? "" : s.toLowerCase();
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