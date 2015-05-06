package ru.barsic.avlab.molecular;

import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.basic.World;
import ru.barsic.avlab.graphics.Painter;
import ru.barsic.avlab.graphics.Scale;
import ru.barsic.avlab.physics.Computation;
import ru.barsic.avlab.physics.IGluer;
import ru.barsic.avlab.physics.Scene;

public class Thermometer extends PhysObject implements IGluer, IMeasuring, VolumeFunction {
	private final WaterTemperatureChangeListener listenerInstance = new WaterTemperatureChangeListener();
	private double temperature = World.ATMOSPHERE_TEMPERATURE;
	private double maxT = 100;
	private double minT = 0;
	private double step = (maxT - minT) / 100;
	private volatile boolean inWater;
	private double immersedVolume;

	public Thermometer(double x, double y, double width, double height, double mass) {
		super(x, y, width, height, mass);
		painter = new ThermometerPathPainter(this);
	}

	@Override
	public int[][] getGlueyPolygon() {
		return new int[][]{((ThermometerPathPainter) painter).xArray, ((ThermometerPathPainter) painter).yArray};
	}

	@Override
	public int[][] getMeasuringPolygon() {
		return new int[][]{((ThermometerPathPainter) painter).xArray, ((ThermometerPathPainter) painter).yArray};
	}

	@Override
	public double volumeFunction(double height) {
		//todo: объем градусника в зависимости от высоты (высота 0 соответствует самой нижней точке)
		return 0;
	}

	private class ThermometerPathPainter extends Painter {

		Scale lineScale;
		private int[] xArray, yArray;

		public ThermometerPathPainter(PhysObject obj) {
			super(obj);
			xArray = new int[5];
			yArray = new int[5];
			setMovable(true);
			lineScale = new Scale(this, Scale.VERTICAL_TYPE, minT, maxT, step, Color.BLACK);
			updatePoints();
			setZIndex(10);
		}

		@Override
		public void updatePoints() {
			lineScale.setPos(getPos().x + size.width / 3, getPos().y + 15);
			lineScale.setSize(size.width / 4, size.height - 20);
			xArray = new int[]{getPos().x, getPos().x + size.width, getPos().x + size.width, getPos().x, getPos().x + size.width / 2};
			yArray = new int[]{getPos().y, getPos().y, getPos().y + size.height, getPos().y + size.height, calcHeightHg()};
		}

		@Override
		public void changePosition(int dx, int dy) {
			for (int i = 0; i < 5; i++) {
				xArray[i] += dx;
				yArray[i] += dy;
			}
		}

		@Override
		public void onDraw(Canvas canvas) {
			Path path = new Path();
			Paint paint = new Paint();
			paint.setColor(Color.rgb(171, 255, 253));

			int peakWidth = size.width / 4;
			int peakHeight = size.height / 5;
			int peakRad = size.width / 8;
			int rad = (size.width - peakWidth) / 2;


			path.moveTo(xArray[0], yArray[0] + rad);
			path.quadTo(xArray[0], yArray[0], xArray[0] + rad, yArray[0]);
			path.lineTo(xArray[1] - rad, yArray[1]);
			path.quadTo(xArray[1], yArray[1], xArray[1], yArray[1] + rad);
			path.lineTo(xArray[2], yArray[2] - rad);
			path.quadTo(xArray[2], yArray[2], xArray[2] - rad, yArray[2]);
			path.lineTo(xArray[4] + peakWidth / 2, yArray[2] + peakHeight);
			path.arcTo(new RectF(xArray[4] - peakWidth / 2, yArray[2] + peakHeight - peakRad,
					xArray[4] + peakWidth / 2, yArray[2] + peakHeight + peakRad), 0, 180);
			path.lineTo(xArray[4] - peakWidth / 2, yArray[2]);
			path.quadTo(xArray[3], yArray[3], xArray[3], yArray[3] - rad);
			path.lineTo(xArray[0], yArray[0] + rad);
			canvas.drawPath(path, paint);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawPath(path, paint);

			paint.setColor(Color.rgb(147, 219, 230));
			paint.setStyle(Paint.Style.FILL);
			RectF rect = new RectF(xArray[4] - peakWidth / 4, yArray[0] + peakRad, xArray[4] + peakWidth / 4, yArray[2] + peakHeight);
			canvas.drawRoundRect(rect, 1, 1, paint);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawRoundRect(rect, 1, 1, paint);

			paint.setColor(Color.RED);
			paint.setStyle(Paint.Style.FILL);
			rect = new RectF(xArray[4] - peakWidth / 4, yArray[4], xArray[4] + peakWidth / 4, yArray[2] + peakHeight);
			canvas.drawRoundRect(rect, 1, 1, paint);
			lineScale.onDraw(canvas);

			Path pathPeak = new Path();
			paint.setColor(Color.rgb(201, 192, 187));
			paint.setStyle(Paint.Style.FILL);
			pathPeak.moveTo(xArray[4] + peakWidth / 2, yArray[2] + peakHeight * 2 / 3);
			pathPeak.lineTo(xArray[4] + peakWidth / 2, yArray[2] + peakHeight);
			pathPeak.arcTo(new RectF(xArray[4] - peakWidth / 2, yArray[2] + peakHeight - peakRad,
					xArray[4] + peakWidth / 2, yArray[2] + peakHeight + peakRad), 0, 180);
			pathPeak.lineTo(xArray[4] - peakWidth / 2, yArray[2] + peakHeight * 2 / 3);
			pathPeak.lineTo(xArray[4] + peakWidth / 2, yArray[2] + peakHeight * 2 / 3);
			canvas.drawPath(pathPeak, paint);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawPath(pathPeak, paint);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			super.onTouch(v, event);
			if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
				if (getParent() == null)
					moveToDefault();
			}
			if (event.getAction() == MotionEvent.ACTION_MOVE) {
				Glass glass = null;
				for (Glass g : Scene.glasses) {
					if (Computation.intersect(getMeasuringPolygon(), g.getActivePolygon()))
						glass = g;

					if (glass != null)
						break;
				}
				if (glass == null) {
					inWater = false;
					return true;
				}
				boolean inWaterOld = inWater;
				inWater = object.y + object.height > glass.getWaterLevel();
				if (inWaterOld != inWater) {
					if (inWater)
						glass.getWater().addChangeListener(listenerInstance);
					else
						glass.getWater().removeChangeListener(listenerInstance);
				}
				if (!inWater)
					return true;
				immersedVolume = glass.getWater().getDivingDepth(glass.getWaterLevel(), object.y + object.height,
						immersedVolume, Thermometer.this, glass);
			}
			return true;
		}

		private int calcHeightHg() {
			int y1 = lineScale.getPos().y;
			int y2 = lineScale.getPos().y + lineScale.getSize().height;
			return (int) ((y1 * (temperature - minT) + y2 * (maxT - temperature)) /
					(maxT - minT));
		}
	}

	private class WaterTemperatureChangeListener implements WaterChangeListener {
		@Override
		public void change(Water water) {
			if (water.getTemperature() == temperature)
				return;

			//todo тут надо создать задачу, которая постепенно меняет температуру на термометре, пока она не станет равной температуре воды
		}
	}
}
