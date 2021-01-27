package space.devport.utils.utility;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;

@UtilityClass
public class ThreadUtil {

    @SuppressWarnings("BusyWait")
    public Thread createRepeatingTask(Runnable task, long interval, @Nullable String name) {
        Thread thread = new Thread(() -> {
            while (true) {
                Instant start = Instant.now();
                task.run();
                try {
                    Thread.sleep(interval - Duration.between(start, Instant.now()).toMillis());
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        if (name != null) thread.setName(name);
        return thread;
    }

    public Thread createRepeatingTask(Runnable task, long delayInMillis) {
        return createRepeatingTask(task, delayInMillis, null);
    }

    // The thread is first delayed by the interval and then ran.
    @SuppressWarnings("BusyWait")
    public Thread createDelayedRepeatingTask(Runnable task, long interval, @Nullable String name) {
        Thread thread = new Thread(() -> {
            while (true) {
                Instant start = Instant.now();
                try {
                    Thread.sleep(interval - Duration.between(start, Instant.now()).toMillis());
                } catch (InterruptedException e) {
                    break;
                }
                task.run();
            }
        });
        if (name != null) thread.setName(name);
        return thread;
    }

    public Thread createDelayedRepeatingTask(Runnable task, long interval) {
        return createDelayedRepeatingTask(task, interval, null);
    }

    @SuppressWarnings("BusyWait")
    public Thread createDelayedRepeatingTask(Runnable task, long delay, long interval, @Nullable String name) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                return;
            }

            while (true) {
                Instant start = Instant.now();
                task.run();
                try {
                    Thread.sleep(interval - Duration.between(start, Instant.now()).toMillis());
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        if (name != null) thread.setName(name);
        return thread;
    }

    public Thread createDelayedRepeatingTask(Runnable task, long delay, long interval) {
        return createDelayedRepeatingTask(task, delay, interval, null);
    }

    public Thread createDelayedTask(Runnable task, long delay, @Nullable String name) {
        Thread thread = new Thread(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ignored) {
            }

            task.run();
        });
        if (name != null) thread.setName(name);
        return thread;
    }

    public Thread createDelayedTask(Runnable task, long delay) {
        return createDelayedTask(task, delay, null);
    }
}
