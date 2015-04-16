package ru.barsic.avlab.molecular;

import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.basic.TouchListener;
import ru.barsic.avlab.graphics.Painter;
import ru.barsic.avlab.helper.Logging;
import ru.barsic.avlab.mechanics.IWeighing;
import ru.barsic.avlab.physics.Computation;
import ru.barsic.avlab.physics.IParent;

import java.util.TimerTask;

public class Glass extends PhysObject implements IWeighing, IParent{
	public Glass(double x, double y, double width, double height, double mass) {
		super(x, y, width, height, mass);
		painter = new GlassPainter(this);
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

	private class GlassPainter extends Painter {
		private  int xArray[];
		private  int yArray[];
		private  int levelXLeft;
		private  int levelXRight;
		private  int levelY;


		public GlassPainter(PhysObject obj) {
			super(obj);
			xArray = new int[4];
			yArray = new int[4];
			setMovable(true);
			updatePoints();
			setZIndex(10);
		}

		@Override
		public void updatePoints() {
			xArray = new int[]{getPos().x, getPos().x + size.width, getPos().x + size.width - size.width / 4, getPos().x + size.width / 4};
			yArray = new int[]{getPos().y, getPos().y, getPos().y + size.height, getPos().y + size.height};
			levelY = yArray[0] + getSize().height / 2;
//			levelXLeft = xArray[0] + ((levelY - yArray[0])*(xArray[3] - xArray[0]))/(yArray[3] - yArray[0]);
//			levelXRight = xArray[0] + ((levelY - yArray[1])*(xArray[1] - xArray[2]))/(yArray[2] - yArray[1]);
			levelXLeft = (xArray[3]*yArray[0] - xArray[0]*yArray[3] - levelY*(xArray[3] - xArray[0]))/(yArray[0] - yArray[3]);
			levelXRight = (xArray[2]*yArray[1] - xArray[1]*yArray[2] - levelY*(xArray[2] - xArray[1]))/(yArray[1] - yArray[2]);

		}

		@Override
		public void changePosition(int dx, int dy) {
			for (int i = 0; i < xArray.length; i++) {
				xArray[i] += dx;
				yArray[i] += dy;
			}
			levelY += dy;
			levelXRight += dx;
			levelXLeft += dx;
		}

		@Override
		public void onDraw(Canvas canvas) {

			Path path = new Path();
			Paint paint = new Paint();
			RectF middle = new RectF(levelXLeft, levelY - size.height / 40, levelXRight, levelY + size.height / 40);
			path.moveTo(levelXRight, levelY);
			path.lineTo(xArray[2], yArray[2]);
			path.lineTo(xArray[3], yArray[3]);
			path.lineTo(levelXLeft, levelY);
			paint.setColor(Color.argb(127, 199, 252, 236));
			paint.setStyle(Paint.Style.FILL);

			canvas.drawOval(middle, paint);
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
			canvas.drawArc(middle, 0, -180, false, paint);
			canvas.drawArc(basis, 0, -180, false, paint);
			if (!getChildren().isEmpty())
				getChildren().get(0).getPainter().onDraw(canvas);
			canvas.drawArc(up, 0, 180, false, paint);
			canvas.drawArc(middle, 0, 180, false, paint);
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
	private class CalculationTask extends TimerTask {
		private final GlassPainter painter;

		public CalculationTask() {
		this.painter = (GlassPainter) getPainter();
		}

		@Override
		public void run() {
			//getChildren().
		}
	}
}
