package view.HoaDon;

import dao.HoaDon_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

public class HoaDon_ChiTietHoaDon_View extends JDialog {

    private JTable tableChiTiet;
    private DefaultTableModel modelChiTiet;
    private HoaDon_DAO hoaDonDAO;

    public HoaDon_ChiTietHoaDon_View(Frame owner, HoaDon hoaDon) {
        super(owner, "Chi tiết Hóa đơn: " + hoaDon.getMaHoaDon(), true);
        this.hoaDonDAO = new HoaDon_DAO();

        setSize(550, 450);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238));

        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        infoPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        infoPanel.setOpaque(false);
        infoPanel.add(new JLabel("Mã Hóa Đơn:"));
        infoPanel.add(new JLabel(hoaDon.getMaHoaDon()));
        infoPanel.add(new JLabel("Khách Hàng:"));
        infoPanel.add(new JLabel(hoaDon.getKhachHang() != null && hoaDon.getKhachHang().getTenKhachHang() != null ? hoaDon.getKhachHang().getTenKhachHang() : "Khách lẻ"));
        infoPanel.add(new JLabel("Ngày Lập:"));
        infoPanel.add(new JLabel(hoaDon.getNgayLap().toString()));
        add(infoPanel, BorderLayout.NORTH);

        String[] columnNames = {"STT", "Tên Món Ăn", "Số Lượng", "Đơn Giá", "Thành Tiền"};
        modelChiTiet = new DefaultTableModel(columnNames, 0) {
             @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableChiTiet = new JTable(modelChiTiet);
        tableChiTiet.setRowHeight(30);
        tableChiTiet.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tableChiTiet.setFillsViewportHeight(true);

        tableChiTiet.getColumnModel().getColumn(0).setPreferredWidth(40);
        tableChiTiet.getColumnModel().getColumn(0).setMaxWidth(40);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tableChiTiet.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tableChiTiet.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        tableChiTiet.getColumnModel().getColumn(2).setPreferredWidth(80);

        DecimalFormat moneyFormat = new DecimalFormat("###,### VNĐ");
        tableChiTiet.getColumnModel().getColumn(3).setCellRenderer(new CurrencyRenderer(moneyFormat));
        tableChiTiet.getColumnModel().getColumn(3).setPreferredWidth(120);
        tableChiTiet.getColumnModel().getColumn(4).setCellRenderer(new CurrencyRenderer(moneyFormat));
        tableChiTiet.getColumnModel().getColumn(4).setPreferredWidth(120);

        JScrollPane scrollPane = new JScrollPane(tableChiTiet);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(new EmptyBorder(5, 0, 10, 20));
        buttonPanel.setOpaque(false);
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());
        buttonPanel.add(btnClose);
        add(buttonPanel, BorderLayout.SOUTH);

        loadChiTietData(hoaDon.getMaHoaDon());
    }

    private void loadChiTietData(String maHoaDon) {
        modelChiTiet.setRowCount(0);
        List<ChiTietHoaDon> chiTietList = hoaDonDAO.getChiTietHoaDonForPrint(maHoaDon);
        int stt = 1;
        for (ChiTietHoaDon ct : chiTietList) {
            modelChiTiet.addRow(new Object[]{
                    stt++,
                    ct.getMonAn() != null ? ct.getMonAn().getTenMonAn() : "N/A",
                    ct.getSoLuong(),
                    ct.getDonGia(),
                    ct.tinhThanhTien()
            });
        }
    }

    static class CurrencyRenderer extends DefaultTableCellRenderer {
        private DecimalFormat formatter;
        public CurrencyRenderer(DecimalFormat formatter) {
            this.formatter = formatter; setHorizontalAlignment(JLabel.RIGHT);
        }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value instanceof Number) { setText(formatter.format(value)); }
            else { setText(""); }
            return cell;
        }
    }
}