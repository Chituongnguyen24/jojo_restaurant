package dao;

import connectDB.ConnectDB;
import entity.*;
import enums.TrangThaiBan;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

public class ChiTietPhieuDatBan_DAO {

    private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    private final NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
    private final Ban_DAO banDAO = new Ban_DAO();
    private final MonAn_DAO monAnDAO = new MonAn_DAO();

    private PhieuDatBan mapResultSetToPhieuDatBan(ResultSet rs) throws SQLException {
        String maPhieu = rs.getString("maPhieu");
        LocalDateTime thoiGianDenHen = rs.getTimestamp("thoiGianDenHen").toLocalDateTime();
        
        Timestamp tsNhanBan = rs.getTimestamp("thoiGianNhanBan");
        LocalDateTime thoiGianNhanBan = (tsNhanBan != null) ? tsNhanBan.toLocalDateTime() : null;

        Timestamp tsTraBan = rs.getTimestamp("thoiGianTraBan");
        LocalDateTime thoiGianTraBan = (tsTraBan != null) ? tsTraBan.toLocalDateTime() : null;

        KhachHang kh = khachHangDAO.getKhachHangById(rs.getString("maKhachHang"));
        NhanVien nv = nhanVienDAO.getNhanVienById(rs.getString("maNV"));
        Ban ban = banDAO.getBanTheoMa(rs.getString("maBan"));

        int soNguoi = rs.getInt("soNguoi");
        String ghiChu = rs.getString("ghiChu");
        String trangThaiPhieu = rs.getString("trangThaiPhieu");
        
        // Dùng constructor 10 tham số (Entity PhieuDatBan mới)
        PhieuDatBan phieu = new PhieuDatBan(maPhieu, thoiGianDenHen, thoiGianNhanBan, thoiGianTraBan, 
                                             kh, nv, ban, soNguoi, ghiChu, trangThaiPhieu);
        return phieu;
    }

