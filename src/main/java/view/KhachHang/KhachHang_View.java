package view.KhachHang;

import dao.KhachHang_DAO;
import entity.KhachHang;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class KhachHang_View extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();

    private JLabel lblTongKH, lblDong, lblVang, lblBac;

    public KhachHang_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(251, 248, 241)); // Nền màu be #FBF8F1

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 15, 30));

        JLabel title = new JLabel("Quản lý khách hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(30, 30, 30));

        JLabel subtitle = new JLabel("Quản lý thông tin khách hàng");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JButton btnAdd = createRoundedButton("+ Thêm khách hàng mới", new Color(76, 175, 80), Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setPreferredSize(new Dimension(200, 45));

        btnAdd.addActionListener(e -> {
            KhachHang_AddDialog dialog = new KhachHang_AddDialog(null, khachHangDAO);
            dialog.setVisible(true);
            loadKhachHangData();
            loadThongKe();
        });

        header.add(titlePanel, BorderLayout.WEST);
        header.add(btnAdd, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // ===== STATS =====
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 30, 25, 30));

        lblTongKH = createStatLabel("0");
        lblDong = createStatLabel("0");
        lblVang = createStatLabel("0");
        lblBac = createStatLabel("0");

        statsPanel.add(createStatBox(lblTongKH, "Tổng KH", new Color(34, 139, 230)));
        statsPanel.add(createStatBox(lblDong, "Đồng", new Color(76, 175, 80)));
        statsPanel.add(createStatBox(lblVang, "Vàng", new Color(255, 152, 0)));
        statsPanel.add(createStatBox(lblBac, "Bạc", new Color(156, 39, 176)));

        // ===== TABLE =====
        String[] cols = {"Mã KH", "Thông tin", "Liên hệ", "Email", "Điểm tích lũy", "Hạng", "Trạng thái", "Sửa", "Xóa"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8 || column == 9;
            }
        };

        table = new JTable(model);
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setGridColor(new Color(230, 230, 230));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        JTableHeader header2 = table.getTableHeader();
        header2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header2.setBackground(new Color(248, 249, 250));
        header2.setForeground(new Color(60, 60, 60));
        header2.setPreferredSize(new Dimension(header2.getWidth(), 45));
        header2.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)));

        // Renderer cho 2 nút với màu đậm hơn
        table.getColumn("Sửa").setCellRenderer(new ButtonRenderer("Sửa", new Color(255, 152, 0), Color.WHITE));
        table.getColumn("Sửa").setCellEditor(new ButtonEditor(new JCheckBox(), "Sửa"));
        table.getColumn("Xóa").setCellRenderer(new ButtonRenderer("Xóa", new Color(244, 67, 54), Color.WHITE));
        table.getColumn("Xóa").setCellEditor(new ButtonEditor(new JCheckBox(), "Xóa"));

        // Set độ rộng cột
        table.getColumnModel().getColumn(7).setPreferredWidth(80);
        table.getColumnModel().getColumn(7).setMaxWidth(80);
        table.getColumnModel().getColumn(8).setPreferredWidth(80);
        table.getColumnModel().getColumn(8).setMaxWidth(80);

        // ===== ScrollPane bo góc =====
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        // Panel trắng bo góc cho bảng
        JPanel tableWrapper = new RoundedPanel(15, Color.WHITE);
        tableWrapper.setLayout(new BorderLayout());
        tableWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        tableWrapper.add(scroll, BorderLayout.CENTER);

        JLabel lblTableTitle = new JLabel("Danh sách khách hàng");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTableTitle.setForeground(new Color(30, 30, 30));
        lblTableTitle.setBorder(new EmptyBorder(0, 30, 15, 0));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        tablePanel.add(lblTableTitle, BorderLayout.NORTH);
        tablePanel.add(tableWrapper, BorderLayout.CENTER);

        // ===== CONTENT =====
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(statsPanel, BorderLayout.NORTH);
        content.add(tablePanel, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);

        loadKhachHangData();
        loadThongKe();
    }

    // ===== Load dữ liệu khách hàng =====
    private void loadKhachHangData() {
        model.setRowCount(0);
        List<KhachHang> dsKH = khachHangDAO.getAllKhachHang();

        for (KhachHang kh : dsKH) {
            String hang = getHang(kh.getDiemTichLuy());
            String trangThai = kh.isLaThanhVien() ? "Thành viên" : "Khách thường";
            model.addRow(new Object[]{
                    kh.getMaKhachHang(),
                    kh.getTenKhachHang(),
                    kh.getSdt(),
                    kh.getEmail(),
                    kh.getDiemTichLuy(),
                    hang,
                    trangThai,
                    "Sửa",
                    "Xóa"
            });
        }
    }

    // ===== Thống kê =====
    private void loadThongKe() {
        List<KhachHang> dsKH = khachHangDAO.getAllKhachHang();

        int tong = dsKH.size();
        int dong = 0, vang = 0, bac = 0;

        for (KhachHang kh : dsKH) {
            String h = getHang(kh.getDiemTichLuy());
            if ("Đồng".equals(h)) dong++;
            else if ("Vàng".equals(h)) vang++;
            else if ("Bạc".equals(h)) bac++;
        }

        lblTongKH.setText(String.valueOf(tong));
        lblDong.setText(String.valueOf(dong));
        lblVang.setText(String.valueOf(vang));
        lblBac.setText(String.valueOf(bac));
    }

    private String getHang(int diem) {
        if (diem < 1000) return "Bạc";
        else if (diem < 5000) return "Đồng";
        else return "Vàng";
    }

    // ====== UI Helper Methods ======
    private JButton createRoundedButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                
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
        
        valueLabel.setForeground(Color.WHITE);

        JPanel inner = new JPanel(new GridLayout(2, 1, 0, 5));
        inner.setOpaque(false);
        inner.add(valueLabel);
        inner.add(textLabel);
        
        box.add(inner, BorderLayout.CENTER);
        return box;
    }

    // Button Renderer
    class ButtonRenderer extends JButton implements TableCellRenderer {
        private final Color bgColor;
        private final Color fgColor;

        public ButtonRenderer(String text, Color bg, Color fg) {
            setText(text);
            this.bgColor = bg;
            this.fgColor = fg;
            setOpaque(false);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setForeground(fgColor);
            return this;
        }
    }

    // Button Editor
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private final String type;

        public ButtonEditor(JCheckBox checkBox, String type) {
            super(checkBox);
            this.type = type;
            button = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    Color bg = type.equals("Sửa") ? new Color(255, 152, 0) : new Color(244, 67, 54);
                    if (getModel().isPressed()) {
                        g2.setColor(bg.darker());
                    } else {
                        g2.setColor(bg);
                    }
                    
                    g2.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            button.setText(type);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setForeground(Color.WHITE);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                int selectedRow = table.getSelectedRow();
                String maKH = table.getValueAt(selectedRow, 0).toString();

                if (type.equals("Sửa")) {
                    SwingUtilities.invokeLater(() -> {
                        KhachHang kh = khachHangDAO.findByMaKH(maKH);
                        if (kh != null) {
                            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(KhachHang_View.this);
                            KhachHang_EditDialog dialog = new KhachHang_EditDialog(parentFrame, kh, khachHangDAO);
                            dialog.setVisible(true);
                            loadKhachHangData();
                            loadThongKe();
                        }
                    });
                } else if (type.equals("Xóa")) {
                    int confirm = JOptionPane.showConfirmDialog(
                        table,
                        "Bạn có chắc chắn muốn xóa khách hàng này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (khachHangDAO.deleteKhachHang(maKH)) {
                            ((DefaultTableModel) table.getModel()).removeRow(selectedRow);
                            loadThongKe();
                        } else {
                            JOptionPane.showMessageDialog(table, "Xóa thất bại!");
                        }
                    }
                }
            }
            isPushed = false;
            return type;
        }
    }

    // ===== RoundedPanel Helper =====
    class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color bgColor;

        public RoundedPanel(int radius, Color color) {
            super();
            cornerRadius = radius;
            bgColor = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            g2.dispose();
        }
    }
}