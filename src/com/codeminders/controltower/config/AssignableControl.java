package com.codeminders.controltower.config;

import com.codeminders.ardrone.ARDrone;
import com.codeminders.ardrone.ARDrone.Animation;
import com.codeminders.ardrone.ARDrone.LED;
import com.codeminders.ardrone.ARDrone.VideoChannel;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author normenhansen
 */
public class AssignableControl {

    public enum COMMAND {

        TAKEOFF, LAND, TRIM, CLEAR_EMERGENCY, PLAY_ANIMATION, PLAY_LED, RESET,
        VIDEO_CYCLE, FRONTAL_CAM, BOTTOM_CAM, BOTTOM_CAM_SMALL, FRONTAL_CAM_SMALL
    }

    public enum CONTROL_KEY {

        PS, SELECT, START, LEFT_STICK, RIGHT_STICK, TRIANGLE, CIRCLE, CROSS, SQUARE, L1, L2, R1, R2
    }

    public enum CONTROL_AXIS {

        LEFT_X, LEFT_Y, RIGHT_X, RIGHT_Y
    }

    public enum DRONE_AXIS {

        FRONT_BACK, LEFT_RIGHT, UP_DOWN, ROTATE
    }
    private CONTROL_KEY key;
    private COMMAND command;
    private Animation anim;
    private LED led;
    private CONTROL_AXIS controlAxis;
    private DRONE_AXIS droneAxis;
    private float frequency;
    private int duration;
    private int delay;
    private String prefString = "";
    private static final VideoChannel[] VIDEO_CYCLE = {VideoChannel.HORIZONTAL_ONLY,
        VideoChannel.VERTICAL_ONLY, VideoChannel.VERTICAL_IN_HORIZONTAL, VideoChannel.HORIZONTAL_IN_VERTICAL};
    private int video_index = 0;

    public AssignableControl(String prefString) {
        String[] strings = prefString.split("/");
        if (strings.length < 0) {
            throw new IllegalStateException("preference string malformed");
        }
        key = CONTROL_KEY.valueOf(strings[0]);
        command = COMMAND.valueOf(strings[1]);
        delay = Integer.parseInt(strings[2]);
        switch (command) {
            case PLAY_ANIMATION:
                anim = Animation.valueOf(strings[3]);
                duration = Integer.parseInt(strings[4]);
                break;
            case PLAY_LED:
                led = LED.valueOf(strings[3]);
                frequency = Float.parseFloat(strings[4]);
                duration = Integer.parseInt(strings[5]);
                break;
        }
        this.prefString = prefString;
    }

    public AssignableControl(CONTROL_KEY key, LED led, int delay, float frequency, int duration) {
        this.command = COMMAND.PLAY_LED;
        this.delay = delay;
        this.led = led;
        this.frequency = frequency;
        this.duration = duration;
        prefString = key.name() + "/" + command.name() + "/" + delay + "/" + led.name() + "/" + frequency + "/" + duration;
    }

    public AssignableControl(CONTROL_KEY key, Animation anim, int delay, int duration) {
        this.command = COMMAND.PLAY_ANIMATION;
        this.delay = delay;
        this.anim = anim;
        this.duration = duration;
        prefString = key.name() + "/" + command.name() + "/" + delay + "/" + anim.name() + "/" + duration;
    }

    public AssignableControl(CONTROL_KEY key, COMMAND command, int delay) {
        this.command = command;
        this.delay = delay;
        prefString = key.name() + "/" + command.name() + "/" + delay;
    }

    public void sendToDrone(ARDrone drone) throws IOException {
        switch (command) {
            case PLAY_ANIMATION:
                Logger.getLogger(AssignableControl.class.getName()).log(Level.FINE, "Sending animation command");
                drone.playAnimation(anim, duration);
                break;
            case PLAY_LED:
                Logger.getLogger(AssignableControl.class.getName()).log(Level.FINE, "Sending LED command");
                drone.playLED(led, frequency, duration);
                break;
            case CLEAR_EMERGENCY:
                Logger.getLogger(AssignableControl.class.getName()).log(Level.FINE, "Sending clear emergency");
                drone.clearEmergencySignal();
                break;
            case TRIM:
                Logger.getLogger(AssignableControl.class.getName()).log(Level.FINE, "Sending trim");
                drone.trim();
                break;
            case TAKEOFF:
                Logger.getLogger(AssignableControl.class.getName()).log(Level.FINE, "Sending takeoff");
                drone.takeOff();
                break;
            case LAND:
                Logger.getLogger(AssignableControl.class.getName()).log(Level.FINE, "Sending land");
                drone.land();
                break;
            case RESET:
                Logger.getLogger(AssignableControl.class.getName()).log(Level.FINE, "Sending reset");
                drone.clearEmergencySignal();
                drone.trim();
                break;
            case VIDEO_CYCLE:
                Logger.getLogger(AssignableControl.class.getName()).log(Level.FINE, "Sending video cycle");
                cycleVideoChannel(drone);
                break;
            case FRONTAL_CAM:
                Logger.getLogger(AssignableControl.class.getName()).log(Level.FINE, "Sending front cam");
                drone.selectVideoChannel(ARDrone.VideoChannel.VERTICAL_ONLY);
                break;
            case BOTTOM_CAM:
                Logger.getLogger(AssignableControl.class.getName()).log(Level.FINE, "Sending bottom cam");
                drone.selectVideoChannel(ARDrone.VideoChannel.HORIZONTAL_ONLY);
                break;
            case BOTTOM_CAM_SMALL:
                Logger.getLogger(AssignableControl.class.getName()).log(Level.FINE, "Sending bottom cam small");
                drone.selectVideoChannel(ARDrone.VideoChannel.VERTICAL_IN_HORIZONTAL);
                break;
            case FRONTAL_CAM_SMALL:
                Logger.getLogger(AssignableControl.class.getName()).log(Level.FINE, "Sending front cam small");
                drone.selectVideoChannel(ARDrone.VideoChannel.HORIZONTAL_IN_VERTICAL);
                break;
        }
    }

    private void cycleVideoChannel(ARDrone drone) throws IOException {
        if (++video_index == VIDEO_CYCLE.length) {
            video_index = 0;
        }
        drone.selectVideoChannel(VIDEO_CYCLE[video_index]);
    }
    
    public CONTROL_KEY getKey() {
        return key;
    }

    public COMMAND getCommand() {
        return command;
    }

    public Animation getAnim() {
        return anim;
    }

    public LED getLed() {
        return led;
    }

    public CONTROL_AXIS getControlAxis() {
        return controlAxis;
    }

    public DRONE_AXIS getDroneAxis() {
        return droneAxis;
    }

    public float getFrequency() {
        return frequency;
    }

    public int getDuration() {
        return duration;
    }

    public int getDelay() {
        return delay;
    }

    public String getPrefString() {
        return prefString;
    }
}
