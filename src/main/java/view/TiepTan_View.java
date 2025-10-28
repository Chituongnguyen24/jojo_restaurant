package view;

import javax.swing.*;
import java.awt.*;
import entity.TaiKhoan;

/**
 * Giao diện dành cho Tiếp tân
 * Chức năng:
 * - Quản lý đặt bàn
 * - Tra cứu thực đơn
 * - Tra cứu hóa đơn
 * - Tra cứu khách hàng
 */
public class TiepTan_View extends JFrame {
    
    private TaiKhoan taiKhoan;
    
    public TiepTan_View(TaiKhoan tk) {
        this.taiKhoan = tk;
        
        // Cấu hình cửa sổ
        setTitle("Nhà hàng JOJO - Tiếp tân");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 600));
        setLocationRelativeTo(null);
        
        // Set icon cho cửa sổ
        try {
            setIconImage(new ImageIcon("images/logo.png").getImage());
        } catch (Exception e) {
            System.err.println("Không tìm thấy logo: " + e.getMessage());
        }
        
        // Thiết lập Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Thêm TrangChu_View với vai trò Tiếp tân
        setContentPane(new TrangChu_View(this, tk, "Tiếp tân"));
        
        // Hiển thị thông báo chào mừng
        SwingUtilities.invokeLater(() -> {
            showWelcomeMessage();
        });
    }
    
    /**
     * Hiển thị thông báo chào mừng khi đăng nhập
     */
    private void showWelcomeMessage() {
        String message = String.format(
            "Xin chào %s!\n\n" +
            "Bạn đã đăng nhập với vai trò: Tiếp tân\n\n" +
            "Các chức năng của bạn:\n" +
            "• Quản lý đặt bàn\n" +
            "• Đặt món cho khách hàng\n" +
            "• Quản lý và thanh toán hóa đơn\n" +
            "• Tra cứu hóa đơn\n" +
            "• Quản lý khách hàng\n" +
            "• Quản lý điểm tích lũy\n" +
            "• Tra cứu thực đơn",
            taiKhoan.getTenDangNhap()
        );
        
        JOptionPane.showMessageDialog(
            this,
            message,
            "Chào mừng",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Lấy thông tin tài khoản hiện tại
     */
    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }
    
    /**
     * Test main method
     */
    public static void main(String[] args) {
        // Enable anti-aliasing cho text
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        SwingUtilities.invokeLater(() -> {
            // Tạo tài khoản test
            TaiKhoan tkTest = new TaiKhoan();
            tkTest.setTenDangNhap("tieptan01");
            tkTest.setVaiTro("TT"); // Tiếp tân
            
            // Hiển thị giao diện
            TiepTan_View view = new TiepTan_View(tkTest);
            view.setVisible(true);
        });
    }
}