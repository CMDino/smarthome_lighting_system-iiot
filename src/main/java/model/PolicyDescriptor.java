package model;

public class PolicyDescriptor {

    private boolean automaticAreaTurnOnLightsPolicy;

    private boolean averageEnergeticConsumption;

    public PolicyDescriptor() {
        this.automaticAreaTurnOnLightsPolicy = false;
        this.averageEnergeticConsumption = false;
    }

    public PolicyDescriptor(boolean automaticAreaTurnOnLightsPolicy, boolean averageEnergeticConsumption) {
        this.automaticAreaTurnOnLightsPolicy = automaticAreaTurnOnLightsPolicy;
        this.averageEnergeticConsumption = averageEnergeticConsumption;
    }

    public boolean isAutomaticAreaTurnOnLightsPolicy() {
        return automaticAreaTurnOnLightsPolicy;
    }

    public void setAutomaticAreaTurnOnLightsPolicy(boolean automaticAreaTurnOnLightsPolicy) {
        this.automaticAreaTurnOnLightsPolicy = automaticAreaTurnOnLightsPolicy;
    }

    public boolean isAverageEnergeticConsumption() {
        return averageEnergeticConsumption;
    }

    public void setAverageEnergeticConsumption(boolean averageEnergeticConsumption) {
        this.averageEnergeticConsumption = averageEnergeticConsumption;
    }

    @Override
    public String toString() {
        return "PolicyDescriptor{" +
                "automaticAreaTurnOnLightsPolicy=" + automaticAreaTurnOnLightsPolicy +
                ", averageEnergeticConsumption=" + averageEnergeticConsumption +
                '}';
    }
}
