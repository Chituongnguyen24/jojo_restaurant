package view.HoaDon;

import dao.*;
import entity.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class HoaDon_Printer {

    private static final DecimalFormat moneyFormatter = new DecimalFormat("#,###");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    // DAO instances
    private static NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
    private static Ban_DAO banDAO = new Ban_DAO();
    private static Thue_DAO thueDAO = new Thue_DAO(); 
    private static KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO(); 
    private static KhachHang_DAO khachHangDAO = new KhachHang_DAO(); 

    private static final int LINE_WIDTH = 48;
    private static final Dimension DEFAULT_DIALOG_SIZE = new Dimension(500, 700); 
    private static final Dimension MIN_DIALOG_SIZE = new Dimension(400, 600);

    public static String generateInvoiceText(HoaDon hoaDon, List<ChiTietHoaDon> chiTietList) {
        return generateInvoiceText(hoaDon, chiTietList, LINE_WIDTH);
    }

    public static String generateInvoiceText(HoaDon hoaDon, List<ChiTietHoaDon> chiTietList, int pageWidth) {
        StringBuilder sb = new StringBuilder();

        // 1. Tải thông tin liên quan (Giữ nguyên)
        NhanVien nv = null;
        Ban ban = null;
        KhuyenMai km = null;
        KhachHang kh = null;
        try {
            if (hoaDon.getNhanVien() != null) 
                nv = nhanVienDAO.getNhanVienById(hoaDon.getNhanVien().getMaNhanVien());
            if (hoaDon.getBan() != null) 
                ban = banDAO.getBanTheoMa(hoaDon.getBan().getMaBan());
            // Lấy lại KM từ DB để đảm bảo có mức giảm giá chính xác
            if (hoaDon.getKhuyenMai() != null && !safeTrim(hoaDon.getKhuyenMai().getMaKM()).equals("KM00000000")) 
                km = khuyenMaiDAO.getKhuyenMaiById(hoaDon.getKhuyenMai().getMaKM());
            if (hoaDon.getKhachHang() != null) 
                kh = khachHangDAO.getKhachHangById(hoaDon.getKhachHang().getMaKH());
        } catch (Exception ignored) {}

        // 2. Header (Giữ nguyên)
        sb.append(center("*** JOJO RESTAURANT ***", pageWidth)).append("\n");
        sb.append(center("12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TPHCM", pageWidth)).append("\n");
        sb.append(center("ĐT: 0123.456.789", pageWidth)).append("\n");
        sb.append(repeat('-', pageWidth)).append("\n");
        sb.append(center("PHIẾU TÍNH TIỀN", pageWidth)).append("\n");
        sb.append(repeat('-', pageWidth)).append("\n");

        // 3. Thông tin chung (Giữ nguyên)
        sb.append(padRight("Số HD: " + safeTrim(hoaDon.getMaHD()), pageWidth)).append("\n");
        String ngay = hoaDon.getNgayLapHoaDon() != null ? hoaDon.getNgayLapHoaDon().format(dateFormatter) : "";
        String gioVao = hoaDon.getGioVao() != null ? hoaDon.getGioVao().format(timeFormatter) : "";
        String gioRa = hoaDon.getGioRa() != null ? hoaDon.getGioRa().format(timeFormatter) : "";
        
        sb.append(padRight("Ngày: " + ngay + "  Giờ vào: " + gioVao, pageWidth)).append("\n");
        sb.append(padRight("Giờ ra: " + gioRa, pageWidth)).append("\n");
        if (ban != null) sb.append(padRight("Bàn: " + safeTrim(ban.getMaBan()), pageWidth)).append("\n");
        if (nv != null) sb.append(padRight("Thu ngân: " + nv.getHoTen(), pageWidth)).append("\n");

        if (kh != null && !"KH00000000".equals(safeTrim(kh.getMaKH()))) {
            sb.append(padRight("Khách hàng: " + kh.getTenKH(), pageWidth)).append("\n");
        } else {
            sb.append(padRight("Khách hàng: Khách lẻ", pageWidth)).append("\n");
        }
        sb.append(repeat('-', pageWidth)).append("\n");

        // 4. Danh sách món (Giữ nguyên)
        int nameCol = 28;
        int qtyCol = 4;
        int priceCol = pageWidth - nameCol - qtyCol;
        sb.append(String.format("%-" + nameCol + "s%" + qtyCol + "s%" + priceCol + "s\n", "Tên Món", "SL", "Thành tiền"));
        sb.append(repeat('-', pageWidth)).append("\n");

        double tongTienMonAn = 0;
        if (chiTietList != null) {
            for (ChiTietHoaDon ct : chiTietList) {
                double thanhTien = ct.getSoLuong() * ct.getDonGiaBan();
                String itemName = ct.getMonAn() != null ? ct.getMonAn().getTenMonAn() : "Món lạ";
                List<String> wrapped = wrapText(itemName, nameCol);
                
                for (int i = 0; i < wrapped.size(); i++) {
                    String namePart = wrapped.get(i);
                    String qtyPart = (i == 0) ? String.valueOf(ct.getSoLuong()) : "";
                    String pricePart = (i == 0) ? formatCurrency(thanhTien) : "";
                    sb.append(String.format("%-" + nameCol + "s%" + qtyCol + "s%" + priceCol + "s\n", namePart, qtyPart, pricePart));
                }
                tongTienMonAn += thanhTien;
            }
        }
        sb.append(repeat('-', pageWidth)).append("\n");

        // ========================================================================
        // 5. TÍNH TOÁN TIỀN (SỬA LẠI HOÀN TOÀN ĐỂ KHỚP FORM CHI TIẾT)
        // ========================================================================
        
        // A. Tổng tiền món
        BigDecimal bdTongMon = BigDecimal.valueOf(tongTienMonAn);

        // B. Tính Giảm giá dựa trên % Khuyến Mãi (Source of Truth)
        double tiLeKM = (km != null) ? km.getMucKM() : 0;
        BigDecimal bdGiam = bdTongMon.multiply(BigDecimal.valueOf(tiLeKM)).setScale(0, RoundingMode.HALF_UP);
        double tienGiamHienThi = bdGiam.doubleValue();

        // C. Tiền Sau Giảm
        BigDecimal bdSauGiam = bdTongMon.subtract(bdGiam);
        if (bdSauGiam.compareTo(BigDecimal.ZERO) < 0) bdSauGiam = BigDecimal.ZERO;

        // D. Lấy tỷ lệ thuế/phí (Có thể lấy từ DB hoặc hardcode nếu cố định)
        double ratePhi = 0.05; // 5%
        double rateVAT = 0.08; // 8%

        // E. Tính Phí Dịch Vụ (5% trên số tiền Sau Giảm)
        BigDecimal bdPhi = bdSauGiam.multiply(BigDecimal.valueOf(ratePhi)).setScale(0, RoundingMode.HALF_UP);

        // F. Tính VAT (8% trên [Sau Giảm + Phí Dịch Vụ]) -> Thuế chồng thuế
        BigDecimal baseVAT = bdSauGiam.add(bdPhi);
        BigDecimal bdVAT = baseVAT.multiply(BigDecimal.valueOf(rateVAT)).setScale(0, RoundingMode.HALF_UP);

        // G. Tổng Cộng Thanh Toán
        BigDecimal bdTongCong = bdSauGiam.add(bdPhi).add(bdVAT).setScale(0, RoundingMode.HALF_UP);


        // ========================================================================
        // 6. HIỂN THỊ TỔNG KẾT
        // ========================================================================
        sb.append(formatLine("Tổng tiền món:", formatCurrency(tongTienMonAn), pageWidth)).append("\n");
        
        if (tienGiamHienThi > 0) {
            String labelKM = "Giảm giá";
            String phanTramStr = "";

            if (km != null && km.getMoTa() != null) labelKM = km.getMoTa();
            if (tiLeKM > 0) {
                phanTramStr = String.format(" (%.0f%%)", tiLeKM * 100);
            }
            sb.append(formatLine(labelKM + phanTramStr + ":", "-" + formatCurrency(tienGiamHienThi), pageWidth)).append("\n");
        }

        if (bdPhi.doubleValue() > 0) {
            sb.append(formatLine("Phí dịch vụ (5%):", formatCurrency(bdPhi.doubleValue()), pageWidth)).append("\n");
        }

        if (bdVAT.doubleValue() > 0) {
            sb.append(formatLine("VAT (8%):", formatCurrency(bdVAT.doubleValue()), pageWidth)).append("\n");
        }

        sb.append(repeat('-', pageWidth)).append("\n");
        
        String strThanhTien = "THÀNH TIỀN: " + formatCurrency(bdTongCong.doubleValue());
        sb.append(center(strThanhTien, pageWidth)).append("\n");
        
        sb.append(repeat('-', pageWidth)).append("\n");

        // 7. Footer (Giữ nguyên)
        sb.append(center("Cảm ơn quý khách! Hẹn gặp lại!", pageWidth)).append("\n");
        sb.append("\n");
        sb.append(padRight("In lúc: " + LocalDateTime.now().format(timeFormatter) + " " + LocalDate.now().format(dateFormatter), pageWidth)).append("\n");

        return centerBlock(sb.toString(), pageWidth);
    }

    public static void showPreview(Frame owner, HoaDon hoaDon, List<ChiTietHoaDon> chiTietList) {
        final JDialog dialog = new JDialog(owner, "Xem trước Hóa Đơn", true);
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setBackground(Color.WHITE);
        textArea.setMargin(new Insets(15, 15, 15, 15));
        
        String invoiceText = generateInvoiceText(hoaDon, chiTietList, LINE_WIDTH);
        textArea.setText(invoiceText);
        textArea.setCaretPosition(0);

        JScrollPane scroll = new JScrollPane(textArea);
        JButton closeBtn = new JButton("Đóng");
        closeBtn.addActionListener(e -> dialog.dispose());
        JPanel bottom = new JPanel();
        bottom.add(closeBtn);

        dialog.add(scroll, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        
        dialog.setSize(DEFAULT_DIALOG_SIZE);
        dialog.setMinimumSize(MIN_DIALOG_SIZE);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    // --- Helpers ---
    private static String safeTrim(String s) { return s == null ? "" : s.trim(); }
    private static String formatCurrency(double value) { return moneyFormatter.format(Math.round(value)) + " đ"; }
    private static String repeat(char ch, int count) { return String.valueOf(ch).repeat(Math.max(0, count)); }
    private static String center(String text, int width) {
        if (text == null) text = ""; if (text.length() >= width) return text;
        int padding = (width - text.length()) / 2;
        return repeat(' ', padding) + text + repeat(' ', width - text.length() - padding);
    }
    private static String padRight(String text, int width) {
        if (text == null) text = ""; return String.format("%-" + width + "s", text);
    }
    private static String formatLine(String left, String right, int width) {
        int space = width - left.length() - right.length();
        return left + repeat(' ', Math.max(1, space)) + right;
    }
    private static List<String> wrapText(String text, int maxLen) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) { lines.add(""); return lines; }
        String[] words = text.trim().split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxLen) {
                lines.add(currentLine.toString()); currentLine = new StringBuilder(word);
            } else {
                if (currentLine.length() > 0) currentLine.append(" "); currentLine.append(word);
            }
        }
        if (currentLine.length() > 0) lines.add(currentLine.toString());
        return lines;
    }
    private static String centerBlock(String text, int width) { return text; }
}