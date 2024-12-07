import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Main {
    private static ArrayList<Attribute> readAttributes(String filepath){
        ArrayList<Attribute> attributes = new ArrayList<>();
        try{
            File attributeFile = new File(filepath);
            Scanner sc = new Scanner(attributeFile);
            boolean readingAttributes = false;
            while(sc.hasNextLine()){
                String line = sc.nextLine().trim();
                if(line.length() == 0){
                    continue;
                }
                if(line.equals("--attributes")){
                    readingAttributes = true;
                    continue;
                }
                if(readingAttributes){
                    String[] parts = line.split(":");
                    Attribute attribute = new Attribute(parts[0].trim());
                    String valuesString = parts[1].trim();
                    valuesString = valuesString.substring(0, valuesString.length() - 1);
                    String[] values = valuesString.split(",\\s*");
                    for(String value : values){
                        attribute.addPossibleValue(value);
                    }
                    attributes.add(attribute);
                }
            }
            sc.close();

        }catch(IOException e){
            e.printStackTrace();
        }
        return attributes;
    }
    private static ArrayList<Example> readData(String filepath){
        ArrayList<Example> examples = new ArrayList<>();
        try{
            File dataFile = new File(filepath);
            Scanner sc = new Scanner(dataFile);
            while(sc.hasNextLine()){
                String line = sc.nextLine().trim();
                Example example = new Example(line.split(",\\s*"));
                examples.add(example);
            }
            sc.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        return examples;
    }
    private static boolean resultMatched(Node root, Example example){
        Node node = root;
        if(node == null){
            System.out.println("Root is null");
            System.exit(1);
        }
        while(!node.name.equals("Leaf")){
            ArrayList<Pair> children = node.children;
            String nodeName = node.name;
            node = node.getChild(example.getAttributeValue(node.name));
            if(node == null){
                System.out.println(nodeName);
                for(Pair pair : children){
                    System.out.println(nodeName + " : " + pair.attributeValue + " " + pair.node.name + " " + pair.node.value);
                }
                System.exit(1);
            }
        }
        return node.value.equals(example.classValue);
    }
    private static double getAccuracy(Node root, ArrayList<Example> examples){
        int correct = 0;
        for(Example example : examples){
            if(resultMatched(root, example)){
                correct++;
            }
        }
        return ((double)correct / examples.size()) * 100;
    }
    private static double getAvgAccuracy(ArrayList<Attribute> attributes, ArrayList<Example> data, int method, boolean onlyBest, int nTrials){
        double accuracy = 0.0;
        for(int i=0; i<nTrials; i++){
            Collections.shuffle(data);
            int splitIndex = (int)(data.size() * 0.8);
            ArrayList<Example> trainingData = new ArrayList<>(data.subList(0, splitIndex)); // 80% of the data is training data
            ArrayList<Example> testingData = new ArrayList<>(data.subList(splitIndex, data.size()));  // 20% of the data is testing data
            DecisionTree decisionTree = new DecisionTree(method,onlyBest);
            Node root = decisionTree.buildTree(attributes, trainingData, trainingData);
            accuracy += getAccuracy(root, testingData);
        }
        return accuracy / nTrials;
    }
    private static void printLine(int width) {
        int totalWidth = width + 10; // Add extra for separators and spaces
        System.out.println("-".repeat(totalWidth));
    }

    private static void printResult(double IGB, double GIB, double IG3B, double GI3B,int nTrials) {
        // Column widths
        int col1Width = 50; // Width of the first column
        int col2Width = 20; // Width of the second column
        int col3Width = 20; // Width of the third column

        System.out.println("\n");
        // Print the table header
        printLine(col1Width + col2Width +col3Width);
        System.out.printf("| %-"+col1Width+"s | %-"+(col2Width+col3Width+3)+"s |%n","", "Average accuracy over "+nTrials+" trials");
        printLine(col1Width + col2Width + col3Width);
        System.out.printf("| %-"+col1Width+"s | %-"+col2Width+"s | %-"+col3Width+"s |%n","Attribute selection strategy", "Information gain", "Gini impurity");
        printLine(col1Width + col2Width + col3Width);

        DecimalFormat df = new DecimalFormat("#.####");
        
        // Print the table rows
        System.out.printf("| %-"+col1Width+"s | %-"+col2Width+"s | %-"+col3Width+"s |%n","Always select the best attribute", df.format(IGB)+" %", df.format(GIB)+" %");
        System.out.printf("| %-"+col1Width+"s | %-"+col2Width+"s | %-"+col3Width+"s |%n","Select one randomly from the top three attributes", df.format(IG3B)+" %", df.format(GI3B)+" %");
        
        // Print the table footer
        printLine(col1Width + col2Width + col3Width);
        System.out.println("\n");

    }
    public static void main(String[] args){
        ArrayList<Attribute> attributes = readAttributes("./dataset/car.c45-names");
        ArrayList<Example> data= readData("./dataset/car.data");
        int nTrials = 20;
        if(args.length > 0){
            nTrials = Integer.parseInt(args[0]);
            if(nTrials < 1){
                System.out.println("Number of trials should be greater than 0");
                nTrials = 20;
                System.out.println("Using default number of trials: 20");
            }
        }
        double avgAccuracy_InformationGain_onlyBest = getAvgAccuracy(attributes, data, 1, true, nTrials);
        double avgAccuracy_InformationGain_bestThree = getAvgAccuracy(attributes, data, 1, false, nTrials);
        double avgAccuracy_GiniImpurity_onlyBest = getAvgAccuracy(attributes, data, 2, true,  nTrials);
        double avgAccuracy_GiniImpurity_bestThree = getAvgAccuracy(attributes, data, 2, false,  nTrials);

        printResult(avgAccuracy_InformationGain_onlyBest, avgAccuracy_GiniImpurity_onlyBest, avgAccuracy_InformationGain_bestThree, avgAccuracy_GiniImpurity_bestThree,nTrials);
    }
}
