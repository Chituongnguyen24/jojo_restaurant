package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TrangChu_View extends JPanel {
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public TrangChu_View() {
        setLayout(new BorderLayout());

        // ===== Thanh menu trên cùng =====
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(245, 245, 245));

        // Logo
        JLabel logoLabel = new JLabel("JoJo Restaurant ");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        menuBar.add(logoLabel);

        // ===== Content Panel (CardLayout) =====
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Thêm các view vào CardLayout
        contentPanel.add(new Ban_View(), "Bàn");
        contentPanel.add(new ThucDon_View(), "Thực đơn");
        contentPanel.add(new HoaDon_View(), "Hóa đơn");
        contentPanel.add(new KhachHang_View(), "Khách hàng");
        contentPanel.add(new KhuyenMai_View(), "Khuyến mãi");
        contentPanel.add(new NhanVien_View(), "Nhân viên");
        contentPanel.add(new ThongKe_View(), "Thống kê");
        contentPanel.add(new TroGiup_View(), "Trợ giúp");

        // Menu items
        String[] menus = {"Bàn", "Thực đơn", "Hóa đơn","Khách hàng", "Khuyến mãi", 
        		"Nhân viên", "Thống kê", "Trợ giúp"
        };

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

        // Thêm menu bar vào panel
        this.add(menuBar, BorderLayout.NORTH);

        // Mặc định hiển thị màn hình Bàn
        cardLayout.show(contentPanel, "Bàn");

        this.add(contentPanel, BorderLayout.CENTER);
    }
}
