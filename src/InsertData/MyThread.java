package InsertData;

import java.io.IOException;

import application.LyricsToJson;

public class MyThread implements Runnable {

	private int from, to;
	private String fileName;
	private String sign;

	public MyThread(int from, int to, String fileName, String sign) {
		this.fileName = fileName;
		this.from = from;
		this.to = to;
		this.sign = sign;
	}

	public void run() {
		try {
			LyricsToJson.crawlAndSaveData(from, to, fileName + ".json", sign);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
