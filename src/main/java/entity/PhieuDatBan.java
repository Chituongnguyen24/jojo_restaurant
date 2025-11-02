package entity;

import java.time.LocalDateTime;

public class PhieuDatBan {
    private String maPhieu;
    private LocalDateTime thoiGianDenHen;
    private LocalDateTime thoiGianNhanBan;
    private LocalDateTime thoiGianTraBan;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private Ban ban;
    private int soNguoi;
    private String ghiChu;
    private String trangThaiPhieu;

    public PhieuDatBan() {
    }

    public PhieuDatBan(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public PhieuDatBan(String maPhieu, LocalDateTime thoiGianDenHen, LocalDateTime thoiGianNhanBan, LocalDateTime thoiGianTraBan, KhachHang khachHang, NhanVien nhanVien, Ban ban, int soNguoi, String ghiChu, String trangThaiPhieu) {
        this.maPhieu = maPhieu;
        this.thoiGianDenHen = thoiGianDenHen;
        this.thoiGianNhanBan = thoiGianNhanBan;
        this.thoiGianTraBan = thoiGianTraBan;
        this.khachHang = khachHang;
        this.nhanVien = nhanVien;
        this.ban = ban;
        this.soNguoi = soNguoi;
        this.ghiChu = ghiChu;
        this.trangThaiPhieu = trangThaiPhieu;
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public LocalDateTime getThoiGianDenHen() {
        return thoiGianDenHen;
    }

    public void setThoiGianDenHen(LocalDateTime thoiGianDenHen) {
        this.thoiGianDenHen = thoiGianDenHen;
    }

    public LocalDateTime getThoiGianNhanBan() {
        return thoiGianNhanBan;
    }

    public void setThoiGianNhanBan(LocalDateTime thoiGianNhanBan) {
        this.thoiGianNhanBan = thoiGianNhanBan;
    }

    public LocalDateTime getThoiGianTraBan() {
        return thoiGianTraBan;
    }

    public void setThoiGianTraBan(LocalDateTime thoiGianTraBan) {
        this.thoiGianTraBan = thoiGianTraBan;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public Ban getBan() {
        return ban;
    }

    public void setBan(Ban ban) {
        this.ban = ban;
    }

    public int getSoNguoi() {
        return soNguoi;
    }

    public void setSoNguoi(int soNguoi) {
        this.soNguoi = soNguoi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public String getTrangThaiPhieu() {
        return trangThaiPhieu;
    }

    public void setTrangThaiPhieu(String trangThaiPhieu) {
        this.trangThaiPhieu = trangThaiPhieu;
    }

    @Override
    public String toString() {
        return maPhieu;
    }

    @Override
    public int hashCode() {
        return maPhieu.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PhieuDatBan other = (PhieuDatBan) obj;
        return maPhieu.equals(other.maPhieu);
    }
}