package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireBookEvent;
import bgu.spl.mics.application.messages.AvailabilityAndPriceEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.OrderResult;


/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	Inventory inventory;

	public InventoryService(int count) {
		super("InventoryService" + count);
		inventory = Inventory.getInstance();

	}

	@Override
	protected void initialize() {
		//System.out.println(getName() + " started");

		subscribeBroadcast(TickBroadcast.class, message->{
			//System.out.println("[" + getName() + "]: got tick: " + message.getTick());
			if(message.getLastTick() == message.getTick()) {
				System.out.println("[" + getName() + "]: Terminating Gracefully! Thread-" + Thread.currentThread().getId() + "::: " + ter.incrementAndGet());
				terminate();
			}
		});

		// Handles the event AvailabilityAndPriceEvent with a relevant callback.
		subscribeEvent(AvailabilityAndPriceEvent.class, message->{
			Integer price = inventory.checkAvailabiltyAndGetPrice(message.getBookName());
			complete(message,price);
		});

		// Handles the event AcquireBookEvent with a relevant callback.
		subscribeEvent(AcquireBookEvent.class, message->{
			OrderResult order = inventory.take(message.getBookName());
			if(order == OrderResult.SUCCESSFULLY_TAKEN)
				complete(message, true);

			else
				complete(message, false);
		});

	}

}
