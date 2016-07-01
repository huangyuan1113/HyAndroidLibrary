package so.laji.android.mvp;

import android.os.Bundle;

import so.laji.android.activity.BaseActivity;

/**
 * mvp 基类
 * Created by folie on 16/6/3.
 */
public abstract class BaseMvpActivity<V extends IView, T extends BasePresenter<V>> extends BaseActivity {

    public T presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
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
