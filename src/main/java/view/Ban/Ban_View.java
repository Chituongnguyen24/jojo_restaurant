package view.Ban;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Ban_View extends JPanel {
    
    private Map<String, java.util.List<TableInfo>> tablesByFloor;
    private JPanel tablesPanel;
    private String currentFloor = "Tầng trệt";
    
    public Ban_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 240));
        
        initializeTableData();
        
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(245, 245, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content area with sidebar and tables
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setOpaque(false);
        
        JPanel leftSidebar = createLeftSidebar();
        contentPanel.add(leftSidebar, BorderLayout.WEST);
        
        JPanel rightPanel = createRightPanel();
        contentPanel.add(rightPanel, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void initializeTableData() {
        tablesByFloor = new HashMap<>();
        
        java.util.List<TableInfo> tangTret = new ArrayList<>();
        tangTret.add(new TableInfo("Bàn 01", 4, TableStatus.TRONG));
        tangTret.add(new TableInfo("Bàn 02", 4, TableStatus.DA_CO_KHACH));
        tangTret.add(new TableInfo("Bàn 03", 4, TableStatus.DA_DUOC_DAT));
        tangTret.add(new TableInfo("Bàn 04", 4, TableStatus.TRONG));
        tangTret.add(new TableInfo("Bàn 05", 8, TableStatus.TRONG));
        tangTret.add(new TableInfo("Bàn 06", 4, TableStatus.TRONG));
        tablesByFloor.put("Tầng trệt", tangTret);
        
        java.util.List<TableInfo> tang2 = new ArrayList<>();
        tang2.add(new TableInfo("Bàn 07", 6, TableStatus.TRONG));
        tang2.add(new TableInfo("Bàn 08", 4, TableStatus.DA_CO_KHACH));
        tang2.add(new TableInfo("Bàn 09", 4, TableStatus.TRONG));
        tang2.add(new TableInfo("Bàn 10", 8, TableStatus.DA_DUOC_DAT));
        tablesByFloor.put("Tầng 2", tang2);
        
        java.util.List<TableInfo> tang3 = new ArrayList<>();
        tang3.add(new TableInfo("Bàn 11", 4, TableStatus.TRONG));
        tang3.add(new TableInfo("Bàn 12", 4, TableStatus.TRONG));
        tang3.add(new TableInfo("Bàn 13", 6, TableStatus.DA_CO_KHACH));
        tablesByFloor.put("Tầng 3", tang3);
        
        java.util.List<TableInfo> sanVuon = new ArrayList<>();
        sanVuon.add(new TableInfo("Bàn SV-01", 6, TableStatus.TRONG));
        sanVuon.add(new TableInfo("Bàn SV-02", 8, TableStatus.DA_DUOC_DAT));
        sanVuon.add(new TableInfo("Bàn SV-03", 4, TableStatus.TRONG));
        tablesByFloor.put("Sân vườn", sanVuon);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Quản lý bàn");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Quản lý trạng thái bàn theo từng khu vực");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createLeftSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        sidebar.setPreferredSize(new Dimension(280, 0));
        
        // Area selection section
        JLabel areaLabel = new JLabel("Khu vực");
        areaLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        areaLabel.setForeground(new Color(60, 60, 60));
        areaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel areaSubLabel = new JLabel("Chọn khu vực để xem số đỏ bàn");
        areaSubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        areaSubLabel.setForeground(new Color(120, 120, 120));
        areaSubLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sidebar.add(areaLabel);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(areaSubLabel);
        sidebar.add(Box.createVerticalStrut(20));
        
        // Floor buttons
        String[] floors = {"Tầng trệt", "Tầng 2", "Tầng 3", "Sân vườn"};
        int[] counts = {4, 7, 4, 3};
        
        for (int i = 0; i < floors.length; i++) {
            JButton floorBtn = createFloorButton(floors[i], counts[i] + "/8", i == 0);
            floorBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(floorBtn);
            sidebar.add(Box.createVerticalStrut(10));
        }
        
        sidebar.add(Box.createVerticalStrut(20));
        
        // Info section
        JLabel infoLabel = new JLabel("Thông tin khu vực");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        infoLabel.setForeground(new Color(60, 60, 60));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sidebar.add(infoLabel);
        sidebar.add(Box.createVerticalStrut(15));
        
        JLabel ruleTitle = new JLabel("6 bàn máy lạnh");
        ruleTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ruleTitle.setForeground(new Color(60, 60, 60));
        ruleTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sidebar.add(ruleTitle);
        sidebar.add(Box.createVerticalStrut(8));
        
        String[] rules = {
            "• Mỗi bàn có bếp nướng âm + hút khói",
            "• Phù hợp gia đình nhỏ, nhóm bạn",
            "• Không phụ phí"
        };
        
        for (String rule : rules) {
            JLabel ruleLabel = new JLabel(rule);
            ruleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            ruleLabel.setForeground(new Color(100, 100, 100));
            ruleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(ruleLabel);
            sidebar.add(Box.createVerticalStrut(5));
        }
        
        sidebar.add(Box.createVerticalGlue());
        
        return sidebar;
    }
    
    private JButton createFloorButton(String floorName, String count, boolean selected) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 0));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setPreferredSize(new Dimension(240, 45));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (selected) {
            button.setBackground(new Color(255, 152, 0));
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 152, 0), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
        } else {
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
        }
        
        JLabel nameLabel = new JLabel(floorName);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(selected ? Color.WHITE : new Color(60, 60, 60));
        
        JLabel countLabel = new JLabel(count);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLabel.setForeground(Color.WHITE);
        
        Color badgeColor = selected ? new Color(220, 120, 0) : new Color(76, 175, 80);
        countLabel.setOpaque(true);
        countLabel.setBackground(badgeColor);
        countLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        button.add(nameLabel, BorderLayout.WEST);
        button.add(countLabel, BorderLayout.EAST);
        
        button.addActionListener(e -> {
            currentFloor = floorName;
            updateTablesDisplay();
        });
        
        return button;
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);
        
        // Map header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel mapTitle = new JLabel("📍 Sơ đồ bàn - Tầng trệt");
        mapTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mapTitle.setForeground(new Color(60, 60, 60));
        mapTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel mapSubtitle = new JLabel("Click vào bàn để đặt bàn hoặc thay đổi trạng thái, bàn đã đặt có khách, bàn được đặt trước");
        mapSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mapSubtitle.setForeground(new Color(120, 120, 120));
        mapSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(mapTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(mapSubtitle);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Tables grid
        tablesPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        tablesPanel.setOpaque(false);
        
        updateTablesDisplay();
        
        JPanel tablesContainer = new JPanel(new BorderLayout());
        tablesContainer.setBackground(Color.WHITE);
        tablesContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        tablesContainer.add(tablesPanel, BorderLayout.CENTER);
        
        // Legend
        JPanel legendPanel = createLegendPanel();
        tablesContainer.add(legendPanel, BorderLayout.SOUTH);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tablesContainer, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateTablesDisplay() {
        tablesPanel.removeAll();
        
        java.util.List<TableInfo> tables = tablesByFloor.get(currentFloor);
        if (tables != null) {
            for (TableInfo table : tables) {
                tablesPanel.add(createTableCard(table));
            }
        }
        
        tablesPanel.revalidate();
        tablesPanel.repaint();
    }
    
    private JPanel createTableCard(TableInfo table) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        table.panel = card;

        card.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
        card.setBackground(getColorByStatus(table.status));

        JLabel lblName = new JLabel(table.name, SwingConstants.CENTER);
        lblName.setFont(new Font("Arial", Font.BOLD, 14));
        card.add(lblName, BorderLayout.CENTER);

        // === Xử lý click ===
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (table.status == TableStatus.TRONG) {
                    new DatBan_Dialog((JFrame) SwingUtilities.getWindowAncestor(card), table, () -> {
                        card.setBackground(getColorByStatus(table.status));
                    }).setVisible(true);
                } 
                else if (table.status == TableStatus.DA_DUOC_DAT) {
                    int confirm = JOptionPane.showConfirmDialog(
                        card,
                        "Khách của bàn " + table.name + " đã đến chưa?",
                        "Xác nhận khách đến",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        table.status = TableStatus.DA_CO_KHACH;
                        card.setBackground(getColorByStatus(table.status));
                    }
                } 
                else if (table.status == TableStatus.DA_CO_KHACH) {
                    int confirm = JOptionPane.showConfirmDialog(
                        card,
                        "Khách đã rời bàn " + table.name + "?",
                        "Trả bàn",
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        table.status = TableStatus.TRONG;
                        card.setBackground(getColorByStatus(table.status));
                        JOptionPane.showMessageDialog(card, "Bàn " + table.name + " đã được trả và sẵn sàng sử dụng!");
                    }
                }
            }
        });

        return card;
    }

    
    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        
        panel.add(createLegendItem("Trống", TableStatus.TRONG.getColor()));
        panel.add(createLegendItem("Đã có khách", TableStatus.DA_CO_KHACH.getColor()));
        panel.add(createLegendItem("Đã được đặt trước", TableStatus.DA_DUOC_DAT.getColor()));
        
        return panel;
    }
    
    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        item.setOpaque(false);
        
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(100, 100, 100));
        
        item.add(colorBox);
        item.add(label);
        
        return item;
    }
    
    private Color getColorByStatus(TableStatus status) {
        switch (status) {
            case TRONG: return new Color(34, 197, 94);
            case DA_DUOC_DAT: return new Color(251, 191, 36); 
            case DA_CO_KHACH: return new Color(239, 68, 68);
            default: return Color.LIGHT_GRAY;
        }
    }

    // Inner classes
    enum TableStatus {
        TRONG("Trống", new Color(34, 197, 94)),
        DA_CO_KHACH("Đã có khách", new Color(239, 68, 68)),
        DA_DUOC_DAT("Đã được đặt trước", new Color(251, 191, 36));
        
        private String text;
        private Color color;
        
        TableStatus(String text, Color color) {
            this.text = text;
            this.color = color;
        }
        
        public String getText() { return text; }
        public Color getColor() { return color; }
    }
    
    static class TableInfo {
        String name;
        int capacity;
        TableStatus status;
        JPanel panel; // <--- Thêm dòng này để lưu panel của bàn

        TableInfo(String name, int capacity, TableStatus status) {
            this.name = name;
            this.capacity = capacity;
            this.status = status;
        }
    }

}