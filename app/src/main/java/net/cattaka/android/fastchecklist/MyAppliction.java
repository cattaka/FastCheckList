package net.cattaka.android.fastchecklist;

import android.app.Application;
import android.content.Context;

/**
 * Created by cattaka on 14/09/27.
 */
public class MyAppliction extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // SQLiteにアクセするためのサーバーを起動する（開発時専用）
        try {
            Class<?> clazz = Class.forName("net.cattaka.telnetsqlite.TelnetSqliteService");
            Thread serverThread = (Thread)clazz.getMethod("createTelnetSqliteServer", Context.class, int.class).invoke(null, this, 12080);
            serverThread.start();
        } catch (Exception e) {
            // ignore
        }
    }
}
