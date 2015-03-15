package ru.barsic.avlab.mechanics;

import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.basic.TouchListener;
import ru.barsic.avlab.graphics.DrawView;
import ru.barsic.avlab.graphics.Painter;
import ru.barsic.avlab.physics.Computation;
import ru.barsic.avlab.physics.IParent;

import java.util.Date;
import java.util.TimerTask;

public class Balance extends PhysObject implements IParent {

	//--------------- CONSTANTS --------------- 
	private static final int LEFT_BOWL = 1;
	private static final int RIGHT_BOWL = 2;
	private double leftMass = 0;
	private double rightMass = 0;

	//--------------- PRIVATE STATIC FIELDS --------------- 

	private static int currentBowl;

	//--------------- PRIVATE STATIC METHODS --------------- 

	//--------------- PRIVATE OBJECT FIELDS --------------- 

	//--------------- PUBLIC OBJECT FIELDS --------------- 

	//--------------- CONSTRUCTORS --------------- 

	public Balance(double x, double y, double width, double height) {
		super(x, y, width, height, 0);
		painter = new BalancePathPainter(this);
	}

	//--------------- PUBLIC OBJECT OVERRIDEN METHODS --------------- 

	@Override
	public boolean isInAria(int x, int y) {

		PhysObject object = TouchListener.selected.object;
		if (object instanceof IWeighing) {
			//System.out.println("ActivePoligon = " + Arrays.deepToString(getActivePolygon()));
			//System.out.println("WeighingPolygon = " + Arrays.deepToString(((IWeighing)TouchListener.selected.object).getWeighingPolygon()));
			if (object.getPainter().getCenter().x <= painter.getCenter().x) {
				currentBowl = LEFT_BOWL;
				object.getPainter().setPos(object.getPainter().getPos().x, ((BalancePathPainter)painter).yArray[2][5] - object.getPainter().getSize().height / 2);
			} else {
				currentBowl = RIGHT_BOWL;
				object.getPainter().setPos(object.getPainter().getPos().x, ((BalancePathPainter)painter).yArray[3][5] - object.getPainter().getSize().height / 2);
			}
			if (Computation.intersect(getActivePolygon(), ((IWeighing)object).getWeighingPolygon())) {

			}
		}
		return object instanceof IWeighing &&
			Computation.intersect(getActivePolygon(), ((IWeighing)object).getWeighingPolygon());
	}


	@Override
	public boolean attach(PhysObject child) {
		if (child instanceof IWeighing) {
			child.getPainter().setPos(child.getPainter().getPos().x,
				((BalancePathPainter)painter).yArray[currentBowl == LEFT_BOWL ? 2 : 3][5] - child.getPainter().getSize().height);
			child.getPainter().setZIndex(getPainter().getZIndex() + 2);
			//---------------------------- если заатачили то сразу массу прибавили
			if (currentBowl == LEFT_BOWL)
				leftMass += child.mass;
			else
				rightMass += child.mass;

			DrawView.timer.schedule(new CalculationTask(), new Date(System.currentTimeMillis()));

			return super.attach(child);

		}
		return false;
	}

	@Override
	public boolean detach(PhysObject child) {
		//----------------------------реализуй правильно метод, я только с массами кусок реализовал (если задетачили то сразу массу отнимаем)
		if (currentBowl == LEFT_BOWL)
			leftMass -= child.mass;
		else
			rightMass -= child.mass;
		if (!getChildren().isEmpty()) {
			boolean result = super.detach(child);
			DrawView.timer.schedule(new CalculationTask(), new Date(System.currentTimeMillis()));
			return result;
		}
		return false;
	}



	@Override
	public int[][] getActivePolygon() {
		int[] x = ((BalancePathPainter)painter).xArray[2];
		int[] y = ((BalancePathPainter)painter).yArray[2];
		int[] xx = ((BalancePathPainter)painter).xArray[3];
		int[] yy = ((BalancePathPainter)painter).yArray[3];
		if (currentBowl == LEFT_BOWL)
			return new int[][] {new int[] {x[5], x[4], x[3], x[6]}, new int[] {y[3], y[6], y[5], y[4]}};
		 else
			return new int[][] {new int[] {xx[5], xx[4], xx[3], xx[6]}, new int[] {yy[3], yy[6], yy[5], yy[4]}};
	}

