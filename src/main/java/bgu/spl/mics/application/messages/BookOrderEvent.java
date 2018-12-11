package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent implements Event {
    private String bookName;
    private Customer customer;
    private int tick;

    public BookOrderEvent(String bookName, Customer customer,int tick){
        this.bookName = bookName;
        this.customer = customer;
        this.tick = tick;
    }
    public String getBookName(){
        return bookName;
    }
    public Customer getCustomer(){
        return customer;
    }
    public int getTick(){
        return tick;
    }


}
