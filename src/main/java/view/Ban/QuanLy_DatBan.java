package view.Ban;

import java.time.*;
import java.util.*;
import javax.swing.SwingUtilities;

public class QuanLy_DatBan {

    // Lập lịch thay đổi trạng thái bàn theo thời gian
    public static void scheduleStatusUpdate(Ban_View.TableInfo table, LocalDateTime gioDen) {
        new Thread(() -> {
            try {
                // Tính thời gian trước 1 tiếng
                long millisTruoc1Gio = Duration.between(LocalDateTime.now(), gioDen.minusHours(1)).toMillis();
                if (millisTruoc1Gio > 0)
                    Thread.sleep(millisTruoc1Gio);

                // === 1 giờ trước: chuyển sang "ĐÃ ĐẶT TRƯỚC" ===
                table.status = Ban_View.TableStatus.DA_DUOC_DAT;
                capNhatMauBan(table);

                // Tính thời gian đến sau 30 phút
                long millisSau30p = Duration.between(LocalDateTime.now(), gioDen.plusMinutes(30)).toMillis();
                if (millisSau30p > 0)
                    Thread.sleep(millisSau30p);

                // === Sau 30 phút kể từ giờ đến: nếu chưa có khách thì thành "TRỐNG" ===
                if (table.status != Ban_View.TableStatus.DA_CO_KHACH) {
                    table.status = Ban_View.TableStatus.TRONG;
                    capNhatMauBan(table);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Hàm cập nhật giao diện bàn (đổi màu JPanel)
    private static void capNhatMauBan(Ban_View.TableInfo table) {
        // Đổi màu theo trạng thái
        SwingUtilities.invokeLater(() -> {
            switch (table.status) {
                case TRONG:
                    table.panel.setBackground(new java.awt.Color(144, 238, 144)); // xanh lá nhạt
                    break;
                case DA_DUOC_DAT:
                    table.panel.setBackground(new java.awt.Color(255, 215, 0)); // vàng
                    break;
                case DA_CO_KHACH:
                    table.panel.setBackground(new java.awt.Color(255, 99, 71)); // đỏ cam
                    break;
            }
            table.panel.revalidate();
            table.panel.repaint();
        });
    }
}
