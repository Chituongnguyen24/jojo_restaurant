package dao;

import connectDB.ConnectDB;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * HoaDon_DAO: lấy dữ liệu thống kê từ HoaDon + ChiTietHoaDon
 *
 * Lưu ý:
 * - Bảng HoaDon: có MaHD (MaHoaDon), NgayLap, MaKhachHang, ...
 * - Bảng ChiTietHoaDon: có MaHD, SoLuong, DonGia (hoặc Gia)
 * - Hàm getDoanhThuTheoNgay(ngay) nhận "ngay" theo dạng offset:
 *      1 -> hôm nay
 *      2 -> hôm qua
 *      ...
 *   và trả về doanh thu tính bằng "triệu VNĐ" (double).
 */
public class HoaDon_DAO {

    // Tổng doanh thu (tính từ ChiTietHoaDon: SUM(SoLuong * DonGia))
    public double getTongDoanhThu() {
        String sql = """
            SELECT SUM(ct.SoLuong * ct.DonGia) AS TongDoanhThu
            FROM ChiTietHoaDon ct
            JOIN HoaDon hd ON ct.MaHD = hd.MaHD
        """;
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("TongDoanhThu"); // trả về VNĐ
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Tổng số hóa đơn
    public int getTongDonHang() {
        String sql = "SELECT COUNT(DISTINCT MaHD) AS SoHoaDon FROM HoaDon";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("SoHoaDon");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Tổng số khách hàng (distinct MaKhachHang trong HoaDon, loại bỏ NULL/empty)
    public int getSoLuongKhachHang() {
        String sql = "SELECT COUNT(DISTINCT MaKhachHang) AS SoKhach FROM HoaDon WHERE MaKhachHang IS NOT NULL AND MaKhachHang <> ''";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("SoKhach");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Doanh thu theo ngày (theo offset)
     * @param ngay offset lấy theo: 1 = hôm nay, 2 = hôm qua, 3 = 2 ngày trước, ...
     * @return doanh thu theo ngày, đơn vị: triệu VNĐ (double)
     */
    public double getDoanhThuTheoNgay(int ngay) {
        // Tính ngày cần lấy: DATEADD(day, -ngay + 1, GETDATE())
        String sql = """
            SELECT SUM(ct.SoLuong * ct.DonGia) AS DoanhThu
            FROM ChiTietHoaDon ct
            JOIN HoaDon hd ON ct.MaHD = hd.MaHD
            WHERE CAST(hd.NgayLap AS DATE) = CAST(DATEADD(DAY, -? + 1, GETDATE()) AS DATE)
        """;
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, ngay);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double vnd = rs.getDouble("DoanhThu"); // VNĐ
                    // trả về "triệu VNĐ" cho phù hợp chart
                    return vnd / 1_000_000.0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Nếu cần: doanh thu theo khoảng ngày (trả Map<LocalDate, Double> bằng VNĐ)
    public Map<LocalDate, Double> getDoanhThuTheoKhoang(int daysBack) {
        Map<LocalDate, Double> map = new HashMap<>();
        String sql = """
            SELECT CAST(hd.NgayLap AS DATE) AS Ngay, SUM(ct.SoLuong * ct.DonGia) AS DoanhThu
            FROM HoaDon hd
            JOIN ChiTietHoaDon ct ON hd.MaHD = ct.MaHD
            WHERE CAST(hd.NgayLap AS DATE) >= CAST(DATEADD(DAY, -? + 1, GETDATE()) AS DATE)
            GROUP BY CAST(hd.NgayLap AS DATE)
            ORDER BY CAST(hd.NgayLap AS DATE)
        """;
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, daysBack);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    LocalDate ngay = rs.getDate("Ngay").toLocalDate();
                    double vnd = rs.getDouble("DoanhThu");
                    map.put(ngay, vnd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}
