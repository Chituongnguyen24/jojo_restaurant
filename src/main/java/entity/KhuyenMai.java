package entity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class KhuyenMai {
    private String maKhuyenMai;       
    private float giaTri;          
    private String tenKhuyenMai;    
    private String dieuKienApDung;    
    private boolean trangThai;      


    public KhuyenMai() {
    }

    public KhuyenMai(String maKhuyenMai, float giaTri, String tenKhuyenMai, String dieuKienApDung, boolean trangThai) {
        setMaKhuyenMai(maKhuyenMai);
        setGiaTri(giaTri);
        setTenKhuyenMai(tenKhuyenMai);
        setDieuKienApDung(dieuKienApDung);
        setTrangThai(trangThai);
    }

    public String getMaKhuyenMai() {
        return maKhuyenMai;
    }

    public void setMaKhuyenMai(String maKhuyenMai) {
        if (maKhuyenMai == null || maKhuyenMai.isEmpty()) {
            String prefix = new SimpleDateFormat("yyyyMM").format(new Date());
            int randomNum = new Random().nextInt(9000) + 1000; 
            this.maKhuyenMai = prefix + "-" + randomNum;
        } else {
            if (!maKhuyenMai.matches("\\d{6}-\\d{4}")) {
                throw new IllegalArgumentException("Mã khuyến mãi phải có định dạng YYYYMM-XXXX");
            }
            this.maKhuyenMai = maKhuyenMai;
        }
    }

    public float getGiaTri() {
        return giaTri;
    }

    public void setGiaTri(float giaTri) {
        if (giaTri <= 0 || giaTri >= 100) {
            throw new IllegalArgumentException("Giá trị khuyến mãi phải trong khoảng 1% đến 99%");
        }
        this.giaTri = giaTri;
    }

    public String getTenKhuyenMai() {
        return tenKhuyenMai;
    }

    public void setTenKhuyenMai(String tenKhuyenMai) {
        if (tenKhuyenMai == null || tenKhuyenMai.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên khuyến mãi không được để trống");
        }
        this.tenKhuyenMai = tenKhuyenMai.trim();
    }

    public String getDieuKienApDung() {
        return dieuKienApDung;
    }

    public void setDieuKienApDung(String dieuKienApDung) {
        if (dieuKienApDung == null || dieuKienApDung.trim().isEmpty()) {
            throw new IllegalArgumentException("Điều kiện áp dụng không được để trống");
        }
        this.dieuKienApDung = dieuKienApDung.trim();
    }

    public boolean getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }
    @Override
    public String toString() {
        return "KhuyenMai {" +
                "maKhuyenMai='" + maKhuyenMai + '\'' +
                ", giaTri=" + giaTri + "%" +
                ", tenKhuyenMai='" + tenKhuyenMai + '\'' +
                ", dieuKienApDung='" + dieuKienApDung + '\'' +
                ", trangThai=" + (trangThai ? "Còn hiệu lực" : "Hết hiệu lực") +
                '}';
    }
}
