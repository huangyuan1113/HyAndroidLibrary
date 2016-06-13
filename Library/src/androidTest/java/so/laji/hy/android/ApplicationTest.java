package so.laji.hy.android;

import android.app.Application;
import android.test.ApplicationTestCase;

import so.laji.android.logger.Logger;

public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        Logger.init();
    }
}