package dao;  

import connectDB.ConnectDB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LuuTru {
    private static LuuTru phuongThuc;
    private static ConnectDB connectDB;

    private LuuTru() {
    	connectDB = ConnectDB.getInstance();
    }

    public static LuuTru layPhuongThuc() {
        if (phuongThuc == null) {
            phuongThuc = new LuuTru();
        }
        return phuongThuc;
    }

    public List<Map<String, Object>> docTatCa(String tenBang, List<String> danhCacCot) {
        List<Map<String, Object>> ketQua = new ArrayList<>();
        String cauLenhSQL = "SELECT " + String.join(", ", danhCacCot) + " FROM " + tenBang;

        try (Connection ketNoi = connectDB.getConnection();
             PreparedStatement lenhChuanBi = ketNoi.prepareStatement(cauLenhSQL);
             ResultSet ketQuaTruyVan = lenhChuanBi.executeQuery()) {

            while (ketQuaTruyVan.next()) {
                Map<String, Object> hang = new java.util.HashMap<>();
                for (String cot : danhCacCot) {
                    hang.put(cot, ketQuaTruyVan.getObject(cot));
                }
                ketQua.add(hang);
            }
            System.out.println("Đọc thành công " + ketQua.size() + " bản ghi từ " + tenBang);
        } catch (SQLException loi) {
            System.err.println("Lỗi đọc dữ liệu từ " + tenBang + ": " + loi.getMessage());
            loi.printStackTrace();
        }
        return ketQua;
    }

    public boolean themMoi(String tenBang, List<String> danhCacCot, List<Object> danhGiaTri) {
        if (danhCacCot.size() != danhGiaTri.size()) {
            System.err.println("Số cột và giá trị không khớp!");
            return false;
        }

        String danhDauCho = String.join(", ", java.util.Collections.nCopies(danhCacCot.size(), "?"));
        String cauLenhSQL = "INSERT INTO " + tenBang + " (" + String.join(", ", danhCacCot) + ") VALUES (" + danhDauCho + ")";

        try (Connection ketNoi = connectDB.getConnection();
             PreparedStatement lenhChuanBi = ketNoi.prepareStatement(cauLenhSQL)) {

            for (int i = 0; i < danhGiaTri.size(); i++) {
                lenhChuanBi.setObject(i + 1, danhGiaTri.get(i));
            }
            int soHangBiAnhHuong = lenhChuanBi.executeUpdate();
            if (soHangBiAnhHuong > 0) {
                System.out.println("Thêm thành công " + soHangBiAnhHuong + " bản ghi vào " + tenBang);
                return true;
            }
        } catch (SQLException loi) {
            System.err.println("Lỗi thêm dữ liệu vào " + tenBang + ": " + loi.getMessage());
            loi.printStackTrace();
        }
        return false;
    }

    public boolean capNhat(String tenBang, List<String> danhCotCapNhat, String dieuKienWhere, List<Object> danhGiaTri) {
        String dieuKienSet = String.join(", ", danhCotCapNhat);
        String cauLenhSQL = "UPDATE " + tenBang + " SET " + dieuKienSet + " WHERE " + dieuKienWhere;

        try (Connection ketNoi = connectDB.getConnection();
             PreparedStatement lenhChuanBi = ketNoi.prepareStatement(cauLenhSQL)) {

            for (int i = 0; i < danhGiaTri.size(); i++) {
                lenhChuanBi.setObject(i + 1, danhGiaTri.get(i));
            }
            int soHangBiAnhHuong = lenhChuanBi.executeUpdate();
            if (soHangBiAnhHuong > 0) {
                System.out.println("Cập nhật thành công " + soHangBiAnhHuong + " bản ghi trong " + tenBang);
                return true;
            }
        } catch (SQLException loi) {
            System.err.println("Lỗi cập nhật dữ liệu trong " + tenBang + ": " + loi.getMessage());
            loi.printStackTrace();
        }
        return false;
    }

    public boolean xoa(String tenBang, String dieuKienWhere, List<Object> danhGiaTriWhere) {
        String cauLenhSQL = "DELETE FROM " + tenBang + " WHERE " + dieuKienWhere;

        try (Connection ketNoi = connectDB.getConnection();
             PreparedStatement lenhChuanBi = ketNoi.prepareStatement(cauLenhSQL)) {

            for (int i = 0; i < danhGiaTriWhere.size(); i++) {
                lenhChuanBi.setObject(i + 1, danhGiaTriWhere.get(i));
            }
            int soHangBiAnhHuong = lenhChuanBi.executeUpdate();
            if (soHangBiAnhHuong > 0) {
                System.out.println("Xóa thành công " + soHangBiAnhHuong + " bản ghi từ " + tenBang);
                return true;
            }
        } catch (SQLException loi) {
            System.err.println("Lỗi xóa dữ liệu từ " + tenBang + ": " + loi.getMessage());
            loi.printStackTrace();
        }
        return false;
    }

    public void dongKetNoi() {
    	connectDB.disconnect();
    }
}