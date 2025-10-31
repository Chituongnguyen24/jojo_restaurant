package view.KhachHang;

import dao.KhachHang_DAO;
import entity.KhachHang;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

public class KhachHang_AddDialog extends JDialog {
    private JTextField txtTenKH, txtSDT, txtEmail;
    private JDatePickerImpl datePickerNS; // THÊM: DatePicker cho NgaySinh
    private JButton btnSave, btnCancel;
    private KhachHang_DAO khachHangDAO;

    public KhachHang_AddDialog(Frame owner, KhachHang_DAO khachHangDAO) {
        super(owner, "Thêm khách hàng mới", true);
        this.khachHangDAO = khachHangDAO;

        setSize(800, 600);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 250));

        // ===== Header với icon =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(255, 255, 255));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 235)),
            new EmptyBorder(20, 25, 20, 25)
        ));

        JLabel lblTitle = new JLabel("Thêm khách hàng mới");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 30, 35));
        
        JLabel lblSubtitle = new JLabel("Điền thông tin khách hàng vào form bên dưới");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(120, 120, 130));

        JPanel titleContainer = new JPanel(new GridLayout(2, 1, 0, 5));
        titleContainer.setOpaque(false);
        titleContainer.add(lblTitle);
        titleContainer.add(lblSubtitle);

        headerPanel.add(titleContainer, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // ===== Form Panel với card style =====
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 250));
        mainPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel formCard = new JPanel();
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setBackground(Color.WHITE);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(12, new Color(230, 230, 235)),
            new EmptyBorder(25, 25, 25, 25)
        ));

        // Tên khách hàng
        formCard.add(createFieldPanel("Tên khách hàng *", 
            txtTenKH = createStyledTextField("Nhập họ và tên")));
        formCard.add(Box.createVerticalStrut(18));

        // Số điện thoại
        formCard.add(createFieldPanel("Số điện thoại *", 
            txtSDT = createStyledTextField("Nhập số điện thoại")));
        formCard.add(Box.createVerticalStrut(18));

        // Email
        formCard.add(createFieldPanel("Email *", 
            txtEmail = createStyledTextField("Nhập địa chỉ email")));
        formCard.add(Box.createVerticalStrut(18));
        
        // Ngày sinh (MỚI)
        formCard.add(createDateFieldPanel("Ngày sinh", datePickerNS = createDatePicker()));
        formCard.add(Box.createVerticalStrut(18));

        // Info note
        JPanel infoPanel = new JPanel(new BorderLayout(10, 0));
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, new Color(33, 150, 243)),
            new EmptyBorder(12, 15, 12, 15)
        ));
        infoPanel.setBackground(new Color(227, 242, 253));

        mainPanel.add(formCard, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // ===== Button Panel =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnPanel.setBorder(new EmptyBorder(0, 25, 25, 25));
        btnPanel.setBackground(new Color(245, 245, 250));

        btnCancel = createModernButton("Hủy", new Color(220, 220, 225), new Color(60, 60, 70));
        btnCancel.addActionListener(e -> dispose());

        btnSave = createModernButton("Lưu khách hàng", new Color(76, 175, 80), Color.WHITE);
        btnSave.addActionListener(this::saveKhachHang);

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        add(btnPanel, BorderLayout.SOUTH);
    }
    
    // THÊM: Tạo DatePicker
    private JDatePickerImpl createDatePicker() {
        UtilDateModel model = new UtilDateModel();
        Calendar cal = Calendar.getInstance();
        model.setDate(cal.get(Calendar.YEAR) - 18, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        model.setSelected(false); // Bắt đầu không chọn gì

        Properties p = new Properties();
        p.put("text.today", "Hôm nay");
        p.put("text.month", "Tháng");
        p.put("text.year", "Năm");

        JDatePanelImpl datePanelImpl = new JDatePanelImpl(model, p);
        JDatePickerImpl picker = new JDatePickerImpl(datePanelImpl, new DateLabelFormatter());
        
        JFormattedTextField dateTextField = picker.getJFormattedTextField();
        dateTextField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateTextField.setPreferredSize(new Dimension(400, 45));
        dateTextField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        dateTextField.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, new Color(220, 220, 230)),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        
        dateTextField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                dateTextField.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(8, new Color(76, 175, 80)),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                dateTextField.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(8, new Color(220, 220, 230)),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }
        });
        
        return picker;
    }
    
    // THÊM: Panel cho DateField
    private JPanel createDateFieldPanel(String label, JDatePickerImpl datePicker) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(50, 50, 60));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        datePicker.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(8));
        panel.add(datePicker);

        return panel;
    }

    private JPanel createFieldPanel(String label, JTextField textField) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(50, 50, 60));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        textField.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(lbl);
        panel.add(Box.createVerticalStrut(8));
        panel.add(textField);

        return panel;
    }
    
    // SỬA: Bỏ logic Placeholder khỏi hàm tạo field
    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(250, 250, 252));
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, new Color(220, 220, 230)),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setPreferredSize(new Dimension(400, 45));
        
        // Thêm Placeholder Logic
        field.setText(placeholder);
        field.setForeground(new Color(160, 160, 170));
        field.putClientProperty("placeholder", placeholder);

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(new Color(40, 40, 50));
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(8, new Color(76, 175, 80)),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(new Color(160, 160, 170));
                }
                field.setBorder(BorderFactory.createCompoundBorder(
                    new RoundedBorder(8, new Color(220, 220, 230)),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }
        });

        return field;
    }


    private JButton createModernButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color buttonColor = bg;
                if (getModel().isPressed()) {
                    buttonColor = bg.darker();
                } else if (getModel().isRollover()) {
                    buttonColor = new Color(
                        Math.min(255, bg.getRed() + 15),
                        Math.min(255, bg.getGreen() + 15),
                        Math.min(255, bg.getBlue() + 15)
                    );
                }

                g2.setColor(buttonColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();

                super.paintComponent(g);
            }
        };

        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(text.equals("Hủy") ? 90 : 140, 40));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return btn;
    }

    private String getFieldValue(JTextField field) {
        String placeholder = (String) field.getClientProperty("placeholder");
        String text = field.getText().trim();
        return text.equals(placeholder) ? "" : text;
    }

    private void saveKhachHang(ActionEvent e) {
        try {
            String tenKH = getFieldValue(txtTenKH);
            String sdt = getFieldValue(txtSDT);
            String email = getFieldValue(txtEmail);
            
            Date selectedDateUtil = (Date) datePickerNS.getModel().getValue();
            LocalDate ngaySinh = null;
            if (selectedDateUtil != null) {
                ngaySinh = selectedDateUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            }

            // Validation
            if (tenKH.isEmpty() || sdt.isEmpty()) {
                showErrorDialog("Vui lòng điền đầy đủ Tên và SĐT!");
                return;
            }
            // Email là bắt buộc theo code cũ, nên giữ lại
            if (email.isEmpty()) { 
                 showErrorDialog("Vui lòng điền đầy đủ Email!");
                return;
            }

            if (!sdt.matches("\\d{10}")) {
                showErrorDialog("Số điện thoại phải có 10 chữ số!");
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                showErrorDialog("Email không hợp lệ!");
                return;
            }

            String maKH = "KH" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
            
            // SỬA: Dùng constructor 7 tham số
            KhachHang kh = new KhachHang(maKH, tenKH, sdt, email, ngaySinh, 0, true); 

            boolean added = khachHangDAO.insertKhachHang(kh);
            if (added) {
                showSuccessDialog("Thêm khách hàng thành công!");
                dispose();
            } else {
                showErrorDialog("Không thể thêm khách hàng. Vui lòng thử lại!");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorDialog("Lỗi: " + ex.getMessage());
        }
    }

    private void showSuccessDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Thành công", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Lỗi", 
            JOptionPane.ERROR_MESSAGE);
    }
    
    // THÊM: DateLabelFormatter
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


    // ===== Custom Rounded Border =====
    class RoundedBorder implements javax.swing.border.Border {
        private int radius;
        private Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(1, 1, 1, 1);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }
}