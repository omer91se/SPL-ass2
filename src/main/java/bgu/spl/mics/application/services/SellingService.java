package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MoneyRegister;

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

	public SellingService(int count) {
		super("SellingService " + count);
		moneyRegister = MoneyRegister.getInstance();
	}

	@Override
	protected void initialize() {
		System.out.println(getName() + " started");
		subscribeEvent(BookOrderEvent.class, message -> {
			//TODO note that Inventory has checkAvalibiltyAndGetPrice,so make sure the Book and his price are returned by the InventoryService.
			//TODO make the credit check.
			//TODO get a car from ResourceService.
			//TODO Send the car and the book to the LogisticService.
			//TODO check who do i get the receipt from
		});
	}

}
