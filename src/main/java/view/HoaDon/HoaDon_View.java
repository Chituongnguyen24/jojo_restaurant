package view.HoaDon;

import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import entity.HoaDon;
import entity.KhachHang;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.Component;

import java.awt.*;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HoaDon_View extends JPanel implements ActionListener {
    
    private HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    
    private JTable tblDanhSachHD;
    private DefaultTableModel modelDanhSachHD;
    
    private JTextField txtTimNhanh;
    private JComboBox<String> cboTrangThaiFilter;

    private JTextField txtMaHD, txtNgayLap, txtGioVao, txtGioRa, txtTongTien, txtKhachHang, txtNhanVien, txtPhuongThuc, txtTrangThai, txtThue, txtKhuyenMai, txtPhieuDatBan;
    private JButton btnThem, btnCapNhat, btnXoa, btnXoaRong;
    
    private HoaDon hoaDonDuocChon = null;
    
    private JLabel lblChuaTT, lblDaTT, lblTongHD, lblDoanhThu;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    private static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 14); 
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color BG_VIEW = new Color(251, 248, 241);
    private static final Color MAU_VIEN = new Color(222, 226, 230);
    private static final Color COLOR_TITLE = new Color(30, 30, 30);


    private static final Color COLOR_TONG_HD = new Color(34, 139, 230);
    private static final Color COLOR_DA_TT = new Color(76, 175, 80);
    private static final Color COLOR_CHUA_TT = new Color(244, 67, 54);
    private static final Color COLOR_DOANH_THU = new Color(255, 152, 0);

    public HoaDon_View() {
        setLayout(new BorderLayout());
        setBackground(BG_VIEW);

        ganSuKien();
         
        JPanel pnlHeader = taoPanelHeader();
        
        JPanel pnlThongKe = taoPanelThongKe();
        JPanel pnlTopWrapper = new JPanel(new BorderLayout());
        pnlTopWrapper.setOpaque(false);
        pnlTopWrapper.add(pnlHeader, BorderLayout.NORTH);
        pnlTopWrapper.add(pnlThongKe, BorderLayout.CENTER);
        add(pnlTopWrapper, BorderLayout.NORTH);
        
        JSplitPane pnlContent = taoPanelNoiDungChinh();
        add(pnlContent, BorderLayout.CENTER);
         
        loadHoaDon();
        loadThongKe();
         
        xoaRong();
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
     
    private JPanel taoPanelHeader() {
        JPanel panelHeader = new JPanel(new BorderLayout(20, 0));
        panelHeader.setBackground(BG_VIEW);
        panelHeader.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
         
        JLabel lblTitle = new JLabel("Quản lý hóa đơn");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(60, 60, 60));
        panelHeader.add(lblTitle, BorderLayout.WEST);
         
        JButton btnAdd = new RoundedButton("Thanh toán mới", COLOR_DA_TT, COLOR_WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnAdd.addActionListener(e -> {
        	
            loadHoaDon();
            loadThongKe();
        });
        panelHeader.add(btnAdd, BorderLayout.EAST);
         
        return panelHeader;
    }
    
    private JPanel taoPanelThongKe() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(0, 30, 25, 30)); 

        lblChuaTT = createStatLabel("0");
        lblDaTT = createStatLabel("0");
        lblTongHD = createStatLabel("0");
        lblDoanhThu = createStatLabel("0 VNĐ");

        statsPanel.add(createStatBox(lblChuaTT, "Chưa thanh toán", COLOR_CHUA_TT));
        statsPanel.add(createStatBox(lblDaTT, "Đã thanh toán", COLOR_DA_TT));
        statsPanel.add(createStatBox(lblTongHD, "Tổng hóa đơn", COLOR_TONG_HD));
        statsPanel.add(createStatBox(lblDoanhThu, "Doanh thu", COLOR_DOANH_THU));
         
        return statsPanel;
    }
     
    private JSplitPane taoPanelNoiDungChinh() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        splitPane.setDividerLocation(0.65);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setBackground(BG_VIEW);

        JPanel pnlTable = taoPanelTimKiemVaDanhSach();
        JPanel pnlForm = taoPanelCRUDForm();
         
        splitPane.setLeftComponent(pnlTable);
        splitPane.setRightComponent(pnlForm);
         
        return splitPane;
    }
     
    private JPanel taoPanelTimKiemVaDanhSach() {
        JPanel pnlWrapper = new JPanel(new BorderLayout());
        pnlWrapper.setOpaque(false);
        pnlWrapper.setBorder(new EmptyBorder(10, 20, 20, 10));

        JPanel pnlTimKiemWrapper = new RoundedPanel(15, COLOR_WHITE, new FlowLayout(FlowLayout.LEFT, 25, 10));
        pnlTimKiemWrapper.setBorder(new EmptyBorder(10, 15, 10, 15));
         
        taoPanelTimKiem(pnlTimKiemWrapper);
         
        String[] columns = {"Mã HD", "Ngày Lập", "Giờ Vào", "Giờ Ra", "Khách Hàng", "Tổng Tiền", "Trạng Thái"};
        modelDanhSachHD = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tblDanhSachHD = createStyledTable(modelDanhSachHD);
        tblDanhSachHD.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tblDanhSachHD.getSelectedRow();
                if (row != -1) {
                    String maHD = modelDanhSachHD.getValueAt(row, 0).toString();
                    hienThiChiTietHoaDon(hoaDonDAO.findByMaHD(maHD)); 
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblDanhSachHD);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(new LineBorder(MAU_VIEN, 1));
        scroll.setBackground(COLOR_WHITE);
        scroll.getViewport().setBackground(COLOR_WHITE);

        pnlWrapper.add(pnlTimKiemWrapper, BorderLayout.NORTH); 
        pnlWrapper.add(scroll, BorderLayout.CENTER); 
        
        return pnlWrapper;
    }
     
    private void taoPanelTimKiem(JPanel pnlParent) {
         
        txtTimNhanh = new JTextField(15);
        txtTimNhanh.setFont(FONT_CHU);
        txtTimNhanh.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(MAU_VIEN, 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));
         
        cboTrangThaiFilter = new JComboBox<>(new String[]{"Tất cả", "Đã thanh toán", "Chưa thanh toán"});
        cboTrangThaiFilter.setFont(FONT_CHU);
        cboTrangThaiFilter.setBorder(new LineBorder(MAU_VIEN, 1, true));
         
        pnlParent.add(new JLabel("Tìm kiếm:"));
        pnlParent.add(txtTimNhanh);
        pnlParent.add(new JLabel("Trạng thái:"));
        pnlParent.add(cboTrangThaiFilter);
         
        ActionListener filterAction = e -> loadHoaDon();
        txtTimNhanh.addActionListener(filterAction);
        cboTrangThaiFilter.addActionListener(filterAction);
    }
     
    private JPanel taoPanelCRUDForm() {
        JPanel pnlForm = new RoundedPanel(15, COLOR_WHITE, new BorderLayout());
        pnlForm.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblFormTitle = new JLabel("Thông tin hóa đơn");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        lblFormTitle.setForeground(COLOR_TITLE);
        lblFormTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        pnlForm.add(lblFormTitle, BorderLayout.NORTH);
         
        JPanel pnlInput = new JPanel(new GridLayout(6, 2, 10, 10));
        pnlInput.setOpaque(false);
         
        pnlInput.add(taoFormLabel("Mã HD:"));
        txtMaHD = createInputText(false);
        pnlInput.add(txtMaHD);
         
        pnlInput.add(taoFormLabel("Tổng tiền:"));
        txtTongTien = createInputText(true);
        pnlInput.add(txtTongTien);
         
        pnlInput.add(taoFormLabel("Trạng thái:"));
        txtTrangThai = createInputText(true);
        pnlInput.add(txtTrangThai);

        pnlInput.add(taoFormLabel("Ngày lập:"));
        txtNgayLap = createInputText(true);
        pnlInput.add(txtNgayLap);
         
        pnlInput.add(taoFormLabel("Giờ vào:"));
        txtGioVao = createInputText(true);
        pnlInput.add(txtGioVao);
         
        pnlInput.add(taoFormLabel("Giờ ra:"));
        txtGioRa = createInputText(true);
        pnlInput.add(txtGioRa);
         
        pnlInput.add(taoFormLabel("Phương thức:"));
        txtPhuongThuc = createInputText(true);
        pnlInput.add(txtPhuongThuc);
         
        pnlInput.add(taoFormLabel("Khách hàng:"));
        txtKhachHang = createInputText(true);
        pnlInput.add(txtKhachHang);
         
        pnlInput.add(taoFormLabel("Nhân viên:"));
        txtNhanVien = createInputText(true);
        pnlInput.add(txtNhanVien);
         
        pnlInput.add(taoFormLabel("Thuế:"));
        txtThue = createInputText(true);
        pnlInput.add(txtThue);
         
        pnlInput.add(taoFormLabel("Khuyến mãi:"));
        txtKhuyenMai = createInputText(true);
        pnlInput.add(txtKhuyenMai);
         
        pnlInput.add(taoFormLabel("P. Đặt bàn:"));
        txtPhieuDatBan = createInputText(true);
        pnlInput.add(txtPhieuDatBan);
         
        pnlForm.add(pnlInput, BorderLayout.CENTER);

        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pnlButton.setOpaque(false);
         
        pnlButton.add(btnThem);
        pnlButton.add(btnCapNhat);
        pnlButton.add(btnXoa);
        pnlButton.add(btnXoaRong);
         
        pnlForm.add(pnlButton, BorderLayout.SOUTH);
         
        return pnlForm;
    }
     
    private JLabel taoFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
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

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(FONT_CHU);
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setGridColor(MAU_VIEN);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(COLOR_TITLE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String status = value.toString();
                    if (status.equals("Đã thanh toán")) {
                        c.setForeground(COLOR_DA_TT);
                    } else {
                        c.setForeground(COLOR_CHUA_TT);
                    }
                }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });
        
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        
        return table;
    }
     
    private boolean validateData() {
        String maHD = txtMaHD.getText().trim();
        String tongTienStr = txtTongTien.getText().trim();
        String trangThai = txtTrangThai.getText().trim();
         
        if (maHD.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã hóa đơn không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtMaHD.requestFocus();
            return false;
        }
         
        if (tongTienStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tổng tiền không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTongTien.requestFocus();
            return false;
        }
        try {
            Double.parseDouble(tongTienStr.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tổng tiền phải là số hợp lệ.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTongTien.requestFocus();
            return false;
        }
         
        if (trangThai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Trạng thái không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTrangThai.requestFocus();
            return false;
        }
         
        return true;
    }
     
    private HoaDon getHoaDonFromForm() {
        String maHD = txtMaHD.getText().trim();
        double tongTien = Double.parseDouble(txtTongTien.getText().trim().replaceAll("[^0-9.]", ""));
        
        HoaDon hd = new HoaDon();
        hd.setMaHD(maHD);
        hd.setTongTienTruocThue(tongTien);
        
        return hd;
    }
     
    private void themHoaDon() {
        if (!validateData()) return;
         
        String newMa = hoaDonDAO.generateNewID();
         
        try {
            HoaDon hdMoi = getHoaDonFromForm();
            hdMoi.setMaHD(newMa); 
             
            if (hoaDonDAO.addHoaDon(hdMoi)) {
                JOptionPane.showMessageDialog(this, "Thêm hóa đơn thành công!");
                loadHoaDon();
                xoaRong();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm hóa đơn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
     
    private void capNhatHoaDon() {
        if (hoaDonDuocChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần cập nhật!");
            return;
        }
        if (!validateData()) return;

        try {
            HoaDon hdCapNhat = getHoaDonFromForm();
             
            hdCapNhat.setKhuyenMai(hoaDonDuocChon.getKhuyenMai());
             
            if (hoaDonDAO.updateHoaDon(hdCapNhat)) {
                JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn thành công!");
                loadHoaDon();
                loadThongKe();
                hienThiChiTietHoaDon(hoaDonDAO.findByMaHD(hdCapNhat.getMaHD()));
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật hóa đơn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
     
    private void xoaHoaDon(String maHoaDon) {
        if (maHoaDon.equals("HD0000")) return;
         
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận xóa hóa đơn [" + maHoaDon + "]? Hóa đơn sẽ bị xóa vĩnh viễn.", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (hoaDonDAO.deleteHoaDon(maHoaDon)) {
                JOptionPane.showMessageDialog(this, "Xóa hóa đơn thành công!");
                loadHoaDon();
                loadThongKe();
                xoaRong();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa hóa đơn thất bại. Vui lòng kiểm tra log.");
            }
        }
    }
     
    private void xoaRong() {
        hoaDonDuocChon = null;
        txtMaHD.setText(hoaDonDAO.generateNewID());
        txtNgayLap.setText("");
        txtGioVao.setText("");
        txtGioRa.setText("");
        txtTongTien.setText("");
        txtKhachHang.setText("");
        txtNhanVien.setText("");
        txtPhuongThuc.setText("");
        txtTrangThai.setText("");
        txtThue.setText("");
        txtKhuyenMai.setText("");
        txtPhieuDatBan.setText("");
         
        btnThem.setEnabled(true);
        btnCapNhat.setEnabled(false);
        btnXoa.setEnabled(false);
    }

    public void loadHoaDon() {
        modelDanhSachHD.setRowCount(0);
         
        String keyword = txtTimNhanh != null ? txtTimNhanh.getText().trim() : "";
        String trangThaiFilter = cboTrangThaiFilter != null ? (String) cboTrangThaiFilter.getSelectedItem() : "Tất cả";
         
        List<HoaDon> list = hoaDonDAO.getAllHoaDon();

        for (HoaDon hd : list) {
            double tongTien = hoaDonDAO.tinhTongTienHoaDon(hd.getMaHD());
            
            KhachHang kh = (hd.getKhachHang() != null && hd.getKhachHang().getMaKH() != null) 
                       ? khachHangDAO.getKhachHangById(hd.getKhachHang().getMaKH()) : null;
            String tenKH = (kh != null && kh.getTenKH() != null) ? kh.getTenKH() : "Khách lẻ";

            String trangThai = hd.isDaThanhToan() ? "Đã thanh toán" : "Chưa thanh toán";
            
            boolean matchKeyword = keyword.isEmpty() || hd.getMaHD().contains(keyword) || tenKH.contains(keyword);
            boolean matchStatus = "Tất cả".equals(trangThaiFilter) || trangThai.equals(trangThaiFilter);
            
            if (matchKeyword && matchStatus) {
                modelDanhSachHD.addRow(new Object[]{
                    hd.getMaHD(),
                    hd.getNgayLapHoaDon().format(DATE_FORMATTER),
                    hd.getGioVao().format(TIME_FORMATTER),
                    hd.getGioRa() != null ? hd.getGioRa().format(TIME_FORMATTER) : "---",
                    tenKH,
                    String.format("%,.0f₫", tongTien),
                    trangThai
                });
            }
        }
    }
     
    private void loadThongKe() {
        List<HoaDon> dsHD = hoaDonDAO.getAllHoaDon();
        int chuaTT = 0, daTT = 0;
        double doanhThu = 0;
        for (HoaDon hd : dsHD) {
            double tongTien = hoaDonDAO.tinhTongTienHoaDon(hd.getMaHD()); 
            if (hd.isDaThanhToan()) { 
                daTT++; 
                doanhThu += tongTien; 
            }
            else { 
                chuaTT++; 
            }
        }
        lblChuaTT.setText(String.valueOf(chuaTT));
        lblDaTT.setText(String.valueOf(daTT));
        lblTongHD.setText(String.valueOf(dsHD.size()));
        lblDoanhThu.setText(String.format("%,.0f VNĐ", doanhThu));
    }
     
    private void hienThiChiTietHoaDon(HoaDon hd) {
        hoaDonDuocChon = hd;
         
        if (hd == null) {
            xoaRong();
            return;
        }
         
        txtMaHD.setText(hd.getMaHD());
        txtTongTien.setText(String.format("%,.0f", hd.getTongTienTruocThue()));
        txtTrangThai.setText(hd.isDaThanhToan() ? "Đã thanh toán" : "Chưa thanh toán");
        txtNgayLap.setText(hd.getNgayLapHoaDon().format(DATE_FORMATTER));
        txtGioVao.setText(hd.getGioVao().format(TIME_FORMATTER));
        txtGioRa.setText(hd.getGioRa() != null ? hd.getGioRa().format(TIME_FORMATTER) : "Chưa kết thúc");
        txtPhuongThuc.setText(hd.getPhuongThucThanhToan());
        txtKhachHang.setText(hd.getKhachHang() != null ? hd.getKhachHang().getTenKH() : "Khách lẻ");
        txtNhanVien.setText(hd.getNhanVien() != null ? hd.getNhanVien().getHoTen() : "");
        txtThue.setText(hd.getThue() != null ? hd.getThue().getTenThue() : "Không");
        txtKhuyenMai.setText(hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMoTa() : "Không");
        txtPhieuDatBan.setText(hd.getPhieuDatBan() != null ? hd.getPhieuDatBan().getMaPhieu() : "Không");
         
        btnThem.setEnabled(false);
        btnCapNhat.setEnabled(true);
        btnXoa.setEnabled(hd.isDaThanhToan());
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        Object o = e.getSource();
         
        if (o == btnXoaRong) {
            xoaRong();
        } else if (o == btnThem) {
            themHoaDon();
        } else if (o == btnCapNhat) {
            capNhatHoaDon();
        } else if (o == btnXoa) {
            if (hoaDonDuocChon != null) {
                xoaHoaDon(hoaDonDuocChon.getMaHD());
            } else {
                 JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xóa!");
            }
        }
    }

    private ImageIcon loadScaledImage(String path, int width, int height) {
        ImageIcon icon = null;
        if (path != null && !path.isEmpty()) {
            icon = new ImageIcon(path); 
        }

        if (icon == null || icon.getIconWidth() == -1) {
            icon = new ImageIcon("images/hd/placeholder.png"); 
        }

        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
     
    private class RoundedPanel extends JPanel {
        private int cornerRadius = 25;
        private Color borderColor = new Color(220, 220, 220);
        private Color bgColor;

        public RoundedPanel(int radius, Color color, LayoutManager layout) {
            super(layout);
            this.cornerRadius = radius;
            this.bgColor = color;
            setOpaque(false);
        }
         
        public void setBorderColor(Color color) {
            this.borderColor = color;
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
        private int cornerRadius = 20;
        private Color bg, fg;

        public RoundedButton(String text, Color bg, Color fg) {
            super(text);
            this.bg = bg;
            this.fg = fg;
            setContentAreaFilled(false); 
            setFocusPainted(false); 
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setForeground(fg);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Color currentColor = bg;
            if (getModel().isPressed()) {
                currentColor = bg.darker();
            } else if (getModel().isRollover()) {
                currentColor = bg.brighter();
            }
             
            g2.setColor(currentColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
             
            super.paintComponent(g);
            g2.dispose();
        }
    }

    private JLabel createStatLabel(String text) {
        JLabel l = new JLabel(text, JLabel.LEFT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 32));
        l.setForeground(COLOR_WHITE);
        return l;
    }

    private JPanel createStatBox(JLabel valueLabel, String label, Color accent) {
        JPanel box = new RoundedPanel(15, accent, new BorderLayout());
        box.setLayout(new BorderLayout());
        box.setBorder(new EmptyBorder(20, 22, 20, 22));
        JLabel textLabel = new JLabel(label, JLabel.LEFT);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(COLOR_WHITE);
        JPanel inner = new JPanel(new GridLayout(2, 1, 0, 5));
        inner.setOpaque(false);
        inner.add(valueLabel);
        inner.add(textLabel);
        box.add(inner, BorderLayout.CENTER);
        return box;
    }
}