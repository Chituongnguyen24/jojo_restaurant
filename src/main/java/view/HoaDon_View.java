package view;

import javax.swing.*;
import java.awt.*;

public class HoaDon_View extends JPanel {
    public HoaDon_View() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Hóa đơn", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.CENTER);
    }
}
