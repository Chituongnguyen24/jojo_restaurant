package view;

import entity.Thue;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

import dao.HoaDon_Thue_DAO;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class HoaDon_Thue_View extends JPanel {
    private JTable table;
    private JTextField searchField;
    private DefaultTableModel model;
    private HoaDon_Thue_DAO thueDAO;

    public HoaDon_Thue_View() {
        setLayout(new BorderLayout());
        setBackground(new Color(252, 249, 244));
//        loadData();
        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(10, 20, 10, 20));

        JLabel title = new JLabel("Quản lý thuế");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(40, 30, 20));

        JLabel subtitle = new JLabel("Tạo và quản lý thông tin thuế");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 90, 80));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(title);
        titlePanel.add(subtitle);

        JButton btnAdd = new JButton("+ Thêm thuế mới");
        btnAdd.setBackground(new Color(220, 100, 30));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setFont(new Font("Arial", Font.BOLD, 13));
        btnAdd.setBorder(new EmptyBorder(8, 15, 8, 15));
        btnAdd.addActionListener(e -> JOptionPane.showMessageDialog(this, "Thêm thuế mới - Gọi DAO.add()"));

        header.add(titlePanel, BorderLayout.WEST);
        header.add(btnAdd, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ===== STATS =====
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        statsPanel.add(createStatBox("3", "Đang áp dụng", new Color(100, 200, 100)));
        statsPanel.add(createStatBox("1", "Tạm ngưng", new Color(255, 100, 100)));
        statsPanel.add(createStatBox("4", "Tổng loại thuế", new Color(255, 153, 51)));

        // ===== SEARCH =====
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        searchPanel.setOpaque(false);

        searchField = new JTextField("🔍 Tìm kiếm thuế...");
        searchField.setFont(new Font("Arial", Font.ITALIC, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(210, 120, 40), 2, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
//        searchField.addActionListener(e -> filterTable());  // Simple search on Enter

        JButton btnFilter = new JButton("Tất cả ▾");
        btnFilter.setFocusPainted(false);
//        btnFilter.addActionListener(e -> loadData());  // Reload all

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(btnFilter, BorderLayout.EAST);

        // ===== TABLE =====
        String[] cols = {"Mã thuế", "Tên thuế", "Phần trăm (%)", "Mô tả", "Trạng thái", "Thao tác"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // Thêm ButtonRenderer/Editor cho cột "Thao tác"
//        table.getColumn("Thao tác").setCellRenderer(new ButtonRenderer());
//        table.getColumn("Thao tác").setCellEditor(new ButtonEditor(this));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new EmptyBorder(10, 20, 20, 20));

        JPanel tablePanel = new JPanel(new BorderLayout());
        JLabel lblTableTitle = new JLabel("Danh sách thuế");
        lblTableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblTableTitle.setBorder(new EmptyBorder(5, 20, 5, 0));

        JLabel lblTableSub = new JLabel("Quản lý thông tin thuế");
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