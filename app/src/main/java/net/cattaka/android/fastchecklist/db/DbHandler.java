package net.cattaka.android.fastchecklist.db;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.cattaka.android.fastchecklist.R;
import net.cattaka.android.fastchecklist.exception.DbException;
import net.cattaka.android.fastchecklist.model.CheckListEntry;
import net.cattaka.android.fastchecklist.model.CheckListEntryCatHands;
import net.cattaka.android.fastchecklist.model.CheckListHistory;
import net.cattaka.android.fastchecklist.model.CheckListHistoryCatHands;
import net.cattaka.android.fastchecklist.model.CheckListItem;
import net.cattaka.android.fastchecklist.model.CheckListItemCatHands;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbHandler {
    private OpenHelper openHelper;
    private SQLiteDatabase db;

    public DbHandler(Context context, String name, boolean checkFirstTime) {
        this(context, name);
        // 初回起動の場合はデータを作成する。
        if (checkFirstTime) {
            try {
                openWritableDatabase();
                if (openHelper.isFirstTime()) {
                    Resources resources = context.getResources();
                    CheckListEntry entry = new CheckListEntry();
                    entry.setTitle(resources.getString(R.string.sample_title));
                    entry.setItems(new ArrayList<CheckListItem>());
                    String[] sampleItems = resources.getStringArray(R.array.sample_items);
                    for (String sampleItem : sampleItems) {
                        CheckListItem item = new CheckListItem();
                        item.setLabel(sampleItem);
                        entry.getItems().add(item);
                    }
                    registerEntry(entry);
                }
            } finally {
                closeDatabase();
            }
        }
    }
    public DbHandler(Context context, String name) {
        openHelper = new OpenHelper(context, name);
    }

    public void openReadableDatabase() throws DbException {
        if (db != null) {
            throw new DbException("db is already opened.");
        }
        db = openHelper.getReadableDatabase();
    }

    public void openWritableDatabase() throws DbException {
        if (db != null) {
            throw new DbException("db is already opened.");
        }
        db = openHelper.getWritableDatabase();
    }

    public boolean isDatabaseOpened() {
        return (db != null);
    }

    public void closeDatabase() {
        if (db != null) {
            db.close();
            db = null;
        }
    }

    private void checkOpened(boolean writable) throws DbException {
        if (db == null) {
            throw new DbException("db is not opened.");
        } else if (writable && db.isReadOnly()) {
            throw new DbException("db is not writable.");
        }
    }

    public Cursor findEntryCursor() throws DbException {
        checkOpened(false);
        return CheckListEntryCatHands.findCursorOrderBySortAsc(db, 0);
    }

    public List<CheckListEntry> findEntry() throws DbException {
        checkOpened(false);
        return CheckListEntryCatHands.findOrderBySortAsc(db, 0);
    }

    public CheckListEntry findEntry(Long id, boolean withItems)
            throws DbException {
        checkOpened(false);
        CheckListEntry entry = CheckListEntryCatHands.findById(db, id);
        if (entry != null && withItems) {
            List<CheckListItem> items = CheckListItemCatHands
                    .findByEntryIdOrderBySortAsc(db, 0, id);
            entry.setItems(items);
        }
        return entry;
    }

    public boolean deleteEntry(Long id) {
        checkOpened(true);
        boolean flag = false;;
        try {
            db.beginTransaction();
            db.delete(CheckListItemCatHands.TABLE_NAME, "entryId=?", new String[]{String.valueOf(id)});
            flag = (CheckListEntryCatHands.delete(db, id) == 1);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return flag;
    }
    
    public long getMaxSortNo() {
        checkOpened(false);
        Long maxSort = 0L;
        Cursor cursor = null;
        try {
            cursor = db
                    .query(CheckListEntryCatHands.TABLE_NAME,
                            new String[] { "max(sort) sort" }, null, null, null,
                            null, null);
            if (cursor.moveToNext()) {
                maxSort = cursor.getLong(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return maxSort;
    }

    public void registerEntry(CheckListEntry entry) {
        checkOpened(true);
        try {
            db.beginTransaction();
            { // entryのinsert/update
                if (entry.getId() != null) {
                    CheckListEntryCatHands.update(db, entry);
                } else {
                    entry.setSort(getMaxSortNo() + 1);
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
        checkOpened(true);
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
        }
    }
    public void swapEntriesSort(Long id1, Long id2) {
        checkOpened(true);
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
        }
    }
}
