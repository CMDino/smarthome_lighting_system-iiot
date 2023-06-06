package message;

import com.fasterxml.jackson.annotation.JsonProperty;

public abstract class GenericMessage {

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("type")
    private String type;

    public GenericMessage() {
    }

    public GenericMessage(long timestamp, String type) {
        this.timestamp = timestamp;
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "GenericMessage{" +
                "timestamp=" + timestamp +
                ", type='" + type + '\'' +
                '}';
    }
}
