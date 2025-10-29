package view.ThucDon;

import dao.DatBan_DAO;
import dao.MonAn_DAO;
import entity.MonAn;
import entity.PhieuDatBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

/**
 * Dialog chọn món ăn - đã cải thiện nút "Đặt" trong bảng: nhỏ, bo góc, căn giữa.
 */
public class ChonMon_Dialog extends JDialog {
    // DAOs
    private MonAn_DAO monAnDAO;
    private DatBan_DAO datBanDAO;

    // Entity
    private PhieuDatBan phieuDatBan;

    // Components
    private JTable tblMonAn;
    private DefaultTableModel modelMonAn;
    private JButton btnHuy; // Đã BỎ btnDat
    private JTextField txtSearch; // Ô tìm kiếm
    
    // Data cache
    private List<MonAn> dsMonAn; // Cache danh sách món ăn

    // Định nghĩa màu sắc và font chữ
    private static final Color BG_COLOR = new Color(248, 249, 250);
    private static final Color TITLE_COLOR = new Color(33, 37, 41);
    private static final Color TABLE_HEADER_BG = new Color(248, 249, 250);
    private static final Color TABLE_GRID_COLOR = new Color(222, 226, 230);
    private static final Color SELECTION_BG = new Color(230, 240, 255);
    private static final Color BTN_GREEN_BG = new Color(40, 167, 69);
    private static final Color BTN_GRAY_BG = new Color(108, 117, 125);
    private static final Color TEXT_COLOR = Color.WHITE;
    
    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);


    public ChonMon_Dialog(Frame parent, PhieuDatBan phieuDatBan) {
        super(parent, "Chọn món ăn cho phiếu " + phieuDatBan.getMaPhieu().trim(), true);
        
        // Khởi tạo
        this.phieuDatBan = phieuDatBan;
        this.monAnDAO = new MonAn_DAO();
        this.datBanDAO = new DatBan_DAO();
        this.dsMonAn = new ArrayList<>();

        // Thiết lập UI
        setSize(700, 550);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(0, 10));
        getContentPane().setBackground(BG_COLOR);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        add(createHeaderAndFilterPanel(), BorderLayout.NORTH);
        add(createTablePanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadTableData();
        addEventListeners();
    }
    
    private JPanel createHeaderAndFilterPanel() {
        JPanel pnlWrapper = new JPanel(new BorderLayout(10, 10));
        pnlWrapper.setOpaque(false);

        JLabel titleLabel = new JLabel("Danh sách món ăn");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        pnlWrapper.add(titleLabel, BorderLayout.NORTH);
        
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFilter.setOpaque(false);

        JLabel lblSearch = new JLabel("Tìm kiếm:");
        lblSearch.setFont(FONT_LABEL);
        pnlFilter.add(lblSearch);

        txtSearch = new JTextField(20);
        txtSearch.setFont(FONT_LABEL);
        pnlFilter.add(txtSearch);
        
        pnlWrapper.add(pnlFilter, BorderLayout.SOUTH);
        
        return pnlWrapper;
    }

    private Component createTablePanel() {
        // Cập nhật model: Thêm cột "Đặt"
        modelMonAn = new DefaultTableModel(new String[]{"Mã món", "Tên món", "Đơn giá", "Trạng thái", "Đặt"}, 0) {
            @Override 
            public boolean isCellEditable(int row, int column) {
                // CHỈ cho phép sửa cột "Đặt" (cột 4)
                return column == 4; 
            }
        };
        
        tblMonAn = createStyledTable(modelMonAn);
        tblMonAn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tblMonAn);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        RoundedPanel tableWrapper = new RoundedPanel(12, Color.WHITE);
        tableWrapper.setLayout(new BorderLayout());
        tableWrapper.setBorder(new EmptyBorder(5, 5, 5, 5));
        tableWrapper.add(scroll, BorderLayout.CENTER);
        
        return tableWrapper;
    }
    
    private Component createButtonPanel() {
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnHuy = createStyledButton("Đóng", BTN_GRAY_BG, TEXT_COLOR);
        pnlButtons.add(btnHuy);
        
        return pnlButtons;
    }

    private void addEventListeners() {
        // Nút Hủy
        btnHuy.addActionListener(e -> dispose());
        
        // Sự kiện lọc
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterData();
            }
        });
    }

    private void loadTableData() {
        dsMonAn = monAnDAO.getAllMonAn(); // Lấy tất cả món 1 lần
        filterData(); // Lọc và hiển thị
    }
    
    private void filterData() {
        modelMonAn.setRowCount(0); // Xóa bảng

        String searchText = txtSearch.getText().trim().toLowerCase();
        
        for (MonAn mon : dsMonAn) {
            if (!mon.isTrangThai()) continue;
            
            boolean matchSearch = mon.getTenMonAn().toLowerCase().contains(searchText);
            
            if (matchSearch) { 
                modelMonAn.addRow(new Object[]{
                    mon.getMaMonAn().trim(),
                    mon.getTenMonAn(),
                    String.format("%,.0f", mon.getDonGia()),
                    "Còn bán",
                    "+ Đặt" // Text cho nút, sẽ được render
                });
            }
        }
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(44); // hơi lớn để có khoảng cách, nhưng nút sẽ nhỏ hơn
        table.setFont(FONT_TABLE);
        table.setSelectionBackground(SELECTION_BG);
        table.setGridColor(TABLE_GRID_COLOR);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TITLE_COLOR);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Căn lề cho các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        rightRenderer.setBorder(new EmptyBorder(0, 0, 0, 10));

        // Gán renderer (Cập nhật chỉ số cột)
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã
        // Cột 1 (Tên) mặc định
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer); // Đơn giá
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Trạng thái
        
        // ===== Thay đổi: dùng renderer/editor mới trả về panel chứa "small button" ở giữa =====
        table.getColumnModel().getColumn(4).setCellRenderer(new SmallButtonCellRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new SmallButtonCellEditor());
        // =======================================================
        
        // Set độ rộng
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // Mã
        table.getColumnModel().getColumn(1).setPreferredWidth(300); // Tên
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Đơn giá
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Trạng thái
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Nút Đặt

        return table;
    }

    private void datMon(int row) {
        String maMonAn = (String) modelMonAn.getValueAt(row, 0);
        String tenMonAn = (String) modelMonAn.getValueAt(row, 1);

        NhapSoLuong_Dialog soLuongDialog = new NhapSoLuong_Dialog((Frame) getParent(), tenMonAn);
        Object[] ketQua = soLuongDialog.showDialog();

        if (ketQua != null) {
            int soLuong = (int) ketQua[0];
            String ghiChu = (String) ketQua[1];

            boolean success = datBanDAO.addOrUpdateChiTiet(
                phieuDatBan.getMaPhieu(),
                maMonAn,
                soLuong,
                ghiChu
            );

            if (success) {
                JOptionPane.showMessageDialog(this, "Đã thêm " + soLuong + " " + tenMonAn + "!");
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm món vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color color = bg;
                if (getModel().isPressed()) {
                    color = bg.darker();
                } else if (getModel().isRollover()) {
                    color = bg.brighter();
                }
                
                g2.setColor(color);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(FONT_BUTTON);
        btn.setPreferredSize(new Dimension(80, 35));
        return btn;
    }

    // RoundedPanel như trước
    class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color bgColor;

        public RoundedPanel(int radius, Color color) {
            this.cornerRadius = radius;
            this.bgColor = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            Color color = (bgColor != null) ? bgColor : getBackground();
            g2.setColor(color);
            
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            
            g2.dispose();
        }
    }

    // =========================================================================
    // New: Small rounded button + cell renderer/editor
    // =========================================================================

    /**
     * Nút nhỏ bo góc dùng trong ô (vẽ custom để ổn định trên mọi LAF).
     */
    static class SmallRoundedButton extends JButton {
        private Color bgColor;
        private Color fgColor;
        private int arc = 10;

        public SmallRoundedButton(String text, Color bg, Color fg) {
            super(text);
            this.bgColor = bg;
            this.fgColor = fg;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(fgColor);
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(72, 28)); // kích thước nhỏ gọn
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color fill = bgColor;
            if (getModel().isPressed()) fill = bgColor.darker();
            else if (getModel().isRollover()) fill = bgColor.brighter();

            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            // Draw text (center)
            FontMetrics fm = g2.getFontMetrics();
            int textWidth = fm.stringWidth(getText());
            int textHeight = fm.getAscent();
            g2.setColor(getForeground());
            g2.drawString(getText(), (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 2);

            g2.dispose();
        }
    }

    /**
     * Renderer trả về một JPanel (FlowLayout center) chứa SmallRoundedButton ở giữa.
     */
    class SmallButtonCellRenderer implements TableCellRenderer {
        private final JPanel panel;
        private final SmallRoundedButton button;

        public SmallButtonCellRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6)); // căn giữa, có padding dọc
            panel.setOpaque(false);
            button = new SmallRoundedButton("+ Đặt", BTN_GREEN_BG, TEXT_COLOR);
            button.setFocusable(false);
            panel.add(button);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            // Cập nhật text nếu cần (value = "+ Đặt")
            String text = (value == null) ? "" : value.toString();
            button.setText(text);

            // Thay đổi màu khi hàng được chọn / focus
            if (isSelected || hasFocus) {
                button.bgColor = BTN_GREEN_BG.darker();
            } else {
                button.bgColor = BTN_GREEN_BG;
            }
            // đảm bảo repaint
            button.repaint();
            return panel;
        }
    }

    /**
     * Editor: panel chứa button; khi nhấn button sẽ gọi datMon(row).
     */
    class SmallButtonCellEditor extends AbstractCellEditor implements TableCellEditor {
        private final JPanel panel;
        private final SmallRoundedButton button;
        private int editingRow = -1;

        public SmallButtonCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
            panel.setOpaque(false);
            button = new SmallRoundedButton("+ Đặt", BTN_GREEN_BG, TEXT_COLOR);
            button.setFocusable(false);
            button.addActionListener(e -> {
                // stop editing then perform action
                // make sure to capture current row
                final int row = editingRow;
                // Stop editing first so table state is consistent
                SwingUtilities.invokeLater(() -> {
                    fireEditingStopped();
                    if (row >= 0) {
                        datMon(row);
                    }
                });
            });
            panel.add(button);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.editingRow = row;
            String text = (value == null) ? "" : value.toString();
            button.setText(text);
            button.bgColor = BTN_GREEN_BG.darker(); // đánh dấu đang edit
            button.repaint();
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "+ Đặt";
        }

        // Khống chế chỉ kích hoạt khi click chuột
        @Override
        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                return ((MouseEvent) e).getClickCount() >= 1;
            }
            return false;
        }
    }
}