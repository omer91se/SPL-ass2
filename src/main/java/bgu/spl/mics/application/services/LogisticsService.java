package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {


	public LogisticsService(int count) {
		super("LogisticsService" + count);

	}

	@Override
	protected void initialize() {
		//System.out.println(getName() + " started");

		subscribeBroadcast(TickBroadcast.class, message->{
			if(message.getLastTick() == message.getTick()) {
				terminate();
				System.out.println("[" + getName() + "]: Terminating Gracefully! Thread- " + Thread.currentThread().getId() + "::: " + ter.incrementAndGet());
			}
		});

		subscribeEvent(DeliveryEvent.class, message -> {

			//Send an event to ResourceService to acquire a vehicle.
			//System.out.println("[" + getName() + "]: Sending AcquireVehicleEvent" );
			Future<Future<DeliveryVehicle>> futureObject = sendEvent(new AcquireVehicleEvent());
			if(futureObject != null) {

				Future<DeliveryVehicle> futureVan = futureObject.get();



				//Wait for the future that holds the DeliveryVehicle to be resolved and deliver the Book.
				//System.out.println("[" + getName() + "]: Waiting for the vehicle" );
				DeliveryVehicle van = futureVan.get();
				//System.out.println("[" + getName() + "]: Got the vehicle" );
				van.deliver(message.getAddress(), message.getDistance());

				//Send an event to ResourceService to release the vehicle.
				sendEvent(new ReleaseVehicleEvent(van));

			}
			else{
				System.out.println("[" + getName() + "]: no resourceService handled that request. sorry. byeeeeeeeeeeeee");
			}
			complete(message,futureObject);
		});
		
	}

}
