package ru.barsic.avlab.graphics;


import android.graphics.*;
import ru.barsic.avlab.helper.ScalingUtil;

public class Scale extends Painter implements IRotatable {

	public static final int VERTICAL_TYPE = 1;
	public static final int HORIZONTAL_TYPE = 2;
	public static final int CIRCULAR_TYPE = 3;
	public static final int TEXT_SIZE = 8;
	protected double min, max;
	protected double step = 1;
	protected int count = 1;
	protected int type;
	protected int signInterval = 10;
	protected int signCount = 2;
	int color;
	protected String[] str;
	protected double[][] defaultXArray, defaultYArray;
	protected int[][] xArray, yArray;
	protected double fontScale = 1.0;

	public Scale(Painter holder, int type, double min, double max, double step, int color) {
		super(holder);
		this.type = type;
		this.min = min;
		this.max = max;
		this.step = step;
		this.color = color;
		this.count = (int) ((max - min) / step) + 1;
		setSignInterval(10);
		xArray = new int[2][2 * count];
		yArray = new int[2][2 * count];
		defaultXArray = new double[2][2 * count];
		defaultYArray = new double[2][2 * count];
		updatePoints();
	}

	@Override
	public void updatePoints() {
		if (type == 0) {
			return;
		}
		double d;
		int j = 0;
		switch (type) {
			case VERTICAL_TYPE:
				fontScale = size.width / 10.0;
				d = 0.5 * size.height / (count - 1);
				for (int i = 0; i < 2 * count; i += 2) {
					defaultXArray[0][i] = getPos().x;
					defaultYArray[0][i] = defaultYArray[0][i + 1] = getPos().y + size.height - d * i;
					if (i % (2 * signInterval) == 0) {
						defaultXArray[0][i + 1] = getPos().x + size.width;
						defaultXArray[1][j] = getPos().x + size.width + 2;
						if (j == 0) {
							defaultYArray[1][j] = defaultYArray[0][i];
						} else if (j == signCount - 1) {
							defaultYArray[1][j] = defaultYArray[0][i];
						} else {
							defaultYArray[1][j] = defaultYArray[0][i];
						}
						j++;
					} else {
						defaultXArray[0][i + 1] = getPos().x + size.width / 2.0;
					}
				}
				for (int k = 0; k < xArray.length; k++) {
					for (int i = 0; i < xArray[k].length; i++) {
						xArray[k][i] = (int) (Math.round(defaultXArray[k][i]));
						yArray[k][i] = (int) (Math.round(defaultYArray[k][i]));
					}
				}
				break;
			case HORIZONTAL_TYPE:
				fontScale = size.height / 10.0;
				d = 0.5 * size.width / (count - 1);
				for (int i = 0; i < 2 * count; i += 2) {
					defaultYArray[0][i] = getPos().y;
					defaultXArray[0][i] = defaultXArray[0][i + 1] = getPos().x + d * i;
					if (i % (2 * signInterval) == 0) {
						defaultYArray[0][i + 1] = getPos().y + size.height;
						defaultYArray[1][j] = getPos().y + size.height;
						if (j == 0) {
							defaultXArray[1][j] = defaultXArray[0][i];
						} else if (j == signCount - 1) {
							defaultXArray[1][j] = defaultXArray[0][i];
						} else {
							defaultXArray[1][j] = defaultXArray[0][i];
						}
						j++;
					} else {
						defaultYArray[0][i + 1] = getPos().y + size.height / 2;
					}
				}
				for (int k = 0; k < xArray.length; k++) {
					for (int i = 0; i < xArray[k].length; i++) {
						xArray[k][i] = (int) (Math.round(defaultXArray[k][i]));
						yArray[k][i] = (int) (Math.round(defaultYArray[k][i]));
					}
				}
				break;
			case CIRCULAR_TYPE:
				fontScale = size.height / 150.0;
				int cx = getPos().x + size.width / 2;
				int cy = getPos().y + size.height / 2;
				double r0 = size.width / 2.0;
				double r1 = r0 - size.width / 10.0;
				double r2 = r0 - size.width / 20.0;
				d = 1;
				for (int i = 0; i < 2 * count; i += 2) {
					defaultYArray[0][i] = cy + r1 * Math.sin(Math.PI * i * step / (360));
					defaultXArray[0][i] = cx + r1 * Math.cos(Math.PI * i * step / (360));
					if (i % (2 * signInterval) == 0) {
						defaultXArray[0][i + 1] = cx + r0 * Math.cos(Math.PI * i * step / (360));
						defaultYArray[0][i + 1] = cy + r0 * Math.sin(Math.PI * i * step / (360));
						defaultXArray[1][j] = cx + (r0 + d) * Math.cos(Math.PI * i * step / (360)) - d;
						defaultYArray[1][j] = cy + (r0 + d) * Math.sin(Math.PI * i * step / (360)) + d;
						j++;
					} else {
						defaultXArray[0][i + 1] = (int) (cx + r2 * Math.cos(Math.PI * i * step / (360)));
						defaultYArray[0][i + 1] = (int) (cy + r2 * Math.sin(Math.PI * i * step / (360)));
					}
				}
				for (int k = 0; k < xArray.length; k++) {
					for (int i = 0; i < xArray[k].length; i++) {
						xArray[k][i] = (int) (Math.round(defaultXArray[k][i]));
						yArray[k][i] = (int) (Math.round(defaultYArray[k][i]));
					}
				}
				break;
		}
	}


	@Override
	public void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(color);
		for (int i = 0; i < 2 * count; i += 2) {
			canvas.drawLine(xArray[0][i], yArray[0][i], xArray[0][i + 1], yArray[0][i + 1], paint);
		}

		paint.setTextSize((float) (TEXT_SIZE * ScalingUtil.getGlobalScaleFactor()));
		for (int i = 0; i < signCount; i++) {
			canvas.drawText(str[i], xArray[1][i], yArray[1][i], paint);
		}
	}

	@Override
	public void changePosition(int dx, int dy) {
		for (int i = 0; i < xArray.length; i++) {
			for (int j = 0; j < xArray[i].length; j++) {
				xArray[i][j] += dx;
				yArray[i][j] += dy;
				defaultXArray[i][j] += dx;
				defaultYArray[i][j] += dy;
			}
		}
	}

	@Override
	public double getAngle() {
		if (holder instanceof IRotatable) {
			return ((IRotatable) holder).getAngle();
		}
		return 0;
	}

	@Override
	public void rotate(double cx, double cy, double angle) {
		double cos = Math.cos(angle), sin = Math.sin(angle);
		if (sin == 0 || cos == 1) {
			for (int k = 0; k < xArray.length; k++) {
				for (int i = 0; i < xArray[k].length; i++) {
					xArray[k][i] = (int) (Math.round(defaultXArray[k][i]));
					yArray[k][i] = (int) (Math.round(defaultYArray[k][i]));
				}
			}
		} else {
			for (int i = 0; i < xArray.length; i++) {
				for (int j = 0; j < xArray[i].length; j++) {
					xArray[i][j] = (int) (Math.round((defaultXArray[i][j] - cx) * cos - (defaultYArray[i][j] - cy) * sin) + cx);
					yArray[i][j] = (int) (Math.round((defaultYArray[i][j] - cy) * cos + (defaultXArray[i][j] - cx) * sin) + cy);
				}
			}
		}
	}

	public void setSignInterval(int signInterval) {
		this.signInterval = signInterval;
		signCount = count / signInterval + 1;
		str = new String[signCount];
		for (int i = 0; i < signCount; i++) {
			str[i] = Integer.toString((int) (min + i * step * signInterval));
		}
	}
}
