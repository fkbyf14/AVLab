package ru.barsic.avlab.molecular;

import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.basic.TouchListener;
import ru.barsic.avlab.graphics.Painter;
import ru.barsic.avlab.helper.Logging;
import ru.barsic.avlab.helper.ScalingUtil;
import ru.barsic.avlab.mechanics.IWeighing;
import ru.barsic.avlab.physics.Computation;
import ru.barsic.avlab.physics.IParent;
import ru.barsic.avlab.physics.Scene;

public class Glass extends PhysObject implements IWeighing, IParent, VolumeFunction {

	private final WaterChangeListener INSTANCE_LISTENER = new GlassWaterListener();

	private Water water;
	private double waterLevel;


	public Glass(double x, double y, double width, double height, double mass) {
		this(x, y, width, height, mass, 0);
	}

	public Glass(double x, double y, double width, double height, double mass, double waterVolume) {
		super(x, y, width, height, mass);
		painter = new GlassPainter(this);
		if (waterVolume > 0) {
			water = new Water(waterVolume);
			water.addChangeListener(INSTANCE_LISTENER);
		}
		Scene.glasses.add(this);
	}

	@Override
	public int[][] getWeighingPolygon() {
		int[] x = ((GlassPainter) painter).xArray;
		int[] y = ((GlassPainter) painter).yArray;
		return new int[][]{new int[]{x[2], x[1], x[0], x[3]}, new int[]{y[0], y[1], y[2], y[3]}};
	}

	@Override
	public boolean isInAria(int x, int y) {
		PhysObject object = TouchListener.selected.object;
		return object instanceof IMeasuring &&
				Computation.intersect(getActivePolygon(), ((IMeasuring) object).getMeasuringPolygon());
	}

	@Override
	public int[][] getActivePolygon() {
		return new int[][]{((GlassPainter) painter).xArray, ((GlassPainter) painter).yArray};

	}
	@Override
	public boolean attach(PhysObject child) {
		if (child instanceof IMeasuring && getChildren().isEmpty() && super.attach(child)) {
			child.getPainter().setPos(((GlassPainter)painter).xArray[0] + painter.getSize().width / 2 - child.getPainter().getSize().width / 2, (((GlassPainter)painter).yArray[2] - child.getPainter().getSize().height - child.getPainter().getSize().height / 5 - 10));
			child.getPainter().setZIndex((int) (painter.getZIndex() - 0.5));
//			int currentY = painter.getPos().y + child.getPainter().getSize().height + child.getPainter().getSize().height / 5;
//			if(painter.getPos().y >= ((GlassPainter)painter).yArray[2])
//				currentY = ((GlassPainter)painter).yArray[2];


			return true;

		}
		Logging.log("Attach Child of Glass", child, "x = " + x + ", y = " + y);
		return false;
	}

	@Override
	public boolean detach(PhysObject child) {
		super.detach(child);

		return true;
	}
	void calcWaterLever(double volumeWater, int radBottom, int radUp){
		double q = - 3*volumeWater/Math.PI/Math.tan((radUp - radBottom)/height)/Math.tan((radUp - radBottom)/height);
		double p = - radBottom*radBottom/Math.tan((radUp - radBottom)/height)/Math.tan((radUp - radBottom)/height);
		double d = q*q + 4*p*p*p/27;
		waterLevel = Math.pow((-q + Math.sqrt(d))/2, 1/3) - p/3/ Math.pow((-q + Math.sqrt(d))/2, 1/3) - radBottom/Math.tan((radUp - radBottom)/height);
	}

	public double getWaterLevel() {
		return waterLevel;
	}

	public Water getWater() {
		return water;
	}

	@Override
	public double volumeFunction(double height) {
		double radBottomSm = ScalingUtil.scalingXToRealSize(((GlassPainter) painter).radBottom);
		double radUpSm = ScalingUtil.scalingXToRealSize(((GlassPainter) painter).radUp);
		double volumeGlass = Math.PI*height*(radBottomSm*radBottomSm + radBottomSm*radUpSm + radUpSm*radUpSm)/3;
		return 0;
	}

