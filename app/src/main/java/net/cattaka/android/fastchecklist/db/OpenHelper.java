package net.cattaka.android.fastchecklist.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import net.cattaka.android.fastchecklist.FastCheckListConstants;
import net.cattaka.android.fastchecklist.R;
import net.cattaka.android.fastchecklist.exception.DbException;
import net.cattaka.android.fastchecklist.model.CheckListEntry;
import net.cattaka.android.fastchecklist.model.CheckListEntryCatHands;
import net.cattaka.android.fastchecklist.model.CheckListHistory;
import net.cattaka.android.fastchecklist.model.CheckListHistoryCatHands;
import net.cattaka.android.fastchecklist.model.CheckListItem;
import net.cattaka.android.fastchecklist.model.CheckListItemCatHands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OpenHelper extends SQLiteOpenHelper {
    public static final int VERSION = 1;
    private Context mContext;

    public OpenHelper(Context context) {
        this(context, FastCheckListConstants.DB_NAME);
    }

    public OpenHelper(Context context, String name) {
        super(context, name, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CheckListEntryCatHands.SQL_CREATE_TABLE);
        db.execSQL(CheckListItemCatHands.SQL_CREATE_TABLE);
        db.execSQL(CheckListHistoryCatHands.SQL_CREATE_TABLE);
        {
            Resources resources = mContext.getResources();
            CheckListEntry entry = new CheckListEntry();
            entry.setTitle(resources.getString(R.string.sample_title));
            entry.setItems(new ArrayList<CheckListItem>());
            String[] sampleItems = resources.getStringArray(R.array.sample_items);
            for (String sampleItem : sampleItems) {
                CheckListItem item = new CheckListItem();
                item.setLabel(sampleItem);
                entry.getItems().add(item);
            }
            registerEntry(db, entry);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        CheckListEntryCatHands.upgrade(db, oldVersion, newVersion);
        CheckListItemCatHands.upgrade(db, oldVersion, newVersion);
        CheckListHistoryCatHands.upgrade(db, oldVersion, newVersion);
    }

    public List<CheckListEntry> findEntry() throws DbException {
        SQLiteDatabase db = getReadableDatabase();
        try {
            return CheckListEntryCatHands.findOrderBySortAsc(db, 0);
        } finally {
            db.close();
        }
    }

    public CheckListEntry findEntry(Long id, boolean withItems)
            throws DbException {
        SQLiteDatabase db = getReadableDatabase();
        try {
            CheckListEntry entry = CheckListEntryCatHands.findById(db, id);
            if (entry != null && withItems) {
                List<CheckListItem> items = CheckListItemCatHands
                        .findByEntryIdOrderBySortAsc(db, 0, id);
                entry.setItems(items);
            }
            return entry;
        } finally {
            db.close();
        }
    }

    public boolean deleteEntry(Long id) {
        SQLiteDatabase db = getWritableDatabase();
        boolean flag = false;
        try {
            db.beginTransaction();
            db.delete(CheckListItemCatHands.TABLE_NAME, "entryId=?", new String[]{String.valueOf(id)});
            flag = (CheckListEntryCatHands.delete(db, id) == 1);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
        return flag;
    }

    private long getMaxSortNo(SQLiteDatabase db) {
        Long maxSort = 0L;
        List<CheckListEntry> entries = CheckListEntryCatHands.findByMaxSortNo(db);
        CheckListEntry entry = (entries.size() > 0) ? entries.get(0) : null;
        maxSort = (entry.getSort() != null) ? entry.getSort() : 0;
        return maxSort;
    }

    public void registerEntry(CheckListEntry entry) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            registerEntry(db, entry);
        } finally {
            db.close();
        }
    }
    public void registerEntry(SQLiteDatabase db, CheckListEntry entry) {
        try {
            db.beginTransaction();
            { // entryのinsert/update
                if (entry.getId() != null) {
                    CheckListEntryCatHands.update(db, entry);
                } else {
                    entry.setSort(getMaxSortNo(db) + 1);
                    CheckListEntryCatHands.insert(db, entry);
                }
            }
            { // 削除されたitemのdelete
                Set<Long> deletedIds = new HashSet<Long>();
                List<CheckListItem> oldItems = CheckListItemCatHands
                        .findByEntryIdOrderBySortAsc(db, 0, entry.getId());
                for (CheckListItem item : oldItems) {
                    deletedIds.add(item.getId());
                }
                if (entry.getItems() != null) {
                    for (CheckListItem item : entry.getItems()) {
                        if (item.getId() != null) {
                            deletedIds.remove(item.getId());
                        }
                    }
                }
                for (Long id : deletedIds) {
                    CheckListItemCatHands.delete(db, id);
                }
            }
            { // itemのinsert/update
                if (entry.getItems() != null) {
                    long sort = 1;
                    for (CheckListItem item : entry.getItems()) {
                        item.setEntryId(entry.getId());
                        item.setSort(sort);
                        if (item.getId() != null) {
                            CheckListItemCatHands.update(db, item);
                        } else {
                            CheckListItemCatHands.insert(db, item);
                        }
                        sort++;
                    }
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public void registerHistory(CheckListHistory history) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            {   // historyのinsert/update
                if (history.getId() != null) {
                    CheckListHistoryCatHands.update(db, history);
                } else {
                    CheckListHistoryCatHands.insert(db, history);
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
    public void swapEntriesSort(Long id1, Long id2) {
        SQLiteDatabase db = getWritableDatabase();
        try {
            db.beginTransaction();
            CheckListEntry entry1 = CheckListEntryCatHands.findById(db, id1);
            CheckListEntry entry2 = CheckListEntryCatHands.findById(db, id2);
            {
                ContentValues values = new ContentValues();
                values.put("sort", entry2.getSort());
                db.update(CheckListEntryCatHands.TABLE_NAME, values, "id=?", new String[]{String.valueOf(id1)});
            }
            {
                ContentValues values = new ContentValues();
                values.put("sort", entry1.getSort());
                db.update(CheckListEntryCatHands.TABLE_NAME, values, "id=?", new String[]{String.valueOf(id2)});
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }
}
