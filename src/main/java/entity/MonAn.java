package entity;

public class MonAn {
    private String maMonAn;
    private String tenMonAn;
    private double donGia;
    private boolean trangThai;
    private String imagePath;
    private String loaiMonAn;

    public MonAn() {
    }

    public MonAn(String maMonAn) {
        this.maMonAn = maMonAn;
    }

    public MonAn(String maMonAn, String tenMonAn, double donGia, boolean trangThai, String imagePath, String loaiMonAn) {
        this.maMonAn = maMonAn;
        this.tenMonAn = tenMonAn;
        this.donGia = donGia;
        this.trangThai = trangThai;
        this.imagePath = imagePath;
        this.loaiMonAn = loaiMonAn;
    }

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

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getLoaiMonAn() {
        return loaiMonAn;
    }

    public void setLoaiMonAn(String loaiMonAn) {
        this.loaiMonAn = loaiMonAn;
    }

    @Override
    public String toString() {
        return tenMonAn;
    }

    @Override
    public int hashCode() {
        return maMonAn.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        MonAn other = (MonAn) obj;
        return maMonAn.equals(other.maMonAn);
    }
}