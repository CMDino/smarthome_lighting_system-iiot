package model;

import java.util.Map;

public class EnergeticSensorDescriptor {
    private Map<String, LightDescriptor> lightDescriptorMap;
    private double value;
    private String unit;
    private String areaId;
    private PolicyDescriptor policyDescriptor;

    public EnergeticSensorDescriptor() {
    }

    public EnergeticSensorDescriptor(Map<String, LightDescriptor> lightDescriptorMap, String areaId,double value, String unit) {
        this.lightDescriptorMap = lightDescriptorMap;
        this.value = value;
        this.unit = unit;
        this.areaId = areaId;
        this.policyDescriptor = new PolicyDescriptor();
    }

    public PolicyDescriptor getPolicyDescriptor() {
        return policyDescriptor;
    }

    public void setPolicyDescriptor(PolicyDescriptor policyDescriptor) {
        this.policyDescriptor = policyDescriptor;
    }

    public Map<String, LightDescriptor> getLightDescriptorMap() {
        return lightDescriptorMap;
    }

    public void setLightDescriptorMap(Map<String, LightDescriptor> lightDescriptorMap) {
        this.lightDescriptorMap = lightDescriptorMap;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    @Override
    public String toString() {
        return "EnergeticSensorDescriptor{" +
                "lightDescriptorMap=" + lightDescriptorMap +
                ", value=" + value +
                ", unit='" + unit + '\'' +
                ", areaId='" + areaId + '\'' +
                ", policyDescriptor=" + policyDescriptor +
                '}';
    }
}
