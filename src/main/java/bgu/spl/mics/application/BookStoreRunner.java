package bgu.spl.mics.application;


import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
            jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);
            System.out.println("json");
        }
        catch(FileNotFoundException e){}

        JsonArray arr = jsonObject.get("initialInventory").getAsJsonArray();
        BookInventoryInfo[] books = new BookInventoryInfo[arr.size()];
        for(int i = 0; i<books.length;i++){
            books[i] = new BookInventoryInfo();
        }

        System.out.println("json");



    }
}
