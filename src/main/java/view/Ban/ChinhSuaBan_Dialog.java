package view.Ban;

import dao.Ban_DAO;
import entity.Ban;
import enums.LoaiBan;
import enums.TrangThaiBan;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Vector;

public class ChinhSuaBan_Dialog extends JDialog {

    private final Ban ban; 
    private final Ban_DAO banDAO;
    private final Runnable onRefreshCallback; 
    private final Map<String, String> khuVucMap;

    private JTextField txtMaBan;
    private JSpinner spinSoCho;
    private JComboBox<LoaiBan> cmbLoaiBan;
    private JComboBox<String> cmbKhuVuc;

    private static final Color COLOR_PRIMARY = new Color(0, 123, 255);
    private static final Color COLOR_DANGER = new Color(220, 53, 69);
    private static final Color COLOR_WHITE = Color.WHITE;
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
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(createLabel("Mã bàn:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtMaBan = new JTextField(20);
        txtMaBan.setFont(FONT_FIELD);
        txtMaBan.setEditable(false);
        txtMaBan.setBackground(new Color(230, 230, 230));
        mainPanel.add(txtMaBan, gbc);

        // Số chỗ
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        mainPanel.add(createLabel("Số chỗ:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0;
        spinSoCho = new JSpinner(new SpinnerNumberModel(4, 1, 100, 1));
        spinSoCho.setFont(FONT_FIELD);
        mainPanel.add(spinSoCho, gbc);

        // Loại bàn
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        mainPanel.add(createLabel("Loại bàn:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0;
        cmbLoaiBan = new JComboBox<>(LoaiBan.values());
        cmbLoaiBan.setFont(FONT_FIELD);
        mainPanel.add(cmbLoaiBan, gbc);

        // Khu vực
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0;
        mainPanel.add(createLabel("Khu vực:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0;
        cmbKhuVuc = new JComboBox<>(new Vector<>(khuVucMap.values())); 
        cmbKhuVuc.setFont(FONT_FIELD);
        mainPanel.add(cmbKhuVuc, gbc);

        // Nút bấm
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 220, 220)));
        
        JButton btnHuy = createButton("Hủy", Color.GRAY);
        btnHuy.addActionListener(e -> dispose());

        JButton btnXoa = createButton("Xóa Bàn", COLOR_DANGER);
        btnXoa.addActionListener(e -> xoaBan());
        
        JButton btnLuu = createButton("Lưu Thay Đổi", COLOR_PRIMARY);
        btnLuu.addActionListener(e -> luuThayDoi());

        buttonPanel.add(btnHuy);
        buttonPanel.add(btnXoa);
        buttonPanel.add(btnLuu);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    //tải dữ liệu của bàn vào các trường
    private void loadData() {
        txtMaBan.setText(ban.getMaBan());
        spinSoCho.setValue(ban.getSoCho());
        cmbLoaiBan.setSelectedItem(ban.getLoaiBan());
        
        //tìm 'tenKhuVuc' từ 'maKhuVuc' của bàn
        String tenKhuVuc = khuVucMap.get(ban.getMaKhuVuc());
        if (tenKhuVuc != null) {
            cmbKhuVuc.setSelectedItem(tenKhuVuc);
        }
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

    private void luuThayDoi() {
        try {
            int soCho = (int) spinSoCho.getValue();
            LoaiBan loaiBan = (LoaiBan) cmbLoaiBan.getSelectedItem();
            
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

            //tạo đối tượng Ban mới với thông tin cập nhật
            Ban banCapNhat = new Ban(
                ban.getMaBan(),
                soCho,
                loaiBan,
                maKhuVuc,
                ban.getTrangThai()
            );

            boolean success = banDAO.capNhatBan(banCapNhat);

            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật bàn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                onRefreshCallback.run(); //làm mới Ban_View
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật bàn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaBan() {
        TrangThaiBan trangThai = ban.getTrangThai();
        
        //Kiểm tra 
        if (trangThai == TrangThaiBan.CO_KHACH || trangThai == TrangThaiBan.DA_DAT) {
            JOptionPane.showMessageDialog(this,
                "Không thể xóa bàn đang có khách hoặc đã được đặt!",
                "Không thể xóa",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn xóa Bàn " + ban.getMaBan() + "?\nHành động này không thể hoàn tác.",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = banDAO.xoaBan(ban.getMaBan());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Đã xóa bàn thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    onRefreshCallback.run(); // Làm mới Ban_View
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