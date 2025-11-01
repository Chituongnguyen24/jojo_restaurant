package view.Ban;

import dao.Ban_DAO;
import dao.PhieuDatBan_DAO;
import entity.Ban;
import entity.PhieuDatBan;
import enums.TrangThaiBan;

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
    private JButton btnSearchPDB, btnDatBan, btnHuyDatBan, btnRefresh;

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

    // --- Hằng số UI ---
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
    
    private Timer clockTimer; 

    public DatBan_View() {
        banDAO = new Ban_DAO();
        phieuDatBanDAO = new PhieuDatBan_DAO();
        danhSachBanTheoKhuVuc = new LinkedHashMap<>();
        tenKhuVuc = new ArrayList<>();
        danhSachPhieuDatDangHoatDong = new ArrayList<>();

        thietLapGiaoDien();
        ganSuKien(); 
        taiDuLieuVaHienThiBanDau();
    }
    
    // =================================================================================
    // == PHẦN TÁI CẤU TRÚC GIAO DIỆN CHÍNH
    // =================================================================================

    private void thietLapGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(MAU_NEN);

        JPanel pnlHeader = taoPanelHeader();
        add(pnlHeader, BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        splitPane.setDividerLocation(0.65);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);

        // Cột Trái: Chứa ComboBox lọc Khu vực và Lưới Bàn
        JPanel pnlLuuTruBan = taoPanelLuuTruBan();
        JScrollPane scrollLuoiBan = new JScrollPane(pnlLuuTruBan);
        scrollLuoiBan.setBorder(null);
        scrollLuoiBan.getVerticalScrollBar().setUnitIncrement(16);
        
        // Cột Phải: Panel Chức năng Đặt/Tra cứu
        JPanel pnlChucNangDatBan = taoPanelChucNangDatBan();

        splitPane.setLeftComponent(scrollLuoiBan);
        splitPane.setRightComponent(pnlChucNangDatBan);

        add(splitPane, BorderLayout.CENTER);
    }
    
    private JPanel taoPanelLuuTruBan() {
        JPanel pnlTong = new JPanel(new BorderLayout(0, 10));
        pnlTong.setBackground(MAU_TRANG);
        pnlTong.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Thanh chọn Khu vực và Thống kê nhỏ
        JPanel pnlFilterBan = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pnlFilterBan.setOpaque(false);
        
        cboFilterKhuVuc = new JComboBox<>();
        cboFilterKhuVuc.setFont(FONT_CHU);
        cboFilterKhuVuc.setPreferredSize(new Dimension(200, 30));

        pnlFilterBan.add(new JLabel("Khu vực:"));
        pnlFilterBan.add(cboFilterKhuVuc);

        // Lưới Bàn
        pnlLuoiBan = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15)); 
        pnlLuoiBan.setBackground(MAU_TRANG);
        pnlLuoiBan.setOpaque(false);

        pnlTong.add(pnlFilterBan, BorderLayout.NORTH);
        pnlTong.add(pnlLuoiBan, BorderLayout.CENTER);
        
        return pnlTong;
    }
    
    private JPanel taoPanelChucNangDatBan() {
        JPanel pnlDatBan = new JPanel(new BorderLayout(0, 10));
        pnlDatBan.setBackground(MAU_TRANG);
        pnlDatBan.setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Panel Bộ lọc và Tra cứu (NORTH)
        JPanel pnlFilter = taoPanelFilter();
        
        // 2. Panel Danh sách bàn trống và PhieuDat (CENTER)
        JPanel pnlDanhSach = taoPanelDanhSach();

        // 3. Panel Form nhập liệu và Button (SOUTH)
        JPanel pnlForm = taoPanelFormVaButton();
        
        pnlDatBan.add(pnlFilter, BorderLayout.NORTH);
        pnlDatBan.add(pnlDanhSach, BorderLayout.CENTER);
        pnlDatBan.add(pnlForm, BorderLayout.SOUTH);
        
        return pnlDatBan;
    }
    
    private JPanel taoPanelFilter() {
        JPanel pnlFilter = new JPanel(new BorderLayout(0, 15));
        pnlFilter.setOpaque(false);
        
        // Hàng 1: Bộ lọc chính (Số khách, Ngày, Giờ)
        JPanel pnlInput = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlInput.setOpaque(false);
        
        cboSoKhach = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 8, 10, 12, 15});
        cboSoKhach.setFont(FONT_CHU);
        cboSoKhach.setPreferredSize(new Dimension(80, 30));
        
        datePicker = new JDateChooser(new Date());
        datePicker.setDateFormatString("dd/MM/yyyy");
        datePicker.setFont(FONT_CHU);
        datePicker.setPreferredSize(new Dimension(120, 30));
        
        SpinnerDateModel modelGio = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE);
        spinnerGioDat = new JSpinner(modelGio);
        JSpinner.DateEditor editorGio = new JSpinner.DateEditor(spinnerGioDat, "HH:mm");
        spinnerGioDat.setEditor(editorGio);
        spinnerGioDat.setFont(FONT_CHU);
        spinnerGioDat.setPreferredSize(new Dimension(80, 30));

        pnlInput.add(new JLabel("Số lượng khách:"));
        pnlInput.add(cboSoKhach);
        pnlInput.add(new JLabel("Ngày đặt:"));
        pnlInput.add(datePicker);
        pnlInput.add(new JLabel("Giờ đặt:"));
        pnlInput.add(spinnerGioDat);
        
        // Hàng 2: Tra cứu phiếu đặt bàn
        JPanel pnlSearch = new JPanel(new BorderLayout(10, 0));
        pnlSearch.setOpaque(false);

        txtSearchPDB = new JTextField(20);
        txtSearchPDB.setFont(FONT_CHU);
        txtSearchPDB.setBorder(new LineBorder(MAU_VIEN, 1));
        
        btnSearchPDB = taoStyledButton("Tìm kiếm đặt bàn", MAU_CAM_CHINH.darker(), MAU_TRANG);
        btnSearchPDB.setPreferredSize(new Dimension(150, 35));

        pnlSearch.add(txtSearchPDB, BorderLayout.CENTER);
        pnlSearch.add(btnSearchPDB, BorderLayout.EAST);
        
        pnlFilter.add(pnlInput, BorderLayout.NORTH);
        pnlFilter.add(pnlSearch, BorderLayout.SOUTH);

        return pnlFilter;
    }
    
    private JPanel taoPanelDanhSach() {
        JPanel pnlDanhSach = new JPanel(new BorderLayout());
        pnlDanhSach.setOpaque(false);
        pnlDanhSach.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, MAU_VIEN));

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(FONT_TIEUDE_NHO);

        // Tab 1: Danh sách bàn trống phù hợp
        String[] colsBanTrong = {"Mã Bàn", "Khu Vực", "Loại Bàn", "Số Chỗ"};
        modelBanTrong = new DefaultTableModel(colsBanTrong, 0);
        tblBanTrong = new JTable(modelBanTrong);
        JScrollPane scrollBanTrong = new JScrollPane(tblBanTrong);
        tabbedPane.addTab("Chọn bàn trống phù hợp", scrollBanTrong);
        
        // Tab 2: Danh sách phiếu đặt bàn chờ xử lý
        String[] colsPhieuDat = {"Mã PDB", "Giờ Hẹn", "Khách hàng", "Bàn", "Trạng Thái"};
        modelPhieuDat = new DefaultTableModel(colsPhieuDat, 0);
        tblPhieuDat = new JTable(modelPhieuDat);
        JScrollPane scrollPhieuDat = new JScrollPane(tblPhieuDat);
        tabbedPane.addTab("Danh sách đơn đặt bàn", scrollPhieuDat);
        
        pnlDanhSach.add(tabbedPane, BorderLayout.CENTER);
        
        return pnlDanhSach;
    }
    
    private JPanel taoPanelFormVaButton() {
        JPanel pnlForm = new JPanel(new BorderLayout(0, 15));
        pnlForm.setOpaque(false);
        pnlForm.setBorder(new EmptyBorder(10, 0, 0, 0));

        // Form nhập liệu
        JPanel pnlInput = new JPanel(new GridLayout(3, 2, 10, 8));
        pnlInput.setOpaque(false);
        
        txtTenKhach = taoStyledTextField("Họ và tên khách hàng");
        txtSdtKhach = taoStyledTextField("Số điện thoại (tra cứu)");
        txtGhiChu = taoStyledTextField("Ghi chú (yêu cầu)");
        
        pnlInput.add(new JLabel("Họ tên khách:"));
        pnlInput.add(txtTenKhach);
        pnlInput.add(new JLabel("SĐT/Mã KH:"));
        pnlInput.add(txtSdtKhach);
        pnlInput.add(new JLabel("Ghi chú:"));
        pnlInput.add(txtGhiChu);

        // Buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlButtons.setOpaque(false);

        btnHuyDatBan = taoStyledButton("Hủy", MAU_DO, MAU_TRANG);
        btnDatBan = taoStyledButton("Đặt bàn/Ghi nhận", MAU_XANH_LA, MAU_TRANG);
        
        pnlButtons.add(btnHuyDatBan);
        pnlButtons.add(btnDatBan);
        
        pnlForm.add(pnlInput, BorderLayout.NORTH);
        pnlForm.add(pnlButtons, BorderLayout.SOUTH);
        
        return pnlForm;
    }
    
    // --- UI Helper Methods (Đã sửa) ---

    private JPanel taoPanelHeader() {
        JPanel pnlHeader = new JPanel(new BorderLayout(15, 0));
        pnlHeader.setBackground(MAU_TRANG);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, MAU_VIEN),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JPanel pnlLeft = new JPanel();
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setOpaque(false);

        JLabel lblTitle = new JLabel("Quản lý đặt bàn");
        lblTitle.setFont(FONT_TIEUDE_LON);
        lblTitle.setForeground(MAU_CHU_CHINH);

        lblThongKeBan = new JLabel("Đang tải...");
        lblThongKeBan.setFont(FONT_CHU);
        lblThongKeBan.setForeground(MAU_CHU_PHU);
        
        lblDateTime = new JLabel();
        clockTimer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm:ss");
            lblDateTime.setText(sdf.format(new Date()));
        });
        clockTimer.start();

        pnlLeft.add(lblTitle);
        pnlLeft.add(Box.createVerticalStrut(5));
        pnlLeft.add(lblThongKeBan);
        pnlLeft.add(lblDateTime);

        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRight.setOpaque(false);

        btnRefresh = taoStyledButton("🔄 Làm mới", MAU_CAM_NHE, MAU_CAM_CHINH);
        btnRefresh.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(MAU_CAM_CHINH, 1, true),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        
        pnlRight.add(btnRefresh);

        pnlHeader.add(pnlLeft, BorderLayout.WEST);
        pnlHeader.add(pnlRight, BorderLayout.EAST);

        return pnlHeader;
    }
    
    private JPanel taoMucChuThich(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        item.setOpaque(false);

        JPanel circle = new JPanel() {
            @Override
            public Dimension getPreferredSize() { return new Dimension(14, 14); }
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        circle.setBackground(color);
        circle.setOpaque(false);

        JLabel label = new JLabel(text);
        label.setFont(FONT_CHU);
        label.setForeground(MAU_CHU_CHINH);

        item.add(circle);
        item.add(label);
        return item;
    }
    
    private JTextField taoStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(FONT_CHU);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MAU_VIEN, 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
        field.setToolTipText(placeholder);
        return field;
    }
    
    private JButton taoStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_TIEUDE_NHO);
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 35));
        btn.setBorder(new EmptyBorder(5, 10, 5, 10)); 
        return btn;
    }

    private JPanel taoTheBan(Ban ban) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(MAU_TRANG);
        card.setBorder(BorderFactory.createLineBorder(MAU_VIEN, 2, true));
        card.setPreferredSize(new Dimension(180, 200));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Color mauVien, mauNen, mauChu;
        String trangThaiText, iconEmoji;
        
        String trangThaiString = ban.getTrangThai() != null ? ban.getTrangThai().trim() : TrangThaiBan.TRONG.toString();

        if (TrangThaiBan.TRONG.toString().equals(trangThaiString)) {
            mauVien = MAU_TRANGTHAI_TRONG;
            mauNen = new Color(232, 245, 233);
            mauChu = MAU_TRANGTHAI_TRONG.darker();
            trangThaiText = "Trống";
            iconEmoji = "✓";
        } else if (TrangThaiBan.CO_KHACH.toString().equals(trangThaiString)) {
            mauVien = MAU_TRANGTHAI_CO_KHACH;
            mauNen = new Color(255, 235, 238);
            mauChu = MAU_TRANGTHAI_CO_KHACH.darker();
            trangThaiText = "Có khách";
            iconEmoji = "👥";
        } else { // DA_DAT
            mauVien = MAU_TRANGTHAI_DA_DAT;
            mauNen = new Color(255, 248, 225);
            mauChu = MAU_TRANGTHAI_DA_DAT.darker();
            trangThaiText = "Đã đặt";
            iconEmoji = "📅";
        }

        card.setBorder(BorderFactory.createLineBorder(mauVien, 2));

        JLabel lblIcon = new JLabel("🪑");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setForeground(MAU_CHU_PHU);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblName = new JLabel(ban.getMaBan() != null ? ban.getMaBan().trim() : "N/A");
        lblName.setFont(FONT_TIEUDE_CHINH);
        lblName.setForeground(MAU_CHU_CHINH);
        lblName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblInfo = new JLabel(String.format("%s • %d chỗ", ban.getLoaiBan(), ban.getSoCho()));
        lblInfo.setFont(FONT_CHU_NHO);
        lblInfo.setForeground(MAU_CHU_PHU);
        lblInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pnlStatus.setBackground(mauNen);
        pnlStatus.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        pnlStatus.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblStatusText = new JLabel(trangThaiText);
        lblStatusText.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStatusText.setForeground(mauChu);

        pnlStatus.add(new JLabel(iconEmoji));
        pnlStatus.add(lblStatusText);

        card.add(Box.createVerticalStrut(10));
        card.add(lblIcon);
        card.add(Box.createVerticalStrut(8));
        card.add(lblName);
        card.add(Box.createVerticalStrut(4));
        card.add(lblInfo);
        card.add(Box.createVerticalStrut(10));
        card.add(pnlStatus);
        card.add(Box.createVerticalStrut(10));

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1 || ban == null) return;
                JOptionPane.showMessageDialog(null, "Đã click vào bàn: " + ban.getMaBan());
            }
        });

        return card;
    }
    
    // =================================================================================
    // == PHẦN XỬ LÝ DỮ LIỆU VÀ LOGIC
    // =================================================================================
    
    private void taiDuLieuVaHienThiBanDau() {
        taiDuLieuKhuVuc();
        taiDuLieuDatBan();
        dongBoTrangThaiDatBan();
        capNhatCboKhuVuc(); 
        
        if (!tenKhuVuc.isEmpty()) {
            khuVucHienTai = tenKhuVuc.get(0);
            cboFilterKhuVuc.setSelectedItem(khuVucHienTai);
        } else {
             khuVucHienTai = "Không có dữ liệu";
        }
        
        capNhatHienThiLuoiBan();
        capNhatThongKeBan();
        capNhatTablePhieuDat();
    }
    
    public void refreshData() {
        SwingUtilities.invokeLater(this::taiDuLieuVaHienThiBanDau);
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
        cboFilterKhuVuc.removeAllItems();
        cboFilterKhuVuc.addItem("Tất cả"); 
        for(String tenKV : tenKhuVuc) {
            cboFilterKhuVuc.addItem(tenKV);
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
        int totalTables = 0;

        if (danhSachBanTheoKhuVuc != null) {
            for (Map.Entry<String, List<Ban>> entry : danhSachBanTheoKhuVuc.entrySet()) {
                String tenKV = entry.getKey();
                List<Ban> dsBan = entry.getValue();

                if ("Tất cả".equals(khuVucHienTai) || khuVucHienTai.equals(tenKV)) {
                    if (dsBan != null) {
                        for (Ban ban : dsBan) {
                            pnlLuoiBan.add(taoTheBan(ban));
                            totalTables++;
                        }
                    }
                }
            }
        }

        if (totalTables == 0) {
            pnlLuoiBan.add(new JLabel("Không có bàn nào để hiển thị trong khu vực này."));
        }

        pnlLuoiBan.revalidate();
        pnlLuoiBan.repaint();
    }
    
    private void capNhatThongKeBan() {
        int total = 0, trong = 0, daDat = 0, coKhach = 0;
        String filterKV = khuVucHienTai;

        if (danhSachBanTheoKhuVuc != null) {
            for (Map.Entry<String, List<Ban>> entry : danhSachBanTheoKhuVuc.entrySet()) {
                String tenKV = entry.getKey();
                if ("Tất cả".equals(filterKV) || filterKV.equals(tenKV)) {
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

        // Khắc phục lỗi format LocalDateTime bằng cách chuyển đổi an toàn sang Date
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM/yyyy");

        if (danhSachPhieuDatDangHoatDong != null) {
            for (PhieuDatBan phieu : danhSachPhieuDatDangHoatDong) {
                String trangThai = phieu.getTrangThaiPhieu().trim();
                if ("Chưa đến".equals(trangThai) || "Đã đến".equals(trangThai)) {
                    
                    String thoiGianHienThi = "N/A";
                    Object thoiGianObj = phieu.getThoiGianDenHen();
                    
                    if (thoiGianObj != null) {
                        if (thoiGianObj instanceof LocalDateTime) {
                            // Chuyển LocalDateTime sang Date
                            LocalDateTime ldt = (LocalDateTime) thoiGianObj;
                            Date date = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
                            thoiGianHienThi = sdf.format(date);
                        } else if (thoiGianObj instanceof Date) {
                            // Nếu là java.util.Date/Timestamp
                            thoiGianHienThi = sdf.format((Date) thoiGianObj);
                        }
                    }
                    
                    modelPhieuDat.addRow(new Object[]{
                        phieu.getMaPhieu(),
                        thoiGianHienThi,
                        phieu.getKhachHang() != null ? phieu.getKhachHang().getTenKH() : "Khách lẻ",
                        phieu.getBan() != null ? phieu.getBan().getMaBan() : "N/A",
                        phieu.getTrangThaiPhieu()
                    });
                }
            }
        }
    }
    
    // =================================================================================
    // == PHẦN XỬ LÝ SỰ KIỆN
    // =================================================================================
    
    private void ganSuKien() {
        btnDatBan.addActionListener(this);
        btnHuyDatBan.addActionListener(this);
        btnSearchPDB.addActionListener(this);
        btnRefresh.addActionListener(this);
        
        // Sự kiện ComboBox Khu vực
        cboFilterKhuVuc.addActionListener(e -> chuyenKhuVuc((String)cboFilterKhuVuc.getSelectedItem()));
        
        // Sự kiện Lọc bàn trống
        ActionListener filterActionListener = e -> {
            // Logic lọc bàn trống (Chưa triển khai)
            // capNhatTableBanTrongPhuHop();
        };
        cboSoKhach.addActionListener(filterActionListener);
        
        // Listener cho JDateChooser
        datePicker.getDateEditor().addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if ("date".equals(evt.getPropertyName())) {
                    // capNhatTableBanTrongPhuHop();
                }
            }
        });
        
        spinnerGioDat.addChangeListener(e -> {
            // capNhatTableBanTrongPhu Hop();
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        
        if (o == btnRefresh) {
            refreshData();
        } else if (o == btnDatBan) {
            JOptionPane.showMessageDialog(this, "Chức năng Đặt bàn/Ghi nhận đang được phát triển.");
        } else if (o == btnHuyDatBan) {
            xoaRongForm();
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
    }
    
    private void timKiemPhieuDat() {
        String keyword = txtSearchPDB.getText().trim();
        JOptionPane.showMessageDialog(this, "Đang tìm kiếm với từ khóa: " + keyword);
    }
}