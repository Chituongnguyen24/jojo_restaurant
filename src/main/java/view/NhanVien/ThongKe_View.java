package view.NhanVien;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension; // Thêm DAO cho món ăn
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.EmptyBorder;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import dao.Ban_DAO;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;

public class ThongKe_View extends JPanel {

    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private final Ban_DAO banDAO = new Ban_DAO();
    private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    private final ChiTietHoaDon_DAO chiTietHoaDonDAO = new ChiTietHoaDon_DAO(); // Khởi tạo DAO

    private JDateChooser dateFrom;
    private JDateChooser dateTo;

    private JComboBox<String> cboLoaiThongKe;
    private JButton btnApDung;
    private JButton btnHienTai;

    private JPanel topPanel;
    private JPanel chartPanel;
    private JPanel bottomPanel; // Panel mới cho 2 thống kê bổ sung
    private JPanel contentPanel;

    private static final DecimalFormat VN_FORMAT = new DecimalFormat("#,###");

    public ThongKe_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 248, 243));

        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.NORTH);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(250, 248, 243));
        
        // Thêm ScrollPane để không bị đè
        contentPanel.setBorder(new EmptyBorder(0, 0, 0, 100));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        add(scrollPane, BorderLayout.CENTER);

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
        cboLoaiThongKe.setSelectedIndex(0);
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
        btnApDung.setEnabled(false);
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
            setDateChooserEnabled(dateFrom, false);
            setDateChooserEnabled(dateTo, false);
            btnApDung.setEnabled(false);
            loadStatistics(null, null);
        });
        panel.add(btnHienTai);

        SwingUtilities.invokeLater(() -> {
            setDateChooserEnabled(dateFrom, false);
            setDateChooserEnabled(dateTo, false);
        });

        return panel;
    }

    private void setDateChooserEnabled(JDateChooser chooser, boolean enabled) {
        if (chooser == null) return;
        
        chooser.setEnabled(enabled);
        
        try {
            JTextFieldDateEditor ed = (JTextFieldDateEditor) chooser.getDateEditor();
            ed.setEditable(enabled);
            ed.setFocusable(enabled);
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
                return;
            case "Tùy chọn":
                dateFrom.setDate(null);
                dateTo.setDate(new Date()); 
                return;
        }
        
        dateFrom.setDate(startDate);
        dateTo.setDate(endDate); 

        loadStatistics(startDate, endDate);
    }

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

        from = getStartOfDay(from);
        to = getEndOfDay(to);
        
        if (from.after(to)) {
            JOptionPane.showMessageDialog(this,
                    "Ngày bắt đầu phải trước hoặc bằng ngày kết thúc!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        loadStatistics(from, to);
    }

    private void loadStatistics(Date from, Date to) {
        contentPanel.removeAll();

        // Wrapper panel chứa tất cả nội dung với preferred size để scroll hoạt động
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(250, 248, 243));

        // Top Panel - 4 thẻ thống kê tổng quan
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

        wrapper.add(topPanel, BorderLayout.NORTH);

        // Center panel chứa 2 hàng biểu đồ
        JPanel centerWrapper = new JPanel();
        centerWrapper.setLayout(new BoxLayout(centerWrapper, BoxLayout.Y_AXIS));
        centerWrapper.setBackground(new Color(250, 248, 243));

        // Chart Panel - 2 biểu đồ đầu (Line + Pie)
        chartPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        chartPanel.setBackground(new Color(250, 248, 243));
        chartPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        chartPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 420));

        chartPanel.add(createLineChartPanel(from, to));
        chartPanel.add(createPieChartPanel(from, to));

        centerWrapper.add(chartPanel);

        // Bottom Panel - 2 biểu đồ cột ngang
        bottomPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        bottomPanel.setBackground(new Color(250, 248, 243));
        bottomPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        bottomPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 470));

        bottomPanel.add(createTopCustomersPanel(from, to));
        bottomPanel.add(createBestSellingDishesPanel(from, to));

        centerWrapper.add(bottomPanel);

        wrapper.add(centerWrapper, BorderLayout.CENTER);

        contentPanel.add(wrapper, BorderLayout.CENTER);

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
    // Inner class: HorizontalBarChartPanel - Biểu đồ cột ngang cho Top 10
    // -------------------------
    private class HorizontalBarChartPanel extends JPanel {
        private final Map<String, Double> data;
        private final String title;
        private final Color barColor;
        private final int padding = 40;
        private final int labelWidth = 150;
        private final int titleHeight = 40;
        private final Color gridColor = new Color(240, 240, 240);

        HorizontalBarChartPanel(Date from, Date to, String title, Color barColor, boolean isCustomer) {
            this.title = title;
            this.barColor = barColor;
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(600, 400));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(230, 230, 230)),
                    new EmptyBorder(15, 15, 15, 15)
            ));

            // Lấy dữ liệu từ DAO
            Map<String, Double> rawData = new LinkedHashMap<>();
            
            if (isCustomer) {
                // Top 10 Khách hàng
                List<Object[]> topCustomers = khachHangDAO.getTop10KhachHang(from, to);
                if (topCustomers != null && !topCustomers.isEmpty()) {
                    for (Object[] customer : topCustomers) {
                        String tenKH = customer[0] != null ? customer[0].toString() : "N/A";
                        Double tongTien = customer[2] != null ? (Double) customer[2] : 0.0;
                        rawData.put(tenKH, tongTien);
                    }
                }
            } else {
                // Top 10 Món bán chạy
                List<Object[]> topDishes = chiTietHoaDonDAO.getTop10MonBanChay(from, to);
                if (topDishes != null && !topDishes.isEmpty()) {
                    topDishes.sort(new Comparator<Object[]>() {
                        @Override
                        public int compare(Object[] o1, Object[] o2) {
                            Double doanhThu1 = (o1[2] != null) ? (Double) o1[2] : 0.0;
                            Double doanhThu2 = (o2[2] != null) ? (Double) o2[2] : 0.0;
                            return doanhThu2.compareTo(doanhThu1); // Sắp xếp giảm dần
                        }
                    });
              // Sắp xếp lại thứ tự các món theo doanh thu giảm dần
                    for (Object[] dish : topDishes) {
                        String tenMon = dish[0] != null ? dish[0].toString() : "N/A";
                        Double doanhThu = dish[2] != null ? (Double) dish[2] : 0.0;
                        rawData.put(tenMon, doanhThu);
                    }
                }
            }

            if (rawData.isEmpty()) {
                this.data = new LinkedHashMap<>();
                this.data.put("Không có dữ liệu", 0.0);
            } else {
                this.data = rawData;
            }

            ToolTipManager.sharedInstance().registerComponent(this);

            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    String key = getBarAt(e.getX(), e.getY());
                    if (key != null) {
                        Double v = data.get(key);
                        setToolTipText(key + ": " + VN_FORMAT.format(v) + " đ");
                    } else {
                        setToolTipText(null);
                    }
                }
            });
        }

        private String getBarAt(int mx, int my) {
            int width = getWidth();
            int height = getHeight();
            int n = data.size();
            if (n == 0) return null;

            int availableHeight = height - titleHeight - 2 * padding;
            int barHeight = Math.max(20, availableHeight / n - 5);
            double max = Collections.max(data.values());
            if (max <= 0) max = 1;

            int i = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                int y = titleHeight + padding + i * (barHeight + 5);
                int barWidth = (int) ((width - labelWidth - 2 * padding) * (entry.getValue() / max));
                Rectangle r = new Rectangle(labelWidth + padding, y, barWidth, barHeight);
                if (r.contains(mx, my)) return entry.getKey();
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
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Vẽ tiêu đề
            g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
            FontMetrics tfm = g2.getFontMetrics();
            int tx = (width - tfm.stringWidth(title)) / 2;
            int ty = (titleHeight + tfm.getAscent()) / 2;
            g2.setColor(new Color(60, 60, 60));
            g2.drawString(title, tx, ty);

            int n = data.size();
            if (n == 0) return;

            int availableHeight = height - titleHeight - 2 * padding;
            int barHeight = Math.max(20, availableHeight / n - 5);
            double max = Collections.max(data.values());
            if (max <= 0) max = 1;

            // Vẽ lưới dọc
            g2.setColor(gridColor);
            int gridLines = 5;
            for (int i = 0; i <= gridLines; i++) {
                int x = labelWidth + padding + (width - labelWidth - 2 * padding) * i / gridLines;
                g2.drawLine(x, titleHeight + padding, x, height - padding);
            }

            // Vẽ các thanh ngang
            int i = 0;
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            FontMetrics fm = g2.getFontMetrics();

            for (Map.Entry<String, Double> entry : data.entrySet()) {
                String label = entry.getKey();
                if (label == null || label.trim().isEmpty()) label = "N/A";
                double value = entry.getValue() != null ? entry.getValue() : 0.0;

                int y = titleHeight + padding + i * (barHeight + 5);
                int barWidth = (int) ((width - labelWidth - 2 * padding) * (value / max));

                // Vẽ nhãn bên trái
                g2.setColor(new Color(80, 80, 80));
                String shortLabel = label.length() > 20 ? label.substring(0, 17) + "..." : label;
                int rank = i + 1;
                String rankLabel = rank + ". " + shortLabel;
                g2.drawString(rankLabel, padding, y + barHeight / 2 + fm.getAscent() / 2 - 2);

                // Vẽ thanh
                g2.setColor(barColor);
                g2.fillRoundRect(labelWidth + padding, y, Math.max(barWidth, 5), barHeight, 8, 8);

                // Vẽ viền thanh
                g2.setColor(barColor.darker());
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(labelWidth + padding, y, Math.max(barWidth, 5), barHeight, 8, 8);

                // Vẽ giá trị bên trong/ngoài thanh
                String valueText = VN_FORMAT.format(value);
                int valueWidth = fm.stringWidth(valueText);
                g2.setColor(Color.WHITE);
                
                if (barWidth > valueWidth + 20) {
                    // Vẽ trong thanh nếu đủ chỗ
                    g2.drawString(valueText, labelWidth + padding + barWidth - valueWidth - 10, 
                                 y + barHeight / 2 + fm.getAscent() / 2 - 2);
                } else {
                    // Vẽ ngoài thanh
                    g2.setColor(new Color(100, 100, 100));
                    g2.drawString(valueText, labelWidth + padding + barWidth + 10, 
                                 y + barHeight / 2 + fm.getAscent() / 2 - 2);
                }

                i++;
            }

            g2.dispose();
        }
    }

    // -------------------------
    // Panel: Top 10 Khách hàng tiềm năng - BIỂU ĐỒ NGANG
    // -------------------------
    private JPanel createTopCustomersPanel(Date from, Date to) {
        return new HorizontalBarChartPanel(from, to, "TOP 10 KHÁCH HÀNG TIỀM NĂNG", 
                                          new Color(52, 152, 219), true);
    }

    // -------------------------
    // Panel: Top 10 Món bán chạy - BIỂU ĐỒ NGANG
    // -------------------------
    private JPanel createBestSellingDishesPanel(Date from, Date to) {
        return new HorizontalBarChartPanel(from, to, "TOP 10 MÓN BÁN CHẠY", 
                                          new Color(46, 204, 113), false);
    }

    // -------------------------
    // Method: Tạo Line Chart Panel
    // -------------------------
    private JPanel createLineChartPanel(Date from, Date to) {
        return new LineChartPanel(from, to);
    }

    // -------------------------
    // Method: Tạo Pie Chart Panel
    // -------------------------
    private JPanel createPieChartPanel(Date from, Date to) {
        return new PieChartPanel(from, to);
    }

    // -------------------------
    // Inner class: LineChartPanel (Java2D) - Biểu đồ đường tăng trưởng doanh thu
    // -------------------------
    private class LineChartPanel extends JPanel {
        private final Map<String, Double> data;
        private final int padding = 40;
        private final int labelPadding = 40;
        private final Color axisColor = new Color(150, 150, 150);
        private final Color lineColor = new Color(52, 152, 219);
        private final Color pointColor = new Color(41, 128, 185);
        private final Color areaColor = new Color(52, 152, 219, 40);
        private final Color gridColor = new Color(220, 220, 220);
        private final int titleHeight = 32;
        private final int pointRadius = 5;

        LineChartPanel(Date from, Date to) {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(700, 360));
            setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
            
            Map<String, Double> doanhThuData;
            if (from == null && to == null) {
                doanhThuData = hoaDonDAO.getDoanhThuTheoKhoangThoiGian(
                    getStartOfDay(getPreviousDate(6)), 
                    getEndOfDay(new Date())
                );
            } else {
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
                    String key = getPointAt(e.getX(), e.getY());
                    if (key != null) {
                        Double v = data.get(key);
                        setToolTipText(key + ": " + VN_FORMAT.format(v) + " đ");
                    } else {
                        setToolTipText(null);
                    }
                }
            });
        }

        private String getPointAt(int mx, int my) {
            int width = getWidth();
            int height = getHeight();
            int n = data.size();
            if (n == 0) return null;
            
            int availableWidth = width - 2 * padding - labelPadding;
            double max = Collections.max(data.values());
            if (max <= 0) max = 1;
            
            int i = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                int x = padding + labelPadding + (i * availableWidth) / Math.max(1, n - 1);
                if (n == 1) x = padding + labelPadding + availableWidth / 2;
                
                double value = entry.getValue() != null ? entry.getValue() : 0.0;
                int y = height - padding - (int) ((height - 2 * padding - titleHeight) * (value / max));
                
                if (Math.abs(mx - x) <= pointRadius + 3 && Math.abs(my - y) <= pointRadius + 3) {
                    return entry.getKey();
                }
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
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            int width = getWidth();
            int height = getHeight();

            String title = "Xu hướng tăng trưởng doanh thu";
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
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(padding + labelPadding, height - padding, width - padding, height - padding);
            g2.drawLine(padding + labelPadding, titleHeight + padding, padding + labelPadding, height - padding);

            int n = data.size();
            if (n == 0) return;
            
            int availableWidth = width - 2 * padding - labelPadding;
            double max = Collections.max(data.values());
            if (max <= 0) max = 1;

            // Tính toán các điểm
            int[] xPoints = new int[n];
            int[] yPoints = new int[n];
            int i = 0;
            
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                double value = entry.getValue() != null ? entry.getValue() : 0.0;
                
                if (n == 1) {
                    xPoints[i] = padding + labelPadding + availableWidth / 2;
                } else {
                    xPoints[i] = padding + labelPadding + (i * availableWidth) / (n - 1);
                }
                
                yPoints[i] = height - padding - (int) ((height - 2 * padding - titleHeight) * (value / max));
                i++;
            }

            // Vẽ vùng dưới đường
            if (n > 1) {
                int[] areaXPoints = new int[n + 2];
                int[] areaYPoints = new int[n + 2];
                
                System.arraycopy(xPoints, 0, areaXPoints, 0, n);
                System.arraycopy(yPoints, 0, areaYPoints, 0, n);
                
                areaXPoints[n] = xPoints[n - 1];
                areaYPoints[n] = height - padding;
                areaXPoints[n + 1] = xPoints[0];
                areaYPoints[n + 1] = height - padding;
                
                g2.setColor(areaColor);
                g2.fillPolygon(areaXPoints, areaYPoints, n + 2);
            }

            // Vẽ đường nối
            g2.setColor(lineColor);
            g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            for (i = 0; i < n - 1; i++) {
                g2.drawLine(xPoints[i], yPoints[i], xPoints[i + 1], yPoints[i + 1]);
            }

            // Vẽ các điểm dữ liệu
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            i = 0;
            for (Map.Entry<String, Double> entry : data.entrySet()) {
                String label = entry.getKey();
                if (label == null || label.trim().isEmpty()) label = "(Chưa có tên)";
                double value = entry.getValue() != null ? entry.getValue() : 0.0;

                // Vẽ điểm với viền
                g2.setColor(Color.WHITE);
                g2.fillOval(xPoints[i] - pointRadius - 1, yPoints[i] - pointRadius - 1, 
                           (pointRadius + 1) * 2, (pointRadius + 1) * 2);
                g2.setColor(pointColor);
                g2.fillOval(xPoints[i] - pointRadius, yPoints[i] - pointRadius, 
                           pointRadius * 2, pointRadius * 2);

                // Vẽ giá trị
                String vtext = VN_FORMAT.format(value) + " đ";
                FontMetrics fm = g2.getFontMetrics();
                int vx = xPoints[i] - fm.stringWidth(vtext) / 2;
                int vy = Math.max(yPoints[i] - 12, titleHeight + padding + fm.getAscent());
                
                g2.setColor(new Color(60, 60, 60));
                g2.drawString(vtext, vx, vy);

                // Vẽ nhãn
                String shortLabel = label.length() > 12 ? label.substring(0, 12) + "..." : label;
                int lx = xPoints[i] - g2.getFontMetrics().stringWidth(shortLabel) / 2;
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
        private final int titleHeight = 32;

        PieChartPanel(Date from, Date to) {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(560, 360));
            setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
            
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

            int pieSize = Math.min(w, h) - 160; 
      
            int pieX = (w - pieSize) / 2 - 100; 
            
            int pieY = (h - pieSize) / 2 + titleHeight / 2; 
            
            int arcWidth = pieSize;
            int arcHeight = pieSize;
            
            int cx = pieX + arcWidth / 2; 
            int cy = pieY + arcHeight / 2; 
            
            int rx = arcWidth / 2;
            int ry = arcHeight / 2;
            
            String title = "THỐNG KÊ ĐẶT BÀN THEO KHU VỰC";
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
                g2.fillArc(pieX, pieY, arcWidth, arcHeight, (int) Math.round(startAngle), (int) Math.round(angle));

                double mid = Math.toRadians(startAngle + angle / 2.0);
                double lx = cx + Math.cos(mid) * (rx + 12); 
                double ly = cy + Math.sin(mid) * (ry + 12); 
                String pct = String.format("%.1f%%", (value / total) * 100);
                g2.setColor(new Color(60, 60, 60));
                g2.drawString(pct, (int) lx - 10, (int) ly);

                startAngle += angle;
                i++;
            }
            int legendX = cx + rx + 60; 
            int legendY = cy - (data.size() * 22) / 2; 
            
            int idx = 0;
            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                Color c = COLORS[idx % COLORS.length];
                g2.setColor(c);
                g2.fillRect(legendX, legendY + idx * 22, 14, 14);
                g2.setColor(new Color(60, 60, 60));
                String label = entry.getKey() + " (" + entry.getValue() + ")";
                
                String shortLabel = label.length() > 25 ? label.substring(0, 22) + "..." : label;
                
                g2.drawString(shortLabel, legendX + 18, legendY + 12 + idx * 22);
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