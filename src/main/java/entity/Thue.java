package entity;

public class Thue {
    private String maThue;
    private String tenThue;
    private double phanTram;
    private String moTa;
    private String trangThai;

    // Constructor
    public Thue() {
	}
    
    public Thue(String maThue) {
		super();
		this.maThue = maThue;
	}

	public Thue(String maThue, String tenThue, double phanTram, String moTa, String trangThai) {
        this.maThue = maThue;
        this.tenThue = tenThue;
        this.phanTram = phanTram;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    // Getters/Setters
    public String getMaThue() { return maThue; }
    public void setMaThue(String maThue) { this.maThue = maThue; }

    public String getTenThue() { return tenThue; }
    public void setTenThue(String tenThue) { this.tenThue = tenThue; }

    public double getPhanTram() { return phanTram; }
    public void setPhanTram(double phanTram) { this.phanTram = phanTram; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}