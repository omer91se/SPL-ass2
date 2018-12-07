package bgu.spl.mics.application;


import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import javafx.util.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) {
        Thread[][] threadArrays = new Thread[6][];
        parseAndInit(threadArrays,args[0]);

        //TODO close the threads.
    }

    /**
     * Create Service threads, starts them, and add them to the {@code threadArray}.
     * <p>
     * @param quantity of threads.
     * @param threadArray
     */
    public static void threadsInit(int quantity, Thread[] threadArray){
        threadArray = new Thread[quantity];
        Thread thread;
        for (int i = 1; i <= quantity; i++) {
            thread = new Thread(new LogisticsService(i));
            threadArray[i - 1] = thread;
            thread.start();
        }
    }

    /**
     *
     * Parse the json file and initialize:
     * Inventory,
     * ResourcesHolder,
     * and threads for each Service.
     *
     * <p>
     *
     * @param threadArrays hold the thread arrays for each type of service.
     * @param path of the json file.
     */
    public static void parseAndInit(Thread[][] threadArrays, String path){
        Gson gson = new Gson();
        File jsonFile = Paths.get(path).toFile();
        Inventory inv = Inventory.getInstance();
        ResourcesHolder holder = ResourcesHolder.getInstance();

        JsonObject jsonObject = null;
        try {
            //Read the whole json file
            jsonObject = gson.fromJson(new FileReader(jsonFile), JsonObject.class);


            //Get the booksInfo list from the json.
            JsonArray inventoryInitArray = jsonObject.get("initialInventory").getAsJsonArray();
            BookInventoryInfo[] bookInventoryInfoArray = new BookInventoryInfo[inventoryInitArray.size()];

            int i = 0;
            //Iterate over the booksInfo and add them into the BookInventoryInfo array
            for (JsonElement bookInfo : inventoryInitArray) {
                JsonObject book = bookInfo.getAsJsonObject();
                String bookName;
                int amount;
                int price;

                bookName = book.get("bookTitle").getAsString();
                amount = book.get("amount").getAsInt();
                price = book.get("price").getAsInt();

                bookInventoryInfoArray[i] = new BookInventoryInfo(bookName, amount, price);
                i++;
            }
            inv.load(bookInventoryInfoArray);

            JsonArray resourcesInitArray = jsonObject.get("initialInventory").getAsJsonArray();
            DeliveryVehicle[] deliveryVehicleArray = new DeliveryVehicle[resourcesInitArray.size()];

            i = 0;
            //Iterate over the booksInfo and add them into the BookInventoryInfo array
            for (JsonElement vehicleInfo : resourcesInitArray) {
                JsonObject vehicle = vehicleInfo.getAsJsonObject();
                int license;
                int speed;

                license = vehicle.get("license").getAsInt();
                speed = vehicle.get("speed").getAsInt();

                deliveryVehicleArray[i] = new DeliveryVehicle(license, speed);
                i++;
            }
            holder.load(deliveryVehicleArray);
            //TODO TIME Service.


            JsonObject servicesJson = jsonObject.get("services").getAsJsonObject();


            int servicesQuant;

            //Init SellingService Threads
            servicesQuant = servicesJson.get("selling").getAsInt();
            Thread[] sellingThreadsArray = new Thread[servicesQuant];
            threadsInit(servicesQuant,sellingThreadsArray);
            threadArrays[1] = sellingThreadsArray;

            //Init InventoryService Threads
            servicesQuant = servicesJson.get("inventoryService").getAsInt();
            Thread[] inventoryThreadsArray = new Thread[servicesQuant];
            threadsInit(servicesQuant,inventoryThreadsArray);
            threadArrays[2] = inventoryThreadsArray;

            //Init LogisticsService Threads
            servicesQuant = servicesJson.get("logistics").getAsInt();
            Thread[] logisticThreadsArray = new Thread[servicesQuant];
            threadsInit(servicesQuant,logisticThreadsArray);
            threadArrays[3] = logisticThreadsArray;

                    //Init ResourceService Threads
            servicesQuant = servicesJson.get("resourcesService").getAsInt();
            Thread[] resourceThreadsArray = new Thread[servicesQuant];
            threadsInit(servicesQuant,resourceThreadsArray);
            threadArrays[4] = resourceThreadsArray;

             //Init APIService Threads
            JsonArray customersJson = jsonObject.get("customers").getAsJsonArray();
            servicesQuant = customersJson.size();
            Customer[] customers = new Customer[servicesQuant];

            //Iterate over the customers info from json file, and create an array of customers.
            for (JsonElement customerInfo : customersJson) {
                JsonObject customerObj = customerInfo.getAsJsonObject();
                int id = customerObj.get("id").getAsInt();
                String name = customerObj.get("name").getAsString();
                String address = customerObj.get("address").getAsString();
                int distance = customerObj.get("distance").getAsInt();

                //Create creditCard info Pair.
                int creditNumber = customerObj.get("number").getAsInt();
                int creditAmount = customerObj.get("amount").getAsInt();
                Pair<Integer, Integer> creditCard = new Pair(creditNumber, creditAmount);

                //Create orderSchedule list.
                List<Pair<String, Integer>> orderScheduleList = new LinkedList<>();
                Pair<String, Integer> schedulePair;
                String bookTitle;
                int tick;
                JsonArray orderSchedule = customerObj.get("orderSchedule").getAsJsonArray();
                for (JsonElement order : orderSchedule) {
                    JsonObject orderObj = order.getAsJsonObject();
                    bookTitle = orderObj.get("bookTitle").getAsString();
                    tick = orderObj.get("tick").getAsInt();
                    schedulePair = new Pair(bookTitle, tick);
                    orderScheduleList.add(schedulePair);
                }

                for (i = 0; i < customers.length; i++)
                    customers[i] = new Customer(id, name, address, distance, creditCard, orderScheduleList);
            }

            //Create an APIService thread for each customer, starts it and put it into APIThreadsArray.
            Thread[] APIThreadsArray = new Thread[servicesQuant];
            Thread thread;
            i = 0;
            for(Customer c : customers){
                thread = new Thread(new APIService(i+1,c));
                APIThreadsArray[i] = thread;
                thread.start();
                i++;
            }

            threadArrays[5] = APIThreadsArray;


        } catch (FileNotFoundException e) {}
    }
}
