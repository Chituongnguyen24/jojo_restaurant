package enums;


public enum LoaiBan {
    THUONG("Thường"),
    VIP("VIP"),
    SAN_THUONG("Sân thượng"),
    SAN_VUON("Sân vườn");

    private final String tenHienThi;

    LoaiBan(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }
}
