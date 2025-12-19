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
        Boolean gioiTinh = (Boolean) rs.getObject("GioiTinh");

        return new KhachHang(
            rs.getString("MaKH"),
            rs.getString("TenKH"),
            rs.getString("SoDienThoai"),
            rs.getString("Email"),
            ngaySinh,
            gioiTinh,
            rs.getInt("DiemTichLuy"),
            rs.getBoolean("LaThanhVien")
        );
    }

    public List<KhachHang> getAllKhachHang() {
        List<KhachHang> ds = new ArrayList<>();
        // SỬA: Lọc khách hàng chưa bị ẩn - chỉ lấy thành viên hoặc khách vãng lai
        String sql = "SELECT * FROM KHACHHANG WHERE (LaThanhVien = 1 OR MaKH = 'KH00000000') AND (TrangThai IS NULL OR TrangThai != N'Đã xóa') ORDER BY CASE WHEN MaKH = 'KH00000000' THEN 0 ELSE 1 END, TenKH";
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

    public boolean isSoDienThoaiExists(String sdt, String currentMaKH) {
        String sql = "SELECT COUNT(*) FROM KHACHHANG WHERE SoDienThoai = ? AND MaKH != ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, sdt);
            stmt.setString(2, currentMaKH != null ? currentMaKH : "KH00000000");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEmailExists(String email, String currentMaKH) {
        if (email == null || email.isEmpty()) return false;

        String sql = "SELECT COUNT(*) FROM KHACHHANG WHERE Email = ? AND MaKH != ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, currentMaKH != null ? currentMaKH : "KH00000000");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean themtKhachHang(KhachHang kh) {
        String sql = "INSERT INTO KHACHHANG(MaKH, TenKH, SoDienThoai, Email, NgaySinh, GioiTinh, DiemTichLuy, LaThanhVien) VALUES (?, ?, ?, ?, ?, ?, ?, 1)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, kh.getMaKH());
            stmt.setString(2, kh.getTenKH());
            stmt.setString(3, kh.getSoDienThoai());
            stmt.setString(4, kh.getEmail());
            stmt.setDate(5, kh.getNgaySinh() != null ? Date.valueOf(kh.getNgaySinh()) : null);

            if (kh.getGioiTinh() != null) stmt.setBoolean(6, kh.getGioiTinh());
            else stmt.setNull(6, java.sql.Types.BIT);

            stmt.setInt(7, kh.getDiemTichLuy());
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean capNhatKhachHang(KhachHang kh) {
        String sql = "UPDATE KHACHHANG SET TenKH=?, SoDienThoai=?, Email=?, NgaySinh=?, GioiTinh=?, DiemTichLuy=?, LaThanhVien=? WHERE MaKH=?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, kh.getTenKH());
            stmt.setString(2, kh.getSoDienThoai());
            stmt.setString(3, kh.getEmail());
            stmt.setDate(4, kh.getNgaySinh() != null ? Date.valueOf(kh.getNgaySinh()) : null);
            if (kh.getGioiTinh() != null) stmt.setBoolean(5, kh.getGioiTinh());
            else stmt.setNull(5, java.sql.Types.BIT);

            stmt.setInt(6, kh.getDiemTichLuy());
            stmt.setBoolean(7, kh.isLaThanhVien());
            stmt.setString(8, kh.getMaKH());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Ẩn khách hàng khỏi UI bằng cách đánh dấu trạng thái "Đã xóa"
     * Thay vì xóa hẳn khỏi database
     */
    public boolean anKhachHang(String maKH) {
        // Giả sử bảng KHACHHANG có cột TrangThai (nếu chưa có cần ALTER TABLE)
        String sql = "UPDATE KHACHHANG SET TrangThai = N'Đã xóa', LaThanhVien = 0 WHERE MaKH = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maKH);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoaKhachHang(String maKH) {
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

    public List<KhachHang> timKhachHang(String keyword) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KHACHHANG WHERE (TenKH LIKE ? OR SoDienThoai LIKE ?) AND LaThanhVien = 1";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) ds.add(createKhachHangFromResultSet(rs));

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
            if (rs.next()) return createKhachHangFromResultSet(rs);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean congDiemTichLuy(String maKH, int diemCongThem) {
        String sql = "UPDATE KhachHang SET DiemTichLuy = DiemTichLuy + ? WHERE MaKH = ?";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, diemCongThem);
            pstmt.setString(2, maKH);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String xepHangKhachHang(int diem) {
        if (diem < 200) return "Đồng";
        else if (diem < 450) return "Bạc";
        else return "Vàng";
    }

    public double getUuDaiTheoHang(String hang, boolean laSinhNhat) {
        if (hang.equals("Đồng")) return laSinhNhat ? 0.10 : 0.05;
        if (hang.equals("Bạc")) return laSinhNhat ? 0.15 : 0.10;
        return laSinhNhat ? 0.20 : 0.15;
    }

    public List<KhachHang> timBangSDT(String sdt) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KHACHHANG WHERE SoDienThoai LIKE ? AND LaThanhVien = 1";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, "%" + sdt + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) ds.add(createKhachHangFromResultSet(rs));

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

            if (rs.next()) return rs.getInt("SoLuong");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getSoLuongKhachDaDatHang(java.util.Date from, java.util.Date to) {
        if (from == null || to == null) return getSoLuongKhachDaDatHang();

        String sql = "SELECT COUNT(DISTINCT MaKH) AS SoLuong FROM HOADON " +
                     "WHERE DaThanhToan = 1 AND MaKH IS NOT NULL AND NgayLapHoaDon BETWEEN ? AND ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setDate(1, new java.sql.Date(from.getTime()));
            stmt.setDate(2, new java.sql.Date(to.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("SoLuong");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String taoMaKHMoi() {
        String lastMaKH = "";
        String sql = "SELECT TOP 1 MaKH FROM KHACHHANG WHERE MaKH != 'KH00000000' ORDER BY MaKH DESC";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) lastMaKH = rs.getString(1).trim();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (lastMaKH.isEmpty()) return "KH25000001";
        String numPart = lastMaKH.substring(2);
        int newNum = Integer.parseInt(numPart) + 1;
        return "KH" + String.format("%08d", newNum);
    }

    public List<Object[]> getTop10KhachHang(java.util.Date from, java.util.Date to) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT TOP 10 kh.TenKH, COUNT(DISTINCT hd.MaHD) AS SoDonHang, SUM(cthd.SoLuong * cthd.DonGiaBan) AS TongChiTieu " +
                     "FROM KHACHHANG kh " +
                     "INNER JOIN HOADON hd ON kh.MaKH = hd.MaKH " +
                     "INNER JOIN CHITIETHOADON cthd ON hd.MaHD = cthd.MaHD " +
                     "WHERE hd.DaThanhToan = 1 AND kh.MaKH != 'KH00000000' ";

        if (from != null) sql += "AND hd.NgayLapHoaDon >= ? ";
        if (to != null) sql += "AND hd.NgayLapHoaDon <= ? ";

        sql += "GROUP BY kh.MaKH, kh.TenKH ORDER BY TongChiTieu DESC";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            int paramIndex = 1;
            if (from != null) stmt.setTimestamp(paramIndex++, new Timestamp(from.getTime()));
            if (to != null) stmt.setTimestamp(paramIndex++, new Timestamp(to.getTime()));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Object[] row = new Object[3];
                    row[0] = rs.getString("TenKH");
                    row[1] = rs.getInt("SoDonHang");
                    row[2] = rs.getDouble("TongChiTieu");
                    result.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public KhachHang getKhachHangBySDT(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) return null;

        String sql = "SELECT * FROM KHACHHANG WHERE SoDienThoai = ? AND MaKH != 'KH00000000'";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, soDienThoai.trim());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) return createKhachHangFromResultSet(rs);

        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm khách hàng theo SĐT: " + e.getMessage());
        }
        return null;
    }

    public KhachHang getKhachHangVangLai() {
        return getKhachHangById("KH00000000");
    }

    public boolean taoKhachHangNhanh(String tenKH, String soDienThoai) {
        if (tenKH == null || tenKH.trim().isEmpty() || soDienThoai == null || soDienThoai.trim().isEmpty()) return false;
        if (getKhachHangBySDT(soDienThoai) != null) return false;

        String maKHMoi = generateNewID();
        String sql = "INSERT INTO KHACHHANG (MaKH, TenKH, SoDienThoai, Email, GioiTinh, DiemTichLuy, LaThanhVien) VALUES (?, ?, ?, NULL, NULL, 0, 1)";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maKHMoi);
            pstmt.setString(2, tenKH.trim());
            pstmt.setString(3, soDienThoai.trim());
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo khách hàng nhanh: " + e.getMessage());
        }
        return false;
    }

    public String generateNewID() {
        String newID = "KH00000001";
        String sql = "SELECT TOP 1 MaKH FROM KHACHHANG WHERE MaKH != 'KH00000000' ORDER BY MaKH DESC";
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastID = rs.getString("MaKH");
                if (lastID != null && lastID.matches("KH\\d{8}")) {
                    int num = Integer.parseInt(lastID.substring(2)) + 1;
                    newID = String.format("KH%08d", num);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tạo mã KH: " + e.getMessage());
        }
        return newID;
    }
}