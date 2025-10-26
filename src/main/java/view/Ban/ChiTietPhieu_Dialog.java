package view.Ban;

import dao.DatBan_DAO;
import entity.PhieuDatBan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ChiTietPhieu_Dialog extends JDialog {

    private PhieuDatBan phieuDatBan;
    private DatBan_DAO datBanDAO;
    private DefaultTableModel modelChiTietMon;

    public ChiTietPhieu_Dialog(Frame owner, PhieuDatBan pdb) {
        super(owner, "Chi tiết Phiếu đặt bàn: " + (pdb != null ? pdb.getMaPhieu().trim() : "N/A"), true);
        this.phieuDatBan = pdb;
        this.datBanDAO = new DatBan_DAO();

        initComponents();
        loadData();
    }

    private void initComponents() {
        setSize(550, 450); // Kích thước dialog
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout(10, 10));
        getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        if (phieuDatBan == null) {
            add(new JLabel("Không tìm thấy thông tin phiếu đặt.", SwingConstants.CENTER), BorderLayout.CENTER);
            return;
        }

        // --- Panel Thông tin chung ---
        JPanel pnlThongTin = new JPanel(new GridBagLayout());
        pnlThongTin.setBorder(BorderFactory.createTitledBorder("Thông tin chung"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Các label
        addInfoRow(pnlThongTin, gbc, 0, "Mã phiếu:", phieuDatBan.getMaPhieu().trim());
        addInfoRow(pnlThongTin, gbc, 1, "Bàn:", phieuDatBan.getBan() != null ? phieuDatBan.getBan().getMaBan().trim() : "N/A");
        addInfoRow(pnlThongTin, gbc, 2, "Thời gian đến:", phieuDatBan.getThoiGianDatFormatted());
        addInfoRow(pnlThongTin, gbc, 3, "Số người:", String.valueOf(phieuDatBan.getSoNguoi()));

        // Định dạng tiền cọc
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        addInfoRow(pnlThongTin, gbc, 4, "Tiền cọc:", currencyFormat.format(phieuDatBan.getTienCoc()));

        // Ghi chú (có thể dài, dùng JTextArea)
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.NORTHWEST; // Căn lề trên trái
        pnlThongTin.add(new JLabel("Ghi chú:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0; // Cho phép giãn cả 2 chiều
        JTextArea txtGhiChuArea = new JTextArea(phieuDatBan.getGhiChu() != null ? phieuDatBan.getGhiChu() : "");
        txtGhiChuArea.setLineWrap(true);
        txtGhiChuArea.setWrapStyleWord(true);
        txtGhiChuArea.setEditable(false);
        txtGhiChuArea.setBackground(pnlThongTin.getBackground()); // Nền giống panel
        JScrollPane scrollGhiChu = new JScrollPane(txtGhiChuArea);
        scrollGhiChu.setPreferredSize(new Dimension(200, 60)); // Kích thước ưu tiên
        pnlThongTin.add(scrollGhiChu, gbc);


        // --- Panel Danh sách món ---
        JPanel pnlMon = new JPanel(new BorderLayout(0, 5));
        pnlMon.setBorder(BorderFactory.createTitledBorder("Danh sách món đã đặt"));

        modelChiTietMon = new DefaultTableModel(new String[]{"Mã Món", "Tên Món", "Số Lượng", "Ghi Chú"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tblChiTiet = new JTable(modelChiTietMon);
        tblChiTiet.setRowHeight(25);
        // Style header (tùy chọn)
        JTableHeader header = tblChiTiet.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));

        pnlMon.add(new JScrollPane(tblChiTiet), BorderLayout.CENTER);

        // --- Nút Đóng ---
        JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDong = new JButton("Đóng");
        btnDong.addActionListener(e -> dispose());
        pnlButton.add(btnDong);

        // --- Bố cục chính của Dialog ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlThongTin, pnlMon);
        splitPane.setResizeWeight(0.4); // Panel thông tin chiếm khoảng 40%
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
        add(pnlButton, BorderLayout.SOUTH);
    }

    // Hàm trợ giúp thêm dòng thông tin vào GridBagLayout
    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, String valueText) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        JLabel label = new JLabel(labelText);
        label.setFont(label.getFont().deriveFont(Font.BOLD)); // In đậm label
        panel.add(label, gbc);

        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel value = new JLabel(valueText != null ? valueText : "N/A");
        panel.add(value, gbc);
    }

    private void loadData() {
        if (phieuDatBan == null) return;

        // Tải danh sách món từ DAO
        List<Object[]> dsMon = datBanDAO.getChiTietTheoMaPhieu(phieuDatBan.getMaPhieu());
        modelChiTietMon.setRowCount(0); // Xóa dữ liệu cũ
        for (Object[] rowData : dsMon) {
            // rowData[0] = maMonAn, rowData[1] = tenMonAn, rowData[2] = soLuong, rowData[3] = ghiChu
            modelChiTietMon.addRow(rowData);
        }
    }
}