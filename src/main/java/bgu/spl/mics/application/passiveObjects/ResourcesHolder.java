package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {

	private static class SingletonHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	private Queue<Future<DeliveryVehicle>> futureQueue;
	private Queue<DeliveryVehicle> vehicles;

	private ResourcesHolder(){
		vehicles = new LinkedList<>();
		futureQueue = new LinkedList<>();
	}


	/**
     * Retrieves the single instance of this class.
     */
	public static ResourcesHolder getInstance() {
		return SingletonHolder.instance;
	}
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	//Synchronized to avoid more than one thread trying to remove a vehicle while there is only one vehicle in the queue.
	public synchronized Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> future = new Future<>();
		if(!vehicles.isEmpty()) {
			DeliveryVehicle van = vehicles.remove();
			future.resolve(van);
		}
		else
			futureQueue.add(future);
		return future;
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		//Synchronized to avoid more than one thread trying to remove a future while there is only one future in the queue
		synchronized (futureQueue) {
			if (!futureQueue.isEmpty())
				futureQueue.remove().resolve(vehicle);
			else
				this.vehicles.add(vehicle);
		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for(DeliveryVehicle van : vehicles)
			this.vehicles.add(van);

	}

}
