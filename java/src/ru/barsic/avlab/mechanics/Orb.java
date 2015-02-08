package ru.barsic.avlab.mechanics;

import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.graphics.Painter;

public class Orb extends PhysObject implements ISuspend {
	  int color;

	public Orb(double x, double y, double width, double height, double mass, int color){
		super(x, y, width, height, mass);
		this.color = color;


		painter = new OrbPainter(this);
	}

	@Override
	public int[][] getSuspendPolygon() {
		return new int[][]{((OrbPainter)painter).xSuspArr, ((OrbPainter)painter).ySuspArr};
	}

	@Override
	public void setSuspendPos(int x, int y) {
		getPainter().setPos(getPainter().getX(),y + painter.getSize().width + painter.getSize().width / 12);
	}

	private  class OrbPainter extends Painter{
		private int x;
		private  int y;
		private int[] xSuspArr, ySuspArr;

		public OrbPainter(PhysObject obj){
			super(obj);
			updatePoints();
			setZIndex(11);
			xSuspArr = new int[4];
			ySuspArr = new int[4];

		}
		@Override
		public boolean isChoice(int x, int y) {
			return x > pos.x - size.width && y > pos.y - size.width && x < pos.x + size.width && y < pos.y + size.width;
		}

		@Override
		public void updatePoints() {
			x = pos.x;
			y = pos.y;
			xSuspArr = new int[]{x - size.width / 8, x, x, x - size.width / 8};
			ySuspArr = new int[]{y - size.width - size.width / 3, y - size.width - size.width / 3, y - size.width, y -size.width};
		}

		@Override
		public void changePosition(int dx, int dy) {
			x += dx;
			y += dy;
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
			canvas.drawCircle(x, y, size.width, paint);
			path.moveTo(x, y - size.width + size.width / 6 );
			path.lineTo(x, y - size.width );
			Point p = new Point(x - size.width / 20, y - size.width - size.width / 3);
			path.quadTo(p.x, p.y, x - size.width / 8, y - size.width - size.width / 12 );
			paint.setColor(Color.rgb(47, 79, 79));
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(2);
			canvas.drawPath(path, paint);
			//canvas.drawCircle(p.x, p.y, size.width / 30, paint);
			//canvas.drawRect(xSuspArr[0], ySuspArr[0],xSuspArr[1],ySuspArr[2], paint);

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
