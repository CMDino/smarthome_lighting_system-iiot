package message;

import com.fasterxml.jackson.annotation.JsonProperty;
import model.PolicyDescriptor;

public class ControlMessage extends GenericMessage{

    @JsonProperty("policy")
    private PolicyDescriptor policyDescriptor;

    public ControlMessage() {
    }

    public ControlMessage(PolicyDescriptor policyDescriptor) {
        super(System.currentTimeMillis(), "control");
        this.policyDescriptor = policyDescriptor;
    }

    public PolicyDescriptor getPolicyDescriptor() {
        return policyDescriptor;
    }

    public void setPolicyDescriptor(PolicyDescriptor policyDescriptor) {
        this.policyDescriptor = policyDescriptor;
    }

    @Override
    public String toString() {
        return "ControlMessage{" +
                "policyDescriptor=" + policyDescriptor +
                '}';
    }
}
