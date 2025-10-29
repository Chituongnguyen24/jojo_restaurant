package dao;

import connectDB.ConnectDB; // Class kết nối CSDL của bạn
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HoaDon_DAO {

	// Trong HoaDon_DAO.java
	public HoaDon getHoaDonByBanChuaThanhToan(String maBan) {
	    HoaDon hd = null;
	    String sql = "SELECT * FROM HOADON WHERE maBan = ? AND daThanhToan = 0"; // Tìm HĐ của bàn và chưa thanh toán
	    try (Connection conn = ConnectDB.getConnection(); // Dùng lớp kết nối của bạn
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {
	        pstmt.setString(1, maBan);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                hd = createHoaDonFromResultSet(rs); 
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Lỗi khi lấy hóa đơn chưa thanh toán của bàn " + maBan + ": " + e.getMessage());
	        e.printStackTrace();
	    }
	    return hd;
	}
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

        try (Connection conn = ConnectDB.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                dsHoaDon.add(createHoaDonFromResultSet(rs));
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

        try (
        	 Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hd.getMaHoaDon());

            if (hd.getKhachHang() != null && hd.getKhachHang().getMaKhachHang() != null) {
                pstmt.setString(2, hd.getKhachHang().getMaKhachHang());
            } else {
                pstmt.setString(2, "KH00000000"); // Mã khách vãng lai mặc định
            }

            pstmt.setString(3, hd.getBan().getMaBan());
            pstmt.setDate(4, Date.valueOf(hd.getNgayLap()));
            pstmt.setString(5, hd.getPhuongThuc());
         
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
                pstmt.setNull(9, Types.TIMESTAMP); 
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

    
    public boolean updateHoaDon(HoaDon hd) {
        String sql = "UPDATE HOADON SET maKhachHang = ?, maBan = ?, ngayLap = ?, phuongThuc = ?, maKhuyenMai = ?, "+
                "maThue = ?, gioVao = ?, gioRa = ?, maNhanVien = ?, maPhieu = ?, daThanhToan = ? "+
                "WHERE maHoaDon = ?";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (hd.getKhachHang() != null && hd.getKhachHang().getMaKhachHang() != null) {
                pstmt.setString(1, hd.getKhachHang().getMaKhachHang());
            } else {
                pstmt.setString(1, "KH00000000"); // Mã khách vãng lai mặc định
            }

            pstmt.setString(2, hd.getBan().getMaBan());
            pstmt.setDate(3, Date.valueOf(hd.getNgayLap()));
            pstmt.setString(4, hd.getPhuongThuc());
            if (hd.getKhuyenMai() != null && hd.getKhuyenMai().getMaKM() != null)
                pstmt.setString(5, hd.getKhuyenMai().getMaKM());
            else
                pstmt.setNull(5, Types.NCHAR);
            pstmt.setString(6, hd.getThue().getMaThue());
            pstmt.setTimestamp(7, Timestamp.valueOf(hd.getGioVao()));
            
            if (hd.getGioRa() != null) {
                pstmt.setTimestamp(8, Timestamp.valueOf(hd.getGioRa()));
            } else {
                pstmt.setNull(8, Types.TIMESTAMP);
            }

            pstmt.setString(9, hd.getNhanVien().getMaNV());
            if (hd.getPhieuDatBan() != null && hd.getPhieuDatBan().getMaPhieu() != null)
                pstmt.setString(10, hd.getPhieuDatBan().getMaPhieu());
            else
                pstmt.setNull(10, Types.NCHAR);
            pstmt.setBoolean(11, hd.isDaThanhToan()); 
            pstmt.setString(12, hd.getMaHoaDon()); 

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
            conn = ConnectDB.getInstance().getConnection();
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
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, daThanhToan);
            pstmt.setString(2, maHoaDon);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật trạng thái thanh toán: " + e.getMessage());
            e.printStackTrace(); }
        return false;
    }

    public double getTongDoanhThu() {
        String sql =
                " SELECT SUM(CAST(cthd.soLuong AS DECIMAL(18, 2)) * cthd.donGia) AS TongDoanhThu" +
                        " FROM HOADON hd INNER JOIN CHITIETHOADON cthd ON hd.maHoaDon = cthd.maHoaDon" +
                        " WHERE hd.daThanhToan = 1";
        try (Connection conn = ConnectDB.getInstance().getConnection();
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
        try (Connection conn = ConnectDB.getInstance().getConnection();
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
        try (Connection conn = ConnectDB.getInstance().getConnection();
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
                " SELECT       SUM(CAST(cthd.soLuong AS DECIMAL(18, 2)) * cthd.donGia) AS DoanhThuTrongNgay" +
                        " FROM         HOADON hd  INNER JOIN   CHITIETHOADON cthd ON hd.maHoaDon = cthd.maHoaDon" +
                        " WHERE   hd.daThanhToan = 1 AND DATEDIFF(day, hd.ngayLap, GETDATE()) <= ?";

        try (Connection conn = ConnectDB.getInstance().getConnection();
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
        double tongTienMonAn = 0;
        double tongTienSauCung = 0;
        String sqlChiTiet = "SELECT SUM(ct.soLuong * ct.donGia) AS Tong FROM CHITIETHOADON ct WHERE ct.maHoaDon = ?";
        try (Connection conn = ConnectDB.getInstance().getConnection();
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
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maHD);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createHoaDonFromResultSet(rs);
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
        try (Connection conn = ConnectDB.getInstance().getConnection();
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

    public double getTongDoanhThu(java.util.Date from, java.util.Date to) {
        if (from == null || to == null) {
            return getTongDoanhThu();
        }

        String sql =
                " SELECT SUM(CAST(cthd.soLuong AS DECIMAL(18, 2)) * cthd.donGia) AS TongDoanhThu" +
                        " FROM HOADON hd INNER JOIN CHITIETHOADON cthd ON hd.maHoaDon = cthd.maHoaDon" +
                        " WHERE hd.daThanhToan = 1 AND hd.ngayLap BETWEEN ? AND ?";

        try (Connection conn = ConnectDB.getInstance().getConnection();
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

        try (Connection conn = ConnectDB.getInstance().getConnection();
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

        try (Connection conn = ConnectDB.getInstance().getConnection();
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

        try (Connection conn = ConnectDB.getInstance().getConnection();
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


    public HoaDon getHoaDonByBan(String maBan) {
        // SỬA LẠI CÁCH NỐI CHUỖI VÀ THÊM ĐỦ CÁC CỘT
        String sql = "SELECT maHoaDon, maKhachHang, maBan, ngayLap, phuongThuc, maKhuyenMai, maThue, gioVao, gioRa, maNhanVien, maPhieu, daThanhToan " +
                     "FROM HOADON " +
                     "WHERE maBan = ? AND daThanhToan = 0"; 
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maBan);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { 
                    return createHoaDonFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm hóa đơn theo mã bàn " + maBan + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return null; // Không tìm thấy hoặc có lỗi
    }
    public String generateNewID() {
        String newID = "HD00001";
        String sql = "SELECT TOP 1 maHoaDon FROM HOADON ORDER BY maHoaDon DESC";

        try (Connection con = ConnectDB.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastID = rs.getString("maHoaDon").trim();
                if (lastID.startsWith("HD")) {
                    try {
                        String numberPart = lastID.substring(2);
                        int num = Integer.parseInt(numberPart) + 1;
                        newID = String.format("HD%05d", num);
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        // Bỏ qua lỗi parsing, sẽ dùng newID mặc định
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newID;
    }

    /*
     Sao chép tất cả món ăn từ ChiTietPhieuDatBan sang ChiTietHoaDon
     */
    public boolean copyChiTietPhieuDatToHoaDon(String maPhieu, String maHoaDon) {
        // Lấy đơn giá từ bảng MonAn tại thời điểm sao chép
        String sql = "INSERT INTO CHITIETHOADON (maHoaDon, maMonAn, soLuong, donGia, ghiChu) " +
                     "SELECT ?, ct.maMonAn, ct.soLuongMonAn, ma.donGia, ct.ghiChu " +
                     "FROM CHITIETPHIEUDATBAN ct " +
                     "JOIN MONAN ma ON ct.maMonAn = ma.maMonAn " +
                     "WHERE ct.maPhieu = ?";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maHoaDon);
            pstmt.setString(2, maPhieu);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi sao chép chi tiết phiếu đặt sang hóa đơn: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean ensureHoaDonAndSyncFromPhieu(String maPhieu, String maBan) {
        Connection conn = null;
        try {
            conn = ConnectDB.getInstance().getConnection();
            conn.setAutoCommit(false);

            // 1) Tìm hoadon chưa thanh toán cho bàn
            HoaDon hoaDon = getHoaDonByBan(maBan); 
            String maHoaDon;
            if (hoaDon == null) {
                // Tạo hoadon mới với giá trị mặc định an toàn (tránh NPE trong addHoaDon)
                maHoaDon = generateNewID();

                String sqlInsert = "INSERT INTO HOADON (maHoaDon, maKhachHang, maBan, ngayLap, phuongThuc, maKhuyenMai, maThue, gioVao, gioRa, maNhanVien, maPhieu, daThanhToan) " +
                                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsert)) {
                    ps.setString(1, maHoaDon);
                    ps.setString(2, "KH00000000"); // khách vãng lai mặc định 
                    ps.setString(3, maBan);
                    ps.setDate(4, java.sql.Date.valueOf(LocalDate.now()));
                    ps.setString(5, "Tiền mặt"); // phương thức mặc định
                    ps.setNull(6, Types.NCHAR); // maKhuyenMai
                    ps.setNull(7, Types.NCHAR); // maThue
                    ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setNull(9, Types.TIMESTAMP); // gioRa
                    ps.setNull(10, Types.NCHAR); // maNhanVien (nếu bạn có mã NV hiện tại, set vào đây)
                    ps.setString(11, maPhieu);
                    ps.setBoolean(12, false); // daThanhToan = 0
                    ps.executeUpdate();
                }
            } else {
                maHoaDon = hoaDon.getMaHoaDon();
                // nếu hoaDon tồn tại nhưng không liên kết với maPhieu, có thể cập nhật maPhieu
                if (hoaDon.getPhieuDatBan() == null && maPhieu != null) {
                    String sqlUpd = "UPDATE HOADON SET maPhieu = ? WHERE maHoaDon = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlUpd)) {
                        ps.setString(1, maPhieu);
                        ps.setString(2, maHoaDon);
                        ps.executeUpdate();
                    }
                }
            }

            // 2) Xóa chi tiết hoá đơn hiện có để tránh nhân đôi (nếu muốn merge thì cần logic phức tạp hơn)
            String sqlDeleteCT = "DELETE FROM CHITIETHOADON WHERE maHoaDon = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlDeleteCT)) {
                ps.setString(1, maHoaDon);
                ps.executeUpdate();
            }

            // 3) Sao chép tất cả chi tiết từ phiếu đặt vào chi tiết hoá đơn
            boolean copied = false;
            String sqlCopy = "INSERT INTO CHITIETHOADON (maHoaDon, maMonAn, soLuong, donGia, ghiChu) " +
                             "SELECT ?, ct.maMonAn, ct.soLuongMonAn, COALESCE(ct.donGia, ma.donGia), ct.ghiChu " +
                             "FROM CHITIETPHIEUDATBAN ct " +
                             "JOIN MONAN ma ON ct.maMonAn = ma.maMonAn " +
                             "WHERE ct.maPhieu = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCopy)) {
                ps.setString(1, maHoaDon);
                ps.setString(2, maPhieu);
                int affected = ps.executeUpdate();
                copied = affected > 0;
            }

            conn.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException e) { e.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}