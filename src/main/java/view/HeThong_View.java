package view;

import javax.swing.*;
import java.awt.*;

public class HeThong_View extends JPanel {
    public HeThong_View() {
        setLayout(new BorderLayout());
        
        JLabel label = new JLabel("Chào mừng đến với Hệ thống quản lý Nhà hàng JOJO!", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        label.setForeground(new Color(60, 60, 60));

        this.add(label, BorderLayout.CENTER);
    }
}
