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
        TaiKhoan tk = null;
        
        // SỬA LỖI LOGIC: Kiểm tra tenDangNhap/userID để xác định có tài khoản liên kết không
        if (rs.getString("tenDangNhap") != null || rs.getInt("userID") > 0) { 
            // Ta tạo TaiKhoan
             tk = new TaiKhoan(
                rs.getInt("userID"), 
                rs.getString("tenDangNhap"),
                rs.getString("matKhau"),
                rs.getString("vaiTro"),
                rs.getBoolean("trangThai"),
                new NhanVien(rs.getString("maNhanVien")) // Gán tạm NV để tạo mối quan hệ
            );
        }

        LocalDate ngaySinh = rs.getObject("ngaySinh", LocalDate.class);
        LocalDate ngayVaoLam = rs.getObject("NgayVaoLam", LocalDate.class); 

        // SỬA LỖI BIÊN DỊCH: Đảm bảo truyền đủ 11 tham số vào constructor NhanVien mới
        NhanVien nv = new NhanVien(
            rs.getString("maNhanVien"),
            rs.getString("hoTen"),
            ngaySinh,
            ngayVaoLam, 
            rs.getString("SoCCCD"), 
            rs.getBoolean("GioiTinh"),
            rs.getString("SoDienThoai"), 
            rs.getString("Email"),
            rs.getString("ChucVu"), 
            rs.getString("TrangThai"), 
            tk // Tham số thứ 11
        );
        
        if (tk != null) {
            tk.setNhanVien(nv); // Thiết lập mối quan hệ hai chiều
        }
        
        return nv;
    }
    
    // Phương thức kiểm tra Tài khoản đã tồn tại chưa, giúp updateNhanVien sạch sẽ hơn
    private boolean checkTaiKhoanExists(Connection conn, String maNV) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TAIKHOAN WHERE maNhanVien=?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, maNV);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> dsNV = new ArrayList<>();
        String sql = "SELECT nv.*, tk.userID, tk.tenDangNhap, tk.matKhau, tk.vaiTro, tk.trangThai " +
                     "FROM NHANVIEN nv " +
                     "LEFT JOIN TAIKHOAN tk ON nv.maNhanVien = tk.maNhanVien";

        try (Connection conn = ConnectDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                dsNV.add(createNhanVienFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsNV;
    }

    public boolean insertNhanVien(NhanVien nv) {
        String sqlNV = "INSERT INTO NHANVIEN(maNhanVien, hoTen, ngaySinh, NgayVaoLam, SoCCCD, GioiTinh, SoDienThoai, Email, ChucVu, TrangThai) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlTK = "INSERT INTO TAIKHOAN(maNhanVien, tenDangNhap, matKhau, vaiTro, trangThai) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = ConnectDB.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtNV = conn.prepareStatement(sqlNV);
                 PreparedStatement pstmtTK = conn.prepareStatement(sqlTK)) {

                pstmtNV.setString(1, nv.getMaNhanVien()); // SỬA: Dùng getMaNhanVien()
                pstmtNV.setString(2, nv.getHoTen());
                pstmtNV.setObject(3, nv.getNgaySinh()); 
                pstmtNV.setObject(4, nv.getNgayVaoLam()); 
                pstmtNV.setString(5, nv.getSoCCCD()); 
                pstmtNV.setBoolean(6, nv.getGioiTinh()); // SỬA: Dùng getGioiTinh()
                pstmtNV.setString(7, nv.getSoDienThoai()); // SỬA: Dùng getSoDienThoai()
                pstmtNV.setString(8, nv.getEmail());
                pstmtNV.setString(9, nv.getChucVu()); 
                pstmtNV.setString(10, nv.getTrangThai()); 
                pstmtNV.executeUpdate();

                if (nv.getTaiKhoan() != null) {
                    pstmtTK.setString(1, nv.getMaNhanVien()); // SỬA: Dùng getMaNhanVien()
                    pstmtTK.setString(2, nv.getTaiKhoan().getTenDangNhap());
                    pstmtTK.setString(3, nv.getTaiKhoan().getMatKhau());
                    pstmtTK.setString(4, nv.getTaiKhoan().getVaiTro());
                    // SỬA: Đảm bảo trạng thái luôn có giá trị (True nếu null)
                    pstmtTK.setBoolean(5, nv.getTaiKhoan().getTrangThai() != null ? nv.getTaiKhoan().getTrangThai() : true);
                    pstmtTK.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateNhanVien(NhanVien nv) {
        String sqlNV = "UPDATE NHANVIEN SET hoTen=?, ngaySinh=?, NgayVaoLam=?, SoCCCD=?, GioiTinh=?, SoDienThoai=?, Email=?, ChucVu=?, TrangThai=? WHERE maNhanVien=?";
        String sqlTKUpdate = "UPDATE TAIKHOAN SET tenDangNhap=?, matKhau=?, vaiTro=?, trangThai=? WHERE maNhanVien=?";
        String sqlTKInsert = "INSERT INTO TAIKHOAN(maNhanVien, tenDangNhap, matKhau, vaiTro, trangThai) VALUES(?, ?, ?, ?, ?)";


        try (Connection conn = ConnectDB.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtNV = conn.prepareStatement(sqlNV)) {

                pstmtNV.setString(1, nv.getHoTen());
                pstmtNV.setObject(2, nv.getNgaySinh());
                pstmtNV.setObject(3, nv.getNgayVaoLam()); 
                pstmtNV.setString(4, nv.getSoCCCD()); 
                pstmtNV.setBoolean(5, nv.getGioiTinh()); // SỬA: Dùng getGioiTinh()
                pstmtNV.setString(6, nv.getSoDienThoai()); // SỬA: Dùng getSoDienThoai()
                pstmtNV.setString(7, nv.getEmail());
                pstmtNV.setString(8, nv.getChucVu()); 
                pstmtNV.setString(9, nv.getTrangThai()); 
                pstmtNV.setString(10, nv.getMaNhanVien()); // SỬA: Dùng getMaNhanVien()
                pstmtNV.executeUpdate();

                if (nv.getTaiKhoan() != null) {
                    // SỬA LỖI: Sử dụng helper method để kiểm tra sự tồn tại của TK
                    boolean tkExists = checkTaiKhoanExists(conn, nv.getMaNhanVien()); 

                    if (tkExists) {
                         try (PreparedStatement pstmtTK = conn.prepareStatement(sqlTKUpdate)) {
                            pstmtTK.setString(1, nv.getTaiKhoan().getTenDangNhap());
                            pstmtTK.setString(2, nv.getTaiKhoan().getMatKhau());
                            pstmtTK.setString(3, nv.getTaiKhoan().getVaiTro());
                            pstmtTK.setBoolean(4, nv.getTaiKhoan().getTrangThai()); 
                            pstmtTK.setString(5, nv.getMaNhanVien()); 
                            pstmtTK.executeUpdate();
                        }
                    } else {
                         try (PreparedStatement pstmtTK = conn.prepareStatement(sqlTKInsert)) {
                            pstmtTK.setString(1, nv.getMaNhanVien()); 
                            pstmtTK.setString(2, nv.getTaiKhoan().getTenDangNhap());
                            pstmtTK.setString(3, nv.getTaiKhoan().getMatKhau());
                            pstmtTK.setString(4, nv.getTaiKhoan().getVaiTro());
                            pstmtTK.setBoolean(5, nv.getTaiKhoan().getTrangThai() != null ? nv.getTaiKhoan().getTrangThai() : true);
                            pstmtTK.executeUpdate();
                        }
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteNhanVien(String maNV) {
        String sqlTK = "DELETE FROM TAIKHOAN WHERE maNhanVien=?";
        String sqlNV = "DELETE FROM NHANVIEN WHERE maNhanVien=?";

        try (Connection conn = ConnectDB.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtTK = conn.prepareStatement(sqlTK);
                 PreparedStatement pstmtNV = conn.prepareStatement(sqlNV)) {

                // Xóa TK trước để tránh lỗi khóa ngoại
                pstmtTK.setString(1, maNV);
                pstmtTK.executeUpdate();

                pstmtNV.setString(1, maNV);
                pstmtNV.executeUpdate();

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public NhanVien findByMaNV(String maNV) {
        String sql = "SELECT nv.*, tk.userID, tk.tenDangNhap, tk.matKhau, tk.vaiTro, tk.trangThai " +
                     "FROM NHANVIEN nv " +
                     "LEFT JOIN TAIKHOAN tk ON nv.maNhanVien = tk.maNhanVien " +
                     "WHERE nv.maNhanVien=?";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNV);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return createNhanVienFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public NhanVien getNhanVienById(String maNV) {
        return findByMaNV(maNV);
    }

    public List<NhanVien> timKiemVaLocNhanVien(String keyword, String vaiTroFilter) {
        List<NhanVien> dsNV = new ArrayList<>();
        String sql = "SELECT nv.*, tk.userID, tk.tenDangNhap, tk.matKhau, tk.vaiTro, tk.trangThai " +
                     "FROM NHANVIEN nv " +
                     "LEFT JOIN TAIKHOAN tk ON nv.maNhanVien = tk.maNhanVien ";
        
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            conditions.add("(nv.hoTen LIKE ? OR nv.SoDienThoai LIKE ? OR nv.Email LIKE ? OR tk.tenDangNhap LIKE ? OR nv.maNhanVien LIKE ? OR nv.SoCCCD LIKE ?)");
            String likeKeyword = "%" + keyword + "%";
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
            params.add(likeKeyword);
        }
        
        if (vaiTroFilter != null && !vaiTroFilter.equals("Tất cả")) {
            if ("NVQL".equals(vaiTroFilter) || "NVTT".equals(vaiTroFilter)) {
                conditions.add("tk.vaiTro = ?");
                params.add(vaiTroFilter);
            } else {
                return dsNV;
            }
        }
        
        if (!conditions.isEmpty()) {
            sql += " WHERE " + String.join(" AND ", conditions);
        }
        
        sql += " ORDER BY nv.maNhanVien";

        try (Connection conn = ConnectDB.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    dsNV.add(createNhanVienFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsNV;
    }
}