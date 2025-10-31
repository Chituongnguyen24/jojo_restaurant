package view.HoaDon;

import entity.KhuyenMai;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import dao.KhuyenMai_DAO;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class KhuyenMai_View extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO();

    private JLabel lblTongKM, lblHoatDong, lblSapBatDau, lblHetHan;

    public KhuyenMai_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(251, 248, 241)); // Nền màu be #FBF8F1

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 15, 30));

        JLabel title = new JLabel("Quản lý khuyến mãi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(30, 30, 30));

        JLabel subtitle = new JLabel("Quản lý thông tin khuyến mãi");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        
        JButton btnAdd = createRoundedButton("+ Thêm khuyến mãi mới", new Color(76, 175, 80), Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setPreferredSize(new Dimension(200, 45));
        btnAdd.addActionListener(e -> {
            KhuyenMai_AddDialog dialog = new KhuyenMai_AddDialog(null, khuyenMaiDAO);
            dialog.setVisible(true);
            loadKhuyenMaiData(); 
            loadThongKe();
        });

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);
        topRow.add(titlePanel, BorderLayout.WEST);
        topRow.add(btnAdd, BorderLayout.EAST);

        JPanel headerContent = new JPanel(new BorderLayout());
        headerContent.setOpaque(false);
        headerContent.add(topRow, BorderLayout.NORTH);

        header.add(headerContent, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ===== STATS =====
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 30, 25, 30));

        lblTongKM = createStatLabel("0");
        lblHoatDong = createStatLabel("0");
        lblSapBatDau = createStatLabel("0");
        lblHetHan = createStatLabel("0");

        statsPanel.add(createStatBox(lblTongKM, "Tổng KM", new Color(34, 139, 230)));
        statsPanel.add(createStatBox(lblHoatDong, "Hoạt động", new Color(76, 175, 80)));
        statsPanel.add(createStatBox(lblSapBatDau, "Sắp BD", new Color(255, 152, 0)));
        statsPanel.add(createStatBox(lblHetHan, "Hết hạn", new Color(156, 39, 176)));

        // ===== TABLE =====
        String[] cols = {"Mã KM", "Mô tả KM", "Giá trị", "Loại KM", "Ngày bắt đầu", "Ngày kết thúc", "Trạng thái", "Sửa", "Xóa"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 7;  // Sửa=7, Xóa=8
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

        JLabel lblTableTitle = new JLabel("Danh sách khuyến mãi");
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

        loadKhuyenMaiData();
        loadThongKe();
    }

    private String getCurrentTrangThai(KhuyenMai km) {
        if (km.getMaKM().equals("KM00000000")) return "Luôn áp dụng";
        
        LocalDate now = LocalDate.now();
        if (now.isBefore(km.getNgayApDung())) { // SỬA: Dùng NgayApDung
            return "Sắp bắt đầu";
        } else if (now.isAfter(km.getNgayHetHan())) { // SỬA: Dùng NgayHetHan
            return "Hết hạn";
        } else {
            return "Hoạt động";
        }
    }

    // ===== Load dữ liệu khuyến mãi =====
    private void loadKhuyenMaiData() {
        loadKhuyenMaiData(null);  
    }

    private void loadKhuyenMaiData(String keyword) {
        model.setRowCount(0);
        List<KhuyenMai> dsKM;
        if (keyword == null || keyword.isEmpty()) {
            dsKM = khuyenMaiDAO.getAllKhuyenMai();
        } else {
            dsKM = khuyenMaiDAO.findKhuyenMai(keyword);  
        }

        for (KhuyenMai km : dsKM) {
            String trangThai = getCurrentTrangThai(km);
            String giaTriHienThi;
            if (km.getMucKM() < 1.0) {
                 giaTriHienThi = String.format("%.0f%%", km.getMucKM() * 100);
            } else {
                 giaTriHienThi = String.format("%,.0f VNĐ", km.getMucKM());
            }
            
            model.addRow(new Object[]{
                    km.getMaKM(),
                    km.getMoTa(), // SỬA: Dùng MoTa
                    giaTriHienThi, // SỬA: Dùng giá trị hiển thị
                    km.getLoaiKM(), // MỚI
                    km.getNgayApDung().toString(), // SỬA: Dùng NgayApDung
                    km.getNgayHetHan().toString(), // SỬA: Dùng NgayHetHan
                    trangThai,
                    "Sửa",
                    "Xóa"
            });
        }
    }

    // ===== Thống kê =====
    private void loadThongKe() {
        List<KhuyenMai> dsKM = khuyenMaiDAO.getAllKhuyenMai();

        int tong = dsKM.size();
        int hoatDong = 0, sapBatDau = 0, hetHan = 0;

        for (KhuyenMai km : dsKM) {
            String tt = getCurrentTrangThai(km);
            if ("Hoạt động".equals(tt) || "Luôn áp dụng".equals(tt)) hoatDong++;
            else if ("Sắp bắt đầu".equals(tt)) sapBatDau++;
            else if ("Hết hạn".equals(tt)) hetHan++;
        }

        lblTongKM.setText(String.valueOf(tong));
        lblHoatDong.setText(String.valueOf(hoatDong));
        lblSapBatDau.setText(String.valueOf(sapBatDau));
        lblHetHan.setText(String.valueOf(hetHan));
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
                String maKM = model.getValueAt(selectedRow, 0).toString();

                if (type.equals("Sửa")) {
                    SwingUtilities.invokeLater(() -> {
                        KhuyenMai km = khuyenMaiDAO.getKhuyenMaiById(maKM);
                        if (km != null) {
                            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(KhuyenMai_View.this);
                            KhuyenMai_EditDialog dialog = new KhuyenMai_EditDialog(parentFrame, km, khuyenMaiDAO);
                            dialog.setVisible(true);
                            loadKhuyenMaiData();  
                            loadThongKe();
                        }
                    });
                } else if (type.equals("Xóa")) {
                    int confirm = JOptionPane.showConfirmDialog(
                        null,
                        "Bạn có chắc chắn muốn xóa khuyến mãi này?",
                        "Xác nhận xóa",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (khuyenMaiDAO.deleteKhuyenMai(maKM)) {
                            model.removeRow(selectedRow);  
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