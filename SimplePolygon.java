import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class SimplePolygon {
    ArrayList<Vertex> vertices;
    ArrayList<Edge> edges;
    boolean clockwise;
    int[][] coordsArray;
    Vertex top, bottom;
    public static Color[] colors = {new Color(170,170,170),new Color(200,100,100),new Color(100,200,100),new Color(100,100,200)};

    /**
     * Constructs a SimplePolygon from an array of vertices
     * @param vertices the vertices of the polygon
     */
    public SimplePolygon(Vertex[] vertices)
    {
        this.vertices = new ArrayList<>();
        this.edges = new ArrayList<>();
        Collections.addAll(this.vertices, vertices);
        if (vertices.length==0)
        {
            return;
        }
        top = bottom = vertices[0];
        for (int i = 0; i < this.vertices.size(); i++)
        {
            if (vertices[i].getY()>top.getY())
                top = vertices[i];
            if (vertices[i].getY()<bottom.getY())
                bottom = vertices[i];
            edges.add(Edge.polygonalEdge(this.vertices.get(i),this.vertices.get((i+1)%this.vertices.size())));
        }
        Vertex lowest = this.vertices.get(0);
        for (Vertex vertex:this.vertices)
        {
            if (vertex.getY()<lowest.getY()||(vertex.getY()==lowest.getY()&&vertex.getX()<lowest.getX()))
                lowest = vertex;
            vertex.setColor(colors[0]);
        }
        clockwise = left(lowest.getCoordsArr(),lowest.getPrev().getCoordsArr(),lowest.getNext().getCoordsArr());
    }
    /**
     * Constructs a SimplePolygon from a list of vertices
     * @param vertices the vertices of the polygon
     */
    public SimplePolygon(ArrayList<Vertex> vertices)
    {
        this.vertices = new ArrayList<>(vertices);
        this.edges = new ArrayList<>();
        for (int i = 0; i < this.vertices.size(); i++)
        {
            edges.add(Edge.polygonalEdge(this.vertices.get(i),this.vertices.get((i+1)%this.vertices.size())));
        }
        if (vertices.size()==0)
            return;
        top = bottom = this.vertices.get(0);
        for (Vertex vertex:this.vertices)
        {
            if (vertex.getY()<bottom.getY())
                bottom = vertex;
            if (vertex.getY()>top.getY())
                top = vertex;
            vertex.setColor(colors[0]);
        }
        clockwise = left(bottom.getCoordsArr(),bottom.getPrev().getCoordsArr(),bottom.getNext().getCoordsArr());
        if (clockwise)
        {
            System.out.println("Reverse Reverse" + this);
            Collections.reverse(this.vertices);
            Collections.reverse(this.edges);
            for (Edge edge : this.edges)
            {
                edge.invert();
            }
            for (Vertex vertex: this.vertices)
                vertex.invert();
        }
        clockwise = false;
    }


    /**
     * Draws the polygon
     * @param g the graphics object being used to draw
     */
    public void paint(Graphics g)
    {
        g.setColor(Color.BLACK);
        g.drawPolygon(getCoordsArray()[0],getCoordsArray()[1],vertices.size());
    }

    /**
     * Generates a coordinate array of the vertices compliant with g.drawPolygon()'s specifications
     * @return coordinate array
     */
    public int[][] getCoordsArray() {
        if (coordsArray != null&&coordsArray.length== vertices.size())
            return coordsArray;
        coordsArray = new int[2][vertices.size()];
        int i = 0;
        for (Vertex vertex : vertices)
        {
            coordsArray[0][i] = vertex.getX();
            coordsArray[1][i++] = vertex.getY();
        }
        return coordsArray;
    }


    /**
     * Determines if two line segments intersect
     * @param a first endpoint of first line segment
     * @param b second endpoint of first line segment
     * @param c first endpoint of second line segment
     * @param d first endpoint of second line segment
     * @return true if they intersect, false otherwise
     */
    public static boolean intersectsProp(int[] a, int[] b, int[] c, int[] d)
    {
        if (collinear(a,b,c) && collinear(a,b,d))
        {
            return (between(a,b,c)||between(a,b,d));
        }
        if ((between(a,b,c)||between(a,b,d)||between(c,d,a)||between(c,d,b)))
        {
            return true;
        }
        if (collinear(a,b,c) || collinear(a,b,d) || collinear (c, d, a) || collinear(c,d,b))
        {
            return false;
        }
        return (left(a,b,c) != left(a,b,d)) && (left(c,d,a) != left(c,d,b));
    }
    /**
     * Determines if a point is between two other points (all collinear)
     * @param a coordinates of a point
     * @param b coordinates of another point
     * @param c coordinates of the point between two other points
     * @return true if c is between a and b, false elsewise.
     */
    public static boolean between(int[]a, int[] b, int[] c)
    {
        if (!collinear(a,b,c))
            return false;
        if (a[0] != b[0])
            return ((a[0] <= c[0]) && (c[0] <=b[0])) ||
                    ((a[0] >= c[0]) && (c[0] >= b[0]));
        return ((a[1] <= c[1]) && (c[1] <= b[1])) ||
                ((a[1] >= c[1]) && (c[1] >= b[1]));
    }
    /**
     * Determines if a point is to the left of two other points
     * @param a coordinates of a point
     * @param b coordinates of another point
     * @param c coordinates of the point that may or may not be to the left
     * @return true if c is to the left of a and b, false elsewise.
     */
    public static boolean left(int[]a, int[] b, int[] c) {
        return crossProduct(a,b,a,c) > 0;
    }

    /**
     * Determines if a point is to the collinear with two other points
     * @param a coordinates of a point
     * @param b coordinates of another point
     * @param c coordinates of the point that may or may not be collinear.
     * @return true if c is to the left of a and b, false elsewise.
     */
    public static boolean collinear(int[]a, int[] b, int[] c) {
        return crossProduct(a,b,a,c) == 0;
    }
    /**
     * Determines signed area of the parallelogram with points a,b,c,d
     * @param a coordinates of point a
     * @param b coordinates of point b
     * @param c coordinates of point c
     * @param d coordinates of point d
     * @return signed area of the parallelogram with points a,b,c,d
     */
    public static int crossProduct(int[] a, int[] b, int[] c, int[]d)
    {
        return (b[0] - a[0]) * (d[1] - c[1]) - (b[1] - a[1]) * (d[0] - c[0]);
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("SimplePolygon{" +
                "vertices=");
        for (Vertex vertex: vertices)
        {
            str.append(Main.gpanel.vertices.indexOf(vertex)+1).append(",");
        }
        return str+"}";
    }

    public ArrayList<SimplePolygon> triangulate()
    {
        ArrayList<SimplePolygon> faces = new ArrayList<>();
        int i = vertices.indexOf(top)+1;
        while (vertices.size()>3&&i++<200)
        {
            SimplePolygon triangle = clipConvex(i% vertices.size());
            if (triangle != null)
            {
                faces.add(triangle);
            }
        }
        return faces;
    }

    public SimplePolygon clipConvex(int i)
    {
        Vertex vertex = vertices.get(i);
        Vertex vPrev = vertices.get((i-1+ vertices.size())% vertices.size());
        Vertex vNext = vertices.get((i+1)% vertices.size());
        if (vertex == top||vertex == bottom||left(vPrev.getCoordsArr(),vNext.getCoordsArr(),vertex.getCoordsArr()))
            return null;
        Vertex[] triangle = {vPrev,vertex,vNext};
        int index = vertices.indexOf(vPrev);
        SimplePolygon face = new SimplePolygon(triangle);
        Edge prev = edges.get(index);
        Edge next = edges.get((index+1)%vertices.size());
        edges.remove(prev);
        edges.remove(next);
        vertices.remove(vertex);
        Edge newEdge = Edge.polygonalEdge(vPrev,vNext);
        if (index > edges.size())
            edges.add(newEdge);
        else
            edges.add(index,newEdge);
        return face;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimplePolygon polygon = (SimplePolygon) o;
        if (polygon.vertices.size()!=this.vertices.size())
            return false;
        for (Vertex vertex: polygon.vertices)
        {
            if (!vertices.contains(vertex))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices);
    }
}