package dao;

import java.sql.*;
import connectDB.ConnectDB;
import entity.TaiKhoan;

public class Login_DAO {
    public TaiKhoan login(String username, String password) {
        TaiKhoan tk = null;
        try {
            Connection con = ConnectDB.getInstance().getConnection();
            String sql = "SELECT * FROM TAIKHOAN WHERE tenDangNhap = ? AND matKhau = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                tk = new TaiKhoan(
                        rs.getString("maNV"),
                        rs.getString("tenDangNhap"),
                        rs.getString("matKhau"),
                        rs.getString("vaiTro")
                );
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tk;
    }
}
