import javax.swing.tree.TreeNode;
import java.io.*;
import java.util.Scanner;
import org.apache.commons.math3.distribution.BetaDistribution;

public class C45Tree {
    J48Node root = null;
    int minimumInstancesOnLeave = 2;
    Sample samples[];
    Attribute[] attrs;
    private boolean validation;
    private int kFold;
    int numberOfSamples;
    BufferedReader buffReader;
    Scanner scan;
    int numberOfAttributes;

    public C45Tree(String dataFile, Attribute attrs[], int numberOfSamples, boolean validation, int kFold) {
        this.attrs = attrs;
        this.validation = validation;
        this.kFold = kFold;
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

        if(this.validation) {
            crossValidation(this.kFold);
        }
        this.composeTree();
        //J48Node node = new J48Node(null, null);
    }

    private void crossValidation(int kFold) {
        int numOfCatValues = this.attrs[this.numberOfAttributes-1].getNumberOfValues();
        int numOfSpecificCatValues[]= new int[numOfCatValues];
        for(int i = 0; i < this.numberOfSamples; i++) {
            for(int j = 0; j < numOfCatValues; j++) {
                if(this.samples[i].getAttValue(this.numberOfAttributes-1).getsValue().equals(this.attrs[this.numberOfAttributes-1].getValue(j))) {
                    numOfSpecificCatValues[j]++;
                }
            }
        }
        Sample sampsCategoricalValue[][] = new Sample[numOfCatValues][];
        for(int i = 0; i < numOfCatValues; i++) {
            sampsCategoricalValue[i] = new Sample[numOfSpecificCatValues[i]];
        }
        int specCatValIndex[] = new int[numOfCatValues];
        for(int i = 0; i < this.numberOfSamples; i++) {
            for(int j = 0; j < numOfCatValues; j++) {
                if(this.samples[i].getAttValue(this.numberOfAttributes-1).getsValue().equals(this.attrs[this.numberOfAttributes-1].getValue(j))) {
                    sampsCategoricalValue[j][specCatValIndex[j]++] = this.samples[i];
                }
            }
        }
        specCatValIndex = new int[numOfCatValues];
        Sample samps[][] = new Sample[kFold][this.numberOfSamples/kFold];
        for(int i = 0; i < kFold-1; i++) {
            for(int j = 0; j < (i+1)*this.numberOfSamples/kFold; j++) {
                for(int h = 0; h < numOfCatValues; h++) {
                    int num = 0;
                    while(num != numOfSpecificCatValues[h]/kFold) {
                        samps[i][j] = this.samples[(i * this.numberOfSamples / kFold) + j];
                    }
                }
            }
        }
    }

    private void composeTree() {
        this.findBranches(null, 0, samples);
    }

