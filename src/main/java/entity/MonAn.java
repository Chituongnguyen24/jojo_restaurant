package entity;

public class MonAn {
    private String maMonAn;
    private String tenMonAn;
    private double donGia;
    private boolean trangThai;

    public MonAn(String maMonAn, String tenMonAn, double donGia, boolean trangThai) {
        this.maMonAn = maMonAn;
        this.tenMonAn = tenMonAn;
        this.donGia = donGia;
        this.trangThai = trangThai;
    }
    
    public MonAn() {}

    public String getMaMonAn() { 
    	return maMonAn; 
    }
    public String getTenMonAn() { 
    	return tenMonAn; 
    }
    public double getDonGia() { 
    	return donGia; 
    }
    public boolean isTrangThai() { 
    	return trangThai; 
    }

    public void setTenMonAn(String tenMonAn) { 
    	this.tenMonAn = tenMonAn; 
    }
    public void setDonGia(double donGia) { 
    	this.donGia = donGia; 
    }
    public void setTrangThai(boolean trangThai) { 
    	this.trangThai = trangThai; 
    }

	@Override
	public String toString() {
		return "MonAn [maMonAn=" + maMonAn + ", tenMonAn=" + tenMonAn + ", donGia=" + donGia + ", trangThai="
				+ trangThai + "]";
	}
    
}
