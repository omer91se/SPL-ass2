package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.DeliveryEvent;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

import java.util.concurrent.TimeUnit;

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
		subscribeEvent(DeliveryEvent.class, message -> {
			Future<DeliveryVehicle> futureObject = sendEvent(new TaviLiOtoEvent());
			if(futureObject != null) {
				DeliveryVehicle van = futureObject.get();
				van.deliver(message.getAddress(), message.getDistance());
				sendEvent(new KahOtotTodaEvent(van));

			}
			else{
				System.out.println("[" + getName() + "]: no resourceService handled that request. sorry. byeeeeeeeeeeeee");
			}
		});
		
	}

}
