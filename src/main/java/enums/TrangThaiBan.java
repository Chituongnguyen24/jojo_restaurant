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


    public static TrangThaiBan fromString(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Trạng thái bàn không được null");
        }
        String trimmedText = text.trim(); 


        for (TrangThaiBan ttb : TrangThaiBan.values()) {
            // 1. Kiểm tra khớp với giá trị hiển thị (có dấu, ví dụ: "Trống") - không phân biệt hoa/thường
            if (ttb.value.equalsIgnoreCase(trimmedText)) {
                return ttb;
            }
            // 2. Kiểm tra khớp với tên hằng số enum (không dấu, ví dụ: "TRONG") - không phân biệt hoa/thường
            if (ttb.name().equalsIgnoreCase(trimmedText)) {  
                return ttb;
            }
        }
    
        throw new IllegalArgumentException("Không tìm thấy trạng thái bàn nào có tên phù hợp: '" + text + "'");
    }

    public Color getColor() {
        return this.color;
    }

}