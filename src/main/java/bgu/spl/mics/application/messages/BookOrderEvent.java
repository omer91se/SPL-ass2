package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;

public class BookOrderEvent implements Event {
    private String bookName;

    public BookOrderEvent(String bookName){
        this.bookName = bookName;
    }


}
