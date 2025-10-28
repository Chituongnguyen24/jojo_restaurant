package view.Login;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Login_View extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblShowPassword;

    private static final String LOGO_PATH = "images/logo.png";
    // Icon đăng nhập (ảnh trung tâm) – sẽ được bo tròn
    private static final String LOGIN_ICON_PATH = "images/icon/login.png";

    // Palette
    private static final Color PRIMARY_COLOR = new Color(230, 126, 34);
    private static final Color PRIMARY_HOVER = new Color(211, 110, 24);
    private static final Color PRIMARY_PRESSED = new Color(192, 96, 18);

    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private static final Color BORDER_COLOR = new Color(229, 231, 235);

    // Nền/phông
    private static final Color CANVAS_BG = new Color(248, 250, 252); // #F8FAFC
    private static final Color CARD_BG = CANVAS_BG;

    private static final boolean USE_SOFT_SHADOW = true;
    private static final String USER_PLACEHOLDER = "Nhập tên đăng nhập";

    public Login_View() {
        setTitle("Nhà hàng JOJO - Đăng nhập");
        setSize(500, 700);
        setMinimumSize(new Dimension(480, 640));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Root: căn giữa tuyệt đối
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(CANVAS_BG);
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        // Card trung tâm
        CardPanel card = new CardPanel();
        card.setOpaque(false);
        card.setLayout(new GridBagLayout());
        card.setBorder(new EmptyBorder(32, 36, 32, 36));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.NONE;
        root.add(card, c);

        // Bố cục card
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // 1. Tên nhà hàng
        gbc.insets = new Insets(0, 0, 8, 0);
        JLabel lblTitle = new JLabel("Nhà hàng JOJO", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(TEXT_PRIMARY);
        card.add(lblTitle, gbc);

        // 2. Subtitle
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 28, 0);
        JLabel lblSubtitle = new JLabel("Hệ thống quản lý nội bộ", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(TEXT_SECONDARY);
        card.add(lblSubtitle, gbc);

        // 3. Ảnh trung tâm: icon đăng nhập (bo tròn)
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 28, 0);
        int avatarSize = 120;
        JLabel lblCenterImage = new JLabel();
        lblCenterImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblCenterImage.setPreferredSize(new Dimension(avatarSize, avatarSize));
        ImageIcon rounded = createCircularIcon(LOGIN_ICON_PATH, avatarSize, 2, new Color(0, 0, 0, 30), null);
        if (rounded != null) {
            lblCenterImage.setIcon(rounded);
        } else {
            // Fallback nếu thiếu ảnh đăng nhập
            lblCenterImage.setText("👤");
            lblCenterImage.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
            lblCenterImage.setForeground(PRIMARY_COLOR);
        }
        card.add(lblCenterImage, gbc);

        // 4. Chào mừng
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 6, 0);
        JLabel lblWelcome = new JLabel("Chào mừng trở lại", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblWelcome.setForeground(TEXT_PRIMARY);
        card.add(lblWelcome, gbc);

        // 5. Instruction
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        JLabel lblInstruction = new JLabel("Đăng nhập để tiếp tục", SwingConstants.CENTER);
        lblInstruction.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblInstruction.setForeground(TEXT_SECONDARY);
        card.add(lblInstruction, gbc);

        // 6. Username label
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 8, 0);
        gbc.anchor = GridBagConstraints.WEST;
        JLabel lblUsername = new JLabel("Tên đăng nhập");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsername.setForeground(TEXT_PRIMARY);
        card.add(lblUsername, gbc);

        // 7. Username field
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 16, 0);
        txtUsername = createStyledTextField(USER_PLACEHOLDER);
        card.add(txtUsername, gbc);

        // 8. Password label
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 8, 0);
        JLabel lblPassword = new JLabel("Mật khẩu");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setForeground(TEXT_PRIMARY);
        card.add(lblPassword, gbc);

        // 9. Password field (có icon show/hide)
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 10, 0);
        JPanel passwordPanel = createPasswordPanel();
        card.add(passwordPanel, gbc);

        // 10. Quên mật khẩu
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 22, 0);
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lblForgot = createLinkLabel("Quên mật khẩu?");
        card.add(lblForgot, gbc);

        // 11. Nút Đăng nhập
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        btnLogin = createStyledButton("Đăng nhập");
        card.add(btnLogin, gbc);

        // Default button
        getRootPane().setDefaultButton(btnLogin);

        // Icon cửa sổ = logo.png
        try {
            setIconImage(new ImageIcon(LOGO_PATH).getImage());
        } catch (Exception ignored) {}

        setContentPane(root);

        setupKeyListeners();
        setupWindowListener();
    }

    // Tạo ImageIcon hình tròn từ ảnh nguồn, có viền tùy chọn
    private static ImageIcon createCircularIcon(String path, int size, int borderThickness, Color borderColor, Color fillBehind) {
        try {
            Image src = new ImageIcon(path).getImage();
            // Vẽ ảnh lên buffer vuông
            BufferedImage square = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = square.createGraphics();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (fillBehind != null) {
                g2.setColor(fillBehind);
                g2.fillRect(0, 0, size, size);
            }

            // Scale ảnh để cover hình vuông (center-crop)
            int srcW = src.getWidth(null);
            int srcH = src.getHeight(null);
            if (srcW <= 0 || srcH <= 0) return null;

            double scale = Math.max(size / (double) srcW, size / (double) srcH);
            int drawW = (int) Math.round(srcW * scale);
            int drawH = (int) Math.round(srcH * scale);
            int dx = (size - drawW) / 2;
            int dy = (size - drawH) / 2;

            // Clip hình tròn rồi vẽ ảnh
            Shape circle = new Ellipse2D.Double(0, 0, size, size);
            g2.setClip(circle);
            g2.drawImage(src, dx, dy, drawW, drawH, null);
            g2.setClip(null);

            // Viền
            if (borderThickness > 0 && borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(borderThickness));
                double off = borderThickness / 2.0;
                g2.draw(new Ellipse2D.Double(off, off, size - borderThickness, size - borderThickness));
            }
            g2.dispose();
            return new ImageIcon(square);
        } catch (Exception ex) {
            return null;
        }
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(380, 44));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(TEXT_SECONDARY);
        field.setText(placeholder);
        field.setBorder(createFieldBorder(false));
        field.setBackground(CANVAS_BG);
        field.setCaretColor(TEXT_PRIMARY);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(TEXT_PRIMARY);
                }
                field.setBorder(createFieldBorder(true));
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(TEXT_SECONDARY);
                }
                field.setBorder(createFieldBorder(false));
            }
        });
        return field;
        }

    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(CANVAS_BG);
        panel.setPreferredSize(new Dimension(380, 44));
        panel.setBorder(createFieldBorder(false));

        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(new EmptyBorder(10, 14, 10, 10));
        txtPassword.setOpaque(false);
        txtPassword.setEchoChar('●');
        txtPassword.setCaretColor(TEXT_PRIMARY);

        lblShowPassword = new JLabel("👁");
        lblShowPassword.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lblShowPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblShowPassword.setBorder(new EmptyBorder(0, 10, 0, 14));
        lblShowPassword.setToolTipText("Hiện/ẩn mật khẩu");

        lblShowPassword.addMouseListener(new MouseAdapter() {
            boolean isShowing = false;
            @Override
            public void mouseClicked(MouseEvent e) {
                isShowing = !isShowing;
                txtPassword.setEchoChar(isShowing ? (char) 0 : '●');
                lblShowPassword.setText(isShowing ?"👁" : "🙈" );
            }
        });

        txtPassword.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { panel.setBorder(createFieldBorder(true)); }
            @Override public void focusLost(FocusEvent e) { panel.setBorder(createFieldBorder(false)); }
        });

        panel.add(txtPassword, BorderLayout.CENTER);
        panel.add(lblShowPassword, BorderLayout.EAST);
        return panel;
    }

    private Border createFieldBorder(boolean focused) {
        Color borderColor = focused ? PRIMARY_COLOR : BORDER_COLOR;
        int thickness = focused ? 2 : 1;
        return new CompoundBorder(
                new LineBorder(borderColor, thickness, true),
                new EmptyBorder(0, 0, 0, 0)
        );
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setPreferredSize(new Dimension(380, 46));
        button.setBackground(PRIMARY_COLOR);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(0, 14, 0, 14));

        button.addChangeListener(e -> {
            if (button.getModel().isPressed()) {
                button.setBackground(PRIMARY_PRESSED);
            } else if (button.getModel().isRollover()) {
                button.setBackground(PRIMARY_HOVER);
            } else {
                button.setBackground(PRIMARY_COLOR);
            }
        });
        return button;
    }

    private JLabel createLinkLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(PRIMARY_COLOR);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        label.setToolTipText(text);
        label.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { label.setFont(new Font("Segoe UI", Font.BOLD, 12)); }
            @Override public void mouseExited(MouseEvent e)  { label.setFont(new Font("Segoe UI", Font.PLAIN, 12)); }
        });
        return label;
    }

    private void setupKeyListeners() {
        txtPassword.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) btnLogin.doClick();
            }
        });
        txtUsername.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) txtPassword.requestFocus();
            }
        });
    }

    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override public void windowOpened(WindowEvent e) { txtUsername.requestFocusInWindow(); }
        });
    }

    public String getUsername() {
        String text = txtUsername.getText().trim();
        return text.equals(USER_PLACEHOLDER) ? "" : text;
    }

    public String getPassword() { return String.valueOf(txtPassword.getPassword()); }

    public void addLoginListener(ActionListener listener) { btnLogin.addActionListener(listener); }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public void clearFields() {
        txtUsername.setText(USER_PLACEHOLDER);
        txtUsername.setForeground(TEXT_SECONDARY);
        txtPassword.setText("");
        txtUsername.requestFocusInWindow();
    }

    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception e) { e.printStackTrace(); }

            Login_View view = new Login_View();
            view.setVisible(true);
            view.addLoginListener(e -> {
                String username = view.getUsername();
                String password = view.getPassword();
                if (username.isEmpty() || password.isEmpty()) {
                    view.showError("Vui lòng nhập đầy đủ thông tin!");
                } else {
                    view.showMessage("Đang đăng nhập với:\nTên: " + username);
                    view.clearFields();
                }
            });
        });
    }

    private static class CardPanel extends JPanel {
        private static final int ARC = 16;
        private static final int SHADOW_OFFSET = 8;
        private static final int SHADOW_ALPHA = 20;

        @Override
        protected void paintComponent(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (USE_SOFT_SHADOW) {
                g2.setColor(new Color(0, 0, 0, SHADOW_ALPHA));
                g2.fillRoundRect(SHADOW_OFFSET, SHADOW_OFFSET,
                        w - SHADOW_OFFSET * 2, h - SHADOW_OFFSET * 2, ARC, ARC);
            }

            g2.setColor(CARD_BG);
            g2.fillRoundRect(0, 0, w - SHADOW_OFFSET, h - SHADOW_OFFSET, ARC, ARC);

            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        public Insets getInsets() {
            return USE_SOFT_SHADOW ? new Insets(0, 0, SHADOW_OFFSET, SHADOW_OFFSET) : new Insets(0, 0, 0, 0);
        }
    }
}