public class Sample {
    AttValue values[];
    int numOfValues;

    public Sample(AttValue values[]) {
        this.values = values;
        this.numOfValues = values.length;
    }

    public AttValue getAttValue(int index) {
        return this.values[index];
    }

    public String toString() {
        String s = "";
        for(int i = 0; i < this.numOfValues; i++) {
            AttValue attv = this.values[i];
            s += " " + attv.getName() + ": ";
            if(attv.isNumeric()) {
                s+= attv.getnValue();
            } else {
                s+= attv.getsValue();
            }
            s+= " |";
        }
        return s;
    }

}
