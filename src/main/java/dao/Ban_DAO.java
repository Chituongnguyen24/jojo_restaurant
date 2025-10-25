package dao;

import connectDB.ConnectDB;
import entity.Ban;
import enums.LoaiBan;
import enums.TrangThaiBan;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Ban_DAO {

    /**
     * Hàm chung để tạo đối tượng Ban từ ResultSet.
     */
    private Ban createBanFromResultSet(ResultSet rs) throws SQLException {
        return new Ban(
            rs.getString("maBan"),
            rs.getInt("soCho"),
            LoaiBan.fromTenHienThi(rs.getString("loaiBan")),
            rs.getString("maKhuVuc"),
            TrangThaiBan.fromString(rs.getString("trangThai"))
        );
    }

    public List<Ban> getAllBan() {
        List<Ban> ds = new ArrayList<>();
        String sql = "SELECT * FROM Ban";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ds.add(createBanFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public Ban getBanTheoMa(String maBan) {
        String sql = "SELECT * FROM Ban WHERE maBan = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maBan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createBanFromResultSet(rs); // Sử dụng hàm chung
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean themBan(Ban ban) {
        String sql = "INSERT INTO Ban (maBan, soCho, loaiBan, maKhuVuc, trangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, ban.getMaBan());
            stmt.setInt(2, ban.getSoCho());
            stmt.setString(3, ban.getLoaiBan().getTenHienThi());
            stmt.setString(4, ban.getMaKhuVuc());
            stmt.setString(5, ban.getTrangThai().toString());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean capNhatBan(Ban ban) {
        String sql = "UPDATE Ban SET soCho = ?, loaiBan = ?, maKhuVuc = ?, trangThai = ? WHERE maBan = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, ban.getSoCho());
            stmt.setString(2, ban.getLoaiBan().getTenHienThi());
            stmt.setString(3, ban.getMaKhuVuc());
            stmt.setString(4, ban.getTrangThai().toString());
            stmt.setString(5, ban.getMaBan());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoaBan(String maBan) {
        String sql = "DELETE FROM Ban WHERE maBan = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Ban> getBanTheoKhuVuc(String maKhuVuc) {
        List<Ban> ds = new ArrayList<>();
        String sql = "SELECT * FROM Ban WHERE maKhuVuc = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maKhuVuc);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(createBanFromResultSet(rs)); // Sử dụng hàm chung
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // --- ĐÃ XÓA HÀM getSoBanTheoKhuVuc() CŨ ĐỂ THAY BẰNG 2 HÀM MỚI BÊN DƯỚI ---

    public List<Ban> getBanTheoTrangThai(TrangThaiBan trangThai) {
        List<Ban> ds = new ArrayList<>();
        String sql = "SELECT * FROM Ban WHERE trangThai = ?";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, trangThai.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(createBanFromResultSet(rs)); // Sử dụng hàm chung
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Lấy danh sách mã khu vực và tên khu vực.
     * Cần thiết cho dialog thêm bàn.
     * @return Map<String, String> (Mã Khu Vực, Tên Khu Vực)
     */
    public Map<String, String> getDanhSachKhuVuc() {
        Map<String, String> map = new LinkedHashMap<>();
        String sql = "SELECT maKhuVuc, tenKhuVuc FROM KHUVUC WHERE trangThai = 1 ORDER BY maKhuVuc";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                map.put(rs.getString("maKhuVuc"), rs.getString("tenKhuVuc"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public List<Ban> getBanDangHoatDong() {
        List<Ban> ds = new ArrayList<>();
        String sql = "SELECT * FROM Ban WHERE trangThai = ? OR trangThai = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, TrangThaiBan.CO_KHACH.toString());
            stmt.setString(2, TrangThaiBan.DA_DAT.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(createBanFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    // ===== PHẦN MỚI ĐƯỢC THÊM VÀO =====

    // Lấy số bàn theo khu vực (không có khoảng thời gian)
    public Map<String, Integer> getSoBanTheoKhuVuc() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql =
            " SELECT kv.tenKhuVuc, COUNT(DISTINCT pdb.maBan) AS SoLuong" +
            " FROM KHUVUC kv" +
            " LEFT JOIN Ban b ON kv.maKhuVuc = b.maKhuVuc" +
            " LEFT JOIN PHIEUDATBAN pdb ON b.maBan = pdb.maBan" +
            " LEFT JOIN HOADON hd ON pdb.maPhieu = hd.maPhieu" +
            " WHERE hd.daThanhToan = 1" +
            " GROUP BY kv.tenKhuVuc, kv.maKhuVuc" +
            " ORDER BY kv.maKhuVuc";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("tenKhuVuc"), rs.getInt("SoLuong"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    // Lấy số bàn theo khu vực có khoảng thời gian
    public Map<String, Integer> getSoBanTheoKhuVuc(java.util.Date from, java.util.Date to) {
        if (from == null || to == null) {
            return getSoBanTheoKhuVuc();
        }

        Map<String, Integer> map = new LinkedHashMap<>();
        String sql =
            " SELECT kv.tenKhuVuc, COUNT(DISTINCT pdb.maBan) AS SoLuong" +
            " FROM KHUVUC kv" +
            " LEFT JOIN Ban b ON kv.maKhuVuc = b.maKhuVuc" +
            " LEFT JOIN PHIEUDATBAN pdb ON b.maBan = pdb.maBan" +
            " LEFT JOIN HOADON hd ON pdb.maPhieu = hd.maPhieu" +
            " WHERE hd.daThanhToan = 1 AND hd.ngayLap BETWEEN ? AND ?" +
            " GROUP BY kv.tenKhuVuc, kv.maKhuVuc" +
            " ORDER BY kv.maKhuVuc";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(from.getTime()));
            stmt.setDate(2, new java.sql.Date(to.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString("tenKhuVuc"), rs.getInt("SoLuong"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}