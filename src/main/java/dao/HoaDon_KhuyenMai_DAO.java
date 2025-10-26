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
}