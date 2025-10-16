package entity;

import java.time.LocalDateTime;

public class PhieuDatBan {
    private String maPhieu;
    private LocalDateTime thoiGianDat;
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private Ban ban;
    private double tienCoc;
    
	public PhieuDatBan() {
	}
	
	public PhieuDatBan(String maPhieu, LocalDateTime thoiGianDat, KhachHang khachHang, NhanVien nhanVien, Ban ban,
			double tienCoc) {
		this.maPhieu = maPhieu;
		this.thoiGianDat = thoiGianDat;
		this.khachHang = khachHang;
		this.nhanVien = nhanVien;
		this.ban = ban;
		this.tienCoc = tienCoc;
	}
	
	public PhieuDatBan(String maPhieu, KhachHang khachHang, NhanVien nhanVien) {
		super();
		this.maPhieu = maPhieu;
		this.khachHang = khachHang;
		this.nhanVien = nhanVien;
	}
	
	public PhieuDatBan(String maPhieu) {
			this.maPhieu = maPhieu;
	}
	public String getMaPhieu() {
		return maPhieu;
	}
	public void setMaPhieu(String maPhieu) {
		this.maPhieu = maPhieu;
	}
	public LocalDateTime getThoiGianDat() {
		return thoiGianDat;
	}
	public void setThoiGianDat(LocalDateTime thoiGianDat) {
		this.thoiGianDat = thoiGianDat;
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
	public double getTienCoc() {
		return tienCoc;
	}
	public void setTienCoc(double tienCoc) {
		this.tienCoc = tienCoc;
	}
	@Override
	public String toString() {
		return "PhieuDatBan [maPhieu=" + maPhieu + ", thoiGianDat=" + thoiGianDat + ", khachHang=" + khachHang
				+ ", nhanVien=" + nhanVien + ", ban=" + ban + ", tienCoc=" + tienCoc + "]";
	}
	
   
}
