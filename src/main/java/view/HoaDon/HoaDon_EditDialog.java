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
    private Thue_DAO thueDAO;
    private HoaDon_KhuyenMai_DAO khuyenMaiDAO;
    private PhieuDatBan_DAO phieuDatBanDAO;

    public HoaDon_EditDialog(Frame owner, HoaDon hoaDon, HoaDon_DAO hoaDonDAO) {
        super(owner, "Chỉnh sửa hóa đơn", true);
        this.hoaDon = hoaDon;
        this.hoaDonDAO = hoaDonDAO;

        // Khởi tạo DAO
        khachHangDAO = new KhachHang_DAO();
        nhanVienDAO = new NhanVien_DAO();
        thueDAO = new Thue_DAO();
        khuyenMaiDAO = new HoaDon_KhuyenMai_DAO();
        phieuDatBanDAO = new PhieuDatBan_DAO();

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

        // Khách hàng
        formPanel.add(new JLabel("Khách hàng:"));
        cbxKhachHang = new JComboBox<>();
        formPanel.add(cbxKhachHang);

        // Nhân viên
        formPanel.add(new JLabel("Nhân viên:"));
        cbxNhanVien = new JComboBox<>();
        formPanel.add(cbxNhanVien);

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

        add(formPanel, BorderLayout.CENTER);

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
        List<KhachHang> dsKhachHang = khachHangDAO.getAllKhachHang();
        dsKhachHang.forEach(cbxKhachHang::addItem);

        List<NhanVien> dsNhanVien = nhanVienDAO.getAllNhanVien();
        dsNhanVien.forEach(cbxNhanVien::addItem);

        List<Thue> dsThue = thueDAO.getAllThue();
        dsThue.forEach(cbxThue::addItem);

        cbxKhuyenMai.addItem(null);
        List<KhuyenMai> dsKhuyenMai = khuyenMaiDAO.getAllKhuyenMai();
        dsKhuyenMai.forEach(cbxKhuyenMai::addItem);

        cbxPhieuDatBan.addItem(null);
        List<PhieuDatBan> dsPhieuDatBan = phieuDatBanDAO.getAllPhieuDatBan();
        dsPhieuDatBan.forEach(cbxPhieuDatBan::addItem);
    }

    private void prefillData() {
        if (hoaDon == null) return;

        if (hoaDon.getKhachHang() != null)
            cbxKhachHang.setSelectedItem(hoaDon.getKhachHang());

        if (hoaDon.getNhanVien() != null)
            cbxNhanVien.setSelectedItem(hoaDon.getNhanVien());

        if (hoaDon.getThue() != null)
            cbxThue.setSelectedItem(hoaDon.getThue());

        if (hoaDon.getKhuyenMai() != null)
            cbxKhuyenMai.setSelectedItem(hoaDon.getKhuyenMai());

        if (hoaDon.getPhieuDatBan() != null)
            cbxPhieuDatBan.setSelectedItem(hoaDon.getPhieuDatBan());

        if (hoaDon.getPhuongThuc() != null)
            cbxPhuongThuc.setSelectedItem(hoaDon.getPhuongThuc());
    }

    private void saveChanges(ActionEvent e) {
        try {
            hoaDon.setKhachHang((KhachHang) cbxKhachHang.getSelectedItem());
            hoaDon.setNhanVien((NhanVien) cbxNhanVien.getSelectedItem());
            hoaDon.setThue((Thue) cbxThue.getSelectedItem());
            hoaDon.setKhuyenMai((KhuyenMai) cbxKhuyenMai.getSelectedItem());
            hoaDon.setPhieuDatBan((PhieuDatBan) cbxPhieuDatBan.getSelectedItem());
            hoaDon.setPhuongThuc((String) cbxPhuongThuc.getSelectedItem());

            boolean updated = hoaDonDAO.updateHoaDon(hoaDon);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại, vui lòng thử lại!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu thay đổi: " + ex.getMessage());
        }
    }
}
