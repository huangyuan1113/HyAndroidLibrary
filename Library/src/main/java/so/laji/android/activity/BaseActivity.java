package so.laji.android.activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import so.laji.android.utils.FlymeUtils;
import so.laji.android.utils.MIUIUtils;

/**
 * 基础Activity
 * Created by folie on 16/3/18.
 */
public class BaseActivity extends AppCompatActivity {

    protected int screenWidth, screenHeight;

    protected Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void setTranslucentStatus(boolean on) {
        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    /**
     * 设置状态栏样式与高度
     *
     * @param darkMode 黑 true,白 false
     */
    protected void setStatusBarFontColor(boolean darkMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (MIUIUtils.isMIUI()) {
                MIUIUtils.setStatusBarLightMode(getWindow(), darkMode);
            } else if (FlymeUtils.isFlyme()) {
                FlymeUtils.setStatusBarLightMode(getWindow(), darkMode);
            }
        }
    }

}
