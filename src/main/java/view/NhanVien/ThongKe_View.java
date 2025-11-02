package view.NhanVien;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import dao.Ban_DAO; // Cần Khai báo Ban_DAO
import dao.HoaDon_DAO;
import dao.KhachHang_DAO; // Cần Khai báo KhachHang_DAO

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.*;
import java.util.Map;
import java.util.Collections;
import java.util.LinkedHashMap;

/**
 * ThongKe_View - FIXED: Logic xử lý filter ngày tháng và gọi DAO đã được tối ưu hóa.
 */
public class ThongKe_View extends JPanel {

    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    // Giữ lại Ban_DAO và KhachHang_DAO vì chúng được dùng để lấy dữ liệu thống kê
    private final Ban_DAO banDAO = new Ban_DAO();
    private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();

    private JDateChooser dateFrom;
    private JDateChooser dateTo;

    private JComboBox<String> cboLoaiThongKe;
    private JButton btnApDung;
    private JButton btnHienTai;

    private JPanel topPanel;
    private JPanel chartPanel;
    private JPanel contentPanel;

    private static final DecimalFormat VN_FORMAT = new DecimalFormat("#,###");

    public ThongKe_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 248, 243));

        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.NORTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(250, 248, 243));
        add(contentPanel, BorderLayout.CENTER);

        // Load ban đầu: Tải dữ liệu "Tất cả"
        loadStatistics(null, null);
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(new Color(250, 248, 243));
        panel.setBorder(new EmptyBorder(15, 20, 10, 20));

        JLabel lblTitle = new JLabel("Bộ lọc thống kê:");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(lblTitle);

        JLabel lblLoai = new JLabel("Loại:");
        lblLoai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lblLoai);

        cboLoaiThongKe = new JComboBox<>(new String[]{
                "Tất cả", "Hôm nay", "Tuần này", "Tháng này", "Năm này", "Tùy chọn"
        });
        cboLoaiThongKe.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboLoaiThongKe.setPreferredSize(new Dimension(120, 30));
        cboLoaiThongKe.setSelectedIndex(0); // Mặc định là "Tất cả"
        cboLoaiThongKe.addActionListener(e -> onLoaiThongKeChanged());
        panel.add(cboLoaiThongKe);

        JLabel lblFrom = new JLabel("Từ ngày:");
        lblFrom.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lblFrom);

        dateFrom = new JDateChooser();
        dateFrom.setDateFormatString("dd/MM/yyyy");
        dateFrom.setPreferredSize(new Dimension(130, 30));
        dateFrom.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateFrom.setDate(null);
        panel.add(dateFrom);

        JLabel lblTo = new JLabel("Đến ngày:");
        lblTo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lblTo);

        dateTo = new JDateChooser();
        dateTo.setDateFormatString("dd/MM/yyyy");
        dateTo.setPreferredSize(new Dimension(130, 30));
        dateTo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateTo.setDate(new Date());
        panel.add(dateTo);

        btnApDung = new JButton("Áp dụng");
        btnApDung.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnApDung.setPreferredSize(new Dimension(100, 30));
        btnApDung.setBackground(new Color(33, 150, 243));
        btnApDung.setForeground(Color.WHITE);
        btnApDung.setFocusPainted(false);
        btnApDung.setBorderPainted(false);
        btnApDung.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnApDung.addActionListener(e -> applyFilter());
        btnApDung.setEnabled(false); // Ban đầu vô hiệu hóa
        panel.add(btnApDung);

        btnHienTai = new JButton("Làm mới");
        btnHienTai.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnHienTai.setPreferredSize(new Dimension(100, 30));
        btnHienTai.setBackground(new Color(76, 175, 80));
        btnHienTai.setForeground(Color.WHITE);
        btnHienTai.setFocusPainted(false);
        btnHienTai.setBorderPainted(false);
        btnHienTai.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHienTai.addActionListener(e -> {
            cboLoaiThongKe.setSelectedIndex(0);
            dateFrom.setDate(null);
            dateTo.setDate(new Date());
            setDateChooserEnabled(dateFrom, false); // Vô hiệu hóa
            setDateChooserEnabled(dateTo, false);   // Vô hiệu hóa
            btnApDung.setEnabled(false);
            loadStatistics(null, null);
        });
        panel.add(btnHienTai);

        // Vô hiệu hóa ban đầu, chỉ "Tùy chọn" mới kích hoạt
        SwingUtilities.invokeLater(() -> {
            setDateChooserEnabled(dateFrom, false);
            setDateChooserEnabled(dateTo, false);
        });

        return panel;
    }

    // SỬA: Cố định việc vô hiệu hóa JDateChooser
    private void setDateChooserEnabled(JDateChooser chooser, boolean enabled) {
        if (chooser == null) return;
        
        chooser.setEnabled(enabled);
        
        try {
            JTextFieldDateEditor ed = (JTextFieldDateEditor) chooser.getDateEditor();
            ed.setEditable(enabled);
            ed.setFocusable(enabled);
            // Thay đổi màu nền để thể hiện trạng thái (đẹp hơn)
            ed.setBackground(enabled ? Color.WHITE : new Color(240, 240, 240));
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            try {
                Component btn = chooser.getCalendarButton();
                if (btn != null) {
                    btn.setEnabled(enabled);
                    btn.setFocusable(enabled);
                }
            } catch (Exception ignored) {
            }
        });
    }

    // SỬA: Logic thay đổi ComboBox để xử lý "Tùy chọn" và tự động tải dữ liệu cho các mốc cố định
    private void onLoaiThongKeChanged() {
        String selected = (String) cboLoaiThongKe.getSelectedItem();
        boolean isCustom = "Tùy chọn".equals(selected);

        setDateChooserEnabled(dateFrom, isCustom);
        setDateChooserEnabled(dateTo, isCustom);
        btnApDung.setEnabled(isCustom);

        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        Date endDate = endCal.getTime();

        Date startDate = null;
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        switch (selected) {
            case "Hôm nay":
                startDate = startCal.getTime();
                break;
            case "Tuần này":
                startCal.set(Calendar.DAY_OF_WEEK, startCal.getFirstDayOfWeek());
                startDate = startCal.getTime();
                break;
            case "Tháng này":
                startCal.set(Calendar.DAY_OF_MONTH, 1);
                startDate = startCal.getTime();
                break;
            case "Năm này":
                startCal.set(Calendar.DAY_OF_YEAR, 1);
                startDate = startCal.getTime();
                break;
            case "Tất cả":
                dateFrom.setDate(null);
                dateTo.setDate(new Date());
                loadStatistics(null, null);
                return; // Thoát vì đã load xong
            case "Tùy chọn":
                dateFrom.setDate(null); // Cho phép người dùng chọn lại
                dateTo.setDate(new Date()); 
                return; // Thoát vì cần nút áp dụng
        }
        
        // Cập nhật JDateChooser cho các mốc cố định
        dateFrom.setDate(startDate);
        dateTo.setDate(endDate); 

        // Tự động tải thống kê cho các mốc cố định
        loadStatistics(startDate, endDate);
    }

    // SỬA: Logic applyFilter() để chỉ dùng khi chọn 'Tùy chọn'
    private void applyFilter() {
        if (!"Tùy chọn".equals(cboLoaiThongKe.getSelectedItem())) {
             JOptionPane.showMessageDialog(this,
                "Chức năng 'Áp dụng' chỉ dùng cho bộ lọc 'Tùy chọn'.",
                "Thông báo",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Date from = dateFrom.getDate();
        Date to = dateTo.getDate();

        if (from == null || to == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn cả ngày bắt đầu và ngày kết thúc.",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Đảm bảo thời gian là 00:00:00.000 (from) và 23:59:59.999 (to)
        from = getStartOfDay(from);
        to = getEndOfDay(to);
        
        if (from.after(to)) {
            JOptionPane.showMessageDialog(this,
                    "Ngày bắt đầu phải trước hoặc bằng ngày kết thúc!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Gọi loadStatistics với ngày tùy chọn
        loadStatistics(from, to);
    }

    private void loadStatistics(Date from, Date to) {
        contentPanel.removeAll();

        topPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        topPanel.setBackground(new Color(250, 248, 243));
        topPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        // Gọi các phương thức DAO đã được bổ sung
        double tongDoanhThu = hoaDonDAO.getTongDoanhThu(from, to);
        int tongDonHang = hoaDonDAO.getTongDonHang(from, to);
        
        // Giả định KhachHang_DAO đã có phương thức này
        int tongKhach = khachHangDAO.getSoLuongKhachDaDatHang(from, to); 
        double trungBinhDon = (tongDonHang > 0) ? (tongDoanhThu / tongDonHang) : 0;

        topPanel.add(createStatCard("Tổng doanh thu", String.format("%s đ", VN_FORMAT.format(tongDoanhThu)), "", new Color(255, 140, 0)));
        topPanel.add(createStatCard("Tổng đơn hàng", String.valueOf(tongDonHang), "", new Color(76, 175, 80)));
        topPanel.add(createStatCard("Giá trị TB/đơn", String.format("%s đ", VN_FORMAT.format(trungBinhDon)), "", new Color(33, 150, 243)));
        topPanel.add(createStatCard("Khách hàng", String.valueOf(tongKhach), "", new Color(0, 200, 83)));

        contentPanel.add(topPanel, BorderLayout.NORTH);

        chartPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        chartPanel.setBackground(new Color(250, 248, 243));
        chartPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        // Tạo lại biểu đồ với ngày tháng mới
        chartPanel.add(new BarChartPanel(from, to));
        chartPanel.add(new PieChartPanel(from, to));

        contentPanel.add(chartPanel, BorderLayout.CENTER);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createStatCard(String title, String value, String percent, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTitle.setForeground(new Color(100, 100, 100));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblValue.setForeground(accent);
        lblValue.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(lblTitle, BorderLayout.WEST);

        card.add(top, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    // -------------------------
    // Inner class: BarChartPanel (Java2D) - Giữ nguyên logic vẽ, thay đổi logic tải data
    // -------------------------
    private class BarChartPanel extends JPanel {
        private final Map<String, Double> data;
        private final int padding = 40;
        private final int labelPadding = 40;
        private final Color axisColor = new Color(150, 150, 150);
        private final Color barColor = new Color(52, 152, 219);
        private final Color gridColor = new Color(220, 220, 220);
        private final int titleHeight = 32;

        BarChartPanel(Date from, Date to) {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(700, 360));
            
            Map<String, Double> doanhThuData;
            if (from == null && to == null) {
                // Mặc định cho "Tất cả" hoặc lần load đầu tiên: 7 ngày gần nhất
                doanhThuData = hoaDonDAO.getDoanhThuTheoKhoangThoiGian(
                    getStartOfDay(getPreviousDate(6)), 
                    getEndOfDay(new Date())
                );
            } else {
                // Sử dụng ngày tháng được truyền vào
                doanhThuData = hoaDonDAO.getDoanhThuTheoKhoangThoiGian(from, to);
            }

            if (doanhThuData == null || doanhThuData.isEmpty()) {
                this.data = new LinkedHashMap<>();
                this.data.put("Không có dữ liệu", 0.0);
            } else {
                Map<String, Double> cleaned = new LinkedHashMap<>();
                for (Map.Entry<String, Double> e : doanhThuData.entrySet()) {
                    String k = e.getKey();
                    if (k == null || k.trim().isEmpty()) k = "(Chưa có tên)";
                    cleaned.put(k, e.getValue() == null ? 0.0 : e.getValue());
                }
                this.data = cleaned;
            }

            ToolTipManager.sharedInstance().registerComponent(this);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    setToolTipText(null);
                }
            });

            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    String key = getLabelAt(e.getX(), e.getY());
                    if (key != null) {
                        Double v = data.get(key);
                        setToolTipText(key + ": " + VN_FORMAT.format(v) + " đ");
                    } else {
                        setToolTipText(null);
                    }
                }
            });
        }

        private String getLabelAt(int mx, int my) {
            int width = getWidth();
            int height = getHeight();
            int n = data.size();
            if (n == 0) return null;
            int availableWidth = width - 2 * padding - labelPadding;
            int barWidth = Math.max(1, availableWidth / (n * 2));
            int startX = padding + labelPadding;
            int i = 0;
            double max = Collections.max(data.values());
            if (max <= 0) max = 1;
            for (String key : data.keySet()) {
                int x = startX + i * 2 * barWidth;
                int barHeight = (int) ((height - 2 * padding - titleHeight) * (data.get(key) / max));
                int y = height - padding - barHeight;
                Rectangle r = new Rectangle(x, y, barWidth, Math.max(barHeight, 1));
                if (r.contains(mx, my)) return key;
                i++;
            }
            return null;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            String title = "Doanh thu";
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            FontMetrics tfm = g2.getFontMetrics();
            int tx = (width - tfm.stringWidth(title)) / 2;
            int ty = (titleHeight + tfm.getAscent()) / 2;
            g2.setColor(new Color(60, 60, 60));
            g2.drawString(title, tx, ty);

            g2.setColor(gridColor);
            int gridLines = 5;
            for (int i = 0; i <= gridLines; i++) {
                int y = titleHeight + padding + (height - 2 * padding - titleHeight) * i / gridLines;
                g2.drawLine(padding + labelPadding, y, width - padding, y);
            }

            g2.setColor(axisColor);
            g2.drawLine(padding + labelPadding, height - padding, width - padding, height - padding);
            g2.drawLine(padding + labelPadding, titleHeight + padding, padding + labelPadding, height - padding);

            int n = data.size();
            int availableWidth = width - 2 * padding - labelPadding;
            int barWidth = Math.max(1, availableWidth / (n * 2));
            int startX = padding + labelPadding;
            double max = Collections.max(data.values());
            if (max <= 0) max = 1;

            int i = 0;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                String label = entry.getKey();
                if (label == null || label.trim().isEmpty()) label = "(Chưa có tên)";
                double value = entry.getValue() != null ? entry.getValue() : 0.0;
                int x = startX + i * 2 * barWidth;
                int barHeight = (int) ((height - 2 * padding - titleHeight) * (value / max));
                int y = height - padding - barHeight;

                g2.setColor(barColor);
                g2.fillRect(x, y, barWidth, Math.max(barHeight, 1));

                g2.setColor(new Color(60, 60, 60));
                String vtext = VN_FORMAT.format(value) + " đ";
                FontMetrics fm = g2.getFontMetrics();
                int vx = x + (barWidth - fm.stringWidth(vtext)) / 2;
                int vy = Math.max(y - 5, titleHeight + padding + fm.getAscent());
                g2.drawString(vtext, vx, vy);

                String shortLabel = label.length() > 12 ? label.substring(0, 12) + "..." : label;
                int lx = x + (barWidth - g2.getFontMetrics().stringWidth(shortLabel)) / 2;
                int ly = height - padding + g2.getFontMetrics().getAscent() + 5;
                g2.drawString(shortLabel, lx, ly);

                i++;
            }

            g2.dispose();
        }
    }

    // -------------------------
    // Inner class: PieChartPanel (Java2D) - Giữ nguyên logic vẽ, thay đổi logic tải data
    // -------------------------
    private class PieChartPanel extends JPanel {
        private final Map<String, Integer> data;
        private final Color[] COLORS = {
                new Color(52, 152, 219),
                new Color(46, 204, 113),
                new Color(243, 156, 18),
                new Color(155, 89, 182),
                new Color(231, 76, 60),
                new Color(52, 73, 94)
        };
        private final int titleHeight = 32;

        PieChartPanel(Date from, Date to) {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(560, 360));
            
            // Giả định Ban_DAO đã có phương thức getSoBanTheoKhuVuc(Date from, Date to)
            Map<String, Integer> raw = banDAO.getSoBanTheoKhuVuc(from, to);
            
            if (raw == null || raw.isEmpty()) {
                this.data = new LinkedHashMap<>();
                this.data.put("Không có dữ liệu", 1);
            } else {
                Map<String, Integer> cleaned = new LinkedHashMap<>();
                boolean allZero = true;
                for (Map.Entry<String, Integer> e : raw.entrySet()) {
                    String k = e.getKey();
                    if (k == null || k.trim().isEmpty()) k = "(Chưa có tên)";
                    Integer v = e.getValue() == null ? 0 : e.getValue();
                    if (v > 0) allZero = false;
                    cleaned.put(k, v);
                }
                if (allZero) {
                    this.data = new LinkedHashMap<>();
                    this.data.put("Không có dữ liệu", 1);
                } else {
                    this.data = cleaned;
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w, h) - 120;
            int cx = w / 2 - 20;
            int cy = (h + titleHeight) / 2 - 10;
            int rx = size / 2;
            int ry = size / 2;

            String title = "Khu vực";
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            FontMetrics tfm = g2.getFontMetrics();
            int tx = (w - tfm.stringWidth(title)) / 2;
            int ty = (titleHeight + tfm.getAscent()) / 2;
            g2.setColor(new Color(60, 60, 60));
            g2.drawString(title, tx, ty);

            double total = 0;
            for (Integer v : data.values()) total += (v != null ? v : 0);
            if (total == 0) total = 1;

            double startAngle = 0;
            int i = 0;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                double value = entry.getValue() != null ? entry.getValue() : 0;
                double angle = value / total * 360.0;

                g2.setColor(COLORS[i % COLORS.length]);
                g2.fillArc(cx - rx, cy - ry, rx * 2, ry * 2, (int) Math.round(startAngle), (int) Math.round(angle));

                double mid = Math.toRadians(startAngle + angle / 2.0);
                double lx = cx + Math.cos(mid) * (rx + 12);
                double ly = cy + Math.sin(mid) * (ry + 12);
                String pct = String.format("%.1f%%", (value / total) * 100);
                g2.setColor(new Color(60, 60, 60));
                g2.drawString(pct, (int) lx - 10, (int) ly);

                startAngle += angle;
                i++;
            }

            int legendX = cx + rx + 20;
            int legendY = cy - ry;
            int idx = 0;
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                Color c = COLORS[idx % COLORS.length];
                g2.setColor(c);
                g2.fillRect(legendX, legendY + idx * 22, 14, 14);
                g2.setColor(new Color(60, 60, 60));
                String label = entry.getKey() + " (" + entry.getValue() + ")";
                g2.drawString(label, legendX + 18, legendY + 12 + idx * 22);
                idx++;
            }

            g2.dispose();
        }
    }

    // Date helpers - Giữ nguyên
    private Date getPreviousDate(int daysAgo) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    private Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    private Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
}