/**
 * 
 * @author siddharthdalya
 *
 */
public class UserSimilarityObject {
	double similarity;
	int trainingUserid;
	
	public int getTrainingUserid() {
		return trainingUserid;
	}

	public void setTrainingUserid(int trainingUserid) {
		this.trainingUserid = trainingUserid;
	}

	public UserSimilarityObject(int userid, double similarityMeasure) {
		trainingUserid = userid;
		similarity = similarityMeasure;
	}
}
