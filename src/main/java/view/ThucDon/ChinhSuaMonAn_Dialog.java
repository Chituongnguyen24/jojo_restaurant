package view.ThucDon;

import javax.swing.*;
import dao.MonAn_DAO;
import entity.MonAn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChinhSuaMonAn_Dialog extends JDialog {
    private MonAn monAn;
    private ThucDon_View parentView;
    private MonAn_DAO monAnDAO = new MonAn_DAO();
    
    private JTextField txtTenMon;
    private JTextField txtDonGia;
    private JLabel lblTrangThaiHienTai;
    private JButton btnLuu;
    private JButton btnDoiTrangThai;
    private JButton btnXoa;

    public ChinhSuaMonAn_Dialog(MonAn monAn, ThucDon_View parentView) {
        this.monAn = monAn;
        this.parentView = parentView;
        initComponents();
        loadData();
    }

    private void initComponents() {
        setTitle("Chỉnh sửa món ăn");
        setModal(true);
        setSize(600, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // Panel chính
        JPanel panelMain = new JPanel(new GridLayout(4, 2, 10, 10));
        panelMain.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panelMain.setBackground(Color.WHITE);

        // Tên món
        JLabel lblTenMon = new JLabel("Tên món:");
        lblTenMon.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTenMon = new JTextField();
        txtTenMon.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Đơn giá
        JLabel lblDonGia = new JLabel("Đơn giá:");
        lblDonGia.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDonGia = new JTextField();
        txtDonGia.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Trạng thái hiện tại (thay thế checkbox)
        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblTrangThaiHienTai = new JLabel();
        lblTrangThaiHienTai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTrangThaiHienTai.setHorizontalAlignment(SwingConstants.CENTER);
        lblTrangThaiHienTai.setOpaque(true);
        lblTrangThaiHienTai.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Thêm components vào panel
        panelMain.add(lblTenMon);
        panelMain.add(txtTenMon);
        panelMain.add(lblDonGia);
        panelMain.add(txtDonGia);
        panelMain.add(lblTrangThai);
        panelMain.add(lblTrangThaiHienTai);
        
        // Placeholder cho hàng trống
        panelMain.add(new JLabel());
        panelMain.add(new JLabel());

        // Panel nút bấm
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelButtons.setBackground(Color.WHITE);
        panelButtons.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // Nút đổi trạng thái
        btnDoiTrangThai = new JButton("Đổi trạng thái");
        btnDoiTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnDoiTrangThai.setBackground(new Color(255, 193, 7));
        btnDoiTrangThai.setForeground(Color.WHITE);
        btnDoiTrangThai.setFocusPainted(false);
        btnDoiTrangThai.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doiTrangThai();
            }
        });

        // Nút xóa
        btnXoa = new JButton("Xóa món");
        btnXoa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnXoa.setBackground(new Color(220, 53, 69));
        btnXoa.setForeground(Color.WHITE);
        btnXoa.setFocusPainted(false);
        btnXoa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                xoaMonAn();
            }
        });

        // Nút lưu
        btnLuu = new JButton("Lưu thay đổi");
        btnLuu.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnLuu.setBackground(new Color(40, 167, 69));
        btnLuu.setForeground(Color.WHITE);
        btnLuu.setFocusPainted(false);
        btnLuu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                luuThayDoi();
            }
        });

        panelButtons.add(btnDoiTrangThai);
        panelButtons.add(btnXoa);
        panelButtons.add(btnLuu);

        add(panelMain, BorderLayout.CENTER);
        add(panelButtons, BorderLayout.SOUTH);
    }

    private void loadData() {
        txtTenMon.setText(monAn.getTenMonAn());
        txtDonGia.setText(String.valueOf((int)monAn.getDonGia()));
        capNhatGiaoDienTrangThai();
    }

    private void capNhatGiaoDienTrangThai() {
        if (monAn.isTrangThai()) {
            lblTrangThaiHienTai.setText("CÓ SẴN");
            lblTrangThaiHienTai.setBackground(new Color(40, 167, 69)); // Xanh lá
            lblTrangThaiHienTai.setForeground(Color.WHITE);
            btnDoiTrangThai.setText("Đổi thành HẾT MÓN");
            btnDoiTrangThai.setBackground(new Color(255, 193, 7)); // Vàng
        } else {
            lblTrangThaiHienTai.setText("HẾT MÓN");
            lblTrangThaiHienTai.setBackground(new Color(220, 53, 69)); // Đỏ
            lblTrangThaiHienTai.setForeground(Color.WHITE);
            btnDoiTrangThai.setText("Đổi thành CÓ SẴN");
            btnDoiTrangThai.setBackground(new Color(40, 167, 69)); // Xanh lá
        }
    }

    private void doiTrangThai() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn đổi trạng thái món " + monAn.getTenMonAn() + "?",
            "Xác nhận đổi trạng thái",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            monAn.setTrangThai(!monAn.isTrangThai());
            if (monAnDAO.updateMonAn(monAn)) {
                JOptionPane.showMessageDialog(this, "Đã đổi trạng thái thành công!");
                capNhatGiaoDienTrangThai();
                parentView.loadMonAn(); // Refresh danh sách
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi đổi trạng thái!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void luuThayDoi() {
        try {
            String tenMoi = txtTenMon.getText().trim();
            double donGiaMoi = Double.parseDouble(txtDonGia.getText().trim());
            
            if (tenMoi.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tên món không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (donGiaMoi <= 0) {
                JOptionPane.showMessageDialog(this, "Đơn giá phải lớn hơn 0!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            monAn.setTenMonAn(tenMoi);
            monAn.setDonGia(donGiaMoi);

            if (monAnDAO.updateMonAn(monAn)) {
                JOptionPane.showMessageDialog(this, "Đã lưu thay đổi thành công!");
                parentView.loadMonAn(); // Refresh danh sách
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi lưu thay đổi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaMonAn() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn xóa món " + monAn.getTenMonAn() + "?\nHành động này không thể hoàn tác!",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (monAnDAO.deleteMonAn(monAn.getMaMonAn())) {
                JOptionPane.showMessageDialog(this, "Đã xóa món ăn thành công!");
                parentView.loadMonAn(); // Refresh danh sách
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi xóa món ăn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}