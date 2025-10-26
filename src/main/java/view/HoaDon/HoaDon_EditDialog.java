package view.HoaDon;

import dao.*;
import entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class HoaDon_EditDialog extends JDialog {
    private JComboBox<KhachHang> cbxKhachHang;
    private JComboBox<NhanVien> cbxNhanVien;
    private JComboBox<Thue> cbxThue;
    private JComboBox<KhuyenMai> cbxKhuyenMai;
    private JComboBox<PhieuDatBan> cbxPhieuDatBan;
    private JComboBox<String> cbxPhuongThuc;
    private JButton btnSave, btnCancel;

    private HoaDon hoaDon;
    private HoaDon_DAO hoaDonDAO;
    private KhachHang_DAO khachHangDAO;
    private NhanVien_DAO nhanVienDAO;
    private HoaDon_Thue_DAO thueDAO;
    private HoaDon_KhuyenMai_DAO khuyenMaiDAO; // Sử dụng DAO thật
    private PhieuDatBan_DAO phieuDatBanDAO; // Cần có DAO này

    public HoaDon_EditDialog(Frame owner, HoaDon hoaDon, HoaDon_DAO hoaDonDAO) {
        super(owner, "Chỉnh sửa hóa đơn", true);
        this.hoaDon = hoaDon;
        this.hoaDonDAO = hoaDonDAO;

        khachHangDAO = new KhachHang_DAO();
        nhanVienDAO = new NhanVien_DAO();
        thueDAO = new HoaDon_Thue_DAO();
        khuyenMaiDAO = new HoaDon_KhuyenMai_DAO(); // Khởi tạo DAO thật
        phieuDatBanDAO = new PhieuDatBan_DAO(); // Cần có DAO này

        setSize(460, 420);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238));

        JLabel lblTitle = new JLabel("Chỉnh sửa thông tin hóa đơn", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Khách hàng:"));
        cbxKhachHang = new JComboBox<>(); formPanel.add(cbxKhachHang);
        formPanel.add(new JLabel("Nhân viên:"));
        cbxNhanVien = new JComboBox<>(); formPanel.add(cbxNhanVien);
        formPanel.add(new JLabel("Phương thức:"));
        cbxPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ tín dụng", "Chuyển khoản"}); formPanel.add(cbxPhuongThuc);
        formPanel.add(new JLabel("Thuế (*):"));
        cbxThue = new JComboBox<>(); formPanel.add(cbxThue);
        formPanel.add(new JLabel("Khuyến mãi:"));
        cbxKhuyenMai = new JComboBox<>(); formPanel.add(cbxKhuyenMai);
        formPanel.add(new JLabel("Phiếu đặt bàn:"));
        cbxPhieuDatBan = new JComboBox<>(); formPanel.add(cbxPhieuDatBan);
        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(new EmptyBorder(10, 10, 15, 10));
        btnPanel.setOpaque(false);
        btnSave = new JButton("Lưu thay đổi");
        btnSave.setBackground(new Color(30, 150, 80)); btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 13)); btnSave.addActionListener(this::saveChanges);
        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(200, 80, 70)); btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 13)); btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        loadComboBoxData();
        prefillData();
    }

    private void loadComboBoxData() {
        khachHangDAO.getAllKhachHang().forEach(cbxKhachHang::addItem);
        nhanVienDAO.getAllNhanVien().forEach(cbxNhanVien::addItem);
        thueDAO.getAllThueActive().forEach(cbxThue::addItem); // Chỉ lấy thuế active
        khuyenMaiDAO.getAllKhuyenMai().forEach(cbxKhuyenMai::addItem); // Bao gồm "Không áp dụng"

        // Tạm thời vô hiệu hóa PDB nếu chưa có DAO
        cbxPhieuDatBan.addItem(null); // Cho phép null
        // phieuDatBanDAO.getAllPhieuDatBan().forEach(cbxPhieuDatBan::addItem); // Cần hàm này
        cbxPhieuDatBan.setEnabled(false);
    }

    private void prefillData() {
        if (hoaDon == null) return;
        selectComboBoxItem(cbxKhachHang, hoaDon.getKhachHang());
        selectComboBoxItem(cbxNhanVien, hoaDon.getNhanVien());
        selectComboBoxItem(cbxThue, hoaDon.getThue());
        if (hoaDon.getKhuyenMai() != null) selectComboBoxItem(cbxKhuyenMai, hoaDon.getKhuyenMai());
        else selectComboBoxItemByMaKM(cbxKhuyenMai, "KM00000000"); // Chọn "Không áp dụng" nếu null
        // selectComboBoxItem(cbxPhieuDatBan, hoaDon.getPhieuDatBan());
        if (hoaDon.getPhuongThuc() != null) cbxPhuongThuc.setSelectedItem(hoaDon.getPhuongThuc());
    }

    private <T> void selectComboBoxItem(JComboBox<T> comboBox, T itemToSelect) {
         if (itemToSelect == null) { comboBox.setSelectedIndex(-1); return; }
        DefaultComboBoxModel<T> model = (DefaultComboBoxModel<T>) comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            if (model.getElementAt(i).equals(itemToSelect)) { comboBox.setSelectedIndex(i); return; }
        }
         comboBox.setSelectedIndex(-1);
         System.err.println("Không tìm thấy item trong ComboBox: " + itemToSelect);
    }

    private void selectComboBoxItemByMaKM(JComboBox<KhuyenMai> comboBox, String maKM) {
        DefaultComboBoxModel<KhuyenMai> model = (DefaultComboBoxModel<KhuyenMai>) comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
             KhuyenMai km = model.getElementAt(i);
            if (km != null && maKM.equals(km.getMaKM())) { comboBox.setSelectedIndex(i); return; }
        }
         comboBox.setSelectedIndex(-1);
    }

    private void saveChanges(ActionEvent e) {
        KhachHang kh = (KhachHang) cbxKhachHang.getSelectedItem();
        NhanVien nv = (NhanVien) cbxNhanVien.getSelectedItem();
        Thue thue = (Thue) cbxThue.getSelectedItem();
        KhuyenMai km = (KhuyenMai) cbxKhuyenMai.getSelectedItem();
        PhieuDatBan pdb = (PhieuDatBan) cbxPhieuDatBan.getSelectedItem();
        String phuongThuc = (String) cbxPhuongThuc.getSelectedItem();

        if (kh == null || nv == null || thue == null || phuongThuc == null || phuongThuc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ thông tin bắt buộc (*)", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            hoaDon.setKhachHang(kh);
            hoaDon.setNhanVien(nv);
            hoaDon.setThue(thue);
             if (km != null && "KM00000000".equals(km.getMaKM().trim())) { hoaDon.setKhuyenMai(km); }
             else { hoaDon.setKhuyenMai(km); }
            hoaDon.setPhieuDatBan(pdb);
            hoaDon.setPhuongThuc(phuongThuc);

            boolean updated = hoaDonDAO.updateHoaDon(hoaDon);
            if (updated) { JOptionPane.showMessageDialog(this, "Cập nhật thành công!"); dispose(); }
            else { JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi Cập Nhật", JOptionPane.ERROR_MESSAGE); }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
        }
    }
}