package bgu.spl.mics;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import bgu.spl.mics.application.messages.TickBroadcast;
import javafx.util.Pair;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {

	private static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	private ConcurrentMap<MicroService,ConcurrentLinkedQueue<Message>> microServiceQueueList;
	private ConcurrentMap<Class,Pair<LinkedList<ConcurrentLinkedQueue<Message>>,ConcurrentLinkedQueue<Message>>> eventList;
	private ConcurrentMap<Class,LinkedList<ConcurrentLinkedQueue<Message>>> broadcastList;
	private ConcurrentMap<Message,Future> eventFutureMap;
	private Object lockEvent;
	private Object lockBroadcast;


	private MessageBusImpl(){
		microServiceQueueList = new ConcurrentHashMap<>();
		eventList = new ConcurrentHashMap<>();
		broadcastList = new ConcurrentHashMap<>();
		eventFutureMap = new ConcurrentHashMap<>();
		lockEvent = new Object();
		lockBroadcast = new Object();
	}

	/**
	 * Checks if the singleton instance is constructed, if not call its private constructor.
	 * <p>
	 * @return The instance of the {@link MessageBusImpl}.
	 */
	public static MessageBusImpl getInstance(){
		return SingletonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {

		//If there is a MicroService that is already subscribed to type {@param type},
		//find the list of MicroServices queues of type {@param type}.
		//and insert m's queue to that list.
		synchronized (this) {
			if (eventList.containsKey(type)) {
				ConcurrentLinkedQueue<Message> mQueue = microServiceQueueList.get(m);
				eventList.get(type).getKey().add(mQueue);
			} else {
				LinkedList<ConcurrentLinkedQueue<Message>> list = new LinkedList<>();
				list.add(microServiceQueueList.get(m));

				//Create a new pair of the list above and the queue of m.
				Pair<LinkedList<ConcurrentLinkedQueue<Message>>, ConcurrentLinkedQueue<Message>> pair = new Pair<>(list, microServiceQueueList.get(m));

				eventList.put(type, pair);
			}
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {

		//If there is a MicroService that is already subscribed to type {@param type},
		//find the list of MicroServices queues of type {@param type}.
		//and insert m's queue to that list.
		synchronized (this) {
			if (broadcastList.containsKey(type)) {
				ConcurrentLinkedQueue<Message> mQueue = microServiceQueueList.get(m);
				broadcastList.get(type).add(mQueue);
			}
			else {
				LinkedList<ConcurrentLinkedQueue<Message>> list = new LinkedList<>();
				list.add(microServiceQueueList.get(m));

				broadcastList.put(type, list);
			}
		}
	}


	@Override
	public <T> void complete(Event<T> e, T result) {

		//resolves e's Future with result.
		eventFutureMap.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {

		//Check if there are MicroServices register to b's type.
		if(broadcastList.containsKey(b.getClass()))
			//Insert b to all the MicroServices that are register to b's type.
			for (ConcurrentLinkedQueue<Message> q : broadcastList.get(b.getClass())) {
				q.add(b);
			}

		//TODO sync is only because of notifyAll()
		synchronized (this) {
			notifyAll();
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		synchronized (this) {

			if (eventList.containsKey(e.getClass())) {
				LinkedList<ConcurrentLinkedQueue<Message>> list = eventList.get(e.getClass()).getKey();
				ConcurrentLinkedQueue<Message> roundRobinQueue = eventList.get(e.getClass()).getValue();

				Pair<LinkedList<ConcurrentLinkedQueue<Message>>, ConcurrentLinkedQueue<Message>> pair = new Pair<>(list, robinHood(list, roundRobinQueue));
				//add e to the relevant(by round-robin) MicroService's queue.
				roundRobinQueue.add(e);

				//Updates the relevant MicrosService's queue.
				eventList.remove(e.getClass());
				eventList.put(e.getClass(), pair);

				Future<T> f = new Future<>();
				eventFutureMap.put(e, f);
				notifyAll();
				return f;
			}
		}
		return null;
	}

	@Override
	public void register(MicroService m) {

		//Create a new queue for m and insert it to microServiceQueueList.
		ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();
		microServiceQueueList.put(m,queue);
	}

	@Override
	public void unregister(MicroService m) {
		ConcurrentLinkedQueue<Message> mQueue = microServiceQueueList.get(m);

		//Removes m's queue from the broadcastList.
		for(Map.Entry<Class,LinkedList<ConcurrentLinkedQueue<Message>>> entry : broadcastList.entrySet()){
			entry.getValue().remove(mQueue);
		}
		//Removes m and m's queue from microServiceQueueList.
		microServiceQueueList.remove(m);

		synchronized(this) {

			//Iterates over eventList and removes mQueue from the relevant lists.
			for (Map.Entry<Class, Pair<LinkedList<ConcurrentLinkedQueue<Message>>, ConcurrentLinkedQueue<Message>>> entry : eventList.entrySet()) {
				//A lock is needed here because the queue that indicates the next iteration in round robin
				//is a shared object between different threads.


				//Checks if mQueue is the next relevant queue in round robin.
				if (entry.getValue().getValue() == mQueue) {

					//Creates a new pair with the next queue in round robin method.
					Pair<LinkedList<ConcurrentLinkedQueue<Message>>, ConcurrentLinkedQueue<Message>> p = new Pair<>(entry.getValue().getKey(), robinHood(entry.getValue().getKey(), mQueue));

					//Updates the eventList with the new pair
					entry.setValue(p);

				}

				//Removes m's queue from event queue list if it is there.
				ConcurrentLinkedQueue<Message> queue;
				for(Iterator<ConcurrentLinkedQueue<Message>> iterator = entry.getValue().getKey().iterator(); iterator.hasNext();){
					queue = iterator.next();
					if(queue == mQueue)
						iterator.remove();
				}
			}

		}

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {

		//TODO sync is only because of wait
		synchronized(this) {
			//If m's message queue is empty, wait, until notified.
			while (microServiceQueueList.get(m).isEmpty()) {
				wait();
			}
			return microServiceQueueList.get(m).remove();
		}
	}


	/**
	 * Find the next MicroService's queue by Round-Robin manner.
	 * <p>
	 * @param list of events registered to a certain event type.
	 * @param q the last queue that got an event.
	 * @return the queue of the next microService in Round-Robin manner.
	 */
	private ConcurrentLinkedQueue<Message> robinHood(LinkedList<ConcurrentLinkedQueue<Message>> list ,ConcurrentLinkedQueue<Message> q) {
		synchronized (this) {

			//Create an iterator that points to q.
			Iterator<ConcurrentLinkedQueue<Message>> qIterator = list.iterator();
			ConcurrentLinkedQueue<Message> queue;
			while(qIterator.hasNext()) {
				queue = qIterator.next();
				if(queue == q)
					break;
			}
			if (qIterator.hasNext())
				return qIterator.next();
			else
				return list.getFirst();
		}
	}


}
