package so.laji.android.mvp;

/**
 * Description：IPresenter
 *
 * @param <V>
 */
public interface IPresenter<V extends IView> {

    /**
     * 附加页面视图
     *
     * @param mvpView 页面视图
     */
    void attachView(V mvpView);

    /**
     * 分离视图
     */
    void detachView();
}
