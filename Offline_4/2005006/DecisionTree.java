import java.util.ArrayList;
import java.util.Random;

public class DecisionTree{
    int method;
    boolean onlyBest;
    public DecisionTree(int method, boolean onlyBest){
        this.method = method;
        this.onlyBest = onlyBest;
    }
    private int[] countClassValues(ArrayList<Example> examples){
        int[] count = {0,0,0,0};
        for(Example example : examples){
            switch(example.classValue){
                case "unacc":
                    count[0]++;
                    break;
                case "acc":
                    count[1]++;
                    break;
                case "good":
                    count[2]++;
                    break;
                case "vgood":
                    count[3]++;
                    break;
            }
        }
        return count;
    }
    private String pluralityValue(ArrayList<Example> examples){
        String[] classValues = {"unacc", "acc", "good", "vgood"}; 
        int[] count = countClassValues(examples);
        
        int max = Integer.MIN_VALUE;
        int maxIndex = -1;
        for(int i = 0; i < count.length; i++){
            if(count[i] > max){
                max = count[i];
                maxIndex = i;
            }
        }
        return classValues[maxIndex];
    }
    private String sameClass(ArrayList<Example> examples){
        String classValue = examples.get(0).classValue;
        for(Example example : examples){
            if(!example.classValue.equals(classValue)){
                return null;
            }
        }
        return classValue;
        
    }
    private double calculateEntropy(ArrayList<Example> examples){
        int[] count = countClassValues(examples);
        double entropy = 0.0;
        for(int i = 0; i < count.length; i++){
            double probability = (double)count[i] / examples.size();
            if(probability != 0){
                // log_2(probability) = ln(probability) / ln(2)
                entropy -= probability * (Math.log(probability) / Math.log(2));
            }
        }
        return entropy;
    }
    private double calculateInformationGain(Attribute attribute, ArrayList<Example> examples){
        double parentEntropy = calculateEntropy(examples);
        double childrenEntropy = 0.0;
        for(String attributeValue: attribute.possibleValues){
            ArrayList<Example> examplesWithValue = new ArrayList<>();
            for(Example example : examples){
                if(example.getAttributeValue(attribute.name).equals(attributeValue)){
                    examplesWithValue.add(example);
                }
            }
            if(examplesWithValue.size() == 0){
                continue;
            }
            childrenEntropy += calculateEntropy(examplesWithValue) * ((double)examplesWithValue.size()/ examples.size());
        }

        return parentEntropy - childrenEntropy;
    }
    private Attribute bestAttribute_InformationGain(ArrayList<Attribute> attributes, ArrayList<Example> examples){
        double maxGain = Double.MIN_VALUE;
        Attribute bestAttribute = null;
        for(Attribute attribute : attributes){
            double gain = calculateInformationGain(attribute, examples);
            if(gain > maxGain){
                // found = true;
                maxGain = gain;
                bestAttribute = attribute;
            }
        }
        return bestAttribute;
    }

    private double calculateGini(ArrayList<Example> examples){
        int[] count = countClassValues(examples);
        double gini = 1.0;
        for(int i=0;i < count.length; i++){
            double probability = (double)count[i] / examples.size();
            gini -= probability * probability;
        }
        return gini;
    }

