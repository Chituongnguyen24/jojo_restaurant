package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import entity.TaiKhoan;
import view.Ban.AnTrucTiep_View;
import view.Ban.Ban_View;
import view.Ban.DatBan_View;
import view.HoaDon.HoaDon_View;
import view.HoaDon.KhuyenMai_View;
import view.KhachHang.KhachHang_View;
import view.Login.DangNhap_View;
import view.Login.DoiMatKhau_View;
import view.NhanVien.NhanVien_View;
import view.NhanVien.ThongKe_View;
import view.ThucDon.DatMonAn_View;
import view.ThucDon.ThucDon_View;

public class TrangChu_View extends JPanel {

    private final JPanel contentPanel;
    private final CardLayout cardLayout;
    private final List<JMenuItem> allMenuItems = new ArrayList<>();
    private final JFrame mainFrame;

    // Khai báo các View
    private KhachHang_View khachHangView;
    private HoaDon_View hoaDonView;
    private Ban_View banView;
    private DatBan_View datBanView;
    private DatMonAn_View datMonAnView;
    private ThucDon_View thucDonView;
    private KhuyenMai_View khuyenMaiView;
    private NhanVien_View nhanVienView;
    private ThongKe_View thongKeView;
    private AnTrucTiep_View anTrucTiepView; 


    private static final String CARD_HOME = "HE_THONG";
    private static final String CARD_QUAN_LY_BAN = "QUAN_LY_BAN";
    private static final String CARD_QUAN_LY_DAT_BAN = "QUAN_LY_DAT_BAN";
    private static final String CARD_QUAN_LY_DAT_MON = "QUAN_LY_DAT_MON";
    private static final String CARD_QUAN_LY_THUC_DON = "QUAN_LY_THUC_DON";
    private static final String CARD_QUAN_LY_HOADON = "QUAN_LY_HOADON";
    private static final String CARD_QUAN_LY_KHUYENMAI = "QUAN_LY_KHUYENMAI";
    private static final String CARD_QUAN_LY_KH = "QUAN_LY_KH";
    private static final String CARD_QUAN_LY_NHANVIEN = "QUAN_LY_NHAN_VIEN";
    private static final String CARD_THONGKE = "THONG_KE";
    
    private static final String CARD_AN_TRUC_TIEP = "AN_TRUC_TIEP";

    private enum Role { MANAGER, RECEPTIONIST }

    public TrangChu_View(JFrame frame, TaiKhoan tk, String vaiTro) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        Role role = determineRole(tk, vaiTro);

        // Xóa headerPanel riêng, gộp tất cả vào menuBar
        JMenuBar menuBar = createMenuBar(tk, role);
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        setupContentPanel(role, tk);

