package view.ThucDon;

import javax.swing.*;
import dao.MonAn_DAO;
import entity.MonAn;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.Image;

import java.awt.*;
import java.util.List;

public class ThucDon_View extends JPanel {
    private JPanel panelDanhSach;
    private MonAn_DAO monAnDAO = new MonAn_DAO();
    
    private static final Color COLOR_BUTTON_ADD = new Color(28, 132, 221);
    private static final Color COLOR_WHITE = Color.WHITE;

    public ThucDon_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(251, 248, 241));

        JPanel panelHeader = new JPanel(new BorderLayout(20, 0));
        panelHeader.setBackground(new Color(251, 248, 241));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("Quản lý thực đơn");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(60, 60, 60));
        panelHeader.add(lblTitle, BorderLayout.WEST);
        
        RoundedButton btnThemMon = new RoundedButton("Thêm Món Mới");
        btnThemMon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThemMon.setBackground(COLOR_BUTTON_ADD);
        btnThemMon.setForeground(COLOR_WHITE);
        btnThemMon.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThemMon.addActionListener(e -> moDialogThemMon());

        JPanel panelNut = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelNut.setOpaque(false);
        panelNut.add(btnThemMon);
        panelHeader.add(panelNut, BorderLayout.EAST); 
        
        add(panelHeader, BorderLayout.NORTH);

        //danh sách món ăn
        panelDanhSach = new JPanel(new GridLayout(0, 4, 15, 15));
        panelDanhSach.setBackground(new Color(251, 248, 241));
        panelDanhSach.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scroll = new JScrollPane(panelDanhSach);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        loadMonAn();
    }
    
    //dialog thêm món
    private void moDialogThemMon() {
    	Runnable refreshAction = () -> loadMonAn();
        ThemMonAn_Dialog dialog = new ThemMonAn_Dialog(
            (JFrame) SwingUtilities.getWindowAncestor(this),
            refreshAction
        );
        dialog.setVisible(true);
    }


    //tải danh sách món ăn từ DB
    public void loadMonAn() {
        panelDanhSach.removeAll();
        List<MonAn> list = monAnDAO.getAllMonAn();

        for (MonAn mon : list) {
            JPanel card = createMonAnCard(mon);
            panelDanhSach.add(card);
        }

        panelDanhSach.revalidate();
        panelDanhSach.repaint();
    }

    //tạo thẻ món ăn
    private JPanel createMonAnCard(MonAn mon) {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(15, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorderColor(new Color(220, 220, 220));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 15));
        panel.setPreferredSize(new Dimension(320, 110)); 
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //tải ảnh
        ImageIcon icon = loadScaledImage(mon.getImagePath(), 90, 90);
        JLabel lblImage = new JLabel(icon);
        lblImage.setPreferredSize(new Dimension(90, 90));
        lblImage.setOpaque(false);
        panel.add(lblImage, BorderLayout.WEST);

        //panel thông tin
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false); 

        JLabel lblTen = new JLabel(mon.getTenMonAn());
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTen.setForeground(new Color(60, 60, 60));

        JLabel lblGia = new JLabel(String.format("%,.0f₫", mon.getDonGia()));
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGia.setForeground(new Color(220, 0, 0));

        // THÊM: Hiển thị Loại món ăn
        JLabel lblLoaiMon = new JLabel("Loại: " + (mon.getLoaiMonAn() != null ? mon.getLoaiMonAn() : "-"));
        lblLoaiMon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLoaiMon.setForeground(new Color(100, 100, 100));

        JLabel lblTrangThai = new JLabel(mon.isTrangThai() ? "Có sẵn" : "Hết món");
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTrangThai.setForeground(mon.isTrangThai() ? new Color(0, 128, 0) : Color.RED);
        
        infoPanel.add(lblTen);
        infoPanel.add(Box.createVerticalGlue()); 
        infoPanel.add(lblGia);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblLoaiMon); // HIỂN THỊ LOẠI MÓN
        infoPanel.add(lblTrangThai);

        panel.add(infoPanel, BorderLayout.CENTER);

        //hover và click
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                //làm mới
                Runnable refreshAction = () -> loadMonAn();
                //gọi dialog với Runnable
                new ChinhSuaMonAn_Dialog(
                    (JFrame) SwingUtilities.getWindowAncestor(ThucDon_View.this),
                    mon,
                    refreshAction
                ).setVisible(true);
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(245, 245, 245));
                panel.setBorderColor(new Color(100, 150, 255));
                panel.repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(Color.WHITE);
                panel.setBorderColor(new Color(220, 220, 220)); 
                panel.repaint();
            }
        });

        return panel;
    }

    private ImageIcon loadScaledImage(String path, int width, int height) {
        ImageIcon icon = null;
        if (path != null && !path.isEmpty()) {
            icon = new ImageIcon(path); 
        }

        if (icon == null || icon.getIconWidth() == -1) {
            icon = new ImageIcon("images/mon an/placeholder.png"); 
        }

        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    
    // Test riêng
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JFrame frame = new JFrame("Quản lý thực đơn");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ThucDon_View());
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
 
    private class RoundedPanel extends JPanel {
        private int cornerRadius = 25;
        private Color borderColor; 

        public RoundedPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
            this.borderColor = Color.GRAY; 
        }
        
        public void setBorderColor(Color color) {
            this.borderColor = color;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius));
            g2.dispose();
            super.paintComponent(g); 
        }
        
        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.borderColor); 
            g2.setStroke(new BasicStroke(1)); 
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius));
            g2.dispose();
        }
    }
    

    private class RoundedButton extends JButton {
        private int cornerRadius = 20;

        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false); 
            setFocusPainted(false); 
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isPressed()) {
                g2.setColor(getBackground().darker());
            } else if (getModel().isRollover()) {
                g2.setColor(getBackground().brighter());
            } else {
                g2.setColor(getBackground());
            }
            
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            
            super.paintComponent(g);
            g2.dispose();
        }
    }
}