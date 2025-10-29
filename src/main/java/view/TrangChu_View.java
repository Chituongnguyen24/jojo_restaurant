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
    private JFrame mainFrame;

    private static final String CARD_HOME = "HE_THONG";
    private static final String CARD_QUAN_LY_BAN = "QUAN_LY_BAN";
    private static final String CARD_QUAN_LY_DAT_BAN = "QUAN_LY_DAT_BAN";
    private static final String CARD_QUAN_LY_DAT_MON = "QUAN_LY_DAT_MON";
    private static final String CARD_QUAN_LY_THUC_DON = "QUAN_LY_THUC_DON";
    private static final String CARD_TRA_CUU_MON_AN = "TRA_CUU_MON_AN";
    private static final String CARD_QUAN_LY_HOADON = "QUAN_LY_HOADON";
    private static final String CARD_TRA_CUU_HOADON = "TRA_CUU_HOADON";
    private static final String CARD_QUAN_LY_KH = "QUAN_LY_KH";
    private static final String CARD_DIEM_KH = "DIEM_TICHLUY";
    private static final String CARD_TRA_CUU_KH = "TRA_CUU_KH";
    private static final String CARD_QUAN_LY_NHANVIEN = "QUAN_LY_NHAN_VIEN";
    private static final String CARD_TRA_CUU_NV = "TRA_CUU_NV";
    private static final String CARD_THONGKE = "THONG_KE";

    public TrangChu_View(JFrame frame, TaiKhoan tk, String vaiTro) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        JPanel headerPanel = createHeaderPanel(tk, vaiTro);
        JMenuBar menuBar = createMenuBar(tk, vaiTro);
        setupContentPanel(tk, vaiTro);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(menuBar, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, CARD_HOME);
    }

    private JPanel createHeaderPanel(TaiKhoan tk, String vaiTro) {
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

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        userPanel.setOpaque(false);

        JLabel userLabel = new JLabel("Xin chào, " + tk.getTenDangNhap() + " (" + vaiTro + ")");
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

    private JMenuBar createMenuBar(TaiKhoan tk, String vaiTro) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(230, 230, 230));
        menuBar.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        Font menuFont = new Font("Arial", Font.BOLD, 14);
        Font menuItemFont = new Font("Arial", Font.PLAIN, 14);

        boolean isManager = "Quản lý".equalsIgnoreCase(vaiTro) || "NVQL".equalsIgnoreCase(tk.getVaiTro());
        boolean isReceptionist = "Tiếp tân".equalsIgnoreCase(vaiTro) || "TT".equalsIgnoreCase(tk.getVaiTro());

        JMenu menuHeThong = new JMenu("Hệ thống");
        menuHeThong.setFont(menuFont);
        JMenuItem mDangXuat = new JMenuItem("Đăng xuất");
        mDangXuat.setFont(menuItemFont);
        mDangXuat.addActionListener(createLogoutAction());
        JMenuItem mDoiMatKhau = new JMenuItem("Đổi mật khẩu");
        mDoiMatKhau.setFont(menuItemFont);
        JMenuItem mThoat = new JMenuItem("Thoát");
        mThoat.setFont(menuItemFont);
        mThoat.addActionListener(e -> System.exit(0));
        menuHeThong.add(mDangXuat);
        menuHeThong.add(mDoiMatKhau);
        menuHeThong.addSeparator();
        menuHeThong.add(mThoat);
        menuBar.add(menuHeThong);

        if (isManager || isReceptionist) {
            JMenu menuBan = new JMenu("Bàn");
            menuBan.setFont(menuFont);
            JMenuItem mQuanLyDatBan = new JMenuItem("Quản lý đặt bàn");
            mQuanLyDatBan.setFont(menuItemFont);
            menuBan.add(mQuanLyDatBan);
            allMenuItems.add(mQuanLyDatBan);
            
            if (isManager) {
                JMenuItem mQuanLyBan = new JMenuItem("Quản lý bàn");
                mQuanLyBan.setFont(menuItemFont);
                menuBan.add(mQuanLyBan);
                allMenuItems.add(mQuanLyBan);
            }
            menuBar.add(menuBan);
        }

        if (isManager) {
            JMenu menuThucDon = new JMenu("Thực đơn");
            menuThucDon.setFont(menuFont);
            JMenuItem mQLDMon = new JMenuItem("Quản lý đặt món");
            mQLDMon.setFont(menuItemFont);
            JMenuItem mXemTD = new JMenuItem("Quản lý thực đơn");
            mXemTD.setFont(menuItemFont);
            JMenuItem mTraCuuMonAn = new JMenuItem("Tra cứu món ăn");
            mTraCuuMonAn.setFont(menuItemFont);
            menuThucDon.add(mQLDMon);
            menuThucDon.add(mXemTD);
            menuThucDon.add(mTraCuuMonAn);
            menuBar.add(menuThucDon);
            allMenuItems.add(mQLDMon);
            allMenuItems.add(mXemTD);
            allMenuItems.add(mTraCuuMonAn);
        } else if (isReceptionist) {
            JMenu menuThucDon = new JMenu("Thực đơn");
            menuThucDon.setFont(menuFont);
            JMenuItem mDatMon = new JMenuItem("Quản lý đặt món");
            mDatMon.setFont(menuItemFont);
            JMenuItem mTraCuuMonAn = new JMenuItem("Tra cứu món ăn");
            mTraCuuMonAn.setFont(menuItemFont);
            menuThucDon.add(mDatMon);
            menuThucDon.add(mTraCuuMonAn);
            menuBar.add(menuThucDon);
            allMenuItems.add(mDatMon);
            allMenuItems.add(mTraCuuMonAn);
        }

        if (isManager || isReceptionist) {
            JMenu menuHoaDon = new JMenu("Hóa đơn");
            menuHoaDon.setFont(menuFont);
            JMenuItem mQuanLyHoaDon = new JMenuItem("Quản lý hóa đơn");
            mQuanLyHoaDon.setFont(menuItemFont);
            JMenuItem mTraCuuHoaDon = new JMenuItem("Tra cứu hóa đơn");
            mTraCuuHoaDon.setFont(menuItemFont);
            menuHoaDon.add(mQuanLyHoaDon);
            menuHoaDon.add(mTraCuuHoaDon);
            menuBar.add(menuHoaDon);
            allMenuItems.add(mQuanLyHoaDon);
            allMenuItems.add(mTraCuuHoaDon);
        }

        if (isManager || isReceptionist) {
            JMenu menuKH = new JMenu("Khách hàng");
            menuKH.setFont(menuFont);
            JMenuItem mQLKH = new JMenuItem("Quản lý khách hàng");
            mQLKH.setFont(menuItemFont);
            JMenuItem mDiem = new JMenuItem("Quản lý điểm tích lũy");
            mDiem.setFont(menuItemFont);
            JMenuItem mTCKH = new JMenuItem("Tra cứu khách hàng");
            mTCKH.setFont(menuItemFont);
            menuKH.add(mQLKH);
            menuKH.add(mDiem);
            menuKH.add(mTCKH);
            menuBar.add(menuKH);
            allMenuItems.add(mQLKH);
            allMenuItems.add(mDiem);
            allMenuItems.add(mTCKH);
        }

        if (isManager) {
            JMenu menuNV = new JMenu("Nhân viên");
            menuNV.setFont(menuFont);
            
            JMenuItem mQLNV = new JMenuItem("Quản lý nhân viên");
            mQLNV.setFont(menuItemFont);
            menuNV.add(mQLNV);

            JMenuItem mTCNV = new JMenuItem("Tra cứu nhân viên");
            mTCNV.setFont(menuItemFont);
            menuNV.add(mTCNV);

            menuNV.addSeparator();

            JMenuItem mTKNV = new JMenuItem("Thống kê");
            mTKNV.setFont(menuItemFont);
            menuNV.add(mTKNV);
            
            menuBar.add(menuNV);

            allMenuItems.add(mQLNV);
            allMenuItems.add(mTCNV);
            allMenuItems.add(mTKNV);
        }

        ActionListener switchPanelAction = createSwitchPanelAction();
        for (JMenuItem item : allMenuItems) {
            item.addActionListener(switchPanelAction);
        }

        return menuBar;
    }

    private void setupContentPanel(TaiKhoan tk, String vaiTro) {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        boolean isManager = "Quản lý".equalsIgnoreCase(vaiTro) || "NVQL".equalsIgnoreCase(tk.getVaiTro());
        boolean isReceptionist = "Tiếp tân".equalsIgnoreCase(vaiTro) || "TT".equalsIgnoreCase(tk.getVaiTro());

        contentPanel.add(new HeThong_View(), CARD_HOME);
        contentPanel.add(new DatBan_View(), CARD_QUAN_LY_DAT_BAN);
        contentPanel.add(new HoaDon_TraCuu_View(), CARD_TRA_CUU_HOADON);
        contentPanel.add(new MonAn_TraCuu_View(), CARD_TRA_CUU_MON_AN);
        contentPanel.add(new KhachHang_TraCuu_View(), CARD_TRA_CUU_KH);
        contentPanel.add(new MonAn_View(), CARD_QUAN_LY_DAT_MON);
        contentPanel.add(new HoaDon_View(), CARD_QUAN_LY_HOADON);
        contentPanel.add(new KhachHang_View(), CARD_QUAN_LY_KH);
        contentPanel.add(new KhachHang_DiemTichLuy_View(), CARD_DIEM_KH);

        if (isManager) {
            contentPanel.add(new Ban_View(), CARD_QUAN_LY_BAN);
            contentPanel.add(new ThucDon_View(), CARD_QUAN_LY_THUC_DON);
            contentPanel.add(new NhanVien_View(), CARD_QUAN_LY_NHANVIEN);
            contentPanel.add(new NhanVien_TraCuu_View(), CARD_TRA_CUU_NV);
            contentPanel.add(new ThongKe_View(), CARD_THONGKE);
        }
    }

    private ActionListener createSwitchPanelAction() {
        return e -> {
            JMenuItem src = (JMenuItem) e.getSource();
            resetMenuHighlight();
            highlightMenu(src);

            String cardName = switch (src.getText()) {
                case "Quản lý bàn" -> CARD_QUAN_LY_BAN;
                case "Quản lý đặt bàn" -> CARD_QUAN_LY_DAT_BAN;
                case "Quản lý đặt món" -> CARD_QUAN_LY_DAT_MON;
                case "Quản lý thực đơn" -> CARD_QUAN_LY_THUC_DON;
                case "Tra cứu món ăn" -> CARD_TRA_CUU_MON_AN;
                case "Quản lý hóa đơn" -> CARD_QUAN_LY_HOADON;
                case "Tra cứu hóa đơn" -> CARD_TRA_CUU_HOADON;
                case "Quản lý khách hàng" -> CARD_QUAN_LY_KH;
                case "Quản lý điểm tích lũy" -> CARD_DIEM_KH;
                case "Tra cứu khách hàng" -> CARD_TRA_CUU_KH;
                case "Quản lý nhân viên" -> CARD_QUAN_LY_NHANVIEN;
                case "Tra cứu nhân viên" -> CARD_TRA_CUU_NV;
                case "Thống kê" -> CARD_THONGKE;
                default -> CARD_HOME;
            };
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
                Login_View login = new Login_View();
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