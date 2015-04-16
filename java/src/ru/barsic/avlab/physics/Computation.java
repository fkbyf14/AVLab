package ru.barsic.avlab.physics;

public class Computation {

	private Computation() {
	}

	/**
	 * Определяет принадлежность точки с координатами (x, y) к полигону,
	 * построенному по точкам из xArray и yArray
	 *
	 * @param xArray - X координаты точек полигона
	 * @param yArray - Y координаты точек полигона
	 * @param x      - X координата точеки
	 * @param y      - Y координата точеки
	 * @return - true or false
	 */
	public static boolean isChoicesChoice(int[] xArray, int[] yArray, int x, int y) {
		int j = 0;
		double k;
		int x1 = xArray[xArray.length - 1];
		int y1 = yArray[yArray.length - 1];
		int x2, y2;
		for (int i = 0; i < xArray.length; i++) {
			x2 = xArray[i];
			y2 = yArray[i];
			if (y == y2) {
				continue;
			}
			if ((y <= y1 || y <= y2) && (y >= y1 || y >= y2)) {
				k = x1 + 1.0 * ((x2 - x1) * (y - y1)) / (y2 - y1);
				if (k < x && k <= Math.max(x1, x2) && k >= Math.min(x1, x2)) {
					j++;
				}
			}
			y1 = y2;
			x1 = x2;
		}
		return ((j & 1) != 0);
	}

	public static double[] rotatePoint(double x, double y, double x0, double y0, double a) {
		double[] arrx1y1 = new double[2];
		double b;
		if (x == x0) {
			if (y > y0) {
				b = -Math.PI / 2;
			} else {
				b = Math.PI / 2;
			}
		} else {
			b = Math.atan(((y0 - y) / (x - x0)));
			if (x < x0) {
				b = b + Math.PI;
			}
		}
		double ca = Math.cos(a + b);
		double sa = Math.sin(a + b);
		double l = Math.sqrt((x0 - x) * (x0 - x) + (y0 - y) * (y0 - y));
		arrx1y1[0] = x0 + l * ca;
		arrx1y1[1] = y0 - l * sa;
		return arrx1y1;
	}

	public static double[][] rotateRect(double xS, double yS, double widthS,
										double heightS, double x0, double y0, double a) {
		double[][] arrRect = new double[][]{
				{xS, yS},
				{xS + widthS, yS},
				{xS + widthS, yS + heightS},
				{xS, yS + heightS},};
		for (int i = 0; i <= 3; i++) {
			double temp = arrRect[i][0];
			arrRect[i][0] = rotatePoint(arrRect[i][0], arrRect[i][1], x0, y0, a)[0];
			arrRect[i][1] = rotatePoint(temp, arrRect[i][1], x0, y0, a)[1];
		}
		return arrRect;
	}

	public static double[] rotateCircle(double x, double y, double width, double x0,
										double y0, double da) {
		double[] newXY = new double[2];
		double xc, yc; //координаты центра окружности
		xc = rotatePoint(x + width / 2, y + width / 2, x0, y0, da)[0];
		yc = rotatePoint(x + width / 2, y + width / 2, x0, y0, da)[1];
		newXY[0] = xc - width / 2;
		newXY[1] = yc - width / 2;
		return newXY;
	}

	public static boolean intersect(int[][] arraysParent, int[][] arraysChild) {
		int[] xArrayParent = arraysParent[0];
		int[] yArrayParent = arraysParent[1];
		int[] xArrayChild = arraysChild[0];
		int[] yArrayChild = arraysChild[1];

		if (xArrayParent[1] >= xArrayChild[0] && xArrayChild[1] >= xArrayParent[0] && yArrayChild[0] <= yArrayParent[3] && yArrayChild[3] >= yArrayParent[0]) {
			return true;
		}
		return false;
	}

}
	/*
	 public void rotate(double cx, double cy, double angle) {
     double da = angle - this.angle;
     this.angle = angle;
     rotate(Math.cos(angle), Math.sin(angle));
     double x = (center.x - cx) * Math.cos(da) - (center.y - cy) * Math.sin(da) + cx - center.x;
     double y = (center.y - cy) * Math.cos(da) + (center.x - cx) * Math.sin(da) + cy - center.y;
     moveBy((int) x, (int) y);
     //        object.setPos(x/GraphScene.globalScaleFactor, y/GraphScene.globalScaleFactor);
     }

     public void rotate(double cos, double sin) {
     if (sin == 0 || cos == 1) {
     compare();
     } else {
     for (int i = 0; i < xArray.length; i++) {
     for (int j = 0; j < xArray[i].length; j++) {
     xArray[i][j] = (int) (Math.round((defaultXArray[i][j] - center.x) * cos - (defaultYArray[i][j] - center.y) * sin) + center.x);
     yArray[i][j] = (int) (Math.round((defaultYArray[i][j] - center.y) * cos + (defaultXArray[i][j] - center.x) * sin) + center.y);
     }
     }
     }
     atr.setTransform(fontScale * cos, fontScale * sin, -fontScale * sin, fontScale * cos, 0, 0);
     font = font.deriveFont(atr);
     for (Painter p : inside) {
     p.rotate(cos, sin);
     }
     }*/