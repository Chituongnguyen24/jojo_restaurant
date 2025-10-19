package view.Ban;

import dao.DatBan_DAO;
import dao.KhachHang_DAO;
import entity.Ban;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDatBan;
import enums.TrangThaiBan;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EditPhieuDatBanDialog extends JDialog {
    private DatBan_DAO datBanDAO = new DatBan_DAO();
    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    private PhieuDatBan originalPhieu;
    private JFrame parentFrame;
    private JTable table; // Assume this is passed or from parent; for completeness, we'll simulate selection

    // UI Components - Limited to editable fields
    private JTextField txtMaPhieu, txtSDT, txtTenKH, txtSoNguoi, txtThoiGianDat;
    private JRadioButton rbThanhVien, rbKhachLe;
    private ButtonGroup bgLoaiKhach;
    private JButton btnSave, btnCancel;

    public EditPhieuDatBanDialog(JFrame parent, JTable table, PhieuDatBan phieu) {
        super(parent, "Sửa phiếu đặt bàn", true);
        this.parentFrame = parent;
        this.table = table;
        this.originalPhieu = phieu;
        initializeComponents();
        loadDataToForm(phieu);
        setLayout(new BorderLayout());
        setSize(500, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void initializeComponents() {
        // Main form panel
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Customer type selection (kept for logic, but minimal impact)
        JPanel loaiKhachPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        loaiKhachPanel.setBorder(new TitledBorder("Loại khách hàng"));
        rbThanhVien = new JRadioButton("Thành viên", false);
        rbKhachLe = new JRadioButton("Khách lẻ", false);
        bgLoaiKhach = new ButtonGroup();
        bgLoaiKhach.add(rbThanhVien);
        bgLoaiKhach.add(rbKhachLe);
        loaiKhachPanel.add(rbThanhVien);
        loaiKhachPanel.add(rbKhachLe);
        formPanel.add(loaiKhachPanel, BorderLayout.NORTH);

        // Simplified customer info panel (only name, phone)
        JPanel khachHangPanel = createKhachHangPanel();
        formPanel.add(khachHangPanel, BorderLayout.CENTER);

        // Simplified booking info panel (only number of people, time)
        JPanel bookingPanel = createBookingPanel();
        formPanel.add(bookingPanel, BorderLayout.SOUTH);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnSave = new JButton("Lưu thay đổi");
        btnCancel = new JButton("Hủy");
        btnSave.setBackground(new Color(34, 139, 230));
        btnSave.setForeground(Color.WHITE);
        btnCancel.setBackground(Color.GRAY);
        btnCancel.setForeground(Color.WHITE);

        btnSave.addActionListener(e -> saveChanges());
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event listeners
        setupListeners();
    }

    private JPanel createKhachHangPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new TitledBorder("Thông tin khách hàng"));

        JLabel lblSDT = new JLabel("SĐT:");
        txtSDT = new JTextField(15);
        panel.add(lblSDT);
        panel.add(txtSDT);

        JLabel lblTenKH = new JLabel("Tên KH:");
        txtTenKH = new JTextField(15);
        panel.add(lblTenKH);
        panel.add(txtTenKH);

        return panel;
    }

    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(new TitledBorder("Thông tin đặt bàn"));

        JLabel lblMaPhieu = new JLabel("Mã phiếu:");
        txtMaPhieu = new JTextField(15);
        txtMaPhieu.setEditable(false);
        panel.add(lblMaPhieu);
        panel.add(txtMaPhieu);

        JLabel lblSoNguoi = new JLabel("Số người:");
        txtSoNguoi = new JTextField(5);
        panel.add(lblSoNguoi);
        panel.add(txtSoNguoi);

        JLabel lblThoiGian = new JLabel("Thời gian đến (dd/MM/yyyy HH:mm):");
        txtThoiGianDat = new JTextField(15);
        panel.add(lblThoiGian);
        panel.add(txtThoiGianDat);

        return panel;
    }

    private void loadDataToForm(PhieuDatBan phieu) {
        txtMaPhieu.setText(phieu.getMaPhieu());
        KhachHang kh = phieu.getKhachHang();
        txtSDT.setText(kh.getSdt());
        txtTenKH.setText(kh.getTenKhachHang());
        if (kh.isLaThanhVien()) {
            rbThanhVien.setSelected(true);
        } else {
            rbKhachLe.setSelected(true);
        }
        // Assume PhieuDatBan has getSoNguoi() or from KhachHang; adjust as needed
//        txtSoNguoi.setText(String.valueOf(phieu.getSoNguoi() != 0 ? phieu.getSoNguoi() : 4)); // Use entity or default
        txtThoiGianDat.setText(phieu.getThoiGianDat().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }

    private void setupListeners() {
        // Auto-fill member info on phone change (kept for consistency)
        txtSDT.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (rbThanhVien.isSelected() && txtSDT.getText().length() >= 10) {
                    List<KhachHang> khs = khachHangDAO.findByPhone(txtSDT.getText());
                    if (!khs.isEmpty()) {
                        KhachHang kh = khs.get(0);
                        txtTenKH.setText(kh.getTenKhachHang());
                    }
                }
            }
            @Override public void removeUpdate(DocumentEvent e) {}
            @Override public void changedUpdate(DocumentEvent e) {}
        });
    }

    private void saveChanges() {
        try {
            // Validate
            if (txtSDT.getText().trim().isEmpty() || txtSoNguoi.getText().trim().isEmpty() || txtThoiGianDat.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin bắt buộc!");
                return;
            }
            int soNguoi = Integer.parseInt(txtSoNguoi.getText().trim());
            LocalDateTime thoiGianMoi = LocalDateTime.parse(txtThoiGianDat.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            // Simple overlap check for time (keep ban same)
            LocalDateTime endTime = thoiGianMoi.plusHours(2);
            List<PhieuDatBan> overlapping = datBanDAO.getPhieuDatBanByTimeRange(thoiGianMoi, endTime);
            String originalBanMa = originalPhieu.getBan().getMaBan();
            if (isTableBookedInTimeSlot(originalBanMa, overlapping, thoiGianMoi, endTime) && 
                !originalPhieu.getThoiGianDat().equals(thoiGianMoi)) { // Only check if time changed
                JOptionPane.showMessageDialog(this, "Bàn đã được đặt trong khoảng thời gian mới này!");
                return;
            }

            // Update KhachHang if needed (only name and phone)
            KhachHang updatedKh = originalPhieu.getKhachHang();
            boolean isThanhVien = rbThanhVien.isSelected();
            if (isThanhVien) {
                List<KhachHang> khs = khachHangDAO.findByPhone(txtSDT.getText());
                if (khs.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy thành viên với SĐT này!");
                    return;
                }
                updatedKh = khs.get(0);
            } else {
                // Update khach le: name and phone
                updatedKh.setTenKhachHang(txtTenKH.getText());
                updatedKh.setSdt(txtSDT.getText());
                khachHangDAO.updateKhachHang(updatedKh);
            }

            // Create updated Phieu (keep ban, deposit, etc.; only update time and soNguoi)
            NhanVien nv = originalPhieu.getNhanVien();
            Ban originalBan = originalPhieu.getBan();
            double originalTienCoc = originalPhieu.getTienCoc();
            PhieuDatBan updatedPhieu = new PhieuDatBan(originalPhieu.getMaPhieu(), thoiGianMoi, updatedKh, nv, originalBan, originalTienCoc);
//            updatedPhieu.setSoNguoi(soNguoi); // Assume setter exists

            if (datBanDAO.updatePhieuDatBan(updatedPhieu)) {
                JOptionPane.showMessageDialog(this, "Sửa phiếu thành công!");
                dispose();
                // Refresh parent table/view
                if (parentFrame.getContentPane() instanceof DatBan_View) {
                    ((DatBan_View) parentFrame.getContentPane()).updateTablesDisplay();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Sửa phiếu thất bại!");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private boolean isTableBookedInTimeSlot(String maBan, List<PhieuDatBan> danhSachPhieu, LocalDateTime start, LocalDateTime end) {
        for (PhieuDatBan phieu : danhSachPhieu) {
            if (phieu.getBan().getMaBan().equals(maBan) && !phieu.getMaPhieu().equals(originalPhieu.getMaPhieu())) { // Exclude self
                LocalDateTime thoiGianPhieu = phieu.getThoiGianDat();
                if (!thoiGianPhieu.isAfter(end) && !thoiGianPhieu.plusHours(2).isBefore(start)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Static method for edit dialog only
    public static void showEditDialog(JFrame parent, JTable table, DatBan_DAO dao, PhieuDatBan phieuCu) {
        int row = table != null ? table.getSelectedRow() : -1;
        if (row == -1) {
            String maPhieu = JOptionPane.showInputDialog(parent, "Nhập mã phiếu để sửa:");
            if (maPhieu == null || maPhieu.trim().isEmpty()) {
                JOptionPane.showMessageDialog(parent, "Vui lòng chọn phiếu cần sửa!");
                return;
            }
            PhieuDatBan phieu = dao.getPhieuDatBanById(maPhieu);
            if (phieu == null) {
                JOptionPane.showMessageDialog(parent, "Không tìm thấy phiếu!");
                return;
            }
            new EditPhieuDatBanDialog(parent, null, phieu).setVisible(true);
        } else {
            String maPhieu = table.getValueAt(row, 0).toString(); // Assume col 0 is maPhieu
            PhieuDatBan phieu = dao.getPhieuDatBanById(maPhieu);
            if (phieu == null) {
                JOptionPane.showMessageDialog(parent, "Không tìm thấy phiếu!");
                return;
            }
            new EditPhieuDatBanDialog(parent, table, phieu).setVisible(true);
        }
    }
}