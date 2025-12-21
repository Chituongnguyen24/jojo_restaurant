package view.Ban;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import dao.Ban_DAO;
import dao.KhuVuc_DAO;
import entity.Ban;
import entity.KhuVuc;
import enums.LoaiBan;
import enums.TrangThaiBan;

public class Ban_View extends JPanel implements ActionListener {

    private Ban_DAO banDAO;
    private KhuVuc_DAO khuVucDAO;
    private Map<String, List<Ban>> danhSachBanTheoKhuVuc;
    @SuppressWarnings("unused")
    private Map<String, Integer> soLuongBanTheoKhuVuc;
    private List<String> tenKhuVuc;
    private String khuVucHienTai;
    private Ban banDuocChon = null;

    private JPanel pnlLuoiBan;
    
    private JTextField txtTimNhanh;
    private JComboBox<String> cboKhuVucFilter;
    private JComboBox<String> cboTrangThaiLoc;

    private JTextField txtMaBan, txtSoCho;
    private JComboBox<String> cboLoaiBan, cboKhuVuc; // Đã xóa cboTrangThai
    private JButton btnThem, btnCapNhat, btnXoa, btnXoaRong;

    private static final String REGEX_SO_CHO = "^[1-9][0-9]*$";

    private Map<String, String> areaImagePaths = new LinkedHashMap<String, String>() {{
        put("Sân thượng", "images/icon/bannho.png");
        put("Sân vườn", "images/icon/thongthuong.png");
        put("Tầng 2", "images/icon/bannho.png");
        put("Tầng trệt", "images/icon/thongthuong.png");
        put("Phòng VIP", "images/icon/vip.png");
    }};
    private static final String DEFAULT_TABLE_IMAGE = "images/icon/thongthuong.png";
    private Map<String, ImageIcon> areaIcons;

