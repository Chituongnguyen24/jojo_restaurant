package entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // Thêm import

public class PhieuDatBan {
    private String maPhieu;
    private LocalDateTime thoiGianDat; // Đây là thời gian khách đến (theo CSDL)
    private KhachHang khachHang;
    private NhanVien nhanVien;
    private Ban ban;
    private int soNguoi; // <<< THÊM TRƯỜNG NÀY (NOT NULL)
    private double tienCoc;
    private String ghiChu; // <<< THÊM TRƯỜNG NÀY

    public PhieuDatBan() {
    }

    // Constructor đầy đủ mới
    public PhieuDatBan(String maPhieu, LocalDateTime thoiGianDat, KhachHang khachHang, NhanVien nhanVien, Ban ban,
            int soNguoi, double tienCoc, String ghiChu) {
        this.maPhieu = maPhieu;
        this.thoiGianDat = thoiGianDat;
        this.khachHang = khachHang;
        this.nhanVien = nhanVien;
        this.ban = ban;
        this.soNguoi = soNguoi; // <<< THÊM
        this.tienCoc = tienCoc;
        this.ghiChu = ghiChu; // <<< THÊM
    }
    
    // (Các constructor cũ của bạn có thể giữ lại hoặc xóa nếu không dùng)
    public PhieuDatBan(String maPhieu) {
		super();
		this.maPhieu = maPhieu;
	}
    // Getter/Setter (Giữ nguyên các cái cũ)
    
    public String getMaPhieu() {
        return maPhieu;
    }

    public void setMaPhieu(String maPhieu) {
        this.maPhieu = maPhieu;
    }

    public LocalDateTime getThoiGianDat() {
        return thoiGianDat;
    }

    public void setThoiGianDat(LocalDateTime thoiGianDat) {
        this.thoiGianDat = thoiGianDat;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public Ban getBan() {
        return ban;
    }

    public void setBan(Ban ban) {
        this.ban = ban;
    }

    public double getTienCoc() {
        return tienCoc;
    }

    public void setTienCoc(double tienCoc) {
        this.tienCoc = tienCoc;
    }

    // <<< THÊM GETTER/SETTER CHO CÁC TRƯỜNG MỚI >>>
    public int getSoNguoi() {
        return soNguoi;
    }

    public void setSoNguoi(int soNguoi) {
        this.soNguoi = soNguoi;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    // Sửa hàm này để hiển thị đúng thời gian khách đến
    public String getThoiGianDatFormatted() {
        if (this.thoiGianDat != null) {
            // Định dạng theo yêu cầu dd/MM/yyyy HH:mm
            return this.thoiGianDat.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
        return "N/A";
    }

    @Override
    public String toString() {
        return "PhieuDatBan [maPhieu=" + maPhieu + ", thoiGianDat=" + thoiGianDat + ", khachHang=" + khachHang
                + ", nhanVien=" + nhanVien + ", ban=" + ban + ", soNguoi=" + soNguoi + ", tienCoc=" + tienCoc
                + ", ghiChu=" + ghiChu + "]";
    }
}