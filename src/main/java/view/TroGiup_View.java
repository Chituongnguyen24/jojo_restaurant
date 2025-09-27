package view;

import javax.swing.*;
import java.awt.*;

public class TroGiup_View extends JPanel {
    public TroGiup_View() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Trợ giúp", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.CENTER);
    }
}
