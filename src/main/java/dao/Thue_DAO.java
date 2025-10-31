package dao;

import connectDB.ConnectDB;
import entity.Thue;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Thue_DAO {

    private Thue createThueFromResultSet(ResultSet rs) throws SQLException {
        String maThue = rs.getString("maSoThue");
        String tenThue = rs.getString("tenThue");
        double tyLeThue = rs.getDouble("tyLeThue");
        String moTa = rs.getString("moTa");
        boolean trangThai = rs.getBoolean("trangThai");
        return new Thue(maThue, tenThue, tyLeThue, moTa, trangThai);
    }

    public List<Thue> getAllThue() {
        List<Thue> dsThue = new ArrayList<>();
        String sql = "SELECT * FROM THUE ORDER BY maSoThue";
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { dsThue.add(createThueFromResultSet(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return dsThue;
    }

    public List<Thue> getAllThueActive() {
        List<Thue> dsThue = new ArrayList<>();
        String sql = "SELECT * FROM THUE WHERE trangThai = 1 ORDER BY maSoThue";
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { dsThue.add(createThueFromResultSet(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return dsThue;
    }

    public Thue getThueById(String maThue) {
        Thue thue = null;
        String sql = "SELECT * FROM THUE WHERE maSoThue = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maThue);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { thue = createThueFromResultSet(rs); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return thue;
    }
}