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
     

        // Đồng hồ
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Consolas", Font.BOLD, 36)); 
        clockLabel.setForeground(new Color(255, 230, 180)); 
        clockLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        clockLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 40));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(clockLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

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
    }
}