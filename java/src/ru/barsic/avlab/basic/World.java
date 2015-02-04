package ru.barsic.avlab.basic;

import ru.barsic.avlab.helper.ScalingUtil;

public class World {

	public static final double WORLD_WIDTH = 70;
	public static final double WORLD_HEIGHT = 21;
	public static final double ACCELERATION_OF_GRAVITY = 9.8;
	public static final double D_T = 0.01;

	public static double deviceX = 1;
	public static double deviceY = 3;

	private final double deviceHeight;
	private final double deviceWidth;

	public World(double deviceWidthInPix, double deviceHeightInPix) {
		this.deviceWidth = deviceWidthInPix / ScalingUtil.pixToSmX;
		this.deviceHeight = deviceHeightInPix / ScalingUtil.pixToSmY;
	}
}
