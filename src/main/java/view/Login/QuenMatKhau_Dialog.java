package view.Login;

import dao.NhanVien_DAO;
import dao.TaiKhoan_DAO;
import entity.NhanVien;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.awt.geom.RoundRectangle2D; 

public class QuenMatKhau_Dialog extends JDialog {
    private JTextField txtTenDangNhap, txtMaNV, txtHoTen, txtSdt, txtEmail, txtNgaySinh;
    private JButton btnGetPassword, btnCancel;
    private JLabel lblStatus;
    private JProgressBar progressBar;

    private static final Color PRIMARY_COLOR = new Color(52, 152, 219); 
    private static final Color PRIMARY_HOVER = new Color(41, 128, 185); 
    private static final Color HEADER_BG_COLOR = new Color(44, 62, 80); 
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    private static final Color ERROR_COLOR = new Color(231, 76, 60);
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(220, 220, 220);
    private static final Color BG_LIGHT = new Color(248, 250, 252);
    private static final Color INFO_BANNER_BG = new Color(235, 247, 255); 
    private static final Color INFO_BANNER_BORDER = new Color(173, 216, 230); 
    private static final Color INFO_BANNER_TEXT = new Color(29, 66, 138); 
    private static final int CORNER_RADIUS = 12; 

    private TaiKhoan_DAO taiKhoan_DAO;
    private NhanVien_DAO nhanVien_DAO;

