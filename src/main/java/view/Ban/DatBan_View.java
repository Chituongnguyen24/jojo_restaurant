package view.Ban;

import dao.Ban_DAO;
import dao.PhieuDatBan_DAO;
import entity.Ban;
import entity.PhieuDatBan;
import enums.TrangThaiBan;
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

import com.toedter.calendar.JDateChooser;

public class DatBan_View extends JPanel implements ActionListener {

    private Ban_DAO banDAO;
    private PhieuDatBan_DAO phieuDatBanDAO;

    // --- Các thành phần UI ---
    private JComboBox<Integer> cboSoKhach;
    private JComboBox<String> cboFilterKhuVuc;
    private JDateChooser datePicker;
    private JSpinner spinnerGioDat;
    private JTextField txtSearchPDB, txtTenKhach, txtSdtKhach, txtGhiChu;
    
    // SỬA: Xóa btnHuyDatBan
    private JButton btnSearchPDB, btnDatBan, btnRefresh;

    private DefaultTableModel modelBanTrong, modelPhieuDat;
    private JTable tblBanTrong, tblPhieuDat;
    private JPanel pnlLuoiBan;
    private JLabel lblThongKeBan;
    private JLabel lblDateTime;

    // --- Dữ liệu ---
    private Map<String, List<Ban>> danhSachBanTheoKhuVuc;
    private List<String> tenKhuVuc;
    private String khuVucHienTai;
    private List<PhieuDatBan> danhSachPhieuDatDangHoatDong;

    private Ban banDangChon = null;
    private PhieuDatBan phieuDangChon = null;

