package dao;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVien_DAO {

    // Lấy toàn bộ nhân viên (JOIN với tài khoản để lấy vai trò)
    public List<NhanVien> getAllNhanVien() {
        List<NhanVien> dsNV = new ArrayList<>();
        String sql = "SELECT nv.maNV, nv.tenNhanVien, nv.gioiTinh, nv.sdt, nv.email, " +
                     "tk.tenDangNhap, tk.matKhau, tk.vaiTro " +
                     "FROM NhanVien nv " +
                     "LEFT JOIN TaiKhoan tk ON nv.maNV = tk.maNV";

        try (Connection conn = new ConnectDB().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                TaiKhoan tk = new TaiKhoan(
                        rs.getString("maNV"),
                        rs.getString("tenDangNhap"),
                        rs.getString("matKhau"),
                        rs.getString("vaiTro")
                );

                NhanVien nv = new NhanVien(
                        rs.getString("maNV"),
                        rs.getString("tenNhanVien"),
                        rs.getBoolean("gioiTinh"),
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

    // Thêm nhân viên + tài khoản
    public boolean insertNhanVien(NhanVien nv) {
        String sqlNV = "INSERT INTO NhanVien(maNV, tenNhanVien, gioiTinh, sdt, email) VALUES(?, ?, ?, ?, ?)";
        String sqlTK = "INSERT INTO TaiKhoan(maNV, tenDangNhap, matKhau, vaiTro) VALUES(?, ?, ?, ?)";

        try (Connection conn = new ConnectDB().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtNV = conn.prepareStatement(sqlNV);
                 PreparedStatement pstmtTK = conn.prepareStatement(sqlTK)) {

                // insert NhanVien
                pstmtNV.setString(1, nv.getMaNV());
                pstmtNV.setString(2, nv.getTenNhanVien());
                pstmtNV.setBoolean(3, nv.isGioiTinh());
                pstmtNV.setString(4, nv.getSdt());
                pstmtNV.setString(5, nv.getEmail());
                pstmtNV.executeUpdate();

                // insert TaiKhoan
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

    // Cập nhật nhân viên + tài khoản
    public boolean updateNhanVien(NhanVien nv) {
        String sqlNV = "UPDATE NhanVien SET tenNhanVien=?, gioiTinh=?, sdt=?, email=? WHERE maNV=?";
        String sqlTK = "UPDATE TaiKhoan SET tenDangNhap=?, matKhau=?, vaiTro=? WHERE maNV=?";

        try (Connection conn = new ConnectDB().getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtNV = conn.prepareStatement(sqlNV);
                 PreparedStatement pstmtTK = conn.prepareStatement(sqlTK)) {

                // update NhanVien
                pstmtNV.setString(1, nv.getTenNhanVien());
                pstmtNV.setBoolean(2, nv.isGioiTinh());
                pstmtNV.setString(3, nv.getSdt());
                pstmtNV.setString(4, nv.getEmail());
                pstmtNV.setString(5, nv.getMaNV());
                pstmtNV.executeUpdate();

                // update TaiKhoan
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

    // Xóa nhân viên + tài khoản
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

    // Tìm nhân viên theo mã (JOIN với tài khoản)
    public NhanVien findByMaNV(String maNV) {
        String sql = "SELECT nv.maNV, nv.tenNhanVien, nv.gioiTinh, nv.sdt, nv.email, " +
                     "tk.tenDangNhap, tk.matKhau, tk.vaiTro " +
                     "FROM NhanVien nv " +
                     "LEFT JOIN TaiKhoan tk ON nv.maNV = tk.maNV " +
                     "WHERE nv.maNV=?";

        try (Connection conn = new ConnectDB().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, maNV);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    TaiKhoan tk = new TaiKhoan(
                            rs.getString("maNV"),
                            rs.getString("tenDangNhap"),
                            rs.getString("matKhau"),
                            rs.getString("vaiTro")
                    );

                    return new NhanVien(
                            rs.getString("maNV"),
                            rs.getString("tenNhanVien"),
                            rs.getBoolean("gioiTinh"),
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
        String sql = "SELECT nv.maNV, nv.tenNhanVien, nv.gioiTinh, nv.sdt, nv.email, " +
                     "tk.tenDangNhap, tk.matKhau, tk.vaiTro " +
                     "FROM NhanVien nv " +
                     "LEFT JOIN TaiKhoan tk ON nv.maNV = tk.maNV " +
                     "WHERE nv.maNV=?";

        try (Connection conn = ConnectDB.getConnection();
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

                    return new NhanVien(
                            rs.getString("maNV"),
                            rs.getString("tenNhanVien"),
                            rs.getBoolean("gioiTinh"),
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

}
