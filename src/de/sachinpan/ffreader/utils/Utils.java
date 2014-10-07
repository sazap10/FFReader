package de.sachinpan.ffreader.utils;

import android.content.Context;
import de.sachinpan.ffreader.SharedObjectStorage;

public class Utils {
	private static Context context;
	private static SharedObjectStorage storage;
	public final static String LINK = "link";
	public final static String CROSSOVER_FLAG = "crossover";
	public final static String TITLE = "title";
	public final static String BASE_URL = "https://www.fanfiction.net/";
	public final static String NO_CONNECTION = "No connection or\n Fanfiction.net is down";
	public final static String PULL_TO_REFRESH = "Please pull to refresh page";

    public static void setContext(Context mcontext) {
        if (context == null)
            context = mcontext;
    }

	public static SharedObjectStorage getObjectStorage(){
		if(storage == null){
			storage = new SharedObjectStorage();
		}
        return storage;
    }
}
