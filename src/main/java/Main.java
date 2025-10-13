import javax.swing.*;
import view.TrangChu_View;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Nhà hàng JOJO - Quản lý");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            // Gọi Trang chủ làm màn hình mặc định
            frame.setContentPane(new TrangChu_View());

            //Set full màn hình
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
            frame.setUndecorated(false); 
            frame.setVisible(true);
        });
    }
}
