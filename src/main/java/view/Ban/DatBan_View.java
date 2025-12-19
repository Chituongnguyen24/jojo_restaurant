package view.Ban;

import dao.Ban_DAO;
import dao.PhieuDatBan_DAO;
import dao.KhachHang_DAO;
import dao.NhanVien_DAO;
import entity.Ban;
import entity.PhieuDatBan;
import entity.KhachHang;
import entity.NhanVien;
import enums.TrangThaiBan;
import enums.LoaiBan;
import view.ThucDon.ChonMon_Dialog;

import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

public class DatBan_View extends JPanel implements ActionListener {

    private final Ban_DAO banDAO;
    private final PhieuDatBan_DAO phieuDatBanDAO;

    // Components
    private final JComboBox<Integer> cboSoKhach;
    private final JComboBox<String> cboFilterKhuVuc;
    private final JDateChooser datePicker;
    private final JComboBox<String> cboCaLamViec;
    private final JSpinner spinnerGioHenCuThe;
    private final JTextField txtSearchPDB, txtTenKhach, txtSdtKhach, txtGhiChu;
    
    private final JButton btnSearchPDB, btnDatBan, btnXemDanhSachPDB, btnThanhToan, btnHuyDatBan;
    private final JButton btnTimBanTheoGio; 
    private final DefaultTableModel modelPhieuDat;
    private final JTable tblPhieuDat;
    
    private final JPanel pnlLuoiBan;
    private final JLabel lblThongKeBan;
    private final JLabel lblDateTime;

    private final JLabel lblMaBanValue, lblKhuVucValue, lblLoaiBanValue, lblSoChoValue;

    // Data Management
    private Map<String, List<Ban>> danhSachBanTheoKhuVuc = new LinkedHashMap<>();
    private List<String> tenKhuVuc = new ArrayList<>();
    private List<PhieuDatBan> danhSachPhieuDatDangHoatDong = new ArrayList<>();
    private String khuVucHienTai = "Tất cả";

    // State
    private Ban banDangChon = null;
    private PhieuDatBan phieuDangChon = null;
    private JPanel cardBanDangChon = null;

    // Styling Constants
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

    private final javax.swing.Timer clockTimer;

    // Inner class for background data loading
    private static class BackgroundData {
        final Map<String, List<Ban>> banTheoKhuVuc;
        final List<PhieuDatBan> phieuDatDangHoatDong;
        final List<String> tenKhuVuc;
        
        BackgroundData(Map<String, List<Ban>> ban, List<PhieuDatBan> phieu) {
            this.banTheoKhuVuc = (ban != null) ? ban : new LinkedHashMap<>();
            this.phieuDatDangHoatDong = (phieu != null) ? phieu : new ArrayList<>();
            this.tenKhuVuc = new ArrayList<>(this.banTheoKhuVuc.keySet());
        }
    }

