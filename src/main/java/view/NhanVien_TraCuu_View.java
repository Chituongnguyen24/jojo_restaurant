package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

class PositionRenderer extends JLabel implements TableCellRenderer {
    public PositionRenderer() {
        setOpaque(true);
        setHorizontalAlignment(CENTER);
        setBorder(new EmptyBorder(5, 10, 5, 10));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        setText(value.toString());
        if ("Quản lý".equals(value)) {
            setBackground(new Color(255, 229, 236));
            setForeground(new Color(224, 49, 99)); 
        } else if ("Tiếp tân".equals(value)) {
            setBackground(new Color(229, 255, 239)); 
            setForeground(new Color(34, 153, 84)); 
        } else {
            setBackground(table.getBackground());
            setForeground(table.getForeground());
        }
        return this;
    }
}