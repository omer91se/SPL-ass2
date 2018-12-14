package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.AcquireVehicleEvent;
import bgu.spl.mics.application.messages.ReleaseVehicleEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.ResourcesHolder;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourceHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService{
	ResourcesHolder resourcesHolder;

	public ResourceService(int count) {
		super("ResourceService" + count);
		resourcesHolder = ResourcesHolder.getInstance();
	}

	@Override
	protected void initialize() {
		//System.out.println(getName() + " started");

		subscribeBroadcast(TickBroadcast.class, message->{
			if(message.getLastTick() == message.getTick()) {
				terminate();
				System.out.println("[" + getName() + "]: Terminating Gracefully! Thread-" + Thread.currentThread().getId() + "::: " + ter.incrementAndGet());
			}
		});
		subscribeEvent(AcquireVehicleEvent.class, message -> {
			Future<DeliveryVehicle> futureAcquireVehicle = resourcesHolder.acquireVehicle();
			//System.out.println("[" + getName() + "]: Got the futureVehicle.");
			complete(message, futureAcquireVehicle);
		});

		subscribeEvent(ReleaseVehicleEvent.class, message -> resourcesHolder.releaseVehicle(message.getVehicle()));
	}


}
