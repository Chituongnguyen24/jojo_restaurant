package view;

import javax.swing.*;
import java.awt.*;

public class NhanVien_View extends JPanel {
    public NhanVien_View() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Màn hình Nhân viên", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 20));
        add(label, BorderLayout.CENTER);
    }
}
