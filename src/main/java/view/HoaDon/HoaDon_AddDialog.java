package view.HoaDon;

import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import entity.HoaDon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.UUID;

public class HoaDon_AddDialog extends JDialog {
    private JTextField txtMaKH, txtPhuongThuc, txtMaKM, txtMaThue, txtMaNV, txtMaPhieu;
    private JButton btnSave, btnCancel;
    private HoaDon_DAO hoaDonDAO;
    private KhachHang_DAO khachHangDAO;

    public HoaDon_AddDialog(Frame owner, HoaDon_DAO hoaDonDAO, KhachHang_DAO khachHangDAO) {
        super(owner, "Thêm hóa đơn mới", true);
        this.hoaDonDAO = hoaDonDAO;
        this.khachHangDAO = khachHangDAO;

        setSize(460, 520);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238)); 

        // ===== Header =====
        JLabel lblTitle = new JLabel("Thêm hóa đơn mới", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);

        // ===== Form =====
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setOpaque(false);

        formPanel.add(new JLabel("Mã KH:"));
        txtMaKH = new JTextField();
        formPanel.add(txtMaKH);

        formPanel.add(new JLabel("Phương thức:"));
        txtPhuongThuc = new JTextField();
        formPanel.add(txtPhuongThuc);

        formPanel.add(new JLabel("Mã khuyến mãi:"));
        txtMaKM = new JTextField();
        formPanel.add(txtMaKM);

        formPanel.add(new JLabel("Mã thuế:"));
        txtMaThue = new JTextField();
        formPanel.add(txtMaThue);

        formPanel.add(new JLabel("Mã nhân viên:"));
        txtMaNV = new JTextField();
        formPanel.add(txtMaNV);

        formPanel.add(new JLabel("Mã phiếu:"));
        txtMaPhieu = new JTextField();
        formPanel.add(txtMaPhieu);

        // Empty row để khớp GridLayout(8,2)
        formPanel.add(new JLabel(""));
        formPanel.add(new JPanel());

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
        btnSave.addActionListener(this::saveHoaDon);

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

    private void saveHoaDon(ActionEvent e) {
        try {
            String maHD = "HD" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            String maKH = txtMaKH.getText().trim();
            String phuongThuc = txtPhuongThuc.getText().trim();
            String maKM = txtMaKM.getText().trim();
            String maThue = txtMaThue.getText().trim();
            String maNV = txtMaNV.getText().trim();
            String maPhieu = txtMaPhieu.getText().trim();

            if (maKH.isEmpty() || phuongThuc.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin hóa đơn!");
                return;
            }

            // Giả định ngày lập, giờ vào/ra hiện tại
            java.util.Date ngayLap = new java.util.Date();
            java.util.Date gioVao = new java.util.Date();
            java.util.Date gioRa = new java.util.Date();

            HoaDon hd = new HoaDon(maHD, maKH, ngayLap, phuongThuc, maKM, maThue, gioVao, gioRa, maNV, maPhieu);

            boolean added = hoaDonDAO.insertHoaDon(hd);
            if (added) {
                JOptionPane.showMessageDialog(this, "Thêm hóa đơn thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Không thể thêm hóa đơn, vui lòng thử lại!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage());
        }
    }
}