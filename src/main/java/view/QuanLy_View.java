package view;

import javax.swing.*;
import entity.TaiKhoan;

public class QuanLy_View extends JFrame {

    public QuanLy_View(TaiKhoan tk) {
        setTitle("Nhà hàng JOJO - Quản lý");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);

        // Set icon cho cửa sổ
        try {
            setIconImage(new ImageIcon("images/logo.png").getImage());
        } catch (Exception e) {
            System.err.println("Không tìm thấy logo: " + e.getMessage());
        }

        setContentPane(new TrangChu_View(this, tk, "Quản lý"));
        setVisible(true);
    }
}