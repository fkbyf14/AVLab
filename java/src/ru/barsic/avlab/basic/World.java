package ru.barsic.avlab.basic;

import ru.barsic.avlab.helper.ScalingUtil;

public class World {

	public static final double WORLD_WIDTH = 50;
	public static final double WORLD_HEIGHT = 20;
	public static final double ACCELERATION_OF_GRAVITY = 9.78;
	public static final double D_T = 0.01;

	//положение верхней левой точки устройства относительно мировых координат
	public static double deviceX = 3;
	public static double deviceY = 8;

	private static double deviceWidthSm;
	private static double deviceHeightSm;


	private World() {
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
		if (ScalingUtil.minGlobalScaleFactor * WORLD_HEIGHT < deviceHeightSm)
			ScalingUtil.minGlobalScaleFactor = deviceHeightSm / WORLD_HEIGHT;
	}
}
