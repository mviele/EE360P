import java.util.HashMap;
import java.util.Map;

public class Inventory{

    private static Inventory inventory;

    private Map<String, Integer> map;
    private Inventory(File file){
        map = new HashMap<>();
        //Read file and add stuff to inventory
    }
}