import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Armin on 7/20/2016.
 */
public class RemoteDeviceInfo {

    public final SimpleStringProperty deviceName = new SimpleStringProperty("");
    public final SimpleStringProperty deviceAddress = new SimpleStringProperty("");

    public String getDeviceAddress() {
        return deviceAddress.get();
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress.set(deviceAddress);
    }

    public String getDeviceName() {
        return deviceName.get();
    }

    public void setDeviceName(String deviceName) {
        this.deviceName.set(deviceName);
    }

    public RemoteDeviceInfo(String name,String address){
        setDeviceName(name);
        setDeviceAddress(address);
    }

    public RemoteDeviceInfo(){
        this("","");
    }
}
