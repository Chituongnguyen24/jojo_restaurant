package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import view.Ban.*;
import view.HoaDon.*;
import view.KhachHang.*;
import view.Login.Login_View;
import view.NhanVien.*;
import view.ThucDon.*;
import entity.TaiKhoan;

public class TrangChu_View extends JPanel {

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private List<JMenuItem> allMenuItems = new ArrayList<>();
    private JMenuItem currentMenuItem = null;

    public TrangChu_View(JFrame frame, TaiKhoan tk, String vaiTro) {
        setLayout(new BorderLayout());

        // ===== HEADER =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        ImageIcon logoIcon = new ImageIcon("images/banner1.png");
        Image img = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(img);

        JLabel logoLabel = new JLabel(" Nhà hàng JOJO - Hệ thống quản lý nội bộ", logoIcon, JLabel.LEFT);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        logoLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(logoLabel, BorderLayout.WEST);

        // ===== USER INFO + LOGOUT =====
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Xin chào, " + tk.getTenDangNhap() + " (" + vaiTro + ")");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        userPanel.add(userLabel);

        JButton logoutBtn = new JButton("Đăng xuất");
        logoutBtn.setBackground(new Color(200, 50, 50));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        userPanel.add(logoutBtn);
        headerPanel.add(userPanel, BorderLayout.EAST);

        // ===== MENU BAR =====
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(230, 230, 230));
        menuBar.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        // ===== CONTENT (CardLayout) =====
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // ===== Card names =====
        final String CARD_HOME = "HỆ_THỐNG";
        final String CARD_QUAN_LY_BAN = "QUAN_LY_BAN";
        final String CARD_QUAN_LY_DAT_BAN = "QUAN_LY_DAT_BAN";
        final String CARD_THUC_DON = "THUC_DON";
        final String CARD_QUAN_LY_HOADON = "QUAN_LY_HOADON";
        final String CARD_TRA_CUU_HOADON = "TRA_CUU_HOADON";
        final String CARD_QUAN_LY_KH = "QUAN_LY_KH";
        final String CARD_DIEM_KH = "DIEM_TICHLUY";
        final String CARD_TRA_CUU_KH = "TRA_CUU_KH";
        final String CARD_NHANVIEN = "NHAN_VIEN";
        final String CARD_TRA_CUU_NV = "TRA_CUU_NV";
        final String CARD_THONGKE_NV = "THONG_KE_NV";

        // ===== ADD VIEWS =====
        contentPanel.add(new HeThong_View(), CARD_HOME);
        contentPanel.add(new Ban_View(), CARD_QUAN_LY_BAN);
        contentPanel.add(new DatBan_View(), CARD_QUAN_LY_DAT_BAN);
        contentPanel.add(new ThucDon_View(), CARD_THUC_DON);
        contentPanel.add(new HoaDon_View(), CARD_QUAN_LY_HOADON);
        contentPanel.add(new HoaDon_TraCuu_View(), CARD_TRA_CUU_HOADON);
        contentPanel.add(new KhachHang_View(), CARD_QUAN_LY_KH);
        contentPanel.add(new KhachHang_DiemTichLuy_View(), CARD_DIEM_KH);
        contentPanel.add(new KhachHang_TraCuu_View(), CARD_TRA_CUU_KH);

        if ("Quản lý".equalsIgnoreCase(vaiTro) || "NVQL".equalsIgnoreCase(tk.getVaiTro())) {
            contentPanel.add(new NhanVien_View(), CARD_NHANVIEN);
            contentPanel.add(new NhanVien_TraCuu_View(), CARD_TRA_CUU_NV);
            contentPanel.add(new NhanVien_ThongKe_View(), CARD_THONGKE_NV);
        }

        // ===== MENU =====
        JMenu menuHeThong = new JMenu("Hệ thống");
        JMenuItem mDangXuat = new JMenuItem("Đăng xuất");
        JMenuItem mThoat = new JMenuItem("Thoát");
        menuHeThong.add(mDangXuat);
        menuHeThong.addSeparator();
        menuHeThong.add(mThoat);

        JMenu menuBan = new JMenu("Bàn");
        JMenuItem mQuanLyBan = new JMenuItem("Quản lý bàn");
        JMenuItem mQuanLyDatBan = new JMenuItem("Quản lý đặt bàn");
        menuBan.add(mQuanLyBan);
        menuBan.add(mQuanLyDatBan);

        JMenu menuThucDon = new JMenu("Thực đơn");
        JMenuItem mQuanLyMon = new JMenuItem("Thực đơn");
        menuThucDon.add(mQuanLyMon);

        JMenu menuHoaDon = new JMenu("Hóa đơn");
        JMenuItem mQuanLyHoaDon = new JMenuItem("Quản lý hóa đơn");
        JMenuItem mTraCuuHoaDon = new JMenuItem("Tra cứu hóa đơn");
        menuHoaDon.add(mQuanLyHoaDon);
        menuHoaDon.add(mTraCuuHoaDon);

