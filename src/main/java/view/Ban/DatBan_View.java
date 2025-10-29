package view.Ban;

import dao.Ban_DAO;
import dao.DatBan_DAO;
import dao.HoaDon_DAO;
import dao.HoaDon_KhuyenMai_DAO;
import dao.HoaDon_Thue_DAO;
import dao.KhachHang_DAO;
import entity.Ban;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.PhieuDatBan;
import entity.Thue;
import enums.TrangThaiBan;
import view.HoaDon.HoaDon_ThanhToan_Dialog; // Import dialog thanh toán

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;
import java.awt.event.MouseListener;
import java.util.Date;

public class DatBan_View extends JPanel {

    private Ban_DAO banDAO;
    private DatBan_DAO datBanDAO;

    private Map<String, List<Ban>> danhSachBanTheoKhuVuc;
    private List<String> tenKhuVuc;
    private String khuVucHienTai;
    private List<PhieuDatBan> danhSachPhieuDatDangHoatDong;

    private Map<String, String> areaImagePaths = new LinkedHashMap<String, String>() {{
        put("Sân thượng", "images/icon/bannho.png");
        put("Sân vườn", "images/icon/thongthuong.png");
        put("Tầng 2", "images/icon/bannho.png");
        put("Tầng trệt", "images/icon/thongthuong.png");
        put("Phòng VIP", "images/icon/vip.png");
    }};
    private static final String DEFAULT_TABLE_IMAGE = "images/icon/thongthuong.png";
    private Map<String, ImageIcon> areaIcons;

    private WrappingFlowPanel pnlLuoiBan;
    private Map<String, JButton> cacNutChonKhuVuc;
    private JPanel pnlChuaNutKhuVuc;
    private JPanel pnlSidebarTrai;
    private JLabel lblThongKeBan;
    private JLabel lblDateTime;
    private Timer clockTimer;

    private static final Color MAU_NEN = new Color(248, 249, 250);
    private static final Color MAU_TRANG = Color.WHITE;
    private static final Color MAU_CHU_CHINH = new Color(33, 37, 41);
    private static final Color MAU_CHU_PHU = new Color(108, 117, 125);
    private static final Color MAU_VIEN = new Color(222, 226, 230);
    private static final Color MAU_CAM_CHINH = new Color(255, 138, 0);
    private static final Color MAU_CAM_NHE = new Color(255, 243, 224);
    private static final Color MAU_XANH_LA = new Color(40, 167, 69);
    private static final Color MAU_DO = new Color(220, 53, 69);
    private static final Color MAU_VANG = new Color(255, 193, 7);

    private static final Color MAU_TRANGTHAI_TRONG = new Color(40, 167, 69);
    private static final Color MAU_TRANGTHAI_CO_KHACH = new Color(220, 53, 69);
    private static final Color MAU_TRANGTHAI_DA_DAT = new Color(255, 193, 7);

    private static final Font FONT_TIEUDE_LON = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font FONT_TIEUDE_CHINH = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font FONT_TIEUDE_NHO = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_CHU_NHO = new Font("Segoe UI", Font.PLAIN, 12);

    public DatBan_View() {
        banDAO = new Ban_DAO();
        datBanDAO = new DatBan_DAO();
        danhSachBanTheoKhuVuc = new LinkedHashMap<>();
        tenKhuVuc = new ArrayList<>();
        cacNutChonKhuVuc = new LinkedHashMap<>();
        danhSachPhieuDatDangHoatDong = new ArrayList<>();
        areaIcons = new LinkedHashMap<>();

        taiTatCaIconKhuVuc();
        thietLapGiaoDien();
        taiDuLieuVaHienThiBanDau();
    }

    private void taiTatCaIconKhuVuc() {
        for (Map.Entry<String, String> entry : areaImagePaths.entrySet()) {
            ImageIcon icon = taiImageIcon(entry.getValue());
            if (icon != null) {
                areaIcons.put(entry.getKey(), icon);
            }
        }
        ImageIcon defaultIcon = taiImageIcon(DEFAULT_TABLE_IMAGE);
        if (defaultIcon != null) {
            areaIcons.put("DEFAULT", defaultIcon);
        }
    }

    private ImageIcon taiImageIcon(String imagePath) {
        try {
            ImageIcon o = new ImageIcon(imagePath);
            if (o == null || o.getIconWidth() <= 0) {
                return null;
            }
            Image img = o.getImage();
            if (img == null) {
                return null;
            }
            Image scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            if (scaledImg == null) {
                return null;
            }
            return new ImageIcon(scaledImg);
        } catch (Exception e) {
            System.err.println("Error loading/scaling icon: " + imagePath + " - " + e.getMessage());
            return null;
        }
    }

    private ImageIcon layIconChoKhuVucHienTai() {
        ImageIcon icon = areaIcons.get(khuVucHienTai);
        return icon != null ? icon : areaIcons.getOrDefault("DEFAULT", null);
    }