        // Chỉ cần add menuBar trực tiếp, không cần topPanel nữa
        add(menuBar, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, CARD_HOME);
    }

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

    private JMenuBar createMenuBar(TaiKhoan tk, Role role) {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(new Color(230, 230, 230));
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        Font menuFont = new Font("Arial", Font.BOLD, 14);
        Font menuItemFont = new Font("Arial", Font.PLAIN, 14);

        // Logo icon ở đầu
        ImageIcon logoIcon = new ImageIcon("images/banner1.png");
        Image img = logoIcon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(img);
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 15));
        menuBar.add(logoLabel);

        // Hệ thống menu
        JMenu menuHeThong = new JMenu("Hệ thống");
        menuHeThong.setFont(menuFont);
        JMenuItem mDangXuat = new JMenuItem("Đăng xuất");
        mDangXuat.setFont(menuItemFont);
        mDangXuat.addActionListener(createLogoutAction());

        JMenuItem mDoiMatKhau = new JMenuItem("Đổi mật khẩu");
        mDoiMatKhau.setFont(menuItemFont);

        
        mDoiMatKhau.addActionListener(e -> {
            String maNVForDialog = (tk != null && tk.getNhanVien() != null) 
                    ? tk.getNhanVien().getMaNhanVien().trim()
                    : (tk != null ? tk.getTenDangNhap().trim() : "");

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
        menuKH.add(mQLKH);
        menuBar.add(menuKH);
        allMenuItems.add(mQLKH);

        if (role == Role.MANAGER) {

            JMenu menuBan = new JMenu("Bàn");
            menuBan.setFont(menuFont);
            JMenuItem mQuanLyBan = new JMenuItem("Quản lý bàn");
            mQuanLyBan.setFont(menuItemFont);
            JMenuItem mQuanLyDatBan = new JMenuItem("Quản lý đặt bàn");
            mQuanLyDatBan.setFont(menuItemFont);
            
            JMenuItem mAnTrucTiep = new JMenuItem("Ăn tại chỗ");
            mAnTrucTiep.setFont(menuItemFont);
            
            menuBan.add(mQuanLyBan);
            menuBan.add(mQuanLyDatBan);
            menuBan.add(mAnTrucTiep); 
            menuBar.add(menuBan);
            allMenuItems.add(mQuanLyBan);
            allMenuItems.add(mQuanLyDatBan);
            allMenuItems.add(mAnTrucTiep); 
            
            JMenu menuThucDon = new JMenu("Thực đơn");
            menuThucDon.setFont(menuFont);
            JMenuItem mQLDMon = new JMenuItem("Quản lý đặt món");
            mQLDMon.setFont(menuItemFont);
            JMenuItem mXemTD = new JMenuItem("Quản lý thực đơn");
            mXemTD.setFont(menuItemFont);
            menuThucDon.add(mQLDMon);
            menuThucDon.add(mXemTD);
            menuBar.add(menuThucDon);
            allMenuItems.add(mQLDMon);
            allMenuItems.add(mXemTD);

            JMenu menuHoaDon = new JMenu("Hóa đơn");
            menuHoaDon.setFont(menuFont);
            JMenuItem mQuanLyHoaDon = new JMenuItem("Quản lý hóa đơn");
            mQuanLyHoaDon.setFont(menuItemFont);
            JMenuItem mQLKM = new JMenuItem("Quản lý khuyến mãi");
            mQLKM.setFont(menuItemFont);
            menuHoaDon.add(mQuanLyHoaDon);
            menuHoaDon.add(mQLKM);
            menuBar.add(menuHoaDon);
            allMenuItems.add(mQuanLyHoaDon);
            allMenuItems.add(mQLKM);
            
            
            JMenu menuNV = new JMenu("Nhân viên");
            menuNV.setFont(menuFont);
            JMenuItem mQLNV = new JMenuItem("Quản lý nhân viên");
            mQLNV.setFont(menuItemFont);
            JMenuItem mTKNV = new JMenuItem("Thống kê");
            mTKNV.setFont(menuItemFont);
            menuNV.add(mQLNV);
            menuNV.addSeparator();
            menuNV.add(mTKNV);
            menuBar.add(menuNV);
            allMenuItems.add(mQLNV);
            allMenuItems.add(mTKNV);
            
        } else {
            JMenu menuBan = new JMenu("Bàn");
            menuBan.setFont(menuFont);
            JMenuItem mQuanLyDatBan = new JMenuItem("Quản lý đặt bàn");
            mQuanLyDatBan.setFont(menuItemFont);     

            JMenuItem mAnTrucTiep = new JMenuItem("Ăn tại chỗ"); 
            mAnTrucTiep.setFont(menuItemFont);
            
            menuBan.add(mQuanLyDatBan);
            menuBan.add(mAnTrucTiep); 
            menuBar.add(menuBan);
            allMenuItems.add(mQuanLyDatBan);
            allMenuItems.add(mAnTrucTiep);

            JMenu menuThucDon = new JMenu("Thực đơn");
            menuThucDon.setFont(menuFont);
            JMenuItem mDatMon = new JMenuItem("Quản lý đặt món");
            mDatMon.setFont(menuItemFont);
            menuThucDon.add(mDatMon);
            menuBar.add(menuThucDon);
            allMenuItems.add(mDatMon);

            JMenu menuHoaDon = new JMenu("Hóa đơn");
            menuHoaDon.setFont(menuFont);
            JMenuItem mQuanLyHoaDon = new JMenuItem("Quản lý hóa đơn");
            mQuanLyHoaDon.setFont(menuItemFont);
            menuHoaDon.add(mQuanLyHoaDon);
            menuBar.add(menuHoaDon);
            allMenuItems.add(mQuanLyHoaDon);
        }

        // Thêm glue để đẩy phần user info sang bên phải
        menuBar.add(Box.createHorizontalGlue());

        // Thêm thông tin user và nút đăng xuất ở cuối menuBar
        String userName = (tk != null && tk.getTenDangNhap() != null) ? tk.getTenDangNhap().trim() : "Người dùng";
        String roleLabel = (role == Role.MANAGER) ? "Quản lý" : "Tiếp tân";
        JLabel userLabel = new JLabel("Xin chào, " + userName + " (" + roleLabel + ")  ");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        userLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        menuBar.add(userLabel);

        JButton logoutBtn = new JButton("Đăng xuất");
        logoutBtn.setOpaque(true);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setBackground(new Color(200, 50, 50));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setPreferredSize(new Dimension(100, 28));
        logoutBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        logoutBtn.addActionListener(createLogoutAction());
        menuBar.add(logoutBtn);

        
        ActionListener switchPanelAction = createSwitchPanelAction();
        for (JMenuItem item : allMenuItems) {
            item.addActionListener(switchPanelAction);
        }

        return menuBar;
    }

    private void setupContentPanel(Role role, TaiKhoan tk) {
        contentPanel.add(new HeThong_View(), CARD_HOME);
    }
    
    private ActionListener createSwitchPanelAction() {
        return e -> {
            JMenuItem src = (JMenuItem) e.getSource();
            resetMenuHighlight();
            highlightMenu(src);

            String text = (src.getText() == null) ? "" : src.getText().trim();
            String cardName;

            switch (text) {
             
                case "Ăn tại chỗ":
                    cardName = CARD_AN_TRUC_TIEP;
                    if (anTrucTiepView == null) {
                        anTrucTiepView = new AnTrucTiep_View();
                        contentPanel.add(anTrucTiepView, cardName);
                    }
                    if(anTrucTiepView != null) {
                        anTrucTiepView.refreshData();
                    }
                    break;
                    
                case "Quản lý bàn":
                    cardName = CARD_QUAN_LY_BAN;
                    if (banView == null) {
                        banView = new Ban_View();
                        contentPanel.add(banView, cardName);
                    }
                    if(banView != null) {
                        banView.refreshData(); 
                    }
                    break;
                    
                case "Quản lý đặt bàn":
                    cardName = CARD_QUAN_LY_DAT_BAN;
                    if (datBanView == null) {
                        datBanView = new DatBan_View();
                        contentPanel.add(datBanView, cardName);
                    }
                    if(datBanView != null) {
                        datBanView.refreshData(); 
                    }
                    break;
                    
                case "Quản lý đặt món":
                    cardName = CARD_QUAN_LY_DAT_MON;
                    if (datMonAnView == null) {
                        datMonAnView = new DatMonAn_View();
                        contentPanel.add(datMonAnView, cardName);
                    }
                    break;
                    
                case "Quản lý thực đơn":
                    cardName = CARD_QUAN_LY_THUC_DON;
                    if (thucDonView == null) {
                        thucDonView = new ThucDon_View();
                        contentPanel.add(thucDonView, cardName);
                    }
                    break;
                    
                case "Quản lý hóa đơn":
                    cardName = CARD_QUAN_LY_HOADON;
                    if (hoaDonView == null) {
                        hoaDonView = new HoaDon_View();
                        contentPanel.add(hoaDonView, cardName);
                    }
                    if(hoaDonView != null) {
                        hoaDonView.refreshTableData(); 
                    }
                    break;

                case "Quản lý khuyến mãi":
                    cardName = CARD_QUAN_LY_KHUYENMAI;
                    if (khuyenMaiView == null) {
                        khuyenMaiView = new KhuyenMai_View();
                        contentPanel.add(khuyenMaiView, cardName);
                    }
                    break;
                    
                case "Quản lý khách hàng":
                    cardName = CARD_QUAN_LY_KH;
                    if (khachHangView == null) {
                        khachHangView = new KhachHang_View();
                        contentPanel.add(khachHangView, cardName);
                    }
                    if(khachHangView != null) {
                        khachHangView.refreshTableData();
                    }
                    break;
                    
                case "Quản lý nhân viên":
                    cardName = CARD_QUAN_LY_NHANVIEN;
                    if (nhanVienView == null) {
                        nhanVienView = new NhanVien_View();
                        contentPanel.add(nhanVienView, cardName);
                    }
                    break;

                case "Thống kê":
                    cardName = CARD_THONGKE;
                    if (thongKeView == null) {
                        thongKeView = new ThongKe_View();
                        contentPanel.add(thongKeView, cardName);
                    }
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
    }
}