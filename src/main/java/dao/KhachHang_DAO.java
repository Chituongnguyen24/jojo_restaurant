package dao;

import entity.KhachHang;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import connectDB.ConnectDB;

public class KhachHang_DAO {

    public List<KhachHang> getAllKhachHang() {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang";

        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("sdt"),
                        rs.getString("email"),
                        rs.getInt("diemTichLuy"),
                        rs.getBoolean("laThanhVien")
                );
                ds.add(kh);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean themKhachHang(KhachHang kh) {
        String sql = "INSERT INTO KhachHang VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, kh.getMaKhachHang());
            stmt.setString(2, kh.getTenKhachHang());
            stmt.setString(3, kh.getSdt());
            stmt.setString(4, kh.getEmail());
            stmt.setInt(5, kh.getDiemTichLuy());
            stmt.setBoolean(6, kh.isLaThanhVien());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoaKhachHang(String maKH) {
        String sql = "DELETE FROM KhachHang WHERE maKhachHang = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maKH);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean capNhatKhachHang(KhachHang kh) {
        String sql = "UPDATE KhachHang SET tenKhachHang=?, sdt=?, email=?, diemTichLuy=?, laThanhVien=? WHERE maKhachHang=?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, kh.getTenKhachHang());
            stmt.setString(2, kh.getSdt());
            stmt.setString(3, kh.getEmail());
            stmt.setInt(4, kh.getDiemTichLuy());
            stmt.setBoolean(5, kh.isLaThanhVien());
            stmt.setString(6, kh.getMaKhachHang());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<KhachHang> timKiemKhachHang(String keyword) {
        List<KhachHang> ds = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE tenKhachHang LIKE ? OR sdt LIKE ?";

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                KhachHang kh = new KhachHang(
                        rs.getString("maKhachHang"),
                        rs.getString("tenKhachHang"),
                        rs.getString("sdt"),
                        rs.getString("email"),
                        rs.getInt("diemTichLuy"),
                        rs.getBoolean("laThanhVien")
                );
                ds.add(kh);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

	public KhachHang findByMaKH(String maKH) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean deleteKhachHang(String maKH) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean updateKhachHang(KhachHang khachHang) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean insertKhachHang(KhachHang kh) {
		// TODO Auto-generated method stub
		return false;
	}
}
