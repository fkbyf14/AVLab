package ru.barsic.avlab.mechanics;

import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.basic.TouchListener;
import ru.barsic.avlab.basic.World;
import ru.barsic.avlab.graphics.DrawView;
import ru.barsic.avlab.graphics.Painter;
import ru.barsic.avlab.graphics.Scale;
import ru.barsic.avlab.helper.ScalingUtil;
import ru.barsic.avlab.physics.Computation;
import ru.barsic.avlab.physics.IGluer;
import ru.barsic.avlab.physics.IParent;

import java.util.Date;
import java.util.TimerTask;

public class Dynamometer extends PhysObject implements IGluer, IParent {
	public static double c = 10d;
	public static double INITIAL_ANGLE = Math.PI / 9;
	double force = 0;
	double maxForce = 20;
	double minForce = -20;
	double k = 114d;
	double alpha = INITIAL_ANGLE;


	public Dynamometer(double x, double y, double width, double height) {
		super(x, y, width, height, 0);
		painter = new DynamometerPathPainter(this);
	}

	@Override
	public int[][] getGlueyPolygon() {
		return new int[][]{((DynamometerPathPainter) painter).xArray, ((DynamometerPathPainter) painter).yArray};
	}

	@Override
	public boolean isInAria(int x, int y) {
		return TouchListener.selected.object instanceof ISuspend &&
				Computation.intersect(getActivePolygon(), ((ISuspend) TouchListener.selected.object).getSuspendPolygon());
	}

	@Override
	public int[][] getActivePolygon() {
		return new int[][]{((DynamometerPathPainter) painter).xActive , ((DynamometerPathPainter) painter).yActive};
	}

	@Override
	public boolean attach(PhysObject child) {
		if (child instanceof ISuspend && getChildren().isEmpty() && ((DynamometerPathPainter) painter).object.getParent() != null && super.attach(child)) {
			DrawView.timer.schedule(new CalculationTask((DynamometerPathPainter) painter, this), new Date(System.currentTimeMillis()));
			child.getPainter().setZIndex(painter.getZIndex() + 2);
			return true;
		}
		return false;
	}

	@Override
	public boolean detach(PhysObject child) {
		if (!getChildren().isEmpty()) {
			boolean result = super.detach(child);
			DrawView.timer.schedule(new CalculationTask((DynamometerPathPainter) painter, this), new Date(System.currentTimeMillis()));
			return result;
		}

		return false;
	}

	private class DynamometerPathPainter extends Painter {

		Scale lineScale;
		private int[] xArray, yArray;
		private int[] xSuspArr, ySuspArr;
		private int[] xActive, yActive;
		private double edgeLength = size.width / 2;
		private final int edgeCount = 16;

		public DynamometerPathPainter(PhysObject obj) {
			super(obj);
			xArray = new int[5];
			yArray = new int[5];
			xSuspArr = new int[4];
			ySuspArr = new int[4];
			xActive = new int[4];
			yActive = new int[4];
			lineScale = new Scale(this, Scale.VERTICAL_TYPE, minForce, maxForce, 1, Color.BLUE);
			updatePoints();
			setZIndex(100);
		}

		@Override
		public boolean isChoice(int x, int y) {
			if (!getChildren().isEmpty())
				return false;

			return super.isChoice(x, y);
		}

		@Override
		public void updateSize() {
			super.updateSize();
			edgeLength = size.width / 2;
		}

		@Override
		public void updatePoints() {
			xArray = new int[]{getPos().x, getPos().x + size.width, getPos().x + size.width, getPos().x, getPos().x + size.width / 2};
			yArray = new int[]{getPos().y, getPos().y, getPos().y + size.height, getPos().y + size.height, calcMarkForce()};
			xSuspArr = new int[]{xArray[4] - size.width / 8, xArray[4] + size.width / 8, xArray[4] + size.width / 8, xArray[4] - size.width / 8};
			ySuspArr = new int[]{calcMarkForce() + 3 * size.height / 4 + size.width / 5, calcMarkForce() + 3 * size.height / 4 + size.width / 5, calcMarkForce() + 3 * size.height / 4 + 3 * size.width / 5, calcMarkForce() + 3 * size.height / 4 + 3 * size.width / 5};
			xActive = new int[]{xArray[4] - size.width / 8 - size.width / 4, xArray[4] + size.width / 8 + size.width / 4, xArray[4] + size.width / 8 + size.width / 4, xArray[4] - size.width / 8 - size.width / 4};
			yActive = new int[]{calcMarkForce() + 3 * size.height / 4 + size.width / 5 - size.width / 4, calcMarkForce() + 3 * size.height / 4 + size.width / 5 - size.width / 4, calcMarkForce() + 3 * size.height / 4 + 3 * size.width / 5 + size.width / 4, calcMarkForce() + 3 * size.height / 4 + 3 * size.width / 5 + size.width / 4};

			lineScale.setPos(getPos().x + size.width / 9, getPos().y + size.height / 7);
			lineScale.setSize(size.width / 4, size.height - size.height / 6);
		}

		@Override
		public void changePosition(int dx, int dy) {
			for (int i = 0; i < 5; i++) {
				xArray[i] += dx;
				yArray[i] += dy;
			}
			for (int i = 0; i < 4; i++) {
				xSuspArr[i] += dx;
				ySuspArr[i] += dy;
			}
		}

