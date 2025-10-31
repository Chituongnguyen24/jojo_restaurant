package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class HoaDon {
    private String maHD;
    private NhanVien nhanVien;
    private KhachHang khachHang;
    private Ban ban;
    private LocalDate ngayLapHoaDon;
    private LocalDateTime gioVao;
    private LocalDateTime gioRa;
    private String phuongThucThanhToan;
    private KhuyenMai khuyenMai;
    private Thue thue;
    private PhieuDatBan phieuDatBan;
    private double tongTienTruocThue;
    private double tongGiamGia;
    private boolean daThanhToan;

    public HoaDon() {
    }

    public HoaDon(String maHD) {
        this.maHD = maHD;
    }

    public HoaDon(String maHD, NhanVien nhanVien, KhachHang khachHang, Ban ban, LocalDate ngayLapHoaDon, LocalDateTime gioVao, LocalDateTime gioRa, String phuongThucThanhToan, KhuyenMai khuyenMai, Thue thue, PhieuDatBan phieuDatBan, double tongTienTruocThue, double tongGiamGia, boolean daThanhToan) {
        this.maHD = maHD;
        this.nhanVien = nhanVien;
        this.khachHang = khachHang;
        this.ban = ban;
        this.ngayLapHoaDon = ngayLapHoaDon;
        this.gioVao = gioVao;
        this.gioRa = gioRa;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.khuyenMai = khuyenMai;
        this.thue = thue;
        this.phieuDatBan = phieuDatBan;
        this.tongTienTruocThue = tongTienTruocThue;
        this.tongGiamGia = tongGiamGia;
        this.daThanhToan = daThanhToan;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public Ban getBan() {
        return ban;
    }

    public void setBan(Ban ban) {
        this.ban = ban;
    }

    public LocalDate getNgayLapHoaDon() {
        return ngayLapHoaDon;
    }

    public void setNgayLapHoaDon(LocalDate ngayLapHoaDon) {
        this.ngayLapHoaDon = ngayLapHoaDon;
    }

    public LocalDateTime getGioVao() {
        return gioVao;
    }

    public void setGioVao(LocalDateTime gioVao) {
        this.gioVao = gioVao;
    }

    public LocalDateTime getGioRa() {
        return gioRa;
    }

    public void setGioRa(LocalDateTime gioRa) {
        this.gioRa = gioRa;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public Thue getThue() {
        return thue;
    }

    public void setThue(Thue thue) {
        this.thue = thue;
    }

    public PhieuDatBan getPhieuDatBan() {
        return phieuDatBan;
    }

    public void setPhieuDatBan(PhieuDatBan phieuDatBan) {
        this.phieuDatBan = phieuDatBan;
    }

    public double getTongTienTruocThue() {
        return tongTienTruocThue;
    }

    public void setTongTienTruocThue(double tongTienTruocThue) {
        this.tongTienTruocThue = tongTienTruocThue;
    }

    public double getTongGiamGia() {
        return tongGiamGia;
    }

    public void setTongGiamGia(double tongGiamGiam) {
        this.tongGiamGia = tongGiamGiam;
    }

    public boolean isDaThanhToan() {
        return daThanhToan;
    }

    public void setDaThanhToan(boolean daThanhToan) {
        this.daThanhToan = daThanhToan;
    }

    @Override
    public String toString() {
        return maHD;
    }

    @Override
    public int hashCode() {
        return maHD.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HoaDon other = (HoaDon) obj;
        return maHD.equals(other.maHD);
    }
}