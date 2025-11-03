package view.Ban;

import dao.Ban_DAO;
import dao.HoaDon_DAO;
import dao.PhieuDatBan_DAO;
import entity.Ban;
import entity.HoaDon;
import entity.PhieuDatBan;
import enums.TrangThaiBan;
import view.ThucDon.ChonMon_Dialog;
import enums.LoaiBan;

// THÊM IMPORTS MỚI
import dao.KhachHang_DAO;
import dao.NhanVien_DAO;
import entity.KhachHang;
import entity.NhanVien;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.awt.geom.RoundRectangle2D;
import java.util.Calendar;
import java.util.concurrent.ExecutionException; // SỬA: Thêm import

import com.toedter.calendar.JDateChooser;

import javax.swing.Timer;

public class DatBan_View extends JPanel implements ActionListener {

    private final Ban_DAO banDAO;
    private final HoaDon_DAO hoaDonDAO;
    private final PhieuDatBan_DAO phieuDatBanDAO;

    // UI components
    private final JComboBox<Integer> cboSoKhach;
    private final JComboBox<String> cboFilterKhuVuc;
    private final JDateChooser datePicker;
    private final JSpinner spinnerGioDat;
    private final JTextField txtSearchPDB, txtTenKhach, txtSdtKhach, txtGhiChu;
    
    private final JButton btnSearchPDB, btnDatBan, btnRefresh, btnXemDanhSachPDB, btnThanhToan; //thêm nút thanh toán cho tiện
    
    private final DefaultTableModel modelPhieuDat;
    private final JTable tblPhieuDat;
    
    private final JPanel pnlLuoiBan;
    private final JLabel lblThongKeBan;
    private final JLabel lblDateTime;

    private final JLabel lblMaBanValue, lblKhuVucValue, lblLoaiBanValue, lblSoChoValue;

    // Data
    private Map<String, List<Ban>> danhSachBanTheoKhuVuc;
    private List<String> tenKhuVuc;
    private String khuVucHienTai;
    private List<PhieuDatBan> danhSachPhieuDatDangHoatDong;

    private Ban banDangChon = null;
    private PhieuDatBan phieuDangChon = null;
    private JPanel cardBanDangChon = null;

    // UI constants
    private static final Color BG_VIEW = new Color(251, 248, 241);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color MAU_VIEN = new Color(222, 226, 230);
    private static final Color COLOR_TITLE = new Color(30, 30, 30);
    private static final Color MAU_CAM_CHINH = new Color(255, 152, 0);
    private static final Color MAU_XANH_LA = new Color(76, 175, 80);
    private static final Color MAU_DO = new Color(244, 67, 54);
    private static final Color MAU_XANH_DUONG = new Color(34, 139, 230);
    private static final Color MAU_XAM_NHE = new Color(108, 117, 125);
    private static final Color MAU_HIGHLIGHT = new Color(100, 150, 255);
    private static final Font FONT_TIEUDE_LON = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_TIEUDE_CHINH = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_TIEUDE_NHO = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_CHU_NHO = new Font("Segoe UI", Font.PLAIN, 12);

    private final Timer clockTimer;

    public DatBan_View() {
        banDAO = new Ban_DAO();
		hoaDonDAO = new HoaDon_DAO();
        phieuDatBanDAO = new PhieuDatBan_DAO();
        danhSachBanTheoKhuVuc = new LinkedHashMap<>();
        tenKhuVuc = new ArrayList<>();
        danhSachPhieuDatDangHoatDong = new ArrayList<>();

        txtSearchPDB = new JTextField(20);
        txtSearchPDB.setToolTipText("Mã PDB, SĐT hoặc Tên khách");

        cboSoKhach = createStyledComboBox();
        for (int i : new Integer[]{1, 2, 3, 4, 5, 6, 8, 10, 12, 15}) cboSoKhach.addItem(i);

        cboFilterKhuVuc = createStyledComboBox();

        datePicker = new JDateChooser(new Date());
        datePicker.setDateFormatString("dd/MM/yyyy");

        SpinnerDateModel modelGio = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
        spinnerGioDat = new JSpinner(modelGio);
        JSpinner.DateEditor editorGio = new JSpinner.DateEditor(spinnerGioDat, "HH:mm");
        spinnerGioDat.setEditor(editorGio);

        txtTenKhach = createStyledTextField("Họ và tên khách hàng");
        txtSdtKhach = createStyledTextField("Số điện thoại");
        txtGhiChu = createStyledTextField("Ghi chú");

        btnSearchPDB = new RoundedButton("Tìm kiếm PDB", MAU_XANH_DUONG, COLOR_WHITE);
        btnSearchPDB.setPreferredSize(new Dimension(140, 38));

        btnDatBan = new RoundedButton("Đặt bàn", MAU_XANH_LA, COLOR_WHITE);
        btnDatBan.setPreferredSize(new Dimension(150, 40));
        
        btnThanhToan = new RoundedButton("Thanh toán", MAU_XANH_DUONG, COLOR_WHITE); // 
        btnThanhToan.setPreferredSize(new Dimension(150, 40));
        btnThanhToan.setVisible(false);
        
        btnRefresh = new RoundedButton("🔄 Làm mới", new Color(255, 243, 224), MAU_CAM_CHINH);
        btnRefresh.setPreferredSize(new Dimension(150, 35));

        btnXemDanhSachPDB = new RoundedButton("DS Phiếu Đặt", MAU_XAM_NHE, COLOR_WHITE);
        btnXemDanhSachPDB.setPreferredSize(new Dimension(150, 35));
        
        lblThongKeBan = new JLabel("Đang tải...");
        lblThongKeBan.setFont(FONT_CHU);
        lblThongKeBan.setForeground(MAU_XAM_NHE);

        lblDateTime = new JLabel();
        lblDateTime.setFont(FONT_CHU);
        lblDateTime.setForeground(MAU_XAM_NHE);

        lblMaBanValue = new JLabel("--");
        lblKhuVucValue = new JLabel("--");
        lblLoaiBanValue = new JLabel("--");
        lblSoChoValue = new JLabel("--");
        
        lblMaBanValue.setFont(FONT_CHU);
        lblKhuVucValue.setFont(FONT_CHU);
        lblLoaiBanValue.setFont(FONT_CHU);
        lblSoChoValue.setFont(FONT_CHU);

        modelPhieuDat = new DefaultTableModel(new String[]{"Mã PDB", "Giờ Hẹn", "Khách hàng", "Bàn", "Trạng Thái"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblPhieuDat = taoStyledTable(modelPhieuDat);
        tblPhieuDat.setRowHeight(30);
        
        pnlLuoiBan = new JPanel(new GridLayout(0, 4, 10, 10));
        pnlLuoiBan.setOpaque(false);
        pnlLuoiBan.setBackground(BG_VIEW);
        pnlLuoiBan.setBorder(new EmptyBorder(15, 15, 15, 15));

        clockTimer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm:ss");
            lblDateTime.setText(sdf.format(new Date()));
        });
        clockTimer.start();

        thietLapGiaoDien();
        ganSuKien(); 
        taiDuLieuVaHienThiBanDau(true);
    }

