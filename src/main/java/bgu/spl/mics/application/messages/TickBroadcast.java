package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private int tick;

    public TickBroadcast(){
        tick = 0;
    }

    public int getTick(){
        return tick;
    }

}
