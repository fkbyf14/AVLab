package ru.barsic.avlab.mechanics;

import java.util.Arrays;

import android.graphics.*;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.basic.TouchListener;
import ru.barsic.avlab.graphics.Painter;
import ru.barsic.avlab.physics.Computation;
import ru.barsic.avlab.physics.IParent;

public class Balance extends PhysObject implements IParent {
	public Balance(double x, double y, double width, double height) {
		super(x, y, width, height, 0);
		painter = new BalancePathPainter(this);
	}
	private class BalancePathPainter extends Painter{
		private int[][] xArray, yArray;

		public BalancePathPainter(PhysObject obj) {
			super(obj);
			xArray = new int[4][4];
			yArray = new int[4][4];
			setZIndex(100);
			updatePoints();
		}

		@Override
		public void updatePoints() {
			xArray[0] = new int[] {pos.x, pos.x + size.width, pos.x + size.width + size.width / 10, pos.x - size.width / 10};
			yArray[0] = new int[] {pos.y, pos.y, pos.y + size.height, pos.y + size.height};
			xArray[1] = new int[] {pos.x + size.width / 2 - size.width / 30, pos.x + size.width / 2 + size.width / 30, pos.x + size.width / 2 + size.width / 30, pos.x + size.width / 2 - size.width / 30};
			yArray[1] = new int[] {pos.y + size.height / 2 - size.width, pos.y + size.height / 2 - size.width, pos.y + size.height / 2, pos.y + size.height / 2};
			xArray[2] = new int[] {pos.x + size.width / 2, pos.x + size.width / 2 - size.width / 4, pos.x + size.width / 2 - size.width / 4, pos.x + size.width / 2 - size.width / 15, pos.x + size.width / 2 - size.width / 15, pos.x + size.width / 15, pos.x + size.width / 15};
			yArray[2] = new int[] {pos.y + size.height / 2 - size.width + size.width / 15, pos.y + size.height / 2 - size.width + size.width / 15, pos.y + size.height / 2 - size.width + size.width / 7, pos.y + size.height / 2 - size.width + size.width / 5, pos.y, pos.y, pos.y + size.height / 2 - size.width + size.width / 5};
			xArray[3] = new int[] {pos.x + size.width / 2, pos.x + size.width / 2 + size.width / 4, pos.x + size.width / 2 + size.width / 4, pos.x + size.width - size.width / 15, pos.x + size.width - size.width / 15, pos.x + size.width / 2 + size.width / 15, pos.x + size.width / 2 + size.width / 15 };
			yArray[3] = new int[] {yArray[1][0] + size.width / 15, yArray[1][0] + size.width / 15, yArray[1][0] + size.width / 7, yArray[1][0] + size.width / 5, pos.y, pos.y, yArray[1][0] + size.width / 5};
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
			Path path = new Path();
			Paint paint = new Paint();
			paint.setColor(Color.BLACK); //основа
			path.moveTo(xArray[0][0], yArray[0][0]);
			for (int i = 0; i < xArray[0].length; i++){
				path.lineTo(xArray[0][i],yArray[0][i]);
			}
			path.lineTo(xArray[0][0], yArray[0][0]);
			paint.setStyle(Paint.Style.FILL);
			canvas.drawRect(xArray[0][3],yArray[0][3],xArray[0][2],yArray[0][2] + size.height / 30, paint);
			canvas.drawPath(path, paint);
			paint.setColor(Color.GRAY);
			path.moveTo(xArray[0][0], yArray[0][0]);
			for (int i = 0; i < xArray[0].length; i++){
				path.lineTo(xArray[0][i],yArray[0][i]);
			}
			path.lineTo(xArray[0][0], yArray[0][0]);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(xArray[0][3],yArray[0][3],xArray[0][2],yArray[0][2] + size.height / 30, paint);
			canvas.drawPath(path, paint);

			paint.setColor(Color.GRAY);//палка
			paint.setStyle(Paint.Style.FILL);
			canvas.drawRect(xArray[1][0], yArray[1][0], xArray[1][1], yArray[1][2], paint);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(xArray[1][0], yArray[1][0], xArray[1][1], yArray[1][2], paint);

			path.reset();//треугольник
			path.moveTo(xArray[1][0], yArray[1][0] + size.width / 10);
			path.lineTo(xArray[0][0] + size.width / 2, yArray[1][0] + size.width / 15 );
			path.lineTo(xArray[1][1], yArray[1][0] + size.width / 10);
			path.lineTo(xArray[1][0], yArray[1][0] + size.width / 10);
			paint.setColor(Color.rgb(59, 68, 75));
			paint.setStyle(Paint.Style.FILL);
			canvas.drawPath(path, paint);
			path.reset();
			path.moveTo(xArray[1][0], yArray[1][0] + size.width / 10);
			path.lineTo(xArray[0][0] + size.width / 2, yArray[1][0] + size.width / 15 );
			path.lineTo(xArray[1][1], yArray[1][0] + size.width / 10);
			path.lineTo(xArray[1][0], yArray[1][0] + size.width / 10);
			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawPath(path, paint);

			path.reset();//левая чашка
			System.out.println("-------------------"+Arrays.deepToString(yArray));
			path.moveTo(xArray[2][0],yArray[2][0]);
			for (int i = 0; i < xArray[2].length; i++){
				path.lineTo(xArray[2][i],yArray[2][i]);
			}
			path.lineTo(xArray[2][2],yArray[2][2]);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(4);
			paint.setColor(Color.rgb(47, 79, 79));
			canvas.drawPath(path, paint);

			RectF ovalL = new RectF(xArray[2][5],yArray[2][5] - size.height / 5, xArray[2][4], yArray[2][5] + size.height / 5);
			canvas.drawOval(ovalL, paint);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.BLACK);
			canvas.drawOval(ovalL, paint);