    private void thietLapGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(BG_VIEW);
        setBorder(new EmptyBorder(0,0,0,0));
        add(taoPanelHeader(), BorderLayout.NORTH);
        add(taoPanelNoiDungChinh(), BorderLayout.CENTER);
    }

    private JPanel taoPanelHeader() {
        JPanel panelHeaderWrapper = new JPanel(new BorderLayout(0, 15));
        panelHeaderWrapper.setBackground(BG_VIEW);
        panelHeaderWrapper.setBorder(new EmptyBorder(20, 30, 0, 30));

        JPanel pnlLeft = new JPanel();
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setOpaque(false);
        JLabel lblTitle = new JLabel("Quản lý đặt bàn");
        lblTitle.setFont(FONT_TIEUDE_LON);
        lblTitle.setForeground(COLOR_TITLE);
        pnlLeft.add(lblTitle);
        pnlLeft.add(Box.createVerticalStrut(5));
        pnlLeft.add(lblThongKeBan);
        pnlLeft.add(lblDateTime);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRight.setOpaque(false);
        pnlRight.add(btnXemDanhSachPDB); 
        pnlRight.add(btnRefresh);

        JPanel pnlTitleBar = new JPanel(new BorderLayout());
        pnlTitleBar.setBackground(BG_VIEW);
        pnlTitleBar.add(pnlLeft, BorderLayout.WEST);
        pnlTitleBar.add(pnlRight, BorderLayout.EAST);

        panelHeaderWrapper.add(pnlTitleBar, BorderLayout.NORTH);
        panelHeaderWrapper.add(taoPanelTimKiem(), BorderLayout.CENTER);
        return panelHeaderWrapper;
    }

    private JPanel taoPanelTimKiem() {
        JPanel pnlSearch = new RoundedPanel(15, COLOR_WHITE, new GridBagLayout());
        pnlSearch.setBorder(new EmptyBorder(12, 15, 12, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlSearch.add(new JLabel("Tìm kiếm PDB:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.3;
        txtSearchPDB.setPreferredSize(new Dimension(200, 38));
        styleComponent(txtSearchPDB);
        pnlSearch.add(txtSearchPDB, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        pnlSearch.add(btnSearchPDB, gbc);

        gbc.gridx = 3; gbc.gridy = 0; gbc.weightx = 0;
        pnlSearch.add(new JLabel("Số khách:"), gbc);
        
        gbc.gridx = 4;
        cboSoKhach.setPreferredSize(new Dimension(80, 38));
        styleComponent(cboSoKhach);
        pnlSearch.add(cboSoKhach, gbc);

        gbc.gridx = 5;
        pnlSearch.add(new JLabel("Khu vực:"), gbc);
        
        gbc.gridx = 6;
        cboFilterKhuVuc.setPreferredSize(new Dimension(150, 38));
        styleComponent(cboFilterKhuVuc);
        pnlSearch.add(cboFilterKhuVuc, gbc);

        gbc.gridx = 7;
        pnlSearch.add(new JLabel("Ngày đặt:"), gbc);
        
        gbc.gridx = 8;
        datePicker.setPreferredSize(new Dimension(130, 38));
        styleComponent(datePicker);
        pnlSearch.add(datePicker, gbc);

        gbc.gridx = 9;
        pnlSearch.add(new JLabel("Giờ đặt:"), gbc);
        
        gbc.gridx = 10;
        spinnerGioDat.setPreferredSize(new Dimension(80, 38));
        styleComponent((JComponent) spinnerGioDat);
        pnlSearch.add(spinnerGioDat, gbc);

        return pnlSearch;
    }

    private void styleComponent(JComponent comp) {
        comp.setFont(FONT_CHU);
        comp.setBorder(BorderFactory.createCompoundBorder(new LineBorder(MAU_VIEN, 1), new EmptyBorder(5, 10, 5, 10)));
        if (comp instanceof JComboBox) ((JComboBox<?>) comp).setBackground(COLOR_WHITE);
        if (comp instanceof JTextField) ((JTextField) comp).setBackground(COLOR_WHITE);
    }

    private JSplitPane taoPanelNoiDungChinh() {
        JPanel pnlListContainer = taoPanelDanhSachBan();
        JPanel pnlCRUD = taoPanelCRUDForm();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlListContainer, pnlCRUD);
        splitPane.setDividerLocation(950);
        splitPane.setResizeWeight(0.75);
        splitPane.setBorder(null);
        splitPane.setBackground(BG_VIEW);
        return splitPane;
    }

    private JPanel taoPanelDanhSachBan() {
        JPanel pnlWrapper = new JPanel(new BorderLayout());
        pnlWrapper.setOpaque(false);
        pnlWrapper.setBorder(new EmptyBorder(10, 30, 30, 10));
        JLabel lblMapTitle = new JLabel("Sơ đồ bàn - Chọn bàn để thao tác");
        lblMapTitle.setFont(FONT_TIEUDE_CHINH);
        lblMapTitle.setForeground(COLOR_TITLE);
        pnlWrapper.add(lblMapTitle, BorderLayout.NORTH);

        JPanel pnlFilterBan = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pnlFilterBan.setOpaque(false);
        pnlFilterBan.setBorder(new EmptyBorder(10, 0, 10, 0));
        pnlFilterBan.add(taoMucChuThich("Trống", MAU_XANH_LA));
        pnlFilterBan.add(taoMucChuThich("Đã đặt", MAU_CAM_CHINH));
        pnlFilterBan.add(taoMucChuThich("Có khách", MAU_DO));
        pnlWrapper.add(pnlFilterBan, BorderLayout.SOUTH);

        JPanel tableContentWrapper = new JPanel(new BorderLayout());
        tableContentWrapper.setOpaque(false);
        tableContentWrapper.add(pnlLuoiBan, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tableContentWrapper);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBackground(BG_VIEW);
        scroll.getViewport().setBackground(BG_VIEW);
        pnlWrapper.add(scroll, BorderLayout.CENTER);

        return pnlWrapper;
    }

    private JPanel taoPanelCRUDForm() {
        RoundedPanel pnlForm = new RoundedPanel(20, COLOR_WHITE, new BorderLayout(0, 10));
        pnlForm.setPreferredSize(new Dimension(380, 450));
        pnlForm.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblFormTitle = new JLabel("Đặt bàn");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(COLOR_TITLE);
        pnlForm.add(lblFormTitle, BorderLayout.NORTH);

        JPanel pnlBanInfo = new JPanel(new GridLayout(2, 4, 5, 5));
        pnlBanInfo.setOpaque(false);
        pnlBanInfo.setBorder(BorderFactory.createTitledBorder(new LineBorder(MAU_VIEN, 1), "Thông tin bàn đã chọn",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, FONT_TIEUDE_NHO, COLOR_TITLE));
        pnlBanInfo.add(taoFormLabel("Mã Bàn:"));
        pnlBanInfo.add(lblMaBanValue);
        pnlBanInfo.add(taoFormLabel("Khu Vực:"));
        pnlBanInfo.add(lblKhuVucValue);
        pnlBanInfo.add(taoFormLabel("Loại:"));
        pnlBanInfo.add(lblLoaiBanValue);
        pnlBanInfo.add(taoFormLabel("Số Chỗ:"));
        pnlBanInfo.add(lblSoChoValue);

        JPanel pnlCenter = new JPanel();
        pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS));
        pnlCenter.setOpaque(false);
        
        JPanel pnlInput = new JPanel(new GridLayout(3, 2, 8, 8));
        pnlInput.setOpaque(false);
        pnlInput.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(MAU_VIEN, 1), "Thông tin khách đặt bàn",
            javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, FONT_TIEUDE_NHO, COLOR_TITLE
        ));
        pnlInput.add(taoFormLabel("Họ tên khách:"));
        pnlInput.add(txtTenKhach);
        pnlInput.add(taoFormLabel("SĐT/Mã KH:"));
        pnlInput.add(txtSdtKhach);
        pnlInput.add(taoFormLabel("Ghi chú:"));
        pnlInput.add(txtGhiChu);
        
        pnlCenter.add(pnlBanInfo); 
        pnlCenter.add(Box.createVerticalStrut(10));
        pnlCenter.add(pnlInput); 
        pnlCenter.add(Box.createVerticalGlue()); 
        
        pnlForm.add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlButton.setOpaque(false);
        pnlButton.add(btnDatBan);
        pnlButton.add(btnThanhToan);
        
        pnlForm.add(pnlButton, BorderLayout.SOUTH);

        return pnlForm;
    }


    private JLabel taoFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_CHU_NHO); // Dùng font nhỏ
        label.setForeground(COLOR_TITLE);
        return label;
    }

    private JPanel taoMucChuThich(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        item.setOpaque(false);
        JPanel circle = new JPanel() {
            @Override public Dimension getPreferredSize() { return new Dimension(14, 14); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        JLabel label = new JLabel(text);
        label.setFont(FONT_CHU);
        label.setForeground(COLOR_TITLE);
        item.add(circle);
        item.add(label);
        return item;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setEditable(true);
        field.setFont(FONT_CHU);
        field.setBorder(BorderFactory.createCompoundBorder(new LineBorder(MAU_VIEN, 1), new EmptyBorder(5, 10, 5, 10)));
        field.setToolTipText(placeholder);
        return field;
    }

    private <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> cbo = new JComboBox<>();
        cbo.setFont(FONT_CHU);
        cbo.setBackground(COLOR_WHITE);
        cbo.setBorder(BorderFactory.createCompoundBorder(new LineBorder(MAU_VIEN, 1), new EmptyBorder(5, 5, 5, 5)));
        return cbo;
    }

    private JTable taoStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(FONT_CHU);
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setGridColor(MAU_VIEN);
        table.getTableHeader().setFont(FONT_TIEUDE_NHO);
        table.getTableHeader().setBackground(new Color(248, 249, 250));
        table.getTableHeader().setForeground(COLOR_TITLE);
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 40));
        return table;
    }
    
    private JPanel taoTheBan(Ban ban) {
        RoundedPanel card = new RoundedPanel(15, COLOR_WHITE, new BorderLayout(8, 0));
        card.setBorder(new EmptyBorder(6, 8, 6, 10));
        card.setPreferredSize(new Dimension(200, 70));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        TrangThaiBan ttb = TrangThaiBan.fromString(ban.getTrangThai());
        Color mauVien = ttb.getColor();
        card.setBorderColor(mauVien);

        JLabel lblIcon = createBanIconLabel(ban);
        card.add(lblIcon, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        String loaiBanRaw = ban.getLoaiBan();
        String loaiBanTen;
        try {
            loaiBanTen = LoaiBan.fromString(loaiBanRaw).getTenHienThi();
        } catch (Exception ex) {
            loaiBanTen = loaiBanRaw;
        }
        if ("Bàn VIP".equalsIgnoreCase(loaiBanTen)) loaiBanTen = "VIP";

        JLabel lblName = new JLabel(String.format("%s (%s)", ban.getMaBan().trim(), loaiBanTen));
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(COLOR_TITLE);

        JLabel lblCapacity = new JLabel(ban.getSoCho() + " chỗ");
        lblCapacity.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCapacity.setForeground(new Color(220, 0, 0));

        JLabel lblTrangThai = new JLabel(ttb.getTenHienThi());
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblTrangThai.setForeground(ttb.getColor());

        infoPanel.add(lblName);
        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(lblCapacity);
        infoPanel.add(Box.createVerticalStrut(2));
        infoPanel.add(lblTrangThai);
        card.add(infoPanel, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) xuLyChonBan(ban, card);
            }
            @Override public void mouseEntered(MouseEvent e) {
                if (card != cardBanDangChon) card.setBackground(new Color(245, 245, 245));
            }
            @Override public void mouseExited(MouseEvent e) {
                if (card != cardBanDangChon) card.setBackground(COLOR_WHITE);
            }
        });

        return card;
    }

    private JLabel createBanIconLabel(Ban ban) {
        String iconFileName = "thongthuong.png"; 
        
        String iconPath = "images/icon/" + iconFileName; 
        
        JLabel lblIcon;
        ImageIcon iconBan = null;
        
        try {
            iconBan = new ImageIcon(iconPath); 
        } catch (Exception e) {
            // Lỗi
        }

        if (iconBan != null && iconBan.getIconWidth() > 0) {
            try {
                ImageIcon rawIcon = iconBan;
                int targetW = 45, targetH = 45;
                Image img = rawIcon.getImage();
                Image scaled = img.getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                lblIcon = new JLabel(new ImageIcon(scaled));
                lblIcon.setPreferredSize(new Dimension(targetW, targetH));
            } catch (Exception ex) {
                lblIcon = createFallbackIcon(); 
            }
        } else {
            lblIcon = createFallbackIcon();
        }

        lblIcon.setOpaque(false);
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        return lblIcon;
    }

    private JLabel createFallbackIcon() {
        JLabel lblIcon = new JLabel("🍽️");
        lblIcon.setPreferredSize(new Dimension(45, 45));
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        lblIcon.setForeground(MAU_XAM_NHE);
        return lblIcon;
    }

    private void taiDuLieuVaHienThiBanDau(boolean resetKhuVuc) {
        taiDuLieuKhuVuc();
        taiDuLieuDatBan();
        dongBoTrangThaiDatBan();

        String khuVucDaChon = (String) cboFilterKhuVuc.getSelectedItem();
        capNhatCboKhuVuc();

        if (resetKhuVuc || khuVucDaChon == null) {
            cboFilterKhuVuc.setSelectedItem("Tất cả");
            khuVucHienTai = "Tất cả";
        } else {
            cboFilterKhuVuc.setSelectedItem(khuVucDaChon);
            khuVucHienTai = khuVucDaChon;
        }

        capNhatHienThiLuoiBan();
        capNhatThongKeBan();
        capNhatTablePhieuDat();
    }
    
    private void taiDuLieuVaHienThiBanDau() {
         taiDuLieuVaHienThiBanDau(false);
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            if (cardBanDangChon != null && banDangChon != null) {
                 try {
                    String maBan = banDangChon.getMaBan();
                    Ban banMoi = banDAO.getBanTheoMa(maBan); 
                    if(banMoi != null) {
                        ((RoundedPanel) cardBanDangChon).setBorderColor(TrangThaiBan.fromString(banMoi.getTrangThai()).getColor());
                    }
                 } catch (Exception e) {
                     // Bỏ qua
                 }
                 ((RoundedPanel) cardBanDangChon).setBackground(COLOR_WHITE);
                 cardBanDangChon.repaint();
            }
            banDangChon = null;
            cardBanDangChon = null;
            phieuDangChon = null;
            
            xoaRongFormVaResetBan(); 
            taiDuLieuVaHienThiBanDau(false); 
        });
    }
    
    public void refreshDataGridOnly() {
        SwingUtilities.invokeLater(() -> {
            taiDuLieuVaHienThiBanDau(false); 
        });
    }


    private void taiDuLieuKhuVuc() {
        try {
            danhSachBanTheoKhuVuc = phieuDatBanDAO.getAllBanByFloor();
            tenKhuVuc = new ArrayList<>(danhSachBanTheoKhuVuc.keySet());
        } catch (Exception e) {
            System.err.println("Lỗi khi tải dữ liệu khu vực: " + e.getMessage());
            danhSachBanTheoKhuVuc = new LinkedHashMap<>();
            tenKhuVuc = new ArrayList<>();
        }
    }

    private void taiDuLieuDatBan() {
        try {
            danhSachPhieuDatDangHoatDong = phieuDatBanDAO.getAllPhieuDatBan();
            if (danhSachPhieuDatDangHoatDong == null) danhSachPhieuDatDangHoatDong = new ArrayList<>();
        } catch (Exception e) {
            danhSachPhieuDatDangHoatDong = new ArrayList<>();
        }
    }

    private void dongBoTrangThaiDatBan() {
        Set<String> maBanDaDat = new HashSet<>();  
        Set<String> maBanCoKhach = new HashSet<>(); 

        if (danhSachPhieuDatDangHoatDong != null) {
            for (PhieuDatBan phieu : danhSachPhieuDatDangHoatDong) {
                if (phieu != null && phieu.getBan() != null && phieu.getBan().getMaBan() != null) {
                    String trangThaiPhieu = phieu.getTrangThaiPhieu().trim();
                    String maBan = phieu.getBan().getMaBan().trim();

                    if ("Chưa đến".equals(trangThaiPhieu)) {
                        maBanDaDat.add(maBan);
                    } else if ("Đã đến".equals(trangThaiPhieu)) {
                        maBanCoKhach.add(maBan);
                    }
                }
            }
        }

        if (danhSachBanTheoKhuVuc != null) {
            for (List<Ban> danhSachBan : danhSachBanTheoKhuVuc.values()) {
                if (danhSachBan != null) {
                    for (Ban ban : danhSachBan) {
                        if (ban != null && ban.getMaBan() != null) {
                            String maBanHienTai = ban.getMaBan().trim();

                            if (maBanCoKhach.contains(maBanHienTai)) {
                                ban.setTrangThai(TrangThaiBan.CO_KHACH.toString());
                            } else if (maBanDaDat.contains(maBanHienTai)) {
                                ban.setTrangThai(TrangThaiBan.DA_DAT.toString());
                            } else {
                                ban.setTrangThai(TrangThaiBan.TRONG.toString());
                            }
                        }
                    }
                }
            }
        }
    }

    private void capNhatCboKhuVuc() {
        ActionListener[] listeners = cboFilterKhuVuc.getActionListeners();
        for (ActionListener l : listeners) cboFilterKhuVuc.removeActionListener(l);
        cboFilterKhuVuc.removeAllItems();
        cboFilterKhuVuc.addItem("Tất cả");
        for (String tenKV : tenKhuVuc) cboFilterKhuVuc.addItem(tenKV);
        for (ActionListener l : listeners) cboFilterKhuVuc.addActionListener(l);
    }

    private void capNhatHienThiLuoiBan() {
        pnlLuoiBan.removeAll();
        int tablesFound = 0;
        if (danhSachBanTheoKhuVuc != null) {
            for (Map.Entry<String, List<Ban>> entry : danhSachBanTheoKhuVuc.entrySet()) {
                String tenKV = entry.getKey();
                List<Ban> dsBan = entry.getValue();
                if ("Tất cả".equals(khuVucHienTai) || khuVucHienTai.equals(tenKV)) {
                    if (dsBan != null) {
                        for (Ban ban : dsBan) {
                            pnlLuoiBan.add(taoTheBan(ban));
                            tablesFound++;
                        }
                    }
                }
            }
        }
        if (tablesFound == 0) {
            pnlLuoiBan.setLayout(new FlowLayout(FlowLayout.CENTER));
            JLabel nTL = new JLabel("Không có bàn nào để hiển thị trong khu vực này.");
            nTL.setFont(FONT_CHU);
            nTL.setForeground(COLOR_TITLE);
            pnlLuoiBan.add(nTL);
        } else {
            pnlLuoiBan.setLayout(new GridLayout(0, 4, 10, 10));
        }
        pnlLuoiBan.revalidate();
        pnlLuoiBan.repaint();
    }

    private void capNhatThongKeBan() {
        int total = 0, trong = 0, daDat = 0, coKhach = 0;
        String filterKV = (String) cboFilterKhuVuc.getSelectedItem();
        if (danhSachBanTheoKhuVuc != null) {
            for (Map.Entry<String, List<Ban>> entry : danhSachBanTheoKhuVuc.entrySet()) {
                String tenKV = entry.getKey();
                if ("Tất cả".equals(filterKV) || (filterKV != null && filterKV.equals(tenKV))) {
                    List<Ban> dsBan = entry.getValue();
                    if (dsBan != null) {
                        for (Ban ban : dsBan) {
                            total++;
                            String trangThai = ban.getTrangThai().trim();
                            if (TrangThaiBan.TRONG.toString().equals(trangThai)) trong++;
                            else if (TrangThaiBan.DA_DAT.toString().equals(trangThai)) daDat++;
                            else if (TrangThaiBan.CO_KHACH.toString().equals(trangThai)) coKhach++;
                        }
                    }
                }
            }
        }
        String labelKV = "Tất cả khu vực";
        if (!"Tất cả".equals(filterKV) && filterKV != null) labelKV = filterKV;
        lblThongKeBan.setText(String.format("%s | Tổng số bàn: %d (Trống: %d, Đã đặt: %d, Có khách: %d)", labelKV, total, trong, daDat, coKhach));
    }

    private void capNhatTablePhieuDat() {
        modelPhieuDat.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        if (danhSachPhieuDatDangHoatDong != null) {
            for (PhieuDatBan phieu : danhSachPhieuDatDangHoatDong) {
                String trangThai = phieu.getTrangThaiPhieu().trim();
                if ("Chưa đến".equals(trangThai) || "Đã đến".equals(trangThai)) {
                    String thoiGianHienThi = "N/A";
                    LocalDateTime ldt = phieu.getThoiGianDenHen();
                    if (ldt != null) {
                        Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                        thoiGianHienThi = sdf.format(date);
                    }
                    modelPhieuDat.addRow(new Object[]{phieu.getMaPhieu(), thoiGianHienThi, 
                        phieu.getKhachHang() != null ? phieu.getKhachHang().getTenKH() : "Khách lẻ",
                        phieu.getBan() != null ? phieu.getBan().getMaBan().trim() : "N/A", phieu.getTrangThaiPhieu()});
                }
            }
        }
    }
    
    private void ganSuKien() {
        btnDatBan.addActionListener(this);
        btnSearchPDB.addActionListener(this);
        btnRefresh.addActionListener(this);
        btnXemDanhSachPDB.addActionListener(this); 
        btnThanhToan.addActionListener(this); 
        cboFilterKhuVuc.addActionListener(e -> {
            String newKhuVuc = (String) cboFilterKhuVuc.getSelectedItem();
            if (newKhuVuc != null && !newKhuVuc.equals(khuVucHienTai)) {
                khuVucHienTai = newKhuVuc;
                capNhatHienThiLuoiBan();
                capNhatThongKeBan();
            }
        });

        cboSoKhach.addActionListener(e -> {
            // Không làm gì (vì table đã bị xóa)
        });

        tblPhieuDat.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                // SỬA: Sự kiện double-click trên bảng này sẽ được xử lý trong pop-up
            }
        });
    }

    private JPanel timCardBan(String maBan) {
        for (Component comp : pnlLuoiBan.getComponents()) {
            if (comp instanceof RoundedPanel) {
                try {
                    RoundedPanel card = (RoundedPanel) comp;
                    Component info = card.getComponent(1); 
                    if (info instanceof JPanel) {
                        JLabel lblName = (JLabel) ((JPanel) info).getComponent(0);
                        if (lblName.getText().startsWith(maBan.trim())) {
                            return card;
                        }
                    }
                } catch (Exception e) {
                    // Lỗi tìm kiếm, bỏ qua
                }
            }
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnRefresh) {
            refreshData();
        } else if (o == btnDatBan) {
            xuLyNutDatBan();
        } else if (o == btnSearchPDB) {
            timKiemPhieuDat();
        } else if (o == btnXemDanhSachPDB) {
            hienThiDanhSachPhieuDat();
        } else if (o == btnThanhToan) {
            xuLyThanhToan(); 
        }
    }
    
    private void hienThiDanhSachPhieuDat() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Danh sách Phiếu Đặt Bàn", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        
        JScrollPane scrollPane = new JScrollPane(tblPhieuDat);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        tblPhieuDat.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblPhieuDat.getSelectedRow();
                    if (row != -1) {
                        String maPhieu = (String) modelPhieuDat.getValueAt(row, 0);
                        PhieuDatBan phieu = phieuDatBanDAO.getPhieuDatBanById(maPhieu);
                        if (phieu != null && phieu.getBan() != null) {
                            JPanel card = timCardBan(phieu.getBan().getMaBan());
                            xuLyChonBan(phieu.getBan(), card);
                            dialog.dispose(); 
                        }
                    }
                }
            }
        });
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton btnDong = new RoundedButton("Đóng", MAU_XAM_NHE, COLOR_WHITE);
        btnDong.addActionListener(e -> dialog.dispose());
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButton.setBackground(Color.WHITE);
        pnlButton.setBorder(new EmptyBorder(5, 10, 5, 10));
        pnlButton.add(btnDong);
        
        dialog.add(pnlButton, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private void xoaRongFormVaResetBan() {
        txtTenKhach.setText("");
        txtSdtKhach.setText("");
        txtGhiChu.setText("");
        lblMaBanValue.setText("--");
        lblKhuVucValue.setText("--");
        lblLoaiBanValue.setText("--");
        lblSoChoValue.setText("--");
        cboSoKhach.setSelectedIndex(0);
        datePicker.setDate(new Date());
        spinnerGioDat.setValue(new Date());
        
        tblPhieuDat.clearSelection();
        
        txtSearchPDB.setText("");
        btnDatBan.setText("Đặt bàn");
        btnThanhToan.setVisible(false); // SỬA: Tắt nút thanh toán khi reset
        phieuDangChon = null;
        banDangChon = null;
    }

    private void timKiemPhieuDat() {
        String keyword = txtSearchPDB.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập từ khóa tìm kiếm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this, "Đang tìm kiếm Phiếu Đặt Bàn với từ khóa: " + keyword);
    }

    private void xuLyChonBan(Ban ban, JPanel clickedCard) {
        if (cardBanDangChon != null && cardBanDangChon instanceof RoundedPanel && banDangChon != null) {
            try {
                Ban banCu = banDAO.getBanTheoMa(banDangChon.getMaBan()); 
                if(banCu != null) {
                    ((RoundedPanel) cardBanDangChon).setBorderColor(TrangThaiBan.fromString(banCu.getTrangThai()).getColor());
                }
            } catch (Exception e) {
                 ((RoundedPanel) cardBanDangChon).setBorderColor(TrangThaiBan.fromString(banDangChon.getTrangThai()).getColor());
            }
            ((RoundedPanel) cardBanDangChon).setBackground(COLOR_WHITE);
            cardBanDangChon.repaint();
        }

        this.banDangChon = ban;
        this.cardBanDangChon = clickedCard;

        if (cardBanDangChon != null && cardBanDangChon instanceof RoundedPanel) {
            ((RoundedPanel) cardBanDangChon).setBorderColor(MAU_HIGHLIGHT);
            ((RoundedPanel) cardBanDangChon).setBackground(new Color(240, 248, 255));
            cardBanDangChon.repaint();
        }

        String khuVuc = "--";
        for (Map.Entry<String, List<Ban>> entry : danhSachBanTheoKhuVuc.entrySet()) {
            if (entry.getValue().stream().anyMatch(b -> b.getMaBan().equals(ban.getMaBan()))) {
                khuVuc = entry.getKey();
                break;
            }
        }

        String loaiBan = ban.getLoaiBan();
        try {
            loaiBan = LoaiBan.fromString(loaiBan).getTenHienThi();
        } catch (Exception ex) { }

        lblMaBanValue.setText(ban.getMaBan().trim());
        lblKhuVucValue.setText(khuVuc);
        lblLoaiBanValue.setText(loaiBan);
        lblSoChoValue.setText(String.valueOf(ban.getSoCho()));

        TrangThaiBan trangThai = TrangThaiBan.fromString(ban.getTrangThai());

        if (trangThai == TrangThaiBan.DA_DAT || trangThai == TrangThaiBan.CO_KHACH) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            xoaRongFormVaResetBan(); 
            this.banDangChon = ban; 
            this.cardBanDangChon = clickedCard;
            btnDatBan.setText("Gọi món");
            if (trangThai == TrangThaiBan.CO_KHACH) {
                btnThanhToan.setVisible(true); 
            } else {
                btnThanhToan.setVisible(false); 
            }

            SwingWorker<PhieuDatBan, Void> worker = new SwingWorker<PhieuDatBan, Void>() {
                @Override
                protected PhieuDatBan doInBackground() throws Exception {
                    return phieuDatBanDAO.getPhieuByBan(ban.getMaBan());
                }

                @Override
                protected void done() {
                    setCursor(Cursor.getDefaultCursor());
                    try {
                        phieuDangChon = get();
                        if (phieuDangChon != null) {
                            if (phieuDangChon.getKhachHang() != null) {
                                String maKH = phieuDangChon.getKhachHang().getMaKH();
                                if (!"KH00000000".equals(maKH)) {
                                    txtTenKhach.setText(phieuDangChon.getKhachHang().getTenKH());
                                    txtSdtKhach.setText(phieuDangChon.getKhachHang().getSoDienThoai());
                                } else {
                                    String ghiChu = phieuDangChon.getGhiChu();
                                    if (ghiChu != null && ghiChu.startsWith("Khách: ")) {
                                        try {
                                            String[] parts = ghiChu.split(" - SĐT: ");
                                            if (parts.length > 0) txtTenKhach.setText(parts[0].substring(7).trim());
                                            if (parts.length > 1) {
                                                String[] parts2 = parts[1].split("\\. Ghi chú: ");
                                                txtSdtKhach.setText(parts2[0].trim());
                                                if (parts2.length > 1) txtGhiChu.setText(parts2[1].trim());
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            cboSoKhach.setSelectedItem(phieuDangChon.getSoNguoi());
                            if (phieuDangChon.getThoiGianDenHen() != null) {
                                Date tgHen = Date.from(phieuDangChon.getThoiGianDenHen().atZone(ZoneId.systemDefault()).toInstant());
                                datePicker.setDate(tgHen);
                                spinnerGioDat.setValue(tgHen);
                            }
                        }
                        
                        if (trangThai == TrangThaiBan.DA_DAT) {
                            JOptionPane.showMessageDialog(DatBan_View.this,
                                String.format("Bàn %s đã được đặt!\nĐã tải thông tin phiếu: %s", ban.getMaBan().trim(),
                                        (phieuDangChon != null ? phieuDangChon.getMaPhieu() : "N/A")), "Bàn đã đặt", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(DatBan_View.this,
                                String.format("Bàn %s đang phục vụ khách!\nBạn có thể gọi thêm món hoặc thanh toán.", ban.getMaBan().trim()),
                                "Bàn đang sử dụng", JOptionPane.INFORMATION_MESSAGE);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            worker.execute();

        } else {
            phieuDangChon = null;
            btnDatBan.setText("Đặt bàn");
        }
    }

    private void xuLyNutDatBan() {
        if (banDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bàn trước!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TrangThaiBan trangThai = TrangThaiBan.fromString(banDangChon.getTrangThai());
        if (trangThai == TrangThaiBan.TRONG) datBanMoi();
        else goiMon();
    }

    private void datBanMoi() {
        final String tenKhach = txtTenKhach.getText().trim();
        final String sdtHoacMaKH = txtSdtKhach.getText().trim();
        final int soNguoi = (int) cboSoKhach.getSelectedItem();
        final String ghiChu = txtGhiChu.getText().trim();

        if (tenKhach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtTenKhach.requestFocus();
            return;
        }
        if (sdtHoacMaKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập SĐT hoặc Mã KH!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtSdtKhach.requestFocus();
            return;
        }

        Date ngayHen = datePicker.getDate();
        Date gioHen = (Date) spinnerGioDat.getValue();
        if (ngayHen == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày hẹn!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Calendar calNgay = Calendar.getInstance();
        calNgay.setTime(ngayHen);
        Calendar calGio = Calendar.getInstance();
        calGio.setTime(gioHen);
        calNgay.set(Calendar.HOUR_OF_DAY, calGio.get(Calendar.HOUR_OF_DAY));
        calNgay.set(Calendar.MINUTE, calGio.get(Calendar.MINUTE));
        calNgay.set(Calendar.SECOND, 0);
        calNgay.set(Calendar.MILLISECOND, 0);
        
        final LocalDateTime thoiGianHenFinal = calNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (thoiGianHenFinal.isBefore(LocalDateTime.now().minusMinutes(5))) {
            int confirm = JOptionPane.showConfirmDialog(this, "Thời gian hẹn đã qua!\nBạn có muốn đặt luôn với thời gian hiện tại không?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.NO_OPTION) {
                 return;
            }
        }
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<PhieuDatBan, String> worker = new SwingWorker<PhieuDatBan, String>() {
            
            private String ghiChuFinal = ghiChu;
            private boolean success = false;
            
            @Override
            protected PhieuDatBan doInBackground() throws Exception {
                
                KhachHang_DAO khachHangDAO = new KhachHang_DAO();
                KhachHang khachHang = null;

                if (sdtHoacMaKH.toUpperCase().startsWith("KH")) {
                    khachHang = khachHangDAO.getKhachHangById(sdtHoacMaKH.toUpperCase());
                    if (khachHang == null) {
                        throw new Exception("Mã khách hàng '" + sdtHoacMaKH + "' không tồn tại!");
                    }
                } else if (sdtHoacMaKH.matches("\\d{10}")) {
                    khachHang = khachHangDAO.getKhachHangBySDT(sdtHoacMaKH);
                    if (khachHang == null) {
                        khachHang = khachHangDAO.getKhachHangById("KH00000000");
                        if (khachHang == null) {
                            throw new Exception("Lỗi: Không tìm thấy khách hàng vãng lai (KH00000000)!");
                        }
                        String ghiChuFull = String.format("Khách: %s - SĐT: %s", tenKhach, sdtHoacMaKH);
                        if (!ghiChu.isEmpty()) {
                            ghiChuFull += ". Ghi chú: " + ghiChu;
                        }
                        ghiChuFinal = ghiChuFull;
                    }
                } else {
                    throw new Exception("SĐT/Mã KH không hợp lệ.\nSĐT phải là 10 số.\nMã KH phải bắt đầu bằng 'KH'.");
                }

                String maPhieuMoi = phieuDatBanDAO.generateNewID();
                NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
                NhanVien nhanVien = nhanVienDAO.getNhanVienById("NVTT001"); 
                if (nhanVien == null) {
                    throw new Exception("Lỗi: Không tìm thấy thông tin nhân viên (NVTT001)!");
                }

                PhieuDatBan phieuMoi = new PhieuDatBan(maPhieuMoi, thoiGianHenFinal, null, null, khachHang, nhanVien, banDangChon, soNguoi, ghiChuFinal, "Chưa đến");
                success = phieuDatBanDAO.insertPhieuDatBan(phieuMoi);

                if (success) {
                    banDangChon.setTrangThai(TrangThaiBan.DA_DAT.name());
                    banDAO.capNhatBan(banDangChon);
                    return phieuMoi;
                }
                return null;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor()); 
                
                try {
                    PhieuDatBan phieuMoi = get(); 
                    
                    if (success && phieuMoi != null) {
                        refreshDataGridOnly(); 
                        
                        phieuDangChon = phieuDatBanDAO.getPhieuDatBanById(phieuMoi.getMaPhieu());

                        String thongBao = String.format("<html>Đặt bàn thành công!<br><br>Mã phiếu: %s<br>Bàn: %s<br>Khách: %s</html>",
                                        phieuMoi.getMaPhieu().trim(), banDangChon.getMaBan().trim(), tenKhach);
                        JOptionPane.showMessageDialog(DatBan_View.this, thongBao, "Đặt bàn thành công", JOptionPane.INFORMATION_MESSAGE);

                        btnDatBan.setText("Gọi món");
                        btnThanhToan.setVisible(false);
                        
                        if (cardBanDangChon != null && cardBanDangChon instanceof RoundedPanel) {
                            ((RoundedPanel) cardBanDangChon).setBorderColor(MAU_HIGHLIGHT);
                            ((RoundedPanel) cardBanDangChon).setBackground(new Color(240, 248, 255));
                            cardBanDangChon.repaint();
                        }
                    } else {
                        JOptionPane.showMessageDialog(DatBan_View.this, "Lỗi khi đặt bàn! Vui lòng thử lại.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (ExecutionException ex) {
                    String errorMessage = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(DatBan_View.this, errorMessage, "Lỗi", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(DatBan_View.this, "Lỗi không xác định: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute(); 
    }
    
    private void goiMon() {
        if (phieuDangChon == null) phieuDangChon = phieuDatBanDAO.getPhieuByBan(banDangChon.getMaBan());
        if (phieuDangChon == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đặt bàn!\nVui lòng kiểm tra lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("Chưa đến".equals(phieuDangChon.getTrangThaiPhieu())) {
            phieuDangChon.setTrangThaiPhieu("Đã đến");
            phieuDangChon.setThoiGianNhanBan(LocalDateTime.now());
            phieuDatBanDAO.updatePhieuDatBan(phieuDangChon);
            banDangChon.setTrangThai(TrangThaiBan.CO_KHACH.name());
            banDAO.capNhatBan(banDangChon);
            
            btnThanhToan.setVisible(true);
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        if (owner == null) owner = JOptionPane.getRootFrame();
        ChonMon_Dialog chonMonDialog = new ChonMon_Dialog(owner, phieuDangChon);
        chonMonDialog.setVisible(true);

        refreshDataGridOnly(); 

        int tiepTuc = JOptionPane.showConfirmDialog(this, "Đã gọi món thành công!\n\nBạn có muốn gọi thêm món không?", "Gọi món thành công", JOptionPane.YES_NO_OPTION);
        if (tiepTuc == JOptionPane.YES_OPTION) goiMon();
        else {
            int thanhToan = JOptionPane.showConfirmDialog(this, "Bạn có muốn chuyển sang thanh toán không?", "Thanh toán", JOptionPane.YES_NO_OPTION);
            if (thanhToan == JOptionPane.YES_OPTION) xuLyThanhToan();
            else xoaRongFormVaResetBan();
        }
    }

 // Sửa hàm này trong file: view/Ban/DatBan_View.java

    private void xuLyThanhToan() {
        if (banDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn để thanh toán!", "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (phieuDangChon == null) {
            phieuDangChon = phieuDatBanDAO.getPhieuByBan(banDangChon.getMaBan());
            if (phieuDangChon == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đặt bàn cho bàn này!", "Lỗi", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chi tiết bàn " + banDangChon.getMaBan(), true);
        dialog.setSize(1000, 700);
        dialog.setLocationRelativeTo(this);

        String maNV = "NVTT001";
        if (phieuDangChon.getNhanVien() != null) maNV = phieuDangChon.getNhanVien().getMaNhanVien();
        Runnable callbackSauKhiThanhToan = () -> {
            refreshDataGridOnly();   
            xoaRongFormVaResetBan(); 
            dialog.dispose();       
        };

        ChiTietPhieuDatBan_View chiTietView = new ChiTietPhieuDatBan_View(
            banDangChon, 
            maNV, 
            callbackSauKhiThanhToan 
        );
        
        dialog.setContentPane(chiTietView);
        dialog.setVisible(true);
    }

    // UI helper classes
    private class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private Color borderColor = MAU_VIEN;
        private final Color bgColor;

        public RoundedPanel(int radius, Color color, LayoutManager layout) {
            super(layout);
            this.cornerRadius = radius;
            this.bgColor = color;
            setOpaque(false);
        }

        public void setBorderColor(Color color) {
            this.borderColor = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.borderColor);
            g2.setStroke(new BasicStroke(1));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
            g2.dispose();
        }
    }

    private class RoundedButton extends JButton {
        private final int doBoGoc;
        private final Color bg;

        public RoundedButton(String text, Color bgColor, Color fg) {
            super(text);
            this.bg = bgColor;
            this.doBoGoc = 20;
            setBackground(bgColor);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setBorder(new EmptyBorder(12, 24, 12, 24));
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(fg);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(100, 35));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color currentColor = getBackground();
            if (getModel().isPressed()) {
                currentColor = getBackground().darker();
            } else if (getModel().isRollover()) {
                currentColor = getBackground().brighter();
            }
            g2.setColor(currentColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), doBoGoc, doBoGoc));
            super.paintComponent(g);
            g2.dispose();
        }
    }
}