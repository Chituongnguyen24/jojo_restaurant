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
        return new MonAn(
            rs.getString("maMonAn"),
            rs.getString("tenMonAn"),
            rs.getDouble("donGia"),
            rs.getBoolean("trangThai"),
            rs.getString("imagePath") // === THÊM MỚI ===
        );
    }

    /**
     * Lấy tất cả món ăn
     */
    public List<MonAn> getAllMonAn() {
        List<MonAn> ds = new ArrayList<>();
        // === CẬP NHẬT SQL: Thêm imagePath ===
        String sql = "SELECT maMonAn, tenMonAn, donGia, trangThai, imagePath FROM MonAn"; 

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                ds.add(createMonAnFromResultSet(rs)); // Dùng hàm trợ giúp
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
        // === CẬP NHẬT SQL: Thêm imagePath ===
        String sql = "SELECT maMonAn, tenMonAn, donGia, trangThai, imagePath FROM MonAn WHERE maMonAn = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, maMonAn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createMonAnFromResultSet(rs); // Dùng hàm trợ giúp
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cập nhật thông tin món ăn (được dùng trong ChinhSuaMonAn_Dialog)
     */
    public boolean updateMonAn(MonAn monAn) {
        // === CẬP NHẬT SQL: Thêm imagePath ===
        String sql = "UPDATE MonAn SET tenMonAn = ?, donGia = ?, trangThai = ?, imagePath = ? WHERE maMonAn = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, monAn.getTenMonAn());
            stmt.setDouble(2, monAn.getDonGia());
            stmt.setBoolean(3, monAn.isTrangThai());
            stmt.setString(4, monAn.getImagePath()); // === THÊM MỚI ===
            stmt.setString(5, monAn.getMaMonAn());  // === ĐỔI VỊ TRÍ ===

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa món ăn (được dùng trong ChinhSuaMonAn_Dialog)
     */
    public boolean deleteMonAn(String maMonAn) {
        String sql = "DELETE FROM MonAn WHERE maMonAn = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maMonAn);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            // Bạn có thể gặp lỗi Foreign Key ở đây nếu món ăn đã có trong hóa đơn
            // Cần xử lý lỗi này ở giao diện
        }
        return false;
    }
    
    /**
     * Thêm món ăn mới (quan trọng nếu bạn có chức năng thêm)
     */
    public boolean themMonAn(MonAn monAn) {
        // === CẬP NHẬT SQL: Thêm imagePath ===
        String sql = "INSERT INTO MonAn (maMonAn, tenMonAn, donGia, trangThai, imagePath) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, monAn.getMaMonAn());
            stmt.setString(2, monAn.getTenMonAn());
            stmt.setDouble(3, monAn.getDonGia());
            stmt.setBoolean(4, monAn.isTrangThai());
            stmt.setString(5, monAn.getImagePath()); // === THÊM MỚI ===

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Lỗi trùng mã
        }
        return false;
    }
    /**
     * === HÀM ĐÃ SỬA LỖI ===
     * Tạo mã món ăn tự động (ví dụ: MA0015 -> MA0016)
     */
    public String getMaMonAnTuDong() {
        String newID = "MA0001"; // ID mặc định nếu bảng trống
        String sql = "SELECT TOP 1 maMonAn FROM MonAn ORDER BY maMonAn DESC";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                String lastID = rs.getString("maMonAn");
                
                // === SỬA LỖI Ở ĐÂY: Thêm .trim() ===
                // Lấy phần số (ví dụ: "MA0015    " -> "MA0015" -> "0015")
                String numPart = lastID.trim().substring(2); 
                // ===================================
                
                int num = Integer.parseInt(numPart);
                num++; // Tăng lên 1
                
                // Định dạng lại thành 4 chữ số (ví dụ: 16 -> "0016")
                newID = String.format("MA%04d", num);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            // Xử lý nếu ID cuối cùng không phải là "MAxxxx" (vd: bị lỗi dữ liệu)
            e.printStackTrace();
            // newID vẫn là "MA0001"
        }
        return newID;
    }
}