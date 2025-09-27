package view;

import javax.swing.*;
import java.awt.*;

public class ThucDon_View extends JPanel {
    public ThucDon_View() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Quản lý Thực đơn", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.CENTER);
    }
}
