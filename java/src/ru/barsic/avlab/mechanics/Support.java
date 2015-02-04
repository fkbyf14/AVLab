package ru.barsic.avlab.mechanics;

import android.annotation.SuppressLint;
import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.basic.TouchListener;
import ru.barsic.avlab.graphics.Painter;
import ru.barsic.avlab.physics.*;


public class Support extends PhysObject implements IParent {

	public Support(double x, double y, double width, double height) {
		super(x, y, width, height, 0);
		painter = new SupportPainter(this);
	}

	@Override
	public int[][] getActivePolygon() {
		return new int[][] {((SupportPainter)painter).xArray[3], ((SupportPainter)painter).yArray[3]};
	}


	private class SupportPainter extends Painter {

		public int[][] xArray, yArray;
		double lengthLever = size.height / 3;
		double heightLever = size.height * 2 / 3;
		private double mX;
		private double mY;
		private  boolean b;

		public SupportPainter(PhysObject obj) {
			super(obj);
			xArray = new int[4][4];
			yArray = new int[4][4];
			updatePoints();
			defaultZ = 20;
			setZIndex(20);

		}

		@Override
		public void updatePoints() {
			int widthBlock = size.width;
			int deltaBlock = widthBlock / 7;
			int depthLever = (size.width / 3);
			int deltaLever = ((widthBlock - depthLever) / 2);



			xArray[0] = new int[] {pos.x, pos.x + size.width, pos.x + size.width, pos.x};
			yArray[0] = new int[] {pos.y, pos.y, pos.y + size.height, pos.y + size.height};
			xArray[1] = new int[] {pos.x - 2*deltaBlock , pos.x + size.width - 2*deltaBlock, pos.x + size.width - 2*deltaBlock, pos.x - 2*deltaBlock };
			yArray[1] = new int[] {pos.y + size.height - (int)heightLever, pos.y + size.height - (int)heightLever, pos.y + size.height - (int)heightLever + widthBlock, pos.y + size.height - (int)heightLever + widthBlock};
			xArray[2] = new int[] {pos.x + size.width  - 3*deltaBlock , pos.x + size.width +  (int)lengthLever, pos.x + size.width +  (int)lengthLever, pos.x + size.width  - 3*deltaBlock };
			yArray[2] = new int[] {pos.y + size.height - (int)heightLever + deltaLever, pos.y + size.height - (int)heightLever + deltaLever, pos.y + size.height - (int)heightLever + deltaLever + depthLever, pos.y + size.height - (int)heightLever + deltaLever + depthLever};
			xArray[3] = new int[] {pos.x + size.width +  (int)lengthLever, pos.x + size.width + (int)lengthLever + widthBlock, pos.x + size.width +  (int)lengthLever + widthBlock, pos.x + size.width + (int)lengthLever};
			yArray[3] = new int[] {pos.y + size.height - (int)heightLever, pos.y + size.height - (int)heightLever, pos.y + size.height - (int)heightLever + widthBlock, pos.y + size.height - (int)heightLever + widthBlock};
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

		@SuppressLint("DrawAllocation")
		@Override
		public void onDraw(Canvas canvas) {
			Paint paint = new Paint();
			paint.setColor(Color.rgb(112, 128, 144)); //цвет серый шифер, основание
			RectF oval1 = new RectF(xArray[0][0] - size.width*2 + size.width/4, yArray[0][3] - size.width/2, xArray[0][3] - size.width*2 + size.width/4 + (4 * size.width), yArray[0][3] + (int)(0.1 * size.height) - size.width/2);
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
			RectF oval2 = new RectF(xArray[1][0], yArray[1][0], xArray[1][1] ,  yArray[1][2]);
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
				if (Computation.isChoicesChoice(xArray[3], yArray[3], (int)event.getX(), (int)event.getY())) {
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
					event.getY() <= yArray[0][3] - size.width * 3 / 2 && event.getX() <= (size.height + pos.x) && getChildren().isEmpty())

					moveBoundBlock((int)(event.getX() - mX), (int)(event.getY() - mY));
				mX = event.getX();
				mY = event.getY();
				break;
			}
			return true;
		}

		public void moveBoundBlock(int dx, int dy) {
			System.out.println("moveBoundBlock");
			for (int j = 0; j < xArray[3].length; j++) {
				yArray[1][j] += dy;
				yArray[3][j] += dy;
				yArray[2][j] += dy;
				xArray[3][j] += dx;


			}
			xArray[2][1] += dx;
			xArray[2][2] += dx;

			/*if(xArray[3][2] <= xArray[1][2]){
				gr.setColor(Color.GRAY);
				gr.fillOval(xArray[1][0] - 4, yArray[1][0], size.width, size.width);
			}
*/
		}
	}

	@Override
	public boolean attach(PhysObject child) {
		if (child instanceof IGluer && getChildren().isEmpty() && super.attach(child)) {
			child.getPainter().setPos(((SupportPainter)painter).xArray[3][1] - (child.getPainter().getSize().width) / 4, child.getPainter().getPos().y);

			return true;

		}
		return false;
	}

	@Override
	public boolean isInAria(int x, int y) {
		return TouchListener.selected.object instanceof IGluer &&
			Computation.intersect(getActivePolygon(), ((IGluer)TouchListener.selected.object).getGlueyPolygon());
	}
}
