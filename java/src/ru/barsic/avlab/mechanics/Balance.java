package ru.barsic.avlab.mechanics;

import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.basic.TouchListener;
import ru.barsic.avlab.graphics.DrawView;
import ru.barsic.avlab.graphics.Painter;
import ru.barsic.avlab.helper.Logging;
import ru.barsic.avlab.physics.Computation;
import ru.barsic.avlab.physics.IParent;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

public class Balance extends PhysObject implements IParent {

	//--------------- CONSTANTS ---------------
	private static final int LEFT_BOWL = 1;
	private static final int RIGHT_BOWL = 2;
	private static final int ANIMATION_STEP = 1;

	private double leftMass = 0;
	private double rightMass = 0;
	private List<PhysObject> atLeft = new LinkedList<>();
	// Текущие объекты на правой чаше
	private List<PhysObject> atRight = new LinkedList<>();
	private double deltaMassMax;
	private double deltaMassMin;


	//--------------- PRIVATE STATIC FIELDS ---------------


	//--------------- PRIVATE STATIC METHODS ---------------

	//--------------- PRIVATE OBJECT FIELDS ---------------

	//--------------- PUBLIC OBJECT FIELDS ---------------

	//--------------- CONSTRUCTORS ---------------

	public Balance(double x, double y, double width, double height) {
		super(x, y, width, height, 0);
		painter = new BalancePathPainter(this);
	}

	//--------------- PUBLIC OBJECT OVERRIDDEN METHODS ---------------

	@Override
	public boolean isInAria(int x, int y) {
		PhysObject object = TouchListener.selected.object;
		return object instanceof IWeighing &&
				Computation.intersect(getActivePolygon(), ((IWeighing) object).getWeighingPolygon());
	}


	@Override
	public boolean attach(PhysObject child) {
		if (child instanceof IWeighing) {
			child.getPainter().setPos(child.getPainter().getPos().x,
					((BalancePathPainter) painter).yArray[getCurrentBowl(child) == LEFT_BOWL ? 2 : 3][5] - child.getPainter().getSize().height);
			child.getPainter().setZIndex(getPainter().getZIndex() + 2);
			if (getCurrentBowl(child) == LEFT_BOWL) {
				leftMass += child.mass;
				atLeft.add(child);
			} else {
				rightMass += child.mass;
				atRight.add(child);
			}

			DrawView.timer.schedule(new CalculationTask(), new Date(System.currentTimeMillis()));

			Logging.log("Attach Weight", child, "x = " + x + ", y = " + y + ", leftMass = " + leftMass + ", rightMass = " + rightMass + ", position");
			return super.attach(child);


		}
		return false;
	}

	@Override
	public boolean detach(PhysObject child) {
			super.detach(child);
			if (atLeft.contains(child)) {
				leftMass -= child.mass;
				atLeft.remove(child);
			}
			if (atRight.contains(child)) {
				rightMass -= child.mass;
				atRight.remove(child);
			}


			DrawView.timer.schedule(new CalculationTask(), new Date(System.currentTimeMillis()));
			Logging.log("Detach Weight", child, "x = " + x + ", y = " + y + ", leftMass = " + leftMass + ", rightMass = " + rightMass);

		return true;
	}


	@Override
	public int[][] getActivePolygon() {
		int[] x = ((BalancePathPainter) painter).xArray[2];
		int[] y = ((BalancePathPainter) painter).yArray[2];
		int[] xx = ((BalancePathPainter) painter).xArray[3];
		int[] yy = ((BalancePathPainter) painter).yArray[3];
		PhysObject object = TouchListener.selected.object;
		if (getCurrentBowl(object) == LEFT_BOWL)
			return new int[][]{new int[]{x[5], x[4], x[3], x[6]}, new int[]{y[3], y[6], y[5], y[4]}};
		else
			return new int[][]{new int[]{xx[5], xx[4], xx[3], xx[6]}, new int[]{yy[3], yy[6], yy[5], yy[4]}};

	}

	//--------------- PUBLIC OBJECT METHODS ---------------

	public int getCurrentBowl(PhysObject object) {
		if (object.getPainter().getCenter().x <= ((BalancePathPainter) painter).xArray[2][4] && object.getPainter().getCenter().y <= ((BalancePathPainter) painter).yArray[2][5] + painter.getSize().height / 5)
			return LEFT_BOWL;
		else {
			if (object.getPainter().getCenter().x >= ((BalancePathPainter) painter).xArray[3][4] && object.getPainter().getCenter().y <= ((BalancePathPainter) painter).yArray[3][5] + painter.getSize().height / 5)
				return RIGHT_BOWL;
		}
		return 0;
	}

	//--------------- PRIVATE OBJECT OVERRIDDEN METHODS ---------------

	//--------------- PRIVATE OBJECT METHODS ---------------

