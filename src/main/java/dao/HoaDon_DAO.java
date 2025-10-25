package dao;

import connectDB.ConnectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HoaDon_DAO {

    public List<HoaDon> getAllHoaDon() {
        List<HoaDon> dsHoaDon = new ArrayList<>();
        String sql = "SELECT * FROM HOADON ORDER BY ngayLap DESC, gioVao DESC";

        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String maHD = rs.getString("maHoaDon");
                LocalDate ngayLap = rs.getDate("ngayLap").toLocalDate();
                LocalDateTime gioVao = rs.getTimestamp("gioVao").toLocalDateTime();
                LocalDateTime gioRa = rs.getTimestamp("gioRa").toLocalDateTime();
                String phuongThuc = rs.getString("phuongThuc");
                boolean daThanhToan = rs.getBoolean("daThanhToan");

                KhachHang kh = new KhachHang(rs.getString("maKhachHang"));
                NhanVien nv = new NhanVien(rs.getString("maNhanVien"));
                Thue thue = new Thue(rs.getString("maThue"));

                String maPhieu = rs.getString("maPhieu");
                PhieuDatBan pdb = (maPhieu != null) ? new PhieuDatBan(maPhieu) : null;

                String maKM = rs.getString("maKhuyenMai");
                KhuyenMai km = (maKM != null) ? new KhuyenMai(maKM) : null;

                HoaDon hd = new HoaDon(maHD, kh, nv, pdb, km, thue, ngayLap, gioVao, gioRa, phuongThuc, daThanhToan);
                dsHoaDon.add(hd);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dsHoaDon;
    }

    public boolean addHoaDon(HoaDon hd) {
        String sql =
            "INSERT INTO HOADON(maHoaDon, maKhachHang, ngayLap, phuongThuc, maKhuyenMai, "+
            "                 maThue, gioVao, gioRa, maNhanVien, maPhieu, daThanhToan)"+
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hd.getMaHoaDon());
            pstmt.setString(2, hd.getKhachHang().getMaKhachHang());
            pstmt.setDate(3, Date.valueOf(hd.getNgayLap()));
            pstmt.setString(4, hd.getPhuongThuc());

            if (hd.getKhuyenMai() != null)
                pstmt.setString(5, hd.getKhuyenMai().getMaKM());
            else
                pstmt.setNull(5, Types.NCHAR);

            pstmt.setString(6, hd.getThue().getMaThue());
            pstmt.setTimestamp(7, Timestamp.valueOf(hd.getGioVao()));
            pstmt.setTimestamp(8, Timestamp.valueOf(hd.getGioRa()));
            pstmt.setString(9, hd.getNhanVien().getMaNV());

            if (hd.getPhieuDatBan() != null)
                pstmt.setString(10, hd.getPhieuDatBan().getMaPhieu());
            else
                pstmt.setNull(10, Types.NCHAR);

            pstmt.setBoolean(11, hd.isDaThanhToan());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateHoaDon(HoaDon hd) {
        String sql = 
            "UPDATE HOADON"+
            "SET maKhachHang = ?, ngayLap = ?, phuongThuc = ?, maKhuyenMai = ?, "+
            "    maThue = ?, gioVao = ?, gioRa = ?, maNhanVien = ?, maPhieu = ?, daThanhToan = ?"+
            "WHERE maHoaDon = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hd.getKhachHang().getMaKhachHang());
            pstmt.setDate(2, Date.valueOf(hd.getNgayLap()));
            pstmt.setString(3, hd.getPhuongThuc());

            if (hd.getKhuyenMai() != null)
                pstmt.setString(4, hd.getKhuyenMai().getMaKM());
            else
                pstmt.setNull(4, Types.NCHAR);

            pstmt.setString(5, hd.getThue().getMaThue());
            pstmt.setTimestamp(6, Timestamp.valueOf(hd.getGioVao()));
            pstmt.setTimestamp(7, Timestamp.valueOf(hd.getGioRa()));
            pstmt.setString(8, hd.getNhanVien().getMaNV());

            if (hd.getPhieuDatBan() != null)
                pstmt.setString(9, hd.getPhieuDatBan().getMaPhieu());
            else
                pstmt.setNull(9, Types.NCHAR);

            pstmt.setBoolean(10, hd.isDaThanhToan());
            pstmt.setString(11, hd.getMaHoaDon());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteHoaDon(String maHD) {
        String sqlDeleteChiTiet = "DELETE FROM CHITIETHOADON WHERE maHoaDon = ?";
        String sqlDeleteHoaDon = "DELETE FROM HOADON WHERE maHoaDon = ?";

        try (Connection conn = ConnectDB.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlDeleteChiTiet);
                 PreparedStatement ps2 = conn.prepareStatement(sqlDeleteHoaDon)) {

                ps1.setString(1, maHD);
                ps1.executeUpdate();

                ps2.setString(1, maHD);
                int affected = ps2.executeUpdate();

                conn.commit();
                return affected > 0;

            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateTrangThaiThanhToan(String maHoaDon, boolean daThanhToan) {
        String sql = "UPDATE HOADON SET daThanhToan = ? WHERE maHoaDon = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBoolean(1, daThanhToan);
            pstmt.setString(2, maHoaDon);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public double getTongDoanhThu() {
        String sql = 
            " SELECT SUM(CAST(cthd.soLuong AS DECIMAL(18, 2)) * cthd.donGia) AS TongDoanhThu" +
            " FROM HOADON hd INNER JOIN CHITIETHOADON cthd ON hd.maHoaDon = cthd.maHoaDon" +
            " WHERE hd.daThanhToan = 1";
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next())
                return rs.getDouble("TongDoanhThu");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTongDonHang() {
        String sql = "SELECT COUNT(*) AS SoLuong FROM HOADON WHERE daThanhToan = 1";
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next())
                return rs.getInt("SoLuong");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getSoLuongKhachHang() {
        String sql = "SELECT COUNT(DISTINCT maKhachHang) AS SoLuong FROM HOADON WHERE daThanhToan = 1";
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next())
                return rs.getInt("SoLuong");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getDoanhThuTheoNgay(int ngayTruoc) {
        String sql = 
               " SELECT     SUM(CAST(cthd.soLuong AS DECIMAL(18, 2)) * cthd.donGia) AS DoanhThuTrongNgay"+
               " FROM      HOADON hd  INNER JOIN      CHITIETHOADON cthd ON hd.maHoaDon = cthd.maHoaDon"+
               " WHERE    hd.daThanhToan = 1 AND DATEDIFF(day, hd.ngayLap, GETDATE()) <= ?";
               
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ngayTruoc);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    
                    return rs.getDouble("DoanhThuTrongNgay");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double tinhTongTienHoaDon(String maHoaDon) {
        double tongTien = 0;

        String sqlChiTiet = 
            " SELECT SUM(CAST(ct.soLuong AS DECIMAL(18, 2)) * ct.donGia) AS Tong" +
            " FROM CHITIETHOADON ct" +
            " WHERE ct.maHoaDon = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt1 = conn.prepareStatement(sqlChiTiet)) {

            pstmt1.setString(1, maHoaDon);
            try (ResultSet rs = pstmt1.executeQuery()) {
                if (rs.next())
                    tongTien = rs.getDouble("Tong");
            }

            String sqlThueKM = 
                " SELECT t.tyLeThue, km.giaTri, km.tenKhuyenMai" +
                " FROM HOADON hd" +
                " LEFT JOIN Thue t ON hd.maThue = t.maSoThue" +
                " LEFT JOIN KhuyenMai km ON hd.maKhuyenMai = km.maKhuyenMai" +
                " WHERE hd.maHoaDon = ?";
            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlThueKM)) {
                pstmt2.setString(1, maHoaDon);
                try (ResultSet rs2 = pstmt2.executeQuery()) {
                    if (rs2.next()) {
                        double thue = rs2.getDouble("tyLeThue");
                        double giamGia = rs2.getDouble("giaTri");
                        if (giamGia > 0) {
                            // Nếu giảm giá < 1 → coi là % (0.1 = 10%)
                            // Nếu giảm giá >= 1 → coi là số tiền cố định (50000 VNĐ)
                            if (giamGia < 1) {
                                tongTien -= tongTien * giamGia;  // Giảm theo %
                            } else {
                                tongTien -= giamGia;  // Giảm số tiền cố định
                            }
                        }
                        
                        // SAU ĐÓ CỘNG THUẾ
                        if (thue > 0) {
                            tongTien += tongTien * thue / 100;
                        }
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tongTien;
    }
    
    public HoaDon findByMaHD(String maHD) {
        String sql = "SELECT * FROM HOADON WHERE maHoaDon = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maHD);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    LocalDate ngayLap = rs.getDate("ngayLap").toLocalDate();
                    LocalDateTime gioVao = rs.getTimestamp("gioVao").toLocalDateTime();
                    LocalDateTime gioRa = rs.getTimestamp("gioRa").toLocalDateTime();
                    String phuongThuc = rs.getString("phuongThuc");
                    boolean daThanhToan = rs.getBoolean("daThanhToan");

                    KhachHang kh = new KhachHang(rs.getString("maKhachHang"));
                    NhanVien nv = new NhanVien(rs.getString("maNhanVien"));
                    Thue thue = new Thue(rs.getString("maThue"));

                    String maPhieu = rs.getString("maPhieu");
                    PhieuDatBan pdb = (maPhieu != null) ? new PhieuDatBan(maPhieu) : null;

                    String maKM = rs.getString("maKhuyenMai");
                    KhuyenMai km = (maKM != null) ? new KhuyenMai(maKM) : null;

                    return new HoaDon(maHD, kh, nv, pdb, km, thue, ngayLap, gioVao, gioRa, phuongThuc, daThanhToan);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public double getTongDoanhThu(java.util.Date from, java.util.Date to) {
        if (from == null || to == null) {
            return getTongDoanhThu();
        }
        
        String sql = 
            " SELECT SUM(CAST(cthd.soLuong AS DECIMAL(18, 2)) * cthd.donGia) AS TongDoanhThu" +
            " FROM HOADON hd INNER JOIN CHITIETHOADON cthd ON hd.maHoaDon = cthd.maHoaDon" +
            " WHERE hd.daThanhToan = 1 AND hd.ngayLap BETWEEN ? AND ?";
            
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(from.getTime()));
            stmt.setDate(2, new java.sql.Date(to.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return rs.getDouble("TongDoanhThu");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTongDonHang(java.util.Date from, java.util.Date to) {
        if (from == null || to == null) {
            return getTongDonHang();
        }
        
        String sql = "SELECT COUNT(*) AS SoLuong FROM HOADON WHERE daThanhToan = 1 AND ngayLap BETWEEN ? AND ?";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(from.getTime()));
            stmt.setDate(2, new java.sql.Date(to.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt("SoLuong");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getSoLuongKhachHang(java.util.Date from, java.util.Date to) {
        if (from == null || to == null) {
            return getSoLuongKhachHang();
        }
        
        String sql = "SELECT COUNT(DISTINCT maKhachHang) AS SoLuong FROM HOADON " + 
                       "WHERE daThanhToan = 1 AND ngayLap BETWEEN ? AND ?";
                       
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(from.getTime()));
            stmt.setDate(2, new java.sql.Date(to.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt("SoLuong");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public Map<String, Double> getDoanhThuTheoKhoangThoiGian(java.util.Date from, java.util.Date to) {
        Map<String, Double> data = new LinkedHashMap<>();
        
        String sql = 
            " SELECT FORMAT(hd.ngayLap, 'dd/MM') AS Ngay, SUM(CAST(cthd.soLuong AS DECIMAL(18, 2)) * cthd.donGia) AS DoanhThu" +
            " FROM HOADON hd INNER JOIN CHITIETHOADON cthd ON hd.maHoaDon = cthd.maHoaDon" +
            " WHERE hd.daThanhToan = 1 AND hd.ngayLap BETWEEN ? AND ?" +
            " GROUP BY FORMAT(hd.ngayLap, 'dd/MM'), hd.ngayLap" +
            " ORDER BY hd.ngayLap";
            
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, new java.sql.Date(from.getTime()));
            stmt.setDate(2, new java.sql.Date(to.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    data.put(rs.getString("Ngay"), rs.getDouble("DoanhThu"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}