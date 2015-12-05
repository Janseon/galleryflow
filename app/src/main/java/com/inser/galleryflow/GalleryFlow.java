package com.inser.galleryflow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

@SuppressLint("NewApi")
public class GalleryFlow extends Gallery {

	private Camera mCamera = new Camera();

	private float unselectedScale;
	private int mCoveflowCenter;

	public GalleryFlow(Context context) {
		super(context);
		init(context);
	}

	public GalleryFlow(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public GalleryFlow(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public void init(Context context) {
		// setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		setStaticTransformationsEnabled(true);
		unselectedScale = 1f;
	}

	public float getUnselectedScale() {
		return unselectedScale;
	}

	public void setUnselectedScale(float unselectedScale) {
		this.unselectedScale = unselectedScale;
	}

	private int getCenterOfCoverflow() {
		return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
	}

	private static int getCenterOfView(View view) {
		return view.getLeft() + view.getWidth() / 2;
	}

	@Override
	protected boolean getChildStaticTransformation(View child, Transformation t) {
		if (android.os.Build.VERSION.SDK_INT >= 16) {
			child.invalidate();
		}
		final int coverFlowWidth = getWidth();
		final int childCenter = getCenterOfView(child);
		final int childWidth = child.getWidth();

		final int actionDistance = (int) ((coverFlowWidth + childWidth) / 2.0f);

		final float effectsAmount = Math.min(1.0f, Math.max(-1.0f, (1.0f / actionDistance) * (childCenter - mCoveflowCenter)));

		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);
		transformImageBitmap(child, t, effectsAmount);
		return true;
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		mCoveflowCenter = getCenterOfCoverflow();
		super.onSizeChanged(w, h, oldw, oldh);
	}

	private void transformImageBitmap(View child, Transformation t, float effectsAmount) {
		// Log.i("transformImageBitmap", "effectsAmount=" + effectsAmount);
		mCamera.save();

		final Matrix imageMatrix = t.getMatrix();
		mCamera.getMatrix(imageMatrix);

		int childWidth = child.getWidth();

		final float zoomAmount = (unselectedScale - 1) * Math.abs(effectsAmount) + 1;

		final float translateX = childWidth / 2.0f;
		final float translateY = child.getHeight() / 2;
		imageMatrix.preTranslate(-translateX, -translateY);
		float zoomAmountX = (float) Math.sqrt(zoomAmount);
		// if (zoomAmountX > 1) {
		// zoomAmountX = 1;
		// }
		imageMatrix.postScale(zoomAmountX, zoomAmount);
		imageMatrix.postTranslate(translateX, translateY);

		mCamera.restore();
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
	}

	private static final float SCROLL_RATIO = 1;// 阻尼系数
	private static final int MAX_Y_OVERSCROLL_DISTANCE = 50;

	@Override
	protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX,
			int maxOverScrollY, boolean isTouchEvent) {
		int newDeltaY = deltaY;
		int delta = (int) (deltaY * SCROLL_RATIO);
		if (delta != 0)
			newDeltaY = delta;
		return super.overScrollBy(deltaX, newDeltaY, scrollX, scrollY, scrollRangeX, scrollRangeY, maxOverScrollX, MAX_Y_OVERSCROLL_DISTANCE, isTouchEvent);
	}
}
