package dao;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connectDB.ConnectDB;
import entity.Ban;
import entity.ChiTietPhieuDatBan;
import entity.KhachHang;
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
            System.err.println("[DAO] Lỗi getAllPhieuDatBan: " + loi.getMessage());
            loi.printStackTrace();
        }
        return danhSach;
    }

    //hàm theo dõi thời gian
    public void capNhatTrangThaiTheoThoiGian(Ban ban, LocalDateTime ngayGioDen) {
        new Thread(() -> {
            try {
                LocalDateTime bayGio = LocalDateTime.now();
                long millisTruoc1Gio = Duration.between(bayGio, ngayGioDen.minusHours(1)).toMillis();
                if (millisTruoc1Gio > 0)
                    Thread.sleep(millisTruoc1Gio);

                ban.setTrangThai(TrangThaiBan.DA_DAT);
                updateTableStatus(ban.getMaBan(), TrangThaiBan.DA_DAT); 

                long millisSau30p = Duration.between(LocalDateTime.now(), ngayGioDen.plusMinutes(30)).toMillis();
                if (millisSau30p > 0)
                    Thread.sleep(millisSau30p);

                if (ban.getTrangThai() != TrangThaiBan.CO_KHACH) {
                    ban.setTrangThai(TrangThaiBan.TRONG);
                    updateTableStatus(ban.getMaBan(), TrangThaiBan.TRONG);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    

    //search by customer name
    public List<PhieuDatBan> searchByCustomerName(String ten) {
        List<PhieuDatBan> danhSach = new ArrayList<>();
        // SỬA SQL: Thêm p.soNguoi, p.ghiChu
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
            System.err.println("[DAO] Lỗi searchByCustomerName: " + loi.getMessage());
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
            System.err.println("[DAO] Lỗi searchByPhone: " + loi.getMessage());
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
            System.err.println("[DAO] Lỗi getPhieuDatBanByTimeRange: " + loi.getMessage());
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
                updateTableStatus(p.getBan().getMaBan(), TrangThaiBan.DA_DAT);
            }
            return soDong > 0;
        } catch (Exception e) {
            System.err.println("[DAO] Lỗi insertPhieuDatBan: " + e.getMessage());
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
                updateTableStatus(phieu.getBan().getMaBan(), TrangThaiBan.DA_DAT);
            }
            return soDong > 0;
        } catch (SQLException loi) {
            System.err.println("[DAO] Lỗi updatePhieuDatBan: " + loi.getMessage());
            loi.printStackTrace();
        }
        return false;
    }


    public boolean cancelPhieuDatBan(String maPhieu, String lyDoHuy) {
        System.err.println("CẢNH BÁO: Hàm cancelPhieuDatBan được gọi nhưng CSDL có thể thiếu cột!");
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
            System.err.println("[DAO] Lỗi getPhieuDatBanById: " + loi.getMessage());
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
            System.err.println("[DAO] Lỗi updateTableStatus: " + loi.getMessage());
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
            System.err.println("[DAO] Lỗi getAllBanByFloor: " + loi.getMessage());
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
                    "WHERE b.MaBan = ?"; // <<< ĐÃ XÓA ĐIỀU KIỆN TRẠNG THÁI

       try (Connection ketNoi = ConnectDB.getConnection();
            PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {
           lenh.setString(1, maBan);
           try (ResultSet ketQua = lenh.executeQuery()) {
               if (ketQua.next()) {
                   return createPhieuDatBanFromResultSet(ketQua);
               }
           }

       } catch (SQLException loi) {
           System.err.println("[DAO] Lỗi getPhieuByBan: " + loi.getMessage());
           loi.printStackTrace();
       }
       return null;
   }

    public List<ChiTietPhieuDatBan> getChiTietByPhieuId(String maPhieu) {
        List<ChiTietPhieuDatBan> danhSach = new ArrayList<>();
        String truyVan = "SELECT * FROM ChiTietPhieuDatBan WHERE maPhieu = ?";

        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {

            lenh.setString(1, maPhieu);
            try (ResultSet ketQua = lenh.executeQuery()) {
                while (ketQua.next()) {
                    ChiTietPhieuDatBan chiTiet = new ChiTietPhieuDatBan();
                    chiTiet.setGhiChu(ketQua.getString("ghiChu"));
                    chiTiet.setSoLuongMonAn(ketQua.getInt("soLuongMonAn"));
                    
                    danhSach.add(chiTiet);
                }
            }
        } catch (SQLException loi) {
            System.err.println("[DAO] Lỗi getChiTietByPhieuId: " + loi.getMessage());
            loi.printStackTrace();
        }
        return danhSach;
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
                //Lấy mã cuối cùng và XÓA KHOẢNG TRẮNG 
                String lastID = rs.getString("MaPhieu").trim(); 
                
                //Kiểm tra xem mã có bắt đầu bằng "PDB" 
                if (lastID.startsWith("PDB")) {
                    try {
                        //Cắt lấy phần số "00051" từ "PDB00051"
                        String numberPart = lastID.substring(3); 
                        
                        //Chuyển số đó thành int
                        int num = Integer.parseInt(numberPart) + 1;
                        
                        //Định dạng lại thành 5 chữ số (PDB00052)
                        newID = String.format("PDB%05d", num);
                        
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        System.err.println("[DAO] Lỗi parsing mã phiếu cuối cùng: " + lastID);
                    }
                }
            }

            
        } catch (SQLException e) {
            System.err.println("[DAO] Lỗi SQL trong generateNewID: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("MaPhieuDatBan mới được tạo: " + newID); 
        return newID;
    }

    //tao PhieuDatBan
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
                
                //xóa chi tiết
                psChiTiet.setString(1, maPhieu);
                psChiTiet.executeUpdate();
                
                //xóa phiếu chính
                psPhieu.setString(1, maPhieu);
                int rowsAffected = psPhieu.executeUpdate();
                
                con.commit(); //hoàn tất
                return rowsAffected > 0;
                
            } catch (SQLException e) {
                con.rollback(); // Rollback nếu có lỗi
                System.err.println("[DAO] Lỗi deletePhieuDatBan (rollback): " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Lỗi Transaction deletePhieuDatBan: " + e.getMessage());
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
        
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, maPhieu);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Trả về mảng Object[] để dễ đưa vào JTable
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
        //Kiểm tra xem món đã tồn tại trong phiếu chưa
        String checkSql = "SELECT soLuongMonAn FROM ChiTietPhieuDatBan WHERE maPhieu = ? AND maMonAn = ?";
        try (Connection con = ConnectDB.getInstance().getConnection()) {
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

            //Nếu đã tồn tại cộng dồn số lượng
            if (soLuongHienTai != -1) {
                String updateSql = "UPDATE ChiTietPhieuDatBan SET soLuongMonAn = ?, ghiChu = ? " +
                                   "WHERE maPhieu = ? AND maMonAn = ?";
                try (PreparedStatement updateStmt = con.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, soLuongHienTai + soLuong); // Cộng dồn
                    updateStmt.setString(2, ghiChu); // Ghi đè ghi chú mới
                    updateStmt.setString(3, maPhieu);
                    updateStmt.setString(4, maMonAn);
                    return updateStmt.executeUpdate() > 0;
                }
            } 
            //Nếu chưa tồn tại thêm mới
            else {
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

    //Xóa một món khỏi ChiTietPhieuDatBan
    public boolean deleteChiTiet(String maPhieu, String maMonAn) {
        String sql = "DELETE FROM ChiTietPhieuDatBan WHERE maPhieu = ? AND maMonAn = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
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