    public DatBan_View() {
        banDAO = new Ban_DAO();
        phieuDatBanDAO = new PhieuDatBan_DAO();

        // Initialize UI Components
        txtSearchPDB = createStyledTextField("Mã PDB, SĐT hoặc Tên khách");
        txtSearchPDB.setPreferredSize(new Dimension(150, 38));

        cboSoKhach = createStyledComboBox();
        for (int i : new Integer[]{1, 2, 3, 4, 5, 6, 8, 10, 12, 15, 20}) cboSoKhach.addItem(i);

        cboFilterKhuVuc = createStyledComboBox();

        datePicker = new JDateChooser(new Date());
        datePicker.setDateFormatString("dd/MM/yyyy");

        cboCaLamViec = createStyledComboBox();
        cboCaLamViec.addItem("Sáng (08h-13h)");
        cboCaLamViec.addItem("Chiều (13h-18h)");
        cboCaLamViec.addItem("Tối (18h-23h)");
        
        int currentHour = LocalTime.now().getHour();
        if (currentHour >= 8 && currentHour < 13) cboCaLamViec.setSelectedIndex(0);
        else if (currentHour >= 13 && currentHour < 18) cboCaLamViec.setSelectedIndex(1);
        else cboCaLamViec.setSelectedIndex(2);
        
        SpinnerDateModel modelGio = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
        spinnerGioHenCuThe = new JSpinner(modelGio);
        JSpinner.DateEditor editorGio = new JSpinner.DateEditor(spinnerGioHenCuThe, "HH:mm");
        spinnerGioHenCuThe.setEditor(editorGio);

        txtTenKhach = createStyledTextField("Họ và tên khách hàng");
        txtSdtKhach = createStyledTextField("Số điện thoại");
        txtGhiChu = createStyledTextField("Ghi chú");

        btnSearchPDB = new RoundedButton("Tìm phiếu", MAU_XANH_DUONG, COLOR_WHITE);
        btnSearchPDB.setPreferredSize(new Dimension(100, 38));

        btnTimBanTheoGio = new RoundedButton("Tìm bàn", MAU_XANH_LA, COLOR_WHITE);
        btnTimBanTheoGio.setPreferredSize(new Dimension(100, 38)); 

        btnDatBan = new RoundedButton("Đặt bàn", MAU_XANH_LA, COLOR_WHITE);
        btnDatBan.setPreferredSize(new Dimension(100, 40));
        
        btnThanhToan = new RoundedButton("Thanh toán", MAU_XANH_DUONG, COLOR_WHITE); 
        btnThanhToan.setPreferredSize(new Dimension(110, 40));
        btnThanhToan.setVisible(false);
        
        btnHuyDatBan = new RoundedButton("Hủy đặt", MAU_DO, COLOR_WHITE); 
        btnHuyDatBan.setPreferredSize(new Dimension(100, 40));
        btnHuyDatBan.setVisible(false);
        
        btnXemDanhSachPDB = new RoundedButton("DS Đặt bàn", MAU_XAM_NHE, COLOR_WHITE);
        btnXemDanhSachPDB.setPreferredSize(new Dimension(120, 35));
        
        lblThongKeBan = new JLabel("Đang tải dữ liệu...");
        lblThongKeBan.setFont(FONT_CHU);
        lblThongKeBan.setForeground(MAU_XAM_NHE);

        lblDateTime = new JLabel();
        lblDateTime.setFont(FONT_CHU);
        lblDateTime.setForeground(MAU_XAM_NHE);

        lblMaBanValue = createInfoLabel("--");
        lblKhuVucValue = createInfoLabel("--");
        lblLoaiBanValue = createInfoLabel("--");
        lblSoChoValue = createInfoLabel("--");

        modelPhieuDat = new DefaultTableModel(new String[]{"Mã PDB", "Giờ Hẹn", "Khách hàng", "Bàn", "Trạng Thái"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblPhieuDat = taoStyledTable(modelPhieuDat);
        
        pnlLuoiBan = new JPanel(new GridLayout(0, 4, 10, 10));
        pnlLuoiBan.setOpaque(false);
        pnlLuoiBan.setBackground(BG_VIEW);
        pnlLuoiBan.setBorder(new EmptyBorder(15, 15, 15, 15));

        clockTimer = new javax.swing.Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm:ss");
            lblDateTime.setText(sdf.format(new Date()));
        });
        clockTimer.start();

        thietLapGiaoDien();
        ganSuKien(); 
        loadDataAsync(true);
    }

