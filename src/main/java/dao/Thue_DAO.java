package dao;

import connectDB.ConnectDB;
import entity.Thue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Thue_DAO {

    public List<Thue> getAllThue() {
        List<Thue> dsThue = new ArrayList<>();
        String sql = "SELECT * FROM Thue";

        try (Connection conn = new ConnectDB().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String maThue = rs.getString("maSoThue");
                String tenThue = rs.getString("tenThue");
                double tyLeThue = rs.getDouble("tyLeThue");
                String moTa = rs.getString("moTa");
                // Chuyển đổi từ BIT (boolean) trong DB sang String
                String trangThai = rs.getBoolean("trangThai") ? "Đang áp dụng" : "Ngừng áp dụng";

                Thue thue = new Thue(maThue, tenThue, tyLeThue, moTa, trangThai);
                dsThue.add(thue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsThue;
    }

    /**
     * Thêm một đối tượng thuế mới vào cơ sở dữ liệu
     * @param thue Đối tượng Thue cần thêm
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean insertThue(Thue thue) {
        String sql = "INSERT INTO Thue(maSoThue, tenThue, tyLeThue, moTa, trangThai) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, thue.getMaThue());
            pstmt.setString(2, thue.getTenThue());
            pstmt.setDouble(3, thue.getPhanTram()); // Khớp với entity
            pstmt.setString(4, thue.getMoTa());
            // Chuyển đổi từ String sang BIT (boolean) để lưu vào DB
            pstmt.setBoolean(5, thue.getTrangThai().equalsIgnoreCase("Đang áp dụng"));

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateThue(Thue thue) {
        String sql = "UPDATE Thue SET tenThue=?, tyLeThue=?, moTa=?, trangThai=? WHERE maSoThue=?";

        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, thue.getTenThue());
            pstmt.setDouble(2, thue.getPhanTram()); // Khớp với entity
            pstmt.setString(3, thue.getMoTa());
            pstmt.setBoolean(4, thue.getTrangThai().equalsIgnoreCase("Đang áp dụng"));
            pstmt.setString(5, thue.getMaThue());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean deleteThue(String maThue) {
        String sql = "DELETE FROM Thue WHERE maSoThue=?";

        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maThue);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Thue findByMaThue(String maThue) {
        String sql = "SELECT * FROM Thue WHERE maSoThue=?";

        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maThue);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String tenThue = rs.getString("tenThue");
                    double tyLeThue = rs.getDouble("tyLeThue");
                    String moTa = rs.getString("moTa");
                    String trangThai = rs.getBoolean("trangThai") ? "Đang áp dụng" : "Ngừng áp dụng";

                    return new Thue(maThue, tenThue, tyLeThue, moTa, trangThai);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}