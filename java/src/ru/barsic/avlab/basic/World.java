package ru.barsic.avlab.basic;

import ru.barsic.avlab.helper.ScalingUtil;

public class World {

	public static final double WORLD_WIDTH = 50;
	public static final double WORLD_HEIGHT = 20;
	private  static double deviceWidthSm;
	private  static double deviceHeightSm;
	public static final double ACCELERATION_OF_GRAVITY = 9.78;
	public static final double D_T = 0.01;

	public static double deviceX = 1;
	public static double deviceY = 3;


	public World(double deviceWidthInPix, double deviceHeightInPix) {
	}


	public static double getDeviceWidthSm() {
		return deviceWidthSm / ScalingUtil.getGlobalScaleFactor();
	}

	public static void setDeviceWidthSm(double deviceWidthSm) {
		World.deviceWidthSm = deviceWidthSm;
	}

	public static double getDeviceHeightSm() {
		return deviceHeightSm / ScalingUtil.getGlobalScaleFactor();
	}

	public static void setDeviceHeightSm(double deviceHeightSm) {
		World.deviceHeightSm = deviceHeightSm;
	}
}
