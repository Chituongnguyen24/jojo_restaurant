package entity;

public class NhanVien {
	private String maNV;
	private String tenNhanVien;
	private String chucVu;
	private boolean gioiTinh;
	private String sdt;
	private String email;
	public String getMaNV() {
		return maNV;
	}
	public void setMaNV(String maNV) {
		this.maNV = maNV;
	}
	public String getTenNhanVien() {
		return tenNhanVien;
	}
	public void setTenNhanVien(String tenNhanVien) {
		this.tenNhanVien = tenNhanVien;
	}
	public String getChucVu() {
		return chucVu;
	}
	public void setChucVu(String chucVu) {
		this.chucVu = chucVu;
	}
	public boolean isGioiTinh() {
		return gioiTinh;
	}
	public void setGioiTinh(boolean gioiTinh) {
		this.gioiTinh = gioiTinh;
	}
	public String getSdt() {
		return sdt;
	}
	public void setSdt(String sdt) {
		this.sdt = sdt;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	@Override
	public String toString() {
		return "NhanVien [maNV=" + maNV + ", tenNhanVien=" + tenNhanVien + ", chucVu=" + chucVu + ", gioiTinh="
				+ gioiTinh + ", sdt=" + sdt + ", email=" + email + "]";
	}
	public NhanVien(String maNV, String tenNhanVien, String chucVu, boolean gioiTinh, String sdt, String email) {
		this.maNV = maNV;
		this.tenNhanVien = tenNhanVien;
		this.chucVu = chucVu;
		this.gioiTinh = gioiTinh;
		this.sdt = sdt;
		this.email = email;
	}
	public NhanVien() {}
	
}
