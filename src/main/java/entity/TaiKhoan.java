package entity;

public class TaiKhoan {
    private int userID;
    private String tenDangNhap;
    private String matKhau;
    private String vaiTro;
    private Boolean trangThai;
    private NhanVien nhanVien;

    public TaiKhoan() {
    }

    public TaiKhoan(int userID, String tenDangNhap, String matKhau, String vaiTro, Boolean trangThai, NhanVien nhanVien) {
        this.userID = userID;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
        this.nhanVien = nhanVien;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public Boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(Boolean trangThai) {
        this.trangThai = trangThai;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    @Override
    public String toString() {
        return "TaiKhoan [userID=" + userID + ", tenDangNhap=" + tenDangNhap + ", vaiTro=" + vaiTro + "]";
    }
}