/**
 * 
 * @author siddharthdalya
 *
 */
public class MovieSimilarityObject {

	int movieid;
	double similarity;
	
	public int getMovieid() {
		return movieid;
	}

	public double getSimilarity() {
		return similarity;
	}
	
	public MovieSimilarityObject(int inputMovieid, double inputSimilarity) {
		movieid = inputMovieid;
		similarity = inputSimilarity;
	}
}
