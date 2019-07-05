package com.byted.camp.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.ContactsContract;
import android.util.Log;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class TodoDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION =2;
    public static final String DATABASE_NAME="todolist.db";

    // TODO 定义数据库名、版本；创建数据库

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TodoContract.SQL_CREATTE_ENTRIES);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int i=0;
        for( i = oldVersion;i<newVersion;i++){
            switch (i){
                case 1:{
                    db.execSQL("ALTER TABLE "+TodoContract.TABLE_NAME
                    +" ADD "+TodoContract.COLUMN_NAME_PRIVORITY+" int ");
                    break;
                }
                default:
                    break;
            }

        }

    }

}
