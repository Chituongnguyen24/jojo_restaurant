package view.NhanVien;

import dao.NhanVien_DAO;
import entity.NhanVien;
import entity.TaiKhoan;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;

public class NhanVien_AddDialog extends JDialog {
    // THÊM CÁC TRƯỜNG MỚI
    private JTextField txtTenNV, txtSDT, txtEmail, txtUser, txtCCCD; 
    private JPasswordField txtPass;
    private JLabel lblPasswordStrength;
    private JDatePickerImpl datePickerNS, datePickerNVL; // Ngày sinh và Ngày vào làm
    private JComboBox<String> cbChucVu, cbTrangThai; // Chức vụ và Trạng thái
    private JRadioButton rdNam, rdNu;
    private JButton btnSave, btnCancel;
    private NhanVien_DAO nhanVienDAO;

    private final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private final Color PRIMARY_DARK = new Color(67, 56, 202);
    private final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private final Color WARNING_COLOR = new Color(245, 158, 11);
    private final Color DANGER_COLOR = new Color(239, 68, 68);
    private final Color BACKGROUND = new Color(249, 250, 251);
    private final Color CARD_BG = Color.WHITE;
    private final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private final Color TEXT_SECONDARY = new Color(107, 114, 128);
    private final Color BORDER_COLOR = new Color(229, 231, 235);
    private final Color INPUT_BG = new Color(249, 250, 251);

