package dao;

import entity.HoaDon;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {
    private List<HoaDon> list = new ArrayList<>();

    public HoaDon_DAO() {
        // Mock data
        list.add(new HoaDon("HD001", "Nguyễn Văn A", LocalDate.of(2025, 10, 1), 1500000, "Tiền mặt", "Đã thanh toán"));
        list.add(new HoaDon("HD002", "Trần Thị B", LocalDate.of(2025, 10, 2), 2300000, "Thẻ tín dụng", "Chưa thanh toán"));
        list.add(new HoaDon("HD003", "Lê Hoàng C", LocalDate.of(2025, 10, 3), 950000, "Chuyển khoản", "Đã thanh toán"));
    }

    public List<HoaDon> getAll() {
        return new ArrayList<>(list);  // Return copy
    }

    public HoaDon getById(String maHD) {
        return list.stream().filter(hd -> hd.getMaHD().equals(maHD)).findFirst().orElse(null);
    }

    public void add(HoaDon hd) {
        list.add(hd);
    }

    public void update(HoaDon hd) {
        list.stream().filter(h -> h.getMaHD().equals(hd.getMaHD())).forEach(h -> {
            h.setKhachHang(hd.getKhachHang());
            h.setNgayLap(hd.getNgayLap());
            h.setTongTien(hd.getTongTien());
            h.setPhuongThuc(hd.getPhuongThuc());
            h.setTrangThai(hd.getTrangThai());
        });
    }

    public void delete(String maHD) {
        list.removeIf(hd -> hd.getMaHD().equals(maHD));
    }
}