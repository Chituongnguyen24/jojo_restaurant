package view.HoaDon;
import entity.KhuyenMai;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import dao.HoaDon_KhuyenMai_DAO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class KhuyenMai_EditDialog extends JDialog {
    private JTextField txtTenKM, txtGiaTri, txtNgayBD, txtNgayKT;
    private JButton btnSave, btnCancel;
    private HoaDon_KhuyenMai_DAO khuyenMaiDAO;
    private KhuyenMai khuyenMai;

    public KhuyenMai_EditDialog(Frame owner, KhuyenMai khuyenMai, HoaDon_KhuyenMai_DAO khuyenMaiDAO) {
        super(owner, "Chỉnh sửa khuyến mãi", true);
        this.khuyenMaiDAO = khuyenMaiDAO;
        this.khuyenMai = khuyenMai;

        setSize(800, 650);
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

        JLabel lblTitle = new JLabel("Chỉnh sửa khuyến mãi");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(new Color(30, 30, 35));
        
        JLabel lblSubtitle = new JLabel("Cập nhật thông tin khuyến mãi vào form bên dưới");
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

        // Tên khuyến mãi
        formCard.add(createFieldPanel("Tên khuyến mãi *", 
            txtTenKM = createStyledTextField("Nhập tên khuyến mãi", khuyenMai.getTenKM())));
        formCard.add(Box.createVerticalStrut(18));

        // Giá trị
        formCard.add(createFieldPanel("Giá trị *", 
            txtGiaTri = createStyledTextField("Nhập giá trị (số)", String.valueOf(khuyenMai.getGiaTri()))));
        formCard.add(Box.createVerticalStrut(18));

        // Ngày bắt đầu
        formCard.add(createFieldPanel("Ngày bắt đầu *", 
            txtNgayBD = createStyledTextField("yyyy-MM-dd", khuyenMai.getNgayBatDau().toString())));
        formCard.add(Box.createVerticalStrut(18));

        // Ngày kết thúc
        formCard.add(createFieldPanel("Ngày kết thúc *", 
            txtNgayKT = createStyledTextField("yyyy-MM-dd", khuyenMai.getNgayKetThuc().toString())));
        formCard.add(Box.createVerticalStrut(18));

        mainPanel.add(formCard, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // ===== Button Panel =====
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        btnPanel.setBorder(new EmptyBorder(0, 25, 25, 25));
        btnPanel.setBackground(new Color(245, 245, 250));

        btnCancel = createModernButton("Hủy", new Color(220, 220, 225), new Color(60, 60, 70));
        btnCancel.addActionListener(e -> dispose());

        btnSave = createModernButton("Lưu khuyến mãi", new Color(76, 175, 80), Color.WHITE);
        btnSave.addActionListener(this::saveChanges);

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        add(btnPanel, BorderLayout.SOUTH);
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

    private JTextField createStyledTextField(String placeholder, String initialValue) {
        JTextField field = new JTextField() {
            private String placeholderText = placeholder;
            private boolean showingPlaceholder = initialValue == null || initialValue.trim().isEmpty();

            {
                setText(showingPlaceholder ? placeholderText : initialValue);
                setForeground(showingPlaceholder ? new Color(160, 160, 170) : new Color(40, 40, 50));
                
                addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (showingPlaceholder) {
                            setText("");
                            setForeground(new Color(40, 40, 50));
                            showingPlaceholder = false;
                        }
                        setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(8, new Color(76, 175, 80)),
                            BorderFactory.createEmptyBorder(12, 16, 12, 16)
                        ));
                    }

                    @Override
                    public void focusLost(FocusEvent e) {
                        if (getText().trim().isEmpty()) {
                            setText(placeholderText);
                            setForeground(new Color(160, 160, 170));
                            showingPlaceholder = true;
                        }
                        setBorder(BorderFactory.createCompoundBorder(
                            new RoundedBorder(8, new Color(220, 220, 230)),
                            BorderFactory.createEmptyBorder(12, 16, 12, 16)
                        ));
                    }
                });
            }

            @Override
            public String getText() {
                return showingPlaceholder ? "" : super.getText();
            }
        };

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(250, 250, 252));
        field.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(8, new Color(220, 220, 230)),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setPreferredSize(new Dimension(400, 45));

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

    private void saveChanges(ActionEvent e) {
        try {
            String tenKM = txtTenKM.getText().trim();
            String giaTriStr = txtGiaTri.getText().trim();
            String ngayBDStr = txtNgayBD.getText().trim();
            String ngayKTStr = txtNgayKT.getText().trim();

            // Validation
            if (tenKM.isEmpty() || giaTriStr.isEmpty() || ngayBDStr.isEmpty() || ngayKTStr.isEmpty()) {
                showErrorDialog("Vui lòng điền đầy đủ thông tin bắt buộc!");
                return;
            }

            double giaTri;
            try {
                giaTri = Double.parseDouble(giaTriStr);
                if (giaTri <= 0) {
                    showErrorDialog("Giá trị khuyến mãi phải lớn hơn 0!");
                    return;
                }
            } catch (NumberFormatException ex) {
                showErrorDialog("Giá trị phải là số hợp lệ!");
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDate ngayBD, ngayKT;
            try {
                ngayBD = LocalDate.parse(ngayBDStr, formatter);
                ngayKT = LocalDate.parse(ngayKTStr, formatter);
            } catch (Exception ex) {
                showErrorDialog("Ngày phải có định dạng yyyy-MM-dd!");
                return;
            }

            if (ngayBD.isAfter(ngayKT)) {
                showErrorDialog("Ngày bắt đầu phải trước ngày kết thúc!");
                return;
            }

            LocalDate now = LocalDate.now();
            String trangThai;
            if (now.isBefore(ngayBD)) {
                trangThai = "Sắp bắt đầu";
            } else if (now.isAfter(ngayKT)) {
                trangThai = "Hết hạn";
            } else {
                trangThai = "Hoạt động";
            }

            // Cập nhật thông tin
            khuyenMai.setTenKM(tenKM);
            khuyenMai.setGiaTri(giaTri);
            khuyenMai.setNgayBatDau(ngayBD);
            khuyenMai.setNgayKetThuc(ngayKT);
            khuyenMai.setTrangThai(trangThai);

            boolean updated = khuyenMaiDAO.updateKhuyenMai(khuyenMai);
            if (updated) {
                showSuccessDialog("Cập nhật thông tin khuyến mãi thành công!");
                dispose();
            } else {
                showErrorDialog("Không thể cập nhật khuyến mãi. Vui lòng thử lại!");
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