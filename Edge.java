import java.awt.*;
import java.util.Objects;

public class Edge {
    private Vertex start;
    private Vertex end;
    private final int[] center;

    /**
     * Constructs an edge from two vertices
     * @param start the first vertex
     * @param end the second vertex
     */
    public Edge(Vertex start, Vertex end)
    {
        this.start = start;
        this.end=end;
        start.addEdge(this);
        end.addEdge(this);
        center = new int[]{(start.getX()+ end.getX())/2,(start.getY()+ end.getY())/2};
    }
    /**
     * Constructs an edge from two vertices, but without adding itself to their edgelists
     * @param start the first vertex
     * @param end the second vertex
     * @param trapezoidal just distingushes the method signature
     */
    public Edge(Vertex start, Vertex end,boolean trapezoidal)
    {
        this.start = start;
        this.end=end;
        center = new int[]{(start.getX()+ end.getX())/2,(start.getY()+ end.getY())/2};
    }

    /**
     * Constructs an Edge for use in a Simple polygon, basically sets them as next to each other
     * @param start start vertex
     * @param end end vertex
     * @return the edge from start to end
     */
    public static Edge polygonalEdge(Vertex start, Vertex end)
    {
        Edge edge = new Edge(start,end,false);
        start.setNext(end);
        end.setPrev(start);
        return edge;
    }

    /**
     * Checks if an edge has a vertex as its start or end
     * @param vertex the vertex in question
     * @return true if an edge has a vertex as its start or end, false otherwise
     */
    public boolean contains(Vertex vertex)
    {
        return start.equals(vertex) || end.equals(vertex);
    }

    public Vertex getEnd() {
        return end;
    }

    public Vertex getStart() {
        return start;
    }

    public Vertex getOther(Vertex vertex)
    {
        if (start == vertex)
            return end;
        return start;
    }

    public int[] getCenter() {
        return center;
    }

    /**
     * Swaps the start and end vertex
     */
    public void invert()
    {
        Vertex temp = start;
        start = end;
        end = temp;
    }
    /**
     * Finds an intersection between a ray from point1 to point2 and the edge
     * @param point1 the origin of the ray
     * @param point2 point determining ray direction
     * @return the point of intersection
     */
    public Vertex linearIntersection(Vertex point1, Vertex point2)
    {
        if (point1==null||point2==null)
            return null;
        if (contains(point2)) {
            return point2;
        }
        if (contains(point1)) {
            return null;
        }
        if (SimplePolygon.left(point1.getCoordsArr(),point2.getCoordsArr(),start.getCoordsArr()) == SimplePolygon.left(point1.getCoordsArr(),point2.getCoordsArr(),end.getCoordsArr()))
            return null;
        double[] diff = subtract(point1.getCoordsDoubleArr(),point2.getCoordsDoubleArr());
        double[][] matrix = {{diff[0],start.getX()-end.getX()},{diff[1],start.getY()-end.getY()}};
        double[][] start = {{-point2.getX()+ end.getX()},{-point2.getY()+ end.getY()}};
        double[][] intersectMult = matrixMultiply(inverse(matrix),start);
        if (intersectMult[0][0]>0)
            return null;
        double[] intersect = add(point2.getCoordsDoubleArr(), scalarMult(intersectMult[0][0],diff));
        int[] temp = {(int)intersect[0],(int)intersect[1]};
        return new Vertex(temp[0],temp[1]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return (start.equals(edge.start) &&
                end.equals(edge.end))||(start.equals(edge.end) &&
                end.equals(edge.start));
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return ""+(Main.gpanel.edges.indexOf(this)+1);
    }
    /**
     * Adds two 2-d vectors together
     * @param x a vector (double array)
     * @param y a vector (double array)
     * @return the sum of the vectors as a double array
     */
    public static double[] add(double[] x, double[]y)
    {
        return new double[]{x[0]+y[0],x[1]+y[1]};
    }

    /**
     * Subtracts two 2-d vectors
     * @param x a vector (double array)
     * @param y a vector (double array)
     * @return the difference of the vectors as a double array
     */
    public static double[] subtract(double[] x, double[]y)
    {
        return new double[]{x[0]-y[0],x[1]-y[1]};
    }

    /**
     * Multiplies a 2-d vector by a scalar
     * @param r the scalar
     * @param m the vector
     * @return the scaled vector
     */
    public static double[] scalarMult(double r, double[] m)
    {
        return new double[]{r*m[0],r*m[1]};
    }

    /**
     * Finds the inverse matrix of the given matrix
     * @param m a matrix, given as a 2-dimensional array
     * @return the inverse matrix
     */
    public static double[][] inverse(double[][] m)
    {
        if (m.length != m[0].length)
            return new double[][]{};
        double[][] ans = new double[m.length][m.length];
        double det = determinant(m);
        if (det == 0)
            return new double[][]{};
        if (m.length==2)
        {
            return new double[][]{{m[1][1]/det,-m[0][1]/det},{-m[1][0]/det,m[0][0]/det}};
        }
        for (int i = 0;i < m.length;i++)
        {
            for (int j = 0; j<m.length;j++)
            {
                ans[j][i] = determinant(minorMatrix(m,i,j))*(((i+j)%2==0)?1:-1)/det;
            }
        }
        return ans;
    }

    /**
     * Finds the specified minor matrix of the given matrix
     * @param m the matrix
     * @param row the row to exclude
     * @param col the column to exclude
     * @return the minor matrix
     */
    public static double[][] minorMatrix(double[][] m, int row, int col)
    {
        double[][] ans = new double[m.length-1][m[0].length-1];
        for (int i = 0; i < m.length; i++)
        {
            if (i == row)
                continue;
            for (int j = 0; j < m[0].length; j++)
            {
                if ( j == col)
                    continue;
                ans[(i>row)?i-1:i][(j>col)?j-1:j] = m[i][j];
            }
        }
        return ans;
    }

    /**
     * Finds the determinant of the given matrix
     * @param m the matrix (2-d array of doubles)
     * @return the determinant (as a double)
     */

    public static double determinant(double[][] m)
    {
        if (m.length!=m[0].length)
        {
            return 0;
        }
        if (m.length == 2)
        {
            return m[0][0]*m[1][1]-m[1][0]*m[0][1];
        }
        double determ = 0;
        for (int i = 0; i < m[0].length;i++)
        {
            determ += m[0][i]*determinant(minorMatrix(m,0,i))*((i%2==0)?1:-1);
        }
        return determ;
    }

    /**
     * Multiplies two matrices
     * @param m1 a matrix (2-d integer array)
     * @param m2 another matrix (2-d integer array)
     * @return the matrix found by multiplying m1 and m2
     */
    public static double[][] matrixMultiply(double[][] m1, double[][] m2)
    {
        if (m1[0].length!=m2.length)
            return new double[][]{};
        double[][] ans= new double[m1.length][m2[0].length];
        for (int i = 0; i < m1.length; i++)
        {
            for (int j = 0; j < m2[0].length;j++)
            {
                ans[i][j] = 0;
                for (int k = 0; k < m2.length; k++)
                {
                    ans[i][j] += m1[i][k]*m2[k][j];
                }
            }
        }
        return ans;
    }

    public void paint(Graphics g, int i) {
        g.setColor(Color.BLACK);
        g.drawLine(start.getX(),start.getY(),end.getX(),end.getY());
        g.drawString(""+i,(start.getX()+end.getX())/2+5,(start.getY()+end.getY())/2+5);
    }
    public void paint(Graphics g)
    {
        g.setColor(new Color(200,150,200));
        g.drawLine(start.getX(),start.getY(),end.getX(),end.getY());
    }
}