    private double calculateGiniGain(Attribute attribute, ArrayList<Example> examples){
        double parentGini = calculateGini(examples);
        double childrenGini = 0.0;
        for(String attributeValue : attribute.possibleValues){
            ArrayList<Example> examplesWithValue = new ArrayList<>();
            for(Example example : examples){
                if(example.getAttributeValue(attribute.name).equals(attributeValue)){
                    examplesWithValue.add(example);
                }
            }
            if(examplesWithValue.size() == 0){
                continue;
            }
            childrenGini += calculateGini(examplesWithValue) * ((double)examplesWithValue.size() / examples.size());
        }
        return parentGini - childrenGini;
    }
    private Attribute bestAttribute_GiniImpurity(ArrayList<Attribute> attributes, ArrayList<Example> examples){
        double maxGain = Double.MIN_VALUE;
        Attribute bestAttribute = null;
        for(Attribute attribute : attributes){
            double gain = calculateGiniGain(attribute, examples);
            if(gain > maxGain){
                maxGain = gain;
                bestAttribute = attribute;
            }
        }
        return bestAttribute;
    }
    public Node buildTree(ArrayList<Attribute> attributes, ArrayList<Example> examples, ArrayList<Example> parentExamples){
        if(examples.size() == 0){
            // System.out.println("No examples remaining!");
            String plurality = pluralityValue(parentExamples);
            return new Node("Leaf",plurality);
        }
        if(attributes.size() == 0){
            // System.out.println("No attributes remaining!");
            String plurality = pluralityValue(examples);
            return new Node("Leaf",plurality);
        }
        String sameClass = sameClass(examples);
        if(sameClass != null){
            return new Node("Leaf",sameClass);
        }
        Attribute bestAttribute = null;
        if(method == 1){
            if(onlyBest){
                bestAttribute = bestAttribute_InformationGain(attributes, examples);
            }
            else{
                Attribute bestAttribute_1 = bestAttribute_InformationGain(attributes, examples);
                ArrayList<Attribute> newAttributes = new ArrayList<>(attributes);
                newAttributes.remove(bestAttribute_1);
                if(newAttributes.size() != 0){
                    Random rand = new Random();
                    Attribute bestAttribute_2 = bestAttribute_InformationGain(newAttributes, examples);
                    newAttributes.remove(bestAttribute_2);
                    if(newAttributes.size() != 0){
                        Attribute bestAttribute_3 = bestAttribute_InformationGain(newAttributes, examples);
                        int random = rand.nextInt(3); // 0, 1, 2
                        if(random == 0){
                            bestAttribute = bestAttribute_1;
                        }
                        else if(random == 1){
                            bestAttribute = bestAttribute_2;
                        }
                        else{
                            bestAttribute = bestAttribute_3;
                        }

                    }
                    else{
                        int random = rand.nextInt(2); // 0, 1
                        if(random == 0){
                            bestAttribute = bestAttribute_1;
                        }
                        else{
                            bestAttribute = bestAttribute_2;
                        }
                    }
                }
                else{
                    bestAttribute = bestAttribute_1;
                }
                
            }
        }
        else if(method == 2){
            if(onlyBest){
                bestAttribute = bestAttribute_GiniImpurity(attributes, examples);
            }
            else{
                Attribute bestAttribute_1 = bestAttribute_GiniImpurity(attributes, examples);
                ArrayList<Attribute> newAttributes = new ArrayList<>(attributes);
                newAttributes.remove(bestAttribute_1);
                if(newAttributes.size() != 0){
                    Random rand = new Random();
                    Attribute bestAttribute_2 = bestAttribute_GiniImpurity(newAttributes, examples);
                    newAttributes.remove(bestAttribute_2);
                    if(newAttributes.size() != 0){
                        Attribute bestAttribute_3 = bestAttribute_GiniImpurity(newAttributes, examples);
                        int random = rand.nextInt(3); // 0, 1, 2
                        if(random == 0){
                            bestAttribute = bestAttribute_1;
                        }
                        else if(random == 1){
                            bestAttribute = bestAttribute_2;
                        }
                        else{
                            bestAttribute = bestAttribute_3;
                        }
                    }
                    else{
                        int random = rand.nextInt(2); // 0, 1
                        if(random == 0){
                            bestAttribute = bestAttribute_1;
                        }
                        else{
                            bestAttribute = bestAttribute_1;
                        }
                    }
                }
                else{
                    bestAttribute = bestAttribute_1;
                }
            }
        }
        // This can occurr when there are attributes and examples remaining 
        // But the gain is 0 for all remaining attributes
        // In this case, we return the plurality value of the examples
        if(bestAttribute == null){
            return new Node("Leaf",pluralityValue(examples));
        }
        Node root = new Node(bestAttribute.name, "-1");
        for(String attributeValue : bestAttribute.possibleValues){
            ArrayList<Example> examplesWithValue = new ArrayList<>();
            for(Example example : examples){
                if(example.getAttributeValue(bestAttribute.name).equals(attributeValue)){
                    examplesWithValue.add(example);
                }
            }
            ArrayList<Attribute> newAttributes = new ArrayList<>(attributes);
            newAttributes.remove(bestAttribute);
            Node child = buildTree(newAttributes, examplesWithValue, examples);
            root.addChild(attributeValue, child);
        }
        return root;
    }
}
