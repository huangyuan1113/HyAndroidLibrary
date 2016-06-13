package so.laji.android.mvp;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import so.laji.android.logger.Logger;
import so.laji.android.utils.FlymeUtils;
import so.laji.android.utils.MIUIUtils;

/**
 * mvp 基类
 * Created by folie on 16/6/3.
 */
public abstract class BaseMvpActivity<V extends IView, T extends BasePresenter<V>> extends AppCompatActivity {

    public T presenter;
    protected Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);
        }
        presenter = initPresenter();


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
     * 设置状态栏字体颜色
     *
     * @param dark 是否把状态栏字体及图标颜色设置为深色(true 黑, false 白)
     */
    protected void setStatusBarFontColor(boolean dark) {
        if (MIUIUtils.isMIUI()) {
            Logger.d("小米系统");
            MIUIUtils.setStatusBarLightMode(getWindow(), dark);
        } else if (FlymeUtils.isFlyme()) {
            Logger.d("魅族系统");
            FlymeUtils.setStatusBarLightMode(getWindow(), dark);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.attachView((V) this);
    }

    @Override
    protected void onDestroy() {
        presenter.detachView();
        super.onDestroy();
    }

    /**
     * 初始化 任命者
     *
     * @return
     */
    public abstract T initPresenter();
}
