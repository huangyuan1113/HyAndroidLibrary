package so.laji.android.utils;


import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.ReplaySubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * RxJava版的 EventBus
 * Created by folie on 16/6/14.
 * <pre>
 *     // 发送事件
 *     RxBus.getDefault().post(new UserEvent (1, "yoyo"));
 *
 *     // rxSubscription是一个Subscription的全局变量，这段代码可以在onCreate/onStart等生命周期内
 *     rxSubscription = RxBus.getDefault().toObserverable(UserEvent.class)
 *                      .subscribe(new Action1() {
 *
 *                          public void call(UserEvent userEvent) {
 *                              long id = userEvent.getId();
 *                              String name = userEvent.getName();
 *                              ...
 *                          }
 *                      },
 *                      new Action1() {
 *
 *                          public void call(Throwable throwable) {
 *                              // 处理异常
 *                          }
 *                     });
 *     // 事件销毁
 *
 *     protected void onDestroy() {
 *          super.onDestroy();
 *          if(!rxSubscription.isUnsubscribed()) {
 *               rxSubscription.unsubscribe();
 *          }
 *     }
 * </pre>
 */
public class RxBus {
    private static volatile RxBus defaultInstance;
    // 主题
    private final Subject bus;
    private final ReplaySubject replaySubject;

    // PublishSubject 只会把在订阅发生的时间点之后来自原始Observable的数据发射给观察者
    public RxBus() {
        bus = new SerializedSubject<>(PublishSubject.create());
        replaySubject = ReplaySubject.create();
    }

    /**
     * 单例RxBus
     *
     * @return RxBus 实例
     */
    public static RxBus getDefault() {
        if (defaultInstance == null) {
            synchronized (RxBus.class) {
                defaultInstance = new RxBus();
            }
        }
        return defaultInstance;
    }

    // 提供了一个新的事件
    public void post(Object o) {
        bus.onNext(o);
    }

    public void postSticky(Object o) {
        if(o != null) {
            replaySubject.onNext(o);
            replaySubject.onCompleted();
        }
    }

    public <T> Observable toStickyObservable(Class<T> eventType) {
        return replaySubject.ofType(eventType);
    }

    // 根据传递的 eventType 类型返回特定类型(eventType)的 被观察者
    public <T> Observable toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }
}

