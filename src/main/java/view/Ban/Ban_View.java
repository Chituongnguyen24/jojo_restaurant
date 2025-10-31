package view.Ban;

import dao.Ban_DAO;
import dao.KhuVuc_DAO; // THÊM
import entity.Ban;
import entity.KhuVuc; // THÊM
import enums.LoaiBan;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class Ban_View extends JPanel {

    private Ban_DAO banDAO;
    private KhuVuc_DAO khuVucDAO; // THÊM
    private Map<String, List<Ban>> danhSachBanTheoKhuVuc;
    private Map<String, Integer> soLuongBanTheoKhuVuc;
    private List<String> tenKhuVuc;
    private String khuVucHienTai;

    private JPanel pnlLuoiBan;
    private JLabel lblTieuDeSoDo;
    private Map<String, JButton> cacNutChonKhuVuc;
    private JPanel pnlChuaNutKhuVuc;
    private JPanel pnlSidebarTrai;

    private Map<String, String> areaImagePaths = new LinkedHashMap<String, String>() {{
        put("Sân thượng", "images/icon/bannho.png");
        put("Sân vườn", "images/icon/thongthuong.png");
        put("Tầng 2", "images/icon/bannho.png");
        put("Tầng trệt", "images/icon/thongthuong.png");
        put("Phòng VIP", "images/icon/vip.png");
    }};
    private static final String DEFAULT_TABLE_IMAGE = "images/icon/thongthuong.png";
    private Map<String, ImageIcon> areaIcons;

    private static final Color MAU_NEN = new Color(254, 252, 247);
    private static final Color MAU_TRANG = Color.WHITE;
    private static final Color MAU_CHU_CHINH = new Color(52, 58, 64);
    private static final Color MAU_CHU_PHU = new Color(108, 117, 125);
    private static final Color MAU_VIEN = new Color(233, 236, 239);
    
    private static final Color MAU_CAM_CHINH = new Color(242, 118, 29);
    private static final Color MAU_CAM_DAM = new Color(242, 118, 29).darker();
    private static final Color MAU_SIDEBAR_KHONGCHON_NEN = new Color(248, 249, 250);
    private static final Color MAU_SIDEBAR_KHONGCHON_NHAN_NEN = new Color(222, 226, 230);

    private static final Color MAU_NUT_THEM = new Color(10, 102, 255);
    private static final Color MAU_NUT_THEM_HOVER = new Color(0, 84, 215);
    private static final Color MAU_VIEN_SANG = new Color(233, 236, 239);

    private static final Font FONT_TIEUDE_LON = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font FONT_TIEUDE_PHU = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_TIEUDE_NHO = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_NUT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_THE_TIEUDE = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_THE_CHU = new Font("Segoe UI", Font.PLAIN, 12);

    public Ban_View() {
        banDAO = new Ban_DAO();
        khuVucDAO = new KhuVuc_DAO(); // KHỞI TẠO DAO KHU VỰC
        danhSachBanTheoKhuVuc = new LinkedHashMap<>();
        soLuongBanTheoKhuVuc = new LinkedHashMap<>();
        tenKhuVuc = new ArrayList<>();
        cacNutChonKhuVuc = new LinkedHashMap<>();
        areaIcons = new LinkedHashMap<>();

        taiTatCaIconKhuVuc();
        thietLapGiaoDien();
        taiLaiDuLieuVaLamMoiUI();
    }

    private void taiLaiDuLieuVaLamMoiUI() { 
        taiDuLieuTuDB(); 
        capNhatNoiDungSidebar();
        capNhatLuaChonSidebar();
        capNhatHienThiLuoiBan();
        capNhatTieuDeSoDo();
    }
    
    private void taiTatCaIconKhuVuc() { 
        for (Map.Entry<String, String> entry : areaImagePaths.entrySet()) { 
            ImageIcon icon = taiImageIcon(entry.getValue()); 
            if (icon != null) { 
                areaIcons.put(entry.getKey(), icon); 
            } 	
        } 
        ImageIcon defaultIcon = taiImageIcon(DEFAULT_TABLE_IMAGE); 
        if (defaultIcon != null) { 
            areaIcons.put("DEFAULT", defaultIcon);
        } 
    }
    
    private ImageIcon taiImageIcon(String imagePath) { 
        try { 
            ImageIcon o = new ImageIcon(imagePath); 
            if (o.getIconWidth() <= 0) return null; 
            Image s = o.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH); 
            return new ImageIcon(s); 
        } catch (Exception e) { 
            return null; 
        } 
    }
    
    private ImageIcon layIconChoKhuVucHienTai() {
        ImageIcon icon = areaIcons.get(khuVucHienTai);
        return icon != null ? icon : areaIcons.get("DEFAULT");
    }

    private void taiDuLieuTuDB() {
        // Lấy danh sách KHUVUC (maKV -> tenKV)
        Map<String, String> kvMap = banDAO.getDanhSachKhuVuc();
        
        // Lấy toàn bộ danh sách bàn
        List<Ban> tatCaBan = banDAO.getAllBan();
        
        // 1. Phân loại bàn theo Tên Khu vực (tenKV)
        danhSachBanTheoKhuVuc.clear();
        for (Ban ban : tatCaBan) {
             String maKV = ban.getKhuVuc().getMaKhuVuc();
             String tenKV = kvMap.getOrDefault(maKV, "Không rõ");
             
             danhSachBanTheoKhuVuc
                .computeIfAbsent(tenKV, k -> new ArrayList<>())
                .add(ban);
        }
        
        // 2. Cập nhật tên Khu vực và số lượng bàn
        soLuongBanTheoKhuVuc = danhSachBanTheoKhuVuc.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey, 
                e -> e.getValue().size(),
                (oldValue, newValue) -> newValue,
                LinkedHashMap::new
            ));
            
        tenKhuVuc = new ArrayList<>(soLuongBanTheoKhuVuc.keySet());

        // 3. Cập nhật khu vực hiện tại
        if (khuVucHienTai == null && !tenKhuVuc.isEmpty()) {
            khuVucHienTai = tenKhuVuc.get(0);
        } else if (tenKhuVuc.isEmpty()) {
            khuVucHienTai = "Không có dữ liệu";
        }
    }

    private void thietLapGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(MAU_NEN);
        JPanel mainPanel = new JPanel(new BorderLayout(25, 25));
        mainPanel.setBackground(MAU_NEN);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));
        mainPanel.add(taoPanelTieuDe(), BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(new BorderLayout(25, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(taoSidebarChonKhuVuc(), BorderLayout.WEST);
        contentPanel.add(taoPanelPhai(), BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel taoPanelTieuDe() {
        TheBoTron panel = new TheBoTron(new BorderLayout(), 20);
        panel.setBackground(MAU_TRANG);
        panel.setBorder(BorderFactory.createCompoundBorder(new VienDoBong(), BorderFactory.createEmptyBorder(30, 35, 30, 35)));
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Quản lý bàn");
        titleLabel.setFont(FONT_TIEUDE_LON);
        titleLabel.setForeground(MAU_CHU_CHINH);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitleLabel = new JLabel("Thêm, sửa thông tin hoặc xóa bàn");
        subtitleLabel.setFont(FONT_TIEUDE_PHU);
        subtitleLabel.setForeground(MAU_CHU_PHU);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);
        panel.add(titlePanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel taoSidebarChonKhuVuc() {
        TheBoTron sidebar = new TheBoTron(null, 20);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(MAU_TRANG);
        sidebar.setBorder(BorderFactory.createCompoundBorder(new VienDoBong(), BorderFactory.createEmptyBorder(30, 25, 30, 25)));
        sidebar.setPreferredSize(new Dimension(300, 0));
        JLabel areaLabel = new JLabel("Khu vực");
        areaLabel.setFont(FONT_TIEUDE_NHO);
        areaLabel.setForeground(MAU_CHU_CHINH);
        areaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel areaSubLabel = new JLabel("Chọn khu vực để xem sơ đồ bàn");
        areaSubLabel.setFont(FONT_CHU);
        areaSubLabel.setForeground(MAU_CHU_PHU);
        areaSubLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(areaLabel);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(areaSubLabel);
        sidebar.add(Box.createVerticalStrut(25));
        pnlChuaNutKhuVuc = new JPanel();
        pnlChuaNutKhuVuc.setLayout(new BoxLayout(pnlChuaNutKhuVuc, BoxLayout.Y_AXIS));
        pnlChuaNutKhuVuc.setOpaque(false);
        sidebar.add(pnlChuaNutKhuVuc);
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private void capNhatNoiDungSidebar() {
        pnlChuaNutKhuVuc.removeAll();
        cacNutChonKhuVuc.clear();
        for (String tenKV : tenKhuVuc) {
            int count = soLuongBanTheoKhuVuc.getOrDefault(tenKV, 0);
            JButton nutKV = taoNutChonKhuVuc(tenKV, count + " bàn");
            nutKV.addActionListener(e -> chuyenKhuVuc(tenKV));
            cacNutChonKhuVuc.put(tenKV, nutKV);
            pnlChuaNutKhuVuc.add(nutKV);
            pnlChuaNutKhuVuc.add(Box.createVerticalStrut(12));
        }
        pnlChuaNutKhuVuc.revalidate();
        pnlChuaNutKhuVuc.repaint();
    }

    private void chuyenKhuVuc(String tenKV) {
        khuVucHienTai = tenKV;
        capNhatLuaChonSidebar();
        capNhatHienThiLuoiBan();
        capNhatTieuDeSoDo();
    }

    private void capNhatLuaChonSidebar() {
        for (Map.Entry<String, JButton> entry : cacNutChonKhuVuc.entrySet()) {
            dinhDangNutKhuVuc(entry.getValue(), entry.getKey().equals(khuVucHienTai));
        }
    }

    private JButton taoNutChonKhuVuc(String tenKV, String soLuong) {
        NutBoTron button = new NutBoTron("", 15);
        button.setLayout(new BorderLayout(15, 0));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        button.setPreferredSize(new Dimension(250, 56));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel nameLabel = new JLabel(tenKV);
        nameLabel.setFont(FONT_NUT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JLabel countLabel = new JLabel(soLuong);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        countLabel.setOpaque(false);
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
        dinhDangNutKhuVuc(button, false);
        return button;
    }

    private void dinhDangNutKhuVuc(JButton button, boolean selected) {
        if (button.getComponentCount() < 2) return;
        JLabel nameLabel = (JLabel) button.getComponent(0);
        JPanel countWrapper = (JPanel) button.getComponent(1);
        JLabel countLabel = (JLabel) countWrapper.getComponent(0);
        if (selected) {
            button.setBackground(MAU_CAM_CHINH);
            nameLabel.setForeground(Color.WHITE);
            countLabel.setForeground(Color.WHITE);
            countWrapper.setBackground(MAU_CAM_DAM);
            button.setBorder(BorderFactory.createLineBorder(MAU_CAM_CHINH, 1));
        } else {
            button.setBackground(MAU_SIDEBAR_KHONGCHON_NEN);
            nameLabel.setForeground(MAU_CHU_CHINH);
            countLabel.setForeground(MAU_CHU_CHINH);
            countWrapper.setBackground(MAU_SIDEBAR_KHONGCHON_NHAN_NEN);
            button.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1));
        }
    }

    private JPanel taoPanelPhai() {
        JPanel panel = new JPanel(new BorderLayout(0, 25));
        panel.setOpaque(false);

        TheBoTron headerPanel = new TheBoTron(new BorderLayout(15, 0), 20);
        headerPanel.setBackground(MAU_TRANG);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(new VienDoBong(), BorderFactory.createEmptyBorder(25, 30, 25, 30)));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        lblTieuDeSoDo = new JLabel("Sơ đồ bàn");
        lblTieuDeSoDo.setFont(FONT_TIEUDE_NHO);
        lblTieuDeSoDo.setForeground(MAU_CHU_CHINH);
        lblTieuDeSoDo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel mapSubtitle = new JLabel("Nhấn vào bàn để chỉnh sửa thông tin hoặc xóa."); 
        mapSubtitle.setFont(FONT_CHU);
        mapSubtitle.setForeground(MAU_CHU_PHU);
        mapSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        titlePanel.add(lblTieuDeSoDo);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(mapSubtitle);

        NutBoTron btnThemBan = new NutBoTron("+ Thêm Bàn Mới", 12);
        btnThemBan.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnThemBan.setBackground(MAU_NUT_THEM);
        btnThemBan.setForeground(MAU_TRANG);
        btnThemBan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThemBan.setPreferredSize(new Dimension(180, 45));
        btnThemBan.addActionListener(e -> moDialogThemBan());
        btnThemBan.addMouseListener(new MouseAdapter() {
            @Override 
            public void mouseEntered(MouseEvent e) { 
                btnThemBan.setBackground(MAU_NUT_THEM_HOVER);
            }
            @Override 
            public void mouseExited(MouseEvent e) { 
                btnThemBan.setBackground(MAU_NUT_THEM);
            }
        });

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(btnThemBan);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(buttonWrapper, BorderLayout.EAST);

        pnlLuoiBan = new JPanel(new GridLayout(0, 4, 20, 20)); // SỬA: pnlLuoiBan nên là FlowLayout để tự gói gọn
        pnlLuoiBan.setOpaque(false);

        TheBoTron tablesContainer = new TheBoTron(new BorderLayout(), 20);
        tablesContainer.setBackground(MAU_TRANG);
        tablesContainer.setBorder(BorderFactory.createCompoundBorder(new VienDoBong(), BorderFactory.createEmptyBorder(30, 30, 30, 30)));
        tablesContainer.add(pnlLuoiBan, BorderLayout.NORTH);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tablesContainer, BorderLayout.CENTER);
        return panel;
    }

    private void moDialogThemBan() {
        ThemBan_Dialog dialog = new ThemBan_Dialog((JFrame) SwingUtilities.getWindowAncestor(this), this::taiLaiDuLieuVaLamMoiUI);
        dialog.setVisible(true);
    }

    private void moDialogChinhSua(Ban ban) {
        ChinhSuaBan_Dialog dialog = new ChinhSuaBan_Dialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            ban,
            this::taiLaiDuLieuVaLamMoiUI
        );
        dialog.setVisible(true);
    }

    private void capNhatTieuDeSoDo() {
        if (lblTieuDeSoDo != null) { 
            lblTieuDeSoDo.setText("Sơ đồ bàn - " + khuVucHienTai); 
        }
    }

    private void capNhatHienThiLuoiBan() {
        pnlLuoiBan.removeAll(); 
        
        // SỬA: Đảm bảo layout là GridLayout hoặc FlowLayout
        pnlLuoiBan.setLayout(new GridLayout(0, 4, 20, 20));
        
        List<Ban> tables = danhSachBanTheoKhuVuc.get(khuVucHienTai);
        if (tables != null && !tables.isEmpty()) {
            for (Ban b : tables) { 
                pnlLuoiBan.add(taoTheBan(b)); 
            }
        } else {
            JLabel nTL = new JLabel("Không có bàn nào trong khu vực này.");
            nTL.setFont(FONT_CHU); 
            nTL.setForeground(MAU_CHU_PHU); 
            nTL.setHorizontalAlignment(SwingConstants.CENTER);
            pnlLuoiBan.setLayout(new FlowLayout()); 
            pnlLuoiBan.add(nTL);
        }
        pnlLuoiBan.revalidate(); 
        pnlLuoiBan.repaint();
    }


    private JPanel taoTheBan(Ban ban) {
        TheBoTron card = new TheBoTron(null, 18);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MAU_TRANG);

        card.setBorder(BorderFactory.createEmptyBorder(20, 18, 20, 18));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon currentIcon = layIconChoKhuVucHienTai();
        JLabel lblIcon = new JLabel(currentIcon);
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        // SỬA: Lấy tên hiển thị từ Enum LoaiBan dựa trên String raw DB
        String loaiBanRaw = ban.getLoaiBan();
        String loaiBanTen;
        try {
            loaiBanTen = LoaiBan.fromString(loaiBanRaw).getTenHienThi();
        } catch (IllegalArgumentException e) {
            loaiBanTen = loaiBanRaw;
        }

        if (loaiBanTen.equalsIgnoreCase("Bàn VIP")) {
            loaiBanTen = "VIP";
        }
        JLabel lblName = new JLabel(String.format("%s (%s)", ban.getMaBan().trim(), loaiBanTen));
        lblName.setFont(FONT_THE_TIEUDE);
        lblName.setForeground(MAU_CHU_CHINH);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCapacity = new JLabel(ban.getSoCho() + " chỗ");
        lblCapacity.setFont(FONT_THE_CHU);
        lblCapacity.setForeground(MAU_CHU_PHU);
        lblCapacity.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblIcon);
        card.add(Box.createVerticalStrut(12));
        card.add(lblName);
        card.add(Box.createVerticalStrut(6));
        card.add(lblCapacity);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    moDialogChinhSua(ban);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 251, 252));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(MAU_TRANG);
            }
        });

        return card;
    }

    private class TheBoTron extends JPanel {
        private int doBoGoc; 
        private Color mauVien;
        
        public TheBoTron(LayoutManager layout, int doBoGoc) { 
            super(layout); 
            this.doBoGoc = doBoGoc; 
            setOpaque(false); 
            this.mauVien = MAU_VIEN; 
        }
        
        public void datMauVien(Color color) {
            this.mauVien = color; 
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, doBoGoc, doBoGoc));
            g2.dispose();
            super.paintComponent(g);
        }
        
        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.mauVien);
            g2.setStroke(new BasicStroke(2));
            g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, doBoGoc, doBoGoc));
            g2.dispose();
        }
    }
    
    private class VienDoBong implements Border { 
        private int shadowSize = 4;
        
        @Override 
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < shadowSize; i++) {
                int alpha = (int) (15 - (i * 3.5));
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
    
    private class NutBoTron extends JButton {
        private int doBoGoc;
        
        public NutBoTron(String text, int doBoGoc) {
            super(text);
            this.doBoGoc = doBoGoc;
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
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), doBoGoc, doBoGoc));
            super.paintComponent(g);
            g2.dispose(); 
        }
    }
}