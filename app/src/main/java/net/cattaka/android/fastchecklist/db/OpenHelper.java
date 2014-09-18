package net.cattaka.android.fastchecklist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.cattaka.android.fastchecklist.model.CheckListEntryCatHands;
import net.cattaka.android.fastchecklist.model.CheckListHistoryCatHands;
import net.cattaka.android.fastchecklist.model.CheckListItemCatHands;

class OpenHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    
    private boolean firstTime = false;
    
    public OpenHelper(Context context, String name) {
        super(context, name, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CheckListEntryCatHands.SQL_CREATE_TABLE);
        db.execSQL(CheckListItemCatHands.SQL_CREATE_TABLE);
        db.execSQL(CheckListHistoryCatHands.SQL_CREATE_TABLE);
        firstTime = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CheckListEntryCatHands.upgrade(db, oldVersion, newVersion);
        CheckListItemCatHands.upgrade(db, oldVersion, newVersion);
        CheckListHistoryCatHands.upgrade(db, oldVersion, newVersion);
    }

    public boolean isFirstTime() {
        return firstTime;
    }
}
