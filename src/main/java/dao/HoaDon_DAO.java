package dao;

import connectDB.ConnectDB;
import entity.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class HoaDon_DAO {

    // Helper: Tạo đối tượng HoaDon từ ResultSet
    private HoaDon createHoaDonFromResultSet(ResultSet rs) throws SQLException {
        String maHD = rs.getString("MaHD");
        LocalDate ngayLap = rs.getDate("NgayLapHoaDon").toLocalDate();
        LocalDateTime gioVao = rs.getTimestamp("GioVao").toLocalDateTime();
        
        Timestamp gioRaTimestamp = rs.getTimestamp("GioRa");
        LocalDateTime gioRa = (gioRaTimestamp != null) ? gioRaTimestamp.toLocalDateTime() : null;
        
        String phuongThuc = rs.getString("phuongThucThanhToan");
        boolean daThanhToan = rs.getBoolean("DaThanhToan");

        KhachHang kh = new KhachHang(rs.getString("MaKH"));
        NhanVien nv = new NhanVien(rs.getString("MaNV"));
        Thue thue = new Thue(rs.getString("MaThue"));
        Ban ban = new Ban(rs.getString("maBan"));

        String maPhieu = rs.getString("MaPhieu");
        PhieuDatBan pdb = (maPhieu != null) ? new PhieuDatBan(maPhieu) : null;

        String maKM = rs.getString("MaKM");
        KhuyenMai km = (maKM != null) ? new KhuyenMai(maKM) : null;
        
        double tongTienTruocThue = rs.getDouble("TongTienTruocThue");
        double tongGiamGia = rs.getDouble("TongGiamGia");

        // Giả định Constructor 15 tham số: maHD, nv, kh, ban, ngayLap, gioVao, gioRa, phuongThuc, km, thue, pdb, tongTienTruocThue, tongGiamGia, daThanhToan
        return new HoaDon(maHD, nv, kh, ban, ngayLap, gioVao, gioRa, phuongThuc, km, thue, pdb, tongTienTruocThue, tongGiamGia, daThanhToan);
    }
    
    // Helper: Chuyển java.util.Date thành java.sql.Date
    private Date toSqlDate(java.util.Date date) {
        if (date == null) return null;
        return new Date(date.getTime());
    }

    /**
     * Lấy hóa đơn chưa thanh toán của một bàn cụ thể.
     */
	public HoaDon getHoaDonByBanChuaThanhToan(String maBan) {
	    HoaDon hd = null;
	    String sql = "SELECT * FROM HOADON WHERE maBan = ? AND daThanhToan = 0";
	    try (Connection conn = ConnectDB.getInstance().getConnection();
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

    /**
     * Lấy tất cả hóa đơn, sắp xếp mới nhất lên đầu.
     */
    public List<HoaDon> getAllHoaDon() {
        List<HoaDon> dsHoaDon = new ArrayList<>();
        String sql = "SELECT MaHD, MaNV, MaKH, maBan, NgayLapHoaDon, GioVao, GioRa, phuongThucThanhToan, MaKM, MaThue, MaPhieu, TongTienTruocThue, TongGiamGia, DaThanhToan " +
                "FROM HOADON ORDER BY NgayLapHoaDon DESC, GioVao DESC";

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
        String sql = "INSERT INTO HOADON(MaHD, MaNV, MaKH, maBan, NgayLapHoaDon, GioVao, GioRa, phuongThucThanhToan, MaKM, MaThue, MaPhieu, TongTienTruocThue, TongGiamGia, DaThanhToan)"+
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
        	 Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hd.getMaHD());
            pstmt.setString(2, hd.getNhanVien().getMaNhanVien());
            
            if (hd.getKhachHang() != null && hd.getKhachHang().getMaKH() != null) {
                pstmt.setString(3, hd.getKhachHang().getMaKH());
            } else {
                pstmt.setString(3, "KH00000000"); // Mã khách vãng lai
            }

            pstmt.setString(4, hd.getBan().getMaBan());
            pstmt.setDate(5, Date.valueOf(hd.getNgayLapHoaDon()));
            pstmt.setTimestamp(6, Timestamp.valueOf(hd.getGioVao()));
            
            if (hd.getGioRa() != null) {
                pstmt.setTimestamp(7, Timestamp.valueOf(hd.getGioRa()));
            } else {
                pstmt.setNull(7, Types.TIMESTAMP); 
            }
            pstmt.setString(8, hd.getPhuongThucThanhToan());
         
            if (hd.getKhuyenMai() != null && hd.getKhuyenMai().getMaKM() != null)
                pstmt.setString(9, hd.getKhuyenMai().getMaKM());
            else
                pstmt.setNull(9, Types.NCHAR);

            pstmt.setString(10, hd.getThue().getMaSoThue());
            
            if (hd.getPhieuDatBan() != null && hd.getPhieuDatBan().getMaPhieu() != null)
                pstmt.setString(11, hd.getPhieuDatBan().getMaPhieu());
            else
                pstmt.setNull(11, Types.NCHAR);

            pstmt.setDouble(12, hd.getTongTienTruocThue());
            pstmt.setDouble(13, hd.getTongGiamGia());
            pstmt.setBoolean(14, hd.isDaThanhToan());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật thông tin hóa đơn.
     */
    public boolean updateHoaDon(HoaDon hd) {
        String sql = "UPDATE HOADON SET MaNV = ?, MaKH = ?, maBan = ?, NgayLapHoaDon = ?, phuongThucThanhToan = ?, MaKM = ?, "+
                "MaThue = ?, GioVao = ?, GioRa = ?, MaPhieu = ?, TongTienTruocThue = ?, TongGiamGia = ?, DaThanhToan = ? "+
                "WHERE MaHD = ?";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, hd.getNhanVien().getMaNhanVien());
            
            if (hd.getKhachHang() != null && hd.getKhachHang().getMaKH() != null) {
                pstmt.setString(2, hd.getKhachHang().getMaKH());
            } else {
                pstmt.setString(2, "KH00000000");
            }

            pstmt.setString(3, hd.getBan().getMaBan());
            pstmt.setDate(4, Date.valueOf(hd.getNgayLapHoaDon()));
            pstmt.setString(5, hd.getPhuongThucThanhToan());
            
            if (hd.getKhuyenMai() != null && hd.getKhuyenMai().getMaKM() != null)
                pstmt.setString(6, hd.getKhuyenMai().getMaKM());
            else
                pstmt.setNull(6, Types.NCHAR);
                
            pstmt.setString(7, hd.getThue().getMaSoThue());
            pstmt.setTimestamp(8, Timestamp.valueOf(hd.getGioVao()));
            
            if (hd.getGioRa() != null) {
                pstmt.setTimestamp(9, Timestamp.valueOf(hd.getGioRa()));
            } else {
                pstmt.setNull(9, Types.TIMESTAMP);
            }

            if (hd.getPhieuDatBan() != null && hd.getPhieuDatBan().getMaPhieu() != null)
                pstmt.setString(10, hd.getPhieuDatBan().getMaPhieu());
            else
                pstmt.setNull(10, Types.NCHAR);
            
            pstmt.setDouble(11, hd.getTongTienTruocThue());
            pstmt.setDouble(12, hd.getTongGiamGia());
            pstmt.setBoolean(13, hd.isDaThanhToan()); 
            pstmt.setString(14, hd.getMaHD()); 

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
        String sqlDeleteChiTiet = "DELETE FROM CHITIETHOADON WHERE MaHD = ?";
        String sqlDeleteHoaDon = "DELETE FROM HOADON WHERE MaHD = ?";
        Connection conn = null; 
        try {
            conn = ConnectDB.getInstance().getConnection();
            conn.setAutoCommit(false); 

            try (PreparedStatement ps1 = conn.prepareStatement(sqlDeleteChiTiet)) {
                ps1.setString(1, maHD);
                ps1.executeUpdate();
            }

            int affected = 0;
            try (PreparedStatement ps2 = conn.prepareStatement(sqlDeleteHoaDon)) {
                ps2.setString(1, maHD);
                affected = ps2.executeUpdate();
            }

            conn.commit(); 
            return affected > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi xóa hóa đơn: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.println("Transaction đang được rollback...");
                    conn.rollback(); 
                } catch (SQLException ex) {
                    System.err.println("Lỗi khi rollback: " + ex.getMessage());
                }
            }
            return false;
        } finally {
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

    /**
     * Cập nhật trạng thái thanh toán của hóa đơn.
     */
    public boolean updateTrangThaiThanhToan(String maHoaDon, boolean daThanhToan) {
        String sql = "UPDATE HOADON SET DaThanhToan = ? WHERE MaHD = ?";
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, daThanhToan);
            pstmt.setString(2, maHoaDon);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật trạng thái thanh toán: " + e.getMessage());
            e.printStackTrace(); 
        }
        return false;
    }

    // ==================== THỐNG KÊ (HÀM CŨ) ====================
    
    public double getTongDoanhThu() {
        String sql = " SELECT SUM(cthd.DonGiaBan * cthd.SoLuong) AS TongDoanhThu FROM HOADON hd INNER JOIN CHITIETHOADON cthd ON hd.MaHD = cthd.MaHD WHERE hd.DaThanhToan = 1";
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
        String sql = "SELECT COUNT(*) AS SoLuong FROM HOADON WHERE DaThanhToan = 1";
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
        String sql = "SELECT COUNT(DISTINCT MaKH) AS SoLuong FROM HOADON WHERE DaThanhToan = 1";
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
        String sql = " SELECT SUM(cthd.DonGiaBan * cthd.SoLuong) AS DoanhThuTrongNgay FROM HOADON hd INNER JOIN CHITIETHOADON cthd ON hd.MaHD = cthd.MaHD WHERE hd.DaThanhToan = 1 AND DATEDIFF(day, hd.NgayLapHoaDon, GETDATE()) <= ?";

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

    /**
     * Tính tổng tiền cuối cùng của hóa đơn sau khi áp dụng KM và Thuế.
     */
    public double tinhTongTienHoaDon(String maHoaDon) {
        double tongTienMonAn = 0;
        double tongTienSauCung = 0;
        String sqlChiTiet = "SELECT SUM(ct.SoLuong * ct.DonGiaBan) AS Tong FROM CHITIETHOADON ct WHERE ct.MaHD = ?";
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt1 = conn.prepareStatement(sqlChiTiet)) {
            pstmt1.setString(1, maHoaDon);
            try (ResultSet rs = pstmt1.executeQuery()) {
                if (rs.next()) tongTienMonAn = rs.getDouble("Tong");
            }
            if (tongTienMonAn <= 0) return 0; 
            tongTienSauCung = tongTienMonAn;

            String sqlThueKM = "SELECT t.tyLeThue, km.MucKM, km.LoaiKM FROM HOADON hd LEFT JOIN THUE t ON hd.MaThue = t.maSoThue LEFT JOIN KHUYENMAI km ON hd.MaKM = km.MaKM WHERE hd.MaHD = ?";
            try (PreparedStatement pstmt2 = conn.prepareStatement(sqlThueKM)) {
                pstmt2.setString(1, maHoaDon);
                try (ResultSet rs2 = pstmt2.executeQuery()) {
                    if (rs2.next()) {
                        double tyLeThue = rs2.getDouble("tyLeThue"); 
                        double mucKM = rs2.getDouble("MucKM"); 
                        String loaiKM = rs2.getString("LoaiKM"); 
                        
                        if (mucKM > 0) {
                            if (mucKM < 1.0 && (loaiKM != null && (loaiKM.contains("Thành viên") || loaiKM.contains("Sinh nhật") || loaiKM.contains("Voucher")))) {
                                tongTienSauCung -= (tongTienSauCung * mucKM); 
                            } else if (mucKM >= 1.0) { 
                                tongTienSauCung -= mucKM; 
                            }
                        }
                        
                        if (tyLeThue > 0) { tongTienSauCung += (tongTienSauCung * tyLeThue); }
                        
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
     */
    public HoaDon findByMaHD(String maHD) {
        String sql = "SELECT MaHD, MaNV, MaKH, maBan, NgayLapHoaDon, GioVao, GioRa, phuongThucThanhToan, MaKM, MaThue, MaPhieu, TongTienTruocThue, TongGiamGia, DaThanhToan " +
                "FROM HOADON WHERE MaHD = ?";
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
        String sql = "SELECT ct.maMonAn, ma.tenMonAn, ct.SoLuong, ct.DonGiaBan " +
                "FROM CHITIETHOADON ct JOIN MONAN ma ON ct.maMonAn = ma.maMonAn " +
                "WHERE ct.MaHD = ?";
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maHoaDon);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MonAn monAn = new MonAn(rs.getString("maMonAn"));
                    monAn.setTenMonAn(rs.getString("tenMonAn"));
                    ChiTietHoaDon ct = new ChiTietHoaDon();
                    ct.setMonAn(monAn);
                    ct.setSoLuong(rs.getInt("SoLuong"));
                    ct.setDonGiaBan(rs.getDouble("DonGiaBan"));
                    chiTietList.add(ct);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy chi tiết hóa đơn " + maHoaDon + ": " + e.getMessage());
            e.printStackTrace(); 
        }
        return chiTietList;
    }
    
    /**
     * Tạo mã hóa đơn mới tự động tăng.
     */
    public String generateNewID() {
        String newID = "HD00001";
        String sql = "SELECT TOP 1 MaHD FROM HOADON ORDER BY MaHD DESC";

        try (Connection con = ConnectDB.getInstance().getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastID = rs.getString("MaHD").trim();
                if (lastID.startsWith("HD")) {
                    try {
                        String numberPart = lastID.substring(2);
                        int num = Integer.parseInt(numberPart) + 1;
                        newID = String.format("HD%05d", num);
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        // Bỏ qua lỗi parsing
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newID;
    }
    
    /**
     * Sao chép chi tiết món ăn từ Phiếu đặt bàn sang Chi tiết hóa đơn.
     */
     public boolean copyChiTietPhieuDatToHoaDon(String maPhieu, String maHoaDon) {
        String sql = "INSERT INTO CHITIETHOADON (MaHD, MaMonAn, SoLuong, DonGiaBan) " +
                     "SELECT ?, ct.maMonAn, ct.soLuongMonAn, ct.DonGiaBan " +
                     "FROM CHITIETPHIEUDATBAN ct " +
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
    
    // ==================== THỐNG KÊ THEO NGÀY (BỔ SUNG) ====================

    /**
     * Lấy tổng doanh thu trong khoảng thời gian [from, to].
     */
    public double getTongDoanhThu(java.util.Date from, java.util.Date to) {
        String sql =
            " SELECT SUM(cthd.SoLuong * cthd.DonGiaBan) AS TongDoanhThu" +
            " FROM HOADON hd INNER JOIN CHITIETHOADON cthd ON hd.MaHD = cthd.MaHD" +
            " WHERE hd.DaThanhToan = 1";
        
        if (from != null) sql += " AND hd.NgayLapHoaDon >= ?";
        if (to != null) sql += " AND hd.NgayLapHoaDon <= ?";
        
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int index = 1;
            if (from != null) pstmt.setDate(index++, toSqlDate(from));
            if (to != null) pstmt.setDate(index, toSqlDate(to));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getDouble("TongDoanhThu");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tổng doanh thu theo ngày: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy tổng số đơn hàng đã thanh toán trong khoảng thời gian [from, to].
     */
    public int getTongDonHang(java.util.Date from, java.util.Date to) {
        String sql = "SELECT COUNT(*) AS SoLuong FROM HOADON WHERE DaThanhToan = 1";
        
        if (from != null) sql += " AND NgayLapHoaDon >= ?";
        if (to != null) sql += " AND NgayLapHoaDon <= ?";
        
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int index = 1;
            if (from != null) pstmt.setDate(index++, toSqlDate(from));
            if (to != null) pstmt.setDate(index, toSqlDate(to));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getInt("SoLuong");
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy tổng đơn hàng theo ngày: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Lấy doanh thu theo ngày (dùng cho BarChart) trong khoảng thời gian [from, to].
     */
    public Map<String, Double> getDoanhThuTheoKhoangThoiGian(java.util.Date from, java.util.Date to) {
        Map<String, Double> data = new LinkedHashMap<>();
        
        String sql =
            " SELECT CONVERT(NVARCHAR, hd.NgayLapHoaDon, 103) AS Ngay, " +
            "        SUM(cthd.SoLuong * cthd.DonGiaBan) AS TongDoanhThu" +
            " FROM HOADON hd INNER JOIN CHITIETHOADON cthd ON hd.MaHD = cthd.MaHD" +
            " WHERE hd.DaThanhToan = 1 AND hd.NgayLapHoaDon BETWEEN ? AND ?" +
            " GROUP BY hd.NgayLapHoaDon ORDER BY hd.NgayLapHoaDon";
        
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, toSqlDate(from));
            pstmt.setDate(2, toSqlDate(to));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String ngay = rs.getString("Ngay");
                    double doanhThu = rs.getDouble("TongDoanhThu");
                    data.put(ngay, doanhThu);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy doanh thu theo khoảng thời gian: " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }
}