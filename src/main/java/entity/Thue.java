package entity;

public class Thue {
    private String maSoThue;
    private String tenThue;
    private double tyLeThue;
    private String moTa;
    private boolean trangThai;

    public Thue() {
    }

    public Thue(String maSoThue) {
        this.maSoThue = maSoThue;
    }

    public Thue(String maSoThue, String tenThue, double tyLeThue, String moTa, boolean trangThai) {
        this.maSoThue = maSoThue;
        this.tenThue = tenThue;
        this.tyLeThue = tyLeThue;
        this.moTa = moTa;
        this.trangThai = trangThai;
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
        return tenThue + " (" + tyLeThue + "%)";
    }

    @Override
    public int hashCode() {
        return maSoThue.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Thue other = (Thue) obj;
        return maSoThue.equals(other.maSoThue);
    }
}