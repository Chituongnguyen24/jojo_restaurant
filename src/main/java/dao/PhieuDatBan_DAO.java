package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;
import entity.Ban;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDatBan;
import enums.LoaiBan;

public class PhieuDatBan_DAO {

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
	            String maPhieu = rs.getString("MaPhieu");
	            LocalDateTime thoiGianDat = rs.getTimestamp("ThoiGianDat").toLocalDateTime();

	            // Lấy đối tượng KhachHang, NhanVien, Ban
	            String maKH = rs.getString("MaKH");
	            KhachHang khachHang = khDAO.getKhachHangById(maKH);

	            String maNV = rs.getString("MaNV");
	            NhanVien nhanVien = nvDAO.getNhanVienById(maNV);

	            String maBan = rs.getString("MaBan");
	            Ban ban = banDAO.getBanById(maBan);

	            double tienCoc = rs.getDouble("TienCoc");

	            // Tạo object PhieuDatBan
	            PhieuDatBan pdb = new PhieuDatBan(maPhieu, thoiGianDat, khachHang, nhanVien, ban, tienCoc);
	            list.add(pdb);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return list;
	}

}
