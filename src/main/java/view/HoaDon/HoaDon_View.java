package view.HoaDon;

import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import entity.HoaDon;
import entity.KhachHang;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

import java.awt.Frame;

public class HoaDon_View extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private KhachHang_DAO khachHangDAO = new KhachHang_DAO(); // Cần DAO này để lấy tên KH

    private JLabel lblChuaTT, lblDaTT, lblTongHD, lblDoanhThu;

    public HoaDon_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(251, 248, 241));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 15, 30));

        JLabel title = new JLabel("Quản lý hoá đơn");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(30, 30, 30));

        JLabel subtitle = new JLabel("Danh sách các hóa đơn chờ thanh toán");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 30, 25, 30));

        lblChuaTT = createStatLabel("0");
        lblDaTT = createStatLabel("0");
        lblTongHD = createStatLabel("0");
        lblDoanhThu = createStatLabel("0 VNĐ");

        statsPanel.add(createStatBox(lblChuaTT, "Chưa thanh toán", new Color(244, 67, 54)));
        statsPanel.add(createStatBox(lblDaTT, "Đã thanh toán", new Color(76, 175, 80)));
        statsPanel.add(createStatBox(lblTongHD, "Tổng hóa đơn", new Color(34, 139, 230)));
        statsPanel.add(createStatBox(lblDoanhThu, "Doanh thu", new Color(255, 152, 0)));

        // GIẢI QUYẾT CONFLICT 1: Giữ phiên bản của HEAD (có cột "Thanh Toán")
        String[] cols = {"Mã HD", "Khách hàng", "Ngày lập", "Tổng tiền", "Phương thức", "Trạng thái", "Thanh Toán", "Sửa", "Xóa"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Điều chỉnh chỉ số cột cho đúng với mảng cols (Thanh Toán = 6, Sửa = 7, Xóa = 8)
                return column == 6 || column == 7 || column == 8;
            }
        };

        table = new JTable(model);
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setGridColor(new Color(230, 230, 230));

        JTableHeader header2 = table.getTableHeader();
        header2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header2.setBackground(new Color(248, 249, 250));
        header2.setForeground(new Color(60, 60, 60));
        header2.setPreferredSize(new Dimension(header2.getWidth(), 45));

        // Code này (từ HEAD) khớp với mảng cols đã chọn
        table.getColumn("Thanh Toán").setCellRenderer(new ButtonRenderer("Thanh Toán", new Color(76, 175, 80), Color.WHITE));
        table.getColumn("Thanh Toán").setCellEditor(new ButtonEditor(new JCheckBox(), "Thanh Toán"));
        table.getColumn("Sửa").setCellRenderer(new ButtonRenderer("Sửa", new Color(255, 152, 0), Color.WHITE));
        table.getColumn("Sửa").setCellEditor(new ButtonEditor(new JCheckBox(), "Sửa"));
        table.getColumn("Xóa").setCellRenderer(new ButtonRenderer("Xóa", new Color(244, 67, 54), Color.WHITE));
        table.getColumn("Xóa").setCellEditor(new ButtonEditor(new JCheckBox(), "Xóa"));

        // Điều chỉnh kích thước cột cho 3 nút
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Thanh Toán
        table.getColumnModel().getColumn(6).setMaxWidth(100);
        table.getColumnModel().getColumn(7).setPreferredWidth(80);  // Sửa
        table.getColumnModel().getColumn(7).setMaxWidth(80);
        table.getColumnModel().getColumn(8).setPreferredWidth(80);  // Xóa
        table.getColumnModel().getColumn(8).setMaxWidth(80);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);

        JPanel tableWrapper = new RoundedPanel(15, Color.WHITE);
        tableWrapper.setLayout(new BorderLayout());
        tableWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        tableWrapper.add(scroll, BorderLayout.CENTER);

        JLabel lblTableTitle = new JLabel("Danh sách hóa đơn chờ thanh toán");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTableTitle.setForeground(new Color(30, 30, 30));
        lblTableTitle.setBorder(new EmptyBorder(0, 30, 15, 0));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        tablePanel.add(lblTableTitle, BorderLayout.NORTH);
        tablePanel.add(tableWrapper, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(statsPanel, BorderLayout.NORTH);
        content.add(tablePanel, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);

        loadHoaDonData();
        loadThongKe();
    }

    private void loadHoaDonData() {
        model.setRowCount(0);
        List<HoaDon> dsHD = hoaDonDAO.getAllHoaDon();
        for (HoaDon hd : dsHD) {
            // GIẢI QUYẾT CONFLICT 2: Giữ logic của HEAD (chỉ tải HĐ chưa thanh toán)
            if (!hd.isDaThanhToan()) {
                KhachHang kh = null;
                // Lấy thông tin KH đầy đủ nếu có mã KH hợp lệ
                if (hd.getKhachHang() != null && hd.getKhachHang().getMaKhachHang() != null) {
                    kh = khachHangDAO.getKhachHangById(hd.getKhachHang().getMaKhachHang());
                }
                String tenKH = (kh != null) ? kh.getTenKhachHang() : "Khách lẻ";
                double tongTien = hoaDonDAO.tinhTongTienHoaDon(hd.getMaHoaDon());
                String trangThai = "Chưa thanh toán";
                model.addRow(new Object[]{
                        hd.getMaHoaDon(), tenKH, hd.getNgayLap(),
                        // Lấy format tiền tốt hơn từ 'main'
                        String.format("%,.0f VNĐ", tongTien), hd.getPhuongThuc(), trangThai,
                        "Thanh Toán", "Sửa", "Xóa"
                });
            }
        }
    }

    private void loadThongKe() {
        List<HoaDon> dsHD = hoaDonDAO.getAllHoaDon();
        int chuaTT = 0, daTT = 0;
        double doanhThu = 0;
        for (HoaDon hd : dsHD) {
            double tongTien = hoaDonDAO.tinhTongTienHoaDon(hd.getMaHoaDon()); // Tính lại tổng tiền đã sửa lỗi
            if (hd.isDaThanhToan()) { daTT++; doanhThu += tongTien; }
            else { chuaTT++; }
        }
        lblChuaTT.setText(String.valueOf(chuaTT));
        lblDaTT.setText(String.valueOf(daTT));
        // GIẢI QUYẾT CONFLICT 3: Giữ phiên bản của HEAD (logic đúng và format đơn giản)
        lblTongHD.setText(String.valueOf(dsHD.size()));
        lblDoanhThu.setText(String.format("%,.0f VNĐ", doanhThu)); // Format tiền tệ
    }

    // Các hàm helper và class nội bên dưới được giữ nguyên (chúng không bị conflict)

    private JButton createRoundedButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isPressed() ? bg.darker() : getModel().isRollover() ? bg.brighter() : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel createStatLabel(String text) {
        JLabel l = new JLabel(text, JLabel.LEFT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 32));
        l.setForeground(Color.WHITE);
        return l;
    }

    private JPanel createStatBox(JLabel valueLabel, String label, Color accent) {
        JPanel box = new RoundedPanel(15, accent);
        box.setLayout(new BorderLayout());
        box.setBorder(new EmptyBorder(20, 22, 20, 22));
        JLabel textLabel = new JLabel(label, JLabel.LEFT);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(Color.WHITE);
        JPanel inner = new JPanel(new GridLayout(2, 1, 0, 5));
        inner.setOpaque(false);
        inner.add(valueLabel);
        inner.add(textLabel);
        box.add(inner, BorderLayout.CENTER);
        return box;
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {
        private final Color bgColor;
        private final Color fgColor;
        public ButtonRenderer(String text, Color bg, Color fg) {
            setText(text); this.bgColor = bg; this.fgColor = fg; setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12)); setFocusPainted(false);
            setBorderPainted(false); setContentAreaFilled(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor); g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 8, 8);
            g2.dispose(); super.paintComponent(g);
        }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setForeground(fgColor); return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private final String type;
        public ButtonEditor(JCheckBox checkBox, String type) {
            super(checkBox); this.type = type; button = new JButton(); button.setOpaque(false);
            button.setContentAreaFilled(false); button.setBorderPainted(false); button.setFocusPainted(false);
            button.addActionListener(e -> fireEditingStopped());
        }
        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            button.setText(type); button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setForeground(Color.WHITE); button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            isPushed = true; return button;
        }
        @Override public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow < 0 || selectedRow >= table.getRowCount()) { isPushed = false; return type; }
                String maHD = table.getValueAt(selectedRow, 0).toString();

                if (type.equals("Thanh Toán")) {
                    SwingUtilities.invokeLater(() -> {
                        HoaDon hd = hoaDonDAO.findByMaHD(maHD);
                        if (hd != null) {
                            KhachHang kh = null;
                            if (hd.getKhachHang() != null && hd.getKhachHang().getMaKhachHang() != null) {
                                kh = khachHangDAO.getKhachHangById(hd.getKhachHang().getMaKhachHang());
                            }
                            hd.setKhachHang(kh); // Gán KH đầy đủ (hoặc null nếu là khách lẻ)
                            double tongTien = hoaDonDAO.tinhTongTienHoaDon(maHD);
                            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(HoaDon_View.this);
                            HoaDon_ThanhToan_Dialog dialog = new HoaDon_ThanhToan_Dialog(parentFrame, hd, hoaDonDAO, tongTien);
                            dialog.setVisible(true);
                            loadHoaDonData(); loadThongKe();
                        }
                    });
                } else if (type.equals("Sửa")) {
                    SwingUtilities.invokeLater(() -> {
                        HoaDon hd = hoaDonDAO.findByMaHD(maHD);
                        if (hd != null) {
                            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(HoaDon_View.this);
                            HoaDon_EditDialog dialog = new HoaDon_EditDialog(parentFrame, hd, hoaDonDAO);
                            dialog.setVisible(true);
                            loadHoaDonData(); loadThongKe();
                        }
                    });
                } else if (type.equals("Xóa")) {
                    int confirm = JOptionPane.showConfirmDialog(table, "Bạn chắc chắn muốn xóa hóa đơn này?",
                            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (hoaDonDAO.deleteHoaDon(maHD)) {
                            ((DefaultTableModel) table.getModel()).removeRow(selectedRow); loadThongKe();
                        } else { JOptionPane.showMessageDialog(table, "Xóa thất bại!"); }
                    }
                }
            }
            isPushed = false; return type;
        }
    }

    class RoundedPanel extends JPanel {
        private final int cornerRadius; private final Color bgColor;
        public RoundedPanel(int radius, Color color) { cornerRadius = radius; bgColor = color; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor); g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            g2.dispose();
        }
    }
}