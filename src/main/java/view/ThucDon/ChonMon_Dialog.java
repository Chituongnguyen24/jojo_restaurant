package view.ThucDon;

// Import mới
import dao.DatBan_DAO;
import dao.MonAn_DAO;
import entity.MonAn;
import entity.PhieuDatBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer; // Import mới

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener; // Import mới
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent; // Import mới
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Dialog chọn món đã được thiết kế lại với NÚT ĐẶT MÓN TRÊN TỪNG DÒNG.
 * (Đã loại bỏ bộ lọc Loại Món Ăn theo yêu cầu).
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
    
    /**
     * Tạo Panel chứa tiêu đề và ô tìm kiếm
     */
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

    /**
     * Tạo Panel chứa Bảng (Đã cập nhật số cột)
     */
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
    
    /**
     * Tạo Panel chứa các nút bấm (Chỉ còn nút Đóng)
     */
    private Component createButtonPanel() {
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnHuy = createStyledButton("Đóng", BTN_GRAY_BG, TEXT_COLOR);
        
        // Đã BỎ btnDat
        
        pnlButtons.add(btnHuy);
        
        return pnlButtons;
    }

    /**
     * Gán sự kiện cho các components (Đã bỏ sự kiện ComboBox)
     */
    private void addEventListeners() {
        // Nút Hủy
        btnHuy.addActionListener(e -> dispose());
        
        // Đã BỎ sự kiện double-click và btnDat
        
        // Sự kiện lọc
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterData();
            }
        });
    }

    /**
     * Tải dữ liệu món ăn từ CSDL vào cache (dsMonAn)
     */
    private void loadTableData() {
        dsMonAn = monAnDAO.getAllMonAn(); // Lấy tất cả món 1 lần
        filterData(); // Lọc và hiển thị
    }
    
    /**
     * Lọc và hiển thị dữ liệu lên bảng (Đã thêm data cho cột 5)
     */
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

    /**
     * Tạo JTable với style chung (Đã cập nhật chỉ số cột)
     */
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
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
        
        // ===== PHẦN MỚI: Thêm Button vào cột "Đặt" (cột 4) =====
        table.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
        // =======================================================
        
        // Set độ rộng
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // Mã
        table.getColumnModel().getColumn(1).setPreferredWidth(250); // Tên
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Đơn giá
        table.getColumnModel().getColumn(3).setPreferredWidth(80);  // Trạng thái
        table.getColumnModel().getColumn(4).setPreferredWidth(80);  // Nút Đặt

        return table;
    }

    /**
     * Xử lý logic khi nhấn nút "+" trên dòng (thay thế cho chonVaDatMon cũ)
     */
    private void datMon(int row) {
        // Lấy mã món từ model
        String maMonAn = (String) modelMonAn.getValueAt(row, 0);
        String tenMonAn = (String) modelMonAn.getValueAt(row, 1);

        // 1. Mở dialog Nhập số lượng
        NhapSoLuong_Dialog soLuongDialog = new NhapSoLuong_Dialog((Frame) getParent(), tenMonAn);
        Object[] ketQua = soLuongDialog.showDialog();

        // 2. Nếu người dùng nhấn "Đặt" (ketQua != null)
        if (ketQua != null) {
            int soLuong = (int) ketQua[0];
            String ghiChu = (String) ketQua[1];

            // 3. Gọi DAO để thêm/cập nhật CSDL
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

    /**
     * Hàm trợ giúp tạo nút bấm có bo góc và hiệu ứng
     */
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

    // =========================================================================
    // LỚP NỘI BỘ (INNER CLASS) CHO PANEL BO GÓC
    // =========================================================================

    /**
     * Một JPanel được tùy chỉnh để có viền bo góc và màu nền.
     */
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
    // LỚP NỘI BỘ (INNER CLASS) CHO BUTTON TRONG BẢNG
    // =========================================================================

    /**
     * Lớp Renderer để vẽ nút "+ Đặt" trong ô của JTable.
     */
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setForeground(Color.WHITE);
            setBackground(BTN_GREEN_BG); // Màu xanh lá
            setText("+ Đặt");
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            // Căn lề nhỏ cho nút
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            
            // Đổi màu khi dòng được chọn (để người dùng biết đang nhấn dòng nào)
            if (isSelected) {
                setBackground(BTN_GREEN_BG.darker());
            } else {
                setBackground(BTN_GREEN_BG);
            }
            
            // value chính là text ta set trong filterData ("+ Đặt")
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    /**
     * Lớp Editor để xử lý sự kiện click nút "+ Đặt" trong JTable.
     */
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private int row; // Dòng đang được click

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox); // Constructor bắt buộc
            button = new JButton();
            button.setOpaque(true);
            button.setForeground(Color.WHITE);
            button.setBackground(BTN_GREEN_BG.darker()); // Màu đậm hơn khi nhấn
            button.setText("+ Đặt");
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            
            // Đây là hành động chính khi nút được nhấn
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Dừng việc chỉnh sửa ô (quan trọng)
                    fireEditingStopped();
                    // Gọi hàm đặt món cho dòng hiện tại (đã lưu ở dưới)
                    datMon(row);
                }
            });
        }

        /**
         * Phương thức này được gọi khi người dùng click vào ô
         */
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row; // Lưu lại dòng đang sửa
            button.setText((value == null) ? "" : value.toString());
            return button;
        }
        
        // Ghi đè phương thức này để trả về giá trị của ô (dù không dùng)
        public Object getCellEditorValue() {
            return "+ Đặt";
        }

        // Ghi đè để ngăn chặn việc edit chỉ sau 1 click (phải click đúng nút)
        @Override
        public boolean isCellEditable(java.util.EventObject anEvent) {
            if (anEvent instanceof MouseEvent) {
                // Chỉ kích hoạt editor khi click chuột
                return ((MouseEvent)anEvent).getClickCount() >= 1;
            }
            return super.isCellEditable(anEvent);
        }
    }
}