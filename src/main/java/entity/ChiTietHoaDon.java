package entity;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private MonAn monAn;
    private double donGiaBan;
    private int soLuong;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(HoaDon hoaDon, MonAn monAn, double donGiaBan, int soLuong) {
        this.hoaDon = hoaDon;
        this.monAn = monAn;
        this.donGiaBan = donGiaBan;
        this.soLuong = soLuong;
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

    public double getDonGiaBan() {
        return donGiaBan;
    }

    public void setDonGiaBan(double donGiaBan) {
        this.donGiaBan = donGiaBan;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    @Override
    public String toString() {
        return "ChiTietHoaDon [maHD=" + hoaDon.getMaHD() + ", maMonAn=" + monAn.getMaMonAn() + ", soLuong=" + soLuong + "]";
    }
}