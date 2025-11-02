package dao;

import connectDB.ConnectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.MonAn;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChiTietHoaDon_DAO {

    private MonAn_DAO monAnDAO = new MonAn_DAO();

    public boolean addChiTietHoaDon(ChiTietHoaDon ct) {
        String sql = "INSERT INTO CHITIETHOADON (MaHD, MaMonAn, DonGiaBan, SoLuong) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, ct.getHoaDon().getMaHD()); // SỬA: getMaHD
            pstmt.setString(2, ct.getMonAn().getMaMonAn());
            pstmt.setDouble(3, ct.getDonGiaBan());
            pstmt.setInt(4, ct.getSoLuong());
            
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<ChiTietHoaDon> getChiTietByMaHD(String maHD) {
        List<ChiTietHoaDon> dsCT = new ArrayList<>();
        String sql = "SELECT MaHD, MaMonAn, DonGiaBan, SoLuong FROM CHITIETHOADON WHERE MaHD = ?";
        
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maHD);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    HoaDon hd = new HoaDon(rs.getString("MaHD"));
                    MonAn ma = monAnDAO.getMonAnTheoMa(rs.getString("MaMonAn")); // SỬA: getMonAnTheoMa
                    double donGiaBan = rs.getDouble("DonGiaBan");
                    int soLuong = rs.getInt("SoLuong");
                    
                    ChiTietHoaDon ct = new ChiTietHoaDon(hd, ma, donGiaBan, soLuong); // SỬA: Thứ tự constructor
                    dsCT.add(ct);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsCT;
    }

    public boolean deleteChiTietByMaHD(String maHD) {
        String sql = "DELETE FROM CHITIETHOADON WHERE MaHD = ?";
        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, maHD);
            return pstmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}