    public QuenMatKhau_Dialog(Frame owner) {
        super(owner, "Khôi phục mật khẩu", true);
        
        taiKhoan_DAO = new TaiKhoan_DAO(); 
        nhanVien_DAO = new NhanVien_DAO();
        
        initComponents();
        setResizable(false);
        setUndecorated(true);
        pack();
        setLocationRelativeTo(owner);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS));
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        mainPanel.add(createHeader(), BorderLayout.NORTH);
        
        mainPanel.add(createContent(), BorderLayout.CENTER);
        
        mainPanel.add(createFooter(), BorderLayout.SOUTH);

        setContentPane(mainPanel);
        getRootPane().setDefaultButton(btnGetPassword);
        
        mainPanel.setBorder(new RoundedBorder(new Color(0,0,0,50), 1, CORNER_RADIUS, 0));
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG_COLOR); 
        header.setBorder(new EmptyBorder(15, 20, 15, 20)); 

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        leftPanel.setOpaque(false);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Khôi phục mật khẩu");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Xác minh danh tính để lấy lại mật khẩu");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(255, 255, 255, 200));

        titlePanel.add(lblTitle);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(lblSubtitle);

        leftPanel.add(titlePanel);

        JButton btnClose = createCloseButton();

        header.add(leftPanel, BorderLayout.WEST);
        header.add(btnClose, BorderLayout.EAST);

        return header;
    }

    private JButton createCloseButton() {
        JButton btn = new JButton("X");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btn.setForeground(Color.WHITE);
        btn.setBackground(HEADER_BG_COLOR); 
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(35, 35));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(new Color(200, 220, 255)); 
            }
            public void mouseExited(MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
        });
        
        btn.addActionListener(e -> dispose());
        return btn;
    }

    private JPanel createContent() {
        JPanel content = new JPanel(new BorderLayout(0, 15));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(20, 25, 20, 25)); 

        JPanel infoBanner = createInfoBanner();
        
        JPanel formPanel = createFormPanel();
        
        JPanel statusPanel = createStatusPanel();

        JPanel formWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        formWrapper.setBackground(Color.WHITE);
        formWrapper.add(formPanel);

        content.add(infoBanner, BorderLayout.NORTH);
        content.add(formWrapper, BorderLayout.CENTER);
        content.add(statusPanel, BorderLayout.SOUTH);

        return content;
    }

    private JPanel createInfoBanner() {
        JPanel banner = new JPanel(new BorderLayout(10, 0));
        banner.setBackground(INFO_BANNER_BG); 
        banner.setBorder(new RoundedBorder(INFO_BANNER_BORDER, 1, CORNER_RADIUS, 5)); 
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.setBorder(new EmptyBorder(12, 15, 12, 15));
        
        JLabel text = new JLabel("<html>Vui lòng nhập <b>chính xác TẤT CẢ</b> thông tin bên dưới.<br/>Hệ thống sẽ xác minh và trả về mật khẩu nếu thông tin khớp.</html>");
        text.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        text.setForeground(INFO_BANNER_TEXT); 
        textPanel.add(text, BorderLayout.CENTER);
        
        banner.add(textPanel, BorderLayout.CENTER);

        return banner;
    }

    // ===== THAY ĐỔI 1: XÓA VIỀN VÀ GỘP 6 TRƯỜNG VÀO 1 GRID =====
    private JPanel createFormPanel() {
        // Dùng 1 panel GridBagLayout duy nhất
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);

        txtTenDangNhap = createStyledTextField("username123");
        txtMaNV = createStyledTextField("NV001");
        txtHoTen = createStyledTextField("Nguyễn Văn A");
        txtSdt = createStyledTextField("0912345678");
        txtEmail = createStyledTextField("example@email.com");
        txtNgaySinh = createStyledTextField("30/01/1990");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 10, 10); // top, left, bottom, right
        
        // Hàng 1
        gbc.gridx = 0;
        gbc.gridy = 0;
        form.add(createFieldGroup("Tên đăng nhập", txtTenDangNhap, "Tài khoản dùng để đăng nhập"), gbc);
        
        gbc.gridx = 1;
        form.add(createFieldGroup("Mã nhân viên", txtMaNV, "Mã số nhân viên của bạn"), gbc);

        // Hàng 2
        gbc.gridx = 0;
        gbc.gridy = 1;
        form.add(createFieldGroup("Họ và tên đầy đủ", txtHoTen, "Nhập chính xác như trong hệ thống"), gbc);

        gbc.gridx = 1;
        form.add(createFieldGroup("Số điện thoại", txtSdt, "10 chữ số, bắt đầu bằng 0"), gbc);
        
        // Hàng 3
        gbc.gridx = 0;
        gbc.gridy = 2;
        form.add(createFieldGroup("Email", txtEmail, "Địa chỉ email đã đăng ký"), gbc);

        gbc.gridx = 1;
        form.add(createFieldGroup("Ngày sinh", txtNgaySinh, "Định dạng: DD/MM/YYYY"), gbc);
        
        // Gắn listener
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onGetPassword();
                }
            }
        };
        txtTenDangNhap.addKeyListener(enterListener);
        txtMaNV.addKeyListener(enterListener);
        txtHoTen.addKeyListener(enterListener);
        txtSdt.addKeyListener(enterListener);
        txtEmail.addKeyListener(enterListener);
        txtNgaySinh.addKeyListener(enterListener);

        return form;
    }

    private JPanel createFieldGroup(String label, JTextField field, String hint) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setBackground(Color.WHITE);
        group.setOpaque(false); 

        JLabel lblField = new JLabel(label);
        lblField.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblField.setForeground(TEXT_PRIMARY);
        lblField.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblHint = new JLabel(hint);
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHint.setForeground(TEXT_SECONDARY);
        lblHint.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(lblField);
        group.add(Box.createVerticalStrut(4));
        group.add(field);
        group.add(Box.createVerticalStrut(2));
        group.add(lblHint);

        return group;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(20); 
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new RoundedBorder(BORDER_COLOR, 1, CORNER_RADIUS / 2, 15));
        
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
                field.setBorder(new RoundedBorder(PRIMARY_COLOR, 2, CORNER_RADIUS / 2, 14)); 
            }
            
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
                field.setBorder(new RoundedBorder(BORDER_COLOR, 1, CORNER_RADIUS / 2, 15)); 
            }
        });

        return field;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        lblStatus = new JLabel(" ");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(TEXT_SECONDARY);
        lblStatus.setHorizontalAlignment(SwingConstants.CENTER);

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setVisible(false);
        progressBar.setPreferredSize(new Dimension(0, 3));
        progressBar.setForeground(PRIMARY_COLOR); 

        panel.add(lblStatus, BorderLayout.NORTH);
        panel.add(progressBar, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        footer.setBackground(BG_LIGHT);
        footer.setBorder(new EmptyBorder(15, 20, 15, 20));

        btnCancel = createStyledButton("Hủy bỏ", new Color(220, 220, 220), new Color(200, 200, 200), TEXT_PRIMARY);
        btnCancel.addActionListener(e -> dispose());

        btnGetPassword = createStyledButton("Lấy lại mật khẩu", PRIMARY_COLOR, PRIMARY_HOVER, Color.WHITE); 
        btnGetPassword.addActionListener(e -> onGetPassword());

        footer.add(btnCancel);
        footer.add(btnGetPassword);

        return footer;
    }

    // ===== THAY ĐỔI 2: SỬA HÀM NÚT BẤM ĐỂ TỰ VẼ NỀN =====
    private JButton createStyledButton(String text, Color bg, Color hoverBg, Color fg) {
        // Dùng mảng 1 phần tử để có thể thay đổi trong anonymous class
        final Color[] currentBg = {bg};

        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vẽ nền bo góc
                g2.setColor(currentBg[0]); 
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), CORNER_RADIUS / 2, CORNER_RADIUS / 2));
                
                g2.dispose();

                // Vẽ chữ (label)
                super.paintComponent(g);
            }
        };
        
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        
        // Dùng RoundedBorder chỉ để lấy padding, viền sẽ trong suốt
        btn.setBorder(new RoundedBorder(new Color(0,0,0,0), 0, CORNER_RADIUS / 2, 20));
        
        btn.setContentAreaFilled(false); // Rất quan trọng: tắt vẽ nền mặc định
        btn.setOpaque(false); // Rất quan trọng: cho phép nền trong suốt
        
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 40));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) {
                    currentBg[0] = hoverBg; // Đổi màu nền
                    btn.repaint(); // Vẽ lại nút
                }
            }
            public void mouseExited(MouseEvent e) {
                currentBg[0] = bg; // Trả lại màu nền
                btn.repaint(); // Vẽ lại nút
            }
        });

        return btn;
    }

    private String getFieldValue(JTextField field, String placeholder) {
        String text = field.getText().trim();
        return (text.isEmpty() || text.equals(placeholder)) ? "" : text;
    }

    private void onGetPassword() {
        String tenDangNhap = getFieldValue(txtTenDangNhap, "username123");
        String maNV = getFieldValue(txtMaNV, "NV001");
        String hoTen = getFieldValue(txtHoTen, "Nguyễn Văn A");
        String sdt = getFieldValue(txtSdt, "0912345678");
        String email = getFieldValue(txtEmail, "example@email.com");
        String ngaySinhStr = getFieldValue(txtNgaySinh, "30/01/1990");

        if (tenDangNhap.isEmpty() || maNV.isEmpty() || hoTen.isEmpty() || 
            sdt.isEmpty() || email.isEmpty() || ngaySinhStr.isEmpty()) {
            showStatus("Vui lòng nhập đầy đủ TẤT CẢ thông tin", ERROR_COLOR);
            return;
        }
        
        if (!ngaySinhStr.matches("^\\d{2}/\\d{2}/\\d{4}$")) {
            showStatus("Định dạng ngày sinh không đúng (phải là DD/MM/YYYY)", ERROR_COLOR);
            return;
        }

        if (!sdt.matches("^0\\d{9}$")) {
            showStatus("Số điện thoại phải có 10 chữ số và bắt đầu bằng 0", ERROR_COLOR);
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showStatus("Định dạng email không hợp lệ", ERROR_COLOR);
            return;
        }

        setControlsEnabled(false);
        progressBar.setVisible(true);
        showStatus("Đang xác minh thông tin...", TEXT_SECONDARY);

        new SwingWorker<Object[], Void>() {
            @Override
            protected Object[] doInBackground() throws Exception {
                try {
                    Thread.sleep(800);

                    TaiKhoan tk = taiKhoan_DAO.findByUsername(tenDangNhap);
                    // Dùng NhanVien_DAO để lấy đầy đủ thông tin xác minh
                    NhanVien nv = nhanVien_DAO.getNhanVienById(maNV);
                    
                    if (tk == null || nv == null) {
                        return new Object[]{false, "Tên đăng nhập hoặc Mã nhân viên không tồn tại"};
                    }
                    
                    // Lấy mã NV từ TaiKhoan để so sánh với mã NV người dùng nhập
                    if (!tk.getNhanVien().getMaNhanVien().equals(maNV)) {
                         return new Object[]{false, "Tên đăng nhập và Mã nhân viên không khớp"};
                    }
                    
                    if (!nv.getHoTen().equalsIgnoreCase(hoTen)) {
                        return new Object[]{false, "Họ tên không chính xác"};
                    }
                    
                    if (!nv.getSoDienThoai().equals(sdt)) {
                        return new Object[]{false, "Số điện thoại không chính xác"};
                    }

                    if (!nv.getEmail().equalsIgnoreCase(email)) {
                        return new Object[]{false, "Email không chính xác"};
                    }

                    LocalDate ngaySinhDB_Obj = nv.getNgaySinh(); 
                    DateTimeFormatter dmyFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String ngaySinhDB_Formatted = ngaySinhDB_Obj.format(dmyFormatter);
                    
                    if (!ngaySinhDB_Formatted.equals(ngaySinhStr)) {
                        return new Object[]{false, "Ngày sinh không chính xác"};
                    }

                    return new Object[]{true, tk.getMatKhau()};

                } catch (Exception e) {
                    e.printStackTrace();
                    return new Object[]{false, "Lỗi hệ thống: " + e.getMessage()};
                }
            }

            @Override
            protected void done() {
                progressBar.setVisible(false);
                
                try {
                    Object[] result = get();
                    boolean success = (Boolean) result[0];
                    String message = (String) result[1];
                    
                    if (success) {
                        showSuccessDialog(message);
                    } else {
                        showStatus(message, ERROR_COLOR);
                        setControlsEnabled(true);
                    }
                } catch (Exception ex) {
                    showStatus("Lỗi không xác định: " + ex.getMessage(), ERROR_COLOR);
                    setControlsEnabled(true);
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private void showSuccessDialog(String password) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel successLabel = new JLabel("Xác minh thành công!");
        successLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        successLabel.setForeground(SUCCESS_COLOR);
        successLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel infoLabel = new JLabel("Mật khẩu của bạn là:");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField passwordField = new JTextField(password);
        passwordField.setFont(new Font("Consolas", Font.BOLD, 18));
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        passwordField.setEditable(false);
        passwordField.setBackground(new Color(240, 248, 255));
        passwordField.setBorder(new RoundedBorder(PRIMARY_COLOR, 2, CORNER_RADIUS / 2, 10));
        passwordField.setMaximumSize(new Dimension(300, 45));

        panel.add(Box.createVerticalStrut(10));
        panel.add(successLabel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(infoLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(passwordField);

        JOptionPane.showMessageDialog(this, panel, "Khôi phục thành công", JOptionPane.PLAIN_MESSAGE);
        dispose();
    }

    private void setControlsEnabled(boolean enabled) {
        txtTenDangNhap.setEnabled(enabled);
        txtMaNV.setEnabled(enabled);
        txtHoTen.setEnabled(enabled);
        txtSdt.setEnabled(enabled);
        txtEmail.setEnabled(enabled);
        txtNgaySinh.setEnabled(enabled);
        btnGetPassword.setEnabled(enabled);
        btnCancel.setEnabled(enabled);
    }

    private void showStatus(String message, Color color) {
        lblStatus.setText(message);
        lblStatus.setForeground(color);
    }
    
    // Class RoundedBorder (để bo góc cho JTextField, Banner, và Dialog chính)
    private static class RoundedBorder implements Border {
        private Color color;
        private int thickness;
        private int radius;
        private int padding; 

        RoundedBorder(Color color, int thickness, int radius, int padding) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
            this.padding = padding;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Double(x + thickness / 2.0, y + thickness / 2.0, width - thickness, height - thickness, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            int p = padding + thickness;
            return new Insets(p, p, p, p);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}