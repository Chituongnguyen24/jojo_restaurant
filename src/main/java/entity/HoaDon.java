package entity;

import java.util.Date;

public class HoaDon {
    private String maHoaDon;
    private String maKhachHang;
    private Date ngayLap;
    private String phuongThuc;
    private String maKhuyenMai;
    private String maThue;
    private Date gioVao;
    private Date gioRa;
    private String maNhanVien;
    private String maPhieu;

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public Date getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(Date ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        this.maKhuyenMai = maKhuyenMai;
    }

    public String getMaThue() {
        return maThue;
    }

    public void setMaThue(String maThue) {
        this.maThue = maThue;
    }

    public Date getGioVao() {
        return gioVao;
    }

    public void setGioVao(Date gioVao) {
        this.gioVao = gioVao;
    }

    public Date getGioRa() {
        return gioRa;
    }

    public void setGioRa(Date gioRa) {
        this.gioRa = gioRa;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    @Override
    public String toString() {
        return "HoaDon [maHoaDon=" + maHoaDon + ", maKhachHang=" + maKhachHang + ", ngayLap=" + ngayLap
                + ", phuongThuc=" + phuongThuc + ", maKhuyenMai=" + maKhuyenMai + ", maThue=" + maThue
                + ", gioVao=" + gioVao + ", gioRa=" + gioRa + ", maNhanVien=" + maNhanVien + ", maPhieu=" + maPhieu + "]";
    }

    public HoaDon(String maHoaDon, String maKhachHang, Date ngayLap, String phuongThuc, String maKhuyenMai, String maThue, Date gioVao, Date gioRa, String maNhanVien, String maPhieu) {
        this.maHoaDon = maHoaDon;
        this.maKhachHang = maKhachHang;
        this.ngayLap = ngayLap;
        this.phuongThuc = phuongThuc;
        this.maKhuyenMai = maKhuyenMai;
        this.maThue = maThue;
        this.gioVao = gioVao;
        this.gioRa = gioRa;
        this.maNhanVien = maNhanVien;
        this.maPhieu = maPhieu;
    }

    public HoaDon() {}
}