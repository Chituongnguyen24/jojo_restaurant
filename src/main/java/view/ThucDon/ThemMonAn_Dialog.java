package view.ThucDon;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter; // Thêm import
import dao.MonAn_DAO;
import entity.MonAn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File; // Thêm import
import java.io.IOException; // Thêm import
import java.nio.file.Files; // Thêm import
import java.nio.file.Path; // Thêm import
import java.nio.file.Paths; // Thêm import
import java.nio.file.StandardCopyOption; // Thêm import

public class ThemMonAn_Dialog extends JDialog {
	private Runnable refreshCallback;
    private MonAn_DAO monAnDAO = new MonAn_DAO();
    
    private JTextField txtMaMon;
    private JTextField txtTenMon;
    private JTextField txtDonGia;
    private JTextField txtImagePath;
    private JCheckBox chkTrangThai;
    private JButton btnLuu;
    private JButton btnHuy;
    private JButton btnChonAnh; // === Nút mới ===
    private JLabel lblAnhPreview; // === Label xem trước ảnh ===

    public ThemMonAn_Dialog(JFrame owner, Runnable refreshCallback) { // Tham số phải là (JFrame, Runnable)
        super(owner, "Thêm Món Ăn Mới", true);
        this.refreshCallback = refreshCallback; // Gán callback
        initComponents();
        loadMaMonAnTuDong();
    }

    private void initComponents() {
        setSize(500, 450); // Tăng kích thước
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // Panel chính
        JPanel panelMain = new JPanel(new GridBagLayout());
        panelMain.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelMain.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã món
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panelMain.add(createLabel("Mã món:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.gridwidth = 2; // Kéo dài 2 cột
        txtMaMon = new JTextField();
        txtMaMon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtMaMon.setEditable(false);
        txtMaMon.setBackground(new Color(230, 230, 230));
        panelMain.add(txtMaMon, gbc);
        
        // Tên món
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.gridwidth = 1;
        panelMain.add(createLabel("Tên món:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; gbc.gridwidth = 2;
        txtTenMon = new JTextField();
        txtTenMon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelMain.add(txtTenMon, gbc);

        // Đơn giá
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.gridwidth = 1;
        panelMain.add(createLabel("Đơn giá:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; gbc.gridwidth = 2;
        txtDonGia = new JTextField();
        txtDonGia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelMain.add(txtDonGia, gbc);
        
        // Đường dẫn ảnh (sửa lại)
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.gridwidth = 1;
        panelMain.add(createLabel("Đường dẫn ảnh:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0; gbc.gridwidth = 1;
        txtImagePath = new JTextField("images/mon an/placeholder.png"); 
        txtImagePath.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtImagePath.setEditable(false); // Không cho sửa trực tiếp
        panelMain.add(txtImagePath, gbc);
        
        gbc.gridx = 2; gbc.gridy = 3; gbc.weightx = 0; gbc.gridwidth = 1;
        btnChonAnh = new JButton("Chọn Ảnh");
        btnChonAnh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnChonAnh.addActionListener(e -> chonAnh());
        panelMain.add(btnChonAnh, gbc);

        // Trạng thái
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.gridwidth = 1;
        panelMain.add(createLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0; gbc.gridwidth = 2;
        chkTrangThai = new JCheckBox("Có sẵn (Đang bán)");
        chkTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chkTrangThai.setBackground(Color.WHITE);
        chkTrangThai.setSelected(true); 
        panelMain.add(chkTrangThai, gbc);
        
        // Xem trước ảnh
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0; gbc.gridwidth = 1;
        panelMain.add(createLabel("Xem trước:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 1.0; gbc.gridwidth = 2;
        lblAnhPreview = new JLabel();
        lblAnhPreview.setPreferredSize(new Dimension(100, 100));
        lblAnhPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblAnhPreview.setHorizontalAlignment(SwingConstants.CENTER);
        updatePreviewImage("images/mon an/placeholder.png");
        panelMain.add(lblAnhPreview, gbc);


        // Panel nút bấm
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panelButtons.setBackground(Color.WHITE);
        panelButtons.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));

        btnHuy = new JButton("Hủy");
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnHuy.setBackground(new Color(220, 53, 69));
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setFocusPainted(false);
        btnHuy.addActionListener(e -> dispose());

        btnLuu = new JButton("Thêm Món");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(40, 167, 69));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        btnLuu.addActionListener(e -> themMonAn());

        panelButtons.add(btnHuy);
        panelButtons.add(btnLuu);

        add(panelMain, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }
    
    private void loadMaMonAnTuDong() {
        String newID = monAnDAO.getMaMonAnTuDong();
        txtMaMon.setText(newID);
    }

    /**
     * === HÀM MỚI ===
     * Mở JFileChooser để chọn ảnh
     */
    private void chonAnh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn hình ảnh món ăn");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh (jpg, png, gif)", "jpg", "png", "gif", "jpeg"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Sao chép file vào dự án và lấy đường dẫn tương đối
            String relativePath = copyImageToProject(selectedFile);
            if (relativePath != null) {
                txtImagePath.setText(relativePath.replace(File.separator, "/"));
                updatePreviewImage(relativePath);
            }
        }
    }
    
    /**
     * === HÀM MỚI ===
     * Sao chép file được chọn vào thư mục "images/mon an" của dự án
     */
    private String copyImageToProject(File sourceFile) {
        try {
            // Đường dẫn thư mục đích (tương đối)
            Path targetDir = Paths.get("images", "mon an");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir); // Tạo thư mục nếu chưa có
            }
            
            // Tạo đường dẫn file đích
            Path targetPath = targetDir.resolve(sourceFile.getName());
            
            // Sao chép file (ghi đè nếu đã tồn tại)
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            
            // Trả về đường dẫn tương đối (ví dụ: images\mon an\ten_file.jpg)
            return targetPath.toString();
            
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi sao chép tệp tin ảnh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    /**
     * === HÀM MỚI ===
     * Cập nhật ảnh xem trước
     */
    private void updatePreviewImage(String path) {
        ImageIcon icon = new ImageIcon(path);
        Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        lblAnhPreview.setIcon(new ImageIcon(img));
    }


    private void themMonAn() {
        try {
            String maMon = txtMaMon.getText().trim(); 
            String tenMon = txtTenMon.getText().trim();
            double donGia = Double.parseDouble(txtDonGia.getText().trim());
            String imagePath = txtImagePath.getText().trim(); // Lấy đường dẫn từ text field
            boolean trangThai = chkTrangThai.isSelected();
            
            if (tenMon.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên món không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (donGia <= 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MonAn monAnMoi = new MonAn(maMon, tenMon, donGia, trangThai, imagePath);

            if (monAnDAO.themMonAn(monAnMoi)) {
                JOptionPane.showMessageDialog(this, "Đã thêm món ăn mới thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm món ăn! (Mã Món đã tồn tại)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}