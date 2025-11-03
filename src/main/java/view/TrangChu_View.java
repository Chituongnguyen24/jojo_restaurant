package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import entity.TaiKhoan;
import view.Ban.*;
import view.HoaDon.*;
import view.KhachHang.*;
import view.Login.DangNhap_View;
import view.Login.DoiMatKhau_View;
import view.NhanVien.NhanVien_View;
import view.NhanVien.ThongKe_View;
import view.ThucDon.*;
import view.HoaDon.KhuyenMai_View;

public class TrangChu_View extends JPanel {

    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final List<JMenuItem> allMenuItems = new ArrayList<>();
    private JMenuItem currentMenuItem = null;
    private final JFrame mainFrame;

    private static final String CARD_HOME = "HE_THONG";
    private static final String CARD_QUAN_LY_BAN = "QUAN_LY_BAN";
    private static final String CARD_QUAN_LY_DAT_BAN = "QUAN_LY_DAT_BAN";
    private static final String CARD_QUAN_LY_DAT_MON = "QUAN_LY_DAT_MON";
    private static final String CARD_QUAN_LY_THUC_DON = "QUAN_LY_THUC_DON";
//    private static final String CARD_TRA_CUU_MON_AN = "TRA_CUU_MON_AN";
    private static final String CARD_QUAN_LY_HOADON = "QUAN_LY_HOADON";
//    private static final String CARD_TRA_CUU_THUE = "TRA_CUU_THUE";
    private static final String CARD_QUAN_LY_KHUYENMAI = "QUAN_LY_KHUYENMAI";
//    private static final String CARD_TRA_CUU_KHUYENMAI = "TRA_CUU_KHUYENMAI";
    
//    private static final String CARD_TRA_CUU_HOADON = "TRA_CUU_HOADON";
    private static final String CARD_QUAN_LY_KH = "QUAN_LY_KH";
//    private static final String CARD_DIEM_KH = "DIEM_TICHLUY";
//    private static final String CARD_TRA_CUU_KH = "TRA_CUU_KH";
    private static final String CARD_QUAN_LY_NHANVIEN = "QUAN_LY_NHAN_VIEN";
//    private static final String CARD_TRA_CUU_NV = "TRA_CUU_NV";
    private static final String CARD_THONGKE = "THONG_KE";

    private enum Role { MANAGER, RECEPTIONIST }

    public TrangChu_View(JFrame frame, TaiKhoan tk, String vaiTro) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        Role role = determineRole(tk, vaiTro);