    private void findBranches(J48Node parent, int childNumber, Sample[] samples) {

        //loga b = log10 b / log10 a -> log2(x) = Math.log(x) / Math.log(2)
        double bestGain = 0;
        int indexBestGain = -1;
        double bestSplit = 0;
        int bestBranchSize[] = new int[1];
        double dataSetEntropy = calcEntropy(samples, -1,0, null);
        if(dataSetEntropy == 0.0 || samples.length == 2) {
            J48Node node = new J48Node(parent, 0, samples, -1, 0);
            calcErrors(node);
            parent.setChild(childNumber, node);
            return;
        }
        for(int i = 0; i < numberOfAttributes-1; i++) {
            if(i == 5) {
                int d = 5;
            }
            double entropy = 0;
            int branchSize[] = new int[1];
            double localBestSplit = 0;
            if(!attrs[i].isNumeric()) {
                branchSize = new int[this.attrs[i].getNumberOfValues()];
                entropy = calcEntropy(samples, i, 0, branchSize);
            } else {
                branchSize = new int[2];
                double sorted[][] = sort(samples, i);
                double bestEntropy = 1;
                int indexBestEntropy = 0;
                double lastSplit = 1000000000;
                for(int j = 0; j < samples.length; j++) {
                    if(lastSplit != sorted[1][j]) {
                        int actualBranchSize[] = new int[2];
                        double splitEntropy = calcEntropy(samples, i, sorted[1][j], actualBranchSize);
                        lastSplit = sorted[1][j];
                        /*boolean minimumInstances = true;
                        int classMinimValues[] = new int[this.attrs[this.numberOfAttributes-1].getNumberOfValues()];
                        for(int k = 0; k < actualBranchSize.length; k++) {
                            if(actualBranchSize[k] < minimumInstancesOnLeave) {
                                minimumInstances = false;
                                break;
                            }
                            for(int h = 0; h < this.attrs[this.numberOfAttributes-1].getNumberOfValues(); h++) {
                                if()
                            }
                        }*/
                        boolean minimumInstances = true;
                        for(int k = 0; k < actualBranchSize.length; k++) {
                            if (actualBranchSize[k] < minimumInstancesOnLeave) {
                                minimumInstances = false;
                                break;
                            }
                        }
                        if ((splitEntropy <= bestEntropy) && minimumInstances) {
                            bestEntropy = splitEntropy;
                            indexBestEntropy = j;
                            localBestSplit = lastSplit;
                            branchSize = actualBranchSize;
                        }
                    }
                }
                entropy = bestEntropy;
            }

            boolean minimumInstances = true;
            for(int k = 0; k < branchSize.length; k++) {
                if(branchSize[k] < minimumInstancesOnLeave) {
                    minimumInstances = false;
                    break;
                }
            }

            if((dataSetEntropy - entropy >= bestGain) && minimumInstances) {
                bestSplit = localBestSplit;
                bestGain = dataSetEntropy - entropy;
                indexBestGain = i;
                bestBranchSize = branchSize;
            }
        }
        if(indexBestGain == -1) {
            J48Node node = new J48Node(parent, 0, samples, -1, 0);
            calcErrors(node);
            parent.setChild(childNumber, node);
            return;
        }
        int numberOfAttrClassValues = this.attrs[indexBestGain].getNumberOfValues();
        J48Node nodes[] = new J48Node[numberOfAttrClassValues];
        Sample samps[][] = new Sample[numberOfAttrClassValues][];
        int currentSampsIndex[] = new int[numberOfAttrClassValues];
        if(!attrs[indexBestGain].isNumeric()) {
            for(int i = 0; i < numberOfAttrClassValues; i++) {
                samps[i] = new Sample[bestBranchSize[i]];
            }
            for(int i = 0; i < samples.length; i++) {
                for(int j = 0; j < numberOfAttrClassValues; j++) {
                    if(samples[i].getAttValue(indexBestGain).getsValue().equals(this.attrs[indexBestGain].getValue(j))) {
                        samps[j][currentSampsIndex[j]++] = samples[i];
                        break;
                    }
                }
            }
        } else {
            numberOfAttrClassValues = 2;
            nodes = new J48Node[2];
            samps = new Sample[2][];
            currentSampsIndex = new int[2];
            for(int i = 0; i < 2; i++) {
                System.out.println(bestBranchSize[i]);
                samps[i] = new Sample[bestBranchSize[i]];
            }
            for(int i = 0; i < samples.length; i++) {
                if(samples[i].getAttValue(indexBestGain).getnValue() <= bestSplit) {
                    samps[0][currentSampsIndex[0]++] = samples[i];
                } else {
                    samps[1][currentSampsIndex[1]++] = samples[i];
                }
            }
        }
        J48Node node = new J48Node(parent, numberOfAttrClassValues, samples, indexBestGain, bestSplit);
        calcErrors(node);
        if(root == null) {
            root = node;
        }
        if(parent != null) {
            parent.setChild(childNumber , node);
        }
        for(int i = 0; i < numberOfAttrClassValues; i++) {
            findBranches(node, i, samps[i]);
        }


        //prune child nodes
        double childSumErrors = 0;
        if(node.sampleSize == 178) {
            int h = 0;
        }
        for(int i = 0; i < node.childrenSize; i++) {
            childSumErrors += node.getChild(i).getSubErrorRate();
        }
        if(childSumErrors > node.getErrorRate()) {
            node.setChildrenSize(0);
        }
    }

    private void calcErrors(J48Node node) {
        int numOfClassVal = this.attrs[this.numberOfAttributes-1].getNumberOfValues();
        Sample[] sampss = node.getSamples();
        int[] classInst = new int[numOfClassVal];
        for(int i = 0; i < node.getSampleSize(); i++) {
            for(int j = 0; j < numOfClassVal; j++) {
                if(sampss[i].getAttValue(this.numberOfAttributes-1).getsValue().equals(this.attrs[this.numberOfAttributes-1].getValue(j))) {
                    classInst[j]++;
                    break;
                }
            }
        }
        int bestClassInd = 0;
        int bestNumOfCl = 0;
        for(int j = 0; j < numOfClassVal; j++) {
            if(classInst[j] >= bestNumOfCl) {
                bestNumOfCl = classInst[j];
                bestClassInd = j;
            }
        }
        int numOfMisClass = 0;
        for(int j = 0; j < numOfClassVal; j++) {
            if(j != bestClassInd) {
                numOfMisClass += classInst[j];
            }
        }
        node.setMisClassifiedSamps(numOfMisClass);
        node.setClassIndex(bestClassInd);
        double errorRate = 0;
        if(numOfMisClass > 0) {
            BetaDistribution dist = new BetaDistribution(sampss.length - numOfMisClass, numOfMisClass + 1);
            errorRate = sampss.length * (1 - dist.inverseCumulativeProbability(0.25));
        } else {
            errorRate = sampss.length * (1- (Math.pow(0.25,((double)1/sampss.length))));
        }
        node.setErrorRate(errorRate);
    }


