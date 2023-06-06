package model;

public class PirSensorDescriptor {
    private String areaId;
    private String positionId;
    private boolean isPresence;

    public PirSensorDescriptor() {
    }

    public PirSensorDescriptor(String areaId, String positionId, boolean isPresence) {
        this.areaId = areaId;
        this.positionId = positionId;
        this.isPresence = isPresence;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public boolean isPresence() {
        return isPresence;
    }

    public void setPresence(boolean presence) {
        isPresence = presence;
    }

    @Override
    public String toString() {
        return "PirSensorDescriptor{" +
                "areaId='" + areaId + '\'' +
                ", positionId='" + positionId + '\'' +
                ", isPresence=" + isPresence +
                '}';
    }
}
