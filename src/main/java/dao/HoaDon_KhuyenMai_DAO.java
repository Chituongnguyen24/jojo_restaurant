package dao;

import connectDB.ConnectDB;
import entity.KhuyenMai;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_KhuyenMai_DAO {

    private KhuyenMai createKhuyenMaiFromResultSet(ResultSet rs) throws SQLException {
        String maKM = rs.getString("maKhuyenMai");
        String tenKM = rs.getString("tenKhuyenMai");
        double giaTri = rs.getDouble("giaTri");
        LocalDate ngayBD = rs.getDate("thoiGianBatDau").toLocalDate();
        LocalDate ngayKT = rs.getDate("thoiGianKetThuc").toLocalDate();
        LocalDate today = LocalDate.now();
        String trangThai;
        if (maKM.equals("KM00000000")) { trangThai = "Luôn áp dụng"; }
        else if (today.isBefore(ngayBD)) { trangThai = "Chưa bắt đầu"; }
        else if (today.isAfter(ngayKT)) { trangThai = "Đã kết thúc"; }
        else { trangThai = "Đang áp dụng"; }
        return new KhuyenMai(maKM, tenKM, giaTri, ngayBD, ngayKT, trangThai);
    }

    public List<KhuyenMai> getAllKhuyenMai() {
        List<KhuyenMai> dsKhuyenMai = new ArrayList<>();
        String sql = "SELECT * FROM KHUYENMAI ORDER BY CASE WHEN maKhuyenMai = 'KM00000000' THEN 0 ELSE 1 END, thoiGianBatDau DESC";
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) { dsKhuyenMai.add(createKhuyenMaiFromResultSet(rs)); }
        } catch (SQLException e) { e.printStackTrace(); }
        return dsKhuyenMai;
    }

    public KhuyenMai getKhuyenMaiById(String maKM) {
        KhuyenMai km = null;
        String sql = "SELECT * FROM KHUYENMAI WHERE maKhuyenMai = ?";
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
        String sql = "INSERT INTO KHUYENMAI (maKhuyenMai, tenKhuyenMai, giaTri, thoiGianBatDau, thoiGianKetThuc) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, km.getMaKM());
            pstmt.setString(2, km.getTenKM());
            pstmt.setDouble(3, km.getGiaTri());
            pstmt.setDate(4, Date.valueOf(km.getNgayBatDau()));
            pstmt.setDate(5, Date.valueOf(km.getNgayKetThuc()));
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
        String sql = "SELECT * FROM KHUYENMAI WHERE maKhuyenMai LIKE ? OR tenKhuyenMai LIKE ? ORDER BY CASE WHEN maKhuyenMai = 'KM00000000' THEN 0 ELSE 1 END, thoiGianBatDau DESC";
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
        String sql = "DELETE FROM KHUYENMAI WHERE maKhuyenMai = ?";
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
        String sql = "UPDATE KHUYENMAI SET tenKhuyenMai = ?, giaTri = ?, thoiGianBatDau = ?, thoiGianKetThuc = ? WHERE maKhuyenMai = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, khuyenMai.getTenKM());
            pstmt.setDouble(2, khuyenMai.getGiaTri());
            pstmt.setDate(3, Date.valueOf(khuyenMai.getNgayBatDau()));
            pstmt.setDate(4, Date.valueOf(khuyenMai.getNgayKetThuc()));
            pstmt.setString(5, khuyenMai.getMaKM());
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
	}
}