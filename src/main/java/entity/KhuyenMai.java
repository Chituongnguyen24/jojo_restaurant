package entity;

import java.time.LocalDate;

public class KhuyenMai {
    private String maKM;
    private String tenChuongTrinh;
    private double giamGiaPhanTram;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String trangThai;

    // Constructor
    public KhuyenMai(String maKM, String tenChuongTrinh, double giamGiaPhanTram, LocalDate ngayBatDau, LocalDate ngayKetThuc, String trangThai) {
        this.maKM = maKM;
        this.tenChuongTrinh = tenChuongTrinh;
        this.giamGiaPhanTram = giamGiaPhanTram;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }
    

    public KhuyenMai() {
	}
    
	public KhuyenMai(String maKM) {
		super();
		this.maKM = maKM;
	}

	// Getters/Setters
    public String getMaKM() { return maKM; }
    public void setMaKM(String maKM) { this.maKM = maKM; }

    public String getTenChuongTrinh() { return tenChuongTrinh; }
    public void setTenChuongTrinh(String tenChuongTrinh) { this.tenChuongTrinh = tenChuongTrinh; }

    public double getGiamGiaPhanTram() { return giamGiaPhanTram; }
    public void setGiamGiaPhanTram(double giamGiaPhanTram) { this.giamGiaPhanTram = giamGiaPhanTram; }

    public LocalDate getNgayBatDau() { return ngayBatDau; }
    public void setNgayBatDau(LocalDate ngayBatDau) { this.ngayBatDau = ngayBatDau; }

    public LocalDate getNgayKetThuc() { return ngayKetThuc; }
    public void setNgayKetThuc(LocalDate ngayKetThuc) { this.ngayKetThuc = ngayKetThuc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
}