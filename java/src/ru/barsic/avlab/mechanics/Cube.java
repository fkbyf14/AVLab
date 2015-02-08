package ru.barsic.avlab.mechanics;

import android.annotation.SuppressLint;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.graphics.Painter;

public class Cube extends PhysObject implements ISuspend {
	int color;

	public Cube(double x, double y, double width, double height, double mass, int color) {
		super(x, y, width, height, mass);
		this.color = color;


		painter = new CubePainter(this);
	}

	@Override
	public int[][] getSuspendPolygon() {

		return new int[][]{((CubePainter)painter).xSuspArr, ((CubePainter)painter).ySuspArr};
	}

	@Override
	public void setSuspendPos(int x, int y) {
		getPainter().setPos(getPainter().getX(), y + painter.getSize().width / 4 + painter.getSize().width / 12 );
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
			setZIndex(11);

		}

		@Override
		public void updatePoints() {
			xArray = new int[]{pos.x, pos.x + size.width, pos.x + size.width, pos.x, pos.x + size.width / 3, pos.x + size.width / 3 + size.width, pos.x + size.width / 3 + size.width };
			yArray= new int[]{pos.y, pos.y, pos.y + size.height, pos.y + size.height, pos.y - size.width / 4, pos.y - size.width / 4, pos.y + size.height - size.width / 4};

			xSuspArr = new int[]{xArray[0] + size.width / 2 +  size.width / 8  , xArray[0] + size.width / 2 + size.width / 3, xArray[0] + size.width / 2 + size.width / 3, xArray[0] + size.width / 2 + size.width / 8 };
			ySuspArr = new int[]{pos.y - size.width / 8 - size.width / 3, pos.y - size.width / 8 - size.width / 3, pos.y - size.width / 8, pos.y - size.width / 8};
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

		@SuppressLint("DrawAllocation")
		@Override
		public void onDraw(Canvas canvas) {Path path = new Path();
			Paint paint = new Paint();
			paint.setColor(color);
			paint.setStyle(Paint.Style.FILL);
			Rect rect = new Rect();
			rect.set(xArray[0] , yArray[0], xArray[1], yArray[2]);
			canvas.drawRect(rect , paint);

			path.moveTo(xArray[0], yArray[0]);
			path.lineTo(xArray[4], yArray[4]);
			path.lineTo(xArray[5], yArray[5]);
			path.lineTo(xArray[6], yArray[6]);
			path.lineTo(xArray[2], yArray[2]);

			canvas.drawPath(path, paint);

			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			Rect rect1 = new Rect();
			rect1.set(xArray[0] , yArray[0], xArray[1], yArray[2]);
			canvas.drawRect(rect1 , paint);
			path.moveTo(xArray[0], yArray[0]);
			path.lineTo(xArray[4], yArray[4]);
			path.lineTo(xArray[5], yArray[5]);
			path.lineTo(xArray[6], yArray[6]);
			path.lineTo(xArray[2], yArray[2]);
			path.moveTo(xArray[5], yArray[5]);
			path.lineTo(xArray[1], yArray[1]);
			canvas.drawPath(path, paint);

			path.reset();
			path.moveTo(xArray[0] + size.width / 2 + size.width / 4, pos.y - size.width / 8 );
			path.lineTo(xArray[0] + size.width / 2 + size.width / 4, yArray[4] );
			Point p = new Point(xArray[0] + size.width / 2 + size.width / 4 - size.width / 20,  pos.y - size.width / 8 - size.width / 3);
			path.quadTo(p.x, p.y, xArray[0] + size.width / 2 + size.width / 4 - size.width / 10, yArray[4] - size.width / 12 );
			paint.setColor(Color.rgb(47, 79, 79));
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(2);
			canvas.drawPath(path, paint);
			//canvas.drawCircle(p.x, p.y, size.width / 30, paint);
			//canvas.drawRect(xSuspArr[0], ySuspArr[0], xSuspArr[1], ySuspArr[2], paint);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			super.onTouch(v, event);
			if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
				if (getParent() == null )
					moveToDefault();
			}
			return true;
		}
	}
}
