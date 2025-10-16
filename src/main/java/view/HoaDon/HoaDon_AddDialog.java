package view.HoaDon;

import dao.*;
import entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class HoaDon_AddDialog extends JDialog {
    private JComboBox<KhachHang> cbxKhachHang;
    private JComboBox<NhanVien> cbxNhanVien;
    private JComboBox<Thue> cbxThue;
    private JComboBox<KhuyenMai> cbxKhuyenMai;
    private JComboBox<PhieuDatBan> cbxPhieuDatBan;
    private JComboBox<String> cbxPhuongThuc;
    private JCheckBox chkThanhToan;
    private JButton btnSave, btnCancel;

    private HoaDon_DAO hoaDonDAO;
    private KhachHang_DAO khachHangDAO;
    private NhanVien_DAO nhanVienDAO;
    private Thue_DAO thueDAO;
    private HoaDon_KhuyenMai_DAO khuyenMaiDAO;
    private PhieuDatBan_DAO phieuDatBanDAO;

    public HoaDon_AddDialog(Frame owner) {
        super(owner, "Thêm hóa đơn mới", true);

        hoaDonDAO = new HoaDon_DAO();
        khachHangDAO = new KhachHang_DAO();
        nhanVienDAO = new NhanVien_DAO();
        thueDAO = new Thue_DAO();
        khuyenMaiDAO = new HoaDon_KhuyenMai_DAO();
        phieuDatBanDAO = new PhieuDatBan_DAO();

        setSize(460, 460);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238));

        JLabel lblTitle = new JLabel("Thêm hóa đơn mới", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Khách hàng (*):"));
        cbxKhachHang = new JComboBox<>();
        formPanel.add(cbxKhachHang);

        formPanel.add(new JLabel("Nhân viên (*):"));
        cbxNhanVien = new JComboBox<>();
        formPanel.add(cbxNhanVien);

        formPanel.add(new JLabel("Phương thức (*):"));
        cbxPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ tín dụng", "Chuyển khoản"});
        formPanel.add(cbxPhuongThuc);

        formPanel.add(new JLabel("Loại thuế (*):"));
        cbxThue = new JComboBox<>();
        formPanel.add(cbxThue);

        formPanel.add(new JLabel("Khuyến mãi:"));
        cbxKhuyenMai = new JComboBox<>();
        formPanel.add(cbxKhuyenMai);

        formPanel.add(new JLabel("Phiếu đặt bàn:"));
        cbxPhieuDatBan = new JComboBox<>();
        formPanel.add(cbxPhieuDatBan);

        formPanel.add(new JLabel("Trạng thái thanh toán:"));
        chkThanhToan = new JCheckBox("Đã thanh toán");
        formPanel.add(chkThanhToan);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(new EmptyBorder(10, 10, 15, 10));
        btnPanel.setOpaque(false);

        btnSave = new JButton("Lưu");
        btnSave.setBackground(new Color(30, 150, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 13));
        btnSave.addActionListener(this::saveHoaDon);

        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(200, 80, 70));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        loadComboBoxData();
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

    private void saveHoaDon(ActionEvent e) {
        KhachHang kh = (KhachHang) cbxKhachHang.getSelectedItem();
        NhanVien nv = (NhanVien) cbxNhanVien.getSelectedItem();
        Thue thue = (Thue) cbxThue.getSelectedItem();
        KhuyenMai km = (KhuyenMai) cbxKhuyenMai.getSelectedItem();
        PhieuDatBan pdb = (PhieuDatBan) cbxPhieuDatBan.getSelectedItem();
        String phuongThuc = (String) cbxPhuongThuc.getSelectedItem();
        boolean thanhToan = chkThanhToan.isSelected();

        if (kh == null || nv == null || thue == null || phuongThuc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ thông tin bắt buộc (*)");
            return;
        }

        try {
            String maHD = "HD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            LocalDate ngayLap = LocalDate.now();
            LocalDateTime gioVao = LocalDateTime.now();
            LocalDateTime gioRa = LocalDateTime.now().plusHours(2);

            HoaDon hd=new HoaDon(maHD, kh, nv, pdb, km, thue, ngayLap, gioVao, gioRa, phuongThuc, thanhToan);
            hd.setDaThanhToan(thanhToan);

            boolean added = hoaDonDAO.addHoaDon(hd);
            if (added) {
                JOptionPane.showMessageDialog(this, "Thêm hóa đơn thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể thêm hóa đơn, vui lòng thử lại!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu hóa đơn: " + ex.getMessage());
        }
    }
}
