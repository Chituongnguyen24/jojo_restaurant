package dao;

import connectDB.ConnectDB;
import entity.TaiKhoan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class TaiKhoan_DAO {

    // Lấy tất cả tài khoản
    public List<TaiKhoan> getAllTaiKhoan() {
        List<TaiKhoan> dsTK = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan";
        try (Connection conn = new ConnectDB().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TaiKhoan tk = new TaiKhoan(
                        rs.getString("maNV"),
                        rs.getString("tenDangNhap"),
                        rs.getString("matKhau"),
                        rs.getString("vaiTro")
                );
                dsTK.add(tk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsTK;
    }

    // Thêm tài khoản
    public boolean insertTaiKhoan(TaiKhoan tk) {
        String sql = "INSERT INTO TaiKhoan(maNV, tenDangNhap, matKhau, vaiTro) VALUES (?, ?, ?, ?)";
        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tk.getMaNV());
            pstmt.setString(2, tk.getTenDangNhap());
            pstmt.setString(3, tk.getMatKhau());
            pstmt.setString(4, tk.getVaiTro());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật tài khoản
    public boolean updateTaiKhoan(TaiKhoan tk) {
        String sql = "UPDATE TaiKhoan SET tenDangNhap=?, matKhau=?, vaiTro=? WHERE maNV=?";
        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tk.getTenDangNhap());
            pstmt.setString(2, tk.getMatKhau());
            pstmt.setString(3, tk.getVaiTro());
            pstmt.setString(4, tk.getMaNV());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa tài khoản theo mã nhân viên
    public boolean deleteTaiKhoan(String maNV) {
        String sql = "DELETE FROM TaiKhoan WHERE maNV=?";
        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNV);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm tài khoản theo tên đăng nhập
    public TaiKhoan findByUsername(String tenDangNhap) {
        String sql = "SELECT * FROM TaiKhoan WHERE tenDangNhap=?";
        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tenDangNhap);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new TaiKhoan(
                            rs.getString("maNV"),
                            rs.getString("tenDangNhap"),
                            rs.getString("matKhau"),
                            rs.getString("vaiTro")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Check đăng nhập (trả về đối tượng TaiKhoan nếu đúng, null nếu sai)
    public TaiKhoan login(String tenDangNhap, String matKhau) {
        String sql = "SELECT * FROM TaiKhoan WHERE tenDangNhap=? AND matKhau=?";
        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tenDangNhap);
            pstmt.setString(2, matKhau);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new TaiKhoan(
                            rs.getString("maNV"),
                            rs.getString("tenDangNhap"),
                            rs.getString("matKhau"),
                            rs.getString("vaiTro")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- MỚI: Tìm tài khoản theo mã nhân viên ---
    public TaiKhoan findByMaNV(String maNV) {
        String sql = "SELECT * FROM TaiKhoan WHERE maNV=?";
        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNV);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new TaiKhoan(
                            rs.getString("maNV"),
                            rs.getString("tenDangNhap"),
                            rs.getString("matKhau"),
                            rs.getString("vaiTro")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateMatKhau(String maNV, String newHashedPassword) {
        String sql = "UPDATE TaiKhoan SET matKhau=? WHERE maNV=?";
        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newHashedPassword);
            pstmt.setString(2, maNV);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}