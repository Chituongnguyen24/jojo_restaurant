package dao;

import entity.KhuyenMai;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_KhuyenMai_DAO{
    private List<KhuyenMai> list = new ArrayList<>();

    public HoaDon_KhuyenMai_DAO() {
        // Mock data
        list.add(new KhuyenMai("KM001", "Giảm 20% cho món mới", 20.0, LocalDate.of(2025, 10, 1), LocalDate.of(2025, 10, 31), "Đang áp dụng"));
        list.add(new KhuyenMai("KM002", "Mua 1 tặng 1", 50.0, LocalDate.of(2025, 9, 15), LocalDate.of(2025, 9, 30), "Hết hạn"));
        list.add(new KhuyenMai("KM003", "Flash sale cuối tuần", 15.0, LocalDate.of(2025, 10, 11), LocalDate.of(2025, 10, 13), "Đang áp dụng"));
    }

    public List<KhuyenMai> getAll() {
        return new ArrayList<>(list);
    }

    public KhuyenMai getById(String maKM) {
        return list.stream().filter(km -> km.getMaKM().equals(maKM)).findFirst().orElse(null);
    }

    public void add(KhuyenMai km) {
        list.add(km);
    }

    public void update(KhuyenMai km) {
        list.stream().filter(k -> k.getMaKM().equals(km.getMaKM())).forEach(k -> {
            k.setTenChuongTrinh(km.getTenChuongTrinh());
            k.setGiamGiaPhanTram(km.getGiamGiaPhanTram());
            k.setNgayBatDau(km.getNgayBatDau());
            k.setNgayKetThuc(km.getNgayKetThuc());
            k.setTrangThai(km.getTrangThai());
        });
    }

    public void delete(String maKM) {
        list.removeIf(km -> km.getMaKM().equals(maKM));
    }
}