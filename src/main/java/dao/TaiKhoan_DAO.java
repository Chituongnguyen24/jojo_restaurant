package dao;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoan_DAO {

    // Helper method để tạo đối tượng TaiKhoan từ ResultSet
    private TaiKhoan createTaiKhoanFromResultSet(ResultSet rs) throws SQLException {
        int userID = rs.getInt("userID");
        String maNV = rs.getString("maNhanVien");
        String tenDangNhap = rs.getString("tenDangNhap");
        String matKhau = rs.getString("matKhau");
        String vaiTro = rs.getString("vaiTro");
        Boolean trangThai = rs.getBoolean("trangThai");
        
        NhanVien nv = new NhanVien(maNV);
        
        // Cần truyền đủ tham số cho constructor mới của TaiKhoan
        return new TaiKhoan(userID, tenDangNhap, matKhau, vaiTro, trangThai, nv);
    }

    // Lấy tất cả tài khoản
    public List<TaiKhoan> getAllTaiKhoan() {
        List<TaiKhoan> dsTK = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan";
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                dsTK.add(createTaiKhoanFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsTK;
    }

    // Thêm tài khoản
    public boolean insertTaiKhoan(TaiKhoan tk) {
        String sql = "INSERT INTO TaiKhoan(maNhanVien, tenDangNhap, matKhau, vaiTro, trangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tk.getNhanVien().getMaNhanVien());
            pstmt.setString(2, tk.getTenDangNhap());
            pstmt.setString(3, tk.getMatKhau());
            pstmt.setString(4, tk.getVaiTro());
            pstmt.setBoolean(5, tk.getTrangThai());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật tài khoản
    public boolean updateTaiKhoan(TaiKhoan tk) {
        String sql = "UPDATE TaiKhoan SET tenDangNhap=?, matKhau=?, vaiTro=?, trangThai=?, maNhanVien=? WHERE userID=?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tk.getTenDangNhap());
            pstmt.setString(2, tk.getMatKhau());
            pstmt.setString(3, tk.getVaiTro());
            pstmt.setBoolean(4, tk.getTrangThai());
            pstmt.setString(5, tk.getNhanVien().getMaNhanVien());
            pstmt.setInt(6, tk.getUserID());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa tài khoản theo userID
    public boolean deleteTaiKhoan(int userID) {
        String sql = "DELETE FROM TaiKhoan WHERE userID=?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm tài khoản theo tên đăng nhập
    public TaiKhoan findByUsername(String tenDangNhap) {
        String sql = "SELECT * FROM TaiKhoan WHERE tenDangNhap=?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tenDangNhap);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createTaiKhoanFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Check đăng nhập (trả về đối tượng TaiKhoan nếu đúng, null nếu sai)
    public TaiKhoan login(String tenDangNhap, String matKhau) {
        String sql = "SELECT * FROM TaiKhoan WHERE tenDangNhap=? AND matKhau=? AND trangThai=1";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tenDangNhap);
            pstmt.setString(2, matKhau);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createTaiKhoanFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Tìm tài khoản theo mã nhân viên
    public TaiKhoan findByMaNV(String maNV) {
        String sql = "SELECT * FROM TaiKhoan WHERE maNhanVien=?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNV);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createTaiKhoanFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật mật khẩu
    public boolean updateMatKhau(String maNV, String newPassword) {
        String sql = "UPDATE TaiKhoan SET matKhau=? WHERE maNhanVien=?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPassword);
            pstmt.setString(2, maNV);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
