package ru.barsic.avlab.physics;

import android.graphics.*;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.graphics.Painter;

import java.util.ArrayList;

public class Scene extends PhysObject {

	public static ArrayList<PhysObject> objects = new ArrayList<>();
	public static ArrayList<IParent> parents = new ArrayList<>();

	public Scene(double x, double y, double width, double height) {
		super(x, y, width, height, 0);
		painter = new ScenePainter(this);
	}

	/**
	 * При создании интерфейса добавляется дополнительное условие, которое
	 * определяет, где будет храниться ссылка на объект.
	 */
	public static void allocation(PhysObject obj) {
		objects.add(obj);
		if (obj instanceof IParent) {
			parents.add((IParent) obj);
		}
	}


	private class ScenePainter extends Painter {
		private int[] xArray, yArray;

		public ScenePainter(PhysObject obj) {
			super(obj);
			xArray = new int[6];
			yArray = new int[6];
			updatePoints();

		}

		@Override
		public void updatePoints() {
			xArray = new int[]{getPos().x, getPos().x + size.width, getPos().x + size.width, getPos().x, getPos().x + size.width / 20, getPos().x + size.width - size.width / 20};
			yArray = new int[]{getPos().y, getPos().y, getPos().y + size.height, getPos().y + size.height, getPos().y - 2 * size.height, getPos().y - 2 * size.height};

		}

		@Override
		public void changePosition(int dx, int dy) {

		}

		@Override
		public void onDraw(Canvas canvas) {
			Paint paint = new Paint();
			Path path = new Path();

			path.moveTo(xArray[0], yArray[0]);
			path.lineTo(xArray[4], yArray[4]);
			path.lineTo(xArray[5], yArray[5]);
			path.lineTo(xArray[1], yArray[1]);

			paint.setColor(Color.rgb(96, 160, 96));
			Rect rect = new Rect();
			rect.set(xArray[0], yArray[0], xArray[1], yArray[2]);
			paint.setStyle(Paint.Style.FILL);
			canvas.drawRect(rect, paint);
			canvas.drawPath(path, paint);

			paint.setColor(Color.BLACK);
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(rect, paint);
			canvas.drawPath(path, paint);


		}


		@Override
		public boolean onTouch(View v, MotionEvent event) {
			super.onTouch(v, event);
			if (event.getAction() == MotionEvent.ACTION_DOWN)

				painter.setZIndex(0);

			return true;

		}

		@Override
		public boolean isChoice(int x, int y) {
			return false;
		}
	}
}