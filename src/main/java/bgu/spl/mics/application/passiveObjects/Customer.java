package bgu.spl.mics.application.passiveObjects;

import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer {
    private int maxTick;
	private int id;
    private String name;
    private String address;
    private int distance;
    private int cardNumber;
    private int credit;
    private List<Pair<String,Integer>> orderSchedule;
    private List<OrderReceipt> orderReceiptList;


	public Customer(int id, String name, String address, int distance, Pair<Integer,Integer> creditCard, List<Pair<String,Integer>> orderSchedule) {
	    this.id = id;
		this.name = name;
		this.address = address;
		this.distance = distance;
		this.orderSchedule = orderSchedule;
		this.credit = creditCard.getValue();
		this.cardNumber = creditCard.getKey();
		this.orderReceiptList = new LinkedList<>();

		findMaxTick();
	}
	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  . 
     */
	public int getId() {
		return id;
	}
	
	/**
     * Retrieves the address of the customer.  
     */
	public String getAddress() {
		return address;
	}
	
	/**
     * Retrieves the distance of the customer from the store.  
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return orderReceiptList;
	}

	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		return credit;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return cardNumber;
	}

	/**
	 * Reduce {@code amount} from {@code credit}.
	 * <p>
	 * @param amount
	 */
	public boolean charge(int amount){
		if(getAvailableCreditAmount() >= amount) {
			credit -= amount;
			return true;
		}
		return false;
	}

	public List<Pair<String,Integer>> getOrderSchedule(){
		return orderSchedule;
	}

    /**
     * Adds {@code receipt} to {@code orderReceiptList}.
     * <p>
     * @param receipt
     */
	public void addRecipt(OrderReceipt receipt) {
        this.orderReceiptList.add(receipt);
    }

    /**
     * Retrieves the latest tick order.
     */
    public int getMaxTick(){
	    return maxTick;
    }

    /**
     * Finds the latest tick order(largest Tick).
     */
    private void findMaxTick(){
	    int maxTick = 0;
	    for(Pair<String,Integer> schedulePair : this.orderSchedule ){
            if(schedulePair.getValue() > maxTick)
                maxTick = schedulePair.getValue();
        }
	    this.maxTick = maxTick;
    }


	/**
	 * adds {@code refundAmount} to the customer {@code credit}
	 * @param refundAmount to add.
	 */
	public void refund(Integer refundAmount) {
    	this.credit =+ refundAmount;
	}
}
