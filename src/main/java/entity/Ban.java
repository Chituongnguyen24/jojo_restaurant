package entity;

import enums.LoaiBan;
import enums.TrangThaiBan;

public class Ban {
	private String maBan;
	private int soCho;
	private LoaiBan loaiBan;
	private String maKhuVuc;
	private TrangThaiBan trangThai;
	public String getMaBan() {
		return maBan;
	}
	public void setMaBan(String maBan) {
		this.maBan = maBan;
	}
	public int getSoCho() {
		return soCho;
	}
	public void setSoCho(int soCho) {
		this.soCho = soCho;
	}
	public LoaiBan getLoaiBan() {
		return loaiBan;
	}
	public void setLoaiBan(LoaiBan loaiBan) {
		this.loaiBan = loaiBan;
	}
	public String getMaKhuVuc() {
		return maKhuVuc;
	}
	public void setMaKhuVuc(String maKhuVuc) {
		this.maKhuVuc = maKhuVuc;
	}
	public TrangThaiBan getTrangThai() {
		return trangThai;
	}
	public void setTrangThai(TrangThaiBan trangThai) {
		this.trangThai = trangThai;
	}
	@Override
	public String toString() {
		return "Ban [maBan=" + maBan + ", soCho=" + soCho + ", loaiBan=" + loaiBan + ", maKhuVuc=" + maKhuVuc
				+ ", trangThai=" + trangThai + "]";
	}
	public Ban() {
		super();
	}
	public Ban(String maBan, int soCho, LoaiBan loaiBan, String maKhuVuc, TrangThaiBan trangThai) {
		super();
		this.maBan = maBan;
		this.soCho = soCho;
		this.loaiBan = loaiBan;
		this.maKhuVuc = maKhuVuc;
		this.trangThai = trangThai;
	}
	
	
}
