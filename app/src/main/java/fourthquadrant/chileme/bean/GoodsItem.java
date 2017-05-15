package fourthquadrant.chileme.bean;

import java.util.ArrayList;
import java.util.Random;

import fourthquadrant.chileme.bean.GlobalValue;

/**
 * Created by Euphoria on 2017/4/3.
 */

public class GoodsItem {
    public int id;
    public String imgUrl;
    public String name;
    public double price;
    public int count;

    public GoodsItem(int id, double price, String name,String imgUrl) {
        this.id = id;
        this.price = price;
        this.name = name;
        this.imgUrl =imgUrl;
    }

}