package view.Ban;

import dao.DatBan_DAO;
import dao.HoaDon_DAO;
import dao.HoaDon_KhuyenMai_DAO;
import dao.HoaDon_Thue_DAO;
import dao.MonAn_DAO;
import entity.Ban;
import entity.ChiTietHoaDon;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.PhieuDatBan;
import view.HoaDon.HoaDon_ThanhToan_Dialog;
import view.ThucDon.ChonMon_Dialog;
import entity.ChiTietPhieuDatBan;
import entity.MonAn;
import entity.HoaDon;
import entity.Thue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ChiTietPhieuDatBan_View extends JPanel {
    private JTable table;
    private PhieuDatBan phieu;
    private Ban ban;
    private DatBan_DAO daoDatBan = new DatBan_DAO();
    private HoaDon_DAO daoHoaDon = new HoaDon_DAO();
    private Runnable onCloseCallback;

    private JPanel orderDetailsCard; // lưu tham chiếu để reload khi gọi thêm món

    private static final Color PRIMARY_COLOR = new Color(37, 99, 235);
    private static final Color SECONDARY_COLOR = new Color(59, 130, 246);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color ACCENT_COLOR = new Color(249, 115, 22);

    // Decimal formatter for Vietnamese-style grouping (no decimals)
    private static final DecimalFormat CURRENCY_FORMAT;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        CURRENCY_FORMAT = new DecimalFormat("#,##0", symbols);
    }

    public ChiTietPhieuDatBan_View(Ban ban, Runnable onCloseCallback) {
        this.ban = ban;
        this.onCloseCallback = onCloseCallback;
        this.phieu = daoDatBan.getPhieuByBan(ban.getMaBan());

        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_COLOR);

        add(createModernHeader(), BorderLayout.NORTH);

        JScrollPane mainScroll = new JScrollPane(createMainContent());
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(mainScroll, BorderLayout.CENTER);
    }

    private JPanel createModernHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
            new EmptyBorder(20, 30, 20, 30)
        ));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel breadcrumb = new JLabel("Quản lý đặt bàn / Chi tiết món ăn");
        breadcrumb.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        breadcrumb.setForeground(TEXT_SECONDARY);

        JLabel title = new JLabel("Chi tiết món ăn - " + (ban != null ? ban.getMaBan() : "N/A"));
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(TEXT_PRIMARY);

        leftPanel.add(breadcrumb);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(title);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);


        JButton btnGoiMon = createModernButton("Gọi thêm món", PRIMARY_COLOR, true);
        btnGoiMon.addActionListener(e -> openChonMonDialog());

        JButton btnThanhToan = createModernButton("Thanh toán", SUCCESS_COLOR, true);
        btnThanhToan.addActionListener(e -> chuyenDenThanhToan());

        rightPanel.add(btnGoiMon);
        rightPanel.add(btnThanhToan);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private void openChonMonDialog() {
        if (phieu == null) {
            JOptionPane.showMessageDialog(this, "Không có phiếu đặt để thêm món.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy Frame cha để làm owner cho dialog
        Frame owner = null;
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof Frame) owner = (Frame) w;

        ChonMon_Dialog dlg = new ChonMon_Dialog(owner, phieu);
        // Dialog ở view.ThucDon là modal -> khi setVisible(true) trả về sau khi đóng
        dlg.setVisible(true);

        // Sau khi dialog đóng, reload dữ liệu phiếu và phần danh sách món
        reloadPhieuAndOrderDetails();
    }

    private void reloadPhieuAndOrderDetails() {
        // Tải lại phiếu từ DB
        if (ban != null) {
            this.phieu = daoDatBan.getPhieuByBan(ban.getMaBan());
        }

        // Thực hiện reload panel orderDetailsCard: thay thế nội dung cũ bằng nội dung mới
        if (orderDetailsCard != null) {
            Container parent = orderDetailsCard.getParent();
            if (parent != null) {
                int index = -1;
                for (int i = 0; i < parent.getComponentCount(); i++) {
                    if (parent.getComponent(i) == orderDetailsCard) {
                        index = i;
                        break;
                    }
                }
                if (index != -1) {
                    parent.remove(index);
                    orderDetailsCard = createOrderDetailsCard();
                    parent.add(orderDetailsCard, index);
                    parent.revalidate();
                    parent.repaint();
                    return;
                }
            }
        }

        // Nếu không tìm thấy parent/position, fallback: gọi lại tạoMainContent và thay thế center của main panel
        Container top = this;
        // tìm main content (giả định add vào BorderLayout.CENTER)
        for (Component c : getComponents()) {
            if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                Component view = sp.getViewport().getView();
                if (view instanceof JPanel) {
                    JPanel mainPanel = (JPanel) view;
                    // tìm và thay thế orderDetailsCard nếu có
                    for (int i = 0; i < mainPanel.getComponentCount(); i++) {
                        Component child = mainPanel.getComponent(i);
                        // nếu child là JPanel và chứa tiêu đề "Danh sách món ăn" (heuristic)
                        if (child instanceof JPanel) {
                            JPanel p = (JPanel) child;
                            // bỏ qua topSection (index 0), thay thế index 1 nếu layout BorderLayout
                            // safer: just rebuild entire main content
                        }
                    }
                    // fallback: thay thế toàn bộ scroll content
                    remove(sp);
                    JScrollPane newScroll = new JScrollPane(createMainContent());
                    newScroll.setBorder(null);
                    newScroll.getVerticalScrollBar().setUnitIncrement(16);
                    add(newScroll, BorderLayout.CENTER);
                    revalidate();
                    repaint();
                    return;
                }
            }
        }
    }

    private JPanel createMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        JPanel topSection = new JPanel(new GridLayout(1, 2, 20, 0));
        topSection.setOpaque(false);
        topSection.add(createInfoCard());
        topSection.add(createStatusCard());

        mainPanel.add(topSection, BorderLayout.NORTH);

        // Lưu tham chiếu orderDetailsCard để có thể reload sau khi gọi món
        orderDetailsCard = createOrderDetailsCard();
        mainPanel.add(orderDetailsCard, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createInfoCard() {
        JPanel card = createCard("Thông tin bàn");
        card.setLayout(new GridLayout(5, 1, 0, 15));

        if (phieu != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String thoiGianStr = phieu.getThoiGianDat().format(formatter);
            String[] parts = thoiGianStr.split(" ");

            card.add(createInfoRow("Mã phiếu", phieu.getMaPhieu()));

            String khTen = "Khách vãng lai";
            KhachHang kh = phieu.getKhachHang();
            if (kh != null && !"KH00000000".equals(kh.getMaKhachHang().trim())) {
                khTen = kh.getTenKhachHang();
            } else if (phieu.getGhiChu() != null && phieu.getGhiChu().startsWith("Khách: ")) {
                try {
                    String[] ghiChuParts = phieu.getGhiChu().split(" - SĐT: ");
                    if (ghiChuParts.length > 0) {
                        khTen = ghiChuParts[0].substring(7).trim();
                    }
                } catch (Exception e) {}
            }

            card.add(createInfoRow("Khách hàng", khTen));
            card.add(createInfoRow("Số bàn", ban.getMaBan()));
            card.add(createInfoRow("Ngày đặt", parts[0]));
            card.add(createInfoRow("Giờ đặt", parts[1]));
        } else {
            card.add(createInfoRow("Số bàn", ban.getMaBan()));
            card.add(createInfoRow("Trạng thái", "Có khách"));
            card.add(createInfoRow("Loại bàn", ban.getLoaiBan() != null ? ban.getLoaiBan().getTenHienThi() : "N/A"));
            card.add(createInfoRow("Số chỗ", String.valueOf(ban.getSoCho())));
        }

        return card;
    }

    private JPanel createStatusCard() {
        JPanel card = createCard("Trạng thái & Thông tin");
        card.setLayout(new BorderLayout(0, 20));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);

        JLabel statusBadge = new JLabel("Đang phục vụ");
        statusBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statusBadge.setForeground(Color.WHITE);
        statusBadge.setBackground(new Color(220, 53, 69));
        statusBadge.setOpaque(true);
        statusBadge.setBorder(new EmptyBorder(8, 16, 8, 16));

        statusPanel.add(statusBadge);

        JPanel infoGrid = new JPanel(new GridLayout(2, 1, 0, 15));
        infoGrid.setOpaque(false);

        if (phieu != null) {
            infoGrid.add(createInfoRow("Số người", String.valueOf(phieu.getSoNguoi())));

            String ghiChuDisplay = "";
            if (phieu.getGhiChu() != null) {
                int noteIndex = phieu.getGhiChu().indexOf(". Ghi chú: ");
                if (noteIndex != -1) {
                    ghiChuDisplay = phieu.getGhiChu().substring(noteIndex + 10).trim();
                }
            }
            infoGrid.add(createInfoRow("Ghi chú", !ghiChuDisplay.isEmpty() ? ghiChuDisplay : "Không có"));
        }

        JPanel summaryPanel = new JPanel(new BorderLayout(10, 0));
        summaryPanel.setOpaque(false);
        summaryPanel.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(12, 15, 12, 15)
        ));
        summaryPanel.setBackground(new Color(239, 246, 255));

        List<ChiTietPhieuDatBan> chiTietList = phieu != null ?
            daoDatBan.getChiTietByPhieuId(phieu.getMaPhieu()) :
            new java.util.ArrayList<>();
        int totalItems = chiTietList.size();

        JLabel summaryText = new JLabel("<html><b>" + totalItems + "</b> món ăn đã đặt</html>");
        summaryText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        summaryText.setForeground(TEXT_PRIMARY);

        summaryPanel.add(summaryText, BorderLayout.CENTER);

        card.add(statusPanel, BorderLayout.NORTH);
        card.add(infoGrid, BorderLayout.CENTER);
        card.add(summaryPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createOrderDetailsCard() {
        JPanel card = createCard("Danh sách món ăn");
        card.setLayout(new BorderLayout(0, 15));

        List<ChiTietPhieuDatBan> chiTietList = phieu != null ?
            daoDatBan.getChiTietByPhieuId(phieu.getMaPhieu()) :
            new java.util.ArrayList<>();

        String[] cols = {"STT", "Tên món ăn", "Đơn giá", "Số lượng", "Thành tiền"};
        Object[][] data = new Object[chiTietList.size()][5];
        double tongTien = 0.0;

        for (int i = 0; i < chiTietList.size(); i++) {
            ChiTietPhieuDatBan ct = chiTietList.get(i);
            MonAn mon = ct.getMonAn() != null ? ct.getMonAn() : new MonAn();

            data[i][0] = i + 1;
            data[i][1] = mon.getTenMonAn() != null ? mon.getTenMonAn() : "N/A";

            // Fix: ensure we retrieve unit price correctly (from detail or from MonAn if detail doesn't have it)
            double donGia = 0;
            try {
                if (ct.getDonGia() > 0) {
                    donGia = ct.getDonGia();
                } else if (mon != null && mon.getDonGia() > 0) {
                    donGia = mon.getDonGia();
                }
            } catch (Exception ex) {
                donGia = 0;
            }

            int soLuong = ct.getSoLuongMonAn();
            double thanhTien = donGia * soLuong;

            // Use proper number formatting instead of casting to int (prevents losing precision)
            data[i][2] = CURRENCY_FORMAT.format(donGia);
            data[i][3] = soLuong;
            data[i][4] = CURRENCY_FORMAT.format(thanhTien);

            tongTien += thanhTien;
        }

        DefaultTableModel model = new DefaultTableModel(data, cols) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_SECONDARY);
        header.setBorder(new MatteBorder(0, 0, 2, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);

        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(300);
        table.getColumnModel().getColumn(2).setPreferredWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setOpaque(false);
        totalPanel.setBorder(new CompoundBorder(
            new MatteBorder(2, 0, 0, 0, BORDER_COLOR),
            new EmptyBorder(20, 0, 0, 0)
        ));

        JPanel totalRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        totalRight.setOpaque(false);

        JLabel lblTotalLabel = new JLabel("Tổng cộng:");
        lblTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotalLabel.setForeground(TEXT_SECONDARY);

        JLabel lblTotal = new JLabel(CURRENCY_FORMAT.format(tongTien) + " VNĐ");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTotal.setForeground(ACCENT_COLOR);

        totalRight.add(lblTotalLabel);
        totalRight.add(lblTotal);
        totalPanel.add(totalRight, BorderLayout.EAST);

        card.add(scrollPane, BorderLayout.CENTER);
        card.add(totalPanel, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createCard(String title) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(0, 20));
        card.setBackground(CARD_COLOR);
        card.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1, true),
            new EmptyBorder(25, 25, 25, 25)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.setBorder(new MatteBorder(0, 0, 1, 0, BORDER_COLOR));

        card.add(titlePanel, BorderLayout.NORTH);

        return card;
    }

    private JPanel createInfoRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        textPanel.setOpaque(false);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblLabel.setForeground(TEXT_SECONDARY);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblValue.setForeground(TEXT_PRIMARY);

        textPanel.add(lblLabel);
        textPanel.add(lblValue);

        row.add(textPanel, BorderLayout.CENTER);

        return row;
    }

    private JButton createModernButton(String text, Color bgColor, boolean isPrimary) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ensure background color is shown on all LAFs and the button doesn't appear white
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                try {
                    btn.setBackground(bgColor.darker());
                } catch (Exception ex) {
                    btn.setBackground(bgColor);
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    private void chuyenDenThanhToan() {
        if (ban == null) {
            JOptionPane.showMessageDialog(this, "Không có thông tin bàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Chuyển đến trang thanh toán
        JFrame mainFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (mainFrame == null) {
            JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy cửa sổ chính.", "Lỗi hệ thống", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Runnable goBackCallback = () -> {
            if (onCloseCallback != null) {
                onCloseCallback.run();
            }
        };

        try {
            HoaDon hoaDonHienTai = daoHoaDon.getHoaDonByBanChuaThanhToan(ban.getMaBan());

            if (hoaDonHienTai == null) {
                JOptionPane.showMessageDialog(this,
                    "Không tìm thấy hóa đơn chưa thanh toán cho bàn " + ban.getMaBan(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return; // Dừng lại nếu không có hóa đơn
            }

            List<ChiTietHoaDon> chiTietList = daoHoaDon.getChiTietHoaDonForPrint(hoaDonHienTai.getMaHoaDon());
            if (chiTietList == null) chiTietList = new ArrayList<>();

            // Tính toán chi tiết tiền
            double tongTienMonAn = 0;
            for (ChiTietHoaDon ct : chiTietList) {
                try {
                    tongTienMonAn += ct.tinhThanhTien();
                } catch (Exception ex) {
                    double donGia = ct.getDonGia() > 0 ? ct.getDonGia() : (ct.getMonAn() != null ? ct.getMonAn().getDonGia() : 0);
                    tongTienMonAn += donGia * ct.getSoLuong();
                }
            }

            // Lấy thông tin Thuế và KM đầy đủ
            HoaDon_Thue_DAO thueDAO = new HoaDon_Thue_DAO();
            HoaDon_KhuyenMai_DAO kmDAO = new HoaDon_KhuyenMai_DAO();

            Thue thue = (hoaDonHienTai.getThue() != null && hoaDonHienTai.getThue().getMaThue() != null)
                ? thueDAO.getThueById(hoaDonHienTai.getThue().getMaThue()) : null;
            KhuyenMai km = (hoaDonHienTai.getKhuyenMai() != null && hoaDonHienTai.getKhuyenMai().getMaKM() != null)
                ? kmDAO.getKhuyenMaiById(hoaDonHienTai.getKhuyenMai().getMaKM()) : null;

            double tienGiam = 0, tienThue = 0, tongTienSauGiam = tongTienMonAn;

            // Tính tiền giảm
            if (km != null && !"KM00000000".equals(km.getMaKM().trim()) && km.getGiaTri() > 0) {
                if (km.getGiaTri() < 1.0) tienGiam = tongTienMonAn * km.getGiaTri();
                else tienGiam = km.getGiaTri();
                tongTienSauGiam -= tienGiam;
                if (tongTienSauGiam < 0) tongTienSauGiam = 0;
            }

            if (thue != null && thue.getTyLeThue() > 0) {
                tienThue = tongTienSauGiam * thue.getTyLeThue();
            }

            double tongThanhToan = tongTienSauGiam + tienThue;

            HoaDon_ThanhToan_Dialog thanhToanDialog = new HoaDon_ThanhToan_Dialog(
                mainFrame, hoaDonHienTai, daoHoaDon,
                tongTienMonAn, tienGiam, tienThue, tongThanhToan,
                chiTietList
            );
            thanhToanDialog.setVisible(true);

            HoaDon hoaDonSauKhiDongDialog = daoHoaDon.findByMaHD(hoaDonHienTai.getMaHoaDon());
            if (hoaDonSauKhiDongDialog != null && hoaDonSauKhiDongDialog.isDaThanhToan()) {
                System.out.println("Hóa đơn " + hoaDonHienTai.getMaHoaDon() + " đã được thanh toán.");
                if (onCloseCallback != null) {
                    onCloseCallback.run();
                }
            } else {
                System.out.println("Hóa đơn " + hoaDonHienTai.getMaHoaDon() + " chưa được thanh toán (dialog bị hủy).");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Đã xảy ra lỗi khi chuẩn bị thanh toán: " + e.getMessage(),
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}