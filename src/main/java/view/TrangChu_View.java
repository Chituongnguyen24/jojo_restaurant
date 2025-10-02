package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TrangChu_View extends JPanel {
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public TrangChu_View() {
        setLayout(new BorderLayout());

        // ===== Banner trên cùng =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // ==== Logo bên trái (ảnh + text) ====
        ImageIcon logoIcon = new ImageIcon("images/banner1.png"); // ảnh trong folder images
        // Resize ảnh
        Image img = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(img);

        JLabel logoLabel = new JLabel(" Nhà hàng JOJO - Hệ thống quản lý nội bộ", logoIcon, JLabel.LEFT);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        logoLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(logoLabel, BorderLayout.WEST);

        // ==== User info bên phải
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Xin chào, Nguyễn Văn A (Quản lý)");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        userPanel.add(userLabel);

        JButton logoutBtn = new JButton("Đăng xuất");
        logoutBtn.setBackground(new Color(200, 50, 50));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        userPanel.add(logoutBtn);

        headerPanel.add(userPanel, BorderLayout.EAST);

        // ===== Thanh menu ngang dưới banner =====
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(230, 230, 230));

        String[] menus = {
         "Hệ Thống","Bàn", "Thực đơn", "Hóa đơn", "Khách hàng", 
           "Nhân viên", "Trợ giúp"
        };

        // Content Panel (CardLayout)
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Thêm các view vào CardLayout
        contentPanel.add(new HeThong_View(), "Hệ Thống");
        contentPanel.add(new Ban_View(), "Bàn");
        contentPanel.add(new ThucDon_View(), "Thực đơn");
        contentPanel.add(new HoaDon_View(), "Hóa đơn");
        contentPanel.add(new KhachHang_View(), "Khách hàng");
        contentPanel.add(new NhanVien_View(), "Nhân viên");
        contentPanel.add(new TroGiup_View(), "Trợ giúp");

        // Tạo các menu
        for (String m : menus) {
            JMenu menu = new JMenu(m);

            // Xử lý khi click vào menu
            menu.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    cardLayout.show(contentPanel, m);
                }
            });

            menuBar.add(menu);
        }

        // ===== Thêm vào layout chính =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(menuBar, BorderLayout.SOUTH);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(contentPanel, BorderLayout.CENTER);

        // Mặc định hiển thị "Trang chủ"
        cardLayout.show(contentPanel, "Hệ Thống");

        // Sự kiện logout
        logoutBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Bạn đã đăng xuất!");
            // TODO: gọi màn hình Login ở đây
        });
    }
}
