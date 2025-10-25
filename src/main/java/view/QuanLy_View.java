package view;

import javax.swing.*;
import entity.TaiKhoan;

public class QuanLy_View extends JFrame {

    public QuanLy_View(TaiKhoan tk) {
        setTitle("Nhà hàng JOJO - Quản lý");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        setContentPane(new TrangChu_View(this, tk, "Quản lý"));
        setVisible(true);
    }
}
