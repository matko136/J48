import javax.swing.tree.TreeNode;
import java.io.*;
import java.util.Scanner;

public class C45Tree {
    Sample samples[];
    Attribute attrs[];
    int numberOfSamples;
    BufferedReader buffReader;
    Scanner scan;
    int numberOfAttributes;
    public C45Tree(String dataFile, Attribute attrs[], int numberOfSamples) {
        this.attrs = attrs;
        try {
            this.scan = new Scanner(new File(dataFile));
            this.scan.useDelimiter(",");
            this.buffReader = new BufferedReader(new FileReader(dataFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.numberOfAttributes = attrs.length;
        this.numberOfSamples = numberOfSamples;
        this.samples = new Sample[numberOfSamples];
    }

    public void start() throws IOException {
        AttValue attVals[] = new AttValue[numberOfAttributes];

        int indexOfSample = 0;
        this.buffReader.readLine();
        String line = "";
        String splitBy = ",";
        while ((line = this.buffReader.readLine()) != null) {
            int indexOfVal = 0;
            String[] attVal = line.split(splitBy);
            while(indexOfVal < numberOfAttributes) {
                if(attrs[indexOfVal].isNumeric()) {
                    attVals[indexOfVal] = new AttValue(true,"", Double.parseDouble(attVal[indexOfVal]));// scan.nextDouble()
                } else {
                    attVals[indexOfVal] = new AttValue(false, attVal[indexOfVal], 0);
                }
                indexOfVal++;
            }
            samples[indexOfSample++] = new Sample(attVals);
            attVals = new AttValue[numberOfAttributes];
        }

        this.composeTree();
        J48Node node = new J48Node(null, null);
    }

    private void composeTree() {
        this.findBranches(null, samples);
    }

    private void findBranches(J48Node parent, Sample[] samples) {
        //loga b = log10 b / log10 a -> log2(x) = Math.log(x) / Math.log(2)
        double bestGain = 0;
        int indexBestGain = 0;
        double dataSetEntropy = calcEntropy(samples, -1,0);
        for(int i = 0; i < numberOfAttributes-1; i++) {
            double entropy = 0;

            if(!attrs[i].isNumeric()) {
                entropy = calcEntropy(samples, i, 0);
            } else {
                int sorted[] = sort(samples, i);

            }

            if(dataSetEntropy - entropy >= bestGain) {
                bestGain = dataSetEntropy - entropy;
                indexBestGain = i;
            }
        }
    }

    private int[] sort(Sample[] samples, int sortedAttrInd) {
        int numOfSamp = samples.length;
        int indexes[] = new int[numOfSamp];
        for(int i = 0; i < numOfSamp; i++) {
            indexes[i] = i;
        }
        for(int i = 0; i < numOfSamp; i++) {
            for(int j = numOfSamp - 1; j > i; j--) {
                if(samples[j].getAttValue(sortedAttrInd).getnValue() < samples[j - 1].getAttValue(sortedAttrInd).getnValue()) {
                    double k = samples[j].getAttValue(sortedAttrInd).getnValue();
                    int in = indexes[j];
                    samples[j].getAttValue(sortedAttrInd).setnValue(samples[j - 1].getAttValue(sortedAttrInd).getnValue());
                    samples[j - 1].getAttValue(sortedAttrInd).setnValue(k);
                    indexes[j] = indexes[j-1];
                    indexes[j - 1] = in;
                }
            }
        }
        return indexes;
    }

    private double calcEntropy(Sample[] samples, int attr, int split) {
        double entropy = 0;
        int numOfSamp = samples.length;
        if(attr == -1) {
            int numberOfClassValues = this.attrs[this.numberOfAttributes-1].getNumberOfValues();
            int classValues[] = new int[numberOfClassValues];
            for(int i = 0; i < numberOfClassValues; i++) {
                classValues[i] = 0;
                //classValues[i] = this.attrs[this.numberOfAttributes-1].getValue(i);
            }
            for(int i = 0; i < numOfSamp; i++) {
                for(int j = 0; j < numberOfClassValues; j++) {
                    //String att = samples[i].getAttValue(this.numberOfAttributes-1).getsValue();
                    //String attName = this.attrs[this.numberOfAttributes-1].getValue(j);
                    if(samples[i].getAttValue(this.numberOfAttributes-1).getsValue().equals(this.attrs[this.numberOfAttributes-1].getValue(j))) {
                        classValues[j]++;
                        break;
                    }
                }
                //if(samples[i].getAttValue(this.numberOfAttributes-1).getsValue() == this.attrs[this.numberOfAttributes-1].getValue());
            }
            for(int i = 0; i < numberOfClassValues; i++) {
                double valueProb = (double)classValues[i]/numOfSamp;
                entropy += valueProb*(Math.log(valueProb)/Math.log(2));
            }
            entropy *= -1;
        } else {
            if(!attrs[attr].isNumeric()) {
                int numberOfAttrClassValues = this.attrs[attr].getNumberOfValues();
                int attrClassValues[] = new int[numberOfAttrClassValues];

                int numberOfClassValues = this.attrs[this.numberOfAttributes-1].getNumberOfValues();
                int classValues[][] = new int[numberOfAttrClassValues][numberOfClassValues];

                for(int i = 0; i < numberOfAttrClassValues; i++) {
                    attrClassValues[i] = 0;
                }

                for(int i = 0; i < numOfSamp; i++) {
                    for(int j = 0; j < numberOfAttrClassValues; j++) {
                        if(samples[i].getAttValue(attr).getsValue().equals(this.attrs[attr].getValue(j))) {
                            for(int k = 0; k < numberOfClassValues; k++) {
                                //String att = samples[i].getAttValue(this.numberOfAttributes-1).getsValue();
                                //String attName = this.attrs[this.numberOfAttributes-1].getValue(k);
                                if(samples[i].getAttValue(this.numberOfAttributes-1).getsValue().equals(this.attrs[this.numberOfAttributes-1].getValue(k))) {
                                    classValues[j][k]++;
                                    break;
                                }
                            }
                            attrClassValues[j]++;
                            break;
                        }
                    }
                }
                for(int i = 0; i < numberOfAttrClassValues; i++) {
                    for(int j = 0; j < numberOfClassValues; j++) {
                        double valueProb = (double)classValues[i][j]/attrClassValues[i];
                        entropy += valueProb*(Math.log(valueProb)/Math.log(2))*(-1)*((double)attrClassValues[i]/numOfSamp);
                    }
                }
            } else {

            }
        }
        return entropy;
    }
}
