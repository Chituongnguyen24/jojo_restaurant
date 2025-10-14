package view.NhanVien;

import dao.NhanVien_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.UUID;

public class NhanVien_AddDialog extends JDialog {
    private JTextField txtTenNV, txtSDT, txtEmail, txtUser, txtPass;
    private JComboBox<String> cbVaiTro;
    private JRadioButton rdNam, rdNu;
    private JButton btnSave, btnCancel;
    private NhanVien_DAO nhanVienDAO;

    public NhanVien_AddDialog(Frame owner, NhanVien_DAO nhanVienDAO) {
        super(owner, "Thêm nhân viên mới", true);
        this.nhanVienDAO = nhanVienDAO;

        setSize(460, 520);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238)); 

        // ===== Header =====
        JLabel lblTitle = new JLabel("Thêm nhân viên mới", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        // ===== Form =====
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Tên nhân viên:"));
        txtTenNV = new JTextField();
        formPanel.add(txtTenNV);

        formPanel.add(new JLabel("Giới tính:"));
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setOpaque(false);
        rdNam = new JRadioButton("Nam");
        rdNu = new JRadioButton("Nữ");
        ButtonGroup bg = new ButtonGroup();
        bg.add(rdNam);
        bg.add(rdNu);
        rdNam.setSelected(true);
        genderPanel.add(rdNam);
        genderPanel.add(rdNu);
        formPanel.add(genderPanel);

        formPanel.add(new JLabel("Số điện thoại:"));
        txtSDT = new JTextField();
        formPanel.add(txtSDT);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Tên đăng nhập:"));
        txtUser = new JTextField();
        formPanel.add(txtUser);

        formPanel.add(new JLabel("Mật khẩu:"));
        txtPass = new JTextField();
        formPanel.add(txtPass);

        formPanel.add(new JLabel("Vai trò:"));
        cbVaiTro = new JComboBox<>(new String[]{"Quản lý", "Tiếp tân"});
        formPanel.add(cbVaiTro);

        add(formPanel, BorderLayout.CENTER);

        // ===== Buttons =====
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(new EmptyBorder(10, 10, 15, 10));
        btnPanel.setOpaque(false);

        btnSave = new JButton("💾 Lưu");
        btnSave.setBackground(new Color(30, 150, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setFont(new Font("Arial", Font.BOLD, 13));
        btnSave.addActionListener(this::saveNhanVien);

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

    private void saveNhanVien(ActionEvent e) {
        try {
            String maNV = "NV" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            String tenNV = txtTenNV.getText().trim();
            String sdt = txtSDT.getText().trim();
            String email = txtEmail.getText().trim();
            String user = txtUser.getText().trim();
            String pass = txtPass.getText().trim();
            String roleDisplay = (String) cbVaiTro.getSelectedItem();
            String role = roleDisplay.equals("Quản lý") ? "NVQL" : "NVTT";

            if (tenNV.isEmpty() || sdt.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin nhân viên!");
                return;
            }

            TaiKhoan tk = new TaiKhoan(maNV, user, pass, role);
            NhanVien nv = new NhanVien(maNV, tenNV, rdNu.isSelected(), sdt, email, tk);

            boolean added = nhanVienDAO.insertNhanVien(nv);
            if (added) {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể thêm nhân viên, vui lòng thử lại!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}
