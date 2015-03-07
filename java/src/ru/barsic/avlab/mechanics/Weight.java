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
		return new int[][] {((WeightPathPainter)painter).xArray, ((WeightPathPainter)painter).yArray};
	}


	private class WeightPathPainter extends Painter {
		private int[] xArray, yArray;
		public WeightPathPainter(PhysObject obj) {
			super(obj);
			xArray = new int[8];
			yArray = new int[8];
			setZIndex(100);
			updatePoints();
		}

		@Override
		public void updatePoints() {
			xArray = new  int[] {pos.x, pos.x + size.width / 5, pos.x + size.width / 5, pos.x + size.width - size.width / 5, pos.x + size.width - size.width / 5, pos.x + size.width, pos.x + size.width, pos.x};
			yArray = new int[] {pos.y, pos.y - size.height / 15, pos.y - size.height / 3, pos.y - size.height / 3, pos.y - size.height / 15, pos.y, pos.y + size.height, pos.y + size.height};
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
			paint.setColor(Color.GRAY);
			path.moveTo(xArray[0],yArray[0]);
			for (int i = 0; i < xArray.length; i++){
				path.lineTo(xArray[i],yArray[i]);
			}
			path.lineTo(xArray[0],yArray[0]);
			paint.setStyle(Paint.Style.FILL);
			canvas.drawPath(path, paint);

			path.reset();
			path.moveTo(xArray[0],yArray[0]);
			for (int i = 0; i < xArray.length; i++){
				path.lineTo(xArray[i],yArray[i]);
			}
			path.lineTo(xArray[0],yArray[0]);
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
