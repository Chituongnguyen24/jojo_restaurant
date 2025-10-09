package entity;

public class NhanVien {
    private String maNV;
    private String tenNhanVien;
    private boolean gioiTinh;
    private String sdt;
    private String email;
    private TaiKhoan taiKhoan; // Liên kết với tài khoản, trong đó có vai trò

    public NhanVien() {}

    public NhanVien(String maNV, String tenNhanVien, boolean gioiTinh, String sdt, String email, TaiKhoan taiKhoan) {
        this.maNV = maNV;
        this.tenNhanVien = tenNhanVien;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
        this.email = email;
        this.taiKhoan = taiKhoan;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public boolean isGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    @Override
    public String toString() {
        return "NhanVien [maNV=" + maNV 
                + ", tenNhanVien=" + tenNhanVien 
                + ", gioiTinh=" + gioiTinh 
                + ", sdt=" + sdt 
                + ", email=" + email 
                + ", vaiTro=" + (taiKhoan != null ? taiKhoan.getVaiTro() : "Chưa có") 
                + "]";
    }
}
