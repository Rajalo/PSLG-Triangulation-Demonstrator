import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * The bar at the bottom of the screen used to change phases
 */
public class InfoPanel extends JPanel implements MouseListener {
    public InfoPanel()
    {
        setBackground(Color.lightGray);
        addMouseListener(this);
        repaint();
    }

    /**
     * Paints the bar at the bottom of the screen
     * @param g Graphics object used by JPanel
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setFont(g.getFont().deriveFont(Font.BOLD));
        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
        g.setFont(newFont);
        String str = "Phase:";
        switch (Main.phase)
        {
            case DRAW:
                str += "Left Click to Add Points, Right to Remove Points, Middle to Toggle edge!";
                break;
            case SWEEP:
                str += "Now we are going to do the Sweep, Click CONTINUE for the next Event";
                break;
            case TRIANGULATE:
                str += "Now we've separated it into monotone mountains for triangulating";
                break;
            case FINAL:
                str += "The algorithm is complete!";
        }
        g.drawString(str, 30,55);
        g.setColor(new Color(100,100,200));
        g.fillRect(700,25,130,50);
        g.setColor(Color.WHITE);
        g.drawString("CONTINUE",710,55);
        g.setColor(new Color(200,70,70));
        g.fillRect(850,25,130,50);
        g.setColor(Color.WHITE);
        g.drawString("CLEAR",860,55);
        g.setFont(currentFont);
    }

    /**
     * Processes mouse being clicked in this region
     * @param e MouseEvent being analyzed
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getX()>700&&e.getX()<830&&e.getY()>25&&e.getY()<75)
            Main.phaseAdd();
        if (e.getX()>850&&e.getX()<980&&e.getY()>25&&e.getY()<75)
            Main.phaseClear();
            repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
