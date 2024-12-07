import java.util.ArrayList;

public class Attribute {
    String name;
    ArrayList<String> possibleValues;
    public Attribute(String name){
        this.name = name;
        possibleValues = new ArrayList<>();
    }
    public void addPossibleValue(String value){
        possibleValues.add(value);
    }
}
