package message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LightMessage extends GenericMessage{

    @JsonProperty("areaId")
    private String areaId;

    @JsonProperty("deviceId")
    private String deviceId;

    @JsonProperty("enable")
    private boolean isEnable;

    @JsonProperty("color")
    private String color;

    @JsonProperty("illumination")
    private int illuminationLevel;

    public LightMessage() {
    }

    public LightMessage(String areaId, String deviceId, boolean isEnable, String color, int illuminationLevel) {
        super(System.currentTimeMillis(), "light");
        this.areaId = areaId;
        this.deviceId = deviceId;
        this.isEnable = isEnable;
        this.color = color;
        this.illuminationLevel = illuminationLevel;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getIlluminationLevel() {
        return illuminationLevel;
    }

    public void setIlluminationLevel(int illuminationLevel) {
        this.illuminationLevel = illuminationLevel;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "LightMessage{" +
                "areaId='" + areaId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", isEnable=" + isEnable +
                ", color='" + color + '\'' +
                ", illuminationLevel=" + illuminationLevel +
                '}';
    }
}
