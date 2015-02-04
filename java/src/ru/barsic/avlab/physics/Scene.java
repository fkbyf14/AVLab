package ru.barsic.avlab.physics;

import java.util.ArrayList;

import ru.barsic.avlab.basic.PhysObject;

public class Scene {

	public static ArrayList<PhysObject> objects = new ArrayList<>();
	public static ArrayList<IParent> parents = new ArrayList<>();

	/**
	 * При создании интерфейса добавляется дополнительное условие, которое
	 * определяет, где будет храниться ссылка на объект.
	 */
	public static void allocation(PhysObject obj) {
		objects.add(obj);
		if (obj instanceof IParent) {
			parents.add((IParent)obj);
		}
	/*	if (obj instanceof IDiver) {
			Molecular.divers.add((IDiver)obj);
		}
		if (obj instanceof IContainer) {
			Molecular.containers.add((IContainer)obj);
		}*/
		/*if (obj instanceof SequentialDevice) {
			Scheme.getDevices().add((SequentialDevice)obj);
		}
		if (obj instanceof Connect) {
			Scheme.getConnects().add((Connect)obj);
		}*/
	}


}