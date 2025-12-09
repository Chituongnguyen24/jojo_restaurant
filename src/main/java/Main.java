import javax.swing.*;
import dao.TaiKhoan_DAO;
import entity.TaiKhoan;
import view.Login.DangNhap_View;
import view.QuanLy_View;
import view.TiepTan_View;

public class Main {
    public static void main(String[] args) {
      
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            
            DangNhap_View loginView = new DangNhap_View();
            loginView.setVisible(true);

       
            loginView.addLoginListener(e -> {
                String username = loginView.getUsername();
                String password = loginView.getPassword();

                if (username.isEmpty() || password.isEmpty()) {
                    loginView.showError("Vui lòng nhập đầy đủ thông tin!");
                    return;
                }

                TaiKhoan_DAO taiKhoanDAO = new TaiKhoan_DAO();
                TaiKhoan tk = taiKhoanDAO.login(username, password);

                if (tk == null) {
                    loginView.showError("Tên đăng nhập hoặc mật khẩu không đúng!");
                    return;
                }

                loginView.dispose();

                switch (tk.getVaiTro().toUpperCase()) {
                    case "NVQL": 
                        new QuanLy_View(tk).setVisible(true);
                        break;
                    case "NVTT":
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