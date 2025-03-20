package io.github.algorythmTTV.TGAA.engine;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemList {
    private HashMap<String, ArrayList<Item>> map;

    public ItemList() {
        map = new HashMap<>();
    }

    public void addItem(Item item) {
        if (!map.containsKey(item.getName())) {
            map.put(item.getName(), new ArrayList<>());
        }
        map.get(item.getName()).add(item);
    }

    public Item takeItem(String name) {
        if (map.containsKey(name)) {
            Item vItem = map.get(name).get(0);
            map.get(name).remove(0);
            if (map.get(name).isEmpty()) {
                map.remove(name);
            }
            return vItem;
        }
        return null;
    }

    public HashMap<String, ArrayList<Item>> getMap() {
        return map;
    }

    @Override
    public String toString() {
        return map.toString();
    }
}
