package entity;

import java.util.Objects;

public class Thue {
    private String maThue;
    private String tenThue;
    private double tyLeThue;
    private String moTa;
    private boolean trangThai;

    public Thue() {
	}

    public Thue(String maThue) {
		this.maThue = maThue;
	}

	public Thue(String maThue, String tenThue, double tyLeThue, String moTa, boolean trangThai) {
        this.maThue = maThue;
        this.tenThue = tenThue;
        this.tyLeThue = tyLeThue;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    

    public String getMaThue() {
		return maThue;
	}

	public void setMaThue(String maThue) {
		this.maThue = maThue;
	}

	public String getTenThue() {
		return tenThue;
	}

	public void setTenThue(String tenThue) {
		this.tenThue = tenThue;
	}

	public double getTyLeThue() {
		return tyLeThue;
	}

	public void setTyLeThue(double tyLeThue) {
		this.tyLeThue = tyLeThue;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
    public String toString() {
        String cleanMaThue = maThue != null ? maThue.trim() : "";
        String cleanTenThue = tenThue != null ? tenThue : "";
        return String.format("%s - %s (%.1f%%)", cleanMaThue, cleanTenThue, tyLeThue * 100);
    }

     @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Thue thue = (Thue) o;
        return Objects.equals(maThue, thue.maThue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maThue);
    }
}