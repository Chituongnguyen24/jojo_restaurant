package view.ThucDon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * JDialog độc lập dùng để nhập số lượng và ghi chú cho một món ăn.
 * Trả về một Object[] nếu thành công, hoặc null nếu hủy.
 */
public class NhapSoLuong_Dialog extends JDialog {
    private JSpinner spnSoLuong;
    private JTextField txtGhiChu;
    private JButton btnDat, btnHuy;
    private Object[] ketQua = null; // Khởi tạo là null

    // Màu sắc
    private static final Color BG_COLOR = new Color(245, 245, 245);
    private static final Color BTN_GREEN_BG = new Color(40, 167, 69);
    private static final Color BTN_GRAY_BG = new Color(108, 117, 125);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);
    
    /**
     * Constructor đã được cập nhật để nhận tên món ăn
     * @param parent Frame cha
     * @param tenMonAn Tên món ăn (hiển thị trên tiêu đề)
     */
    public NhapSoLuong_Dialog(Frame parent, String tenMonAn) {
        super(parent, "Nhập số lượng cho: " + tenMonAn, true);
        
        setSize(350, 230); // Tăng kích thước
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(0, 10));
        getContentPane().setBackground(BG_COLOR);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        // ===== FORM NHẬP LIỆU =====
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Số lượng ---
        JLabel lblSoLuong = new JLabel("Nhập số lượng:");
        lblSoLuong.setFont(FONT_LABEL);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblSoLuong, gbc);

        spnSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spnSoLuong.setFont(FONT_LABEL);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        pnlForm.add(spnSoLuong, gbc);

        // --- Ghi chú ---
        JLabel lblGhiChu = new JLabel("Nhập ghi chú:");
        lblGhiChu.setFont(FONT_LABEL);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblGhiChu, gbc);

        txtGhiChu = new JTextField();
        txtGhiChu.setFont(FONT_LABEL);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        pnlForm.add(txtGhiChu, gbc);

        add(pnlForm, BorderLayout.CENTER);

        // ===== NÚT BẤM =====
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnHuy = createStyledButton("Hủy", BTN_GRAY_BG, TEXT_COLOR);
        btnDat = createStyledButton("Đặt", BTN_GREEN_BG, TEXT_COLOR);
        
        pnlButtons.add(btnHuy);
        pnlButtons.add(btnDat);

        add(pnlButtons, BorderLayout.SOUTH);

        // ===== SỰ KIỆN =====
        btnDat.addActionListener(this::dat);
        btnHuy.addActionListener(e -> dispose()); // Chỉ cần đóng, ketQua vẫn là null
        getRootPane().setDefaultButton(btnDat);
    }

    /**
     * Xử lý sự kiện khi nhấn nút "Đặt"
     */
    private void dat(ActionEvent e) {
        int soLuong = (int) spnSoLuong.getValue();
        String ghiChu = txtGhiChu.getText().trim();
        // Lưu kết quả vào biến
        this.ketQua = new Object[]{soLuong, ghiChu};
        dispose(); // Đóng dialog
    }

    /**
     * Hiển thị dialog và trả về kết quả.
     * @return Object[] nếu nhấn "Đặt", null nếu nhấn "Hủy" hoặc đóng.
     */
    public Object[] showDialog() {
        setVisible(true);
        // Code sẽ bị block ở đây cho đến khi dialog (dispose())
        return this.ketQua; // Trả về kết quả
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
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
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
}