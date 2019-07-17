package com.byted.camp.todolist.db;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    static final int DATABASE_START_VERSION=1;//最初版本为1
    static final int DATABASE_VERRSION=1;//当前版本为1
    static final String DATABASE_NAME="todo";//数据库名称

    public static final String TABLE_NAME="Note";//表名

    public static final String id="id";
    public static final String date="date";
    public static final String state="state";
    public static final String content="content";

    static final String CREATE_TABLE_NOTE="create table "
        +TABLE_NAME+"("
        +id+" integer primary key autoincrement,"
        +date+" long,"
        +state+" int,"
        +content+" text)";//创建Note数据表


    public static final String WHERE_CLAUSE=id+"=?";



    // TODO 定义表结构和 SQL 语句常量

    private TodoContract() {
    }

}
