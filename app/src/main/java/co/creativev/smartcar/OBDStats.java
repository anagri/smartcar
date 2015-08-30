package co.creativev.smartcar;

public class OBDStats {
    public static final OBDStats NULL = new OBDStats(null, null, null, null);
    public final Integer engineLoad;
    public final Integer speed;
    public final Integer rpm;
    public final Integer coolantTemp;

    public OBDStats(Integer engineLoad, Integer speed, Integer rpm, Integer coolantTemp) {
        this.engineLoad = engineLoad;
        this.speed = speed;
        this.rpm = rpm;
        this.coolantTemp = coolantTemp;
    }
}
