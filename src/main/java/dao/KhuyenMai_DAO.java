package dao;

import connectDB.ConnectDB;
import entity.KhuyenMai;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMai_DAO {

    private KhuyenMai createKhuyenMaiFromResultSet(ResultSet rs) throws SQLException {
        LocalDate ngayApDung = rs.getDate("NgayApDung") != null ? rs.getDate("NgayApDung").toLocalDate() : null;
        LocalDate ngayHetHan = rs.getDate("NgayHetHan") != null ? rs.getDate("NgayHetHan").toLocalDate() : null;
        Boolean trangThaiKM = (Boolean) rs.getObject("trangThaiKM");

        return new KhuyenMai(
            rs.getString("MaKM"),
            rs.getString("MoTa"),
            ngayApDung,
            ngayHetHan,
            rs.getDouble("MucKM"),
            trangThaiKM,
            rs.getString("LoaiKM")
        );
    }
    
    public List<KhuyenMai> getAllKhuyenMai() {
        List<KhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT * FROM KHUYENMAI WHERE trangThaiKM = 1 ORDER BY CASE WHEN MaKM = 'KM00000000' THEN 0 ELSE 1 END, NgayApDung DESC"; 
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ds.add(createKhuyenMaiFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean themKhuyenMai(KhuyenMai km) {
        String sql = "INSERT INTO KHUYENMAI(MaKM, MoTa, NgayApDung, NgayHetHan, MucKM, trangThaiKM, LoaiKM) VALUES (?, ?, ?, ?, ?, ?, ?)"; 
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, km.getMaKM()); 
            stmt.setString(2, km.getMoTa()); 
            stmt.setDate(3, km.getNgayApDung() != null ? Date.valueOf(km.getNgayApDung()) : null);
            stmt.setDate(4, km.getNgayHetHan() != null ? Date.valueOf(km.getNgayHetHan()) : null);
            stmt.setDouble(5, km.getMucKM());
            
            if (km.getTrangThaiKM() != null) {
                stmt.setBoolean(6, km.getTrangThaiKM());
            } else {
                stmt.setNull(6, java.sql.Types.BIT);
            }
            
            stmt.setString(7, km.getLoaiKM());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean capNhatKhuyenMai(KhuyenMai km) {
        String sql = "UPDATE KHUYENMAI SET MoTa=?, NgayApDung=?, NgayHetHan=?, MucKM=?, trangThaiKM=?, LoaiKM=? WHERE MaKM=?"; 
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, km.getMoTa()); 
            stmt.setDate(2, km.getNgayApDung() != null ? Date.valueOf(km.getNgayApDung()) : null);
            stmt.setDate(3, km.getNgayHetHan() != null ? Date.valueOf(km.getNgayHetHan()) : null);
            stmt.setDouble(4, km.getMucKM());
            
            if (km.getTrangThaiKM() != null) {
                stmt.setBoolean(5, km.getTrangThaiKM());
            } else {
                stmt.setNull(5, java.sql.Types.BIT);
            }
            
            stmt.setString(6, km.getLoaiKM());
            stmt.setString(7, km.getMaKM()); 

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoaKhuyenMai(String maKM) {
        String sql = "UPDATE KHUYENMAI SET trangThaiKM = 0 WHERE MaKM = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maKM);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<KhuyenMai> timKhuyenMai(String keyword) {
        List<KhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT * FROM KHUYENMAI WHERE (MoTa LIKE ? OR MaKM LIKE ?) AND trangThaiKM = 1 ORDER BY CASE WHEN MaKM = 'KM00000000' THEN 0 ELSE 1 END, NgayApDung DESC"; 

        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, "%" + keyword + "%");
            stmt.setString(2, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                ds.add(createKhuyenMaiFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public KhuyenMai getKhuyenMaiById(String maKM) {
        String sql = "SELECT * FROM KHUYENMAI WHERE MaKM = ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maKM);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return createKhuyenMaiFromResultSet(rs);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public String taoMaKMMoi() {
        String lastMaKM = "";
        String sql = "SELECT TOP 1 MaKM FROM KHUYENMAI WHERE MaKM LIKE 'KM[0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]' ORDER BY MaKM DESC";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                lastMaKM = rs.getString(1).trim();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (lastMaKM.isEmpty() || lastMaKM.length() < 10) {
            return "KM25000001";
        }
        
        String numPart = lastMaKM.substring(2); 
        int newNum;
        
        try {
            newNum = Integer.parseInt(numPart) + 1;
        } catch (NumberFormatException e) {
            return "KM25000001";
        }
        
        return "KM" + String.format("%08d", newNum);
    }
    
    public List<String> getUniqueLoaiKhuyenMai() {
        List<String> ds = new ArrayList<>();
        String sql = "SELECT DISTINCT LoaiKM FROM KHUYENMAI WHERE trangThaiKM = 1 AND LoaiKM IS NOT NULL ORDER BY LoaiKM";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ds.add(rs.getString("LoaiKM"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

	public List<KhuyenMai> getAllActiveKhuyenMai() {
        List<KhuyenMai> ds = new ArrayList<>();
        String sql = "SELECT * FROM KHUYENMAI WHERE trangThaiKM = 1 ORDER BY CASE WHEN MaKM = 'KM00000000' THEN 0 ELSE 1 END, NgayApDung DESC"; 
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ds.add(createKhuyenMaiFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
	}
}