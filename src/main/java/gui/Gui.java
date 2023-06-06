package gui;

import consumer.SmartHomeClient;
import model.EnergeticSensorDescriptor;
import model.LightDescriptor;
import model.PirSensorDescriptor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.Timer;

public class Gui extends JPanel implements ActionListener {

    private static final int REFRESH_TIME = 1000;
    private static final int REFRESH_DELAY = 6000;
    private IMqttClient client;
    private HashMap<String, JComponent> jComponentAreaHashMap;

    private JFrame frame;

    private JPanel startPanel;

    private JButton averageButton;

    private JButton addNewLightButton;

    private JButton changeLightButton;

    private JTextField areaIdTextField;

    private JTextField illuminationLevelTextField;

    private JTextField deviceIdTextField;

    private JComboBox<String> colorComboBox;

    private JComboBox<String> onOffComboBox;

    private JCheckBox automaticTurnOffLightsCheckBox;

    private SmartHomeClient smarthomeClient;

    /**
     * start the Gui behavior with the timer that refresh the Gui
     * @throws MqttException
     */
    public Gui() throws MqttException {
        setupSmartHomeClient();
        this.jComponentAreaHashMap = new HashMap<>();
        start();

        Timer timeRefresh =  new Timer();
        timeRefresh.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshGui();
            }
        }, REFRESH_DELAY, REFRESH_TIME);
    }

    /**
     * Setup of Smart Home Client, setup of mqtt connection and start connection
     * @throws MqttException
     */
    private void setupSmartHomeClient() throws MqttException {
        String BROKER_ADDRESS = "127.0.0.1";
        int BROKER_PORT = 1883;

        String clientId = UUID.randomUUID().toString();

        MqttClientPersistence persistence = new MemoryPersistence();

        this.client = new MqttClient(
                String.format("tcp://%s:%d",BROKER_ADDRESS, BROKER_PORT),
                clientId,
                persistence);

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);

        this.client.connect(options);

        this.smarthomeClient = new SmartHomeClient();
        this.smarthomeClient.init(this.client);
    }

    /**
     * function that makes up the main panel
     */
    public void start() {
        startPanel = new JPanel();
        this.startPanel.setBorder(BorderFactory.createEmptyBorder(100,30,100,0));
        this.startPanel.setLayout(new BoxLayout(this.startPanel, BoxLayout.PAGE_AXIS));

        this.frame = new JFrame();
        this.averageButton = new JButton("Calculate Average Consumption");
        this.addNewLightButton = new JButton("Add New Light");
        this.changeLightButton = new JButton("Change Light");
        this.averageButton.addActionListener(this);
        this.addNewLightButton.addActionListener(this);
        this.changeLightButton.addActionListener(this);
        this.frame.setTitle("SmartHome");
        this.frame.setPreferredSize(new Dimension(1000,700));
        this.frame.add(this.startPanel, BorderLayout.CENTER);
        this.frame.add(makePolicyPanel(), BorderLayout.EAST);
        this.frame.pack();
        this.frame.setVisible(true);
    }

    /**
     * function that refresh the gui using PirSensorDescriptorHashMap and EnergeticSensorDescriptorHashMap
     */
    private void refreshGui(){
        if(!this.smarthomeClient.getEnergeticSensorDescriptorHashMap().isEmpty()) {
            this.startPanel.removeAll();
            this.jComponentAreaHashMap = new HashMap<>();
            this.smarthomeClient.getEnergeticSensorDescriptorHashMap().forEach((key, value) -> {
                addJComponentArea(key, makePanel(String.format("Area #%s", key), value));
                this.startPanel.add(this.jComponentAreaHashMap.get(key));
                if(!this.smarthomeClient.getPirSensorDescriptorHashMap().isEmpty()){
                    this.smarthomeClient.getPirSensorDescriptorHashMap().forEach((key_1, value_1) ->{
                        if(key.equals(key_1.left)) {
                            addPirSensorToJPanel(this.startPanel, value_1);
                        }
                    });
                }
                if(!this.smarthomeClient.getEnergeticSensorDescriptorHashMap().get(key).getLightDescriptorMap().isEmpty()) {
                    this.smarthomeClient.getEnergeticSensorDescriptorHashMap().get(key).getLightDescriptorMap().forEach((key_1, value_1)->{
                        addLightToJPanel(this.startPanel, value_1);
                    });
                }
            });
        }
        refresh();
    }

    /**
     * function that repaints the gui
     */
    private void refresh(){
        this.frame.remove(this.startPanel);
        this.frame.add(this.startPanel, BorderLayout.CENTER);
        this.frame.revalidate();
        this.frame.repaint();
    }

    /**
     * function that makes up the policy panel
     * @return
     */
    private JPanel makePolicyPanel(){
        JPanel policyPanel = new JPanel();
        policyPanel.setBorder(BorderFactory.createEmptyBorder(30,50,100,0));
        policyPanel.setLayout(new BoxLayout(policyPanel, BoxLayout.PAGE_AXIS));

        this.areaIdTextField = new JTextField();
        this.areaIdTextField.setMaximumSize(new Dimension(300,30));
        this.illuminationLevelTextField = new JTextField();
        this.illuminationLevelTextField.setMaximumSize(new Dimension(300,30));
        this.deviceIdTextField = new JTextField();
        this.deviceIdTextField.setMaximumSize(new Dimension(300,30));
        Label textAreaId = new Label("areaId:");
        Label textDeviceId = new Label("deviceId:");
        Label textIlluminationLevel = new Label("Illumination Level:");
        Label textColor = new Label("Color:");
        Label textOnOff = new Label("ON/OFF:");
        textAreaId.setMaximumSize(new Dimension(300,30));
        textDeviceId.setMaximumSize(new Dimension(300,30));
        textIlluminationLevel.setMaximumSize(new Dimension(300,30));
        textColor.setMaximumSize(new Dimension(300,30));
        textOnOff.setMaximumSize(new Dimension(300,30));
        String[] color = {"white", "blue", "red", "green", "yellow", "orange"};
        String[] on_off = {"ON", "OFF"};
        this.colorComboBox = new JComboBox<>(color);
        this.colorComboBox.setMaximumSize(new Dimension(300,30));
        this.onOffComboBox = new JComboBox<>(on_off);
        this.onOffComboBox.setMaximumSize(new Dimension(300,30));
        this.automaticTurnOffLightsCheckBox = new JCheckBox("Automatic Turn Off");
        this.automaticTurnOffLightsCheckBox.addActionListener(this);
        policyPanel.add(textAreaId);
        policyPanel.add(this.areaIdTextField);
        policyPanel.add(textIlluminationLevel);
        policyPanel.add(this.illuminationLevelTextField);
        policyPanel.add(textColor);
        policyPanel.add(this.colorComboBox);
        policyPanel.add(textDeviceId);
        policyPanel.add(this.deviceIdTextField);
        policyPanel.add(textOnOff);
        policyPanel.add(this.onOffComboBox);
        policyPanel.add(this.averageButton);
        policyPanel.add(this.addNewLightButton);
        policyPanel.add(this.changeLightButton);
        policyPanel.add(this.automaticTurnOffLightsCheckBox);
        return policyPanel;
    }

    /**
     * function that adds jComponent in HashMap
     * @param key areaId
     * @param jComponent
     */
    private void addJComponentArea(String key, JComponent jComponent){
        this.jComponentAreaHashMap.put(key, jComponent);
    }

    /**
     * function that adds the information of Lights in Jpanel
     * @param jPanel
     * @param lightDescriptor
     */
    private void addLightToJPanel(JComponent jPanel, LightDescriptor lightDescriptor){
        String lightOn;
        if(lightDescriptor.isEnable())
            lightOn = "ON";
        else
            lightOn = "OFF";
        String text;
        text = String.format("DeviceId: %s    Color: %s   Illumination: %s    Light: %s", lightDescriptor.getDeviceId(),
                lightDescriptor.getColor(), lightDescriptor.getIlluminationLevel(), lightOn);
        JLabel label = new JLabel(text);
        jPanel.add(label);
    }

    /**
     * function that adds the information of Pir Sensor in Jpanel
     * @param jPanel
     * @param pirSensorDescriptor
     */
    private void addPirSensorToJPanel(JComponent jPanel, PirSensorDescriptor pirSensorDescriptor){
        String text;
        text = String.format("RoomId: %s  Presence: %s", pirSensorDescriptor.getPositionId(), pirSensorDescriptor.isPresence());
        JLabel label = new JLabel(text);
        jPanel.add(label);
    }

    /**
     * function that returns the information of Area and EnergeticSensor to be added to JPanel
     * @param text
     * @param energeticSensorDescriptor
     * @return
     */
    protected JComponent makePanel(String text, EnergeticSensorDescriptor energeticSensorDescriptor) {
        if(this.smarthomeClient.getAverageHashMap().containsKey(text.replace("Area #", ""))) {
            return new JLabel(String.format("%s\tEnergetic Consumption: %s\tUnit: %s\tAverage: %s", text, energeticSensorDescriptor.getValue(), energeticSensorDescriptor.getUnit(),
                    this.smarthomeClient.getAverageHashMap().get(text.replace("Area #", "")).right));
        }
        return new JLabel(String.format("%s\tEnergetic Consumption: %s\tUnit: %s\tAverage: %s", text, energeticSensorDescriptor.getValue(), energeticSensorDescriptor.getUnit(), 0.0));
    }

    /**
     * function that processes button events
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.averageButton) {
            if(this.areaIdTextField.getText() != null && !Objects.equals(this.areaIdTextField.getText(), "") && this.smarthomeClient.getEnergeticSensorDescriptorHashMap().containsKey(this.areaIdTextField.getText())){
                try{
                    Integer.parseInt(this.areaIdTextField.getText());
                    this.smarthomeClient.startAveragePolicy(this.areaIdTextField.getText());
                }catch (Exception ignored){}
            }
        } else if (e.getSource() == this.addNewLightButton) {
            if(this.areaIdTextField.getText() != null && !Objects.equals(this.areaIdTextField.getText(), "") &&
                    this.illuminationLevelTextField.getText() != null && !Objects.equals(this.illuminationLevelTextField.getText(), "")){
                try {
                    Integer.parseInt(this.areaIdTextField.getText());
                    Integer.parseInt(this.illuminationLevelTextField.getText());
                    if(Integer.parseInt(this.illuminationLevelTextField.getText()) >= 0 && Integer.parseInt(this.illuminationLevelTextField.getText()) <= 100 &&
                            this.smarthomeClient.getEnergeticSensorDescriptorHashMap().containsKey(this.areaIdTextField.getText())) {
                        this.smarthomeClient.addNewLight(this.client, this.areaIdTextField.getText(), null, (String) this.colorComboBox.getSelectedItem(), Integer.parseInt(this
                                .illuminationLevelTextField.getText()), Objects.equals(this.onOffComboBox.getSelectedItem(), "ON"));
                    }
                }catch (Exception ignored){}
            }
        } else if(e.getSource() == this.automaticTurnOffLightsCheckBox && this.automaticTurnOffLightsCheckBox.isSelected()){
            if (this.areaIdTextField.getText() != null && !Objects.equals(this.areaIdTextField.getText(), "") && this.smarthomeClient.getEnergeticSensorDescriptorHashMap().containsKey(this.areaIdTextField.getText())) {
                try {
                    Integer.parseInt(this.areaIdTextField.getText());
                    this.smarthomeClient.OnAutomaticTurnOffLight(this.client, this.areaIdTextField.getText());
                } catch (Exception ignored) {}
            }
        } else if(e.getSource() == this.automaticTurnOffLightsCheckBox && !this.automaticTurnOffLightsCheckBox.isSelected()){
            if(this.areaIdTextField.getText() != null && !Objects.equals(this.areaIdTextField.getText(), "") && this.smarthomeClient.getEnergeticSensorDescriptorHashMap().containsKey(this.areaIdTextField.getText())) {
                try {
                    Integer.parseInt(this.areaIdTextField.getText());
                    this.smarthomeClient.OffAutomaticTurnOffLight(this.client, this.areaIdTextField.getText());
                } catch (Exception ignored) {}
            }
        } else if(e.getSource() == this.changeLightButton){
            if(this.areaIdTextField.getText() != null && !Objects.equals(this.areaIdTextField.getText(), "") &&
                    this.illuminationLevelTextField.getText() != null && !Objects.equals(this.illuminationLevelTextField.getText(), "") &&
                    this.deviceIdTextField.getText() != null && !Objects.equals(this.deviceIdTextField.getText(), "")){
                try{
                    Integer.parseInt(this.areaIdTextField.getText());
                    Integer.parseInt(this.illuminationLevelTextField.getText());
                    Integer.parseInt(this.deviceIdTextField.getText());
                    if(Integer.parseInt(this.illuminationLevelTextField.getText()) >= 0 && Integer.parseInt(this.illuminationLevelTextField.getText()) <= 100 &&
                            this.smarthomeClient.getEnergeticSensorDescriptorHashMap().containsKey(this.areaIdTextField.getText()) &&
                            this.smarthomeClient.getEnergeticSensorDescriptorHashMap().get(this.areaIdTextField.getText()).getLightDescriptorMap().containsKey(this.deviceIdTextField.getText())){
                        this.smarthomeClient.changeLight(this.client, this.areaIdTextField.getText(), this.deviceIdTextField.getText(), (String) this.colorComboBox.getSelectedItem(), Integer.parseInt(this
                                .illuminationLevelTextField.getText()), Objects.equals(this.onOffComboBox.getSelectedItem(), "ON"));
                    }
                }catch (Exception ignored){}
            }
        }
    }

    public static void main(String[] args) throws MqttException {
        new Gui();
    }
}
