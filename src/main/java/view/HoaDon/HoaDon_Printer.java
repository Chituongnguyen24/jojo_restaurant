package view.HoaDon;

import dao.*;
import entity.*;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HoaDon_Printer {

    private static final DecimalFormat moneyFormatter = new DecimalFormat("###,###"); // Bỏ VNĐ để căn lề dễ hơn
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    // === DAO cần thiết ===
    private static NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
    private static Ban_DAO banDAO = new Ban_DAO();
    private static HoaDon_Thue_DAO thueDAO = new HoaDon_Thue_DAO();
    private static HoaDon_KhuyenMai_DAO khuyenMaiDAO = new HoaDon_KhuyenMai_DAO();

    public static String generateInvoiceText(HoaDon hoaDon, List<ChiTietHoaDon> chiTietList) {
        StringBuilder sb = new StringBuilder();

        // Lấy thông tin chi tiết
        NhanVien nv = nhanVienDAO.getNhanVienById(hoaDon.getNhanVien().getMaNV());
        Ban ban = banDAO.getBanTheoMa(hoaDon.getBan().getMaBan());
        Thue thue = thueDAO.getThueById(hoaDon.getThue().getMaThue());
        KhuyenMai km = null;
        if (hoaDon.getKhuyenMai() != null && !hoaDon.getKhuyenMai().getMaKM().equals("KM00000000")) {
             km = khuyenMaiDAO.getKhuyenMaiById(hoaDon.getKhuyenMai().getMaKM());
        }
        KhachHang kh = hoaDon.getKhachHang(); // Đã lấy đầy đủ từ Dialog

        // Header
        sb.append("        *** JOJO RESTAURANT ***\n");
        sb.append("   12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TPHCM\n");
        sb.append("          ĐT: 0123.456.789\n");
        sb.append("------------------------------------------\n");
        sb.append("           PHIẾU TÍNH TIỀN\n");
        sb.append("------------------------------------------\n");

        // Thông tin hóa đơn
        sb.append("Số HD: ").append(hoaDon.getMaHoaDon().trim()).append("\n");
        sb.append("Ngày: ").append(hoaDon.getNgayLap().format(dateFormatter));
        sb.append(" Giờ vào: ").append(hoaDon.getGioVao().format(timeFormatter)).append("\n");
        sb.append("Giờ ra: ").append(hoaDon.getGioRa().format(timeFormatter)).append("\n");
        if (ban != null) sb.append("Bàn: ").append(ban.getMaBan().trim()).append("\n");
        if (nv != null) sb.append("Thu ngân: ").append(nv.getTenNhanVien()).append("\n");

        // Thông tin khách hàng
        if (kh != null && !"KH00000000".equals(kh.getMaKhachHang().trim())) { // Kiểm tra mã khách lẻ
            sb.append("Khách hàng: ").append(kh.getTenKhachHang()).append("\n");
            if (kh.getSdt() != null && !kh.getSdt().trim().isEmpty() && !"0000000000".equals(kh.getSdt().trim())) {
                 sb.append("SĐT KH: ").append(kh.getSdt()).append("\n");
            }
        } else {
            sb.append("Khách hàng: Khách lẻ\n");
             if (hoaDon.getPhieuDatBan() != null) {
                 sb.append("Phiếu đặt: ").append(hoaDon.getPhieuDatBan().getMaPhieu().trim()).append("\n");
             }
        }
        sb.append("------------------------------------------\n");

        // Chi tiết món ăn
        sb.append(String.format("%-20s%5s%13s\n", "Tên Món", "SL", "Thành Tiền"));
        sb.append("------------------------------------------\n");
        double tongTienMonAn = 0;
        for (ChiTietHoaDon ct : chiTietList) {
            double thanhTien = ct.getSoLuong() * ct.getDonGia();
            sb.append(String.format("%-20s%5d%13s\n",
                    truncate(ct.getMonAn().getTenMonAn(), 19),
                    ct.getSoLuong(),
                    moneyFormatter.format(thanhTien)));
            tongTienMonAn += thanhTien;
        }
        sb.append("------------------------------------------\n");

        // Tổng kết
        sb.append(String.format("%-25s%13s\n", "Tổng cộng:", moneyFormatter.format(tongTienMonAn)));
        double tienGiam = 0;
        double tienThue = 0;
        double tongTienSauGiam = tongTienMonAn;

        if (km != null) {
            String tenKM = km.getTenKM() != null ? km.getTenKM() : "Khuyến mãi";
            if (km.getGiaTri() < 1.0) { // Giảm %
                tienGiam = tongTienMonAn * km.getGiaTri();
                sb.append(String.format("%-25s%13s\n", tenKM + String.format(" (%.0f%%):", km.getGiaTri() * 100), "-" + moneyFormatter.format(tienGiam)));
            } else { // Giảm tiền
                tienGiam = km.getGiaTri();
                 sb.append(String.format("%-25s%13s\n", tenKM + ":", "-" + moneyFormatter.format(tienGiam)));
            }
            tongTienSauGiam -= tienGiam;
            if (tongTienSauGiam < 0) tongTienSauGiam = 0;
        }

        if (thue != null && thue.getTyLeThue() > 0) {
            tienThue = tongTienSauGiam * thue.getTyLeThue();
            sb.append(String.format("%-25s%13s\n", thue.getTenThue() + String.format(" (%.1f%%):", thue.getTyLeThue() * 100), moneyFormatter.format(tienThue)));
        }
        double tongThanhToan = tongTienSauGiam + tienThue;

        sb.append("------------------------------------------\n");
        // Dùng font chữ đậm hoặc cách khác để làm nổi bật dòng này
        sb.append(String.format("%-25s%13s\n", "THÀNH TIỀN:", moneyFormatter.format(tongThanhToan)));
        sb.append("------------------------------------------\n");

        // Footer
        sb.append("\n        Cảm ơn quý khách!\n");
        sb.append("           Hẹn gặp lại!\n");
        sb.append("\nIn lúc: ").append(LocalDateTime.now().format(timeFormatter))
          .append(" ").append(LocalDate.now().format(dateFormatter)).append("\n");


        return sb.toString();
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 1) + ".";
    }
}