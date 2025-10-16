package enums;

public enum TrangThaiBan {
    DA_DAT("Đã đặt"),
    CO_KHACH("Có khách"),
    TRONG("Trống");

    private final String value;

    TrangThaiBan(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    // Hàm quan trọng để chuyển đổi từ String trong DB sang Enum
    public static TrangThaiBan fromString(String text) {
        for (TrangThaiBan ttb : TrangThaiBan.values()) {
            if (ttb.value.equalsIgnoreCase(text)) {
                return ttb;
            }
        }
        // Báo lỗi nếu không tìm thấy chuỗi tương ứng
        throw new IllegalArgumentException("Không tìm thấy trạng thái bàn nào có tên: " + text);
    }
}