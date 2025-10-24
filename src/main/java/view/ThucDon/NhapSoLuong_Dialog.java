package view.ThucDon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Dialog (từ image_247283.png)
public class NhapSoLuong_Dialog extends JDialog {
    private JSpinner spnSoLuong;
    private JTextField txtGhiChu;
    private JButton btnDat, btnHuy;
    private Object[] ketQua;

    public NhapSoLuong_Dialog(Frame parent) {
        super(parent, "Nhập thông tin", true);
        setSize(320, 200); // Tăng kích thước nhẹ
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(0, 10)); // Khoảng cách dọc
        // Màu nền chung
        getContentPane().setBackground(new Color(245, 245, 245));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15)); // Padding

        // ===== FORM NHẬP LIỆU =====
        JPanel pnlForm = new JPanel(new GridBagLayout()); // Dùng GridBagLayout để căn chỉnh tốt hơn
        pnlForm.setOpaque(false); // Nền trong suốt
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Khoảng cách ô
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Số lượng ---
        JLabel lblSoLuong = new JLabel("Nhập số lượng:");
        lblSoLuong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST; // Căn trái label
        pnlForm.add(lblSoLuong, gbc);

        spnSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spnSoLuong.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0; // Cho phép spinner giãn ra
        pnlForm.add(spnSoLuong, gbc);

        // --- Ghi chú ---
        JLabel lblGhiChu = new JLabel("Nhập ghi chú:");
        lblGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        pnlForm.add(lblGhiChu, gbc);

        txtGhiChu = new JTextField();
        txtGhiChu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        pnlForm.add(txtGhiChu, gbc);

        add(pnlForm, BorderLayout.CENTER);

        // ===== NÚT BẤM =====
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false); // Nền trong suốt

        // Dùng hàm tạo nút mới
        btnDat = createStyledButton("Đặt", new Color(76, 175, 80), Color.WHITE); // Xanh lá
        btnHuy = createStyledButton("Hủy", new Color(108, 117, 125), Color.WHITE); // Xám

        pnlButtons.add(btnHuy);
        pnlButtons.add(btnDat);

        add(pnlButtons, BorderLayout.SOUTH);

        // ===== SỰ KIỆN (Giữ nguyên) =====
        btnDat.addActionListener(e -> dat());
        btnHuy.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnDat); // Enter để "Đặt"
    }

    private void dat() {
        int soLuong = (int) spnSoLuong.getValue();
        String ghiChu = txtGhiChu.getText().trim();
        this.ketQua = new Object[]{soLuong, ghiChu};
        dispose();
    }

    // Hàm này để GoiMon_View gọi để lấy kết quả
    public Object[] showDialog() {
        setVisible(true);
        // Sẽ bị block ở đây cho đến khi dialog đóng
        return this.ketQua; // Trả về {soLuong, ghiChu} hoặc null
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