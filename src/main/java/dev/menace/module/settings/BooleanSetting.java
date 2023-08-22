package dev.menace.module.settings;

public class BooleanSetting extends Setting {

	private boolean toggled;
	
	public BooleanSetting(String name, boolean visible, boolean toggled) {
		super(name, visible);
		this.toggled = toggled;
	}

	public boolean getValue() {
		return toggled;
	}

	public void setValue(boolean toggled) {
		this.toggled = toggled;
	}

}
