package view.Ban;

import dao.DatBan_DAO;
import dao.KhachHang_DAO;
import entity.Ban;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhieuDatBan;
import enums.LoaiBan;
import enums.TrangThaiBan;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class DatBan_View extends JPanel {
    
    private Map<String, List<Ban>> banTheoTang;
    private JPanel bangPanel;
    private String currentFloor = "Tầng trệt";
    private DatBan_DAO daoDatBan = new DatBan_DAO();
    private KhachHang_DAO daoKhachHang = new KhachHang_DAO();
    
    // Thời gian tìm kiếm
    private JTextField truongSoLuongKhach;
    private JTextField truongThoiGianDen; // Format: dd/MM/yyyy HH:mm
    private JButton nutTimKiem;
    
    public DatBan_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 240));
        
        initializeTableData();
        
        JPanel bangChinh = new JPanel(new BorderLayout(20, 20));
        bangChinh.setBackground(new Color(245, 245, 240));
        bangChinh.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Phần đầu trang
        JPanel bangDauTrang = createHeaderPanel();
        bangChinh.add(bangDauTrang, BorderLayout.NORTH);
        
        // Khu vực nội dung với thanh bên trái và bàn
        JPanel bangNoiDung = new JPanel(new BorderLayout(20, 0));
        bangNoiDung.setOpaque(false);
        
        JPanel thanhBenTrai = createLeftSidebar();
        bangNoiDung.add(thanhBenTrai, BorderLayout.WEST);
        
        JPanel bangPhai = createRightPanel();
        bangNoiDung.add(bangPhai, BorderLayout.CENTER);
        
        bangChinh.add(bangNoiDung, BorderLayout.CENTER);
        
        // Thanh cuộn
        JScrollPane thanhCuon = new JScrollPane(bangChinh);
        thanhCuon.setBorder(null);
        thanhCuon.getVerticalScrollBar().setUnitIncrement(16);
        
        add(thanhCuon, BorderLayout.CENTER);
    }
    
    private void initializeTableData() {
        // Lấy dữ liệu thực từ DAO
        banTheoTang = daoDatBan.getAllBanByFloor();
    }
    
    private JPanel createHeaderPanel() {
        JPanel bang = new JPanel(new BorderLayout());
        bang.setOpaque(false);
        
        // Thêm ô nhập tìm kiếm thời gian
        JPanel bangNhapTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        bangNhapTimKiem.setOpaque(false);
        
        JLabel nhanSoLuong = new JLabel("Số lượng khách:");
        truongSoLuongKhach = new JTextField(5);
        truongSoLuongKhach.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JLabel nhanThoiGian = new JLabel("Thời gian đến (dd/MM/yyyy HH:mm):");
        truongThoiGianDen = new JTextField(15);
        truongThoiGianDen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        truongThoiGianDen.setText(LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        
        nutTimKiem = new JButton("Tìm bàn khả dụng");
        nutTimKiem.setBackground(new Color(34, 139, 230));
        nutTimKiem.setForeground(Color.WHITE);
        nutTimKiem.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nutTimKiem.addActionListener(e -> searchTablesByTime());
        
        bangNhapTimKiem.add(nhanSoLuong);
        bangNhapTimKiem.add(truongSoLuongKhach);
        bangNhapTimKiem.add(nhanThoiGian);
        bangNhapTimKiem.add(truongThoiGianDen);
        bangNhapTimKiem.add(nutTimKiem);
        
        bang.add(bangNhapTimKiem, BorderLayout.EAST);
        
        JPanel bangTieuDe = new JPanel();
        bangTieuDe.setLayout(new BoxLayout(bangTieuDe, BoxLayout.Y_AXIS));
        bangTieuDe.setOpaque(false);
        
        JLabel nhanTieuDe = new JLabel("Quản lý đặt bàn");
        nhanTieuDe.setFont(new Font("Segoe UI", Font.BOLD, 28));
        nhanTieuDe.setForeground(new Color(60, 60, 60));
        nhanTieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel nhanPhu = new JLabel("Quản lý trạng thái bàn theo từng khu vực và thời gian");
        nhanPhu.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nhanPhu.setForeground(new Color(120, 120, 120));
        nhanPhu.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        bangTieuDe.add(nhanTieuDe);
        bangTieuDe.add(Box.createVerticalStrut(5));
        bangTieuDe.add(nhanPhu);
        
        bang.add(bangTieuDe, BorderLayout.WEST);
        
        return bang;
    }
    
    private void searchTablesByTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        try {
            int soLuongKhach = Integer.parseInt(truongSoLuongKhach.getText().trim());
            LocalDateTime thoiGianDen = LocalDateTime.parse(truongThoiGianDen.getText().trim(), formatter);
            
            if (soLuongKhach <= 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng khách hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Lấy danh sách phiếu đặt bàn trong khoảng thời gian (giả sử +2 giờ cho slot)
            LocalDateTime thoiGianKetThuc = thoiGianDen.plusHours(2); // Giả sử slot 2 giờ
            List<PhieuDatBan> danhSachPhieu = daoDatBan.getPhieuDatBanByTimeRange(thoiGianDen, thoiGianKetThuc);
            
            // Lọc bàn theo tầng hiện tại, phù hợp số lượng, và không bị đặt trong slot
            List<Ban> danhSachBan = banTheoTang.get(currentFloor);
            List<Ban> banPhuHop = new ArrayList<>();
            for (Ban ban : danhSachBan) {
                if (ban.getSoCho() >= soLuongKhach && !isTableBookedInTimeSlot(ban.getMaBan(), danhSachPhieu, thoiGianDen, thoiGianKetThuc)) {
                    banPhuHop.add(ban);
                }
            }
            
            // Hiển thị chỉ bàn khả dụng
            updateTablesDisplay(banPhuHop);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Định dạng thời gian không hợp lệ! Sử dụng dd/MM/yyyy HH:mm", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean isTableBookedInTimeSlot(String maBan, List<PhieuDatBan> danhSachPhieu, LocalDateTime start, LocalDateTime end) {
        for (PhieuDatBan phieu : danhSachPhieu) {
            if (phieu.getBan().getMaBan().equals(maBan)) {
                LocalDateTime thoiGianPhieu = phieu.getThoiGianDat();
                if (!thoiGianPhieu.isAfter(end) && !thoiGianPhieu.plusHours(2).isBefore(start)) { // Overlap slot
                    return true;
                }
            }
        }
        return false;
    }
    
    private JPanel createLeftSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        sidebar.setPreferredSize(new Dimension(280, 0));
        
        // Area selection section
        JLabel areaLabel = new JLabel("Khu vực");
        areaLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        areaLabel.setForeground(new Color(60, 60, 60));
        areaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel areaSubLabel = new JLabel("Chọn khu vực để xem số đỏ bàn");
        areaSubLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        areaSubLabel.setForeground(new Color(120, 120, 120));
        areaSubLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sidebar.add(areaLabel);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(areaSubLabel);
        sidebar.add(Box.createVerticalStrut(20));
        
        // Floor buttons
        String[] floors = {"Tầng trệt", "Tầng 2", "Tầng 3", "Sân vườn"};
        int[] counts = {4, 7, 4, 3};
        
        for (int i = 0; i < floors.length; i++) {
            JButton floorBtn = createFloorButton(floors[i], counts[i] + "/8", i == 0);
            floorBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(floorBtn);
            sidebar.add(Box.createVerticalStrut(10));
        }
        
        sidebar.add(Box.createVerticalStrut(20));
        
        // Info section
        JLabel infoLabel = new JLabel("Thông tin khu vực");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        infoLabel.setForeground(new Color(60, 60, 60));
        infoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sidebar.add(infoLabel);
        sidebar.add(Box.createVerticalStrut(15));
        
        JLabel ruleTitle = new JLabel("6 bàn máy lạnh");
        ruleTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        ruleTitle.setForeground(new Color(60, 60, 60));
        ruleTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        sidebar.add(ruleTitle);
        sidebar.add(Box.createVerticalStrut(8));
        
        String[] rules = {
            "• Mỗi bàn có bếp nướng âm + hút khói",
            "• Phù hợp gia đình nhỏ, nhóm bạn",
            "• Không phụ phí"
        };
        
        for (String rule : rules) {
            JLabel ruleLabel = new JLabel(rule);
            ruleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            ruleLabel.setForeground(new Color(100, 100, 100));
            ruleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            sidebar.add(ruleLabel);
            sidebar.add(Box.createVerticalStrut(5));
        }
        
        sidebar.add(Box.createVerticalGlue());
        
        return sidebar;
    }
    
    private JButton createFloorButton(String floorName, String count, boolean selected) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 0));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        button.setPreferredSize(new Dimension(240, 45));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        if (selected) {
            button.setBackground(new Color(255, 152, 0));
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 152, 0), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
        } else {
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
            ));
        }
        
        JLabel nameLabel = new JLabel(floorName);
        nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        nameLabel.setForeground(selected ? Color.WHITE : new Color(60, 60, 60));
        
        JLabel countLabel = new JLabel(count);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        countLabel.setForeground(Color.WHITE);
        
        Color badgeColor = selected ? new Color(220, 120, 0) : new Color(76, 175, 80);
        countLabel.setOpaque(true);
        countLabel.setBackground(badgeColor);
        countLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        button.add(nameLabel, BorderLayout.WEST);
        button.add(countLabel, BorderLayout.EAST);
        
        button.addActionListener(e -> {
            currentFloor = floorName;
            // Refresh data từ DB để đảm bảo thời gian thực
            initializeTableData();
            updateTablesDisplay();
        });
        
        return button;
    }
    
    private void styleButton(JButton button) {
        button.setBackground(new Color(34, 139, 230));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
    }
    
    private void showChiTietDialog() {
        String maPhieu = JOptionPane.showInputDialog(this, "Nhập mã phiếu để xem chi tiết:");
        if (maPhieu != null && !maPhieu.trim().isEmpty()) {
            PhieuDatBan phieu = daoDatBan.getPhieuDatBanById(maPhieu);
            if (phieu != null) {
                JFrame chiTietFrame = new JFrame("Chi tiết phiếu đặt bàn");
                chiTietFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                chiTietFrame.add(new ChiTietPhieuDatBan_View(phieu));
                chiTietFrame.pack();
                chiTietFrame.setLocationRelativeTo(this);
                chiTietFrame.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu!");
            }
        }
    }
    
    private void showBookingDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Đặt bàn", true);
        dialog.setSize(500, 500);
        dialog.setLayout(new BorderLayout());
        
        // Phần chọn loại khách hàng
        JPanel panelLoaiKhach = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelLoaiKhach.setBorder(BorderFactory.createTitledBorder("Loại khách hàng"));
        JRadioButton radioThanhVien = new JRadioButton("Thành viên", false);
        JRadioButton radioKhachLe = new JRadioButton("Khách lẻ", true);
        ButtonGroup groupLoaiKhach = new ButtonGroup();
        groupLoaiKhach.add(radioThanhVien);
        groupLoaiKhach.add(radioKhachLe);
        panelLoaiKhach.add(radioThanhVien);
        panelLoaiKhach.add(radioKhachLe);
        
        // Phần nhập thông tin khách hàng
        JPanel panelKhachHang = new JPanel(new GridLayout(0, 2, 10, 10));
        panelKhachHang.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        
        JLabel lblSDT = new JLabel("SĐT:");
        JTextField txtSDT = new JTextField(15);
        panelKhachHang.add(lblSDT);
        panelKhachHang.add(txtSDT);
        
        JLabel lblTenKH = new JLabel("Tên KH:");
        JTextField txtTenKH = new JTextField(15);
        panelKhachHang.add(lblTenKH);
        panelKhachHang.add(txtTenKH);
        
        JLabel lblEmail = new JLabel("Email:");
        JTextField txtEmail = new JTextField(15);
        panelKhachHang.add(lblEmail);
        panelKhachHang.add(txtEmail);
        
        JLabel lblSoNguoi = new JLabel("Số người:");
        JTextField txtSoNguoi = new JTextField(5);
        panelKhachHang.add(lblSoNguoi);
        panelKhachHang.add(txtSoNguoi);
        
        JLabel lblThoiGian = new JLabel("Thời gian đến (dd/MM/yyyy HH:mm):");
        JTextField txtThoiGian = new JTextField(15);
        txtThoiGian.setText(LocalDateTime.now().plusHours(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        panelKhachHang.add(lblThoiGian);
        panelKhachHang.add(txtThoiGian);
        
        // Ẩn/hiện trường theo loại khách
        lblTenKH.setVisible(true);
        txtTenKH.setVisible(true);
        lblEmail.setVisible(true);
        txtEmail.setVisible(true);
        
        radioKhachLe.addActionListener(e -> {
            lblTenKH.setVisible(true);
            txtTenKH.setVisible(true);
            lblEmail.setVisible(true);
            txtEmail.setVisible(true);
        });
        
        radioThanhVien.addActionListener(e -> {
            lblTenKH.setVisible(false);
            txtTenKH.setVisible(false);
            lblEmail.setVisible(false);
            txtEmail.setVisible(false);
        });
        
        // Event listener cho SĐT
        txtSDT.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                if (radioThanhVien.isSelected() && txtSDT.getText().length() >= 10) {
                    List<KhachHang> dsKH = daoKhachHang.findByPhone(txtSDT.getText());
                    if (!dsKH.isEmpty()) {
                        KhachHang kh = dsKH.get(0);
                        txtTenKH.setText(kh.getTenKhachHang());
                        txtEmail.setText(kh.getEmail());
                    }
                }
            }
            
            @Override
            public void removeUpdate(DocumentEvent e) {}
            
            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.add(panelLoaiKhach, BorderLayout.NORTH);
        formPanel.add(panelKhachHang, BorderLayout.CENTER);
        
        JButton btnXacNhan = new JButton("Xác nhận đặt bàn");
        styleButton(btnXacNhan);
        btnXacNhan.addActionListener(e -> {
            if (txtSDT.getText().trim().isEmpty() || txtSoNguoi.getText().trim().isEmpty() || txtThoiGian.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin bắt buộc!");
                return;
            }
            if (radioKhachLe.isSelected() && (txtTenKH.getText().trim().isEmpty() || txtEmail.getText().trim().isEmpty())) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin khách lẻ!");
                return;
            }
            if (radioThanhVien.isSelected()) {
                List<KhachHang> dsKH = daoKhachHang.findByPhone(txtSDT.getText());
                if (dsKH.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Không tìm thấy thành viên với SĐT này!");
                    return;
                }
            }
            // Tạo NhanVien hiện tại
            NhanVien nvHienTai = new NhanVien();
            nvHienTai.setMaNV("NV001");
            nvHienTai.setTenNhanVien("Nhân viên hiện tại");
            
            KhachHang kh;
            if (radioThanhVien.isSelected()) {
                kh = daoKhachHang.findByPhone(txtSDT.getText()).get(0);
            } else {
                kh = new KhachHang("KHLE" + System.currentTimeMillis(), txtTenKH.getText(), txtSDT.getText(), txtEmail.getText(), 0, false);
                daoKhachHang.insertKhachHang(kh);
            }
            
            LocalDateTime thoiGianDen = LocalDateTime.parse(txtThoiGian.getText(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            int soNguoi = Integer.parseInt(txtSoNguoi.getText());
            // Tìm bàn phù hợp
            List<Ban> danhSachBan = banTheoTang.get(currentFloor);
            Ban banChon = null;
            LocalDateTime endTime = thoiGianDen.plusHours(2);
            List<PhieuDatBan> danhSachPhieu = daoDatBan.getPhieuDatBanByTimeRange(thoiGianDen, endTime);
            for (Ban ban : danhSachBan) {
                if (ban.getSoCho() >= soNguoi && !isTableBookedInTimeSlot(ban.getMaBan(), danhSachPhieu, thoiGianDen, endTime)) {
                    banChon = ban;
                    break;
                }
            }
            if (banChon == null) {
                JOptionPane.showMessageDialog(dialog, "Không tìm thấy bàn phù hợp!");
                return;
            }
            PhieuDatBan phieu = new PhieuDatBan("PDB" + System.currentTimeMillis(), thoiGianDen, kh, nvHienTai, banChon, 100000);
            if (daoDatBan.addPhieuDatBan(phieu)) {
                JOptionPane.showMessageDialog(dialog, "Đặt bàn thành công!");
                dialog.dispose();
                // Refresh data sau khi đặt
                initializeTableData();
                updateTablesDisplay();
            } else {
                JOptionPane.showMessageDialog(dialog, "Đặt bàn thất bại!");
            }
        });
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(btnXacNhan, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void showCancelDialogForBan(Ban ban) {
        PhieuDatBan phieu = daoDatBan.getPhieuByBan(ban.getMaBan());
        if (phieu == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đặt bàn cho bàn này!");
            return;
        }
        String lyDo = JOptionPane.showInputDialog(this, "Lý do hủy đặt bàn cho " + ban.getMaBan() + ":", "Lý do hủy", JOptionPane.QUESTION_MESSAGE);
        if (lyDo != null && !lyDo.trim().isEmpty()) {
            if (daoDatBan.cancelPhieuDatBan(phieu.getMaPhieu(), lyDo)) {
                JOptionPane.showMessageDialog(this, "Hủy đặt bàn thành công!");
                // Refresh data sau khi hủy
                initializeTableData();
                updateTablesDisplay();
            } else {
                JOptionPane.showMessageDialog(this, "Hủy đặt bàn thất bại!");
            }
        }
    }
    
    private void showEditDialogForBan(Ban ban) {
        PhieuDatBan phieu = daoDatBan.getPhieuByBan(ban.getMaBan());
        if (phieu == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy phiếu đặt bàn cho bàn này!");
            return;
        }
        EditPhieuDatBanDialog.showEditDialog((JFrame) SwingUtilities.getWindowAncestor(this), null, daoDatBan, phieu);
        // Refresh sau edit
        initializeTableData();
        updateTablesDisplay();
    }
    
    private void showCancelDialog() {
        // Fallback general cancel
        String sdt = JOptionPane.showInputDialog(this, "Nhập SĐT khách hàng:");
        if (sdt != null && !sdt.trim().isEmpty()) {
            List<PhieuDatBan> danhSachPhieu = daoDatBan.searchByPhone(sdt);
            if (danhSachPhieu.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin đặt bàn!");
                return;
            }
            // Hiển thị danh sách, chọn để hủy
            String maPhieuChon = (String) JOptionPane.showInputDialog(this, "Chọn phiếu để hủy:", "Danh sách phiếu", JOptionPane.QUESTION_MESSAGE, null, 
                danhSachPhieu.stream().map(PhieuDatBan::getMaPhieu).toArray(), danhSachPhieu.get(0).getMaPhieu());
            if (maPhieuChon != null) {
                String lyDo = JOptionPane.showInputDialog(this, "Lý do hủy:");
                if (daoDatBan.cancelPhieuDatBan(maPhieuChon, lyDo)) {
                    JOptionPane.showMessageDialog(this, "Hủy đặt bàn thành công!");
                    // Refresh data sau khi hủy
                    initializeTableData();
                    updateTablesDisplay();
                } else {
                    JOptionPane.showMessageDialog(this, "Hủy đặt bàn thất bại!");
                }
            }
        }
    }
    
    private void showEditDialog() {
        // Fallback general edit
        String sdt = JOptionPane.showInputDialog(this, "Nhập SĐT khách hàng:");
        if (sdt != null && !sdt.trim().isEmpty()) {
            List<PhieuDatBan> danhSachPhieu = daoDatBan.searchByPhone(sdt);
            if (danhSachPhieu.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin đặt bàn!");
                return;
            }
            // Hiển thị danh sách, chọn phiếu
            String maPhieuChon = (String) JOptionPane.showInputDialog(this, "Chọn phiếu để sửa:", "Danh sách phiếu", JOptionPane.QUESTION_MESSAGE, null, 
                danhSachPhieu.stream().map(PhieuDatBan::getMaPhieu).toArray(), danhSachPhieu.get(0).getMaPhieu());
            if (maPhieuChon != null) {
                PhieuDatBan phieuCu = daoDatBan.getPhieuDatBanById(maPhieuChon);
                EditPhieuDatBanDialog.showEditDialog((JFrame) SwingUtilities.getWindowAncestor(this), null, daoDatBan, phieuCu);
                // Refresh sau edit
                initializeTableData();
                updateTablesDisplay();
            }
        }
    }
    
    // Các method khác giữ nguyên hoặc chỉnh sửa tương tự như code gốc
    // Ví dụ: updateTablesDisplay() để hiển thị chỉ bàn TRONG/DA_DUOC_DAT cho tương lai
    
    public void updateTablesDisplay() {
        updateTablesDisplay(null); // Không filter
    }
    
    private void updateTablesDisplay(List<Ban> danhSachBanLoc) {
        bangPanel.removeAll();
        
        List<Ban> danhSachBan = danhSachBanLoc != null ? danhSachBanLoc : banTheoTang.get(currentFloor);
        if (danhSachBan != null) {
            for (Ban ban : danhSachBan) {
                bangPanel.add(createTableCard(ban, ban.getTrangThai()));
            }
        }
        
        bangPanel.revalidate();
        bangPanel.repaint();
    }
    
    private JPanel createTableCard(Ban ban, TrangThaiBan trangThai) {
        JPanel the = new JPanel(new BorderLayout(0, 10));
        the.setCursor(new Cursor(Cursor.HAND_CURSOR));
        the.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 2, true),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        
        Color mauNen = trangThai.getColor();
        the.setBackground(mauNen);
        
        JLabel nhanTen = new JLabel(ban.getMaBan());
        nhanTen.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nhanTen.setForeground(Color.WHITE);
        nhanTen.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel nhanSucChua = new JLabel(ban.getSoCho() + " chỗ");
        nhanSucChua.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        nhanSucChua.setForeground(new Color(255, 255, 255, 200));
        nhanSucChua.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(nhanTen, BorderLayout.CENTER);
        contentPanel.add(nhanSucChua, BorderLayout.SOUTH);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.setOpaque(false);
        buttonPanel.setVisible(false);
        
        if (trangThai == TrangThaiBan.DA_DAT) {
            JButton btnHuy = new JButton("Hủy đặt bàn");
            btnHuy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnHuy.setPreferredSize(new Dimension(80, 25));
            btnHuy.addActionListener(e -> showCancelDialogForBan(ban));
            
            JButton btnCapNhat = new JButton("Cập nhật");
            btnCapNhat.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnCapNhat.setPreferredSize(new Dimension(80, 25));
            btnCapNhat.addActionListener(e -> showEditDialogForBan(ban));
            
            buttonPanel.add(btnHuy);
            buttonPanel.add(btnCapNhat);
        } else if (trangThai == TrangThaiBan.TRONG) {
            JButton btnDat = new JButton("Tạo phiếu đặt bàn");
            btnDat.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btnDat.setPreferredSize(new Dimension(120, 25));
            btnDat.addActionListener(e -> showBookingDialogForTable(ban));
            
            buttonPanel.add(btnDat);
        }
        
        the.add(contentPanel, BorderLayout.CENTER);
        the.add(buttonPanel, BorderLayout.SOUTH);
        
        // Hover effect
        the.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                the.setBackground(mauNen.darker());
                buttonPanel.setVisible(true);
            }
            public void mouseExited(MouseEvent e) {
                the.setBackground(mauNen);
                buttonPanel.setVisible(false);
            }
            public void mouseClicked(MouseEvent e) {
                // Fallback click logic
                if (trangThai == TrangThaiBan.TRONG) {
                    showBookingDialogForTable(ban);
                } else {
                    JOptionPane.showMessageDialog(DatBan_View.this, 
                        "Quản lý bàn: " + ban.getMaBan() + "\nTrạng thái: " + trangThai.toString(),
                        "Chi tiết bàn",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        return the;
    }
    
    private void showBookingDialogForTable(Ban ban) {
        // Modify showBookingDialog to preselect ban, but for now call general and note
        showBookingDialog(); // Can extend to pass ban and auto select
    }
    
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);
        
        // Map header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel mapTitle = new JLabel("📍 Sơ đồ bàn - " + currentFloor);
        mapTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        mapTitle.setForeground(new Color(60, 60, 60));
        mapTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel mapSubtitle = new JLabel("Click vào bàn để đặt bàn hoặc thay đổi trạng thái, bàn đã đặt có khách, bàn được đặt trước");
        mapSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        mapSubtitle.setForeground(new Color(120, 120, 120));
        mapSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(mapTitle);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(mapSubtitle);
        
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        
        // Tables grid
        bangPanel = new JPanel(new GridLayout(0, 3, 15, 15));
        bangPanel.setOpaque(false);
        
        updateTablesDisplay();
        
        JPanel tablesContainer = new JPanel(new BorderLayout());
        tablesContainer.setBackground(Color.WHITE);
        tablesContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1, true),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        tablesContainer.add(bangPanel, BorderLayout.CENTER);
        
        // Legend
        JPanel legendPanel = createLegendPanel();
        tablesContainer.add(legendPanel, BorderLayout.SOUTH);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(tablesContainer, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));
        
        panel.add(createLegendItem("Trống", TrangThaiBan.TRONG.getColor()));
        panel.add(createLegendItem("Đã được đặt trước", TrangThaiBan.DA_DAT.getColor()));
        
        return panel;
    }
    
    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        item.setOpaque(false);
        
        JPanel colorBox = new JPanel();
        colorBox.setBackground(color);
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(100, 100, 100));
        
        item.add(colorBox);
        item.add(label);
        
        return item;
    }
}