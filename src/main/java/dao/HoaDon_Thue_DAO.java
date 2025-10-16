package dao;

import entity.Thue;

import java.util.ArrayList;
import java.util.List;

public class HoaDon_Thue_DAO {
	private List<Thue> list = new ArrayList<>();

    public HoaDon_Thue_DAO(){
        // Mock data
        list.add(new Thue("T001", "VAT", 10.0, "Thuế giá trị gia tăng", "Đang áp dụng"));
        list.add(new Thue("T002", "Phí dịch vụ", 5.0, "Phí dịch vụ nhà hàng", "Đang áp dụng"));
        list.add(new Thue("T003", "Thuế GTGT đặc biệt", 8.0, "Cho món ăn đặc biệt", "Tạm ngưng"));
    }

    public List<Thue> getAll() {
        return new ArrayList<>(list);
    }

    public Thue getById(String maThue) {
        return list.stream().filter(t -> t.getMaThue().equals(maThue)).findFirst().orElse(null);
    }

    public void add(Thue thue) {
        list.add(thue);
    }

    public void update(Thue thue) {
        list.stream().filter(t -> t.getMaThue().equals(thue.getMaThue())).forEach(t -> {
            t.setTenThue(thue.getTenThue());
            t.setPhanTram(thue.getPhanTram());
            t.setMoTa(thue.getMoTa());
            t.setTrangThai(thue.getTrangThai());
        });
    }

    public void delete(String maThue) {
        list.removeIf(t -> t.getMaThue().equals(maThue));
    }
}