import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Shows the left side of the screen with the polygon
 */
public class GraphPanel extends JPanel implements MouseListener, KeyListener, MouseMotionListener {
    ArrayList<Vertex> vertices;
    ArrayList<Edge> edges;
    static Vertex ghostStart,ghostEnd;
    Vertex[] corners;
    ArrayList<Edge> sweepLineStatus;
    ArrayList<Edge> trapezoidalization;
    ArrayList<SimplePolygon> faces;
    int sweepEvent;
    ArrayList<String> tableRows;
    public GraphPanel()
    {
        setBackground(Color.white);
        addMouseListener(this);
        addMouseMotionListener(this);
        setFocusable(true);
        addKeyListener(this);
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        ghostStart = new Vertex(-5000,-5000);
        ghostEnd = new Vertex(5000,-5000);
        faces = new ArrayList<>();
        repaint();
    }

    /**
     * Sets up the points for the Sweep Algorithm to be applied to them
     */
    public void setUpSweep()
    {
        sweepLineStatus = new ArrayList<>();
        corners = new Vertex[] {
                new Vertex(0,0),
                new Vertex(this.getWidth(),0),
                new Vertex(0,this.getHeight()),
                new Vertex(this.getWidth(),this.getHeight())
        };
        vertices.sort(Comparator.comparingInt(Vertex::getY));
        trapezoidalization = new ArrayList<>();
        tableRows = new ArrayList<>();
        tableRows.add(String.format("%-20s%-25s%-20s","Event","SLS","Pointers"));
        sweepEvent = 0;
        faces = new ArrayList<>();
    }

    /**
     * Process the next event in the Sweep Algorithm
     */
    public void sweepNext()
    {
        if (sweepLineStatus == null||sweepEvent>=vertices.size())
            setUpSweep();
        Vertex event = vertices.get(sweepEvent++);
        Vertex lray = new Vertex(event.getX()-1,event.getY());
        Vertex rray = new Vertex(event.getX()+1,event.getY());
        int rIndex = -1;
        int lIndex = -1;
        Vertex lpointer = new Vertex(0,event.getY());
        Vertex rpointer = new Vertex(corners[1].getX(),event.getY());
        for (Edge edge : sweepLineStatus)
        {
            Vertex lcandidate = edge.linearIntersection(event,lray);
            if (lcandidate!=null&&lcandidate.getX()> lpointer.getX())
            {
                lpointer = lcandidate;
                lIndex = edges.indexOf(edge);
            }
            Vertex rcandidate = edge.linearIntersection(event,rray);
            if (rcandidate!=null&&rcandidate.getX()<rpointer.getX())
            {
                rpointer = rcandidate;
                rIndex = edges.indexOf(edge);
            }
        }
        for (Edge edge : event.getEdges())
        {
            if (edge.getCenter()[1]<event.getY())
                sweepLineStatus.remove(edge);
            else
                sweepLineStatus.add(edge);
        }
        if (sweepEvent==vertices.size())
        {
            edges.add(new Edge(corners[2],event));
            edges.add(new Edge(corners[3],event));
            edges.add(new Edge(corners[0],corners[1]));
            edges.add(new Edge(corners[1],corners[3]));
            edges.add(new Edge(corners[2],corners[0]));
            edges.add(new Edge(corners[3],corners[2]));
        }
        if (sweepEvent==1)
        {
            edges.add(new Edge(corners[0],event));
            edges.add(new Edge(corners[1],event));
        }
        for (int i = 0; i < sweepEvent-1; i++)
        {
            Vertex edgeUp = vertices.get(i);
            boolean clear = true;
            for (Edge edge: trapezoidalization)
            {
                if (edge.getEnd().getY() <= edgeUp.getY())
                    continue;
                if (SimplePolygon.intersectsProp(event.getCoordsArr(),edgeUp.getCoordsArr(),edge.getStart().getCoordsArr(),edge.getEnd().getCoordsArr()))
                {
                    clear = false;
                    break;
                }
            }
            if (!clear)
                continue;
            Edge newEdge = new Edge(event,edgeUp);
            for (Edge edge: edges)
            {
                if (edge.equals(newEdge))
                {
                    clear = false;
                    break;
                }
                if (!edge.contains(event)&&!edge.contains(edgeUp)&&SimplePolygon.intersectsProp(edgeUp.getCoordsArr(),event.getCoordsArr(),edge.getStart().getCoordsArr(),edge.getEnd().getCoordsArr()))
                {
                    newEdge.getEnd().removeEdge(newEdge);
                    newEdge.getStart().removeEdge(newEdge);
                    clear = false;
                    break;
                }
            }
            if (clear)
                edges.add(newEdge);
        }
        trapezoidalization.add(new Edge(lpointer,rpointer,true));
        String str = "L=" + ((lIndex==-1)?"left":lIndex);
        str +=",R=" + ((rIndex==-1)?"right":rIndex);
        tableRows.add(String.format("%-20s%-25s%-20s",sweepEvent,sweepLineStatus,str));
    }

