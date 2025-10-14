package view.NhanVien;

import dao.NhanVien_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Giao diện tra cứu nhân viên — đồng bộ với NhanVien_View
 */
public class NhanVien_TraCuu_View extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JComboBox<String> cboFilter;
    private NhanVien_DAO nhanVienDAO = new NhanVien_DAO();

    public NhanVien_TraCuu_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(251, 248, 241)); // Nền be #FBF8F1

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 30, 15, 30));

        JLabel title = new JLabel("Tra cứu nhân viên");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(30, 30, 30));

        JLabel subtitle = new JLabel("Tìm kiếm và lọc nhân viên theo vai trò hoặc thông tin cá nhân");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 100, 100));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);
        header.add(titlePanel, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ===== SEARCH & FILTER =====
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(new EmptyBorder(0, 30, 10, 30));

        txtSearch = new JTextField(28);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtSearch.setBorder(new CompoundBorder(
                new LineBorder(new Color(210, 210, 200), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        txtSearch.addActionListener(e -> loadNhanVienData());

        cboFilter = new JComboBox<>(new String[]{"Tất cả", "Quản lý", "Tiếp tân"});
        cboFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cboFilter.setBackground(Color.WHITE);
        cboFilter.setBorder(new LineBorder(new Color(220, 210, 200), 1, true));
        cboFilter.addActionListener(e -> loadNhanVienData());

        JButton btnSearch = createRoundedButton("Tìm kiếm", new Color(34, 139, 230), Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSearch.setPreferredSize(new Dimension(120, 36));
        btnSearch.addActionListener(e -> loadNhanVienData());

        searchPanel.add(new JLabel("Từ khóa:"));
        searchPanel.add(txtSearch);
        searchPanel.add(new JLabel("Vai trò:"));
        searchPanel.add(cboFilter);
        searchPanel.add(btnSearch);

        // ===== TABLE =====
        String[] cols = {"Mã NV", "Tên nhân viên", "Giới tính", "SĐT", "Email", "Tài khoản", "Vai trò"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // chỉ xem
            }
        };

        table = new JTable(model);
        table.setRowHeight(42);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(new Color(230, 240, 255));
        table.setGridColor(new Color(230, 230, 230));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));

        JTableHeader header2 = table.getTableHeader();
        header2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header2.setBackground(new Color(248, 249, 250));
        header2.setForeground(new Color(60, 60, 60));
        header2.setPreferredSize(new Dimension(header2.getWidth(), 45));
        header2.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)));

        // ScrollPane bo góc
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        JPanel tableWrapper = new RoundedPanel(15, Color.WHITE);
        tableWrapper.setLayout(new BorderLayout());
        tableWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        tableWrapper.add(scroll, BorderLayout.CENTER);

        JLabel lblTableTitle = new JLabel("Danh sách nhân viên tra cứu được");
        lblTableTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTableTitle.setForeground(new Color(30, 30, 30));
        lblTableTitle.setBorder(new EmptyBorder(10, 30, 15, 0));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setOpaque(false);
        tablePanel.add(lblTableTitle, BorderLayout.NORTH);
        tablePanel.add(tableWrapper, BorderLayout.CENTER);

        // ===== MAIN CONTENT =====
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.add(searchPanel, BorderLayout.NORTH);
        content.add(tablePanel, BorderLayout.CENTER);

        add(content, BorderLayout.CENTER);

        // Load lần đầu
        loadNhanVienData();
    }

    // ====== Load + filter dữ liệu ======
    private void loadNhanVienData() {
        model.setRowCount(0);
        List<NhanVien> dsNV = nhanVienDAO.getAllNhanVien();

        String keyword = txtSearch.getText().trim().toLowerCase();
        String filter = (String) cboFilter.getSelectedItem();

        List<NhanVien> filtered = dsNV.stream().filter(nv -> {
            TaiKhoan tk = nv.getTaiKhoan();
            String ten = safeLower(nv.getTenNhanVien());
            String sdt = safeLower(nv.getSdt());
            String email = safeLower(nv.getEmail());
            String user = tk != null ? safeLower(tk.getTenDangNhap()) : "";
            String vaiTro = tk != null && tk.getVaiTro() != null ? tk.getVaiTro() : "";
            
            // Hiển thị vai trò
            String roleDisplay = "";
            if (vaiTro.equals("NVQL")) {
                roleDisplay = "quản lý";
            } else if (vaiTro.equals("NVTT")) {
                roleDisplay = "tiếp tân";
            }

            boolean matchKeyword = keyword.isEmpty()
                    || ten.contains(keyword)
                    || sdt.contains(keyword)
                    || email.contains(keyword)
                    || user.contains(keyword)
                    || roleDisplay.contains(keyword);

            boolean matchFilter = filter.equals("Tất cả")
                    || (filter.equals("Quản lý") && vaiTro.equals("NVQL"))
                    || (filter.equals("Tiếp tân") && vaiTro.equals("NVTT"));

            return matchKeyword && matchFilter;
        }).collect(Collectors.toList());

        for (NhanVien nv : filtered) {
            TaiKhoan tk = nv.getTaiKhoan();
            String vaiTro = tk != null && tk.getVaiTro() != null ? tk.getVaiTro() : "";
            String roleDisplay = vaiTro.equals("NVQL") ? "Quản lý" : (vaiTro.equals("NVTT") ? "Tiếp tân" : "-");
            
            model.addRow(new Object[]{
                    nv.getMaNV(),
                    nv.getTenNhanVien(),
                    nv.isGioiTinh() ? "Nữ" : "Nam",
                    nv.getSdt(),
                    nv.getEmail(),
                    tk != null ? tk.getTenDangNhap() : "Chưa có",
                    roleDisplay
            });
        }
    }

    private String safeLower(String s) {
        return (s == null) ? "" : s.toLowerCase();
    }

    // ===== UI Helpers =====
    private JButton createRoundedButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) g2.setColor(bg.darker());
                else if (getModel().isRollover()) g2.setColor(bg.brighter());
                else g2.setColor(bg);

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
        return btn;
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