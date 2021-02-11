public class J48Node {
    J48Node parent;
    J48Node children[];

    public J48Node(J48Node parent, J48Node children[]) {
        this.parent = parent;
        this.children = children;
    }
}
