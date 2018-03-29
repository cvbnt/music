package t.n.b.v.c.music;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 54654 on 2018/3/18.
 */

public class SqliteHelper extends SQLiteOpenHelper {
    private static final String CREATE_DATA="create table data("
            +"id integer primary key autoincrement,"
            +"username text,"
            +"password text)";
    public SqliteHelper(Context context) {
        super(context, "data.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
