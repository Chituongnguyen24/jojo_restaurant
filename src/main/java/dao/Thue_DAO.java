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
        // FIX: Chuẩn hóa cách gọi Connection
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dsThue.add(createThueFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsThue;
    }

    public List<Thue> getAllActiveTaxes() {
        List<Thue> dsThue = new ArrayList<>();
        String sql = "SELECT * FROM THUE WHERE trangThai = 1 ORDER BY maSoThue";
        // FIX: Chuẩn hóa cách gọi Connection
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                dsThue.add(createThueFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsThue;
    }

    /**
     * Tìm thuế theo mã
     */
    public Thue getThueById(String maThue) {
        Thue thue = null;
        String sql = "SELECT * FROM THUE WHERE maSoThue = ?";
        // FIX: Chuẩn hóa cách gọi Connection
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maThue);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    thue = createThueFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return thue;
    }

    /**
     * [HÀM MỚI] Thêm một loại thuế mới vào CSDL
     */
    public boolean addThue(Thue thue) {
        String sql = "INSERT INTO THUE (maSoThue, tenThue, tyLeThue, moTa, trangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, thue.getMaSoThue());
            pstmt.setString(2, thue.getTenThue());
            pstmt.setDouble(3, thue.getTyLeThue());
            pstmt.setString(4, thue.getMoTa());
            pstmt.setBoolean(5, thue.isTrangThai());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateThue(Thue thue) {
        String sql = "UPDATE THUE SET tenThue = ?, tyLeThue = ?, moTa = ?, trangThai = ? WHERE maSoThue = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, thue.getTenThue());
            pstmt.setDouble(2, thue.getTyLeThue());
            pstmt.setString(3, thue.getMoTa());
            pstmt.setBoolean(4, thue.isTrangThai());
            pstmt.setString(5, thue.getMaSoThue()); 
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Thue getThueMacDinh() {
        
        return getThueById("VAT08"); 
    }
}