	//--------------- PUBLIC INNER CLASSES ---------------

	//--------------- PRIVATE INNER CLASSES ---------------

	private class BalancePathPainter extends Painter {

		public int[][] xArray, yArray;
		public int x1;
		public int y1;
		public int l;
		private int deltaMax;

		public BalancePathPainter(PhysObject obj) {
			super(obj);
			xArray = new int[4][4];
			yArray = new int[4][4];
			deltaMax = 30;
			deltaMassMax = 0.5;

			deltaMassMin = 0.005;
			l = size.width / 4;
			x1 = l;
			setZIndex(100);
			updatePoints();
		}

		@Override
		public boolean isChoice(int x, int y) {
			return false;
		}

		@Override
		public void updatePos() {
			super.updatePos();
			int oldL = l;
			l = size.width / 4;
			x1 = x1 * l / oldL;
		}

		@Override
		public void updatePoints() {
			xArray[0] = new int[]{getPos().x, getPos().x + size.width, getPos().x + size.width + size.width / 10, getPos().x - size.width / 10};
			yArray[0] = new int[]{getPos().y, getPos().y, getPos().y + size.height, getPos().y + size.height};
			xArray[1] = new int[]{getPos().x + size.width / 2 - size.width / 30, getPos().x + size.width / 2 + size.width / 30, getPos().x + size.width / 2 + size.width / 30, getPos().x + size.width / 2 - size.width / 30};
			yArray[1] = new int[]{getPos().y + size.height / 2 - size.width, getPos().y + size.height / 2 - size.width, getPos().y + size.height / 2, getPos().y + size.height / 2};
			xArray[2] = new int[]{getPos().x + size.width / 2, getPos().x + size.width / 2 - l, getPos().x + size.width / 2 - l, getPos().x + size.width / 2 - x1 + 3 * l / 4, getPos().x + size.width / 2 - x1 + 3 * l / 4, getPos().x + size.width / 2 - x1 - 3 * l / 4, getPos().x + size.width / 2 - x1 - 3 * l / 4};
			yArray[2] = new int[]{getPos().y + size.height / 2 - size.width + size.width / 15, getPos().y + size.height / 2 - size.width + size.width / 15 + y1, getPos().y + size.height / 2 - size.width + size.width / 7 + y1, getPos().y + size.height / 2 - size.width + size.width / 5 + y1, getPos().y + y1, getPos().y + y1, getPos().y + size.height / 2 - size.width + size.width / 5 + y1};
			xArray[3] = new int[]{getPos().x + size.width / 2, getPos().x + size.width / 2 + x1, getPos().x + size.width / 2 + x1, getPos().x + size.width / 2 + x1 + 3 * l / 4, getPos().x + size.width / 2 + x1 + 3 * l / 4, getPos().x + size.width / 2 + x1 - 3 * l / 4, getPos().x + size.width / 2 + x1 - 3 * l / 4};
			yArray[3] = new int[]{yArray[1][0] + size.width / 15, yArray[1][0] + size.width / 15 - y1, yArray[1][0] + size.width / 7 - y1, yArray[1][0] + size.width / 5 - y1, getPos().y - y1, getPos().y - y1, yArray[1][0] + size.width / 5 - y1};
		}

		@Override
		public void changePosition(int dx, int dy) {
//            for (int i = 0; i < xArray.length; i++) {
//                for (int j = 0; j < xArray[i].length; j++) {
//                    xArray[i][j] += dx;
//                    yArray[i][j] += dy;
//                }
//            }
		}

