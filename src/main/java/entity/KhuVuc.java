package entity;

import java.util.Objects;

public class KhuVuc {
    private String maKhuVuc;
    private String tenKhuVuc;
    private String moTa;
    private boolean trangThai;

    public KhuVuc() {
    }

    public KhuVuc(String maKhuVuc) {
        this.maKhuVuc = maKhuVuc;
    }

    public KhuVuc(String maKhuVuc, String tenKhuVuc, String moTa, boolean trangThai) {
        this.maKhuVuc = maKhuVuc;
        this.tenKhuVuc = tenKhuVuc;
        this.moTa = moTa;
        this.trangThai = trangThai;
    }

    public String getMaKhuVuc() {
        return maKhuVuc;
    }

    public void setMaKhuVuc(String maKhuVuc) {
        this.maKhuVuc = maKhuVuc;
    }

    public String getTenKhuVuc() {
        return tenKhuVuc;
    }

    public void setTenKhuVuc(String tenKhuVuc) {
        this.tenKhuVuc = tenKhuVuc;
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
        return tenKhuVuc != null ? tenKhuVuc : (maKhuVuc != null ? maKhuVuc.trim() : "N/A");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KhuVuc khuVuc = (KhuVuc) o;
        return Objects.equals(maKhuVuc, khuVuc.maKhuVuc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKhuVuc);
    }
}