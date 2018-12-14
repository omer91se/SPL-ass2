package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseVehicleEvent implements Event {
    DeliveryVehicle Vehicle;

    public ReleaseVehicleEvent(DeliveryVehicle Vehicle) {
        this.Vehicle = Vehicle;
    }

    public DeliveryVehicle getVehicle() {
        return Vehicle;
    }
}
