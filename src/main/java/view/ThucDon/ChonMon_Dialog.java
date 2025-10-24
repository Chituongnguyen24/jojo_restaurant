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

import java.awt.*;
import java.util.List;

// Dialog (từ image_246f95.jpg)
public class ChonMon_Dialog extends JDialog {
    private MonAn_DAO monAnDAO;
    private DatBan_DAO datBanDAO;
    private PhieuDatBan phieuDatBan; // Phiếu đặt bàn hiện tại

    private JTable tblMonAn;
    private DefaultTableModel modelMonAn;
    private JButton btnDat, btnHuy;

    public ChonMon_Dialog(Frame parent, PhieuDatBan phieuDatBan) {
        super(parent, "Chọn món ăn cho phiếu " + phieuDatBan.getMaPhieu().trim(), true);
        this.phieuDatBan = phieuDatBan;
        this.monAnDAO = new MonAn_DAO();
        this.datBanDAO = new DatBan_DAO();

        setSize(650, 450); // Tăng kích thước nhẹ
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(0, 10)); // Khoảng cách dọc
        // Màu nền chung
        getContentPane().setBackground(new Color(245, 245, 245));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15)); // Padding

        // ===== TIÊU ĐỀ (Mới) =====
        JLabel titleLabel = new JLabel("Danh sách món ăn", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(50, 50, 50));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Padding dưới
        add(titleLabel, BorderLayout.NORTH);

        // ===== BẢNG MÓN ĂN =====
        modelMonAn = new DefaultTableModel(new String[]{"Mã món ăn", "Tên món", "Đơn giá", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        // Dùng hàm tạo bảng mới
        tblMonAn = createStyledTable(modelMonAn);
        tblMonAn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(tblMonAn);
        // Bỏ viền cũ, dùng viền của RoundedPanel
        scroll.setBorder(BorderFactory.createEmptyBorder());

        // Bọc bảng trong RoundedPanel trắng
        RoundedPanel tableWrapper = new RoundedPanel(12, Color.WHITE);
        tableWrapper.setLayout(new BorderLayout());
        tableWrapper.setBorder(new EmptyBorder(5, 5, 5, 5)); // Padding nhỏ bên trong
        tableWrapper.add(scroll, BorderLayout.CENTER);

        add(tableWrapper, BorderLayout.CENTER);

        // ===== NÚT BẤM =====
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        // Dùng hàm tạo nút mới
        btnDat = createStyledButton("Đặt món đã chọn", new Color(76, 175, 80), Color.WHITE); // Xanh lá
        btnHuy = createStyledButton("Đóng", new Color(108, 117, 125), Color.WHITE); // Xám
        // Tăng kích thước nút "Đặt"
        btnDat.setPreferredSize(new Dimension(150, 35));

        pnlButtons.add(btnHuy);
        pnlButtons.add(btnDat);

        add(pnlButtons, BorderLayout.SOUTH);

        // ===== SỰ KIỆN (Giữ nguyên) =====
        btnDat.addActionListener(e -> chonVaDatMon());
        btnHuy.addActionListener(e -> dispose());
        tblMonAn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Double-click để đặt
                    chonVaDatMon();
                }
            }
        });

        // Tải dữ liệu
        taiDanhSachMonAn();
    }

    // ===== HÀM TRỢ GIÚP TẠO JTABLE (Mới) =====
    /**
     * Tạo JTable với style chung (Sao chép từ MonAn_View)
     */
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(35); // Chiều cao hàng
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setGridColor(new Color(235, 235, 235));
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowGrid(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(50, 50, 50));
        header.setPreferredSize(new Dimension(header.getWidth(), 35)); // Chiều cao tiêu đề
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Căn giữa cột Giá và Trạng thái (Tùy chọn)
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        if (model.getColumnCount() > 2) table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        if (model.getColumnCount() > 3) table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        return table;
    }

    // ===== LỚP RoundedPanel (Cần thêm vào cuối file nếu chưa có) =====
    class RoundedPanel extends JPanel {
        // ... (Code giống như đã dán vào MonAn_View) ...
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

    private void taiDanhSachMonAn() {
        modelMonAn.setRowCount(0);
        List<MonAn> ds = monAnDAO.getAllMonAn(); // Lấy tất cả món
        for (MonAn mon : ds) {
            if (mon.isTrangThai()) { // Chỉ hiển thị món "Còn bán" (trangThai = true)
                modelMonAn.addRow(new Object[]{
                    mon.getMaMonAn().trim(),
                    mon.getTenMonAn(),
                    String.format("%,.0f", mon.getDonGia()), // Định dạng tiền
                    mon.isTrangThai() ? "Còn bán" : "Hết"
                });
            }
        }
    }

    private void chonVaDatMon() {
        int selectedRow = tblMonAn.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một món ăn!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String maMonAn = (String) modelMonAn.getValueAt(selectedRow, 0);

        // 1. Mở dialog Nhập số lượng
        NhapSoLuong_Dialog soLuongDialog = new NhapSoLuong_Dialog((Frame) getParent());
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
                JOptionPane.showMessageDialog(this, "Thêm món thành công!");
                dispose(); // Đóng dialog chọn món
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm món vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
        // Nếu người dùng nhấn "Hủy" (ketQua == null), không làm gì
    }
    private JButton createStyledButton(String text, Color bg, Color fg) {
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
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13)); // Font chung cho nút dialog
        btn.setPreferredSize(new Dimension(80, 35)); // Kích thước nút dialog
        return btn;
    }
}