package space.devport.utils.utility;

import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

public class ThreadUtil {

    public static Thread createThreadWithRepeatingTask(Runnable task, long delayInMillis, @Nullable String name) {
        Thread thread = new Thread(() -> {
            while (true) {
                Instant start = Instant.now();
                task.run();
                try {
                    Thread.sleep(delayInMillis - Duration.between(start, Instant.now()).toMillis());
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        if (name != null) thread.setName(name);
        return thread;
    }

    public static Thread createThreadWithRepeatingTask(Runnable task, long delayInMillis) {
        return createThreadWithRepeatingTask(task, delayInMillis, null);
    }

}
