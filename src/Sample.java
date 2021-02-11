public class Sample {
    AttValue values[];
    int numOfValues;

    public Sample(AttValue values[]) {
        this.values = values;
    }

    public AttValue getAttValue(int index) {
        return this.values[index];
    }


}
