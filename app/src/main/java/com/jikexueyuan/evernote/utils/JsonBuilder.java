package com.jikexueyuan.evernote.utils;

import com.google.gson.Gson;
import com.jikexueyuan.evernote.model.Entity;

import java.util.HashMap;
import java.util.List;

public class JsonBuilder {

    public static String buildJson(List<Entity> list,String usernname) {
        Gson gson = new Gson();
        HashMap map = new HashMap();
        map.clear();
        map.put("list", list);
        map.put("username",usernname);
        return gson.toJson(map);
    }
}
