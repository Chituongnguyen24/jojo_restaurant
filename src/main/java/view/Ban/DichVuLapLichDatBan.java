package view.Ban; // Đã đổi package

import dao.Ban_DAO;
import entity.Ban;
import enums.TrangThaiBan;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

/**
 * Dịch vụ Singleton chạy ngầm để quản lý các bàn đã đặt.
 * Sẽ tự động trả bàn về "Trống" nếu quá 30 phút mà khách không đến.
 */
public class DichVuLapLichDatBan {
    private static DichVuLapLichDatBan instance;
    private Ban_DAO banDAO;
    private final ScheduledExecutorService boLapLich; 
    private final List<LichHenKiemTra> danhSachLichHen; 

    private DichVuLapLichDatBan() {
        this.boLapLich = Executors.newSingleThreadScheduledExecutor();
        this.danhSachLichHen = new CopyOnWriteArrayList<>();
    }

    public static synchronized DichVuLapLichDatBan getInstance() {
        if (instance == null) {
            instance = new DichVuLapLichDatBan();
        }
        return instance;
    }

    public void start(Ban_DAO banDAO) {
        this.banDAO = banDAO;
        boLapLich.scheduleAtFixedRate(this::kiemTraCacLichHen, 1, 1, TimeUnit.MINUTES);
        System.out.println("Dịch vụ Lập lịch Đặt bàn đã khởi động.");
    }

    private void kiemTraCacLichHen() {
        if (banDAO == null) return; 

        LocalDateTime now = LocalDateTime.now();
        
        for (LichHenKiemTra lichHen : danhSachLichHen) {
            if (now.isAfter(lichHen.getGioDen().plusMinutes(30))) {
                System.out.println("Phát hiện lịch hẹn hết hạn cho bàn: " + lichHen.getMaBan());
                
                Ban ban = banDAO.getBanById(lichHen.getMaBan());

                if (ban != null && ban.getTrangThai() == TrangThaiBan.DA_DAT) {
                    ban.setTrangThai(TrangThaiBan.TRONG);
                    boolean success = banDAO.capNhatBan(ban); 

                    if (success) {
                        System.out.println("Đã tự động trả bàn: " + lichHen.getMaBan());
                        SwingUtilities.invokeLater(lichHen.getHanhDongKhiHetHan());
                    }
                }
                
                danhSachLichHen.remove(lichHen);
            }
        }
    }

    public void themLichHenKiemTra(Ban ban, LocalDateTime gioDen, Runnable hanhDongKhiHetHan) {
        huyLichHen(ban.getMaBan()); 
        
        LichHenKiemTra lichHen = new LichHenKiemTra(ban.getMaBan(), gioDen, hanhDongKhiHetHan);
        danhSachLichHen.add(lichHen);
        System.out.println("Đã thêm lịch hẹn cho bàn: " + ban.getMaBan() + " lúc " + gioDen);
    }

    public void huyLichHen(String maBan) {
        danhSachLichHen.removeIf(lichHen -> lichHen.getMaBan().equals(maBan));
        System.out.println("Đã hủy lịch hẹn (nếu có) cho bàn: " + maBan);
    }
}