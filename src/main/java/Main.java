

import javax.swing.*;
import dao.Login_DAO;
import entity.TaiKhoan;
import view.Login.Login_View;
import view.QuanLy_View;
import view.TiepTan_View;

public class Main {
    public static void main(String[] args) {
        // Bật khử răng cưa font chữ
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Mở màn hình đăng nhập
            Login_View loginView = new Login_View();
            loginView.setVisible(true);

            // Gắn sự kiện cho nút đăng nhập
            loginView.addLoginListener(e -> {
                String username = loginView.getUsername();
                String password = loginView.getPassword();

                if (username.isEmpty() || password.isEmpty()) {
                    loginView.showError("Vui lòng nhập đầy đủ thông tin!");
                    return;
                }

                Login_DAO loginDAO = new Login_DAO();
                TaiKhoan tk = loginDAO.login(username, password);

                if (tk == null) {
                    loginView.showError("Tên đăng nhập hoặc mật khẩu không đúng!");
                    return;
                }

                // Nếu đăng nhập đúng
                loginView.showMessage("Đăng nhập thành công! Xin chào " + tk.getVaiTro());
                loginView.dispose();

                switch (tk.getVaiTro().toUpperCase()) {
                    case "NVQL": // Nhân viên quản lý
                        new QuanLy_View(tk).setVisible(true);
                        break;
                    case "NVTT": // Nhân viên tiếp tân
                        new TiepTan_View(tk).setVisible(true);
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Vai trò không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            });
        });
    }
}
