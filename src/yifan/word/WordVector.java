package yifan.word;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class WordVector implements Serializable {

	private static final long serialVersionUID = 8775485200501885686L;

	public static class Weight implements Serializable {
		private static final long serialVersionUID = 5457651299326361360L;
		public double weight = 0;

		public Weight(double weight) {
			super();
			this.weight = weight;
		}

		public void multiply(double multiple) {
			weight *= multiple;
		}

		public void add(double add) {
			weight += add;
		}
	}

	boolean normalized = false;
	Map<String, Weight> words = new HashMap<String, Weight>();

	public void addWord(String word) {
		Weight w = words.get(word);
		if (w == null) {
			w = new Weight(0);
			words.put(word, w);
		}
		w.weight++;
	}

	public void addWord(String word, double weight) {
		Weight w = words.get(word);
		if (w == null)
			words.put(word, new Weight(weight));
		else {
			w.weight += weight;
		}
	}

	public void normalize() {
		if (normalized)
			return;
		double denominator = 0;
		for (Weight w : words.values()) {
			denominator += w.weight * w.weight;
		}
		denominator = (float) Math.sqrt((double) denominator);
		for (Weight w : words.values()) {
			w.weight /= denominator;
		}
		normalized = true;
	}

	public int size() {
		return words.size();
	}

	public double cosineDistance(WordVector v2) {
		WordVector v1 = this;
		v1.normalize();
		v2.normalize();
		WordVector toIter, toFind;
		if (v1.size() < v2.size()) {
			toIter = v1;
			toFind = v2;
		} else {
			toIter = v2;
			toFind = v1;
		}
		double r = 0;
		for (Entry<String, Weight> entry : toIter.words.entrySet()) {
			Weight w = toFind.words.get(entry.getKey());
			if (w == null)
				continue;
			r += w.weight * entry.getValue().weight;
		}
		return r;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (Map.Entry<String, WordVector.Weight> aEntry : words.entrySet()) {
			String term = aEntry.getKey();
			if (term.matches(".*[\\w].*") || term.length() == 1)
				continue;
			result.append(term + " ");
		}
		return result.toString();
	}

	public Map<String, Weight> getWords() {
		return words;
	}

}
