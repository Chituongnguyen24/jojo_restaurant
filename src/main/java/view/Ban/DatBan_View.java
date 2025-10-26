package view.Ban;

import dao.Ban_DAO;
import dao.DatBan_DAO;
import entity.Ban;
import entity.PhieuDatBan;
import enums.TrangThaiBan;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

/**
 * DatBan_View - giao diện quản lý đặt bàn
 *
 * Thay đổi chính trong phiên bản này:
 * - Khi click vào bàn:
 *   - Nếu trạng thái = TRONG  => mở DatBan_Dialog để tạo đặt bàn mới.
 *   - Nếu trạng thái = DA_DAT => lấy PhieuDatBan từ DAO và mở ChiTietPhieu_Dialog (show details) KÈM nút "Chỉnh sửa" để mở form sửa.
 *   - Nếu trạng thái = CO_KHACH => cố lấy PhieuDatBan; nếu có thì mở ChiTietPhieu_Dialog, nếu không có thì chỉ thông báo (không mở form đặt).
 *
 * LƯU Ý về dialog:
 * - DatBan_Dialog phải hỗ trợ 2 chế độ: tạo mới (như trước) và chỉnh sửa (nếu được truyền PhieuDatBan).
 *   Nếu DatBan_Dialog hiện tại của bạn chỉ có constructor (JFrame, Ban, Runnable), bạn nên bổ sung thêm constructor:
 *     DatBan_Dialog(JFrame owner, Ban ban, PhieuDatBan phieu, Runnable onRefresh)
 *   hoặc DatBan_Dialog tự load PhieuDatBan từ DAO khi phát hiện bàn đã có phiếu.
 *
 * - ChiTietPhieu_Dialog nên có nút "Chỉnh sửa" hoặc bạn có thể chỉnh sửa method moDialogChiTiet(...) để truyền onRefresh và mở DatBan_Dialog khi người dùng nhấn "Chỉnh sửa".
 */
public class DatBan_View extends JPanel {

    private final Ban_DAO banDAO = new Ban_DAO();
    private final DatBan_DAO datBanDAO = new DatBan_DAO();

    private Map<String, List<Ban>> danhSachBanTheoKhuVuc = new LinkedHashMap<>();
    private Map<String, Integer> soLuongBanTheoKhuVuc = new LinkedHashMap<>();
    private List<String> tenKhuVuc = new ArrayList<>();
    private String khuVucHienTai;

    private final Map<String, String> areaImagePaths = new LinkedHashMap<String, String>() {{
        put("Sân thượng", "images/icon/bannho.png");
        put("Sân vườn", "images/icon/thongthuong.png");
        put("Tầng 2", "images/icon/bannho.png");
        put("Tầng trệt", "images/icon/thongthuong.png");
        put("Phòng VIP", "images/icon/vip.png");
    }};
    private static final String DEFAULT_TABLE_IMAGE = "images/icon/bansanthuong.png";
    private final Map<String, ImageIcon> areaIcons = new LinkedHashMap<>();

    private static final Color MAU_NEN = new Color(254, 252, 247);
    private static final Color MAU_TRANG = Color.WHITE;
    private static final Color MAU_CHU_CHINH = new Color(52, 58, 64);
    private static final Color MAU_CHU_PHU = new Color(108, 117, 125);
    private static final Color MAU_VIEN = new Color(233, 236, 239);

    private static final Color MAU_CAM_CHINH = new Color(242, 118, 29);
    private static final Color MAU_CAM_DAM = new Color(166, 70, 16);

    private static final Color MAU_NUT_THEM = new Color(10, 102, 255);
    private static final Color MAU_NUT_THEM_HOVER = new Color(0, 84, 215);

    private static final Color MAU_TRONG = new Color(40, 167, 69);
    private static final Color MAU_DAT = new Color(255, 248, 225);
    private static final Color MAU_COKHACH = new Color(255, 235, 238);
    private static final Color MAU_XANH_DUONG = new Color(0, 123, 255);

    private static final Font FONT_TIEUDE_LON = new Font("Segoe UI", Font.BOLD, 32);
    private static final Font FONT_TIEUDE_PHU = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_TIEUDE_NHO = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_NUT = new Font("Segoe UI", Font.PLAIN, 15);
    private static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_THE_TIEUDE = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_THE_CHU = new Font("Segoe UI", Font.PLAIN, 12);

