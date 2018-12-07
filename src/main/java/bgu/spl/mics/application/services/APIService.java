package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.BookOrderEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private Customer customer;

	public APIService(int count, Customer customer) {
		super("APIService " + count );

		if(count<0 | customer == null)
			throw new IllegalArgumentException("customer can not be null and count must be a positive number");

		this.customer = customer;
	}

	@Override
	protected void initialize() {
		System.out.println("Sender " + getName() + " started");
		subscribeBroadcast(TickBroadcast.class, message -> {
			List<Pair<String,Integer>> schedule = customer.getOrderSchedule();
			for(Pair<String,Integer> schedulePair : schedule){
				if(message.getTick() == schedulePair.getValue()){
					//TODO ORDER THE BOOK!!!! (Copy from the examples [: )
				}
			}


		});


	//	Future<OrderReceipt> futureObject = (Future<OrderReceipt>)sendEvent(new BookOrderEvent());

		
	}

}
