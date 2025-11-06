package view.Ban; 

import dao.Ban_DAO;
import dao.HoaDon_DAO;
import dao.PhieuDatBan_DAO;
import dao.KhachHang_DAO;
import dao.NhanVien_DAO;
import entity.Ban;
import entity.HoaDon;
import entity.PhieuDatBan;
import entity.KhachHang;
import entity.NhanVien;
import enums.TrangThaiBan;
import enums.LoaiBan;
import view.ThucDon.ChonMon_Dialog;

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; 
import java.util.concurrent.ExecutionException;
import java.awt.geom.RoundRectangle2D;

/**
 * View quản lý khách vãng lai (ăn trực tiếp)
 * Giao diện đồng bộ với DatBan_View
 */
public class AnTrucTiep_View extends JPanel implements ActionListener {

    // DAOs
    private final Ban_DAO banDAO;
    private final HoaDon_DAO hoaDonDAO;
    private final PhieuDatBan_DAO phieuDatBanDAO;
    private final KhachHang_DAO khachHangDAO;
    private final NhanVien_DAO nhanVienDAO;
    
    // Components
    private final JPanel pnlLuoiBan;
    private final JLabel lblThongKeBan;
    private final JLabel lblDateTime;
    private final JButton btnRefresh;
    private final JComboBox<String> cboFilterKhuVuc;
    
    private DefaultTableModel modelBanCoKhach;
    private JTable tblBanCoKhach;
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm dd/MM/yy");

    // Data
    private Map<String, List<Ban>> danhSachBanTheoKhuVuc = new LinkedHashMap<>();
    private List<PhieuDatBan> danhSachPhieuDatDangHoatDong = new ArrayList<>();
    private String khuVucHienTai = "Tất cả";

