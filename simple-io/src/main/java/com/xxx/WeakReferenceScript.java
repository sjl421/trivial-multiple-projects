package com.xxx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class WeakReferenceScript {

    private Set<Object> items = new HashSet<>();

    private void addItem(int itemId) {
        Item item = new Item(itemId);
//        items.add(item);
        WeakReference<Item> weakItem = new WeakReference<>(item);
        items.add(weakItem);
        System.out.println(items);
        System.out.println(items.size());
    }

    public static void main(String[] args) {
//        WeakReferenceScript instance = new WeakReferenceScript();
//        for (int i = 0; i < Integer.MAX_VALUE; i++) {
//            instance.addItem(i);
//        }
        try {
            String s = null;
            s.toString();
        } catch (Exception e) {
            final Logger logger = LoggerFactory.getLogger("");
            logger.error("", e);
        }
    }

    private static class Item {
        private final int id;
        private String memoryHolder = "";
        private Item(int id) {
            this.id = id;
            for (int i = 0; i < 1000; i++) {
                memoryHolder += "Hold the memory!!";
            }
        }
    }
}