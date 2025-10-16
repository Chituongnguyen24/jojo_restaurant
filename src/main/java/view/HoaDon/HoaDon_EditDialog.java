package view.HoaDon;

import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import entity.HoaDon;
import entity.KhachHang;
import entity.TaiKhoan;  // Không cần, nhưng giữ nếu có liên quan

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class HoaDon_EditDialog extends JDialog {
    private JTextField txtMaKH, txtPhuongThuc, txtMaKM, txtMaThue, txtMaNV, txtMaPhieu;
    private JButton btnSave, btnCancel;
    private HoaDon_DAO hoaDonDAO;
    private KhachHang_DAO khachHangDAO;
    private HoaDon hoaDon;

    public HoaDon_EditDialog(Frame owner, HoaDon hoaDon, HoaDon_DAO hoaDonDAO, KhachHang_DAO khachHangDAO) {
        super(owner, "Chỉnh sửa hóa đơn", true);
        this.hoaDonDAO = hoaDonDAO;
        this.khachHangDAO = khachHangDAO;
        this.hoaDon = hoaDon;

        setSize(460, 520);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238));

        JLabel lblTitle = new JLabel("Chỉnh sửa thông tin hóa đơn", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Mã KH:"));
        txtMaKH = new JTextField(hoaDon.getMaKhachHang());
        formPanel.add(txtMaKH);

        formPanel.add(new JLabel("Phương thức:"));
        txtPhuongThuc = new JTextField(hoaDon.getPhuongThuc());
        formPanel.add(txtPhuongThuc);

        formPanel.add(new JLabel("Mã khuyến mãi:"));
        txtMaKM = new JTextField(hoaDon.getMaKhuyenMai());
        formPanel.add(txtMaKM);

        formPanel.add(new JLabel("Mã thuế:"));
        txtMaThue = new JTextField(hoaDon.getMaThue());
        formPanel.add(txtMaThue);

        formPanel.add(new JLabel("Mã nhân viên:"));
        txtMaNV = new JTextField(hoaDon.getMaNhanVien());
        formPanel.add(txtMaNV);

        formPanel.add(new JLabel("Mã phiếu:"));
        txtMaPhieu = new JTextField(hoaDon.getMaPhieu());
        formPanel.add(txtMaPhieu);

        // Empty row để khớp GridLayout(8,2)
        formPanel.add(new JLabel(""));
        formPanel.add(new JPanel());

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
            hoaDon.setMaKhachHang(txtMaKH.getText());
            hoaDon.setPhuongThuc(txtPhuongThuc.getText());
            hoaDon.setMaKhuyenMai(txtMaKM.getText());
            hoaDon.setMaThue(txtMaThue.getText());
            hoaDon.setMaNhanVien(txtMaNV.getText());
            hoaDon.setMaPhieu(txtMaPhieu.getText());

            boolean updated = hoaDonDAO.updateHoaDon(hoaDon);
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