package view.Login;

import dao.TaiKhoan_DAO;
import entity.TaiKhoan;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;

/**
 * Modern Change Password Dialog with fixed input field sizing.
 * 
 * Features:
 * - Clean, modern UI with proper color scheme
 * - Password strength validation
 * - Real-time password matching feedback
 * - Toggle password visibility
 * - Proper input field sizing (no squished fields)
 * - Keyboard shortcuts (Enter to save, Esc to cancel)
 * 
 * Usage: DoiMatKhau_View.showDialog(parentFrame, "NV00001");
 */
public class DoiMatKhau_View extends JDialog {

    private JTextField txtMaNV;
    private JPasswordField txtMatKhauCu;
    private JPasswordField txtMatKhauMoi;
    private JPasswordField txtXacNhan;
    private JButton btnToggleOld;
    private JButton btnToggleNew;
    private JButton btnToggleConfirm;
    private JButton btnSave;
    private JButton btnCancel;
    private JLabel lblRequirements;
    private JLabel lblMatchStatus;

    private final TaiKhoan_DAO taiKhoanDAO = new TaiKhoan_DAO();
    private static final int MIN_PASSWORD_LENGTH = 6;

    // Modern colors
    private static final Color PRIMARY_COLOR = new Color(59, 130, 246);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color DANGER_COLOR = new Color(239, 68, 68);
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color INPUT_BACKGROUND = new Color(249, 250, 251);

