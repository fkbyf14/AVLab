package ru.barsic.avlab.helper;

import ru.barsic.avlab.graphics.DrawView;
import ru.barsic.avlab.graphics.Painter;

public class ScalingUtil {
	//сантимеров в дюйме

	public static final double WORLD_WIDTH = 70;
	public static final double WORLD_HEIGHT = 21;
	public static final double INCH_TO_SM = 2.54;
	public static double pixToSmX;
	public static double pixToSmY;

	private static double globalScaleFactor = 1.0; // определяет соотношение между 1 м и 1 пикселем

	public static int scalingRealSizeX(double size) {
		return (int)Math.round(size * globalScaleFactor * pixToSmX);
	}

	public static int scalingRealSizeY(double size) {
		return (int)Math.round(size * globalScaleFactor * pixToSmY);
	}

	public static void setGlobalScaleFactor(double globalScaleFactor) {
		ScalingUtil.globalScaleFactor = globalScaleFactor;
		for (Painter pai : DrawView.painters) {
			if (pai.getHolder() == null) {
				pai.updateSize();
			}
		}
	}

	public static double getGlobalScaleFactor() {
		return globalScaleFactor;
	}
}
