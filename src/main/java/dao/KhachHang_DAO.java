package dao;

import entity.KhachHang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;

public class KhachHang_DAO {

    // 1. Lấy tất cả khách hàng
    public List<KhachHang> getAllKhachHang() {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang";

        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("sdt"),
                        rs.getString("email"),
                        rs.getInt("diemTichLuy"),
                        rs.getBoolean("laThanhVien")
                );
                ds.add(kh);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // 2. Thêm khách hàng
    public boolean insertKhachHang(KhachHang kh) {
        String sql = "INSERT INTO KhachHang(maKhachHang, tenKhachHang, sdt, email, diemTichLuy, laThanhVien) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, kh.getMaKhachHang());
            stmt.setString(2, kh.getTenKhachHang());
            stmt.setString(3, kh.getSdt());
            stmt.setString(4, kh.getEmail());
            stmt.setInt(5, kh.getDiemTichLuy());
            stmt.setBoolean(6, kh.isLaThanhVien());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 3. Cập nhật khách hàng
    public boolean updateKhachHang(KhachHang kh) {
        String sql = "UPDATE KhachHang SET tenKhachHang=?, sdt=?, email=?, diemTichLuy=?, laThanhVien=? WHERE maKhachHang=?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, kh.getTenKhachHang());
            stmt.setString(2, kh.getSdt());
            stmt.setString(3, kh.getEmail());
            stmt.setInt(4, kh.getDiemTichLuy());
            stmt.setBoolean(5, kh.isLaThanhVien());
            stmt.setString(6, kh.getMaKhachHang());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 4. Xóa khách hàng theo mã
    public boolean deleteKhachHang(String maKH) {
        String sql = "DELETE FROM KhachHang WHERE maKhachHang = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maKH);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 5. Tìm kiếm khách hàng theo tên hoặc sdt
    public List<KhachHang> timKiemKhachHang(String keyword) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE tenKhachHang LIKE ? OR sdt LIKE ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("sdt"),
                        rs.getString("email"),
                        rs.getInt("diemTichLuy"),
                        rs.getBoolean("laThanhVien")
                );
                ds.add(kh);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // 6. Lấy khách hàng theo mã
    public KhachHang getKhachHangById(String maKH) {
        String sql = "SELECT * FROM KhachHang WHERE maKhachHang = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maKH);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("sdt"),
                        rs.getString("email"),
                        rs.getInt("diemTichLuy"),
                        rs.getBoolean("laThanhVien")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


}
