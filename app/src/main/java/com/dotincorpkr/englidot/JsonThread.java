package com.dotincorpkr.englidot;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by wjddk on 2017-02-06.
 */

public class JsonThread extends Thread {
    private String url;
    private OkHttpClient mClient;
    String base_url = "http://englidot.azurewebsites.net/";

    public JsonThread(String keyword) {
        this.url = base_url+keyword;
        mClient = new OkHttpClient();
    }

    @Override
    public void run() {
        super.run();

        try {
            Request request = new Request.Builder().url(url).build();
            Response response = mClient.newCall(request).execute();

            while (true) {
                if (response.isSuccessful()) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    msg.obj = response.body().string();
                    jHandler.sendMessage(msg);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler jHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                JsonElement root = new JsonParser().parse(msg.obj.toString()).getAsJsonObject().get("jsondata");

                ArrayList<TagData> TagData_List = new Gson().fromJson(root, new TypeToken<ArrayList<TagData>>() {
                }.getType());
                Singleton.getInstance().setSingleton_List(TagData_List);
            }
        }
    };

}
