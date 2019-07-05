package com.byted.camp.todolist.db;

import java.util.Date;

/**
 * Created on 2019/1/22.
 *
 * @author xuyingyi@bytedance.com (Yingyi Xu)
 */
public final class TodoContract {

    // TODO 定义表结构和 SQL 语句常量

    public static final String TABLE_NAME = "Notes";
    public static final String COLUMN_NAME_ID ="ID";
    public static final String COLUMN_NAME_DATE ="Date";
    public static final String COLUMN_NAME_STATE="State";
    public static final String COLUMN_NAME_Content="Content";
    public static final String COLUMN_NAME_PRIVORITY="Privority";

    public static final String SQL_CREATTE_ENTRIES=
            "CREATE TABLE "+TABLE_NAME+" ( "+
                    COLUMN_NAME_ID+" INTEGER PRIMARY KEY, "+
                    COLUMN_NAME_DATE+" long, "+
                    COLUMN_NAME_Content+ " text, "+
                    COLUMN_NAME_STATE+ " int, "+
                    COLUMN_NAME_PRIVORITY+ " int "+
                    ")";

    public static final String SQL_DELETE_ENTRIES=
            "DROP TABLE IF EXISTS Notes";

    private TodoContract() {
    }

}
