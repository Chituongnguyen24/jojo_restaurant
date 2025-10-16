package view;

import dao.KhachHang_DAO;
import entity.KhachHang;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class KhachHang_View extends JPanel implements ActionListener {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JButton btnAdd, btnFilter;
    private KhachHang_DAO khDAO = new KhachHang_DAO();

    public KhachHang_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(252, 249, 244));

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Quản lý khách hàng");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(40, 30, 20));

        JLabel subtitle = new JLabel("Quản lý thông tin khách hàng");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 90, 80));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        btnAdd = new JButton("+ Thêm khách hàng mới");
        btnAdd.setBackground(new Color(220, 100, 30));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 13));
        btnAdd.setBorder(new EmptyBorder(8, 15, 8, 15));

        header.add(titlePanel, BorderLayout.WEST);
        header.add(btnAdd, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ===== STATS =====
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // Sẽ cập nhật số liệu thực tế từ database
        statsPanel.add(createStatBox("0", "Tổng khách hàng", new Color(255, 153, 51)));
        statsPanel.add(createStatBox("0", "Thành viên VIP", new Color(100, 200, 100)));
        statsPanel.add(createStatBox("0", "Thành viên thường", new Color(100, 150, 200)));
        statsPanel.add(createStatBox("0", "Khách lẻ", new Color(150, 150, 150)));

        // ===== SEARCH =====
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        searchPanel.setOpaque(false);

        searchField = new JTextField("Tìm kiếm khách hàng...");
        searchField.setFont(new Font("Arial", Font.ITALIC, 13));
        searchField.setForeground(Color.GRAY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 120, 40), 2, true),
                new EmptyBorder(5, 10, 5, 10)
        ));


        // Xử lý placeholder
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Tìm kiếm khách hàng...")) {
                    searchField.setText("");
                    searchField.setFont(new Font("Arial", Font.PLAIN, 13));
                    searchField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Tìm kiếm khách hàng...");
                    searchField.setFont(new Font("Arial", Font.ITALIC, 13));
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // Tìm kiếm khi nhấn Enter
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    timKiemKhachHang();
                }
            }
        });

        btnFilter = new JButton("Tất cả các hạng ▾");
        btnFilter.setFocusPainted(false);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(btnFilter, BorderLayout.EAST);

        // ===== TABLE =====
        String[] cols = {"Mã KH", "Tên KH", "SĐT", "Email", "Điểm TL", "Thành viên", "Thao tác"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho phép edit trực tiếp
            }
        };
        
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(10, 20, 20, 20));

        JPanel tablePanel = new JPanel(new BorderLayout());
        JLabel lblTableTitle = new JLabel("Danh sách khách hàng");
        lblTableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTableTitle.setBorder(new EmptyBorder(5, 20, 5, 0));

        JLabel lblTableSub = new JLabel("Quản lý thông tin khách hàng và điểm tích lũy");
        lblTableSub.setFont(new Font("Arial", Font.PLAIN, 13));
        lblTableSub.setForeground(Color.DARK_GRAY);
        lblTableSub.setBorder(new EmptyBorder(0, 20, 10, 0));

        JPanel tblTitlePanel = new JPanel(new GridLayout(2, 1));
        tblTitlePanel.setOpaque(false);
        tblTitlePanel.add(lblTableTitle);
        tblTitlePanel.add(lblTableSub);

        tablePanel.add(tblTitlePanel, BorderLayout.NORTH);
        tablePanel.add(scroll, BorderLayout.CENTER);

        // ===== MAIN CONTENT =====
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(statsPanel, BorderLayout.NORTH);
        content.add(searchPanel, BorderLayout.CENTER);
        content.add(tablePanel, BorderLayout.SOUTH);

        add(content, BorderLayout.CENTER);

        // ===== Đăng ký sự kiện =====
        btnAdd.addActionListener(this);
        btnFilter.addActionListener(this);
        
        // Xử lý double click để sửa
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    suaKhachHang();
                }
            }
        });

        // ===== Load dữ liệu ban đầu =====
        loadDataTable(khDAO.getAllKhachHang());
        updateStats();
    }

    // ===== Hiển thị dữ liệu =====
    private void loadDataTable(List<KhachHang> list) {
        model.setRowCount(0);
        for (KhachHang kh : list) {
            model.addRow(new Object[]{
                    kh.getMaKhachHang(),
                    kh.getTenKhachHang(),
                    kh.getSdt(),
                    kh.getEmail(),
                    kh.getDiemTichLuy(),
                    kh.isLaThanhVien() ? "Có" : "Không",
                    "✎  🗑" // Thao tác
            });
        }
    }

    // ===== Cập nhật thống kê =====
    private void updateStats() {
        List<KhachHang> all = khDAO.getAllKhachHang();
        int total = all.size();
        int vip = 0, regular = 0, guest = 0;
        
        for (KhachHang kh : all) {
            if (kh.isLaThanhVien()) {
                if (kh.getDiemTichLuy() >= 1000) vip++;
                else regular++;
            } else {
                guest++;
            }
        }
        
        // Cập nhật các stat box (cần reference đến các label)
        // Hoặc refresh lại toàn bộ panel
    }

    // ===== Tìm kiếm =====
    private void timKiemKhachHang() {
        String keyword = searchField.getText().trim();
        if (keyword.equals("Tìm kiếm khách hàng...") || keyword.isEmpty()) {
            loadDataTable(khDAO.getAllKhachHang());
        } else {
            loadDataTable(khDAO.timKiemKhachHang(keyword));
        }
    }

    // ===== Sự kiện =====
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(btnAdd)) {
            themKhachHang();
        } else if (o.equals(btnFilter)) {
            // Hiển thị menu filter
            showFilterMenu();
        }
    }

    // ===== Menu filter =====
    private void showFilterMenu() {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem itemAll = new JMenuItem("Tất cả");
        JMenuItem itemVIP = new JMenuItem("Thành viên VIP (≥1000 điểm)");
        JMenuItem itemRegular = new JMenuItem("Thành viên thường");
        JMenuItem itemGuest = new JMenuItem("Khách lẻ");
        
        itemAll.addActionListener(e -> {
            loadDataTable(khDAO.getAllKhachHang());
            btnFilter.setText("Tất cả các hạng ▾");
        });
        
        itemVIP.addActionListener(e -> {
            List<KhachHang> list = khDAO.getAllKhachHang();
            list.removeIf(kh -> !kh.isLaThanhVien() || kh.getDiemTichLuy() < 1000);
            loadDataTable(list);
            btnFilter.setText("Thành viên VIP ▾");
        });
        
        itemRegular.addActionListener(e -> {
            List<KhachHang> list = khDAO.getAllKhachHang();
            list.removeIf(kh -> !kh.isLaThanhVien() || kh.getDiemTichLuy() >= 1000);
            loadDataTable(list);
            btnFilter.setText("Thành viên thường ▾");
        });
        
        itemGuest.addActionListener(e -> {
            List<KhachHang> list = khDAO.getAllKhachHang();
            list.removeIf(KhachHang::isLaThanhVien);
            loadDataTable(list);
            btnFilter.setText("Khách lẻ ▾");
        });
        
        menu.add(itemAll);
        menu.add(itemVIP);
        menu.add(itemRegular);
        menu.add(itemGuest);
        
        menu.show(btnFilter, 0, btnFilter.getHeight());
    }

    // ===== Thêm khách hàng =====
    private void themKhachHang() {
        String ma = JOptionPane.showInputDialog(this, "Nhập mã KH:");
        if (ma == null || ma.trim().isEmpty()) return;

        // Kiểm tra trùng mã
        for (int i = 0; i < model.getRowCount(); i++) {
            if (ma.equals(model.getValueAt(i, 0))) {
                JOptionPane.showMessageDialog(this, "❌ Mã khách hàng đã tồn tại!");
                return;
            }
        }

        String ten = JOptionPane.showInputDialog(this, "Nhập tên KH:");
        if (ten == null || ten.trim().isEmpty()) return;
        
        String sdt = JOptionPane.showInputDialog(this, "Nhập số điện thoại:");
        if (sdt == null || sdt.trim().isEmpty()) return;
        
        String email = JOptionPane.showInputDialog(this, "Nhập email:");
        if (email == null || email.trim().isEmpty()) return;
        
        String diemStr = JOptionPane.showInputDialog(this, "Nhập điểm tích lũy:", "0");
        if (diemStr == null) return;
        
        int diem = 0;
        try {
            diem = Integer.parseInt(diemStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Điểm tích lũy phải là số!");
            return;
        }
        
        boolean laTV = JOptionPane.showConfirmDialog(this, "Là thành viên?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

        KhachHang kh = new KhachHang(ma, ten, sdt, email, diem, laTV);
        if (khDAO.insertKhachHang(kh)) {
            JOptionPane.showMessageDialog(this, "✅ Thêm thành công!");
            loadDataTable(khDAO.getAllKhachHang());
            updateStats();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Thêm thất bại!");
        }
    }

    // ===== Sửa khách hàng =====
    private void suaKhachHang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "⚠ Chọn khách hàng để sửa!");
            return;
        }

        String ma = model.getValueAt(row, 0).toString();
        String ten = JOptionPane.showInputDialog(this, "Tên KH:", model.getValueAt(row, 1));
        if (ten == null) return;
        
        String sdt = JOptionPane.showInputDialog(this, "SĐT:", model.getValueAt(row, 2));
        if (sdt == null) return;
        
        String email = JOptionPane.showInputDialog(this, "Email:", model.getValueAt(row, 3));
        if (email == null) return;
        
        String diemStr = JOptionPane.showInputDialog(this, "Điểm TL:", model.getValueAt(row, 4));
        if (diemStr == null) return;
        
        int diem = 0;
        try {
            diem = Integer.parseInt(diemStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "❌ Điểm tích lũy phải là số!");
            return;
        }
        
        boolean laTV = JOptionPane.showConfirmDialog(this, "Là thành viên?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

        KhachHang kh = new KhachHang(ma, ten, sdt, email, diem, laTV);
        if (khDAO.updateKhachHang(kh)) {
            JOptionPane.showMessageDialog(this, "✅ Cập nhật thành công!");
            loadDataTable(khDAO.getAllKhachHang());
            updateStats();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Cập nhật thất bại!");
        }
    }

    // ===== Xóa khách hàng =====
    private void xoaKhachHang() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "⚠ Chọn khách hàng để xóa!");
            return;
        }

        String ma = model.getValueAt(row, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc muốn xóa khách hàng " + ma + "?", 
                "Xác nhận", JOptionPane.YES_NO_OPTION);
                
        if (confirm == JOptionPane.YES_OPTION) {
            if (khDAO.deleteKhachHang(ma)) {
                JOptionPane.showMessageDialog(this, "🗑 Xóa thành công!");
                loadDataTable(khDAO.getAllKhachHang());
                updateStats();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Xóa thất bại!");
            }
        }
    }
    
    private JPanel createStatBox(String value, String label, Color color) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Color.WHITE);
        box.setBorder(new CompoundBorder(
                new LineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel valLabel = new JLabel(value, JLabel.LEFT);
        valLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valLabel.setForeground(color.darker());

        JLabel textLabel = new JLabel(label, JLabel.LEFT);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 13));

        JPanel inner = new JPanel(new GridLayout(2, 1));
        inner.setOpaque(false);
        inner.add(valLabel);
        inner.add(textLabel);

        box.add(inner, BorderLayout.CENTER);
        return box;
    }
}