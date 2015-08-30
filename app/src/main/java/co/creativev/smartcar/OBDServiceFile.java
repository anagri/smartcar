package co.creativev.smartcar;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;

public class OBDServiceFile implements OBDService {
    private final ArrayBlockingQueue<OBDStats> queue;

    public OBDServiceFile() {
        queue = new ArrayBlockingQueue<>(100);
        Timer timer = new Timer();
        final Random random = new Random();

        timer.scheduleAtFixedRate(new TimerTask() {
            public int count = 0;
            OBDStats lastObject = new OBDStats(random.nextInt(100), random.nextInt(100), random.nextInt(3000), random.nextInt(50));

            @Override
            public void run() {
                count++;
                if (count >= 100) {
                    queue.add(OBDStats.NULL);
                    cancel();
                } else {
                    lastObject = new OBDStats(lastObject.engineLoad + random.nextInt(5) - random.nextInt(4),
                            lastObject.speed + random.nextInt(5) - random.nextInt(7),
                            lastObject.rpm + random.nextInt(10) - random.nextInt(7),
                            lastObject.coolantTemp + random.nextInt(10) - random.nextInt(8));
                    queue.add(lastObject);
                }
            }
        }, 500, 500);
    }

    @Override
    public OBDStats fetch() {
        try {
            OBDStats take = queue.take();
            return take == OBDStats.NULL ? null : take;
        } catch (InterruptedException e) {
            return null;
        }
    }
}
