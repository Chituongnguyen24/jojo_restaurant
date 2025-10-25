package view.Ban;

import java.time.LocalDateTime;

//đối tượng để lưu trữ nhiệm vụ lập lịch trong bộ nhớ.

public class LichHenKiemTra {
    private final String maBan;
    private final LocalDateTime gioDen;
    private final Runnable hanhDongKhiHetHan; // Hành động sẽ chạy khi hết hạn

    public LichHenKiemTra(String maBan, LocalDateTime gioDen, Runnable hanhDongKhiHetHan) {
        this.maBan = maBan;
        this.gioDen = gioDen;
        this.hanhDongKhiHetHan = hanhDongKhiHetHan;
    }

    public String getMaBan() {
        return maBan;
    }

    public LocalDateTime getGioDen() {
        return gioDen;
    }

    public Runnable getHanhDongKhiHetHan() {
        return hanhDongKhiHetHan;
    }
}