		@Override
		public void onDraw(Canvas canvas) {
			Path path = new Path();
			Paint paint = new Paint();
			paint.setColor(Color.rgb(250, 240, 190));
			Rect rect = new Rect();
			rect.set(xArray[0], yArray[0], xArray[1], yArray[2]);
			canvas.drawRect(rect, paint);
			paint.setColor(Color.GRAY);
			canvas.drawCircle(xArray[4], yArray[0] + size.width / 15, size.width / 15, paint);
			drawSpring(canvas, path, paint);
			lineScale.onDraw(canvas);
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

		private int calcMarkForce() {
			int y1 = lineScale.getPos().y;
			int y2 = lineScale.getPos().y + lineScale.getSize().height;
			return (int) ((y1 * (force - minForce) + y2 * (maxForce - force)) / (maxForce - minForce));
		}

		private void drawSpring(Canvas canvas, Path path, Paint paint) {
			path.moveTo(xArray[4], yArray[0] + size.width / 30);
			path.rLineTo((float) (edgeLength / 2 * Math.cos(alpha)), (float) (edgeLength / 2 * Math.sin(alpha)));
			float edgeX = (float) (edgeLength * Math.cos(alpha));
			float edgeY = (float) (edgeLength * Math.sin(alpha));
			for (int i = 0; i < edgeCount / 2; i++) {
				path.rLineTo(-edgeX, edgeY);
				path.rLineTo(edgeX, edgeY);
			}
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(2);
			canvas.drawPath(path, paint);

			paint.setColor(Color.RED);
			canvas.drawLine(xArray[0], yArray[0] + size.width / 30 + edgeCount * edgeY + edgeY / 2, xArray[1], yArray[0] + size.width / 30 + edgeCount * edgeY + edgeY / 2, paint);
			paint.setColor(Color.GRAY);
			canvas.drawLine(xArray[4], yArray[0] + size.width / 30 + edgeCount * edgeY + edgeY / 2, xArray[4], yArray[0] + size.width / 30 + edgeCount * edgeY + 3 * size.height / 4 + edgeY / 2, paint);

			Point p = new Point(xArray[4] - size.width / 10, (int) (yArray[0] + size.width / 30 + edgeCount * edgeY + 3 * size.height / 4 + edgeY / 2 + size.width / 5));
			path.reset();
			path.moveTo(xArray[4], yArray[0] + size.width / 30 + edgeCount * edgeY + 3 * size.height / 4 + edgeY / 2);
			path.quadTo(p.x, p.y, xArray[4] + size.width / 10, yArray[0] + size.width / 30 + edgeCount * edgeY + 3 * size.height / 4 + edgeY / 2 + size.width / 12);
			paint.setColor(Color.rgb(47, 79, 79));
			canvas.drawPath(path, paint);
//			Point pp = new Point(xArray[4], (int) (yArray[0] + size.width / 30 + edgeCount * edgeY + 3 * size.height / 4 + edgeY / 2 + size.width / 5));
//
//			canvas.drawPoint(pp.x, pp.y, paint);
		}

		private void updateChildren() {
			float edgeY = (float) (edgeLength * Math.sin(alpha));
			if (!getChildren().isEmpty())
				((ISuspend) getChildren().get(0)).setSuspendPos(0, (int) (yArray[0] + size.width / 30 + edgeCount * edgeY + 3 * size.height / 4 + edgeY / 2 + size.width / 12));

		}
	}

	private static class CalculationTask extends TimerTask {

		private final Dynamometer dynamometer;
		private static double y0;
		private static double m;
		private static double fundFreq;
		private static double ksi;
		private static double freq;
		private static double lengthAllEdges;

		public CalculationTask(DynamometerPathPainter painter, Dynamometer dynamometer) {
			this.dynamometer = dynamometer;
			lengthAllEdges = (painter.edgeCount + 0.5) * painter.edgeLength;
		}

		@Override
		public void run() {
			PhysObject child = null;
			if (!dynamometer.getChildren().isEmpty()) {
				child = dynamometer.getChildren().get(0);
				m = child.mass;
				fundFreq = Math.sqrt(dynamometer.k / m);
				ksi = c / (2d * Math.sqrt(dynamometer.k * m));
				freq = fundFreq * Math.sqrt(1d - ksi * ksi);
				y0 = m * World.ACCELERATION_OF_GRAVITY / dynamometer.k;

			}
			double t = 0d;
			double dy = 0;
			double currentLength;
			long currentTime = System.currentTimeMillis();
			while (dy > 0.0001 || (System.currentTimeMillis() - currentTime) < 1500) {
				double cos = Math.cos(freq * t);
				double sin = Math.sin(freq * t);
				double c2 = y0 * ksi * fundFreq / freq;
				dy = Math.exp(-ksi * fundFreq * t) * (y0 * cos + c2 * sin);
				System.out.println("CHILD!!! =  " + child);
				if (child != null)
					currentLength = Math.sin(dynamometer.alpha) * lengthAllEdges + ScalingUtil.scalingRealSizeToY(dy);
				else
					currentLength = Math.sin(dynamometer.alpha) * lengthAllEdges - ScalingUtil.scalingRealSizeToY(dy);
				dynamometer.alpha = Math.asin(currentLength / lengthAllEdges);
				((DynamometerPathPainter) dynamometer.getPainter()).updateChildren();

				t += 0.01;
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (child == null)
				dynamometer.alpha = INITIAL_ANGLE;
		}
	}
}