    private double[][] sort(Sample[] samples, int sortedAttrInd) {
        int numOfSamp = samples.length;
        double attrValues[][] = new double[2][numOfSamp];
        for(int i = 0; i < numOfSamp; i++) {
            attrValues[0][i] = i;
            attrValues[1][i] = samples[i].getAttValue(sortedAttrInd).getnValue();
        }
        for(int i = 0; i < numOfSamp; i++) {
            for(int j = numOfSamp - 1; j > i; j--) {
                if(attrValues[1][j] < attrValues[1][j - 1]) {
                    double k = attrValues[1][j];
                    double in = attrValues[0][j];
                    attrValues[1][j] = attrValues[1][j - 1];
                    attrValues[1][j - 1] = k;
                    attrValues[0][j] = attrValues[0][j-1];
                    attrValues[0][j - 1] = in;
                }
            }
        }
        return attrValues;
    }

    private double calcEntropy(Sample[] samples, int attr, double split, int branchSize[]) {
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
                if(valueProb > 0.0) {
                    entropy += valueProb * (Math.log(valueProb) / Math.log(2));
                }
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
                    branchSize[i] = attrClassValues[i];
                    for(int j = 0; j < numberOfClassValues; j++) {
                        double valueProb = (double)classValues[i][j]/attrClassValues[i];
                        if(valueProb > 0) {
                            entropy += valueProb * (Math.log(valueProb) / Math.log(2)) * (-1) * ((double) attrClassValues[i] / numOfSamp);
                        }
                    }
                }
            } else {
                int numberOfClassValues = this.attrs[this.numberOfAttributes-1].getNumberOfValues();
                int classValues[][] = new int[2][numberOfClassValues];
                int classSize[] = new int[2];
                for(int i = 0; i < numOfSamp; i++) {
                    for(int j = 0; j < numberOfClassValues; j++) {
                        if(samples[i].getAttValue(this.numberOfAttributes-1).getsValue().equals(this.attrs[this.numberOfAttributes-1].getValue(j))) {
                            if(samples[i].getAttValue(attr).getnValue() <= split) {
                                classValues[0][j]++;
                                classSize[0]++;
                            } else {
                                classValues[1][j]++;
                                classSize[1]++;
                            }
                            break;
                        }
                    }
                }
                branchSize[0] = classSize[0];
                branchSize[1] = classSize[1];
                for(int j = 0; j < 2; j++) {
                    for(int i = 0; i < numberOfClassValues; i++) {
                        double valueProb = (double) classValues[j][i] / classSize[j];
                        if(valueProb > 0) {
                            entropy += valueProb * (Math.log(valueProb) / Math.log(2)) * (-1) * ((double) classSize[j] / numOfSamp);
                        }
                    }
                }
            }
        }
        return entropy;
    }

    public void drawTree() {
        this.draw(root, 1);
    }

    private void draw(J48Node node, int level) {
        int numOfClassVal = this.attrs[this.numberOfAttributes-1].getNumberOfValues();
        if(node.childrenSize > 0) {
            for (int i = 0; i < node.childrenSize; i++) {
                for (int j = 1; j < level; j++) {
                    System.out.print("|");
                    System.out.print("\t");
                }
                if (!this.attrs[node.attr].isNumeric()) {
                    System.out.println(this.attrs[node.attr].getValue(i));
                    draw(node.children[i], level + 1);
                } else {
                    System.out.print(this.attrs[node.attr].getName());
                    if (i == 0) {
                        System.out.println(" <= " + node.getTreshold());
                    } else {
                        System.out.println(" > " + node.getTreshold());
                    }
                    draw(node.children[i], level + 1);
                }
            }
        } else {
            Sample[] sampss = node.getSamples();
            int[] classInst = new int[numOfClassVal];
            for(int i = 0; i < node.getSampleSize(); i++) {
                for(int j = 0; j < numOfClassVal; j++) {
                    if(sampss[i].getAttValue(this.numberOfAttributes-1).getsValue().equals(this.attrs[this.numberOfAttributes-1].getValue(j))) {
                        classInst[j]++;
                        break;
                    }
                }
            }
            int bestClassInd = 0;
            int bestNumOfCl = 0;
            for(int j = 0; j < numOfClassVal; j++) {
                if(classInst[j] >= bestNumOfCl) {
                    bestNumOfCl = classInst[j];
                    bestClassInd = j;
                }
            }

            for (int j = 1; j < level; j++) {
                System.out.print("|");
                System.out.print("\t");
            }
            final String RED = "\033[0;31m";
            final String RESET = "\033[0m";
            System.out.println(RED + this.attrs[this.numberOfAttributes-1].getValue(bestClassInd) + ": " + node.getSampleSize() + ((node.getSampleSize()-bestNumOfCl > 0) ? (" / " + (node.getSampleSize()-bestNumOfCl)) : "") + RESET);
        }
    }

}
