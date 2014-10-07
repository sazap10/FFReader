package de.sachinpan.ffreader;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by sachin on 23/01/14.
 */
public class SharedObjectStorage {
    private final Random random = new Random(Calendar.getInstance().getTimeInMillis());
    private HashMap<Long, WeakReference<Object>> sharedObjects = new HashMap<Long, WeakReference<Object>>();

    public synchronized Long putSharedObject(Object o) {
        long key;
        do {
            key = random.nextLong();
        } while (sharedObjects.containsKey(key));

        sharedObjects.put(key, new WeakReference<Object>(o));
        return key;
    }

    public synchronized Object getSharedObject(long key) {
        if (sharedObjects.containsKey(key)) {
            return sharedObjects.get(key).get();
        }
        return null;
    }
}