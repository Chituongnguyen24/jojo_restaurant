package entity;

import java.time.LocalDate;

public class HoaDon {
    private String maHD;
    private String khachHang;
    private LocalDate ngayLap;
    private double tongTien;
    private String phuongThuc;
    private String trangThai;

    // Constructor
    public HoaDon(String maHD, String khachHang, LocalDate ngayLap, double tongTien, String phuongThuc, String trangThai) {
        this.maHD = maHD;
        this.khachHang = khachHang;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
        this.phuongThuc = phuongThuc;
        this.trangThai = trangThai;
    }

    // Getters/Setters
    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }

    public String getKhachHang() { return khachHang; }
    public void setKhachHang(String khachHang) { this.khachHang = khachHang; }

    public LocalDate getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDate ngayLap) { this.ngayLap = ngayLap; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public String getPhuongThuc() { return phuongThuc; }
    public void setPhuongThuc(String phuongThuc) { this.phuongThuc = phuongThuc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}