package bgu.spl.mics.application;


import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        Gson gson = new Gson();

        //TODO check where to take file path from
        File jsonFile = Paths.get(args[0]).toFile();
        Inventory inv = Inventory.getInstance();
        JsonObject jsonObject = null;
        try {
            //Read the whole json file
            jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);


            //Get the booksInfo list from the json.
            JsonArray arr = jsonObject.get("initialInventory").getAsJsonArray();
            BookInventoryInfo[] bookInventoryInfoArray = new BookInventoryInfo[arr.size()];

            int i = 0;

            //Iterate over the booksInfo and add them into the BookInventoryInfo array
            for(JsonElement bookInfo : arr){
                JsonObject book = bookInfo.getAsJsonObject();
                String bookName;
                int amount;
                int price;

                bookName = book.get("bookTitle").getAsString();
                amount = book.get("amount").getAsInt();
                price = book.get("price").getAsInt();

                bookInventoryInfoArray[i] = new BookInventoryInfo(bookName,amount,price);

            }
            inv.load(bookInventoryInfoArray);
        }
        catch(FileNotFoundException e){}






    }
}