	//--------------- PUBLIC OBJECT METHODS --------------- 

	public int getCurrentBowl(PhysObject obj) {
		return obj.getPainter().getCenter().x <= painter.getCenter().x ? LEFT_BOWL : RIGHT_BOWL;
	}

	//--------------- PRIVATE OBJECT OVERRIDEN METHODS --------------- 

	//--------------- PRIVATE OBJECT METHODS --------------- 

	//--------------- PUBLIC INNER CLASSES --------------- 

	//--------------- PRIVATE INNER CLASSES --------------- 

	private class BalancePathPainter extends Painter {

	//внутри внутренних классов порядок такой же

		private int[][] xArray, yArray;
		private int[] initialLeft, initialRight;

		public BalancePathPainter(PhysObject obj) {
			super(obj);
			xArray = new int[4][4];
			yArray = new int[4][4];
			initialLeft = yArray[2];
			initialRight = yArray[3];
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
			xArray[3] = new int[] {pos.x + size.width / 2, pos.x + size.width / 2 + size.width / 4, pos.x + size.width / 2 + size.width / 4, pos.x + size.width - size.width / 15, pos.x + size.width - size.width / 15, pos.x + size.width / 2 + size.width / 15, pos.x + size.width / 2 + size.width / 15};
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
			if (event.getAction() == MotionEvent.ACTION_DOWN ) {
				if (!getChildren().isEmpty())
					painter.setZIndex(100);
				for(PhysObject p : getChildren()) {
					p.getPainter().setZIndex(getZIndex() + 10);
				}
			}
			return true;
		}

		private void drawRightBowl(Canvas canvas, Path path, Paint paint) {
			path.reset();//правая чашка
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
			path.reset();//левая чашка
			//System.out.println("-------------------"+Arrays.deepToString(yArray));
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

	//balance нам тут не нужен, так как это класс не статический(т.е. он существует только для объекта, поэтому он знает все о своем внешнем классе. другой такой объект не может получить такой же экземляр этого класса, )
		//поэтому вметсто balance.getChildren()(как для статического внетреннего класса) мы пишем просто getchildren()
		private final BalancePathPainter painter;

//		private double leftMass = 0;
//		private double rightMass = 0;

		public CalculationTask() {
			this.painter = (BalancePathPainter)getPainter();
		}

		@Override
		public void run() {
			for (PhysObject p : getChildren()) {


				if (leftMass > rightMass && p.getChildren().isEmpty()) {
					//цикл по левой чашке
					for (int i = 0; i < painter.yArray[2].length; i++) {
						painter.yArray[2][i] += painter.getSize().height - painter.getSize().height / 3;
						painter.yArray[3][i] -= painter.getSize().height + painter.getSize().height / 3;
						if (p.getPainter().getCenter().x < painter.getCenter().x){
							p.getPainter().getPos().y +=  painter.getSize().height - painter.getSize().height / 3;
						}
						else
							p.getPainter().getPos().y -=  painter.getSize().height + painter.getSize().height / 3;
					}

				} else {
					//цикл по правой чашке
					for (int i = 0; i < painter.yArray[3].length; i++) {
						painter.yArray[2][i] -= painter.getSize().height + painter.getSize().height / 3;
						painter.yArray[3][i] += painter.getSize().height - painter.getSize().height / 3;
						if (p.getPainter().getCenter().x < painter.getCenter().x){
							p.getPainter().getPos().y -=  painter.getSize().height + painter.getSize().height / 3;
						}
						else
							p.getPainter().getPos().y +=  painter.getSize().height - painter.getSize().height / 3;
					}
				}
			}
		}
	}

}