    private static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 14); 
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color BG_VIEW = new Color(251, 248, 241); 
    private static final Color MAU_VIEN = new Color(222, 226, 230);
    private static final Color COLOR_TITLE = new Color(30, 30, 30);
    
    private static final Font FONT_TIEUDE_LON = new Font("Segoe UI", Font.BOLD, 26); 

    private class BackgroundData {
        final Map<String, List<Ban>> banTheoKhuVuc;
        final List<String> tenKhuVuc;
        final List<KhuVuc> dsKhuVucTuDB; 

        public BackgroundData(List<Ban> tatCaBan, List<KhuVuc> dsKhuVuc) {
            this.dsKhuVucTuDB = (dsKhuVuc != null) ? dsKhuVuc : new ArrayList<>();
            
            Map<String, String> kvMap = this.dsKhuVucTuDB.stream()
                .collect(Collectors.toMap(KhuVuc::getMaKhuVuc, KhuVuc::getTenKhuVuc));
            
            Map<String, List<Ban>> mapBan = new LinkedHashMap<>();
            if (tatCaBan != null) {
                for (Ban ban : tatCaBan) {
                     String maKV = ban.getKhuVuc().getMaKhuVuc();
                     String tenKV = kvMap.getOrDefault(maKV, "Không rõ");
                     mapBan.computeIfAbsent(tenKV.trim(), k -> new ArrayList<>()).add(ban);
                }
            }
            this.banTheoKhuVuc = mapBan;
            this.tenKhuVuc = new ArrayList<>(this.banTheoKhuVuc.keySet());
        }
    }

    public Ban_View() {
        banDAO = new Ban_DAO();
        khuVucDAO = new KhuVuc_DAO();
        danhSachBanTheoKhuVuc = new LinkedHashMap<>();
        soLuongBanTheoKhuVuc = new LinkedHashMap<>();
        tenKhuVuc = new ArrayList<>();
        areaIcons = new LinkedHashMap<>();
        
        khoiTaoFilterComponents(); 
        ganSuKien(); 
        taiTatCaIconKhuVuc(); 
        thietLapGiaoDien();
        
        loadDataAsync();
    }
    
    private void ganSuKien() {
        btnThem = new RoundedButton("Thêm", new Color(76, 175, 80), COLOR_WHITE);
        btnCapNhat = new RoundedButton("Cập nhật", new Color(34, 139, 230), COLOR_WHITE);
        btnXoa = new RoundedButton("Xóa", new Color(244, 67, 54), COLOR_WHITE);
        btnXoaRong = new RoundedButton("Xóa rỗng", new Color(108, 117, 125), COLOR_WHITE);

        btnThem.addActionListener(this);
        btnCapNhat.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaRong.addActionListener(this);
    }
    
    private void khoiTaoFilterComponents() {
        txtTimNhanh = new JTextField(15);
        cboKhuVucFilter = new JComboBox<>();
        cboTrangThaiLoc = new JComboBox<>(new String[]{"Tất cả", "Trống", "Có khách", "Đã đặt"});
        
        ActionListener filterAction = e -> capNhatHienThiLuoiBan();
        txtTimNhanh.addActionListener(filterAction);
        cboKhuVucFilter.addActionListener(e -> {
            String selectedArea = (String) cboKhuVucFilter.getSelectedItem();
            if (selectedArea != null && !selectedArea.equals(khuVucHienTai)) {
                chuyenKhuVuc(selectedArea);
            }
        });
        cboTrangThaiLoc.addActionListener(filterAction);
    }
    
    private void taiLaiDuLieuVaLamMoiUI() { 
        loadDataAsync();
    }
    
    private void loadDataAsync() {
        pnlLuoiBan.removeAll();
        pnlLuoiBan.setLayout(new FlowLayout(FlowLayout.CENTER));
        JLabel lblLoading = new JLabel("Đang tải dữ liệu bàn, vui lòng chờ...");
        lblLoading.setFont(FONT_CHU);
        pnlLuoiBan.add(lblLoading);
        pnlLuoiBan.revalidate();
        pnlLuoiBan.repaint();

        String currentKhuVuc = khuVucHienTai;
        
        SwingWorker<BackgroundData, Void> worker = new SwingWorker<BackgroundData, Void>() {
            @Override
            protected BackgroundData doInBackground() throws Exception {
                List<Ban> tatCaBan = banDAO.getAllBan();
                List<KhuVuc> dsKhuVuc = khuVucDAO.getAllKhuVuc();
                return new BackgroundData(tatCaBan, dsKhuVuc);
            }

            @Override
            protected void done() {
                try {
                    BackgroundData data = get();
                    
                    danhSachBanTheoKhuVuc = data.banTheoKhuVuc;
                    tenKhuVuc = data.tenKhuVuc;
                    soLuongBanTheoKhuVuc = data.banTheoKhuVuc.entrySet().stream()
                            .collect(Collectors.toMap(
                               Map.Entry::getKey, 
                               e -> e.getValue().size(),
                               (oldValue, newValue) -> newValue,
                               LinkedHashMap::new
                            ));
                    
                    // Cập nhật Combobox Form
                    cboKhuVuc.removeAllItems();
                    if (data.dsKhuVucTuDB != null) {
                        for (KhuVuc kv : data.dsKhuVucTuDB) {
                            cboKhuVuc.addItem(kv.getTenKhuVuc().trim());
                        }
                    }
                    cboLoaiBan.removeAllItems();
                    for (LoaiBan lb : LoaiBan.values()) cboLoaiBan.addItem(lb.getTenHienThi());
                    
                    // Cập nhật Combobox Filter
                    ActionListener[] listeners = cboKhuVucFilter.getActionListeners();
                    for(ActionListener l : listeners) cboKhuVucFilter.removeActionListener(l);
                    
                    cboKhuVucFilter.removeAllItems();
                    if (tenKhuVuc != null) {
                        for (String tenKV : tenKhuVuc) cboKhuVucFilter.addItem(tenKV);
                    }
                    
                    for(ActionListener l : listeners) cboKhuVucFilter.addActionListener(l);
                    
                    // Khôi phục khu vực
                    if (currentKhuVuc != null && tenKhuVuc.contains(currentKhuVuc)) {
                        khuVucHienTai = currentKhuVuc;
                    } else if (!tenKhuVuc.isEmpty()) {
                        khuVucHienTai = tenKhuVuc.get(0);
                    } else {
                        khuVucHienTai = "Không có dữ liệu";
                    }
                    cboKhuVucFilter.setSelectedItem(khuVucHienTai);
                    
                    capNhatHienThiLuoiBan();
                    xoaRong();

                } catch (Exception e) {
                    e.printStackTrace();
                    pnlLuoiBan.removeAll();
                    pnlLuoiBan.add(new JLabel("Lỗi khi tải dữ liệu bàn: " + e.getMessage()));
                    pnlLuoiBan.revalidate();
                    pnlLuoiBan.repaint();
                }
            }
        };
        
        worker.execute();
    }

    private JPanel taoPanelHeader() {
        JPanel panelHeaderWrapper = new JPanel(new BorderLayout(0, 15));
        panelHeaderWrapper.setBackground(BG_VIEW);
        panelHeaderWrapper.setBorder(new EmptyBorder(20, 30, 0, 30));
        
        JLabel lblTitle = new JLabel("Quản lý bàn");
        lblTitle.setFont(FONT_TIEUDE_LON);
        lblTitle.setForeground(COLOR_TITLE);
        panelHeaderWrapper.add(lblTitle, BorderLayout.NORTH);
        
        JPanel pnlLoc = taoPanelTimKiem();
        panelHeaderWrapper.add(pnlLoc, BorderLayout.CENTER);
        
        return panelHeaderWrapper;
    }
    
    private JPanel taoPanelTimKiem() {
        JPanel pnlSearch = new RoundedPanel(15, COLOR_WHITE, new FlowLayout(FlowLayout.LEFT, 20, 10));
        pnlSearch.setBorder(new EmptyBorder(10, 15, 10, 15));

        JComponent[] filterComponents = {txtTimNhanh, cboKhuVucFilter, cboTrangThaiLoc};
        for (JComponent comp : filterComponents) {
            comp.setFont(FONT_CHU);
            comp.setBackground(COLOR_WHITE);
            comp.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(MAU_VIEN, 1),
                new EmptyBorder(5, 10, 5, 10)
            ));
        }

        pnlSearch.add(new JLabel("Tìm kiếm:"));
        pnlSearch.add(txtTimNhanh);
        pnlSearch.add(new JLabel("Khu vực:"));
        pnlSearch.add(cboKhuVucFilter);
        pnlSearch.add(new JLabel("Trạng thái:"));
        pnlSearch.add(cboTrangThaiLoc);
        
        return pnlSearch;
    }

    private void chuyenKhuVuc(String tenKV) {
        khuVucHienTai = tenKV;
        capNhatHienThiLuoiBan();
        xoaRong();
    }
    
    private JSplitPane taoPanelNoiDungChinh() {
        
        JPanel pnlListContainer = taoPanelDanhSachBan();
        JPanel pnlCRUD = taoPanelCRUDForm();
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, pnlListContainer, pnlCRUD);
        splitPane.setDividerLocation(800); 
        splitPane.setResizeWeight(0.7);
        splitPane.setBorder(null);
        splitPane.setBackground(BG_VIEW);

        return splitPane;
    }
    
    private JPanel taoPanelDanhSachBan() {
        JPanel pnlWrapper = new JPanel(new BorderLayout());
        pnlWrapper.setOpaque(false);
        pnlWrapper.setBorder(new EmptyBorder(10, 30, 30, 10));

        JLabel lblMapTitle = new JLabel("Sơ đồ bàn - Chọn bàn để chỉnh sửa");
        lblMapTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblMapTitle.setForeground(COLOR_TITLE);
        pnlWrapper.add(lblMapTitle, BorderLayout.NORTH);

        pnlLuoiBan = new JPanel(new GridLayout(0, 2, 20, 20)); 
        pnlLuoiBan.setBackground(BG_VIEW);
        pnlLuoiBan.setBorder(new EmptyBorder(15, 15, 15, 15));
        
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


    private void thietLapGiaoDien() {
        setLayout(new BorderLayout());
        setBackground(BG_VIEW);
        
        JPanel pnlHeader = taoPanelHeader();
        add(pnlHeader, BorderLayout.NORTH);
        
        JSplitPane pnlContent = taoPanelNoiDungChinh(); 
        add(pnlContent, BorderLayout.CENTER);
    }
    
    private void capNhatHienThiLuoiBan() {
        pnlLuoiBan.removeAll(); 
        pnlLuoiBan.setLayout(new GridLayout(0, 2, 20, 20)); 
        
        List<Ban> tables = danhSachBanTheoKhuVuc.get(khuVucHienTai);
        String trangThaiLoc = (String) cboTrangThaiLoc.getSelectedItem();
        String keyword = txtTimNhanh.getText().trim();
        
        if (tables != null && !tables.isEmpty()) {
            List<Ban> filteredTables = tables.stream().filter(b -> {
                String ttbTen = TrangThaiBan.fromString(b.getTrangThai()).getTenHienThi();
                boolean matchStatus = "Tất cả".equals(trangThaiLoc) || ttbTen.equals(trangThaiLoc);
                boolean matchKeyword = keyword.isEmpty() || 
                    b.getMaBan().toLowerCase().contains(keyword.toLowerCase()) ||
                    ttbTen.toLowerCase().contains(keyword.toLowerCase());
                return matchStatus && matchKeyword;
            }).collect(Collectors.toList());

            if (filteredTables.isEmpty() && !"Tất cả".equals(trangThaiLoc) && keyword.isEmpty()) {
                 JLabel nTL = new JLabel("Không có bàn nào ở trạng thái '" + trangThaiLoc + "'.");
                 nTL.setFont(FONT_CHU); nTL.setForeground(COLOR_TITLE); 
                 pnlLuoiBan.setLayout(new FlowLayout()); 
                 pnlLuoiBan.add(nTL);
            } else if (filteredTables.isEmpty()) {
                 JLabel nTL = new JLabel("Không tìm thấy bàn phù hợp.");
                 nTL.setFont(FONT_CHU); nTL.setForeground(COLOR_TITLE); 
                 pnlLuoiBan.setLayout(new FlowLayout()); 
                 pnlLuoiBan.add(nTL);
            } else {
                 for (Ban b : filteredTables) { 
                      pnlLuoiBan.add(taoTheBan(b)); 
                 }
            }
            
        } else {
            JLabel nTL = new JLabel("Không có bàn nào trong khu vực này.");
            nTL.setFont(FONT_CHU); 
            nTL.setForeground(COLOR_TITLE); 
            pnlLuoiBan.setLayout(new FlowLayout()); 
            pnlLuoiBan.add(nTL);
        }
        
        pnlLuoiBan.revalidate(); 
        pnlLuoiBan.repaint();
    }


    private JPanel taoTheBan(Ban ban) {
        RoundedPanel panel = new RoundedPanel(25, COLOR_WHITE, new BorderLayout(15, 0));
        panel.setBorder(new EmptyBorder(10, 10, 10, 15));
        panel.setPreferredSize(new Dimension(320, 110)); 
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        TrangThaiBan ttb = TrangThaiBan.fromString(ban.getTrangThai());
        panel.setBorderColor(ttb.getColor()); 

        ImageIcon icon = layIconChoKhuVucHienTai(); 
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setPreferredSize(new Dimension(90, 90));
        lblIcon.setOpaque(false);
        panel.add(lblIcon, BorderLayout.WEST);

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
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblName.setForeground(COLOR_TITLE);

        JLabel lblCapacity = new JLabel(ban.getSoCho() + " chỗ");
        lblCapacity.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCapacity.setForeground(new Color(220, 0, 0));

        JLabel lblTrangThai = new JLabel(ttb.getTenHienThi());
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTrangThai.setForeground(ttb.getColor());
        
        infoPanel.add(lblName);
        infoPanel.add(Box.createVerticalGlue()); 
        infoPanel.add(lblCapacity);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblTrangThai);

        panel.add(infoPanel, BorderLayout.CENTER);

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    hienThiBanDuocChon(ban);
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(245, 245, 245));
                panel.setBorderColor(new Color(100, 150, 255));
                panel.repaint();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(COLOR_WHITE);
                panel.setBorderColor(ttb.getColor()); 
                panel.repaint();
            }
        });

        return panel;
    }
    
    private void hienThiBanDuocChon(Ban ban) {
        banDuocChon = ban;
        txtMaBan.setText(ban.getMaBan().trim());
        txtSoCho.setText(String.valueOf(ban.getSoCho()));
        
        String tenKV = "Không rõ";
        for (KhuVuc kv : khuVucDAO.getAllKhuVuc()) {
             if(kv.getMaKhuVuc().equals(ban.getKhuVuc().getMaKhuVuc())) {
                 tenKV = kv.getTenKhuVuc();
                 break;
             }
        }
        if (tenKV != null) cboKhuVuc.setSelectedItem(tenKV.trim());
        
        cboLoaiBan.setSelectedItem(LoaiBan.fromString(ban.getLoaiBan()).getTenHienThi());
        
        capNhatTrangThaiNut();
    }
    
    private Ban getBanFromForm() {
        String maBan = txtMaBan.getText().trim();
        int soCho = Integer.parseInt(txtSoCho.getText().trim());
        String loaiBanTen = (String) cboLoaiBan.getSelectedItem();
        String khuVucTen = (String) cboKhuVuc.getSelectedItem();
        
        String maKV = khuVucDAO.getMaKhuVucTheoTen(khuVucTen);
        KhuVuc kv = new KhuVuc(maKV);
        
        String loaiBanRaw = LoaiBan.fromString(loaiBanTen).toString();
        
        // Tự động xác định trạng thái
        String trangThaiRaw;
        if (banDuocChon != null) {
            // Nếu đang cập nhật, giữ nguyên trạng thái cũ
            trangThaiRaw = banDuocChon.getTrangThai();
        } else {
            // Nếu thêm mới, mặc định là TRỐNG
            trangThaiRaw = TrangThaiBan.TRONG.toString();
        }

        return new Ban(maBan, soCho, kv, loaiBanRaw, trangThaiRaw);
    }
    
    private boolean validateData() {
        String soChoStr = txtSoCho.getText().trim();
        
        if (soChoStr.isEmpty() || !soChoStr.matches(REGEX_SO_CHO)) {
            JOptionPane.showMessageDialog(this, "Số chỗ không hợp lệ (phải là số nguyên dương).", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtSoCho.requestFocus();
            return false;
        }
        
        if (cboLoaiBan.getSelectedItem() == null || cboKhuVuc.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ Loại bàn và Khu vực.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    private void themBan() {
        if (!validateData()) return;

        try {
            Ban banMoi = getBanFromForm();

            String khuVucTen = (String) cboKhuVuc.getSelectedItem();
            String maKV = khuVucDAO.getMaKhuVucTheoTen(khuVucTen);

            String maBanMoi = banDAO.taoMaBanMoiTheoKhuVuc(maKV);

            banMoi.setMaBan(maBanMoi);

            if (banDAO.getBanTheoMa(maBanMoi) != null) {
                JOptionPane.showMessageDialog(this, "Lỗi khi tạo mã tự động. Vui lòng thử lại.", "Lỗi trùng lặp", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (banDAO.themBan(banMoi)) {
                JOptionPane.showMessageDialog(this, "Thêm bàn thành công! Mã bàn mới: " + maBanMoi);
                taiLaiDuLieuVaLamMoiUI(); 
            } else {
                JOptionPane.showMessageDialog(this, "Thêm bàn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm bàn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void capNhatBan() {
        if (banDuocChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn cần cập nhật!");
            return;
        }
        if (!validateData()) return;

        try {
            Ban banCapNhat = getBanFromForm();
            
            if (banDAO.capNhatBan(banCapNhat)) {
                JOptionPane.showMessageDialog(this, "Cập nhật bàn thành công!");
                taiLaiDuLieuVaLamMoiUI(); 
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật bàn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật bàn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void xoaBan() {
        if (banDuocChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn cần xóa!");
            return;
        }
        
        if (!TrangThaiBan.TRONG.equals(TrangThaiBan.fromString(banDuocChon.getTrangThai()))) {
            JOptionPane.showMessageDialog(this, "Chỉ có thể xóa bàn khi trạng thái là 'Trống'.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận ẩn bàn [" + banDuocChon.getMaBan() + "]? Bàn sẽ không hiển thị trong danh sách.", 
            "Xác nhận ẩn bàn", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (banDAO.anBan(banDuocChon.getMaBan())) {
                JOptionPane.showMessageDialog(this, "Ẩn bàn thành công!");
                taiLaiDuLieuVaLamMoiUI(); 
            } else {
                JOptionPane.showMessageDialog(this, "Ẩn bàn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void xoaRong() {
        banDuocChon = null;

        txtMaBan.setText("--Tự động tạo--"); 
        txtMaBan.setEditable(false);
        
        txtSoCho.setText("");

        if (cboLoaiBan.getItemCount() > 0) cboLoaiBan.setSelectedItem(LoaiBan.THUONG.getTenHienThi()); 
        
        if (khuVucHienTai != null) {
            cboKhuVuc.setSelectedItem(khuVucHienTai); 
        }
        
        capNhatTrangThaiNut();
    }
    
    private void capNhatTrangThaiNut() {
        boolean isAdding = banDuocChon == null;
        
        btnThem.setEnabled(isAdding);
        btnCapNhat.setEnabled(!isAdding);
        
        boolean isTrangThaiTrong = !isAdding && TrangThaiBan.TRONG.equals(TrangThaiBan.fromString(banDuocChon.getTrangThai()));
        btnXoa.setEnabled(isTrangThaiTrong);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        Object o = e.getSource();
        
        if (o == btnXoaRong) {
            xoaRong();
        } else if (o == btnThem) {
            themBan();
        } else if (o == btnCapNhat) {
            capNhatBan();
        } else if (o == btnXoa) {
            xoaBan();
        } 
    }
    
    private ImageIcon layIconChoKhuVucHienTai() {
        ImageIcon icon = areaIcons.get(khuVucHienTai);
        return icon != null ? icon : areaIcons.get("DEFAULT");
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
            if (o.getIconWidth() <= 0) return null; 
            Image s = o.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH); 
            return new ImageIcon(s); 
        } catch (Exception e) { 
            return null; 
        } 
    }

    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color borderColor = MAU_VIEN; // Mặc định
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
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius));
            g2.dispose();
            super.paintComponent(g); 
        }
        
        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(this.borderColor); 
            g2.setStroke(new BasicStroke(1)); 
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius));
            g2.dispose();
        }
    }
    
    private class RoundedButton extends JButton {
        private int doBoGoc;
        
        public RoundedButton(String text, Color bg, Color fg) {
            super(text);
            this.doBoGoc = 20;
            setBackground(bg);
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
    
    private JComboBox<String> createInputComboBox(boolean editable) {
        JComboBox<String> cbo = new JComboBox<>();
        cbo.setEnabled(editable);
        cbo.setFont(FONT_CHU);
        cbo.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MAU_VIEN, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return cbo;
    }
    
    private JPanel taoPanelCRUDForm() {
        RoundedPanel pnlForm = new RoundedPanel(20, COLOR_WHITE, new BorderLayout());
        pnlForm.setPreferredSize(new Dimension(350, 450));
        pnlForm.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblFormTitle = new JLabel("Chi tiết Bàn");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        lblFormTitle.setForeground(COLOR_TITLE);
        pnlForm.add(lblFormTitle, BorderLayout.NORTH);

        // Giảm số hàng từ 5 xuống 4 vì đã xóa Trạng thái
        JPanel pnlInput = new JPanel(new GridLayout(4, 2, 10, 10)); 
        pnlInput.setOpaque(false);
        pnlInput.setBorder(new EmptyBorder(15, 0, 15, 0));

        pnlInput.add(taoFormLabel("Mã bàn:"));
        txtMaBan = createInputText(false);
        txtMaBan.setText("--Tự động tạo--");
        pnlInput.add(txtMaBan);

        pnlInput.add(taoFormLabel("Số chỗ:"));
        txtSoCho = createInputText(true);
        pnlInput.add(txtSoCho);

        pnlInput.add(taoFormLabel("Loại bàn:"));
        cboLoaiBan = createInputComboBox(true);
        pnlInput.add(cboLoaiBan);
        
        pnlInput.add(taoFormLabel("Khu vực:"));
        cboKhuVuc = createInputComboBox(true);
        pnlInput.add(cboKhuVuc);

        // Đã xóa input Trạng thái tại đây
        
        pnlForm.add(pnlInput, BorderLayout.CENTER);
        
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        pnlButton.setOpaque(false);
        
        pnlButton.add(btnThem);
        pnlButton.add(btnCapNhat);
        pnlButton.add(btnXoa);
        pnlButton.add(btnXoaRong);

        pnlForm.add(pnlButton, BorderLayout.SOUTH);
        
        return pnlForm;
    }

    public void refreshData() {
        loadDataAsync();
    }
}