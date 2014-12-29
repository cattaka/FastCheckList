package net.cattaka.android.fastchecklist.test;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;

import net.cattaka.android.fastchecklist.test.R;

/**
 * Created by cattaka on 14/11/29.
 */
public class UnlockKeyguardActivity extends Activity {
    private static Handler sHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_keyguard);

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkUnlocked();
            }
        }, 100);
    }

    private void checkUnlocked() {
        KeyguardManager km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (!km.inKeyguardRestrictedInputMode() && pm.isScreenOn()) {
            finish();
        } else {
            sHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkUnlocked();
                }
            }, 100);
        }
    }
}
