package view.ThucDon;

import dao.PhieuDatBan_DAO; 
import dao.MonAn_DAO;
import entity.MonAn;
import entity.PhieuDatBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
// SỬA: Thêm các import bị thiếu
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat; // Cần cho NhapSoLuong_Dialog (nếu gộp)

public class ChonMon_Dialog extends JDialog {
    // DAOs
    private MonAn_DAO monAnDAO;
    private PhieuDatBan_DAO phieuDatBanDAO; 

    // Entity
    private PhieuDatBan phieuDatBan;

    // Components
    private JTable tblMenu; 
    private DefaultTableModel modelMenu; 
    private JTable tblChiTiet; 
    private DefaultTableModel modelChiTiet; 
    
    private JButton btnHuyMon; 
    private JButton btnDong;
    private JTextField txtSearch; 

    // Data cache
    private List<MonAn> dsMonAn; 

    // Định nghĩa màu sắc và font chữ
    private static final Color BG_COLOR = new Color(248, 249, 250);
    private static final Color TITLE_COLOR = new Color(33, 37, 41);
    private static final Color TABLE_HEADER_BG = new Color(248, 249, 250);
    private static final Color TABLE_GRID_COLOR = new Color(222, 226, 230);
    private static final Color SELECTION_BG = new Color(230, 240, 255);
    private static final Color BTN_GREEN_BG = new Color(40, 167, 69);
    private static final Color BTN_GRAY_BG = new Color(108, 117, 125);
    private static final Color BTN_RED_BG = new Color(220, 53, 69); 
    private static final Color TEXT_COLOR = Color.WHITE;
    
