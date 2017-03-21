/*
 * Group EIDs:
 * mtv364
 * raz354
 * rl26589
 * 
 */

public class Order{

    private int orderID;
    private String username; 
    private String productName; 
    private int quantity;

    public Order(int orderID, String username, String productName, int quantity){
        this.orderID = orderID;
        this.username = username;
        this.productName = productName;
        this.quantity = quantity;
    }

    public int getOrderID(){
        return this.orderID;
    }

    public String getUsername(){
        return this.username;
    }

    public String getProductName(){
        return this.productName;
    }

    public int getQuantity(){
        return this.quantity;
    }

    //<order-id> <user-name> <product-name> <quantity>
    public String toString(){
        return Integer.toString(orderID) + " " + username + " " + productName + " " + Integer.toString(quantity);
    }
}