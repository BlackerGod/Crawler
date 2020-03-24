package test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;
import java.util.HashMap;

public class GsonTest {


    static class TestGson{
        private int aaa;
        private int bbb;
    }
    public static void main(String[] args) {



        Gson gson = new GsonBuilder().create();
        // 键值转Json格式
//        HashMap<String,String> hashMap = new HashMap<>();
//        hashMap.put("name","张飞");
//        hashMap.put("type","肉坦");
//        hashMap.put("skill1","肉墙撞击");
//        hashMap.put("skill2","勇往直前");
//        String result = gson.toJson(hashMap);
//        System.out.println(result);

        // Json转换成普通类型
        String jsonString = "{\"aaa\":1,\"bbb\":2}";
        TestGson ts = gson.fromJson(jsonString,TestGson.class);
        System.out.println(ts.aaa);
        System.out.println(ts.bbb);

    }
}