    // SỬA: Thêm TEXT_PRIMARY (hoặc dùng TITLE_COLOR)
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);

    private static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_TABLE = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 13);


    public ChonMon_Dialog(Frame parent, PhieuDatBan phieuDatBan) {
        super(parent, "Gọi món cho phiếu " + phieuDatBan.getMaPhieu().trim(), true);

        // Khởi tạo
        this.phieuDatBan = phieuDatBan;
        this.monAnDAO = new MonAn_DAO();
        this.phieuDatBanDAO = new PhieuDatBan_DAO();
        this.dsMonAn = new ArrayList<>();

        // Thiết lập UI
        setSize(850, 700); 
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(0, 10));
        getContentPane().setBackground(BG_COLOR);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        add(createHeaderAndFilterPanel(), BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            createTablePanelMenu(), // Nửa trên (Menu)
            createTablePanelChiTiet() // Nửa dưới (Món đã đặt)
        );
        splitPane.setDividerLocation(300); 
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        
        add(splitPane, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);

        loadTableData();
        loadChiTietData(); 
        addEventListeners();
    }

    private JPanel createHeaderAndFilterPanel() {
        JPanel pnlWrapper = new JPanel(new BorderLayout(10, 10));
        pnlWrapper.setOpaque(false);

        JLabel titleLabel = new JLabel("Danh sách món ăn (Menu)");
        titleLabel.setFont(FONT_TITLE);
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        pnlWrapper.add(titleLabel, BorderLayout.NORTH);

        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFilter.setOpaque(false);

        JLabel lblSearch = new JLabel("Tìm kiếm món ăn:");
        lblSearch.setFont(FONT_LABEL);
        pnlFilter.add(lblSearch);

        txtSearch = new JTextField(20);
        txtSearch.setFont(FONT_LABEL);
        pnlFilter.add(txtSearch);

        pnlWrapper.add(pnlFilter, BorderLayout.SOUTH);

        return pnlWrapper;
    }

    private Component createTablePanelMenu() {
        modelMenu = new DefaultTableModel(new String[]{"Mã món", "Tên món", "Đơn giá", "Trạng thái", "Đặt"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        tblMenu = createStyledTable(modelMenu);
        tblMenu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // --- SỬA: Chuyển logic renderer/editor về đây ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        rightRenderer.setBorder(new EmptyBorder(0, 0, 0, 10));
        
        tblMenu.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã
        tblMenu.getColumnModel().getColumn(2).setCellRenderer(rightRenderer); // Đơn giá
        tblMenu.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Trạng thái
        tblMenu.getColumnModel().getColumn(4).setCellRenderer(new SmallButtonCellRenderer());
        tblMenu.getColumnModel().getColumn(4).setCellEditor(new SmallButtonCellEditor());
        
        // Set độ rộng
        tblMenu.getColumnModel().getColumn(0).setPreferredWidth(60);  // Mã
        tblMenu.getColumnModel().getColumn(1).setPreferredWidth(350); // Tên
        tblMenu.getColumnModel().getColumn(2).setPreferredWidth(100); // Đơn giá
        tblMenu.getColumnModel().getColumn(3).setPreferredWidth(80);  // Trạng thái
        tblMenu.getColumnModel().getColumn(4).setPreferredWidth(80);  // Nút Đặt
        // --- Hết sửa ---

        JScrollPane scroll = new JScrollPane(tblMenu);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        RoundedPanel tableWrapper = new RoundedPanel(12, Color.WHITE);
        tableWrapper.setLayout(new BorderLayout());
        tableWrapper.setBorder(new EmptyBorder(5, 5, 5, 5));
        tableWrapper.add(scroll, BorderLayout.CENTER);

        return tableWrapper;
    }

    private Component createTablePanelChiTiet() {
        modelChiTiet = new DefaultTableModel(new String[]{"Mã món", "Tên món", "Số lượng", "Đơn Giá", "Ghi chú"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblChiTiet = createStyledTable(modelChiTiet);
        tblChiTiet.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // --- SỬA: Chuyển logic renderer về đây ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        rightRenderer.setBorder(new EmptyBorder(0, 0, 0, 10));
        
        // Custom renderer cho bảng chi tiết (có xử lý selected)
        tblChiTiet.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                
                if (column == 1) { // Tên món
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else if (column == 2) { // Số lượng
                    setHorizontalAlignment(CENTER);
                    setFont(new Font("Segoe UI", Font.BOLD, 14));
                } else if (column == 3) { // Đơn giá
                    setHorizontalAlignment(RIGHT);
                } else {
                    setHorizontalAlignment(LEFT);
                }
                
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground()); // Dùng màu của table
                } else {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                    setForeground(TEXT_PRIMARY); // SỬA: Đã fix lỗi
                }
                
                setBorder(new EmptyBorder(10, 15, 10, 15));
                return c;
            }
        });
        
        // Set độ rộng
        tblChiTiet.getColumnModel().getColumn(0).setPreferredWidth(60);  // Mã
        tblChiTiet.getColumnModel().getColumn(1).setPreferredWidth(350); // Tên
        tblChiTiet.getColumnModel().getColumn(2).setPreferredWidth(50);  // SL
        tblChiTiet.getColumnModel().getColumn(3).setPreferredWidth(100); // Đơn giá
        tblChiTiet.getColumnModel().getColumn(4).setPreferredWidth(150); // Ghi chú
        // --- Hết sửa ---

        JScrollPane scroll = new JScrollPane(tblChiTiet);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        RoundedPanel tableWrapper = new RoundedPanel(12, Color.WHITE);
        tableWrapper.setLayout(new BorderLayout());
        tableWrapper.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(TABLE_GRID_COLOR, 1), "Các món đã đặt cho phiếu " + phieuDatBan.getMaPhieu().trim(),
            TitledBorder.LEFT, TitledBorder.TOP, FONT_HEADER, TITLE_COLOR
        ));
        tableWrapper.add(scroll, BorderLayout.CENTER);

        return tableWrapper;
    }

    private Component createButtonPanel() {
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnHuyMon = createStyledButton("Hủy món đã chọn", BTN_RED_BG, TEXT_COLOR);
        btnHuyMon.setPreferredSize(new Dimension(160, 35)); // Tăng size
        
        btnDong = createStyledButton("Đóng", BTN_GRAY_BG, TEXT_COLOR);
        
        pnlButtons.add(btnHuyMon);
        pnlButtons.add(btnDong);

        return pnlButtons;
    }

    private void addEventListeners() {
        // Nút Đóng
        btnDong.addActionListener(e -> dispose());

        // SỬA: Nút Hủy Món
        btnHuyMon.addActionListener(e -> xuLyHuyMon());

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
        if (dsMonAn == null) dsMonAn = new ArrayList<>();
        filterData(); // Lọc và hiển thị
    }
    
    private void loadChiTietData() {
        modelChiTiet.setRowCount(0);
        
        List<Object[]> dsMon = phieuDatBanDAO.getChiTietTheoMaPhieu(phieuDatBan.getMaPhieu()); 
        
        for (Object[] row : dsMon) {
            MonAn monAn = monAnDAO.getMonAnTheoMa((String) row[0]);
            double donGia = (monAn != null) ? monAn.getDonGia() : 0.0;
            
            modelChiTiet.addRow(new Object[]{
                row[0], // MaMon
                row[1], // TenMon
                row[2], // SoLuong
                String.format("%,.0f", donGia), // Đơn giá
                row[3]  // GhiChu
            });
        }
    }

    private void filterData() {
        modelMenu.setRowCount(0); 
        String searchText = txtSearch.getText().trim().toLowerCase();

        for (MonAn mon : dsMonAn) {
            if (mon == null) continue;
            
            if (!mon.isTrangThai()) continue; 

            String ten = mon.getTenMonAn() != null ? mon.getTenMonAn().toLowerCase() : "";
            boolean matchSearch = ten.contains(searchText);

            if (matchSearch) {
                modelMenu.addRow(new Object[]{
                    mon.getMaMonAn() != null ? mon.getMaMonAn().trim() : "",
                    mon.getTenMonAn() != null ? mon.getTenMonAn() : "N/A",
                    String.format("%,.0f", mon.getDonGia()),
                    "Còn bán",
                    "+ Đặt" // Text cho nút
                });
            }
        }
    }

    // ===== HÀM ĐÃ SỬA (FIX MÀU CHỮ) =====
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(44); 
        table.setFont(FONT_TABLE);
        table.setSelectionBackground(SELECTION_BG);
        
        // SỬA: Đặt màu chữ khi chọn là màu đen (TITLE_COLOR)
        table.setSelectionForeground(TITLE_COLOR); 
        
        table.setGridColor(TABLE_GRID_COLOR);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_HEADER);
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TITLE_COLOR);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        return table;
    }
    // ===== KẾT THÚC SỬA =====

    void datMon(int row) {
        if (row < 0 || row >= modelMenu.getRowCount()) return;

        String maMonAn = (String) modelMenu.getValueAt(row, 0);
        String tenMonAn = (String) modelMenu.getValueAt(row, 1);

        NhapSoLuong_Dialog soLuongDialog = new NhapSoLuong_Dialog((Frame) getParent(), tenMonAn);
        Object[] ketQua = soLuongDialog.showDialog();

        if (ketQua != null) {
            int soLuong = (int) ketQua[0];
            String ghiChu = (String) ketQua[1];

            boolean success = false;
            try {
                success = phieuDatBanDAO.addOrUpdateChiTiet( 
                    phieuDatBan.getMaPhieu(),
                    maMonAn,
                    soLuong,
                    ghiChu
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                success = false;
            }

            if (success) {
                loadChiTietData(); // Tải lại bảng dưới
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm món vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void xuLyHuyMon() {
        int selectedRow = tblChiTiet.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn một món trong bảng 'Món đã đặt' (ở dưới) để hủy!", 
                "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (phieuDatBan == null) return;

        String maMonAn = (String) modelChiTiet.getValueAt(selectedRow, 0);
        String tenMonAn = (String) modelChiTiet.getValueAt(selectedRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn HỦY món: " + tenMonAn + "?", 
            "Xác nhận hủy", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = phieuDatBanDAO.deleteChiTiet(phieuDatBan.getMaPhieu(), maMonAn);
            if (success) {
                JOptionPane.showMessageDialog(this, "Hủy món thành công!");
                loadChiTietData(); // Tải lại bảng dưới
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi khi hủy món!", 
                    "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
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

    class SmallButtonCellRenderer implements javax.swing.table.TableCellRenderer {
        private final JPanel panel;
        private final SmallRoundedButton button;

        public SmallButtonCellRenderer() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8)); // Sửa padding
            panel.setOpaque(true); 
            button = new SmallRoundedButton("+ Đặt", BTN_GREEN_BG, TEXT_COLOR);
            button.setFocusable(false);
            panel.add(button);
        }

        @Override
        public Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            String text = (value == null) ? "" : value.toString();
            button.setText(text);

            if (isSelected) { // SỬA: Sửa cả nền panel
                panel.setBackground(table.getSelectionBackground());
                button.bgColor = BTN_GREEN_BG.darker();
            } else {
                panel.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 250, 252));
                button.bgColor = BTN_GREEN_BG;
            }
            button.repaint();
            return panel;
        }
    }

    class SmallButtonCellEditor extends AbstractCellEditor implements javax.swing.table.TableCellEditor {
        private final JPanel panel;
        private final SmallRoundedButton button;
        private int editingRow = -1;

        public SmallButtonCellEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8)); 
            panel.setOpaque(true); 
            button = new SmallRoundedButton("+ Đặt", BTN_GREEN_BG, TEXT_COLOR);
            button.setFocusable(false);
            button.addActionListener(e -> {
                final int row = editingRow;
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
        public Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {
            this.editingRow = row;
            String text = (value == null) ? "" : value.toString();
            button.setText(text);
            panel.setBackground(table.getSelectionBackground()); 
            button.bgColor = BTN_GREEN_BG.darker();
            button.repaint();
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "+ Đặt";
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            if (e instanceof MouseEvent) {
                return ((MouseEvent) e).getClickCount() >= 1;
            }
            return false;
        }
    }
}