package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;

public class AvailabilityAndPriceEvent implements Event {
    private String bookName;

    public AvailabilityAndPriceEvent(String bookName) {
        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }
}
