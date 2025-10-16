package entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class HoaDon {
    private String maHoaDon;
    private KhachHang khachHang; 
    private NhanVien nhanVien;  
    private PhieuDatBan phieuDatBan; 
    private KhuyenMai khuyenMai;
    private Thue thue;          
    private LocalDate ngayLap;
    private LocalDateTime gioVao;
    private LocalDateTime gioRa;
    private String phuongThuc;
    private boolean daThanhToan;
    
    public HoaDon() {
    }



	public HoaDon(String maHoaDon, KhachHang khachHang, NhanVien nhanVien, PhieuDatBan phieuDatBan, KhuyenMai khuyenMai,
			Thue thue, LocalDate ngayLap, LocalDateTime gioVao, LocalDateTime gioRa, String phuongThuc,
			boolean daThanhToan) {
		super();
		this.maHoaDon = maHoaDon;
		this.khachHang = khachHang;
		this.nhanVien = nhanVien;
		this.phieuDatBan = phieuDatBan;
		this.khuyenMai = khuyenMai;
		this.thue = thue;
		this.ngayLap = ngayLap;
		this.gioVao = gioVao;
		this.gioRa = gioRa;
		this.phuongThuc = phuongThuc;
		this.daThanhToan = daThanhToan;
	}



	public HoaDon(String maHoaDon, KhachHang khachHang, NhanVien nhanVien, PhieuDatBan phieuDatBan) {
		super();
		this.maHoaDon = maHoaDon;
		this.khachHang = khachHang;
		this.nhanVien = nhanVien;
		this.phieuDatBan = phieuDatBan;
	}



	public String getMaHoaDon() {
		return maHoaDon;
	}



	public void setMaHoaDon(String maHoaDon) {
		this.maHoaDon = maHoaDon;
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



	public PhieuDatBan getPhieuDatBan() {
		return phieuDatBan;
	}



	public void setPhieuDatBan(PhieuDatBan phieuDatBan) {
		this.phieuDatBan = phieuDatBan;
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



	public LocalDate getNgayLap() {
		return ngayLap;
	}



	public void setNgayLap(LocalDate ngayLap) {
		this.ngayLap = ngayLap;
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



	public String getPhuongThuc() {
		return phuongThuc;
	}



	public void setPhuongThuc(String phuongThuc) {
		this.phuongThuc = phuongThuc;
	}



	public boolean isDaThanhToan() {
		return daThanhToan;
	}



	public void setDaThanhToan(boolean daThanhToan) {
		this.daThanhToan = daThanhToan;
	}



	@Override
	public String toString() {
		return "HoaDon [maHoaDon=" + maHoaDon + ", khachHang=" + khachHang + ", nhanVien=" + nhanVien + ", phieuDatBan="
				+ phieuDatBan + ", khuyenMai=" + khuyenMai + ", thue=" + thue + ", ngayLap=" + ngayLap + ", gioVao="
				+ gioVao + ", gioRa=" + gioRa + ", phuongThuc=" + phuongThuc + ", daThanhToan=" + daThanhToan + "]";
	}
    
   
}