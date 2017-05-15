package fourthquadrant.chileme.bean;

import android.text.format.Time;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Euphoria on 2017/4/3.
 */

public class GlobalValue {
    public static String baseUrl="http://192.168.137.1:8080/Chileme/";
    public static ArrayList<String> shop_names = new ArrayList<>();
    public static ArrayList<String> shop_intro = new ArrayList<>();
    public static ArrayList<String> shop_imgUrl = new ArrayList<>();

    public static ArrayList<Integer> order_shop_num = new ArrayList<>();
    public static ArrayList<Double> order_total = new ArrayList<>();
    public static ArrayList<String> order_time = new ArrayList<>();

    public static Map<String, Integer> goods_num_map = new HashMap<>();
    public static ArrayList<String> goods_name = new ArrayList<>();
    public static ArrayList<Double> goods_price = new ArrayList<>();
    public static ArrayList<String> goods_imgUrl = new ArrayList<>();
    public static ArrayList<Integer> goods_shop = new ArrayList<>();
    public static ArrayList<String> goods_num = new ArrayList<>();
    public static ArrayList<Integer> favourite_num = new ArrayList<>();
    public static ArrayList<Map<String, Object>> order_list = new ArrayList<>();

}
