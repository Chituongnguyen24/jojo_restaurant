package view;

import javax.swing.*;
import java.awt.*;

public class ThongKe_View extends JPanel {
    public ThongKe_View() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Thống kê", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.CENTER);
    }
}
