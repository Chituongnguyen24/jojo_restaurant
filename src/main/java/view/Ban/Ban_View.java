package view.Ban;

import dao.Ban_DAO;
import dao.KhuVuc_DAO;
import entity.Ban;
import entity.KhuVuc;
import enums.LoaiBan;
import enums.TrangThaiBan;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class Ban_View extends JPanel implements ActionListener {

    private Ban_DAO banDAO;
    private KhuVuc_DAO khuVucDAO;
    private Map<String, List<Ban>> danhSachBanTheoKhuVuc;
    private Map<String, Integer> soLuongBanTheoKhuVuc;
    private List<String> tenKhuVuc;
    private String khuVucHienTai;
    private Ban banDuocChon = null;

    private JPanel pnlLuoiBan;
    
    private JTextField txtTimNhanh;
    private JComboBox<String> cboKhuVucFilter;
    private JComboBox<String> cboTrangThaiLoc;

    private JTextField txtMaBan, txtSoCho;
    private JComboBox<String> cboLoaiBan, cboKhuVuc, cboTrangThai;
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
        
        doDuLieuCbo();
        taiLaiDuLieuVaLamMoiUI();
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
    
    private void doDuLieuCbo() {
        // CẦN ĐẢM BẢO cboKhuVuc, cboLoaiBan, cboTrangThai ĐƯỢC KHỞI TẠO TRƯỚC
        
        cboKhuVuc.removeAllItems();
        cboLoaiBan.removeAllItems();
        cboTrangThai.removeAllItems();
        cboKhuVucFilter.removeAllItems();

        List<KhuVuc> dsKhuVuc = khuVucDAO.getAllKhuVuc();
        for (KhuVuc kv : dsKhuVuc) {
            String tenKV = kv.getTenKhuVuc().trim();
            cboKhuVuc.addItem(tenKV);
            cboKhuVucFilter.addItem(tenKV);
        }
        
        for (LoaiBan lb : LoaiBan.values()) {
            cboLoaiBan.addItem(lb.getTenHienThi());
        }
        
        for (TrangThaiBan ttb : TrangThaiBan.values()) {
            cboTrangThai.addItem(ttb.getTenHienThi());
        }
        
        if (khuVucHienTai == null && cboKhuVucFilter.getItemCount() > 0) {
            khuVucHienTai = (String) cboKhuVucFilter.getItemAt(0);
        }
    }

    private void taiLaiDuLieuVaLamMoiUI() { 
        taiDuLieuTuDB(); 

        if (khuVucHienTai != null) {
            cboKhuVucFilter.setSelectedItem(khuVucHienTai);
        }
        
        capNhatHienThiLuoiBan();
        xoaRong();
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
    
    private void taiDuLieuTuDB() {
        Map<String, String> kvMap = banDAO.getDanhSachKhuVuc();
        List<Ban> tatCaBan = banDAO.getAllBan();
        
        danhSachBanTheoKhuVuc.clear();
        for (Ban ban : tatCaBan) {
             String maKV = ban.getKhuVuc().getMaKhuVuc();
             String tenKV = kvMap.getOrDefault(maKV, "Không rõ");
             
             danhSachBanTheoKhuVuc
                .computeIfAbsent(tenKV.trim(), k -> new ArrayList<>())
                .add(ban);
        }
        
        soLuongBanTheoKhuVuc = danhSachBanTheoKhuVuc.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey, 
                e -> e.getValue().size(),
                (oldValue, newValue) -> newValue,
                LinkedHashMap::new
            ));
            
        tenKhuVuc = new ArrayList<>(soLuongBanTheoKhuVuc.keySet());

        if (khuVucHienTai == null && !tenKhuVuc.isEmpty()) {
            khuVucHienTai = tenKhuVuc.get(0);
        } else if (tenKhuVuc.isEmpty()) {
            khuVucHienTai = "Không có dữ liệu";
        }
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
        
        String tenKV = banDAO.getTenKhuVuc(ban.getKhuVuc().getMaKhuVuc());
        if (tenKV != null) cboKhuVuc.setSelectedItem(tenKV.trim());
        
        cboLoaiBan.setSelectedItem(LoaiBan.fromString(ban.getLoaiBan()).getTenHienThi());
        cboTrangThai.setSelectedItem(TrangThaiBan.fromString(ban.getTrangThai()).getTenHienThi());
        
        capNhatTrangThaiNut();
    }
    
    private Ban getBanFromForm() {
        String maBan = txtMaBan.getText().trim();
        int soCho = Integer.parseInt(txtSoCho.getText().trim());
        String loaiBanTen = (String) cboLoaiBan.getSelectedItem();
        String khuVucTen = (String) cboKhuVuc.getSelectedItem();
        String trangThaiTen = (String) cboTrangThai.getSelectedItem();
        
        String maKV = khuVucDAO.getMaKhuVucTheoTen(khuVucTen);
        KhuVuc kv = new KhuVuc(maKV);
        
        String loaiBanRaw = LoaiBan.fromString(loaiBanTen).toString();
        String trangThaiRaw = TrangThaiBan.fromString(trangThaiTen).toString();

        return new Ban(maBan, soCho, kv, loaiBanRaw, trangThaiRaw);
    }
    
    private boolean validateData() {
        String maBan = txtMaBan.getText().trim();
        String soChoStr = txtSoCho.getText().trim();
        
        if (maBan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã bàn không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtMaBan.requestFocus();
            return false;
        }
        
        if (soChoStr.isEmpty() || !soChoStr.matches(REGEX_SO_CHO)) {
            JOptionPane.showMessageDialog(this, "Số chỗ không hợp lệ (phải là số nguyên dương).", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtSoCho.requestFocus();
            return false;
        }
        
        if (cboLoaiBan.getSelectedItem() == null || cboKhuVuc.getSelectedItem() == null || cboTrangThai.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ Loại bàn, Khu vực và Trạng thái.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }

    private void themBan() {
        if (!validateData()) return;
        
        String maBanMoi = txtMaBan.getText().trim();

        if (banDAO.getBanTheoMa(maBanMoi) != null) {
            JOptionPane.showMessageDialog(this, "Mã bàn đã tồn tại. Vui lòng sử dụng mã khác hoặc xóa rỗng để tạo mã mới.", "Lỗi trùng lặp", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Ban banMoi = getBanFromForm();
            banMoi.setMaBan(maBanMoi); 
            
            if (banDAO.themBan(banMoi)) {
                JOptionPane.showMessageDialog(this, "Thêm bàn thành công!");
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
            "Xác nhận xóa bàn [" + banDuocChon.getMaBan() + "]? Hành động này không thể hoàn tác.", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (banDAO.xoaBan(banDuocChon.getMaBan())) {
                JOptionPane.showMessageDialog(this, "Xóa bàn thành công!");
                taiLaiDuLieuVaLamMoiUI(); 
            } else {
                JOptionPane.showMessageDialog(this, "Xóa bàn thất bại. Bàn có thể đã có phiếu đặt hoặc hóa đơn liên quan.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void xoaRong() {
        banDuocChon = null;

        txtMaBan.setText(banDAO.getMaBanTuDong()); 
        txtMaBan.setEditable(true);
        
        txtSoCho.setText("");

        if (cboLoaiBan.getItemCount() > 0) cboLoaiBan.setSelectedItem(LoaiBan.THUONG.getTenHienThi()); 
        
        if (khuVucHienTai != null) {
            cboKhuVuc.setSelectedItem(khuVucHienTai); 
        }
        
        if (cboTrangThai.getItemCount() > 0) cboTrangThai.setSelectedItem(TrangThaiBan.TRONG.getTenHienThi());
        
        capNhatTrangThaiNut();
    }
    
    private void capNhatTrangThaiNut() {
        boolean isAdding = banDuocChon == null;
        
        txtMaBan.setEditable(isAdding);
        
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
        private Color bg;
        
        public RoundedButton(String text, Color bg, Color fg) {
            super(text);
            this.bg = bg;
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

        JPanel pnlInput = new JPanel(new GridLayout(5, 2, 10, 10)); // 5 hàng 2 cột
        pnlInput.setOpaque(false);
        pnlInput.setBorder(new EmptyBorder(15, 0, 15, 0));

        pnlInput.add(taoFormLabel("Mã bàn:"));
        txtMaBan = createInputText(false);
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

        pnlInput.add(taoFormLabel("Trạng thái:"));
        cboTrangThai = createInputComboBox(true);
        pnlInput.add(cboTrangThai);
        
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
 
}