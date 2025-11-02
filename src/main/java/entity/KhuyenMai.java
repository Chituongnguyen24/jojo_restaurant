package entity;

import java.time.LocalDate;

public class KhuyenMai {
    private String maKM;
    private String moTa;
    private LocalDate ngayApDung;
    private LocalDate ngayHetHan;
    private double mucKM;
    private Boolean trangThaiKM;
    private String loaiKM;

    public KhuyenMai() {
    }

    public KhuyenMai(String maKM) {
        this.maKM = maKM;
    }

    public KhuyenMai(String maKM, String moTa, LocalDate ngayApDung, LocalDate ngayHetHan, double mucKM, Boolean trangThaiKM, String loaiKM) {
        this.maKM = maKM;
        this.moTa = moTa;
        this.ngayApDung = ngayApDung;
        this.ngayHetHan = ngayHetHan;
        this.mucKM = mucKM;
        this.trangThaiKM = trangThaiKM;
        this.loaiKM = loaiKM;
    }

    public String getMaKM() {
        return maKM;
    }

    public void setMaKM(String maKM) {
        this.maKM = maKM;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public LocalDate getNgayApDung() {
        return ngayApDung;
    }

    public void setNgayApDung(LocalDate ngayApDung) {
        this.ngayApDung = ngayApDung;
    }

    public LocalDate getNgayHetHan() {
        return ngayHetHan;
    }

    public void setNgayHetHan(LocalDate ngayHetHan) {
        this.ngayHetHan = ngayHetHan;
    }

    public double getMucKM() {
        return mucKM;
    }

    public void setMucKM(double mucKM) {
        this.mucKM = mucKM;
    }

    public Boolean getTrangThaiKM() {
        return trangThaiKM;
    }

    public void setTrangThaiKM(Boolean trangThaiKM) {
        this.trangThaiKM = trangThaiKM;
    }

    public String getLoaiKM() {
        return loaiKM;
    }

    public void setLoaiKM(String loaiKM) {
        this.loaiKM = loaiKM;
    }

    @Override
    public String toString() {
        return moTa + " (" + maKM + ")";
    }

    @Override
    public int hashCode() {
        return maKM.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        KhuyenMai other = (KhuyenMai) obj;
        return maKM.equals(other.maKM);
    }
}