    private void thietLapGiaoDien() {
        setLayout(new BorderLayout(0, 0));
        setBackground(MAU_NEN);

        pnlSidebarTrai = taoSidebarChonKhuVuc();
        add(pnlSidebarTrai, BorderLayout.WEST);

        JPanel pnlCenter = new JPanel(new BorderLayout(0, 0));
        pnlCenter.setBackground(MAU_NEN);

        JPanel pnlHeader = taoPanelHeader();
        pnlCenter.add(pnlHeader, BorderLayout.NORTH);

        pnlLuoiBan = new WrappingFlowPanel();
        pnlLuoiBan.setBackground(MAU_TRANG);
        pnlLuoiBan.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JScrollPane scrollLuoiBan = new JScrollPane(pnlLuoiBan);
        scrollLuoiBan.setBorder(null);
        scrollLuoiBan.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollLuoiBan.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollLuoiBan.getVerticalScrollBar().setUnitIncrement(16);
        pnlCenter.add(scrollLuoiBan, BorderLayout.CENTER);

        add(pnlCenter, BorderLayout.CENTER);
    }

    private JPanel taoPanelHeader() {
        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BorderLayout(15, 0));
        pnlHeader.setBackground(MAU_TRANG);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, MAU_VIEN),
            BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));

        JPanel pnlLeft = new JPanel();
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setOpaque(false);

        JLabel lblTitle = new JLabel("Quản lý đặt bàn");
        lblTitle.setFont(FONT_TIEUDE_LON);
        lblTitle.setForeground(MAU_CHU_CHINH);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblThongKeBan = new JLabel("Đang tải...");
        lblThongKeBan.setFont(FONT_CHU);
        lblThongKeBan.setForeground(MAU_CHU_PHU);
        lblThongKeBan.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Thêm đồng hồ thời gian thực
        lblDateTime = new JLabel();
        lblDateTime.setFont(FONT_CHU_NHO);
        lblDateTime.setForeground(MAU_CHU_PHU);
        lblDateTime.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        clockTimer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm:ss");
            lblDateTime.setText(sdf.format(new Date()));
        });
        clockTimer.start();

        pnlLeft.add(lblTitle);
        pnlLeft.add(Box.createVerticalStrut(8));
        pnlLeft.add(lblThongKeBan);
        pnlLeft.add(Box.createVerticalStrut(5));
        pnlLeft.add(lblDateTime);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRight.setOpaque(false);

        // Nút làm mới
        JButton btnRefresh = new JButton("🔄 Làm mới");
        btnRefresh.setFont(FONT_CHU);
        btnRefresh.setBackground(MAU_CAM_NHE);
        btnRefresh.setForeground(MAU_CAM_CHINH);
        btnRefresh.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAU_CAM_CHINH, 1, true),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(e -> refreshData());
        
        btnRefresh.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnRefresh.setBackground(new Color(255, 235, 204));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnRefresh.setBackground(MAU_CAM_NHE);
            }
        });

        JPanel pnlLegend = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        pnlLegend.setBackground(MAU_TRANG);
        pnlLegend.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAU_VIEN, 1, true),
            BorderFactory.createEmptyBorder(10, 18, 10, 18)
        ));
        pnlLegend.add(taoMucChuThich("Trống", MAU_TRANGTHAI_TRONG));
        pnlLegend.add(taoMucChuThich("Đã đặt", MAU_TRANGTHAI_DA_DAT));
        pnlLegend.add(taoMucChuThich("Có khách", MAU_TRANGTHAI_CO_KHACH));

        pnlRight.add(btnRefresh);
        pnlRight.add(pnlLegend);

        pnlHeader.add(pnlLeft, BorderLayout.WEST);
        pnlHeader.add(pnlRight, BorderLayout.EAST);

        return pnlHeader;
    }

    private void taiDuLieuVaHienThiBanDau() {
        taiDuLieuKhuVuc();
        taiDuLieuDatBan();
        dongBoTrangThaiDatBan();
        capNhatNoiDungSidebar();
        capNhatLuaChonSidebar();
        capNhatHienThiLuoiBan();
        capNhatThongKeBan();
    }

    public void refreshData() {
        SwingUtilities.invokeLater(this::taiDuLieuVaHienThiBanDau);
    }

    private void taiDuLieuKhuVuc() {
        try {
            Map<String, List<Ban>> banTheoKhuVuc = datBanDAO.getAllBanByFloor();
            tenKhuVuc = new ArrayList<>(banTheoKhuVuc.keySet());
            danhSachBanTheoKhuVuc = banTheoKhuVuc;

            if (khuVucHienTai == null || !tenKhuVuc.contains(khuVucHienTai)) {
                if (!tenKhuVuc.isEmpty()) {
                    khuVucHienTai = tenKhuVuc.get(0);
                } else {
                    khuVucHienTai = "Không có dữ liệu";
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading area data: " + e.getMessage());
            tenKhuVuc = new ArrayList<>();
            danhSachBanTheoKhuVuc = new LinkedHashMap<>();
            khuVucHienTai = "Lỗi tải dữ liệu";
        }
    }

    private void taiDuLieuDatBan() {
        try {
            danhSachPhieuDatDangHoatDong = datBanDAO.getAllPhieuDatBan();
            if (danhSachPhieuDatDangHoatDong == null) {
                danhSachPhieuDatDangHoatDong = new ArrayList<>();
            }
        } catch (Exception e) {
            System.err.println("Error loading booking data: " + e.getMessage());
            danhSachPhieuDatDangHoatDong = new ArrayList<>();
        }
    }

    private void dongBoTrangThaiDatBan() {
        Set<String> maBanDaDat = new HashSet<>();
        if (danhSachPhieuDatDangHoatDong != null) {
            for (PhieuDatBan phieu : danhSachPhieuDatDangHoatDong) {
                if (phieu != null && phieu.getBan() != null && phieu.getBan().getMaBan() != null) {
                    maBanDaDat.add(phieu.getBan().getMaBan().trim());
                }
            }
        }

        if (danhSachBanTheoKhuVuc != null) {
            for (List<Ban> danhSachBan : danhSachBanTheoKhuVuc.values()) {
                if (danhSachBan != null) {
                    for (Ban ban : danhSachBan) {
                        if (ban != null && ban.getMaBan() != null) {
                            String maBanHienTai = ban.getMaBan().trim();
                            TrangThaiBan trangThaiHienTai = ban.getTrangThai();

                            boolean coPhieuDat = maBanDaDat.contains(maBanHienTai);

                            if (coPhieuDat) {
                                if (trangThaiHienTai == TrangThaiBan.TRONG) {
                                    ban.setTrangThai(TrangThaiBan.DA_DAT);
                                }
                            } else {
                                if (trangThaiHienTai == TrangThaiBan.DA_DAT) {
                                    ban.setTrangThai(TrangThaiBan.TRONG);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private JPanel taoSidebarChonKhuVuc() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout(0, 15));
        sidebar.setBackground(MAU_TRANG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, MAU_VIEN));
        sidebar.setPreferredSize(new Dimension(260, 0));

        JPanel pnlHeaderSidebar = new JPanel();
        pnlHeaderSidebar.setLayout(new BoxLayout(pnlHeaderSidebar, BoxLayout.Y_AXIS));
        pnlHeaderSidebar.setOpaque(false);
        pnlHeaderSidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 10, 15));

        JLabel lblTitle = new JLabel("Khu vực");
        lblTitle.setFont(FONT_TIEUDE_CHINH);
        lblTitle.setForeground(MAU_CHU_CHINH);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubtitle = new JLabel("Chọn khu vực để xem bàn");
        lblSubtitle.setFont(FONT_CHU_NHO);
        lblSubtitle.setForeground(MAU_CHU_PHU);
        lblSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlHeaderSidebar.add(lblTitle);
        pnlHeaderSidebar.add(Box.createVerticalStrut(5));
        pnlHeaderSidebar.add(lblSubtitle);
        pnlHeaderSidebar.add(Box.createVerticalStrut(10));
        JSeparator separator = new JSeparator();
        separator.setForeground(MAU_VIEN);
        pnlHeaderSidebar.add(separator);

        pnlChuaNutKhuVuc = new JPanel();
        pnlChuaNutKhuVuc.setLayout(new BoxLayout(pnlChuaNutKhuVuc, BoxLayout.Y_AXIS));
        pnlChuaNutKhuVuc.setBackground(MAU_NEN);
        pnlChuaNutKhuVuc.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JScrollPane scrollKhuVuc = new JScrollPane(pnlChuaNutKhuVuc);
        scrollKhuVuc.setBorder(null);
        scrollKhuVuc.setBackground(MAU_NEN);
        scrollKhuVuc.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollKhuVuc.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollKhuVuc.getVerticalScrollBar().setUnitIncrement(16);

        sidebar.add(pnlHeaderSidebar, BorderLayout.NORTH);
        sidebar.add(scrollKhuVuc, BorderLayout.CENTER);

        return sidebar;
    }

    private JPanel taoMucChuThich(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        item.setOpaque(false);

        JPanel circle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(14, 14);
            }
        };
        circle.setBackground(color);
        circle.setOpaque(true);

        JLabel label = new JLabel(text);
        label.setFont(FONT_CHU);
        label.setForeground(MAU_CHU_CHINH);

        item.add(circle);
        item.add(label);
        return item;
    }

    private void capNhatNoiDungSidebar() {
        pnlChuaNutKhuVuc.removeAll();
        cacNutChonKhuVuc.clear();

        if (tenKhuVuc == null || tenKhuVuc.isEmpty()) {
            JLabel lblEmpty = new JLabel("Không có khu vực");
            lblEmpty.setFont(FONT_CHU);
            lblEmpty.setForeground(MAU_CHU_PHU);
            lblEmpty.setHorizontalAlignment(SwingConstants.CENTER);
            pnlChuaNutKhuVuc.add(lblEmpty);
        } else {
            for (String tenKV : tenKhuVuc) {
                if (tenKV != null) {
                    List<Ban> dsBan = danhSachBanTheoKhuVuc.get(tenKV);
                    int soBan = (dsBan != null) ? dsBan.size() : 0;

                    JButton btn = taoNutChonKhuVuc(tenKV, soBan);
                    btn.addActionListener(e -> chuyenKhuVuc(tenKV));
                    cacNutChonKhuVuc.put(tenKV, btn);
                    pnlChuaNutKhuVuc.add(btn);
                    pnlChuaNutKhuVuc.add(Box.createVerticalStrut(8));
                }
            }
        }

        pnlChuaNutKhuVuc.revalidate();
        pnlChuaNutKhuVuc.repaint();
    }

    private void chuyenKhuVuc(String tenKV) {
        if (tenKV != null && !tenKV.equals(khuVucHienTai)) {
            khuVucHienTai = tenKV;
            capNhatLuaChonSidebar();
            capNhatHienThiLuoiBan();
            capNhatThongKeBan();
        }
    }

    private void capNhatLuaChonSidebar() {
        if (cacNutChonKhuVuc != null) {
            for (Map.Entry<String, JButton> entry : cacNutChonKhuVuc.entrySet()) {
                if (entry != null && entry.getKey() != null && entry.getValue() != null) {
                    dinhDangNutKhuVuc(entry.getValue(), entry.getKey().equals(khuVucHienTai));
                }
            }
        }
    }

    private JButton taoNutChonKhuVuc(String ten, int soBan) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(10, 3));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);

        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setOpaque(false);

        JLabel lblTen = new JLabel(ten);
        lblTen.setFont(FONT_TIEUDE_NHO);
        lblTen.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTongBan = new JLabel(soBan + " bàn");
        lblTongBan.setFont(FONT_CHU_NHO);
        lblTongBan.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnlStats = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        pnlStats.setOpaque(false);
        pnlStats.setAlignmentX(Component.LEFT_ALIGNMENT);

        List<Ban> dsBan = danhSachBanTheoKhuVuc.get(ten);
        int trong = 0, daDat = 0, coKhach = 0;
        if (dsBan != null) {
            for (Ban ban : dsBan) {
                if (ban != null && ban.getTrangThai() != null) {
                    switch (ban.getTrangThai()) {
                        case TRONG: trong++; break;
                        case DA_DAT: daDat++; break;
                        case CO_KHACH: coKhach++; break;
                    }
                }
            }
        }

        if (trong > 0) pnlStats.add(taoBadgeTrangThai(trong, MAU_TRANGTHAI_TRONG));
        if (daDat > 0) pnlStats.add(taoBadgeTrangThai(daDat, MAU_TRANGTHAI_DA_DAT));
        if (coKhach > 0) pnlStats.add(taoBadgeTrangThai(coKhach, MAU_TRANGTHAI_CO_KHACH));

        pnlContent.add(lblTen);
        pnlContent.add(Box.createVerticalStrut(3));
        pnlContent.add(lblTongBan);
        pnlContent.add(Box.createVerticalStrut(5));
        pnlContent.add(pnlStats);

        btn.add(pnlContent, BorderLayout.CENTER);
        dinhDangNutKhuVuc(btn, false);

        return btn;
    }

    private JPanel taoBadgeTrangThai(int soLuong, Color mauNen) {
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        Color bgColor = new Color(mauNen.getRed(), mauNen.getGreen(), mauNen.getBlue(), 40);
        badge.setBackground(bgColor);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(mauNen.darker(), 1, true),
            BorderFactory.createEmptyBorder(1, 5, 1, 5)
        ));

        JLabel lblSo = new JLabel(String.valueOf(soLuong));
        lblSo.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblSo.setForeground(mauNen.darker().darker());

        badge.add(lblSo);
        return badge;
    }

    private void dinhDangNutKhuVuc(JButton btn, boolean selected) {
        if (btn == null || btn.getComponentCount() == 0) return;

        if (btn.getComponent(0) instanceof JPanel) {
            JPanel pnlContent = (JPanel) btn.getComponent(0);
            if (pnlContent.getComponentCount() >= 3 &&
                pnlContent.getComponent(0) instanceof JLabel &&
                pnlContent.getComponent(2) instanceof JLabel)
            {
                JLabel lblTen = (JLabel) pnlContent.getComponent(0);
                JLabel lblTongBan = (JLabel) pnlContent.getComponent(2);

                if (selected) {
                    btn.setBackground(MAU_CAM_NHE);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, MAU_CAM_CHINH),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                    ));
                    lblTen.setForeground(MAU_CAM_CHINH);
                    lblTongBan.setForeground(MAU_CAM_CHINH.darker());
                } else {
                    btn.setBackground(MAU_TRANG);
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, MAU_TRANG),
                        BorderFactory.createEmptyBorder(10, 12, 10, 12)
                    ));
                    lblTen.setForeground(MAU_CHU_CHINH);
                    lblTongBan.setForeground(MAU_CHU_PHU);
                }
            }
        }

        for (MouseListener ml : btn.getMouseListeners()) {
            if (ml instanceof MouseAdapter && ml.getClass().isAnonymousClass()) {
                btn.removeMouseListener(ml);
            }
        }

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!selected) {
                    btn.setBackground(MAU_NEN);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(selected ? MAU_CAM_NHE : MAU_TRANG);
            }
        });
    }

    private void capNhatThongKeBan() {
        if (khuVucHienTai == null || danhSachBanTheoKhuVuc == null) {
            lblThongKeBan.setText("Không có dữ liệu khu vực");
            return;
        }

        List<Ban> dsBan = danhSachBanTheoKhuVuc.get(khuVucHienTai);
        int total = (dsBan != null) ? dsBan.size() : 0;

        int trong = 0, daDat = 0, coKhach = 0;
        if (dsBan != null) {
            for (Ban ban : dsBan) {
                if (ban != null && ban.getTrangThai() != null) {
                    switch (ban.getTrangThai()) {
                        case TRONG: trong++; break;
                        case DA_DAT: daDat++; break;
                        case CO_KHACH: coKhach++; break;
                    }
                }
            }
        }

        lblThongKeBan.setText(String.format(
            "%s • %d bàn (Trống: %d, Đã đặt: %d, Có khách: %d)",
            khuVucHienTai, total, trong, daDat, coKhach
        ));
    }

    void capNhatHienThiLuoiBan() {
        pnlLuoiBan.removeAll();

        if (khuVucHienTai == null || danhSachBanTheoKhuVuc == null) {
            pnlLuoiBan.add(new JLabel("Không thể tải danh sách bàn."));
        } else {
            List<Ban> dsBanTrongKV = danhSachBanTheoKhuVuc.get(khuVucHienTai);

            if (dsBanTrongKV != null && !dsBanTrongKV.isEmpty()) {
                for (Ban ban : dsBanTrongKV) {
                    if (ban != null) {
                        JPanel card = taoTheBan(ban);
                        pnlLuoiBan.add(card);
                    }
                }
            } else {
                JPanel emptyPanel = new JPanel(new GridBagLayout());
                emptyPanel.setBackground(MAU_TRANG);
                emptyPanel.setPreferredSize(new Dimension(600, 400));

                JPanel emptyContent = new JPanel();
                emptyContent.setLayout(new BoxLayout(emptyContent, BoxLayout.Y_AXIS));
                emptyContent.setOpaque(false);

                JLabel iconEmpty = new JLabel("🪑");
                iconEmpty.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
                iconEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel lblEmpty = new JLabel("Khu vực này không có bàn");
                lblEmpty.setFont(FONT_TIEUDE_NHO);
                lblEmpty.setForeground(MAU_CHU_PHU);
                lblEmpty.setAlignmentX(Component.CENTER_ALIGNMENT);

                emptyContent.add(iconEmpty);
                emptyContent.add(Box.createVerticalStrut(10));
                emptyContent.add(lblEmpty);

                emptyPanel.add(emptyContent);
                pnlLuoiBan.add(emptyPanel);
            }
        }

        pnlLuoiBan.revalidate();
        pnlLuoiBan.repaint();
    }

    private JPanel taoTheBan(Ban ban) {
        RoundedPanel card = new RoundedPanel(null, 12);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MAU_TRANG);

        Color mauVien, mauNen, mauChu;
        String trangThaiText, iconEmoji;

        TrangThaiBan currentStatus = ban.getTrangThai() != null ? ban.getTrangThai() : TrangThaiBan.TRONG;

        switch (currentStatus) {
            case TRONG:
                mauVien = MAU_TRANGTHAI_TRONG;
                mauNen = new Color(232, 245, 233);
                mauChu = MAU_TRANGTHAI_TRONG.darker();
                trangThaiText = "Trống";
                iconEmoji = "✓";
                break;
            case CO_KHACH:
                mauVien = MAU_TRANGTHAI_CO_KHACH;
                mauNen = new Color(255, 235, 238);
                mauChu = MAU_TRANGTHAI_CO_KHACH.darker();
                trangThaiText = "Có khách";
                iconEmoji = "👥";
                break;
            default:
                mauVien = MAU_TRANGTHAI_DA_DAT;
                mauNen = new Color(255, 248, 225);
                mauChu = MAU_TRANGTHAI_DA_DAT.darker();
                trangThaiText = "Đã đặt";
                iconEmoji = "📅";
                break;
        }

        card.setBorderColor(mauVien);
        card.setBorderThickness(2);
        card.setBorder(BorderFactory.createEmptyBorder(15, 12, 15, 12));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setPreferredSize(new Dimension(180, 200));
        card.setMaximumSize(new Dimension(180, 200));

        // Icon emoji nhỏ
        JLabel lblEmoji = new JLabel(iconEmoji);
        lblEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        lblEmoji.setHorizontalAlignment(SwingConstants.CENTER);
        lblEmoji.setForeground(mauChu);
        lblEmoji.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Icon bàn chính
        ImageIcon currentIcon = layIconChoKhuVucHienTai();
        JLabel lblIcon;
        if (currentIcon != null) {
            lblIcon = new JLabel(currentIcon);
        } else {
            lblIcon = new JLabel("🪑");
            lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            lblIcon.setForeground(MAU_CHU_PHU);
        }
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        String loaiBanTen = (ban.getLoaiBan() != null) ? ban.getLoaiBan().getTenHienThi() : "N/A";
        if ("Bàn VIP".equalsIgnoreCase(loaiBanTen)) loaiBanTen = "VIP";

        JLabel lblName = new JLabel(ban.getMaBan() != null ? ban.getMaBan().trim() : "N/A");
        lblName.setFont(FONT_TIEUDE_CHINH);
        lblName.setForeground(MAU_CHU_CHINH);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblInfo = new JLabel(String.format("%s • %d chỗ", loaiBanTen, ban.getSoCho()));
        lblInfo.setFont(FONT_CHU_NHO);
        lblInfo.setForeground(MAU_CHU_PHU);
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pnlStatus.setOpaque(true);
        pnlStatus.setBackground(mauNen);
        pnlStatus.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        pnlStatus.setMaximumSize(new Dimension(160, 30));
        pnlStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblStatusText = new JLabel(trangThaiText);
        lblStatusText.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStatusText.setForeground(mauChu);

        pnlStatus.add(lblEmoji);
        pnlStatus.add(lblStatusText);

        card.add(lblIcon);
        card.add(Box.createVerticalStrut(8));
        card.add(lblName);
        card.add(Box.createVerticalStrut(4));
        card.add(lblInfo);
        card.add(Box.createVerticalStrut(10));
        card.add(pnlStatus);

        // Xóa các MouseListener cũ
        for(MouseListener ml : card.getMouseListeners()) {
            if (ml instanceof MouseAdapter && ml.getClass().isAnonymousClass()){
                card.removeMouseListener(ml);
            }
        }

        card.addMouseListener(new MouseAdapter() {
            private Color originalBg = MAU_TRANG;

            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 250, 252));
                card.setBorderThickness(3);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(originalBg);
                card.setBorderThickness(2);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || ban == null) return;

                TrangThaiBan trangThai = ban.getTrangThai();
                if (trangThai == null) trangThai = TrangThaiBan.TRONG;

                switch(trangThai) {
                    case TRONG:
                        moDialogDatBan(ban);
                        break;
                    case DA_DAT:
                        xuLyBanDaDat(ban);
                        break;
                    case CO_KHACH:
                        xuLyBanCoKhach(ban);
                        break;
                }
            }
        });

        String tooltipHtml = String.format(
            "<html><div style='padding:5px;'>" +
            "<b style='font-size:12px;'>%s</b><br>" +
            "<span style='color:#666;'>%s - %d chỗ</span><br>" +
            "<span style='color:%s; font-weight:bold;'>● %s</span>" +
            "</div></html>",
            ban.getMaBan() != null ? ban.getMaBan().trim() : "N/A",
            loaiBanTen,
            ban.getSoCho(),
            String.format("#%02x%02x%02x", mauChu.getRed(), mauChu.getGreen(), mauChu.getBlue()),
            trangThaiText
        );
        card.setToolTipText(tooltipHtml);

        return card;
    }

    /**
     * Xử lý khi nhấn vào bàn "Đã đặt" (ĐÃ SỬA THEO YÊU CẦU:
     * Chỉ chuyển trạng thái, không tạo hóa đơn)
     */
    private void xuLyBanDaDat(Ban ban) {
        if (ban == null || ban.getMaBan() == null) return;

        // 1. Xác nhận khách đã đến
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Xác nhận khách đã đến bàn " + ban.getMaBan().trim() + "?\n" +
            "Bàn sẽ chuyển sang trạng thái 'Có khách'.", // Đã cập nhật thông báo
            "Xác nhận khách đến",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (choice != JOptionPane.YES_OPTION) {
            return; // Người dùng nhấn No hoặc Hủy
        }

        // 2. Lấy thông tin phiếu đặt (vẫn cần để mở dialog gọi món)
        PhieuDatBan phieu = datBanDAO.getPhieuByBan(ban.getMaBan().trim());
        if (phieu == null) {
            JOptionPane.showMessageDialog(this,
                "Lỗi: Không tìm thấy phiếu đặt bàn tương ứng!",
                "Lỗi dữ liệu", JOptionPane.ERROR_MESSAGE);
            refreshData(); // Làm mới để đồng bộ
            return;
        }

        // BỎ QUA BƯỚC 3: TẠO HÓA ĐƠN MỚI (THEO YÊU CẦU)

        try {
            // 4. Cập nhật trạng thái bàn
            ban.setTrangThai(TrangThaiBan.CO_KHACH);
            
            // Dùng Ban_DAO để cập nhật trạng thái
            if (!banDAO.capNhatBan(ban)) { // Giả định bạn có hàm banDAO.capNhatBan(ban)
                 throw new Exception("Không thể cập nhật trạng thái bàn lên CSDL.");
            }

            // 5. Làm mới giao diện
            refreshData();
            
            // 6. Hỏi xem có muốn gọi món ngay không
            int orderChoice = JOptionPane.showConfirmDialog(
                this,
                "Bàn " + ban.getMaBan().trim() + " đã chuyển sang 'Có khách'.\n" +
                "Bạn có muốn chuyển đến trang gọi món không?", // Đã cập nhật thông báo
                "Nhận bàn thành công",
                JOptionPane.YES_NO_OPTION
            );
            
            if (orderChoice == JOptionPane.YES_OPTION) {
                // Mở dialog chọn món
                moDialogChonMon(phieu);
            }
            
           
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Đã xảy ra lỗi trong quá trình nhận bàn: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            refreshData();
        }
    }
    private void xuLyBanCoKhach(Ban ban) {
        if (ban == null || ban.getMaBan() == null) return;

        // Tùy chọn: Xem chi tiết món hoặc thanh toán
        String[] options = {"Xem chi tiết món", "Thanh toán", "Hủy"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Bàn " + ban.getMaBan() + " đang có khách. Bạn muốn?",
            "Bàn có khách",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == 0) {
            hienThiChiTietMonAn(ban);
        } else if (choice == 1) {
            // Đây là nơi gọi đến dialog thanh toán
            moDialogThanhToan(ban);
        }
    }

    private void hienThiChiTietMonAn(Ban ban) {
        PhieuDatBan phieu = datBanDAO.getPhieuByBan(ban.getMaBan().trim());
        
        if (phieu == null) {
            JOptionPane.showMessageDialog(this,
                "Không tìm thấy thông tin đặt bàn.",
                "Lỗi",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        
        Runnable closeCallback = this::refreshData;
        
        ChiTietPhieuDatBan_View detailView = new ChiTietPhieuDatBan_View(ban, closeCallback);
        
        Container parentContainer = getParent();
        if (parentContainer instanceof JComponent && parentContainer.getLayout() instanceof CardLayout) {
            parentContainer.add(detailView, "CHI_TIET_MON_AN_VIEW");
            CardLayout cl = (CardLayout)(parentContainer.getLayout());
            cl.show(parentContainer, "CHI_TIET_MON_AN_VIEW");
        } else if (parentFrame != null) {
            parentFrame.getContentPane().removeAll();
            parentFrame.getContentPane().add(detailView, BorderLayout.CENTER);
            parentFrame.revalidate();
            parentFrame.repaint();
        }
    }

    private void moDialogThanhToan(Ban ban) {
        if (ban == null || ban.getMaBan() == null) {
            JOptionPane.showMessageDialog(this, "Thông tin bàn không hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy cửa sổ chính.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            return;
        }

        HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
        HoaDon_Thue_DAO thueDAO = new HoaDon_Thue_DAO();
        HoaDon_KhuyenMai_DAO khuyenMaiDAO = new HoaDon_KhuyenMai_DAO();

        try {
            HoaDon hoaDonHienTai = hoaDonDAO.getHoaDonByBanChuaThanhToan(ban.getMaBan());

            if (hoaDonHienTai == null) {
                 JOptionPane.showMessageDialog(this,
                    "Không tìm thấy hóa đơn chưa thanh toán cho bàn " + ban.getMaBan(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                 return;
            }

            KhachHang_DAO khachHangDAO = new KhachHang_DAO();
            KhachHang kh = null;
            if (hoaDonHienTai.getKhachHang() != null && hoaDonHienTai.getKhachHang().getMaKhachHang() != null) {
                 kh = khachHangDAO.getKhachHangById(hoaDonHienTai.getKhachHang().getMaKhachHang());
            }
             hoaDonHienTai.setKhachHang(kh);


            List<ChiTietHoaDon> chiTietList = hoaDonDAO.getChiTietHoaDonForPrint(hoaDonHienTai.getMaHoaDon());
            if (chiTietList == null) chiTietList = new ArrayList<>();

            double tongTienMonAn = 0;
            for (ChiTietHoaDon ct : chiTietList) {
                tongTienMonAn += ct.tinhThanhTien();
            }

            Thue thue = (hoaDonHienTai.getThue() != null && hoaDonHienTai.getThue().getMaThue() != null)
                        ? thueDAO.getThueById(hoaDonHienTai.getThue().getMaThue()) : null;
            KhuyenMai km = (hoaDonHienTai.getKhuyenMai() != null && hoaDonHienTai.getKhuyenMai().getMaKM() != null)
                        ? khuyenMaiDAO.getKhuyenMaiById(hoaDonHienTai.getKhuyenMai().getMaKM()) : null;

            double tienGiam = 0, tienThue = 0, tongTienSauGiam = tongTienMonAn;

            if (km != null && !"KM00000000".equals(km.getMaKM().trim()) && km.getGiaTri() > 0) {
                if (km.getGiaTri() < 1.0) tienGiam = tongTienMonAn * km.getGiaTri();
                else tienGiam = km.getGiaTri();
                tongTienSauGiam -= tienGiam;
                if (tongTienSauGiam < 0) tongTienSauGiam = 0;
            }
            if (thue != null && thue.getTyLeThue() > 0) {
                tienThue = tongTienSauGiam * thue.getTyLeThue();
            }
            double tongThanhToan = tongTienSauGiam + tienThue;

            HoaDon_ThanhToan_Dialog thanhToanDialog = new HoaDon_ThanhToan_Dialog(
                parentFrame, hoaDonHienTai, hoaDonDAO,
                tongTienMonAn, tienGiam, tienThue, tongThanhToan,
                chiTietList
            );
            thanhToanDialog.setVisible(true);

            HoaDon hoaDonSauKhiDongDialog = hoaDonDAO.findByMaHD(hoaDonHienTai.getMaHoaDon());
            if (hoaDonSauKhiDongDialog != null && hoaDonSauKhiDongDialog.isDaThanhToan()) {
                boolean updatedStatus = datBanDAO.updateTableStatus(ban.getMaBan(), TrangThaiBan.TRONG);
                if(updatedStatus){
                     System.out.println("Đã cập nhật bàn " + ban.getMaBan() + " về TRỐNG sau thanh toán.");
                } else {
                     System.err.println("Lỗi: Không cập nhật được trạng thái bàn " + ban.getMaBan() + " về TRỐNG.");
                }
                refreshData();
            } else {
                 System.out.println("Hóa đơn " + hoaDonHienTai.getMaHoaDon() + " chưa được thanh toán (dialog hủy).");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Không thể mở màn hình thanh toán: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
 

    private void moDialogDatBan(Ban ban) {
        if (ban == null) return;
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame == null) return;

        Runnable refreshCallback = this::refreshData;

        DatBan_Dialog dialog = new DatBan_Dialog(parentFrame, ban, refreshCallback);
        dialog.setVisible(true);
    }

    // Inner class: RoundedPanel
    private class RoundedPanel extends JPanel {
        private int cornerRadius = 12;
        private Color borderColor = MAU_VIEN;
        private int borderThickness = 2;

        public RoundedPanel(LayoutManager layout, int radius) {
            super(layout);
            this.cornerRadius = radius;
            setOpaque(false);
        }

        public void setBorderColor(Color color) {
            this.borderColor = (color != null) ? color : MAU_VIEN;
            repaint();
        }

        public void setBorderThickness(int thickness) {
            this.borderThickness = Math.max(1, thickness);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (!isOpaque() && getBackground().getAlpha() < 255) {
                super.paintComponent(g);
            }
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(),
                cornerRadius, cornerRadius));

            if (borderThickness > 0) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(borderThickness));
                int offset = borderThickness / 2;
                g2.draw(new RoundRectangle2D.Double(offset, offset,
                    getWidth() - borderThickness, getHeight() - borderThickness,
                    cornerRadius, cornerRadius));
            }
            g2.dispose();
            if (isOpaque() || getBackground().getAlpha() == 255) {
                super.paintComponent(g);
            }
        }
    }

    // Inner class: WrappingFlowPanel
    private class WrappingFlowPanel extends JPanel implements Scrollable {
        private FlowLayout layout;

        public WrappingFlowPanel() {
            layout = new FlowLayout(FlowLayout.LEFT, 15, 15);
            setLayout(layout);
            setOpaque(false);
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            Dimension size = getPreferredSize();
            Container parent = getParent();
            if (parent instanceof JViewport) {
                size.width = ((JViewport) parent).getWidth();
            }
            return size;
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return (orientation == SwingConstants.VERTICAL) ? visibleRect.height : visibleRect.width;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            Container parent = getParent();
            if (parent instanceof JViewport) {
                return (((JViewport)parent).getWidth() > getPreferredSize().width);
            }
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }
    private void moDialogChonMon(PhieuDatBan phieuDatBan) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (parentFrame == null) return;

        // Tạo và hiển thị dialog chọn món
        view.ThucDon.ChonMon_Dialog chonMonDialog = 
            new view.ThucDon.ChonMon_Dialog(parentFrame, phieuDatBan);
        
        chonMonDialog.setVisible(true);
        
        // Sau khi dialog chọn món đóng, không cần làm gì
        // (Dialog đó tự xử lý việc thêm món)
    }
}