package qi.muxi.jx3serverstatus;

import java.io.Serializable;

/**
 * a class defining and storing configuration.
 * Created by Muxi on 5/13/2015.
 *
 * @author Muxi
 */
public class Configuration implements Serializable {
    /**
     * a boolean storing vibrate enable status.
     */
    private boolean vibrate;
    /**
     * a boolean storing sound enable status.
     */
    private boolean sound;
    /**
     * a boolean storing light enable status.
     */
    private boolean light;

    /**
     * Called by construction, initializing all boolean flags to false.
     */
    public Configuration() {
        this.vibrate = false;
        this.sound = false;
        this.light = false;
    }

    /**
     * Called by construction, initializing all boolean flags by input.
     *
     * @param vibrate the boolean storing vibrate enable status.
     * @param sound   the boolean storing sound enable status.
     * @param light   the boolean storing light enable status.
     */
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