	private class GlassPainter extends Painter {
		private  int xArray[];
		private  int yArray[];
		private  int levelXLeft;
		private  int levelXRight;
		private  int waterLever;
		private  int radBottom;
		private  int radUp;


		public GlassPainter(PhysObject obj) {
			super(obj);
			xArray = new int[4];
			yArray = new int[4];
			setMovable(true);
			updatePoints();
			setZIndex(10);
		}

		public void updateSize() {
			super.updateSize();
			updateWaterLevel();
		}

		private void updateWaterLevel() {
			waterLevel = ScalingUtil.scalingRealSizeToY(Glass.this.waterLevel);
		}

		@Override
		public void updatePoints() {
			xArray = new int[]{getPos().x, getPos().x + size.width, getPos().x + size.width - size.width / 4, getPos().x + size.width / 4};
			yArray = new int[]{getPos().y, getPos().y, getPos().y + size.height, getPos().y + size.height};
			waterLever = yArray[0] + getSize().height / 2;
			levelXLeft = (xArray[3]*yArray[0] - xArray[0]*yArray[3] - waterLever *(xArray[3] - xArray[0]))/(yArray[0] - yArray[3]);
			levelXRight = (xArray[2]*yArray[1] - xArray[1]*yArray[2] - waterLever *(xArray[2] - xArray[1]))/(yArray[1] - yArray[2]);
			radBottom = (xArray[2] - xArray[3])/2;
			radUp = size.width / 2;
		}

		@Override
		public void changePosition(int dx, int dy) {
			for (int i = 0; i < xArray.length; i++) {
				xArray[i] += dx;
				yArray[i] += dy;
			}
			waterLever += dy;
			levelXRight += dx;
			levelXLeft += dx;
		}

		@Override
		public void onDraw(Canvas canvas) {

			Path path = new Path();
			Paint paint = new Paint();
			RectF waterRect = new RectF(levelXLeft, waterLever - size.height / 40, levelXRight, waterLever + size.height / 40);
			path.moveTo(levelXRight, waterLever);
			path.lineTo(xArray[2], yArray[2]);
			path.lineTo(xArray[3], yArray[3]);
			path.lineTo(levelXLeft, waterLever);
			paint.setColor(Color.argb(127, 199, 252, 236));
			paint.setStyle(Paint.Style.FILL);

			canvas.drawOval(waterRect, paint);
			canvas.drawPath(path, paint);
			RectF basis = new RectF(xArray[3], yArray[3] - size.height / 50, xArray[2], yArray[3] + size.height / 50);
			canvas.drawOval(basis, paint);
			path.reset();
			RectF up = new RectF(xArray[0], yArray[0] - size.height / 30, xArray[1], yArray[0] + size.height / 30);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			path.moveTo(xArray[1], yArray[1]);
			path.lineTo(xArray[2], yArray[2]);
			path.moveTo(xArray[3], yArray[3]);
			path.lineTo(xArray[0], yArray[0]);
			paint.setColor(Color.BLACK);
			canvas.drawPath(path, paint);
			canvas.drawArc(up, 0, -180, false, paint);
			canvas.drawArc(waterRect, 0, -180, false, paint);
			canvas.drawArc(basis, 0, -180, false, paint);
			if (!getChildren().isEmpty())
				getChildren().get(0).getPainter().onDraw(canvas);
			canvas.drawArc(up, 0, 180, false, paint);
			canvas.drawArc(waterRect, 0, 180, false, paint);
			canvas.drawArc(basis, 0, 180, false, paint);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			super.onTouch(v, event);
			if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
				if (getParent() == null)
					moveToDefault();
			}
			return true;
		}
	}

	private class GlassWaterListener implements WaterChangeListener {
		private double oldLevel;
		@Override
		public void change(Water water) {
			double waterLevel = getWaterLevel();
			if (oldLevel != waterLevel) {
				((GlassPainter)getPainter()).updateWaterLevel();
				oldLevel = waterLevel;
				painter.updateSize();
			}
		}
	}
}