    private void thietLapGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(BG_VIEW);
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
        pnlSearch.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Row 1
        gbc.gridx = 0; gbc.gridy = 0;
        pnlSearch.add(new JLabel("Tìm kiếm:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.1;
        pnlSearch.add(txtSearchPDB, gbc);
        
        gbc.gridx = 2; gbc.weightx = 0;
        pnlSearch.add(btnSearchPDB, gbc);

        gbc.gridx = 3;
        pnlSearch.add(new JLabel("Số khách:"), gbc);
        
        gbc.gridx = 4;
        pnlSearch.add(cboSoKhach, gbc);

        gbc.gridx = 5;
        pnlSearch.add(new JLabel("Khu vực:"), gbc);
        
        gbc.gridx = 6; gbc.weightx = 0.1;
        pnlSearch.add(cboFilterKhuVuc, gbc);

        gbc.gridx = 7; gbc.weightx = 0;
        pnlSearch.add(new JLabel("Ngày:"), gbc);
        
        gbc.gridx = 8; gbc.weightx = 0.1;
        pnlSearch.add(datePicker, gbc);

        gbc.gridx = 9; gbc.weightx = 0;
        pnlSearch.add(new JLabel("Ca:"), gbc); 
        
        gbc.gridx = 10;
        pnlSearch.add(cboCaLamViec, gbc);
        
        gbc.gridx = 11; gbc.weightx = 0;
        pnlSearch.add(btnTimBanTheoGio, gbc);

        return pnlSearch;
    }

    private JSplitPane taoPanelNoiDungChinh() {
        JPanel pnlListContainer = taoPanelDanhSachBan();
        JPanel pnlCRUD = taoPanelCRUDForm();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlListContainer, pnlCRUD);
        splitPane.setDividerLocation(900);
        splitPane.setResizeWeight(0.7);
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
        pnlFilterBan.add(taoMucChuThich("Trống", MAU_XANH_LA));
        pnlFilterBan.add(taoMucChuThich("Đã đặt", MAU_CAM_CHINH));
        pnlFilterBan.add(taoMucChuThich("Có khách", MAU_DO));
        pnlWrapper.add(pnlFilterBan, BorderLayout.SOUTH);

        JPanel tableContentWrapper = new JPanel(new BorderLayout());
        tableContentWrapper.setOpaque(false);
        tableContentWrapper.add(pnlLuoiBan, BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(tableContentWrapper);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(BG_VIEW);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        pnlWrapper.add(scroll, BorderLayout.CENTER);

        return pnlWrapper;
    }

    private JPanel taoPanelCRUDForm() {
        RoundedPanel pnlForm = new RoundedPanel(20, COLOR_WHITE, new BorderLayout(0, 10));
        pnlForm.setPreferredSize(new Dimension(350, 450));
        pnlForm.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel lblFormTitle = new JLabel("Thông tin đặt bàn");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(COLOR_TITLE);
        lblFormTitle.setHorizontalAlignment(SwingConstants.CENTER);
        pnlForm.add(lblFormTitle, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel();
        pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.Y_AXIS));
        pnlCenter.setOpaque(false);

        // Panel Thông tin bàn
        JPanel pnlBanInfo = new JPanel(new GridLayout(2, 4, 5, 5));
        pnlBanInfo.setOpaque(false);
        pnlBanInfo.setBorder(BorderFactory.createTitledBorder(new LineBorder(MAU_VIEN, 1), "Bàn đã chọn",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, FONT_TIEUDE_NHO, COLOR_TITLE));
        pnlBanInfo.add(taoFormLabel("Mã Bàn:"));
        pnlBanInfo.add(lblMaBanValue);
        pnlBanInfo.add(taoFormLabel("Khu Vực:"));
        pnlBanInfo.add(lblKhuVucValue);
        pnlBanInfo.add(taoFormLabel("Loại:"));
        pnlBanInfo.add(lblLoaiBanValue);
        pnlBanInfo.add(taoFormLabel("Số Chỗ:"));
        pnlBanInfo.add(lblSoChoValue);

        // Panel Input khách hàng
        JPanel pnlInput = new JPanel(new GridLayout(4, 2, 8, 10));
        pnlInput.setOpaque(false);
        pnlInput.setBorder(BorderFactory.createTitledBorder(new LineBorder(MAU_VIEN, 1), "Khách hàng",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP, FONT_TIEUDE_NHO, COLOR_TITLE));
        pnlInput.add(taoFormLabel("Họ tên:"));
        pnlInput.add(txtTenKhach);
        pnlInput.add(taoFormLabel("SĐT/Mã KH:"));
        pnlInput.add(txtSdtKhach);
        pnlInput.add(taoFormLabel("Giờ đến:"));
        pnlInput.add(spinnerGioHenCuThe);
        pnlInput.add(taoFormLabel("Ghi chú:"));
        pnlInput.add(txtGhiChu);
        
        pnlCenter.add(pnlBanInfo); 
        pnlCenter.add(Box.createVerticalStrut(15));
        pnlCenter.add(pnlInput); 
        pnlCenter.add(Box.createVerticalGlue());
        
        pnlForm.add(pnlCenter, BorderLayout.CENTER);

        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pnlButton.setOpaque(false);
        pnlButton.add(btnDatBan);
        pnlButton.add(btnHuyDatBan); 
        pnlButton.add(btnThanhToan);
        
        pnlForm.add(pnlButton, BorderLayout.SOUTH);

        return pnlForm;
    }

