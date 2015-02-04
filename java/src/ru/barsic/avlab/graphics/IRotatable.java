package ru.barsic.avlab.graphics;

public interface IRotatable {

	/**
	 * @return значение угла, на который повёрнут объект
	 */
	public double getAngle();

	/**
	 * Поворачивает объект на {@code angle} радиан относительно точки (x, y).
	 *
	 * @param cx    - X координата точки поворота
	 * @param cy    - Y координата точки поворота
	 * @param angle - значение угла поворота в радианах
	 */
	public void rotate(double cx, double cy, double angle);
}
