package com.dotincorpkr.englidot;

import java.util.ArrayList;

/**
 * Created by wjddk on 2017-02-06.
 */
public class Singleton {
    private static Singleton ourInstance = new Singleton();
    ArrayList <TagData> Singleton_List = new ArrayList<>();

    public static Singleton getInstance() {
        return ourInstance;
    }

    private Singleton() {
    }

    public ArrayList<TagData> getSingleton_List() {
        return Singleton_List;
    }

    public void setSingleton_List(ArrayList<TagData> singleton_List) {
        Singleton_List = singleton_List;
    }
}
