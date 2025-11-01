package view.NhanVien;

import dao.NhanVien_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

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

public class NhanVien_View extends JPanel implements ActionListener {
    private JTable table;
    private DefaultTableModel model;
    private NhanVien_DAO nhanVienDAO = new NhanVien_DAO();

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private static final String REGEX_TEN = "^[\\p{L} .'-]+$"; 
    private static final String REGEX_SDT = "^(0[0-9]{9,10})$"; 
    private static final String REGEX_EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String REGEX_CCCD = "^[0-9]{12}$"; 

    private JLabel lblTongNV, lblQuanLy, lblThuNgan; 
    
    // Thêm các trường thiếu: txtNgaySinh, txtNgayVaoLam, txtCCCD
    private JTextField txtMaNV, txtTenNV, txtSdt, txtEmail, txtTenDN, txtTimNhanh, txtNgaySinh, txtNgayVaoLam, txtCCCD;
    private JComboBox<String> cboChucVu, cboGioiTinh, cboFilterChucVu, cboFilterGioiTinh, cboFilterTrangThai;
    private JButton btnThem, btnCapNhat, btnXoa, btnXoaRong, btnDoiMK; 

    private static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 14); 
    private static final Color MAU_TRANG = Color.WHITE;
    private static final Color MAU_VIEN = new Color(222, 226, 230);
    private static final Color BG_VIEW = new Color(251, 248, 241); 
    private static final Color COLOR_TITLE = new Color(30, 30, 30);
    private static final Color COLOR_SUBTITLE = new Color(100, 100, 100);
    
    private static final Color COLOR_TONG_NV = new Color(34, 139, 230); 
    private static final Color COLOR_QL = new Color(255, 152, 0); 
    private static final Color COLOR_TN = new Color(76, 175, 80); 

    public NhanVien_View() {
        setLayout(new BorderLayout());
        setBackground(BG_VIEW); 
        
        ganSuKien(); 

        JPanel pnlHeader = taoPanelHeaderVaThongKe();
        add(pnlHeader, BorderLayout.NORTH);

        JSplitPane pnlContent = taoPanelNoiDungChinh();
        // Giảm kích thước vùng form để xem được toàn bộ cột
        pnlContent.setDividerLocation(0.70); 
        pnlContent.setResizeWeight(0.70);
        add(pnlContent, BorderLayout.CENTER);

        cboFilterChucVu.setSelectedIndex(0); 
        cboFilterGioiTinh.setSelectedIndex(0);
        cboFilterTrangThai.setSelectedIndex(0);
        
        loadNhanVienData();
        loadThongKe();
        xoaRong();
    }

    private void ganSuKien() {
        btnThem = createRoundedButton("Thêm", COLOR_TN, MAU_TRANG);
        btnCapNhat = createRoundedButton("Cập nhật", COLOR_TONG_NV, MAU_TRANG);
        btnXoa = createRoundedButton("Nghỉ việc", new Color(244, 67, 54), MAU_TRANG); 
        btnXoaRong = createRoundedButton("Xóa rỗng", new Color(108, 117, 125), MAU_TRANG);
        btnDoiMK = createRoundedButton("Đổi MK", new Color(156, 39, 176), MAU_TRANG);
        
        btnThem.addActionListener(this);
        btnCapNhat.addActionListener(this);
        btnXoa.addActionListener(this);
        btnXoaRong.addActionListener(this);
        btnDoiMK.addActionListener(this);
    }

    private JPanel taoPanelHeaderVaThongKe() {
        JPanel pnlHeaderWrapper = new JPanel(new BorderLayout());
        pnlHeaderWrapper.setOpaque(false);
        pnlHeaderWrapper.setBorder(new EmptyBorder(20, 30, 0, 30));

        JLabel title = new JLabel("Quản lý Nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        title.setForeground(COLOR_TITLE);
        
        JLabel subtitle = new JLabel("Quản lý thông tin và tài khoản người dùng");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14)); 
        subtitle.setForeground(COLOR_SUBTITLE);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        
        pnlHeaderWrapper.add(titlePanel, BorderLayout.NORTH);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0)); 
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 0, 25, 0)); 

        lblTongNV = createStatLabel("0");
        lblQuanLy = createStatLabel("0");
        lblThuNgan = createStatLabel("0");


        statsPanel.add(createStatBox(lblTongNV, "Tổng Nhân viên", COLOR_TONG_NV));
        statsPanel.add(createStatBox(lblQuanLy, "Chức vụ Quản lý", COLOR_QL));
        statsPanel.add(createStatBox(lblThuNgan, "Chức vụ Thu ngân", COLOR_TN)); 
        
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
        JPanel pnlForm = taoPanelChiTietNhanVien();
        
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
        
        
        // Cập nhật cột: Thêm CCCD, Ngày sinh, Ngày vào làm
        String[] cols = {"Mã NV", "Tên NV", "Giới tính", "SĐT", "CCCD", "Ngày sinh", "Ngày vào làm", "Chức vụ", "Tên ĐN", "Trạng thái"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(FONT_CHU); 
        
        // Thiết lập độ rộng cột nhỏ hơn để hiển thị nhiều cột
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        TableColumnModel tcm = table.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(60); // Mã NV
        tcm.getColumn(1).setPreferredWidth(120); // Tên NV
        tcm.getColumn(2).setPreferredWidth(60); // Giới tính
        tcm.getColumn(3).setPreferredWidth(90); // SĐT
        tcm.getColumn(4).setPreferredWidth(100); // CCCD
        tcm.getColumn(5).setPreferredWidth(90); // Ngày sinh
        tcm.getColumn(6).setPreferredWidth(95); // Ngày vào làm
        tcm.getColumn(7).setPreferredWidth(100); // Chức vụ
        tcm.getColumn(8).setPreferredWidth(90); // Tên ĐN
        tcm.getColumn(9).setPreferredWidth(80); // Trạng thái
        
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

        JLabel lblTableTitle = new JLabel("Danh sách Nhân viên");
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
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        pnlSearch.setOpaque(false); 
        
        txtTimNhanh = new JTextField(15);
        txtTimNhanh.setFont(FONT_CHU);
        txtTimNhanh.setBorder(new LineBorder(MAU_VIEN, 1, true));
        
        String[] chucVuOptions = {"Tất cả", "Quản lý", "Nhân viên thu ngân"};
        cboFilterChucVu = new JComboBox<>(chucVuOptions);
        cboFilterChucVu.setFont(FONT_CHU);
        cboFilterChucVu.setBorder(new LineBorder(MAU_VIEN, 1, true));
        
        String[] gioiTinhOptions = {"Tất cả", "Nam", "Nữ"};
        cboFilterGioiTinh = new JComboBox<>(gioiTinhOptions);
        cboFilterGioiTinh.setFont(FONT_CHU);
        cboFilterGioiTinh.setBorder(new LineBorder(MAU_VIEN, 1, true));
        
        String[] trangThaiOptions = {"Đang làm", "Đã nghỉ"};
        cboFilterTrangThai = new JComboBox<>(trangThaiOptions);
        cboFilterTrangThai.setFont(FONT_CHU);
        cboFilterTrangThai.setBorder(new LineBorder(MAU_VIEN, 1, true));
        
        
        pnlSearch.add(new JLabel("Từ khóa:"));
        pnlSearch.add(txtTimNhanh);
        pnlSearch.add(new JLabel("Chức vụ:"));
        pnlSearch.add(cboFilterChucVu);
        pnlSearch.add(new JLabel("Giới tính:"));
        pnlSearch.add(cboFilterGioiTinh);
        pnlSearch.add(new JLabel("Trạng thái:"));
        pnlSearch.add(cboFilterTrangThai);
        
        txtTimNhanh.addActionListener(e -> locDuLieu());
        cboFilterChucVu.addActionListener(e -> locDuLieu());
        cboFilterGioiTinh.addActionListener(e -> locDuLieu());
        cboFilterTrangThai.addActionListener(e -> locDuLieu());
        
        return pnlSearch;
    }
    
    private JPanel taoPanelChiTietNhanVien() {
        JPanel pnlForm = new RoundedPanel(15, MAU_TRANG);
        pnlForm.setLayout(new BorderLayout());
        pnlForm.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblFormTitle = new JLabel("Thông tin chi tiết Nhân viên");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        lblFormTitle.setForeground(COLOR_TITLE);
        lblFormTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        pnlForm.add(lblFormTitle, BorderLayout.NORTH);
        
        // Đã chuyển sang GridLayout (dạng 2 cột: Label | Input)
        JPanel pnlInput = new JPanel(new GridLayout(0, 2, 10, 8)); 
        pnlInput.setOpaque(false);
        
        // 1. Mã NV
        pnlInput.add(taoFormLabel("Mã NV:"));
        txtMaNV = new JTextField();
        txtMaNV.setEditable(false);
        pnlInput.add(taoInputComponent(txtMaNV, false));
        
        // 2. Tên NV
        pnlInput.add(taoFormLabel("Tên NV (*):"));
        txtTenNV = new JTextField();
        pnlInput.add(taoInputComponent(txtTenNV, true));
        
        // 3. CCCD
        pnlInput.add(taoFormLabel("CCCD (*):"));
        txtCCCD = new JTextField();
        pnlInput.add(taoInputComponent(txtCCCD, true));

        // 4. Ngày sinh
        pnlInput.add(taoFormLabel("Ngày sinh:"));
        txtNgaySinh = new JTextField();
        pnlInput.add(taoInputComponent(txtNgaySinh, true));
        
        // 5. Ngày vào làm
        pnlInput.add(taoFormLabel("Ngày vào làm:"));
        txtNgayVaoLam = new JTextField();
        pnlInput.add(taoInputComponent(txtNgayVaoLam, true));
        
        // 6. Giới tính
        pnlInput.add(taoFormLabel("Giới tính:"));
        String[] gioiTinhOptions = {"Nam", "Nữ"};
        cboGioiTinh = new JComboBox<>(gioiTinhOptions);
        pnlInput.add(taoInputComponent(cboGioiTinh, true));
        
        // 7. SĐT
        pnlInput.add(taoFormLabel("SĐT (*):"));
        txtSdt = new JTextField();
        pnlInput.add(taoInputComponent(txtSdt, true));
        
        // 8. Email
        pnlInput.add(taoFormLabel("Email:"));
        txtEmail = new JTextField();
        pnlInput.add(taoInputComponent(txtEmail, true));
        
        // 9. Chức vụ
        pnlInput.add(taoFormLabel("Chức vụ (*):"));
        String[] chucVuOptions = {"Nhân viên thu ngân", "Quản lý"};
        cboChucVu = new JComboBox<>(chucVuOptions);
        pnlInput.add(taoInputComponent(cboChucVu, true));
        
        // 10. Tên Đăng nhập
        pnlInput.add(taoFormLabel("Tên ĐN:"));
        txtTenDN = new JTextField();
        txtTenDN.setEditable(false);
        pnlInput.add(taoInputComponent(txtTenDN, false));
        
        // (Trống giữ chỗ)
        pnlInput.add(new JLabel("")); 
        pnlInput.add(new JLabel(""));

        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pnlButton.setOpaque(false);
        pnlButton.add(btnThem);
        pnlButton.add(btnCapNhat);
        pnlButton.add(btnXoa);
        pnlButton.add(btnXoaRong);
        pnlButton.add(btnDoiMK); 
        
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
        String maNV = txtMaNV.getText().trim();
        String ten = txtTenNV.getText().trim();
        String sdt = txtSdt.getText().trim();
        String email = txtEmail.getText().trim();
        String cccd = txtCCCD.getText().trim();
        String ngaySinhStr = txtNgaySinh.getText().trim();
        String ngayVaoLamStr = txtNgayVaoLam.getText().trim();

        String currentMaNV = isAdding ? null : maNV; 
        
        // 1. Tên
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên nhân viên không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTenNV.requestFocus();
            return false;
        }
        if (!ten.matches(REGEX_TEN)) {
            JOptionPane.showMessageDialog(this, "Tên nhân viên không hợp lệ (Chỉ chứa chữ cái, khoảng trắng).", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTenNV.requestFocus();
            return false;
        }
        
        // 2. CCCD
        if (cccd.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Số CCCD không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtCCCD.requestFocus();
            return false;
        }
        if (!cccd.matches(REGEX_CCCD)) {
            JOptionPane.showMessageDialog(this, "Số CCCD không hợp lệ (Phải là 12 chữ số).", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtCCCD.requestFocus();
            return false;
        }

        // 3. SĐT
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
        if (nhanVienDAO.isSoDienThoaiExists(sdt, currentMaNV)) {
            JOptionPane.showMessageDialog(this, "Số điện thoại này đã được đăng ký cho nhân viên khác.", "Lỗi trùng lặp", JOptionPane.ERROR_MESSAGE);
            txtSdt.requestFocus();
            return false;
        }

        // 4. Email
        if (!email.isEmpty()) {
            if (!email.matches(REGEX_EMAIL)) {
                JOptionPane.showMessageDialog(this, "Địa chỉ Email không hợp lệ.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                txtEmail.requestFocus();
                return false;
            }
            if (nhanVienDAO.isEmailExists(email, currentMaNV)) {
                JOptionPane.showMessageDialog(this, "Email này đã được đăng ký cho nhân viên khác.", "Lỗi trùng lặp", JOptionPane.ERROR_MESSAGE);
                txtEmail.requestFocus();
                return false;
            }
        }
        
        // 5. Ngày sinh
        if (!ngaySinhStr.isEmpty()) {
            try {
                LocalDate.parse(ngaySinhStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Ngày sinh không đúng định dạng dd/MM/yyyy.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                txtNgaySinh.requestFocus();
                return false;
            }
        }

        // 6. Ngày vào làm
        if (!ngayVaoLamStr.isEmpty()) {
            try {
                LocalDate.parse(ngayVaoLamStr, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Ngày vào làm không đúng định dạng dd/MM/yyyy.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                txtNgayVaoLam.requestFocus();
                return false;
            }
        }
        
        return true;
    }

    
    private void loadNhanVienData() {
        model.setRowCount(0);
        List<NhanVien> dsNV = nhanVienDAO.getAllNhanVien();

        for (NhanVien nv : dsNV) {
            String gioiTinh = (nv.getGioiTinh() == Boolean.TRUE) ? "Nam" : "Nữ";
            String tenDN = nv.getTaiKhoan() != null ? nv.getTaiKhoan().getTenDangNhap() : "Chưa có TK";
            String ngaySinh = nv.getNgaySinh() != null ? nv.getNgaySinh().format(DATE_FORMATTER) : "";
            String ngayVaoLam = nv.getNgayVaoLam() != null ? nv.getNgayVaoLam().format(DATE_FORMATTER) : "";
            
            model.addRow(new Object[]{
                    nv.getMaNhanVien(), 
                    nv.getHoTen(),
                    gioiTinh,
                    nv.getSoDienThoai(),
                    nv.getSoCCCD(),
                    ngaySinh,
                    ngayVaoLam,
                    nv.getChucVu(),
                    tenDN,
                    nv.getTrangThai()
            });
        }
    }
    
    private void locDuLieu() {
        model.setRowCount(0);
        String keyword = txtTimNhanh.getText().trim();
        String chucVuFilter = (String) cboFilterChucVu.getSelectedItem();
        String gioiTinhFilter = (String) cboFilterGioiTinh.getSelectedItem();
        String trangThaiFilter = (String) cboFilterTrangThai.getSelectedItem();

        List<NhanVien> dsNV = nhanVienDAO.getAllNhanVienFull(); 
        
        List<NhanVien> filteredList = dsNV.stream()
            .filter(nv -> {
                String tenDN = nv.getTaiKhoan() != null ? nv.getTaiKhoan().getTenDangNhap() : "Chưa có TK";
                
                boolean matchKeyword = keyword.isEmpty() || 
                    nv.getHoTen().toLowerCase().contains(keyword.toLowerCase()) || 
                    (nv.getSoDienThoai() != null && nv.getSoDienThoai().contains(keyword)) ||
                    (nv.getSoCCCD() != null && nv.getSoCCCD().contains(keyword)) ||
                    tenDN.toLowerCase().contains(keyword.toLowerCase());
                    
                boolean matchChucVu = "Tất cả".equals(chucVuFilter) || nv.getChucVu().equals(chucVuFilter);
                
                boolean matchGioiTinh;
                if ("Tất cả".equals(gioiTinhFilter)) {
                    matchGioiTinh = true;
                } else if ("Nam".equals(gioiTinhFilter)) {
                    matchGioiTinh = nv.getGioiTinh() == Boolean.TRUE;
                } else { 
                    matchGioiTinh = nv.getGioiTinh() == Boolean.FALSE || nv.getGioiTinh() == null;
                }   
                
                boolean matchTrangThai = nv.getTrangThai().equals(trangThaiFilter);
                
                return matchKeyword && matchChucVu && matchGioiTinh && matchTrangThai;
            })
            .collect(Collectors.toList());

        for (NhanVien nv : filteredList) {
            String gioiTinh = (nv.getGioiTinh() == Boolean.TRUE) ? "Nam" : "Nữ";
            String tenDN = nv.getTaiKhoan() != null ? nv.getTaiKhoan().getTenDangNhap() : "Chưa có TK";
            String ngaySinh = nv.getNgaySinh() != null ? nv.getNgaySinh().format(DATE_FORMATTER) : "";
            String ngayVaoLam = nv.getNgayVaoLam() != null ? nv.getNgayVaoLam().format(DATE_FORMATTER) : "";
            
            model.addRow(new Object[]{
                    nv.getMaNhanVien(), 
                    nv.getHoTen(),
                    gioiTinh,
                    nv.getSoDienThoai(),
                    nv.getSoCCCD(),
                    ngaySinh,
                    ngayVaoLam,
                    nv.getChucVu(),
                    tenDN,
                    nv.getTrangThai()
            });
        }
    }

    private void hienThiLenForm(int row) {
        txtMaNV.setText(model.getValueAt(row, 0).toString());
        txtTenNV.setText(model.getValueAt(row, 1).toString());
        txtSdt.setText(model.getValueAt(row, 3).toString());
        txtCCCD.setText(model.getValueAt(row, 4).toString());
        txtNgaySinh.setText(model.getValueAt(row, 5).toString());
        txtNgayVaoLam.setText(model.getValueAt(row, 6).toString());
        txtTenDN.setText(model.getValueAt(row, 8).toString());
        
        txtEmail.setText(model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "");
        
        String gioiTinh = model.getValueAt(row, 2).toString();
        cboGioiTinh.setSelectedItem(gioiTinh); 
        
        String chucVu = model.getValueAt(row, 7).toString();
        cboChucVu.setSelectedItem(chucVu);
        
        if (model.getValueAt(row, 9).toString().equals("Đã nghỉ")) {
             btnXoa.setEnabled(false);
             btnCapNhat.setEnabled(false);
             btnDoiMK.setEnabled(false);
        } else {
             btnXoa.setEnabled(true);
             btnCapNhat.setEnabled(true);
             btnDoiMK.setEnabled(true);
        }
        btnThem.setEnabled(false);
    }
    
    private NhanVien getNhanVienFromForm() {
        String maNV = txtMaNV.getText().trim();
        String tenNV = txtTenNV.getText().trim();
        String sdt = txtSdt.getText().trim();
        String email = txtEmail.getText().trim();
        String cccd = txtCCCD.getText().trim();
        
        LocalDate ngaySinh = null;
        try {
            ngaySinh = LocalDate.parse(txtNgaySinh.getText().trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
        }
        
        LocalDate ngayVaoLam = null;
        try {
            ngayVaoLam = LocalDate.parse(txtNgayVaoLam.getText().trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
        }

        Boolean gioiTinh = "Nam".equals(cboGioiTinh.getSelectedItem());
        String chucVu = (String) cboChucVu.getSelectedItem();
        String trangThai = "Đang làm";
        
        NhanVien nv = new NhanVien(maNV);
        nv.setHoTen(tenNV);
        nv.setSoCCCD(cccd);
        nv.setNgaySinh(ngaySinh);
        nv.setNgayVaoLam(ngayVaoLam);
        nv.setSoDienThoai(sdt);
        nv.setEmail(email);
        nv.setGioiTinh(gioiTinh);
        nv.setChucVu(chucVu);
        nv.setTrangThai(trangThai);
        
        // Cần truyền TaiKhoan (chỉ tên DN) cho hàm thêm/cập nhật
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(txtTenDN.getText().trim());
        nv.setTaiKhoan(tk);
        
        return nv;
    }
    
    private void themNhanVien() {
        if (!validateData(true)) return; 

        String newMaNV = nhanVienDAO.taoMaNVMoi();
        String matKhauMacDinh = "P@ssword123"; 

        NhanVien nvMoi = getNhanVienFromForm();
        if (nvMoi != null) {
            nvMoi.setMaNhanVien(newMaNV); 
            
            if (nhanVienDAO.themNhanVien(nvMoi, matKhauMacDinh)) {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!\nTên đăng nhập: " + nvMoi.getTaiKhoan().getTenDangNhap() + "\nMật khẩu: " + matKhauMacDinh, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadNhanVienData();
                loadThongKe();
                xoaRong();
                txtMaNV.setText(nhanVienDAO.taoMaNVMoi()); 
            } else {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void capNhatNhanVien() {
        if (txtMaNV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần cập nhật!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateData(false)) return; 
        
        NhanVien nvCapNhat = getNhanVienFromForm();
        if (nvCapNhat != null) {
            
            if (nhanVienDAO.capNhatNhanVien(nvCapNhat)) {
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadNhanVienData();
                loadThongKe();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void xoaNhanVien() {
        String maNV = txtMaNV.getText().trim();
        if (maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần chuyển trạng thái nghỉ!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận chuyển nhân viên [" + maNV + "] sang trạng thái NGHỈ VIỆC? (Tài khoản sẽ bị vô hiệu hóa).", 
            "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (nhanVienDAO.xoaNhanVien(maNV)) {
                JOptionPane.showMessageDialog(this, "Chuyển trạng thái nghỉ thành công! Tài khoản đã bị vô hiệu hóa.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadNhanVienData();
                loadThongKe();
                xoaRong();
            } else {
                JOptionPane.showMessageDialog(this, "Chuyển trạng thái nghỉ thất bại! (Có thể nhân viên còn dữ liệu liên quan).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void doiMatKhau() {
        String maNV = txtMaNV.getText().trim();
        String tenDN = txtTenDN.getText().trim();
        if (maNV.isEmpty() || tenDN.equals("Chưa có TK")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần đổi mật khẩu!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Logic đổi mật khẩu sẽ nằm trong lớp Dialog hoặc lớp DAO.
        // Ở đây, ta giả lập thông báo:
        JOptionPane.showMessageDialog(this, "Chức năng đổi mật khẩu đang được phát triển. Vui lòng liên hệ Admin để thực hiện reset.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        
    }
    
    private void loadThongKe() {
        List<NhanVien> dsNV = nhanVienDAO.getAllNhanVien();

        int tong = 0;
        int quanLy = 0, thuNgan = 0;

        for (NhanVien nv : dsNV) {
             // Chỉ đếm những người có tài khoản đang hoạt động (Đang làm)
            if ("Đang làm".equals(nv.getTrangThai())) { 
                tong++;
                if ("Quản lý".equals(nv.getChucVu())) quanLy++;
                else if ("Nhân viên thu ngân".equals(nv.getChucVu())) thuNgan++;
            }
        }

        lblTongNV.setText(String.valueOf(tong));
        lblQuanLy.setText(String.valueOf(quanLy));
        lblThuNgan.setText(String.valueOf(thuNgan));
    }

    private void xoaRong() {
        txtMaNV.setText(nhanVienDAO.taoMaNVMoi()); 
        txtTenNV.setText("");
        txtSdt.setText("");
        txtEmail.setText("");
        txtCCCD.setText("");
        txtNgaySinh.setText("");
        txtNgayVaoLam.setText("");
        txtTenDN.setText("");
        cboChucVu.setSelectedIndex(0);
        cboGioiTinh.setSelectedIndex(0);
        table.clearSelection();
        btnThem.setEnabled(true);
        btnCapNhat.setEnabled(false);
        btnXoa.setEnabled(false);
        btnDoiMK.setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        
        if (o == btnXoaRong) {
            xoaRong();
        } else if (o == btnThem) {
            themNhanVien();
        } else if (o == btnCapNhat) {
            capNhatNhanVien();
        } else if (o == btnXoa) {
            xoaNhanVien();
        } else if (o == btnDoiMK) {
            doiMatKhau();
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