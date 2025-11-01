package view.ThucDon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import dao.MonAn_DAO;
import entity.MonAn;
import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.Image;

import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.List;

public class ThucDon_View extends JPanel implements ActionListener {
    private JPanel panelDanhSach;
    private MonAn_DAO monAnDAO = new MonAn_DAO();
    
    // UI Components for Search/Filter
    private JTextField txtTimNhanh;
    private JComboBox<String> cboLoaiMonFilter;
    private JComboBox<String> cboTrangThaiFilter;

    // UI Components for CRUD Form (Right Side)
    private JTextField txtMaMonAn, txtTenMonAn, txtDonGia;
    private JComboBox<String> cboLoaiMon;
    private JCheckBox chkTrangThai;
    
    private MonAn monAnDuocChon = null; // Món ăn đang được chọn/hiển thị chi tiết
    
    private JButton btnThem, btnCapNhat, btnXoa, btnXoaRong;

    // Regex cơ bản
    private static final String REGEX_TEN_MON = "^[\\p{L}0-9 .'-]+$";
    private static final String REGEX_DON_GIA = "^[1-9][0-9]*$"; // Số nguyên dương

    private static final Font FONT_CHU = new Font("Segoe UI", Font.PLAIN, 14); 
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color BG_VIEW = new Color(251, 248, 241);
    private static final Color MAU_VIEN = new Color(222, 226, 230);
    private static final Color COLOR_TITLE = new Color(30, 30, 30);


    public ThucDon_View() {
        setLayout(new BorderLayout());
        setBackground(BG_VIEW);

        // Khởi tạo các nút để gán sự kiện
        ganSuKien();
        
        JPanel pnlHeader = taoPanelHeader();
        add(pnlHeader, BorderLayout.NORTH);

        JSplitPane pnlContent = taoPanelNoiDungChinh();
        add(pnlContent, BorderLayout.CENTER);
        
        // Setup Combo Box Loại Món Ăn
        cboLoaiMon.addItem("Chọn loại");
        monAnDAO.getUniqueLoaiMonAn().forEach(cboLoaiMon::addItem);
        
        // Khởi tạo và load dữ liệu lần đầu
        loadMonAn();
        
        // Clear form và tạo mã mới
        xoaRong();
    }
    
    private void ganSuKien() {
        btnThem = new RoundedButton("Thêm", new Color(76, 175, 80), COLOR_WHITE);
        btnCapNhat = new RoundedButton("Cập nhật", new Color(34, 139, 230), COLOR_WHITE);
        btnXoa = new RoundedButton("Hết món", new Color(244, 67, 54), COLOR_WHITE);
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
        
        JLabel lblTitle = new JLabel("Quản lý thực đơn");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(60, 60, 60));
        panelHeader.add(lblTitle, BorderLayout.WEST);
        
        return panelHeader;
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
        
        // Thêm thanh Tìm kiếm và Lọc
        taoPanelTimKiem(pnlTimKiemWrapper);
        
        // Danh sách món ăn
        panelDanhSach = new JPanel(new GridLayout(0, 2, 15, 15));
        panelDanhSach.setBackground(BG_VIEW);
        panelDanhSach.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scroll = new JScrollPane(panelDanhSach);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(BG_VIEW);
        scroll.getViewport().setBackground(BG_VIEW);

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
        
        cboLoaiMonFilter = new JComboBox<>();
        cboLoaiMonFilter.setFont(FONT_CHU);
        cboLoaiMonFilter.setBorder(new LineBorder(MAU_VIEN, 1, true));
        
        String[] trangThaiOptions = {"Tất cả", "Còn bán", "Hết món"};
        cboTrangThaiFilter = new JComboBox<>(trangThaiOptions);
        cboTrangThaiFilter.setFont(FONT_CHU);
        cboTrangThaiFilter.setBorder(new LineBorder(MAU_VIEN, 1, true));
        