    // Borders for focus effects
    private static final Border DEFAULT_TEXT_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 14, 10, 14)
    );
    private static final Border FOCUSED_TEXT_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            new EmptyBorder(9, 13, 9, 13)
    );
    private static final Border DEFAULT_PASS_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(10, 14, 10, 65)
    );
    private static final Border FOCUSED_PASS_BORDER = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            new EmptyBorder(9, 13, 9, 64)
    );

    private boolean showOldPassword = false;
    private boolean showNewPassword = false;
    private boolean showConfirmPassword = false;

    public DoiMatKhau_View(Frame owner, String maNV) {
        super(owner, "Đổi mật khẩu", true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(BACKGROUND_COLOR);

        mainContainer.add(createHeader(), BorderLayout.NORTH);

        JPanel contentWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contentWrapper.setBackground(BACKGROUND_COLOR);
        contentWrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel contentPanel = createContentPanel(maNV);
        contentPanel.setBackground(CARD_COLOR);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 235, 235)),
                new EmptyBorder(24, 24, 24, 24)
        ));

        contentWrapper.add(contentPanel);
        mainContainer.add(contentWrapper, BorderLayout.CENTER);

        mainContainer.add(createFooter(), BorderLayout.SOUTH);

        getContentPane().add(mainContainer);
        
        pack();
        setSize(Math.max(getWidth(), 560), Math.max(getHeight(), 580));
        setResizable(false);
        setLocationRelativeTo(owner);

        setupEventHandlers();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, darkerColor(PRIMARY_COLOR)),
                new EmptyBorder(24, 24, 20, 24)
        ));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel titleLabel = new JLabel("Đổi mật khẩu");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Vui lòng nhập mật khẩu cũ và mật khẩu mới");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(209, 213, 219));
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(6));
        content.add(subtitleLabel);

        header.add(content, BorderLayout.WEST);
        return header;
    }

    private JPanel createContentPanel(String maNV) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_COLOR);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setPreferredSize(new Dimension(480, 380));
        panel.setMinimumSize(new Dimension(400, 300));

        // Mã nhân viên
        panel.add(createFieldLabel("Mã nhân viên", false));
        panel.add(Box.createVerticalStrut(8));
        txtMaNV = createStyledTextField("VD: NV00001");
        txtMaNV.setText(maNV == null ? "" : maNV);
        txtMaNV.setEditable(false);
        txtMaNV.setBackground(new Color(243, 244, 246));
        txtMaNV.setForeground(TEXT_SECONDARY);
        panel.add(txtMaNV);
        panel.add(Box.createVerticalStrut(18));

        // Mật khẩu cũ
        panel.add(createFieldLabel("Mật khẩu cũ", true));
        panel.add(Box.createVerticalStrut(8));
        txtMatKhauCu = createStyledPasswordField("Nhập mật khẩu hiện tại");
        btnToggleOld = createToggleButton();
        panel.add(createPasswordEntry(txtMatKhauCu, btnToggleOld));
        panel.add(Box.createVerticalStrut(18));

        // Mật khẩu mới
        panel.add(createFieldLabel("Mật khẩu mới", true));
        panel.add(Box.createVerticalStrut(8));
        txtMatKhauMoi = createStyledPasswordField("Nhập mật khẩu mới");
        btnToggleNew = createToggleButton();
        panel.add(createPasswordEntry(txtMatKhauMoi, btnToggleNew));
        panel.add(Box.createVerticalStrut(8));

        // Requirements
        lblRequirements = new JLabel("Tối thiểu " + MIN_PASSWORD_LENGTH + " ký tự.");
        lblRequirements.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRequirements.setForeground(TEXT_SECONDARY);
        lblRequirements.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblRequirements);
        panel.add(Box.createVerticalStrut(18));

        // Xác nhận mật khẩu
        panel.add(createFieldLabel("Xác nhận mật khẩu mới", true));
        panel.add(Box.createVerticalStrut(8));
        txtXacNhan = createStyledPasswordField("Nhập lại mật khẩu mới");
        btnToggleConfirm = createToggleButton();
        panel.add(createPasswordEntry(txtXacNhan, btnToggleConfirm));
        panel.add(Box.createVerticalStrut(8));

        // Match status
        lblMatchStatus = new JLabel(" ");
        lblMatchStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblMatchStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblMatchStatus);
        panel.add(Box.createVerticalStrut(12));

        return panel;
    }

    /**
     * Fixed createPasswordEntry using JPanel with null layout for precise control.
     * This ensures fields don't get squished.
     */
    private JPanel createPasswordEntry(JPasswordField field, JButton button) {
        JPanel entryPanel = new JPanel(null); // null layout for manual positioning
        entryPanel.setOpaque(false);
        entryPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        int fieldHeight = 44;
        entryPanel.setPreferredSize(new Dimension(480, fieldHeight));
        entryPanel.setMinimumSize(new Dimension(300, fieldHeight));
        entryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, fieldHeight));

        entryPanel.add(field);
        entryPanel.add(button);

        // Layout components on resize
        entryPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = entryPanel.getWidth();
                int h = entryPanel.getHeight();
                
                Dimension btnSize = button.getPreferredSize();
                int btnW = btnSize.width;
                
                // Password field takes full width, button overlays on right
                field.setBounds(0, 0, w, h);
                button.setBounds(w - btnW - 4, (h - btnSize.height) / 2, btnW, btnSize.height);
            }
        });

        return entryPanel;
    }

    private JLabel createFieldLabel(String text, boolean required) {
        JLabel label = new JLabel(text + (required ? " *" : ""));
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(TEXT_PRIMARY);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(156, 163, 175));
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = getInsets().left;
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(placeholder, x, y);
                    g2.dispose();
                }
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(480, 44));
        field.setMinimumSize(new Dimension(300, 44));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setBorder(DEFAULT_TEXT_BORDER);
        field.setBackground(INPUT_BACKGROUND);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(FOCUSED_TEXT_BORDER);
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(DEFAULT_TEXT_BORDER);
            }
        });

        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(156, 163, 175));
                    g2.setFont(getFont());
                    FontMetrics fm = g2.getFontMetrics();
                    int x = getInsets().left;
                    int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(placeholder, x, y);
                    g2.dispose();
                }
            }
        };
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(DEFAULT_PASS_BORDER);
        field.setBackground(INPUT_BACKGROUND);
        field.setEchoChar('•');
        field.setPreferredSize(new Dimension(480, 44));
        field.setMinimumSize(new Dimension(300, 44));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(FOCUSED_PASS_BORDER);
            }
            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(DEFAULT_PASS_BORDER);
            }
        });
        return field;
    }

    private JButton createToggleButton() {
        JButton btn = new JButton("Hiện");
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setPreferredSize(new Dimension(58, 32));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText("Hiện/ẩn mật khẩu");
        btn.setForeground(TEXT_SECONDARY);
        btn.setOpaque(false);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(PRIMARY_COLOR);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(TEXT_SECONDARY);
            }
        });
        return btn;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        footer.setBackground(BACKGROUND_COLOR);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_COLOR),
                new EmptyBorder(14, 14, 14, 14)
        ));

        btnCancel = createStyledButton("Hủy", null, TEXT_PRIMARY);
        btnCancel.setPreferredSize(new Dimension(110, 42));

        btnSave = createStyledButton("Lưu thay đổi", PRIMARY_COLOR, Color.WHITE);
        btnSave.setPreferredSize(new Dimension(140, 42));
        btnSave.setEnabled(false);

        footer.add(btnCancel);
        footer.add(btnSave);

        return footer;
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (bgColor != null) {
                    if (!isEnabled()) {
                        g2.setColor(new Color(203, 213, 225));
                    } else if (getModel().isPressed()) {
                        g2.setColor(darkerColor(bgColor));
                    } else if (getModel().isRollover()) {
                        g2.setColor(brighterColor(bgColor));
                    } else {
                        g2.setColor(bgColor);
                    }
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else {
                    if (getModel().isPressed()) {
                        g2.setColor(new Color(229, 231, 235));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(243, 244, 246));
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    }
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(fgColor);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setOpaque(false);

        return btn;
    }

    private void setupEventHandlers() {
        try {
            if (btnToggleOld != null && txtMatKhauCu != null) {
                btnToggleOld.addActionListener(e -> {
                    showOldPassword = !showOldPassword;
                    txtMatKhauCu.setEchoChar(showOldPassword ? (char) 0 : '•');
                    btnToggleOld.setText(showOldPassword ? "Ẩn" : "Hiện");
                });
            }
            if (btnToggleNew != null && txtMatKhauMoi != null) {
                btnToggleNew.addActionListener(e -> {
                    showNewPassword = !showNewPassword;
                    txtMatKhauMoi.setEchoChar(showNewPassword ? (char) 0 : '•');
                    btnToggleNew.setText(showNewPassword ? "Ẩn" : "Hiện");
                });
            }
            if (btnToggleConfirm != null && txtXacNhan != null) {
                btnToggleConfirm.addActionListener(e -> {
                    showConfirmPassword = !showConfirmPassword;
                    txtXacNhan.setEchoChar(showConfirmPassword ? (char) 0 : '•');
                    btnToggleConfirm.setText(showConfirmPassword ? "Ẩn" : "Hiện");
                });
            }

            DocumentListener docListener = new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) { onInputChanged(); }
                @Override
                public void removeUpdate(DocumentEvent e) { onInputChanged(); }
                @Override
                public void changedUpdate(DocumentEvent e) { onInputChanged(); }
            };
            if (txtMatKhauMoi != null) txtMatKhauMoi.getDocument().addDocumentListener(docListener);
            if (txtXacNhan != null) txtXacNhan.getDocument().addDocumentListener(docListener);
            if (txtMatKhauCu != null) txtMatKhauCu.getDocument().addDocumentListener(docListener);

            if (btnSave != null) btnSave.addActionListener(e -> onSave());
            if (btnCancel != null) btnCancel.addActionListener(e -> dispose());

            KeyAdapter keyAdapter = new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (btnSave != null && btnSave.isEnabled()) onSave();
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        dispose();
                    }
                }
            };
            if (txtMatKhauCu != null) txtMatKhauCu.addKeyListener(keyAdapter);
            if (txtMatKhauMoi != null) txtMatKhauMoi.addKeyListener(keyAdapter);
            if (txtXacNhan != null) txtXacNhan.addKeyListener(keyAdapter);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onInputChanged() {
        if (txtMatKhauMoi == null || txtXacNhan == null || txtMatKhauCu == null) return;

        String newPass = new String(txtMatKhauMoi.getPassword());
        String confirm = new String(txtXacNhan.getPassword());
        String oldPass = new String(txtMatKhauCu.getPassword());

        if (confirm.isEmpty()) {
            lblMatchStatus.setText(" ");
        } else if (newPass.equals(confirm)) {
            lblMatchStatus.setText("✓ Mật khẩu khớp");
            lblMatchStatus.setForeground(SUCCESS_COLOR);
        } else {
            lblMatchStatus.setText("✗ Mật khẩu không khớp");
            lblMatchStatus.setForeground(DANGER_COLOR);
        }

        boolean valid = !oldPass.isEmpty()
                && newPass.length() >= MIN_PASSWORD_LENGTH
                && newPass.equals(confirm);
        if (btnSave != null) btnSave.setEnabled(valid);
    }

    private void onSave() {
        String maNV = txtMaNV.getText().trim();
        char[] oldPass = txtMatKhauCu.getPassword();
        char[] newPass = txtMatKhauMoi.getPassword();
        char[] confirm = txtXacNhan.getPassword();

        if (maNV.isEmpty()) {
            showError("Mã nhân viên không hợp lệ.");
            return;
        }
        if (oldPass.length == 0) {
            showError("Vui lòng nhập mật khẩu cũ.");
            return;
        }
        if (newPass.length < MIN_PASSWORD_LENGTH) {
            showError("Mật khẩu mới phải có ít nhất " + MIN_PASSWORD_LENGTH + " ký tự.");
            return;
        }
        if (!String.valueOf(newPass).equals(String.valueOf(confirm))) {
            showError("Mật khẩu mới và xác nhận không khớp.");
            return;
        }

        TaiKhoan tk = taiKhoanDAO.findByMaNV(maNV);
        if (tk == null) {
            showError("Không tìm thấy tài khoản.");
            return;
        }

        String stored = tk.getMatKhau();
        boolean okVerify = stored != null && stored.equals(String.valueOf(oldPass));

        if (!okVerify) {
            showError("Mật khẩu cũ không đúng.");
            Arrays.fill(oldPass, '\0');
            return;
        }

        String newPassStr = String.valueOf(newPass);
        boolean updated = taiKhoanDAO.updateMatKhau(maNV, newPassStr);

        Arrays.fill(oldPass, '\0');
        Arrays.fill(newPass, '\0');
        Arrays.fill(confirm, '\0');

        if (updated) {
            showSuccess("Đổi mật khẩu thành công!");
            clearPasswords();
            dispose();
        } else {
            showError("Đổi mật khẩu thất bại. Vui lòng thử lại.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearPasswords() {
        if (txtMatKhauCu != null) txtMatKhauCu.setText("");
        if (txtMatKhauMoi != null) txtMatKhauMoi.setText("");
        if (txtXacNhan != null) txtXacNhan.setText("");
    }

    private Color darkerColor(Color c) {
        return new Color(
                Math.max((int) (c.getRed() * 0.85), 0),
                Math.max((int) (c.getGreen() * 0.85), 0),
                Math.max((int) (c.getBlue() * 0.85), 0)
        );
    }

    private Color brighterColor(Color c) {
        return new Color(
                Math.min((int) (c.getRed() * 1.08), 255),
                Math.min((int) (c.getGreen() * 1.08), 255),
                Math.min((int) (c.getBlue() * 1.08), 255)
        );
    }

    public static void showDialog(Frame owner, String maNV) {
        SwingUtilities.invokeLater(() -> {
            DoiMatKhau_View dlg = new DoiMatKhau_View(owner, maNV);
            dlg.setVisible(true);
        });
    }
}