package view.ThucDon;

import javax.swing.*;
import dao.MonAn_DAO;
import entity.MonAn;
import java.awt.*;
import java.util.List;

public class ThucDon_View extends JPanel {
    private JPanel panelDanhSach;
    private MonAn_DAO monAnDAO = new MonAn_DAO();

    public ThucDon_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(251, 248, 241));

        // Panel header với tiêu đề đơn giản
        JPanel panelHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelHeader.setBackground(new Color(251, 248, 241));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("Quản lý thực đơn");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(60, 60, 60));
        panelHeader.add(lblTitle);
        
        add(panelHeader, BorderLayout.NORTH);

        // Panel danh sách món ăn
        panelDanhSach = new JPanel(new GridLayout(0, 3, 15, 15));
        panelDanhSach.setBackground(new Color(251, 248, 241));
        panelDanhSach.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scroll = new JScrollPane(panelDanhSach);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        add(scroll, BorderLayout.CENTER);

        loadMonAn();
    }

    // Tải danh sách món ăn từ DB
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

    // Tạo từng "thẻ" hiển thị món ăn
    private JPanel createMonAnCard(MonAn mon) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(280, 150));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Panel trên cùng cho tên món
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(Color.WHITE);
        panelTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        
        JLabel lblTen = new JLabel(mon.getTenMonAn());
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTen.setForeground(new Color(60, 60, 60));
        
        panelTop.add(lblTen, BorderLayout.CENTER);
        
        // Panel giữa cho mô tả
        JPanel panelMiddle = new JPanel(new BorderLayout());
        panelMiddle.setBackground(Color.WHITE);
        panelMiddle.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        
        JTextArea txtMoTa = new JTextArea(generateMoTa(mon.getTenMonAn()));
        txtMoTa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtMoTa.setForeground(new Color(80, 80, 80));
        txtMoTa.setLineWrap(true);
        txtMoTa.setWrapStyleWord(true);
        txtMoTa.setEditable(false);
        txtMoTa.setBackground(Color.WHITE);
        txtMoTa.setRows(2);
        
        panelMiddle.add(txtMoTa, BorderLayout.CENTER);
        
        // Panel dưới cùng cho giá và trạng thái
        JPanel panelBottom = new JPanel(new BorderLayout());
        panelBottom.setBackground(Color.WHITE);
        panelBottom.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        
        JLabel lblGia = new JLabel(String.format("%,.0f₫", mon.getDonGia()));
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGia.setForeground(new Color(220, 0, 0));
        
        JLabel lblTrangThai = new JLabel(mon.isTrangThai() ? "Có sẵn" : "Hết món");
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTrangThai.setForeground(mon.isTrangThai() ? new Color(0, 128, 0) : Color.RED);
        
        panelBottom.add(lblGia, BorderLayout.WEST);
        panelBottom.add(lblTrangThai, BorderLayout.EAST);

        panel.add(panelTop, BorderLayout.NORTH);
        panel.add(panelMiddle, BorderLayout.CENTER);
        panel.add(panelBottom, BorderLayout.SOUTH);

        // Hiệu ứng hover và click
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                new ChinhSuaMonAn_Dialog(mon, ThucDon_View.this).setVisible(true);
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(245, 245, 245));
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(100, 150, 255), 2),
                    BorderFactory.createEmptyBorder(14, 14, 14, 14)
                ));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(Color.WHITE);
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));
            }
        });

        return panel;
    }

    // Hàm phụ để tạo mô tả từ tên món (tạm thời)
    private String generateMoTa(String tenMon) {
        return tenMon + " - Món ăn đặc biệt với nguyên liệu tươi ngon, hương vị thơm ngon khó cưỡng.";
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
}