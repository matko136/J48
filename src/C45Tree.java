import javax.swing.tree.TreeNode;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
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
    int confMat[][];
    boolean prune = false;

    public C45Tree(String dataFile, Attribute attrs[], int numberOfSamples, boolean validation, boolean prune, int kFold) {
        confMat = new int[2][2];
        this.attrs = attrs;
        this.validation = validation;
        this.prune = prune;
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
                    attVals[indexOfVal] = new AttValue(true,"", Double.parseDouble(attVal[indexOfVal]), attrs[indexOfVal].getName());// scan.nextDouble()
                } else {
                    attVals[indexOfVal] = new AttValue(false, attVal[indexOfVal], 0, attrs[indexOfVal].getName());
                }
                indexOfVal++;
            }
            samples[indexOfSample++] = new Sample(attVals);
            attVals = new AttValue[numberOfAttributes];
        }

        if(this.validation) {
            crossValidation(this.kFold);
        }

        System.out.println("========================Final tree========================");
        composeTree(this.samples);
        drawTree();

        System.out.println("\nConfusion matrix:");
        for(int i = 0; i < 2; i++) {
            System.out.println();
            for(int j = 0; j < 2; j++)
                System.out.print(" " + this.confMat[i][j]);
        }

        //this.composeTree();
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
        List<List<Sample>> samps = new ArrayList<List<Sample>>(kFold);
        //ArrayList<Sample>[] samps= new ArrayList<Sample>()[];
        //Sample samps[][] = new Sample[kFold][];

        for (int i = 0; i < kFold; i++) {
            samps.add(new ArrayList<Sample>());
        }

        for(int i = 0; i < numOfCatValues; i++) {
            int count = 0;
            for(int j = 0; j < sampsCategoricalValue[i].length; j++)  {
                samps.get(count).add(sampsCategoricalValue[i][j]);
                count++;
                if(count % kFold == 0)
                    count = 0;
            }
        }

        /*for(int i = 0; i < kFold; i++) {
            int celk = 0;
            for(int h = 0; h < numOfCatValues; h++) {
                int num = 0;
                while(num != numOfSpecificCatValues[h]/kFold) {
                    int k = ((i*numOfSpecificCatValues[h]/kFold)+num+celk);
                    int z = (i*numOfSpecificCatValues[h]/kFold)+num;
                    samps[i][num+celk] = sampsCategoricalValue[h][((i*numOfSpecificCatValues[h]/kFold)+num)];//this.samples[(i * this.numberOfSamples / kFold) + j];
                    num++;
                }
                celk += num;
            }
        }*/

        for(int indexTest = 0; indexTest < kFold; indexTest++) {
            ArrayList<Sample> trainSamples = new ArrayList<Sample>();
            //Sample trainSamples[] = new Sample[this.numberOfSamples-this.numberOfSamples/kFold];
            int indexSamp = 0;
            for(int i = 0; i < kFold; i++) {
                if(i != indexTest) {
                    List<Sample> actFold = samps.get(i);
                    for(int j = 0; j < actFold.size(); j++) {
                        trainSamples.add(actFold.get(j));
                    }
                }
            }
            System.out.println("\n" + (indexTest+1) + " th fold");
            System.out.println("Train samples");
            for(int i = 0; i < trainSamples.size(); i++) {
                System.out.println(trainSamples.get(i));
            }
            /*for(int i = 0; i < samps.length; i++) {
                if(i != indexTest) {
                    for (int j = 0; j < samps[i].length; j++) {
                        System.out.println(samps[i][j].toString());
                    }
                }
            }*/
            System.out.println("Test samples");
            for(int j = 0; j < samps.get(indexTest).size(); j++) {
                System.out.println(samps.get(indexTest).get(j));
            }
            composeTree(listToArr(trainSamples));
            drawTree();
            testTree(listToArr(samps.get(indexTest)));
            this.root = null;
        }

        /*for(int indexTest = 0; indexTest < 10; indexTest++) {
            Sample trainSamples[] = new Sample[this.numberOfSamples-this.numberOfSamples/kFold];
            int indexSamp = 0;
            for(int i = 0; i < kFold; i++) {
                if(i != indexTest) {
                    for(int j = 0; j < this.numberOfSamples/kFold; j++) {
                        trainSamples[indexSamp++] = samps[i][j];
                    }
                }
            }
            System.out.println("\n" + (indexTest+1) + " th fold");
            System.out.println("Train samples");
            for(int i = 0; i < samps.length; i++) {
                if(i != indexTest) {
                    for (int j = 0; j < samps[i].length; j++) {
                        System.out.println(samps[i][j].toString());
                    }
                }
            }
            System.out.println("Test samples");
            for(int j = 0; j < this.numberOfSamples/kFold; j++) {
                System.out.println(samps[indexTest][j]);
            }
            composeTree(trainSamples);
            drawTree();
            testTree(samps[indexTest]);
            this.root = null;
        }*/


    }

    private Sample[] listToArr(List<Sample> sampss) {
        Sample retArr[] = new Sample[sampss.size()];
        int count = 0;
        for (Sample sam : sampss) {
            retArr[count++] = sam;
        }
        return  retArr;
    }

    private void composeTree(Sample[] samps) {
        this.findBranches(null, 0, samps);
    }

    private void testTree(Sample[] samps) {
        for(int i = 0; i < samps.length; i++) {
            boolean leave = false;
            J48Node actNode = root;
            int nodeIndClass = 0;
            while (!leave) {
                if (actNode.childrenSize == 0) {
                    nodeIndClass = actNode.getClassIndex();
                    leave = true;
                } else {
                    int indAttr = actNode.getAttr();
                    if (this.attrs[indAttr].isNumeric()) {
                        double mean = actNode.getMean();
                        double std = actNode.getStd();
                        if(samps[i].getAttValue(indAttr).getnValue() < (mean-std)) {
                            actNode = actNode.getChild(0);
                        } else if(samps[i].getAttValue(indAttr).getnValue() >= (mean-std) && samps[i].getAttValue(indAttr).getnValue() < mean) {
                            actNode = actNode.getChild(1);
                        } else if(samps[i].getAttValue(indAttr).getnValue() >= mean && samps[i].getAttValue(indAttr).getnValue() < (mean+std)) {
                            actNode = actNode.getChild(2);
                        } else { //if(samples[i].getAttValue(attr).getnValue() >= (mean+std))
                            actNode = actNode.getChild(3);
                        }

                        /*if(samps[i].getAttValue(indAttr).getnValue() <= actNode.getTreshold())
                            actNode = actNode.getChild(0);
                        else
                            actNode = actNode.getChild(1);*/
                    } else {
                        for(int j = 0; j < this.attrs[indAttr].getNumberOfValues(); j++) {
                            if(samps[i].getAttValue(indAttr).getsValue().equals(this.attrs[indAttr].getValue(j))) {
                                actNode = actNode.getChild(j);
                                break;
                            }
                        }
                    }
                }
            }
            String realValue = samps[i].getAttValue(this.numberOfAttributes-1).getsValue();
            if(this.attrs[this.numberOfAttributes-1].getValue(nodeIndClass).equals(realValue)) {
                if(realValue.equals(this.attrs[this.numberOfAttributes-1].getValue(0))) {
                    confMat[1][1]++;
                } else {
                    confMat[0][0]++;
                }
            } else {
                if(realValue.equals(this.attrs[this.numberOfAttributes-1].getValue(1))) {
                    confMat[0][1]++;
                } else {
                    confMat[1][0]++;
                }
            }
        }
    }

    private void findBranches(J48Node parent, int childNumber, Sample[] samples) {

        //loga b = log10 b / log10 a -> log2(x) = Math.log(x) / Math.log(2)
        double bestGain = 0;
        int indexBestGain = -1;
        double bestSplit = 0;
        int bestBranchSize[] = new int[1];
        double bestMeanStd[] = new double[2];
        double dataSetEntropy = calcEntropy(samples, -1, null,null);
        if(dataSetEntropy == 0.0 || samples.length == 2) {
            J48Node node = new J48Node(parent, 0, samples, -1, 0,0);
            calcErrors(node);
            parent.setChild(childNumber, node);
            return;
        }
        for(int i = 0; i < numberOfAttributes-1; i++) {
            /*if(i == 5) {
                int d = 5;
            }*/
            double entropy = 0;
            int branchSize[] = new int[1];
            double localBestSplit = 0;
            double meanStd[] = new double[2];
            if(!attrs[i].isNumeric()) {
                branchSize = new int[this.attrs[i].getNumberOfValues()];
                entropy = calcEntropy(samples, i, branchSize,null);
            } else {
                branchSize = new int[4];
                entropy = calcEntropy(samples, i,  branchSize, meanStd);
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
                bestMeanStd = meanStd;
            }
        }
        if(indexBestGain == -1) {
            J48Node node = new J48Node(parent, 0, samples, -1, 0,0);
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
            numberOfAttrClassValues = 4;
            nodes = new J48Node[4];
            samps = new Sample[4][];
            currentSampsIndex = new int[4];
            for(int i = 0; i < 4; i++) {
                System.out.println(bestBranchSize[i]);
                samps[i] = new Sample[bestBranchSize[i]];
            }
            for(int i = 0; i < samples.length; i++) {
                double mean = bestMeanStd[0];
                double std = bestMeanStd[1];
                /*if(bestBranchSize[3] == 12)
                    System.out.println();*/
                if(samples[i].getAttValue(indexBestGain).getnValue() < (mean-std)) {
                    samps[0][currentSampsIndex[0]++] = samples[i];
                } else if(/*samples[i].getAttValue(indexBestGain).getnValue() >= (mean-std) && */samples[i].getAttValue(indexBestGain).getnValue() < mean) {
                    samps[1][currentSampsIndex[1]++] = samples[i];
                } else if(/*samples[i].getAttValue(indexBestGain).getnValue() >= mean && */samples[i].getAttValue(indexBestGain).getnValue() < (mean+std)) {
                    samps[2][currentSampsIndex[2]++] = samples[i];
                } else { //if(samples[i].getAttValue(attr).getnValue() >= (mean+std))
                    samps[3][currentSampsIndex[3]++] = samples[i];
                }
                /*if(samples[i].getAttValue(indexBestGain).getnValue() <= bestSplit) {
                    samps[0][currentSampsIndex[0]++] = samples[i];
                } else {
                    samps[1][currentSampsIndex[1]++] = samples[i];
                }*/
            }
        }
        J48Node node = new J48Node(parent, numberOfAttrClassValues, samples, indexBestGain, bestMeanStd[0],bestMeanStd[1]);
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
        if(prune) {
            double childSumErrors = 0;
            /*if (node.sampleSize == 178) {
                int h = 0;
            }*/
            for (int i = 0; i < node.childrenSize; i++) {
                childSumErrors += node.getChild(i).getSubErrorRate();
            }
            if (childSumErrors > node.getErrorRate()) {
                node.setChildrenSize(0);
            }
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

    private double calcEntropy(Sample[] samples, int attr,int branchSize[], double meanStd[]) {
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
                int classValues[][] = new int[4][numberOfClassValues];
                int classSize[] = new int[4];
                double sumVal = 0;
                for(int i = 0; i < samples.length; i++) {
                    sumVal += samples[i].getAttValue(attr).getnValue();
                }
                double mean = sumVal/samples.length;
                double sumStd = 0;
                for(int i = 0; i < samples.length; i++) {
                    sumStd += Math.pow((samples[i].getAttValue(attr).getnValue()-mean),2);
                }
                double std = Math.sqrt(sumStd/samples.length);
                for(int i = 0; i < numOfSamp; i++) {
                    for(int j = 0; j < numberOfClassValues; j++) {
                        if(samples[i].getAttValue(this.numberOfAttributes-1).getsValue().equals(this.attrs[this.numberOfAttributes-1].getValue(j))) {
                            if(samples[i].getAttValue(attr).getnValue() < (mean-std)) {
                                classValues[0][j]++;
                                classSize[0]++;
                            } else if(samples[i].getAttValue(attr).getnValue() >= (mean-std) && samples[i].getAttValue(attr).getnValue() < mean) {
                                classValues[1][j]++;
                                classSize[1]++;
                            } else if(samples[i].getAttValue(attr).getnValue() >= mean && samples[i].getAttValue(attr).getnValue() < (mean+std)) {
                                classValues[2][j]++;
                                classSize[2]++;
                            } else { //if(samples[i].getAttValue(attr).getnValue() >= (mean+std))
                                classValues[3][j]++;
                                classSize[3]++;
                            }
                            break;
                        }
                    }
                }
                branchSize[0] = classSize[0];
                branchSize[1] = classSize[1];
                branchSize[2] = classSize[2];
                branchSize[3] = classSize[3];
                meanStd[0] = mean;
                meanStd[1] = std;
                for(int j = 0; j < 4; j++) {
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
                    double mean = node.getMean();
                    double std = node.getStd();
                    switch(i) {
                        case 0:
                            System.out.println(" < " + (mean-std));
                            break;
                        case 1:
                            System.out.println(" <" + (mean-std) + ".." + (mean) + ")");
                            break;
                        case 2:
                            System.out.println(" <" + (mean) + ".." + (mean+std) + ")");
                            break;
                        case 3:
                            System.out.println(" >= " + (mean+std));
                    }
                    /*if (i == 0) {
                        System.out.println(" < " + (mean-std));
                    } else {
                        System.out.println(" > " + node.getTreshold());
                    }*/
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
