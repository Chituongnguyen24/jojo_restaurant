package dao;

import connectDB.ConnectDB;
import entity.KhuVuc;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhuVuc_DAO {

    // Helper để tạo đối tượng KhuVuc từ ResultSet
    private KhuVuc createKhuVucFromResultSet(ResultSet rs) throws SQLException {
        return new KhuVuc(
            rs.getString("maKhuVuc"),
            rs.getString("tenKhuVuc"),
            rs.getString("moTa"),
            rs.getBoolean("trangThai")
        );
    }
    
    public List<KhuVuc> getAllKhuVuc() {
        List<KhuVuc> ds = new ArrayList<>();
        String sql = "SELECT * FROM KHUVUC";

        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ds.add(createKhuVucFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
    
    // Phương thức lấy KhuVuc theo mã
    public KhuVuc getKhuVucByMa(String maKhuVuc) {
        String sql = "SELECT * FROM KHUVUC WHERE maKhuVuc = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, maKhuVuc);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createKhuVucFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}