        JMenu menuKH = new JMenu("Khách hàng");
        JMenuItem mQLKH = new JMenuItem("Quản lý khách hàng");
        JMenuItem mDiem = new JMenuItem("Quản lý điểm tích lũy");
        JMenuItem mTCKH = new JMenuItem("Tra cứu khách hàng");
        menuKH.add(mQLKH);
        menuKH.add(mDiem);
        menuKH.add(mTCKH);

        JMenu menuNV = null;
        JMenuItem mQLNV = null, mTCNV = null, mTKNV = null;
        if ("Quản lý".equalsIgnoreCase(vaiTro) || "NVQL".equalsIgnoreCase(tk.getVaiTro())) {
            menuNV = new JMenu("Nhân viên");
            mQLNV = new JMenuItem("Nhân viên");
            mTCNV = new JMenuItem("Tra cứu nhân viên");
            mTKNV = new JMenuItem("Thống kê nhân viên");
            menuNV.add(mQLNV);
            menuNV.add(mTCNV);
            menuNV.add(mTKNV);
        }

        // ===== ADD TO MENU BAR =====
        menuBar.add(menuHeThong);
        menuBar.add(menuBan);
        menuBar.add(menuThucDon);
        menuBar.add(menuHoaDon);
        menuBar.add(menuKH);
        if (menuNV != null) menuBar.add(menuNV);

        // ===== Lưu tất cả menu item để reset highlight =====
        allMenuItems.add(mQuanLyBan);
        allMenuItems.add(mQuanLyDatBan);
        allMenuItems.add(mQuanLyMon);
        allMenuItems.add(mQuanLyHoaDon);
        allMenuItems.add(mTraCuuHoaDon);
        allMenuItems.add(mQLKH);
        allMenuItems.add(mDiem);
        allMenuItems.add(mTCKH);
        if (mQLNV != null) {
            allMenuItems.add(mQLNV);
            allMenuItems.add(mTCNV);
            allMenuItems.add(mTKNV);
        }

        // ===== Hàm highlight =====
        ActionListener switchPanel = e -> {
            JMenuItem src = (JMenuItem) e.getSource();
            resetMenuHighlight();
            highlightMenu(src);

            switch (src.getText()) {
                case "Quản lý bàn" -> cardLayout.show(contentPanel, CARD_QUAN_LY_BAN);
                case "Quản lý đặt bàn" -> cardLayout.show(contentPanel, CARD_QUAN_LY_DAT_BAN);
                case "Thực đơn" -> cardLayout.show(contentPanel, CARD_THUC_DON);
                case "Quản lý hóa đơn" -> cardLayout.show(contentPanel, CARD_QUAN_LY_HOADON);
                case "Tra cứu hóa đơn" -> cardLayout.show(contentPanel, CARD_TRA_CUU_HOADON);
                case "Quản lý khách hàng" -> cardLayout.show(contentPanel, CARD_QUAN_LY_KH);
                case "Quản lý điểm tích lũy" -> cardLayout.show(contentPanel, CARD_DIEM_KH);
                case "Tra cứu khách hàng" -> cardLayout.show(contentPanel, CARD_TRA_CUU_KH);
                case "Nhân viên" -> cardLayout.show(contentPanel, CARD_NHANVIEN);
                case "Tra cứu nhân viên" -> cardLayout.show(contentPanel, CARD_TRA_CUU_NV);
                case "Thống kê nhân viên" -> cardLayout.show(contentPanel, CARD_THONGKE_NV);
            }
            contentPanel.revalidate();
            contentPanel.repaint();
        };

        // ===== Gán action =====
        for (JMenuItem item : allMenuItems) {
            item.addActionListener(switchPanel);
        }

        // ===== Đăng xuất =====
        ActionListener logoutAction = e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn đăng xuất?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                frame.dispose();
                Login_View login = new Login_View();
                login.setVisible(true);
            }
        };
        logoutBtn.addActionListener(logoutAction);
        mDangXuat.addActionListener(logoutAction);
        mThoat.addActionListener(e -> System.exit(0));

        // ===== ADD TO LAYOUT =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(menuBar, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        // Hiển thị view mặc định
        cardLayout.show(contentPanel, CARD_HOME);
    }

    private void highlightMenu(JMenuItem item) {
        item.setBackground(new Color(60, 120, 200));
        item.setForeground(Color.WHITE);
        currentMenuItem = item;
    }

    private void resetMenuHighlight() {
        for (JMenuItem item : allMenuItems) {
            item.setBackground(UIManager.getColor("MenuItem.background"));
            item.setForeground(Color.BLACK);
        }
    }
}