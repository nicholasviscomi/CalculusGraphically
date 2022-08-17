package Graphing;

public class Node {
    public Node next;
    public double x, y;

    public Node(Node next, double x, double y) {
        this.next = next;
        this.x = x;
        this.y = y;
    }
}
