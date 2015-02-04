package ru.barsic.avlab.basic;

import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.graphics.DrawView;
import ru.barsic.avlab.graphics.Painter;
import ru.barsic.avlab.helper.ScalingUtil;

public class TouchListener implements View.OnTouchListener {

	public static int addX;
	public static int addY;
	public static int currentZ;
	public static Painter selected;
	public int screenHeight;
	public int screenWidth;
	private int firstFingerX = -1;
	private int firstFingerY = -1;
	private int secondFingerX = -1;
	private int secondFingerY = -1;
	private double startDistance = -1;
	private double startScaleFactor;

	public TouchListener(int screenHeight, int screenWidth) {
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
	}

	public static Painter choiceObject(int x, int y) {
		for (int i = DrawView.painters.size() - 1; i >= 0; i--) {
			if (DrawView.painters.get(i).isChoice(x, y))
				return DrawView.painters.get(i);
		}
		return null;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();
		int touchCount = event.getPointerCount();
		int currentTouch = event.getActionIndex();
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN: // нажатие
			selected = choiceObject(x, y);
			System.out.println("Mouse pressed selected = " + selected);
			addX = x;
			addY = y;
			firstFingerX = x;
			firstFingerY = y;

			if (selected != null) {
				currentZ = selected.getZIndex();
				selected.setZIndex(DrawView.maxZIndex);
				Painter h = selected;
				while (h.getHolder() != null) {
					h = h.getHolder();
					if (h.isMovable())
						break;
				}
				if (!selected.isMovable())
					selected = h;
//            selected.setZIndex(GraphScene.maxZIndex);
//            GraphScene.sortByZ();
				selected.onTouch(view, event);
			} else {
				for (Painter painter : DrawView.painters) {
					painter.onTouch(view, event);
				}
			}
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			if (startDistance == -1 && currentTouch == 1) {
				secondFingerX = (int)event.getX(1);
				secondFingerY = (int)event.getY(1);
				startDistance = calcDistance(firstFingerX, firstFingerY, secondFingerX, secondFingerY);
				startScaleFactor = ScalingUtil.getGlobalScaleFactor();
			}
			break;
		case MotionEvent.ACTION_MOVE: // движение
			if (selected != null)
				selected.onTouch(view, event);
			else
				DrawView.moveVisibleArea(x - addX, y - addY);
			addX = x;
			addY = y;
			if (currentTouch == 0) {
				firstFingerX = x;
				firstFingerY = y;
			}

			if (currentTouch == 1) {
				secondFingerX = (int)event.getX(1);
				secondFingerY = (int)event.getY(1);
			}
			if (startDistance != -1)
				ScalingUtil.setGlobalScaleFactor(startScaleFactor *
					calcDistance(firstFingerX, firstFingerY, secondFingerX, secondFingerY) / startDistance);
			break;
		case MotionEvent.ACTION_UP: // отпускание
		case MotionEvent.ACTION_CANCEL:
			if (selected != null) {
				System.out.println("Mouse pressed released = " + selected);
				selected.setZIndex(currentZ);
				selected.onTouch(view, event);
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			if (touchCount == 2) {
				System.out.println("!!!!!!!!!!!!!!!!!");
				startDistance = -1;
			}
			break;
		}

		return true;
	}

	private double calcDistance(int aX, int aY, int bX, int bY) {
		return Math.sqrt((aX - bX) * (aX - bX) +
			(aY - bY) * (aY - bY));
	}
}
