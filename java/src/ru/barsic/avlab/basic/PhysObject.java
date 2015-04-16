package ru.barsic.avlab.basic;

import ru.barsic.avlab.graphics.Painter;
import ru.barsic.avlab.helper.Logging;
import ru.barsic.avlab.helper.ScalingUtil;
import ru.barsic.avlab.physics.Scene;

import java.util.ArrayList;

public abstract class PhysObject {

	//================= public instance field =================

	public double x, y; // координаты в физическом мире(см)
	public double defaultX, defaultY;
	public double width, height; // линейные размеры тела, можно и не указывать(см)
	public double mass;

	//================= private instance field =================

	protected Painter painter;
	private PhysObject parent;
	private ArrayList<PhysObject> children = new ArrayList<>();

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

	@Override
	public String toString() {
		return "{" + getClass().getSimpleName() + "," +
				"position=(" + ScalingUtil.scalingRealSizeToX(x) + "," + ScalingUtil.scalingRealSizeToY(y) + ")," +
				"par=" + printParent() + "," +
				"kids=" + printChildren() +
				"}";
	}
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


	/**
	 * Добавить дочерний объект.
	 *
	 * @param child Дочерний объект
	 * @return True если объект согласился усыновить переданный объект, иначе -
	 * False
	 */
	public boolean attach(PhysObject child) {
		Logging.log("attach", this, "attach child = " + child);
		if (child.equals(this))
			return false;
		children.add(child);
		child.parent = this;
		return true;
	}

	/**
	 * Отбавить дочерний объект.
	 *
	 * @param child Дочерний объект
	 * @return True если объект позволил отнять его ребенка
	 */
	public boolean detach(PhysObject child) {
		Logging.log("detach", this, "detach child = " + child);
		children.remove(child);
		child.parent = null;
		return true;
	}

	public void updatePos() {
		if (painter != null) {
			this.x = ScalingUtil.scalingXToRealSize(painter.getPos().x) + World.deviceX;
			this.y = ScalingUtil.scalingYToRealSize(painter.getPos().y) + World.deviceY;
		}
	}

	public void moveToDefault() {
		this.x = defaultX;
		this.y = defaultY;
		painter.updatePos();
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
