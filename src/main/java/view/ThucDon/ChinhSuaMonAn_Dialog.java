package view.ThucDon;

import dao.MonAn_DAO;
import entity.MonAn;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class ChinhSuaMonAn_Dialog extends JDialog {
    private MonAn monAn;
    private Runnable refreshCallback;
    private MonAn_DAO monAnDAO = new MonAn_DAO();
    
    private JTextField txtTenMon;
    private JTextField txtDonGia;
    private JTextField txtImagePath; 
    private JComboBox<String> cmbLoaiMon; // THÊM
    private JLabel lblTrangThaiHienTai;
    private JLabel lblAnhPreview; 
    private JButton btnLuu;
    private JButton btnDoiTrangThai;
    private JButton btnXoa;
    private JButton btnChonAnh; 

    public ChinhSuaMonAn_Dialog(JFrame owner, MonAn monAn, Runnable refreshCallback) { 
        super(owner, "Chỉnh sửa món ăn: " + monAn.getTenMonAn(), true);
        this.monAn = monAn;
        this.refreshCallback = refreshCallback; 
        initComponents(); 
        loadData();
        loadLoaiMonAn();
    }

    private void initComponents() {
        setTitle("Chỉnh sửa món ăn: " + monAn.getTenMonAn());
        setSize(700, 400); 
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);
        
        // === Panel bên trái (Chứa ảnh và nút chọn ảnh) ===
        JPanel panelLeft = new JPanel();
        panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.Y_AXIS));
        panelLeft.setBackground(Color.WHITE);
        panelLeft.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        lblAnhPreview = new JLabel();
        lblAnhPreview.setPreferredSize(new Dimension(200, 200));
        lblAnhPreview.setMinimumSize(new Dimension(200, 200));
        lblAnhPreview.setMaximumSize(new Dimension(200, 200));
        lblAnhPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        lblAnhPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lblAnhPreview.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        btnChonAnh = new JButton("Thay đổi ảnh");
        btnChonAnh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnChonAnh.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnChonAnh.addActionListener(e -> chonAnh());
        
        panelLeft.add(lblAnhPreview);
        panelLeft.add(Box.createVerticalStrut(10));
        panelLeft.add(btnChonAnh);
        panelLeft.add(Box.createVerticalGlue());

        // === Panel bên phải (Chứa thông tin) ===
        JPanel panelRight = new JPanel(new BorderLayout(10, 10));
        panelRight.setBackground(Color.WHITE);
        panelRight.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 20));

        // Panel chính (Grid) cho các trường thông tin
        JPanel panelMain = new JPanel(new GridBagLayout());
        panelMain.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Tên món
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panelMain.add(createLabel("Tên món:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtTenMon = new JTextField();
        txtTenMon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelMain.add(txtTenMon, gbc);

        // Đơn giá
        gbc.gridx = 0; gbc.gridy = 1;
        panelMain.add(createLabel("Đơn giá:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtDonGia = new JTextField();
        txtDonGia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panelMain.add(txtDonGia, gbc);
        
        // Loại món (THÊM)
        gbc.gridx = 0; gbc.gridy = 2;
        panelMain.add(createLabel("Loại món:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        cmbLoaiMon = new JComboBox<>();
        cmbLoaiMon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbLoaiMon.setBackground(Color.WHITE);
        panelMain.add(cmbLoaiMon, gbc);
        
        // Đường dẫn ảnh
        gbc.gridx = 0; gbc.gridy = 3;
        panelMain.add(createLabel("Đường dẫn ảnh:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        txtImagePath = new JTextField();
        txtImagePath.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtImagePath.setEditable(false);
        txtImagePath.setBackground(new Color(230, 230, 230));
        panelMain.add(txtImagePath, gbc);

        // Trạng thái hiện tại
        gbc.gridx = 0; gbc.gridy = 4;
        panelMain.add(createLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        lblTrangThaiHienTai = new JLabel();
        lblTrangThaiHienTai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTrangThaiHienTai.setHorizontalAlignment(SwingConstants.CENTER);
        lblTrangThaiHienTai.setOpaque(true);
        lblTrangThaiHienTai.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panelMain.add(lblTrangThaiHienTai, gbc);
        
        // Nút đổi trạng thái
        gbc.gridx = 1; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        btnDoiTrangThai = new JButton("Đổi trạng thái");
        btnDoiTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnDoiTrangThai.addActionListener(e -> doiTrangThai());
        panelMain.add(btnDoiTrangThai, gbc);

        // Panel nút bấm (Xóa, Lưu)
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelButtons.setBackground(Color.WHITE);

        // Nút xóa
        btnXoa = new JButton("Xóa món");
        btnXoa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXoa.setBackground(new Color(220, 53, 69));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setFocusPainted(false);
        btnXoa.addActionListener(e -> xoaMonAn());

        // Nút lưu
        btnLuu = new JButton("Lưu thay đổi");
        btnLuu.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLuu.setBackground(new Color(40, 167, 69));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        btnLuu.addActionListener(e -> luuThayDoi());

        panelButtons.add(btnXoa);
        panelButtons.add(btnLuu);
        
        panelRight.add(panelMain, BorderLayout.CENTER);
        panelRight.add(panelButtons, BorderLayout.SOUTH);

        add(panelLeft, BorderLayout.WEST);
        add(panelRight, BorderLayout.CENTER);
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        return label;
    }

    private void loadLoaiMonAn() {
        // Lấy danh sách loại món ăn duy nhất từ DAO
        List<String> loaiMonAnList = monAnDAO.getUniqueLoaiMonAn();
        cmbLoaiMon.removeAllItems();
        
        for (String loai : loaiMonAnList) {
            cmbLoaiMon.addItem(loai);
        }
    }

    private void loadData() {
        txtTenMon.setText(monAn.getTenMonAn());
        txtDonGia.setText(String.valueOf((int)monAn.getDonGia()));
        txtImagePath.setText(monAn.getImagePath()); 
        cmbLoaiMon.setSelectedItem(monAn.getLoaiMonAn()); // Tải Loại món ăn
        updatePreviewImage(monAn.getImagePath()); 
        capNhatGiaoDienTrangThai();
    }
    
    /**
     * Tải và scale ảnh xem trước
     */
    private void updatePreviewImage(String path) {
        ImageIcon icon = null;
        if (path != null && !path.isEmpty()) {
            icon = new ImageIcon(path);
        }
        
        if (icon == null || icon.getIconWidth() == -1) {
            icon = new ImageIcon("images/mon an/placeholder.png");
        }
        
        Image img = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        lblAnhPreview.setIcon(new ImageIcon(img));
    }
    
    /**
     * Mở JFileChooser để chọn ảnh
     */
    private void chonAnh() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn hình ảnh món ăn");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Hình ảnh (jpg, png, gif)", "jpg", "png", "gif", "jpeg"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String relativePath = copyImageToProject(selectedFile);
            if (relativePath != null) {
                txtImagePath.setText(relativePath.replace(File.separator, "/"));
                updatePreviewImage(relativePath);
            }
        }
    }
    
    /**
     * Sao chép file được chọn vào thư mục "images/mon an" của dự án
     */
    private String copyImageToProject(File sourceFile) {
        try {
            Path targetDir = Paths.get("images", "mon an");
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }
            Path targetPath = targetDir.resolve(sourceFile.getName());
            Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetPath.toString();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi sao chép tệp tin ảnh!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }


    private void capNhatGiaoDienTrangThai() {
        if (monAn.isTrangThai()) {
            lblTrangThaiHienTai.setText("CÓ SẴN");
            lblTrangThaiHienTai.setBackground(new Color(40, 167, 69)); 
            lblTrangThaiHienTai.setForeground(Color.WHITE);
            btnDoiTrangThai.setText("Đổi thành HẾT MÓN");
            btnDoiTrangThai.setBackground(new Color(255, 193, 7)); 
        } else {
            lblTrangThaiHienTai.setText("HẾT MÓN");
            lblTrangThaiHienTai.setBackground(new Color(220, 53, 69)); 
            lblTrangThaiHienTai.setForeground(Color.WHITE);
            btnDoiTrangThai.setText("Đổi thành CÓ SẴN");
            btnDoiTrangThai.setBackground(new Color(40, 167, 69)); 
        }
    }

    private void doiTrangThai() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn đổi trạng thái món " + monAn.getTenMonAn() + "?",
            "Xác nhận đổi trạng thái",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
        	monAn.setTrangThai(!monAn.isTrangThai());
            if (monAnDAO.updateMonAn(monAn)) {
                JOptionPane.showMessageDialog(this, "Đã đổi trạng thái thành công!");
                capNhatGiaoDienTrangThai();
                if (refreshCallback != null) {
                    refreshCallback.run(); 
                }
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi đổi trạng thái!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                monAn.setTrangThai(!monAn.isTrangThai()); 
            }
        }
    }

    /**
     * Lưu thay đổi (cập nhật 6 trường)
     */
    private void luuThayDoi() {
        try {
            String tenMoi = txtTenMon.getText().trim();
            double donGiaMoi = Double.parseDouble(txtDonGia.getText().trim());
            String imagePathMoi = txtImagePath.getText().trim(); 
            String loaiMonAnMoi = (String) cmbLoaiMon.getSelectedItem(); // LẤY LOẠI MÓN
            
            if (tenMoi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên món không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (donGiaMoi <= 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            monAn.setTenMonAn(tenMoi);
            monAn.setDonGia(donGiaMoi);
            monAn.setImagePath(imagePathMoi);
            monAn.setLoaiMonAn(loaiMonAnMoi); // Cập nhật loại món ăn

            if (monAnDAO.updateMonAn(monAn)) {
            	JOptionPane.showMessageDialog(this, "Đã lưu thay đổi thành công!");
                if (refreshCallback != null) {
                    refreshCallback.run(); 
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu thay đổi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaMonAn() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa món " + monAn.getTenMonAn() + "?\nHành động này không thể hoàn tác!",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (monAnDAO.deleteMonAn(monAn.getMaMonAn())) {
                JOptionPane.showMessageDialog(this, "Đã xóa món ăn thành công!");
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa món ăn! (Món ăn có thể đang nằm trong một hóa đơn)", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}