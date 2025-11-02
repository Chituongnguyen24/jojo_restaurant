package dao;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhanVien_DAO {

    private NhanVien createNhanVienFromResultSet(ResultSet rs) throws SQLException {
        LocalDate ngaySinh = rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null;
        LocalDate ngayVaoLam = rs.getDate("ngayVaoLam") != null ? rs.getDate("ngayVaoLam").toLocalDate() : null;
        Boolean gioiTinh = (Boolean) rs.getObject("gioiTinh");
        
        // Sửa lỗi: Bổ sung khai báo biến maNV và userID
        String maNV = rs.getString("maNV").trim(); 
        int userID = rs.getInt("userID");

        TaiKhoan tk = new TaiKhoan();
        
        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(maNV);
        nv.setHoTen(rs.getString("tenNhanVien"));
        nv.setGioiTinh(gioiTinh);
        nv.setSoDienThoai(rs.getString("sdt").trim());
        nv.setEmail(rs.getString("email"));
        
        nv.setNgaySinh(ngaySinh); 
        nv.setNgayVaoLam(ngayVaoLam); 
        nv.setSoCCCD(null);
        // Các trường này cần được JOIN từ bảng TAIKHOAN
        nv.setChucVu(rs.getString("vaiTro").trim().equals("NVQL") ? "Quản lý" : "Nhân viên thu ngân"); 
        nv.setTrangThai(rs.getBoolean("trangThai") ? "Đang làm" : "Đã nghỉ"); 
        
        // Cần tạo TaiKhoan đầy đủ để gán
        tk = new TaiKhoan(userID, rs.getString("tenDangNhap").trim(), rs.getString("matKhau"), rs.getString("vaiTro").trim(), rs.getBoolean("trangThai"), nv);
        
        nv.setTaiKhoan(tk); 
        
        return nv;
    }

    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> ds = new ArrayList<>();
        // Sửa truy vấn: Thêm userID và trangThai để khớp với Entity TaiKhoan
        String sql = "SELECT NV.*, TK.vaiTro, TK.tenDangNhap, TK.matKhau, TK.userID, TK.trangThai FROM NHANVIEN NV " + 
                     "INNER JOIN TAIKHOAN TK ON NV.maNhanVien = TK.maNhanVien";
        
        try (Connection con = ConnectDB.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String maNV = rs.getString("maNhanVien").trim();
                Boolean gioiTinh = (Boolean) rs.getObject("GioiTinh");
                
                String vaiTro = rs.getString("vaiTro").trim();
                Boolean trangThai = rs.getBoolean("trangThai");
                
                NhanVien nv = new NhanVien(maNV);
                nv.setHoTen(rs.getString("hoTen"));
                nv.setGioiTinh(gioiTinh);
                nv.setSoDienThoai(rs.getString("SoDienThoai").trim());
                nv.setEmail(rs.getString("Email"));
                nv.setNgaySinh(rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null);
                nv.setNgayVaoLam(rs.getDate("NgayVaoLam") != null ? rs.getDate("NgayVaoLam").toLocalDate() : null);
                nv.setSoCCCD(rs.getString("SoCCCD"));
                
                nv.setChucVu(vaiTro.equals("NVQL") ? "Quản lý" : "Nhân viên thu ngân");
                nv.setTrangThai(rs.getString("TrangThai")); 
                
                TaiKhoan tk = new TaiKhoan(rs.getInt("userID"), rs.getString("tenDangNhap").trim(), rs.getString("matKhau"), vaiTro, trangThai, nv);
                nv.setTaiKhoan(tk);
                
                ds.add(nv);
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
            stmt.setString(2, currentMaNV != null ? currentMaNV : "NV0000000"); 
            
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
            stmt.setString(2, currentMaNV != null ? currentMaNV : "NV0000000"); 
            
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
                stmtNV.setBoolean(6, nv.getGioiTinh() != null ? nv.getGioiTinh() : true);
                stmtNV.setString(7, nv.getSoDienThoai()); 
                stmtNV.setString(8, nv.getEmail());
                stmtNV.setString(9, nv.getChucVu().equals("Quản lý") ? "NVQL" : "NVTT");
                stmtNV.setString(10, nv.getTrangThai());
                
                if (stmtNV.executeUpdate() == 0) throw new SQLException("Thêm NHANVIEN thất bại.");
            }
            
            try (PreparedStatement stmtTK = con.prepareStatement(sqlTK)) {
                String vaiTro = nv.getChucVu().equals("Quản lý") ? "NVQL" : "NVTT";
                String tenDN = nv.getEmail() != null && !nv.getEmail().isEmpty() ? 
                                nv.getEmail().split("@")[0] : nv.getSoDienThoai();
                
                stmtTK.setString(1, nv.getMaNhanVien()); 
                stmtTK.setString(2, tenDN);
                stmtTK.setString(3, matKhau);
                stmtTK.setString(4, vaiTro);
                stmtTK.setBoolean(5, true); 
                
                if (stmtTK.executeUpdate() == 0) throw new SQLException("Thêm TAIKHOAN thất bại.");
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
                stmtNV.setBoolean(5, nv.getGioiTinh() != null ? nv.getGioiTinh() : true);
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

    public boolean xoaNhanVien(String maNV) {
        // Cập nhật trạng thái TrangThai=Đã nghỉ trong bảng NHANVIEN và trangThai=0 trong TAIKHOAN
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
                int affected = stmt2.executeUpdate();
                if (affected == 0) throw new SQLException("Cập nhật TAIKHOAN thất bại, có thể không tồn tại.");
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
            return "NVTT011"; // Bắt đầu từ mã lớn nhất tiếp theo sau NVTT010
        }
        
        String prefix = lastMaNV.substring(0, 4); // Lấy NVQL hoặc NVTT
        String numPart = lastMaNV.substring(4); 
        
        try {
            int newNum = Integer.parseInt(numPart) + 1;
            return prefix + String.format("%03d", newNum);
        } catch (NumberFormatException e) {
             return "NVTT011"; // Mã dự phòng
        }
    }
    
    public NhanVien getNhanVienById(String maNV) {
        String sql = "SELECT NV.*, TK.vaiTro, TK.tenDangNhap, TK.matKhau, TK.userID, TK.trangThai FROM NHANVIEN NV " + 
                     "INNER JOIN TAIKHOAN TK ON NV.maNhanVien = TK.maNhanVien WHERE NV.maNhanVien = ?";
        
        try (Connection con = ConnectDB.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, maNV);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Boolean gioiTinh = (Boolean) rs.getObject("GioiTinh");
                
                int userID = rs.getInt("userID");
                String tenDangNhap = rs.getString("tenDangNhap").trim();
                String matKhau = rs.getString("matKhau");
                String vaiTro = rs.getString("vaiTro").trim();
                Boolean trangThai = rs.getBoolean("trangThai");
                
                NhanVien nv = new NhanVien(maNV);
                nv.setHoTen(rs.getString("hoTen"));
                nv.setGioiTinh(gioiTinh);
                nv.setSoDienThoai(rs.getString("SoDienThoai").trim());
                nv.setEmail(rs.getString("Email"));
                nv.setNgaySinh(rs.getDate("ngaySinh") != null ? rs.getDate("ngaySinh").toLocalDate() : null);
                nv.setNgayVaoLam(rs.getDate("NgayVaoLam") != null ? rs.getDate("NgayVaoLam").toLocalDate() : null);
                nv.setSoCCCD(rs.getString("SoCCCD"));
                
                nv.setChucVu(vaiTro.equals("NVQL") ? "Quản lý" : "Nhân viên thu ngân");
                nv.setTrangThai(rs.getString("TrangThai")); 
                
                TaiKhoan tk = new TaiKhoan(userID, tenDangNhap, matKhau, vaiTro, trangThai, nv);
                
                nv.setTaiKhoan(tk);
                
                return nv;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}