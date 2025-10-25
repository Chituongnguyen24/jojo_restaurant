package view.Ban;

import dao.Ban_DAO;
import entity.Ban;
import enums.LoaiBan;
import enums.TrangThaiBan;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ThemBan_Dialog extends JDialog {

    private final Ban_DAO banDAO;
    private final Runnable onRefreshCallback;
    private final Map<String, String> khuVucMap;

    private JTextField txtMaBan;
    private JSpinner spinSoCho;
    private JComboBox<LoaiBan> cmbLoaiBan;
    private JComboBox<String> cmbKhuVuc;

    private static final Color COLOR_PRIMARY = new Color(0, 123, 255);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Font FONT_LABEL = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_FIELD = new Font("Segoe UI", Font.PLAIN, 14);

    public ThemBan_Dialog(Frame owner, Runnable onRefreshCallback) {
        super(owner, "Thêm Bàn Mới", true);
        this.banDAO = new Ban_DAO();
        this.onRefreshCallback = onRefreshCallback;
        
        //lấy danh sách khu vực để điền vào ComboBox
        this.khuVucMap = banDAO.getDanhSachKhuVuc();

        initComponents();
        setSize(450, 350);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã bàn
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(createLabel("Mã bàn:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 1.0;
        txtMaBan = new JTextField(20);
        txtMaBan.setFont(FONT_FIELD);
        mainPanel.add(txtMaBan, gbc);

        // Số chỗ
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0;
        mainPanel.add(createLabel("Số chỗ:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 1.0;
        spinSoCho = new JSpinner(new SpinnerNumberModel(4, 1, 100, 1));
        spinSoCho.setFont(FONT_FIELD);
        mainPanel.add(spinSoCho, gbc);

        // Loại bàn
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.weightx = 0;
        mainPanel.add(createLabel("Loại bàn:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        gbc.weightx = 1.0;
        cmbLoaiBan = new JComboBox<>(LoaiBan.values());
        cmbLoaiBan.setFont(FONT_FIELD);
        mainPanel.add(cmbLoaiBan, gbc);

        // Khu vực
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0;
        mainPanel.add(createLabel("Khu vực:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.weightx = 1.0;
        //Tên Khu Vực
        cmbKhuVuc = new JComboBox<>(khuVucMap.values().toArray(new String[0])); 
        cmbKhuVuc.setFont(FONT_FIELD);
        mainPanel.add(cmbKhuVuc, gbc);

        // Nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        JButton btnThem = createButton("Thêm Bàn", COLOR_PRIMARY);
        btnThem.addActionListener(e -> themBan());
        JButton btnHuy = createButton("Hủy", Color.GRAY);
        btnHuy.addActionListener(e -> dispose());
        buttonPanel.add(btnHuy);
        buttonPanel.add(btnThem);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_LABEL);
        return label;
    }

    private JButton createButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(background);
        button.setForeground(COLOR_WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void themBan() {
        String maBan = txtMaBan.getText().trim();
        int soCho = (int) spinSoCho.getValue();
        LoaiBan loaiBan = (LoaiBan) cmbLoaiBan.getSelectedItem();
        
        //tenKhuVuc từ ComboBox
        String tenKhuVuc = (String) cmbKhuVuc.getSelectedItem();
        //tìm maKhuVuc tương ứng
        String maKhuVuc = khuVucMap.entrySet().stream()
                .filter(entry -> entry.getValue().equals(tenKhuVuc))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);

        //kiểm tra dữ liệu
        if (maBan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã bàn không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (maKhuVuc == null) {
            JOptionPane.showMessageDialog(this, "Khu vực không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //tạo Ban mới
        Ban newBan = new Ban(
            maBan,
            soCho,
            loaiBan,
            maKhuVuc,
            TrangThaiBan.TRONG
        );

        boolean success = banDAO.themBan(newBan);

        if (success) {
            JOptionPane.showMessageDialog(this, "Thêm bàn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            onRefreshCallback.run();
            dispose(); 
        } else {
            JOptionPane.showMessageDialog(this, "Thêm bàn thất bại! (Có thể do trùng mã bàn)", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}