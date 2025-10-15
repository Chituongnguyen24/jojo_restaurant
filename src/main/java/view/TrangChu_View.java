package view;

import javax.swing.*;

import view.NhanVien.NhanVien_ThongKe_View;
import view.NhanVien.NhanVien_TraCuu_View;
import view.NhanVien.NhanVien_View;
import view.ThucDon.ThucDon_View;

import java.awt.*;
import java.awt.event.*;

public class TrangChu_View extends JPanel {
    private JPanel contentPanel;
    private CardLayout cardLayout;

    public TrangChu_View() {
        setLayout(new BorderLayout());

        // ===== Banner trên cùng =====
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

        // ==== User info bên phải ====
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Xin chào, Nguyễn Văn A (Quản lý)");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        userPanel.add(userLabel);

        JButton logoutBtn = new JButton("Đăng xuất");
        logoutBtn.setBackground(new Color(200, 50, 50));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        userPanel.add(logoutBtn);

        headerPanel.add(userPanel, BorderLayout.EAST);

        //Thanh menu ngang
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(230, 230, 230));
        menuBar.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        //Content Panel
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        contentPanel.add(new HeThong_View(), "Hệ Thống");
        contentPanel.add(new Ban_View(), "Quản lý bàn");
        contentPanel.add(new Ban_DatBan_View(), "Quản lý đặt bàn");
        
        contentPanel.add(new ThucDon_View(), "Thực đơn");
        
        contentPanel.add(new HoaDon_View(), "Quản lý hóa đơn");
        contentPanel.add(new Thue_View(), "Quản lý thuế");
        contentPanel.add(new KhuyenMai_View(), "Quản lý khuyến mãi");
        
        contentPanel.add(new KhachHang_View(), "Quản lý khách hàng");
        contentPanel.add(new KhachHang_DiemTichLuy_View(), "Quản lý điểm tích lũy");
        
        contentPanel.add(new NhanVien_View(), "Nhân viên");
        contentPanel.add(new NhanVien_TraCuu_View(), "Tra cứu");
        contentPanel.add(new NhanVien_ThongKe_View(), "Thống kê");

        //Menu Hệ thống
        JMenu menuHeThong = new JMenu("Hệ thống");
        JMenuItem mDangXuat = new JMenuItem("Đăng xuất");
        JMenuItem mDoiMatKhau = new JMenuItem("Đổi mật khẩu");
        JMenuItem mThoat = new JMenuItem("Thoát");
        menuHeThong.add(mDangXuat);
        menuHeThong.add(mDoiMatKhau);
        menuHeThong.addSeparator();
        menuHeThong.add(mThoat);

        //Menu Bàn
        JMenu menuBan = new JMenu("Bàn");
        JMenuItem mQLBan = new JMenuItem("Quản lý bàn");
        JMenuItem mQLDatBan = new JMenuItem("Quản lý đặt bàn");
        menuBan.add(mQLBan);
        menuBan.add(mQLDatBan);

        //Menu Thực đơn
        JMenu menuThucDon = new JMenu("Thực đơn");
        JMenuItem mQLMon = new JMenuItem("Quản lý món ăn");
        JMenuItem mXemTD = new JMenuItem("Quản lý thực đơn");
        JMenuItem mTraCuuMonAn = new JMenuItem("Tra Cứu");
        menuThucDon.add(mQLMon);
        menuThucDon.add(mXemTD);
        menuThucDon.add(mTraCuuMonAn);

        //Menu Hóa đơn
        JMenu menuHoaDon = new JMenu("Hóa đơn");
        JMenuItem mQLHD = new JMenuItem("Quản lý hóa đơn");
        JMenuItem mQLThue = new JMenuItem("Quản lý thuế");
        JMenuItem mQLKM = new JMenuItem("Quản lý khuyến mãi");
        menuHoaDon.add(mQLHD);
        menuHoaDon.add(mQLThue);
        menuHoaDon.add(mQLKM);

        //Menu Khách hàng
        JMenu menuKH = new JMenu("Khách hàng");
        JMenuItem mQLKH = new JMenuItem("Quản lý khách hàng");
        JMenuItem mQLDiem = new JMenuItem("Quản lý điểm tích lũy");
        JMenuItem mTraCuuKhachHang = new JMenuItem("Tra cứu");
        menuKH.add(mQLKH);
        menuKH.add(mQLDiem);
        menuKH.add(mTraCuuKhachHang);
        //Menu Nhân viên
        JMenu menuNV = new JMenu("Nhân viên");
        JMenuItem mQuanLy = new JMenuItem("Quản lý nhân viên");
        JMenuItem mTraCuu = new JMenuItem("Tra cứu");
        JMenuItem mThongKe = new JMenuItem("Thống kê");
        JMenuItem mKhuyenMai = new JMenuItem("Khuyến mãi");

        menuNV.add(mQuanLy);
        menuNV.add(mTraCuu);
        menuNV.add(mThongKe);
        menuNV.add(mKhuyenMai);


        //Thêm vào menuBar
        menuBar.add(menuHeThong);
        menuBar.add(menuBan);
        menuBar.add(menuThucDon);
        menuBar.add(menuHoaDon);
        menuBar.add(menuKH);
        menuBar.add(menuNV);

        //Gắn hành động chuyển view
        
     // Menu Bàn
        mQLBan.addActionListener(e -> cardLayout.show(contentPanel, "Quản lý bàn"));
        mQLDatBan.addActionListener(e -> cardLayout.show(contentPanel, "Quản lý đặt bàn"));

        // Menu Thực đơn
        mQLMon.addActionListener(e -> cardLayout.show(contentPanel, "Quản lý thực đơn"));
        mXemTD.addActionListener(e -> cardLayout.show(contentPanel, "Thực đơn"));
        mTraCuuMonAn.addActionListener(e -> cardLayout.show(contentPanel, "Thực đơn"));

        // Menu Hóa đơn
        mQLHD.addActionListener(e -> cardLayout.show(contentPanel, "Quản lý hóa đơn"));
        mQLThue.addActionListener(e -> cardLayout.show(contentPanel, "Quản lý thuế"));
        mQLKM.addActionListener(e -> cardLayout.show(contentPanel, "Quản lý khuyến mãi"));

        // Menu Khách hàng
        mQLKH.addActionListener(e -> cardLayout.show(contentPanel, "Quản lý khách hàng"));
        mQLDiem.addActionListener(e -> cardLayout.show(contentPanel, "Quản lý điểm tích lũy"));
        mTraCuuKhachHang.addActionListener(e -> cardLayout.show(contentPanel, "Khách hàng"));
        
        mQuanLy.addActionListener(e -> cardLayout.show(contentPanel, "Nhân viên"));
        mTraCuu.addActionListener(e -> cardLayout.show(contentPanel, "Tra cứu"));
        mThongKe.addActionListener(e -> cardLayout.show(contentPanel, "Thống kê"));
        mDangXuat.addActionListener(e -> JOptionPane.showMessageDialog(this, "Đăng xuất thành công!"));

        //Thêm phần trên vào layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(menuBar, BorderLayout.SOUTH);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(contentPanel, BorderLayout.CENTER);

        //Mặc định hiển thị "Hệ thống"
        cardLayout.show(contentPanel, "Hệ Thống");
    }
}
