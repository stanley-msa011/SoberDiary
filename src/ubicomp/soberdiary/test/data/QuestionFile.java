package ubicomp.soberdiary.test.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class QuestionFile {

	private File file;
	private BufferedWriter writer;
	private File directory;

	public QuestionFile(File directory) {
		this.directory = directory;
	}

	public void write(int emotion, int craving) {
		String a = emotion + "\t" + craving;
		file = new File(directory, "question.txt");
		try {
			writer = new BufferedWriter(new FileWriter(file));
		} catch (IOException e) {
			writer = null;
		}
		try {
			writer.write(a);
		} catch (IOException e) {
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
			}
		}
	}

}
