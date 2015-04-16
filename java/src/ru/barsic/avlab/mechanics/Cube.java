package ru.barsic.avlab.mechanics;

import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.graphics.Painter;
import ru.barsic.avlab.helper.Logging;

public class Cube extends PhysObject implements ISuspend, IWeighing {
	int color;

	public Cube(double x, double y, double width, double height, double mass, int color) {
		super(x, y, width, height, mass);
		this.color = color;


		painter = new CubePainter(this);
	}

	@Override
	public int[][] getSuspendPolygon() {

		return new int[][]{((CubePainter) painter).xSuspArr, ((CubePainter) painter).ySuspArr};
	}

	@Override
	public void setSuspendPos(int x, int y) {
		getPainter().setPos(getParent().getPainter().getPos().x - getParent().getPainter().getSize().width - getParent().getPainter().getSize().width / 3  + painter.getSize().width / 2, y + painter.getSize().width / 4 + painter.getSize().width / 10);
		Logging.log("setSuspendPos", this, "x = " + x + ", y = " + y);
	}

	@Override
	public int[][] getWeighingPolygon() {
		int[] x = ((CubePainter) painter).xArray;
		int[] y = ((CubePainter) painter).yArray;
		return new int[][]{new int[]{x[2], x[1], x[0], x[3]}, new int[]{y[0], y[1], y[2], y[3]}};
	}

	private class CubePainter extends Painter {
		private int[] xArray;
		private int[] yArray;
		private int[] xSuspArr, ySuspArr;

		public CubePainter(PhysObject obj) {
			super(obj);

			xSuspArr = new int[4];
			ySuspArr = new int[4];
			xArray = new int[7];
			yArray = new int[7];
			updatePoints();
			setZIndex(12);

		}

		@Override
		public void updatePoints() {
			xArray = new int[]{getPos().x, getPos().x + size.width, getPos().x + size.width, getPos().x, getPos().x + size.width / 3, getPos().x + size.width / 3 + size.width, getPos().x + size.width / 3 + size.width};
			yArray = new int[]{getPos().y, getPos().y, getPos().y + size.height, getPos().y + size.height, getPos().y - size.width / 4, getPos().y - size.width / 4, getPos().y + size.height - size.width / 4};

			xSuspArr = new int[]{xArray[0] + size.width / 2 + size.width / 8, xArray[0] + size.width / 2 + size.width / 3, xArray[0] + size.width / 2 + size.width / 3, xArray[0] + size.width / 2 + size.width / 8};
			ySuspArr = new int[]{getPos().y - size.width / 8 - size.width / 3, getPos().y - size.width / 8 - size.width / 3, getPos().y - size.width / 8, getPos().y - size.width / 8};
		}


		@Override
		public void changePosition(int dx, int dy) {
			for (int i = 0; i < 7; i++) {
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
			paint.setColor(color);
			paint.setStyle(Paint.Style.FILL);
			Rect rect = new Rect();
			rect.set(xArray[0], yArray[0], xArray[1], yArray[2]);
			canvas.drawRect(rect, paint);

			path.moveTo(xArray[0], yArray[0]);
			path.lineTo(xArray[4], yArray[4]);
			path.lineTo(xArray[5], yArray[5]);
			path.lineTo(xArray[6], yArray[6]);
			path.lineTo(xArray[2], yArray[2]);

			canvas.drawPath(path, paint);

			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			Rect rect1 = new Rect();
			rect1.set(xArray[0], yArray[0], xArray[1], yArray[2]);
			canvas.drawRect(rect1, paint);
			path.moveTo(xArray[0], yArray[0]);
			path.lineTo(xArray[4], yArray[4]);
			path.lineTo(xArray[5], yArray[5]);
			path.lineTo(xArray[6], yArray[6]);
			path.lineTo(xArray[2], yArray[2]);
			path.moveTo(xArray[5], yArray[5]);
			path.lineTo(xArray[1], yArray[1]);
			canvas.drawPath(path, paint);

			path.reset();
			path.moveTo(xArray[0] + size.width / 2 + size.width / 4, getPos().y - size.width / 8);
			path.lineTo(xArray[0] + size.width / 2 + size.width / 4, yArray[4]);
			Point p = new Point(xArray[0] + size.width / 2 + size.width / 4 - size.width / 20, getPos().y - size.width / 8 - size.width / 3);
			path.quadTo(p.x, p.y, xArray[0] + size.width / 2 + size.width / 4 - size.width / 10, yArray[4] - size.width / 12);
			paint.setColor(Color.rgb(47, 79, 79));
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(2);
			canvas.drawPath(path, paint);
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
}
