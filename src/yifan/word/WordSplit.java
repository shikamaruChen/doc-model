package yifan.word;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.NlpAnalysis;

import yifan.util.IOUtils;
import static yifan.util.Property.*;

public class WordSplit {
	private Map<Integer, WordVector> wordVectors = new HashMap<>();
	private Set<String> stopwords = new HashSet<>();

	public void run() throws IOException {
		loadStopWords(STOP_FILE);
		dealArticles(RAW_FILE, FILE_TYPE);
		outputVector(VECTOR_FILE);
		similarity(EDGE_FILE, THRESHOLD);
	}

	// 方法：分词，此处用到了ansj的nlp分词，精度高，能识别新词，但很耗时
	private String splitWord(String aString) {
		List<Term> list = NlpAnalysis.parse(aString);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			builder.append(list.get(i).getName() + " ");
		}
		return builder.toString();
	}

	private ArrayList<String> GetLineDocs(String file) throws IOException {
		ArrayList<String> strings = new ArrayList<>();
		BufferedReader reader = IOUtils.bufferReader(file);
		String line;
		while ((line = reader.readLine()) != null) {
			line = splitWord(line); // 分词
			strings.add(line);
		}
		reader.close();
		return strings;
	}

	// 方法：处理json文本，提取文章到一个list中
	private ArrayList<String> GetJsonDocs(String file) throws IOException {
		ArrayList<String> strings = new ArrayList<>();
		BufferedReader reader = IOUtils.bufferReader(file);
		String b = reader.readLine();

		// 替换文中的格式符号
		b = b.substring(b.indexOf('{'), b.lastIndexOf(']'));
		b = b.replace('{', '\0');
		b = b.replace('}', '\n');
		b = b.replace("\"title\"", "");
		b = b.replace("\"content\"", "");
		b = b.replace("\"topicWords\"", "");
		b = b.replace("\\r", "");
		b = b.replace("\\n", "");

		// 向list中写入
		InputStream stream = new ByteArrayInputStream(b.getBytes());
		InputStreamReader reader11 = new InputStreamReader(stream);
		BufferedReader bufferedReader = new BufferedReader(reader11);
		String bsString;
		int i = 0;
		// while ((bsString = reader.readLine()) != null) {
		while ((bsString = bufferedReader.readLine()) != null) {
			// bsString = splitWord(bsString); // 分词
			strings.add(bsString);
		}
		return strings;
	}

	public void getTopic(String raw, String out, int n, String... topics)
			throws IOException {
		BufferedWriter writer = IOUtils.bufferWriter(out);
		ArrayList<String> docs = GetJsonDocs(raw);
		Collections.shuffle(docs);
		for (String doc : docs) {
			boolean contain = true;
			for (String topic : topics)
				contain = contain && doc.contains(topic);
			if (contain) {
				writer.write(doc);
				writer.newLine();
				n--;
			}
			if (n <= 0)
				break;
		}
		writer.close();
	}

	// 方法：处理文章，过滤停用词，生成向量
	public void dealArticles(String file, String type) throws IOException {
		IOUtils.console("word split begin:");
		ArrayList<String> lStrings = null;
		if (type.equals("json"))
			lStrings = GetJsonDocs(file);
		else if (type.equals("plain"))
			lStrings = GetLineDocs(file);

		for (int i = 0; i < lStrings.size(); i++) {
			WordVector vector = new WordVector();
			String string = lStrings.get(i);
			String[] ssString = string.split(" ");
			for (int j = 0; j < ssString.length; j++) {
				if (!stopwords.contains(ssString[j].trim())) {
					vector.addWord(ssString[j].trim());
				}
			}
			vector.normalize();
			wordVectors.put(i, vector);
		}
		IOUtils.console("word split finished");
	}

	// 方法：加载停用词文件
	public void loadStopWords(String file) throws IOException {
		IOUtils.console("stop words load finished");
		BufferedReader bufferedReader = IOUtils.bufferReader(file);
		String b;
		while ((b = bufferedReader.readLine()) != null) {
			stopwords.add(b);
		}
	}

	public void similarity(String file, double threshold) throws IOException {
		IOUtils.console("calculate document similarities begins");
		PrintWriter writer = IOUtils.printWriter(file);
		for (Map.Entry<Integer, WordVector> aEntry : wordVectors.entrySet()) {
			Map<Integer, Double> all_distance = new HashMap<>();
			for (Map.Entry<Integer, WordVector> bEntry : wordVectors.entrySet()) {
				all_distance.put(bEntry.getKey(), aEntry.getValue()
						.cosineDistance(bEntry.getValue()));
			}
			List<Map.Entry<Integer, Double>> list = sort_by_value(all_distance);
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < list.size(); i++) {
				double sim = list.get(i).getValue();
				if (sim < threshold)
					break;
				int nid = list.get(i).getKey();
				builder.append(nid + "," + sim + " ");
			}
			writer.println("id:" + aEntry.getKey() + " " + builder.toString());
		}
		writer.close();
		IOUtils.console("calculate document similarities finished");
		IOUtils.console("output similarities to file " + file);
	}

	// 方法：对每个向量给出相关度最大的前n名,并输出到文件
	public void similarity(int n) throws IOException {
		PrintWriter writer = IOUtils.printWriter(EDGE_FILE);

		for (Map.Entry<Integer, WordVector> aEntry : wordVectors.entrySet()) {
			Map<Integer, Double> all_distance = new HashMap<>();
			for (Map.Entry<Integer, WordVector> bEntry : wordVectors.entrySet()) {
				all_distance.put(bEntry.getKey(), aEntry.getValue()
						.cosineDistance(bEntry.getValue()));
			}
			List<Map.Entry<Integer, Double>> list = sort_by_value(all_distance);
			StringBuilder builder = new StringBuilder();
			int m = (n > all_distance.size() ? all_distance.size() : n);
			for (int i = 0; i < m; i++) {
				builder.append("(" + list.get(i).getKey() + ","
						+ list.get(i).getValue() + ")" + " ");
			}
			writer.println("id: " + aEntry.getKey() + " " + builder.toString());
		}
		writer.close();
	}

	// 方法：hashmap按值排序
	public List<Map.Entry<Integer, Double>> sort_by_value(
			Map<Integer, Double> aMap) {
		List<Map.Entry<Integer, Double>> list = new ArrayList<Map.Entry<Integer, Double>>(
				aMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
			public int compare(Map.Entry<Integer, Double> o1,
					Map.Entry<Integer, Double> o2) {
				// if ((1000 * o2.getValue() - 1000 * o1.getValue()) >= 0)
				// return 1;
				// else
				// return -1;
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		return list;
	}

	// 方法： 输出向量到文件
	public void outputVector(String file) throws IOException {
		IOUtils.console("output vectors to file " + file);
		PrintWriter printWriter = IOUtils.printWriter(file);
		printWriter.println(wordVectors.size());
		for (Map.Entry<Integer, WordVector> a : wordVectors.entrySet()) {
			printWriter.println(a.getValue());
		}
		printWriter.close();
	}

	public static void main(String[] args) throws IOException {
		WordSplit word = new WordSplit();
		word.getTopic("data/test.json", "topics", 2, "习近平", "访美", "投资", "股票");
	}
}
