package view;

import javax.swing.*;
import java.awt.*;

public class Ban_View extends JPanel {
    public Ban_View() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Quản lý Bàn", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.CENTER);
    }
}
