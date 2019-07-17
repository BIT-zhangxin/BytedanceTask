package com.byted.camp.todolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public class TodoDbHelper extends SQLiteOpenHelper {



    // TODO 定义数据库名、版本；创建数据库

    public TodoDbHelper(Context context) {
        super(context,TodoContract.DATABASE_NAME, null, TodoContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(TodoContract.CREATE_TABLE_NOTE);
        }catch (SQLiteException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i=oldVersion;i<newVersion;i++){
            switch (i){
                case 1:
                    try {
                        db.execSQL(TodoContract.ALTER_TABLE_NOTE);
                    }catch (SQLiteException e){
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    break;
                default:
                    break;
            }
        }
    }

}
