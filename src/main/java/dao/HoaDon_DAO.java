package dao;

import connectDB.ConnectDB;
import entity.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HoaDon_DAO {

    private Thue_DAO thueDAO= new Thue_DAO();

	// Helper: Tạo đối tượng HoaDon từ ResultSet
    private HoaDon createHoaDonFromResultSet(ResultSet rs) throws SQLException {
        // ... (Giữ nguyên logic tạo HoaDon từ ResultSet)
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

        // Giả định Constructor mới nhất
        return new HoaDon(maHD, nv, kh, ban, ngayLap, gioVao, gioRa, phuongThuc, km, thue, pdb, tongTienTruocThue, tongGiamGia, daThanhToan);
    }
    
    // Helper: Chuyển java.time.LocalDate sang java.sql.Date
    private Date toSqlDate(LocalDate date) {
        if (date == null) return null;
        return Date.valueOf(date);
    }
    
    // Helper: Chuyển java.util.Date sang java.sql.Date
    private Date toSqlDate(java.util.Date date) {
        if (date == null) return null;
        return new Date(date.getTime());
    }

    /**
     * Lấy tất cả hóa đơn, sắp xếp mới nhất lên đầu.
     */
    public List<HoaDon> getAllHoaDon() {
        List<HoaDon> dsHoaDon = new ArrayList<>();
        // SỬA: Lấy các cột mới trong Entity HoaDon (maBan, TongTienTruocThue, TongGiamGia)
        String sql = "SELECT MaHD, MaNV, MaKH, maBan, NgayLapHoaDon, GioVao, GioRa, phuongThucThanhToan, MaKM, MaThue, MaPhieu, ISNULL(TongTienTruocThue, 0) AS TongTienTruocThue, ISNULL(TongGiamGia, 0) AS TongGiamGia, DaThanhToan " +
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
        // SỬA: Dùng tên cột mới
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
                pstmt.setString(3, "KH00000000"); 
            }

            pstmt.setString(4, hd.getBan().getMaBan());
            pstmt.setDate(5, toSqlDate(hd.getNgayLapHoaDon())); // SỬA: Dùng helper toSqlDate
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
        // SỬA: Dùng tên cột mới
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
                    // conn.close(); // Giả định ConnectDB quản lý kết nối
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

    /**
     * Tính tổng tiền cuối cùng của hóa đơn sau khi áp dụng KM và Thuế.
     */
    public double tinhTongTienHoaDon(String maHoaDon) {
        // 1. Lấy thông tin cơ bản của hóa đơn (Đã bao gồm TongTienTruocThue, TongGiamGia)
        HoaDon hd = findByMaHD(maHoaDon);
        if (hd == null) return 0;
        
        // 2. Lấy tỷ lệ thuế đã lưu (vì chỉ có MaThue được lưu)
        Thue thue = (hd.getThue() != null) ? thueDAO.getThueById(hd.getThue().getMaSoThue()) : null;
        double tyLeThue = (thue != null) ? thue.getTyLeThue() : 0.0;
        
        double tongTienTruocThue = hd.getTongTienTruocThue();
        double tongGiamGia = hd.getTongGiamGia();
        
        // 3. Tính toán tổng tiền cuối cùng
        double tongTienSauGiam = tongTienTruocThue - tongGiamGia;
        if (tongTienSauGiam < 0) tongTienSauGiam = 0;
        
        // ÁP DỤNG THUẾ
        double tongThanhToan = tongTienSauGiam * (1 + tyLeThue);
        
        return tongThanhToan;
    }

    private KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO();

    /**
     * Tìm hóa đơn theo mã.
     */
    public HoaDon findByMaHD(String maHD) {
        String sql = "SELECT MaHD, MaNV, MaKH, maBan, NgayLapHoaDon, GioVao, GioRa, phuongThucThanhToan, MaKM, MaThue, MaPhieu, ISNULL(TongTienTruocThue, 0) AS TongTienTruocThue, ISNULL(TongGiamGia, 0) AS TongGiamGia, DaThanhToan " +
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
        String sql = "SELECT ct.maMonAn, ma.tenMonAn, ct.soLuong, ct.donGia " +
                "FROM CHITIETHOADON ct JOIN MONAN ma ON ct.maMonAn = ma.maMonAn " +
                "WHERE ct.maHoaDon = ?";
        try (Connection conn = ConnectDB.getConnection(); // SỬA: Dùng ConnectDB.getConnection()
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maHoaDon);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    MonAn monAn = new MonAn(rs.getString("maMonAn"));
                    monAn.setTenMonAn(rs.getString("tenMonAn"));
                    ChiTietHoaDon ct = new ChiTietHoaDon();
                    ct.setMonAn(monAn);
                    ct.setSoLuong(rs.getInt("soLuong"));
                    // SỬA: Lấy donGia từ bảng CHITIETHOADON (donGia)
                    ct.setDonGiaBan(rs.getDouble("donGia")); 
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
        // SỬA: Thay thế 'maHoaDon' bằng 'MaHD' (tên cột thực tế trong DB)
        String sql = "SELECT TOP 1 MaHD FROM HOADON ORDER BY MaHD DESC"; 

        try (Connection con = ConnectDB.getConnection(); 
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                // SỬA: Lấy tên cột chính xác từ ResultSet
                String lastID = rs.getString("MaHD").trim(); 
                if (lastID.startsWith("HD")) {
                    // ... (logic tạo mã mới)
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
    
    // ... (Các hàm thống kê cũ được giữ nguyên)
    
    public double getTongDoanhThu(java.util.Date from, java.util.Date to) {
        if (from == null || to == null) {
            return getTongDoanhThu(); // Gọi hàm không tham số nếu ngày bị thiếu
        }
        
        // Đảm bảo getStartOfDay và getEndOfDay được gọi nếu cần
        
        String sql = " SELECT SUM(cthd.DonGiaBan * cthd.SoLuong) AS TongDoanhThu" +
                     " FROM HOADON hd INNER JOIN CHITIETHOADON cthd ON hd.MaHD = cthd.MaHD" +
                     " WHERE hd.DaThanhToan = 1 AND hd.NgayLapHoaDon BETWEEN ? AND ?";

        try (Connection con = ConnectDB.getConnection(); 
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setDate(1, toSqlDate(from)); // SỬ DỤNG HELPER toSqlDate
            stmt.setDate(2, toSqlDate(to)); // SỬ DỤNG HELPER toSqlDate

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("TongDoanhThu");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTongDonHang(java.util.Date from, java.util.Date to) {
        if (from == null || to == null) {
            return getTongDonHang(); // Gọi hàm không tham số nếu ngày bị thiếu
        }
        
        String sql = "SELECT COUNT(*) AS SoLuong FROM HOADON WHERE DaThanhToan = 1 AND NgayLapHoaDon BETWEEN ? AND ?";

        try (Connection con = ConnectDB.getConnection(); 
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setDate(1, toSqlDate(from));
            stmt.setDate(2, toSqlDate(to));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("SoLuong");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public double getTongDoanhThu() {
        String sql = " SELECT SUM(cthd.DonGiaBan * cthd.SoLuong) AS TongDoanhThu" +
                     " FROM HOADON hd INNER JOIN CHITIETHOADON cthd ON hd.MaHD = cthd.MaHD" +
                     " WHERE hd.DaThanhToan = 1";

        try (Connection con = ConnectDB.getConnection(); 
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("TongDoanhThu");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getTongDonHang() {
        String sql = "SELECT COUNT(*) AS SoLuong FROM HOADON WHERE DaThanhToan = 1";
        
        try (Connection con = ConnectDB.getConnection(); 
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("SoLuong");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public Map<String, Double> getDoanhThuTheoKhoangThoiGian(java.util.Date from, java.util.Date to) {
        Map<String, Double> doanhThuTheoNgay = new LinkedHashMap<>();
        
        // Nếu ngày là null, mặc định lấy dữ liệu 7 ngày gần nhất
        if (from == null || to == null) {
            Calendar cal = Calendar.getInstance();
            to = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, -6);
            from = cal.getTime();
        }

        // 1. Tạo chuỗi ngày giữa 'from' và 'to' để đảm bảo map có đầy đủ ngày (kể cả ngày không có doanh thu)
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar start = Calendar.getInstance();
        start.setTime(from);
        Calendar end = Calendar.getInstance();
        end.setTime(to);
        
        while (!start.after(end)) {
            doanhThuTheoNgay.put(sdf.format(start.getTime()), 0.0);
            start.add(Calendar.DAY_OF_MONTH, 1);
        }
        
        // 2. Truy vấn dữ liệu thực tế từ DB
        String sql =
            " SELECT CONVERT(VARCHAR, NgayLapHoaDon, 103) AS Ngay, " + // Định dạng dd/MM/yyyy
            "        SUM(HOADON.TongTienTruocThue * (1 + THUE.tyLeThue) - HOADON.TongGiamGia) AS DoanhThuNgay " +
            " FROM HOADON HOADON" +
            " LEFT JOIN THUE THUE ON HOADON.MaThue = THUE.maSoThue " +
            " WHERE HOADON.DaThanhToan = 1 AND HOADON.NgayLapHoaDon BETWEEN ? AND ?" +
            " GROUP BY CONVERT(VARCHAR, NgayLapHoaDon, 103), HOADON.NgayLapHoaDon" +
            " ORDER BY HOADON.NgayLapHoaDon";

        try (Connection con = ConnectDB.getConnection(); 
             PreparedStatement stmt = con.prepareStatement(sql)) {

            // Chuyển java.util.Date sang java.sql.Date
            stmt.setDate(1, toSqlDate(from)); 
            stmt.setDate(2, toSqlDate(to)); 

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String ngay = rs.getString("Ngay");
                    double doanhThu = rs.getDouble("DoanhThuNgay");
                    // Cập nhật giá trị vào map (giữ lại các ngày không có doanh thu là 0.0)
                    doanhThuTheoNgay.put(ngay, doanhThu); 
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn doanh thu theo ngày: " + e.getMessage());
            e.printStackTrace();
            return new LinkedHashMap<>(); // Trả về map trống nếu có lỗi
        }
        
        return doanhThuTheoNgay;
    }
    
}