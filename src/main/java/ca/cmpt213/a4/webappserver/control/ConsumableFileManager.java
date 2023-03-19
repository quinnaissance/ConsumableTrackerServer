package ca.cmpt213.a4.webappserver.control;

import ca.cmpt213.a4.webappserver.model.Consumable;

import java.io.*;
import java.util.List;

/**
 * Handles loading/saving json based on a provided ConsumableManager object
 * @author Steven Quinn (301462499) – CMPT 213 D100 – Fall 2021
 */
public class ConsumableFileManager {

    private String jsonFilename;
    private ConsumableManager conman;

    /**
     * Default constructor, ties it to a ConsumableManager
     * @param conman ConsumableManager object
     */
    public ConsumableFileManager(String jsonFilename, ConsumableManager conman) {
        this.jsonFilename = jsonFilename;
        this.conman = conman;
    }

    /**
     * Return the chosen name of the json file
     * @return String name
     */
    public String getJsonFilename() {
        return jsonFilename;
    }

    /**
     * Save ConsumableManager list to json file
     * Return true if successful save
     */
    public boolean saveJsonFile() {
        try {
            // Check for previous file
            File prev = new File(jsonFilename);
            if (prev.exists())
                prev.delete();

            // Write new json file
            FileWriter fw = new FileWriter(jsonFilename);
            fw.write(ConsumableManager.serializeConsumableList(conman.getConsumableList()));
            fw.close();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Load json and return parsed list
     * Return true if successful load
     */
    public boolean loadJsonFile() {

        // Load json and return parsed list
        File f = new File(jsonFilename);

        if (f.exists()) { // Parse into json
            try {
                BufferedReader br = new BufferedReader(new FileReader(f));

                // Create list from read json
                List<Consumable> newList = ConsumableManager.deserializeConsumableList(br.readLine());

                // Check for null objects
                if (newList.contains(null))
                    return false;

                // Only set new list if non-empty
                if (newList.size() > 0)
                    conman.setConsumableList(newList);

                // True for valid list (may still be empty)
                return true;

            } catch (IOException e) { // Should be unreachable
                e.printStackTrace();
            }
        }
        return false;
    }
}
