package view;

import javax.swing.*;
import java.awt.*;

public class TrangChu_View extends JPanel {
    private JPanel contentPanel;

    public TrangChu_View() {
        setLayout(new BorderLayout());

        //Thanh menu trên cùng 
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 245, 245));

        // Logo
        JLabel logoLabel = new JLabel("🍽️ JoJo Restaurant ");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        menuBar.add(logoLabel);

        // Các menu item
        String[] menus = {
                "Hệ thống", "Bàn", "Thực đơn", "Hóa đơn",
                "Khách hàng", "Khuyến mãi", "Nhân viên",
                "Thống kê", "Trợ giúp"
        };

        for (String m : menus) {
            JMenu menu = new JMenu(m);
            menuBar.add(menu);
        }

        // Thêm menu bar vào panel
        this.add(menuBar, BorderLayout.NORTH);

        //Khu vực nội dung
        contentPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Chào mừng đến Nhà hàng JoJo!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);

        this.add(contentPanel, BorderLayout.CENTER);
    }
}
