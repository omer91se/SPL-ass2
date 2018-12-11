package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class AcquireBookEvent implements Event {
    private String bookName;

    public AcquireBookEvent(String bookName){this.bookName = bookName;}

    public String getBookName(){return bookName; }
}
