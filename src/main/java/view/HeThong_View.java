package view;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HeThong_View extends JPanel {
    private Image backgroundImage;
    private JLabel clockLabel;
    private Timer timer;

    public HeThong_View() {
        backgroundImage = new ImageIcon("images/background.jpg").getImage();

        setLayout(new BorderLayout());
        setOpaque(false);
        JLabel label = new JLabel("Chào mừng đến với Hệ thống quản lý Nhà hàng JOJO!", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 26)); // chữ to hơn
        label.setForeground(Color.WHITE);
        label.setOpaque(false);

        // Đồng hồ
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Consolas", Font.BOLD, 36)); // 💥 Tăng kích thước đồng hồ
        clockLabel.setForeground(new Color(255, 230, 180)); // màu sáng ấm
        clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        clockLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 40));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(clockLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
        add(label, BorderLayout.CENTER);

        startClock();
    }

    private void startClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss | dd/MM/yyyy");
        timer = new Timer(1000, e -> clockLabel.setText(sdf.format(new Date())));
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();
    }
}
