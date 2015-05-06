package ru.barsic.avlab.molecular;

import ru.barsic.avlab.basic.World;

import java.util.ArrayList;

public class Water {

	public static final double DENSITY = 1000;

	private double volumeWater;
	private double volumeObjects;
	private double temperature = World.ATMOSPHERE_TEMPERATURE;
	private final ArrayList<WaterChangeListener> listeners = new ArrayList<>();

	public Water(double volumeWater) {
		this.volumeWater = volumeWater;
	}

	public void addChangeListener(WaterChangeListener listener) {
		listeners.add(listener);
		listener.change(this);
	}

	public void removeChangeListener(WaterChangeListener listener) {
		listeners.remove(listener);
		listener.change(this);
	}

	public void changeVolumeWater(double delta) {
		volumeWater += delta;
		notifyListeners();
	}

	public void changeVolumeObjects(double delta) {
		volumeObjects += delta;
		notifyListeners();
	}

	public void changeTemperature(double delta) {
		temperature += delta;
	}

	public double getMass() {
		return DENSITY * volumeWater;
	}

	public double getTemperature() {
		return temperature;
	}

	public double getVolumeWater() {
		return volumeWater;
	}

	public double getVolumeObjects() {
		return volumeObjects;
	}

	public double getDivingDepth(double waterLevel, double objectBottomPos, double immersedObjectVolumeOld,
							   VolumeFunction immersedObject, VolumeFunction waterObj) {
		if (objectBottomPos <= waterLevel)
			return 0;
		double newWaterLevel = 0;//todo: вычислить уровень воды
		double delta = immersedObject.volumeFunction(newWaterLevel) - immersedObjectVolumeOld;
		changeVolumeObjects(delta);
		return delta;
	}

	private void notifyListeners() {
		for (WaterChangeListener listener : listeners)
			listener.change(this);
	}
}
