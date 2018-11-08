package com.cn.jianshi;

/**
 * Created by Administrator on 2017/9/7.
 */

public class Person {
    public long _id;
    public String date,time;
    public double tiwen;
    //体温信息
    public Person(long _id, String date, String time,double tiwen) {
        this._id = _id;
        this.date = date;
        this.time = time;
        this.tiwen = tiwen;
    }
    public Person(){}

}
