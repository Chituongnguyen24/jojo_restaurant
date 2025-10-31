package view.HoaDon;

import dao.Ban_DAO;
import dao.KhachHang_DAO;
import dao.NhanVien_DAO;
import dao.Thue_DAO; // Thay thế HoaDon_Thue_DAO
import dao.KhuyenMai_DAO; // Thay thế HoaDon_KhuyenMai_DAO
import entity.Ban;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.NhanVien;
import entity.Thue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_Printer {

    private static final DecimalFormat moneyFormatter = new DecimalFormat("#,###");
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    // === DAO cần thiết ===
    private static NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
    private static Ban_DAO banDAO = new Ban_DAO();
    private static Thue_DAO thueDAO = new Thue_DAO(); // SỬA
    private static KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO(); // SỬA
    private static KhachHang_DAO khachHangDAO = new KhachHang_DAO(); // THÊM

    // Chiều rộng "giấy" in nội dung
    private static final int LINE_WIDTH = 48;
    // Kích thước mặc định dialog để lần đầu mở có đủ chỗ (tăng chiều rộng)
    private static final Dimension DEFAULT_DIALOG_SIZE = new Dimension(500, 800); // Giảm bớt để gần với thực tế
    private static final Dimension MIN_DIALOG_SIZE = new Dimension(400, 600);

    // --------- PUBLIC API ---------

    public static String generateInvoiceText(HoaDon hoaDon, List<ChiTietHoaDon> chiTietList) {
        return generateInvoiceText(hoaDon, chiTietList, LINE_WIDTH);
    }

    public static String generateInvoiceText(HoaDon hoaDon, List<ChiTietHoaDon> chiTietList, int pageWidth) {
        StringBuilder sb = new StringBuilder();

        // Lấy thông tin chi tiết (thử tránh ném lỗi DAO)
        NhanVien nv = null;
        Ban ban = null;
        Thue thue = null;
        KhuyenMai km = null;
        KhachHang kh = null;
        try {
            if (hoaDon.getNhanVien() != null) {
                nv = nhanVienDAO.getNhanVienById(hoaDon.getNhanVien().getMaNhanVien()); // SỬA: getMaNhanVien
            }
            if (hoaDon.getBan() != null) {
                ban = banDAO.getBanTheoMa(hoaDon.getBan().getMaBan());
            }
            if (hoaDon.getThue() != null) {
                thue = thueDAO.getThueById(hoaDon.getThue().getMaSoThue()); // SỬA: getMaSoThue
            }
            if (hoaDon.getKhuyenMai() != null && !safeTrim(hoaDon.getKhuyenMai().getMaKM()).equals("KM00000000")) {
                km = khuyenMaiDAO.getKhuyenMaiById(hoaDon.getKhuyenMai().getMaKM());
            }
            if (hoaDon.getKhachHang() != null) {
                 kh = khachHangDAO.getKhachHangById(hoaDon.getKhachHang().getMaKH()); // SỬA: getMaKH
            }
        } catch (Exception ignored) {}

        // Header (format theo LINE_WIDTH)
        sb.append(center("*** JOJO RESTAURANT ***", LINE_WIDTH)).append("\n");
        sb.append(center("12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TPHCM", LINE_WIDTH)).append("\n");
        sb.append(center("ĐT: 0123.456.789", LINE_WIDTH)).append("\n");
        sb.append(repeat('-', LINE_WIDTH)).append("\n");
        sb.append(center("PHIẾU TÍNH TIỀN", LINE_WIDTH)).append("\n");
        sb.append(repeat('-', LINE_WIDTH)).append("\n");

        // Thông tin hóa đơn
        sb.append(padRight("Số HD: " + safeTrim(hoaDon.getMaHD()), LINE_WIDTH)).append("\n"); // SỬA: getMaHD
        String ngay = hoaDon.getNgayLapHoaDon() != null ? hoaDon.getNgayLapHoaDon().format(dateFormatter) : ""; // SỬA: getNgayLapHoaDon
        String gioVao = hoaDon.getGioVao() != null ? hoaDon.getGioVao().format(timeFormatter) : "";
        String gioRa = hoaDon.getGioRa() != null ? hoaDon.getGioRa().format(timeFormatter) : "";
        sb.append(padRight("Ngày: " + ngay + "  Giờ vào: " + gioVao, LINE_WIDTH)).append("\n");
        sb.append(padRight("Giờ ra: " + gioRa, LINE_WIDTH)).append("\n");
        if (ban != null) sb.append(padRight("Bàn: " + safeTrim(ban.getMaBan()), LINE_WIDTH)).append("\n");
        if (nv != null) sb.append(padRight("Thu ngân: " + nv.getHoTen(), LINE_WIDTH)).append("\n"); // SỬA: getHoTen

        // Khách hàng
        if (kh != null && !"KH00000000".equals(safeTrim(kh.getMaKH()))) { // SỬA: getMaKH
            sb.append(padRight("Khách hàng: " + kh.getTenKH(), LINE_WIDTH)).append("\n"); // SỬA: getTenKH
            if (kh.getSoDienThoai() != null && !kh.getSoDienThoai().trim().isEmpty() && !"0000000000".equals(kh.getSoDienThoai().trim())) {
                sb.append(padRight("SĐT KH: " + kh.getSoDienThoai(), LINE_WIDTH)).append("\n"); // SỬA: getSoDienThoai
            }
        } else {
            sb.append(padRight("Khách hàng: Khách lẻ", LINE_WIDTH)).append("\n");
            if (hoaDon.getPhieuDatBan() != null) {
                sb.append(padRight("Phiếu đặt: " + safeTrim(hoaDon.getPhieuDatBan().getMaPhieu()), LINE_WIDTH)).append("\n");
            }
        }
        sb.append(repeat('-', LINE_WIDTH)).append("\n");

        // Bảng món
        int nameCol = 28;
        int qtyCol = 4;
        int priceCol = LINE_WIDTH - nameCol - qtyCol;
        sb.append(String.format("%-" + nameCol + "s%" + qtyCol + "s%" + priceCol + "s\n", "Tên Món", "SL", "Thành tiền"));
        sb.append(repeat('-', LINE_WIDTH)).append("\n");

        double tongTienMonAn = 0;
        if (chiTietList != null) {
            for (ChiTietHoaDon ct : chiTietList) {
                // SỬA: Dùng getDonGiaBan
                double thanhTien = ct.getSoLuong() * ct.getDonGiaBan(); 
                String itemName = ct.getMonAn() != null ? ct.getMonAn().getTenMonAn() : "";
                List<String> wrapped = wrapText(itemName, nameCol);
                for (int i = 0; i < wrapped.size(); i++) {
                    String namePart = wrapped.get(i);
                    String qtyPart = (i == 0) ? String.valueOf(ct.getSoLuong()) : "";
                    String pricePart = (i == 0) ? formatCurrency(thanhTien) : "";
                    sb.append(String.format("%-" + nameCol + "s%" + qtyCol + "s%" + priceCol + "s\n",
                            namePart, qtyPart, pricePart));
                }
                tongTienMonAn += thanhTien;
            }
        }
        sb.append(repeat('-', LINE_WIDTH)).append("\n");

        // Tổng kết
        sb.append(formatLine("Tổng tiền:", formatCurrency(tongTienMonAn), LINE_WIDTH)).append("\n");
        double tienGiam = 0;
        double tienThue = 0;
        double tongTienSauGiam = tongTienMonAn;

        if (km != null) {
            String tenKM = km.getMoTa() != null ? km.getMoTa() : "Khuyến mãi"; // SỬA: getMoTa
            double mucKM = km.getMucKM(); // SỬA: getMucKM
            if (mucKM < 1.0) { // Giảm theo phần trăm
                tienGiam = tongTienMonAn * mucKM;
                sb.append(formatLine(tenKM + String.format(" (%.0f%%):", mucKM * 100), "-" + formatCurrency(tienGiam), LINE_WIDTH)).append("\n");
            } else { // Giảm theo số tiền
                tienGiam = mucKM;
                sb.append(formatLine(tenKM + ":", "-" + formatCurrency(tienGiam), LINE_WIDTH)).append("\n");
            }
            tongTienSauGiam -= tienGiam;
            if (tongTienSauGiam < 0) tongTienSauGiam = 0;
        }

        if (thue != null && thue.getTyLeThue() > 0) {
            double tyLeThue = thue.getTyLeThue(); // SỬA: getTyLeThue
            tienThue = tongTienSauGiam * tyLeThue;
            sb.append(formatLine(thue.getTenThue() + String.format(" (%.1f%%):", tyLeThue * 100), formatCurrency(tienThue), LINE_WIDTH)).append("\n");
        }

        double tongThanhToan = tongTienSauGiam + tienThue;

        sb.append(repeat('-', LINE_WIDTH)).append("\n");

        // Làm tròn đến 1000 đồng gần nhất
        long rounded = Math.round(tongThanhToan / 1000.0) * 1000;
        long roundingDiff = rounded - Math.round(tongThanhToan);

        sb.append(center("=> " + padBoth("THÀNH TIỀN", formatCurrency(tongThanhToan), LINE_WIDTH - 4), LINE_WIDTH)).append("\n");
        if (roundingDiff != 0) {
            sb.append(formatLine("Làm tròn:", (roundingDiff > 0 ? "+" : "") + formatCurrency(roundingDiff), LINE_WIDTH)).append("\n");
            sb.append(formatLine("Tổng phải thanh toán:", formatCurrency(rounded), LINE_WIDTH)).append("\n");
        } else {
            sb.append(formatLine("Tổng phải thanh toán:", formatCurrency(rounded), LINE_WIDTH)).append("\n");
        }
        sb.append(repeat('-', LINE_WIDTH)).append("\n");

        // Footer
        sb.append(center("Cảm ơn quý khách! Hẹn gặp lại!", LINE_WIDTH)).append("\n");
        sb.append("\n");
        sb.append(padRight("In lúc: " + LocalDateTime.now().format(timeFormatter) + "  " + LocalDate.now().format(dateFormatter), LINE_WIDTH)).append("\n");

        // Căn đều block theo pageWidth (tính left/right padding)
        String block = sb.toString();
        return centerBlock(block, pageWidth);
    }

    /**
     * Hiển thị preview trong một dialog được tạo bên trong lớp này.
     * Dialog dùng font Monospaced, kích thước mặc định rộng hơn để ngay khi mở nội dung đã căn đều.
     */
    public static void showPreview(Frame owner, HoaDon hoaDon, List<ChiTietHoaDon> chiTietList) {
        // Tạo dialog modal
        final JDialog dialog = new JDialog(owner, "Xem trước Hóa Đơn", true);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setBackground(Color.WHITE);
        textArea.setMargin(new Insets(12, 12, 12, 12));
        textArea.setLineWrap(false);

        JScrollPane scroll = new JScrollPane(textArea,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        JButton closeBtn = new JButton("Đóng");
        closeBtn.addActionListener((ActionEvent e) -> dialog.dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(closeBtn);

        dialog.getContentPane().setLayout(new BorderLayout(8, 8));
        dialog.getContentPane().add(scroll, BorderLayout.CENTER);
        dialog.getContentPane().add(bottom, BorderLayout.SOUTH);

        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        dialog.setMinimumSize(MIN_DIALOG_SIZE);
        dialog.setPreferredSize(DEFAULT_DIALOG_SIZE);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);

        // Cố gắng tính pageWidth dựa trên viewport width / FontMetrics trước khi gọi setVisible
        // (vì ta đã setPreferredSize lớn hơn, viewport nên có kích thước hợp lý)
        dialog.validate();
        dialog.doLayout();

        int viewportWidth = scroll.getViewport().getWidth();
        // Nếu chưa có viewport width (0), fallback dùng dialog width trừ Ổn định padding
        if (viewportWidth <= 0) {
            viewportWidth = dialog.getWidth() - 80; // trừ ước lượng padding + scrollbar
        }

        FontMetrics fm = textArea.getFontMetrics(textArea.getFont());
        int charWidth = fm.charWidth('M');
        if (charWidth <= 0) charWidth = 7; // fallback

        Insets insets = textArea.getInsets();
        int available = viewportWidth - insets.left - insets.right - 24; // thêm margin an toàn
        int pageWidth = Math.max(40, available / Math.max(1, charWidth));

        // Sinh text đã căn đều theo pageWidth đã tính
        String text = generateInvoiceText(hoaDon, chiTietList, pageWidth);

        // Gán text và hiển thị dialog
        textArea.setText(text);
        textArea.setCaretPosition(0);

        // Hiển thị dialog modal
        dialog.setVisible(true);
    }

    // --------- Helpers ---------
    private static String safeTrim(String s) {
        return s == null ? "" : s.trim();
    }

    private static String formatCurrency(double value) {
        long rounded = Math.round(value);
        return moneyFormatter.format(rounded) + " đ";
    }

    private static String repeat(char ch, int count) {
        StringBuilder s = new StringBuilder(Math.max(0, count));
        for (int i = 0; i < count; i++) s.append(ch);
        return s.toString();
    }

    private static String center(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text;
        int left = (width - text.length()) / 2;
        int right = width - text.length() - left;
        return repeat(' ', left) + text + repeat(' ', right);
    }

    private static String padRight(String text, int width) {
        if (text == null) text = "";
        if (text.length() >= width) return text.substring(0, width);
        return text + repeat(' ', width - text.length());
    }

    private static String formatLine(String left, String right, int width) {
        if (left == null) left = "";
        if (right == null) right = "";
        int space = width - left.length() - right.length();
        if (space < 1) space = 1;
        return left + repeat(' ', space) + right;
    }

    private static String padBoth(String left, String right, int width) {
        return formatLine(left, right, width);
    }

    private static List<String> wrapText(String text, int maxLen) {
        List<String> lines = new ArrayList<>();
        if (text == null) {
            lines.add("");
            return lines;
        }
        String remaining = text.trim();
        while (!remaining.isEmpty()) {
            if (remaining.length() <= maxLen) {
                lines.add(remaining);
                break;
            } else {
                int breakPos = -1;
                for (int i = maxLen; i >= 0; i--) {
                    if (i < remaining.length() && Character.isWhitespace(remaining.charAt(i))) {
                        breakPos = i;
                        break;
                    }
                }
                if (breakPos <= 0) {
                    lines.add(remaining.substring(0, maxLen));
                    remaining = remaining.substring(maxLen).trim();
                } else {
                    lines.add(remaining.substring(0, breakPos));
                    remaining = remaining.substring(breakPos).trim();
                }
            }
        }
        if (lines.isEmpty()) lines.add("");
        return lines;
    }

    private static String centerBlock(String block, int pageWidth) {
        if (block == null) return "";
        StringBuilder out = new StringBuilder();
        String[] lines = block.split("\\r?\\n", -1);
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            if (line.isEmpty()) {
                out.append(repeat(' ', pageWidth));
                if (i < lines.length - 1) out.append("\n");
                continue;
            }
            List<String> wrapped = wrapText(line, pageWidth);
            for (int j = 0; j < wrapped.size(); j++) {
                String wl = wrapped.get(j);
                int len = wl.length();
                if (len >= pageWidth) {
                    out.append(wl);
                } else {
                    int totalPad = pageWidth - len;
                    int leftPad = totalPad / 2;
                    int rightPad = totalPad - leftPad;
                    out.append(repeat(' ', leftPad)).append(wl).append(repeat(' ', rightPad));
                }
                if (!(i == lines.length - 1 && j == wrapped.size() - 1)) out.append("\n");
            }
        }
        return out.toString();
    }
}