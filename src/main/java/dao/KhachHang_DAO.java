package dao;

import java.sql.*;
import java.util.*;
import connectDB.ConnectDB;
import entity.KhachHang;

public class KhachHang_DAO {

    // ====== 1️⃣ Lấy tất cả khách hàng ======
    public List<KhachHang> getAllKhachHang() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setMaKhachHang(rs.getString("MaKhachHang"));
                kh.setTenKhachHang(rs.getString("TenKhachHang"));
                kh.setSdt(rs.getString("SDT"));
                kh.setEmail(rs.getString("Email"));
                kh.setDiemTichLuy(rs.getInt("DiemTichLuy"));
                kh.setLaThanhVien(rs.getBoolean("LaThanhVien"));
                list.add(kh);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====== 2️⃣ Thêm khách hàng ======
    public boolean insertKhachHang(KhachHang kh) {
        String sql = """
            INSERT INTO KhachHang (MaKhachHang, TenKhachHang, SDT, Email, DiemTichLuy, LaThanhVien)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, kh.getMaKhachHang());
            ps.setString(2, kh.getTenKhachHang());
            ps.setString(3, kh.getSdt());
            ps.setString(4, kh.getEmail());
            ps.setInt(5, kh.getDiemTichLuy());
            ps.setBoolean(6, kh.isLaThanhVien());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== 3️⃣ Cập nhật thông tin khách hàng ======
    public boolean updateKhachHang(KhachHang kh) {
        String sql = """
            UPDATE KhachHang
            SET TenKhachHang = ?, SDT = ?, Email = ?, DiemTichLuy = ?, LaThanhVien = ?
            WHERE MaKhachHang = ?
        """;

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, kh.getTenKhachHang());
            ps.setString(2, kh.getSdt());
            ps.setString(3, kh.getEmail());
            ps.setInt(4, kh.getDiemTichLuy());
            ps.setBoolean(5, kh.isLaThanhVien());
            ps.setString(6, kh.getMaKhachHang());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== 4️⃣ Xóa khách hàng ======
    public boolean deleteKhachHang(String maKhachHang) {
        String sql = "DELETE FROM KhachHang WHERE MaKhachHang = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, maKhachHang);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====== 5️⃣ Tìm khách hàng theo số điện thoại ======
    public KhachHang findBySdt(String sdt) {
        String sql = "SELECT * FROM KhachHang WHERE SDT = ?";
        KhachHang kh = null;

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                kh = new KhachHang();
                kh.setMaKhachHang(rs.getString("MaKhachHang"));
                kh.setTenKhachHang(rs.getString("TenKhachHang"));
                kh.setSdt(rs.getString("SDT"));
                kh.setEmail(rs.getString("Email"));
                kh.setDiemTichLuy(rs.getInt("DiemTichLuy"));
                kh.setLaThanhVien(rs.getBoolean("LaThanhVien"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return kh;
    }

    // ====== 6️⃣ Thống kê khách hàng thành viên / không thành viên ======
    public Map<String, Integer> thongKeThanhVien() {
        Map<String, Integer> data = new LinkedHashMap<>();
        String sql = """
            SELECT 
                CASE WHEN LaThanhVien = 1 THEN N'Thành viên' ELSE N'Khách thường' END AS Loai,
                COUNT(*) AS SoLuong
            FROM KhachHang
            GROUP BY LaThanhVien
        """;

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                data.put(rs.getString("Loai"), rs.getInt("SoLuong"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}
