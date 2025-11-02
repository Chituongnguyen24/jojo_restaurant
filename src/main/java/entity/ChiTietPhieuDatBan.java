package entity;

public class ChiTietPhieuDatBan {
    private MonAn monAn;
    private PhieuDatBan phieuDatBan;
    private int soLuongMonAn;
    private double donGiaBan;
    private String ghiChu;

    public ChiTietPhieuDatBan() {
    }

    public ChiTietPhieuDatBan(MonAn monAn, PhieuDatBan phieuDatBan, int soLuongMonAn, double donGiaBan, String ghiChu) {
        this.monAn = monAn;
        this.phieuDatBan = phieuDatBan;
        this.soLuongMonAn = soLuongMonAn;
        this.donGiaBan = donGiaBan;
        this.ghiChu = ghiChu;
    }

    public MonAn getMonAn() {
        return monAn;
    }

    public void setMonAn(MonAn monAn) {
        this.monAn = monAn;
    }

    public PhieuDatBan getPhieuDatBan() {
        return phieuDatBan;
    }

    public void setPhieuDatBan(PhieuDatBan phieuDatBan) {
        this.phieuDatBan = phieuDatBan;
    }

    public int getSoLuongMonAn() {
        return soLuongMonAn;
    }

    public void setSoLuongMonAn(int soLuongMonAn) {
        this.soLuongMonAn = soLuongMonAn;
    }

    public double getDonGiaBan() {
        return donGiaBan;
    }

    public void setDonGiaBan(double donGiaBan) {
        this.donGiaBan = donGiaBan;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public String toString() {
        return "ChiTietPhieuDatBan [maMonAn=" + monAn.getMaMonAn() + ", maPhieu=" + phieuDatBan.getMaPhieu() + ", soLuong=" + soLuongMonAn + "]";
    }
}