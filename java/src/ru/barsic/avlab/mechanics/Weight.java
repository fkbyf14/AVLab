package ru.barsic.avlab.mechanics;

import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.graphics.Painter;

public class Weight extends PhysObject implements IWeighing {
	public Weight(double x, double y, double width, double height, double mass) {
		super(x, y, width, height, mass);
		painter = new WeightPathPainter(this);
	}

	@Override
	public int[][] getWeighingPolygon() {
		int[] x = ((WeightPathPainter) painter).xArray;
		int[] y = ((WeightPathPainter) painter).yArray;
		return new int[][]{new int[]{x[6], x[5], x[0], x[7]}, new int[]{y[0], y[5], y[6], y[7]}};
	}


	private class WeightPathPainter extends Painter {
		private int[] xArray, yArray;
		int mm = (int) (mass * 1000d);
		String m = Integer.toString(mm);

		public WeightPathPainter(PhysObject obj) {
			super(obj);
			xArray = new int[8];
			yArray = new int[8];
			setZIndex(100);
			updatePoints();
		}

		@Override
		public void updatePoints() {
			xArray = new int[]{getPos().x, getPos().x + size.width / 5, getPos().x + size.width / 5, getPos().x + size.width - size.width / 5, getPos().x + size.width - size.width / 5, getPos().x + size.width, getPos().x + size.width, getPos().x};
			yArray = new int[]{getPos().y, getPos().y - size.height / 15, getPos().y - size.height / 3, getPos().y - size.height / 3, getPos().y - size.height / 15, getPos().y, getPos().y + size.height, getPos().y + size.height};
		}

		@Override
		public void changePosition(int dx, int dy) {
			for (int i = 0; i < xArray.length; i++) {
				xArray[i] += dx;
				yArray[i] += dy;
			}
		}

		@Override
		public void onDraw(Canvas canvas) {

			Path path = new Path();
			Paint paint = new Paint();

			RectF basis = new RectF(xArray[0], yArray[7] - size.height / 9, xArray[5], yArray[7] + size.height / 9);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.GRAY);
			canvas.drawOval(basis, paint);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			canvas.drawOval(basis, paint);

			path.moveTo(xArray[0], yArray[0]);
			for (int i = 0; i < xArray.length; i++) {
				path.lineTo(xArray[i], yArray[i]);

			}
			path.lineTo(xArray[0], yArray[0]);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.GRAY);
			canvas.drawPath(path, paint);

			path.reset();
			path.moveTo(xArray[0], yArray[0]);
			for (int i = 0; i < xArray.length - 1; i++) {
				path.lineTo(xArray[i], yArray[i]);
			}
			path.moveTo(xArray[7], yArray[7]);
			path.lineTo(xArray[0], yArray[0]);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			canvas.drawPath(path, paint);

			RectF oval = new RectF(xArray[0], yArray[2] - size.height / 10, xArray[5], yArray[2] + size.height / 10);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.GRAY);
			canvas.drawOval(oval, paint);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			canvas.drawOval(oval, paint);
			//paint.setStrokeWidth(1);
			paint.setTextSize(15);
			//paint.setTextAlign(Paint.Align.CENTER);
			paint.setStyle(Paint.Style.FILL);
			canvas.drawText(m, getCenter().x - paint.measureText(m) / 2, yArray[6] - size.height / 9, paint);


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
