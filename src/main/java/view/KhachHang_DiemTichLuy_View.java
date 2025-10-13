package view;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;

public class KhachHang_DiemTichLuy_View extends JPanel {
    private JTable table;
    private JTextField searchField;

    public KhachHang_DiemTichLuy_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(252, 249, 244));

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Quản lý điểm tích lũy");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(40, 30, 20));

        JLabel subtitle = new JLabel("Cập nhật và quản lý điểm thưởng khách hàng");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 90, 80));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JButton btnAdd = new JButton("+ Cập nhật điểm");
        btnAdd.setBackground(new Color(220, 100, 30));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 13));
        btnAdd.setBorder(new EmptyBorder(8, 15, 8, 15));

        header.add(titlePanel, BorderLayout.WEST);
        header.add(btnAdd, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ===== STATS =====
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        statsPanel.add(createStatBox("1,250", "Tổng điểm", new Color(255, 153, 51)));
        statsPanel.add(createStatBox("15", "Khách VIP", new Color(100, 200, 100)));
        statsPanel.add(createStatBox("5", "Cập nhật hôm nay", new Color(255, 200, 100)));

        // ===== SEARCH =====
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        searchPanel.setOpaque(false);

        searchField = new JTextField("🔍 Tìm kiếm khách hàng...");
        searchField.setFont(new Font("Arial", Font.ITALIC, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 120, 40), 2, true),
                new EmptyBorder(5, 10, 5, 10)
        ));

        JButton btnFilter = new JButton("Tất cả ▾");
        btnFilter.setFocusPainted(false);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(btnFilter, BorderLayout.EAST);

        // ===== TABLE =====
        String[] cols = {"Mã KH", "Tên khách hàng", "Điểm hiện tại", "Điểm cập nhật", "Ngày cập nhật", "Trạng thái", "Thao tác"};
        Object[][] data = {
                {"KH001", "Nguyễn Văn A", "1,250", "+100", "2025-10-01", "Hoạt động", "✎ 🗑"},
                {"KH002", "Trần Thị B", "680", "+50", "2025-10-01", "Hoạt động", "✎ 🗑"},
                {"KH003", "Lê Hoàng C", "450", "+0", "2025-10-01", "Hoạt động", "✎ 🗑"}
        };

        DefaultTableModel model = new DefaultTableModel(data, cols);
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(10, 20, 20, 20));

        JPanel tablePanel = new JPanel(new BorderLayout());
        JLabel lblTableTitle = new JLabel("Danh sách điểm tích lũy");
        lblTableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTableTitle.setBorder(new EmptyBorder(5, 20, 5, 0));

        JLabel lblTableSub = new JLabel("Quản lý điểm thưởng khách hàng");
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