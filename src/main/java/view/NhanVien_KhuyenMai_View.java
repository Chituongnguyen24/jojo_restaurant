package view;

import javax.swing.*;
import java.awt.*;

public class NhanVien_KhuyenMai_View extends JPanel {
    public NhanVien_KhuyenMai_View() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Chức năng Khuyến mãi Nhân viên", JLabel.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        add(lbl, BorderLayout.CENTER);
    }
}

