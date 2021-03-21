public class J48Node {
    J48Node parent;
    J48Node children[];
    int childrenSize;
    Sample samples[];
    int attr;
    double treshold;
    int sampleSize;

    public J48Node(J48Node parent, int childrenSize, Sample samples[], int decisionAttr, double treshold) {
        this.parent = parent;
        this.children = new J48Node[childrenSize];
        this.childrenSize = childrenSize;
        this.samples = samples;
        this.attr = decisionAttr;
        this.treshold = treshold;
        this.sampleSize = samples.length;
    }

    public void setChild(int index, J48Node child) {
        this.children[index] = child;
    }

    public double getTreshold() {
        return treshold;
    }

    public Sample[] getSamples() {
        return samples;
    }

    public Sample getSample(int index) {
        return samples[index];
    }

    public int getSampleSize() {
        return sampleSize;
    }


}
