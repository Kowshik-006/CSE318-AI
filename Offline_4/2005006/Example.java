public class Example{
    String buying;
    String maint;
    String doors;
    String persons;
    String lug_boot;
    String safety;
    String classValue;

    public Example(){
        buying = "";
        maint = "";
        doors = "";
        persons = "";
        lug_boot = "";
        safety = "";
        classValue = "";
    }
    public Example(String[] values){
        buying = values[0];
        maint = values[1];
        doors = values[2];
        persons = values[3];
        lug_boot = values[4];
        safety = values[5];
        classValue = values[6];
    }

    public String getAttributeValue(String attribute){
        switch(attribute){
            case "buying":
                return buying;
            case "maint":
                return maint;
            case "doors":
                return doors;
            case "persons":
                return persons;
            case "lug_boot":
                return lug_boot;
            case "safety":
                return safety;
            default:
                return null;
        }
    }
}