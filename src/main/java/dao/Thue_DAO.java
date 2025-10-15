package dao;

import connectDB.ConnectDB;
import entity.Thue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Thue_DAO {

    // Lấy toàn bộ thuế
    public List<Thue> getAllThue() {
        List<Thue> dsThue = new ArrayList<>();
        String sql = "SELECT * FROM Thue";

        try (Connection conn = new ConnectDB().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Thue thue = new Thue(
                        rs.getString("maSoThue"),
                        rs.getString("tenThue"),
                        rs.getDouble("tyLeThue")  
                );
                dsThue.add(thue);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsThue;
    }

    // Thêm thuế
    public boolean insertThue(Thue thue) {
        String sql = "INSERT INTO Thue(maSoThue, tenThue, tyLeThue) VALUES(?, ?, ?)";

        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, thue.getMaSoThue());
            pstmt.setString(2, thue.getTenThue());
            pstmt.setDouble(3, thue.getTiLeThue()); 
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật thuế
    public boolean updateThue(Thue thue) {
        String sql = "UPDATE Thue SET tenThue=?, tyLeThue=? WHERE maSoThue=?";

        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, thue.getTenThue());
            pstmt.setDouble(2, thue.getTiLeThue());  // Sử dụng setter của entity
            pstmt.setString(3, thue.getMaSoThue());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa thuế
    public boolean deleteThue(String maThue) {
        String sql = "DELETE FROM Thue WHERE maSoThue=?";

        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maThue);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm thuế theo mã
    public Thue findByMaThue(String maThue) {
        String sql = "SELECT * FROM Thue WHERE maSoThue=?";

        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maThue);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Thue(
                            rs.getString("maSoThue"),
                            rs.getString("tenThue"),
                            rs.getDouble("tyLeThue")  // Sử dụng tên cột đúng từ DB
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}