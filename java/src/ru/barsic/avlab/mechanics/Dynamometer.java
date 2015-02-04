package ru.barsic.avlab.mechanics;

import java.util.Date;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.*;
import ru.barsic.avlab.graphics.*;
import ru.barsic.avlab.physics.*;

public class Dynamometer extends PhysObject implements IGluer, IParent {

	double force = 0;
	double maxForce = 2.5;
	double minForce = -2.5;
	double step = (maxForce + minForce) / 60;
	double alpha = Math.PI / 8;
	double k = 200.0;
	TimerTask calculationTask;

	public Dynamometer(double x, double y, double width, double height) {
		super(x, y, width, height, 0);
		painter = new DynamometerPathPainter(this);
		calculationTask = new CalculationTask((DynamometerPathPainter)painter);
	}

	@Override
	public int[][] getGlueyPolygon() {
		return new int[][]{((DynamometerPathPainter)painter).xArray, ((DynamometerPathPainter)painter).yArray};
	}

	@Override
	public boolean isInAria(int x, int y) {
		return TouchListener.selected.object instanceof ISuspend &&
			Computation.intersect(getActivePolygon(), ((ISuspend)TouchListener.selected.object).getSuspendPolygon());
	}

	@Override
	public int[][] getActivePolygon() {
		return new int[][]{((DynamometerPathPainter)painter).xSuspArr, ((DynamometerPathPainter)painter).ySuspArr};
	}

	private class DynamometerPathPainter extends Painter {

		Scale lineScale;

		private int[] xArray, yArray;
		private int[] xSuspArr, ySuspArr;
		private final double edgeLength = size.width / 2;

		private final int edgeCount = 16;
		private final double initialSpringLengthY = (edgeCount + 0.5) * Math.sin(alpha) * edgeLength;

		public DynamometerPathPainter(PhysObject obj) {
			super(obj);
			xArray = new int[5];
			yArray = new int[5];
			xSuspArr = new int[4];
			ySuspArr = new int[4];
			lineScale = new Scale(this, Scale.VERTICAL_TYPE, minForce, maxForce, 1, Color.BLUE);
			updatePoints();
			setZIndex(100);
		}

		private int calcMarkForce() {
			int y1 = lineScale.getY();
			int y2 = lineScale.getY() + lineScale.getSize().height;
			return (int)((y1 * (force - minForce) + y2 * (maxForce - force)) / (maxForce - minForce));
		}

		@Override
		public void updatePoints() {
			xArray = new int[] {pos.x, pos.x + size.width, pos.x + size.width, pos.x, pos.x + size.width / 2};
			yArray = new int[] {pos.y, pos.y, pos.y + size.height, pos.y + size.height, calcMarkForce()};
			xSuspArr = new int[] {xArray[4] - size.width / 10, xArray[4], xArray[4] + size.width / 10, xArray[4] - size.width / 10};
			ySuspArr = new int[] {yArray[2] + size.height / 3, yArray[2] + size.height / 3, yArray[2] + size.height / 3 + size.height / 5, yArray[2] + size.height / 3 + size.height / 5  };

			lineScale.setPos(pos.x + size.width / 9, pos.y + size.height / 7);
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

		@SuppressLint("DrawAllocation")
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
			//canvas.drawPoint(xArray[4], yArray[0], paint);

			drawSpring(canvas, path, paint);

			paint.setColor(Color.RED);
			canvas.drawLine(xArray[0], yArray[0]  + 16*size.height / 30, xArray[1],yArray[0]  + 16*size.height / 30, paint);
			paint.setColor(Color.GRAY);
			canvas.drawLine(xArray[4], yArray[0]  + 16*size.height / 30, xArray[4], yArray[2] + size.height / 3, paint );
			Point p = new Point(xArray[4] - size.width / 10, yArray[2] + size.height / 3 + size.width / 5 );
			path.reset();
			path.moveTo(xArray[4], yArray[2] + size.height / 3);
			path.quadTo(p.x, p.y, xArray[4] + size.width / 10, yArray[2] + size.height / 3 + size.width / 12);
			paint.setColor(Color.rgb(47, 79, 79));
			//paint.setStrokeWidth(2);
			canvas.drawPath(path, paint);
			canvas.drawCircle(p.x,p.y, (float)1, paint);
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

		private void drawSpring(Canvas canvas, Path path, Paint paint) {
			path.moveTo(xArray[4], yArray[0] + size.width / 30);
			path.rLineTo((float)(edgeLength / 2 * Math.cos(alpha)), (float)(edgeLength / 2 * Math.sin(alpha)));
			float edgeX = (float)(edgeLength * Math.cos(alpha));
			float edgeY = (float)(edgeLength * Math.sin(alpha));
			for (int i = 0; i < edgeCount / 2; i++) {
				path.rLineTo(-edgeX, edgeY);
				path.rLineTo(edgeX, edgeY);
			}
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(2);

			canvas.drawPath(path, paint);
		}
	}

	private class CalculationTask extends TimerTask {

		private final DynamometerPathPainter painter;
		private volatile boolean condition = true;

		public CalculationTask(DynamometerPathPainter painter) {
			this.painter = painter;
		}

		@Override
		public void run() {
			double currentLength = (painter.edgeCount + 0.5) * Math.sin(alpha) * painter.edgeLength;
			double dy = -(currentLength - painter.initialSpringLengthY) / 1;
			double dy1 = dy;
			double dy2 = dy;
			double a;
			double dt = World.D_T;
			while(condition) {
				a = k / getChildren().get(0).mass * (-dy) + World.ACCELERATION_OF_GRAVITY;
				dy = a * dt * dt + 2 * dy1 + dy2;
				dy1 = dy;
				dy2 = dy1;
				currentLength += (dy *1);
				alpha = Math.asin(currentLength / painter.edgeLength / (painter.edgeCount + 0.5));
				try {
					Thread.sleep((long)(1000*dt));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean attach(PhysObject child) {
		if (child instanceof ISuspend && getChildren().isEmpty() && super.attach(child)) {
			child.getPainter().setPos(child.getPainter().getPos().x - (child.getPainter().getSize().width) / 20, child.getPainter().getPos().y );
			DrawView.timer.schedule( calculationTask , new Date(System.currentTimeMillis()));
			return true;
		}
		return false;
	}
}
