package view.Ban;

import dao.Ban_DAO;
import entity.Ban;
import entity.KhuVuc; // Import KhuVuc
import enums.LoaiBan;
import enums.TrangThaiBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.Vector;

public class ChinhSuaBan_Dialog extends JDialog {

    private final Ban ban;
    private final Ban_DAO banDAO;
    private final Runnable onRefreshCallback;
    private final Map<String, String> khuVucMap;

    private JTextField txtMaBan;
    private JSpinner spinSoCho;
    private JComboBox<String> cmbLoaiBan; // SỬA: Dùng String raw DB
    private JComboBox<String> cmbKhuVuc;

    // Theme
    private static final Color COLOR_PRIMARY = new Color(0, 123, 255);
    private static final Color COLOR_PRIMARY_HOVER = new Color(0, 100, 210);
    private static final Color COLOR_DANGER = new Color(220, 53, 69);
    private static final Color COLOR_DANGER_HOVER = new Color(190, 45, 60);
    private static final Color COLOR_GRAY = new Color(120, 120, 120);
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);

    public ChinhSuaBan_Dialog(Frame owner, Ban ban, Runnable onRefreshCallback) {
        super(owner, "Chỉnh sửa Bàn: " + ban.getMaBan(), true);
        this.ban = ban;
        this.banDAO = new Ban_DAO();
        this.onRefreshCallback = onRefreshCallback;

        this.khuVucMap = banDAO.getDanhSachKhuVuc();

        initComponents();
        loadData();

        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initComponents() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBorder(new EmptyBorder(12, 12, 12, 12));
        root.setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(12, 12, 12, 12))
        );

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã bàn
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        mainPanel.add(createLabel("Mã bàn:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        txtMaBan = new JTextField(20);
        txtMaBan.setFont(FONT_FIELD);
        txtMaBan.setEditable(false);
        txtMaBan.setBackground(new Color(245, 245, 245));
        txtMaBan.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(6, 8, 6, 8)
        ));
        mainPanel.add(txtMaBan, gbc);

        // Số chỗ
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        mainPanel.add(createLabel("Số chỗ:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        spinSoCho = new JSpinner(new SpinnerNumberModel(4, 1, 100, 1));
        spinSoCho.setFont(FONT_FIELD);
        JComponent editor = spinSoCho.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor defEditor = (JSpinner.DefaultEditor) editor;
            defEditor.getTextField().setHorizontalAlignment(SwingConstants.CENTER);
            defEditor.getTextField().setFont(FONT_FIELD);
            defEditor.getTextField().setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(220,220,220)),
                    new EmptyBorder(6, 8, 6, 8)
            ));
            defEditor.getTextField().setBackground(Color.WHITE);
        }
        mainPanel.add(spinSoCho, gbc);

        // Loại bàn (SỬA: Hiển thị String raw DB)
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        mainPanel.add(createLabel("Loại bàn:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        String[] loaiBanValues = new String[LoaiBan.values().length];
        for (int i = 0; i < LoaiBan.values().length; i++) {
            loaiBanValues[i] = LoaiBan.values()[i].name();
        }
        cmbLoaiBan = new JComboBox<>(loaiBanValues);
        cmbLoaiBan.setFont(FONT_FIELD);
        cmbLoaiBan.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                new EmptyBorder(6, 8, 6, 8)
        ));
        mainPanel.add(cmbLoaiBan, gbc);

        // Khu vực
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        mainPanel.add(createLabel("Khu vực:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        cmbKhuVuc = new JComboBox<>(new Vector<>(khuVucMap.values()));
        cmbKhuVuc.setFont(FONT_FIELD);
        cmbKhuVuc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                new EmptyBorder(6, 8, 6, 8)
        ));
        mainPanel.add(cmbKhuVuc, gbc);

        // Add main panel to root
        root.add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 12));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(245, 245, 245)));

        JButton btnHuy = createButton("Hủy", COLOR_GRAY, COLOR_GRAY.darker());
        btnHuy.addActionListener(e -> dispose());

        JButton btnXoa = createButton("Xóa Bàn", COLOR_DANGER, COLOR_DANGER_HOVER);
        btnXoa.addActionListener(e -> xoaBan());

        JButton btnLuu = createButton("Lưu Thay Đổi", COLOR_PRIMARY, COLOR_PRIMARY_HOVER);
        btnLuu.addActionListener(e -> luuThayDoi());

        Dimension btnSize = new Dimension(150, 42);
        btnHuy.setPreferredSize(btnSize);
        btnXoa.setPreferredSize(btnSize);
        btnLuu.setPreferredSize(btnSize);

        btnLuu.setVisible(true);
        btnLuu.setEnabled(true);

        buttonPanel.add(btnHuy);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLuu);

        root.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(root);
    }

    private void loadData() {
        txtMaBan.setText(ban.getMaBan());
        spinSoCho.setValue(ban.getSoCho());
        
        // SỬA: Sử dụng giá trị String raw DB để chọn
        cmbLoaiBan.setSelectedItem(ban.getLoaiBan()); 

        String tenKhuVuc = banDAO.getTenKhuVuc(ban.getKhuVuc().getMaKhuVuc()); // LẤY TÊN KV
        if (tenKhuVuc != null) {
            cmbKhuVuc.setSelectedItem(tenKhuVuc);
        }
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    private JButton createButton(String text, Color background, Color hoverBackground) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));

        double luminance = (0.299 * background.getRed() + 0.587 * background.getGreen() + 0.114 * background.getBlue()) / 255;
        Color fg = luminance > 0.6 ? new Color(40,40,40) : Color.WHITE;
        button.setForeground(fg);

        button.setBackground(background);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(hoverBackground);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) button.setBackground(background);
            }
        });

        button.addPropertyChangeListener("enabled", evt -> {
            boolean enabled = (boolean) evt.getNewValue();
            if (!enabled) {
                button.setBackground(new Color(235, 235, 235));
                button.setForeground(new Color(120, 120, 120));
            } else {
                button.setBackground(background);
                button.setForeground(fg);
            }
        });

        return button;
    }

    private void luuThayDoi() {
        try {
            int soCho = (int) spinSoCho.getValue();
            String loaiBan = (String) cmbLoaiBan.getSelectedItem(); // LẤY STRING RAW DB

            String tenKhuVuc = (String) cmbKhuVuc.getSelectedItem();
            String maKhuVuc = khuVucMap.entrySet().stream()
                    .filter(entry -> entry.getValue().equals(tenKhuVuc))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);

            if (maKhuVuc == null) {
                JOptionPane.showMessageDialog(this, "Khu vực không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // TẠO ENTITY KHUVUC
            KhuVuc khuVuc = new KhuVuc(maKhuVuc);

            Ban banCapNhat = new Ban(
                    ban.getMaBan(),
                    soCho,
                    khuVuc, // ĐÃ SỬA: Dùng Entity KhuVuc
                    loaiBan, // ĐÃ SỬA: Dùng String LoaiBan
                    ban.getTrangThai()
            );

            boolean success = banDAO.capNhatBan(banCapNhat);

            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật bàn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                if (onRefreshCallback != null) onRefreshCallback.run();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật bàn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaBan() {
        String trangThaiString = ban.getTrangThai();

        // SỬA: Chuyển String sang Enum để kiểm tra
        TrangThaiBan trangThai = TrangThaiBan.fromString(trangThaiString);

        if (trangThai == TrangThaiBan.CO_KHACH || trangThai == TrangThaiBan.DA_DAT) {
            JOptionPane.showMessageDialog(this,
                    "Không thể xóa bàn đang có khách hoặc đã được đặt!",
                    "Không thể xóa",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object[] options = {"Xác nhận", "Hủy"};
        int confirm = JOptionPane.showOptionDialog(
                this,
                "Bạn có chắc chắn muốn xóa Bàn " + ban.getMaBan() + "?\nHành động này không thể hoàn tác.",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[1]
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = banDAO.xoaBan(ban.getMaBan());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Đã xóa bàn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    if (onRefreshCallback != null) onRefreshCallback.run();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Xóa bàn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Xóa bàn thất bại! (Lỗi: " + e.getMessage() + ")", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}