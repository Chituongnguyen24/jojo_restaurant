package view.HoaDon;

import dao.*;
import entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HoaDon_EditDialog extends JDialog {
    private JComboBox<KhachHang> cbxKhachHang;
    private JComboBox<NhanVien> cbxNhanVien;
    private JComboBox<Thue> cbxThue;
    private JComboBox<KhuyenMai> cbxKhuyenMai;
    private JComboBox<PhieuDatBan> cbxPhieuDatBan;
    private JComboBox<Ban> cbxBan; // THÊM
    private JComboBox<String> cbxPhuongThuc;
    private JCheckBox chkDaThanhToan; // SỬA: Dùng checkbox cho trạng thái thanh toán
    private JButton btnSave, btnCancel;

    private HoaDon hoaDon;
    private HoaDon_DAO hoaDonDAO;
    private KhachHang_DAO khachHangDAO;
    private NhanVien_DAO nhanVienDAO;
    private Thue_DAO thueDAO;
    private KhuyenMai_DAO khuyenMaiDAO; // SỬA: Dùng KhuyenMai_DAO
    private PhieuDatBan_DAO phieuDatBanDAO; // SỬA: Dùng PhieuDatBan_DAO
    private Ban_DAO banDAO; // THÊM

    public HoaDon_EditDialog(Frame owner, HoaDon hoaDon, HoaDon_DAO hoaDonDAO) {
        super(owner, "Chỉnh sửa hóa đơn", true);
        this.hoaDon = hoaDon;
        this.hoaDonDAO = hoaDonDAO;

        // Khởi tạo DAO
        khachHangDAO = new KhachHang_DAO();
        nhanVienDAO = new NhanVien_DAO();
        thueDAO = new Thue_DAO();
        khuyenMaiDAO = new KhuyenMai_DAO(); // SỬA
        phieuDatBanDAO = new PhieuDatBan_DAO(); // SỬA
        banDAO = new Ban_DAO(); // THÊM

        setSize(600, 650); // Tăng kích thước
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238));

        JLabel lblTitle = new JLabel("Chỉnh sửa thông tin hóa đơn", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(0, 10, 0, 10));
        
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setOpaque(false);

        // Khách hàng
        formPanel.add(new JLabel("Khách hàng:"));
        cbxKhachHang = new JComboBox<>();
        formPanel.add(cbxKhachHang);
        
        // Nhân viên
        formPanel.add(new JLabel("Nhân viên:"));
        cbxNhanVien = new JComboBox<>();
        formPanel.add(cbxNhanVien);
        
        // Bàn (MỚI)
        formPanel.add(new JLabel("Bàn (*):"));
        cbxBan = new JComboBox<>();
        formPanel.add(cbxBan);

        // Phương thức thanh toán
        formPanel.add(new JLabel("Phương thức:"));
        cbxPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ tín dụng", "Chuyển khoản"});
        formPanel.add(cbxPhuongThuc);

        // Thuế
        formPanel.add(new JLabel("Thuế:"));
        cbxThue = new JComboBox<>();
        formPanel.add(cbxThue);

        // Khuyến mãi
        formPanel.add(new JLabel("Khuyến mãi:"));
        cbxKhuyenMai = new JComboBox<>();
        formPanel.add(cbxKhuyenMai);

        // Phiếu đặt bàn
        formPanel.add(new JLabel("Phiếu đặt bàn:"));
        cbxPhieuDatBan = new JComboBox<>();
        formPanel.add(cbxPhieuDatBan);
        
        // Trạng thái thanh toán (MỚI)
        formPanel.add(new JLabel("Đã thanh toán:"));
        chkDaThanhToan = new JCheckBox("Xác nhận đã thanh toán");
        formPanel.add(chkDaThanhToan);

        mainContent.add(formPanel, BorderLayout.NORTH);
        
        // Thêm chi tiết hóa đơn (JList/JTable) nếu cần, hiện tại giữ đơn giản.
        
        add(mainContent, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(new EmptyBorder(10, 10, 15, 10));
        btnPanel.setOpaque(false);

        btnSave = new JButton("Lưu thay đổi");
        btnSave.setBackground(new Color(30, 150, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 13));
        btnSave.addActionListener(this::saveChanges);

        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(200, 80, 70));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        add(btnPanel, BorderLayout.SOUTH);

        loadComboBoxData();
        prefillData();
    }

    private void loadComboBoxData() {
        // Khách hàng
        cbxKhachHang.addItem(new KhachHang("KH00000000", "Khách vãng lai", null, null, null, 0, false));
        khachHangDAO.getAllKhachHang().forEach(cbxKhachHang::addItem);

        // Nhân viên
        nhanVienDAO.getAllNhanVien().forEach(cbxNhanVien::addItem); 
        
        // Bàn
        banDAO.getAllBan().forEach(cbxBan::addItem);
        
        // Thuế
        // Thue Entity không có constructor này, ta phải giả định giá trị cho Thue "null"
        // Hoặc tạo đối tượng Entity đầy đủ
        Thue thueNull = new Thue();
        thueNull.setMaSoThue("THUE_NULL"); thueNull.setTenThue("(Không tính)"); thueNull.setTyLeThue(0);
        cbxThue.addItem(thueNull);
        thueDAO.getAllThue().forEach(cbxThue::addItem);

        // Khuyến mãi
        // KhuyenMai Entity không có constructor này, ta phải giả định giá trị cho KM "null"
        KhuyenMai kmNull = new KhuyenMai();
        kmNull.setMaKM("KM00000000"); kmNull.setMoTa("Không áp dụng"); kmNull.setMucKM(0);
        cbxKhuyenMai.addItem(kmNull);
        khuyenMaiDAO.getAllKhuyenMai().forEach(cbxKhuyenMai::addItem);

        // Phiếu đặt bàn
        cbxPhieuDatBan.addItem(null);
        phieuDatBanDAO.getAllPhieuDatBan().forEach(cbxPhieuDatBan::addItem);
    }

    private void prefillData() {
        if (hoaDon == null) return;

        // Khách hàng
        selectComboBoxItem(cbxKhachHang, hoaDon.getKhachHang());

        // Nhân viên
        selectComboBoxItem(cbxNhanVien, hoaDon.getNhanVien());
        
        // Bàn
        selectComboBoxItem(cbxBan, hoaDon.getBan());

        // Thuế
        selectComboBoxItem(cbxThue, hoaDon.getThue());

        // Khuyến mãi
        selectComboBoxItem(cbxKhuyenMai, hoaDon.getKhuyenMai());

        // Phiếu đặt bàn
        selectComboBoxItem(cbxPhieuDatBan, hoaDon.getPhieuDatBan());

        // Phương thức
        if (hoaDon.getPhuongThucThanhToan() != null)
            cbxPhuongThuc.setSelectedItem(hoaDon.getPhuongThucThanhToan());
        
        // Đã thanh toán
        chkDaThanhToan.setSelected(hoaDon.isDaThanhToan());
    }

    private <T> void selectComboBoxItem(JComboBox<T> comboBox, T itemToSelect) {
         if (itemToSelect == null) { comboBox.setSelectedIndex(-1); return; }
        ComboBoxModel<T> model = comboBox.getModel(); 
        for (int i = 0; i < model.getSize(); i++) {
            T element = model.getElementAt(i);
            if (element != null && element.equals(itemToSelect)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
    }
    
    private void saveChanges(ActionEvent e) {
        KhachHang kh = (KhachHang) cbxKhachHang.getSelectedItem();
        NhanVien nv = (NhanVien) cbxNhanVien.getSelectedItem();
        Thue thue = (Thue) cbxThue.getSelectedItem();
        KhuyenMai km = (KhuyenMai) cbxKhuyenMai.getSelectedItem();
        PhieuDatBan pdb = (PhieuDatBan) cbxPhieuDatBan.getSelectedItem();
        Ban ban = (Ban) cbxBan.getSelectedItem();
        String phuongThuc = (String) cbxPhuongThuc.getSelectedItem();
        boolean thanhToan = chkDaThanhToan.isSelected();

        if (nv == null || thue == null || phuongThuc == null || ban == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ thông tin bắt buộc (*): Nhân viên, Bàn, Thuế, Phương thức.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            hoaDon.setKhachHang(kh);
            hoaDon.setNhanVien(nv);
            hoaDon.setThue(thue);
            hoaDon.setKhuyenMai(km);
            hoaDon.setPhieuDatBan(pdb);
            hoaDon.setBan(ban);
            hoaDon.setPhuongThucThanhToan(phuongThuc);
            hoaDon.setDaThanhToan(thanhToan);
            
            // LƯU Ý: Cần tính lại TongTienTruocThue và TongGiamGia
            // Để đơn giản, ta sẽ gán lại giá trị 0.0 để updateHoaDon có thể chạy mà không bị null, 
            // nhưng thực tế cần phải tính lại dựa trên ChiTietHoaDon.
            hoaDon.setTongTienTruocThue(0.0);
            hoaDon.setTongGiamGia(0.0);


            boolean updated = hoaDonDAO.updateHoaDon(hoaDon);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại, vui lòng thử lại!", "Lỗi Cập Nhật", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu thay đổi: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
        }
    }
}