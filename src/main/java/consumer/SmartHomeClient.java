package consumer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import message.LightMessage;
import message.ControlMessage;
import message.TelemetryMessage;
import model.EnergeticSensorDescriptor;
import model.PirSensorDescriptor;
import model.PolicyDescriptor;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import resource.EnergeticSensorResource;
import resource.PirSensorResource;
import java.util.*;

public class SmartHomeClient {

    private static final Logger logger = LoggerFactory.getLogger(SmartHomeClient.class);

    private static final String CONTROL_TOPIC = "control";

    private static final String ADD_LIGHT_TOPIC = "add-light";

    private static final String CHANGE_LIGHT_TOPIC = "change-light";

    private static final String BASE_TOPIC = "area";

    private static final String DEVICE_TOPIC = "device";

    private final int PERIOD_TASK_AVERAGE  = 60;

    private static final int UPDATE_PERIOD_SECOND = 1000;

    private static ObjectMapper mapper;

    private HashMap<ImmutablePair<String, String>, PirSensorDescriptor> pirSensorDescriptorHashMap;
    private HashMap<String, EnergeticSensorDescriptor> energeticSensorDescriptorHashMap;
    private boolean isActivePolicy1 = false;
    private HashMap<String, ImmutableTriple<Boolean, Integer, Double>> averageHashMap;
    private HashMap<String, ImmutablePair<Boolean, Boolean>> automaticTurnOffLightsHashMap;

    /**
     * start the client behavior
     * @param client
     * @throws MqttException
     */
    public void init(IMqttClient client) throws MqttException {
        mapper = new ObjectMapper();
        this.pirSensorDescriptorHashMap = new HashMap<>();
        this.energeticSensorDescriptorHashMap = new HashMap<>();
        this.averageHashMap = new HashMap<>();
        this.automaticTurnOffLightsHashMap = new HashMap<>();

        subscribeToEnergeticTelemetry(client);
        subscribeToPresenceTelemetry(client);
    }

    /**
     * subscribe to energetic telemetry message
     * @param client
     * @throws MqttException
     */
    private void subscribeToEnergeticTelemetry(IMqttClient client) throws MqttException {
        String TOPIC = String.format("%s/+/%s", BASE_TOPIC, EnergeticSensorResource.RESOURCE_TYPE);
        client.subscribe(TOPIC, (topic, msg) -> {
            if(msg != null) {
                byte[] payload = msg.getPayload();
                logger.info("message received -> topic: {} - payload: {}", topic, new String(payload));
                TelemetryMessage<EnergeticSensorDescriptor> telemetryMessage = mapper.readValue(new String(payload), new TypeReference<TelemetryMessage<EnergeticSensorDescriptor>>() {});
                updateEnergeticSensorDescriptorHashMap(telemetryMessage);
            }
        });
    }

    /**
     * subscribe to presence telemetry message
     * @param client
     * @throws MqttException
     */
    private void subscribeToPresenceTelemetry(IMqttClient client) throws MqttException {
        String TOPIC = String.format("%s/+/+/%s", BASE_TOPIC, PirSensorResource.RESOURCE_TYPE);
        client.subscribe(TOPIC, (topic, msg) -> {
            if(msg != null) {
                byte[] payload = msg.getPayload();
                logger.info("message received -> topic: {} - payload: {}", topic, new String(payload));
                TelemetryMessage<PirSensorDescriptor> telemetryMessage = mapper.readValue(new String(payload), new TypeReference<TelemetryMessage<PirSensorDescriptor>>() {});
                updatePirSensorDescriptorMap(telemetryMessage);
            }
        });
    }

    /**
     * function that adds or updates the PirSensorHashMap
     * @param telemetryMessage
     */
    private void updatePirSensorDescriptorMap(TelemetryMessage<PirSensorDescriptor> telemetryMessage){
        if(!this.pirSensorDescriptorHashMap.containsKey(new ImmutablePair<>(telemetryMessage.getDataValue().getAreaId(), telemetryMessage.getDataValue().getPositionId()))){
            this.pirSensorDescriptorHashMap.put(new ImmutablePair<>(telemetryMessage.getDataValue().getAreaId(), telemetryMessage.getDataValue().getPositionId()), telemetryMessage.getDataValue());
        } else{
            this.pirSensorDescriptorHashMap.replace(new ImmutablePair<>(telemetryMessage.getDataValue().getAreaId(), telemetryMessage.getDataValue().getPositionId()), telemetryMessage.getDataValue());
        }
    }

