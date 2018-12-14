package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TickBroadcast;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private int speed;
	private int duration;
	private int tick;

	public TimeService(int speed, int duration) {
		super("TimeService");
		this.speed = speed;
		this.duration = duration;
		this.tick = 1;
	}

	@Override
	protected void initialize() {
		//System.out.println(getName() + " started");

		TimerTask task = new TimerTask() {
			public void run() {
				sendBroadcast(new TickBroadcast(tick,duration));
				tick++;
				if(tick == duration+1)
					cancel();
			}
		};
		Timer timer = new Timer("Timer");

		timer.scheduleAtFixedRate(task, 0, speed);
		System.out.println("[" + getName() + "]: Terminating Gracefully! Thread-" + Thread.currentThread().getId() + "::: " + ter.incrementAndGet());

		try{
		TimeUnit.MILLISECONDS.sleep(duration*speed);
		}
		catch(InterruptedException e){}

		timer.cancel();
		timer.purge();
		terminate();

	}

}
