package entity;

public class MonAn {
	private String maMonAn;
	private String tenMonAn;
	private double donGia;
	private boolean trangThai;
	
	// === THÊM TRƯỜNG NÀY ===
	private String imagePath; 
	// =======================

	// Constructors
	public MonAn() {
		super();
	}

	public MonAn(String maMonAn) {
		super();
		this.maMonAn = maMonAn;
	}
	
	// === CẬP NHẬT CONSTRUCTOR ĐẦY ĐỦ ===
	public MonAn(String maMonAn, String tenMonAn, double donGia, boolean trangThai, String imagePath) {
		super();
		this.maMonAn = maMonAn;
		this.tenMonAn = tenMonAn;
		this.donGia = donGia;
		this.trangThai = trangThai;
		this.imagePath = imagePath; // Thêm vào
	}

	// Getters and Setters
	public String getMaMonAn() {
		return maMonAn;
	}

	public void setMaMonAn(String maMonAn) {
		this.maMonAn = maMonAn;
	}

	public String getTenMonAn() {
		return tenMonAn;
	}

	public void setTenMonAn(String tenMonAn) {
		this.tenMonAn = tenMonAn;
	}

	public double getDonGia() {
		return donGia;
	}

	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}
	
	// === THÊM GETTER VÀ SETTER CHO imagePath ===
	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	// ======================================

	@Override
	public String toString() {
		return "MonAn [maMonAn=" + maMonAn + ", tenMonAn=" + tenMonAn + ", donGia=" + donGia + ", trangThai=" + trangThai
				+ ", imagePath=" + imagePath + "]"; // Thêm imagePath
	}
}