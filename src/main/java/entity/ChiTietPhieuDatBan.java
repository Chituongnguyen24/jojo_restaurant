package entity;

public class ChiTietPhieuDatBan {
    private PhieuDatBan phieuDatBan;
    private MonAn monAn;
    private int soLuongMonAn;
    private double donGia;
    private String ghiChu;
	public ChiTietPhieuDatBan() {
	}
	public ChiTietPhieuDatBan(PhieuDatBan phieuDatBan, MonAn monAn, int soLuongMonAn, double donGia, String ghiChu) {
		super();
		this.phieuDatBan = phieuDatBan;
		this.monAn = monAn;
		this.soLuongMonAn = soLuongMonAn;
		this.donGia = donGia;
		this.ghiChu = ghiChu;
	}
	public PhieuDatBan getPhieuDatBan() {
		return phieuDatBan;
	}
	public void setPhieuDatBan(PhieuDatBan phieuDatBan) {
		this.phieuDatBan = phieuDatBan;
	}
	public MonAn getMonAn() {
		return monAn;
	}
	public void setMonAn(MonAn monAn) {
		this.monAn = monAn;
	}
	public int getSoLuongMonAn() {
		return soLuongMonAn;
	}
	public void setSoLuongMonAn(int soLuongMonAn) {
		this.soLuongMonAn = soLuongMonAn;
	}
	public double getDonGia() {
		return donGia;
	}
	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}
	public String getGhiChu() {
		return ghiChu;
	}
	public void setGhiChu(String ghiChu) {
		this.ghiChu = ghiChu;
	}
	@Override
	public String toString() {
		return "ChiTietPhieuDatBan [phieuDatBan=" + phieuDatBan + ", monAn=" + monAn + ", soLuongMonAn=" + soLuongMonAn
				+ ", donGia=" + donGia + ", ghiChu=" + ghiChu + "]";
	}
	public double tinhTongTien() {
	    return soLuongMonAn * donGia;
	}

    
}
