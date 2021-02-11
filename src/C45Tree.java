import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class C45Tree {
    Sample samples[];
    Attribute attrs[];
    int numberOfSamples;
    Scanner scan;
    int numberOfAttributes;
    public C45Tree(String dataFile, Attribute attrs[], int numberOfSamples) {
        this.attrs = attrs;
        try {
            this.scan = new Scanner(new File(dataFile));
            this.scan.useDelimiter(",");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.numberOfAttributes = attrs.length;
        this.numberOfSamples = numberOfSamples;
        this.samples = new Sample[numberOfSamples];
    }

    public void start() {
        AttValue attVals[] = new AttValue[numberOfAttributes];
        int indexOfVal = 0;
        int indexOfSample = 0;
        while (this.scan.hasNext()) {
            if(indexOfVal < numberOfAttributes) {
                if(attrs[indexOfVal].isNumeric()) {
                    attVals[indexOfVal] = new AttValue(true,"", Double.parseDouble(scan.next()));// scan.nextDouble()
                } else {
                    attVals[indexOfVal] = new AttValue(false, scan.next(), 0);
                }
                indexOfVal++;
            } else {
                samples[indexOfSample] = new Sample(attVals);
                attVals = new AttValue[this.numberOfAttributes];
                indexOfSample++;
                indexOfVal = 0;
            }
        }
    }
}