    private JPanel pnlLuoiBan;
    private JPanel pnlChuaNutKhuVuc;
    private JLabel lblTieuDeSoDo;
    private final Map<String, JButton> cacNutChonKhuVuc = new LinkedHashMap<>();

    private JPanel pnlDanhSachDatBan;
    private JTable tblDanhSachDatBan;
    private DefaultTableModel modelDanhSachDatBan;
    private TableRowSorter<DefaultTableModel> boLocSapXep;
    private JTextField txtTimSDT;
    private JSpinner spnDateFrom;
    private JSpinner spnDateTo;

    public DatBan_View() {
        taiTatCaIconKhuVuc();
        thietLapGiaoDien();
        taiLaiDuLieuVaLamMoiUI();
    }

    private void taiTatCaIconKhuVuc() {
        for (Map.Entry<String, String> entry : areaImagePaths.entrySet()) {
            ImageIcon icon = taiImageIcon(entry.getValue());
            if (icon != null) areaIcons.put(entry.getKey(), icon);
        }
        ImageIcon defaultIcon = taiImageIcon(DEFAULT_TABLE_IMAGE);
        if (defaultIcon != null) areaIcons.put("DEFAULT", defaultIcon);
    }

    private ImageIcon taiImageIcon(String imagePath) {
        try {
            URL res = getClass().getResource("/" + imagePath);
            if (res != null) {
                BufferedImage img = ImageIO.read(res);
                Image s = img.getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                return new ImageIcon(s);
            } else {
                ImageIcon o = new ImageIcon(imagePath);
                if (o.getIconWidth() <= 0) return null;
                Image s = o.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                return new ImageIcon(s);
            }
        } catch (IOException e) {
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private ImageIcon layIconChoKhuVucHienTai() {
        ImageIcon icon = areaIcons.get(khuVucHienTai);
        return icon != null ? icon : areaIcons.get("DEFAULT");
    }

    private void taiLaiDuLieuVaLamMoiUI() {
        taiDuLieuTuDB();
        capNhatNoiDungSidebar();
        capNhatLuaChonSidebar();
        capNhatHienThiLuoiBan();
        capNhatTieuDeSoDo();
        capNhatBangDatBan();
    }

    private void taiDuLieuTuDB() {
        Map<String, String> kvMap = banDAO.getDanhSachKhuVuc();
        tenKhuVuc = new ArrayList<>(kvMap.values());
        soLuongBanTheoKhuVuc = banDAO.getSoBanTheoKhuVuc();
        if (khuVucHienTai == null && !tenKhuVuc.isEmpty()) khuVucHienTai = tenKhuVuc.get(0);
        else if (tenKhuVuc.isEmpty()) khuVucHienTai = "Không có dữ liệu";
        danhSachBanTheoKhuVuc.clear();
        for (Map.Entry<String, String> entry : kvMap.entrySet()) {
            String maKV = entry.getKey();
            String tenKV = entry.getValue();
            List<Ban> ds = banDAO.getBanTheoKhuVuc(maKV);
            danhSachBanTheoKhuVuc.put(tenKV, ds);
        }
    }

    private void thietLapGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(MAU_NEN);
        JPanel mainPanel = new JPanel(new BorderLayout(25, 25));
        mainPanel.setBackground(MAU_NEN);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(35, 45, 35, 45));
        mainPanel.add(taoPanelTieuDe(), BorderLayout.NORTH);
        JPanel contentPanel = new JPanel(new BorderLayout(25, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(taoSidebarChonKhuVuc(), BorderLayout.WEST);
        contentPanel.add(taoPanelPhai(), BorderLayout.CENTER);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel taoPanelTieuDe() {
        TheBoTron panel = new TheBoTron(new BorderLayout(), 20);
        panel.setBackground(MAU_TRANG);
        panel.setBorder(BorderFactory.createCompoundBorder(new VienDoBong(), BorderFactory.createEmptyBorder(30, 35, 30, 35)));
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Quản lý đặt bàn");
        titleLabel.setFont(FONT_TIEUDE_LON);
        titleLabel.setForeground(MAU_CHU_CHINH);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subtitleLabel = new JLabel("Chọn bàn để đặt, xem chi tiết hoặc chỉnh sửa");
        subtitleLabel.setFont(FONT_TIEUDE_PHU);
        subtitleLabel.setForeground(MAU_CHU_PHU);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);
        panel.add(titlePanel, BorderLayout.WEST);
        return panel;
    }

    private JPanel taoSidebarChonKhuVuc() {
        TheBoTron sidebar = new TheBoTron(null, 20);
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(MAU_TRANG);
        sidebar.setBorder(BorderFactory.createCompoundBorder(new VienDoBong(), BorderFactory.createEmptyBorder(30, 25, 30, 25)));
        sidebar.setPreferredSize(new Dimension(300, 0));
        JLabel areaLabel = new JLabel("Khu vực");
        areaLabel.setFont(FONT_TIEUDE_NHO);
        areaLabel.setForeground(MAU_CHU_CHINH);
        areaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel areaSubLabel = new JLabel("Chọn khu vực để xem sơ đồ bàn");
        areaSubLabel.setFont(FONT_CHU);
        areaSubLabel.setForeground(MAU_CHU_PHU);
        areaSubLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(areaLabel);
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(areaSubLabel);
        sidebar.add(Box.createVerticalStrut(25));
        pnlChuaNutKhuVuc = new JPanel();
        pnlChuaNutKhuVuc.setLayout(new BoxLayout(pnlChuaNutKhuVuc, BoxLayout.Y_AXIS));
        pnlChuaNutKhuVuc.setOpaque(false);
        sidebar.add(pnlChuaNutKhuVuc);
        sidebar.add(Box.createVerticalGlue());
        return sidebar;
    }

    private void capNhatNoiDungSidebar() {
        pnlChuaNutKhuVuc.removeAll();
        cacNutChonKhuVuc.clear();
        for (String tenKV : tenKhuVuc) {
            int count = soLuongBanTheoKhuVuc.getOrDefault(tenKV, 0);
            JButton nutKV = taoNutChonKhuVuc(tenKV, count + " bàn");
            nutKV.addActionListener(e -> chuyenKhuVuc(tenKV));
            cacNutChonKhuVuc.put(tenKV, nutKV);
            pnlChuaNutKhuVuc.add(nutKV);
            pnlChuaNutKhuVuc.add(Box.createVerticalStrut(12));
        }
        pnlChuaNutKhuVuc.revalidate();
        pnlChuaNutKhuVuc.repaint();
    }

    private void chuyenKhuVuc(String tenKV) {
        khuVucHienTai = tenKV;
        capNhatLuaChonSidebar();
        capNhatHienThiLuoiBan();
        capNhatTieuDeSoDo();
    }

    private void capNhatLuaChonSidebar() {
        for (Map.Entry<String, JButton> entry : cacNutChonKhuVuc.entrySet()) {
            dinhDangNutKhuVuc(entry.getValue(), entry.getKey().equals(khuVucHienTai));
        }
    }

    private JButton taoNutChonKhuVuc(String tenKV, String soLuong) {
        NutBoTron button = new NutBoTron("", 15);
        button.setLayout(new BorderLayout(15, 0));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        button.setPreferredSize(new Dimension(250, 56));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        JLabel nameLabel = new JLabel(tenKV);
        nameLabel.setFont(FONT_NUT);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        JLabel countLabel = new JLabel(soLuong);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        countLabel.setOpaque(false);
        countLabel.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel countWrapper = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        countWrapper.setOpaque(false);
        countWrapper.add(countLabel, BorderLayout.CENTER);
        button.add(nameLabel, BorderLayout.WEST);
        button.add(countWrapper, BorderLayout.EAST);
        dinhDangNutKhuVuc(button, false);
        return button;
    }

    private void dinhDangNutKhuVuc(JButton button, boolean selected) {
        if (button.getComponentCount() < 2) return;
        JLabel nameLabel = (JLabel) button.getComponent(0);
        JPanel countWrapper = (JPanel) button.getComponent(1);
        JLabel countLabel = (JLabel) countWrapper.getComponent(0);
        if (selected) {
            button.setBackground(MAU_CAM_CHINH);
            nameLabel.setForeground(Color.WHITE);
            countLabel.setForeground(Color.WHITE);
            countWrapper.setBackground(MAU_CAM_DAM);
            button.setBorder(BorderFactory.createLineBorder(MAU_CAM_CHINH, 1));
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        } else {
            button.setBackground(new Color(248, 249, 250));
            nameLabel.setForeground(MAU_CHU_CHINH);
            countLabel.setForeground(MAU_CHU_CHINH);
            countWrapper.setBackground(new Color(222, 226, 230));
            button.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 1));
            nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));
        }
    }

    private JPanel taoPanelPhai() {
        JPanel panel = new JPanel(new BorderLayout(0, 25));
        panel.setOpaque(false);

        TheBoTron headerPanel = new TheBoTron(new BorderLayout(15, 0), 20);
        headerPanel.setBackground(MAU_TRANG);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(new VienDoBong(), BorderFactory.createEmptyBorder(25, 30, 25, 30)));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        lblTieuDeSoDo = new JLabel("Sơ đồ bàn");
        lblTieuDeSoDo.setFont(FONT_TIEUDE_NHO);
        lblTieuDeSoDo.setForeground(MAU_CHU_CHINH);
        lblTieuDeSoDo.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel mapSubtitle = new JLabel("Nhấn vào bàn để đặt hoặc xem chi tiết.");
        mapSubtitle.setFont(FONT_CHU);
        mapSubtitle.setForeground(MAU_CHU_PHU);
        mapSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(lblTieuDeSoDo);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(mapSubtitle);

        NutBoTron btnThemBan = new NutBoTron("+ Thêm Bàn Mới", 12);
        btnThemBan.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnThemBan.setBackground(MAU_NUT_THEM);
        btnThemBan.setForeground(MAU_TRANG);
        btnThemBan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnThemBan.setPreferredSize(new Dimension(180, 45));
        btnThemBan.addActionListener(e -> moDialogThemBan());
        btnThemBan.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnThemBan.setBackground(MAU_NUT_THEM_HOVER); }
            @Override public void mouseExited(MouseEvent e) { btnThemBan.setBackground(MAU_NUT_THEM); }
        });

        JPanel buttonWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonWrapper.setOpaque(false);
        buttonWrapper.add(btnThemBan);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        headerPanel.add(buttonWrapper, BorderLayout.EAST);

        pnlLuoiBan = new JPanel(new GridLayout(0, 4, 20, 20));
        pnlLuoiBan.setOpaque(false);

        TheBoTron tablesContainer = new TheBoTron(new BorderLayout(), 20);
        tablesContainer.setBackground(MAU_TRANG);
        tablesContainer.setBorder(BorderFactory.createCompoundBorder(new VienDoBong(), BorderFactory.createEmptyBorder(30, 30, 30, 30)));
        tablesContainer.add(pnlLuoiBan, BorderLayout.NORTH);

        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tablesContainer, BorderLayout.CENTER);

        return panel;
    }

    private void moDialogThemBan() {
        ThemBan_Dialog dialog = new ThemBan_Dialog((JFrame) SwingUtilities.getWindowAncestor(this), this::taiLaiDuLieuVaLamMoiUI);
        dialog.setVisible(true);
    }

    /**
     * Mở dialog đặt bàn
     * - Nếu đã có phiếu (bị gọi cho bàn đã đặt) thì DatBan_Dialog nên hỗ trợ sửa (nếu DatBan_Dialog có constructor hỗ trợ).
     * - Nếu DatBan_Dialog không hỗ trợ sửa, DatBan_Dialog có thể tự load phieu via datBanDAO.getPhieuByBan(ban.getMaBan()) và chuyển sang chế độ sửa.
     */
    private void moDialogDatBan(Ban ban) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        // If you implemented DatBan_Dialog edit constructor, prefer:
        // PhieuDatBan pdb = datBanDAO.getPhieuByBan(ban.getMaBan());
        // DatBan_Dialog dialog = new DatBan_Dialog(parent, ban, pdb, this::taiLaiDuLieuVaLamMoiUI);
        // Otherwise use simple constructor — DatBan_Dialog should detect existing phieu and prefill if needed.
        DatBan_Dialog dialog = new DatBan_Dialog(parent, ban, this::taiLaiDuLieuVaLamMoiUI);
        dialog.setVisible(true);
    }

    /**
     * Mở dialog hiển thị chi tiết phiếu; truyền onRefresh để dialog có thể gọi lại khi hủy hoặc chỉnh sửa thành công.
     * ChiTietPhieu_Dialog cần hỗ trợ nhận Runnable onRefresh trong constructor.
     */
    private void moDialogChiTiet(PhieuDatBan pdb) {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
        ChiTietPhieu_Dialog d = new ChiTietPhieu_Dialog(parent, pdb, this::taiLaiDuLieuVaLamMoiUI);
        d.setVisible(true);
    }

    private void capNhatTieuDeSoDo() {
        if (lblTieuDeSoDo != null) lblTieuDeSoDo.setText("Sơ đồ bàn - " + khuVucHienTai);
    }

    private void capNhatHienThiLuoiBan() {
        pnlLuoiBan.removeAll();
        pnlLuoiBan.setLayout(new GridLayout(0, 4, 20, 20));
        List<Ban> tables = danhSachBanTheoKhuVuc.get(khuVucHienTai);
        if (tables != null && !tables.isEmpty()) {
            for (Ban b : tables) pnlLuoiBan.add(taoTheBan(b));
        } else {
            JLabel nTL = new JLabel("Không có bàn nào trong khu vực này.");
            nTL.setFont(FONT_CHU);
            nTL.setForeground(MAU_CHU_PHU);
            nTL.setHorizontalAlignment(SwingConstants.CENTER);
            pnlLuoiBan.setLayout(new FlowLayout());
            pnlLuoiBan.add(nTL);
        }
        pnlLuoiBan.revalidate();
        pnlLuoiBan.repaint();
    }

    private JPanel taoTheBan(Ban ban) {
        TheBoTron card = new TheBoTron(null, 18);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MAU_TRANG);
        card.setBorder(BorderFactory.createEmptyBorder(20, 18, 20, 18));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        ImageIcon currentIcon = layIconChoKhuVucHienTai();
        JLabel lblIcon;
        if (currentIcon != null) lblIcon = new JLabel(currentIcon);
        else { lblIcon = new JLabel("[Bàn]"); lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 16)); lblIcon.setForeground(MAU_CHU_PHU); }
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        String loaiBanTen = ban.getLoaiBan() != null ? ban.getLoaiBan().getTenHienThi() : "";
        if (loaiBanTen.equalsIgnoreCase("Bàn VIP")) loaiBanTen = "VIP";
        JLabel lblName = new JLabel(String.format("%s (%s)", ban.getMaBan().trim(), loaiBanTen));
        lblName.setFont(FONT_THE_TIEUDE);
        lblName.setForeground(MAU_CHU_CHINH);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCapacity = new JLabel(ban.getSoCho() + " chỗ");
        lblCapacity.setFont(FONT_THE_CHU);
        lblCapacity.setForeground(MAU_CHU_PHU);
        lblCapacity.setAlignmentX(Component.CENTER_ALIGNMENT);

        Color badgeBg;
        Color badgeFg = MAU_CHU_CHINH;
        String statusText;
        if (ban.getTrangThai() == TrangThaiBan.TRONG) {
            badgeBg = new Color(232, 245, 233);
            statusText = "Trống";
            badgeFg = MAU_TRONG.darker();
        } else if (ban.getTrangThai() == TrangThaiBan.DA_DAT) {
            badgeBg = MAU_DAT;
            statusText = "Đã đặt";
            badgeFg = MAU_CAM_DAM;
        } else {
            badgeBg = MAU_COKHACH;
            statusText = "Có khách";
            badgeFg = MAU_COKHACH.darker();
        }
        JPanel badge = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        badge.setBackground(badgeBg);
        badge.setMaximumSize(new Dimension(140, 30));
        badge.setBorder(new EmptyBorder(6, 10, 6, 10));
        JLabel lblStatus = new JLabel(statusText);
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatus.setForeground(badgeFg);
        badge.add(lblStatus);
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblIcon);
        card.add(Box.createVerticalStrut(12));
        card.add(lblName);
        card.add(Box.createVerticalStrut(6));
        card.add(lblCapacity);
        card.add(Box.createVerticalStrut(12));
        card.add(badge);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (!SwingUtilities.isLeftMouseButton(e)) return;

                TrangThaiBan tt = ban.getTrangThai();
                try {
                    if (tt == TrangThaiBan.TRONG) {
                        // Bàn trống -> mở đặt bàn
                        moDialogDatBan(ban);
                        return;
                    }

                    // Với DA_DAT hoặc CO_KHACH -> cố lấy phiếu
                    PhieuDatBan pdb = datBanDAO.getPhieuByBan(ban.getMaBan());
                    if (pdb != null) {
                        // có phiếu -> mở dialog chi tiết (với edit/hủy tuỳ trạng thái)
                        moDialogChiTiet(pdb);
                        return;
                    }

                    // Không tìm thấy phiếu:
                    // Thay vì báo lỗi, hiển thị info "Khách vãng lai" — tạo PhieuDatBan tạm để ChiTietPhieu_Dialog hiển thị
                    PhieuDatBan temp = new PhieuDatBan();
                    temp.setMaPhieu("N/A");
                    temp.setBan(ban);
                    temp.setKhachHang(null); // null -> ChiTietPhieu_Dialog sẽ hiển thị "Khách vãng lai"
                    temp.setSoNguoi(ban.getSoCho());
                    // Không set thoiGian/tien cọc/ghi chu; ChiTietPhieu_Dialog sẽ hiển thị N/A hoặc trống

                    moDialogChiTiet(temp);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(DatBan_View.this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
            @Override public void mouseEntered(MouseEvent e) { card.setBackground(new Color(250, 251, 252)); }
            @Override public void mouseExited(MouseEvent e) { card.setBackground(MAU_TRANG); }
        });

        return card;
    }

    private void capNhatBangDatBan() {
        if (modelDanhSachDatBan == null) return;
        modelDanhSachDatBan.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        List<PhieuDatBan> ds = datBanDAO.getAllPhieuDatBan();
        for (PhieuDatBan p : ds) {
            String time = p.getThoiGianDat() != null ? sdf.format(java.sql.Timestamp.valueOf(p.getThoiGianDat())) : "N/A";
            String banMa = p.getBan() != null ? p.getBan().getMaBan() : "N/A";
            String sdt = p.getKhachHang() != null ? p.getKhachHang().getSdt() : "N/A";
            modelDanhSachDatBan.addRow(new Object[]{p.getMaPhieu(), banMa, sdt, time, "Chi tiết"});
        }
    }

    // --- helper UI classes (rounded containers, shadows, pill buttons) ---

    class TheBoTron extends JPanel {
        private final int doBoGoc;
        private Color mauVien = MAU_VIEN;
        public TheBoTron(LayoutManager layout, int doBoGoc) { super(layout); this.doBoGoc = doBoGoc; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, doBoGoc, doBoGoc));
            g2.dispose();
            super.paintComponent(g);
        }
        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.mauVien);
            g2.setStroke(new BasicStroke(2));
            g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, doBoGoc, doBoGoc));
            g2.dispose();
        }
    }

    class VienDoBong implements Border {
        private final int shadowSize = 4;
        @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < shadowSize; i++) {
                int alpha = (int) (15 - (i * 3.5));
                g2.setColor(new Color(0, 0, 0, alpha));
                g2.drawRoundRect(x + i, y + i, width - 1 - (i * 2), height - 1 - (i * 2), 20, 20);
            }
            g2.dispose();
        }
        @Override public Insets getBorderInsets(Component c) { return new Insets(shadowSize, shadowSize, shadowSize, shadowSize); }
        @Override public boolean isBorderOpaque() { return false; }
    }

    class NutBoTron extends JButton {
        private final int doBoGoc;
        public NutBoTron(String text, int doBoGoc) { super(text); this.doBoGoc = doBoGoc; setContentAreaFilled(false); setFocusPainted(false); setBorderPainted(false); setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24)); setOpaque(false); setHorizontalAlignment(SwingConstants.CENTER); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color bg = getBackground();
            if (getModel().isPressed()) g2.setColor(bg.darker());
            else if (getModel().isRollover()) g2.setColor(bg.brighter());
            else g2.setColor(bg);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), doBoGoc, doBoGoc));
            super.paintComponent(g);
            g2.dispose();
        }
    }
}