    public List<PhieuDatBan> getAllPhieuDatBan() {
        // ... (Giữ nguyên)
        List<PhieuDatBan> dsPDB = new ArrayList<>();
        String sql = "SELECT * FROM PHIEUDATBAN ORDER BY thoiGianDenHen DESC";
        
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                dsPDB.add(mapResultSetToPhieuDatBan(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPDB;
    }

    public PhieuDatBan getPhieuDatBanById(String maPhieu) {
        // ... (Giữ nguyên)
        String sql = "SELECT * FROM PHIEUDATBAN WHERE maPhieu = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maPhieu);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhieuDatBan(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public PhieuDatBan getPhieuByBan(String maBan) {
        // ... (Giữ nguyên)
        String sql = "SELECT TOP 1 * FROM PHIEUDATBAN WHERE maBan = ? AND trangThaiPhieu = N'Chưa đến' ORDER BY thoiGianDenHen ASC";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maBan);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPhieuDatBan(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean insertPhieuDatBan(PhieuDatBan phieu) {
        // ... (Giữ nguyên)
        String sql = "INSERT INTO PHIEUDATBAN (maPhieu, thoiGianDenHen, maKhachHang, maNV, maBan, soNguoi, ghiChu, trangThaiPhieu) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, phieu.getMaPhieu());
            pstmt.setTimestamp(2, Timestamp.valueOf(phieu.getThoiGianDenHen()));
            pstmt.setString(3, phieu.getKhachHang().getMaKH());
            pstmt.setString(4, phieu.getNhanVien().getMaNhanVien());
            pstmt.setString(5, phieu.getBan().getMaBan());
            pstmt.setInt(6, phieu.getSoNguoi());
            pstmt.setString(7, phieu.getGhiChu());
            pstmt.setString(8, phieu.getTrangThaiPhieu()); 
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePhieuDatBan(PhieuDatBan phieu) {
        // ... (Giữ nguyên)
        String sql = "UPDATE PHIEUDATBAN SET thoiGianDenHen = ?, thoiGianNhanBan = ?, thoiGianTraBan = ?, maKhachHang = ?, maNV = ?, maBan = ?, soNguoi = ?, ghiChu = ?, trangThaiPhieu = ? WHERE maPhieu = ?";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(phieu.getThoiGianDenHen()));
            
            if (phieu.getThoiGianNhanBan() != null)
                pstmt.setTimestamp(2, Timestamp.valueOf(phieu.getThoiGianNhanBan()));
            else
                pstmt.setNull(2, Types.TIMESTAMP);
            
            if (phieu.getThoiGianTraBan() != null)
                pstmt.setTimestamp(3, Timestamp.valueOf(phieu.getThoiGianTraBan()));
            else
                pstmt.setNull(3, Types.TIMESTAMP);
            
            pstmt.setString(4, phieu.getKhachHang().getMaKH());
            pstmt.setString(5, phieu.getNhanVien().getMaNhanVien());
            pstmt.setString(6, phieu.getBan().getMaBan());
            pstmt.setInt(7, phieu.getSoNguoi());
            pstmt.setString(8, phieu.getGhiChu());
            pstmt.setString(9, phieu.getTrangThaiPhieu()); 
            pstmt.setString(10, phieu.getMaPhieu());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deletePhieuDatBan(String maPhieu) {
        // ... (Giữ nguyên)
        String sqlDeleteCT = "DELETE FROM CHITIETPHIEUDATBAN WHERE maPhieu = ?";
        String sqlDeletePDB = "DELETE FROM PHIEUDATBAN WHERE maPhieu = ?";
        
        try (Connection conn = ConnectDB.getConnection()) {
            conn.setAutoCommit(false);
            
            try (PreparedStatement ps1 = conn.prepareStatement(sqlDeleteCT);
                 PreparedStatement ps2 = conn.prepareStatement(sqlDeletePDB)) {
                
                ps1.setString(1, maPhieu);
                ps1.executeUpdate();

                ps2.setString(1, maPhieu);
                ps2.executeUpdate();

                conn.commit();
                return true;
                
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public String generateNewID() {
        // ... (Giữ nguyên)
        String newID = "PDB00001";
        String sql = "SELECT TOP 1 maPhieu FROM PHIEUDATBAN ORDER BY maPhieu DESC";
        
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                String lastID = rs.getString("maPhieu");
                if (lastID != null && lastID.matches("PDB\\d{5}")) {
                    int num = Integer.parseInt(lastID.trim().substring(3)) + 1;
                    newID = String.format("PDB%05d", num);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return newID;
    }

    public Map<String, List<Ban>> getAllBanByFloor() {
        // ... (Giữ nguyên)
        Map<String, List<Ban>> banTheoKhuVuc = new LinkedHashMap<>();
        
        try {
            List<Ban> allBan = banDAO.getAllBan();
            List<PhieuDatBan> phieuDat = getAllPhieuDatBan();
            
            for (Ban ban : allBan) {
                String maKV = ban.getKhuVuc().getMaKhuVuc();
                String tenKV = banDAO.getTenKhuVuc(maKV);
                
                if (!banTheoKhuVuc.containsKey(tenKV)) {
                    banTheoKhuVuc.put(tenKV, new ArrayList<>());
                }
                
                ban.setTrangThai(TrangThaiBan.TRONG.name()); 

                for (PhieuDatBan phieu : phieuDat) {
                    if (phieu.getBan() != null && phieu.getBan().getMaBan().trim().equals(ban.getMaBan().trim()) && phieu.getTrangThaiPhieu().equals("Chưa đến")) {
                         ban.setTrangThai(TrangThaiBan.DA_DAT.name()); 
                         break;
                    }
                }
                
                banTheoKhuVuc.get(tenKV).add(ban);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            return null; 
        }
        
        return banTheoKhuVuc;
    }


    public List<ChiTietPhieuDatBan> getChiTietByPhieuId(String maPhieu) {
        List<ChiTietPhieuDatBan> dsCT = new ArrayList<>();
        
        // SỬA SQL: Join thêm bảng PHIEUDATBAN, NHANVIEN, KHACHHANG để lấy tên
        String sql = "SELECT ct.maMonAn, ct.soLuongMonAn, ct.DonGiaBan, ct.ghiChu, " +
                     "nv.tenNV, kh.tenKH " + // Lấy thêm cột tên
                     "FROM CHITIETPHIEUDATBAN ct " +
                     "JOIN PHIEUDATBAN pdb ON ct.maPhieu = pdb.maPhieu " +
                     "LEFT JOIN NHANVIEN nv ON pdb.maNV = nv.maNV " +
                     "LEFT JOIN KHACHHANG kh ON pdb.maKhachHang = kh.maKH " +
                     "WHERE ct.maPhieu = ?";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maPhieu);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 1. Tạo đối tượng Nhân viên và set tên
                    NhanVien nv = new NhanVien();
                    nv.setHoTen(sql);

                    // 2. Tạo đối tượng Khách hàng và set tên
                    KhachHang kh = new KhachHang();
                    kh.setTenKH(rs.getString("tenKH"));

                    // 3. Tạo PhieuDatBan và gán NV, KH vào
                    PhieuDatBan phieu = new PhieuDatBan(maPhieu);
                    phieu.setNhanVien(nv);
                    phieu.setKhachHang(kh);

                    // 4. Lấy thông tin món ăn
                    MonAn mon = monAnDAO.getMonAnTheoMa(rs.getString("maMonAn")); 
                    
                    int soLuong = rs.getInt("soLuongMonAn");
                    double donGiaBan = rs.getDouble("DonGiaBan");
                    String ghiChu = rs.getString("ghiChu");
                    
                    // 5. Tạo chi tiết phiếu
                    ChiTietPhieuDatBan ct = new ChiTietPhieuDatBan(mon, phieu, soLuong, donGiaBan, ghiChu);
                    dsCT.add(ct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsCT;
    }

    public boolean addOrUpdateChiTiet(String maPhieu, String maMonAn, int soLuong, String ghiChu) {
        // ... (Giữ nguyên)
        PhieuDatBan phieu = getPhieuDatBanById(maPhieu);
        MonAn mon = monAnDAO.getMonAnTheoMa(maMonAn);

        if (phieu == null || mon == null) return false;

        String sqlCheck = "SELECT soLuongMonAn, DonGiaBan FROM CHITIETPHIEUDATBAN WHERE maPhieu = ? AND maMonAn = ?";
        String sqlUpdate = "UPDATE CHITIETPHIEUDATBAN SET soLuongMonAn = ?, ghiChu = ?, DonGiaBan = ? WHERE maPhieu = ? AND maMonAn = ?";
        String sqlInsert = "INSERT INTO CHITIETPHIEUDATBAN (maPhieu, maMonAn, soLuongMonAn, DonGiaBan, ghiChu) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement psCheck = conn.prepareStatement(sqlCheck);
             PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate);
             PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {

            psCheck.setString(1, maPhieu);
            psCheck.setString(2, maMonAn);
            
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next()) {
                    // Update
                    int currentSL = rs.getInt("soLuongMonAn");
                    int newSL = currentSL + soLuong;
                    
                    psUpdate.setInt(1, newSL);
                    psUpdate.setString(2, ghiChu);
                    psUpdate.setDouble(3, mon.getDonGia()); // Lấy đơn giá hiện tại của món
                    psUpdate.setString(4, maPhieu);
                    psUpdate.setString(5, maMonAn);
                    return psUpdate.executeUpdate() > 0;
                } else {
                    // Insert
                    psInsert.setString(1, maPhieu);
                    psInsert.setString(2, maMonAn);
                    psInsert.setInt(3, soLuong);
                    psInsert.setDouble(4, mon.getDonGia()); // Lấy đơn giá hiện tại của món
                    psInsert.setString(5, ghiChu);
                    return psInsert.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteChiTiet(String maPhieu, String maMonAn) {
        // ... (Giữ nguyên)
        String sql = "DELETE FROM CHITIETPHIEUDATBAN WHERE maPhieu = ? AND maMonAn = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maPhieu);
            pstmt.setString(2, maMonAn);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Object[]> getChiTietTheoMaPhieu(String maPhieu) {
        // ... (Giữ nguyên)
        List<Object[]> result = new ArrayList<>();
        String sql = 
            "SELECT ct.maMonAn, ma.tenMonAn, ct.soLuongMonAn, ct.ghiChu " +
            "FROM CHITIETPHIEUDATBAN ct JOIN MONAN ma ON ct.maMonAn = ma.maMonAn " +
            "WHERE ct.maPhieu = ?";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maPhieu);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    result.add(new Object[]{
                        rs.getString("maMonAn"),
                        rs.getString("tenMonAn"),
                        rs.getInt("soLuongMonAn"),
                        rs.getString("ghiChu")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
