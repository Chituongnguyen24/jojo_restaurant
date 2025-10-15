package view.KhachHang;

import dao.KhachHang_DAO;
import entity.KhachHang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class KhachHang_EditDialog extends JDialog {
    private JTextField txtTenKH, txtSDT, txtEmail;
    private JCheckBox chkThanhVien;
    private JTextField txtDiem;
    private JButton btnSave, btnCancel;
    private KhachHang_DAO khachHangDAO;
    private KhachHang khachHang;

    public KhachHang_EditDialog(Frame owner, KhachHang khachHang, KhachHang_DAO khachHangDAO) {
        super(owner, "Chỉnh sửa khách hàng", true);
        this.khachHangDAO = khachHangDAO;
        this.khachHang = khachHang;

        setSize(460, 400);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238));

        JLabel lblTitle = new JLabel("Chỉnh sửa thông tin khách hàng", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Tên khách hàng:"));
        txtTenKH = new JTextField(khachHang.getTenKhachHang());
        formPanel.add(txtTenKH);

        formPanel.add(new JLabel("Số điện thoại:"));
        txtSDT = new JTextField(khachHang.getSdt());
        formPanel.add(txtSDT);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField(khachHang.getEmail());
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Điểm tích lũy:"));
        txtDiem = new JTextField(String.valueOf(khachHang.getDiemTichLuy()));
        formPanel.add(txtDiem);

        formPanel.add(new JLabel("Là thành viên:"));
        chkThanhVien = new JCheckBox();
        chkThanhVien.setSelected(khachHang.isLaThanhVien());
        formPanel.add(chkThanhVien);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(new EmptyBorder(10, 10, 15, 10));
        btnPanel.setOpaque(false);

        btnSave = new JButton("💾 Lưu thay đổi");
        btnSave.setBackground(new Color(30, 150, 80));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSave.addActionListener(this::saveChanges);

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

    private void saveChanges(ActionEvent e) {
        try {
            khachHang.setTenKhachHang(txtTenKH.getText());
            khachHang.setSdt(txtSDT.getText());
            khachHang.setEmail(txtEmail.getText());
            khachHang.setDiemTichLuy(Integer.parseInt(txtDiem.getText()));
            khachHang.setLaThanhVien(chkThanhVien.isSelected());

            boolean updated = khachHangDAO.updateKhachHang(khachHang);
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