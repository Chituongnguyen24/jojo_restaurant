package view.HoaDon;

import dao.*;
import entity.*;
import enums.TrangThaiBan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class HoaDon_AddDialog extends JDialog {

    // Components UI
    private JComboBox<NhanVien> cbxNhanVien;
    private JComboBox<Thue> cbxThue;
    private JComboBox<KhuyenMai> cbxKhuyenMai;
    private JComboBox<Ban> cbxBan;
    private JComboBox<String> cbxPhuongThuc;
    private JCheckBox chkThanhToan;
    private JButton btnSave, btnCancel;
    
    // Components UI mới
    private JTextField txtMaPhieuDatBan;
    private JButton btnTimPDB;
    private JTextField txtSoDienThoai;
    private JButton btnTimKH;
    private JLabel lblTenKhachHangValue; // Hiển thị tên KH tìm được
    private JButton btnReset;

    // DAOs
    private HoaDon_DAO hoaDonDAO;
    private KhachHang_DAO khachHangDAO;
    private NhanVien_DAO nhanVienDAO;
    private Thue_DAO thueDAO;
    private KhuyenMai_DAO khuyenMaiDAO;
    private Ban_DAO banDAO;
    private PhieuDatBan_DAO phieuDatBanDAO; // THÊM: PDB DAO

    // Fields trạng thái
    private KhachHang khachHangHienTai;
    private PhieuDatBan phieuDatBanHienTai;

    public HoaDon_AddDialog(Frame owner) {
        super(owner, "Thêm hóa đơn mới", true);

        // Khởi tạo DAOs
        hoaDonDAO = new HoaDon_DAO();
        khachHangDAO = new KhachHang_DAO();
        nhanVienDAO = new NhanVien_DAO();
        thueDAO = new Thue_DAO();
        khuyenMaiDAO = new KhuyenMai_DAO();
        banDAO = new Ban_DAO();
        phieuDatBanDAO = new PhieuDatBan_DAO(); // KHỞI TẠO MỚI

        // Cài đặt Dialog
        setSize(550, 580); // Tăng kích thước
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238));

        // === PHẦN TITLE ===
        JLabel lblTitle = new JLabel("Tạo hóa đơn mới", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20)); // Tăng font
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        // === PHẦN FORM (Sử dụng GridBagLayout) ===
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 25, 20, 25));
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Khoảng cách giữa các components
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- Hàng 0: Mã Phiếu Đặt Bàn ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3; // Label
        formPanel.add(new JLabel("Mã Phiếu Đặt Bàn:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.7; // Panel cho TextField + Button
        formPanel.add(createSearchPanel(
            txtMaPhieuDatBan = new JTextField(),
            btnTimPDB = new JButton("Tìm"),
            this::timPhieuDatBan
        ), gbc);

        // --- Hàng 1: Số Điện Thoại ---
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("SĐT Khách hàng (*):"), gbc);
        
        gbc.gridx = 1;
        formPanel.add(createSearchPanel(
            txtSoDienThoai = new JTextField(),
            btnTimKH = new JButton("Tìm"),
            this::timKhachHangTheoSDT
        ), gbc);
        
        // --- Hàng 2: Tên Khách Hàng ---
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Tên Khách hàng:"), gbc);
        
        gbc.gridx = 1;
        lblTenKhachHangValue = new JLabel("Vui lòng tìm SĐT hoặc Mã PĐB...");
        lblTenKhachHangValue.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 14));
        lblTenKhachHangValue.setForeground(Color.BLUE);
        formPanel.add(lblTenKhachHangValue, gbc);

        // --- Hàng 3: Nhân viên ---
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Nhân viên (*):"), gbc);
        gbc.gridx = 1;
        cbxNhanVien = new JComboBox<>();
        formPanel.add(cbxNhanVien, gbc);

        // --- Hàng 4: Chọn Bàn ---
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Chọn Bàn (*):"), gbc);
        gbc.gridx = 1;
        cbxBan = new JComboBox<>();
        formPanel.add(cbxBan, gbc);

        // --- Hàng 5: Phương thức TT ---
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Phương thức (*):"), gbc);
        gbc.gridx = 1;
        cbxPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ tín dụng", "Chuyển khoản"});
        formPanel.add(cbxPhuongThuc, gbc);

        // --- Hàng 6: Loại thuế ---
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Loại thuế (*):"), gbc);
        gbc.gridx = 1;
        cbxThue = new JComboBox<>();
        formPanel.add(cbxThue, gbc);

        // --- Hàng 7: Khuyến mãi ---
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Khuyến mãi:"), gbc);
        gbc.gridx = 1;
        cbxKhuyenMai = new JComboBox<>();
        formPanel.add(cbxKhuyenMai, gbc);

        // --- Hàng 8: Trạng thái TT ---
        gbc.gridx = 0; gbc.gridy = 8;
        formPanel.add(new JLabel("Trạng thái thanh toán:"), gbc);
        gbc.gridx = 1;
        chkThanhToan = new JCheckBox("Đã thanh toán");
        chkThanhToan.setOpaque(false);
        formPanel.add(chkThanhToan, gbc);
        
        add(formPanel, BorderLayout.CENTER);

        // === PHẦN BUTTON ===
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setBorder(new EmptyBorder(10, 10, 15, 10));
        btnPanel.setOpaque(false);

        btnSave = createStyledButton("Lưu", new Color(30, 150, 80));
        btnSave.addActionListener(this::saveHoaDon);

        btnCancel = createStyledButton("Hủy", new Color(200, 80, 70));
        btnCancel.addActionListener(e -> dispose());
        
        btnReset = createStyledButton("Làm mới", new Color(108, 117, 125));
        btnReset.addActionListener(e -> resetForm());

        btnPanel.add(btnSave);
        btnPanel.add(btnReset); // Thêm nút Reset
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        // Tải dữ liệu và cài đặt trạng thái ban đầu
        loadComboBoxData();
        resetForm(); // Đặt trạng thái ban đầu
    }

    /**
     * Helper tạo panel chứa JTextField và JButton "Tìm"
     */
    private JPanel createSearchPanel(JTextField textField, JButton button, java.awt.event.ActionListener actionListener) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.setOpaque(false);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(actionListener);
        panel.add(textField, BorderLayout.CENTER);
        panel.add(button, BorderLayout.EAST);
        return panel;
    }
    
    /**
     * Helper tạo JButton với style chung
     */
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(100, 35));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Tải dữ liệu cho các ComboBox
     */
    private void loadComboBoxData() {
        // Không tải KH nữa
        
        // Tải Nhân viên
        List<NhanVien> dsNhanVien = nhanVienDAO.getAllNhanVien();
        dsNhanVien.forEach(cbxNhanVien::addItem);

        // Tải Thuế
        List<Thue> dsThue = thueDAO.getAllThue();
        dsThue.forEach(cbxThue::addItem);

        // Tải Khuyến mãi
        cbxKhuyenMai.addItem(null); // Cho phép không chọn KM
        List<KhuyenMai> dsKhuyenMai = khuyenMaiDAO.getAllKhuyenMai();
        dsKhuyenMai.forEach(cbxKhuyenMai::addItem);
        
        // Tải bàn sẽ được xử lý trong resetForm()
    }
    
    /**
     * Tải danh sách các bàn TRỐNG vào ComboBox
     */
    private void loadBanTrong() {
        cbxBan.removeAllItems();
        // SỬA: Chỉ lấy bàn trống để thanh toán trực tiếp
        List<Ban> dsBanTrong = banDAO.getAllBan().stream()
            .filter(b -> b.getTrangThai().trim().equalsIgnoreCase("Trống"))
            .collect(Collectors.toList());
        if (dsBanTrong.isEmpty()) {
             cbxBan.addItem(null); // Hoặc hiển thị thông báo
        } else {
            dsBanTrong.forEach(cbxBan::addItem);
        }
        cbxBan.setEnabled(true);
    }
    
    /**
     * Đặt lại form về trạng thái ban đầu (cho khách vãng lai)
     */
    private void resetForm() {
        txtMaPhieuDatBan.setText("");
        txtSoDienThoai.setText("");
        lblTenKhachHangValue.setText("Vui lòng tìm SĐT hoặc Mã PĐB...");
        
        phieuDatBanHienTai = null;
        khachHangHienTai = null; // Sẽ lấy khách vãng lai khi tìm SĐT rỗng
        
        // Mở khóa các trường
        txtMaPhieuDatBan.setEnabled(true);
        btnTimPDB.setEnabled(true);
        txtSoDienThoai.setEnabled(true);
        btnTimKH.setEnabled(true);
        
        chkThanhToan.setSelected(false);
        cbxPhuongThuc.setSelectedIndex(0);
        cbxKhuyenMai.setSelectedItem(null);
        cbxThue.setSelectedIndex(0);
        cbxNhanVien.setSelectedIndex(0);
        
        loadBanTrong(); 
    }

    private void timPhieuDatBan(ActionEvent e) {
        String maPDB = txtMaPhieuDatBan.getText().trim();
        if (maPDB.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập Mã Phiếu Đặt Bàn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        PhieuDatBan pdb = phieuDatBanDAO.getPhieuDatBanById(maPDB);
        if (pdb == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy Phiếu Đặt Bàn với mã: " + maPDB, "Lỗi", JOptionPane.ERROR_MESSAGE);
            resetForm();
        } else {
            this.phieuDatBanHienTai = pdb;
            this.khachHangHienTai = pdb.getKhachHang();

            txtSoDienThoai.setText(khachHangHienTai.getSoDienThoai());
            lblTenKhachHangValue.setText(khachHangHienTai.getTenKH());
            
            cbxBan.removeAllItems();
            cbxBan.addItem(pdb.getBan());
    
            txtSoDienThoai.setEnabled(false);
            btnTimKH.setEnabled(false);
            cbxBan.setEnabled(false);
            
            JOptionPane.showMessageDialog(this, "Đã tải thông tin từ Phiếu Đặt Bàn. Bàn " + pdb.getBan().getMaBan() + " đã được chọn.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void timKhachHangTheoSDT(ActionEvent e) {
        String sdt = txtSoDienThoai.getText().trim();
        
        KhachHang kh = khachHangDAO.getKhachHangBySDT(sdt); 
        
        if (kh == null) {
            this.khachHangHienTai = khachHangDAO.getKhachHangById("KH000"); 
            lblTenKhachHangValue.setText(this.khachHangHienTai.getTenKH()); 
            JOptionPane.showMessageDialog(this, "Không tìm thấy SĐT. Sử dụng 'Khách vãng lai'.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        } else {
            // Tìm thấy KH
            this.khachHangHienTai = kh;
            lblTenKhachHangValue.setText(kh.getTenKH());
        }
        
        txtMaPhieuDatBan.setText("");
        this.phieuDatBanHienTai = null;
        txtMaPhieuDatBan.setEnabled(true);
        btnTimPDB.setEnabled(true);
        
        loadBanTrong();
    }


    private void saveHoaDon(ActionEvent e) {
        NhanVien nv = (NhanVien) cbxNhanVien.getSelectedItem();
        Thue thue = (Thue) cbxThue.getSelectedItem();
        KhuyenMai km = (KhuyenMai) cbxKhuyenMai.getSelectedItem();
        Ban ban = (Ban) cbxBan.getSelectedItem();
        KhachHang kh = this.khachHangHienTai;
        PhieuDatBan pdb = this.phieuDatBanHienTai;
        
        String phuongThuc = (String) cbxPhuongThuc.getSelectedItem();
        boolean thanhToan = chkThanhToan.isSelected();

        if (kh == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tìm Khách hàng (theo SĐT) hoặc Phiếu Đặt Bàn.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (nv == null || thue == null || ban == null || phuongThuc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ thông tin bắt buộc (*).", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (pdb == null) {
            if (!ban.getTrangThai().trim().equalsIgnoreCase("Trống")) {
                JOptionPane.showMessageDialog(this, "Bàn " + ban.getMaBan() + " không ở trạng thái 'Trống'. Vui lòng chọn bàn khác.", "Lỗi chọn bàn", JOptionPane.ERROR_MESSAGE);
                loadBanTrong(); 
                return;
            }
            
            HoaDon hdTonTai = hoaDonDAO.getHoaDonByBanChuaThanhToan(ban.getMaBan());
            if (hdTonTai != null && !thanhToan) { // Nếu tạo HĐ mới mà không thanh toán ngay
                 JOptionPane.showMessageDialog(this, 
                    "Lỗi: Bàn " + ban.getMaBan() + " đã có hóa đơn (" + hdTonTai.getMaHD() + ") chưa thanh toán.\n" +
                    "Không thể tạo thêm hóa đơn mới.", 
                    "Lỗi Nghiệp Vụ", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        try {
            String maHD = hoaDonDAO.generateNewID();
            LocalDate ngayLap = LocalDate.now();
            LocalDateTime gioVao = LocalDateTime.now();
            
           LocalDateTime gioRa = null; 
            if(thanhToan) {
                gioRa = gioVao;
            }

            HoaDon hd = new HoaDon(
                maHD, nv, kh, ban, ngayLap, gioVao, gioRa, phuongThuc, km, thue, 
                pdb, 
                0.0, 
                0.0, 
                thanhToan
            );

            boolean added = hoaDonDAO.addHoaDon(hd);
            
            if (added) {
                 if (!thanhToan) { 
                    banDAO.capNhatTrangThaiBan(ban.getMaBan(), TrangThaiBan.CO_KHACH);
                 } else {
                     banDAO.capNhatTrangThaiBan(ban.getMaBan(), TrangThaiBan.TRONG);
                 }
                 
                 if (pdb != null) {
                     phieuDatBanDAO.capNhatThoiGianNhanBan(pdb.getMaPhieu(), gioVao);
                 }
                
                JOptionPane.showMessageDialog(this, "Thêm hóa đơn thành công! Mã HD: " + maHD);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể thêm hóa đơn, vui lòng thử lại!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}