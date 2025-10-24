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

    // 1. Get all booking tickets (ĐÃ SỬA)
    public List<PhieuDatBan> getAllPhieuDatBan() {
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

    // Hàm theo dõi thời gian (Lấy từ file gốc của bạn)
    public void capNhatTrangThaiTheoThoiGian(Ban ban, LocalDateTime ngayGioDen) {
        new Thread(() -> {
            try {
                LocalDateTime bayGio = LocalDateTime.now();
                long millisTruoc1Gio = Duration.between(bayGio, ngayGioDen.minusHours(1)).toMillis();
                if (millisTruoc1Gio > 0)
                    Thread.sleep(millisTruoc1Gio);

                ban.setTrangThai(TrangThaiBan.DA_DAT);
                updateTableStatus(ban.getMaBan(), TrangThaiBan.DA_DAT); // Sửa: gọi hàm updateTableStatus

                long millisSau30p = Duration.between(LocalDateTime.now(), ngayGioDen.plusMinutes(30)).toMillis();
                if (millisSau30p > 0)
                    Thread.sleep(millisSau30p);

                if (ban.getTrangThai() != TrangThaiBan.CO_KHACH) {
                    ban.setTrangThai(TrangThaiBan.TRONG);
                    updateTableStatus(ban.getMaBan(), TrangThaiBan.TRONG); // Sửa: gọi hàm updateTableStatus
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    // (Bỏ hàm capNhatTrangThaiBan rỗng vì đã có updateTableStatus)

    // 2. Search by customer name (ĐÃ SỬA)
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

    // 3. Search by phone (ĐÃ SỬA)
    public List<PhieuDatBan> searchByPhone(String sdt) {
        List<PhieuDatBan> danhSach = new ArrayList<>();
        // SỬA SQL: Thêm p.soNguoi, p.ghiChu và BỎ p.TrangThai (vì không tồn tại)
        String truyVan = "SELECT p.MaPhieu, p.ThoiGianDat, p.TienCoc, " +
                     "p.soNguoi, p.ghiChu, " +
                     "k.MaKhachHang, k.TenKhachHang, k.SDT, k.Email, k.DiemTichLuy, k.LaThanhVien, " +
                     "nv.MaNV, nv.TenNhanVien, nv.GioiTinh, nv.SDT AS NV_SDT, nv.Email AS NV_Email, " +
                     "b.MaBan, b.SoCho, b.LoaiBan, b.MaKhuVuc, b.TrangThai AS TrangThaiBan " +
                     "FROM PhieuDatBan p " +
                     "JOIN KhachHang k ON p.MaKhachHang = k.MaKhachHang " +
                     "JOIN NhanVien nv ON p.MaNV = nv.MaNV " +
                     "JOIN Ban b ON p.MaBan = b.MaBan " +
                     "WHERE k.SDT LIKE ?"; // Bỏ "AND p.TrangThai = 'DA_DAT'"

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

    // 4. Get by time range (ĐÃ SỬA)
    public List<PhieuDatBan> getPhieuDatBanByTimeRange(LocalDateTime start, LocalDateTime end) {
        List<PhieuDatBan> danhSach = new ArrayList<>();
        // SỬA SQL: Thêm p.soNguoi, p.ghiChu và BỎ p.TrangThai (vì không tồn tại)
        String truyVan = "SELECT p.MaPhieu, p.ThoiGianDat, p.TienCoc, " +
                     "p.soNguoi, p.ghiChu, " +
                     "k.MaKhachHang, k.TenKhachHang, k.SDT, k.Email, k.DiemTichLuy, k.LaThanhVien, " +
                     "nv.MaNV, nv.TenNhanVien, nv.GioiTinh, nv.SDT AS NV_SDT, nv.Email AS NV_Email, " +
                     "b.MaBan, b.SoCho, b.LoaiBan, b.MaKhuVuc, b.TrangThai AS TrangThaiBan " +
                     "FROM PhieuDatBan p " +
                     "JOIN KhachHang k ON p.MaKhachHang = k.MaKhachHang " +
                     "JOIN NhanVien nv ON p.MaNV = nv.MaNV " +
                     "JOIN Ban b ON p.MaBan = b.MaBan " +
                     "WHERE p.ThoiGianDat BETWEEN ? AND ?"; // Bỏ "AND p.TrangThai = 'DA_DAT'"

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

    // 5. Add new booking (Hàm này lấy từ PhieuDatBan_DAO.java của bạn)
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
                // Tự động cập nhật trạng thái bàn trong CSDL
                updateTableStatus(p.getBan().getMaBan(), TrangThaiBan.DA_DAT);
            }
            return soDong > 0;
        } catch (Exception e) {
            System.err.println("[DAO] Lỗi insertPhieuDatBan: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // 6. Update booking (ĐÃ SỬA)
    public boolean updatePhieuDatBan(PhieuDatBan phieu) {
        // SỬA SQL: Thêm soNguoi, ghiChu
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
            lenh.setInt(6, phieu.getSoNguoi()); // Thêm
            lenh.setString(7, phieu.getGhiChu()); // Thêm
            lenh.setString(8, phieu.getMaPhieu()); // Index 8

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

    // 7. Cancel booking (Cần sửa CSDL nếu muốn dùng)
    public boolean cancelPhieuDatBan(String maPhieu, String lyDoHuy) {
        // LƯU Ý: CSDL jojo_v6.sql của bạn KHÔNG có cột 'TrangThai', 'LyDoHuy', 'TienHoanCoc'
        // Hàm này sẽ BÁO LỖI SQL nếu bạn gọi.
        // Nếu bạn muốn dùng hàm này, bạn phải chạy lệnh SQL:
        // ALTER TABLE PhieuDatBan ADD TrangThai NVARCHAR(20)
        // ALTER TABLE PhieuDatBan ADD LyDoHuy NVARCHAR(255)
        // ALTER TABLE PhieuDatBan ADD TienHoanCoc MONEY
        
        System.err.println("CẢNH BÁO: Hàm cancelPhieuDatBan được gọi nhưng CSDL có thể thiếu cột!");
        return false; // Tạm thời trả về false
    }

    // 8. Get by ID (ĐÃ SỬA)
    public PhieuDatBan getPhieuDatBanById(String maPhieu) {
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

    // 9. Update table status (ĐÃ ĐIỀN THÂN HÀM)
    private boolean updateTableStatus(String maBan, TrangThaiBan trangThai) {
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

    // 10. Get all tables by floor (ĐÃ ĐIỀN THÂN HÀM)
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

    // Get PhieuDatBan by Ban (ĐÃ SỬA)
    public PhieuDatBan getPhieuByBan(String maBan) {
        // SỬA SQL: Bỏ "AND p.TrangThai = 'DA_DAT'" vì cột này không tồn tại
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
           // ... (Giữ nguyên phần còn lại của hàm) ...
//...
           lenh.setString(1, maBan);
           try (ResultSet ketQua = lenh.executeQuery()) {
               if (ketQua.next()) {
                   return createPhieuDatBanFromResultSet(ketQua);
               }
           }
//...
       } catch (SQLException loi) {
           System.err.println("[DAO] Lỗi getPhieuByBan: " + loi.getMessage());
           loi.printStackTrace();
       }
       return null;
   }

    // Get ChiTietPhieuDatBan by PhieuId (ĐÃ ĐIỀN THÂN HÀM VÀ SỬA LỖI)
    public List<ChiTietPhieuDatBan> getChiTietByPhieuId(String maPhieu) {
        List<ChiTietPhieuDatBan> danhSach = new ArrayList<>();
        // CSDL jojo_v6 không có cột donGia trong ChiTietPhieuDatBan
        String truyVan = "SELECT * FROM ChiTietPhieuDatBan WHERE maPhieu = ?";

        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {

            lenh.setString(1, maPhieu);
            try (ResultSet ketQua = lenh.executeQuery()) {
                while (ketQua.next()) {
                    ChiTietPhieuDatBan chiTiet = new ChiTietPhieuDatBan();
                    chiTiet.setGhiChu(ketQua.getString("ghiChu"));
                    chiTiet.setSoLuongMonAn(ketQua.getInt("soLuongMonAn"));
                    
                    // BỎ DÒNG LỖI: chiTiet.setDonGia(ketQua.getDouble("donGia"));
                    
                    // Bạn cần lấy MonAn và PhieuDatBan nếu Entity của bạn yêu cầu
                    // Ví dụ:
                    // MonAn_DAO monDAO = new MonAn_DAO();
                    // chiTiet.setMonAn(monDAO.getMonAnById(ketQua.getString("maMonAn")));
                    // chiTiet.setPhieuDatBan(new PhieuDatBan(maPhieu)); // Dùng constructor 1 tham số

                    danhSach.add(chiTiet);
                }
            }
        } catch (SQLException loi) {
            System.err.println("[DAO] Lỗi getChiTietByPhieuId: " + loi.getMessage());
            loi.printStackTrace();
        }
        return danhSach;
    }

    // Get TrangThai (Lấy từ file gốc của bạn)
    public String getTrangThai() {
        return TrangThaiBan.DA_DAT.toString();
    }
    
    // Tự động tạo mã phiếu (Lấy từ file PhieuDatBan_DAO.java của bạn)
    public String generateNewID() {
        String newID = "PDB00001"; // Mã mặc định
        String sql = "SELECT TOP 1 MaPhieu FROM PhieuDatBan ORDER BY MaPhieu DESC";
        
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                // 1. Lấy mã cuối cùng và XÓA KHOẢNG TRẮNG (Rất quan trọng)
                String lastID = rs.getString("MaPhieu").trim(); 
                
                // 2. Kiểm tra xem mã có bắt đầu bằng "PDB" không
                if (lastID.startsWith("PDB")) {
                    try {
                        // 3. Cắt lấy phần số (ví dụ: "00051" từ "PDB00051")
                        String numberPart = lastID.substring(3); 
                        
                        // 4. Chuyển số đó thành integer (51), cộng 1 = 52
                        int num = Integer.parseInt(numberPart) + 1;
                        
                        // 5. Định dạng lại thành 5 chữ số (PDB00052)
                        newID = String.format("PDB%05d", num);
                        
                    } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                        // Nếu cắt chuỗi hoặc parse số thất bại, in lỗi
                        System.err.println("[DAO] Lỗi parsing mã phiếu cuối cùng: " + lastID);
                        // (Lúc này newID vẫn là "PDB00001", vẫn có thể gây lỗi nếu PDB00001 tồn tại)
                        // Tình huống này gần như không xảy ra nếu CSDL của bạn sạch.
                    }
                }
            }
            // Nếu bảng trống (rs.next() = false), newID sẽ là "PDB00001"
            // Nhưng CSDL của bạn đã có PDB00001, nên rs.next() sẽ luôn là TRUE.
            
        } catch (SQLException e) {
            System.err.println("[DAO] Lỗi SQL trong generateNewID: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("MaPhieuDatBan mới được tạo: " + newID); // In ra để kiểm tra
        return newID;
    }

    // Helper: Create PhieuDatBan from ResultSet (ĐÃ SỬA)
    private PhieuDatBan createPhieuDatBanFromResultSet(ResultSet ketQua) throws SQLException {
        // 1. Tạo KhachHang
        KhachHang kh = new KhachHang(
                ketQua.getString("MaKhachHang"),
                ketQua.getString("TenKhachHang"),
                ketQua.getString("SDT"),
                ketQua.getString("Email"),
                ketQua.getInt("DiemTichLuy"),
                ketQua.getBoolean("LaThanhVien")
        );

        // 2. Tạo NhanVien (Giả định NhanVien.java có constructor 6 tham số)
        NhanVien nv = new NhanVien();
        nv.setMaNV(ketQua.getString("MaNV"));
        nv.setTenNhanVien(ketQua.getString("TenNhanVien"));
        nv.setGioiTinh(ketQua.getBoolean("GioiTinh"));
        nv.setSdt(ketQua.getString("NV_SDT"));
        nv.setEmail(ketQua.getString("NV_Email"));
        // nv.setTaiKhoan(null); // Nếu cần, bạn phải truy vấn bảng TaiKhoan

        // 3. Tạo Ban (Giả định Ban.java có constructor 5 tham số)
        Ban ban = new Ban(
                ketQua.getString("MaBan"),
                ketQua.getInt("SoCho"),
                LoaiBan.fromTenHienThi(ketQua.getString("LoaiBan")),
                ketQua.getString("MaKhuVuc"),
                TrangThaiBan.fromString(ketQua.getString("TrangThaiBan"))
        );

        // 4. Lấy các trường còn lại
        LocalDateTime thoiGianDat = ketQua.getTimestamp("ThoiGianDat").toLocalDateTime();
        double tienCoc = ketQua.getDouble("TienCoc");
        int soNguoi = ketQua.getInt("soNguoi");
        String ghiChu = ketQua.getString("ghiChu");

        // 5. Gọi constructor 8 tham số của PhieuDatBan
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
        // Lưu ý: Cần xóa ChiTietPhieuDatBan trước
        String sqlChiTiet = "DELETE FROM ChiTietPhieuDatBan WHERE MaPhieu = ?";
        String sqlPhieu = "DELETE FROM PhieuDatBan WHERE MaPhieu = ?";
        
        try (Connection con = ConnectDB.getConnection()) {
            con.setAutoCommit(false); // Bắt đầu Transaction
            
            try (PreparedStatement psChiTiet = con.prepareStatement(sqlChiTiet);
                 PreparedStatement psPhieu = con.prepareStatement(sqlPhieu)) {
                
                // Xóa chi tiết
                psChiTiet.setString(1, maPhieu);
                psChiTiet.executeUpdate();
                
                // Xóa phiếu chính
                psPhieu.setString(1, maPhieu);
                int rowsAffected = psPhieu.executeUpdate();
                
                con.commit(); // Hoàn tất Transaction
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
    /**
     * Lấy tất cả ChiTietPhieuDatBan của một Phiếu, JOIN với MonAn
     * (Sửa lại hàm getChiTietByPhieuId cũ)
     */
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

    /**
     * Thêm món vào ChiTietPhieuDatBan
     * (Hoặc cập nhật số lượng nếu đã tồn tại)
     */
    public boolean addOrUpdateChiTiet(String maPhieu, String maMonAn, int soLuong, String ghiChu) {
        // 1. Kiểm tra xem món đã tồn tại trong phiếu chưa
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

            // 2. Nếu đã tồn tại -> Cập nhật (Cộng dồn số lượng)
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
            // 3. Nếu chưa tồn tại -> Thêm mới
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

    /**
     * Xóa một món khỏi ChiTietPhieuDatBan
     */
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