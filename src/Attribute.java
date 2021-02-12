public class Attribute {
    boolean numeric;
    int numberOfValues;
    String values[];

    public Attribute(int numberOfValues, String values[]) {
        this.numeric = false;
        this.values = values;
        this.numberOfValues = values.length;
    }

    public Attribute() {
        this.numeric = true;
        this.numberOfValues = 1;
    }

    boolean isNumeric() {
        return numeric;
    }

    String[] getValues() {
        return this.values;
    }

    String getValue(int index) {
        return this.values[index];
    }


    int getNumberOfValues() {
        return this.numberOfValues;
    }
}
