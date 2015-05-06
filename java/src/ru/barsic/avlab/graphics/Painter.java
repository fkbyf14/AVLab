package ru.barsic.avlab.graphics;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.PhysObject;
import ru.barsic.avlab.basic.TouchListener;
import ru.barsic.avlab.basic.World;
import ru.barsic.avlab.helper.Dimension;
import ru.barsic.avlab.helper.Logging;
import ru.barsic.avlab.helper.ScalingUtil;
import ru.barsic.avlab.physics.IParent;
import ru.barsic.avlab.physics.Scene;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Класс графического объекта. Предусмотрено автоматическое добавление объекта в
 * список отрисовки при инициализации.Обработка касаний
 */
public abstract class Painter implements View.OnTouchListener, Comparable<Painter> {

	/**
	 * Ссылка на отрисоваемый физический объект.
	 */
	public final PhysObject object;
	/**
	 * ZIndex, который был выдан во время инициализации объекта
	 */
	protected int defaultZ;

	/**
	 * Владелец объекта. Чаще всего его нет.
	 */
	protected Painter holder;

	/**
	 * Положение графического объекта на графической сцене. Не путать с
	 * положением физического объекта на физической сцене.
	 */
	/**
	 * Линейные размеры графического объекта. Координаты точки центра будут:
	 * (pos.x + size.width / 2, pos.y + size.height / 2)
	 */
	protected Dimension size;
	/**
	 * Список зависящих объектов
	 */
	protected ArrayList<Painter> inside = new ArrayList();

	private Point pos;

	/**
	 * Центральная точка графического объекта. Может когда-то кому-то
	 * пригодиться...
	 */
	private Point center;

	/**
	 * Определяет положение объекта в списке отрисовки.
	 */
	private int zIndex;

	/**
	 * Поле, указывающее на возможность объекта перемещаться по графической
	 * сцене.
	 */
	private boolean movable;

	/**
	 * @param obj физический объект, который рисуем
	 */
	public Painter(PhysObject obj) {
		this.object = obj;
		movable = true;
		pos = new Point(ScalingUtil.scalingRealSizeToX(obj.x - World.deviceX), ScalingUtil.scalingRealSizeToY(obj.y - World.deviceY));
		int width = ScalingUtil.scalingRealSizeToX(obj.width);
		int height = ScalingUtil.scalingRealSizeToY(obj.height);
		size = new Dimension(width, height);
		center = new Point(pos.x + width / 2, pos.y + height / 2);
		DrawView.painters.add(this);
	}

