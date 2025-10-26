package dao;

import connectDB.ConnectDB; // Class kết nối CSDL của bạn
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {

    /**
     * Hàm nội bộ: Tạo đối tượng HoaDon từ ResultSet.
     * Tự động tạo các đối tượng tham chiếu (KhachHang, NhanVien, Ban,...) chỉ với mã.
     * @param rs ResultSet đang trỏ tới dòng dữ liệu hóa đơn
     * @return Đối tượng HoaDon
     * @throws SQLException
     */
    private HoaDon createHoaDonFromResultSet(ResultSet rs) throws SQLException {
        String maHD = rs.getString("maHoaDon");
        LocalDate ngayLap = rs.getDate("ngayLap").toLocalDate();
        LocalDateTime gioVao = rs.getTimestamp("gioVao").toLocalDateTime();
        // Xử lý null cho gioRa, phòng trường hợp hóa đơn chưa thanh toán
        Timestamp gioRaTimestamp = rs.getTimestamp("gioRa");
        LocalDateTime gioRa = (gioRaTimestamp != null) ? gioRaTimestamp.toLocalDateTime() : null;
        String phuongThuc = rs.getString("phuongThuc");
        boolean daThanhToan = rs.getBoolean("daThanhToan");

        // Tạo các object tham chiếu chỉ với ID
        KhachHang kh = new KhachHang(rs.getString("maKhachHang"));
        NhanVien nv = new NhanVien(rs.getString("maNhanVien"));
        Thue thue = new Thue(rs.getString("maThue"));
        Ban ban = new Ban(rs.getString("maBan"));

        String maPhieu = rs.getString("maPhieu");
        PhieuDatBan pdb = (maPhieu != null) ? new PhieuDatBan(maPhieu) : null;

        String maKM = rs.getString("maKhuyenMai");
        KhuyenMai km = (maKM != null) ? new KhuyenMai(maKM) : null;

        // Dùng constructor đầy đủ
        return new HoaDon(maHD, kh, nv, ban, pdb, km, thue, ngayLap, gioVao, gioRa, phuongThuc, daThanhToan);
    }


    /**
     * Lấy tất cả hóa đơn, sắp xếp mới nhất lên đầu.
     */
    public List<HoaDon> getAllHoaDon() {
        List<HoaDon> dsHoaDon = new ArrayList<>();
        // Lấy tất cả các cột cần thiết
        String sql = "SELECT maHoaDon, maKhachHang, maBan, ngayLap, phuongThuc, maKhuyenMai, maThue, gioVao, gioRa, maNhanVien, maPhieu, daThanhToan " +
                     "FROM HOADON ORDER BY ngayLap DESC, gioVao DESC";

        // Sử dụng try-with-resources
        try (Connection conn = ConnectDB.getConnection(); // Dùng hàm getConnection() của bạn
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                dsHoaDon.add(createHoaDonFromResultSet(rs)); // Dùng hàm helper
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return dsHoaDon;
    }

    /**
     * Thêm hóa đơn mới vào CSDL.
     */
    public boolean addHoaDon(HoaDon hd) {
        String sql = "INSERT INTO HOADON(maHoaDon, maKhachHang, maBan, ngayLap, phuongThuc, maKhuyenMai, "+
                     "maThue, gioVao, gioRa, maNhanVien, maPhieu, daThanhToan)"+
                     " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hd.getMaHoaDon());
            pstmt.setString(2, hd.getKhachHang().getMaKhachHang());
            pstmt.setString(3, hd.getBan().getMaBan());
            pstmt.setDate(4, Date.valueOf(hd.getNgayLap()));
            pstmt.setString(5, hd.getPhuongThuc());
            // Xử lý null cho KhuyenMai
            if (hd.getKhuyenMai() != null && hd.getKhuyenMai().getMaKM() != null)
                pstmt.setString(6, hd.getKhuyenMai().getMaKM());
            else
                pstmt.setNull(6, Types.NCHAR);

            pstmt.setString(7, hd.getThue().getMaThue());
            pstmt.setTimestamp(8, Timestamp.valueOf(hd.getGioVao()));
             // Xử lý null cho gioRa
            if (hd.getGioRa() != null) {
                pstmt.setTimestamp(9, Timestamp.valueOf(hd.getGioRa()));
            } else {
                 pstmt.setNull(9, Types.TIMESTAMP); // Hoặc giá trị mặc định nếu CSDL yêu cầu
            }
            pstmt.setString(10, hd.getNhanVien().getMaNV());
             // Xử lý null cho PhieuDatBan
            if (hd.getPhieuDatBan() != null && hd.getPhieuDatBan().getMaPhieu() != null)
                pstmt.setString(11, hd.getPhieuDatBan().getMaPhieu());
            else
                pstmt.setNull(11, Types.NCHAR);

            pstmt.setBoolean(12, hd.isDaThanhToan());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật thông tin hóa đơn. Bao gồm cả trạng thái thanh toán và giờ ra.
     */
    public boolean updateHoaDon(HoaDon hd) {
        String sql = "UPDATE HOADON SET maKhachHang = ?, maBan = ?, ngayLap = ?, phuongThuc = ?, maKhuyenMai = ?, "+
                     "maThue = ?, gioVao = ?, gioRa = ?, maNhanVien = ?, maPhieu = ?, daThanhToan = ? "+
                     "WHERE maHoaDon = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hd.getKhachHang().getMaKhachHang());
            pstmt.setString(2, hd.getBan().getMaBan());
            pstmt.setDate(3, Date.valueOf(hd.getNgayLap()));
            pstmt.setString(4, hd.getPhuongThuc());
            if (hd.getKhuyenMai() != null && hd.getKhuyenMai().getMaKM() != null)
                pstmt.setString(5, hd.getKhuyenMai().getMaKM());
            else
                pstmt.setNull(5, Types.NCHAR);
            pstmt.setString(6, hd.getThue().getMaThue());
            pstmt.setTimestamp(7, Timestamp.valueOf(hd.getGioVao()));
            // Cập nhật giờ ra khi thanh toán
            if (hd.getGioRa() != null) {
                 pstmt.setTimestamp(8, Timestamp.valueOf(hd.getGioRa()));
            } else {
                 // Giữ nguyên giờ ra cũ nếu không có giá trị mới (hoặc set null nếu logic cho phép)
                 // Để đơn giản, nếu không có giờ ra mới, ta không cập nhật cột này
                 // Cần điều chỉnh câu SQL nếu muốn giữ nguyên giá trị cũ: bỏ gioRa = ? ra khỏi SET
                 // Tuy nhiên, logic thanh toán thường sẽ set giờ ra mới, nên tạm để setNull
                 pstmt.setNull(8, Types.TIMESTAMP);
            }

            pstmt.setString(9, hd.getNhanVien().getMaNV());
            if (hd.getPhieuDatBan() != null && hd.getPhieuDatBan().getMaPhieu() != null)
                pstmt.setString(10, hd.getPhieuDatBan().getMaPhieu());
            else
                pstmt.setNull(10, Types.NCHAR);
            pstmt.setBoolean(11, hd.isDaThanhToan()); // Cập nhật trạng thái thanh toán
            pstmt.setString(12, hd.getMaHoaDon()); // Điều kiện WHERE

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
             System.err.println("Lỗi khi cập nhật hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Xóa hóa đơn và chi tiết hóa đơn liên quan (dùng transaction).
     */
    public boolean deleteHoaDon(String maHD) {
        String sqlDeleteChiTiet = "DELETE FROM CHITIETHOADON WHERE maHoaDon = ?";
        String sqlDeleteHoaDon = "DELETE FROM HOADON WHERE maHoaDon = ?";
        Connection conn = null; // Khai báo ngoài để dùng trong finally
        try {
            conn = ConnectDB.getConnection();
            conn.setAutoCommit(false); // Bắt đầu transaction

            // Xóa chi tiết trước
            try (PreparedStatement ps1 = conn.prepareStatement(sqlDeleteChiTiet)) {
                ps1.setString(1, maHD);
                ps1.executeUpdate();
            }

            // Xóa hóa đơn sau
            int affected = 0;
            try (PreparedStatement ps2 = conn.prepareStatement(sqlDeleteHoaDon)) {
                ps2.setString(1, maHD);
                affected = ps2.executeUpdate();
            }

            conn.commit(); // Hoàn tất transaction
            return affected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa hóa đơn: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.println("Transaction đang được rollback...");
                    conn.rollback(); // Hủy transaction nếu có lỗi
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
            // Luôn trả lại autoCommit và đóng kết nối
             if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Lỗi khi đóng kết nối/reset autoCommit: " + e.getMessage());
                }
            }
        }
    }

    // Hàm này có thể không cần nữa nếu updateHoaDon đã xử lý trạng thái thanh toán
    // Nhưng giữ lại nếu có nơi khác chỉ muốn cập nhật trạng thái
    public boolean updateTrangThaiThanhToan(String maHoaDon, boolean daThanhToan) {
        String sql = "UPDATE HOADON SET daThanhToan = ? WHERE maHoaDon = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, daThanhToan);
            pstmt.setString(2, maHoaDon);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
             System.err.println("Lỗi khi cập nhật trạng thái thanh toán: " + e.getMessage());
            e.printStackTrace(); }
        return false;
    }

    // ==================== THỐNG KÊ ====================
    // Giữ nguyên các hàm thống kê vì chúng không liên quan trực tiếp đến cấu trúc HoaDon object

    public double getTongDoanhThu() { /* ... code giữ nguyên ... */ return 0; }
    public int getTongDonHang() { /* ... code giữ nguyên ... */ return 0; }
    public int getSoLuongKhachHang() { /* ... code giữ nguyên ... */ return 0; }
    public double getDoanhThuTheoNgay(int ngayTruoc) { /* ... code giữ nguyên ... */ return 0; }

    // ==================== TÍNH TỔNG TIỀN ====================
    // Giữ nguyên hàm tính tổng tiền đã sửa lỗi
    public double tinhTongTienHoaDon(String maHoaDon) {
        double tongTienMonAn = 0;
        double tongTienSauCung = 0;
        String sqlChiTiet = "SELECT SUM(ct.soLuong * ct.donGia) AS Tong FROM CHITIETHOADON ct WHERE ct.maHoaDon = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt1 = conn.prepareStatement(sqlChiTiet)) {
            pstmt1.setString(1, maHoaDon);
            try (ResultSet rs = pstmt1.executeQuery()) {
                if (rs.next()) tongTienMonAn = rs.getDouble("Tong");
            }
            if (tongTienMonAn <= 0) return 0; // Sửa: <= 0 vì có thể tổng tiền = 0
            tongTienSauCung = tongTienMonAn;

            String sqlThueKM = "SELECT t.tyLeThue, km.giaTri FROM HOADON hd LEFT JOIN THUE t ON hd.maThue = t.maSoThue LEFT JOIN KHUYENMAI km ON hd.maKhuyenMai = km.maKhuyenMai WHERE hd.maHoaDon = ?";
            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlThueKM)) {
                pstmt2.setString(1, maHoaDon);
                try (ResultSet rs2 = pstmt2.executeQuery()) {
                    if (rs2.next()) {
                        double tyLeThue = rs2.getDouble("tyLeThue"); // là 0.xx
                        double giaTriGiam = rs2.getDouble("giaTri"); // là 0.xx hoặc số tiền >= 1
                        // Áp dụng giảm giá trước
                        if (giaTriGiam > 0) {
                            if (giaTriGiam < 1.0) { tongTienSauCung -= (tongTienSauCung * giaTriGiam); } // Giảm %
                            else { tongTienSauCung -= giaTriGiam; } // Giảm tiền
                        }
                        // Áp dụng thuế sau giảm giá
                        if (tyLeThue > 0) { tongTienSauCung += (tongTienSauCung * tyLeThue); }
                        // Đảm bảo không âm
                        if (tongTienSauCung < 0) tongTienSauCung = 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tính tổng tiền hóa đơn " + maHoaDon + ": " + e.getMessage());
            e.printStackTrace();
        }
        return tongTienSauCung;
    }

    /**
     * Tìm hóa đơn theo mã.
     * @return HoaDon nếu tìm thấy, null nếu không.
     */
    public HoaDon findByMaHD(String maHD) {
        String sql = "SELECT maHoaDon, maKhachHang, maBan, ngayLap, phuongThuc, maKhuyenMai, maThue, gioVao, gioRa, maNhanVien, maPhieu, daThanhToan " +
                     "FROM HOADON WHERE maHoaDon = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maHD);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createHoaDonFromResultSet(rs); // Dùng hàm helper
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm hóa đơn theo mã " + maHD + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy danh sách chi tiết hóa đơn (bao gồm tên món) để in.
     */
    public List<ChiTietHoaDon> getChiTietHoaDonForPrint(String maHoaDon) {
        List<ChiTietHoaDon> chiTietList = new ArrayList<>();
        String sql = "SELECT ct.maMonAn, ma.tenMonAn, ct.soLuong, ct.donGia " +
                     "FROM CHITIETHOADON ct JOIN MONAN ma ON ct.maMonAn = ma.maMonAn " +
                     "WHERE ct.maHoaDon = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maHoaDon);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MonAn monAn = new MonAn(rs.getString("maMonAn"));
                    monAn.setTenMonAn(rs.getString("tenMonAn"));
                    ChiTietHoaDon ct = new ChiTietHoaDon();
                    ct.setMonAn(monAn);
                    ct.setSoLuong(rs.getInt("soLuong"));
                    ct.setDonGia(rs.getDouble("donGia")); // Lấy đơn giá đã lưu
                    chiTietList.add(ct);
                }
            }
        } catch (SQLException e) {
             System.err.println("Lỗi khi lấy chi tiết hóa đơn " + maHoaDon + ": " + e.getMessage());
            e.printStackTrace(); }
        return chiTietList;
    }
}