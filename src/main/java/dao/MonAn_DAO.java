package dao;

import connectDB.ConnectDB;
import entity.MonAn;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonAn_DAO {

    // 🔹 Lấy toàn bộ món ăn
    public List<MonAn> getAllMonAn() {
        List<MonAn> dsMonAn = new ArrayList<>();
        String sql = "SELECT * FROM MONAN";

        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String maMon = rs.getString("maMonAn").trim();
                String tenMon = rs.getString("tenMonAn");
                double donGia = rs.getDouble("donGia");
                boolean trangThai = rs.getBoolean("trangThai");

                MonAn monAn = new MonAn(maMon, tenMon, donGia, trangThai);
                dsMonAn.add(monAn);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dsMonAn;
    }

    // 🔹 Thêm món ăn mới
    public boolean insertMonAn(MonAn monAn) {
        String sql = "INSERT INTO MONAN(maMonAn, tenMonAn, donGia, trangThai) VALUES(?, ?, ?, ?)";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, monAn.getMaMonAn());
            pstmt.setString(2, monAn.getTenMonAn());
            pstmt.setDouble(3, monAn.getDonGia());
            pstmt.setBoolean(4, monAn.isTrangThai());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 🔹 Cập nhật món ăn
    public boolean updateMonAn(MonAn monAn) {
        String sql = "UPDATE MONAN SET tenMonAn=?, donGia=?, trangThai=? WHERE maMonAn=?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, monAn.getTenMonAn());
            pstmt.setDouble(2, monAn.getDonGia());
            pstmt.setBoolean(3, monAn.isTrangThai());
            pstmt.setString(4, monAn.getMaMonAn());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 🔹 Xóa món ăn theo mã
    public boolean deleteMonAn(String maMonAn) {
        String sql = "DELETE FROM MONAN WHERE maMonAn=?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maMonAn);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // 🔹 Tìm món ăn theo mã
    public MonAn findByMaMonAn(String maMonAn) {
        String sql = "SELECT * FROM MONAN WHERE maMonAn=?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maMonAn);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new MonAn(
                            rs.getString("maMonAn").trim(),
                            rs.getString("tenMonAn"),
                            rs.getDouble("donGia"),
                            rs.getBoolean("trangThai")
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