    /**
     * function that adds or updates the EnergeticSensorHashMap
     * @param telemetryMessage
     */
    private void updateEnergeticSensorDescriptorHashMap(TelemetryMessage<EnergeticSensorDescriptor> telemetryMessage){
        if(!this.energeticSensorDescriptorHashMap.containsKey(telemetryMessage.getDataValue().getAreaId())){
            this.energeticSensorDescriptorHashMap.put(telemetryMessage.getDataValue().getAreaId(), telemetryMessage.getDataValue());
        }else{
            this.energeticSensorDescriptorHashMap.replace(telemetryMessage.getDataValue().getAreaId(), telemetryMessage.getDataValue());
        }
    }

    /**
     * publish to control topic to change the policy of Energetic sensors
     * @param client
     * @param areaId
     * @param controlMessage
     */
    private void publishToControlTopic(IMqttClient client, String areaId, ControlMessage controlMessage){
        new Thread(() -> {
            try {
                String messagePayload = mapper.writeValueAsString(controlMessage);

                MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
                mqttMessage.setQos(0);

                String topic = String.format("%s/%s/%s", BASE_TOPIC, areaId, CONTROL_TOPIC);

                client.publish(topic, mqttMessage);

                logger.info("Control data {} published on {} topic", messagePayload, topic);
            } catch (Exception e) {
                logger.error("Error: publish Control Message");
            }
        }).start();
    }

    /**
     * publish to add light topic to add the new light in areaId
     * @param client
     * @param areaId
     * @param lightMessage
     */
    private void publishToAddLightTopic(IMqttClient client, String areaId, LightMessage lightMessage){
        new Thread(() -> {
            try {
                String messagePayload = mapper.writeValueAsString(lightMessage);

                MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
                mqttMessage.setQos(0);

                String topic = String.format("%s/%s/%s", BASE_TOPIC, areaId, ADD_LIGHT_TOPIC);

                client.publish(topic, mqttMessage);

                logger.info("Light data {} published on {} topic", messagePayload, topic);
            } catch (Exception e) {
                logger.error("Error: publish Control Message");
            }
        }).start();
    }

    /**
     * publish to change light topic to change a light in areaId
     * @param client
     * @param areaId
     * @param deviceId
     * @param lightMessage
     */
    private void publishToChangeLightTopic(IMqttClient client, String areaId, String deviceId, LightMessage lightMessage){
        new Thread(() -> {
            try {
                String messagePayload = mapper.writeValueAsString(lightMessage);

                MqttMessage mqttMessage = new MqttMessage(messagePayload.getBytes());
                mqttMessage.setQos(0);

                String topic = String.format("%s/%s/%s/%s/%s", BASE_TOPIC, areaId, DEVICE_TOPIC, deviceId, CHANGE_LIGHT_TOPIC);

                client.publish(topic, mqttMessage);

                logger.info("Light data {} published on {} topic", messagePayload, topic);
            } catch (Exception e) {
                logger.error("Error: publish Control Message");
            }
        }).start();
    }

    /**
     * when the automatic turn-off lights policy is active, the function checks all of Presence Sensors
     * in areaId and if all of these are false, turn-off all lights in the area
     * @param client
     * @param areaId
     */
    public void OnAutomaticTurnOffLight(IMqttClient client, String areaId){
        this.automaticTurnOffLightsHashMap.put(areaId, new ImmutablePair<>(true, false));
        Timer timerTurnOffLight = new Timer();
        timerTurnOffLight.schedule(new TimerTask() {
            @Override
            public void run() {
                if(automaticTurnOffLightsHashMap.get(areaId).left) {
                    pirSensorDescriptorHashMap.forEach((key, value) -> {
                        if (Objects.equals(key.left, areaId) && value.isPresence()) {
                            automaticTurnOffLightsHashMap.replace(areaId, new ImmutablePair<>(automaticTurnOffLightsHashMap.get(areaId).left, true));
                        }
                    });
                    if (!automaticTurnOffLightsHashMap.get(areaId).right) {
                        publishToControlTopic(client, areaId, makeControlMessage(
                                new PolicyDescriptor(true, energeticSensorDescriptorHashMap.get(areaId).getPolicyDescriptor().isAverageEnergeticConsumption())
                        ));
                        automaticTurnOffLightsHashMap.replace(areaId, new ImmutablePair<>(false, false));
                        timerTurnOffLight.cancel();
                        timerTurnOffLight.purge();
                    }
                    automaticTurnOffLightsHashMap.replace(areaId, new ImmutablePair<>(automaticTurnOffLightsHashMap.get(areaId).left, false));
                }
            }
        }, 1000, UPDATE_PERIOD_SECOND);
    }

