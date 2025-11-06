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

    private Thue_DAO thueDAO = new Thue_DAO();
    private KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO();

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

        return new HoaDon(maHD, nv, kh, ban, ngayLap, gioVao, gioRa, phuongThuc, km, thue, pdb, tongTienTruocThue, tongGiamGia, daThanhToan);
    }

    private Date toSqlDate(LocalDate date) {
        if (date == null) return null;
        return Date.valueOf(date);
    }

    private Date toSqlDate(java.util.Date date) {
        if (date == null) return null;
        return new Date(date.getTime());
    }

    public List<HoaDon> getAllHoaDon() {
        List<HoaDon> dsHoaDon = new ArrayList<>();
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
    
    public List<HoaDon> getRecentHoaDon() {
        List<HoaDon> dsHoaDon = new ArrayList<>();
        // THÊM "TOP 100" VÀO CÂU SQL
        String sql = "SELECT TOP 100 MaHD, MaNV, MaKH, maBan, NgayLapHoaDon, GioVao, GioRa, phuongThucThanhToan, MaKM, MaThue, MaPhieu, ISNULL(TongTienTruocThue, 0) AS TongTienTruocThue, ISNULL(TongGiamGia, 0) AS TongGiamGia, DaThanhToan " +
                     "FROM HOADON ORDER BY NgayLapHoaDon DESC, GioVao DESC";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                dsHoaDon.add(createHoaDonFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy danh sách hóa đơn gần đây: " + e.getMessage());
            e.printStackTrace();
        }
        return dsHoaDon;
    }
    public boolean addHoaDon(HoaDon hd) {
        String sql = "INSERT INTO HOADON(MaHD, MaNV, MaKH, maBan, NgayLapHoaDon, GioVao, GioRa, phuongThucThanhToan, MaKM, MaThue, MaPhieu, TongTienTruocThue, TongGiamGia, DaThanhToan)" +
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
            pstmt.setDate(5, toSqlDate(hd.getNgayLapHoaDon()));
            pstmt.setTimestamp(6, Timestamp.valueOf(hd.getGioVao()));

            LocalDateTime gioRaDefault = (hd.getGioRa() != null) ? hd.getGioRa() : hd.getGioVao();
            pstmt.setTimestamp(7, Timestamp.valueOf(gioRaDefault));

            String phuongThuc = hd.getPhuongThucThanhToan();
            if (phuongThuc == null || phuongThuc.trim().isEmpty()) {
                phuongThuc = "Chưa xác định";
            }
            pstmt.setString(8, phuongThuc);

            if (hd.getKhuyenMai() != null && hd.getKhuyenMai().getMaKM() != null)
                pstmt.setString(9, hd.getKhuyenMai().getMaKM());
            else
                pstmt.setNull(9, Types.VARCHAR); 

            pstmt.setString(10, hd.getThue().getMaSoThue());

            if (hd.getPhieuDatBan() != null && hd.getPhieuDatBan().getMaPhieu() != null)
                pstmt.setString(11, hd.getPhieuDatBan().getMaPhieu());
            else
                pstmt.setNull(11, Types.NCHAR); 

            pstmt.setDouble(12, hd.getTongTienTruocThue());
            pstmt.setDouble(13, hd.getTongGiamGia());
            pstmt.setBoolean(14, hd.isDaThanhToan());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Debug: Insert HD thành công: " + hd.getMaHD()); 
                return true;
            } else {
                System.err.println("Debug: Insert HD fail (0 rows): " + hd.getMaHD()); 
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thêm hóa đơn: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateHoaDon(HoaDon hd) {
        String sql = "UPDATE HOADON SET MaNV = ?, MaKH = ?, maBan = ?, NgayLapHoaDon = ?, phuongThucThanhToan = ?, MaKM = ?, " +
                "MaThue = ?, GioVao = ?, GioRa = ?, MaPhieu = ?, TongTienTruocThue = ?, TongGiamGia = ?, DaThanhToan = ? " +
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
            pstmt.setDate(4, toSqlDate(hd.getNgayLapHoaDon()));
            pstmt.setString(5, hd.getPhuongThucThanhToan());

            if (hd.getKhuyenMai() != null && hd.getKhuyenMai().getMaKM() != null)
                pstmt.setString(6, hd.getKhuyenMai().getMaKM());
            else
                pstmt.setNull(6, Types.VARCHAR); 

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
                } catch (SQLException e) {
                    System.err.println("Lỗi khi reset autoCommit: " + e.getMessage());
                }
            }
        }
    }

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

    public List<ChiTietHoaDon> getChiTietHoaDonForPrint(String maHoaDon) {
        List<ChiTietHoaDon> chiTietList = new ArrayList<>();
        String sql = "SELECT ct.maMonAn, ma.tenMonAn, ct.SoLuong, ct.DonGiaBan AS donGia " +
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

    public String generateNewID() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
        String datePrefix = "HD" + sdf.format(new java.util.Date());
        String newID = datePrefix + "0001"; 

        String sql = "SELECT TOP 1 MaHD FROM HOADON WHERE MaHD LIKE ? ORDER BY MaHD DESC";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = con.prepareStatement(sql)) {
            
            pstmt.setString(1, datePrefix + "%"); 

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String lastID = rs.getString("MaHD").trim();
                    String numberPart = lastID.substring(8); 
                    int num = Integer.parseInt(numberPart) + 1;
                    newID = String.format(datePrefix + "%04d", num);
                }
            }
        } catch (SQLException | NumberFormatException | StringIndexOutOfBoundsException e) {
            System.err.println("Lỗi khi tạo mã HD mới: " + e.getMessage());
            e.printStackTrace();
        }
        return newID;
    }

    public double getTongDoanhThu(java.util.Date from, java.util.Date to) {
        if (from == null || to == null) {
            return getTongDoanhThu();
        }
        String sql = " SELECT SUM(cthd.DonGiaBan * cthd.SoLuong) AS TongDoanhThu" +
                " FROM HOADON hd INNER JOIN CHITIETHOADON cthd ON hd.MaHD = cthd.MaHD" +
                " WHERE hd.DaThanhToan = 1 AND hd.NgayLapHoaDon BETWEEN ? AND ?";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setDate(1, toSqlDate(from));
            stmt.setDate(2, toSqlDate(to)); 

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
            return getTongDonHang();
        }
        
        String sql = "SELECT COUNT(*) AS SoLuong FROM HOADON WHERE DaThanhToan = 1 AND NgayLapHoaDon BETWEEN ? AND ?";

        try (Connection con = ConnectDB.getInstance().getConnection();
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

        try (Connection con = ConnectDB.getInstance().getConnection();
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
        
        try (Connection con = ConnectDB.getInstance().getConnection();
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

        if (from == null || to == null) {
            Calendar cal = Calendar.getInstance();
            to = cal.getTime();
            cal.add(Calendar.DAY_OF_MONTH, -6);
            from = cal.getTime();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar start = Calendar.getInstance();
        start.setTime(from);
        Calendar end = Calendar.getInstance();
        end.setTime(to);

        while (!start.after(end)) {
            doanhThuTheoNgay.put(sdf.format(start.getTime()), 0.0);
            start.add(Calendar.DAY_OF_MONTH, 1);
        }

        String sql =
                " SELECT CONVERT(VARCHAR, NgayLapHoaDon, 103) AS Ngay, " +
                "        SUM(HOADON.TongTienTruocThue) AS DoanhThuNgay " +
                " FROM HOADON HOADON" +
                " WHERE HOADON.DaThanhToan = 1 AND HOADON.NgayLapHoaDon BETWEEN ? AND ?" +
                " GROUP BY CONVERT(VARCHAR, NgayLapHoaDon, 103), HOADON.NgayLapHoaDon" +
                " ORDER BY HOADON.NgayLapHoaDon";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setDate(1, toSqlDate(from));
            stmt.setDate(2, toSqlDate(to));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String ngay = rs.getString("Ngay");
                    double doanhThu = rs.getDouble("DoanhThuNgay");
                    doanhThuTheoNgay.put(ngay, doanhThu);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn doanh thu theo ngày: " + e.getMessage());
            e.printStackTrace();
            return new LinkedHashMap<>();
        }
        return doanhThuTheoNgay;
    }

    public HoaDon getHoaDonByMaPhieuDat(String maPhieu) {
        String sql = "SELECT TOP 1 * FROM HOADON " +
                     "WHERE MaPhieu = ? " +
                     "ORDER BY GioVao DESC"; 

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maPhieu.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createHoaDonFromResultSet(rs); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public HoaDon getHoaDonByBanChuaThanhToan(String maBan) {
        String sql = "SELECT MaHD, MaNV, MaKH, maBan, NgayLapHoaDon, GioVao, GioRa, " +
                "phuongThucThanhToan, MaKM, MaThue, MaPhieu, " +
                "ISNULL(TongTienTruocThue, 0) AS TongTienTruocThue, " +
                "ISNULL(TongGiamGia, 0) AS TongGiamGia, DaThanhToan " +
                "FROM HOADON " +
                "WHERE maBan = ? AND DaThanhToan = 0 " +
                "ORDER BY NgayLapHoaDon DESC, GioVao DESC";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maBan.trim());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createHoaDonFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi lấy hóa đơn chưa thanh toán cho bàn " + maBan + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean taoHoaDonTuPhieuDat(PhieuDatBan phieu, String maNV) {
        if (phieu == null || phieu.getBan() == null) {
            System.err.println("Debug: Phiếu đặt bàn hoặc bàn bị null!");
            return false;
        }
        
        HoaDon hdExist = getHoaDonByBanChuaThanhToan(phieu.getBan().getMaBan());
        if (hdExist != null) {
            System.out.println("Debug: Bàn " + phieu.getBan().getMaBan() + " đã có hóa đơn " + hdExist.getMaHD());
            return true;
        }
        
        String maHDMoi = generateNewID();
        LocalDate ngayLap = LocalDate.now();
        LocalDateTime gioVao = LocalDateTime.now();
        
        KhachHang kh = phieu.getKhachHang();
        if (kh == null || kh.getMaKH() == null) {
            kh = new KhachHang("KH00000000");
        }
        
        NhanVien nv = new NhanVien(maNV);
        Ban ban = phieu.getBan();
        
        Thue thueMacDinh = thueDAO.getThueMacDinh(); 
        if (thueMacDinh == null) {
            thueMacDinh = new Thue("VAT08"); 
        }

        HoaDon hdMoi = new HoaDon(
                maHDMoi,
                nv,
                kh,
                ban,
                ngayLap,
                gioVao,
                gioVao, 
                "Chưa xác định", 
                null, 
                thueMacDinh,
                phieu, 
                0.0, 
                0.0, 
                false 
        );
        
        System.out.println("Debug: Tạo HD mới: " + maHDMoi + " từ PDB " + phieu.getMaPhieu()); 
        
        boolean success = addHoaDon(hdMoi);
        
        if (success) {
            success = copyChiTietTuPhieuSangHoaDon(phieu.getMaPhieu(), maHDMoi);
            
            if (success) {
                success = capNhatTongTienHoaDon(maHDMoi);
                if (success) {
                    System.out.println("Debug: Tạo HD từ PDB thành công: " + maHDMoi); 
                } else {
                    System.err.println("Debug: Cập nhật tổng tiền fail cho HD " + maHDMoi); 
                }
            } else {
                System.err.println("Debug: Copy chi tiết fail cho HD " + maHDMoi); 
            }
        } else {
            System.err.println("Debug: Insert HD fail: " + maHDMoi); 
        }
        
        return success;
    }

    public boolean copyChiTietTuPhieuSangHoaDon(String maPhieu, String maHD) {
        String sqlSelect = "SELECT maMonAn, soLuongMonAn, DonGiaBan FROM CHITIETPHIEUDATBAN WHERE maPhieu = ?";
        String sqlInsert = "INSERT INTO CHITIETHOADON (MaHD, MaMonAn, DonGiaBan, SoLuong) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement psSelect = conn.prepareStatement(sqlSelect);
             PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
            
            psSelect.setString(1, maPhieu);
            
            try (ResultSet rs = psSelect.executeQuery()) {
                boolean hasRows = false;
                while (rs.next()) {
                    hasRows = true;
                    psInsert.setString(1, maHD);
                    psInsert.setString(2, rs.getString("maMonAn"));
                    psInsert.setDouble(3, rs.getDouble("DonGiaBan"));
                    psInsert.setInt(4, rs.getInt("soLuongMonAn"));
                    psInsert.addBatch();
                }
                
                if (hasRows) {
                    psInsert.executeBatch();
                }
                return true; 
            }
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi copy chi tiết món: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean capNhatTongTienHoaDon(String maHD) {
        String sqlTinhTong = "SELECT SUM(DonGiaBan * SoLuong) AS TongTien FROM CHITIETHOADON WHERE MaHD = ?";
        String sqlUpdate = "UPDATE HOADON SET TongTienTruocThue = ? WHERE MaHD = ?";
        
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement psTinh = conn.prepareStatement(sqlTinhTong);
             PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
            
            psTinh.setString(1, maHD);
            double tongTien = 0.0;
            
            try (ResultSet rs = psTinh.executeQuery()) {
                if (rs.next()) {
                    tongTien = rs.getDouble("TongTien");
                }
            }
            
            psUpdate.setDouble(1, tongTien);
            psUpdate.setString(2, maHD);
            
            return psUpdate.executeUpdate() > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi khi cập nhật tổng tiền hóa đơn: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public String apDungMaKhuyenMai(String maHD, String maKM) {
        if (maKM == null || maKM.trim().isEmpty() || maKM.trim().equalsIgnoreCase("KM00000000")) {
            if(huyMaKhuyenMai(maHD)) {
                return "OK"; 
            } else {
                return "Lỗi khi hủy khuyến mãi.";
            }
        }
        
        KhuyenMai km = khuyenMaiDAO.getKhuyenMaiById(maKM.trim());
        if (km == null) {
            return "Mã khuyến mãi không tồn tại!";
        }

        if (!km.getTrangThaiKM()) {
            return "Mã khuyến mãi này đã bị vô hiệu hóa!";
        }

        LocalDate now = LocalDate.now();
        if (km.getNgayApDung() != null && now.isBefore(km.getNgayApDung())) {
            return "Mã khuyến mãi chưa có hiệu lực!";
        }
        if (km.getNgayHetHan() != null && now.isAfter(km.getNgayHetHan())) {
            return "Mã khuyến mãi đã hết hạn!";
        }

        HoaDon hd = findByMaHD(maHD);
        if (hd == null) {
            return "Không tìm thấy hóa đơn!";
        }

        double tongTienTruocThue = hd.getTongTienTruocThue();

        double tienGiam = tongTienTruocThue * km.getMucKM();

        if (tienGiam > tongTienTruocThue) {
            tienGiam = tongTienTruocThue;
        }

        String sql = "UPDATE HOADON SET MaKM = ?, TongGiamGia = ? WHERE MaHD = ?";
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maKM.trim());
            pstmt.setDouble(2, tienGiam);
            pstmt.setString(3, maHD);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                return "OK"; 
            }

        } catch (SQLException e) {
            System.err.println("Lỗi khi áp dụng mã KM: " + e.getMessage());
            e.printStackTrace();
        }

        return "Lỗi khi lưu mã khuyến mãi vào CSDL!";
    }

    public boolean huyMaKhuyenMai(String maHD) {
        String sql = "UPDATE HOADON SET MaKM = ?, TongGiamGia = 0 WHERE MaHD = ?";
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "KM00000000"); 
            pstmt.setString(2, maHD);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi hủy mã KM: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean thanhToanHoaDon(String maHD, String phuongThucThanhToan) {
        String sql = "UPDATE HOADON SET DaThanhToan = 1, phuongThucThanhToan = ?, GioRa = GETDATE() WHERE MaHD = ?";

        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phuongThucThanhToan);
            pstmt.setString(2, maHD);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Debug: Thanh toán HD thành công: " + maHD); 
                return true;
            } else {
                System.err.println("Debug: Thanh toán HD fail (0 rows): " + maHD); 
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi thanh toán hóa đơn: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public double tinhTongTienHoaDon(String maHoaDon) {
        HoaDon hoaDon = findByMaHD(maHoaDon);
        if (hoaDon == null) return 0;

        List<Thue> danhSachThueApDung = thueDAO.getAllActiveTaxes(); 

        double tienMonAn = hoaDon.getTongTienTruocThue();
        double tienGiamGia = hoaDon.getTongGiamGia();            

        double tienSauGiamGia = tienMonAn - tienGiamGia;
        if (tienSauGiamGia < 0) tienSauGiamGia = 0;

        double tienPhiDichVu = 0; 
        double tienVAT = 0;       
        double tyLePhiDichVu = 0; 
        double tyLeVAT = 0;

        for (Thue thue : danhSachThueApDung) { 
            if (thue.getMaSoThue().equals("PHIPK5")) {
                tyLePhiDichVu = thue.getTyLeThue(); // 0.05
            } else if (thue.getMaSoThue().equals("VAT08")) {
                tyLeVAT = thue.getTyLeThue(); // 0.08
            }
        }

        tienPhiDichVu = tienSauGiamGia * tyLePhiDichVu;
        double soTienDeTinhVAT = tienSauGiamGia+ tienPhiDichVu; 

        tienVAT = soTienDeTinhVAT * tyLeVAT; 

        double tongTienPhaiTra = tienSauGiamGia + tienPhiDichVu + tienVAT; 

        return Math.round(tongTienPhaiTra); 
    }
    
   
    public boolean updateKhachHangVaKhuyenMai(String maHD, String maKH, String maKM) {
        
        if (maKH == null || maKH.trim().isEmpty()) {
            maKH = "KH00000000"; 
        }
        
        HoaDon hd = findByMaHD(maHD);
        if (hd == null) {
            System.err.println("Lỗi: Không tìm thấy HD để cập nhật KH/KM: " + maHD);
            return false;
        }
        
        double tongTienTruocThue = hd.getTongTienTruocThue();
        double tienGiamMoi = 0.0;

        if (maKM == null || maKM.trim().isEmpty() || maKM.trim().equalsIgnoreCase("KM00000000") || maKM.trim().equalsIgnoreCase("null")) {
            maKM = "KM00000000"; 
            tienGiamMoi = 0.0;
        } else {
            KhuyenMai km = khuyenMaiDAO.getKhuyenMaiById(maKM);
            if (km == null) {
                System.err.println("Lỗi: Mã KM không tồn tại: " + maKM);
                return false;
            }
            
            tienGiamMoi = tongTienTruocThue * km.getMucKM();
            if (tienGiamMoi > tongTienTruocThue) {
                tienGiamMoi = tongTienTruocThue;
            }
        }

        String sql = "UPDATE HOADON SET MaKH = ?, MaKM = ?, TongGiamGia = ? WHERE MaHD = ?";
        
        try (Connection conn = ConnectDB.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maKH.trim());
            pstmt.setString(2, maKM.trim());
            pstmt.setDouble(3, tienGiamMoi);
            pstmt.setString(4, maHD.trim());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Lỗi nghiêm trọng khi cập nhật KH/KM cho Hóa đơn: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}