package view.NhanVien;

import dao.NhanVien_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D; 
import java.time.LocalDate; 
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NhanVien_View extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JLabel lblTongNV, lblQuanLy, lblTiepTan;

    public NhanVien_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(251, 248, 241));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 15, 30));

        JLabel title = new JLabel("Quản lý nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(30, 30, 30));

        JLabel subtitle = new JLabel("Quản lý thông tin và tài khoản nhân viên");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JButton btnAdd = createRoundedButton("+ Thêm nhân viên mới", new Color(76, 175, 80), Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.setPreferredSize(new Dimension(200, 45));

        btnAdd.addActionListener(e -> {
            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            // SỬA: Thay đổi constructor của AddDialog để sử dụng các trường mới
            NhanVien_AddDialog dialog = new NhanVien_AddDialog(parentFrame, nhanVienDAO);
            dialog.setVisible(true);
            loadNhanVienData();
            loadThongKe();
        });

        header.add(titlePanel, BorderLayout.WEST);
        header.add(btnAdd, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 30, 25, 30));

        lblTongNV = createStatLabel("0");
        lblQuanLy = createStatLabel("0");
        lblTiepTan = createStatLabel("0");

        statsPanel.add(createStatBox(lblTongNV, "Nhân viên hoạt động", new Color(34, 139, 230)));
        statsPanel.add(createStatBox(lblQuanLy, "Quản lý", new Color(76, 175, 80)));
        statsPanel.add(createStatBox(lblTiepTan, "Tiếp tân", new Color(255, 152, 0)));

        // SỬA: Thêm các cột mới
        String[] cols = {"Mã NV", "Tên NV", "Giới tính", "Ngày sinh", "SĐT", "CCCD", "Chức vụ", "Trạng thái", "Tài khoản", "Vai trò TK", "Sửa", "Xóa"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 10;
            }
        };

        table = new JTable(model);
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setGridColor(new Color(230, 230, 230));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(new Color(248, 249, 250));
        tableHeader.setForeground(new Color(60, 60, 60));
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 45));
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)));
        ((DefaultTableCellRenderer) tableHeader.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
             @Override
             public Component getTableCellRendererComponent(JTable table, Object value,
                     boolean isSelected, boolean hasFocus, int row, int column) {
                 Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                 if (!isSelected) {
                     c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                 }
                 setBorder(new EmptyBorder(5, 10, 5, 10)); 
               
                 // Cột căn giữa
                 if (column == 0 || column == 2 || column == 3 || column == 7) { 
                     setHorizontalAlignment(CENTER);
                 } else {
                     setHorizontalAlignment(LEFT);
                 }
                
                 if (!isSelected) {
                    setForeground(table.getForeground());
                 }
                 return c;
             }
        };

        for (int i = 0; i < cols.length - 2; i++) { 
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        table.getColumn("Sửa").setCellRenderer(new ButtonRenderer("Sửa", new Color(255, 152, 0), Color.WHITE));
        table.getColumn("Sửa").setCellEditor(new ButtonEditor(new JCheckBox(), "Sửa"));
        table.getColumn("Xóa").setCellRenderer(new ButtonRenderer("Xóa", new Color(244, 67, 54), Color.WHITE));
        table.getColumn("Xóa").setCellEditor(new ButtonEditor(new JCheckBox(), "Xóa"));

        // SỬA: Điều chỉnh độ rộng cột
        table.getColumnModel().getColumn(0).setPreferredWidth(70); // Mã NV
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Tên NV
        table.getColumnModel().getColumn(2).setPreferredWidth(60); // Giới tính
        table.getColumnModel().getColumn(3).setPreferredWidth(80); // Ngày sinh
        table.getColumnModel().getColumn(4).setPreferredWidth(90); // SĐT
        table.getColumnModel().getColumn(5).setPreferredWidth(100); // CCCD (MỚI)
        table.getColumnModel().getColumn(6).setPreferredWidth(100); // Chức vụ (MỚI)
        table.getColumnModel().getColumn(7).setPreferredWidth(70); // Trạng thái (MỚI)
        table.getColumnModel().getColumn(8).setPreferredWidth(80); // Tài khoản
        table.getColumnModel().getColumn(9).setPreferredWidth(70); // Vai trò TK
        table.getColumnModel().getColumn(10).setPreferredWidth(60); // Sửa
        table.getColumnModel().getColumn(10).setMaxWidth(60);
        table.getColumnModel().getColumn(11).setPreferredWidth(60); // Xóa
        table.getColumnModel().getColumn(11).setMaxWidth(60);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        JPanel tableWrapper = new RoundedPanel(15, Color.WHITE);
        tableWrapper.setLayout(new BorderLayout());
        tableWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        tableWrapper.add(scroll, BorderLayout.CENTER);

        JLabel lblTableTitle = new JLabel("Danh sách nhân viên");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTableTitle.setForeground(new Color(30, 30, 30));
        lblTableTitle.setBorder(new EmptyBorder(0, 30, 15, 0)); 

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.setBorder(new EmptyBorder(0, 30, 30, 30));
        tablePanel.add(lblTableTitle, BorderLayout.NORTH);
        tablePanel.add(tableWrapper, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(statsPanel, BorderLayout.NORTH);
        content.add(tablePanel, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);

        loadNhanVienData();
        loadThongKe();
    }

    private void loadNhanVienData() {
        model.setRowCount(0);
        List<NhanVien> dsNV = nhanVienDAO.getAllNhanVien();

        for (NhanVien nv : dsNV) {
            TaiKhoan tk = nv.getTaiKhoan();
            String vaiTroTK = tk != null && tk.getVaiTro() != null ? tk.getVaiTro() : "-";
            String ngaySinhStr = (nv.getNgaySinh() != null) ? nv.getNgaySinh().format(DATE_FORMATTER) : "-";

            model.addRow(new Object[]{
                    nv.getMaNhanVien(),
                    nv.getHoTen(),
                    !nv.getGioiTinh() ? "Nam" : "Nữ", // SỬA: Dùng getGioiTinh()
                    ngaySinhStr,
                    nv.getSoDienThoai(), // SỬA: Dùng getSoDienThoai()
                    nv.getSoCCCD() != null ? nv.getSoCCCD() : "-", // MỚI
                    nv.getChucVu() != null ? nv.getChucVu() : "-", // MỚI
                    nv.getTrangThai() != null ? nv.getTrangThai() : "-", // MỚI
                    tk != null ? tk.getTenDangNhap() : "Chưa có",
                    vaiTroTK,
                    "Sửa",
                    "Xóa"
            });
        }
    }

    private void loadThongKe() {
        List<NhanVien> dsNV = nhanVienDAO.getAllNhanVien();

        int tong = 0;
        int quanLy = 0, tiepTan = 0;

        for (NhanVien nv : dsNV) {
            if (nv.getTrangThai() != null && nv.getTrangThai().equals("Đang làm")) {
                 tong++;
                 if (nv.getChucVu() != null) {
                     if (nv.getChucVu().contains("quản lý")) quanLy++;
                     else if (nv.getChucVu().contains("tiếp tân")) tiepTan++;
                 }
            }
        }

        lblTongNV.setText(String.valueOf(tong));
        lblQuanLy.setText(String.valueOf(quanLy));
        lblTiepTan.setText(String.valueOf(tiepTan));
    }

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
            if (isSelected) {
                setBackground(bgColor.darker());
            } else {
                setBackground(bgColor);
            }
            return this;
        }
    }

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
                int selectedRow = table.convertRowIndexToModel(table.getEditingRow());
                if (selectedRow != -1) {
                    String maNV = model.getValueAt(selectedRow, 0).toString();

                    if (type.equals("Sửa")) {
                        SwingUtilities.invokeLater(() -> {
                            NhanVien nv = nhanVienDAO.findByMaNV(maNV);
                            if (nv != null) {
                                Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(NhanVien_View.this);
                                NhanVien_EditDialog dialog = new NhanVien_EditDialog(parentFrame, nv, nhanVienDAO);
                                dialog.setVisible(true);
                                loadNhanVienData();
                                loadThongKe();
                            } else {
                                JOptionPane.showMessageDialog(NhanVien_View.this, "Không tìm thấy nhân viên với mã: " + maNV);
                            }
                        });
                    } else if (type.equals("Xóa")) {
                        SwingUtilities.invokeLater(() -> {
                            Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(table);
                            int confirm = JOptionPane.showConfirmDialog(
                                    parentFrame,
                                    "Bạn có chắc chắn muốn xóa nhân viên [" + maNV + "]?",
                                    "Xác nhận xóa",
                                    JOptionPane.YES_NO_OPTION,
                                    JOptionPane.WARNING_MESSAGE
                            );
                            if (confirm == JOptionPane.YES_OPTION) {
                                boolean deleted = nhanVienDAO.deleteNhanVien(maNV);
                                if (deleted) {
                                    JOptionPane.showMessageDialog(parentFrame, "Xóa nhân viên thành công!");
                                    model.removeRow(selectedRow);
                                    loadThongKe();
                                } else {
                                    JOptionPane.showMessageDialog(parentFrame, "Xóa nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        });
                    }
                }
            }
            isPushed = false;
            return type;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

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