    /**
     * function that changes the automatic turn-off lights from true to false
     * @param client
     * @param areaId
     */
    public void OffAutomaticTurnOffLight(IMqttClient client, String areaId){
        publishToControlTopic(client, areaId, makeControlMessage(
                new PolicyDescriptor(false, energeticSensorDescriptorHashMap.get(areaId).getPolicyDescriptor().isAverageEnergeticConsumption())
        ));
    }

    /**
     * when the average policy is active, the function calculates the average of energetic consumption
     * in an interval of 60 seconds
     * @param areaId
     */
    public void startAveragePolicy(String areaId){
        this.averageHashMap.put(areaId, new ImmutableTriple<>(true, 0, 0.0));
        Timer timerAverage = new Timer();
        timerAverage.schedule(new TimerTask() {
            @Override
            public void run() {
                if(averageHashMap.get(areaId).left.equals(true) && averageHashMap.get(areaId).middle<PERIOD_TASK_AVERAGE){
                    averageHashMap.replace(areaId, new ImmutableTriple<>(true, averageHashMap.get(areaId).middle + 1, averageHashMap.get(areaId).right + energeticSensorDescriptorHashMap.get(areaId).getValue()/PERIOD_TASK_AVERAGE));
                }else {
                    averageHashMap.replace(areaId, new ImmutableTriple<>(false, averageHashMap.get(areaId).middle, averageHashMap.get(areaId).right));
                    timerAverage.cancel();
                    timerAverage.purge();
                }
            }
        }, 0, UPDATE_PERIOD_SECOND);
    }

    public void addNewLight(IMqttClient client, String areaId, String deviceId, String color, int illuminationLevel, boolean isEnable){
        publishToAddLightTopic(client, areaId, makeAddLightMessage(areaId, deviceId, isEnable, color, illuminationLevel));
    }

    public void changeLight(IMqttClient client, String areaId, String deviceId, String color, int illuminationLevel, boolean isEnable){
        publishToChangeLightTopic(client, areaId, deviceId, makeAddLightMessage(areaId, deviceId, isEnable, color, illuminationLevel));
    }

    private ControlMessage makeControlMessage(PolicyDescriptor policyDescriptor){
        return new ControlMessage(policyDescriptor);
    }

    private LightMessage makeAddLightMessage(String areaId, String deciceId, boolean isEnable, String color, int illuminationLevel){
        return new LightMessage(areaId, deciceId, isEnable, color, illuminationLevel);
    }

    public HashMap<String, EnergeticSensorDescriptor> getEnergeticSensorDescriptorHashMap() {
        return energeticSensorDescriptorHashMap;
    }

    public void setEnergeticSensorDescriptorHashMap(HashMap<String, EnergeticSensorDescriptor> energeticSensorDescriptorHashMap) {
        this.energeticSensorDescriptorHashMap = energeticSensorDescriptorHashMap;
    }

    public HashMap<ImmutablePair<String, String>, PirSensorDescriptor> getPirSensorDescriptorHashMap() {
        return pirSensorDescriptorHashMap;
    }

    public void setPirSensorDescriptorHashMap(HashMap<ImmutablePair<String, String>, PirSensorDescriptor> pirSensorDescriptorHashMap) {
        this.pirSensorDescriptorHashMap = pirSensorDescriptorHashMap;
    }

    public HashMap<String, ImmutableTriple<Boolean, Integer, Double>> getAverageHashMap() {
        return averageHashMap;
    }

    public void setAverageHashMap(HashMap<String, ImmutableTriple<Boolean, Integer, Double>> averageHashMap) {
        this.averageHashMap = averageHashMap;
    }

    public static void main(String[] args) throws MqttException {
        logger.info("Start MQTT Smarthome Consumer");
        String BROKER_ADDRESS = "127.0.0.1";
        int BROKER_PORT = 1883;

        String clientId = UUID.randomUUID().toString();

        MqttClientPersistence persistence = new MemoryPersistence();

        IMqttClient client = new MqttClient(
                String.format("tcp://%s:%d",BROKER_ADDRESS, BROKER_PORT),
                clientId,
                persistence);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        client.connect(options);

        logger.info("Client connected with id: {}", clientId);

        SmartHomeClient smarthomeClient = new SmartHomeClient();
        smarthomeClient.init(client);
    }
}
