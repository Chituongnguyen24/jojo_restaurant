package dao;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhanVien_DAO {

    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> dsNV = new ArrayList<>();
        String sql = "SELECT nv.maNV, nv.tenNhanVien, nv.gioiTinh, nv.ngaySinh, nv.sdt, nv.email, " +
                     "tk.tenDangNhap, tk.matKhau, tk.vaiTro " +
                     "FROM NhanVien nv " +
                     "LEFT JOIN TaiKhoan tk ON nv.maNV = tk.maNV";

        try (Connection conn = new ConnectDB().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TaiKhoan tk = null;
                if (rs.getString("tenDangNhap") != null) {
                    tk = new TaiKhoan(
                            rs.getString("maNV"),
                            rs.getString("tenDangNhap"),
                            rs.getString("matKhau"),
                            rs.getString("vaiTro")
                    );
                }

                LocalDate ngaySinh = rs.getObject("ngaySinh", LocalDate.class);

                NhanVien nv = new NhanVien(
                        rs.getString("maNV"),
                        rs.getString("tenNhanVien"),
                        rs.getBoolean("gioiTinh"),
                        ngaySinh,
                        rs.getString("sdt"),
                        rs.getString("email"),
                        tk
                );

                dsNV.add(nv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsNV;
    }

    public boolean insertNhanVien(NhanVien nv) {
        String sqlNV = "INSERT INTO NhanVien(maNV, tenNhanVien, gioiTinh, ngaySinh, sdt, email) VALUES(?, ?, ?, ?, ?, ?)";
        String sqlTK = "INSERT INTO TaiKhoan(maNV, tenDangNhap, matKhau, vaiTro) VALUES(?, ?, ?, ?)";

        try (Connection conn = new ConnectDB().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtNV = conn.prepareStatement(sqlNV);
                 PreparedStatement pstmtTK = conn.prepareStatement(sqlTK)) {

                pstmtNV.setString(1, nv.getMaNV());
                pstmtNV.setString(2, nv.getTenNhanVien());
                pstmtNV.setBoolean(3, nv.isGioiTinh());
                pstmtNV.setObject(4, nv.getNgaySinh()); // Sử dụng setObject cho LocalDate
                pstmtNV.setString(5, nv.getSdt());
                pstmtNV.setString(6, nv.getEmail());
                pstmtNV.executeUpdate();

                if (nv.getTaiKhoan() != null) {
                    pstmtTK.setString(1, nv.getMaNV());
                    pstmtTK.setString(2, nv.getTaiKhoan().getTenDangNhap());
                    pstmtTK.setString(3, nv.getTaiKhoan().getMatKhau());
                    pstmtTK.setString(4, nv.getTaiKhoan().getVaiTro());
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
        String sqlNV = "UPDATE NhanVien SET tenNhanVien=?, gioiTinh=?, ngaySinh=?, sdt=?, email=? WHERE maNV=?";
        String sqlTK = "UPDATE TaiKhoan SET tenDangNhap=?, matKhau=?, vaiTro=? WHERE maNV=?";

        try (Connection conn = new ConnectDB().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtNV = conn.prepareStatement(sqlNV);
                 PreparedStatement pstmtTK = conn.prepareStatement(sqlTK)) {

                pstmtNV.setString(1, nv.getTenNhanVien());
                pstmtNV.setBoolean(2, nv.isGioiTinh());
                pstmtNV.setObject(3, nv.getNgaySinh()); // Sử dụng setObject cho LocalDate
                pstmtNV.setString(4, nv.getSdt());
                pstmtNV.setString(5, nv.getEmail());
                pstmtNV.setString(6, nv.getMaNV());
                pstmtNV.executeUpdate();

                if (nv.getTaiKhoan() != null) {
                    pstmtTK.setString(1, nv.getTaiKhoan().getTenDangNhap());
                    pstmtTK.setString(2, nv.getTaiKhoan().getMatKhau());
                    pstmtTK.setString(3, nv.getTaiKhoan().getVaiTro());
                    pstmtTK.setString(4, nv.getMaNV());
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

    public boolean deleteNhanVien(String maNV) {
        String sqlTK = "DELETE FROM TaiKhoan WHERE maNV=?";
        String sqlNV = "DELETE FROM NhanVien WHERE maNV=?";

        try (Connection conn = new ConnectDB().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtTK = conn.prepareStatement(sqlTK);
                 PreparedStatement pstmtNV = conn.prepareStatement(sqlNV)) {

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
        String sql = "SELECT nv.maNV, nv.tenNhanVien, nv.gioiTinh, nv.ngaySinh, nv.sdt, nv.email, " +
                     "tk.tenDangNhap, tk.matKhau, tk.vaiTro " +
                     "FROM NhanVien nv " +
                     "LEFT JOIN TaiKhoan tk ON nv.maNV = tk.maNV " +
                     "WHERE nv.maNV=?";

        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNV);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    TaiKhoan tk = null;
                     if (rs.getString("tenDangNhap") != null) {
                        tk = new TaiKhoan(
                            rs.getString("maNV"),
                            rs.getString("tenDangNhap"),
                            rs.getString("matKhau"),
                            rs.getString("vaiTro")
                        );
                     }
                    LocalDate ngaySinh = rs.getObject("ngaySinh", LocalDate.class);
                    return new NhanVien(
                            rs.getString("maNV"),
                            rs.getString("tenNhanVien"),
                            rs.getBoolean("gioiTinh"),
                            ngaySinh,
                            rs.getString("sdt"),
                            rs.getString("email"),
                            tk
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public NhanVien getNhanVienById(String maNV) {
        String sql = "SELECT nv.maNV, nv.tenNhanVien, nv.gioiTinh, nv.ngaySinh, nv.sdt, nv.email, " +
                     "tk.tenDangNhap, tk.matKhau, tk.vaiTro " +
                     "FROM NhanVien nv " +
                     "LEFT JOIN TaiKhoan tk ON nv.maNV = tk.maNV " +
                     "WHERE nv.maNV=?";

        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNV);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    TaiKhoan tk = null;
                    if (rs.getString("tenDangNhap") != null) {
                        tk = new TaiKhoan(
                                rs.getString("maNV"),
                                rs.getString("tenDangNhap"),
                                rs.getString("matKhau"),
                                rs.getString("vaiTro")
                        );
                    }
                    LocalDate ngaySinh = rs.getObject("ngaySinh", LocalDate.class);
                    return new NhanVien(
                            rs.getString("maNV"),
                            rs.getString("tenNhanVien"),
                            rs.getBoolean("gioiTinh"),
                            ngaySinh,
                            rs.getString("sdt"),
                            rs.getString("email"),
                            tk
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<NhanVien> timKiemVaLocNhanVien(String keyword, String vaiTroFilter) {
        List<NhanVien> dsNV = new ArrayList<>();
        String sql = "SELECT nv.maNV, nv.tenNhanVien, nv.gioiTinh, nv.ngaySinh, nv.sdt, nv.email, " +
                     "tk.tenDangNhap, tk.matKhau, tk.vaiTro " +
                     "FROM NhanVien nv " +
                     "LEFT JOIN TaiKhoan tk ON nv.maNV = tk.maNV ";
        
        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            conditions.add("(nv.tenNhanVien LIKE ? OR nv.sdt LIKE ? OR nv.email LIKE ? OR tk.tenDangNhap LIKE ? OR nv.maNV LIKE ?)");
            String likeKeyword = "%" + keyword + "%";
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

        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    TaiKhoan tk = null;
                    if (rs.getString("tenDangNhap") != null) {
                        tk = new TaiKhoan(
                                rs.getString("maNV"),
                                rs.getString("tenDangNhap"),
                                rs.getString("matKhau"),
                                rs.getString("vaiTro")
                        );
                    }
                    LocalDate ngaySinh = rs.getObject("ngaySinh", LocalDate.class);
                    NhanVien nv = new NhanVien(
                            rs.getString("maNV"),
                            rs.getString("tenNhanVien"),
                            rs.getBoolean("gioiTinh"),
                            ngaySinh,
                            rs.getString("sdt"),
                            rs.getString("email"),
                            tk
                    );
                    dsNV.add(nv);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi tìm kiếm và lọc nhân viên: " + e.getMessage());
            e.printStackTrace();
        }
        return dsNV;
    }
}