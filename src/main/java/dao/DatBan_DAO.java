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
import entity.NhanVien;
import entity.PhieuDatBan;
import enums.LoaiBan;
import enums.TrangThaiBan;

public class DatBan_DAO {

    // 1. Get all booking tickets
    public List<PhieuDatBan> getAllPhieuDatBan() {
        List<PhieuDatBan> danhSach = new ArrayList<>();

        String truyVan = "SELECT p.MaPhieu, p.ThoiGianDat, p.TienCoc, p.TrangThai AS TrangThaiPhieu, " +
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

    // 2. Search booking tickets by customer name
    public List<PhieuDatBan> searchByCustomerName(String ten) {
        List<PhieuDatBan> danhSach = new ArrayList<>();

        String truyVan = "SELECT p.MaPhieu, p.ThoiGianDat, p.TienCoc, p.TrangThai AS TrangThaiPhieu, " +
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
                    PhieuDatBan phieu = createPhieuDatBanFromResultSet(ketQua);
                    danhSach.add(phieu);
                }
            }

        } catch (SQLException loi) {
            System.err.println("[DAO] Lỗi searchByCustomerName: " + loi.getMessage());
            loi.printStackTrace();
        }

        return danhSach;
    }

    // 3. Search booking tickets by customer phone (for cancel/edit)
    public List<PhieuDatBan> searchByPhone(String sdt) {
        List<PhieuDatBan> danhSach = new ArrayList<>();

        String truyVan = "SELECT p.MaPhieu, p.ThoiGianDat, p.TienCoc, p.TrangThai AS TrangThaiPhieu, " +
                     "k.MaKhachHang, k.TenKhachHang, k.SDT, k.Email, k.DiemTichLuy, k.LaThanhVien, " +
                     "nv.MaNV, nv.TenNhanVien, nv.GioiTinh, nv.SDT AS NV_SDT, nv.Email AS NV_Email, " +
                     "b.MaBan, b.SoCho, b.LoaiBan, b.MaKhuVuc, b.TrangThai AS TrangThaiBan " +
                     "FROM PhieuDatBan p " +
                     "JOIN KhachHang k ON p.MaKhachHang = k.MaKhachHang " +
                     "JOIN NhanVien nv ON p.MaNV = nv.MaNV " +
                     "JOIN Ban b ON p.MaBan = b.MaBan " +
                     "WHERE k.SDT LIKE ? AND p.TrangThai = 'DA_DAT'";  // Only un-cancelled tickets

        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {

            lenh.setString(1, "%" + sdt + "%");

            try (ResultSet ketQua = lenh.executeQuery()) {
                while (ketQua.next()) {
                    PhieuDatBan phieu = createPhieuDatBanFromResultSet(ketQua);
                    danhSach.add(phieu);
                }
            }

        } catch (SQLException loi) {
            System.err.println("[DAO] Lỗi searchByPhone: " + loi.getMessage());
            loi.printStackTrace();
        }

        return danhSach;
    }

    // 4. Get booking tickets by time range (for overlap check)
    public List<PhieuDatBan> getPhieuDatBanByTimeRange(LocalDateTime start, LocalDateTime end) {
        List<PhieuDatBan> danhSach = new ArrayList<>();

        String truyVan = "SELECT p.MaPhieu, p.ThoiGianDat, p.TienCoc, p.TrangThai AS TrangThaiPhieu, " +
                     "k.MaKhachHang, k.TenKhachHang, k.SDT, k.Email, k.DiemTichLuy, k.LaThanhVien, " +
                     "nv.MaNV, nv.TenNhanVien, nv.GioiTinh, nv.SDT AS NV_SDT, nv.Email AS NV_Email, " +
                     "b.MaBan, b.SoCho, b.LoaiBan, b.MaKhuVuc, b.TrangThai AS TrangThaiBan " +
                     "FROM PhieuDatBan p " +
                     "JOIN KhachHang k ON p.MaKhachHang = k.MaKhachHang " +
                     "JOIN NhanVien nv ON p.MaNV = nv.MaNV " +
                     "JOIN Ban b ON p.MaBan = b.MaBan " +
                     "WHERE p.ThoiGianDat BETWEEN ? AND ? " +
                     "AND p.TrangThai = 'DA_DAT'";  // Only active tickets

        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {

            lenh.setTimestamp(1, Timestamp.valueOf(start));
            lenh.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet ketQua = lenh.executeQuery()) {
                while (ketQua.next()) {
                    PhieuDatBan phieu = createPhieuDatBanFromResultSet(ketQua);
                    danhSach.add(phieu);
                }
            }

        } catch (SQLException loi) {
            System.err.println("[DAO] Lỗi getPhieuDatBanByTimeRange: " + loi.getMessage());
            loi.printStackTrace();
        }

        return danhSach;
    }

    // 5. Add new booking ticket
    public boolean addPhieuDatBan(PhieuDatBan phieu) {
        String truyVan = "INSERT INTO PhieuDatBan (MaPhieu, ThoiGianDat, MaKhachHang, MaNV, MaBan, TienCoc, TrangThai) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {

            lenh.setString(1, phieu.getMaPhieu());
            lenh.setTimestamp(2, Timestamp.valueOf(phieu.getThoiGianDat()));
            lenh.setString(3, phieu.getKhachHang().getMaKhachHang());
            lenh.setString(4, phieu.getNhanVien().getMaNV());
            lenh.setString(5, phieu.getBan().getMaBan());
            lenh.setDouble(6, phieu.getTienCoc());
            lenh.setString(7, "DA_DAT");

            int soDong = lenh.executeUpdate();
            if (soDong > 0) {
                updateTableStatus(phieu.getBan().getMaBan(), TrangThaiBan.DA_DAT);
            }

            return soDong > 0;

        } catch (SQLException loi) {
            System.err.println("[DAO] Lỗi addPhieuDatBan: " + loi.getMessage());
            loi.printStackTrace();
        }
        return false;
    }

    // 6. Update booking ticket (for edit)
    public boolean updatePhieuDatBan(PhieuDatBan phieu) {
        String truyVan = "UPDATE PhieuDatBan SET ThoiGianDat = ?, MaKhachHang = ?, MaNV = ?, MaBan = ?, TienCoc = ? " +
                     "WHERE MaPhieu = ?";

        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {

            lenh.setTimestamp(1, Timestamp.valueOf(phieu.getThoiGianDat()));
            lenh.setString(2, phieu.getKhachHang().getMaKhachHang());
            lenh.setString(3, phieu.getNhanVien().getMaNV());
            lenh.setString(4, phieu.getBan().getMaBan());
            lenh.setDouble(5, phieu.getTienCoc());
            lenh.setString(6, phieu.getMaPhieu());

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

    // 7. Cancel booking ticket (update status, calculate refund)
    public boolean cancelPhieuDatBan(String maPhieu, String lyDoHuy) {
        PhieuDatBan phieu = getPhieuDatBanById(maPhieu);
        if (phieu == null) return false;

        LocalDateTime hienTai = LocalDateTime.now();
        LocalDateTime thoiGianDat = phieu.getThoiGianDat();
        double hoanCoc = 0.0;
        String trangThaiMoi = "DA_HUY";

        long gioChenhLech = java.time.Duration.between(hienTai, thoiGianDat).toHours();
        if (gioChenhLech >= 24) {
            hoanCoc = phieu.getTienCoc();
        } else if (gioChenhLech >= 2) {
            hoanCoc = phieu.getTienCoc() * 0.7;
        } else if (gioChenhLech >= 1) {
            hoanCoc = phieu.getTienCoc() * 0.5;
        } else {
            hoanCoc = 0.0;
        }

        String truyVan = "UPDATE PhieuDatBan SET TrangThai = ?, LyDoHuy = ?, TienHoanCoc = ? WHERE MaPhieu = ?";
        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {

            lenh.setString(1, trangThaiMoi);
            lenh.setString(2, lyDoHuy);
            lenh.setDouble(3, hoanCoc);
            lenh.setString(4, maPhieu);

            int soDong = lenh.executeUpdate();
            if (soDong > 0) {
                updateTableStatus(phieu.getBan().getMaBan(), TrangThaiBan.TRONG);
            }

            return soDong > 0;

        } catch (SQLException loi) {
            System.err.println("[DAO] Lỗi cancelPhieuDatBan: " + loi.getMessage());
            loi.printStackTrace();
        }
        return false;
    }

    // 8. Get booking ticket by ID
    public PhieuDatBan getPhieuDatBanById(String maPhieu) {
        String truyVan = "SELECT p.MaPhieu, p.ThoiGianDat, p.TienCoc, p.TrangThai AS TrangThaiPhieu, " +
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

    // 9. Update table status
    private boolean updateTableStatus(String maBan, TrangThaiBan trangThai) {
        String truyVan = "UPDATE Ban SET TrangThai = ? WHERE MaBan = ?";
        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {

            lenh.setString(1, trangThai.toString());
            lenh.setString(2, maBan);

            return lenh.executeUpdate() > 0;

        } catch (SQLException loi) {
            System.err.println("[DAO] Lỗi updateTableStatus: " + loi.getMessage());
            loi.printStackTrace();
        }
        return false;
    }

    // 10. Get all tables by floor
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

    // Get PhieuDatBan by Ban
    public PhieuDatBan getPhieuByBan(String maBan) {
        String truyVan = "SELECT p.MaPhieu, p.ThoiGianDat, p.TienCoc, p.TrangThai AS TrangThaiPhieu, " +
                     "k.MaKhachHang, k.TenKhachHang, k.SDT, k.Email, k.DiemTichLuy, k.LaThanhVien, " +
                     "nv.MaNV, nv.TenNhanVien, nv.GioiTinh, nv.SDT AS NV_SDT, nv.Email AS NV_Email, " +
                     "b.MaBan, b.SoCho, b.LoaiBan, b.MaKhuVuc, b.TrangThai AS TrangThaiBan " +
                     "FROM PhieuDatBan p " +
                     "JOIN KhachHang k ON p.MaKhachHang = k.MaKhachHang " +
                     "JOIN NhanVien nv ON p.MaNV = nv.MaNV " +
                     "JOIN Ban b ON p.MaBan = b.MaBan " +
                     "WHERE b.MaBan = ? AND p.TrangThai = 'DA_DAT'";

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

    // Get ChiTietPhieuDatBan by PhieuId
    public List<ChiTietPhieuDatBan> getChiTietByPhieuId(String maPhieu) {
        List<ChiTietPhieuDatBan> danhSach = new ArrayList<>();

        String truyVan = "SELECT * FROM ChiTietPhieuDatBan WHERE maPhieu = ?";

        try (Connection ketNoi = ConnectDB.getConnection();
             PreparedStatement lenh = ketNoi.prepareStatement(truyVan)) {

            lenh.setString(1, maPhieu);

            try (ResultSet ketQua = lenh.executeQuery()) {
                while (ketQua.next()) {
                    // Sử dụng constructor mặc định và set các trường cơ bản, không load MonAn hoặc PhieuDatBan
                    ChiTietPhieuDatBan chiTiet = new ChiTietPhieuDatBan();
                    chiTiet.setGhiChu(ketQua.getString("ghiChu"));
                    chiTiet.setSoLuongMonAn(ketQua.getInt("soLuongMonAn"));
                    chiTiet.setDonGia(ketQua.getDouble("donGia"));
                    // Các trường khác nếu có, ví dụ maChiTiet nếu tồn tại
                    // chiTiet.setMaChiTiet(ketQua.getString("maChiTiet")); // nếu entity có field này
                    danhSach.add(chiTiet);
                }
            }

        } catch (SQLException loi) {
            System.err.println("[DAO] Lỗi getChiTietByPhieuId: " + loi.getMessage());
            loi.printStackTrace();
        }

        return danhSach;
    }

    // Get TrangThai (placeholder, adjust as needed)
    public String getTrangThai() {
        return TrangThaiBan.DA_DAT.toString();
    }

    // Helper: Create PhieuDatBan from ResultSet
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

        return new PhieuDatBan(
                ketQua.getString("MaPhieu"),
                thoiGianDat,
                kh,
                nv,
                ban,
                tienCoc
        );
    }

}