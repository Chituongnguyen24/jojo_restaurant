package view.ThucDon;

import dao.MonAn_DAO;
import entity.MonAn;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class MonAn_TraCuu_View extends JPanel {

    private JTextField txtTuKhoa;
    private JComboBox<String> cmbTrangThai;
    private JButton btnTimKiem;
    private JTable tblMonAn;
    private DefaultTableModel modelMonAn;
    private MonAn_DAO monAnDAO;

    // Màu sắc
    private static final Color BG_COLOR = new Color(245, 245, 245); // Màu nền chính
    private static final Color HEADER_BG = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color BUTTON_COLOR = new Color(0, 123, 255);
    private static final Color EDIT_BTN_COLOR = new Color(255, 193, 7); // Vàng
    private static final Color DELETE_BTN_COLOR = new Color(220, 53, 69); // Đỏ

    public MonAn_TraCuu_View() {
        monAnDAO = new MonAn_DAO();
        initComponents();
        loadData(); // Tải dữ liệu ban đầu
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 15));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(15, 25, 20, 25));

        // ===== PANEL TÌM KIẾM (NORTH) =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                " Tìm kiếm và lọc món ăn ", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 14), TEXT_COLOR));

        JLabel lblTuKhoa = new JLabel("Từ khóa:");
        lblTuKhoa.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTuKhoa = new JTextField(20);
        txtTuKhoa.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel lblTrangThai = new JLabel("Loại:"); // Giữ tên "Loại" giống ảnh
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbTrangThai = new JComboBox<>(new String[]{"Tất cả", "Còn bán", "Hết"});
        cmbTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbTrangThai.setPreferredSize(new Dimension(120, txtTuKhoa.getPreferredSize().height));

        btnTimKiem = createStyledButton("Tìm kiếm", BUTTON_COLOR, Color.WHITE);
        btnTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnTimKiem.setPreferredSize(new Dimension(100, txtTuKhoa.getPreferredSize().height + 5));
        btnTimKiem.addActionListener(e -> searchData());

        searchPanel.add(lblTuKhoa);
        searchPanel.add(txtTuKhoa);
        searchPanel.add(lblTrangThai);
        searchPanel.add(cmbTrangThai);
        searchPanel.add(btnTimKiem);

        add(searchPanel, BorderLayout.NORTH);

        // ===== PANEL BẢNG (CENTER) =====
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setOpaque(false);

        JLabel tableTitle = new JLabel("Danh sách món ăn tra cứu được");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        tableTitle.setForeground(TEXT_COLOR);
        tableTitle.setBorder(new EmptyBorder(0, 5, 0, 0)); // Padding trái
        tableContainer.add(tableTitle, BorderLayout.NORTH);

        // --- Bảng ---
        String[] columnNames = {"Mã Món", "Tên Món Ăn", "Đơn Giá", "Trạng Thái", "Đường dẫn ảnh", "Sửa", "Xóa"};
        modelMonAn = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Chỉ cho phép edit cột Sửa và Xóa
                return column == 5 || column == 6;
            }
        };
        tblMonAn = new JTable(modelMonAn);
        setupTableStyle(); // Áp dụng style

        // Thêm Renderer và Editor cho nút Sửa/Xóa
        tblMonAn.getColumn("Sửa").setCellRenderer(new ButtonRenderer("Sửa", EDIT_BTN_COLOR, Color.BLACK));
        tblMonAn.getColumn("Sửa").setCellEditor(new ButtonEditor(new JCheckBox(), "Sửa"));
        tblMonAn.getColumn("Xóa").setCellRenderer(new ButtonRenderer("Xóa", DELETE_BTN_COLOR, Color.WHITE));
        tblMonAn.getColumn("Xóa").setCellEditor(new ButtonEditor(new JCheckBox(), "Xóa"));

        // Set độ rộng cột nút
        tblMonAn.getColumnModel().getColumn(5).setPreferredWidth(80);
        tblMonAn.getColumnModel().getColumn(5).setMaxWidth(80);
        tblMonAn.getColumnModel().getColumn(6).setPreferredWidth(80);
        tblMonAn.getColumnModel().getColumn(6).setMaxWidth(80);

        JScrollPane scrollPane = new JScrollPane(tblMonAn);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        // Bọc bảng trong RoundedPanel trắng (giống HoaDon_View)
        RoundedPanel tableWrapper = new RoundedPanel(12, Color.WHITE);
        tableWrapper.setLayout(new BorderLayout());
        tableWrapper.setBorder(new EmptyBorder(10, 10, 10, 10));
        tableWrapper.add(scrollPane, BorderLayout.CENTER);

        tableContainer.add(tableWrapper, BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);
        
        add(tableContainer, BorderLayout.CENTER);

        // ===== PANEL NÚT THÊM (SOUTH) - MỚI =====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomPanel.setOpaque(false); // Nền trong suốt

        JButton btnThemMon = createStyledButton("Thêm Món Mới", new Color(23, 162, 184), Color.WHITE); // Màu xanh dương nhạt
        btnThemMon.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnThemMon.setPreferredSize(new Dimension(150, 40));
        btnThemMon.addActionListener(e -> moDialogThemMon()); // Gọi hàm mở dialog thêm

        bottomPanel.add(btnThemMon);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- CÁC HÀM XỬ LÝ ---

    private void setupTableStyle() {
        tblMonAn.setRowHeight(35);
        tblMonAn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tblMonAn.setGridColor(new Color(230, 230, 230));
        tblMonAn.setSelectionBackground(new Color(200, 220, 255));
        tblMonAn.setSelectionForeground(Color.BLACK);

        JTableHeader header = tblMonAn.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(HEADER_BG);
        header.setForeground(TEXT_COLOR);
        header.setPreferredSize(new Dimension(100, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        // Căn giữa và định dạng cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        tblMonAn.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Mã Món
        tblMonAn.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() { // Đơn giá
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value instanceof Double) {
                    setText(currencyFormatter.format(value));
                    setHorizontalAlignment(SwingConstants.RIGHT);
                }
                return c;
            }
        });
        tblMonAn.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); // Trạng Thái
    }

    // Tải toàn bộ dữ liệu
    public void loadData() { // Để public để Dialog gọi được
        modelMonAn.setRowCount(0); // Xóa dữ liệu cũ
        List<MonAn> dsMonAn = monAnDAO.getAllMonAn();
        populateTable(dsMonAn);
    }

    // Tìm kiếm dữ liệu
    private void searchData() {
        String keyword = txtTuKhoa.getText().trim();
        String statusFilter = cmbTrangThai.getSelectedItem().toString();
        modelMonAn.setRowCount(0); // Xóa dữ liệu cũ
        List<MonAn> dsMonAn = monAnDAO.searchMonAn(keyword, statusFilter);
        populateTable(dsMonAn);
    }

    // Đổ dữ liệu vào bảng
    private void populateTable(List<MonAn> dsMonAn) {
        for (MonAn mon : dsMonAn) {
            modelMonAn.addRow(new Object[]{
                mon.getMaMonAn().trim(),
                mon.getTenMonAn(),
                mon.getDonGia(),
                mon.isTrangThai() ? "Còn bán" : "Hết",
                mon.getImagePath(),
                "Sửa", // Text cho nút Sửa
                "Xóa"  // Text cho nút Xóa
            });
        }
    }

    // Tạo nút bo tròn (Copy từ HoaDon_View)
    private JButton createStyledButton(String text, Color bg, Color fg) {
        // ... (Code hàm createStyledButton giống hệt như trong HoaDon_View)
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
        return btn;
    }

    // Lớp nội bộ RoundedPanel (Copy từ HoaDon_View)
    class RoundedPanel extends JPanel {
        // ... (Code lớp RoundedPanel giống hệt như trong HoaDon_View)
         private final int cornerRadius;
        private final Color bgColor;

        public RoundedPanel(int radius, Color color) {
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

    // Lớp nội bộ ButtonRenderer (Copy và chỉnh sửa từ HoaDon_View)
    class ButtonRenderer extends JButton implements TableCellRenderer {
        private Color bgColor;
        private Color fgColor;

        public ButtonRenderer(String text, Color bg, Color fg) {
            setText(text);
            this.bgColor = bg;
            this.fgColor = fg;
            setOpaque(true); // Đặt true để nền hiển thị
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setFocusPainted(false);
            setBorderPainted(false); // Không vẽ viền mặc định
            //setContentAreaFilled(false); // Bỏ dòng này
            setBorder(new EmptyBorder(5, 5, 5, 5)); // Thêm padding nhỏ
        }

        // Không cần override paintComponent nữa, dùng style mặc định của JButton

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            // Đặt màu nền và màu chữ
            setBackground(bgColor);
            setForeground(fgColor);
            // Nếu muốn đổi màu khi hover/select (phức tạp hơn, tạm bỏ qua)
            // if (isSelected) { ... }
            return this;
        }
    }
    
    private void moDialogThemMon() {
        // Hành động làm mới là gọi lại hàm loadData() của view này
        Runnable refreshAction = () -> loadData();

        ThemMonAn_Dialog dialog = new ThemMonAn_Dialog(
            (JFrame) SwingUtilities.getWindowAncestor(this), // Lấy frame cha
            refreshAction // Truyền Runnable
        );
        dialog.setVisible(true);
    }

    // Lớp nội bộ ButtonEditor (Copy và chỉnh sửa từ HoaDon_View)
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean isPushed;
        private String label;
        private String currentMaMonAn; // Lưu mã món ăn của hàng đang sửa/xóa

        public ButtonEditor(JCheckBox checkBox, String type) {
            super(checkBox);
            this.label = type;
            button = new JButton();
            button.setOpaque(true);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setBorder(new EmptyBorder(5, 5, 5, 5));
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            button.setText(label);
            // Lấy mã món ăn từ cột 0 của hàng đang được chọn
            currentMaMonAn = table.getValueAt(row, 0).toString();

            // Đặt màu nền và chữ dựa trên type
            if ("Sửa".equals(label)) {
                button.setBackground(EDIT_BTN_COLOR); // Màu vàng
                button.setForeground(Color.BLACK);
            } else if ("Xóa".equals(label)) {
                button.setBackground(DELETE_BTN_COLOR); // Màu đỏ
                button.setForeground(Color.WHITE);
            }
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && currentMaMonAn != null) {
                // >>> QUAN TRỌNG: Định nghĩa hành động làm mới NGAY TẠI ĐÂY <<<
                // Hành động này sẽ gọi lại hàm loadData() của MonAn_TraCuu_View
                Runnable refreshAction = () -> loadData();

                if ("Sửa".equals(label)) {
                    // Chạy việc mở dialog trên Event Dispatch Thread (EDT)
                    SwingUtilities.invokeLater(() -> {
                        MonAn monAnToEdit = monAnDAO.getMonAnTheoMa(currentMaMonAn);
                        if (monAnToEdit != null) {
                            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(MonAn_TraCuu_View.this);
                            // >>> GỌI ChinhSuaMonAn_Dialog VỚI RUNNABLE <<<
                            ChinhSuaMonAn_Dialog editDialog = new ChinhSuaMonAn_Dialog(parentFrame, monAnToEdit, refreshAction);
                            editDialog.setVisible(true);
                            // Sau khi dialog đóng, refreshAction sẽ được gọi (nếu sửa thành công)
                        } else {
                           JOptionPane.showMessageDialog(button, "Không tìm thấy món ăn với mã: " + currentMaMonAn, "Lỗi", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } else if ("Xóa".equals(label)) {
                    // Chạy xác nhận xóa trên EDT
                    SwingUtilities.invokeLater(() -> {
                        int confirm = JOptionPane.showConfirmDialog(
                                button,
                                "Bạn có chắc chắn muốn xóa món ăn [" + currentMaMonAn + "]?\nHành động này không thể hoàn tác!",
                                "Xác nhận xóa",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.WARNING_MESSAGE
                        );
                        if (confirm == JOptionPane.YES_OPTION) {
                            if (monAnDAO.deleteMonAn(currentMaMonAn)) {
                                JOptionPane.showMessageDialog(button, "Xóa món ăn thành công!");
                                // Gọi trực tiếp loadData() sau khi xóa thành công
                                loadData();
                            } else {
                                JOptionPane.showMessageDialog(button, "Xóa thất bại! (Món ăn có thể đang nằm trong hóa đơn/phiếu đặt)", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    });
                }
            }
            isPushed = false;
            currentMaMonAn = null; // Reset mã món sau khi xử lý xong
            return label; // Giá trị trả về không quan trọng
        }

        // Ghi đè phương thức này để đảm bảo isPushed được reset đúng cách
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        // Ghi đè phương thức này để reset isPushed khi hủy edit
        @Override
	    public void cancelCellEditing() {
	        isPushed = false;
	        super.cancelCellEditing();
	    }
    }
}