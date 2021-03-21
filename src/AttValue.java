public class AttValue extends Attribute {
    String sValue;
    double nValue;

    public AttValue(boolean numeric, String sValue, double nValue) {
        super("");
        this.numeric = numeric;
        this.sValue = sValue;
        this.nValue = nValue;
    }

    public String getsValue() {
        return sValue;
    }

    public double getnValue() {
        return this.nValue;
    }

    public void setnValue(double nValue) {
        this.nValue = nValue;
    }
}