    public NhanVien_AddDialog(Frame owner, NhanVien_DAO nhanVienDAO) {
        super(owner, "Thêm nhân viên mới", true);
        this.nhanVienDAO = nhanVienDAO;

        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);
        setSize(700, 720);
        add(createMainPanel(), BorderLayout.CENTER);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private JPanel createMainPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BACKGROUND);
        wrapper.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new ShadowBorder(),
                new EmptyBorder(30, 35, 30, 35)
        ));

        cardPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(createFormPanel());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(CARD_BG);
        scrollPane.getViewport().setBackground(CARD_BG);
        cardPanel.add(scrollPane, BorderLayout.CENTER);
        cardPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        wrapper.add(cardPanel);
        return wrapper;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Thêm Nhân Viên Mới");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Điền thông tin chi tiết của nhân viên");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitleLabel);

        panel.add(textPanel, BorderLayout.CENTER);
        return panel;
    }

     private JPanel createFormPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(CARD_BG);
        mainPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 18, 0);
        gbc.weightx = 1.0;

        // Hàng 1: Tên nhân viên
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        txtTenNV = new JTextField();
        mainPanel.add(createFormField("Tên nhân viên", txtTenNV), gbc);

        // Hàng 2: Giới tính & Ngày sinh
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 18, 10);
        JPanel genderPanel = createGenderPanel();
        mainPanel.add(createFormField("Giới tính", genderPanel), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 18, 0);
        JPanel datePanelNS = createDatePanel(false); // Ngày sinh
        mainPanel.add(createFormField("Ngày sinh", datePanelNS), gbc);
        
        // Hàng 3: SĐT & Email
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 18, 10);
        txtSDT = new JTextField();
        mainPanel.add(createFormField("Số điện thoại", txtSDT), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 18, 0);
        txtEmail = new JTextField();
        mainPanel.add(createFormField("Email", txtEmail), gbc);
        
        // Hàng 4: Số CCCD & Ngày vào làm (THÊM CÁC TRƯỜNG NÀY)
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 18, 10);
        txtCCCD = new JTextField(); 
        mainPanel.add(createFormField("Số CCCD", txtCCCD), gbc); 

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 18, 0);
        JPanel datePanelNVL = createDatePanel(true); // Ngày vào làm
        mainPanel.add(createFormField("Ngày vào làm", datePanelNVL), gbc); 
        
        // Hàng 5: Chức vụ & Trạng thái (THÊM CÁC TRƯỜNG NÀY)
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 18, 10);
        cbChucVu = new JComboBox<>(new String[]{"Nhân viên quản lý", "Nhân viên tiếp tân"});
        styleComboBox(cbChucVu);
        mainPanel.add(createFormField("Chức vụ", cbChucVu), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 10, 18, 0);
        cbTrangThai = new JComboBox<>(new String[]{"Đang làm", "Đã nghỉ"});
        styleComboBox(cbTrangThai);
        mainPanel.add(createFormField("Trạng thái", cbTrangThai), gbc);
        
        // Hàng 6: Tên đăng nhập
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 18, 10);
        txtUser = new JTextField();
        mainPanel.add(createFormField("Tên đăng nhập", txtUser), gbc);

        // Hàng 7: Mật khẩu (CỘT RỘNG)
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        JPanel passwordPanel = createPasswordPanel();
        mainPanel.add(createFormField("Mật khẩu", passwordPanel), gbc);

        return mainPanel;
    }


    private JPanel createFormField(String label, Component field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        textLabel.setForeground(TEXT_PRIMARY);
        textLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(textLabel);
        panel.add(Box.createVerticalStrut(6));

        if (field instanceof JTextField) {
            styleTextField((JTextField) field);
        } else if (field instanceof JComboBox) {
        } else if (field instanceof JPanel) {
             field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
             ((JComponent) field).setAlignmentX(Component.LEFT_ALIGNMENT);
        }


        if (!(field instanceof JPanel)) {
             field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
             if (field instanceof JComponent) {
                 ((JComponent) field).setAlignmentX(Component.LEFT_ALIGNMENT);
             }
        }

        panel.add(field);
        return panel;
    }


    private JPanel createGenderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        rdNam = createModernRadioButton("Nam", true);
        rdNu = createModernRadioButton("Nữ", false);

        ButtonGroup bg = new ButtonGroup();
        bg.add(rdNam);
        bg.add(rdNu);

        panel.add(rdNam);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(rdNu);

        return panel;
    }

    private JRadioButton createModernRadioButton(String text, boolean selected) {
        JRadioButton rb = new JRadioButton(text);
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rb.setOpaque(false);
        rb.setFocusPainted(false);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rb.setSelected(selected);
        rb.setForeground(TEXT_PRIMARY);
        return rb;
    }
    
    // SỬA: Hàm tạo DatePicker để phân biệt Ngày sinh và Ngày vào làm
    private JPanel createDatePanel(boolean isNgayVaoLam) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);

        UtilDateModel model = new UtilDateModel();
        Calendar cal = Calendar.getInstance();
        
        if (!isNgayVaoLam) {
             // Ngày sinh: Mặc định 18 năm trước
             int currentYear = cal.get(Calendar.YEAR);
             model.setDate(currentYear - 18, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
             datePickerNS = new JDatePickerImpl(new JDatePanelImpl(model, new Properties()), new DateLabelFormatter());
        } else {
             // Ngày vào làm: Mặc định hôm nay
             model.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
             datePickerNVL = new JDatePickerImpl(new JDatePanelImpl(model, new Properties()), new DateLabelFormatter());
        }
        
        model.setSelected(true);

        Properties p = new Properties();
        p.put("text.today", "Hôm nay");
        p.put("text.month", "Tháng");
        p.put("text.year", "Năm");

        JDatePickerImpl picker = isNgayVaoLam ? datePickerNVL : datePickerNS;
        JDatePanelImpl datePanelImpl = new JDatePanelImpl(model, p);
        picker = new JDatePickerImpl(datePanelImpl, new DateLabelFormatter());

        JFormattedTextField dateTextField = picker.getJFormattedTextField();
        dateTextField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateTextField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        dateTextField.setBackground(INPUT_BG);
        dateTextField.setForeground(TEXT_PRIMARY);
        dateTextField.setPreferredSize(new Dimension(220, dateTextField.getPreferredSize().height + 4));

        dateTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                dateTextField.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(PRIMARY_COLOR, 2, true),
                        new EmptyBorder(7, 11, 7, 11)
                ));
                dateTextField.setBackground(Color.WHITE);
            }

            @Override
            public void focusLost(FocusEvent e) {
                dateTextField.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_COLOR, 1, true),
                        new EmptyBorder(8, 12, 8, 12)
                ));
                dateTextField.setBackground(INPUT_BG);
            }
        });

        panel.add(picker);
        return panel;
    }


    private JPanel createPasswordPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        txtPass = new JPasswordField();
        styleTextField(txtPass);
        txtPass.setEchoChar('●');
        txtPass.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(txtPass);

        JPanel strengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        strengthPanel.setOpaque(false);
        strengthPanel.setBorder(new EmptyBorder(6, 0, 0, 0));
        strengthPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblPasswordStrength = new JLabel(" ");
        lblPasswordStrength.setFont(new Font("Segoe UI", Font.BOLD, 11));
        strengthPanel.add(lblPasswordStrength);

        JLabel hintLabel = new JLabel(" (8-20 ký tự, 1 hoa, 1 thường, 1 số, 1 ký tự đặc biệt)");
        hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        hintLabel.setForeground(TEXT_SECONDARY);
        strengthPanel.add(hintLabel);

        panel.add(strengthPanel);

        txtPass.addCaretListener(e -> updatePasswordStrength());

        return panel;
    }

    private void updatePasswordStrength() {
        String password = new String(txtPass.getPassword());
        if (password.isEmpty()) {
            lblPasswordStrength.setText(" ");
            return;
        }

        int strength = 0;
        if (password.length() >= 8 && password.length() <= 20) strength++;
        if (password.matches(".*[A-Z].*")) strength++;
        if (password.matches(".*[a-z].*")) strength++;
        if (password.matches(".*\\d.*")) strength++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) strength++;

        if (strength <= 2) {
            lblPasswordStrength.setText("Yếu");
            lblPasswordStrength.setForeground(DANGER_COLOR);
        } else if (strength <= 4) {
            lblPasswordStrength.setText("Trung bình");
            lblPasswordStrength.setForeground(WARNING_COLOR);
        } else {
            lblPasswordStrength.setText("Mạnh");
            lblPasswordStrength.setForeground(SUCCESS_COLOR);
        }
    }

    private void styleTextField(JTextField textField) {
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        textField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 12, 10, 12)
        ));
        textField.setBackground(INPUT_BG);
        textField.setForeground(TEXT_PRIMARY);

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(PRIMARY_COLOR, 2, true),
                        new EmptyBorder(9, 11, 9, 11)
                ));
                textField.setBackground(Color.WHITE);
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(BORDER_COLOR, 1, true),
                        new EmptyBorder(10, 12, 10, 12)
                ));
                textField.setBackground(INPUT_BG);
            }
        });
    }

    private void styleComboBox(JComboBox<String> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        comboBox.setBackground(INPUT_BG);
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        comboBox.setPreferredSize(new Dimension(comboBox.getPreferredSize().width, 40));
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnCancel = createModernButton("Hủy", false);
        btnCancel.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn hủy? Dữ liệu chưa lưu sẽ bị mất.",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) dispose();
        });

        btnSave = createModernButton("Thêm nhân viên", true);
        btnSave.addActionListener(this::saveNhanVien);

        panel.add(btnCancel);
        panel.add(btnSave);

        return panel;
    }

    private JButton createModernButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 24, 10, 24));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(true);

        if (isPrimary) {
            button.setBackground(PRIMARY_COLOR);
            button.setForeground(Color.WHITE);
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(PRIMARY_DARK);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(PRIMARY_COLOR);
                }
            });
        } else {
            button.setBackground(Color.WHITE);
            button.setForeground(TEXT_PRIMARY);
            button.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(BORDER_COLOR, 1, true),
                    new EmptyBorder(9, 23, 9, 23)
            ));
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(BACKGROUND);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(Color.WHITE);
                }
            });
        }

        return button;
    }

    private void saveNhanVien(ActionEvent e) {
        try {
            // Lấy dữ liệu cho các trường mới
            String maNV = "NV" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            String tenNV = txtTenNV.getText().trim();
            boolean gioiTinh = rdNam.isSelected();
            
            // Ngày sinh
            Date selectedDateNSUtil = null;
            Object modelValueNS = datePickerNS.getModel().getValue();
            if (modelValueNS instanceof Date) {
                 selectedDateNSUtil = (Date) modelValueNS;
            }
            if (selectedDateNSUtil == null) {
                showError("Vui lòng chọn ngày sinh!");
                datePickerNS.getJFormattedTextField().requestFocus();
                return;
            }
            LocalDate ngaySinh = selectedDateNSUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            // Ngày vào làm
            Date selectedDateNVLUtil = null;
            Object modelValueNVL = datePickerNVL.getModel().getValue();
            if (modelValueNVL instanceof Date) {
                 selectedDateNVLUtil = (Date) modelValueNVL;
            }
            if (selectedDateNVLUtil == null) {
                showError("Vui lòng chọn ngày vào làm!");
                datePickerNVL.getJFormattedTextField().requestFocus();
                return;
            }
            LocalDate ngayVaoLam = selectedDateNVLUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            
            String cccd = txtCCCD.getText().trim();
            String sdt = txtSDT.getText().trim();
            String email = txtEmail.getText().trim();
            String chucVuDisplay = (String) cbChucVu.getSelectedItem();
            String trangThai = (String) cbTrangThai.getSelectedItem();

            String user = txtUser.getText().trim();
            String pass = new String(txtPass.getPassword()).trim();
            String vaiTroTK = chucVuDisplay.equals("Nhân viên quản lý") ? "NVQL" : "NVTT"; // Vai trò TK

            // Ràng buộc
            if (tenNV.isEmpty()) { showError("Tên nhân viên không được để trống!"); txtTenNV.requestFocus(); return; }
            if (sdt.isEmpty()) { showError("Số điện thoại không được để trống!"); txtSDT.requestFocus(); return; }
            if (cccd.isEmpty()) { showError("Số CCCD không được để trống!"); txtCCCD.requestFocus(); return; }
            if (user.isEmpty()) { showError("Tên đăng nhập không được để trống!"); txtUser.requestFocus(); return; }
            if (pass.isEmpty()) { showError("Mật khẩu không được để trống!"); txtPass.requestFocus(); return; }

            if (!isValidPassword(pass)) {
                showError("Mật khẩu không đáp ứng yêu cầu:\n\n" +
                          "• 8-20 ký tự\n" +
                          "• Ít nhất 1 chữ hoa\n" +
                          "• Ít nhất 1 chữ thường\n" +
                          "• Ít nhất 1 số\n" +
                          "• Ít nhất 1 ký tự đặc biệt (!@#$...)\n");
                 txtPass.requestFocus();
                return;
            }
            
            // Khởi tạo Entity TaiKhoan và NhanVien
            TaiKhoan tk = new TaiKhoan();
            tk.setTenDangNhap(user);
            tk.setMatKhau(pass);
            tk.setVaiTro(vaiTroTK);
            tk.setTrangThai(trangThai.equals("Đang làm")); // Trạng thái TK = Trạng thái NV

            NhanVien nv = new NhanVien();
            nv.setMaNhanVien(maNV); // SỬA: Dùng setMaNhanVien
            nv.setHoTen(tenNV); // SỬA: Dùng setHoTen
            nv.setGioiTinh(gioiTinh);
            nv.setNgaySinh(ngaySinh);
            nv.setNgayVaoLam(ngayVaoLam); // MỚI
            nv.setSoCCCD(cccd); // MỚI
            nv.setSoDienThoai(sdt); // SỬA: Dùng setSoDienThoai
            nv.setEmail(email.isEmpty() ? null : email);
            nv.setChucVu(chucVuDisplay); // MỚI
            nv.setTrangThai(trangThai); // MỚI
            nv.setTaiKhoan(tk);
            tk.setNhanVien(nv); // Thiết lập mối quan hệ hai chiều

            boolean added = nhanVienDAO.insertNhanVien(nv);
            if (added) {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                showError("Thêm nhân viên thất bại! Có thể do lỗi kết nối hoặc mã/tên đăng nhập bị trùng.");
            }

        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showError("Đã xảy ra lỗi không mong muốn: " + ex.getMessage());
        }
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 20) return false;
        return password.matches(".*[A-Z].*") && password.matches(".*[a-z].*")
                && password.matches(".*\\d.*") && password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    private class ShadowBorder extends LineBorder {
        public ShadowBorder() {
            super(new Color(0, 0, 0, 0), 0, true);
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(0, 0, 0, 10));
            for (int i = 0; i < 4; i++) {
                g2.drawRoundRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1, 12, 12);
            }

            g2.setColor(BORDER_COLOR);
            g2.drawRoundRect(x, y, width - 1, height - 1, 12, 12);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(4, 4, 4, 4);
        }

         @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = 4;
            return insets;
        }
    }


    private static class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
        private final java.text.SimpleDateFormat dateFormatter =
                new java.text.SimpleDateFormat("dd/MM/yyyy");

        @Override
        public Object stringToValue(String text) throws ParseException {
            if (text == null || text.trim().isEmpty()) return null;
            try {
                 return dateFormatter.parse(text);
            } catch (ParseException e) {
                 throw e;
            }
        }

        @Override
        public String valueToString(Object value) throws ParseException {
            if (value == null) return "";
            if (value instanceof Date) return dateFormatter.format((Date) value);
            if (value instanceof Calendar) return dateFormatter.format(((Calendar) value).getTime());
            return "";
        }
    }
}