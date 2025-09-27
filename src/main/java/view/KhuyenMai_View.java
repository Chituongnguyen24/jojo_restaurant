package view;

import javax.swing.*;
import java.awt.*;

public class KhuyenMai_View extends JPanel {
    public KhuyenMai_View() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Khuyến mãi", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.CENTER);
    }
}
