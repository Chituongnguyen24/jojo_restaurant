package entity;

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
        return tenKhuVuc;
    }

    @Override
    public int hashCode() {
        return maKhuVuc.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KhuVuc other = (KhuVuc) obj;
        return maKhuVuc.equals(other.maKhuVuc);
    }
}