package dao;

import entity.KhachHang;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;

public class KhachHang_DAO {

    private KhachHang createKhachHangFromResultSet(ResultSet rs) throws SQLException {
        LocalDate ngaySinh = rs.getDate("NgaySinh") != null ? rs.getDate("NgaySinh").toLocalDate() : null;

        return new KhachHang(
            rs.getString("MaKH"),
            rs.getString("TenKH"),
            rs.getString("SoDienThoai"),
            rs.getString("Email"),
            ngaySinh,
            rs.getInt("DiemTichLuy"),
            rs.getBoolean("LaThanhVien")
        );
    }
    
    public List<KhachHang> getAllKhachHang() {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KHACHHANG WHERE LaThanhVien = 1"; 
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ds.add(createKhachHangFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean insertKhachHang(KhachHang kh) {
        String sql = "INSERT INTO KHACHHANG(MaKH, TenKH, SoDienThoai, Email, NgaySinh, DiemTichLuy, LaThanhVien) VALUES (?, ?, ?, ?, ?, ?, 1)"; 
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, kh.getMaKH()); // SỬA: getMaKH()
            stmt.setString(2, kh.getTenKH()); // SỬA: getTenKH()
            stmt.setString(3, kh.getSoDienThoai()); // SỬA: getSoDienThoai()
            stmt.setString(4, kh.getEmail());
            stmt.setDate(5, kh.getNgaySinh() != null ? Date.valueOf(kh.getNgaySinh()) : null);
            stmt.setInt(6, kh.getDiemTichLuy());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateKhachHang(KhachHang kh) {
        String sql = "UPDATE KHACHHANG SET TenKH=?, SoDienThoai=?, Email=?, NgaySinh=?, DiemTichLuy=?, LaThanhVien=? WHERE MaKH=?"; 
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, kh.getTenKH()); // SỬA: getTenKH()
            stmt.setString(2, kh.getSoDienThoai()); // SỬA: getSoDienThoai()
            stmt.setString(3, kh.getEmail());
            stmt.setDate(4, kh.getNgaySinh() != null ? Date.valueOf(kh.getNgaySinh()) : null);
            stmt.setInt(5, kh.getDiemTichLuy());
            stmt.setBoolean(6, kh.isLaThanhVien());
            stmt.setString(7, kh.getMaKH()); // SỬA: getMaKH()

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // "Xóa" khách hàng: Chuyển LaThanhVien = 0
    public boolean deleteKhachHang(String maKH) {
        String sql = "UPDATE KHACHHANG SET LaThanhVien = 0 WHERE MaKH = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maKH);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<KhachHang> findKhachHang(String keyword) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KHACHHANG WHERE (TenKH LIKE ? OR SoDienThoai LIKE ? OR Email LIKE ?) AND LaThanhVien = 1"; 

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");
            stmt.setString(3, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ds.add(createKhachHangFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public KhachHang getKhachHangById(String maKH) {
        String sql = "SELECT * FROM KHACHHANG WHERE MaKH = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maKH);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return createKhachHangFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // 8. Cập nhật điểm tích lũy 
    public void capNhatDiemTichLuy(String maKH, double tongTienThanhToan) {
        int diemMoi = (int) (tongTienThanhToan / 50000); 
        KhachHang kh = getKhachHangById(maKH);
        if (kh != null) {
            int tongDiem = kh.getDiemTichLuy() + diemMoi;
            kh.setDiemTichLuy(tongDiem);
            updateKhachHang(kh); 
        }
    }

    // 9. Xếp hạng từ điểm
    public String xepHangKhachHang(int diem) {
        if (diem < 200) return "Đồng";
        else if (diem < 450) return "Bạc";
        else return "Vàng";
    }
    
    // 10. Ưu đãi từ hạng (KHÔNG THAY ĐỔI)
    public double getUuDaiTheoHang(String hang, boolean laSinhNhat) {
        if (hang.equals("Đồng")) return laSinhNhat ? 0.10 : 0.05;
        if (hang.equals("Bạc")) return laSinhNhat ? 0.15 : 0.10;
        return laSinhNhat ? 0.20 : 0.15;
    }

    public List<KhachHang> findByPhone(String sdt) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KHACHHANG WHERE SoDienThoai LIKE ? AND LaThanhVien = 1";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, "%" + sdt + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ds.add(createKhachHangFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
    
    public int getSoLuongKhachDaDatHang() {
        String sql = "SELECT COUNT(DISTINCT MaKH) AS SoLuong FROM HOADON WHERE DaThanhToan = 1 AND MaKH IS NOT NULL";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next())
                return rs.getInt("SoLuong");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    
    public int getSoLuongKhachDaDatHang(java.util.Date from, java.util.Date to) {
        if (from == null || to == null) {
            return getSoLuongKhachDaDatHang();
        }
        
        String sql = "SELECT COUNT(DISTINCT MaKH) AS SoLuong FROM HOADON " +
                     "WHERE DaThanhToan = 1 AND MaKH IS NOT NULL AND NgayLapHoaDon BETWEEN ? AND ?";
                     
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

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
}