package com.cn.jianshi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBManager {
    private DBHelper helper;
    private SQLiteDatabase db;
    private Map<String, Integer> map;

    public DBManager(Context context) {
        helper = new DBHelper(context);
        //因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0, mFactory);
        //所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
        db = helper.getWritableDatabase();
    }
    //刷身份证时插入一条新的记录,健康信息为空
    public void save(Person person, String mType) {
        db.beginTransaction();  //开始事务
        try {
            db.execSQL("INSERT INTO person VALUES( ?, ?, ?,?)" , new Object[]{person._id,person.date,person.time,person.tiwen});
        db.setTransactionSuccessful();  //设置事务成功完成
        } finally {
            db.endTransaction();    //结束事务
        }
    }
    //测量后更新数据
    public void updateInformation(Person person) {
        ContentValues cv = new ContentValues();
        cv.put("time",person.time);
        cv.put("wendu",person.tiwen);
        db.update("person", cv, "_id = ?", new String[]{String.valueOf(person._id)});
    }
    public List<Person> queryMany(String sfid) {
        ArrayList<Person> persons = new ArrayList<Person>();
        Cursor c = db.rawQuery("select * from person where date = ?", new String[]{sfid});
        Person person;
        while (c.moveToNext()) {
            person = new Person();
            person.time = c.getString(c.getColumnIndex("time"));
            person.tiwen = c.getDouble(c.getColumnIndex("wendu"));
            persons.add(person);
        }
        c.close();
        //db.close();
        return persons;
    }
    public List<String> queryAllDate() {
        ArrayList<String> dateCollection = new ArrayList<String>();
        String temp = "";
        Cursor c = db.rawQuery("select * from person",null);
        c.moveToNext();
        temp = c.getString(c.getColumnIndex("date"));
        temp = temp.substring(0,4)+String.format("%02d",Integer.parseInt(temp.substring(4,6))-1)+temp.substring(6,8);
        dateCollection.add(temp);
        while (c.moveToNext()) {
            temp = c.getString(c.getColumnIndex("date"));
            temp = temp.substring(0,4)+String.format("%02d",Integer.parseInt(temp.substring(4,6))-1)+temp.substring(6,8);
            if(!dateCollection.contains(temp))
                dateCollection.add(temp);
        }
        c.close();
        //db.close();
        return dateCollection;
    }
    public long allCaseNum( ){
        String sql = "select count(*) from person";
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        return count;
    }
    public void closeDB() {
        db.close();
    }
}
