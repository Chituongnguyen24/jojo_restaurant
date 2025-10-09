package entity;

public class Thue {
	private String maSoThue;
	private String tenThue;
	private double tiLeThue;
	public Thue() {
	}
	public Thue(String maSoThue, String tenThue, double tiLeThue) {
		this.maSoThue = maSoThue;
		this.tenThue = tenThue;
		this.tiLeThue = tiLeThue;
	}
	public String getMaSoThue() {
		return maSoThue;
	}
	public void setMaSoThue(String maSoThue) {
		this.maSoThue = maSoThue;
	}
	public String getTenThue() {
		return tenThue;
	}
	public void setTenThue(String tenThue) {
		this.tenThue = tenThue;
	}
	public double getTiLeThue() {
		return tiLeThue;
	}
	public void setTiLeThue(double tiLeThue) {
		this.tiLeThue = tiLeThue;
	}
	@Override
	public String toString() {
		return "Thue [maSoThue=" + maSoThue + ", tenThue=" + tenThue + ", tiLeThue=" + tiLeThue + "]";
	}
	
}
