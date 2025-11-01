package enums;

import java.awt.Color;

public enum TrangThaiBan {
    DA_DAT("Đã đặt", new Color(251, 191, 36)),
    CO_KHACH("Có khách", new Color(239, 68, 68)),
    TRONG("Trống", new Color(34, 197, 94));

    private final Color color;
	private String tenHienThi;

    TrangThaiBan(String tenHienThi, Color color) {
        this.tenHienThi = tenHienThi;
        this.color = color;
    }
    
    public String getTenHienThi() {
        return this.tenHienThi;
    }

    @Override
    public String toString() {
        return this.tenHienThi;
    }


    public static TrangThaiBan fromString(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Trạng thái bàn không được null");
        }
        String trimmedText = text.trim(); 


        for (TrangThaiBan ttb : TrangThaiBan.values()) {
           if (ttb.tenHienThi.equalsIgnoreCase(trimmedText)) {
                return ttb;
            }
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