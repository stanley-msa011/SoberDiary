package ubicomp.soberdiary.test.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import ubicomp.soberdiary.main.R;
import ubicomp.soberdiary.main.ui.toast.CustomToast;
import ubicomp.soberdiary.system.config.PreferenceControl;
import android.content.Context;

public class BracDataHandlerDebugModeNormal extends BracDataHandler {

	public BracDataHandlerDebugModeNormal(Context context, long timestamp) {
		super(timestamp);
	}

	@Override
	public int start() {

		PreferenceControl.setUpdateDetection(false);
		PreferenceControl.setUpdateDetectionTimestamp(0);
		CustomToast.generateToast(R.string.after_test_pass, 0);
		PreferenceControl.setDebugDetectionTimestamp(ts);

		return SUCCESS;
	}

	@Override
	protected double parseTextFile(File textFile) {
		double median = 0;
		try {
			@SuppressWarnings("resource")
			Scanner s = new Scanner(textFile);
			int index = 0;
			List<Double> valueArray = new ArrayList<Double>();
			while (s.hasNext()) {
				index++;
				String word = s.next();
				if (index % 4 == 3)
					valueArray.add(Double.valueOf(word));
			}
			if (valueArray.size() == 0)
				return ERROR;
			Double[] values = valueArray.toArray(new Double[valueArray.size()]);
			Arrays.sort(values);
			median = values[(values.length - 1) / 2];

		} catch (FileNotFoundException e1) {
			return ERROR;
		}
		return median;
	}

}