    // UI constants
    private static final Color BG_VIEW = new Color(251, 248, 241);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color MAU_VIEN = new Color(222, 226, 230);
    private static final Color COLOR_TITLE = new Color(30, 30, 30);
    private static final Color MAU_CAM_CHINH = new Color(255, 152, 0);
    private static final Color MAU_XANH_LA = new Color(76, 175, 80);
    private static final Color MAU_DO = new Color(244, 67, 54);
    private static final Color MAU_XAM_NHE = new Color(108, 117, 125);
    private static final Font FONT_TIEUDE_LON = new Font("Segoe UI", Font.BOLD, 26);
    private static final Font FONT_TIEUDE_CHINH = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 14);

    private final Timer clockTimer;

    private class BackgroundData {
        final Map<String, List<Ban>> banTheoKhuVuc;
        final List<PhieuDatBan> phieuDatDangHoatDong;
        
        BackgroundData(Map<String, List<Ban>> ban, List<PhieuDatBan> phieu) {
            this.banTheoKhuVuc = (ban != null) ? ban : new LinkedHashMap<>();
            this.phieuDatDangHoatDong = (phieu != null) ? phieu : new ArrayList<>();
        }
    }

    public AnTrucTiep_View() {
        banDAO = new Ban_DAO();
        hoaDonDAO = new HoaDon_DAO();
        phieuDatBanDAO = new PhieuDatBan_DAO();
        khachHangDAO = new KhachHang_DAO();
        nhanVienDAO = new NhanVien_DAO();

        lblThongKeBan = new JLabel("Đang tải...");
        lblThongKeBan.setFont(FONT_CHU);
        lblThongKeBan.setForeground(MAU_XAM_NHE);

        lblDateTime = new JLabel();
        lblDateTime.setFont(FONT_CHU);
        lblDateTime.setForeground(MAU_XAM_NHE);
        
        btnRefresh = new RoundedButton("Làm mới", new Color(255, 243, 224), MAU_CAM_CHINH);
        btnRefresh.setPreferredSize(new Dimension(150, 35));
        
        cboFilterKhuVuc = createStyledComboBox();

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
        loadDataAsync(true);
    }
    
    private void loadDataAsync(boolean resetKhuVuc) {
        pnlLuoiBan.removeAll();
        pnlLuoiBan.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lblLoading = new JLabel("Đang tải dữ liệu sơ đồ bàn, vui lòng chờ...");
        lblLoading.setFont(FONT_CHU);
        pnlLuoiBan.add(lblLoading);
        pnlLuoiBan.revalidate();
        pnlLuoiBan.repaint();
        lblThongKeBan.setText("Đang tải...");

        String khuVucDaChon = (String) cboFilterKhuVuc.getSelectedItem();

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

                    capNhatTrangThaiBanTheoThoiGian();
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
                    capNhatTableBanCoKhach();

                } catch (Exception e) {
                    e.printStackTrace();
                    pnlLuoiBan.removeAll();
                    pnlLuoiBan.add(new JLabel("Lỗi khi tải dữ liệu: " + e.getMessage()));
                    pnlLuoiBan.revalidate();
                    pnlLuoiBan.repaint();
                    lblThongKeBan.setText("Tải dữ liệu thất bại!");
                }
            }
        };
        worker.execute();
    }

    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            loadDataAsync(false);
        });
    }

    private void thietLapGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(BG_VIEW);
        setBorder(new EmptyBorder(0,0,0,0));
        add(taoPanelHeader(), BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(taoPanelDanhSachBan());
        splitPane.setRightComponent(taoPanelDanhSachThanhToan());
        splitPane.setDividerLocation(850);
        splitPane.setResizeWeight(0.75);
        splitPane.setBorder(null);
        splitPane.setBackground(BG_VIEW);

        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel taoPanelHeader() {
        JPanel panelHeaderWrapper = new JPanel(new BorderLayout(0, 15));
        panelHeaderWrapper.setBackground(BG_VIEW);
        panelHeaderWrapper.setBorder(new EmptyBorder(20, 30, 0, 30));

        // Left panel
        JPanel pnlLeft = new JPanel();
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Khách vào bàn (Ăn tại chỗ)");
        lblTitle.setFont(FONT_TIEUDE_LON);
        lblTitle.setForeground(COLOR_TITLE);
        
        pnlLeft.add(lblTitle);
        pnlLeft.add(Box.createVerticalStrut(5));
        pnlLeft.add(lblThongKeBan);
        pnlLeft.add(lblDateTime);
        
        // Right panel
        JPanel pnlRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlRight.setOpaque(false);
        pnlRight.add(new JLabel("Khu vực:"));
        pnlRight.add(cboFilterKhuVuc);
        pnlRight.add(btnRefresh);

        JPanel pnlTitleBar = new JPanel(new BorderLayout());
        pnlTitleBar.setBackground(BG_VIEW);
        pnlTitleBar.add(pnlLeft, BorderLayout.WEST);
        pnlTitleBar.add(pnlRight, BorderLayout.EAST);

        panelHeaderWrapper.add(pnlTitleBar, BorderLayout.NORTH);
        return panelHeaderWrapper;
    }

    private JPanel taoPanelDanhSachBan() {
        JPanel pnlWrapper = new JPanel(new BorderLayout());
        pnlWrapper.setOpaque(false);
        pnlWrapper.setBorder(new EmptyBorder(10, 30, 30, 10));
        
        JLabel lblMapTitle = new JLabel("Sơ đồ bàn - Click vào bàn để thao tác");
        lblMapTitle.setFont(FONT_TIEUDE_CHINH);
        lblMapTitle.setForeground(COLOR_TITLE);
        pnlWrapper.add(lblMapTitle, BorderLayout.NORTH);

        JPanel pnlFilterBan = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        pnlFilterBan.setOpaque(false);
        pnlFilterBan.setBorder(new EmptyBorder(10, 0, 10, 0));
        pnlFilterBan.add(taoMucChuThich("Trống (Click để mở bàn)", MAU_XANH_LA));
        pnlFilterBan.add(taoMucChuThich("Đã đặt (Vào 'Quản lý đặt bàn')", MAU_CAM_CHINH));
        pnlFilterBan.add(taoMucChuThich("Có khách (Click để gọi món/thanh toán)", MAU_DO));
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

    private JPanel taoPanelDanhSachThanhToan() {
        JPanel pnlWrapper = new JPanel(new BorderLayout());
        pnlWrapper.setOpaque(false);
        pnlWrapper.setBorder(new EmptyBorder(10, 10, 30, 30));

        JLabel lblTitle = new JLabel("Bàn đang có khách (Double-click để thanh toán)");
        lblTitle.setFont(FONT_TIEUDE_CHINH);
        lblTitle.setForeground(COLOR_TITLE);
        lblTitle.setBorder(new EmptyBorder(0, 0, 20, 0));
        pnlWrapper.add(lblTitle, BorderLayout.NORTH);

        modelBanCoKhach = new DefaultTableModel(new String[]{"Mã Bàn", "Khu Vực", "Giờ Vào", "Khách Hàng"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tblBanCoKhach = new JTable(modelBanCoKhach);
        
        tblBanCoKhach.setRowHeight(35);
        tblBanCoKhach.setFont(FONT_CHU);
        tblBanCoKhach.setSelectionBackground(new Color(230, 240, 255));
        tblBanCoKhach.setGridColor(MAU_VIEN);
        tblBanCoKhach.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tblBanCoKhach.getTableHeader().setBackground(new Color(248, 249, 250));
        tblBanCoKhach.getTableHeader().setForeground(COLOR_TITLE);
        tblBanCoKhach.getTableHeader().setPreferredSize(new Dimension(tblBanCoKhach.getTableHeader().getWidth(), 40));
        
        tblBanCoKhach.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tblBanCoKhach.getSelectedRow();
                    if (row == -1) return;
                    
                    String maBan = (String) modelBanCoKhach.getValueAt(row, 0);
                    Ban ban = timBanTuMa(maBan);
                    if (ban != null) {
                        xuLyThanhToan(ban);
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblBanCoKhach);
        scroll.setBorder(BorderFactory.createLineBorder(MAU_VIEN));
        pnlWrapper.add(scroll, BorderLayout.CENTER);
        
        return pnlWrapper;
    }

    private void ganSuKien() {
        btnRefresh.addActionListener(this);
        
        cboFilterKhuVuc.addActionListener(e -> {
            String newKhuVuc = (String) cboFilterKhuVuc.getSelectedItem();
            if (newKhuVuc != null && !newKhuVuc.equals(khuVucHienTai)) {
                khuVucHienTai = newKhuVuc;
                capNhatHienThiLuoiBan();
                capNhatThongKeBan();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o == btnRefresh) {
            refreshData();
        }
    }

    // =============================================================
    // XỬ LÝ LOGIC CHÍNH
    // =============================================================

    private void xuLyChonBan(Ban ban, JPanel clickedCard) {
        TrangThaiBan trangThai = TrangThaiBan.fromString(ban.getTrangThai());

        switch (trangThai) {
            case TRONG:
                xuLyVaoBan(ban);
                break;
            case CO_KHACH:
                Object[] options = {"Gọi thêm món", "Thanh toán", "Hủy"};
                int choice = JOptionPane.showOptionDialog(
                    this,
                    "Bàn " + ban.getMaBan().trim() + " đang có khách. Bạn muốn làm gì?",
                    "Chọn hành động",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, options, options[0]
                );
                
                if (choice == 0) { // Gọi thêm món
                    xuLyGoiThemMon(ban);
                } else if (choice == 1) { // Thanh toán
                    xuLyThanhToan(ban);
                }
                break;
            case DA_DAT:
                JOptionPane.showMessageDialog(this, 
                    "Bàn [" + ban.getMaBan().trim() + "] đã được đặt trước.\n" +
                    "Vui lòng vào mục 'Quản lý đặt bàn' để check-in cho khách.", 
                    "Bàn đã đặt", JOptionPane.WARNING_MESSAGE);
                break;
        }
    }

    private void xuLyVaoBan(Ban ban) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có muốn mở bàn " + ban.getMaBan().trim() + " cho khách vãng lai không?", 
            "Xác nhận vào bàn", 
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<PhieuDatBan, Void> worker = new SwingWorker<PhieuDatBan, Void>() {
            @Override
            protected PhieuDatBan doInBackground() throws Exception {
                String maPhieuMoi = phieuDatBanDAO.generateNewID();
                KhachHang kh = khachHangDAO.getKhachHangById("KH00000000");
                NhanVien nv = nhanVienDAO.getNhanVienById("NVTT001");
                LocalDateTime now = LocalDateTime.now();

                PhieuDatBan phieuMoi = new PhieuDatBan(
                    maPhieuMoi, now, now, null, kh, nv, ban, 1,
                    "Khách vãng lai", "Đã đến"
                );
                
                if (!phieuDatBanDAO.insertPhieuDatBan(phieuMoi)) {
                    throw new Exception("Lỗi khi thêm phiếu đặt bàn vào CSDL.");
                }

                ban.setTrangThai(TrangThaiBan.CO_KHACH.name());
                if (!banDAO.capNhatBan(ban)) {
                    throw new Exception("Lỗi khi cập nhật trạng thái bàn.");
                }
                
                return phieuMoi;
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    PhieuDatBan phieuMoi = get();
                    if (phieuMoi != null) {
                        // Mở dialog chọn món
                        openChonMonDialog(phieuMoi);
                        // === SỬA ĐỔI 1: Tải lại dữ liệu SAU KHI mở bàn & gọi món ===
                        refreshData(); 
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(AnTrucTiep_View.this, 
                        "Mở bàn thất bại: " + e.getCause().getMessage(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                    refreshData();
                }
            }
        };
        worker.execute();
    }

    private void xuLyGoiThemMon(Ban ban) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<PhieuDatBan, Void> worker = new SwingWorker<PhieuDatBan, Void>() {
            @Override
            protected PhieuDatBan doInBackground() throws Exception {
                return phieuDatBanDAO.getPhieuByBan(ban.getMaBan());
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    PhieuDatBan phieu = get();
                    if (phieu != null) {
                        openChonMonDialog(phieu);
                    } else {
                        JOptionPane.showMessageDialog(AnTrucTiep_View.this, 
                            "Không tìm thấy phiếu đặt bàn của bàn này.", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        refreshData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private void xuLyThanhToan(Ban ban) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<PhieuDatBan, Void> worker = new SwingWorker<PhieuDatBan, Void>() {
            @Override
            protected PhieuDatBan doInBackground() throws Exception {
                return phieuDatBanDAO.getPhieuByBan(ban.getMaBan());
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    PhieuDatBan phieu = get();
                    if (phieu != null) {
                        openThanhToanDialog(ban, phieu);
                    } else {
                        JOptionPane.showMessageDialog(AnTrucTiep_View.this, 
                            "Không tìm thấy phiếu đặt bàn của bàn này.", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                        refreshData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
    
    private void openChonMonDialog(PhieuDatBan phieu) {
        if (phieu == null) return;
        
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        if (owner == null) owner = JOptionPane.getRootFrame();
        
        ChonMon_Dialog chonMonDialog = new ChonMon_Dialog(owner, phieu);
        chonMonDialog.setVisible(true);
        // === SỬA ĐỔI 2: Xóa refreshData() ở đây để tránh "load miết" ===
        // refreshData(); // <-- XÓA DÒNG NÀY
    }

    private void openThanhToanDialog(Ban ban, PhieuDatBan phieu) {
        if (phieu == null || ban == null) return;
        
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        if (owner == null) owner = JOptionPane.getRootFrame();
        
        String maNV = "NVTT001";
        if (phieu.getNhanVien() != null) maNV = phieu.getNhanVien().getMaNhanVien();
        
        Runnable callbackSauKhiThanhToan = () -> {
            refreshData(); // Chỉ refresh sau khi thanh toán thành công
        };

        ChiTietPhieuDatBan_View chiTietView = new ChiTietPhieuDatBan_View(
            ban, 
            maNV, 
            callbackSauKhiThanhToan
        );
        
        JDialog dialog = new JDialog(owner, "Chi tiết thanh toán Bàn " + ban.getMaBan(), true);
        dialog.setSize(1000, 700);
        dialog.setLocationRelativeTo(owner);
        dialog.setContentPane(chiTietView);
        dialog.setVisible(true);
        
        // === SỬA ĐỔI 3: Xóa refreshData() ở đây (vì callback đã xử lý) ===
        // refreshData(); // <-- XÓA DÒNG NÀY
    }

    // =============================================================
    // CẬP NHẬT TRẠNG THÁI VÀ UI
    // =============================================================

    private void capNhatTrangThaiBanTheoThoiGian() {
        LocalDateTime now = LocalDateTime.now();
        Set<String> maBanDaDat = new HashSet<>();
        Set<String> maBanCoKhach = new HashSet<>();
        
        if (danhSachPhieuDatDangHoatDong != null) {
            for (PhieuDatBan phieu : danhSachPhieuDatDangHoatDong) {
                if (phieu == null || phieu.getBan() == null) continue;
                String maBan = phieu.getBan().getMaBan().trim();
                String trangThaiPhieu = phieu.getTrangThaiPhieu().trim();

                if ("Đã đến".equals(trangThaiPhieu)) {
                    maBanCoKhach.add(maBan);
                } else if ("Chưa đến".equals(trangThaiPhieu)) {
                    maBanDaDat.add(maBan);
                }
            }
        }

        if (danhSachBanTheoKhuVuc != null) {
            for (List<Ban> danhSachBan : danhSachBanTheoKhuVuc.values()) {
                if (danhSachBan != null) {
                    for (Ban ban : danhSachBan) {
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

    private void capNhatCboKhuVuc() {
        ActionListener[] listeners = cboFilterKhuVuc.getActionListeners();
        for (ActionListener l : listeners) cboFilterKhuVuc.removeActionListener(l);
        cboFilterKhuVuc.removeAllItems();
        cboFilterKhuVuc.addItem("Tất cả");
        if (danhSachBanTheoKhuVuc != null) {
            for (String tenKV : danhSachBanTheoKhuVuc.keySet()) {
                cboFilterKhuVuc.addItem(tenKV);
            }
        }
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

    private void capNhatTableBanCoKhach() {
        if (modelBanCoKhach == null) return;
        modelBanCoKhach.setRowCount(0);
        
        if (danhSachPhieuDatDangHoatDong != null) {
            for (PhieuDatBan phieu : danhSachPhieuDatDangHoatDong) {
                if ("Đã đến".equals(phieu.getTrangThaiPhieu().trim())) {
                    Ban ban = phieu.getBan();
                    KhachHang kh = phieu.getKhachHang();
                    
                    String tenKV = "N/A";
                    Ban banDayDu = timBanTuMa(ban.getMaBan());
                    if (banDayDu != null && banDayDu.getKhuVuc() != null) {
                         tenKV = banDAO.getTenKhuVuc(banDayDu.getKhuVuc().getMaKhuVuc());
                    }

                    String gioVao = "N/A";
                    if(phieu.getThoiGianNhanBan() != null) {
                        gioVao = phieu.getThoiGianNhanBan().format(dtf);
                    } else if (phieu.getThoiGianDenHen() != null) {
                        gioVao = phieu.getThoiGianDenHen().format(dtf);
                    }
                                     
                    String tenKH = "Khách vãng lai";
                    if (kh != null && !"KH00000000".equals(kh.getMaKH())) {
                        tenKH = kh.getTenKH();
                    }
                    
                    modelBanCoKhach.addRow(new Object[]{
                        ban.getMaBan(),
                        tenKV,
                        gioVao,
                        tenKH
                    });
                }
            }
        }
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
        lblThongKeBan.setText(String.format("%s | Tổng số bàn: %d (Trống: %d, Đã đặt: %d, Có khách: %d)", 
            labelKV, total, trong, daDat, coKhach));
    }
    
    private Ban timBanTuMa(String maBan) {
        if (danhSachBanTheoKhuVuc == null || maBan == null) return null;
        for (List<Ban> listBan : danhSachBanTheoKhuVuc.values()) {
            for (Ban ban : listBan) {
                if (ban.getMaBan().equals(maBan)) {
                    return ban;
                }
            }
        }
        return null;
    }
    
    // =============================================================
    // TẠO COMPONENTS
    // =============================================================
    
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

    // =============================================================
    // TẠO THẺ BÀN
    // =============================================================

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
            @Override 
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    xuLyChonBan(ban, card);
                }
            }
            @Override 
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(245, 245, 245));
            }
            @Override 
            public void mouseExited(MouseEvent e) {
                card.setBackground(COLOR_WHITE);
            }
        });

        return card;
    }

    private JLabel createBanIconLabel(Ban ban) {
        String iconPath = "images/icon/thongthuong.png";
        
        JLabel lblIcon;
        ImageIcon iconBan = null;
        try {
            iconBan = new ImageIcon(iconPath); 
        } catch (Exception e) { }

        if (iconBan != null && iconBan.getIconWidth() > 0) {
            try {
                Image img = iconBan.getImage();
                Image scaled = img.getScaledInstance(45, 45, Image.SCALE_SMOOTH);
                lblIcon = new JLabel(new ImageIcon(scaled));
                lblIcon.setPreferredSize(new Dimension(45, 45));
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
    
    // =============================================================
    // INNER CLASSES
    // =============================================================
    
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

        public RoundedButton(String text, Color bgColor, Color fg) {
            super(text);
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