package resource;

import model.PirSensorDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class PirSensorResource extends SmartObjectResource<PirSensorDescriptor>{

    private static final Logger logger = LoggerFactory.getLogger(PirSensorResource.class);

    private static final long UPDATE_PERIOD = 5000;

    private static final long DELAY_PERIOD = 5000;

    public static final String RESOURCE_TYPE = "iot:smthome:presence";

    private Random random = null;
    private Timer timerUpdate = null;
    private PirSensorDescriptor pirSensorDescriptor;

    public PirSensorResource(String areaId, String positionId) {
        super(UUID.randomUUID().toString(), PirSensorResource.RESOURCE_TYPE);
        this.pirSensorDescriptor = new PirSensorDescriptor();
        this.pirSensorDescriptor.setAreaId(areaId);
        this.pirSensorDescriptor.setPositionId(positionId);
        init();
    }

    private void init(){
        try{
            this.random = new Random(System.currentTimeMillis());
            this.pirSensorDescriptor.setPresence(this.random.nextBoolean());

            start();
        }catch(Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    private void start(){
        try{
            this.timerUpdate = new Timer();
            this.timerUpdate.schedule(new TimerTask() {
                @Override
                public void run() {
                    pirSensorDescriptor.setPresence(random.nextBoolean());
                    notifyUpdate(pirSensorDescriptor);
                }
            }, DELAY_PERIOD, UPDATE_PERIOD);
        }catch(Exception e){
            logger.error(e.getLocalizedMessage());
        }
    }

    @Override
    public PirSensorDescriptor loadUpdateValue() {
        return this.pirSensorDescriptor;
    }

    public boolean isPresence(){
        return this.pirSensorDescriptor.isPresence();
    }
    public void setPresence(boolean isPresence){
        this.pirSensorDescriptor.setPresence(isPresence);
    }
}
