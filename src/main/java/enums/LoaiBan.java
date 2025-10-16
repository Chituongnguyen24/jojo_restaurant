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

    // Hàm map từ chuỗi DB hoặc người dùng sang enum
    public static LoaiBan fromTenHienThi(String ten) {
        if (ten == null) {
            throw new IllegalArgumentException("Tên LoaiBan không được null");
        }
        switch (ten.trim().toUpperCase()) {
            case "THUONG":
            case "BAN_THUONG":
            case "BÀN THƯỜNG":
                return THUONG;
            case "VIP":
            case "BAN_VIP":
                return VIP;
            case "SAN_THUONG":
            case "SÂN THƯỢNG":
                return SAN_THUONG;
            case "SAN_VUON":
            case "SÂN VƯỜN":
                return SAN_VUON;
            default:
                throw new IllegalArgumentException("Không tìm thấy LoaiBan cho: " + ten);
        }
    }
}
