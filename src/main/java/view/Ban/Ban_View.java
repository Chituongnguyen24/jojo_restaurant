package view.Ban;

import dao.Ban_DAO;
import entity.Ban;
import enums.TrangThaiBan;

// Import thêm cho Graphics
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
// Import khác
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class Ban_View extends JPanel {

    // ... (Giữ nguyên các biến thành viên) ...
    private Ban_DAO banDAO;
    private Map<String, List<Ban>> tablesByArea; 
    private Map<String, Integer> areaTableCounts; 
    private List<String> areaNames;
    private String currentArea;
    private JPanel tablesPanel;
    private JLabel mapTitle;
    private Map<String, JButton> floorButtons;
    private JPanel floorButtonContainer; 
    private JPanel leftSidebar;
    
    // ... (Giữ nguyên các hằng số Màu sắc và Font) ...
    private static final Color COLOR_BACKGROUND = new Color(245, 245, 240);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_TEXT_PRIMARY = new Color(60, 60, 60);
    private static final Color COLOR_TEXT_SECONDARY = new Color(120, 120, 120);
    private static final Color COLOR_BORDER = new Color(230, 230, 230);
    private static final Color COLOR_PRIMARY = new Color(255, 152, 0);
    private static final Color COLOR_PRIMARY_DARK = new Color(220, 120, 0);
    private static final Color COLOR_BADGE_DEFAULT = new Color(76, 175, 80);
    private static final Color COLOR_BUTTON_ADD = new Color(28, 132, 221);
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);


    public Ban_View() {
        // ... (Giữ nguyên toàn bộ constructor) ...
        banDAO = new Ban_DAO();
        tablesByArea = new LinkedHashMap<>();
        areaTableCounts = new LinkedHashMap<>();
        areaNames = new ArrayList<>();
        floorButtons = new LinkedHashMap<>();
        
        DichVuLapLichDatBan.getInstance().start(banDAO);
        
        setLayout(new BorderLayout());
        setBackground(COLOR_BACKGROUND);
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(COLOR_BACKGROUND);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(new BorderLayout(20, 0));
        contentPanel.setOpaque(false);
        leftSidebar = createLeftSidebar();
        contentPanel.add(leftSidebar, BorderLayout.WEST);
        JPanel rightPanel = createRightPanel();
        contentPanel.add(rightPanel, BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        reloadDataAndRefreshUI();
    }
    
    // ... (Giữ nguyên các hàm: reloadDataAndRefreshUI, loadDataFromDB, 
    //      createHeaderPanel, createLeftSidebar, updateSidebarContent, 
    //      switchArea, updateSidebarSelection, createFloorButton, styleFloorButton) ...
    // (Các hàm này không thay đổi)
    private void reloadDataAndRefreshUI() {
        loadDataFromDB();
        updateSidebarContent();
        updateSidebarSelection();
        updateTablesDisplay();
        updateHeaderTitle();
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
                List<Ban> tables = banDAO.getBanTheoKhuVuc(maKhuVuc);
                tablesByArea.put(areaName, tables);
            }
        }
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Quản lý bàn");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(COLOR_TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitleLabel = new JLabel("Quản lý trạng thái bàn theo từng khu vực");
        subtitleLabel.setFont(FONT_SUBTITLE);
        subtitleLabel.setForeground(COLOR_TEXT_SECONDARY);
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
        sidebar.setBackground(COLOR_WHITE);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        sidebar.setPreferredSize(new Dimension(280, 0));
        JLabel areaLabel = new JLabel("Khu vực");
        areaLabel.setFont(FONT_HEADER);
        areaLabel.setForeground(COLOR_TEXT_PRIMARY);
        areaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel areaSubLabel = new JLabel("Chọn khu vực để xem sơ đồ bàn");
        areaSubLabel.setFont(FONT_BODY);
        areaSubLabel.setForeground(COLOR_TEXT_SECONDARY);
        areaSubLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(areaLabel);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(areaSubLabel);
        sidebar.add(Box.createVerticalStrut(20));
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
            String countText = String.format("%d bàn", count);
            JButton floorBtn = createFloorButton(areaName, countText);
            floorBtn.addActionListener(e -> switchArea(areaName));
            floorButtons.put(areaName, floorBtn);
            floorButtonContainer.add(floorBtn);
            floorButtonContainer.add(Box.createVerticalStrut(10));
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
            boolean isSelected = entry.getKey().equals(currentArea);
            styleFloorButton(entry.getValue(), isSelected);
        }
    }

    private JButton createFloorButton(String floorName, String count) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 0));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setPreferredSize(new Dimension(240, 45));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel nameLabel = new JLabel(floorName);
        nameLabel.setFont(FONT_BUTTON);
        JLabel countLabel = new JLabel(count);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLabel.setOpaque(true);
        countLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        button.add(nameLabel, BorderLayout.WEST);
        button.add(countLabel, BorderLayout.EAST);
        styleFloorButton(button, false); 
        return button;
    }

    private void styleFloorButton(JButton button, boolean selected) {
        if (button.getComponentCount() < 2) return; 
        JLabel nameLabel = (JLabel) button.getComponent(0);
        JLabel countLabel = (JLabel) button.getComponent(1);
        Border border;
        if (selected) {
            button.setBackground(COLOR_PRIMARY);
            border = BorderFactory.createLineBorder(COLOR_PRIMARY, 1, true);
            nameLabel.setForeground(COLOR_WHITE);
            countLabel.setForeground(COLOR_WHITE);
            countLabel.setBackground(COLOR_PRIMARY_DARK);
        } else {
            button.setBackground(COLOR_WHITE);
            border = BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true);
            nameLabel.setForeground(COLOR_TEXT_PRIMARY);
            countLabel.setForeground(COLOR_WHITE);
            countLabel.setBackground(COLOR_BADGE_DEFAULT);
        }
        button.setBorder(BorderFactory.createCompoundBorder(
            border,
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
    }


    /**
     * === SỬA ĐỔI ===
     * Tạo khung bên phải (sử dụng RoundedButton)
     */
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        JPanel headerPanel = new JPanel(new BorderLayout(10, 0));
        headerPanel.setBackground(COLOR_WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        mapTitle = new JLabel("📍 Sơ đồ bàn"); 
        mapTitle.setFont(FONT_HEADER);
        mapTitle.setForeground(COLOR_TEXT_PRIMARY);
        mapTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel mapSubtitle = new JLabel("Click vào bàn để thay đổi trạng thái (đặt bàn, nhận khách, trả bàn)");
        mapSubtitle.setFont(FONT_BODY);
        mapSubtitle.setForeground(COLOR_TEXT_SECONDARY);
        mapSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(mapTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(mapSubtitle);

        // === SỬA: Sử dụng RoundedButton ===
        JButton btnThemBan = new RoundedButton("Thêm Bàn Mới");
        btnThemBan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThemBan.setBackground(COLOR_BUTTON_ADD);
        btnThemBan.setForeground(COLOR_WHITE);
        // btnThemBan.setFocusPainted(false); // Đã có trong RoundedButton
        // btnThemBan.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Đã có trong RoundedButton
        btnThemBan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThemBan.addActionListener(e -> moDialogThemBan());
        
        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(btnThemBan);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(buttonWrapper, BorderLayout.EAST);

        // ... (Giữ nguyên phần còn lại của hàm) ...
        tablesPanel = new JPanel(new GridLayout(0, 4, 15, 15)); 
        tablesPanel.setOpaque(false);
        JPanel tablesContainer = new JPanel(new BorderLayout());
        tablesContainer.setBackground(COLOR_WHITE);
        tablesContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        tablesContainer.add(tablesPanel, BorderLayout.NORTH); 
        JPanel legendPanel = createLegendPanel();
        tablesContainer.add(legendPanel, BorderLayout.SOUTH);
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tablesContainer, BorderLayout.CENTER);
        return panel;
    }
    
    // ... (Giữ nguyên: moDialogThemBan, updateHeaderTitle, updateTablesDisplay) ...
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


    /**
     * === SỬA ĐỔI ===
     * Tạo một ô (Card) đại diện cho 1 bàn (sử dụng RoundedPanel)
     */
    private JPanel createTableCard(Ban ban) {
        // === SỬA LỖI: Tách làm 2 dòng ===
        // 1. Khởi tạo RoundedPanel, tạm thời chưa có layout (truyền null)
        RoundedPanel card = new RoundedPanel(null);
        
        // 2. Đặt layout SAU KHI 'card' đã tồn tại
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        // ===================================
        
        card.setBackground(ban.getTrangThai().getColor());
        
        // Đặt màu viền cho RoundedPanel
        card.setBorderColor(ban.getTrangThai().getColor().darker());
        
        // Giữ lại border này, nó sẽ hoạt động như padding
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25)); 
        
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        String loaiBanTen = ban.getLoaiBan().getTenHienThi();
        if (loaiBanTen.equalsIgnoreCase("Bàn VIP")) {
            loaiBanTen = "VIP";
        }

        JLabel lblName = new JLabel(String.format("%s (%s)", ban.getMaBan(), loaiBanTen));
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setForeground(COLOR_WHITE);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblName.setOpaque(false); // làm trong suốt JLabel

        JLabel lblCapacity = new JLabel(ban.getSoCho() + " chỗ");
        lblCapacity.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblCapacity.setForeground(new Color(240, 240, 240));
        lblCapacity.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblCapacity.setOpaque(false); // làm trong suốt JLabel

        card.add(lblName);
        card.add(Box.createVerticalStrut(5));
        card.add(lblCapacity);

        // === Xử lý click (Giữ nguyên) ===
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
                    int confirm = JOptionPane.showConfirmDialog(
                        card, 
                        "Khách của bàn " + ban.getMaBan() + " đã đến?", 
                        "Xác nhận khách đến", 
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        updateBanStatus(card, ban, TrangThaiBan.CO_KHACH, null);
                    }
                } else if (currentStatus == TrangThaiBan.CO_KHACH) {
                    int confirm = JOptionPane.showConfirmDialog(
                        card, 
                        "Khách đã rời bàn" + ban.getMaBan() + "?", 
                        "Trả bàn", 
                        JOptionPane.YES_NO_OPTION
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        String message = "Bàn " + ban.getMaBan() + "đã được trả!";
                        updateBanStatus(card, ban, TrangThaiBan.TRONG, message);
                    }
                }
            }
        });

        return card;
    }

    /**
     * === SỬA ĐỔI ===
     * Hàm trợ giúp để cập nhật trạng thái bàn (cho RoundedPanel)
     */
    private void updateBanStatus(JPanel card, Ban ban, TrangThaiBan newStatus, String successMessage) {
        TrangThaiBan oldStatus = ban.getTrangThai();
        
        if (oldStatus == TrangThaiBan.DA_DAT && newStatus == TrangThaiBan.CO_KHACH) {
            DichVuLapLichDatBan.getInstance().huyLichHen(ban.getMaBan());
        }
        
        ban.setTrangThai(newStatus);
        boolean success = banDAO.capNhatBan(ban);

        if (success) {
            // Cập nhật UI
            card.setBackground(ban.getTrangThai().getColor());
            
            // === SỬA: Cập nhật màu viền của RoundedPanel ===
            if (card instanceof RoundedPanel) {
                ((RoundedPanel) card).setBorderColor(ban.getTrangThai().getColor().darker());
            }
            
            if (successMessage != null && !successMessage.isEmpty()) {
                JOptionPane.showMessageDialog(card, successMessage);
            }
        } else {
            ban.setTrangThai(oldStatus); // Rollback
            JOptionPane.showMessageDialog(card, "Lỗi! Không thể cập nhật trạng thái bàn.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ... (Giữ nguyên các hàm: createLegendPanel, createLegendItem) ...
    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDER));
        panel.add(createLegendItem(TrangThaiBan.TRONG.toString(), TrangThaiBan.TRONG.getColor()));
        panel.add(createLegendItem(TrangThaiBan.CO_KHACH.toString(), TrangThaiBan.CO_KHACH.getColor()));
        panel.add(createLegendItem(TrangThaiBan.DA_DAT.toString(), TrangThaiBan.DA_DAT.getColor()));
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
        label.setFont(FONT_BODY);
        label.setForeground(new Color(100, 100, 100));
        item.add(colorBox);
        item.add(label);
        return item;
    }
    
    
    // =================================================================
    // === CÁC LỚP NỘI BỘ MỚI ĐỂ BO TRÒN GÓC ===
    // =================================================================

    /**
     * Lớp nội bộ cho Panel bo tròn (Dùng cho các Bàn)
     */
    private class RoundedPanel extends JPanel {
        private int cornerRadius = 25; // Độ bo góc
        private Color borderColor; // Màu viền

        public RoundedPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false); // Quan trọng: Panel phải trong suốt
            this.borderColor = getBackground().darker(); // Màu viền mặc định
        }
        
        // Hàm để cập nhật màu viền từ bên ngoài
        public void setBorderColor(Color color) {
            this.borderColor = color;
            repaint(); // Yêu cầu vẽ lại
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Vẽ NỀN bo tròn
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius));
            
            g2.dispose();
            
            // Vẽ các component con (JLabel) LÊN TRÊN nền
            super.paintComponent(g); 
        }
        
        @Override
        protected void paintBorder(Graphics g) {
            // Vẽ VIỀN bo tròn
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.borderColor); // Sử dụng màu viền đã lưu
            g2.setStroke(new BasicStroke(1));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius));
            g2.dispose();
        }
    }

    /**
     * Lớp nội bộ cho Nút bo tròn (Dùng cho nút "Thêm Bàn Mới")
     */
    private class RoundedButton extends JButton {
        private int cornerRadius = 20; // Độ bo góc

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false); // Không vẽ nền mặc định
            setFocusPainted(false); // Không vẽ viền khi focus
            
            // Chúng ta giữ lại Border, nhưng dùng nó làm PADDING
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Chọn màu nền dựa trên trạng thái (hover, pressed)
            if (getModel().isPressed()) {
                g2.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g2.setColor(getBackground().brighter());
            } else {
                g2.setColor(getBackground());
            }
            
            // Vẽ NỀN bo tròn
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            
            // Vẽ TEXT của nút lên trên
            super.paintComponent(g);
            g2.dispose();
        }
    }
    
} 