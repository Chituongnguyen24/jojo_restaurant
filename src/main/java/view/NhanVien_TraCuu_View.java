package view;

import javax.swing.*;
import java.awt.*;

public class NhanVien_TraCuu_View extends JPanel {
    public NhanVien_TraCuu_View() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Chức năng Tra cứu Nhân viên", JLabel.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        add(lbl, BorderLayout.CENTER);
    }
}
