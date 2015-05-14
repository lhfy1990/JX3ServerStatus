package qi.muxi.jx3serverstatus;

import java.io.Serializable;

/**
 * Created by Muxi on 5/13/2015.
 *
 * @author Muxi
 */
public class Configuration implements Serializable {
    private boolean vibrate;
    private boolean sound;
    private boolean light;

    public Configuration() {
        this.vibrate = false;
        this.sound = false;
        this.light = false;
    }

    public Configuration(boolean vibrate, boolean sound, boolean light) {
        this.vibrate = vibrate;
        this.sound = sound;
        this.light = light;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public boolean isSound() {
        return sound;
    }

    public void setSound(boolean sound) {
        this.sound = sound;
    }

    public boolean isLight() {
        return light;
    }

    public void setLight(boolean light) {
        this.light = light;
    }

}
