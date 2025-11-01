package view.HoaDon;

import dao.KhuyenMai_DAO;
import entity.KhuyenMai;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class KhuyenMai_View extends JPanel implements ActionListener {

    private JTable table;
    private DefaultTableModel model;
    private KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO();
    
    // ĐÃ SỬA REGEX: Cho phép chữ, số, khoảng trắng và ký tự đặc biệt hợp lệ trong mô tả
    private static final String REGEX_MOTA = "^[\\p{L}0-9 .'-%,()]+$"; 
    // ĐÃ SỬA REGEX: Cho phép số thập phân (0.xxxx) hoặc số nguyên (>= 1, có thể có .xxxx)
    private static final String REGEX_MUCKM = "^(0\\.[0-9]{1,4}|[1-9][0-9]*(\\.[0-9]{1,4})?|0)$"; 

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JLabel lblTongKM, lblHoatDong, lblSapBatDau, lblHetHan;
    
    private JTextField txtMaKM, txtMoTa, txtNgayApDung, txtNgayHetHan, txtMucKM, txtTimNhanh;
    private JComboBox<String> cboLoaiKM, cboLoaiKMFilter, cboTrangThaiFilter;
    private JCheckBox chkTrangThaiKM;
    private JButton btnThem, btnCapNhat, btnXoa, btnXoaRong; 

    private static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 14); 
    private static final Color MAU_TRANG = Color.WHITE;
    private static final Color MAU_VIEN = new Color(222, 226, 230);
    
    private static final Color BG_VIEW = new Color(251, 248, 241); 
    private static final Color COLOR_TITLE = new Color(30, 30, 30);
    private static final Color COLOR_SUBTITLE = new Color(100, 100, 100);
    
    private static final Color COLOR_TONG_KM = new Color(34, 139, 230); 
    private static final Color COLOR_HOAT_DONG = new Color(76, 175, 80); 
    private static final Color COLOR_SAP_BD = new Color(255, 152, 0); 
    private static final Color COLOR_HET_HAN = new Color(156, 39, 176); 


    public KhuyenMai_View() {
        setLayout(new BorderLayout());
        setBackground(BG_VIEW); 
        
        ganSuKien(); 

        JPanel pnlHeader = taoPanelHeaderVaThongKe();
        add(pnlHeader, BorderLayout.NORTH);

        JSplitPane pnlContent = taoPanelNoiDungChinh();
        add(pnlContent, BorderLayout.CENTER);

        // Khởi tạo và điền dữ liệu cho filter/form
        List<String> loaiKMList = khuyenMaiDAO.getUniqueLoaiKhuyenMai();
        cboLoaiKMFilter.addItem("Tất cả");
        loaiKMList.forEach(cboLoaiKMFilter::addItem);
        if (cboLoaiKMFilter.getItemCount() > 0) {
            cboLoaiKMFilter.setSelectedIndex(0);
        }

        cboTrangThaiFilter.setSelectedIndex(0);
        
        // Khởi tạo ComboBox loại KM cho form nhập
        cboLoaiKM.addItem("Chọn loại"); 
        cboLoaiKM.addItem("Theo phần trăm"); 
        cboLoaiKM.addItem("Theo giá trị");
        
        loadKhuyenMaiData();
        loadThongKe();
        xoaRong();
    }

    private void ganSuKien() {
        btnThem = createRoundedButton("Thêm", new Color(76, 175, 80), MAU_TRANG);
        btnCapNhat = createRoundedButton("Cập nhật", new Color(34, 139, 230), MAU_TRANG);
        btnXoa = createRoundedButton("Vô hiệu hóa", new Color(244, 67, 54), MAU_TRANG);
        btnXoaRong = createRoundedButton("Xóa rỗng", new Color(108, 117, 125), MAU_TRANG);
        
        btnThem.addActionListener(this);
        btnCapNhat.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaRong.addActionListener(this);
    }

    private JPanel taoPanelHeaderVaThongKe() {
        JPanel pnlHeaderWrapper = new JPanel(new BorderLayout());
        pnlHeaderWrapper.setOpaque(false);
        pnlHeaderWrapper.setBorder(new EmptyBorder(20, 30, 0, 30));

        JLabel title = new JLabel("Quản lý khuyến mãi");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        title.setForeground(COLOR_TITLE);
        
        JLabel subtitle = new JLabel("Quản lý thông tin và theo dõi các chương trình khuyến mãi");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        subtitle.setForeground(COLOR_SUBTITLE);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        
        pnlHeaderWrapper.add(titlePanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0)); 
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 0, 25, 0)); 

        lblTongKM = createStatLabel("0");
        lblHoatDong = createStatLabel("0");
        lblSapBatDau = createStatLabel("0");
        lblHetHan = createStatLabel("0");


        statsPanel.add(createStatBox(lblTongKM, "Tổng KM", COLOR_TONG_KM));
        statsPanel.add(createStatBox(lblHoatDong, "Hoạt động", COLOR_HOAT_DONG));
        statsPanel.add(createStatBox(lblSapBatDau, "Sắp BD", COLOR_SAP_BD)); 
        statsPanel.add(createStatBox(lblHetHan, "Hết hạn", COLOR_HET_HAN));
        
        pnlHeaderWrapper.add(statsPanel, BorderLayout.CENTER);
        
        return pnlHeaderWrapper;
    }
    
    private JSplitPane taoPanelNoiDungChinh() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        splitPane.setDividerLocation(0.65);
        splitPane.setResizeWeight(0.65);
        splitPane.setBorder(null);
        splitPane.setBackground(BG_VIEW);

        JPanel pnlTable = taoPanelTimKiemVaBang();
        JPanel pnlForm = taoPanelChiTietKhuyenMai();
        
        splitPane.setLeftComponent(pnlTable);
        splitPane.setRightComponent(pnlForm);
        
        return splitPane;
    }
    
    private JPanel taoPanelTimKiemVaBang() {
        JPanel pnlTableWrapper = new JPanel(new BorderLayout());
        pnlTableWrapper.setOpaque(false); 
        pnlTableWrapper.setBorder(new EmptyBorder(10, 30, 30, 15));

        JPanel pnlTimKiemWrapper = new RoundedPanel(15, MAU_TRANG);
        pnlTimKiemWrapper.setLayout(new BorderLayout());
        pnlTimKiemWrapper.setBorder(new EmptyBorder(10, 15, 10, 15)); 
        
        JPanel pnlTimKiemContent = taoPanelTimKiem();
        pnlTimKiemWrapper.add(pnlTimKiemContent, BorderLayout.CENTER);
        
        JPanel tableHeaderPanel = new JPanel(new BorderLayout());
        tableHeaderPanel.setOpaque(false);
        tableHeaderPanel.add(pnlTimKiemWrapper, BorderLayout.NORTH);
        
        
        String[] cols = {"Mã KM", "Mô tả KM", "Giá trị", "Loại KM", "Ngày BĐ", "Ngày KT", "Trạng thái"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(FONT_CHU); 
        
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        tableHeader.setBackground(new Color(248, 249, 250));
        tableHeader.setForeground(new Color(60, 60, 60));
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 40));
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)));
        ((DefaultTableCellRenderer) tableHeader.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    hienThiLenForm(row);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null); 
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        JPanel tableWrapper = new RoundedPanel(15, MAU_TRANG);
        tableWrapper.setLayout(new BorderLayout());
        tableWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        tableWrapper.add(scroll, BorderLayout.CENTER);

        JLabel lblTableTitle = new JLabel("Danh sách chương trình khuyến mãi");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        lblTableTitle.setForeground(COLOR_TITLE);
        lblTableTitle.setBorder(new EmptyBorder(0, 0, 15, 0)); 

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false); 
        tablePanel.add(lblTableTitle, BorderLayout.NORTH);
        tablePanel.add(tableWrapper, BorderLayout.CENTER);
        
        pnlTableWrapper.add(pnlTimKiemWrapper, BorderLayout.NORTH); 
        pnlTableWrapper.add(tablePanel, BorderLayout.CENTER); 
        
        return pnlTableWrapper;
    }
    
    private JPanel taoPanelTimKiem() {
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 10));
        pnlSearch.setOpaque(false); 
        
        txtTimNhanh = new JTextField(20);
        txtTimNhanh.setFont(FONT_CHU);
        txtTimNhanh.setBorder(new LineBorder(MAU_VIEN, 1, true));
        
        cboLoaiKMFilter = new JComboBox<>();
        cboLoaiKMFilter.setFont(FONT_CHU);
        cboLoaiKMFilter.setBorder(new LineBorder(MAU_VIEN, 1, true));
        
        String[] trangThaiOptions = {"Tất cả", "Hoạt động", "Sắp bắt đầu", "Hết hạn", "Không hoạt động"};
        cboTrangThaiFilter = new JComboBox<>(trangThaiOptions);
        cboTrangThaiFilter.setFont(FONT_CHU);
        cboTrangThaiFilter.setBorder(new LineBorder(MAU_VIEN, 1, true));
        
        JLabel lblTimNhanh = new JLabel("Tìm kiếm nhanh:");
        lblTimNhanh.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        pnlSearch.add(lblTimNhanh);
        pnlSearch.add(txtTimNhanh);
        
        JLabel lblLoaiKM = new JLabel("Loại KM:");
        lblLoaiKM.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlSearch.add(lblLoaiKM);
        pnlSearch.add(cboLoaiKMFilter);
        
        JLabel lblTrangThai = new JLabel("Trạng thái:");
        lblTrangThai.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlSearch.add(lblTrangThai);
        pnlSearch.add(cboTrangThaiFilter);
        
        txtTimNhanh.addActionListener(e -> locDuLieu());
        cboLoaiKMFilter.addActionListener(e -> locDuLieu());
        cboTrangThaiFilter.addActionListener(e -> locDuLieu());
        
        return pnlSearch;
    }
    
    private JPanel taoPanelChiTietKhuyenMai() {
        JPanel pnlForm = new RoundedPanel(15, MAU_TRANG);
        pnlForm.setLayout(new BorderLayout());
        pnlForm.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblFormTitle = new JLabel("Thông tin chi tiết Khuyến mãi");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        lblFormTitle.setForeground(COLOR_TITLE);
        lblFormTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        pnlForm.add(lblFormTitle, BorderLayout.NORTH);
        
        JPanel pnlInput = new JPanel(new GridLayout(0, 2, 10, 10)); 
        pnlInput.setOpaque(false);
        
        txtMaKM = taoTextFieldCoLabel(pnlInput, "Mã KM:");
        txtMaKM.setEditable(false);
        
        txtMoTa = taoTextFieldCoLabel(pnlInput, "Mô tả KM:");
        
        txtNgayApDung = taoTextFieldCoLabel(pnlInput, "Ngày áp dụng:");
        
        txtNgayHetHan = taoTextFieldCoLabel(pnlInput, "Ngày hết hạn:");
        
        txtMucKM = taoTextFieldCoLabel(pnlInput, "Mức KM (0.05 hoặc 50000):");
        
        pnlInput.add(taoFormLabel("Loại KM:"));
        cboLoaiKM = new JComboBox<>();
        cboLoaiKM.setFont(FONT_CHU);
        pnlInput.add(taoInputComponent(cboLoaiKM, true));
        
        pnlInput.add(taoFormLabel("Trạng thái KM:"));
        chkTrangThaiKM = new JCheckBox("Kích hoạt");
        chkTrangThaiKM.setFont(FONT_CHU);
        pnlInput.add(chkTrangThaiKM);


        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pnlButton.setOpaque(false);
        pnlButton.add(btnThem);
        pnlButton.add(btnCapNhat);
        pnlButton.add(btnXoa);
        pnlButton.add(btnXoaRong);
        
        pnlForm.add(pnlInput, BorderLayout.CENTER);
        pnlForm.add(pnlButton, BorderLayout.SOUTH);
        
        return pnlForm;
    }
    
    private JLabel taoFormLabel (String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", ABORT, 14)); 
        return label;
    }

    private JTextField taoTextFieldCoLabel(JPanel pnlParent, String labelText) {
        pnlParent.add(taoFormLabel(labelText));
        JTextField field = new JTextField();
        field.setFont(FONT_CHU);
        field.setBorder(new LineBorder(MAU_VIEN, 1));
        pnlParent.add(field);
        return field;
    }
    
    private Component taoInputComponent(JComponent component, boolean isEditable) {
        if (component instanceof JTextField) {
             ((JTextField) component).setEditable(isEditable);
             ((JTextField) component).setFont(FONT_CHU);
        } else if (component instanceof JComboBox) {
             ((JComboBox) component).setEnabled(isEditable);
             ((JComboBox) component).setFont(FONT_CHU);
        }
        
        if (component instanceof JComboBox || component instanceof JTextField) {
            component.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(MAU_VIEN, 1),
                new EmptyBorder(5, 10, 5, 10) 
            ));
        }

        return component;
    }

    private boolean validateData(boolean isAdding) {
        String maKM = txtMaKM.getText().trim();
        String moTa = txtMoTa.getText().trim();
        String ngayApDungStr = txtNgayApDung.getText().trim();
        String ngayHetHanStr = txtNgayHetHan.getText().trim();
        String mucKMStr = txtMucKM.getText().trim();
        String loaiKM = (String) cboLoaiKM.getSelectedItem();

        if (moTa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mô tả khuyến mãi không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtMoTa.requestFocus();
            return false;
        }
        if (!moTa.matches(REGEX_MOTA)) {
            JOptionPane.showMessageDialog(this, "Mô tả không hợp lệ (Chỉ chứa chữ cái, số, khoảng trắng và ký tự '.', '-', '%', '(', ')').", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtMoTa.requestFocus();
            return false;
        }

        if (ngayApDungStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ngày áp dụng không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtNgayApDung.requestFocus();
            return false;
        }
        LocalDate ngayBD = null;
        try {
            ngayBD = LocalDate.parse(ngayApDungStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Ngày áp dụng không đúng định dạng dd/MM/yyyy.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtNgayApDung.requestFocus();
            return false;
        }

        if (ngayHetHanStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ngày hết hạn không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtNgayHetHan.requestFocus();
            return false;
        }
        LocalDate ngayKT = null;
        try {
            ngayKT = LocalDate.parse(ngayHetHanStr, DATE_FORMATTER);
            if (ngayKT.isBefore(ngayBD)) {
                 JOptionPane.showMessageDialog(this, "Ngày hết hạn phải sau hoặc cùng ngày áp dụng.", "Lỗi logic ngày", JOptionPane.ERROR_MESSAGE);
                 txtNgayHetHan.requestFocus();
                 return false;
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Ngày hết hạn không đúng định dạng dd/MM/yyyy.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtNgayHetHan.requestFocus();
            return false;
        }

        if (mucKMStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mức KM không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtMucKM.requestFocus();
            return false;
        }
        if (!mucKMStr.matches(REGEX_MUCKM)) {
              JOptionPane.showMessageDialog(this, "Mức KM không hợp lệ. Phải là số (ví dụ: 0.05, 50, 10000).", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
              txtMucKM.requestFocus();
              return false;
        }

        if (loaiKM == null || loaiKM.isEmpty() || loaiKM.equals("Chọn loại")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại khuyến mãi.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            cboLoaiKM.requestFocus();
            return false;
        }
        
        return true;
    }

    private void loadKhuyenMaiData() {
        model.setRowCount(0);
        // Sửa: Lấy tất cả KM, kể cả Không hoạt động, để việc lọc hiển thị chính xác hơn.
        // KhuyenMai_DAO.getAllKhuyenMai() hiện chỉ lấy trangThaiKM = 1. Giả định bạn đã có phương thức getAllKhuyenMaiIncludeInactive()
        // Tạm thời sử dụng getAllKhuyenMai() và logic lọc/hiển thị trạng thái phía client.
        List<KhuyenMai> dsKM = khuyenMaiDAO.getAllKhuyenMai();

        for (KhuyenMai km : dsKM) {
            String trangThai = getCurrentTrangThai(km);
            String giaTriHienThi;
            
            // Logic hiển thị giá trị: <1.0 là phần trăm, >=1.0 là tiền VNĐ (Giả định)
            if (km.getMucKM() < 1.0) {
                 giaTriHienThi = String.format("%.0f%%", km.getMucKM() * 100);
            } else {
                 giaTriHienThi = String.format("%,.0f VNĐ", km.getMucKM());
            }
            String ngayApDungStr = (km.getNgayApDung() != null) ? km.getNgayApDung().format(DATE_FORMATTER) : "";
            String ngayHetHanStr = (km.getNgayHetHan() != null) ? km.getNgayHetHan().format(DATE_FORMATTER) : "";
            
            model.addRow(new Object[]{
                    km.getMaKM(), 
                    km.getMoTa(),
                    giaTriHienThi,
                    km.getLoaiKM(),
                    ngayApDungStr,
                    ngayHetHanStr,
                    trangThai
            });
        }
    }
    
    private void locDuLieu() {
        model.setRowCount(0);
        String keyword = txtTimNhanh.getText().trim();
        String loaiFilter = (String) cboLoaiKMFilter.getSelectedItem();
        String trangThaiFilter = (String) cboTrangThaiFilter.getSelectedItem();

        // Sửa: Sử dụng lại getAllKhuyenMai() nếu không có phương thức get tất cả (active/inactive)
        List<KhuyenMai> dsKM = khuyenMaiDAO.getAllKhuyenMai();
        
        List<KhuyenMai> filteredList = dsKM.stream()
            .filter(km -> {
                String ttHienTai = getCurrentTrangThai(km);
                String giaTri = String.format("%.0f", km.getMucKM());
                
                // Lọc theo từ khóa
                boolean matchKeyword = keyword.isEmpty() || 
                     km.getMoTa().toLowerCase().contains(keyword.toLowerCase()) || 
                     km.getMaKM().toLowerCase().contains(keyword.toLowerCase()) ||
                     giaTri.contains(keyword);
                     
                // Lọc theo loại KM
                boolean matchLoai = "Tất cả".equals(loaiFilter) || km.getLoaiKM().equals(loaiFilter);
                
                // Lọc theo trạng thái
                boolean matchTrangThai = "Tất cả".equals(trangThaiFilter) || ttHienTai.equals(trangThaiFilter);
                
                return matchKeyword && matchLoai && matchTrangThai;
            })
            .collect(Collectors.toList());

        for (KhuyenMai km : filteredList) {
            String trangThai = getCurrentTrangThai(km);
            String giaTriHienThi;
            if (km.getMucKM() < 1.0) {
                 giaTriHienThi = String.format("%.0f%%", km.getMucKM() * 100);
            } else {
                 giaTriHienThi = String.format("%,.0f VNĐ", km.getMucKM());
            }
            String ngayApDungStr = (km.getNgayApDung() != null) ? km.getNgayApDung().format(DATE_FORMATTER) : "";
            String ngayHetHanStr = (km.getNgayHetHan() != null) ? km.getNgayHetHan().format(DATE_FORMATTER) : "";
            
            model.addRow(new Object[]{
                    km.getMaKM(), 
                    km.getMoTa(),
                    giaTriHienThi,
                    km.getLoaiKM(),
                    ngayApDungStr,
                    ngayHetHanStr,
                    trangThai
            });
        }
    }

    private void hienThiLenForm(int row) {
        String maKM = model.getValueAt(row, 0).toString();
        
        // Sửa: Lấy thông tin KM đầy đủ từ DAO, bao gồm cả trangThaiKM (boolean)
        KhuyenMai km = khuyenMaiDAO.getKhuyenMaiById(maKM);
        if (km == null) {
            xoaRong();
            return;
        }
        
        txtMaKM.setText(km.getMaKM());
        txtMoTa.setText(km.getMoTa());
        
        // SỬA BUG QUAN TRỌNG: Hiển thị giá trị gốc (double) lên form để dễ chỉnh sửa
        txtMucKM.setText(String.valueOf(km.getMucKM())); 
        
        cboLoaiKM.setSelectedItem(km.getLoaiKM());
        
        txtNgayApDung.setText((km.getNgayApDung() != null) ? km.getNgayApDung().format(DATE_FORMATTER) : "");
        txtNgayHetHan.setText((km.getNgayHetHan() != null) ? km.getNgayHetHan().format(DATE_FORMATTER) : "");
        
        // SỬA: Hiển thị trạng thái KM (boolean) từ DB lên CheckBox
        chkTrangThaiKM.setSelected(km.getTrangThaiKM() != null && km.getTrangThaiKM());
    }
    
    // Phương thức này chỉ được dùng để phân tích dữ liệu hiển thị trong bảng
    private double getMucKMFromDisplay(String display) {
        display = display.trim().replace(",", "");
        if (display.endsWith("%")) {
            // Loại bỏ %, chuyển thành số, chia 100 (ví dụ: "5%" -> 0.05)
            try {
                return Double.parseDouble(display.replace("%", "")) / 100.0;
            } catch (NumberFormatException e) {
                return 0.0;
            }
        } else if (display.endsWith("VNĐ")) {
            // Loại bỏ " VNĐ" và dấu phân cách hàng nghìn (ví dụ: "10000 VNĐ" -> 10000.0)
            try {
                return Double.parseDouble(display.replace("VNĐ", "").trim()); 
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }
    
    // Giả định Entity KhuyenMai đã được sửa để có thêm LoaiKM và trangThaiKM
    private KhuyenMai getKhuyenMaiFromForm() {
        String maKM = txtMaKM.getText().trim();
        String moTa = txtMoTa.getText().trim();
        
        LocalDate ngayApDung = null;
        try {
            ngayApDung = LocalDate.parse(txtNgayApDung.getText().trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
        }
        
        LocalDate ngayHetHan = null;
        try {
            ngayHetHan = LocalDate.parse(txtNgayHetHan.getText().trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
        }
        
        double mucKM = 0;
        try {
            mucKM = Double.parseDouble(txtMucKM.getText().trim()); 
        } catch (NumberFormatException e) {
        }
        
        String loaiKM = (String) cboLoaiKM.getSelectedItem();
        Boolean trangThaiKM = chkTrangThaiKM.isSelected();
        
        if (maKM.equals("KM00000000")) return null;
        
        // KhuyenMai(String maKM, String moTa, LocalDate ngayApDung, LocalDate ngayHetHan, double mucKM, Boolean trangThaiKM, String loaiKM)
        return new KhuyenMai(maKM, moTa, ngayApDung, ngayHetHan, mucKM, trangThaiKM, loaiKM); 
    }
    
    private void themKhuyenMai() {
        if (!validateData(true)) return; 

        String newMaKM = khuyenMaiDAO.taoMaKMMoi();
        
        KhuyenMai kmMoi = getKhuyenMaiFromForm();
        if (kmMoi != null) {
            kmMoi.setMaKM(newMaKM); 
            kmMoi.setTrangThaiKM(chkTrangThaiKM.isSelected()); // Lấy trạng thái từ Checkbox
            
            if (khuyenMaiDAO.themKhuyenMai(kmMoi)) {
                JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadKhuyenMaiData();
                loadThongKe();
                // Sửa: Chỉ set lại mã KM mới sau khi thêm thành công, và gọi xoaRong để clear form
                xoaRong(); 
            } else {
                JOptionPane.showMessageDialog(this, "Thêm khuyến mãi thất bại! Vui lòng kiểm tra log.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void capNhatKhuyenMai() {
        if (txtMaKM.getText().trim().isEmpty() || txtMaKM.getText().trim().equals("KM00000000")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần cập nhật!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateData(false)) return; 
        
        KhuyenMai kmCapNhat = getKhuyenMaiFromForm();
        if (kmCapNhat != null) {
            kmCapNhat.setTrangThaiKM(chkTrangThaiKM.isSelected()); 
            
            if (khuyenMaiDAO.capNhatKhuyenMai(kmCapNhat)) {
                JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadKhuyenMaiData();
                loadThongKe();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thất bại! Vui lòng kiểm tra log.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void xoaKhuyenMai() {
        String maKM = txtMaKM.getText().trim();
        if (maKM.isEmpty() || maKM.equals("KM00000000")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần vô hiệu hóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        KhuyenMai km = khuyenMaiDAO.getKhuyenMaiById(maKM);
        if (km != null && !km.getTrangThaiKM()) {
             JOptionPane.showMessageDialog(this, "Khuyến mãi này đã ở trạng thái Không hoạt động.", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
             return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận vô hiệu hóa khuyến mãi [" + maKM + "]? Khuyến mãi sẽ bị chuyển thành trạng thái Không hoạt động (trangThaiKM=0).", 
            "Xác nhận vô hiệu hóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (khuyenMaiDAO.xoaKhuyenMai(maKM)) {
                JOptionPane.showMessageDialog(this, "Vô hiệu hóa khuyến mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadKhuyenMaiData();
                loadThongKe();
                xoaRong();
            } else {
                JOptionPane.showMessageDialog(this, "Vô hiệu hóa khuyến mãi thất bại! Vui lòng kiểm tra log.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private String getCurrentTrangThai(KhuyenMai km) {
        if (km.getMaKM().equals("KM00000000")) return "Luôn áp dụng";
        if (km.getTrangThaiKM() != null && !km.getTrangThaiKM()) return "Không hoạt động"; // Kiểm tra trạng thái DB trước

        LocalDate now = LocalDate.now();
        if (km.getNgayApDung() != null && now.isBefore(km.getNgayApDung())) {
            return "Sắp bắt đầu";
        } else if (km.getNgayHetHan() != null && now.isAfter(km.getNgayHetHan())) {
            return "Hết hạn";
        } else {
            return "Hoạt động";
        }
    }
    
    private void loadThongKe() {
        // Cần đảm bảo DAO có thể lấy được tất cả KM (active/inactive) để thống kê chính xác
        List<KhuyenMai> dsKM = khuyenMaiDAO.getAllKhuyenMai(); 

        int tong = dsKM.size();
        int hoatDong = 0, sapBatDau = 0, hetHan = 0;

        for (KhuyenMai km : dsKM) {
            String tt = getCurrentTrangThai(km);
            if ("Hoạt động".equals(tt) || "Luôn áp dụng".equals(tt)) hoatDong++;
            else if ("Sắp bắt đầu".equals(tt)) sapBatDau++;
            else if ("Hết hạn".equals(tt)) hetHan++;
        }

        lblTongKM.setText(String.valueOf(tong));
        lblHoatDong.setText(String.valueOf(hoatDong));
        lblSapBatDau.setText(String.valueOf(sapBatDau));
        lblHetHan.setText(String.valueOf(hetHan));
    }

    private void xoaRong() {
        txtMoTa.setText("");
        txtNgayApDung.setText("");
        txtNgayHetHan.setText("");
        txtMucKM.setText("");
        cboLoaiKM.setSelectedIndex(0);
        chkTrangThaiKM.setSelected(true);
        table.clearSelection();
        // SỬA: Tạo và gán mã KM mới ngay khi xóa rỗng
        txtMaKM.setText(khuyenMaiDAO.taoMaKMMoi()); 
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        
        if (o == btnXoaRong) {
            xoaRong();
        } else if (o == btnThem) {
            themKhuyenMai();
        } else if (o == btnCapNhat) {
            capNhatKhuyenMai();
        } else if (o == btnXoa) {
            xoaKhuyenMai();
        } 
    }

    private JButton createRoundedButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(100, 35)); 
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        return btn;
    }

    private JLabel createStatLabel(String text) {
        JLabel l = new JLabel(text, JLabel.LEFT);
        l.setFont(new Font("Segoe UI", Font.BOLD, 26)); 
        l.setForeground(MAU_TRANG);
        return l;
    }

    private JPanel createStatBox(JLabel valueLabel, String label, Color accent) {
        JPanel box = new RoundedPanel(15, accent); 
        box.setLayout(new BorderLayout());
        box.setBorder(new EmptyBorder(10, 15, 10, 15)); 

        JLabel textLabel = new JLabel(label, JLabel.LEFT);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(MAU_TRANG);
        
        valueLabel.setForeground(MAU_TRANG);

        JPanel inner = new JPanel(new GridLayout(2, 1, 0, 5)); 
        inner.setOpaque(false);
        inner.add(valueLabel);
        inner.add(textLabel);
        
        box.add(inner, BorderLayout.CENTER);
        return box;
    }
    
    class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color bgColor;

        public RoundedPanel(int radius, Color color) {
            super();
            cornerRadius = radius;
            bgColor = color;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            g2.dispose();
        }
    }
}