/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Inventory{

    private static Inventory inventory;

    private Map<String, Integer> inventoryMap;
    private List<Order> orderList;
    private int nextOrderID;
    private Inventory(File file){
        inventoryMap = new HashMap<>();
        try{
            Scanner s = new Scanner(file);
            orderList = new ArrayList<>();
            nextOrderID = 1;
            
            // parse the inventory file
            while(s.hasNext()){
                int k;
                String cur = s.next();		
                if(s.hasNextInt()){
                    k = s.nextInt();
                }
                else{
                    throw new Exception("ERROR: Invalid input file");
                }
                inventoryMap.put(cur, k);
            } 
            s.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public synchronized static Inventory getInstance(File file){
        if(inventory == null){
            inventory = new Inventory(file);
        }

        return inventory;
    }

    public String purchase(String username, String productName, int quantity){
        if(inventoryMap.containsKey(productName)){
            if(inventoryMap.get(productName) >= quantity){
                //Subtract inventory
                inventoryMap.put(productName, inventoryMap.get(productName) - quantity);

                //Add order to orderList
                Order order = new Order(nextOrderID++, username, productName, quantity);
                orderList.add(order);

                //Successful purchase message
                return "You order has been placed, " + order.toString();
            }

            //Insufficient quantity message
            return "Not Available - Not enough items";
        }
        else{
        //No such product message
        return "Not Available - We do not sell this product";
        }
        
    }

  public String cancel(int orderID){
    for(Order order: orderList){
        if(order.getOrderID() == orderID){
            //Add inventory back
            String product = order.getProductName();
            inventoryMap.put(product, order.getQuantity() + inventoryMap.get(product));

            //Remove order from order list
            orderList.remove(order);

            //Successful cancellation message
            return "Order " + Integer.toString(orderID) + " is canceled"; 
        }
    }

    //No such order message
    return Integer.toString(orderID) + " not found, no such order";
  }

  public String search(String username){
    String searchResult = new String();
    for(Order order: orderList){
      if(order.getUsername().equals(username)){
        searchResult += Integer.toString(order.getOrderID()) + ", " + 
          order.getProductName() + ", " + Integer.toString(order.getQuantity()) + "\n";
      }
    }

    return searchResult.length() != 0 ? searchResult : "No order found for " + username; 
  }

  public String list(){
    String listString = new String();
    for(String s: inventoryMap.keySet()){
      listString += s + " " + Integer.toString(inventoryMap.get(s)) + "\n";
    }
    
    return listString;
  }
}