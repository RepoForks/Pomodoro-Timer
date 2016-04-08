package ua.edu.cdu.fotius.lisun.pomodoroproductivitytimer.ui.base;

/**
 * Created by andrei on 05.04.2016.
 */
public class MvpPresenter<V extends MvpView> {

    private V mView;

    public void attach(V v) {
        mView = v;
    }

    public void detach() {
        mView = null;
    }

    public V getView() {
        return mView;
    }

    public boolean isAttached() {
        return (mView != null);
    }
}
