package yifan;

import static yifan.util.Property.*;

import java.io.IOException;

import yifan.doc.DocGraph;
import yifan.lda.LDA;
import yifan.util.Property;
import yifan.word.WordSplit;

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Property property = new Property();
		property.configure();
		
//		WordSplit split = new WordSplit();
//		split.loadStopWords(STOP_FILE);
//		split.dealArticles(RAW_FILE, FILE_TYPE);
//		split.outputVector(VECTOR_FILE);
		
		new WordSplit().run();
		new LDA().run();
		new DocGraph().run();
	}

}
