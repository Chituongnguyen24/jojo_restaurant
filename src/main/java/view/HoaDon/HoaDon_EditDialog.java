package view.HoaDon;

import dao.*;
import entity.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class HoaDon_EditDialog extends JDialog {
    private JComboBox<KhachHang> cbxKhachHang;
    private JComboBox<NhanVien> cbxNhanVien;
    private JComboBox<Thue> cbxThue;
    private JComboBox<KhuyenMai> cbxKhuyenMai;
    private JComboBox<PhieuDatBan> cbxPhieuDatBan;
    private JComboBox<String> cbxPhuongThuc;
    private JButton btnSave, btnCancel;

    private HoaDon hoaDon;
    private HoaDon_DAO hoaDonDAO;
    private KhachHang_DAO khachHangDAO;
    private NhanVien_DAO nhanVienDAO;
    private HoaDon_Thue_DAO thueDAO;
    private HoaDon_KhuyenMai_DAO khuyenMaiDAO; 
    private DatBan_DAO phieuDatBanDAO;

    private final KhachHang KH_PLACEHOLDER = new KhachHang("KH_NULL", "Khách lẻ", "", "", 0, false);
    private final Thue THUE_PLACEHOLDER = new Thue("THUE_NULL", "Không tính thuế", 0, "", false);
    private final KhuyenMai KM_PLACEHOLDER = new KhuyenMai("KM_NULL", "Không áp dụng KM", 0, null, null, "");
    private final PhieuDatBan PDB_PLACEHOLDER = new PhieuDatBan("PDB_NULL");
	private List<ChiTietHoaDon> chiTietList;
	private Vector<String> monAnVector;
	private JList listMonAn;

    public HoaDon_EditDialog(Frame owner, HoaDon hoaDon, HoaDon_DAO hoaDonDAO) {
        super(owner, "Chỉnh sửa hóa đơn", true);
        this.hoaDon = hoaDon;
        this.hoaDonDAO = hoaDonDAO;

        khachHangDAO = new KhachHang_DAO();
        nhanVienDAO = new NhanVien_DAO();
        thueDAO = new HoaDon_Thue_DAO(); 
        khuyenMaiDAO = new HoaDon_KhuyenMai_DAO(); 
        phieuDatBanDAO = new DatBan_DAO();

        setSize(500, 600);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(250, 246, 238));

        JLabel lblTitle = new JLabel("Chỉnh sửa thông tin hóa đơn", JLabel.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setBorder(new EmptyBorder(15, 10, 15, 10));
        add(lblTitle, BorderLayout.NORTH);
        
        JPanel	pnCen= new JPanel(new BorderLayout());
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        formPanel.setOpaque(false);

        // --- Khách hàng (Optional) ---
        formPanel.add(new JLabel("Khách hàng:"));
        cbxKhachHang = new JComboBox<>();
        cbxKhachHang.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof KhachHang) {
                    setText(((KhachHang) value).getTenKhachHang());
                } else if (value == null){
                     setText("Chọn khách hàng..."); 
                }
                return this;
            }
        });
        formPanel.add(cbxKhachHang);

        // --- Nhân viên (Required) ---
        formPanel.add(new JLabel("Nhân viên (*):"));
        cbxNhanVien = new JComboBox<>();
        // Custom renderer để chỉ hiển thị tên NV
        cbxNhanVien.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof NhanVien) {
                    setText(((NhanVien) value).getTenNhanVien());
                } else if (value == null){
                     setText("Chọn nhân viên...");
                }
                return this;
            }
        });
        formPanel.add(cbxNhanVien);

        // --- Phương thức thanh toán (Required) ---
        formPanel.add(new JLabel("Phương thức (*):"));
        cbxPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ tín dụng", "Chuyển khoản"});
        formPanel.add(cbxPhuongThuc);

        // --- Thuế (Optional) ---
        formPanel.add(new JLabel("Thuế:"));
        cbxThue = new JComboBox<>();
        // Custom renderer để chỉ hiển thị tên Thuế
        cbxThue.setRenderer(new DefaultListCellRenderer() {
             @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Thue) {
                    Thue t = (Thue) value;
                    // Hiển thị tên + % cho rõ
                    setText(String.format("%s (%.1f%%)", t.getTenThue(), t.getTyLeThue() * 100));
                } else if (value == null){
                     setText("Chọn loại thuế...");
                }
                return this;
            }
        });
        formPanel.add(cbxThue);

        formPanel.add(new JLabel("Khuyến mãi:"));
        cbxKhuyenMai = new JComboBox<>();
        formPanel.add(cbxKhuyenMai);
        formPanel.add(new JLabel("Phiếu đặt bàn:"));
        cbxPhieuDatBan = new JComboBox<>();
        cbxPhieuDatBan.setRenderer(new DefaultListCellRenderer() {
             @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof PhieuDatBan) {
                     PhieuDatBan pdb = (PhieuDatBan) value;
                     if(PDB_PLACEHOLDER.equals(pdb)){
                          setText("(Không có)");
                     } else {
                          setText(pdb.getMaPhieu() != null ? pdb.getMaPhieu().trim() : "(Lỗi)");
                     }
                } else if (value == null) {
                    setText("(Không có)");
                }
                return this;
            }
        });
        formPanel.add(cbxPhieuDatBan);
        
        List<ChiTietHoaDon> chiTietList = hoaDonDAO.getChiTietHoaDonForPrint(hoaDon.getMaHoaDon());
        if (chiTietList == null) {
        	chiTietList = new ArrayList<>();
        }

        JPanel monAnPanel = createMonAnListPanel(chiTietList);
        
        pnCen.setBorder(new EmptyBorder(10, 10, 15, 10));
        pnCen.setOpaque(false);
        pnCen.add(formPanel, BorderLayout.CENTER);
        pnCen.add(monAnPanel, BorderLayout.SOUTH);
        add(pnCen,BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setBorder(new EmptyBorder(10, 10, 15, 10));
        btnPanel.setOpaque(false);
        btnSave = new JButton("Lưu thay đổi");
        btnSave.setBackground(new Color(30, 150, 80)); btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 13)); btnSave.addActionListener(this::saveChanges);
        btnCancel = new JButton("Hủy");
        btnCancel.setBackground(new Color(200, 80, 70)); btnCancel.setForeground(Color.WHITE);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 13)); btnCancel.addActionListener(e -> dispose());
        btnPanel.add(btnSave); btnPanel.add(btnCancel);
        add(btnPanel, BorderLayout.SOUTH);

        loadComboBoxData();
        prefillData();
    }

    private void loadComboBoxData() {
        cbxKhachHang.addItem(KH_PLACEHOLDER);
        khachHangDAO.getAllKhachHang().forEach(cbxKhachHang::addItem); 

        nhanVienDAO.getAllNhanVien().forEach(cbxNhanVien::addItem); 

        cbxThue.addItem(THUE_PLACEHOLDER);
        thueDAO.getAllThueActive().forEach(cbxThue::addItem);

        khuyenMaiDAO.getAllKhuyenMai().forEach(cbxKhuyenMai::addItem);

        cbxPhieuDatBan.removeAllItems();
        if (hoaDon != null && hoaDon.getPhieuDatBan() != null && hoaDon.getPhieuDatBan().getMaPhieu() != null) {
             PhieuDatBan pdbChiTiet = phieuDatBanDAO.getPhieuDatBanById(hoaDon.getPhieuDatBan().getMaPhieu());
             if (pdbChiTiet != null) {
                 cbxPhieuDatBan.addItem(pdbChiTiet);
             } else {
                 cbxPhieuDatBan.addItem(hoaDon.getPhieuDatBan());
             }
        } else {
             cbxPhieuDatBan.addItem(PDB_PLACEHOLDER);
        }
    }

    private void prefillData() {
        if (hoaDon == null) return;

        if (hoaDon.getKhachHang() != null && !hoaDon.getKhachHang().getMaKhachHang().trim().equals("KH00000000")) {
            selectComboBoxItem(cbxKhachHang, hoaDon.getKhachHang());
        } else {
            cbxKhachHang.setSelectedItem(KH_PLACEHOLDER);
        }

        selectComboBoxItem(cbxNhanVien, hoaDon.getNhanVien());

        if (hoaDon.getThue() != null) {
            selectComboBoxItem(cbxThue, hoaDon.getThue());
        } else {
             cbxThue.setSelectedItem(THUE_PLACEHOLDER);
        }

        if (hoaDon.getKhuyenMai() != null) {
            selectComboBoxItem(cbxKhuyenMai, hoaDon.getKhuyenMai());
        } else {
             selectComboBoxItemByMaKM(cbxKhuyenMai, "KM00000000"); 
        }

        cbxPhieuDatBan.setEnabled(false);

        // Chọn Phương thức
        if (hoaDon.getPhuongThuc() != null) {
            cbxPhuongThuc.setSelectedItem(hoaDon.getPhuongThuc());
        } else {
            cbxPhuongThuc.setSelectedItem("Tiền mặt"); 
        }
    }

    private <T> void selectComboBoxItem(JComboBox<T> comboBox, T itemToSelect) {
         if (itemToSelect == null) { comboBox.setSelectedIndex(-1); return; }
        ComboBoxModel<T> model = comboBox.getModel(); 
        for (int i = 0; i < model.getSize(); i++) {
            T element = model.getElementAt(i);
            if (element != null && element.equals(itemToSelect)) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
         comboBox.setSelectedIndex(-1); 
    }

    private void selectComboBoxItemByMaKM(JComboBox<KhuyenMai> comboBox, String maKM) {
        ComboBoxModel<KhuyenMai> model = comboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
             KhuyenMai km = model.getElementAt(i);
            if (km != null && km.getMaKM() != null && maKM.equals(km.getMaKM().trim())) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
         comboBox.setSelectedIndex(-1); 
    }
    private JPanel createMonAnListPanel(List<ChiTietHoaDon> chiTietList) {
        JPanel monAnPanel = new JPanel(new BorderLayout());
        monAnPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết món ăn"));
        monAnPanel.setOpaque(false);

        monAnVector = createMonAnVector(chiTietList);
        listMonAn = new JList<>(monAnVector);
        listMonAn.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listMonAn.setLayoutOrientation(JList.VERTICAL);
        listMonAn.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        listMonAn.setBackground(Color.WHITE);

        JScrollPane scrollPaneMonAn = new JScrollPane(listMonAn);
        scrollPaneMonAn.setPreferredSize(new Dimension(400, 150));

        monAnPanel.add(scrollPaneMonAn, BorderLayout.CENTER);
        return monAnPanel;
    }
    private Vector<String> createMonAnVector(List<ChiTietHoaDon> chiTietList) {
        Vector<String> vector = new Vector<>();
        if (chiTietList == null || chiTietList.isEmpty()) {
            vector.add("  (Chưa có món nào trong hóa đơn)");
            return vector;
        }
        // Thêm tiêu đề cột cho JList
        vector.add(String.format("%-4s %-25s %8s %15s", "STT", "Tên món", "SL", "Đơn giá"));
        vector.add("-------------------------------------------------------"); 

        DecimalFormat itemPriceFormatter = new DecimalFormat("###,###"); 

        for (int i = 0; i < chiTietList.size(); i++) {
            ChiTietHoaDon cthd = chiTietList.get(i);
            String tenMon = "N/A";
            double donGia = 0;
            if(cthd.getMonAn() != null) {
                tenMon = cthd.getMonAn().getTenMonAn() != null ? cthd.getMonAn().getTenMonAn() : "Lỗi tên món";
                donGia = cthd.tinhThanhTien();
            }

            // Cắt bớt tên món nếu quá dài
            if (tenMon.length() > 24) {
                tenMon = tenMon.substring(0, 21) + "...";
            }

            vector.add(String.format("%-4d %-25s %8d %15s",
                                     i + 1,
                                     tenMon,
                                     cthd.getSoLuong(),
                                     itemPriceFormatter.format(donGia))); // Format đơn giá
        }
        return vector;
    }

    private void saveChanges(ActionEvent e) {
        // Lấy các giá trị được chọn
        KhachHang khSelected = (KhachHang) cbxKhachHang.getSelectedItem();
        NhanVien nvSelected = (NhanVien) cbxNhanVien.getSelectedItem();
        Thue thueSelected = (Thue) cbxThue.getSelectedItem();
        KhuyenMai kmSelected = (KhuyenMai) cbxKhuyenMai.getSelectedItem();
        String phuongThuc = (String) cbxPhuongThuc.getSelectedItem();

        if (nvSelected == null || phuongThuc == null || phuongThuc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn Nhân viên và Phương thức thanh toán!", "Thiếu thông tin bắt buộc", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {

            if (khSelected == null || KH_PLACEHOLDER.equals(khSelected)) {
                hoaDon.setKhachHang(new KhachHang("KH00000000")); 
            } else {
                hoaDon.setKhachHang(khSelected);
            }

            hoaDon.setNhanVien(nvSelected);

            if (thueSelected == null || THUE_PLACEHOLDER.equals(thueSelected)) {
            } else {
                hoaDon.setThue(thueSelected);
            }

            if (kmSelected == null || KM_PLACEHOLDER.equals(kmSelected) || "KM00000000".equals(kmSelected.getMaKM().trim())) {
                hoaDon.setKhuyenMai(new KhuyenMai("KM00000000")); 
            } else {
                hoaDon.setKhuyenMai(kmSelected);
            }

            hoaDon.setPhuongThuc(phuongThuc);

            // --- Gọi DAO ---
            boolean updated = hoaDonDAO.updateHoaDon(hoaDon);
            if (updated) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!", "Lỗi Cập Nhật", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu: " + ex.getMessage(), "Lỗi Hệ Thống", JOptionPane.ERROR_MESSAGE);
        }
    }
}