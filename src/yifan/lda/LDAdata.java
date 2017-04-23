package yifan.lda;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import yifan.util.IOUtils;

public class LDAdata {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String in = "output1444723478911.txt";
		String out = "output";

		BufferedReader reader = IOUtils.bufferReader(in);
		BufferedWriter writer = IOUtils.bufferWriter(out);
		String line;
		writer.write("10001");
		writer.newLine();
		while ((line = reader.readLine()) != null) {
			String[] terms = line.split(" ");
			String words = "";
			for (int i = 2; i < terms.length; i += 2) {
				if (terms[i].equals("¡¾") || terms[i].equals("¡¿") || terms[i].matches(".*[\\w].*"))
					continue;
				words += terms[i] + " ";
			}
			writer.write(words);
			writer.newLine();
		}
		reader.close();
		writer.close();
	}

}
