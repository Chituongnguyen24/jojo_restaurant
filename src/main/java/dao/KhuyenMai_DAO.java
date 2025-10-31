package dao;

import connectDB.ConnectDB;
import entity.KhuyenMai;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMai_DAO {

    private KhuyenMai createKhuyenMaiFromResultSet(ResultSet rs) throws SQLException {
        // Lấy các trường mới từ DB (giả định đã tồn tại trong bảng KHUYENMAI)
        Boolean trangThaiKM = rs.getBoolean("trangThaiKM");
        String loaiKM = rs.getString("LoaiKM");
        
        // Lấy các trường cũ
        String maKM = rs.getString("MaKM");
        String moTa = rs.getString("MoTa"); // SỬA: Đổi tenKhuyenMai thành MoTa
        double mucKM = rs.getDouble("MucKM"); // SỬA: Đổi giaTri thành MucKM
        LocalDate ngayBD = rs.getDate("NgayApDung").toLocalDate(); // SỬA: Đổi thoiGianBatDau thành NgayApDung
        LocalDate ngayKT = rs.getDate("NgayHetHan").toLocalDate(); // SỬA: Đổi thoiGianKetThuc thành NgayHetHan
        
        // LƯU Ý: KhuyenMai Entity cũ bạn gửi dùng tên trường khác nhau so với SQL, tôi đã ánh xạ:
        // Entity: tenKM -> MoTa (SQL), giaTri -> MucKM (SQL), NgayBatDau -> NgayApDung (SQL), NgayKetThuc -> NgayHetHan (SQL)
        
        return new KhuyenMai(maKM, moTa, ngayBD, ngayKT, mucKM, trangThaiKM, loaiKM);
    }

    public List<KhuyenMai> getAllKhuyenMai() {
        List<KhuyenMai> dsKhuyenMai = new ArrayList<>();
        // SỬA SQL: Thêm trangThaiKM và LoaiKM
        String sql = "SELECT MaKM, MoTa, MucKM, NgayApDung, NgayHetHan, trangThaiKM, LoaiKM FROM KHUYENMAI ORDER BY CASE WHEN MaKM = 'KM00000000' THEN 0 ELSE 1 END, NgayApDung DESC";
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { dsKhuyenMai.add(createKhuyenMaiFromResultSet(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return dsKhuyenMai;
    }

    public KhuyenMai getKhuyenMaiById(String maKM) {
        KhuyenMai km = null;
        // SỬA SQL: Thêm trangThaiKM và LoaiKM
        String sql = "SELECT MaKM, MoTa, MucKM, NgayApDung, NgayHetHan, trangThaiKM, LoaiKM FROM KHUYENMAI WHERE MaKM = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maKM);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { km = createKhuyenMaiFromResultSet(rs); }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return km;
    }

	public boolean insertKhuyenMai(KhuyenMai km) {
        // SỬA SQL: Thêm trangThaiKM và LoaiKM
        String sql = "INSERT INTO KHUYENMAI (MaKM, MoTa, MucKM, NgayApDung, NgayHetHan, trangThaiKM, LoaiKM) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, km.getMaKM());
            pstmt.setString(2, km.getMoTa());
            pstmt.setDouble(3, km.getMucKM());
            pstmt.setDate(4, Date.valueOf(km.getNgayApDung()));
            pstmt.setDate(5, Date.valueOf(km.getNgayHetHan()));
            pstmt.setBoolean(6, km.getTrangThaiKM() != null ? km.getTrangThaiKM() : true); // Mặc định là True
            pstmt.setString(7, km.getLoaiKM());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
	}

	public List<KhuyenMai> findKhuyenMai(String keyword) {
        List<KhuyenMai> dsKhuyenMai = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllKhuyenMai();
        }
        // SỬA SQL: Thêm trangThaiKM và LoaiKM (MoTa tương ứng với tên chương trình)
        String sql = "SELECT MaKM, MoTa, MucKM, NgayApDung, NgayHetHan, trangThaiKM, LoaiKM FROM KHUYENMAI WHERE MaKM LIKE ? OR MoTa LIKE ? ORDER BY CASE WHEN MaKM = 'KM00000000' THEN 0 ELSE 1 END, NgayApDung DESC";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword.trim() + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dsKhuyenMai.add(createKhuyenMaiFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsKhuyenMai;
	}

	public boolean deleteKhuyenMai(String maKM) {
        String sql = "DELETE FROM KHUYENMAI WHERE MaKM = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maKM);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
	}

	public boolean updateKhuyenMai(KhuyenMai khuyenMai) {
        // SỬA SQL: Thêm trangThaiKM và LoaiKM
        String sql = "UPDATE KHUYENMAI SET MoTa = ?, MucKM = ?, NgayApDung = ?, NgayHetHan = ?, trangThaiKM = ?, LoaiKM = ? WHERE MaKM = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, khuyenMai.getMoTa());
            pstmt.setDouble(2, khuyenMai.getMucKM());
            pstmt.setDate(3, Date.valueOf(khuyenMai.getNgayApDung()));
            pstmt.setDate(4, Date.valueOf(khuyenMai.getNgayHetHan()));
            pstmt.setBoolean(5, khuyenMai.getTrangThaiKM() != null ? khuyenMai.getTrangThaiKM() : true);
            pstmt.setString(6, khuyenMai.getLoaiKM());
            pstmt.setString(7, khuyenMai.getMaKM());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
	}
    
    /**
     * Lấy danh sách các loại khuyến mãi duy nhất (để điền vào ComboBox)
     */
    public List<String> getUniqueLoaiKhuyenMai() {
        List<String> loaiKMList = new ArrayList<>();
        String sql = "SELECT DISTINCT LoaiKM FROM KHUYENMAI WHERE LoaiKM IS NOT NULL ORDER BY LoaiKM";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                loaiKMList.add(rs.getString("LoaiKM"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return loaiKMList;
    }
}