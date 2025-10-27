package entity;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private MonAn monAn;
    private int soLuong;
    private double donGia;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(HoaDon hoaDon, MonAn monAn, int soLuong, double donGia) {
        this.hoaDon = hoaDon;
        this.monAn = monAn;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    

    public HoaDon getHoaDon() {
		return hoaDon;
	}

	public void setHoaDon(HoaDon hoaDon) {
		this.hoaDon = hoaDon;
	}

	public MonAn getMonAn() {
		return monAn;
	}

	public void setMonAn(MonAn monAn) {
		this.monAn = monAn;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public double getDonGia() {
		return donGia;
	}

	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}

	@Override
    public String toString() {
        return "ChiTietHoaDon{" +
               "hoaDon=" + (hoaDon != null ? hoaDon.getMaHoaDon() : "null") +
               ", monAn=" + (monAn != null ? monAn.getTenMonAn() : "null") +
               ", soLuong=" + soLuong +
               ", donGia=" + donGia +
               '}';
    }

	public Object tinhThanhTien() {
		// TODO Auto-generated method stub
		return null;
	}
}