package ubicomp.soberdiary.test.camera;

import android.graphics.Point;
import android.widget.FrameLayout;
import ubicomp.soberdiary.test.Tester;

public interface CameraCaller extends Tester{
	public void stopByFail(int fail);
	public FrameLayout getPreviewFrameLayout();
	public Point getPreviewSize();
}
