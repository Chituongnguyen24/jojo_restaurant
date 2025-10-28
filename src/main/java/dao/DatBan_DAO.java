package dao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connectDB.ConnectDB;
import entity.Ban;
import entity.ChiTietPhieuDatBan;
import entity.KhachHang;
import entity.MonAn; // Import đã được thêm vào
import entity.NhanVien;
import entity.PhieuDatBan;
import enums.LoaiBan;
import enums.TrangThaiBan;

public class DatBan_DAO {

    public List<PhieuDatBan> getAllPhieuDatBan() {
        List<PhieuDatBan> danhSach = new ArrayList<>();
        String truyVan = "SELECT p.MaPhieu, p.ThoiGianDat, p.TienCoc, " +
                     "p.soNguoi, p.ghiChu, " +
                     "k.MaKhachHang, k.TenKhachHang, k.SDT, k.Email, k.DiemTichLuy, k.LaThanhVien, " +
                     "nv.MaNV, nv.TenNhanVien, nv.GioiTinh, nv.SDT AS NV_SDT, nv.Email AS NV_Email, " +
                     "b.MaBan, b.SoCho, b.LoaiBan, b.MaKhuVuc, b.TrangThai AS TrangThaiBan " +
                     "FROM PhieuDatBan p " +
                     "JOIN KhachHang k ON p.MaKhachHang = k.MaKhachHang " +
                     "JOIN NhanVien nv ON p.MaNV = nv.MaNV " +
                     "JOIN Ban b ON p.MaBan = b.MaBan";

        try (Connection ketNoi = ConnectDB.getConnection();
             Statement lenh = ketNoi.createStatement();
             ResultSet ketQua = lenh.executeQuery(truyVan)) {

            while (ketQua.next()) {
                PhieuDatBan phieu = createPhieuDatBanFromResultSet(ketQua);
                danhSach.add(phieu);
            }
        } catch (SQLException loi) {
            loi.printStackTrace();
        }
        return danhSach;
    }

