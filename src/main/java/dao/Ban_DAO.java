package dao;

import connectDB.ConnectDB;
import entity.Ban;
import enums.LoaiBan;
import enums.TrangThaiBan;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Ban_DAO {

    public List<Ban> getAllBan() {
        List<Ban> ds = new ArrayList<>();
        String sql = "SELECT * FROM Ban";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Ban ban = new Ban(
                    rs.getString("maBan"),
                    rs.getInt("soCho"),
                    LoaiBan.valueOf(rs.getString("loaiBan")),
                    rs.getString("maKhuVuc"),
                    TrangThaiBan.valueOf(rs.getString("trangThai"))
                );
                ds.add(ban);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public Ban getBanTheoMa(String maBan) {
        String sql = "SELECT * FROM Ban WHERE maBan = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maBan);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Ban(
                        rs.getString("maBan"),
                        rs.getInt("soCho"),
                        LoaiBan.valueOf(rs.getString("loaiBan")),
                        rs.getString("maKhuVuc"),
                        TrangThaiBan.valueOf(rs.getString("trangThai"))
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean themBan(Ban ban) {
        String sql = "INSERT INTO Ban (maBan, soCho, loaiBan, maKhuVuc, trangThai) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, ban.getMaBan());
            stmt.setInt(2, ban.getSoCho());
            stmt.setString(3, ban.getLoaiBan().toString());
            stmt.setString(4, ban.getMaKhuVuc());
            stmt.setString(5, ban.getTrangThai().toString());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean capNhatBan(Ban ban) {
        String sql = """
            UPDATE Ban
            SET soCho = ?, loaiBan = ?, maKhuVuc = ?, trangThai = ?
            WHERE maBan = ?
        """;
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, ban.getSoCho());
            stmt.setString(2, ban.getLoaiBan().toString());
            stmt.setString(3, ban.getMaKhuVuc());
            stmt.setString(4, ban.getTrangThai().toString());
            stmt.setString(5, ban.getMaBan());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoaBan(String maBan) {
        String sql = "DELETE FROM Ban WHERE maBan = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maBan);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Ban> getBanTheoKhuVuc(String maKhuVuc) {
        List<Ban> ds = new ArrayList<>();
        String sql = "SELECT * FROM Ban WHERE maKhuVuc = ?";
        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maKhuVuc);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(new Ban(
                        rs.getString("maBan"),
                        rs.getInt("soCho"),
                        LoaiBan.valueOf(rs.getString("loaiBan")),
                        rs.getString("maKhuVuc"),
                        TrangThaiBan.valueOf(rs.getString("trangThai"))
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public Map<String, Integer> getSoBanTheoKhuVuc() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT maKhuVuc, COUNT(maBan) AS soBan FROM Ban GROUP BY maKhuVuc";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                map.put(rs.getString("maKhuVuc"), rs.getInt("soBan"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public List<Ban> getBanTheoTrangThai(TrangThaiBan trangThai) {
        List<Ban> ds = new ArrayList<>();
        String sql = "SELECT * FROM Ban WHERE trangThai = ?";

        try (Connection con = ConnectDB.getInstance().getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, trangThai.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ds.add(new Ban(
                        rs.getString("maBan"),
                        rs.getInt("soCho"),
                        LoaiBan.valueOf(rs.getString("loaiBan")),
                        rs.getString("maKhuVuc"),
                        TrangThaiBan.valueOf(rs.getString("trangThai"))
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
}
