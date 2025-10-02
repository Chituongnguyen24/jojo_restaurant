package enums;

public enum TrangThaiBan {
    TRONG("Trống"),
    DANG_SU_DUNG("Đang sử dụng"),
    DA_DAT_TRUOC("Đã đặt trước");

    private final String tenHienThi;

    TrangThaiBan(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
