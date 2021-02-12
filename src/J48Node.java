public class J48Node {
    J48Node parent;
    J48Node children[];
    int childrenSize;
    Sample samples[];
    int attr;

    public J48Node(J48Node parent, int childrenSize, Sample samples[], int decisionAttr, double treshold) {
        this.parent = parent;
        this.children = new J48Node[childrenSize];
        this.childrenSize = childrenSize;
        this.samples = samples;
        this.attr = decisionAttr;
    }

    public void setChild(int index, J48Node child) {
        this.children[index] = child;
    }
}
