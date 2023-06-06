package device;

import com.fasterxml.jackson.core.JsonProcessingException;
import message.TelemetryMessage;
import model.PirSensorDescriptor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.PirSensorResource;
import resource.SmartObjectResource;
import java.util.UUID;

public class PresenceMonitoringSmartObject extends GenericSmartObject{

    private static final Logger logger = LoggerFactory.getLogger(PresenceMonitoringSmartObject.class);

    private String areaId;

    private String positionId;

    private SmartObjectResource<PirSensorDescriptor> smartObjectResource;

    public PresenceMonitoringSmartObject() {
        super();
    }

    /**
     * init the presence monitoring smart object
     * @param areaId of area
     * @param positionId of position
     * @param mqttClient
     * @param smartObjectResource PirSensorResource
     */
    public void init(String areaId, String positionId, IMqttClient mqttClient, SmartObjectResource<PirSensorDescriptor> smartObjectResource){
        this.areaId = areaId;
        this.positionId = positionId;
        this.mqttClient = mqttClient;
        this.smartObjectResource = smartObjectResource;

        logger.info("Presence Monitoring Smart Object created");
    }

    /**
     * start the presence monitoring, publish on telemetry topic the telemetry message periodically
     */
    public void start(){
        this.smartObjectResource.addDataListener((resource, updatedValue) -> {
            try {
                publishTelemetryData(String.format("%s/%s/%s/%s", BASE_TOPIC, areaId, positionId, this.smartObjectResource.getType()),
                        new TelemetryMessage<PirSensorDescriptor>(System.currentTimeMillis(), PirSensorResource.RESOURCE_TYPE, updatedValue));
            } catch (MqttException | JsonProcessingException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) throws MqttException {
        final String BROKER_IP = "127.0.0.1";
        final int BROKER_PORT = 1883;

        String uuid = UUID.randomUUID().toString();

        final String areaId = "1";
        final String positionId = "2";

        MqttClientPersistence persistence = new MemoryPersistence();
        IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d",
                BROKER_IP, BROKER_PORT), uuid, persistence);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        mqttClient.connect(options);

        PresenceMonitoringSmartObject presenceMonitoringSmartObject = new PresenceMonitoringSmartObject();
        presenceMonitoringSmartObject.init(areaId, positionId, mqttClient, new PirSensorResource(areaId, positionId));
        presenceMonitoringSmartObject.start();
    }
}