    //search by customer name
    public List<PhieuDatBan> searchByCustomerName(String ten) {
        List<PhieuDatBan> danhSach = new ArrayList<>();
        String truyVan = "SELECT p.MaPhieu, p.ThoiGianDat, p.TienCoc, " +
                     "p.soNguoi, p.ghiChu, " +
                     "k.MaKhachHang, k.TenKhachHang, k.SDT, k.Email, k.DiemTichLuy, k.LaThanhVien, " +
                     "nv.MaNV, nv.TenNhanVien, nv.GioiTinh, nv.SDT AS NV_SDT, nv.Email AS NV_Email, " +
                     "b.MaBan, b.SoCho, b.LoaiBan, b.MaKhuVuc, b.TrangThai AS TrangThaiBan " +
                     "FROM PhieuDatBan p " +
                     "JOIN KhachHang k ON p.MaKhachHang = k.MaKhachHang " +
                     "JOIN NhanVien nv ON p.MaNV = nv.MaNV " +
                     "JOIN Ban b ON p.MaBan = b.MaBan " +
                     "WHERE k.TenKhachHang LIKE ?";

        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
            lenh.setString(1, "%" + ten + "%");
            try (ResultSet ketQua = lenh.executeQuery()) {
                while (ketQua.next()) {
                    danhSach.add(createPhieuDatBanFromResultSet(ketQua));
                }
            }
        } catch (SQLException loi) {
            loi.printStackTrace();
        }
        return danhSach;
    }

    public List<PhieuDatBan> searchByPhone(String sdt) {
        List<PhieuDatBan> danhSach = new ArrayList<>();
        String truyVan = "SELECT p.MaPhieu, p.ThoiGianDat, p.TienCoc, " +
                     "p.soNguoi, p.ghiChu, " +
                     "k.MaKhachHang, k.TenKhachHang, k.SDT, k.Email, k.DiemTichLuy, k.LaThanhVien, " +
                     "nv.MaNV, nv.TenNhanVien, nv.GioiTinh, nv.SDT AS NV_SDT, nv.Email AS NV_Email, " +
                     "b.MaBan, b.SoCho, b.LoaiBan, b.MaKhuVuc, b.TrangThai AS TrangThaiBan " +
                     "FROM PhieuDatBan p " +
                     "JOIN KhachHang k ON p.MaKhachHang = k.MaKhachHang " +
                     "JOIN NhanVien nv ON p.MaNV = nv.MaNV " +
                     "JOIN Ban b ON p.MaBan = b.MaBan " +
                     "WHERE k.SDT LIKE ?"; 

        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
            lenh.setString(1, "%" + sdt + "%");
            try (ResultSet ketQua = lenh.executeQuery()) {
                while (ketQua.next()) {
                    danhSach.add(createPhieuDatBanFromResultSet(ketQua));
                }
            }
        } catch (SQLException loi) {
            loi.printStackTrace();
        }
        return danhSach;
    }

    public List<PhieuDatBan> getPhieuDatBanByTimeRange(LocalDateTime start, LocalDateTime end) {
        List<PhieuDatBan> danhSach = new ArrayList<>();
        String truyVan = "SELECT p.MaPhieu, p.ThoiGianDat, p.TienCoc, " +
                     "p.soNguoi, p.ghiChu, " +
                     "k.MaKhachHang, k.TenKhachHang, k.SDT, k.Email, k.DiemTichLuy, k.LaThanhVien, " +
                     "nv.MaNV, nv.TenNhanVien, nv.GioiTinh, nv.SDT AS NV_SDT, nv.Email AS NV_Email, " +
                     "b.MaBan, b.SoCho, b.LoaiBan, b.MaKhuVuc, b.TrangThai AS TrangThaiBan " +
                     "FROM PhieuDatBan p " +
                     "JOIN KhachHang k ON p.MaKhachHang = k.MaKhachHang " +
                     "JOIN NhanVien nv ON p.MaNV = nv.MaNV " +
                     "JOIN Ban b ON p.MaBan = b.MaBan " +
                     "WHERE p.ThoiGianDat BETWEEN ? AND ?";

        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
            lenh.setTimestamp(1, Timestamp.valueOf(start));
            lenh.setTimestamp(2, Timestamp.valueOf(end));
            try (ResultSet ketQua = lenh.executeQuery()) {
                while (ketQua.next()) {
                    danhSach.add(createPhieuDatBanFromResultSet(ketQua));
                }
            }
        } catch (SQLException loi) {
            loi.printStackTrace();
        }
        return danhSach;
    }

    public boolean insertPhieuDatBan(PhieuDatBan p) {
        String sql = "INSERT INTO PhieuDatBan(MaPhieu, ThoiGianDat, MaKhachHang, MaNV, MaBan, SoNguoi, TienCoc, GhiChu) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getMaPhieu());
            ps.setTimestamp(2, Timestamp.valueOf(p.getThoiGianDat()));
            ps.setString(3, p.getKhachHang().getMaKhachHang());
            ps.setString(4, p.getNhanVien().getMaNV());
            ps.setString(5, p.getBan().getMaBan());
            ps.setInt(6, p.getSoNguoi());
            ps.setDouble(7, p.getTienCoc());
            ps.setString(8, p.getGhiChu());

            int soDong = ps.executeUpdate();
            if (soDong > 0) {
                // Khi thêm phiếu, cập nhật trạng thái bàn trong DB ngay lập tức
                updateTableStatus(p.getBan().getMaBan(), TrangThaiBan.DA_DAT);
            }
            return soDong > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePhieuDatBan(PhieuDatBan phieu) {
        String truyVan = "UPDATE PhieuDatBan SET ThoiGianDat = ?, MaKhachHang = ?, MaNV = ?, MaBan = ?, TienCoc = ?, " +
                     "soNguoi = ?, ghiChu = ? " +
                     "WHERE MaPhieu = ?";

        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {

            lenh.setTimestamp(1, Timestamp.valueOf(phieu.getThoiGianDat()));
            lenh.setString(2, phieu.getKhachHang().getMaKhachHang());
            lenh.setString(3, phieu.getNhanVien().getMaNV());
            lenh.setString(4, phieu.getBan().getMaBan());
            lenh.setDouble(5, phieu.getTienCoc());
            lenh.setInt(6, phieu.getSoNguoi()); 
            lenh.setString(7, phieu.getGhiChu());
            lenh.setString(8, phieu.getMaPhieu()); 

            int soDong = lenh.executeUpdate();
            if (soDong > 0) {
                // Cập nhật trạng thái bàn trong DB
                updateTableStatus(phieu.getBan().getMaBan(), TrangThaiBan.DA_DAT);
            }
            return soDong > 0;
        } catch (SQLException loi) {
            loi.printStackTrace();
        }
        return false;
    }

    public boolean cancelPhieuDatBan(String maPhieu, String lyDoHuy) {
        // Hàm này có vẻ là stub, logic hủy đặt đang được xử lý ở View
        // bằng cách gọi deletePhieuDatBan() và Ban_DAO.capNhatBan()
        // Tạm thời để trống và trả về false để không phá vỡ logic hiện tại.
        return false; 
    }

    public PhieuDatBan getPhieuDatBanById(String maPhieu) {
        String truyVan = "SELECT p.MaPhieu, p.ThoiGianDat, p.TienCoc, " +
                     "p.soNguoi, p.ghiChu, " +
                     "k.MaKhachHang, k.TenKhachHang, k.SDT, k.Email, k.DiemTichLuy, k.LaThanhVien, " +
                     "nv.MaNV, nv.TenNhanVien, nv.GioiTinh, nv.SDT AS NV_SDT, nv.Email AS NV_Email, " +
                     "b.MaBan, b.SoCho, b.LoaiBan, b.MaKhuVuc, b.TrangThai AS TrangThaiBan " +
                     "FROM PhieuDatBan p " +
                     "JOIN KhachHang k ON p.MaKhachHang = k.MaKhachHang " +
                     "JOIN NhanVien nv ON p.MaNV = nv.MaNV " +
                     "JOIN Ban b ON p.MaBan = b.MaBan " +
                     "WHERE p.MaPhieu = ?";

        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
            lenh.setString(1, maPhieu);
            try (ResultSet ketQua = lenh.executeQuery()) {
                if (ketQua.next()) {
                    return createPhieuDatBanFromResultSet(ketQua);
                }
            }
        } catch (SQLException loi) {
            loi.printStackTrace();
        }
        return null;
    }

    public boolean updateTableStatus(String maBan, TrangThaiBan trangThai) {
        String truyVan = "UPDATE Ban SET TrangThai = ? WHERE MaBan = ?";
        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {

            lenh.setString(1, trangThai.toString()); // Dùng toString() của Enum
            lenh.setString(2, maBan);

            return lenh.executeUpdate() > 0;

        } catch (SQLException loi) {
            loi.printStackTrace();
        }
        return false;
    }

    public Map<String, List<Ban>> getAllBanByFloor() {
        Map<String, List<Ban>> banTheoTang = new HashMap<>();
        String truyVan = "SELECT b.MaBan, b.SoCho, b.LoaiBan, b.MaKhuVuc, b.TrangThai, k.TenKhuVuc " +
                         "FROM Ban b JOIN KHUVUC k ON b.MaKhuVuc = k.MaKhuVuc";

        try (Connection ketNoi = ConnectDB.getConnection();
             Statement lenh = ketNoi.createStatement();
             ResultSet ketQua = lenh.executeQuery(truyVan)) {

            while (ketQua.next()) {
                Ban ban = new Ban(
                    ketQua.getString("MaBan"),
                    ketQua.getInt("SoCho"),
                    LoaiBan.fromTenHienThi(ketQua.getString("LoaiBan")),
                    ketQua.getString("MaKhuVuc"),
                    TrangThaiBan.fromString(ketQua.getString("TrangThai"))
                );
                String tenTang = ketQua.getString("TenKhuVuc");
                banTheoTang.computeIfAbsent(tenTang, k -> new ArrayList<>()).add(ban);
            }
        } catch (SQLException loi) {
            loi.printStackTrace();
        }
        return banTheoTang;
    }

    public PhieuDatBan getPhieuByBan(String maBan) {
       String truyVan = "SELECT p.MaPhieu, p.ThoiGianDat, p.TienCoc, " +
                    "p.soNguoi, p.ghiChu, " +
                    "k.MaKhachHang, k.TenKhachHang, k.SDT, k.Email, k.DiemTichLuy, k.LaThanhVien, " +
                    "nv.MaNV, nv.TenNhanVien, nv.GioiTinh, nv.SDT AS NV_SDT, nv.Email AS NV_Email, " +
                    "b.MaBan, b.SoCho, b.LoaiBan, b.MaKhuVuc, b.TrangThai AS TrangThaiBan " +
                    "FROM PhieuDatBan p " +
                    "JOIN KhachHang k ON p.MaKhachHang = k.MaKhachHang " +
                    "JOIN NhanVien nv ON p.MaNV = nv.MaNV " +
                    "JOIN Ban b ON p.MaBan = b.MaBan " +
                    "WHERE b.MaBan = ?"; 

       try (Connection ketNoi = ConnectDB.getConnection();
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
           lenh.setString(1, maBan);
           try (ResultSet ketQua = lenh.executeQuery()) {
               if (ketQua.next()) {
                   return createPhieuDatBanFromResultSet(ketQua);
               }
           }
       } catch (SQLException loi) {
           loi.printStackTrace();
       }
       return null;
   }

    /**
     * Lấy chi tiết món ăn theo mã phiếu.
     * Phương thức này được lấy từ file thứ 2 và điều chỉnh để phù hợp với
     * schema của file 1 (ví dụ: soLuongMonAn, ghiChu).
     */
    public List<ChiTietPhieuDatBan> getChiTietByPhieuId(String maPhieu) {
        List<ChiTietPhieuDatBan> list = new ArrayList<>();
        String sql = "SELECT ct.*, ma.tenMonAn, ma.donGia as giaGoc " +
                     "FROM ChiTietPhieuDatBan ct " +
                     "JOIN MonAn ma ON ct.maMonAn = ma.maMonAn " +
                     "WHERE ct.maPhieu = ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, maPhieu);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MonAn monAn = new MonAn();
                    monAn.setMaMonAn(rs.getString("maMonAn"));
                    monAn.setTenMonAn(rs.getString("tenMonAn"));
                    monAn.setDonGia(rs.getDouble("giaGoc")); // Giả định entity MonAn có setDonGia
                    
                    ChiTietPhieuDatBan ct = new ChiTietPhieuDatBan();
                    ct.setMonAn(monAn);
                    ct.setSoLuongMonAn(rs.getInt("soLuongMonAn")); // Tên cột từ file 1
                    ct.setGhiChu(rs.getString("ghiChu")); // Tên cột từ file 1
                    
                    // Thử lấy đơn giá từ ChiTietPhieuDatBan (nếu có)
                    try {
                        ct.setDonGia(rs.getDouble("donGia"));
                    } catch (SQLException e) {
                        // Bỏ qua nếu cột 'donGia' không tồn tại trong ChiTietPhieuDatBan
                    }
                    
                    list.add(ct);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getTrangThai() {
        return TrangThaiBan.DA_DAT.toString();
    }
    
    public String generateNewID() {
        String newID = "PDB00001"; 
        String sql = "SELECT TOP 1 MaPhieu FROM PhieuDatBan ORDER BY MaPhieu DESC";
        
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastID = rs.getString("MaPhieu").trim(); 
                if (lastID.startsWith("PDB")) {
                    try {
                        String numberPart = lastID.substring(3); 
                        int num = Integer.parseInt(numberPart) + 1;
                        newID = String.format("PDB%05d", num);
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

    private PhieuDatBan createPhieuDatBanFromResultSet(ResultSet ketQua) throws SQLException {
        KhachHang kh = new KhachHang(
                ketQua.getString("MaKhachHang"),
                ketQua.getString("TenKhachHang"),
                ketQua.getString("SDT"),
                ketQua.getString("Email"),
                ketQua.getInt("DiemTichLuy"),
                ketQua.getBoolean("LaThanhVien")
        );

        NhanVien nv = new NhanVien();
        nv.setMaNV(ketQua.getString("MaNV"));
        nv.setTenNhanVien(ketQua.getString("TenNhanVien"));
        nv.setGioiTinh(ketQua.getBoolean("GioiTinh"));
        nv.setSdt(ketQua.getString("NV_SDT"));
        nv.setEmail(ketQua.getString("NV_Email"));

        Ban ban = new Ban(
                ketQua.getString("MaBan"),
                ketQua.getInt("SoCho"),
                LoaiBan.fromTenHienThi(ketQua.getString("LoaiBan")),
                ketQua.getString("MaKhuVuc"),
                TrangThaiBan.fromString(ketQua.getString("TrangThaiBan"))
        );

        LocalDateTime thoiGianDat = ketQua.getTimestamp("ThoiGianDat").toLocalDateTime();
        double tienCoc = ketQua.getDouble("TienCoc");
        int soNguoi = ketQua.getInt("soNguoi");
        String ghiChu = ketQua.getString("ghiChu");

        return new PhieuDatBan(
                ketQua.getString("MaPhieu"),
                thoiGianDat,
                kh,
                nv,
                ban,
                soNguoi,
                tienCoc,
                ghiChu
        );
    }
    
    public boolean deletePhieuDatBan(String maPhieu) {
        String sqlChiTiet = "DELETE FROM ChiTietPhieuDatBan WHERE MaPhieu = ?";
        String sqlPhieu = "DELETE FROM PhieuDatBan WHERE MaPhieu = ?";
        
        try (Connection con = ConnectDB.getConnection()) {
            con.setAutoCommit(false); 
            
            try (PreparedStatement psChiTiet = con.prepareStatement(sqlChiTiet);
                 PreparedStatement psPhieu = con.prepareStatement(sqlPhieu)) {
                
                // Xóa chi tiết trước
                psChiTiet.setString(1, maPhieu);
                psChiTiet.executeUpdate();
                
                // Xóa phiếu sau
                psPhieu.setString(1, maPhieu);
                int rowsAffected = psPhieu.executeUpdate();
                
                con.commit(); 
                return rowsAffected > 0;
                
            } catch (SQLException e) {
                con.rollback(); 
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Object[]> getChiTietTheoMaPhieu(String maPhieu) {
        List<Object[]> ds = new ArrayList<>();
        String sql = "SELECT ma.maMonAn, ma.tenMonAn, ct.soLuongMonAn, ct.ghiChu " +
                     "FROM ChiTietPhieuDatBan ct " +
                     "JOIN MonAn ma ON ct.maMonAn = ma.maMonAn " +
                     "WHERE ct.maPhieu = ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, maPhieu);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(new Object[] {
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
        return ds;
    }

    public boolean addOrUpdateChiTiet(String maPhieu, String maMonAn, int soLuong, String ghiChu) {
        String checkSql = "SELECT soLuongMonAn FROM ChiTietPhieuDatBan WHERE maPhieu = ? AND maMonAn = ?";
        try (Connection con = ConnectDB.getConnection()) {
            int soLuongHienTai = -1;
            
            try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
                checkStmt.setString(1, maPhieu);
                checkStmt.setString(2, maMonAn);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        soLuongHienTai = rs.getInt("soLuongMonAn");
                    }
                }
            }

            if (soLuongHienTai != -1) {
                // Đã tồn tại -> Cập nhật (cộng dồn số lượng)
                String updateSql = "UPDATE ChiTietPhieuDatBan SET soLuongMonAn = ?, ghiChu = ? " +
                                   "WHERE maPhieu = ? AND maMonAn = ?";
                try (PreparedStatement updateStmt = con.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, soLuongHienTai + soLuong); 
                    updateStmt.setString(2, ghiChu); 
                    updateStmt.setString(3, maPhieu);
                    updateStmt.setString(4, maMonAn);
                    return updateStmt.executeUpdate() > 0;
                }
            } 
            else {
                // Chưa tồn tại -> Thêm mới
                String insertSql = "INSERT INTO ChiTietPhieuDatBan (maPhieu, maMonAn, soLuongMonAn, ghiChu) " +
                                   "VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertStmt = con.prepareStatement(insertSql)) {
                    insertStmt.setString(1, maPhieu);
                    insertStmt.setString(2, maMonAn);
                    insertStmt.setInt(3, soLuong);
                    insertStmt.setString(4, ghiChu);
                    return insertStmt.executeUpdate() > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteChiTiet(String maPhieu, String maMonAn) {
        String sql = "DELETE FROM ChiTietPhieuDatBan WHERE maPhieu = ? AND maMonAn = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, maPhieu);
            stmt.setString(2, maMonAn);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}	