	/**
	 * Предполагается, что объект несёт в себе вспомагательную функцию
	 *
	 * @param holder владелец объекта
	 */
	public Painter(Painter holder) {
		if (holder == null)
			throw new IllegalArgumentException("Holder can not be null");
		object = null;
		movable = false;
		setHolder(holder);
		pos = new Point(holder.pos.x, holder.pos.y);
		size = new Dimension(holder.size.width, holder.size.height);
		center = new Point(pos.x + size.width / 2, pos.y + size.height / 2);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		int z = object.getPainter().getZIndex();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: // нажатие
				Logging.log("ACTION_DOWN", this, "x = " + x + ", y = " + y + ", z = " + z);
				if (object.getParent() != null) {
					object.getParent().detach(object);
				}
				break;
			case MotionEvent.ACTION_MOVE: // движение
				if (event.getPointerCount() == 1) {
					System.out.println("ACTION_MOVE x = " + x + " addX =" + TouchListener.addX + " y = " + y + " addY =" + TouchListener.addY);
					moveBy(x - TouchListener.addX, y - TouchListener.addY);
				}
				break;
			case MotionEvent.ACTION_UP: // отпускание
				Logging.log("ACTION_UP", this, "x = " + x + ", y = " + y + ", z = " + z);
			case MotionEvent.ACTION_CANCEL:

				for (IParent parent : Scene.parents) {
					if (parent.isInAria(x, y)) {
						((PhysObject) parent).attach(object);
						break;
					}
				}
				break;
		}
		return true;
	}

	/**
	 * Удаление объекта и всех его частей из списка отрисуемых
	 */
	public void remove() {
		DrawView.painters.remove(this);
		for (Painter p : inside)
			p.remove();
	}

	/**
	 * первоначальная оценка при выборе объекта. При сложной форме объекта -
	 * переопределить
	 *
	 * @param x - X координата курсора
	 * @param y - Y координата курсора
	 * @return - true or false
	 */
	public boolean isChoice(int x, int y) {
		return x > pos.x && y > pos.y && x < pos.x + size.width && y < pos.y + size.height;
	}

	/**
	 * Определяет положение и размер объекта в графической сцене, относительно
	 * его состояния в "физическом мире"
	 */
	public void updateSize() {
		if (object == null)
			return;

		System.out.println("updateSize obj" + object);
		System.out.println("updateSize device.x = " + World.deviceX + ", device.y = " + World.deviceY);


		setSize(ScalingUtil.scalingRealSizeToX(object.width), ScalingUtil.scalingRealSizeToY(object.height));
		setPos(ScalingUtil.scalingRealSizeToX(object.x - World.deviceX), ScalingUtil.scalingRealSizeToY(object.y - World.deviceY));
		for (Painter p : inside) {
			System.out.println("******************** p = " + p);
			p.updateSize();
		}
	}

	/**
	 * Определение координат точек, участвующих в отрисовке
	 */
	abstract public void updatePoints();

	/**
	 * Вспомогательная процедура изменения координат точек отрисовки при
	 * перемещении объекта. Может быть пустой.
	 *
	 * @param dx - сдвиг по оси X
	 * @param dy - сдвиг по оси Y
	 */
	abstract public void changePosition(int dx, int dy);

	/**
	 * Отрисовка компонента на основе полей, инициализированных в init() и
	 * определённых в updatePoints()
	 *
	 * @param canvas - "холст" для отрисовки
	 */
	abstract public void onDraw(Canvas canvas);

	/**
	 * Смещение объекта и всех его частей на вектор (dx, dy). Если объект имеет
	 * сложную форму, необходимо переопределение
	 *
	 * @param dx - сдвиг по оси X
	 * @param dy - сдвиг по оси Y
	 */
	public void moveBy(int dx, int dy) {
		pos.x += dx;
		pos.y += dy;
		center.x += dx;
		center.y += dy;
		changePosition(dx, dy);
		if (object != null)
			object.updatePos();
		for (Painter p : inside)
			p.moveBy(dx, dy);
	}

	public Painter getHolder() {
		return holder;
	}

	/**
	 * Смена "владельца" объекта с учётом интересов его предыдущего "хозяина"
	 *
	 * @param holder холдер
	 */
	public void setHolder(Painter holder) {
		if (this.holder != null)
			this.holder.inside.remove(this);
		if (holder != null) {
			holder.inside.add(this);
			this.holder = holder;
		} else {
			this.holder = null;
		}
	}

	public boolean isMovable() {
		return movable;
	}

	public void setMovable(boolean movable) {
		this.movable = movable;
	}

	public int getZIndex() {
		return zIndex;
	}

	public int getDefaultZ() {
		return defaultZ;
	}

	public void setZIndex(int zIndex) {
		Logging.log("setZIndex", this, "oldZ=" + this.zIndex + ", newZ = " + zIndex);
		this.zIndex = zIndex;

		Collections.sort(DrawView.painters);
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(int x, int y) {
		pos.x = x;
		pos.y = y;
		updateCenter();
		updatePoints();
		if (object != null)
			object.updatePos();
	}

	private void updateCenter() {
		center.x = pos.x + size.width / 2;
		center.y = pos.y + size.height / 2;
	}

	public void updatePos() {
		if (object != null || holder != null) {
			pos.x = ScalingUtil.scalingRealSizeToX(object.x - World.deviceX);
			pos.y = ScalingUtil.scalingRealSizeToY(object.y - World.deviceY);
			updateCenter();
			updatePoints();
			for (PhysObject obj : object.getChildren())
				obj.getPainter().updatePos();
		}
	}

	public Dimension getSize() {
		return size;
	}

	public void setSize(int width, int height) {
		size.width = width;
		size.height = height;
		updateCenter();
		updatePoints();
	}

	public Point getCenter() {
		return center;
	}

	public void moveToDefault() {
		if (object != null)
			object.moveToDefault();
	}

	public int compareTo(Painter other) {
		return zIndex - other.zIndex;
	}

	@Override
	public String toString() {
		return "{" + object + "}";
	}
}
