package ubicomp.soberdiary.test.camera;

import java.util.Iterator;
import java.util.List;

import ubicomp.soberdiary.main.App;
import ubicomp.soberdiary.test.Tester;
import ubicomp.soberdiary.test.data.ImageFileHandler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.ErrorCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;

@SuppressLint("InlinedApi")
public class CameraRecorder {

	private CameraCaller cameraCaller;
	private Context context;
	private Camera camera;

	private Camera.PictureCallback pictureCallback;
	private ImageFileHandler imgFileHandler;

	private PreviewWindow preview;
	private FrameLayout previewFrame = null;
	private SurfaceHolder previewHolder;

	public int picture_count = 0;

	private static final String TAG = "CAM_RECORDER";

	public CameraRecorder(CameraCaller cameraCaller, ImageFileHandler imgFileHandler) {
		this.cameraCaller = cameraCaller;
		this.context = App.getContext();
		this.imgFileHandler = imgFileHandler;
		imgFileHandler.setRecorder(this);
		pictureCallback = new PictureCallback();
	}

	public void init() {
		picture_count = 0;
		int camera_count = 1;
		camera_count = Camera.getNumberOfCameras();
		if (camera_count > 1)
			camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		else
			camera = Camera.open();

		camera.setDisplayOrientation(90);
		Parameters params = camera.getParameters();
		List<Size> list = params.getSupportedPictureSizes();
		Point bestSize = getBestSize(list);
		params.setPictureSize(bestSize.x, bestSize.y);
		try {
			camera.setParameters(params);
		} catch (Exception e) {
			Log.d(TAG, "setParamError " + e.toString());
		}

		camera.setErrorCallback(new ErrorCallback() {
			@Override
			public void onError(int error, Camera cam) {
				Log.d(TAG, "ERROR_UNKNOWN " + (error == Camera.CAMERA_ERROR_UNKNOWN));
				Log.d(TAG, "SERVER_DIED " + (error == Camera.CAMERA_ERROR_SERVER_DIED));
				Log.d(TAG, "ERROR NUM=" + error);
			}
		});

		setSurfaceCallback();
	}

	protected Point getBestSize(List<Size> list) {
		int bestWidth = Integer.MAX_VALUE;
		int bestHeight = Integer.MAX_VALUE;
		if (list.size() > 0) {
			Iterator<Camera.Size> iter = list.iterator();
			while (iter.hasNext()) {
				Camera.Size cur = iter.next();
				if (cur.width < bestWidth && cur.height < bestHeight) {
					bestWidth = cur.width;
					bestHeight = cur.height;
				}
			}
		}
		return new Point(bestWidth, bestHeight);
	}

	public void start() {
		if (preview != null)
			preview.setVisibility(View.VISIBLE);
	}

	@SuppressWarnings("deprecation")
	private void setSurfaceCallback() {
		previewFrame = null;
		previewFrame = cameraCaller.getPreviewFrameLayout();
		if (previewFrame != null) {
			preview = new PreviewWindow(context, this);
			previewHolder = preview.getHolder();
			if (Build.VERSION.SDK_INT < 11)
				previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			previewHolder.addCallback(preview);
			Point size = cameraCaller.getPreviewSize();
			size.x -= 10;
			size.y -= 10;
			previewFrame.addView(preview, size.x, size.y);
			preview.setVisibility(View.INVISIBLE);
		}
	}

	public void takePicture() {
		if (camera != null)
			camera.takePicture(null, new RawPictureCallback(), pictureCallback);
	}

	public void close() {
		if (preview != null)
			preview.setVisibility(View.INVISIBLE);
		if (previewFrame != null)
			previewFrame.removeView(preview);
		if (camera != null) {
			Camera tmp = camera;
			camera = null;
			tmp.stopPreview();
			tmp.release();
			tmp = null;
		}

	}

	public class RawPictureCallback implements Camera.PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
		}
	}

	public class PictureCallback implements Camera.PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			picture_count++;

			Message msg = new Message();
			Bundle data_b = new Bundle();
			data_b.putByteArray("Img", data);
			msg.setData(data_b);
			msg.what = picture_count;
			imgFileHandler.sendMessage(msg);
			preview.restartPreview();
		}
	}

	public void closeSuccess() {
		close();
		cameraCaller.updateDoneState(Tester._CAMERA);
	}

	public void closeFail(int type) {
		close();
		cameraCaller.stopByFail(type);
	}

	public Camera getCamera() {
		return camera;
	}
}
