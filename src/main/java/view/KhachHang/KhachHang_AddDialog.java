package view.KhachHang;

import dao.KhachHang_DAO;
import entity.KhachHang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.UUID;

public class KhachHang_AddDialog extends JDialog {
    private JTextField txtTenKH, txtSDT, txtEmail;
    private JCheckBox chkThanhVien;
    private JTextField txtDiem;
    private JButton btnSave, btnCancel;
    private KhachHang_DAO khachHangDAO;

    public KhachHang_AddDialog(Frame owner, KhachHang_DAO khachHangDAO) {
        super(owner, "Thêm khách hàng mới", true);
        this.khachHangDAO = khachHangDAO;

        setSize(460, 400);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238)); 

        // ===== Header =====
        JLabel lblTitle = new JLabel("Thêm khách hàng mới", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        // ===== Form =====
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Tên khách hàng:"));
        txtTenKH = new JTextField();
        formPanel.add(txtTenKH);

        formPanel.add(new JLabel("Số điện thoại:"));
        txtSDT = new JTextField();
        formPanel.add(txtSDT);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField();
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Điểm tích lũy:"));
        txtDiem = new JTextField("0");
        formPanel.add(txtDiem);

        formPanel.add(new JLabel("Là thành viên:"));
        chkThanhVien = new JCheckBox();
        chkThanhVien.setSelected(false);
        formPanel.add(chkThanhVien);

        add(formPanel, BorderLayout.CENTER);

        // ===== Buttons =====
        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(new EmptyBorder(10, 10, 15, 10));
        btnPanel.setOpaque(false);

        btnSave = new JButton("💾 Lưu");
        btnSave.setBackground(new Color(30, 150, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.addActionListener(this::saveKhachHang);

        btnCancel = new JButton("✖ Hủy");
        btnCancel.setBackground(new Color(200, 80, 70));
        btnCancel.setForeground(Color.WHITE);
        btnCancel.setFocusPainted(false);
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancel.addActionListener(e -> dispose());

        btnPanel.add(btnSave);
        btnPanel.add(btnCancel);

        add(btnPanel, BorderLayout.SOUTH);
    }

    private void saveKhachHang(ActionEvent e) {
        try {
            String maKH = "KH" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            String tenKH = txtTenKH.getText().trim();
            String sdt = txtSDT.getText().trim();
            String email = txtEmail.getText().trim();
            int diem = Integer.parseInt(txtDiem.getText().trim());
            boolean thanhVien = chkThanhVien.isSelected();

            if (tenKH.isEmpty() || sdt.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin khách hàng!");
                return;
            }

            KhachHang kh = new KhachHang(maKH, tenKH, sdt, email, diem, thanhVien);

            boolean added = khachHangDAO.insertKhachHang(kh);
            if (added) {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể thêm khách hàng, vui lòng thử lại!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}