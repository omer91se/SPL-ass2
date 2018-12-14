package bgu.spl.mics.application.passiveObjects;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Passive data-object representing the store inventory.
 * It holds a collection of {@link BookInventoryInfo} for all the
 * books in the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Inventory {

	private static class SingletonHolder {
		private static Inventory instance = new Inventory();
	}

	private Collection<BookInventoryInfo> books;


	private Inventory(){
		books = new LinkedList<>();
	}

	/**
     * Retrieves the single instance of this class.
     */
	public static Inventory getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
     * Initializes the store inventory. This method adds all the items given to the store
     * inventory.
     * <p>
     * @param inventory 	Data structure containing all data necessary for initialization
     * 						of the inventory.
     */
	public void load (BookInventoryInfo[] inventory ) {
		for(BookInventoryInfo bookInfo : inventory){
			books.add(bookInfo);
		}
		
	}
	
	/**
     * Attempts to take one book from the store.
     * <p>
     * @param book 		Name of the book to take from the store
     * @return 	an {@link Enum} with options NOT_IN_STOCK and SUCCESSFULLY_TAKEN.
     * 			The first should not change the state of the inventory while the 
     * 			second should reduce by one the number of books of the desired type.
     */
	public OrderResult take(String book) {
		BookInventoryInfo bookInfo = checkAvailabilityAndGetBook(book);
		if(bookInfo != null && bookInfo.takeBook()){
			return OrderResult.SUCCESSFULLY_TAKEN;
		}
		return OrderResult.NOT_IN_STOCK;
	}
	
	
	
	/**
     * Checks if a certain book is available in the inventory.
     * <p>
     * @param book 		Name of the book.
     * @return the price of the book if it is available, -1 otherwise.
     */
	public int checkAvailabiltyAndGetPrice(String book) {
		BookInventoryInfo bookInfo = checkAvailabilityAndGetBook(book);
		if(bookInfo != null){
			return bookInfo.getPrice();
		}
		return -1;
	}
	
	/**
     * 
     * <p>
     * Prints to a file name @filename a serialized object HashMap<String,Integer> which is a Map of all the books in the inventory. The keys of the Map (type {@link String})
     * should be the titles of the books while the values (type {@link Integer}) should be
     * their respective available amount in the inventory. 
     * This method is called by the main method in order to generate the output.
     */
	public void printInventoryToFile(String filename){
		//TODO: Implement this later:/
	}

	/**
	 *
	 *
	 * @param book
	 * @return the {@BookInventoryInfo} if available. null if not.
	 */
	private BookInventoryInfo checkAvailabilityAndGetBook(String book){
		for(BookInventoryInfo bookInfo : books){
			if(bookInfo.getBookTitle().equals(book)){
				if(bookInfo.getAmountInInventory() > 0){
					return bookInfo;
				} else break;
			}
		}
		return null;
	}
}
