package view.NhanVien;

import dao.NhanVien_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class NhanVien_EditDialog extends JDialog {
    private JTextField txtTenNV, txtSDT, txtEmail, txtUser, txtPass;
    private JComboBox<String> cbVaiTro;
    private JRadioButton rdNam, rdNu;
    private JButton btnSave, btnCancel;
    private NhanVien_DAO nhanVienDAO;
    private NhanVien nhanVien;

    public NhanVien_EditDialog(Frame owner, NhanVien nhanVien, NhanVien_DAO nhanVienDAO) {
        super(owner, "Chỉnh sửa nhân viên", true);
        this.nhanVienDAO = nhanVienDAO;
        this.nhanVien = nhanVien;

        setSize(460, 520);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238));

        JLabel lblTitle = new JLabel("Chỉnh sửa thông tin nhân viên", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Tên nhân viên:"));
        txtTenNV = new JTextField(nhanVien.getTenNhanVien());
        formPanel.add(txtTenNV);

        formPanel.add(new JLabel("Giới tính:"));
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setOpaque(false);
        rdNam = new JRadioButton("Nam");
        rdNu = new JRadioButton("Nữ");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rdNam);
        bg.add(rdNu);
        if (nhanVien.isGioiTinh()) rdNu.setSelected(true);
        else rdNam.setSelected(true);
        genderPanel.add(rdNam);
        genderPanel.add(rdNu);
        formPanel.add(genderPanel);

        formPanel.add(new JLabel("Số điện thoại:"));
        txtSDT = new JTextField(nhanVien.getSdt());
        formPanel.add(txtSDT);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField(nhanVien.getEmail());
        formPanel.add(txtEmail);

        TaiKhoan tk = nhanVien.getTaiKhoan();
        formPanel.add(new JLabel("Tên đăng nhập:"));
        txtUser = new JTextField(tk != null ? tk.getTenDangNhap() : "");
        formPanel.add(txtUser);

        formPanel.add(new JLabel("Mật khẩu:"));
        txtPass = new JTextField(tk != null ? tk.getMatKhau() : "");
        formPanel.add(txtPass);

        formPanel.add(new JLabel("Vai trò:"));
        cbVaiTro = new JComboBox<>(new String[]{"Quản lý", "Tiếp tân"});
        if (tk != null && tk.getVaiTro().equalsIgnoreCase("NVQL"))
            cbVaiTro.setSelectedItem("Quản lý");
        else
            cbVaiTro.setSelectedItem("Tiếp tân");
        formPanel.add(cbVaiTro);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(new EmptyBorder(10, 10, 15, 10));
        btnPanel.setOpaque(false);

        btnSave = new JButton("💾 Lưu thay đổi");
        btnSave.setBackground(new Color(30, 150, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setFont(new Font("Arial", Font.BOLD, 13));
        btnSave.addActionListener(this::saveChanges);

        btnCancel = new JButton("✖ Hủy");
        btnCancel.setBackground(new Color(200, 80, 70));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 13));
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void saveChanges(ActionEvent e) {
        try {
            nhanVien.setTenNhanVien(txtTenNV.getText());
            nhanVien.setGioiTinh(rdNu.isSelected());
            nhanVien.setSdt(txtSDT.getText());
            nhanVien.setEmail(txtEmail.getText());

            TaiKhoan tk = nhanVien.getTaiKhoan();
            if (tk == null) tk = new TaiKhoan();
            tk.setMaNV(nhanVien.getMaNV());
            tk.setTenDangNhap(txtUser.getText());
            tk.setMatKhau(txtPass.getText());
            tk.setVaiTro(cbVaiTro.getSelectedItem().equals("Quản lý") ? "NVQL" : "NVTT");
            nhanVien.setTaiKhoan(tk);

            boolean updated = nhanVienDAO.updateNhanVien(nhanVien);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại, vui lòng thử lại.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Đã xảy ra lỗi: " + ex.getMessage());
        }
    }
}
