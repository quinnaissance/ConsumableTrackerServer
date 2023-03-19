package ca.cmpt213.a4.webappserver.controllers;

import ca.cmpt213.a4.webappserver.control.ConsumableFileManager;
import ca.cmpt213.a4.webappserver.control.ConsumableManager;

import ca.cmpt213.a4.webappserver.model.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ConsumableController {

    // Manage the consumable list
    private ConsumableManager cm = new ConsumableManager();
    private ConsumableFileManager cfm = new ConsumableFileManager("json_list.json", cm);

    /**
     * On startup, look for a json list and load it if it exists
     */
    @EventListener(ApplicationReadyEvent.class)
    public void loadJsonOnStartup() {

        // Attempt to load file
        boolean load = cfm.loadJsonFile();
    }

    /**
     * Check server status
     * @return
     */
    @GetMapping("/ping")
    public String getPing() {
        return "System is up!";
    }

    /**
     * List all items
     * @return JsonArray list
     */
    @GetMapping("/listAll")
    public JsonArray listAll() {
        String serialized = ConsumableManager.serializeConsumableList(cm.getConsumableList());

        return JsonParser.parseString(serialized).getAsJsonArray();
    }

    /**
     * List all expired items
     * @return JsonArray list
     */
    @GetMapping("/listExpired")
    public JsonArray listExpired() {
        List<Consumable> newList = ConsumableManager.getSublistWithExpiryCriteria(cm.getConsumableList(),
                ConsumableManager.ExpiryPeriod.EXPIRED);

        return JsonParser.parseString(ConsumableManager.serializeConsumableList(newList)).getAsJsonArray();
    }

    /**
     * List all non-expired items
     * @return JsonArray list
     */
    @GetMapping("/listNonExpired")
    public JsonArray listNonExpired() {
        List<Consumable> newList = ConsumableManager.getSublistWithExpiryCriteria(cm.getConsumableList(),
                ConsumableManager.ExpiryPeriod.UNEXPIRED);

        return JsonParser.parseString(ConsumableManager.serializeConsumableList(newList)).getAsJsonArray();
    }

    /**
     * List all items expiring within 7 days
     * @return JsonArray list
     */
    @GetMapping("/listExpiringIn7Days")
    public JsonArray listExpiringIn7Days() {
        List<Consumable> newList = ConsumableManager.getSublistWithExpiryCriteria(cm.getConsumableList(),
                ConsumableManager.ExpiryPeriod.EXPIRING_WITHIN_WEEK);

        return JsonParser.parseString(ConsumableManager.serializeConsumableList(newList)).getAsJsonArray();
    }

    /**
     * Add a Consumable item
     * @param serializedConsumable Serialized item
     * @return JsonObject
     */
    @PostMapping("/addItem")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonObject addItem(@RequestBody JsonObject serializedConsumable) {

        // Deserialize
        Consumable newCon = ConsumableManager.deserializeConsumableItem(serializedConsumable.toString());

        // Check that constructed Consumable is valid
        if (newCon != null) {
            cm.addToConsumableList(newCon);
            return serializedConsumable;
        } else {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid input");
        }

    }

    /**
     * Add a Food item
     * @param serializedConsumable Serialized item
     * @return JsonObject
     */
    @PostMapping("/addFood")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonObject addFood(@RequestBody JsonObject serializedConsumable) {
        return addItem(serializedConsumable);
    }

    /**
     * Add a Drink item
     * @param serializedConsumable Serialized item
     * @return JsonObject
     */
    @PostMapping("/addDrink")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonObject addDrink(@RequestBody JsonObject serializedConsumable) {
        return addItem(serializedConsumable);
    }

    /**
     * Remove item from list
     * @param serializedConsumable Serialized item
     * @return JsonObject
     */
    @PostMapping("/removeItem")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonArray removeItem(@RequestBody JsonObject serializedConsumable) {

        Consumable newCon = ConsumableManager.deserializeConsumableItem(serializedConsumable.toString());

        // Check that constructed Consumable is valid
        if (newCon != null) {

            // Match new object with something in the list
            if (cm.getConsumableList().contains(newCon)) {
                // Remove it
                cm.getConsumableList().remove(newCon);

                // Return updated list as JsonArray
                String listSerialized = ConsumableManager.serializeConsumableList(cm.getConsumableList());
                return JsonParser.parseString(listSerialized).getAsJsonArray();

            } else {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "List does not contain object in request");
            }

        } else { // Invalid json
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Invalid Consumable object");
        }

    }

    /**
     * Save list and exit program
     * @return
     */
    @GetMapping("/exit")
    public String exit() {
        // Save list
        boolean save = cfm.saveJsonFile();

        // If file was saved, return confirmation
        if (save) {
            return "List has been saved with " + cm.getConsumableList().size() + " item(s).";
        } else {
            // Unsuccessful save
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to save list");
        }
    }

    /**
     * Handle invalid json input
     */
    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Invalid json body")
    @ExceptionHandler(JsonSyntaxException.class)
    public void badJsonExceptionHandler() {
        // Do nothing (it automatically sends response status)
    }

}
