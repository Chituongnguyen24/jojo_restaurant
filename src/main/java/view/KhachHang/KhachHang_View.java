package view.KhachHang;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import dao.KhachHang_DAO;
import entity.KhachHang;

public class KhachHang_View extends JPanel implements ActionListener {
    private JTable table;
    private DefaultTableModel model;
    private KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    
    private static final String REGEX_TEN = "^[\\p{L} .'-]+$"; 
    private static final String REGEX_SDT = "^(0[0-9]{9,10})$"; 
    private static final String REGEX_EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    
    
    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private JLabel lblTongKH, lblDong, lblVang, lblBac;
    
    private JTextField txtMaKH, txtTenKH, txtSdt, txtEmail, txtNgaySinh, txtDiemTichLuy, txtTimNhanh;
    private JComboBox<String> cboHang, cboGioiTinh, cboFilterHang, cboFilterGioiTinh;
    private JButton btnThem, btnCapNhat, btnXoa, btnXoaRong; 

    private static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 14); 
    private static final Color MAU_TRANG = Color.WHITE;
    private static final Color MAU_VIEN = new Color(222, 226, 230);
    
    private static final Color BG_VIEW = new Color(251, 248, 241); 
    private static final Color COLOR_TITLE = new Color(30, 30, 30);
    private static final Color COLOR_SUBTITLE = new Color(100, 100, 100);
    
    private static final Color COLOR_TONG_KH = new Color(34, 139, 230); 
    private static final Color COLOR_DONG = new Color(76, 175, 80); 
    private static final Color COLOR_BAC = new Color(156, 39, 176); 
    private static final Color COLOR_VANG = new Color(255, 152, 0); 


    public KhachHang_View() {
        setLayout(new BorderLayout());
        setBackground(BG_VIEW); 
        
        ganSuKien(); 

        JPanel pnlHeader = taoPanelHeaderVaThongKe();
        add(pnlHeader, BorderLayout.NORTH);

        JSplitPane pnlContent = taoPanelNoiDungChinh();
        add(pnlContent, BorderLayout.CENTER);

        cboFilterHang.setSelectedIndex(0); 
        cboFilterGioiTinh.setSelectedIndex(0);
        
        loadKhachHangData();
        loadThongKe();
        xoaRong();
    }

    private void ganSuKien() {
        btnThem = createRoundedButton("Thêm", new Color(76, 175, 80), MAU_TRANG);
        btnCapNhat = createRoundedButton("Cập nhật", new Color(34, 139, 230), MAU_TRANG);
        btnXoa = createRoundedButton("Xóa", new Color(244, 67, 54), MAU_TRANG);
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

        JLabel title = new JLabel("Quản lý khách hàng");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        title.setForeground(COLOR_TITLE);
        
        JLabel subtitle = new JLabel("Quản lý thông tin và theo dõi hạng thành viên");
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

        lblTongKH = createStatLabel("0");
        lblDong = createStatLabel("0");
        lblBac = createStatLabel("0");
        lblVang = createStatLabel("0");


        statsPanel.add(createStatBox(lblTongKH, "Tổng KH Thành viên", COLOR_TONG_KH));
        statsPanel.add(createStatBox(lblDong, "Khách hàng hạng Đồng", COLOR_DONG));
        statsPanel.add(createStatBox(lblBac, "Khách hàng hạng Bạc", COLOR_BAC)); 
        statsPanel.add(createStatBox(lblVang, "Khách hàng hạng Vàng", COLOR_VANG));
        
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
        JPanel pnlForm = taoPanelChiTietKhachHang();
        
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
        
        
        String[] cols = {"Mã KH", "Tên KH", "SĐT", "Email", "Ngày sinh", "Giới tính", "Điểm", "Hạng"};
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

        JLabel lblTableTitle = new JLabel("Danh sách khách hàng thành viên");
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
        
        String[] hangOptions = {"Tất cả", "Đồng", "Bạc", "Vàng"};
        cboFilterHang = new JComboBox<>(hangOptions);
        cboFilterHang.setFont(FONT_CHU);
        cboFilterHang.setBorder(new LineBorder(MAU_VIEN, 1, true));
        
        String[] gioiTinhOptions = {"Tất cả", "Nam", "Nữ"};
        cboFilterGioiTinh = new JComboBox<>(gioiTinhOptions);
        cboFilterGioiTinh.setFont(FONT_CHU);
        cboFilterGioiTinh.setBorder(new LineBorder(MAU_VIEN, 1, true));
        
        JLabel lblTimNhanh = new JLabel("Tìm kiếm nhanh:");
        lblTimNhanh.setFont(new Font("Segoe UI", Font.BOLD, 14)); 
        pnlSearch.add(lblTimNhanh);
        pnlSearch.add(txtTimNhanh);
        
        JLabel lblHang = new JLabel("Hạng:");
        lblHang.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlSearch.add(lblHang);
        pnlSearch.add(cboFilterHang);
        
        JLabel lblGioiTinh = new JLabel("Giới tính:");
        lblGioiTinh.setFont(new Font("Segoe UI", Font.BOLD, 14));
        pnlSearch.add(lblGioiTinh);
        pnlSearch.add(cboFilterGioiTinh);
        
        txtTimNhanh.addActionListener(e -> locDuLieu());
        cboFilterHang.addActionListener(e -> locDuLieu());
        cboFilterGioiTinh.addActionListener(e -> locDuLieu());
        
        return pnlSearch;
    }
    
    private JPanel taoPanelChiTietKhachHang() {
        JPanel pnlForm = new RoundedPanel(15, MAU_TRANG);
        pnlForm.setLayout(new BorderLayout());
        pnlForm.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblFormTitle = new JLabel("Thông tin chi tiết Khách hàng");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        lblFormTitle.setForeground(COLOR_TITLE);
        lblFormTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        pnlForm.add(lblFormTitle, BorderLayout.NORTH);
        
        JPanel pnlInput = new JPanel(new GridLayout(0, 2, 10, 10)); 
        pnlInput.setOpaque(false);
        
        // Mã KH
        pnlInput.add(taoFormLabel("Mã KH:"));
        txtMaKH = new JTextField();
        txtMaKH.setEditable(false);
        pnlInput.add(taoInputComponent(txtMaKH, false));
        
        // Tên KH
        pnlInput.add(taoFormLabel("Tên KH:"));
        txtTenKH = new JTextField();
        pnlInput.add(taoInputComponent(txtTenKH, true));
        
        // SĐT
        pnlInput.add(taoFormLabel("SĐT:"));
        txtSdt = new JTextField();
        pnlInput.add(taoInputComponent(txtSdt, true));
        
        // Email
        pnlInput.add(taoFormLabel("Email:"));
        txtEmail = new JTextField();
        pnlInput.add(taoInputComponent(txtEmail, true));
        
        // Ngày sinh
        pnlInput.add(taoFormLabel("Ngày sinh:")); 
        txtNgaySinh = new JTextField();
        pnlInput.add(taoInputComponent(txtNgaySinh, true));
        
        // Điểm tích lũy
        pnlInput.add(taoFormLabel("Điểm tích lũy:"));
        txtDiemTichLuy = new JTextField("0");
        txtDiemTichLuy.setEditable(false);
        pnlInput.add(taoInputComponent(txtDiemTichLuy, false));
        
        // Hạng
        pnlInput.add(taoFormLabel("Hạng:"));
        String[] hangOptions = {"Đồng", "Bạc", "Vàng"};
        cboHang = new JComboBox<>(hangOptions);
        cboHang.setEnabled(false);
        pnlInput.add(taoInputComponent(cboHang, false));
        
        // Giới tính
        pnlInput.add(taoFormLabel("Giới tính:"));
        String[] gioiTinhOptions = {"Nam", "Nữ"};
        cboGioiTinh = new JComboBox<>(gioiTinhOptions);
        pnlInput.add(taoInputComponent(cboGioiTinh, true));


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

    private Component taoInputComponent(JComponent component, boolean isEditable) {
        if (component instanceof JTextField) {
             ((JTextField) component).setEditable(isEditable);
             ((JTextField) component).setFont(FONT_CHU);
        } else if (component instanceof JComboBox) {
             ((JComboBox<?>) component).setEnabled(isEditable);
             ((JComboBox<?>) component).setFont(FONT_CHU);
        }
       
        if (component instanceof JComboBox || component instanceof JTextField) {
            component.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(MAU_VIEN, 1),
                new EmptyBorder(5, 10, 5, 10) 
            ));
        }

        return component;
    }
    
    // --- PHƯƠNG THỨC VALIDATION VÀ KIỂM TRA TRÙNG LẶP ---
    private boolean validateData(boolean isAdding) {
        String maKH = txtMaKH.getText().trim();
        String ten = txtTenKH.getText().trim();
        String sdt = txtSdt.getText().trim();
        String email = txtEmail.getText().trim();
        String ngaySinhStr = txtNgaySinh.getText().trim();
        String currentMaKH = isAdding ? null : maKH; // Mã khách hàng hiện tại để loại trừ khi check trùng

        // 1. Tên (Không rỗng và theo Regex)
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTenKH.requestFocus();
            return false;
        }
        if (!ten.matches(REGEX_TEN)) {
            JOptionPane.showMessageDialog(this, "Tên khách hàng không hợp lệ (Chỉ chứa chữ cái, khoảng trắng).", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTenKH.requestFocus();
            return false;
        }

        // 2. SĐT (Không rỗng, theo Regex và kiểm tra trùng lặp)
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtSdt.requestFocus();
            return false;
        }
        if (!sdt.matches(REGEX_SDT)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại không hợp lệ (10-11 số, bắt đầu bằng 0).", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtSdt.requestFocus();
            return false;
        }
        if (khachHangDAO.isSoDienThoaiExists(sdt, currentMaKH)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại này đã được đăng ký cho khách hàng khác.", "Lỗi trùng lặp", JOptionPane.ERROR_MESSAGE);
            txtSdt.requestFocus();
            return false;
        }

        // 3. Email (Nếu có nhập thì phải hợp lệ và kiểm tra trùng lặp)
        if (!email.isEmpty()) {
            if (!email.matches(REGEX_EMAIL)) {
                JOptionPane.showMessageDialog(this, "Địa chỉ Email không hợp lệ.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                txtEmail.requestFocus();
                return false;
            }
            if (khachHangDAO.isEmailExists(email, currentMaKH)) {
                JOptionPane.showMessageDialog(this, "Email này đã được đăng ký cho khách hàng khác.", "Lỗi trùng lặp", JOptionPane.ERROR_MESSAGE);
                txtEmail.requestFocus();
                return false;
            }
        }

        // 4. Ngày sinh (Nếu có nhập thì phải đúng format dd/MM/yyyy)
        if (!ngaySinhStr.isEmpty()) {
            try {
                LocalDate.parse(ngaySinhStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Ngày sinh không đúng định dạng dd/MM/yyyy.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                txtNgaySinh.requestFocus();
                return false;
            }
        }
        
        return true;
    }

    // =======================================================================
    // == PHẦN XỬ LÝ DỮ LIỆU
    // =======================================================================
    
    private void loadKhachHangData() {
        model.setRowCount(0);
        List<KhachHang> dsKH = khachHangDAO.getAllKhachHang();

        for (KhachHang kh : dsKH) {
            if (kh.isLaThanhVien()) {
                String hang = getHang(kh.getDiemTichLuy());
                
                String gioiTinh;
                if (kh.getGioiTinh() == Boolean.TRUE) { 
                    gioiTinh = "Nam";
                } else {
                    gioiTinh = "Nữ";
                }
                String ngaySinh = (kh.getNgaySinh() != null) ? kh.getNgaySinh().format(DATE_FORMATTER) : "";
                
                model.addRow(new Object[]{
                        kh.getMaKH(), 
                        kh.getTenKH(),
                        kh.getSoDienThoai(),
                        kh.getEmail(),
                        ngaySinh,
                        gioiTinh, 
                        kh.getDiemTichLuy(),
                        hang
                });
            }
        }
    }
    
    public void refreshTableData() {
//        System.out.println("KhachHang_View: Đang làm mới dữ liệu...");
        loadKhachHangData(); 
    }
    
    private void locDuLieu() {
        model.setRowCount(0);
        String keyword = txtTimNhanh.getText().trim();
        String hangFilter = (String) cboFilterHang.getSelectedItem();
        String gioiTinhFilter = (String) cboFilterGioiTinh.getSelectedItem();

        List<KhachHang> dsKH = khachHangDAO.getAllKhachHang();
        
        List<KhachHang> filteredList = dsKH.stream()
            .filter(kh -> {
                if (!kh.isLaThanhVien()) return false;
                
                boolean matchKeyword = keyword.isEmpty() || 
                    kh.getTenKH().toLowerCase().contains(keyword.toLowerCase()) || 
                    (kh.getSoDienThoai() != null && kh.getSoDienThoai().contains(keyword));
                    
                boolean matchHang = "Tất cả".equals(hangFilter) || getHang(kh.getDiemTichLuy()).equals(hangFilter);
                
                boolean matchGioiTinh;
                
                if ("Tất cả".equals(gioiTinhFilter)) {
                    matchGioiTinh = true;
                } else if ("Nam".equals(gioiTinhFilter)) {
                    matchGioiTinh = kh.getGioiTinh() == Boolean.TRUE;
                } else { 
                    matchGioiTinh = kh.getGioiTinh() == Boolean.FALSE || kh.getGioiTinh() == null;
                }   		
                return matchKeyword && matchHang && matchGioiTinh;
            })
            .collect(Collectors.toList());

        for (KhachHang kh : filteredList) {
            String hang = getHang(kh.getDiemTichLuy());
            String gioiTinh = (kh.getGioiTinh() == Boolean.TRUE) ? "Nam" : "Nữ";
            String ngaySinh = (kh.getNgaySinh() != null) ? kh.getNgaySinh().format(DATE_FORMATTER) : "";
            
            model.addRow(new Object[]{
                    kh.getMaKH(), 
                    kh.getTenKH(),
                    kh.getSoDienThoai(),
                    kh.getEmail(),
                    ngaySinh,
                    gioiTinh,
                    kh.getDiemTichLuy(),
                    hang
            });
        }
    }

    private void hienThiLenForm(int row) {
        txtMaKH.setText(model.getValueAt(row, 0).toString());
        txtTenKH.setText(model.getValueAt(row, 1).toString());
        txtSdt.setText(model.getValueAt(row, 2).toString());
        txtEmail.setText(model.getValueAt(row, 3).toString());
        txtNgaySinh.setText(model.getValueAt(row, 4).toString());
        txtDiemTichLuy.setText(model.getValueAt(row, 6).toString());
        
        String gioiTinh = model.getValueAt(row, 5).toString();
        cboGioiTinh.setSelectedItem(gioiTinh); 
        
        String hang = model.getValueAt(row, 7).toString();
        cboHang.setSelectedItem(hang);
    }
    
    private KhachHang getKhachHangFromForm() {
        String maKH = txtMaKH.getText().trim();
        String tenKH = txtTenKH.getText().trim();
        String sdt = txtSdt.getText().trim();
        String email = txtEmail.getText().trim();
        
        LocalDate ngaySinh = null;
        try {
            ngaySinh = LocalDate.parse(txtNgaySinh.getText().trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
        }
        
        int diemTichLuy = 0;
        try {
            diemTichLuy = Integer.parseInt(txtDiemTichLuy.getText().trim());
        } catch (NumberFormatException e) {
        }
        
        Boolean gioiTinh = "Nam".equals(cboGioiTinh.getSelectedItem());
        
        if (maKH.equals("KH00000000")) return null;
        
        return new KhachHang(maKH, tenKH, sdt, email, ngaySinh, gioiTinh, diemTichLuy, true);
    }
    
    private void themKhachHang() {
        if (!validateData(true)) return; 

        String newMaKH = khachHangDAO.taoMaKHMoi();
        
        KhachHang khMoi = getKhachHangFromForm();
        if (khMoi != null) {
            khMoi.setMaKH(newMaKH); 
            khMoi.setDiemTichLuy(0);
            khMoi.setLaThanhVien(true);
            
            if (khachHangDAO.themtKhachHang(khMoi)) {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadKhachHangData();
                loadThongKe();
                xoaRong();
                txtMaKH.setText(khMoi.getMaKH()); 
            } else {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void capNhatKhachHang() {
        if (txtMaKH.getText().trim().isEmpty() || txtMaKH.getText().trim().equals("KH00000000")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần cập nhật!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateData(false)) return; 
        
        KhachHang khCapNhat = getKhachHangFromForm();
        if (khCapNhat != null) {
            khCapNhat.setLaThanhVien(true); 
            
            if (khachHangDAO.capNhatKhachHang(khCapNhat)) {
                JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadKhachHangData();
                loadThongKe();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void xoaKhachHang() {
        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty() || maKH.equals("KH00000000")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận xóa khách hàng [" + maKH + "]? Khách hàng sẽ bị chuyển thành trạng thái vãng lai (LaThanhVien=0).", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (khachHangDAO.xoaKhachHang(maKH)) {
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadKhachHangData();
                loadThongKe();
                xoaRong();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadThongKe() {
        List<KhachHang> dsKH = khachHangDAO.getAllKhachHang();

        int tong = 0;
        int dong = 0, vang = 0, bac = 0;

        for (KhachHang kh : dsKH) {
             if (kh.isLaThanhVien()) { 
                tong++; 
                String h = getHang(kh.getDiemTichLuy()); 
                if ("Đồng".equals(h)) dong++;
                else if ("Vàng".equals(h)) vang++;
                else if ("Bạc".equals(h)) bac++;
            }
        }

        lblTongKH.setText(String.valueOf(tong));
        lblDong.setText(String.valueOf(dong));
        lblVang.setText(String.valueOf(vang));
        lblBac.setText(String.valueOf(bac));
    }

    private String getHang(int diem) {
        return khachHangDAO.xepHangKhachHang(diem);
    }
    
    private void xoaRong() {
        txtMaKH.setText(""); 
        txtTenKH.setText("");
        txtSdt.setText("");
        txtEmail.setText("");
        txtNgaySinh.setText("");
        txtDiemTichLuy.setText("0");
        cboHang.setSelectedItem("Đồng");
        cboGioiTinh.setSelectedIndex(0);
        table.clearSelection();
        txtMaKH.setText(khachHangDAO.taoMaKHMoi()); 
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        
        if (o == btnXoaRong) {
            xoaRong();
        } else if (o == btnThem) {
            themKhachHang();
        } else if (o == btnCapNhat) {
            capNhatKhachHang();
        } else if (o == btnXoa) {
            xoaKhachHang();
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