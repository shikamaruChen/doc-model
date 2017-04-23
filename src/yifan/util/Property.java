package yifan.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Property {
	public static String DATA_DIR;
	public static String RES_DIR;
	public static String TOPIC_DIR;
	public static String DT_FILE;// doc-topic
	public static String TW_FILE;// topic-words
	public static String EDGE_FILE;
	public static String STOP_FILE;
	public static String RAW_FILE;
	public static String VECTOR_FILE;
	public static String GRAPH_FILE;
	public static String FILE_TYPE;
	public static int TOPIC_NUM;
	public static int NUM_ITER;
	public static int SAVE_STEP;
	public static double ALPHA;
	public static double BETA;
	public static double THRESHOLD;
	public static double MAX_P;
	public static double MIN_P;

	public void configure() throws IOException {
		String propertyName = "yifan/config.properties";
		Properties properties = new Properties();
		InputStream stream = getClass().getClassLoader().getResourceAsStream(
				propertyName);

		if (stream != null)
			properties.load(stream);
		else
			throw new FileNotFoundException("property file " + propertyName
					+ " not found in the classpath");
		FILE_TYPE = properties.getProperty("file.raw.type");
		DATA_DIR = properties.getProperty("dir.data");
		RES_DIR = properties.getProperty("dir.result");
		TOPIC_DIR = properties.getProperty("dir.topic");
		DT_FILE = RES_DIR + "/" + properties.getProperty("file.doc.topic");
		TW_FILE = RES_DIR + "/" + properties.getProperty("file.topic.word");
		EDGE_FILE = RES_DIR + "/" + properties.getProperty("file.edge");
		STOP_FILE = DATA_DIR + "/" + properties.getProperty("file.stopword");
		RAW_FILE = DATA_DIR + "/" + properties.getProperty("file.raw.doc");
		VECTOR_FILE = RES_DIR + "/" + properties.getProperty("file.vector");
		GRAPH_FILE = RES_DIR + "/" + properties.getProperty("file.doc.graph");
		TOPIC_NUM = Integer.parseInt(properties.getProperty("lda.topic.num"));
		NUM_ITER = Integer.parseInt(properties.getProperty("lda.iter.num"));
		SAVE_STEP = Integer.parseInt(properties.getProperty("lda.save.step"));
		ALPHA = Double.parseDouble(properties.getProperty("lda.alpha"));
		BETA = Double.parseDouble(properties.getProperty("lda.beta"));
		THRESHOLD = Double.parseDouble(properties
				.getProperty("split.sim.threshold"));
		MAX_P = Double.parseDouble(properties
				.getProperty("graph.max.probability"));
		MIN_P = Double.parseDouble(properties
				.getProperty("graph.min.probability"));
	}
}
