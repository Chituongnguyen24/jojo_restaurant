package view;

import javax.swing.*;
import entity.TaiKhoan;

public class TiepTan_View extends JFrame {

    public TiepTan_View(TaiKhoan tk) {
        setTitle("Nhà hàng JOJO - Tiếp tân");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        setContentPane(new TrangChu_View(this, tk, "Tiếp tân"));
        setVisible(true);
    }
}