    /**
     * Constructs a monotone mountain from a pair of consecutive edges in it.
     * @param edge1 an edge of the PSLG
     * @param edge2 another edge of the PSLG
     * @param common the common vertex of the pair
     * @return a list of vertices from which a simple polygon can be constructed
     */
    public ArrayList<Vertex> constructPolygon(Edge edge1, Edge edge2, Vertex common)
    {
        ArrayList<Vertex> face = new ArrayList<>();
        Vertex start = edge1.getOther(common);
        Vertex end = edge2.getOther(common);
        if (SimplePolygon.left(common.getCoordsArr(), end.getCoordsArr(), start.getCoordsArr()))
        {
            Vertex temp = start;
            start = end;
            end = temp;
            edge1 = edge2;
        }
        face.add(end);
        face.add(common);
        int i = 0;
        while (start != end&&i++<vertices.size())
        {
            face.add(start);
            edge1 = start.getEdges().get((start.getEdges().indexOf(edge1)+1)%start.getEdges().size());
            start = edge1.getOther(start);
        }
        if (i >= vertices.size()-2)
            return new ArrayList<>();
        return face;
    }

    /**
     * Resets the PSLG to what the user inputted
     */
    public void reset()
    {
        ArrayList<Edge> leftover = new ArrayList<>();
        int index = 0;
        for (;index<edges.size();index++)
        {
            if (edges.get(index).contains(corners[0]))
                break;
            leftover.add(edges.get(index));
        }
        edges = leftover;
        faces = new ArrayList<>();
        for (int j = 0; j < 4; j++) {
            vertices.remove(corners[j]);
        }
        for (Vertex vertex : vertices)
        {
            vertex.clear(edges);
        }
    }

    /**
     * Identifies and triangulates the monotone mountains in the PSLG
     */
    public void triangulate()
    {
        vertices.add(corners[0]);
        vertices.add(corners[1]);
        vertices.add(corners[2]);
        vertices.add(corners[3]);
        for (Vertex vertex : vertices)
        {
            vertex.getEdges().sort((o1, o2) -> {
                if ((o1.getCenter()[1]-vertex.getY())*(o2.getCenter()[1]-vertex.getY())<0)
                    return o1.getCenter()[1]-o2.getCenter()[1];
                int crossproduct= SimplePolygon.crossProduct(vertex.getCoordsArr(),o1.getCenter(),vertex.getCoordsArr(),o2.getCenter());
                return Integer.compare(crossproduct, 0);
            });
        }
        for (Vertex vertex : vertices) {
            ArrayList<Edge> edges = vertex.getEdges();
            for (int j = 0; j < edges.size(); j++) {
                Edge edge1 = edges.get(j);
                Edge edge2 = edges.get((j + 1) % edges.size());
                if (edge1.getCenter()[1] > vertex.getY() && edge2.getCenter()[1] > vertex.getY()) {
                    ArrayList<Vertex> vertices = constructPolygon(edge1, edge2, vertex);
                    if (vertices.size() <= 3)
                        continue;
                    SimplePolygon newFace = new SimplePolygon(vertices);
                    faces.add(newFace);
                }
            }
        }
        int faceCount = faces.size();
        for (int i = 0; i < faceCount;i++)
        {
            faces.addAll(faces.get(i).triangulate());
        }
    }

