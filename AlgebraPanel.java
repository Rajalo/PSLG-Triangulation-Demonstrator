import javax.swing.*;
import java.awt.*;

/**
 * Shows information about the process of the algorithm
 */
public class AlgebraPanel extends JPanel{
    public AlgebraPanel()
    {
        setBackground(new Color(250,210,250));
    }

    /**
     * Paints the info about the next clip on the right side of the screen
     * @param g Graphics object used by JPanel
     */
    public void paint(Graphics g)
    {
        super.paintComponent(g);
        if (Main.phase== Main.PhaseType.DRAW)
            return;
        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.2F);
        g.setFont(newFont);
        if (Main.gpanel.tableRows!=null) {
            int y = 20;
            for (String str : Main.gpanel.tableRows) {
                g.drawString(str, 30, y);
                y += 30;
            }
        }
        g.setFont(currentFont);
    }
}
