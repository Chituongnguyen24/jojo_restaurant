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
     * Chuyển đổi một ResultSet thành đối tượng Ban.
     * ĐÃ CẬP NHẬT: Sử dụng fromTenHienThi và fromString để map enum.
     */
    private Ban createBanFromResultSet(ResultSet rs) throws SQLException {
        return new Ban(
            rs.getString("maBan"),
            rs.getInt("soCho"),
            // SỬA Ở ĐÂY: Dùng hàm map tùy chỉnh thay vì valueOf()
            LoaiBan.fromTenHienThi(rs.getString("loaiBan")), 
            rs.getString("maKhuVuc"),
            // SỬA Ở ĐÂY: Dùng hàm map tùy chỉnh thay vì valueOf()
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
                ds.add(createBanFromResultSet(rs)); // Sử dụng hàm chung
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
            // SỬA Ở ĐÂY: Lưu tên hiển thị (giống CSDL) thay vì tên enum (THUONG)
            stmt.setString(3, ban.getLoaiBan().getTenHienThi()); 
            stmt.setString(4, ban.getMaKhuVuc());
             // Giữ nguyên: toString() của TrangThaiBan đã trả về giá trị đúng ("Trống", "Có khách"...)
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
            // SỬA Ở ĐÂY: Lưu tên hiển thị (giống CSDL)
            stmt.setString(2, ban.getLoaiBan().getTenHienThi());
            stmt.setString(3, ban.getMaKhuVuc());
            // Giữ nguyên: toString() của TrangThaiBan đã trả về giá trị đúng
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
        // Sửa chính tả "makhuVuc" -> "maKhuVuc" (tên cột trong DB)
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

    public Map<String, Integer> getSoBanTheoKhuVuc() {
        Map<String, Integer> map = new LinkedHashMap<>();
        // Sửa chính tả "makhuVuc" -> "maKhuVuc" (tên cột trong DB)
        // Thêm JOIN với KHUVUC để lấy tenKhuVuc, và ORDER BY để nhất quán
        String sql = "SELECT kv.tenKhuVuc, COUNT(b.maBan) AS soBan " +
                     "FROM Ban b " +
                     "JOIN KHUVUC kv ON b.maKhuVuc = kv.maKhuVuc " +
                     "GROUP BY kv.tenKhuVuc, kv.maKhuVuc " +
                     "ORDER BY kv.maKhuVuc";


        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Sửa: Dùng tenKhuVuc làm key để nhất quán với giao diện
                map.put(rs.getString("tenKhuVuc"), rs.getInt("soBan")); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public List<Ban> getBanTheoTrangThai(TrangThaiBan trangThai) {
        List<Ban> ds = new ArrayList<>();
        String sql = "SELECT * FROM Ban WHERE trangThai = ?";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            // Giữ nguyên: toString() của TrangThaiBan đã trả về giá trị đúng
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

    public Ban getBanById(String maBan) {
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
            while(rs.next()) {
                map.put(rs.getString("maKhuVuc"), rs.getString("tenKhuVuc"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

}