package ru.barsic.avlab.graphics;

import java.util.ArrayList;
import java.util.Collections;

import android.graphics.Canvas;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;
import ru.barsic.avlab.basic.*;
import ru.barsic.avlab.helper.*;
import ru.barsic.avlab.physics.IParent;
import ru.barsic.avlab.physics.Scene;

/**
 * Класс графического объекта. Предусмотрено автоматическое добавление объекта в
 * список отрисовки при инициализации. Является наследником MouseAdapter для
 * обработки событий мыши.
 *
 *
 */
public abstract class Painter implements View.OnTouchListener, Comparable<Painter> {



	/**
	 * Владелец объекта. Чаще всего его нет.
	 */
	protected Painter holder;

	/**
	 * Положение графического объекта на графической сцене. Не путать с
	 * положением физического объекта на физической сцене.
	 */
	protected Point pos;

	/**
	 * Линейные размеры графического объекта. Координаты точки центра будут:
	 * (pos.x + size.width / 2, pos.y + size.height / 2)
	 */
	protected Dimension size;

	/**
	 * Список зависящих объектов
	 */
	protected ArrayList<Painter> inside = new ArrayList<>();


	/**
	 * Ссылка на отрисоваемый физический объект.
	 */
	public final PhysObject object;

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
	 * ZIndex, который был выдан во время инициализации объекта
	 */
	protected int defaultZ;

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
		pos = new Point(ScalingUtil.scalingRealSizeX(obj.x - World.deviceX), ScalingUtil.scalingRealSizeY(obj.y - World.deviceY));
		int width =  ScalingUtil.scalingRealSizeX(obj.width);
		int height = ScalingUtil.scalingRealSizeY(obj.height);
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
		object = null;
		movable = false;
		setHolder(holder);
		if (holder != null) {
			pos = new Point(holder.getX(), holder.getY());
			size = new Dimension(holder.size.width, holder.size.height);
			center = new Point(pos.x + size.width / 2, pos.y + size.height / 2);
		}
		//DrawView.painters.add(this);
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
		setSize(ScalingUtil.scalingRealSizeX(object.width), ScalingUtil.scalingRealSizeY(object.height));
		setPos(ScalingUtil.scalingRealSizeX(object.x  - World.deviceX), ScalingUtil.scalingRealSizeY(object.y  - World.deviceY));
		for (Painter p : inside)
			p.updateSize();
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
	//TODO: WTF
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
		Logging.log("setZIndex",this,"old="+this.zIndex+", new = "+zIndex);
//		this.zIndex = zIndex;
		this.zIndex = zIndex;

		Collections.sort(DrawView.painters);

		//for (Painter p : inside)
		//	p.setZIndex(p.getZIndex() + delta);// или по какому другому алгоритму...
	}

	public int getX() {
		return pos.x;
	}

	public int getY() {
		return pos.y;
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(Point pos) {
		moveBy(pos.x - this.pos.x, pos.y - this.pos.y);
	}

	public void setPos(int x, int y) {
		moveBy(x - this.pos.x, y - this.pos.y);
	}

	public Dimension getSize() {
		return size;
	}

	public void setSize(int width, int height) {
		size.width = width;
		size.height = height;
		center.x = pos.x + size.width / 2;
		center.y = pos.y + size.height / 2;
		updatePoints();
	}

	//todo: BUG!!!!!
//	public void returnDefaultPosition() {
//		setZIndex(defaultZ);
//		moveBy(pos.x - ScalingUtil.scalingRealSizeX(object.defaultX), pos.y - ScalingUtil.scalingRealSizeY(object.defaultY));
//	}

	public void moveToDefault() {
		pos.x = ScalingUtil.scalingRealSizeX(object.defaultX - World.deviceX);
		pos.y = ScalingUtil.scalingRealSizeY(object.defaultY - World.deviceY);
		updatePoints();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: // нажатие
			Logging.log("ACTION_DOWN", this,"x = " + x + ", y = " + y);
			if (object.getParent() != null) {
				object.getParent().detach(object);
			}
			break;
		case MotionEvent.ACTION_MOVE: // движение
			moveBy(x - TouchListener.addX, y - TouchListener.addY);
			if (object != null) {
				object.x = pos.x / ScalingUtil.getGlobalScaleFactor();
				object.y = pos.y / ScalingUtil.getGlobalScaleFactor();
			}
			break;
		case MotionEvent.ACTION_UP: // отпускание
			Logging.log("ACTION_UP", this,"x = " + x + ", y = " + y);
		case MotionEvent.ACTION_CANCEL:

			for (IParent parent : Scene.parents) {
				if (parent.isInAria(x, y)) {
					((PhysObject)parent).attach(object);
					break;
				}
			}
			break;
		}
		return true;
	}

	public int compareTo(Painter other) {
		return zIndex - other.zIndex;
	}

	@Override
	public String toString() {
		return "{" + object + "}";
	}
}
