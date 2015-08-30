package co.creativev.smartcar;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ObdCommand {

    private final String cmd;
    private long start;
    private long end;
    private long responseTimeDelay = 200;
    private String rawData;

    public ObdCommand(String command) {
        cmd = command;
    }

    public void run(InputStream in, OutputStream out) throws IOException,
            InterruptedException {
        start = System.currentTimeMillis();

        out.write((cmd + "\r").getBytes());
        out.flush();

        Thread.sleep(responseTimeDelay);

        byte b = 0;
        StringBuilder res = new StringBuilder();

        // read until '>' arrives
        while ((char) (b = (byte) in.read()) != '>') {
            res.append((char) b);
        }

        rawData = res.toString().replaceAll("SEARCHING", "");
        rawData = rawData.replaceAll("\\s", "");
        rawData = rawData.replaceAll("\\s", ""); //removes all [ \t\n\x0B\f\r]
        rawData = rawData.replaceAll("(BUS INIT)|(BUSINIT)|(\\.)", "");

        end = System.currentTimeMillis();
    }

    public String getRawData() {
        return rawData;
    }
}
