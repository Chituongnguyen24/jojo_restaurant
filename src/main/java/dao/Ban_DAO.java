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

    public List<Ban> getAllBan() {
        List<Ban> ds = new ArrayList<>();
        String sql = "SELECT * FROM Ban";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ban ban = new Ban(
                    rs.getString("maBan"),
                    rs.getInt("soCho"),
                    LoaiBan.fromTenHienThi(rs.getString("loaiBan")),
                    rs.getString("maKhuVuc"),
                    TrangThaiBan.fromString(rs.getString("trangThai"))
                );
                ds.add(ban);
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
                    return new Ban(
                        rs.getString("maBan"),
                        rs.getInt("soCho"),
                        LoaiBan.fromTenHienThi(rs.getString("loaiBan")),
                        rs.getString("maKhuVuc"),
                        TrangThaiBan.fromString(rs.getString("trangThai"))
                    );
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
                    Ban ban = new Ban(
                        rs.getString("maBan"),
                        rs.getInt("soCho"),
                        LoaiBan.fromTenHienThi(rs.getString("loaiBan")),
                        rs.getString("maKhuVuc"),
                        TrangThaiBan.fromString(rs.getString("trangThai"))
                    );
                    ds.add(ban);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public List<Ban> getBanTheoTrangThai(TrangThaiBan trangThai) {
        List<Ban> ds = new ArrayList<>();
        String sql = "SELECT * FROM Ban WHERE trangThai = ?";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, trangThai.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Ban ban = new Ban(
                        rs.getString("maBan"),
                        rs.getInt("soCho"),
                        LoaiBan.fromTenHienThi(rs.getString("loaiBan")),
                        rs.getString("maKhuVuc"),
                        TrangThaiBan.fromString(rs.getString("trangThai"))
                    );
                    ds.add(ban);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

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
                    Ban ban = new Ban(
                        rs.getString("maBan"),
                        rs.getInt("soCho"),
                        LoaiBan.fromTenHienThi(rs.getString("loaiBan")),
                        rs.getString("maKhuVuc"),
                        TrangThaiBan.fromString(rs.getString("trangThai"))
                    );
                    ds.add(ban);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    /**
     * Trả về tổng số bàn (bao gồm cả bàn chưa đặt) theo tên khu vực.
     * Key của map = tenKhuVuc (để dễ hiển thị tên), giữ thứ tự theo maKhuVuc.
     */
    public Map<String, Integer> getSoBanTheoKhuVuc() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql =
            " SELECT kv.maKhuVuc, kv.tenKhuVuc, COUNT(b.maBan) AS SoLuong" +
            " FROM KHUVUC kv" +
            " LEFT JOIN Ban b ON kv.maKhuVuc = b.maKhuVuc" +
            " GROUP BY kv.maKhuVuc, kv.tenKhuVuc" +
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

    /**
     * Trả về số bàn từng khu vực có hóa đơn đã thanh toán trong khoảng from..to.
     * Nếu from/to null => trả về getSoBanTheoKhuVuc()
     *
     * Lưu ý: câu SQL sử dụng LEFT JOIN với điều kiện lọc trong ON để không vô tình
     * biến LEFT JOIN thành INNER JOIN.
     */
    public Map<String, Integer> getSoBanTheoKhuVuc(java.util.Date from, java.util.Date to) {
        if (from == null || to == null) {
            return getSoBanTheoKhuVuc();
        }

        Map<String, Integer> map = new LinkedHashMap<>();
        String sql =
            " SELECT kv.maKhuVuc, kv.tenKhuVuc, " +
            " COUNT(DISTINCT CASE WHEN hd.maPhieu IS NOT NULL THEN b.maBan ELSE NULL END) AS SoLuong" +
            " FROM KHUVUC kv" +
            " LEFT JOIN Ban b ON kv.maKhuVuc = b.maKhuVuc" +
            " LEFT JOIN PHIEUDATBAN pdb ON b.maBan = pdb.maBan" +
            " LEFT JOIN HOADON hd ON pdb.maPhieu = hd.maPhieu AND hd.daThanhToan = 1 AND hd.ngayLap BETWEEN ? AND ?" +
            " GROUP BY kv.maKhuVuc, kv.tenKhuVuc" +
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

    public boolean capNhatTrangThaiBan(String maBan, TrangThaiBan trangThaiMoi) {
        String sql = "UPDATE Ban SET trangThai = ? WHERE maBan = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ConnectDB.getInstance().getConnection();
            stmt = conn.prepareStatement(sql);

            String trangThaiValue = trangThaiMoi.toString();

            // === DEBUGGING ===
            System.out.println("Ban_DAO.capNhatTrangThaiBan: Cập nhật bàn '" + maBan + "' về trạng thái '" + trangThaiValue + "'"); // Log giá trị CÓ DẤU
            // ===============

         
            stmt.setString(1, trangThaiValue);
            stmt.setString(2, maBan);

            int rowsAffected = stmt.executeUpdate();

            // === DEBUGGING ===
            System.out.println("Ban_DAO.capNhatTrangThaiBan: Số dòng bị ảnh hưởng = " + rowsAffected);
            // ===============

            return rowsAffected > 0; // Trả về true nếu cập nhật thành công

        } catch (SQLException e) {
            // === DEBUGGING ===
            System.err.println("Ban_DAO.capNhatTrangThaiBan: Lỗi SQL khi cập nhật trạng thái bàn!");
            e.printStackTrace();
            // ===============
            return false;
        } catch (Exception e) {
             // === DEBUGGING ===
            System.err.println("Ban_DAO.capNhatTrangThaiBan: Lỗi không xác định!");
            e.printStackTrace();
            // ===============
            return false;
        }
        finally {
            // Đóng tài nguyên
            try { if (stmt != null) stmt.close(); } catch (SQLException ignored) {}
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }
}