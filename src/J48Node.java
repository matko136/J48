public class J48Node {
    J48Node parent;
    J48Node children[];
    int childrenSize;
    Sample samples[];
    int attr;
    double treshold;
    int sampleSize;
    int classIndex;
    int misClassifiedSamps;
    double errorRate;

    public J48Node(J48Node parent, int childrenSize, Sample samples[], int decisionAttr, double treshold) {
        this.parent = parent;
        this.children = new J48Node[childrenSize];
        this.childrenSize = childrenSize;
        this.samples = samples;
        this.attr = decisionAttr;
        this.treshold = treshold;
        this.sampleSize = samples.length;
    }

    public void setChildrenSize(int size) {
        this.childrenSize = size;
    }

    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }

    public void setMisClassifiedSamps(int misClassifiedSamps) {
        this.misClassifiedSamps = misClassifiedSamps;
    }

    public void setErrorRate(double errorRate) {
        this.errorRate = errorRate;
    }

    public int getClassIndex() {
        return this.classIndex;
    }

    public int getMisClassifiedSamps() {
        return this.misClassifiedSamps;
    }

    public double getErrorRate() {
        return this.errorRate;
    }

    public double getSubErrorRate() {
        if(childrenSize == 0) {
            return this.errorRate;
        } else {
            double errors = 0;
            for(int i = 0; i < childrenSize; i++) {
                errors += children[i].getSubErrorRate();
            }
            return errors;
        }
    }

    public void setChild(int index, J48Node child) {
        this.children[index] = child;
    }

    public J48Node getChild(int index) {
        return this.children[index];
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
