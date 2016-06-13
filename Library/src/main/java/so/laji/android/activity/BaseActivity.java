package so.laji.android.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import so.laji.android.utils.DeviceTool;
import so.laji.android.view.SystemBarTintManager;

/**
 * 基础Activity
 * Created by folie on 16/3/18.
 */
public class BaseActivity extends AppCompatActivity {

    protected int screenWidth, screenHeight;

    protected SystemBarTintManager tintManager;

    protected Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        tintManager = new SystemBarTintManager(this);
    }

    /**
     * 设置状态栏样式与高度
     *
     * @param colorRes 顶部状态栏背景色
     * @param topBar   顶部状态栏下的view
     * @param darkMode 黑 true,白 false
     */
    protected void setTopBarHeight(int colorRes, View topBar, boolean darkMode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(colorRes);  //设置上方状态栏的颜色
            if (topBar != null) {
                if (topBar.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) topBar.getLayoutParams();
                    params.topMargin = DeviceTool.getStatusHeight(this);
                    topBar.setLayoutParams(params);
                } else if (topBar.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topBar.getLayoutParams();
                    params.topMargin = DeviceTool.getStatusHeight(this);
                    topBar.setLayoutParams(params);
                }
            }
            setMiuiStatusBarDarkMode(this, darkMode);
            setMeizuStatusBarDarkIcon(this, darkMode);
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
     * 设置小米的系统栏
     *
     * @param activity
     * @param dark
     * @return
     */
    private boolean setMiuiStatusBarDarkMode(Activity activity, boolean dark) {
        Class<? extends Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(activity.getWindow(), dark ? darkModeFlag : 0, darkModeFlag);
            return true;
        } catch (Exception e) {
        }
        return false;
    }


    /**
     * 设置魅族的系统栏
     *
     * @param activity
     * @param dark
     * @return
     */
    private boolean setMeizuStatusBarDarkIcon(Activity activity, boolean dark) {
        boolean result = false;
        if (activity != null) {
            try {
                WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
                Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
                Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
                darkFlag.setAccessible(true);
                meizuFlags.setAccessible(true);
                int bit = darkFlag.getInt(null);
                int value = meizuFlags.getInt(lp);
                if (dark) {
                    value |= bit;
                } else {
                    value &= ~bit;
                }
                meizuFlags.setInt(lp, value);
                activity.getWindow().setAttributes(lp);
                result = true;
            } catch (Exception e) {
            }
        }
        return result;
    }
}
