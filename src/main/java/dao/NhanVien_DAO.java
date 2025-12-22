package dao;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NhanVien_DAO {

    private NhanVien createNhanVienFromResultSet(ResultSet rs) throws SQLException {
        String maNV = rs.getString("maNhanVien").trim();
        String hoTen = rs.getString("hoTen");
        Boolean gioiTinh = (Boolean) rs.getObject("GioiTinh");
        String sdt = rs.getString("SoDienThoai") != null ? rs.getString("SoDienThoai").trim() : "";
        String email = rs.getString("Email");
        String cccd = rs.getString("SoCCCD");
        
        LocalDate ngaySinh = rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null;
        LocalDate ngayVaoLam = rs.getDate("NgayVaoLam") != null ? rs.getDate("NgayVaoLam").toLocalDate() : null;
        String trangThaiNV = rs.getString("TrangThai");

        NhanVien nv = new NhanVien(maNV);
        nv.setHoTen(hoTen);
        nv.setGioiTinh(gioiTinh);
        nv.setSoDienThoai(sdt);
        nv.setEmail(email);
        nv.setSoCCCD(cccd);
        nv.setNgaySinh(ngaySinh);
        nv.setNgayVaoLam(ngayVaoLam);
        nv.setTrangThai(trangThaiNV);

        int userID = rs.getInt("userID");
        String tenDangNhap = rs.getString("tenDangNhap") != null ? rs.getString("tenDangNhap").trim() : "";
        String matKhau = rs.getString("matKhau");
        String vaiTro = rs.getString("vaiTro") != null ? rs.getString("vaiTro").trim() : "";
        Boolean trangThaiTK = rs.getBoolean("trangThaiTK");

        nv.setChucVu("NVQL".equals(vaiTro) ? "Quản lý" : "Nhân viên thu ngân");

        TaiKhoan tk = new TaiKhoan(userID, tenDangNhap, matKhau, vaiTro, trangThaiTK, nv);
        nv.setTaiKhoan(tk);

        return nv;
    }

    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> ds = new ArrayList<>();
        String sql = "SELECT NV.*, TK.userID, TK.tenDangNhap, TK.matKhau, TK.vaiTro, TK.trangThai AS trangThaiTK " +
                     "FROM NHANVIEN NV " +
                     "LEFT JOIN TAIKHOAN TK ON NV.maNhanVien = TK.maNhanVien " +
                     "WHERE NV.TrangThai != N'Đã xóa'";

        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ds.add(createNhanVienFromResultSet(rs));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }
    
    public List<NhanVien> getAllNhanVienFull() {
        return getAllNhanVien();
    }

    public boolean isSoDienThoaiExists(String sdt, String currentMaNV) {
        String sql = "SELECT COUNT(*) FROM NHANVIEN WHERE SoDienThoai = ? AND maNhanVien != ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, sdt);
            stmt.setString(2, currentMaNV != null ? currentMaNV : ""); 
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEmailExists(String email, String currentMaNV) {
        if (email == null || email.isEmpty()) return false;
        
        String sql = "SELECT COUNT(*) FROM NHANVIEN WHERE Email = ? AND maNhanVien != ?";
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, currentMaNV != null ? currentMaNV : ""); 
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean themNhanVien(NhanVien nv, String matKhau) {
        String sqlNV = "INSERT INTO NHANVIEN(maNhanVien, hoTen, ngaySinh, NgayVaoLam, SoCCCD, GioiTinh, SoDienThoai, Email, ChucVu, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; 
        String sqlTK = "INSERT INTO TAIKHOAN(maNhanVien, tenDangNhap, matKhau, vaiTro, trangThai) VALUES (?, ?, ?, ?, ?)";
        
        Connection con = ConnectDB.getConnection();
        boolean success = false;
        
        try {
            con.setAutoCommit(false);

            try (PreparedStatement stmtNV = con.prepareStatement(sqlNV)) {
                stmtNV.setString(1, nv.getMaNhanVien()); 
                stmtNV.setString(2, nv.getHoTen()); 
                stmtNV.setDate(3, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
                stmtNV.setDate(4, nv.getNgayVaoLam() != null ? Date.valueOf(nv.getNgayVaoLam()) : null);
                stmtNV.setString(5, nv.getSoCCCD());
                stmtNV.setObject(6, nv.getGioiTinh()); 
                stmtNV.setString(7, nv.getSoDienThoai()); 
                stmtNV.setString(8, nv.getEmail());
                stmtNV.setString(9, nv.getChucVu().equals("Quản lý") ? "NVQL" : "NVTT");
                stmtNV.setString(10, nv.getTrangThai());
                
                if (stmtNV.executeUpdate() == 0) throw new SQLException("Thêm NHANVIEN thất bại.");
            }
            
            try (PreparedStatement stmtTK = con.prepareStatement(sqlTK)) {
                String vaiTro = nv.getChucVu().equals("Quản lý") ? "NVQL" : "NVTT";
                String tenDN = nv.getTaiKhoan() != null && !nv.getTaiKhoan().getTenDangNhap().isEmpty() 
                                ? nv.getTaiKhoan().getTenDangNhap() 
                                : (nv.getEmail() != null && !nv.getEmail().isEmpty() ? nv.getEmail().split("@")[0] : nv.getSoDienThoai());
                
                stmtTK.setString(1, nv.getMaNhanVien()); 
                stmtTK.setString(2, tenDN);
                stmtTK.setString(3, matKhau);
                stmtTK.setString(4, vaiTro);
                stmtTK.setBoolean(5, true); 
                
                if (stmtTK.executeUpdate() == 0) throw new SQLException("Thêm TAIKHOAN thất bại.");
                
                if(nv.getTaiKhoan() != null) nv.getTaiKhoan().setTenDangNhap(tenDN);
            }
            
            con.commit();
            success = true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public boolean capNhatNhanVien(NhanVien nv) {
        String sqlNV = "UPDATE NHANVIEN SET hoTen=?, ngaySinh=?, NgayVaoLam=?, SoCCCD=?, GioiTinh=?, SoDienThoai=?, Email=?, ChucVu=?, TrangThai=? WHERE maNhanVien=?"; 
        String sqlTK = "UPDATE TAIKHOAN SET vaiTro=? WHERE maNhanVien=?"; 

        Connection con = ConnectDB.getConnection();
        boolean success = false;
        
        try {
            con.setAutoCommit(false); 

            try (PreparedStatement stmtNV = con.prepareStatement(sqlNV)) {
                stmtNV.setString(1, nv.getHoTen()); 
                stmtNV.setDate(2, nv.getNgaySinh() != null ? Date.valueOf(nv.getNgaySinh()) : null);
                stmtNV.setDate(3, nv.getNgayVaoLam() != null ? Date.valueOf(nv.getNgayVaoLam()) : null);
                stmtNV.setString(4, nv.getSoCCCD());
                stmtNV.setObject(5, nv.getGioiTinh());
                stmtNV.setString(6, nv.getSoDienThoai()); 
                stmtNV.setString(7, nv.getEmail());
                stmtNV.setString(8, nv.getChucVu().equals("Quản lý") ? "NVQL" : "NVTT");
                stmtNV.setString(9, nv.getTrangThai());
                stmtNV.setString(10, nv.getMaNhanVien()); 

                if (stmtNV.executeUpdate() == 0) throw new SQLException("Cập nhật NHANVIEN thất bại.");
            }
            
            try (PreparedStatement stmtTK = con.prepareStatement(sqlTK)) {
                String vaiTro = nv.getChucVu().equals("Quản lý") ? "NVQL" : "NVTT";
                stmtTK.setString(1, vaiTro);
                stmtTK.setString(2, nv.getMaNhanVien()); 
                
                stmtTK.executeUpdate(); 
            }
            
            con.commit();
            success = true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public boolean anNhanVien(String maNV) {
        String sqlUpdateNV = "UPDATE NHANVIEN SET TrangThai = N'Đã xóa' WHERE maNhanVien = ?";
        String sqlUpdateTK = "UPDATE TAIKHOAN SET trangThai = 0 WHERE maNhanVien = ?";
        
        Connection con = ConnectDB.getConnection();
        boolean success = false;

        try {
            con.setAutoCommit(false);

            try (PreparedStatement stmt1 = con.prepareStatement(sqlUpdateNV)) {
                stmt1.setString(1, maNV);
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = con.prepareStatement(sqlUpdateTK)) {
                stmt2.setString(1, maNV);
                stmt2.executeUpdate();
            }

            con.commit();
            success = true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }

    public boolean xoaNhanVien(String maNV) {
        String sqlUpdateNV = "UPDATE NHANVIEN SET TrangThai = N'Đã nghỉ' WHERE maNhanVien = ?";
        String sqlUpdateTK = "UPDATE TAIKHOAN SET trangThai = 0 WHERE maNhanVien = ?";
        
        Connection con = ConnectDB.getConnection();
        boolean success = false;

        try {
            con.setAutoCommit(false);

            try (PreparedStatement stmt1 = con.prepareStatement(sqlUpdateNV)) {
                stmt1.setString(1, maNV);
                stmt1.executeUpdate();
            }

            try (PreparedStatement stmt2 = con.prepareStatement(sqlUpdateTK)) {
                stmt2.setString(1, maNV);
                stmt2.executeUpdate();
            }

            con.commit();
            success = true;

        } catch (SQLException e) {
            try {
                con.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return success;
    }
    
    public String taoMaNVMoi() {
        String lastMaNV = "";
        String sql = "SELECT TOP 1 maNhanVien FROM NHANVIEN WHERE maNhanVien LIKE 'NV%' ORDER BY maNhanVien DESC";
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                lastMaNV = rs.getString(1).trim();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (lastMaNV.isEmpty()) {
            return "NVTT001";
        }
        
        try {
            String prefix = lastMaNV.substring(0, 4);
            String numPart = lastMaNV.substring(4); 
            int newNum = Integer.parseInt(numPart) + 1;
            return prefix + String.format("%03d", newNum);
        } catch (Exception e) {
             return "NVTT001";
        }
    }
    
    public NhanVien getNhanVienById(String maNV) {
        String sql = "SELECT NV.*, TK.userID, TK.tenDangNhap, TK.matKhau, TK.vaiTro, TK.trangThai AS trangThaiTK " +
                     "FROM NHANVIEN NV " +
                     "LEFT JOIN TAIKHOAN TK ON NV.maNhanVien = TK.maNhanVien " +
                     "WHERE NV.maNhanVien = ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maNV);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createNhanVienFromResultSet(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public Map<String, NhanVien> getAllByMaList(List<String> maList) {
        Map<String, NhanVien> map = new HashMap<>();
        if (maList.isEmpty()) return map;

        String placeholders = String.join(",", Collections.nCopies(maList.size(), "?"));
        String sql = "SELECT NV.*, TK.userID, TK.tenDangNhap, TK.matKhau, TK.vaiTro, TK.trangThai AS trangThaiTK " +
                     "FROM NHANVIEN NV " +
                     "LEFT JOIN TAIKHOAN TK ON NV.maNhanVien = TK.maNhanVien " +
                     "WHERE NV.maNhanVien IN (" + placeholders + ")";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < maList.size(); i++) {
                pstmt.setString(i + 1, maList.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    NhanVien nv = createNhanVienFromResultSet(rs);
                    map.put(nv.getMaNhanVien().trim(), nv);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}