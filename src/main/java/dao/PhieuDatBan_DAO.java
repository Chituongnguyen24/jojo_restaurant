package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;   // <<< THÊM
import java.sql.Timestamp;  // <<< THÊM
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.Ban;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDatBan;

public class PhieuDatBan_DAO {

    // SỬA LẠI HÀM NÀY ĐỂ ĐỌC ĐÚNG CỘT
	public List<PhieuDatBan> getAllPhieuDatBan() {
	    List<PhieuDatBan> list = new ArrayList<>();
	    String sql = "SELECT * FROM PhieuDatBan";

	    try (Connection con = ConnectDB.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        KhachHang_DAO khDAO = new KhachHang_DAO();
	        NhanVien_DAO nvDAO = new NhanVien_DAO();
	        Ban_DAO banDAO = new Ban_DAO();

	        while (rs.next()) {
	            String maPhieu = rs.getString("maPhieu");
	            LocalDateTime thoiGianDat = rs.getTimestamp("thoiGianDat").toLocalDateTime(); // Đây là thời gian khách đến
	            String maKH = rs.getString("maKhachHang"); // CSDL dùng maKhachHang
	            String maNV = rs.getString("maNV");
	            String maBan = rs.getString("maBan");
                int soNguoi = rs.getInt("soNguoi"); // <<< THÊM
	            double tienCoc = rs.getDouble("tienCoc");
                String ghiChu = rs.getString("ghiChu"); // <<< THÊM

	            KhachHang khachHang = khDAO.getKhachHangById(maKH);
	            NhanVien nhanVien = nvDAO.getNhanVienById(maNV);
	            Ban ban = banDAO.getBanById(maBan);

	            // Tạo object PhieuDatBan bằng constructor mới
	            PhieuDatBan pdb = new PhieuDatBan(maPhieu, thoiGianDat, khachHang, nhanVien, ban, soNguoi, tienCoc, ghiChu);
	            list.add(pdb);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return list;
	}

    // <<< THÊM PHƯƠNG THỨC NÀY >>>
    // 2. Thêm phiếu đặt bàn mới
    public boolean insertPhieuDatBan(PhieuDatBan p) {
        String sql = "INSERT INTO PhieuDatBan(MaPhieu, ThoiGianDat, MaKhachHang, MaNV, MaBan, SoNguoi, TienCoc, GhiChu) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getMaPhieu());
            ps.setTimestamp(2, Timestamp.valueOf(p.getThoiGianDat())); // Thời gian khách đến
            ps.setString(3, p.getKhachHang().getMaKhachHang());
            ps.setString(4, p.getNhanVien().getMaNV());
            ps.setString(5, p.getBan().getMaBan());
            ps.setInt(6, p.getSoNguoi());
            ps.setDouble(7, p.getTienCoc());
            ps.setString(8, p.getGhiChu());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("[DAO] Lỗi insertPhieuDatBan: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // <<< THÊM PHƯƠNG THỨC NÀY >>>
    // 3. Tự động tạo mã phiếu đặt bàn mới (ví dụ: PDB00001)
    public String generateNewID() {
        String newID = "PDB00001"; // Mã mặc định nếu bảng trống
        String sql = "SELECT TOP 1 MaPhieu FROM PhieuDatBan ORDER BY MaPhieu DESC";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastID = rs.getString("MaPhieu");
                if (lastID != null && lastID.matches("PDB\\d{5}")) {
                    int num = Integer.parseInt(lastID.substring(3)) + 1;
                    newID = String.format("PDB%05d", num);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO] Lỗi generateNewID PhieuDatBan: " + e.getMessage());
            e.printStackTrace();
        }
        return newID;
    }
}