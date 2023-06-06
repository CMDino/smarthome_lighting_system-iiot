package device;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import message.TelemetryMessage;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GenericSmartObject {

    private static final Logger logger = LoggerFactory.getLogger(GenericSmartObject.class);

    protected static final String BASE_TOPIC = "area";

    protected static final int QOS = 0;

    protected IMqttClient mqttClient;

    protected ObjectMapper mapper;

    public GenericSmartObject() {
        this.mapper = new ObjectMapper();
    }

    /**
     * publish telemetry message on telemetry topic
     * @param topic
     * @param telemetryMessage
     * @throws MqttException
     * @throws JsonProcessingException
     */
    protected void publishTelemetryData(String topic, TelemetryMessage telemetryMessage) throws MqttException, JsonProcessingException {
        try {
            if (this.mqttClient != null && this.mqttClient.isConnected()
                    && telemetryMessage != null && topic != null) {

                String messagePayload = mapper.writeValueAsString(telemetryMessage);

                MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
                mqttMessage.setQos(QOS);

                mqttClient.publish(topic, mqttMessage);

                logger.info("Telemetry data published on {} topic", topic);
            } else
                logger.error("Error: publish telemetry data");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
    }
}
