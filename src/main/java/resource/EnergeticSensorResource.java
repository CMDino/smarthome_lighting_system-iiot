package resource;

import model.EnergeticSensorDescriptor;
import model.LightDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class EnergeticSensorResource extends SmartObjectResource<EnergeticSensorDescriptor>{

    private static final double MIN_ENERGETIC_CONSUMPTION = 0.1;

    private static final double MAX_ENERGETIC_CONSUMPTION = 1.0;

    private static final long UPDATE_PERIOD = 5000;

    private static final long DELAY_PERIOD = 5000;

    public static final String RESOURCE_TYPE = "iot:smthome:energetic";

    private Random random = null;
    private Timer timerUpdate = null;
    private EnergeticSensorDescriptor energeticSensorDescriptor;
    HashMap<String, LightDescriptor> lightMap;

    public EnergeticSensorResource(String areaId) {
        super(UUID.randomUUID().toString(), EnergeticSensorResource.RESOURCE_TYPE);
        LightDescriptor lightDescriptor = new LightDescriptor(areaId,"1", true, 100, "orange");
        this.lightMap = new HashMap<>();
        this.lightMap.put("1", lightDescriptor);
        this.energeticSensorDescriptor = new EnergeticSensorDescriptor(lightMap, areaId, MAX_ENERGETIC_CONSUMPTION, "kW/h");

        init();
    }

    private void init(){
        this.random = new Random(System.currentTimeMillis());

        start();
    }

    private void start(){
        this.timerUpdate = new Timer();
        this.timerUpdate.schedule(new TimerTask() {
            @Override
            public void run() {
                if(energeticSensorDescriptor.getPolicyDescriptor().isAutomaticAreaTurnOnLightsPolicy()){
                    lightMap.forEach((key, value)->{
                        value.setEnable(false);
                    });
                }
                energeticSensorDescriptor.setValue(MIN_ENERGETIC_CONSUMPTION + random.nextDouble() * (MAX_ENERGETIC_CONSUMPTION - MIN_ENERGETIC_CONSUMPTION));
                notifyUpdate(energeticSensorDescriptor);
            }
        }, DELAY_PERIOD, UPDATE_PERIOD);
    }

    public void addLightMap(String areaId, boolean enable, int illuminationLevel, String color){
        this.lightMap.put(String.valueOf(this.lightMap.size()+1),
                new LightDescriptor(areaId, String.valueOf(this.lightMap.size()+1), enable, illuminationLevel, color));
    }

    @Override
    public EnergeticSensorDescriptor loadUpdateValue() {
        return this.energeticSensorDescriptor;
    }

    public EnergeticSensorDescriptor getEnergeticSensorDescriptor() {
        return energeticSensorDescriptor;
    }

    public void setEnergeticSensorDescriptor(EnergeticSensorDescriptor energeticSensorDescriptor) {
        this.energeticSensorDescriptor = energeticSensorDescriptor;
    }
}
