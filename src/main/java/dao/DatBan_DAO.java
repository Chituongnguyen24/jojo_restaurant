package dao;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import connectDB.ConnectDB;
import entity.Ban;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDatBan;
import enums.LoaiBan;
import enums.TrangThaiBan;

public class DatBan_DAO {

    public List<PhieuDatBan> getAllPhieuDatBan() {
        List<PhieuDatBan> ds = new ArrayList<>();

        String sql = """
                SELECT 
                    p.MaPhieu,
                    p.ThoiGianDat,
                    p.TienCoc,
                    k.MaKhachHang, k.TenKhachHang, k.SDT, k.Email, k.DiemTichLuy, k.LaThanhVien,
                    nv.MaNV, nv.TenNhanVien, nv.GioiTinh, nv.SDT AS NV_SDT, nv.Email AS NV_Email,
                    b.MaBan, b.SoCho, b.LoaiBan, b.MaKhuVuc, b.TrangThai
                FROM PhieuDatBan p
                JOIN KhachHang k ON p.MaKhachHang = k.MaKhachHang
                JOIN NhanVien nv ON p.MaNV = nv.MaNV
                JOIN Ban b ON p.MaBan = b.MaBan
                """;

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Thông tin khách hàng
                KhachHang kh = new KhachHang(
                        rs.getString("MaKhachHang"),
                        rs.getString("TenKhachHang"),
                        rs.getString("SDT"),
                        rs.getString("Email"),
                        rs.getInt("DiemTichLuy"),
                        rs.getBoolean("LaThanhVien")
                );

                // Thông tin nhân viên
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("MaNV"));
                nv.setTenNhanVien(rs.getString("TenNhanVien"));
                nv.setGioiTinh(rs.getBoolean("GioiTinh"));
                nv.setSdt(rs.getString("NV_SDT"));
                nv.setEmail(rs.getString("NV_Email"));

                // Thông tin bàn
                Ban ban = new Ban(
                        rs.getString("MaBan"),
                        rs.getInt("SoCho"),
                        LoaiBan.fromTenHienThi(rs.getString("LoaiBan")),
                        rs.getString("MaKhuVuc"),
                        TrangThaiBan.fromString(rs.getString("TrangThai"))
                );

                // Phiếu đặt bàn
                LocalDateTime thoiGianDat = rs.getTimestamp("ThoiGianDat").toLocalDateTime();
                double tienCoc = rs.getDouble("TienCoc");

                PhieuDatBan phieu = new PhieuDatBan(
                        rs.getString("MaPhieu"),
                        thoiGianDat,
                        kh,
                        nv,
                        ban,
                        tienCoc
                );

                ds.add(phieu);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ds;
    }
    //Hàm theo dõi thời gian đặt bàn
    public void capNhatTrangThaiTheoThoiGian(Ban ban, LocalDateTime ngayGioDen) {
        new Thread(() -> {
            try {
                LocalDateTime bayGio = LocalDateTime.now();

                // Thời điểm trước 1 giờ
                long millisTruoc1Gio = Duration.between(bayGio, ngayGioDen.minusHours(1)).toMillis();
                if (millisTruoc1Gio > 0)
                    Thread.sleep(millisTruoc1Gio);

                // Cập nhật trạng thái bàn thành "Đã đặt" trước giờ đến 1 tiếng
                ban.setTrangThai(TrangThaiBan.DA_DAT);
                capNhatTrangThaiBan(ban); // Gọi DAO hoặc cập nhật giao diện

                // Sau giờ đến 30 phút
                long millisSau30p = Duration.between(LocalDateTime.now(), ngayGioDen.plusMinutes(30)).toMillis();
                if (millisSau30p > 0)
                    Thread.sleep(millisSau30p);

                // Nếu chưa chuyển sang "Có khách" thì chuyển thành "Trống"
                if (ban.getTrangThai() != TrangThaiBan.CO_KHACH) {
                    ban.setTrangThai(TrangThaiBan.TRONG);
                    capNhatTrangThaiBan(ban);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Hàm cập nhật trạng thái bàn
    public void capNhatTrangThaiBan(Ban ban) {
        // Gọi SQL UPDATE trạng thái bàn trong database
        // ví dụ:
        // String sql = "UPDATE Ban SET trangThai = ? WHERE maBan = ?";
        // PreparedStatement ps = connection.prepareStatement(sql);
        // ...
    }

    // ====================================================
    // 🔍 TÌM KIẾM THEO TÊN KHÁCH HÀNG
    // ====================================================
    public List<PhieuDatBan> timTheoTenKhachHang(String ten) {
        List<PhieuDatBan> ds = new ArrayList<>();

        String sql = """
                SELECT 
                    p.MaPhieu,
                    p.ThoiGianDat,
                    p.TienCoc,
                    k.MaKhachHang, k.TenKhachHang, k.SDT, k.Email, k.DiemTichLuy, k.LaThanhVien,
                    nv.MaNV, nv.TenNhanVien, nv.GioiTinh, nv.SDT AS NV_SDT, nv.Email AS NV_Email,
                    b.MaBan, b.SoCho, b.LoaiBan, b.MaKhuVuc, b.TrangThai
                FROM PhieuDatBan p
                JOIN KhachHang k ON p.MaKhachHang = k.MaKhachHang
                JOIN NhanVien nv ON p.MaNV = nv.MaNV
                JOIN Ban b ON p.MaBan = b.MaBan
                WHERE k.TenKhachHang LIKE ?
                """;

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, "%" + ten + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    KhachHang kh = new KhachHang(
                            rs.getString("MaKhachHang"),
                            rs.getString("TenKhachHang"),
                            rs.getString("SDT"),
                            rs.getString("Email"),
                            rs.getInt("DiemTichLuy"),
                            rs.getBoolean("LaThanhVien")
                    );

                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("MaNV"));
                    nv.setTenNhanVien(rs.getString("TenNhanVien"));
                    nv.setGioiTinh(rs.getBoolean("GioiTinh"));
                    nv.setSdt(rs.getString("NV_SDT"));
                    nv.setEmail(rs.getString("NV_Email"));

                    Ban ban = new Ban(
                            rs.getString("MaBan"),
                            rs.getInt("SoCho"),
                            LoaiBan.fromTenHienThi(ten),
                            rs.getString("MaKhuVuc"),
                            TrangThaiBan.fromString(rs.getString("TrangThai")) 
                    );

                    LocalDateTime thoiGianDat = rs.getTimestamp("ThoiGianDat").toLocalDateTime();
                    double tienCoc = rs.getDouble("TienCoc");

                    PhieuDatBan phieu = new PhieuDatBan(
                            rs.getString("MaPhieu"),
                            thoiGianDat,
                            kh,
                            nv,
                            ban,
                            tienCoc
                    );

                    ds.add(phieu);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ds;
    }
}