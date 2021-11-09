package tetris;

public class KeyDevice {
	private UsedKeys key;	
	private DeviceType deviceType;
	
	public KeyDevice( UsedKeys key, DeviceType deviceType) {
		this.key = key;
		this.deviceType = deviceType;
	}
	
	public UsedKeys getKey() {
		return key;
	}
	
	public void setKey(UsedKeys key) {
		this.key = key;
	}
	
	public DeviceType getDeviceType() {
		return deviceType;
	}
	
	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KeyDevice kd = (KeyDevice) o;
        return key == kd.key &&	deviceType == kd.deviceType;
    }
	
}
