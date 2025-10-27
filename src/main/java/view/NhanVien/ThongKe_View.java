package view.NhanVien;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import dao.Ban_DAO;
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;
import java.util.Map;


public class ThongKe_View extends JPanel {

    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private final Ban_DAO banDAO = new Ban_DAO();
    private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();

    private JDateChooser dateFrom;
    private JDateChooser dateTo;
    private JTextFieldDateEditor dateFromEditor;
    private JTextFieldDateEditor dateToEditor;

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
        dateFrom.setEnabled(false);
        dateFromEditor = (JTextFieldDateEditor) dateFrom.getDateEditor();
        if (dateFromEditor != null) dateFromEditor.setEnabled(false);
        panel.add(dateFrom);

        JLabel lblTo = new JLabel("Đến ngày:");
        lblTo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lblTo);

        dateTo = new JDateChooser();
        dateTo.setDateFormatString("dd/MM/yyyy");
        dateTo.setPreferredSize(new Dimension(130, 30));
        dateTo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateTo.setDate(new Date());
        dateTo.setEnabled(false);
        dateToEditor = (JTextFieldDateEditor) dateTo.getDateEditor();
        if (dateToEditor != null) dateToEditor.setEnabled(false);
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
            dateFrom.setEnabled(false);
            if (dateFromEditor != null) dateFromEditor.setEnabled(false);
            dateTo.setEnabled(false);
            if (dateToEditor != null) dateToEditor.setEnabled(false);
            loadStatistics(null, null);
        });
        panel.add(btnHienTai);

        return panel;
    }

    private void onLoaiThongKeChanged() {
        String selected = (String) cboLoaiThongKe.getSelectedItem();
        boolean isCustom = "Tùy chọn".equals(selected);

        dateFrom.setEnabled(isCustom);
        dateTo.setEnabled(isCustom);
        if (dateFromEditor != null) dateFromEditor.setEnabled(isCustom);
        if (dateToEditor != null) dateToEditor.setEnabled(isCustom);

        if (!isCustom) {
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
                    break;
            }

            dateFrom.setDate(startDate);
            dateTo.setDate("Tất cả".equals(selected) ? new Date() : endDate);

            if (!"Tất cả".equals(selected)) {
                loadStatistics(startDate, endDate);
            } else {
                loadStatistics(null, null);
            }
        } else {
            dateFrom.setDate(null);
            dateTo.setDate(new Date());
        }
    }

    private void applyFilter() {
        Date from = dateFrom.getDate();
        Date to = dateTo.getDate();

        if ("Tùy chọn".equals(cboLoaiThongKe.getSelectedItem())) {
            if (from == null || to == null) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn cả ngày bắt đầu và ngày kết thúc.",
                        "Thiếu thông tin",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            Calendar calFrom = Calendar.getInstance();
            calFrom.setTime(from);
            calFrom.set(Calendar.HOUR_OF_DAY, 0);
            calFrom.set(Calendar.MINUTE, 0);
            calFrom.set(Calendar.SECOND, 0);
            calFrom.set(Calendar.MILLISECOND, 0);
            from = calFrom.getTime();

            Calendar calTo = Calendar.getInstance();
            calTo.setTime(to);
            calTo.set(Calendar.HOUR_OF_DAY, 23);
            calTo.set(Calendar.MINUTE, 59);
            calTo.set(Calendar.SECOND, 59);
            calTo.set(Calendar.MILLISECOND, 999);
            to = calTo.getTime();

            if (from.after(to)) {
                JOptionPane.showMessageDialog(this,
                        "Ngày bắt đầu phải trước hoặc bằng ngày kết thúc!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            loadStatistics(from, to);
        } else {
            onLoaiThongKeChanged();
        }
    }

    private void loadStatistics(Date from, Date to) {
        contentPanel.removeAll();

        topPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        topPanel.setBackground(new Color(250, 248, 243));
        topPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        double tongDoanhThu = hoaDonDAO.getTongDoanhThu(from, to);
        int tongDonHang = hoaDonDAO.getTongDonHang(from, to);
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
    // Inner class: BarChartPanel (Java2D)
    // -------------------------
    private class BarChartPanel extends JPanel {
        private final Map<String, Double> data;
        private final int padding = 40;
        private final int labelPadding = 40;
        private final Color axisColor = new Color(150, 150, 150);
        private final Color barColor = new Color(52, 152, 219);
        private final Color gridColor = new Color(220, 220, 220);

        BarChartPanel(Date from, Date to) {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(700, 360));
            Map<String, Double> doanhThuData;
            if (from == null || to == null) {
                Date endDate = getEndOfDay(new Date());
                Date startDate = getStartOfDay(getPreviousDate(6));
                doanhThuData = hoaDonDAO.getDoanhThuTheoKhoangThoiGian(startDate, endDate);
            } else {
                doanhThuData = hoaDonDAO.getDoanhThuTheoKhoangThoiGian(from, to);
            }
            if (doanhThuData == null || doanhThuData.isEmpty()) {
                this.data = new LinkedHashMap<>();
                this.data.put("Không có dữ liệu", 0.0);
            } else {
                this.data = new LinkedHashMap<>(doanhThuData);
            }

            // tooltip-like: show value when hover a bar
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    String label = getLabelAt(e.getX(), e.getY());
                    if (label != null) {
                        Double v = data.get(label);
                        JOptionPane.showMessageDialog(BarChartPanel.this, label + ": " + VN_FORMAT.format(v) + " đ");
                    }
                }
            });
        }

        // helper to detect bar under mouse (simple)
        private String getLabelAt(int mx, int my) {
            int width = getWidth();
            int height = getHeight();
            int n = data.size();
            int availableWidth = width - 2 * padding - labelPadding;
            int barWidth = (n > 0) ? Math.max(1, availableWidth / (n * 2)) : 10;
            int startX = padding + labelPadding;
            int i = 0;
            double max = Collections.max(data.values());
            if (max <= 0) max = 1;
            for (String key : data.keySet()) {
                int x = startX + i * 2 * barWidth;
                int barHeight = (int) ((height - 2 * padding) * (data.get(key) / max));
                int y = height - padding - barHeight;
                Rectangle r = new Rectangle(x, y, barWidth, barHeight);
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

            // draw background grid lines
            g2.setColor(gridColor);
            int gridLines = 5;
            for (int i = 0; i <= gridLines; i++) {
                int y = padding + (height - 2 * padding) * i / gridLines;
                g2.drawLine(padding + labelPadding, y, width - padding, y);
            }

            // axes
            g2.setColor(axisColor);
            g2.drawLine(padding + labelPadding, height - padding, width - padding, height - padding); // x-axis
            g2.drawLine(padding + labelPadding, padding, padding + labelPadding, height - padding); // y-axis

            // bars
            int n = data.size();
            int availableWidth = width - 2 * padding - labelPadding;
            int barWidth = (n > 0) ? Math.max(1, availableWidth / (n * 2)) : 10;
            int startX = padding + labelPadding;
            double max = Collections.max(data.values());
            if (max <= 0) max = 1;

            int i = 0;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                String label = entry.getKey();
                double value = entry.getValue() != null ? entry.getValue() : 0.0;
                int x = startX + i * 2 * barWidth;
                int barHeight = (int) ((height - 2 * padding) * (value / max));
                int y = height - padding - barHeight;

                // bar
                g2.setColor(barColor);
                g2.fillRect(x, y, barWidth, barHeight);

                // value text above bar
                g2.setColor(new Color(60, 60, 60));
                String vtext = VN_FORMAT.format(value) + " đ";
                FontMetrics fm = g2.getFontMetrics();
                int tx = x + (barWidth - fm.stringWidth(vtext)) / 2;
                int ty = Math.max(y - 5, padding);
                g2.drawString(vtext, tx, ty);

                // label below bar (rotated or wrapped)
                String shortLabel = label;
                if (shortLabel.length() > 10) shortLabel = shortLabel.substring(0, 10) + "...";
                int lx = x + (barWidth - g2.getFontMetrics().stringWidth(shortLabel)) / 2;
                int ly = height - padding + g2.getFontMetrics().getAscent() + 5;
                g2.drawString(shortLabel, lx, ly);

                i++;
            }

            g2.dispose();
        }
    }

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

        PieChartPanel(Date from, Date to) {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(560, 360));
            Map<String, Integer> raw = banDAO.getSoBanTheoKhuVuc(from, to);
            if (raw == null || raw.isEmpty()) {
                this.data = new LinkedHashMap<>();
                this.data.put("Không có dữ liệu", 1);
            } else {
                this.data = new LinkedHashMap<>(raw);
                boolean allZero = true;
                for (Integer v : data.values()) if (v != null && v > 0) { allZero = false; break; }
                if (allZero) {
                    this.data.clear();
                    this.data.put("Không có dữ liệu", 1);
                }
            }

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    String label = getSliceAt(e.getX(), e.getY());
                    if (label != null) {
                        Integer v = data.get(label);
                        JOptionPane.showMessageDialog(PieChartPanel.this, label + ": " + (v != null ? v : 0));
                    }
                }
            });
        }

        private String getSliceAt(int mx, int my) {
            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w, h) - 80;
            int cx = w / 2 - 20;
            int cy = h / 2 - 10;
            int rx = size / 2;
            int ry = size / 2;
            double total = 0;
            for (Integer v : data.values()) total += (v != null ? v : 0);
            if (total == 0) return null;
            double start = 0;
            int i = 0;
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                double value = entry.getValue() != null ? entry.getValue() : 0;
                double angle = value / total * 360.0;
                double mid = start + angle / 2.0;
                // compute point angle from center
                double dx = mx - cx;
                double dy = my - cy;
                double mouseAngle = Math.toDegrees(Math.atan2(dy, dx));
                mouseAngle = mouseAngle < 0 ? 360 + mouseAngle : mouseAngle;
                double startAngle = start;
                double endAngle = start + angle;
                if (mouseAngle >= startAngle && mouseAngle <= endAngle) {
                    return entry.getKey();
                }
                start += angle;
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

            int w = getWidth();
            int h = getHeight();
            int size = Math.min(w, h) - 80;
            int cx = w / 2 - 20;
            int cy = h / 2 - 10;
            int rx = size / 2;
            int ry = size / 2;

            double total = 0;
            for (Integer v : data.values()) total += (v != null ? v : 0);
            if (total == 0) total = 1;

            double startAngle = 0;
            int i = 0;
            Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);
            g2.setFont(labelFont);
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                double value = entry.getValue() != null ? entry.getValue() : 0;
                double angle = value / total * 360.0;

                g2.setColor(COLORS[i % COLORS.length]);
                g2.fillArc(cx - rx, cy - ry, rx * 2, ry * 2, (int) Math.round(startAngle), (int) Math.round(angle));

                // draw label (percentage) line
                double mid = Math.toRadians(startAngle + angle / 2.0);
                double lx = cx + Math.cos(mid) * (rx + 10);
                double ly = cy + Math.sin(mid) * (ry + 10);
                String pct = String.format("%.1f%%", (value / total) * 100);
                g2.setColor(new Color(60, 60, 60));
                g2.drawString(pct, (int) lx - 10, (int) ly);

                startAngle += angle;
                i++;
            }

            // legend on right
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

    // Date helpers
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