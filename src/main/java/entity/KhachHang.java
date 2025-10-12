package entity;

public class KhachHang {
    private String maKhachHang;
    private String tenKhachHang;
    private String sdt;
    private String email;
    private int diemTichLuy;
    private boolean laThanhVien;

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
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

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    public boolean isLaThanhVien() {
        return laThanhVien;
    }

    public void setLaThanhVien(boolean laThanhVien) {
        this.laThanhVien = laThanhVien;
    }

    @Override
    public String toString() {
        return "KhachHang [maKhachHang=" + maKhachHang + ", tenKhachHang=" + tenKhachHang + ", sdt=" + sdt
                + ", email=" + email + ", diemTichLuy=" + diemTichLuy + ", laThanhVien=" + laThanhVien + "]";
    }

    public KhachHang(String maKhachHang, String tenKhachHang, String sdt, String email, int diemTichLuy, boolean laThanhVien) {
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.sdt = sdt;
        this.email = email;
        this.diemTichLuy = diemTichLuy;
        this.laThanhVien = laThanhVien;
    }

    public KhachHang() {}
}