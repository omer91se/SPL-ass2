package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int tick;
    private int lastTick;

    public TickBroadcast(int tick,int lastTick){
        this.tick = tick;
        this.lastTick = lastTick;
    }

    public int getTick(){
        return tick;
    }

    public int getLastTick() { return lastTick; }
}
