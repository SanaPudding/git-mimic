package gitlet;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Stage implements Serializable {

    private Map<String, String> addMaps;
    private Map<String, String> removeMaps;

    public Stage() {
        addMaps = new HashMap<>();
        removeMaps = new HashMap<>();
    }

    // Getters
    public String getAdd(String filename) {
        return addMaps.get(filename);
    }

    public String getRemove(String filename) {
        return removeMaps.get(filename);
    }

    public boolean isAddedIn(String filename) {
        return addMaps.containsKey(filename);
    }

    public boolean isRemovemedIn(String filename) {
        return removeMaps.containsKey(filename);
    }

    public boolean isStaged(String filename) {
        return addMaps.containsKey(filename) | removeMaps.containsKey(filename);
    }

    public boolean isEmpty() {
        return addMaps.isEmpty() && removeMaps.isEmpty();
    }

    public int getAddCount() {
        return addMaps.size();
    }

    public int getRemoveCount() {
        return removeMaps.size();
    }

    public Map<String, String> getAllAdds() {
        return addMaps;
    }

    public Map<String, String> getAllRemoves() {
        return removeMaps;
    }

    // Setters
    public void setAdd(String filename, String fileId) {
        addMaps.put(filename, fileId);
    }

    public void setRemove(String filename, String fileId) {
        removeMaps.put(filename, fileId);
    }

    public void clear() {
        addMaps.clear();
        removeMaps.clear();
    }

    // Remove Methods
    public void rmAdd(String filename) {
        addMaps.remove(filename);
    }

    public void rmRemove(String filename) {
        removeMaps.remove(filename);
    }
}