    private JLabel taoFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_CHU_NHO);
        label.setForeground(COLOR_TITLE);
        return label;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_CHU);
        label.setForeground(new Color(50, 50, 50));
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
        item.add(circle);
        item.add(label);
        return item;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
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
        table.setSelectionForeground(Color.BLACK);
        table.getTableHeader().setFont(FONT_TIEUDE_NHO);
        table.getTableHeader().setBackground(new Color(245, 245, 245));
        return table;
    }

    private JPanel taoTheBan(Ban ban) {
        RoundedPanel card = new RoundedPanel(15, COLOR_WHITE, new BorderLayout(8, 0));
        card.setBorder(new EmptyBorder(6, 8, 6, 10));
        card.setPreferredSize(new Dimension(200, 75));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        TrangThaiBan ttb = TrangThaiBan.fromString(ban.getTrangThai());
        card.setBorderColor(ttb.getColor());

        JLabel lblIcon = createBanIconLabel(ban);
        card.add(lblIcon, BorderLayout.WEST);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblName = new JLabel(ban.getMaBan().trim());
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(COLOR_TITLE);

        JLabel lblCapacity = new JLabel(ban.getSoCho() + " chỗ");
        lblCapacity.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblCapacity.setForeground(MAU_XAM_NHE);

        JLabel lblTrangThai = new JLabel(ttb.getTenHienThi());
        lblTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 11));
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
        });

        return card;
    }

    private JLabel createBanIconLabel(Ban ban) {
        JLabel lblIcon = new JLabel("🍽️");
        lblIcon.setPreferredSize(new Dimension(45, 45));
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        return lblIcon;
    }

    // --- LOGIC VÀ XỬ LÝ DỮ LIỆU ---

    private void loadDataAsync(boolean resetKhuVuc) {
        pnlLuoiBan.removeAll();
        pnlLuoiBan.setLayout(new FlowLayout(FlowLayout.CENTER));
        pnlLuoiBan.add(new JLabel("Đang tải dữ liệu..."));
        pnlLuoiBan.revalidate();
        pnlLuoiBan.repaint();
        
        SwingWorker<BackgroundData, Void> worker = new SwingWorker<BackgroundData, Void>() {
            @Override
            protected BackgroundData doInBackground() throws Exception {
                Map<String, List<Ban>> banData = phieuDatBanDAO.getAllBanByFloor();
                List<PhieuDatBan> phieuData = phieuDatBanDAO.getAllPhieuDatBan();
                return new BackgroundData(banData, phieuData);
            }

            @Override
            protected void done() {
                try {
                    BackgroundData data = get();
                    danhSachBanTheoKhuVuc = data.banTheoKhuVuc;
                    danhSachPhieuDatDangHoatDong = data.phieuDatDangHoatDong;
                    tenKhuVuc = data.tenKhuVuc;

                    capNhatTrangThaiBanTheoThoiGian();
                    capNhatCboKhuVuc();
                    if (resetKhuVuc) {
                        cboFilterKhuVuc.setSelectedItem("Tất cả");
                        khuVucHienTai = "Tất cả";
                    } else if (khuVucHienTai != null) {
                        cboFilterKhuVuc.setSelectedItem(khuVucHienTai);
                    }
                    
                    capNhatHienThiLuoiBan();
                    capNhatThongKeBan();
                    capNhatTablePhieuDat();
                } catch (Exception e) {
                    e.printStackTrace();
                    pnlLuoiBan.removeAll();
                    pnlLuoiBan.add(new JLabel("Lỗi tải dữ liệu!"));
                }
            }
        };
        worker.execute();
    }

    public void refreshData() {
        xoaRongFormVaResetBan();
        loadDataAsync(false);
    }

    private void capNhatTrangThaiBanTheoThoiGian() {
        Date dateVal = datePicker.getDate();
        if (dateVal == null) dateVal = new Date();
        
        LocalDateTime ngayChon = dateVal.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                                    .withHour(0).withMinute(0).withSecond(0);

        LocalTime startCa, endCa;
        String caChon = (String) cboCaLamViec.getSelectedItem();
        
        if (caChon != null && caChon.contains("Sáng")) {
            startCa = LocalTime.of(8, 0); endCa = LocalTime.of(13, 0);
        } else if (caChon != null && caChon.contains("Chiều")) {
            startCa = LocalTime.of(13, 1); endCa = LocalTime.of(18, 0);
        } else {
            startCa = LocalTime.of(18, 1); endCa = LocalTime.of(23, 0);
        }
        
        LocalDateTime thoiGianBatDauCa = LocalDateTime.of(ngayChon.toLocalDate(), startCa);
        LocalDateTime thoiGianKetThucCa = LocalDateTime.of(ngayChon.toLocalDate(), endCa);
        LocalDateTime now = LocalDateTime.now();

        Set<String> maBanDaDatTrongCa = new HashSet<>();
        Set<String> maBanCoKhachHienTai = new HashSet<>();
        
        if (danhSachPhieuDatDangHoatDong != null) {
            for (PhieuDatBan phieu : danhSachPhieuDatDangHoatDong) {
                if (phieu == null || phieu.getBan() == null) continue;
                String maBan = phieu.getBan().getMaBan().trim();
                String trangThaiPhieu = phieu.getTrangThaiPhieu().trim();
                LocalDateTime thoiGianHen = phieu.getThoiGianDenHen();
    
                if ("Đã đến".equals(trangThaiPhieu)) maBanCoKhachHienTai.add(maBan);
    
                if (thoiGianHen != null && !thoiGianHen.isBefore(thoiGianBatDauCa) && !thoiGianHen.isAfter(thoiGianKetThucCa)) {
                    if ("Chưa đến".equals(trangThaiPhieu) || "Đã đến".equals(trangThaiPhieu)) {
                        maBanDaDatTrongCa.add(maBan);
                    }
                }
            }
        }

        boolean dangXemCaHienTai = now.isAfter(thoiGianBatDauCa) && now.isBefore(thoiGianKetThucCa) 
                                   && now.toLocalDate().isEqual(ngayChon.toLocalDate());

        if (danhSachBanTheoKhuVuc != null) {
            for (List<Ban> dsBan : danhSachBanTheoKhuVuc.values()) {
                for (Ban ban : dsBan) {
                    String ma = ban.getMaBan().trim();
                    if (dangXemCaHienTai && maBanCoKhachHienTai.contains(ma)) ban.setTrangThai(TrangThaiBan.CO_KHACH.toString());
                    else if (maBanDaDatTrongCa.contains(ma)) ban.setTrangThai(TrangThaiBan.DA_DAT.toString());
                    else ban.setTrangThai(TrangThaiBan.TRONG.toString());
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
        pnlLuoiBan.setLayout(new GridLayout(0, 4, 10, 10));
        
        int count = 0;
        if (danhSachBanTheoKhuVuc != null) {
            for (Map.Entry<String, List<Ban>> entry : danhSachBanTheoKhuVuc.entrySet()) {
                if ("Tất cả".equals(khuVucHienTai) || entry.getKey().equals(khuVucHienTai)) {
                    for (Ban ban : entry.getValue()) {
                        pnlLuoiBan.add(taoTheBan(ban));
                        count++;
                    }
                }
            }
        }
        
        if (count == 0) {
            pnlLuoiBan.setLayout(new FlowLayout(FlowLayout.CENTER));
            pnlLuoiBan.add(new JLabel("Không có bàn nào."));
        }
        
        pnlLuoiBan.revalidate();
        pnlLuoiBan.repaint();
    }

    private void capNhatThongKeBan() {
        int total = 0, trong = 0, daDat = 0, coKhach = 0;
        if (danhSachBanTheoKhuVuc != null) {
            for (Map.Entry<String, List<Ban>> entry : danhSachBanTheoKhuVuc.entrySet()) {
                if ("Tất cả".equals(khuVucHienTai) || entry.getKey().equals(khuVucHienTai)) {
                    for (Ban b : entry.getValue()) {
                        total++;
                        String tt = b.getTrangThai();
                        if (TrangThaiBan.TRONG.toString().equals(tt)) trong++;
                        else if (TrangThaiBan.DA_DAT.toString().equals(tt)) daDat++;
                        else coKhach++;
                    }
                }
            }
        }
        lblThongKeBan.setText(String.format("Tổng: %d | Trống: %d | Đã đặt: %d | Có khách: %d", total, trong, daDat, coKhach));
    }

    private void capNhatTablePhieuDat() {
        modelPhieuDat.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        if (danhSachPhieuDatDangHoatDong != null) {
            for (PhieuDatBan p : danhSachPhieuDatDangHoatDong) {
                if ("Đã thanh toán".equals(p.getTrangThaiPhieu()) || "Đã hủy".equals(p.getTrangThaiPhieu())) continue;
                
                String time = p.getThoiGianDenHen() != null ? sdf.format(Date.from(p.getThoiGianDenHen().atZone(ZoneId.systemDefault()).toInstant())) : "N/A";
                modelPhieuDat.addRow(new Object[]{
                    p.getMaPhieu(), 
                    time, 
                    p.getKhachHang() != null ? p.getKhachHang().getTenKH() : "Vãng lai", 
                    p.getBan() != null ? p.getBan().getMaBan() : "", 
                    p.getTrangThaiPhieu()
                });
            }
        }
    }

    private void ganSuKien() {
        btnDatBan.addActionListener(this);
        btnSearchPDB.addActionListener(this);
        btnXemDanhSachPDB.addActionListener(this); 
        btnThanhToan.addActionListener(this); 
        btnHuyDatBan.addActionListener(this); 
        btnTimBanTheoGio.addActionListener(this);
        
        cboFilterKhuVuc.addActionListener(e -> {
            khuVucHienTai = (String) cboFilterKhuVuc.getSelectedItem();
            capNhatHienThiLuoiBan();
            capNhatThongKeBan();
        });
        
        // Auto-refresh when date or shift changes
        datePicker.addPropertyChangeListener("date", e -> locBanTheoThoiGian());
        cboCaLamViec.addActionListener(e -> locBanTheoThoiGian());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnDatBan) xuLyNutDatBan();
        else if (o == btnSearchPDB) timKiemPhieuDat();
        else if (o == btnXemDanhSachPDB) hienThiDanhSachPhieuDat();
        else if (o == btnThanhToan) xuLyThanhToan(); 
        else if (o == btnHuyDatBan) xuLyHuyDatBan();
        else if (o == btnTimBanTheoGio) locBanTheoThoiGian();
    }

    private void xuLyThanhToan() {
        if (banDangChon == null || phieuDangChon == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Xác nhận thanh toán cho bàn " + banDangChon.getMaBan().trim() + "?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            boolean updatePhieu = phieuDatBanDAO.updateTrangThai(phieuDangChon.getMaPhieu(), "Đã thanh toán");
            banDangChon.setTrangThai(TrangThaiBan.TRONG.toString());
            boolean updateBan = banDAO.capNhatBan(banDangChon);
            
            if (updatePhieu && updateBan) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xuLyHuyDatBan() {
        if (phieuDangChon == null || banDangChon == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn hủy phiếu đặt " + phieuDangChon.getMaPhieu() + "?", 
                "Hủy đặt bàn", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            phieuDatBanDAO.updateTrangThai(phieuDangChon.getMaPhieu(), "Đã hủy");
            banDangChon.setTrangThai(TrangThaiBan.TRONG.toString());
            banDAO.capNhatBan(banDangChon);
            refreshData();
        }
    }

    private void goiMon() {
        if (banDangChon == null) return;

        if (TrangThaiBan.fromString(banDangChon.getTrangThai()) == TrangThaiBan.DA_DAT) {
            if (phieuDangChon != null) {
                phieuDatBanDAO.updateTrangThai(phieuDangChon.getMaPhieu(), "Đã đến");
            }
            banDangChon.setTrangThai(TrangThaiBan.CO_KHACH.toString());
            banDAO.capNhatBan(banDangChon);
        }

        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        Frame parentFrame = (parentWindow instanceof Frame) ? (Frame) parentWindow : null;
        
        ChonMon_Dialog dialog = new ChonMon_Dialog(parentFrame, phieuDangChon);
        dialog.setVisible(true);
        refreshData();
    }

    private void locBanTheoThoiGian() {
        capNhatTrangThaiBanTheoThoiGian();
        capNhatHienThiLuoiBan();
        capNhatThongKeBan();
    }

    private LocalDateTime getThoiGianDaChon() {
        Date date = datePicker.getDate();
        Date time = (Date) spinnerGioHenCuThe.getValue();
        if (date == null) date = new Date();
        if (time == null) time = new Date();
        
        Calendar calDate = Calendar.getInstance(); calDate.setTime(date);
        Calendar calTime = Calendar.getInstance(); calTime.setTime(time);
        
        return LocalDateTime.of(
            calDate.get(Calendar.YEAR), 
            calDate.get(Calendar.MONTH) + 1, 
            calDate.get(Calendar.DAY_OF_MONTH),
            calTime.get(Calendar.HOUR_OF_DAY), 
            calTime.get(Calendar.MINUTE)
        );
    }

    private void xuLyChonBan(Ban ban, JPanel clickedCard) {
        if (cardBanDangChon != null && cardBanDangChon instanceof RoundedPanel) {
            ((RoundedPanel) cardBanDangChon).setBackground(COLOR_WHITE);
        }
        
        this.banDangChon = ban;
        this.cardBanDangChon = clickedCard;
        
        if (clickedCard instanceof RoundedPanel) {
            ((RoundedPanel) clickedCard).setBackground(new Color(240, 248, 255));
        }

        lblMaBanValue.setText(ban.getMaBan().trim());
        lblSoChoValue.setText(String.valueOf(ban.getSoCho()));
        
        String khuVuc = "--";
        for (Map.Entry<String, List<Ban>> entry : danhSachBanTheoKhuVuc.entrySet()) {
            if (entry.getValue().stream().anyMatch(b -> b.getMaBan().equals(ban.getMaBan()))) {
                khuVuc = entry.getKey();
                break;
            }
        }
        lblKhuVucValue.setText(khuVuc);
        
        try {
            lblLoaiBanValue.setText(LoaiBan.fromString(ban.getLoaiBan()).getTenHienThi());
        } catch (Exception e) {
            lblLoaiBanValue.setText(ban.getLoaiBan());
        }

        TrangThaiBan ttb = TrangThaiBan.fromString(ban.getTrangThai());
        if (ttb == TrangThaiBan.TRONG) {
            xoaRongFormKhongResetBan();
            btnDatBan.setText("Đặt bàn");
            btnThanhToan.setVisible(false);
            btnHuyDatBan.setVisible(false);
            phieuDangChon = null;
        } else {
            phieuDangChon = phieuDatBanDAO.getPhieuByBan(ban.getMaBan());
            btnDatBan.setText("Gọi món");
            btnThanhToan.setVisible(ttb == TrangThaiBan.CO_KHACH);
            btnHuyDatBan.setVisible(ttb == TrangThaiBan.DA_DAT);
            
            if (phieuDangChon != null) {
                if (phieuDangChon.getKhachHang() != null) {
                    txtTenKhach.setText(phieuDangChon.getKhachHang().getTenKH());
                    txtSdtKhach.setText(phieuDangChon.getKhachHang().getSoDienThoai());
                }
                txtGhiChu.setText(phieuDangChon.getGhiChu());
                cboSoKhach.setSelectedItem(phieuDangChon.getSoNguoi());
                if (phieuDangChon.getThoiGianDenHen() != null) {
                    Date d = Date.from(phieuDangChon.getThoiGianDenHen().atZone(ZoneId.systemDefault()).toInstant());
                    datePicker.setDate(d);
                    spinnerGioHenCuThe.setValue(d);
                }
            }
        }
    }
    
    private void xoaRongFormKhongResetBan() {
        txtTenKhach.setText(""); 
        txtSdtKhach.setText(""); 
        txtGhiChu.setText("");
        cboSoKhach.setSelectedIndex(0);
        datePicker.setDate(new Date());
        spinnerGioHenCuThe.setValue(new Date());
    }

    private void datBanMoi() {
        String ten = txtTenKhach.getText().trim();
        String sdt = txtSdtKhach.getText().trim();
        
        if (ten.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên và SĐT khách!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if ((int)cboSoKhach.getSelectedItem() > banDangChon.getSoCho()) {
            JOptionPane.showMessageDialog(this, "Số khách vượt quá sức chứa của bàn!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            NhanVien nv = new NhanVien_DAO().getNhanVienById("NVTT001"); 
            KhachHang kh = new KhachHang_DAO().getKhachHangBySDT(sdt);
            
            if (kh == null) {
                // Nếu không tìm thấy khách, dùng khách vãng lai hoặc logic thêm mới tùy bạn
                kh = new KhachHang_DAO().getKhachHangById("KH00000000"); 
            }
            
            if (kh == null || nv == null) {
                 JOptionPane.showMessageDialog(this, "Lỗi dữ liệu nhân viên hoặc khách hàng mặc định!");
                 return;
            }

            PhieuDatBan phieu = new PhieuDatBan(
                phieuDatBanDAO.generateNewID(), 
                getThoiGianDaChon(), 
                null, 
                null, 
                kh, 
                nv, 
                banDangChon, 
                (int)cboSoKhach.getSelectedItem(), 
                txtGhiChu.getText(), 
                "Chưa đến"
            );

            if (phieuDatBanDAO.insertPhieuDatBan(phieu)) {
                banDangChon.setTrangThai(TrangThaiBan.DA_DAT.toString());
                banDAO.capNhatBan(banDangChon);
                JOptionPane.showMessageDialog(this, "Đặt bàn thành công!");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Đặt bàn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi hệ thống: " + e.getMessage());
        }
    }

    private void xoaRongFormVaResetBan() {
        xoaRongFormKhongResetBan();
        lblMaBanValue.setText("--"); 
        lblKhuVucValue.setText("--");
        lblLoaiBanValue.setText("--");
        lblSoChoValue.setText("--");
        btnDatBan.setText("Đặt bàn");
        btnThanhToan.setVisible(false); 
        btnHuyDatBan.setVisible(false);
        banDangChon = null; 
        phieuDangChon = null; 
        cardBanDangChon = null;
    }

    private void hienThiDanhSachPhieuDat() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Danh sách phiếu đặt", true);
        dialog.setSize(750, 400);
        dialog.add(new JScrollPane(tblPhieuDat));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void timKiemPhieuDat() {
        String k = txtSearchPDB.getText().trim().toLowerCase();
        if (k.isEmpty()) {
            capNhatTablePhieuDat();
            return;
        }
        
        modelPhieuDat.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        if (danhSachPhieuDatDangHoatDong != null) {
            for (PhieuDatBan p : danhSachPhieuDatDangHoatDong) {
                if (p == null) continue;
                
                String tenKH = p.getKhachHang() != null ? p.getKhachHang().getTenKH() : "Vãng lai";
                String sdt = p.getKhachHang() != null ? p.getKhachHang().getSoDienThoai() : "";
                
                if (p.getMaPhieu().toLowerCase().contains(k) || 
                   tenKH.toLowerCase().contains(k) ||
                   sdt.contains(k)) {
                    
                    if ("Đã thanh toán".equals(p.getTrangThaiPhieu()) || "Đã hủy".equals(p.getTrangThaiPhieu())) continue;
                    
                    String time = p.getThoiGianDenHen() != null ? sdf.format(Date.from(p.getThoiGianDenHen().atZone(ZoneId.systemDefault()).toInstant())) : "N/A";
                    String maBan = p.getBan() != null ? p.getBan().getMaBan() : "";
                    modelPhieuDat.addRow(new Object[]{p.getMaPhieu(), time, tenKH, maBan, p.getTrangThaiPhieu()});
                }
            }
        }
    }

    private void xuLyNutDatBan() {
        if (banDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn trước!");
            return;
        }
        if (TrangThaiBan.fromString(banDangChon.getTrangThai()) == TrangThaiBan.TRONG) {
            datBanMoi();
        } else {
            goiMon();
        }
    }

    // --- CUSTOM COMPONENTS ---

    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color borderColor = MAU_VIEN;
        private Color bgColor;

        public RoundedPanel(int radius, Color color, LayoutManager layout) {
            super(layout);
            this.cornerRadius = radius;
            this.bgColor = color;
            setOpaque(false);
        }

        public void setBorderColor(Color color) { this.borderColor = color; repaint(); }
        public void setBackground(Color color) { this.bgColor = color; repaint(); }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
            g2.dispose();
            super.paintComponent(g);
        }

        @Override protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius));
            g2.dispose();
        }
    }

    private class RoundedButton extends JButton {
        public RoundedButton(String text, Color bg, Color fg) {
            super(text);
            setBackground(bg);
            setForeground(fg);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("Segoe UI", Font.BOLD, 13));
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (getModel().isPressed()) g2.setColor(getBackground().darker());
            else if (getModel().isRollover()) g2.setColor(getBackground().brighter());
            else g2.setColor(getBackground());
            
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
