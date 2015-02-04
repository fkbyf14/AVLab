package ru.barsic.avlab.basic;

import java.util.ArrayList;

import ru.barsic.avlab.graphics.Painter;
import ru.barsic.avlab.physics.Scene;

//import org.apache.log4j.Logger;

public abstract class PhysObject {

//	private static final Logger log = Logger.getLogger(PhysObject.class);

	//================= public instance field =================

	public double x, y; // координаты в физическом мире(см)
	public double defaultX, defaultY;
	public double width, height; // линейные размеры тела, можно и не указывать(см)
	public double mass;

	//================= private instance field =================

	protected Painter painter;
	private PhysObject parent;
	private ArrayList<PhysObject> children = new ArrayList<>();

	private double x1, y1, x2, y2;
	private double dt = 0.01;

	//================= public instance method =================

	public Painter getPainter() {
		return painter;
	}

	public PhysObject getParent() {
		return parent;
	}

	public ArrayList<PhysObject> getChildren() {
		return children;
	}


	protected PhysObject(double x, double y, double width, double height, double mass) {
		Scene.allocation(this);
		this.x = x;
		this.y = y;
		this.defaultX = x;
		this.defaultY = y;
		this.width = width;
		this.height = height;
		this.mass = mass;
	}

	/**
	 * Добавить дочерний объект.
	 *
	 * @param child Дочерний объект
	 * @return True если объект согласился усыновить переданный объект, иначе -
	 * False
	 */
	public boolean attach(PhysObject child) {
		if (child.equals(this))
			return false;
		children.add(child);
		child.parent = this;
		child.painter.setHolder(painter);
//		log.info(this + " attach " + child);
		return true;
	}

	/**
	 * Отбавить дочерний объект.
	 *
	 * @param child Дочерний объект
	 * @return True если объект позволил отнять его ребенка
	 */
	public boolean detach(PhysObject child) {
		children.remove(child);
		child.parent = null;
		child.painter.setHolder(null);
		return true;
	}

	public void setPos(double x, double y) {
		this.x = x;
		this.y = y;
		painter.setPos((int)(this.x), (int)(this.y));
	}

	@Override
	public String toString() {
		return "PhysObject={" + getClass().getSimpleName() + "," +
			"position=(" + x + "," + y + ")," +
			"parent=" + printParent() + "," +
			"children=" + printChildren() +
			"}";
	}

	private String printParent() {
		return parent == null ? null : parent.getClass().getSimpleName();
	}

	private String printChildren() {
		String result = "(";
		int i = 0;
		for (PhysObject child : children) {
			result += child.getClass().getSimpleName();
			if (i < children.size() - 1)
				result += ",";
			i++;
		}
		result += ")";
		return result;
	}
}
