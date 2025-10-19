package enums;

import java.awt.Color;

public enum TrangThaiBan {
    DA_DAT("Đã đặt", new Color(251, 191, 36)),
    CO_KHACH("Có khách", new Color(239, 68, 68)),
    TRONG("Trống", new Color(34, 197, 94));

    private final String value;
    private final Color color;

    TrangThaiBan(String value, Color color) {
        this.value = value;
        this.color = color;
    }

    @Override
    public String toString() {
        return this.value;
    }

    // Hàm quan trọng để chuyển đổi từ String trong DB sang Enum
    public static TrangThaiBan fromString(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Trạng thái bàn không được null");
        }
        for (TrangThaiBan ttb : TrangThaiBan.values()) {
            if (ttb.value.equalsIgnoreCase(text.trim())) {
                return ttb;
            }
        }
        // Báo lỗi nếu không tìm thấy chuỗi tương ứng
        throw new IllegalArgumentException("Không tìm thấy trạng thái bàn nào có tên: " + text);
    }

    public Color getColor() {
        return this.color;
    }

}