package ru.barsic.avlab.graphics;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.*;
import android.util.DisplayMetrics;
import android.view.*;
import ru.barsic.avlab.basic.TouchListener;
import ru.barsic.avlab.basic.World;
import ru.barsic.avlab.helper.ScalingUtil;

public class DrawView extends SurfaceView implements SurfaceHolder.Callback {

	//======================== public static fields ========================

	public static Point position = new Point(0, 0);
	public static int width;
	public static int height;
	public static int maxZIndex = 100000;
	public static int currentZIndex = 0;
	public static ArrayList<Painter> painters = new ArrayList<>();
	public static Timer timer = new Timer("calculation timer");

	//======================== private static fields ========================

	private static final int PAINTING_DELAY = 60;

	private static DrawView drawViewInstance;

	private static final Comparator<Painter> Z_COMPARATOR = new Comparator<Painter>() {
		@Override
		public int compare(Painter v1, Painter v2) {
			return v1.getZIndex() < v2.getZIndex() ? -1 : 1;
		}
	};

	//======================== private instance fields ========================

	DrawRunnable drawRunnable;
	final TouchListener touchListener;

	//======================== public static methods ========================

	public static DrawView createInstance(Context context) {
		if (drawViewInstance != null)
			return drawViewInstance;
		drawViewInstance = new DrawView(context);

		return drawViewInstance;
	}

	public static DrawView getInstance() {
		if (drawViewInstance == null)
			throw new IllegalStateException("Instance is not created");
		return drawViewInstance;
	}

	public static void sortByZ() {
		Collections.sort(painters, Z_COMPARATOR);
	}

	public static void returnPrimaryPosition() {
		for (Painter vi : painters) {
			if (vi.getHolder() == null) {
				vi.moveBy(-position.x, -position.y);
			}
		}
		position.x = 0;
		position.y = 0;
	}

	public static void moveVisibleArea(int dx, int dy) {
		position.x += dx;
		position.y += dy;
		World.deviceY -= dy / ScalingUtil.pixToSmY;
		World.deviceX -= dx / ScalingUtil.pixToSmX;
		System.out.println("WORLD x:" + World.deviceX + ", y:" + World.deviceY);
		for (Painter vi : painters) {
			if (vi.getHolder() == null) {
				vi.moveBy(dx, dy);
			}
		}
	}

	//======================== public instance methods  ========================

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		drawRunnable = new DrawRunnable(getHolder());
		Thread thread = new Thread();
		drawRunnable.setRunning(true);
		Executor executor = Executors.newSingleThreadExecutor();
		executor.execute(drawRunnable);
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		boolean retry = true;
		drawRunnable.setRunning(false);
		while (retry) {
			try {
				System.out.println("98597345897345897348957348957348957");
				//drawRunnable.join();
				//todo: разобраться!
				retry = false;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//======================== private implementation ========================

	private DrawView(Context context) {
		super(context);
		getHolder().addCallback(this);
		DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
		ScalingUtil.pixToSmX = displaymetrics.xdpi / ScalingUtil.INCH_TO_SM;
		ScalingUtil.pixToSmY = displaymetrics.ydpi / ScalingUtil.INCH_TO_SM;
		this.touchListener = new TouchListener(displaymetrics.heightPixels, displaymetrics.widthPixels);
		//this.setBackgroundColor(Color.WHITE);
		this.setOnTouchListener(touchListener);
	}

	//======================== DrawThread ========================

	class DrawRunnable implements Runnable {

		private volatile boolean running = false;
		private final SurfaceHolder surfaceHolder;

		public DrawRunnable(SurfaceHolder surfaceHolder) {
			this.surfaceHolder = surfaceHolder;
		}

		public void setRunning(boolean running) {
			this.running = running;
		}

		@Override
		public void run() {
			Canvas canvas;
			while (running) {
				canvas = null;
				try {
					canvas = surfaceHolder.lockCanvas(null);
					if (canvas == null)
						continue;

					canvas.drawColor(Color.GREEN);
					Paint paint = new Paint();
					paint.setColor(Color.BLACK);
					paint.setStyle(Paint.Style.STROKE);
					canvas.drawRect(3 , 3, touchListener.screenWidth - 3, touchListener.screenHeight - 3, paint);
					for (Painter painter : painters)
						painter.onDraw(canvas);
				} finally {
					if (canvas != null)
						surfaceHolder.unlockCanvasAndPost(canvas);
				}
				try {
					Thread.sleep(PAINTING_DELAY);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
