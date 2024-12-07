import java.util.ArrayList;

class Pair{
    String attributeValue;
    Node node;
    Pair(String attributeValue, Node node){
        this.attributeValue = attributeValue;
        this.node = node;
    }
}

public class Node {
    String name;
    String value;
    ArrayList<Pair> children;
    Node(String name, String value){
        this.name = name;
        this.value = value;
        children = new ArrayList<>();
    }
    public void addChild(String attributeValue, Node node){
        children.add(new Pair(attributeValue, node));
    }
    public String getName(){
        return name;
    }
    public String getValue(){
        return value;
    }
    public Node getChild(String attributeValue){
        for(Pair pair : children){
            if(pair.attributeValue.equals(attributeValue)){
                return pair.node;
            }
        }
        return null;
    }
}
