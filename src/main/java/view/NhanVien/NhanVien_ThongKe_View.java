package view.NhanVien;


import javax.swing.*;
import java.awt.*;

public class NhanVien_ThongKe_View extends JPanel {
    public NhanVien_ThongKe_View() {
        setLayout(new BorderLayout());
        JLabel lbl = new JLabel("Chức năng Thống kê Nhân viên", JLabel.CENTER);
        lbl.setFont(new Font("Arial", Font.BOLD, 18));
        add(lbl, BorderLayout.CENTER);
    }
}