			path.reset();//правая чашка
			path.moveTo(xArray[3][0], yArray[3][0]);
			for (int i = 0; i < xArray[3].length; i++){
				path.lineTo(xArray[3][i],yArray[3][i]);
			}
			path.lineTo(xArray[3][2],yArray[3][2]);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(4);
			paint.setColor(Color.rgb(47, 79, 79));
			canvas.drawPath(path, paint);

			RectF ovalR = new RectF(xArray[3][5],yArray[3][5] - size.height / 5, xArray[3][4], yArray[3][5] + size.height / 5);
			canvas.drawOval(ovalR, paint);
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.BLACK);
			canvas.drawOval(ovalR, paint);

//			path.reset();
//			path.moveTo(634, -255);
//			path.lineTo(493, -255);
//			path.lineTo(493, -182);
//			path.lineTo(597, -149);
//			path.lineTo(597, 256);
//			path.lineTo(389, 256);
//			path.lineTo(389, -149);
//			paint.setStrokeWidth(6);
//			paint.setColor(Color.BLACK);
//			paint.setStyle(Paint.Style.STROKE);
//			canvas.drawPath(path, paint);
//
//			path.reset();
//			path.moveTo(456, 83);
//			path.lineTo(468, 78);
//			path.lineTo(468, 58);
//			path.lineTo(507, 58);
//			path.lineTo(507, 78);
//			path.lineTo(519, 83);
//			path.lineTo(519, 159);
//			path.lineTo(456, 159);
//			paint.setStrokeWidth(6);
//			paint.setColor(Color.BLACK);
//			canvas.drawPath(path, paint);

		}
	}

	@Override
	public boolean isInAria(int x, int y) {
		if(TouchListener.selected.object instanceof IWeighing){
			//System.out.println("ActivePoligon = " + Arrays.deepToString(getActivePolygon()));
			//System.out.println("WeighingPolygon = " + Arrays.deepToString(((IWeighing)TouchListener.selected.object).getWeighingPolygon()));
			if(Computation.intersect(getActivePolygon(), ((IWeighing)TouchListener.selected.object).getWeighingPolygon())){

			}
		}
		return TouchListener.selected.object instanceof IWeighing &&
			Computation.intersect(getActivePolygon(), ((IWeighing)TouchListener.selected.object).getWeighingPolygon());
	}

	@Override
	public boolean attach(PhysObject child) {
		if (child instanceof IWeighing  && getChildren().isEmpty() && super.attach(child)) {
			child.getPainter().setPos(child.getPainter().getPos().x, ((BalancePathPainter)painter).yArray[2][4]);

			return true;

		}
		return false;
	}

	@Override
	public int[][] getActivePolygon() {
		return new int[][] {((BalancePathPainter)painter).xArray[2], ((BalancePathPainter)painter).yArray[2]}; //((BalancePathPainter)painter).yArray[3], ((BalancePathPainter)painter).yArray[3]};
	}
}
