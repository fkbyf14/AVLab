package ru.barsic.avlab.mechanics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.basic.TouchListener;
import ru.barsic.avlab.graphics.Painter;
import ru.barsic.avlab.helper.Logging;
import ru.barsic.avlab.helper.ScalingUtil;
import ru.barsic.avlab.physics.Computation;
import ru.barsic.avlab.physics.IGluer;
import ru.barsic.avlab.physics.IParent;


public class Support extends PhysObject implements IParent {

	private double lengthLever;
	private double heightLever;

	public Support(double x, double y, double width, double height) {
		super(x, y, width, height, 0);
		lengthLever = height / 3;
		heightLever = height * 2 / 3;
		painter = new SupportPainter(this);
	}

	@Override
	public int[][] getActivePolygon() {
		return new int[][]{((SupportPainter) painter).xArray[3], ((SupportPainter) painter).yArray[3]};
	}

	@Override
	public boolean attach(PhysObject child) {
		if (child instanceof IGluer && getChildren().isEmpty() && super.attach(child)) {
			child.getPainter().setPos(((SupportPainter) painter).xArray[3][1] - (child.getPainter().getSize().width) / 4, child.getPainter().getPos().y);
			child.getPainter().setZIndex(painter.getZIndex() - 1);
			return true;

		}
		Logging.log("Attach Child of Support", child, "x = " + x + ", y = " + y);
		return false;
	}

	@Override
	public boolean isInAria(int x, int y) {
		return TouchListener.selected.object instanceof IGluer &&
				Computation.intersect(getActivePolygon(), ((IGluer) TouchListener.selected.object).getGlueyPolygon());
	}

	private class SupportPainter extends Painter {

		public int[][] xArray, yArray;
		int lengthLever;
		int heightLever;

		private double mX;
		private double mY;
		private boolean b;

		public SupportPainter(PhysObject obj) {
			super(obj);
			lengthLever = ScalingUtil.scalingRealSizeToX(Support.this.lengthLever);
			heightLever = ScalingUtil.scalingRealSizeToY(Support.this.heightLever);
			xArray = new int[4][4];
			yArray = new int[4][4];
			updatePoints();
			defaultZ = 20;
			setZIndex(20);

		}

		@Override
		public void updateSize() {
			super.updateSize();
			lengthLever = ScalingUtil.scalingRealSizeToX(Support.this.lengthLever);
			heightLever = ScalingUtil.scalingRealSizeToY(Support.this.heightLever);

		}

		@Override
		public void updatePoints() {
			int widthBlock = size.width;
			int deltaBlock = widthBlock / 7;
			int depthLever = (size.width / 3);
			int deltaLever = ((widthBlock - depthLever) / 2);

			xArray[0] = new int[]{getPos().x, getPos().x + size.width, getPos().x + size.width, getPos().x};
			yArray[0] = new int[]{getPos().y, getPos().y, getPos().y + size.height, getPos().y + size.height};
			xArray[1] = new int[]{getPos().x - 2 * deltaBlock, getPos().x + size.width - 2 * deltaBlock, getPos().x + size.width - 2 * deltaBlock, getPos().x - 2 * deltaBlock};
			yArray[1] = new int[]{getPos().y + size.height - heightLever, getPos().y + size.height - heightLever, getPos().y + size.height - heightLever + widthBlock, getPos().y + size.height - heightLever + widthBlock};
			xArray[2] = new int[]{getPos().x + size.width - 3 * deltaBlock, getPos().x + size.width + lengthLever, getPos().x + size.width + lengthLever, getPos().x + size.width - 3 * deltaBlock};
			yArray[2] = new int[]{getPos().y + size.height - heightLever + deltaLever, getPos().y + size.height - heightLever + deltaLever, getPos().y + size.height - heightLever + deltaLever + depthLever, getPos().y + size.height - heightLever + deltaLever + depthLever};
			xArray[3] = new int[]{getPos().x + size.width + lengthLever, getPos().x + size.width + lengthLever + widthBlock, getPos().x + size.width + lengthLever + widthBlock, getPos().x + size.width + lengthLever};
			yArray[3] = new int[]{getPos().y + size.height - heightLever, getPos().y + size.height - heightLever, getPos().y + size.height - heightLever + widthBlock, getPos().y + size.height - heightLever + widthBlock};
		}

		@Override
		public void changePosition(int dx, int dy) {
			for (int i = 0; i < xArray.length; i++) {
				for (int j = 0; j < xArray[i].length; j++) {
					xArray[i][j] += dx;
					yArray[i][j] += dy;
				}
			}
		}

		@Override
		public void onDraw(Canvas canvas) {
			Paint paint = new Paint();
			paint.setColor(Color.rgb(112, 128, 144)); //цвет серый шифер, основание
			RectF oval1 = new RectF(xArray[0][0] - size.width * 2 + size.width / 4, yArray[0][3] - size.width / 2, xArray[0][3] - size.width * 2 + size.width / 4 + (4 * size.width), yArray[0][3] + (int) (0.1 * size.height) - size.width / 2);
			canvas.drawOval(oval1, paint);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			canvas.drawOval(oval1, paint);
			paint.setStyle(Paint.Style.FILL);
			RectF rect1 = new RectF(xArray[0][0], yArray[0][0], xArray[0][0] + size.width / 2, yArray[0][0] + size.height);
			canvas.drawRect(rect1, paint);
			RectF rect2 = new RectF(xArray[2][0], yArray[2][0], xArray[2][1], yArray[2][2]);
			canvas.drawRect(rect2, paint);
			paint.setColor(Color.rgb(112, 128, 144));
			RectF oval2 = new RectF(xArray[1][0], yArray[1][0], xArray[1][1], yArray[1][2]);
			canvas.drawOval(oval2, paint);

			if (!getChildren().isEmpty())
				getChildren().get(0).getPainter().onDraw(canvas);

			RectF rect3 = new RectF(xArray[3][0], yArray[3][0], xArray[3][1], yArray[3][2]);
			canvas.drawRect(rect3, paint);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			canvas.drawRect(rect3, paint);

		}

		@Override
		public boolean isChoice(int x, int y) {
			return super.isChoice(x, y) || Computation.isChoicesChoice(xArray[3], yArray[3], x, y);
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (Computation.isChoicesChoice(xArray[3], yArray[3], (int) event.getX(), (int) event.getY())) {
						setMovable(false);
						b = true;
					} else {
						setMovable(false);
						b = false;
					}
					mX = event.getX();
					mY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					if (isMovable()) {
						super.onTouch(v, event);
					} else if (b && event.getY() >= yArray[0][0] + size.width / 2 &&
							event.getY() <= yArray[0][3] - size.width * 3 / 2 && event.getX() <= (size.height + getPos().x) && getChildren().isEmpty())

						moveBoundBlock((int) (event.getX() - mX), (int) (event.getY() - mY));
					mX = event.getX();
					mY = event.getY();
					break;
			}
			return true;
		}

		public void moveBoundBlock(int dx, int dy) {
			System.out.println("moveBoundBlock");
			lengthLever += dx;
			heightLever -= dy;
			Support.this.lengthLever = ScalingUtil.scalingXToRealSize(lengthLever);
			Support.this.heightLever = ScalingUtil.scalingYToRealSize(heightLever);

			updatePoints();
		}
	}
}