        JPanel headerPanel = createHeaderPanel(tk, role);
        JMenuBar menuBar = createMenuBar(tk, role);
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        setupContentPanel(role, tk);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(menuBar, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, CARD_HOME);
    }

    // Normalize role detection:
    // prefer tk.getVaiTro() if set; otherwise fall back to passed vaiTro string.
    private Role determineRole(TaiKhoan tk, String vaiTro) {
        String src = null;
        if (tk != null && tk.getVaiTro() != null && !tk.getVaiTro().trim().isEmpty()) {
            src = tk.getVaiTro().trim();
        } else if (vaiTro != null && !vaiTro.trim().isEmpty()) {
            src = vaiTro.trim();
        }
        if (src == null) return Role.RECEPTIONIST;
        src = src.toLowerCase();
        if (src.contains("quản") || src.contains("quan") || src.contains("nvql") || src.contains("manager")) {
            return Role.MANAGER;
        } else {
            return Role.RECEPTIONIST;
        }
    }

    private JPanel createHeaderPanel(TaiKhoan tk, Role role) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        ImageIcon logoIcon = new ImageIcon("images/banner1.png");
        Image img = logoIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(img);

        String roleLabel = (role == Role.MANAGER) ? "Quản lý" : "Tiếp tân";
        JLabel logoLabel = new JLabel(" Nhà hàng JOJO - Hệ thống quản lý nội bộ", logoIcon, JLabel.LEFT);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 16));
        logoLabel.setForeground(new Color(50, 50, 50));
        headerPanel.add(logoLabel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        String userName = (tk != null && tk.getTenDangNhap() != null) ? tk.getTenDangNhap().trim() : "Người dùng";
        JLabel userLabel = new JLabel("Xin chào, " + userName + " (" + roleLabel + ")");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        userPanel.add(userLabel);

        JButton logoutBtn = new JButton("Đăng xuất");
        styleLogoutButton(logoutBtn);
        logoutBtn.addActionListener(createLogoutAction());
        userPanel.add(logoutBtn);
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private void styleLogoutButton(JButton btn) {
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setBackground(new Color(200, 50, 50));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 28));
    }

    private JMenuBar createMenuBar(TaiKhoan tk, Role role) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(230, 230, 230));
        menuBar.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        Font menuFont = new Font("Arial", Font.BOLD, 14);
        Font menuItemFont = new Font("Arial", Font.PLAIN, 14);

        // Hệ thống menu
        JMenu menuHeThong = new JMenu("Hệ thống");
        menuHeThong.setFont(menuFont);
        JMenuItem mDangXuat = new JMenuItem("Đăng xuất");
        mDangXuat.setFont(menuItemFont);
        mDangXuat.addActionListener(createLogoutAction());

        JMenuItem mDoiMatKhau = new JMenuItem("Đổi mật khẩu");
        mDoiMatKhau.setFont(menuItemFont);

        // FIX LỖI MÃ NHÂN VIÊN: Lấy MaNV trực tiếp từ TaiKhoan
        mDoiMatKhau.addActionListener(e -> {
            String maNVForDialog = (tk != null && tk.getNhanVien() != null) 
                    ? tk.getNhanVien().getMaNhanVien().trim() // Sử dụng MaNV nếu có
                    : (tk != null ? tk.getTenDangNhap().trim() : ""); // Fallback sang Tên đăng nhập

            if (maNVForDialog.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Không tìm thấy thông tin đăng nhập.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            DoiMatKhau_View.showDialog(mainFrame, maNVForDialog);
        });

        JMenuItem mThoat = new JMenuItem("Thoát");
        mThoat.setFont(menuItemFont);
        mThoat.addActionListener(e -> System.exit(0));

        menuHeThong.add(mDangXuat);
        menuHeThong.add(mDoiMatKhau);
        menuHeThong.addSeparator();
        menuHeThong.add(mThoat);
        menuBar.add(menuHeThong);

        // --- Common menus ---
        JMenu menuKH = new JMenu("Khách hàng");
        menuKH.setFont(menuFont);
        JMenuItem mQLKH = new JMenuItem("Quản lý khách hàng");
        mQLKH.setFont(menuItemFont);
//        JMenuItem mDiem = new JMenuItem("Quản lý điểm tích lũy");
//        mDiem.setFont(menuItemFont);
//        JMenuItem mTCKH = new JMenuItem("Tra cứu khách hàng");
//        mTCKH.setFont(menuItemFont);
        menuKH.add(mQLKH);
//        menuKH.add(mDiem);
//        menuKH.add(mTCKH);
        menuBar.add(menuKH);
        allMenuItems.add(mQLKH);
//        allMenuItems.add(mDiem);
//        allMenuItems.add(mTCKH);

        // --- Role-specific menus ---
        if (role == Role.MANAGER) {
            // Manager sees full admin menus
            JMenu menuBan = new JMenu("Bàn");
            menuBan.setFont(menuFont);
            JMenuItem mQuanLyBan = new JMenuItem("Quản lý bàn");
            mQuanLyBan.setFont(menuItemFont);
            JMenuItem mQuanLyDatBan = new JMenuItem("Quản lý đặt bàn");
            mQuanLyDatBan.setFont(menuItemFont);
            menuBan.add(mQuanLyBan);
            menuBan.add(mQuanLyDatBan);
            menuBar.add(menuBan);
            allMenuItems.add(mQuanLyBan);
            allMenuItems.add(mQuanLyDatBan);

            JMenu menuThucDon = new JMenu("Thực đơn");
            menuThucDon.setFont(menuFont);
            JMenuItem mQLDMon = new JMenuItem("Quản lý đặt món");
            mQLDMon.setFont(menuItemFont);
            JMenuItem mXemTD = new JMenuItem("Quản lý thực đơn");
            mXemTD.setFont(menuItemFont);
//            JMenuItem mTraCuuMonAn = new JMenuItem("Tra cứu món ăn");
//            mTraCuuMonAn.setFont(menuItemFont);
            menuThucDon.add(mQLDMon);
            menuThucDon.add(mXemTD);
//            menuThucDon.add(mTraCuuMonAn);
            menuBar.add(menuThucDon);
            allMenuItems.add(mQLDMon);
            allMenuItems.add(mXemTD);
//            allMenuItems.add(mTraCuuMonAn);

            JMenu menuHoaDon = new JMenu("Hóa đơn");
            menuHoaDon.setFont(menuFont);
            JMenuItem mQuanLyHoaDon = new JMenuItem("Quản lý hóa đơn");
            mQuanLyHoaDon.setFont(menuItemFont);
//            JMenuItem mTraCuuHoaDon = new JMenuItem("Tra cứu hóa đơn");
//            mTraCuuHoaDon.setFont(menuItemFont);
//            JMenuItem mTCThue = new JMenuItem("Tra cứu thuế");
//            mTCThue.setFont(menuItemFont);
            JMenuItem mQLKM = new JMenuItem("Quản lý khuyến mãi");
            mQLKM.setFont(menuItemFont);
//            JMenuItem mTCKM = new JMenuItem("Tra cứu khuyến mãi");
//            mTCKM.setFont(menuItemFont);
            menuHoaDon.add(mQuanLyHoaDon);
//            menuHoaDon.add(mTraCuuHoaDon);
//            menuHoaDon.add(mTCThue);
            menuHoaDon.add(mQLKM);
//            menuHoaDon.add(mTCKM);
            menuBar.add(menuHoaDon);
            allMenuItems.add(mQuanLyHoaDon);
//            allMenuItems.add(mTraCuuHoaDon);
//            allMenuItems.add(mTCThue);
            allMenuItems.add(mQLKM);
//            allMenuItems.add(mTCKM);
            
            
            JMenu menuNV = new JMenu("Nhân viên");
            menuNV.setFont(menuFont);
            JMenuItem mQLNV = new JMenuItem("Quản lý nhân viên");
            mQLNV.setFont(menuItemFont);
//            JMenuItem mTCNV = new JMenuItem("Tra cứu nhân viên");
//            mTCNV.setFont(menuItemFont);
            JMenuItem mTKNV = new JMenuItem("Thống kê");
            mTKNV.setFont(menuItemFont);
            menuNV.add(mQLNV);
//            menuNV.add(mTCNV);
            menuNV.addSeparator();
            menuNV.add(mTKNV);
            menuBar.add(menuNV);
            allMenuItems.add(mQLNV);
//            allMenuItems.add(mTCNV);
            allMenuItems.add(mTKNV);
        } else {
            // Receptionist: limited menus relevant to reception
            JMenu menuBan = new JMenu("Bàn");
            menuBan.setFont(menuFont);
            JMenuItem mQuanLyDatBan = new JMenuItem("Quản lý đặt bàn");
            mQuanLyDatBan.setFont(menuItemFont);
            menuBan.add(mQuanLyDatBan);
            menuBar.add(menuBan);
            allMenuItems.add(mQuanLyDatBan);

            JMenu menuThucDon = new JMenu("Thực đơn");
            menuThucDon.setFont(menuFont);
            JMenuItem mDatMon = new JMenuItem("Quản lý đặt món");
            mDatMon.setFont(menuItemFont);
//            JMenuItem mTraCuuMonAn = new JMenuItem("Tra cứu món ăn");
//            mTraCuuMonAn.setFont(menuItemFont);
            menuThucDon.add(mDatMon);
//            menuThucDon.add(mTraCuuMonAn);
            menuBar.add(menuThucDon);
            allMenuItems.add(mDatMon);
//            allMenuItems.add(mTraCuuMonAn);

            JMenu menuHoaDon = new JMenu("Hóa đơn");
            menuHoaDon.setFont(menuFont);
            JMenuItem mQuanLyHoaDon = new JMenuItem("Quản lý hóa đơn");
            mQuanLyHoaDon.setFont(menuItemFont);
//            JMenuItem mTraCuuHoaDon = new JMenuItem("Tra cứu hóa đơn");
//            mTraCuuHoaDon.setFont(menuItemFont);
//            JMenuItem mTCKM = new JMenuItem("Tra cứu khuyến mãi");
//            mTCKM.setFont(menuItemFont);
            menuHoaDon.add(mQuanLyHoaDon);
//            menuHoaDon.add(mTraCuuHoaDon);
//            menuHoaDon.add(mTCKM);
            menuBar.add(menuHoaDon);
            allMenuItems.add(mQuanLyHoaDon);
//            allMenuItems.add(mTraCuuHoaDon);
//            allMenuItems.add(mTCKM);
        }

        
        ActionListener switchPanelAction = createSwitchPanelAction();
        for (JMenuItem item : allMenuItems) {
            item.addActionListener(switchPanelAction);
        }

        return menuBar;
    }

    private void setupContentPanel(Role role, TaiKhoan tk) {
        
        contentPanel.add(new HeThong_View(), CARD_HOME);
        contentPanel.add(new DatBan_View(), CARD_QUAN_LY_DAT_BAN);
//        contentPanel.add(new HoaDon_TraCuu_View(), CARD_TRA_CUU_HOADON);
//        contentPanel.add(new MonAn_TraCuu_View(), CARD_TRA_CUU_MON_AN);
//        contentPanel.add(new KhachHang_TraCuu_View(), CARD_TRA_CUU_KH);
        contentPanel.add(new DatMonAn_View(), CARD_QUAN_LY_DAT_MON);
        contentPanel.add(new HoaDon_View(), CARD_QUAN_LY_HOADON);
//        contentPanel.add(new KhuyenMai_TraCuu_View(),CARD_TRA_CUU_KHUYENMAI);
        contentPanel.add(new KhachHang_View(), CARD_QUAN_LY_KH);
//        contentPanel.add(new KhachHang_DiemTichLuy_View(), CARD_DIEM_KH);

        if (role == Role.MANAGER) {
           
            contentPanel.add(new Ban_View(), CARD_QUAN_LY_BAN);
            contentPanel.add(new ThucDon_View(), CARD_QUAN_LY_THUC_DON);
//            contentPanel.add(new Thue_TraCuu_View(),CARD_TRA_CUU_THUE);
            contentPanel.add(new KhuyenMai_View(),CARD_QUAN_LY_KHUYENMAI);
            contentPanel.add(new NhanVien_View(), CARD_QUAN_LY_NHANVIEN);
//            contentPanel.add(new NhanVien_TraCuu_View(), CARD_TRA_CUU_NV);
            contentPanel.add(new ThongKe_View(), CARD_THONGKE);
        } 
    }

    private ActionListener createSwitchPanelAction() {
        return e -> {
            JMenuItem src = (JMenuItem) e.getSource();
            resetMenuHighlight();
            highlightMenu(src);

            String text = (src.getText() == null) ? "" : src.getText().trim();
            String cardName;
            switch (text) {
                case "Quản lý bàn":
                    cardName = CARD_QUAN_LY_BAN;
                    break;
                case "Quản lý đặt bàn":
                    cardName = CARD_QUAN_LY_DAT_BAN;
                    break;
                case "Quản lý đặt món":
                    cardName = CARD_QUAN_LY_DAT_MON;
                    break;
                case "Quản lý thực đơn":
                    cardName = CARD_QUAN_LY_THUC_DON;
                    break;
//                case "Tra cứu món ăn":
//                    cardName = CARD_TRA_CUU_MON_AN;
//                    break;
                case "Quản lý hóa đơn":
                    cardName = CARD_QUAN_LY_HOADON;
                    break;
//                case "Tra cứu hóa đơn":
//                    cardName = CARD_TRA_CUU_HOADON;
//                    break;
//                case "Tra cứu thuế":
//                    cardName = CARD_TRA_CUU_THUE;
//                    break;
                case "Quản lý khuyến mãi":
                    cardName = CARD_QUAN_LY_KHUYENMAI;
                    break;                
//                case "Tra cứu khuyến mãi":
//                    cardName = CARD_TRA_CUU_KHUYENMAI;
//                    break;
                case "Quản lý khách hàng":
                    cardName = CARD_QUAN_LY_KH;
                    break;
//                case "Quản lý điểm tích lũy":
//                    cardName = CARD_DIEM_KH;
//                    break;
//                case "Tra cứu khách hàng":
//                    cardName = CARD_TRA_CUU_KH;
//                    break;
                case "Quản lý nhân viên":
                    cardName = CARD_QUAN_LY_NHANVIEN;
                    break;
//                case "Tra cứu nhân viên":
//                    cardName = CARD_TRA_CUU_NV;
//                    break;
                case "Thống kê":
                    cardName = CARD_THONGKE;
                    break;
                default:
                    cardName = CARD_HOME;
                    break;
            }
            cardLayout.show(contentPanel, cardName);
        };
    }

    private ActionListener createLogoutAction() {
        return e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn đăng xuất?", "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                mainFrame.dispose();
                DangNhap_View login = new DangNhap_View();
                login.setVisible(true);
            }
        };
    }

    private void highlightMenu(JMenuItem item) {
        if (item != null) {
            item.setOpaque(true);
            item.setBackground(new Color(60, 120, 200));
            item.setForeground(Color.WHITE);
            currentMenuItem = item;
        }
    }

    private void resetMenuHighlight() {
        for (JMenuItem item : allMenuItems) {
            if (item != null) {
                item.setOpaque(false);
                item.setBackground(UIManager.getColor("MenuItem.background"));
                item.setForeground(UIManager.getColor("MenuItem.foreground"));
            }
        }
        currentMenuItem = null;
    }
}