package dao;

import connectDB.ConnectDB;
import entity.MonAn;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonAn_DAO {

    /**
     * Hàm trợ giúp để tạo đối tượng MonAn từ ResultSet
     */
    private MonAn createMonAnFromResultSet(ResultSet rs) throws SQLException {
        // Cần đảm bảo rằng cột "loaiMonAn" tồn tại trong ResultSet
        return new MonAn(
            rs.getString("maMonAn"),
            rs.getString("tenMonAn"),
            rs.getDouble("donGia"),
            rs.getBoolean("trangThai"),
            rs.getString("imagePath"),
            rs.getString("loaiMonAn") // === THÊM MỚI: LoaiMonAn ===
        );
    }

    /**
     * Lấy tất cả món ăn
     */
    public List<MonAn> getAllMonAn() {
        List<MonAn> ds = new ArrayList<>();
        // === CẬP NHẬT SQL: Thêm imagePath và loaiMonAn ===
        String sql = "SELECT maMonAn, tenMonAn, donGia, trangThai, imagePath, loaiMonAn FROM MonAn"; 

        try (Connection con = ConnectDB.getInstance().getConnection();
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
        // === CẬP NHẬT SQL: Thêm imagePath và loaiMonAn ===
        String sql = "SELECT maMonAn, tenMonAn, donGia, trangThai, imagePath, loaiMonAn FROM MonAn WHERE maMonAn = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, maMonAn);
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
        // === CẬP NHẬT SQL: Thêm imagePath và loaiMonAn ===
        String sql = "UPDATE MonAn SET tenMonAn = ?, donGia = ?, trangThai = ?, imagePath = ?, loaiMonAn = ? WHERE maMonAn = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, monAn.getTenMonAn());
            stmt.setDouble(2, monAn.getDonGia());
            stmt.setBoolean(3, monAn.isTrangThai());
            stmt.setString(4, monAn.getImagePath());
            stmt.setString(5, monAn.getLoaiMonAn()); // === THÊM MỚI ===
            stmt.setString(6, monAn.getMaMonAn());  

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa món ăn (dùng trong ChinhSuaMonAn_Dialog)
     */
    public boolean deleteMonAn(String maMonAn) {
        String sql = "DELETE FROM MonAn WHERE maMonAn = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maMonAn);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Thêm món ăn mới (quan trọng nếu bạn có chức năng thêm)
     */
    public boolean themMonAn(MonAn monAn) {
        // === CẬP NHẬT SQL: Thêm imagePath và loaiMonAn ===
        String sql = "INSERT INTO MonAn (maMonAn, tenMonAn, donGia, trangThai, imagePath, loaiMonAn) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, monAn.getMaMonAn());
            stmt.setString(2, monAn.getTenMonAn());
            stmt.setDouble(3, monAn.getDonGia());
            stmt.setBoolean(4, monAn.isTrangThai());
            stmt.setString(5, monAn.getImagePath());
            stmt.setString(6, monAn.getLoaiMonAn()); // === THÊM MỚI ===

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
        String sql = "SELECT TOP 1 maMonAn FROM MonAn ORDER BY maMonAn DESC";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastID = rs.getString("maMonAn");
                String numPart = lastID.trim().substring(2); 
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
        StringBuilder sqlBuilder = new StringBuilder("SELECT maMonAn, tenMonAn, donGia, trangThai, imagePath, loaiMonAn FROM MonAn WHERE tenMonAn LIKE ?");
        List<Object> params = new ArrayList<>();
        params.add("%" + keyword + "%"); 

        Boolean trangThaiValue = null;
        if ("Còn bán".equals(trangThaiFilter)) {
            trangThaiValue = true;
        } else if ("Hết".equals(trangThaiFilter)) {
            trangThaiValue = false;
        }

        if (trangThaiValue != null) {
            sqlBuilder.append(" AND trangThai = ?");
            params.add(trangThaiValue); 
        }
        
        // === LỌC THEO LOẠI MÓN ĂN ===
        if (loaiMonAnFilter != null && !loaiMonAnFilter.equalsIgnoreCase("Tất cả")) {
            sqlBuilder.append(" AND loaiMonAn = ?");
            params.add(loaiMonAnFilter);
        }

        sqlBuilder.append(" ORDER BY maMonAn"); 

        try (Connection con = ConnectDB.getInstance().getConnection();
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
        try (Connection con = ConnectDB.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                loaiMonAnList.add(rs.getString("loaiMonAn"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loaiMonAnList;
    }
}