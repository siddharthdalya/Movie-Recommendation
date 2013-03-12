/**
 * This class stores user id with its average rating
 * @author siddharthdalya
 *
 */
public class UserAverageRatingObject {
	double averageRating;
	int userId;
	public double getAverageRating() {
		return averageRating;
	}
	public void setAverageRating(int averageRating) {
		this.averageRating = averageRating;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public UserAverageRatingObject(int inputUserid, double inputAverageRating) {
		userId = inputUserid;
		averageRating = inputAverageRating;
	}
}
