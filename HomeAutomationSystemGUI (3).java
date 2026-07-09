import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

// Main Class
public class HomeAutomationSystemGUI extends JFrame {

    private Light light = new Light();
    private Fan fan = new Fan();
    private AC ac = new AC();
    private Heater heater = new Heater();
    private SecuritySystem securitySystem = new SecuritySystem();
    private Sprinkler sprinkler = new Sprinkler();
    private Scheduler scheduler = new Scheduler();

    private JTextArea statusArea;

    public HomeAutomationSystemGUI() {
        setTitle("Smart Home Automation System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 600);
        setLayout(new BorderLayout());

        scheduler.runScheduler(this::updateStatuses);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(8, 2, 10, 10));

        addButton(buttonPanel, "Toggle Light", () -> {
            light.toggle();
            updateStatuses();
        });
        addButton(buttonPanel, "Set Light Brightness", () -> {
            if (light.isOn()) {
                String input = JOptionPane.showInputDialog("Enter brightness (0-100):");
                if (input != null) {
                    int brightness = Integer.parseInt(input);
                    if (brightness >= 0 && brightness <= 100) {
                        light.setBrightness(brightness);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid brightness value.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Light is OFF. Turn it ON first.");
            }
            updateStatuses();
        });
        addButton(buttonPanel, "Toggle Fan", () -> {
            fan.toggle();
            updateStatuses();
        });
        addButton(buttonPanel, "Set Fan Speed", () -> {
            if (fan.isOn()) {
                String input = JOptionPane.showInputDialog("Enter fan speed (0-5):");
                if (input != null) {
                    int speed = Integer.parseInt(input);
                    if (speed >= 0 && speed <= 5) {
                        fan.setSpeed(speed);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid speed value.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Fan is OFF. Turn it ON first.");
            }
            updateStatuses();
        });
        addButton(buttonPanel, "Toggle AC", () -> {
            ac.toggle();
            updateStatuses();
        });
        addButton(buttonPanel, "Set AC Temperature", () -> {
            if (ac.isOn()) {
                String input = JOptionPane.showInputDialog("Enter AC temperature (16-30°C):");
                if (input != null) {
                    int temp = Integer.parseInt(input);
                    if (temp >= 16 && temp <= 30) {
                        ac.setTemperature(temp);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid temperature.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "AC is OFF. Turn it ON first.");
            }
            updateStatuses();
        });
        addButton(buttonPanel, "Toggle Heater", () -> {
            heater.toggle();
            updateStatuses();
        });
        addButton(buttonPanel, "Set Heater Temperature", () -> {
            if (heater.isOn()) {
                String input = JOptionPane.showInputDialog("Enter Heater temperature (20-35°C):");
                if (input != null) {
                    int temp = Integer.parseInt(input);
                    if (temp >= 20 && temp <= 35) {
                        heater.setTemperature(temp);
                    } else {
                        JOptionPane.showMessageDialog(this, "Invalid temperature.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Heater is OFF. Turn it ON first.");
            }
            updateStatuses();
        });
        addButton(buttonPanel, "Toggle Security System", () -> {
            securitySystem.toggle();
            updateStatuses();
        });
        addButton(buttonPanel, "Toggle Sprinkler", () -> {
            sprinkler.toggle();
            updateStatuses();
        });
        addButton(buttonPanel, "Reset All Devices", () -> {
            resetAllDevices();
            updateStatuses();
        });
        addButton(buttonPanel, "Schedule a Device", this::scheduleDevice);

        statusArea = new JTextArea();
        statusArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusArea);

        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);

        updateStatuses();
        setVisible(true);
    }

    private void addButton(JPanel panel, String label, Runnable action) {
        JButton button = new JButton(label);
        button.addActionListener(e -> action.run());
        panel.add(button);
    }

    private void updateStatuses() {
        StringBuilder status = new StringBuilder();
        light.show(status);
        fan.show(status);
        ac.show(status);
        heater.show(status);
        securitySystem.show(status);
        sprinkler.show(status);
        statusArea.setText(status.toString());
    }

    private void resetAllDevices() {
        light = new Light();
        fan = new Fan();
        ac = new AC();
        heater = new Heater();
        securitySystem = new SecuritySystem();
        sprinkler = new Sprinkler();
    }

    private void scheduleDevice() {
        String[] options = {"Fan", "AC", "Heater", "Light", "Sprinkler"};
        String deviceName = (String) JOptionPane.showInputDialog(this, "Select Device:",
                "Schedule Device", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        Device device = switch (deviceName) {
            case "Fan" -> fan;
            case "AC" -> ac;
            case "Heater" -> heater;
            case "Light" -> light;
            case "Sprinkler" -> sprinkler;
            default -> null;
        };

        if (device == null) return;

        int onOff = JOptionPane.showConfirmDialog(this, "Turn ON device?", "Schedule", JOptionPane.YES_NO_OPTION);
        boolean turnOn = (onOff == JOptionPane.YES_OPTION);

        String timeStr = JOptionPane.showInputDialog("Enter time (HH:mm 24-hour format):");
        try {
            LocalTime time = LocalTime.parse(timeStr);

            int duration = 0;
            if (turnOn) {
                String durStr = JOptionPane.showInputDialog("Enter duration in minutes (0 for no auto-OFF):");
                if (durStr != null && !durStr.isEmpty()) {
                    duration = Integer.parseInt(durStr);
                }
            }
            scheduler.schedule(device, turnOn, time, duration);
            JOptionPane.showMessageDialog(this, "Task scheduled successfully.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid time format.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HomeAutomationSystemGUI::new);
    }
}

// ================= Devices =================
abstract class Device {
    protected boolean isOn;

    public Device() {
        this.isOn = false;
    }

    public void toggle() {
        isOn = !isOn;
    }

    public boolean isOn() {
        return isOn;
    }

    public abstract String getStatus();

    public void show(StringBuilder sb) {
        sb.append(getStatus()).append("\n");
    }
}

class Light extends Device {
    private int brightness;

    public Light() {
        brightness = 100;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    @Override
    public String getStatus() {
        return "Light: " + (isOn ? "ON (Brightness: " + brightness + "%)" : "OFF");
    }
}

class Fan extends Device {
    private int speed;

    public Fan() {
        speed = 3;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public String getStatus() {
        return "Fan: " + (isOn ? "ON (Speed: " + speed + ")" : "OFF");
    }
}

class AC extends Device {
    private int temperature;

    public AC() {
        temperature = 24;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public String getStatus() {
        return "AC: " + (isOn ? "ON (Temperature: " + temperature + "°C)" : "OFF");
    }
}

class Heater extends Device {
    private int temperature;

    public Heater() {
        temperature = 28;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    @Override
    public String getStatus() {
        return "Heater: " + (isOn ? "ON (Temperature: " + temperature + "°C)" : "OFF");
    }
}

class SecuritySystem extends Device {
    @Override
    public String getStatus() {
        return "Security System: " + (isOn ? "ENABLED" : "DISABLED");
    }
}

class Sprinkler extends Device {
    @Override
    public String getStatus() {
        return "Sprinkler System: " + (isOn ? "ON" : "OFF");
    }
}

// =============== Scheduler ===============
class Scheduler {
    private static class ScheduledTask {
        Device device;
        boolean turnOn;
        LocalTime time;
        int durationMinutes;

        ScheduledTask(Device device, boolean turnOn, LocalTime time, int durationMinutes) {
            this.device = device;
            this.turnOn = turnOn;
            this.time = time;
            this.durationMinutes = durationMinutes;
        }
    }

    private final List<ScheduledTask> tasks = Collections.synchronizedList(new ArrayList<>());

    public void schedule(Device device, boolean turnOn, LocalTime time, int durationMinutes) {
        tasks.add(new ScheduledTask(device, turnOn, time, durationMinutes));

        if (turnOn && durationMinutes > 0) {
            LocalTime offTime = time.plusMinutes(durationMinutes);
            tasks.add(new ScheduledTask(device, false, offTime, 0));
        }
    }

    public void runScheduler(Runnable updateUI) {
        Thread thread = new Thread(() -> {
            while (true) {
                LocalTime now = LocalTime.now().withSecond(0).withNano(0);
                synchronized (tasks) {
                    Iterator<ScheduledTask> iterator = tasks.iterator();
                    while (iterator.hasNext()) {
                        ScheduledTask task = iterator.next();
                        if (task.time.equals(now)) {
                            if (task.turnOn && !task.device.isOn()) {
                                task.device.toggle();
                            } else if (!task.turnOn && task.device.isOn()) {
                                task.device.toggle();
                            }
                            iterator.remove();
                            updateUI.run();
                        }
                    }
                }
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
