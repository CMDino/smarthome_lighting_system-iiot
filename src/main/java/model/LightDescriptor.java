package model;

public class LightDescriptor {
    private String deviceId;
    private String areaId;
    private boolean isEnable;
    private int illuminationLevel;
    private String color;

    public LightDescriptor() {
    }

    public LightDescriptor(String areaId, String deviceId, boolean isEnable, int illuminationLevel, String color) {
        this.deviceId = deviceId;
        this.areaId = areaId;
        this.isEnable = isEnable;
        this.illuminationLevel = illuminationLevel;
        this.color = color;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public int getIlluminationLevel() {
        return illuminationLevel;
    }

    public void setIlluminationLevel(int illuminationLevel) {
        this.illuminationLevel = illuminationLevel;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "LightDescriptor{" +
                "deviceId='" + deviceId + '\'' +
                ", areaId='" + areaId + '\'' +
                ", isEnable=" + isEnable +
                ", illuminationLevel=" + illuminationLevel +
                ", color=" + color +
                '}';
    }
}
