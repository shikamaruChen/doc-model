package yifan.lda;
import static yifan.util.Property.*;

public class LDA implements Runnable{
	@Override
	public void run() {
		LDAOption option = new LDAOption();
		option.K = TOPIC_NUM;
		option.alpha = ALPHA;
		option.beta = BETA;
		option.dir = TOPIC_DIR;
		option.dfile = VECTOR_FILE;
		option.dtfile = DT_FILE;
		option.twfile = TW_FILE;
		option.est = true;
		option.inf = false;
		option.niters = NUM_ITER;
		option.savestep = SAVE_STEP;
		Estimator estimator = new Estimator();
		estimator.init(option);
		estimator.estimate();
	}
}
