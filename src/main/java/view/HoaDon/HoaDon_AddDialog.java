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
    private JComboBox<KhachHang> cbxKhachHang;
    private JComboBox<NhanVien> cbxNhanVien;
    private JComboBox<Thue> cbxThue;
    private JComboBox<KhuyenMai> cbxKhuyenMai;
    private JComboBox<Ban> cbxBan; // THÊM: Chọn bàn
    private JComboBox<String> cbxPhuongThuc;
    private JCheckBox chkThanhToan;
    private JButton btnSave, btnCancel;

    private HoaDon_DAO hoaDonDAO;
    private KhachHang_DAO khachHangDAO;
    private NhanVien_DAO nhanVienDAO;
    private Thue_DAO thueDAO;
    private KhuyenMai_DAO khuyenMaiDAO;
    private Ban_DAO banDAO; // THÊM: Ban DAO

    public HoaDon_AddDialog(Frame owner) {
        super(owner, "Thêm hóa đơn mới (Thanh toán trực tiếp)", true);

        hoaDonDAO = new HoaDon_DAO();
        khachHangDAO = new KhachHang_DAO();
        nhanVienDAO = new NhanVien_DAO();
        thueDAO = new Thue_DAO();
        khuyenMaiDAO = new KhuyenMai_DAO();
        banDAO = new Ban_DAO(); // KHỞI TẠO

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
        
        formPanel.add(new JLabel("Chọn Bàn (*):")); // THÊM
        cbxBan = new JComboBox<>();
        formPanel.add(cbxBan);

        formPanel.add(new JLabel("Phương thức (*):"));
        cbxPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ tín dụng", "Chuyển khoản"});
        formPanel.add(cbxPhuongThuc);

        formPanel.add(new JLabel("Loại thuế (*):"));
        cbxThue = new JComboBox<>();
        formPanel.add(cbxThue);

        formPanel.add(new JLabel("Khuyến mãi:"));
        cbxKhuyenMai = new JComboBox<>();
        formPanel.add(cbxKhuyenMai);


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
        
        // SỬA: Chỉ lấy bàn trống để thanh toán trực tiếp
        List<Ban> dsBanTrong = banDAO.getAllBan().stream()
            .filter(b -> b.getTrangThai().trim().equalsIgnoreCase("Trống"))
            .collect(Collectors.toList());
        dsBanTrong.forEach(cbxBan::addItem);

        List<Thue> dsThue = thueDAO.getAllThue();
        dsThue.forEach(cbxThue::addItem);

        cbxKhuyenMai.addItem(null);
        List<KhuyenMai> dsKhuyenMai = khuyenMaiDAO.getAllKhuyenMai(); // SỬA: Dùng getAll()
        dsKhuyenMai.forEach(cbxKhuyenMai::addItem);
    }

    private void saveHoaDon(ActionEvent e) {
        KhachHang kh = (KhachHang) cbxKhachHang.getSelectedItem();
        NhanVien nv = (NhanVien) cbxNhanVien.getSelectedItem();
        Thue thue = (Thue) cbxThue.getSelectedItem();
        KhuyenMai km = (KhuyenMai) cbxKhuyenMai.getSelectedItem();
        Ban ban = (Ban) cbxBan.getSelectedItem(); // LẤY BÀN
        
        String phuongThuc = (String) cbxPhuongThuc.getSelectedItem();
        boolean thanhToan = chkThanhToan.isSelected();

        if (kh == null || nv == null || thue == null || ban == null || phuongThuc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ thông tin bắt buộc (*), bao gồm cả BÀN.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String maHD = hoaDonDAO.generateNewID(); // SỬ DỤNG DAO ĐỂ TẠO MÃ HD
            LocalDate ngayLap = LocalDate.now();
            LocalDateTime gioVao = LocalDateTime.now();
            LocalDateTime gioRa = LocalDateTime.now().plusHours(2); // Tạm thời

            // SỬ DỤNG CONSTRUCTOR 14 THAM SỐ (Cột PDB là NULL, TongTien = 0.0)
            HoaDon hd = new HoaDon(
                maHD, nv, kh, ban, ngayLap, gioVao, gioRa, phuongThuc, km, thue, 
                null, // PhieuDatBan = null
                0.0, // TongTienTruocThue
                0.0, // TongGiamGia
                thanhToan
            );

            boolean added = hoaDonDAO.addHoaDon(hd);
            if (added) {
                // Sửa trạng thái bàn thành 'Có khách' nếu chưa thanh toán, hoặc 'Trống' nếu đã thanh toán ngay
                 if (!thanhToan) {
                    banDAO.capNhatTrangThaiBan(ban.getMaBan(), TrangThaiBan.CO_KHACH);
                 } else {
                     banDAO.capNhatTrangThaiBan(ban.getMaBan(), TrangThaiBan.TRONG);
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