    /**
     * Paints the left side of the screen
     * @param g Graphics object used by JPanel
     */
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        int i = 1;
        for (Vertex vertex: vertices)
        {
            vertex.paint(g,i++);
        }
        i = 1;
        for (Edge edge : edges)
        {
            edge.paint(g,i++);
        }
        for (SimplePolygon face : faces)
        {
            face.paint(g);
        }
        if (Main.phase == Main.PhaseType.DRAW)
        {
            g.setColor(new Color(100,100,200));
            g.drawLine(ghostStart.getX(),ghostStart.getY(),ghostEnd.getX(),ghostEnd.getY());
        }
        if (Main.phase == Main.PhaseType.SWEEP)
        {
            for (Edge edge : trapezoidalization)
            {
                edge.paint(g);
            }
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }
    /**
     * Determines what to do when mouse is pressed
     * @param e KeyEvent containing info on which mouse button was pressed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (Main.phase == Main.PhaseType.DRAW) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                vertices.add(new Vertex(e.getX(), e.getY()));
            }
            if (e.getButton() == MouseEvent.BUTTON3&&vertices.size()>0) {
                Vertex closest = vertices.get(0);
                double dist = closest.distance(e.getX(),e.getY());
                for (int i = 1; i < vertices.size(); i++) {
                    Vertex vertex = vertices.get(i);
                    if (vertex.distance(e.getX(),e.getY()) < dist) {
                        dist = vertex.distance(e.getX(),e.getY()) ;
                        closest = vertex;
                    }
                }
                vertices.remove(closest);
                for (int i =0; i < edges.size();i++)
                {
                    if (edges.get(i).contains(closest)) {
                        Edge edge = edges.get(i);
                        edge.getStart().removeEdge(edge);
                        edge.getEnd().removeEdge(edge);
                        edges.remove(i);
                        i--;
                    }
                }
            }
            if (e.getButton() == MouseEvent.BUTTON2&&vertices.size()>1)
            {
                Edge newEdge = new Edge(ghostStart,ghostEnd);
                int i = 0;
                for (; i < edges.size(); i++)
                {
                    if (edges.get(i).equals(newEdge))
                    {
                        edges.remove(i);
                        i--;
                        break;
                    }
                    if (edges.get(i).contains(ghostStart)||edges.get(i).contains(ghostEnd))
                    {
                        continue;
                    }
                    if (SimplePolygon.intersectsProp(ghostStart.getCoordsArr(),ghostEnd.getCoordsArr(),edges.get(i).getStart().getCoordsArr(),edges.get(i).getEnd().getCoordsArr()))
                    {
                        i--;
                        newEdge.getStart().removeEdge(newEdge);
                        newEdge.getEnd().removeEdge(newEdge);
                        break;
                    }
                }
                if (i == edges.size()) {
                    edges.add(newEdge);
                }
            }
        }
        repaint();
        Main.algebraPanel.repaint();
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

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (Main.phase== Main.PhaseType.DRAW&&vertices.size()>1)
        {
            for (Vertex vertex: vertices)
            {
                if (vertex.distance(e.getX(),e.getY())<ghostStart.distance(e.getX(),e.getY()))
                {
                    ghostStart = vertex;
                }
            }
            int[] mouseCoords = {e.getX(),e.getY()};
            if (ghostEnd.equals(ghostStart))
            {
                ghostEnd = new Vertex(ghostStart.getX()+5000,ghostStart.getY());
            }
            for (Vertex vertex: vertices)
            {
                if (vertex != ghostStart&&
                        Math.abs(SimplePolygon.crossProduct(vertex.getCoordsArr(),ghostStart.getCoordsArr(),mouseCoords, ghostStart.getCoordsArr())
                        )<= Math.abs(SimplePolygon.crossProduct(ghostEnd.getCoordsArr(),ghostStart.getCoordsArr(),mouseCoords, ghostStart.getCoordsArr())))
                {
                    ghostEnd = vertex;
                }
            }
            repaint();
        }
    }
}