package view.NhanVien;

import dao.Ban_DAO;
import dao.HoaDon_DAO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.ui.RectangleInsets;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;

public class NhanVien_ThongKe_View extends JPanel {

    private HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private Ban_DAO banDAO = new Ban_DAO();

    public NhanVien_ThongKe_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(250, 248, 243));

        JPanel topPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        topPanel.setBackground(new Color(250, 248, 243));
        topPanel.setBorder(new EmptyBorder(20, 20, 10, 20));

        double tongDoanhThu = hoaDonDAO.getTongDoanhThu();
        int tongDonHang = hoaDonDAO.getTongDonHang();
        int tongKhach = hoaDonDAO.getSoLuongKhachHang();
        double trungBinhDon = tongDonHang > 0 ? tongDoanhThu / tongDonHang : 0;

        topPanel.add(createStatCard("Tổng doanh thu", String.format("%,.0fđ", tongDoanhThu), "", new Color(255, 140, 0)));
        topPanel.add(createStatCard("Tổng đơn hàng", String.valueOf(tongDonHang), "", new Color(76, 175, 80)));
        topPanel.add(createStatCard("Giá trị TB/đơn", String.format("%,.0fđ", trungBinhDon), "", new Color(33, 150, 243)));
        topPanel.add(createStatCard("Khách hàng", String.valueOf(tongKhach), "", new Color(0, 200, 83)));

        add(topPanel, BorderLayout.NORTH);

        // ====== Panel chứa biểu đồ ======
        JPanel chartPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        chartPanel.setBackground(new Color(250, 248, 243));
        chartPanel.setBorder(new EmptyBorder(10, 20, 20, 20));

        chartPanel.add(createBarChartPanel());
        chartPanel.add(createPieChartPanel());

        add(chartPanel, BorderLayout.CENTER);
    }

    // ====== Hàm tạo thẻ thống kê ======
    private JPanel createStatCard(String title, String value, String percent, Color accent) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(10, 15, 10, 15)
        ));
        card.setPreferredSize(new Dimension(200, 100));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValue.setForeground(accent);

        JLabel lblPercent = new JLabel(percent);
        lblPercent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblPercent.setForeground(percent.startsWith("-") ? Color.RED : new Color(0, 150, 0));

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(Color.WHITE);
        top.add(lblTitle, BorderLayout.WEST);
        top.add(lblPercent, BorderLayout.EAST);

        card.add(top, BorderLayout.NORTH);
        card.add(lblValue, BorderLayout.CENTER);

        return card;
    }

    // ====== Biểu đồ cột doanh thu theo ngày ======
    private JPanel createBarChartPanel() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 1; i <= 7; i++) {
            double doanhThu = hoaDonDAO.getDoanhThuTheoNgay(i);
            dataset.addValue(doanhThu, "Doanh thu", "T" + i);
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Doanh thu theo ngày", "Ngày", "Triệu VNĐ", dataset,
                PlotOrientation.VERTICAL, false, true, false
        );

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.setInsets(new RectangleInsets(10, 10, 10, 10));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(33, 150, 243));
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(new Font("Segoe UI", Font.PLAIN, 12));

        ChartPanel panel = new ChartPanel(barChart);
        panel.setPreferredSize(new Dimension(400, 250));
        return panel;
    }

    // ====== Biểu đồ tròn khu vực ======
    private JPanel createPieChartPanel() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Integer> map = banDAO.getSoBanTheoKhuVuc();

        for (String khuVuc : map.keySet()) {
            dataset.setValue(khuVuc, map.get(khuVuc));
        }

        JFreeChart pieChart = ChartFactory.createPieChart(
                "Phân bố đặt bàn theo khu vực", dataset, true, true, false
        );

        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0} ({2})", new DecimalFormat("0"), new DecimalFormat("0.0%")
        ));
        plot.setSimpleLabels(true);
        plot.setLabelFont(new Font("Segoe UI", Font.BOLD, 12));
        plot.setBackgroundPaint(new Color(250, 248, 243));
        plot.setOutlineVisible(false);

        ChartPanel panel = new ChartPanel(pieChart);
        panel.setPreferredSize(new Dimension(400, 250));
        return panel;
    }
}
