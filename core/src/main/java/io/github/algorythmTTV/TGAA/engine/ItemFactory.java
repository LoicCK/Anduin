package io.github.algorythmTTV.TGAA.engine;

import io.github.algorythmTTV.TGAA.engine.items.HealthPotion;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ItemFactory {
    public static final Map <String, Supplier<Object>> items = new HashMap<>();

    static {
        items.put("HealthPotion", HealthPotion::new);
    }

    public static Object createItem(String name) {
        Supplier<Object> supplier = items.get(name);
        if (supplier != null) {
            return supplier.get();
        }
        throw new IllegalArgumentException("Objet inconnu : " + name);
    }
}
