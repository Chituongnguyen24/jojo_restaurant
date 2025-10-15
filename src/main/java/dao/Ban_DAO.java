package dao;

import connectDB.ConnectDB;
import java.sql.*;
import java.util.*;

public class Ban_DAO {

 
    public Map<String, Integer> getSoBanTheoKhuVuc() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT maKhuVuc, COUNT(maBan) AS soBan FROM Ban GROUP BY maKhuVuc";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String maKhuVuc = rs.getString("maKhuVuc");
                int soBan = rs.getInt("soBan");
                map.put(maKhuVuc, soBan);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}
