package dao;

import entity.KhachHang;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;

public class KhachHang_DAO {

    // 1. Lấy tất cả khách hàng (chỉ thành viên: laThanhVien = 1)
    public List<KhachHang> getAllKhachHang() {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE laThanhVien = 1"; 
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("sdt"),
                        rs.getString("email"),
                        rs.getInt("diemTichLuy"),
                        rs.getBoolean("laThanhVien")
                );
                ds.add(kh);
            }

        } catch (SQLException e) {
            System.err.println("[DAO] Lỗi getAllKhachHang: " + e.getMessage()); 
            e.printStackTrace();
        }
        return ds;
    }

    // 2. Thêm khách hàng (mặc định laThanhVien = 1)
    public boolean insertKhachHang(KhachHang kh) {
        String sql = "INSERT INTO KhachHang(maKhachHang, tenKhachHang, sdt, email, diemTichLuy, laThanhVien) VALUES (?, ?, ?, ?, ?, 1)"; // Fix: =1
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, kh.getMaKhachHang());
            stmt.setString(2, kh.getTenKhachHang());
            stmt.setString(3, kh.getSdt());
            stmt.setString(4, kh.getEmail());
            stmt.setInt(5, kh.getDiemTichLuy());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] Lỗi insertKhachHang: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 3. Cập nhật khách hàng (giữ laThanhVien=1 cứng, không update)
    public boolean updateKhachHang(KhachHang kh) {
        String sql = "UPDATE KhachHang SET tenKhachHang=?, sdt=?, email=?, diemTichLuy=?, laThanhVien=1 WHERE maKhachHang=?"; // Fix: 5 placeholders, laThanhVien=1 cứng
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, kh.getTenKhachHang());
            stmt.setString(2, kh.getSdt());
            stmt.setString(3, kh.getEmail());
            stmt.setInt(4, kh.getDiemTichLuy());
            stmt.setString(5, kh.getMaKhachHang()); // Fix: Chỉ set đến 5, bỏ setBoolean

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] Lỗi updateKhachHang: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 4. "Xóa" khách hàng: Chuyển laThanhVien = 0 (ẩn khỏi view)
    public boolean deleteKhachHang(String maKH) {
        String sql = "UPDATE KhachHang SET laThanhVien = 0 WHERE maKhachHang = ?"; // Fix: =0 thay vì FALSE
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maKH);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[DAO] Lỗi deleteKhachHang: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 5. Tìm kiếm khách hàng (chỉ thành viên: laThanhVien = 1)
    public List<KhachHang> findKhachHang(String keyword) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE (tenKhachHang LIKE ? OR sdt LIKE ?) AND laThanhVien = 1"; // Fix: =1

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("sdt"),
                        rs.getString("email"),
                        rs.getInt("diemTichLuy"),
                        rs.getBoolean("laThanhVien")
                );
                ds.add(kh);
            }

        } catch (SQLException e) {
            System.err.println("[DAO] Lỗi findKhachHang: " + e.getMessage());
            e.printStackTrace();
        }
        return ds;
    }

    // 6. Lấy khách hàng theo mã (chỉ nếu laThanhVien = 1)
    public KhachHang getKhachHangById(String maKH) {
        String sql = "SELECT * FROM KhachHang WHERE maKhachHang = ? AND laThanhVien = 1"; // Fix: =1
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maKH);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("sdt"),
                        rs.getString("email"),
                        rs.getInt("diemTichLuy"),
                        rs.getBoolean("laThanhVien")
                );
            }

        } catch (SQLException e) {
            System.err.println("[DAO] Lỗi getKhachHangById: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // 8. Cập nhật điểm tích lũy
    public void capNhatDiemTichLuy(String maKH, double tongTienThanhToan) {
        int diemMoi = (int) (tongTienThanhToan / 50000); // 50k = 1 điểm
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

    // 10. Ưu đãi từ hạng
    public double getUuDaiTheoHang(String hang, boolean laSinhNhat) {
        if (hang.equals("Đồng")) return laSinhNhat ? 0.10 : 0.05;
        if (hang.equals("Bạc")) return laSinhNhat ? 0.15 : 0.10;
        return laSinhNhat ? 0.20 : 0.15;
    }

    // 11. Tìm khách hàng theo SĐT (chỉ thành viên: laThanhVien = 1)
    public List<KhachHang> findByPhone(String sdt) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE sdt LIKE ? AND laThanhVien = 1";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, "%" + sdt + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                // Tạo object thủ công, không dùng hàm chung
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("sdt"),
                        rs.getString("email"),
                        rs.getInt("diemTichLuy"),
                        rs.getBoolean("laThanhVien")
                );
                ds.add(kh);
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Lỗi findByPhone: " + e.getMessage());
            e.printStackTrace();
        }
        return ds;
    }
    
  //12. Lấy số lượng khách hàng (đã thanh toán) - Dùng cho thống kê
     
    public int getSoLuongKhachDaDatHang() {
        String sql = "SELECT COUNT(DISTINCT maKhachHang) AS SoLuong FROM HOADON WHERE daThanhToan = 1 AND maKhachHang IS NOT NULL";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next())
                return rs.getInt("SoLuong");

        } catch (SQLException e) {
            System.err.println("[DAO] Lỗi getSoLuongKhachDaDatHang: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    
    // 13. Lấy số lượng khách hàng (đã thanh toán) theo ngày - Dùng cho thống kê
    
    public int getSoLuongKhachDaDatHang(java.util.Date from, java.util.Date to) {
        if (from == null || to == null) {
            return getSoLuongKhachDaDatHang();
        }
        
        String sql = "SELECT COUNT(DISTINCT maKhachHang) AS SoLuong FROM HOADON " +
                     "WHERE daThanhToan = 1 AND maKhachHang IS NOT NULL AND ngayLap BETWEEN ? AND ?";
                     
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(from.getTime()));
            stmt.setDate(2, new java.sql.Date(to.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt("SoLuong");
            }

        } catch (SQLException e) {
            System.err.println("[DAO] Lỗi getSoLuongKhachDaDatHang(from, to): " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}