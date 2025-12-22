package dao;

import connectDB.ConnectDB;
import entity.*;
import enums.TrangThaiBan;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;

public class PhieuDatBan_DAO {

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
        Ban ban = banDAO.getBanTheoMa(rs.getString("maBan"));  // SỬA: Sử dụng Ban ban = banDAO.getBanTheoMa(...)

        int soNguoi = rs.getInt("soNguoi");
        String ghiChu = rs.getString("ghiChu");
        String trangThaiPhieu = rs.getString("trangThaiPhieu");
        
        PhieuDatBan phieu = new PhieuDatBan(maPhieu, thoiGianDenHen, thoiGianNhanBan, thoiGianTraBan, 
                                            kh, nv, ban, soNguoi, ghiChu, trangThaiPhieu);  // SỬA: Sử dụng ban thay vì "sav ban"
        return phieu;
    }

    public List<PhieuDatBan> getAllPhieuDatBan() {
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
    
    public List<PhieuDatBan> getPhieuDatBanDangHoatDong() {
        List<PhieuDatBan> list = new ArrayList<>();

        // Danh sách tạm lưu dữ liệu thô từ ResultSet
        List<PhieuDatBanTemp> tempList = new ArrayList<>();

        Set<String> maKHSet = new HashSet<>();
        Set<String> maNVSet = new HashSet<>();
        Set<String> maBanSet = new HashSet<>();

        String sql = """
            SELECT maPhieu, thoiGianDenHen, thoiGianNhanBan, thoiGianTraBan,
                   maKhachHang, maNV, maBan, soNguoi, ghiChu, trangThaiPhieu
            FROM PHIEUDATBAN
            WHERE trangThaiPhieu IN (N'Chưa đến', N'Đã đến')
              AND thoiGianDenHen >= DATEADD(day, -1, GETDATE())
              AND thoiGianDenHen <= DATEADD(day, +2, GETDATE())
            ORDER BY thoiGianDenHen DESC
            """;

        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Duyệt 1 lần duy nhất: vừa thu thập mã, vừa lưu dữ liệu tạm
            while (rs.next()) {
                String maPhieu = rs.getString("maPhieu");
                LocalDateTime thoiGianDenHen = rs.getTimestamp("thoiGianDenHen").toLocalDateTime();

                Timestamp tsNhanBan = rs.getTimestamp("thoiGianNhanBan");
                LocalDateTime thoiGianNhanBan = (tsNhanBan != null) ? tsNhanBan.toLocalDateTime() : null;

                Timestamp tsTraBan = rs.getTimestamp("thoiGianTraBan");
                LocalDateTime thoiGianTraBan = (tsTraBan != null) ? tsTraBan.toLocalDateTime() : null;

                String maKH = rs.getString("maKhachHang");
                String maNV = rs.getString("maNV");
                String maBan = rs.getString("maBan");

                int soNguoi = rs.getInt("soNguoi");
                String ghiChu = rs.getString("ghiChu");
                String trangThaiPhieu = rs.getString("trangThaiPhieu");

                // Lưu tạm
                tempList.add(new PhieuDatBanTemp(maPhieu, thoiGianDenHen, thoiGianNhanBan, thoiGianTraBan,
                        maKH, maNV, maBan, soNguoi, ghiChu, trangThaiPhieu));

                // Thu thập mã
                if (maKH != null) maKHSet.add(maKH.trim());
                if (maNV != null) maNVSet.add(maNV.trim());
                if (maBan != null) maBanSet.add(maBan.trim());
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return list;
        }

        // Load bulk 1 lần
        Map<String, KhachHang> mapKH = khachHangDAO.getAllByMaList(new ArrayList<>(maKHSet));
        Map<String, NhanVien> mapNV = nhanVienDAO.getAllByMaList(new ArrayList<>(maNVSet));
        Map<String, Ban> mapBan = banDAO.getAllByMaList(new ArrayList<>(maBanSet));

        // Duyệt danh sách tạm để tạo entity đầy đủ
        for (PhieuDatBanTemp temp : tempList) {
            KhachHang kh = mapKH.getOrDefault(temp.maKhachHang != null ? temp.maKhachHang.trim() : null, getKhachHangVangLai());
            NhanVien nv = mapNV.get(temp.maNV != null ? temp.maNV.trim() : null);
            Ban ban = mapBan.get(temp.maBan != null ? temp.maBan.trim() : null);

            PhieuDatBan phieu = new PhieuDatBan(
                    temp.maPhieu,
                    temp.thoiGianDenHen,
                    temp.thoiGianNhanBan,
                    temp.thoiGianTraBan,
                    kh, nv, ban,
                    temp.soNguoi,
                    temp.ghiChu,
                    temp.trangThaiPhieu
            );
            list.add(phieu);
        }

        return list;
    }
    private static class PhieuDatBanTemp {
        String maPhieu;
        LocalDateTime thoiGianDenHen;
        LocalDateTime thoiGianNhanBan;
        LocalDateTime thoiGianTraBan;
        String maKhachHang;
        String maNV;
        String maBan;
        int soNguoi;
        String ghiChu;
        String trangThaiPhieu;

        PhieuDatBanTemp(String maPhieu, LocalDateTime thoiGianDenHen, LocalDateTime thoiGianNhanBan,
                        LocalDateTime thoiGianTraBan, String maKhachHang, String maNV, String maBan,
                        int soNguoi, String ghiChu, String trangThaiPhieu) {
            this.maPhieu = maPhieu;
            this.thoiGianDenHen = thoiGianDenHen;
            this.thoiGianNhanBan = thoiGianNhanBan;
            this.thoiGianTraBan = thoiGianTraBan;
            this.maKhachHang = maKhachHang;
            this.maNV = maNV;
            this.maBan = maBan;
            this.soNguoi = soNguoi;
            this.ghiChu = ghiChu;
            this.trangThaiPhieu = trangThaiPhieu;
        }
    }
    public PhieuDatBan getPhieuByBan(String maBan) {
        // Sửa: Lấy phiếu "Chưa đến" HOẶC "Đã đến" (để xử lý CO_KHACH)
        String sql = "SELECT TOP 1 * FROM PHIEUDATBAN WHERE maBan = ? AND (trangThaiPhieu = N'Chưa đến' OR trangThaiPhieu = N'Đã đến') ORDER BY thoiGianDenHen DESC";
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

 // Sửa trong PhieuDatBan_DAO.java - Method insertPhieuDatBan (Sửa lỗi "sav ban" thành "phieu.getBan()")

    public boolean insertPhieuDatBan(PhieuDatBan phieu) {
        String sql = "INSERT INTO PHIEUDATBAN (maPhieu, thoiGianDenHen, maKhachHang, maNV, maBan, soNguoi, ghiChu, trangThaiPhieu) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, phieu.getMaPhieu());
            pstmt.setTimestamp(2, Timestamp.valueOf(phieu.getThoiGianDenHen()));
            pstmt.setString(3, phieu.getKhachHang().getMaKH()); 
            pstmt.setString(4, phieu.getNhanVien().getMaNhanVien()); 
            pstmt.setString(5, phieu.getBan().getMaBan());  // SỬA: Sử dụng phieu.getBan().getMaBan() thay vì "sav ban"
            pstmt.setInt(6, phieu.getSoNguoi());
            pstmt.setString(7, phieu.getGhiChu());
            pstmt.setString(8, phieu.getTrangThaiPhieu()); 
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

 // Sửa trong PhieuDatBan_DAO.java - Method updatePhieuDatBan (Thêm debug log và kiểm tra CHECK constraint)

 // Sửa trong PhieuDatBan_DAO.java - Method updatePhieuDatBan (Sửa lỗi "sav ban" thành "phieu.getBan()")

    public boolean updatePhieuDatBan(PhieuDatBan phieu) {
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
            pstmt.setString(6, phieu.getBan().getMaBan());  // SỬA: Sử dụng phieu.getBan().getMaBan() thay vì "sav ban"
            pstmt.setInt(7, phieu.getSoNguoi());
            pstmt.setString(8, phieu.getGhiChu());
            pstmt.setString(9, phieu.getTrangThaiPhieu()); 
            pstmt.setString(10, phieu.getMaPhieu());
            
            int rowsAffected = pstmt.executeUpdate();  // SỬA: Sử dụng biến rowsAffected
            if (rowsAffected > 0) {
                System.out.println("Debug: Cập nhật PDB thành công: " + phieu.getMaPhieu() + " (trạng thái: " + phieu.getTrangThaiPhieu() + ")");  // Debug log
                return true;
            } else {
                System.err.println("Debug: Cập nhật PDB fail (0 rows): " + phieu.getMaPhieu() + " (trạng thái: " + phieu.getTrangThaiPhieu() + ")");  // Debug log
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Debug: Lỗi SQL khi update PDB " + phieu.getMaPhieu() + ": " + e.getMessage() + " (trạng thái: " + phieu.getTrangThaiPhieu() + ")");  // Debug log chi tiết
            e.printStackTrace();
            return false;
        }
    }
    
    
    public boolean deletePhieuDatBan(String maPhieu) {
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
    
    // ===== HÀM ĐÃ SỬA LỖI LOGIC =====
    public String generateNewID() {
        // Cột là nchar(10), nên chúng ta dùng format PDB + 7 chữ số (PDBXXXXXXX)
        String newID = "PDB0000001"; // Fallback nếu DB trống
        
        // SỬA: Query này sẽ tìm SỐ lớn nhất, bất kể độ dài chuỗi
        String sql = "SELECT MAX(CAST(SUBSTRING(maPhieu, 4, 10) AS INT)) FROM PHIEUDATBAN WHERE maPhieu LIKE 'PDB[0-9]%'";
        
        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                int maxNum = rs.getInt(1); // Lấy SỐ lớn nhất (ví dụ: 16)
                if (maxNum == 0) {
                    // DB trống, dùng mã fallback
                    return newID;
                }
                int newNum = maxNum + 1; // 17
                newID = String.format("PDB%07d", newNum); // Format thành PDB0000017
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // Đảm bảo mã trả về không quá 10 ký tự
        if(newID.length() > 10) {
            newID = newID.substring(0, 10);
        }
        
        return newID;
    }

 // Sửa trong Ban_DAO.java - Method getAllBanByFloor() (Cập nhật trạng thái bàn dựa vào PDB "Hoàn thành")

    public Map<String, List<Ban>> getAllBanByFloor() {
        Map<String, List<Ban>> banTheoKhuVuc = new LinkedHashMap<>();

        String sql = """
            SELECT 
                b.maBan, 
                b.soCho, 
                b.loaiBan, 
                b.trangThai,
                kv.tenKhuVuc
            FROM Ban b
            INNER JOIN KHUVUC kv ON b.maKhuVuc = kv.maKhuVuc
            WHERE (b.trangThai != N'ĐÃ XÓA' OR b.trangThai IS NULL)
              AND kv.trangThai = 1
            ORDER BY kv.tenKhuVuc, b.maBan
            """;

        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String tenKhuVuc = rs.getString("tenKhuVuc").trim();

                // Tạo bàn với KhuVuc chỉ có tên (đủ dùng cho view)
                KhuVuc khuVucTam = new KhuVuc(null, tenKhuVuc, null, true);

                Ban ban = new Ban(
                    rs.getString("maBan").trim(),
                    rs.getInt("soCho"),
                    khuVucTam,
                    rs.getString("loaiBan"),
                    rs.getString("trangThai")
                );

                banTheoKhuVuc.computeIfAbsent(tenKhuVuc, k -> new ArrayList<>()).add(ban);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return new LinkedHashMap<>();
        }

        // === Cập nhật trạng thái realtime từ phiếu đang hoạt động ===
        List<PhieuDatBan> phieuHoatDong = getPhieuDatBanDangHoatDong();

        // Tạo map để tra cứu nhanh
        Map<String, PhieuDatBan> mapPhieu = new LinkedHashMap<>();
        for (PhieuDatBan p : phieuHoatDong) {
            if (p.getBan() != null) {
                mapPhieu.put(p.getBan().getMaBan(), p);
            }
        }

        // Duyệt và cập nhật trạng thái
        for (List<Ban> listBan : banTheoKhuVuc.values()) {
            for (Ban ban : listBan) {
                // Lấy trạng thái mới nhất từ DB (nếu admin đổi thủ công)
                Ban banMoiNhat = banDAO.getBanTheoMa(ban.getMaBan());
                if (banMoiNhat != null) {
                    ban.setTrangThai(banMoiNhat.getTrangThai());
                }

                PhieuDatBan phieu = mapPhieu.get(ban.getMaBan());
                if (phieu != null) {
                    if ("Chưa đến".equals(phieu.getTrangThaiPhieu().trim())) {
                        ban.setTrangThai(TrangThaiBan.DA_DAT.name());
                    } else if ("Đã đến".equals(phieu.getTrangThaiPhieu().trim())) {
                        ban.setTrangThai(TrangThaiBan.CO_KHACH.name());
                    }
                } else if (!TrangThaiBan.CO_KHACH.name().equals(ban.getTrangThai())) {
                    ban.setTrangThai(TrangThaiBan.TRONG.name());
                }
            }
        }

        return banTheoKhuVuc;
    }

    // ====================================================================
    // PHẦN CHITIETPHIEUDATBAN (CTPDB)
    // ====================================================================

    public List<ChiTietPhieuDatBan> getChiTietByPhieuId(String maPhieu) {
        List<ChiTietPhieuDatBan> dsCT = new ArrayList<>();
        String sql = "SELECT maMonAn, soLuongMonAn, DonGiaBan, ghiChu FROM CHITIETPHIEUDATBAN WHERE maPhieu = ?";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maPhieu);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    PhieuDatBan phieu = new PhieuDatBan(maPhieu);
                    MonAn mon = monAnDAO.getMonAnTheoMa(rs.getString("maMonAn")); 
                    int soLuong = rs.getInt("soLuongMonAn");
                    double donGiaBan = rs.getDouble("DonGiaBan");
                    String ghiChu = rs.getString("ghiChu");
                    
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
    
    public KhachHang getKhachHangVangLai() {
        return khachHangDAO.getKhachHangById("KH00000000");
    }

    public boolean updateTrangThai(String maPhieu, String trangThaiMoi) {
        String sql = "UPDATE PHIEUDATBAN SET trangThaiPhieu = ? WHERE maPhieu = ?";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, trangThaiMoi);
            pstmt.setString(2, maPhieu);
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean capNhatThoiGianNhanBan(String maPhieu, LocalDateTime thoiGianNhanBan) {
        String sql = "UPDATE PHIEUDATBAN " +
                     "SET [thoiGianNhanBan] = ?, [trangThaiPhieu] = N'Đã đến' " +
                     "WHERE [maPhieu] = ?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setTimestamp(1, Timestamp.valueOf(thoiGianNhanBan));
            pstmt.setString(2, maPhieu);

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}
