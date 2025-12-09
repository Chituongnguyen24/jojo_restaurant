package dao;

import connectDB.ConnectDB;
import entity.MonAn;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonAn_DAO {


    private MonAn createMonAnFromResultSet(ResultSet rs) throws SQLException {
        return new MonAn(
            rs.getString("maMonAn").trim(),
            rs.getString("tenMonAn"),
            rs.getDouble("donGia"),
            rs.getBoolean("trangThai"),
            rs.getString("imagePath"),
            rs.getString("loaiMonAn") // Đảm bảo constructor MonAn có 6 tham số
        );
    }

    public List<MonAn> getAllMonAn() {
        List<MonAn> ds = new ArrayList<>();
        // Lấy tất cả, bao gồm cả các món đã vô hiệu hóa (trangThai = 0)
        String sql = "SELECT maMonAn, tenMonAn, donGia, trangThai, imagePath, loaiMonAn FROM MonAn ORDER BY maMonAn"; 

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ds.add(createMonAnFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
    
    /**
     * Lấy món ăn theo mã
     */
     public MonAn getMonAnTheoMa(String maMonAn) {
        String sql = "SELECT maMonAn, tenMonAn, donGia, trangThai, imagePath, loaiMonAn FROM MonAn WHERE maMonAn = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, maMonAn.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createMonAnFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cập nhật thông tin món ăn (dùng trong ChinhSuaMonAn_Dialog)
     */
    public boolean updateMonAn(MonAn monAn) {
        String sql = "UPDATE MonAn SET tenMonAn = ?, donGia = ?, trangThai = ?, imagePath = ?, loaiMonAn = ? WHERE maMonAn = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, monAn.getTenMonAn());
            stmt.setDouble(2, monAn.getDonGia());
            stmt.setBoolean(3, monAn.isTrangThai());
            stmt.setString(4, monAn.getImagePath());
            stmt.setString(5, monAn.getLoaiMonAn()); 
            stmt.setString(6, monAn.getMaMonAn().trim());  

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Vô hiệu hóa món ăn (Thay thế cho delete, cập nhật trạng thái thành Hết món)
     */
    public boolean disableMonAn(String maMonAn) {
        String sql = "UPDATE MonAn SET trangThai = 0 WHERE maMonAn = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maMonAn.trim());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Thêm món ăn mới 
     */
    public boolean themMonAn(MonAn monAn) {
        String sql = "INSERT INTO MonAn (maMonAn, tenMonAn, donGia, trangThai, imagePath, loaiMonAn) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, monAn.getMaMonAn().trim());
            stmt.setString(2, monAn.getTenMonAn());
            stmt.setDouble(3, monAn.getDonGia());
            stmt.setBoolean(4, monAn.isTrangThai());
            stmt.setString(5, monAn.getImagePath());
            stmt.setString(6, monAn.getLoaiMonAn()); 

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Tạo mã món ăn tự động
     */
    public String getMaMonAnTuDong() {
        String newID = "MA0001"; 
        String sql = "SELECT TOP 1 maMonAn FROM MonAn WHERE maMonAn LIKE 'MA[0-9][0-9][0-9][0-9]' ORDER BY maMonAn DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastID = rs.getString("maMonAn").trim();
                String numPart = lastID.substring(2); 
                int num = Integer.parseInt(numPart);
                num++; 
                newID = String.format("MA%04d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
             e.printStackTrace();
        }
        return newID;
    }
    
    /**
     * Tìm kiếm và lọc món ăn
     */
    public List<MonAn> searchMonAn(String keyword, String trangThaiFilter, String loaiMonAnFilter) {
        List<MonAn> ds = new ArrayList<>();
        
        // Bỏ điều kiện lọc bằng tên món ăn, cho phép tìm kiếm rỗng
        StringBuilder sqlBuilder = new StringBuilder("SELECT maMonAn, tenMonAn, donGia, trangThai, imagePath, loaiMonAn FROM MonAn WHERE 1=1");
        List<Object> params = new ArrayList<>();

        // Lọc theo Từ khóa (Tên món ăn)
        if (keyword != null && !keyword.isEmpty()) {
            sqlBuilder.append(" AND tenMonAn LIKE ?");
            params.add("%" + keyword + "%"); 
        }

        // Lọc theo Trạng thái
        Boolean trangThaiValue = null;
        if ("Còn bán".equals(trangThaiFilter)) {
            trangThaiValue = true;
        } else if ("Hết món".equals(trangThaiFilter) || "Hết".equals(trangThaiFilter)) {
            trangThaiValue = false;
        }

        if (trangThaiValue != null) {
            sqlBuilder.append(" AND trangThai = ?");
            params.add(trangThaiValue); 
        }
        
        // LỌC THEO LOẠI MÓN ĂN
        if (loaiMonAnFilter != null && !loaiMonAnFilter.equalsIgnoreCase("Tất cả")) {
            sqlBuilder.append(" AND loaiMonAn = ?");
            params.add(loaiMonAnFilter);
        }

        sqlBuilder.append(" ORDER BY maMonAn"); 

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sqlBuilder.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(createMonAnFromResultSet(rs)); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
    
    /**
     * Lấy danh sách các loại món ăn duy nhất (để điền vào ComboBox)
     */
    public List<String> getUniqueLoaiMonAn() {
        List<String> loaiMonAnList = new ArrayList<>();
        String sql = "SELECT DISTINCT loaiMonAn FROM MonAn WHERE loaiMonAn IS NOT NULL ORDER BY loaiMonAn";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                // Đảm bảo trim() nếu loaiMonAn là NCHAR
                loaiMonAnList.add(rs.getString("loaiMonAn").trim()); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loaiMonAnList;
    }
}
