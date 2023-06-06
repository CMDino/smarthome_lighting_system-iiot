package device;

import com.fasterxml.jackson.core.JsonProcessingException;
import message.LightMessage;
import message.ControlMessage;
import message.TelemetryMessage;
import model.EnergeticSensorDescriptor;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.EnergeticSensorResource;
import java.util.UUID;

public class LightSmartObject extends GenericSmartObject{

    private static final Logger logger = LoggerFactory.getLogger(LightSmartObject.class);

    private static final String CONTROL_TOPIC = "control";

    private static final String ADD_LIGHT_TOPIC = "add-light";

    private static final String CHANGE_LIGHT_TOPIC = "change-light";

    private static final String DEVICE_TOPIC = "device";

    private String areaId;

    private EnergeticSensorResource smartObjectResource;

    public LightSmartObject() {
        super();
    }

    /**
     * init the light smart object
     * @param areaId of area
     * @param mqttClient
     * @param smartObjectResource Energetic Sensor Resource
     */

    public void init(String areaId, IMqttClient mqttClient, EnergeticSensorResource smartObjectResource) {
        this.areaId = areaId;
        this.mqttClient = mqttClient;
        this.smartObjectResource = smartObjectResource;

        logger.info("Light Smart Object created");
    }

    /**
     * start the light activity, publish on telemetry topic the telemetry message periodically
     */
    public void start() throws MqttException {
        this.smartObjectResource.addDataListener((resource, updatedValue) -> {
            try {
                publishTelemetryData(String.format("%s/%s/%s", BASE_TOPIC, areaId, this.smartObjectResource.getType()),
                        new TelemetryMessage<EnergeticSensorDescriptor>(System.currentTimeMillis(), EnergeticSensorResource.RESOURCE_TYPE, updatedValue));
            } catch (MqttException | JsonProcessingException e) {
                e.printStackTrace();
            }
        });

        registerToControlTopic();
        registerToAddNewLightTopic();
        registerToChangeLightTopic();
    }

    /**
     * register to control topic for policy state changing
     * @throws MqttException
     */
    private void registerToControlTopic() throws MqttException {
        String TOPIC = String.format("%s/%s/%s", BASE_TOPIC, areaId, CONTROL_TOPIC);
        this.mqttClient.subscribe(TOPIC, (topic, msg) -> {
            if(msg != null) {
                changeStateResource(parseControlMessage(msg));
            }
        });
    }

    /**
     * register to add new light topic for adds a new light
     * @throws MqttException
     */
    private void registerToAddNewLightTopic() throws MqttException {
        String TOPIC = String.format("%s/%s/%s", BASE_TOPIC, areaId, ADD_LIGHT_TOPIC);
        this.mqttClient.subscribe(TOPIC, (topic, msg) -> {
            if(msg != null){
                addNewLight(parseAddLightMessage(msg));
            }
        });
    }

    /**
     * register to add new light topic for light state changing
     * @throws MqttException
     */
    private void registerToChangeLightTopic() throws MqttException {
        String TOPIC = String.format("%s/%s/%s/+/%s", BASE_TOPIC, areaId, DEVICE_TOPIC, CHANGE_LIGHT_TOPIC);
        this.mqttClient.subscribe(TOPIC, (topic, msg) ->{
            if(msg != null){
                changeLight(parseAddLightMessage(msg));
            }
        });
    }

    /**
     * parse control message from mqtt message
     * @param mqttMessage
     * @return Parse Control Message
     * @throws JsonProcessingException
     */
    private ControlMessage parseControlMessage(MqttMessage mqttMessage) throws JsonProcessingException {
        logger.info("parse control message");
        byte[] payload = mqttMessage.getPayload();
        return this.mapper.readValue(new String(payload), ControlMessage.class);
    }

    /**
     * parse add new light message from mqtt message
     * @param mqttMessage
     * @return
     * @throws JsonProcessingException
     */
    private LightMessage parseAddLightMessage(MqttMessage mqttMessage) throws JsonProcessingException {
        logger.info("parse add light message");
        byte[] payload = mqttMessage.getPayload();
        return this.mapper.readValue(new String(payload), LightMessage.class);
    }

    /**
     * changes state resource from indication by control message
     * @param controlMessage
     */
    private void changeStateResource(ControlMessage controlMessage){
        this.smartObjectResource
                .getEnergeticSensorDescriptor()
                .setPolicyDescriptor(controlMessage.getPolicyDescriptor());
    }

    /**
     * adds the new light
     * @param lightMessage
     */
    private void addNewLight(LightMessage lightMessage){
        this.smartObjectResource
                .addLightMap(lightMessage.getAreaId(),
                        lightMessage.isEnable(),
                        lightMessage.getIlluminationLevel(),
                        lightMessage.getColor());
    }

    /**
     * changes light state from indication by add new light message
     * @param lightMessage
     */
    private void changeLight(LightMessage lightMessage){
        this.smartObjectResource
                .getEnergeticSensorDescriptor()
                .getLightDescriptorMap()
                .get(lightMessage.getDeviceId())
                .setEnable(lightMessage.isEnable());
        this.smartObjectResource
                .getEnergeticSensorDescriptor()
                .getLightDescriptorMap()
                .get(lightMessage.getDeviceId())
                .setColor(lightMessage.getColor());
        this.smartObjectResource
                .getEnergeticSensorDescriptor()
                .getLightDescriptorMap()
                .get(lightMessage.getDeviceId())
                .setIlluminationLevel(lightMessage.getIlluminationLevel());
    }

    public static void main(String[] args) throws MqttException {
        final String BROKER_IP = "127.0.0.1";
        final int BROKER_PORT = 1883;

        String uuid = UUID.randomUUID().toString();

        final String areaId = "1";

        MqttClientPersistence persistence = new MemoryPersistence();
        IMqttClient mqttClient = new MqttClient(String.format("tcp://%s:%d",
                BROKER_IP, BROKER_PORT), uuid, persistence);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        mqttClient.connect(options);

        LightSmartObject lightSmartObject = new LightSmartObject();
        lightSmartObject.init(areaId, mqttClient, new EnergeticSensorResource(areaId));
        lightSmartObject.start();
    }
}