		@Override
		public void onDraw(Canvas canvas) {
			Path path = new Path();
			Paint paint = new Paint();
			paint.setColor(Color.BLACK); //основа
			path.moveTo(xArray[0][0], yArray[0][0]);
			for (int i = 0; i < xArray[0].length; i++) {
				path.lineTo(xArray[0][i], yArray[0][i]);
			}
			path.lineTo(xArray[0][0], yArray[0][0]);
			paint.setStyle(Paint.Style.FILL);
			canvas.drawRect(xArray[0][3], yArray[0][3], xArray[0][2], yArray[0][2] + size.height / 30, paint);
			canvas.drawPath(path, paint);
			paint.setColor(Color.GRAY);
			path.moveTo(xArray[0][0], yArray[0][0]);
			for (int i = 0; i < xArray[0].length; i++) {
				path.lineTo(xArray[0][i], yArray[0][i]);
			}
			path.lineTo(xArray[0][0], yArray[0][0]);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(xArray[0][3], yArray[0][3], xArray[0][2], yArray[0][2] + size.height / 30, paint);
			canvas.drawPath(path, paint);

			paint.setColor(Color.GRAY);//палка
			paint.setStyle(Paint.Style.FILL);
			canvas.drawRect(xArray[1][0], yArray[1][0], xArray[1][1], yArray[1][2], paint);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(xArray[1][0], yArray[1][0], xArray[1][1], yArray[1][2], paint);

			path.reset();//треугольник
			path.moveTo(xArray[1][0], yArray[1][0] + size.width / 10);
			path.lineTo(xArray[0][0] + size.width / 2, yArray[1][0] + size.width / 15);
			path.lineTo(xArray[1][1], yArray[1][0] + size.width / 10);
			path.lineTo(xArray[1][0], yArray[1][0] + size.width / 10);
			paint.setColor(Color.rgb(59, 68, 75));
			paint.setStyle(Paint.Style.FILL);
			canvas.drawPath(path, paint);
			path.reset();
			path.moveTo(xArray[1][0], yArray[1][0] + size.width / 10);
			path.lineTo(xArray[0][0] + size.width / 2, yArray[1][0] + size.width / 15);
			path.lineTo(xArray[1][1], yArray[1][0] + size.width / 10);
			path.lineTo(xArray[1][0], yArray[1][0] + size.width / 10);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawPath(path, paint);

			drawLeftBowl(canvas, path, paint);
			drawRightBowl(canvas, path, paint);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			super.onTouch(v, event);
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (!getChildren().isEmpty())
					painter.setZIndex(100);
				for (PhysObject p : getChildren()) {
					p.getPainter().setZIndex(getZIndex() + 10);
				}
			}
			return true;
		}

		private void drawRightBowl(Canvas canvas, Path path, Paint paint) {
			path.reset();
			path.moveTo(xArray[3][0], yArray[3][0]);
			for (int i = 0; i < xArray[3].length; i++) {
				path.lineTo(xArray[3][i], yArray[3][i]);
			}
			path.lineTo(xArray[3][2], yArray[3][2]);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(4);
			paint.setColor(Color.rgb(47, 79, 79));
			canvas.drawPath(path, paint);

			RectF ovalR = new RectF(xArray[3][5], yArray[3][5] - size.height / 5, xArray[3][4], yArray[3][5] + size.height / 5);
			canvas.drawOval(ovalR, paint);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.BLACK);
			canvas.drawOval(ovalR, paint);
		}

		private void drawLeftBowl(Canvas canvas, Path path, Paint paint) {
			path.reset();
			path.moveTo(xArray[2][0], yArray[2][0]);
			for (int i = 0; i < xArray[2].length; i++) {
				path.lineTo(xArray[2][i], yArray[2][i]);
			}
			path.lineTo(xArray[2][2], yArray[2][2]);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(4);
			paint.setColor(Color.rgb(47, 79, 79));
			canvas.drawPath(path, paint);

			RectF ovalL = new RectF(xArray[2][5], yArray[2][5] - size.height / 5, xArray[2][4], yArray[2][5] + size.height / 5);
			canvas.drawOval(ovalL, paint);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.BLACK);
			canvas.drawOval(ovalL, paint);
		}

	}

	private class CalculationTask extends TimerTask {

		public static final double T_MAX = 100;
		public static final double T_MIN = 10;
		private final BalancePathPainter painter;
		private long sleepTime;
		private int delta;

		public CalculationTask() {
			this.painter = (BalancePathPainter) getPainter();
			double deltaMass = leftMass - rightMass;
			if (deltaMass > deltaMassMax)
				deltaMass = deltaMassMax;
			delta = (int) (deltaMass * painter.deltaMax / deltaMassMax);
			sleepTime = (long) ((deltaMass - deltaMassMax) * (T_MAX - T_MIN) / (deltaMassMin - deltaMassMax) + T_MIN);
			System.out.println("deltaMass="+deltaMass);
			System.out.println("sleepTime="+sleepTime);
		}

		@Override
		public void run() {
			if (Math.abs(delta) > painter.deltaMax)
				delta = (int) (Math.signum(delta) * painter.deltaMax);
			int deltaSign = (int) Math.signum(delta - painter.y1);
			while (true) {
				for (PhysObject p : atLeft)
					p.getPainter().moveBy(0, deltaSign * ANIMATION_STEP);
				for (PhysObject p : atRight)
					p.getPainter().moveBy(0, deltaSign * -1 * ANIMATION_STEP);
				if (deltaSign > 0) {
					painter.y1 += ANIMATION_STEP;
					painter.x1 = (int) Math.sqrt(painter.l * painter.l - painter.y1 * painter.y1);
					if (delta <= painter.y1)
						break;
				} else {
					painter.y1 -= ANIMATION_STEP;
					painter.x1 = (int) Math.sqrt(painter.l * painter.l - painter.y1 * painter.y1);
					if (delta >= painter.y1)
						break;
				}
				painter.updatePoints();
				try {
					Thread.sleep(sleepTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			painter.updatePoints();
		}
	}
}


