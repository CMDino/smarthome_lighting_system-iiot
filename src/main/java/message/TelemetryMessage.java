package message;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TelemetryMessage<T> extends GenericMessage{

    @JsonProperty("value")
    private T dataValue;

    public TelemetryMessage() {
    }

    public TelemetryMessage(long timestamp, String type, T dataValue) {
        super(timestamp, type);
        this.dataValue = dataValue;
    }

    public T getDataValue() {
        return dataValue;
    }

    public void setDataValue(T dataValue) {
        this.dataValue = dataValue;
    }

    @Override
    public String toString() {
        return "TelemetryMessage{" +
                "dataValue=" + dataValue +
                '}';
    }
}