        // Đổ dữ liệu LoaiMonAn
        cboLoaiMonFilter.addItem("Tất cả");
        monAnDAO.getUniqueLoaiMonAn().forEach(cboLoaiMonFilter::addItem);
        
        pnlParent.add(new JLabel("Tìm kiếm:"));
        pnlParent.add(txtTimNhanh);
        pnlParent.add(new JLabel("Loại:"));
        pnlParent.add(cboLoaiMonFilter);
        pnlParent.add(new JLabel("Trạng thái:"));
        pnlParent.add(cboTrangThaiFilter);
        
        // Sự kiện lọc
        ActionListener filterAction = e -> loadMonAn();
        txtTimNhanh.addActionListener(filterAction);
        cboLoaiMonFilter.addActionListener(filterAction);
        cboTrangThaiFilter.addActionListener(filterAction);
    }
    
    private JPanel taoPanelCRUDForm() {
        JPanel pnlForm = new RoundedPanel(15, COLOR_WHITE, new BorderLayout());
        pnlForm.setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel lblFormTitle = new JLabel("Thông tin món ăn");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 20)); 
        lblFormTitle.setForeground(COLOR_TITLE);
        lblFormTitle.setBorder(new EmptyBorder(0, 0, 15, 0));
        pnlForm.add(lblFormTitle, BorderLayout.NORTH);
        
        JPanel pnlInput = new JPanel(new GridLayout(6, 2, 10, 10)); // Form 6 hàng 2 cột
        pnlInput.setOpaque(false);
        
        // Mã món
        pnlInput.add(taoFormLabel("Mã món:"));
        txtMaMonAn = createInputText(false);
        pnlInput.add(txtMaMonAn);
        
        // Tên món
        pnlInput.add(taoFormLabel("Tên món:"));
        txtTenMonAn = createInputText(true);
        pnlInput.add(txtTenMonAn);
        
        // Đơn giá
        pnlInput.add(taoFormLabel("Đơn giá:"));
        txtDonGia = createInputText(true);
        pnlInput.add(txtDonGia);

        // Loại món
        pnlInput.add(taoFormLabel("Loại món:"));
        cboLoaiMon = createInputComboBox(true);
        pnlInput.add(cboLoaiMon);
        
        // Trạng thái
        pnlInput.add(taoFormLabel("Trạng thái:"));
        chkTrangThai = new JCheckBox("Còn bán/Có sẵn");
        chkTrangThai.setFont(FONT_CHU);
        pnlInput.add(chkTrangThai);
        
        pnlForm.add(pnlInput, BorderLayout.CENTER);

        // Buttons
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

    // =======================================================================
    // == PHẦN XỬ LÝ DATA/LOGIC VÀ CRUD
    // =======================================================================
    
    private boolean validateData() {
        String ten = txtTenMonAn.getText().trim();
        String giaStr = txtDonGia.getText().trim();
        String loai = (String) cboLoaiMon.getSelectedItem();
        
        // 1. Tên món ăn
        if (ten.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên món ăn không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTenMonAn.requestFocus();
            return false;
        }
        if (!ten.matches(REGEX_TEN_MON)) {
            JOptionPane.showMessageDialog(this, "Tên món ăn không hợp lệ.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtTenMonAn.requestFocus();
            return false;
        }
        
        // 2. Đơn giá
        if (giaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Đơn giá không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtDonGia.requestFocus();
            return false;
        }
        if (!giaStr.matches(REGEX_DON_GIA)) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số nguyên dương.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtDonGia.requestFocus();
            return false;
        }
        
        // 3. Loại món
        if (loai == null || loai.equals("Chọn loại")) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại món ăn.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            cboLoaiMon.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private MonAn getMonAnFromForm() {
        String ma = txtMaMonAn.getText().trim();
        String ten = txtTenMonAn.getText().trim();
        double gia = Double.parseDouble(txtDonGia.getText().trim());
        String loai = (String) cboLoaiMon.getSelectedItem();
        boolean trangThai = chkTrangThai.isSelected();
        
        // Giả sử ImagePath được quản lý ở đây là null/default nếu không có chức năng upload
        // Trong trường hợp này, ta giả định là giữ nguyên imagePath cũ nếu là Update, hoặc để null nếu là Thêm mới.
        String imagePath = (monAnDuocChon != null && monAnDuocChon.getImagePath() != null) 
                            ? monAnDuocChon.getImagePath() : "images/mon an/placeholder.png";

        return new MonAn(ma, ten, gia, trangThai, imagePath, loai);
    }
    
    private void themMonAn() {
        if (!validateData()) return;
        
        String newMa = monAnDAO.getMaMonAnTuDong();
        
        try {
            MonAn monMoi = getMonAnFromForm();
            monMoi.setMaMonAn(newMa); 
            
            if (monAnDAO.themMonAn(monMoi)) {
                JOptionPane.showMessageDialog(this, "Thêm món ăn thành công!");
                loadMonAn();
                xoaRong();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm món ăn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm món: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void capNhatMonAn() {
        if (monAnDuocChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn cần cập nhật!");
            return;
        }
        if (!validateData()) return;

        try {
            MonAn monCapNhat = getMonAnFromForm();
            
            // Giữ lại đường dẫn ảnh gốc (do form này không có chức năng upload)
            monCapNhat.setImagePath(monAnDuocChon.getImagePath());
            
            if (monAnDAO.updateMonAn(monCapNhat)) {
                JOptionPane.showMessageDialog(this, "Cập nhật món ăn thành công!");
                loadMonAn();
                hienThiChiTietMonAn(monAnDAO.getMonAnTheoMa(monCapNhat.getMaMonAn())); // Cập nhật form chi tiết
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật món ăn thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật món: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void hetMonAn(String maMonAn) {
        if (maMonAn.equals("MA0000")) return; // Giả định món default không được xóa
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Xác nhận hết món ăn [" + maMonAn + "]? Món ăn sẽ chuyển sang trạng thái hết món.", 
            "Xác nhận hết món", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // SỬ DỤNG disableMonAn từ DAO đã sửa
            if (monAnDAO.disableMonAn(maMonAn)) {
                JOptionPane.showMessageDialog(this, "Chuyển sang trạng thái hết món thành công!");
                loadMonAn();
                xoaRong(); // Clear form after action
            } else {
                JOptionPane.showMessageDialog(this, "Chuyển sang trạng thái hết món thất bại. Vui lòng kiểm tra log.");
            }
        }
    }
    
    private void xoaRong() {
        monAnDuocChon = null;
        txtMaMonAn.setText(monAnDAO.getMaMonAnTuDong());
        txtTenMonAn.setText("");
        txtDonGia.setText("");
        cboLoaiMon.setSelectedIndex(0);
        chkTrangThai.setSelected(true);
        
        // Thiết lập trạng thái nút
        btnThem.setEnabled(true);
        btnCapNhat.setEnabled(false);
        btnXoa.setEnabled(false);
    }

    //tải danh sách món ăn từ DB
    public void loadMonAn() {
        panelDanhSach.removeAll();
        
        String keyword = txtTimNhanh != null ? txtTimNhanh.getText().trim() : "";
        String trangThaiFilter = cboTrangThaiFilter != null ? (String) cboTrangThaiFilter.getSelectedItem() : "Tất cả";
        String loaiMonAnFilter = cboLoaiMonFilter != null ? (String) cboLoaiMonFilter.getSelectedItem() : "Tất cả";
        
        List<MonAn> list = monAnDAO.searchMonAn(keyword, trangThaiFilter, loaiMonAnFilter);

        for (MonAn mon : list) {
            JPanel card = createMonAnCard(mon);
            panelDanhSach.add(card);
        }

        panelDanhSach.revalidate();
        panelDanhSach.repaint();
    }
    
    private void hienThiChiTietMonAn(MonAn mon) {
        monAnDuocChon = mon;
        
        if (mon == null) {
            xoaRong(); // Dùng xoaRong để clear form và tạo mã mới
            return;
        }
        
        // Đổ dữ liệu lên form
        txtMaMonAn.setText(mon.getMaMonAn());
        txtTenMonAn.setText(mon.getTenMonAn());
        txtDonGia.setText(String.format("%.0f", mon.getDonGia()));
        cboLoaiMon.setSelectedItem(mon.getLoaiMonAn());
        chkTrangThai.setSelected(mon.isTrangThai());
        
        // Thiết lập trạng thái nút
        btnThem.setEnabled(false);
        btnCapNhat.setEnabled(true);
        btnXoa.setEnabled(mon.isTrangThai()); // Chỉ cho phép xóa nếu món đang "Còn bán"
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        Object o = e.getSource();
        
        if (o == btnXoaRong) {
            xoaRong();
        } else if (o == btnThem) {
            themMonAn();
        } else if (o == btnCapNhat) {
            capNhatMonAn();
        } else if (o == btnXoa) {
            if (monAnDuocChon != null) {
                hetMonAn(monAnDuocChon.getMaMonAn());
            } else {
                 JOptionPane.showMessageDialog(this, "Vui lòng chọn món ăn để chuyển sang trạng thái!");
            }
        }
    }

    //tạo thẻ món ăn
    private JPanel createMonAnCard(MonAn mon) {
        RoundedPanel panel = new RoundedPanel(25, Color.WHITE, new BorderLayout(15, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 15));
        panel.setPreferredSize(new Dimension(320, 110)); 
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        //tải ảnh
        ImageIcon icon = loadScaledImage(mon.getImagePath(), 90, 90); 
        JLabel lblImage = new JLabel(icon);
        lblImage.setPreferredSize(new Dimension(90, 90));
        lblImage.setOpaque(false);
        panel.add(lblImage, BorderLayout.WEST);

        //panel thông tin
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false); 

        JLabel lblTen = new JLabel(mon.getTenMonAn());
        lblTen.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTen.setForeground(new Color(60, 60, 60));

        JLabel lblGia = new JLabel(String.format("%,.0f₫", mon.getDonGia()));
        lblGia.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblGia.setForeground(new Color(220, 0, 0));

        JLabel lblLoaiMon = new JLabel("Loại: " + (mon.getLoaiMonAn() != null ? mon.getLoaiMonAn() : "-"));
        lblLoaiMon.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblLoaiMon.setForeground(new Color(100, 100, 100));

        JLabel lblTrangThai = new JLabel(mon.isTrangThai() ? "Có sẵn" : "Hết món");
        lblTrangThai.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblTrangThai.setForeground(mon.isTrangThai() ? new Color(0, 128, 0) : Color.RED);
        
        infoPanel.add(lblTen);
        infoPanel.add(Box.createVerticalGlue()); 
        infoPanel.add(lblGia);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblLoaiMon); 
        infoPanel.add(lblTrangThai);

        panel.add(infoPanel, BorderLayout.CENTER);

        //hover và click
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                hienThiChiTietMonAn(mon); // Hiển thị chi tiết ở panel bên phải
            }
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(245, 245, 245));
                ((RoundedPanel) panel).setBorderColor(new Color(100, 150, 255));
                panel.repaint();
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(Color.WHITE);
                ((RoundedPanel) panel).setBorderColor(new Color(220, 220, 220)); 
                panel.repaint();
            }
        });

        return panel;
    }

    private ImageIcon loadScaledImage(String path, int width, int height) {
        ImageIcon icon = null;
        if (path != null && !path.isEmpty()) {
            icon = new ImageIcon(path); 
        }

        if (icon == null || icon.getIconWidth() == -1) {
            icon = new ImageIcon("images/mon an/placeholder.png"); 
        }

        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }
    
    // =======================================================================
    // == CUSTOM UI COMPONENTS
    // =======================================================================
 
    private class RoundedPanel extends JPanel {
        private int cornerRadius = 25;
        private Color borderColor = new Color(220, 220, 220); // Mặc định
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
}