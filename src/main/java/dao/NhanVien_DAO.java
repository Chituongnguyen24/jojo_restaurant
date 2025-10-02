package dao;

import connectDB.ConnectDB;
import entity.NhanVien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVien_DAO {
    
    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> dsNV = new ArrayList<>();
        String sql = "SELECT * FROM NhanVien";
        try (Connection conn = new ConnectDB().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                NhanVien nv = new NhanVien(
                        rs.getString("maNV"),
                        rs.getString("tenNhanVien"),
                        rs.getString("chucVu"),
                        rs.getBoolean("gioiTinh"),
                        rs.getString("sdt"),
                        rs.getString("email")
                );
                dsNV.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsNV;
    }

    // Thêm nhân viên
    public boolean insertNhanVien(NhanVien nv) {
        String sql = "INSERT INTO NhanVien(maNV, tenNhanVien, chucVu, gioiTinh, sdt, email) VALUES(?, ?, ?, ?, ?, ?)";
        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nv.getMaNV());
            pstmt.setString(2, nv.getTenNhanVien());
            pstmt.setString(3, nv.getChucVu());
            pstmt.setBoolean(4, nv.isGioiTinh());
            pstmt.setString(5, nv.getSdt());
            pstmt.setString(6, nv.getEmail());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật nhân viên
    public boolean updateNhanVien(NhanVien nv) {
        String sql = "UPDATE NhanVien SET tenNhanVien=?, chucVu=?, gioiTinh=?, sdt=?, email=? WHERE maNV=?";
        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nv.getTenNhanVien());
            pstmt.setString(2, nv.getChucVu());
            pstmt.setBoolean(3, nv.isGioiTinh());
            pstmt.setString(4, nv.getSdt());
            pstmt.setString(5, nv.getEmail());
            pstmt.setString(6, nv.getMaNV());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa nhân viên theo mã
    public boolean deleteNhanVien(String maNV) {
        String sql = "DELETE FROM NhanVien WHERE maNV=?";
        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maNV);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm nhân viên theo mã
    public NhanVien findByMaNV(String maNV) {
        String sql = "SELECT * FROM NhanVien WHERE maNV=?";
        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maNV);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new NhanVien(
                            rs.getString("maNV"),
                            rs.getString("tenNhanVien"),
                            rs.getString("chucVu"),
                            rs.getBoolean("gioiTinh"),
                            rs.getString("sdt"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
