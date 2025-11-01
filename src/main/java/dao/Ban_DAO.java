package dao;

import connectDB.ConnectDB;
import entity.Ban;
import entity.KhuVuc;
import enums.TrangThaiBan;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Ban_DAO {
    
    // Helper để tạo đối tượng Ban từ ResultSet
    private Ban createBanFromResultSet(ResultSet rs) throws SQLException {
        String maKV = rs.getString("maKhuVuc");
        KhuVuc khuVuc = new KhuVuc(maKV); // Tạo đối tượng KhuVuc chỉ với mã
        
        // SỬA: Thay thế maKhuVuc (String) bằng đối tượng KhuVuc
        return new Ban(
            rs.getString("maBan"),
            rs.getInt("soCho"),
            khuVuc, // ĐÃ SỬA
            rs.getString("loaiBan"), // Lấy raw String
            rs.getString("trangThai") // Lấy raw String
        );
    }
    
    public List<Ban> getAllBan() {
        List<Ban> ds = new ArrayList<>();
        String sql = "SELECT * FROM Ban";

        try (Connection con = ConnectDB.getConnection();
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
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maBan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createBanFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean themBan(Ban ban) {
        // LƯU Ý: Phải lấy MaKV và LoaiBan/TrangThai dưới dạng String
        String sql = "INSERT INTO Ban (maBan, soCho, loaiBan, maKhuVuc, trangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, ban.getMaBan());
            stmt.setInt(2, ban.getSoCho());
            stmt.setString(3, ban.getLoaiBan()); // Dùng String (ví dụ: "THUONG")
            stmt.setString(4, ban.getKhuVuc().getMaKhuVuc()); // LẤY MÃ KHU VỰC
            stmt.setString(5, ban.getTrangThai());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean capNhatBan(Ban ban) {
        String sql = "UPDATE Ban SET soCho = ?, loaiBan = ?, maKhuVuc = ?, trangThai = ? WHERE maBan = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, ban.getSoCho());
            stmt.setString(2, ban.getLoaiBan());
            stmt.setString(3, ban.getKhuVuc().getMaKhuVuc()); // LẤY MÃ KHU VỰC
            stmt.setString(4, ban.getTrangThai());
            stmt.setString(5, ban.getMaBan());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoaBan(String maBan) {
        String sql = "DELETE FROM Ban WHERE maBan = ?";
        try (Connection con = ConnectDB.getConnection();
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
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maKhuVuc);
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

    public List<Ban> getBanTheoTrangThai(String trangThai) {
        List<Ban> ds = new ArrayList<>();
        String sql = "SELECT * FROM Ban WHERE trangThai = ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, trangThai);
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

    public Map<String, String> getDanhSachKhuVuc() {
        Map<String, String> map = new LinkedHashMap<>();
        String sql = "SELECT maKhuVuc, tenKhuVuc FROM KHUVUC WHERE trangThai = 1 ORDER BY maKhuVuc";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                // key=maKhuVuc, value=tenKhuVuc
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
        try (Connection con = ConnectDB.getConnection();
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

    public Map<String, Integer> getSoBanTheoKhuVuc() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql =
            " SELECT kv.maKhuVuc, kv.tenKhuVuc, COUNT(b.maBan) AS SoLuong" +
            " FROM KHUVUC kv" +
            " LEFT JOIN Ban b ON kv.maKhuVuc = b.maKhuVuc" +
            " GROUP BY kv.maKhuVuc, kv.tenKhuVuc" +
            " ORDER BY kv.maKhuVuc";

        try (Connection con = ConnectDB.getConnection();
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

    public Map<String, Integer> getSoBanTheoKhuVuc(java.util.Date from, java.util.Date to) {
        if (from == null || to == null) {
            return getSoBanTheoKhuVuc();
        }

        Map<String, Integer> map = new LinkedHashMap<>();
        String sql =
            " SELECT kv.maKhuVuc, kv.tenKhuVuc, " +
            " COUNT(DISTINCT CASE WHEN hd.MaHD IS NOT NULL THEN b.maBan ELSE NULL END) AS SoLuong" +
            " FROM KHUVUC kv" +
            " LEFT JOIN Ban b ON kv.maKhuVuc = b.maKhuVuc" +
            " LEFT JOIN PHIEUDATBAN pdb ON b.maBan = pdb.maBan" +
            " LEFT JOIN HOADON hd ON pdb.maPhieu = hd.MaPhieu AND hd.DaThanhToan = 1 AND hd.NgayLapHoaDon BETWEEN ? AND ?" +
            " GROUP BY kv.maKhuVuc, kv.tenKhuVuc" +
            " ORDER BY kv.maKhuVuc";

        try (Connection con = ConnectDB.getConnection();
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
        
        // SỬA LỖI: Chuyển sang try-with-resources để quản lý tài nguyên nhất quán
        try (Connection conn = ConnectDB.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String trangThaiValue = trangThaiMoi.toString();

            stmt.setString(1, trangThaiValue);
            stmt.setString(2, maBan);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        // Bỏ khối finally cũ
    }

    public String getTenKhuVuc(String maKV) {
        String sql = "SELECT tenKhuVuc FROM KHUVUC WHERE maKhuVuc = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maKV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("tenKhuVuc");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getMaBanTuDong() {
        String newID = "B01"; 
        String sql = "SELECT TOP 1 maBan FROM Ban WHERE maBan LIKE 'B[0-9]%' OR maBan LIKE 'B[0-9][0-9]%' ORDER BY maBan DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastID = rs.getString("maBan").trim();
                // Lấy phần số: chỉ xem xét các mã bắt đầu bằng B
                if (lastID.startsWith("B")) {
                     String numPart = lastID.substring(1); 
                     try {
                         int num = Integer.parseInt(numPart);
                         num++; 
                         newID = "B" + String.format("%02d", num); // Format B01, B02, ... B10
                     } catch (NumberFormatException e) {
                         // Nếu không parse được, dùng mã mặc định
                     }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newID;
    }
    public List<Ban> getBanTrongPhuHop(int soCho, String maKhuVuc) {
        List<Ban> ds = new ArrayList<>();
        String sql = "SELECT * FROM Ban WHERE soCho >= ? AND trangThai = ? AND maKhuVuc LIKE ?";
        
        // Nếu chọn "Tất cả khu vực", dùng %
        String finalMaKV = (maKhuVuc == null || maKhuVuc.equals("Tất cả")) ? "%" : maKhuVuc;

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, soCho);
            stmt.setString(2, TrangThaiBan.TRONG.toString());
            stmt.setString(3, finalMaKV);
            
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
}