package view.ThucDon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeListener; // SỬA: THÊM IMPORT


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

        // SỬA: Bỏ min/max (để null) cho phép nhập tự do, để ta tự validate
        spnSoLuong = new JSpinner(new SpinnerNumberModel(1, null, null, 1)); 
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
    // ========================================================
    // === HÀM ĐÃ SỬA: FIX LỖI TRÀN SỐ VÀ THÔNG BÁO LỖI ===
    // ========================================================
    private void dat(ActionEvent e) {
        String soLuongText;
        JComponent editor = spnSoLuong.getEditor();

        // Lấy text thô từ editor của JSpinner
        if (editor instanceof JSpinner.DefaultEditor) {
            soLuongText = ((JSpinner.DefaultEditor) editor).getTextField().getText().trim();
        } else {
            // Fallback (ít khi xảy ra, nhưng để an toàn)
            try {
                spnSoLuong.commitEdit();
                soLuongText = String.valueOf(spnSoLuong.getValue());
            } catch (java.text.ParseException ex) {
                 JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập một số nguyên hợp lệ.", 
                    "Lỗi định dạng", 
                    JOptionPane.ERROR_MESSAGE);
                 spnSoLuong.setValue(1);
                 return;
            }
        }

        long soLuongLong;
        int soLuong;

        // BƯỚC 1: Kiểm tra định dạng (phải là số, không phải "abc")
        try {
            // Thử parse sang Long để bắt số cực lớn
            soLuongLong = Long.parseLong(soLuongText);
        } catch (NumberFormatException ex) {
            // Nếu parse Long thất bại -> chắc chắn là lỗi định dạng (ví dụ: "abc", "1.5")
            JOptionPane.showMessageDialog(this, 
                "Số lượng phải là một con số nguyên (ví dụ: 1, 2, 3).", 
                "Lỗi định dạng", 
                JOptionPane.ERROR_MESSAGE);
            spnSoLuong.setValue(1); // Reset
            return; // Không đóng
        }

        // BƯỚC 2: Kiểm tra tràn số (quá lớn/quá nhỏ cho kiểu int)
        if (soLuongLong > Integer.MAX_VALUE) {
            JOptionPane.showMessageDialog(this, 
                "Số lượng nhập vào quá lớn (tràn số).\nVui lòng nhập số nhỏ hơn.", 
                "Lỗi tràn số", 
                JOptionPane.ERROR_MESSAGE);
            spnSoLuong.setValue(100); // Reset về max logic (100)
            return; // Không đóng
        }
        if (soLuongLong < Integer.MIN_VALUE) {
            JOptionPane.showMessageDialog(this, 
                "Số lượng nhập vào quá nhỏ (tràn số).", 
                "Lỗi tràn số", 
                JOptionPane.ERROR_MESSAGE);
            spnSoLuong.setValue(1); // Reset về min logic
            return; // Không đóng
        }
        
        // Nếu qua 2 bước trên, số đã hợp lệ, ép kiểu về int
        soLuong = (int) soLuongLong;

        // BƯỚC 3: Kiểm tra logic nghiệp vụ (1 -> 100)
        if (soLuong > 100) {
            JOptionPane.showMessageDialog(this, 
                "Số lượng không được vượt quá 100.", 
                "Lỗi số lượng", 
                JOptionPane.ERROR_MESSAGE);
            spnSoLuong.setValue(100); // Reset về 100
            return; // Không đóng dialog
        }
        
        if (soLuong < 1) { 
            JOptionPane.showMessageDialog(this, 
                "Số lượng phải lớn hơn 0.", 
                "Lỗi số lượng", 
                JOptionPane.ERROR_MESSAGE);
            spnSoLuong.setValue(1); // Reset về 1
            return; // Không đóng dialog
        }
        
        // Nếu mọi thứ OK, gán kết quả và đóng
        String ghiChu = txtGhiChu.getText().trim();
        this.ketQua = new Object[]{soLuong, ghiChu};
        dispose(); // Đóng dialog
    }
    // ========================================================
    // === KẾT THÚC SỬA LỖI ===
    // ========================================================

    /**
     * Hiển thị dialog và trả về kết quả.
     * @return Object[] nếu nhấn "Đặt", null nếu nhấn "Hủy" hoặc đóng.
     */
    public Object[] showDialog() {
        setVisible(true);
        return this.ketQua;
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