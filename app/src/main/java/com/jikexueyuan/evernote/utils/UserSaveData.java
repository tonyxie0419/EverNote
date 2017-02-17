package com.jikexueyuan.evernote.utils;

import android.os.AsyncTask;

import com.jikexueyuan.evernote.model.Entity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;

/**
 * 网络存储
 */
public class UserSaveData extends AsyncTask<Void, Void, Void> {

    private List<Entity> list;
    private String username;
    private static final String HOST = "192.168.1.106";

    public UserSaveData(List<Entity> list, String username) {
        this.list = list;
        this.username = username;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            System.out.println("doInBackground");
            Socket socket = new Socket(HOST, 8888);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            if (socket.isConnected()) {
//                System.out.println("connect");
                String data = JsonBuilder.buildJson(list, username);
                writer.write(data + "\n");
                writer.flush();
                writer.close();
                Thread.sleep(1000);
                socket.close();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
