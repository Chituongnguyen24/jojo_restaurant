package view.Ban;

import dao.Ban_DAO;
import entity.Ban;
import enums.TrangThaiBan;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.Border;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class Ban_View extends JPanel {

    private Ban_DAO banDAO;
    private Map<String, List<Ban>> tablesByArea;
    private Map<String, Integer> areaTableCounts;
    private List<String> areaNames;
    private String currentArea;
    private JPanel tablesPanel;
    private JLabel mapTitle;
    private Map<String, JButton> floorButtons;
    private JPanel floorButtonContainer;

    // Icon paths cho từng khu vực
    private Map<String, String> areaImagePaths = new LinkedHashMap<String, String>() {{
        put("Sân thượng", "images/icon/bannho.png");
        put("Sân vườn", "images/icon/thongthuong.png");
        put("Tầng 2", "images/icon/bannho.png");
        put("Tầng trệt", "images/icon/thongthuong.png");
        put("Phòng VIP", "images/icon/vip.png");
    }};

    private static final String DEFAULT_TABLE_IMAGE = "images/icon/bansanthuong.png";
    private Map<String, ImageIcon> areaIcons;

 
    // Colors 
    private static final Color COLOR_BACKGROUND = new Color(254, 252, 247); // Màu be nhạt
    private static final Color COLOR_WHITE = Color.WHITE; // Dùng cho nền các panel
    private static final Color COLOR_TEXT_PRIMARY = new Color(52, 58, 64); // Chữ chính (đậm)
    private static final Color COLOR_TEXT_SECONDARY = new Color(108, 117, 125); // Chữ phụ (xám)
    private static final Color COLOR_BORDER = new Color(233, 236, 239); // Viền panel
    private static final Color COLOR_BORDER_LIGHT = new Color(233, 236, 239); // Viền legend

    // --- Sidebar ---
    private static final Color COLOR_PRIMARY = new Color(242, 118, 29); 
    private static final Color COLOR_PRIMARY_DARK = new Color(242, 118, 29).darker(); 
    private static final Color COLOR_SIDEBAR_UNSELECTED_BG = new Color(248, 249, 250); 
    private static final Color COLOR_SIDEBAR_UNSELECTED_BADGE_BG = new Color(222, 226, 230); 

    // --- Trạng thái bàn ---
    private static final Color COLOR_STATUS_TRONG = new Color(40, 167, 69); // Xanh lá
    private static final Color COLOR_STATUS_CO_KHACH = new Color(220, 53, 69); // Đỏ
    private static final Color COLOR_STATUS_DA_DAT = new Color(255, 193, 7); // Vàng
    
    private static final Color COLOR_PRIMARY_LIGHT = new Color(255, 187, 115); 
    private static final Color COLOR_BUTTON_ADD = new Color(10, 102, 255);
    private static final Color COLOR_BUTTON_ADD_HOVER = new Color(0, 84, 215);
    private static final Color COLOR_SHADOW = new Color(0, 0, 0, 12);
    
  
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_CARD_TITLE = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_CARD_BODY = new Font("Segoe UI", Font.PLAIN, 12);

    public Ban_View() {
        banDAO = new Ban_DAO();
        tablesByArea = new LinkedHashMap<>();
        areaTableCounts = new LinkedHashMap<>();
        areaNames = new ArrayList<>();
        floorButtons = new LinkedHashMap<>();
        areaIcons = new LinkedHashMap<>();

        loadAllAreaIcons();
        DichVuLapLichDatBan.getInstance().start(banDAO);

        setLayout(new BorderLayout());
        setBackground(COLOR_BACKGROUND);

        JPanel mainPanel = new JPanel(new BorderLayout(25, 25));
        mainPanel.setBackground(COLOR_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout(25, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(createLeftSidebar(), BorderLayout.WEST);
        contentPanel.add(createRightPanel(), BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        reloadDataAndRefreshUI();
    }

    private void reloadDataAndRefreshUI() {
        loadDataFromDB();
        updateSidebarContent();
        updateSidebarSelection();
        updateTablesDisplay();
        updateHeaderTitle();
    }

    private void loadAllAreaIcons() {
        for (Map.Entry<String, String> entry : areaImagePaths.entrySet()) {
            ImageIcon icon = loadImageIcon(entry.getValue());
            if (icon != null) {
                areaIcons.put(entry.getKey(), icon);
            }
        }
        ImageIcon defaultIcon = loadImageIcon(DEFAULT_TABLE_IMAGE);
        if (defaultIcon != null) {
            areaIcons.put("DEFAULT", defaultIcon);
        }
    }

    private ImageIcon loadImageIcon(String imagePath) {
        try {
            ImageIcon originalIcon = new ImageIcon(imagePath);
            if (originalIcon.getIconWidth() <= 0) return null;
            Image scaledImage = originalIcon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            return null;
        }
    }

    private ImageIcon getIconForCurrentArea() {
        ImageIcon icon = areaIcons.get(currentArea);
        return icon != null ? icon : areaIcons.get("DEFAULT");
    }

    private void loadDataFromDB() {
        areaTableCounts = banDAO.getSoBanTheoKhuVuc();
        areaNames = new ArrayList<>(areaTableCounts.keySet());
        
        if (currentArea == null && !areaNames.isEmpty()) {
            currentArea = areaNames.get(0);
        } else if (areaNames.isEmpty()) {
            currentArea = "Không có dữ liệu";
        }
        
        tablesByArea.clear();
        Map<String, String> khuVucMaVsTen = banDAO.getDanhSachKhuVuc();
        
        for (String areaName : areaNames) {
            String maKhuVuc = khuVucMaVsTen.entrySet().stream()
                .filter(entry -> entry.getValue().equals(areaName))
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
            if (maKhuVuc != null) {
                tablesByArea.put(areaName, banDAO.getBanTheoKhuVuc(maKhuVuc));
            }
        }
    }

    private JPanel createHeaderPanel() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(), 20);
        panel.setBackground(COLOR_WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            BorderFactory.createEmptyBorder(30, 35, 30, 35)
        ));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Quản lý bàn");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Quản lý trạng thái bàn theo từng khu vực một cách hiệu quả");
        subtitleLabel.setFont(FONT_SUBTITLE);
        subtitleLabel.setForeground(COLOR_TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);

        panel.add(titlePanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel createLeftSidebar() {
        RoundedPanel sidebar = new RoundedPanel(null, 20);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_WHITE);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            BorderFactory.createEmptyBorder(30, 25, 30, 25)
        ));
        sidebar.setPreferredSize(new Dimension(300, 0));

        JLabel areaLabel = new JLabel("Khu vực");
        areaLabel.setFont(FONT_HEADER);
        areaLabel.setForeground(COLOR_TEXT_PRIMARY);
        areaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel areaSubLabel = new JLabel("Chọn khu vực để xem sơ đồ bàn");
        areaSubLabel.setFont(FONT_BODY);
        areaSubLabel.setForeground(COLOR_TEXT_SECONDARY);
        areaSubLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(areaLabel);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(areaSubLabel);
        sidebar.add(Box.createVerticalStrut(25));

        floorButtonContainer = new JPanel();
        floorButtonContainer.setLayout(new BoxLayout(floorButtonContainer, BoxLayout.Y_AXIS));
        floorButtonContainer.setOpaque(false);
        sidebar.add(floorButtonContainer);
        sidebar.add(Box.createVerticalGlue());

        return sidebar;
    }

    private void updateSidebarContent() {
        floorButtonContainer.removeAll();
        floorButtons.clear();
        
        for (String areaName : areaNames) {
            int count = areaTableCounts.getOrDefault(areaName, 0);
            JButton floorBtn = createFloorButton(areaName, count + " bàn");
            floorBtn.addActionListener(e -> switchArea(areaName));
            floorButtons.put(areaName, floorBtn);
            floorButtonContainer.add(floorBtn);
            floorButtonContainer.add(Box.createVerticalStrut(12));
        }
        
        floorButtonContainer.revalidate();
        floorButtonContainer.repaint();
    }

    private void switchArea(String areaName) {
        currentArea = areaName;
        updateSidebarSelection();
        updateTablesDisplay();
        updateHeaderTitle();
    }

    private void updateSidebarSelection() {
        for (Map.Entry<String, JButton> entry : floorButtons.entrySet()) {
            styleFloorButton(entry.getValue(), entry.getKey().equals(currentArea));
        }
    }

    private JButton createFloorButton(String floorName, String count) {
        RoundedButton button = new RoundedButton("", 15);
        button.setLayout(new BorderLayout(15, 0));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        button.setPreferredSize(new Dimension(250, 56));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel(floorName);
        nameLabel.setFont(FONT_BUTTON);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JLabel countLabel = new JLabel(count);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        countLabel.setOpaque(false); // Đã sửa lỗi hiển thị
        countLabel.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel countWrapper = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        countWrapper.setOpaque(false);
        countWrapper.add(countLabel, BorderLayout.CENTER);

        button.add(nameLabel, BorderLayout.WEST);
        button.add(countWrapper, BorderLayout.EAST);
        styleFloorButton(button, false);
        return button;
    }

    private void styleFloorButton(JButton button, boolean selected) {
        if (button.getComponentCount() < 2) return;
        
        JLabel nameLabel = (JLabel) button.getComponent(0);
        JPanel countWrapper = (JPanel) button.getComponent(1);
        JLabel countLabel = (JLabel) countWrapper.getComponent(0);

        if (selected) {
            // Màu cam khi được chọn
            button.setBackground(COLOR_PRIMARY);
            nameLabel.setForeground(Color.WHITE);
            countLabel.setForeground(Color.WHITE);
            countWrapper.setBackground(COLOR_PRIMARY_DARK); // Badge cam đậm
            button.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY, 1));
        } else {
            // Màu xám nhạt khi không chọn
            button.setBackground(COLOR_SIDEBAR_UNSELECTED_BG);
            nameLabel.setForeground(COLOR_TEXT_PRIMARY);
            countLabel.setForeground(COLOR_TEXT_PRIMARY); // Chữ đen/xám
            countWrapper.setBackground(COLOR_SIDEBAR_UNSELECTED_BADGE_BG); // Badge xám nhạt
            button.setBorder(BorderFactory.createLineBorder(COLOR_BORDER, 1));
        }
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 25));
        panel.setOpaque(false);

        RoundedPanel headerPanel = new RoundedPanel(new BorderLayout(15, 0), 20);
        headerPanel.setBackground(COLOR_WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        mapTitle = new JLabel("Sơ đồ bàn");
        mapTitle.setFont(FONT_HEADER);
        mapTitle.setForeground(COLOR_TEXT_PRIMARY);
        mapTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel mapSubtitle = new JLabel("Click vào bàn để thay đổi trạng thái (đặt bàn, nhận khách, trả bàn)");
        mapSubtitle.setFont(FONT_BODY);
        mapSubtitle.setForeground(COLOR_TEXT_SECONDARY);
        mapSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(mapTitle);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(mapSubtitle);

        RoundedButton btnThemBan = new RoundedButton("+ Thêm Bàn Mới", 12);
        btnThemBan.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnThemBan.setBackground(COLOR_BUTTON_ADD);
        btnThemBan.setForeground(COLOR_WHITE);
        btnThemBan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThemBan.setPreferredSize(new Dimension(180, 45));
        btnThemBan.addActionListener(e -> moDialogThemBan());
        btnThemBan.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnThemBan.setBackground(COLOR_BUTTON_ADD_HOVER);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnThemBan.setBackground(COLOR_BUTTON_ADD);
            }
        });

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(btnThemBan);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(buttonWrapper, BorderLayout.EAST);

        tablesPanel = new JPanel(new GridLayout(0, 4, 20, 20));
        tablesPanel.setOpaque(false);

        RoundedPanel tablesContainer = new RoundedPanel(new BorderLayout(), 20);
        tablesContainer.setBackground(COLOR_WHITE);
        tablesContainer.setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));
        tablesContainer.add(tablesPanel, BorderLayout.NORTH);
        tablesContainer.add(createLegendPanel(), BorderLayout.SOUTH);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tablesContainer, BorderLayout.CENTER);
        return panel;
    }

    private void moDialogThemBan() {
        ThemBan_Dialog dialog = new ThemBan_Dialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            this::reloadDataAndRefreshUI
        );
        dialog.setVisible(true);
    }

    private void updateHeaderTitle() {
        if (mapTitle != null) {
            mapTitle.setText("Sơ đồ bàn - " + currentArea);
        }
    }

    private void updateTablesDisplay() {
        tablesPanel.removeAll();
        List<Ban> tables = tablesByArea.get(currentArea);
        if (tables != null) {
            for (Ban ban : tables) {
                tablesPanel.add(createTableCard(ban));
            }
        }
        tablesPanel.revalidate();
        tablesPanel.repaint();
    }

    private JPanel createTableCard(Ban ban) {
        RoundedPanel card = new RoundedPanel(null, 18);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_WHITE);

        // Gán màu trạng thái mới (Xanh/Đỏ/Vàng)
        Color mauTrangThaiHienTai;
        if (ban.getTrangThai() == TrangThaiBan.TRONG) {
            mauTrangThaiHienTai = COLOR_STATUS_TRONG;
        } else if (ban.getTrangThai() == TrangThaiBan.CO_KHACH) {
            mauTrangThaiHienTai = COLOR_STATUS_CO_KHACH;
        } else { // DA_DAT hoặc trạng thái khác
            mauTrangThaiHienTai = COLOR_STATUS_DA_DAT;
        }

        card.setBorderColor(mauTrangThaiHienTai); // Set màu viền mới
        card.setBorder(BorderFactory.createEmptyBorder(20, 18, 20, 18));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Icon ảnh
        ImageIcon currentIcon = getIconForCurrentArea();
        JLabel lblIcon = new JLabel();
        lblIcon.setIcon(currentIcon);
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Tên bàn
        String loaiBanTen = ban.getLoaiBan().getTenHienThi();
        if (loaiBanTen.equalsIgnoreCase("Bàn VIP")) {
            loaiBanTen = "VIP";
        }
        JLabel lblName = new JLabel(String.format("%s (%s)", ban.getMaBan(), loaiBanTen));
        lblName.setFont(FONT_CARD_TITLE);
        lblName.setForeground(COLOR_TEXT_PRIMARY);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Số chỗ
        JLabel lblCapacity = new JLabel(ban.getSoCho() + " chỗ");
        lblCapacity.setFont(FONT_CARD_BODY);
        lblCapacity.setForeground(COLOR_TEXT_SECONDARY);
        lblCapacity.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Status badge
        JPanel statusBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        statusBadge.setOpaque(false);
        statusBadge.setBackground(mauTrangThaiHienTai); 
        statusBadge.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

        JLabel lblStatus = new JLabel(ban.getTrangThai().toString());
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStatus.setForeground(COLOR_WHITE);
        lblStatus.setBorder(BorderFactory.createEmptyBorder(6, 16, 6, 16));
        statusBadge.add(lblStatus);
        statusBadge.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblIcon);
        card.add(Box.createVerticalStrut(12));
        card.add(lblName);
        card.add(Box.createVerticalStrut(6));
        card.add(lblCapacity);
        card.add(Box.createVerticalStrut(12));
        card.add(statusBadge);

        // Click events
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                TrangThaiBan currentStatus = ban.getTrangThai();

                if (currentStatus == TrangThaiBan.TRONG) {
                    DatBan_Dialog dialog = new DatBan_Dialog(
                        (JFrame) SwingUtilities.getWindowAncestor(card),
                        ban,
                        () -> reloadDataAndRefreshUI()
                    );
                    dialog.setVisible(true);
                } else if (currentStatus == TrangThaiBan.DA_DAT) {
                    int confirm = JOptionPane.showConfirmDialog(card,
                        "Khách của bàn " + ban.getMaBan() + " đã đến?",
                        "Xác nhận khách đến",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        updateBanStatus(card, ban, TrangThaiBan.CO_KHACH, null);
                    }
                } else if (currentStatus == TrangThaiBan.CO_KHACH) {
                    int confirm = JOptionPane.showConfirmDialog(card,
                        "Khách đã rời bàn " + ban.getMaBan() + "?",
                        "Trả bàn",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        updateBanStatus(card, ban, TrangThaiBan.TRONG, "Bàn " + ban.getMaBan() + " đã được trả!");
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 251, 252));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(COLOR_WHITE);
            }
        });

        return card;
    }

    private void updateBanStatus(JPanel card, Ban ban, TrangThaiBan newStatus, String successMessage) {
        TrangThaiBan oldStatus = ban.getTrangThai();

        if (oldStatus == TrangThaiBan.DA_DAT && newStatus == TrangThaiBan.CO_KHACH) {
            DichVuLapLichDatBan.getInstance().huyLichHen(ban.getMaBan());
        }

        ban.setTrangThai(newStatus);
        boolean success = banDAO.capNhatBan(ban);

        if (success) {
            reloadDataAndRefreshUI();
            if (successMessage != null && !successMessage.isEmpty()) {
                JOptionPane.showMessageDialog(card, successMessage);
            }
        } else {
            ban.setTrangThai(oldStatus);
            JOptionPane.showMessageDialog(card, "Lỗi! Không thể cập nhật trạng thái bàn.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 20));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER_LIGHT));

        panel.add(createLegendItem("Trống", COLOR_STATUS_TRONG));
        panel.add(createLegendItem("Đã có khách", COLOR_STATUS_CO_KHACH));
        panel.add(createLegendItem("Đã được đặt trước", COLOR_STATUS_DA_DAT));
        return panel;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        item.setOpaque(false);

        JPanel colorBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
            }
        };
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(24, 24));
        colorBox.setOpaque(false);

        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setForeground(COLOR_TEXT_SECONDARY);

        item.add(colorBox);
        item.add(label);
        return item;
    }

    // Inner classes
    private class ShadowBorder implements Border {
        private int shadowSize = 4;

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int i = 0; i < shadowSize; i++) {
                int alpha = (int) (20 - (i * 4));
                g2.setColor(new Color(0, 0, 0, alpha));
                g2.drawRoundRect(x + i, y + i, width - 1 - (i * 2), height - 1 - (i * 2), 20, 20);
            }
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }

    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color borderColor;
        private int borderWidth = 2;

        public RoundedPanel(LayoutManager layout, int cornerRadius) {
            super(layout);
            this.cornerRadius = cornerRadius;
            setOpaque(false);
            this.borderColor = COLOR_BORDER;
        }

        public void setBorderColor(Color color) {
            this.borderColor = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.borderColor);
            g2.setStroke(new BasicStroke(borderWidth));
            g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, cornerRadius, cornerRadius));
            g2.dispose();
        }
    }

    private class RoundedButton extends JButton {
        private int cornerRadius;

        public RoundedButton(String text, int cornerRadius) {
            super(text);
            this.cornerRadius = cornerRadius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color bg = getBackground();
            if (getModel().isPressed()) {
                g2.setColor(bg.darker());
            } else if (getModel().isRollover()) {
            	g2.setColor(bg.brighter());
            } else {
                g2.setColor(bg);
            }

            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            super.paintComponent(g);
            g2.dispose();
        }
    }
}