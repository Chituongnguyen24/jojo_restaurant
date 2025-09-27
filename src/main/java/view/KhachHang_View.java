package view;

import javax.swing.*;
import java.awt.*;

public class KhachHang_View extends JPanel {
    public KhachHang_View() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Khách hàng", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.CENTER);
    }
}
