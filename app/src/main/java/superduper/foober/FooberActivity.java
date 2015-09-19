package superduper.foober;

import android.app.Application;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;

public class FooberActivity extends Application {

    public static JobManager JOB_MANAGER;

    @Override
    public void onCreate() {
        super.onCreate();
        createJobManager();
    }

    private void createJobManager() {
        Configuration config = new Configuration.Builder(this)
                .minConsumerCount(0)
                .maxConsumerCount(5)
                .loadFactor(3)
                .consumerKeepAlive(120)
                .build();

        JOB_MANAGER = new JobManager(this,config);
    }

    public static JobManager getJobManager() {
        return JOB_MANAGER;
    }
}
