package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private MoneyRegister moneyRegister;
	private int tick;
	private static AtomicInteger orderId = new AtomicInteger(1);

	public SellingService(int count) {
		super("SellingService " + count);
		moneyRegister = MoneyRegister.getInstance();
		tick = 1;
	}

	@Override
	protected void initialize() {
		System.out.println(getName() + " started");

		subscribeBroadcast(TickBroadcast.class, message->this.tick = message.getTick());

		subscribeEvent(BookOrderEvent.class, message -> {
			Integer price = sendBookAvailabilityAndGetPriceEvent(message);

			//price == -1 means that the book is not available.
			Boolean gotBook = null;
				if(price != null && price != -1) {
					synchronized (message.getCustomer()) {
						if (!message.getCustomer().charge(price)) {
							System.out.println("[" + getName() + "]:" + " Not enough money, terminating");
							return;
						}

						//customer has enough money to purchase the book.
						else {

							//The book is out of stock, return money to customer.
							if (!(gotBook = sendBookAcquireEvent(message))) {
								message.getCustomer().refund(price);
								return;
							}

						}
					}
				}

			if(gotBook != null && gotBook){
				sendLogisticEvent(message);
			}

			//Create the receipt
			int orderId = this.orderId.incrementAndGet();
			int customerId = message.getCustomer().getId();
			String bookTitle = message.getBookName();

			OrderReceipt receipt = new OrderReceipt(orderId,getName(),customerId,bookTitle,price,this.tick,message.getTick(),this.tick);
			moneyRegister.file(receipt);
			complete(message, receipt);
		});
	}


	/**
	 * Checks if the book exists in inventory, if so, return its price.
	 * @param message
	 * @return price of the book. null if the book does not exists in the inventory.
	 */
	private Integer sendBookAvailabilityAndGetPriceEvent(BookOrderEvent message) {
		Integer price = null;
		Future<Integer> bookAvailNPriceFuture = (Future<Integer>) sendEvent(new AvailabilityAndPriceEvent(message.getBookName()));
		if (bookAvailNPriceFuture != null) {
			price = bookAvailNPriceFuture.get(100, TimeUnit.MILLISECONDS);
		}
		return price;
	}

	/**
	 * send a request to take the book. if it is no longer available, return false.
	 * @param message
	 * @return
	 */
	private Boolean sendBookAcquireEvent(BookOrderEvent message) {
		Boolean bookAcquired = false;
		Future<Boolean> bookAcquiredFuture = (Future<Boolean>) sendEvent(new AcquireBookEvent(message.getBookName()));
		if (bookAcquiredFuture != null) {
			bookAcquired = bookAcquiredFuture.get(100, TimeUnit.MILLISECONDS);

		}
		else{
			System.out.println("[" + getName() + "]: time has elapsed, no InventoryService handled request");
		}
		return bookAcquired;
	}

	/**
	 * Send a {@link DeliveryEvent} with the distance of the customer
	 * @param message
	 */
	private void sendLogisticEvent(BookOrderEvent message) {
		sendEvent(new DeliveryEvent(message.getCustomer().getAddress(), message.getCustomer().getDistance()));
	}





}
