package entity;

import java.time.LocalDate;

public class KhachHang {
    private String maKH;
    private String tenKH;
    private String soDienThoai;
    private String email;
    private LocalDate ngaySinh;
    private int diemTichLuy;
    private boolean laThanhVien;

    public KhachHang() {
    }

    public KhachHang(String maKH) {
        this.maKH = maKH;
    }

    public KhachHang(String maKH, String tenKH, String soDienThoai, String email, LocalDate ngaySinh, int diemTichLuy, boolean laThanhVien) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.ngaySinh = ngaySinh;
        this.diemTichLuy = diemTichLuy;
        this.laThanhVien = laThanhVien;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
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
        return tenKH + " (" + maKH + ")";
    }

    @Override
    public int hashCode() {
        return maKH.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KhachHang other = (KhachHang) obj;
        return maKH.equals(other.maKH);
    }
}