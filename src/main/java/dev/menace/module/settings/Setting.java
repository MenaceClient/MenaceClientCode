package dev.menace.module.settings;

public class Setting {

	private final String name;
	private boolean visible;
	
	public Setting(String name, boolean visible) {
		this.name = name;
		this.visible = visible;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getName() {
		return name;
	}

}