    // --- Hằng số UI ---
    private static final Color BG_VIEW = new Color(251, 248, 241);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color MAU_VIEN = new Color(222, 226, 230);
    private static final Color COLOR_TITLE = new Color(30, 30, 30);
    private static final Color MAU_CAM_CHINH = new Color(255, 152, 0);
    private static final Color MAU_XANH_LA = new Color(76, 175, 80);
    private static final Color MAU_DO = new Color(244, 67, 54);
    private static final Color MAU_XANH_DUONG = new Color(34, 139, 230);
    private static final Color MAU_XAM_NHE = new Color(108, 117, 125);
    private static final Color MAU_TRANGTHAI_TRONG = MAU_XANH_LA;
    private static final Color MAU_TRANGTHAI_CO_KHACH = MAU_DO;
    private static final Color MAU_TRANGTHAI_DA_DAT = MAU_CAM_CHINH;
    private static final Font FONT_TIEUDE_LON = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_TIEUDE_CHINH = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_TIEUDE_NHO = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 14);

    private Timer clockTimer;

    public DatBan_View() {
        banDAO = new Ban_DAO();
        phieuDatBanDAO = new PhieuDatBan_DAO();
        danhSachBanTheoKhuVuc = new LinkedHashMap<>();
        tenKhuVuc = new ArrayList<>();
        danhSachPhieuDatDangHoatDong = new ArrayList<>();

        khoiTaoComponents();
        thietLapGiaoDien();
        ganSuKien();
        taiDuLieuVaHienThiBanDau(true); // Tải lần đầu, reset
    }

    private void khoiTaoComponents() {
        txtSearchPDB = new JTextField(15);
        txtSearchPDB.setToolTipText("Mã PDB, SĐT hoặc Tên khách");

        cboSoKhach = createStyledComboBox();
        for (int i : new Integer[]{1, 2, 3, 4, 5, 6, 8, 10, 12, 15}) {
            cboSoKhach.addItem(i);
        }

        cboFilterKhuVuc = createStyledComboBox();

        datePicker = new JDateChooser(new Date());
        datePicker.setDateFormatString("dd/MM/yyyy");

        SpinnerDateModel modelGio = new SpinnerDateModel(new Date(), null, null, Calendar.MINUTE);
        spinnerGioDat = new JSpinner(modelGio);
        JSpinner.DateEditor editorGio = new JSpinner.DateEditor(spinnerGioDat, "HH:mm");
        spinnerGioDat.setEditor(editorGio);

        txtTenKhach = createStyledTextField("Họ và tên khách hàng");
        txtSdtKhach = createStyledTextField("Số điện thoại (tra cứu)");
        txtGhiChu = createStyledTextField("Ghi chú (yêu cầu)");

        btnSearchPDB = new RoundedButton("Tìm kiếm PDB", MAU_XANH_DUONG, COLOR_WHITE);
        btnSearchPDB.setPreferredSize(new Dimension(150, 35));

        btnDatBan = new RoundedButton("Đặt bàn", MAU_XANH_LA, COLOR_WHITE);

        // SỬA: Xóa btnHuyDatBan
        // btnHuyDatBan = new RoundedButton("Hủy", MAU_DO, COLOR_WHITE);

        btnRefresh = new RoundedButton("🔄 Làm mới", new Color(255, 243, 224), MAU_CAM_CHINH);
        btnRefresh.setPreferredSize(new Dimension(150, 35));

        lblThongKeBan = new JLabel("Đang tải...");
        lblThongKeBan.setFont(FONT_CHU);
        lblThongKeBan.setForeground(MAU_XAM_NHE);

        lblDateTime = new JLabel();
        lblDateTime.setFont(FONT_CHU);
        lblDateTime.setForeground(MAU_XAM_NHE);

        clockTimer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm:ss");
            lblDateTime.setText(sdf.format(new Date()));
        });
        clockTimer.start();
    }

    private void ganSuKien() {
        btnDatBan.addActionListener(this);
        // SỬA: Xóa listener btnHuyDatBan
        // btnHuyDatBan.addActionListener(this);
        btnSearchPDB.addActionListener(this);
        btnRefresh.addActionListener(this);

        cboFilterKhuVuc.addActionListener(e -> {
            String newKhuVuc = (String) cboFilterKhuVuc.getSelectedItem();
            if (newKhuVuc != null && !newKhuVuc.equals(khuVucHienTai)) {
                khuVucHienTai = newKhuVuc;
                capNhatHienThiLuoiBan();
                capNhatThongKeBan();
                capNhatTableBanTrongPhuHop();
            }
        });

        cboSoKhach.addActionListener(e -> capNhatTableBanTrongPhuHop());

        datePicker.getDateEditor().addPropertyChangeListener(evt -> {
            if ("date".equals(evt.getPropertyName())) {
                // Logic
            }
        });

        spinnerGioDat.addChangeListener(e -> {
            // Logic
        });

        tblBanTrong.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblBanTrong.getSelectedRow();
                    if (row != -1) {
                        String maBan = (String) modelBanTrong.getValueAt(row, 0);
                        for (List<Ban> dsBan : danhSachBanTheoKhuVuc.values()) {
                            for (Ban ban : dsBan) {
                                if (ban.getMaBan().trim().equals(maBan)) {
                                    xuLyChonBan(ban);
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        });

        tblPhieuDat.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblPhieuDat.getSelectedRow();
                    if (row != -1) {
                        String maPhieu = (String) modelPhieuDat.getValueAt(row, 0);
                        PhieuDatBan phieu = phieuDatBanDAO.getPhieuDatBanById(maPhieu);
                        if (phieu != null && phieu.getBan() != null) {
                            xuLyChonBan(phieu.getBan());
                        }
                    }
                }
            }
        });
    }


    private void thietLapGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(BG_VIEW);
        JPanel pnlHeader = taoPanelHeader();
        add(pnlHeader, BorderLayout.NORTH);
        JSplitPane pnlContent = taoPanelNoiDungChinh();
        add(pnlContent, BorderLayout.CENTER);
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
        pnlRight.add(btnRefresh);
        JPanel pnlTitleBar = new JPanel(new BorderLayout());
        pnlTitleBar.setBackground(BG_VIEW);
        pnlTitleBar.add(pnlLeft, BorderLayout.WEST);
        pnlTitleBar.add(pnlRight, BorderLayout.EAST);
        panelHeaderWrapper.add(pnlTitleBar, BorderLayout.NORTH);
        JPanel pnlLoc = taoPanelTimKiem();
        panelHeaderWrapper.add(pnlLoc, BorderLayout.CENTER);
        return panelHeaderWrapper;
    }

    private JPanel taoPanelTimKiem() {
        JPanel pnlSearch = new RoundedPanel(15, COLOR_WHITE, new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnlSearch.setBorder(new EmptyBorder(10, 15, 10, 15));
        JComponent[] filterComponents = {txtSearchPDB, (JComponent) cboSoKhach, (JComponent) cboFilterKhuVuc, datePicker, (JComponent) spinnerGioDat};
        for (JComponent comp : filterComponents) {
            if (comp instanceof JTextField) {
                ((JTextField) comp).setFont(FONT_CHU);
                ((JTextField) comp).setBackground(COLOR_WHITE);
            } else if (comp instanceof JComboBox) {
                ((JComboBox<?>) comp).setFont(FONT_CHU);
                ((JComboBox<?>) comp).setBackground(COLOR_WHITE);
            } else if (comp instanceof JDateChooser) {
                ((JDateChooser) comp).setFont(FONT_CHU);
            } else if (comp instanceof JSpinner) {
                ((JSpinner) comp).setFont(FONT_CHU);
            }
            comp.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(MAU_VIEN, 1),
                    new EmptyBorder(5, 10, 5, 10)
            ));
        }
        pnlSearch.add(new JLabel("Tìm kiếm PDB:"));
        pnlSearch.add(txtSearchPDB);
        pnlSearch.add(new JLabel("Số khách:"));
        pnlSearch.add(cboSoKhach);
        pnlSearch.add(new JLabel("Khu vực:"));
        pnlSearch.add(cboFilterKhuVuc);
        pnlSearch.add(new JLabel("Ngày đặt:"));
        pnlSearch.add(datePicker);
        pnlSearch.add(new JLabel("Giờ đặt:"));
        pnlSearch.add(spinnerGioDat);
        return pnlSearch;
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
        pnlFilterBan.add(taoMucChuThich("Trống", MAU_TRANGTHAI_TRONG));
        pnlFilterBan.add(taoMucChuThich("Đã đặt", MAU_TRANGTHAI_DA_DAT));
        pnlFilterBan.add(taoMucChuThich("Có khách", MAU_TRANGTHAI_CO_KHACH));
        pnlWrapper.add(pnlFilterBan, BorderLayout.SOUTH);
        pnlLuoiBan = new JPanel(new GridLayout(0, 3, 12, 12));
        pnlLuoiBan.setBackground(BG_VIEW);
        pnlLuoiBan.setBorder(new EmptyBorder(15, 15, 15, 15));
        pnlLuoiBan.setOpaque(false);
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
        RoundedPanel pnlForm = new RoundedPanel(20, COLOR_WHITE, new BorderLayout());
        pnlForm.setPreferredSize(new Dimension(320, 450));
        pnlForm.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel lblFormTitle = new JLabel("Chức năng Đặt/Tra cứu");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblFormTitle.setForeground(COLOR_TITLE);
        pnlForm.add(lblFormTitle, BorderLayout.NORTH);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        String[] colsBanTrong = {"Mã Bàn", "Khu Vực", "Loại Bàn", "Số Chỗ"};
        modelBanTrong = new DefaultTableModel(colsBanTrong, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBanTrong = taoStyledTable(modelBanTrong);
        tblBanTrong.setRowHeight(30);
        JScrollPane scrollBanTrong = new JScrollPane(tblBanTrong);
        scrollBanTrong.setBorder(new LineBorder(MAU_VIEN, 1));
        tabbedPane.addTab("Bàn trống phù hợp", scrollBanTrong);
        String[] colsPhieuDat = {"Mã PDB", "Giờ Hẹn", "Khách hàng", "Bàn", "Trạng Thái"};
        modelPhieuDat = new DefaultTableModel(colsPhieuDat, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPhieuDat = taoStyledTable(modelPhieuDat);
        tblPhieuDat.setRowHeight(30);
        JScrollPane scrollPhieuDat = new JScrollPane(tblPhieuDat);
        scrollPhieuDat.setBorder(new LineBorder(MAU_VIEN, 1));
        tabbedPane.addTab("Đơn đặt bàn", scrollPhieuDat);
        pnlForm.add(tabbedPane, BorderLayout.CENTER);
        JPanel pnlInput = new JPanel(new GridLayout(3, 2, 8, 8));
        pnlInput.setOpaque(false);
        pnlInput.setBorder(new EmptyBorder(12, 0, 12, 0));
        pnlInput.add(taoFormLabel("Họ tên khách:"));
        pnlInput.add(txtTenKhach);
        pnlInput.add(taoFormLabel("SĐT/Mã KH:"));
        pnlInput.add(txtSdtKhach);
        pnlInput.add(taoFormLabel("Ghi chú:"));
        pnlInput.add(txtGhiChu);
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pnlButton.setOpaque(false);
        
        // SỬA: Xóa btnHuyDatBan
        // pnlButton.add(btnHuyDatBan);
        pnlButton.add(btnDatBan);
        
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setOpaque(false);
        pnlBottom.add(pnlInput, BorderLayout.NORTH);
        pnlBottom.add(pnlButton, BorderLayout.SOUTH);
        pnlForm.add(pnlBottom, BorderLayout.SOUTH);
        return pnlForm;
    }

    private JLabel taoFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(COLOR_TITLE);
        return label;
    }

    private JTextField createInputText(boolean editable) {
        JTextField field = new JTextField();
        field.setEditable(editable);
        field.setFont(FONT_CHU);
        field.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(MAU_VIEN, 1),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JPanel taoMucChuThich(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        item.setOpaque(false);
        JPanel circle = new JPanel() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(14, 14);
            }
            @Override
            protected void paintComponent(Graphics g) {
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
        JTextField field = createInputText(true);
        field.setToolTipText(placeholder);
        return field;
    }

    private <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> cbo = new JComboBox<>();
        cbo.setFont(FONT_CHU);
        cbo.setBackground(COLOR_WHITE);
        cbo.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(MAU_VIEN, 1),
                new EmptyBorder(5, 5, 5, 5)
        ));
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

    // ===== HÀM ĐÃ SỬA (thay thế hàm cũ) =====
    private JPanel taoTheBan(Ban ban) {
        RoundedPanel card = new RoundedPanel(20, COLOR_WHITE, new BorderLayout(10, 0));
        card.setBorder(new EmptyBorder(8, 8, 8, 12));
        card.setPreferredSize(new Dimension(250, 85));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        TrangThaiBan ttb = TrangThaiBan.fromString(ban.getTrangThai());
        Color mauVien = ttb.getColor();
        card.setBorderColor(mauVien);
     
        // SỬA: Gọi helper an toàn để tải icon
        JLabel lblIcon = createBanIconLabel(ban); // Sửa: đổi tên biến
        card.add(lblIcon, BorderLayout.WEST); // Sửa: đổi tên biến
        
        // Phần còn lại của hàm giữ nguyên...
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        String loaiBanRaw = ban.getLoaiBan();
        String loaiBanTen;
        try {
            loaiBanTen = LoaiBan.fromString(loaiBanRaw).getTenHienThi();
        } catch (IllegalArgumentException e) {
            loaiBanTen = loaiBanRaw;
        }
        if (loaiBanTen.equalsIgnoreCase("Bàn VIP")) {
            loaiBanTen = "VIP";
        }
        JLabel lblName = new JLabel(String.format("%s (%s)", ban.getMaBan().trim(), loaiBanTen));
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblName.setForeground(COLOR_TITLE);
        JLabel lblCapacity = new JLabel(ban.getSoCho() + " chỗ");
        lblCapacity.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCapacity.setForeground(new Color(220, 0, 0));
        JLabel lblTrangThai = new JLabel(ttb.getTenHienThi());
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTrangThai.setForeground(ttb.getColor());
        infoPanel.add(lblName);
        infoPanel.add(Box.createVerticalGlue());
        infoPanel.add(lblCapacity);
        infoPanel.add(Box.createVerticalStrut(3));
        infoPanel.add(lblTrangThai);
        card.add(infoPanel, BorderLayout.CENTER);
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    xuLyChonBan(ban);
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(245, 245, 245));
                card.setBorderColor(new Color(100, 150, 255));
                card.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(COLOR_WHITE);
                card.setBorderColor(mauVien);
                card.repaint();
            }
        });
        return card;
    }
    
    // ===== HÀM MỚI (Helper tải ảnh) =====
    /**
     * [HÀM MỚI] Helper: Tải icon cho bàn một cách an toàn
     * (Sử dụng 1 icon 'thongthuong.png' đồng bộ, nếu lỗi fallback về '🍽️')
     */
    private JLabel createBanIconLabel(Ban ban) {  
        String iconPath = "images/icon/thongthuong.png";
        java.net.URL imgURL = getClass().getClassLoader().getResource(iconPath);
        JLabel lblIcon;

        if (imgURL != null) {
            
            ImageIcon iconBan = new ImageIcon(imgURL); 
            lblIcon = new JLabel(iconBan);
            lblIcon.setPreferredSize(new Dimension(60, 60));
        } else {
            lblIcon = new JLabel("🍽️");
            lblIcon.setPreferredSize(new Dimension(60, 60));
            lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            lblIcon.setForeground(MAU_XAM_NHE);
        }

        // Code chung cho cả 2 trường hợp
        lblIcon.setOpaque(false);
        lblIcon.setHorizontalAlignment(SwingConstants.CENTER);
        return lblIcon;
    }
    
    // =================================================================================
    // == PHẦN XỬ LÝ DỮ LIỆU VÀ LOGIC
    // =================================================================================

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
        capNhatTableBanTrongPhuHop();
    }
    
    private void taiDuLieuVaHienThiBanDau() {
         taiDuLieuVaHienThiBanDau(false);
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> taiDuLieuVaHienThiBanDau(false));
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
            if (danhSachPhieuDatDangHoatDong == null) {
                danhSachPhieuDatDangHoatDong = new ArrayList<>();
            }
        } catch (Exception e) {
            danhSachPhieuDatDangHoatDong = new ArrayList<>();
        }
    }

    private void dongBoTrangThaiDatBan() {
        Set<String> maBanDaDat = new HashSet<>();
        if (danhSachPhieuDatDangHoatDong != null) {
            for (PhieuDatBan phieu : danhSachPhieuDatDangHoatDong) {
                if (phieu != null && phieu.getBan() != null && phieu.getBan().getMaBan() != null && "Chưa đến".equals(phieu.getTrangThaiPhieu())) {
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
                            if (!TrangThaiBan.CO_KHACH.toString().equals(ban.getTrangThai().trim())) {
                                if (maBanDaDat.contains(maBanHienTai)) {
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
    }

    private void capNhatCboKhuVuc() {
        ActionListener[] listeners = cboFilterKhuVuc.getActionListeners();
        for (ActionListener l : listeners) {
            cboFilterKhuVuc.removeActionListener(l);
        }
        cboFilterKhuVuc.removeAllItems();
        cboFilterKhuVuc.addItem("Tất cả");
        for (String tenKV : tenKhuVuc) {
            cboFilterKhuVuc.addItem(tenKV);
        }
        for (ActionListener l : listeners) {
            cboFilterKhuVuc.addActionListener(l);
        }
    }

    private void chuyenKhuVuc(String tenKV) {
        if (tenKV != null) {
            khuVucHienTai = tenKV;
            capNhatHienThiLuoiBan();
            capNhatThongKeBan();
        }
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
            pnlLuoiBan.setLayout(new GridLayout(0, 3, 12, 12));
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
                if ("Tất cả".equals(filterKV) || filterKV != null && filterKV.equals(tenKV)) {
                    List<Ban> dsBan = entry.getValue();
                    if (dsBan != null) {
                        for (Ban ban : dsBan) {
                            total++;
                            String trangThai = ban.getTrangThai().trim();
                            if (TrangThaiBan.TRONG.toString().equals(trangThai)) {
                                trong++;
                            } else if (TrangThaiBan.DA_DAT.toString().equals(trangThai)) {
                                daDat++;
                            } else if (TrangThaiBan.CO_KHACH.toString().equals(trangThai)) {
                                coKhach++;
                            }
                        }
                    }
                }
            }
        }
        String labelKV = "Tất cả khu vực";
        if (!"Tất cả".equals(filterKV) && filterKV != null) {
            labelKV = filterKV;
        }
        lblThongKeBan.setText(String.format(
                "%s | Tổng số bàn: %d (Trống: %d, Đã đặt: %d, Có khách: %d)",
                labelKV, total, trong, daDat, coKhach
        ));
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
                    modelPhieuDat.addRow(new Object[]{
                            phieu.getMaPhieu(),
                            thoiGianHienThi,
                            phieu.getKhachHang() != null ? phieu.getKhachHang().getTenKH() : "Khách lẻ",
                            phieu.getBan() != null ? phieu.getBan().getMaBan().trim() : "N/A",
                            phieu.getTrangThaiPhieu()
                    });
                }
            }
        }
    }

    // =================================================================================
    // == PHẦN XỬ LÝ SỰ KIỆN
    // =================================================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o == btnRefresh) {
            refreshData(); 
        } else if (o == btnDatBan) {
            xuLyNutDatBan();
        } else if (o == btnSearchPDB) {
            timKiemPhieuDat();
        }
    }

    private void xoaRongForm() {
        txtTenKhach.setText("");
        txtSdtKhach.setText("");
        txtGhiChu.setText("");
        cboSoKhach.setSelectedIndex(0);
        datePicker.setDate(new Date());
        spinnerGioDat.setValue(new Date());
        tblBanTrong.clearSelection();
        tblPhieuDat.clearSelection();
        txtSearchPDB.setText("");
        btnDatBan.setText("Đặt bàn");
        phieuDangChon = null; 
    }


    private void timKiemPhieuDat() {
        String keyword = txtSearchPDB.getText().trim();
        JOptionPane.showMessageDialog(this, "Đang tìm kiếm Phiếu Đặt Bàn với từ khóa: " + keyword);
    }

    private void xuLyChonBan(Ban ban) {
        this.banDangChon = ban; // Set bàn
        TrangThaiBan trangThai = TrangThaiBan.fromString(ban.getTrangThai());

        if (trangThai == TrangThaiBan.DA_DAT) {
            xoaRongForm();
            phieuDangChon = phieuDatBanDAO.getPhieuByBan(ban.getMaBan());
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
            btnDatBan.setText("Gọi món");
            JOptionPane.showMessageDialog(this,
                    String.format("Bàn %s đã được đặt! \nĐã tải thông tin phiếu: %s",
                            ban.getMaBan().trim(), (phieuDangChon != null ? phieuDangChon.getMaPhieu() : "N/A")),
                    "Bàn đã đặt",
                    JOptionPane.INFORMATION_MESSAGE);

        } else if (trangThai == TrangThaiBan.CO_KHACH) {
            xoaRongForm();
            this.phieuDangChon = phieuDatBanDAO.getPhieuByBan(ban.getMaBan());
            btnDatBan.setText("Gọi món");
            JOptionPane.showMessageDialog(this,
                    String.format("Bàn %s đang phục vụ khách!\nBạn có thể gọi thêm món hoặc thanh toán.",
                            ban.getMaBan().trim()),
                    "Bàn đang sử dụng",
                    JOptionPane.INFORMATION_MESSAGE);

        } else {
            // Bàn TRỐNG → KHÔNG XÓA RỖNG FORM
            phieuDangChon = null;
            btnDatBan.setText("Đặt bàn");
            // SỬA: Xóa pop-up khi chọn bàn trống
//            JOptionPane.showMessageDialog(this,
//                    String.format("Đã chọn bàn %s (Trống).\nVui lòng xác nhận thông tin và nhấn 'Đặt bàn'.",
//                            ban.getMaBan().trim()),
//                    "Chọn bàn trống",
//                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void xuLyNutDatBan() {
        if (banDangChon == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một bàn trước!",
                    "Lỗi", JOptionPane.WARNING_MESSAGE);
            return;
        }
        TrangThaiBan trangThai = TrangThaiBan.fromString(banDangChon.getTrangThai());
        if (trangThai == TrangThaiBan.TRONG) {
            datBanMoi();
        } else {
            goiMon();
        }
    }

    private void datBanMoi() {
        String tenKhach = txtTenKhach.getText().trim();
        String sdtKhach = txtSdtKhach.getText().trim();
        int soNguoi = (int) cboSoKhach.getSelectedItem();
        String ghiChu = txtGhiChu.getText().trim();

        if (tenKhach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtTenKhach.requestFocus();
            return;
        }
        if (sdtKhach.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            txtSdtKhach.requestFocus();
            return;
        }
        if (!sdtKhach.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ! Vui lòng nhập 10 chữ số.", "Lỗi", JOptionPane.WARNING_MESSAGE);
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
        LocalDateTime thoiGianHen = calNgay.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        if (thoiGianHen.isBefore(LocalDateTime.now().minusMinutes(5))) {
            int confirm = JOptionPane.showConfirmDialog(this, "Thời gian hẹn đã qua!\nBạn có muốn đặt luôn với thời gian hiện tại không?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                thoiGianHen = LocalDateTime.now();
            } else {
                return;
            }
        }

        KhachHang_DAO khachHangDAO = new KhachHang_DAO();
        KhachHang khachHang = khachHangDAO.getKhachHangBySDT(sdtKhach);
        if (khachHang == null) {
            khachHang = khachHangDAO.getKhachHangById("KH00000000");
            if (khachHang == null) {
                JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy khách hàng vãng lai trong hệ thống!", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String ghiChuFull = String.format("Khách: %s - SĐT: %s", tenKhach, sdtKhach);
            if (!ghiChu.isEmpty()) {
                ghiChuFull += ". Ghi chú: " + ghiChu;
            }
            ghiChu = ghiChuFull;
        }

        String maPhieuMoi = phieuDatBanDAO.generateNewID();
        NhanVien_DAO nhanVienDAO = new NhanVien_DAO();
        NhanVien nhanVien = nhanVienDAO.getNhanVienById("NVTT001"); // TODO: Lấy từ session
        if (nhanVien == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy thông tin nhân viên (NVTT001)!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        PhieuDatBan phieuMoi = new PhieuDatBan(maPhieuMoi, thoiGianHen, null, null, khachHang, nhanVien, banDangChon, soNguoi, ghiChu, "Chưa đến");
        boolean success = phieuDatBanDAO.insertPhieuDatBan(phieuMoi);

        if (success) {
            banDangChon.setTrangThai(TrangThaiBan.DA_DAT.name());
            banDAO.capNhatBan(banDangChon); 

            refreshData();

            int goiMonLuon = JOptionPane.showConfirmDialog(this,
                    String.format("Đặt bàn thành công!\n\nMã phiếu: %s\nBàn: %s\nKhách: %s\n\nBạn có muốn gọi món ngay không?",
                            maPhieuMoi, banDangChon.getMaBan().trim(), tenKhach),
                    "Đặt bàn thành công",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (goiMonLuon == JOptionPane.YES_OPTION) {
                this.phieuDangChon = phieuDatBanDAO.getPhieuDatBanById(maPhieuMoi);
                goiMon();
            } else {
                xoaRongForm();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi đặt bàn! Vui lòng thử lại.", "Lỗi CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void goiMon() {
        if (phieuDangChon == null) {
            phieuDangChon = phieuDatBanDAO.getPhieuByBan(banDangChon.getMaBan());
        }
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
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        if (owner == null) {
            owner = JOptionPane.getRootFrame();
        }
        view.ThucDon.ChonMon_Dialog chonMonDialog = new view.ThucDon.ChonMon_Dialog(owner, phieuDangChon);
        chonMonDialog.setVisible(true);

        refreshData();

        int tiepTuc = JOptionPane.showConfirmDialog(this, "Đã gọi món thành công!\n\nBạn có muốn gọi thêm món không?", "Gọi món thành công", JOptionPane.YES_NO_OPTION);
        if (tiepTuc == JOptionPane.YES_OPTION) {
            goiMon();
        } else {
            int thanhToan = JOptionPane.showConfirmDialog(this, "Bạn có muốn chuyển sang thanh toán không?", "Thanh toán", JOptionPane.YES_NO_OPTION);
            if (thanhToan == JOptionPane.YES_OPTION) {
                xuLyThanhToan();
            } else {
                xoaRongForm();
            }
        }
    }

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

        String maNV = "NVTT001"; // TODO: Lấy mã NV từ session
        if (phieuDangChon.getNhanVien() != null) {
            maNV = phieuDangChon.getNhanVien().getMaNhanVien();
        }
        view.Ban.ChiTietPhieuDatBan_View chiTietView = new view.Ban.ChiTietPhieuDatBan_View(
                banDangChon,
                maNV,
                () -> {
                    refreshData();
                    dialog.dispose();
                }
        );
        dialog.setContentPane(chiTietView);
        dialog.setVisible(true);
        xoaRongForm();
    }

    private void capNhatTableBanTrongPhuHop() {
        modelBanTrong.setRowCount(0);
        int soKhach = (int) cboSoKhach.getSelectedItem();
        String khuVuc = (String) cboFilterKhuVuc.getSelectedItem();
        if (danhSachBanTheoKhuVuc != null) {
            for (Map.Entry<String, List<Ban>> entry : danhSachBanTheoKhuVuc.entrySet()) {
                String tenKV = entry.getKey();
                if (!"Tất cả".equals(khuVuc) && !khuVuc.equals(tenKV)) {
                    continue;
                }
                List<Ban> dsBan = entry.getValue();
                if (dsBan != null) {
                    for (Ban ban : dsBan) {
                        if (TrangThaiBan.TRONG.name().equals(ban.getTrangThai())
                                && ban.getSoCho() >= soKhach) {
                            modelBanTrong.addRow(new Object[]{
                                    ban.getMaBan().trim(),
                                    tenKV,
                                    ban.getLoaiBan(),
                                    ban.getSoCho()
                            });
                        }
                    }
                }
            }
        }
    }

    // =================================================================================
    // == LỚP HỖ TRỢ UI ĐỒNG BỘ
    // =================================================================================
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
        private int doBoGoc;
        private Color bg;
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