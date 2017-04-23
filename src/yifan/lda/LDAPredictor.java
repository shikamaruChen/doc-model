package yifan.lda;

import java.io.FileNotFoundException;
import java.io.IOException;

public class LDAPredictor {

	private Inferencer inferencer;

	//////����ģ���ļ���ַ��ʼ��
	public LDAPredictor(String dir, String modelName) {
		LDAOption option = new LDAOption();
		
		option.dir = dir;
		option.dtfile = modelName;
		option.inf = true;
		inferencer = new Inferencer();
		inferencer.init(option);
	}
	
	/////////�ƶ�������
	public Model inference(String data){
		String [] docs = new String[1];
		docs[0] = data;
		return inferencer.inference(docs);
	}

	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		
		LDAPredictor predictor = new LDAPredictor("d:/arec/model", "model-00590");
		
		String input = "���� ���� ���� ��� �佹 ��Ȧ ��ͷ";
		Model model = predictor.inference(input);
		
		double [] dist = model.theta[0];
		for (double d : dist) {
			System.out.print(d + " ");
		}
		
//		
//		LDAPredictor predictor2 = new LDAPredictor("D:/arec/ldaInferencer.model");
//		System.out.println("Inference:");
//		Model model = predictor2.inference("���� ���� ���� ��� �佹 ��Ȧ ��ͷ");
//		
//		double [] dist = model.theta[0];
//		Arrays.sort(dist);
//		for (double d : dist) {
//			System.out.println(d + " ");
//		}

	}
	
	
	
	
	
	
	
	
	
}
