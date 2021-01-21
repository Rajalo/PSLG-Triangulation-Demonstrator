import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class Vertex {
    private final int x,y;
    private Vertex next,prev;
    private final ArrayList<Edge> edges;
    private Color color;

    /**
     * Constructs a vertex
     * @param x the x-coord of the vertex
     * @param y the y-coord of the vertex
     */
    public Vertex(int x,int y)
    {
        this.x = x;
        this.y = y;
        color = new Color(200,100,100);
        edges = new ArrayList<>();
    }

    /**
     * Makes an array with the coords of the vertex
     * @return integer array [x,y]
     */
    public int[] getCoordsArr()
    {
        return new int[] {x,y};
    }

    /**
     * Makes an array with the coords of the vertex as doubles
     * @return double array [x,y]
     */
    public double[] getCoordsDoubleArr()
    {
        return new double[] {x,y};
    }

    public void addEdge(Edge edge)
    {
        if (edges.contains(edge))
            return;
        edges.add(edge);
    }
    public void removeEdge(Edge edge)
    {
        edges.remove(edge);
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Vertex getNext() {
        return next;
    }
    public Vertex getPrev() {
        return prev;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setNext(Vertex next) {
        this.next = next;
    }

    public void setPrev(Vertex prev) {
        this.prev = prev;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    /**
     * Gets rid of any of the given edges from the edgeList
     * @param edgeArrayList a list of edges that may need to be removed
     */
    public void clear(ArrayList<Edge> edgeArrayList)
    {
        for (int i = 0; i < edges.size();i++)
        {
            Edge edge = edges.get(i);
            if (!edgeArrayList.contains(edge))
            {
                removeEdge(edge);
                i--;
            }
        }
    }

    /**
     * Paints the vertex in the GraphPanel
     * @param g the graphics object for the GraphPanel
     * @param i the index of the vertex
     */
    public void paint(Graphics g, int i)
    {
        g.setColor(color);
        g.fillOval(x-4,y-4,8,8);
        g.setColor(Color.BLACK);
        g.drawString(""+i,x+5,y+5);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return x == vertex.x &&
                y == vertex.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    /**
     * Finds distance from the given coordinates to the Vertex
     * @param x the x-coord
     * @param y the y-coord
     * @return the distance from the inputted coords to this instance of Vertex
     */
    public double distance(int x, int y)
    {
        return Math.hypot(this.x-x,this.y-y);
    }

    /**
     * Swaps the next and previous vertices
     */
    public void invert() {
        Vertex temp = next;
        next = prev;
        prev = temp;
    }
}
