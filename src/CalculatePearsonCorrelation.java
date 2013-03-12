import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author siddharthdalya
 *
 */

public class CalculatePearsonCorrelation {

	/**
	 * This function returns the list of top k Pearson Correlation
	 * @param userid - input user id
	 * @param kValue - value of k
	 * @return
	 */
	public static final List<UserSimilarityObject> calculatePearsonCorrelation(Matrix inputMatrix, int userid, int inputMovieid,int kValue) {
		Double inputUserAvgRating = inputMatrix.calculateAverageRatingForUser(userid, inputMovieid);
		List<UserSimilarityObject> kSimilarUserList = new ArrayList<UserSimilarityObject>();

		/* Initialize a list of k elements */
		for (int i = 0; i < kValue; i++) {
			UserSimilarityObject temp = new UserSimilarityObject(0,0);
			kSimilarUserList.add(i, temp);
		}
		
		try {
			/* Read test file to generate a movie list and rating list for the given user */
			MovieList mlist = inputMatrix.loadMovieAndRatingData(userid);
			List<Integer> movieList = mlist.getMovieList();
			List<Double> ratingList = mlist.getRatingList();
			/* Calculate Similarity */
			for (int rowid = 0; rowid < inputMatrix.rows; rowid++) {
				// Use movie list size as column
				Double numerator  = 0.0;
				Double denom1 = 0.0;
				Double denom2 = 0.0;
				int commonMovieCount = 0;
				for (int columnid = 0; columnid < movieList.size(); columnid++) {
					int movieid = movieList.get(columnid);
					/* Get movie rating from training data and test data */
					double inputUserMovieRating = ratingList.get(columnid);
					double trainingUserMovieRating = inputMatrix.matrix[rowid][movieid-1];
					
					if (inputUserMovieRating != 0 && trainingUserMovieRating != 0) {
						/*Get average rating for training user from hash map*/
							
						Double trainingUserAvgRating = inputMatrix.trainingAverageRatingMap.get(rowid+1);		
						numerator = numerator + ((inputUserMovieRating - inputUserAvgRating)*(trainingUserMovieRating-trainingUserAvgRating));
						denom1 = denom1 + Math.pow((inputUserMovieRating - inputUserAvgRating),2);
						denom2 = denom2 + Math.pow((trainingUserMovieRating-trainingUserAvgRating), 2);
						commonMovieCount++;
						}	
					}
				
				
				/* Take denominator square root only if positive and greater than zero */
				if (denom1 > 0.0 && denom2 > 0.0) {
					double denom = (Math.sqrt(denom1)*Math.sqrt(denom2));
					if (denom > 0.0) {
						double similarity = numerator/ denom;
						similarity = (commonMovieCount / (commonMovieCount + 2.0)) * similarity;
						/* Add actual user id by adding 1 to rowid */
						UserSimilarityObject obj = new UserSimilarityObject(rowid+1, similarity);
						UserSimilarityObject temp = kSimilarUserList.get(0);
						if (Math.abs(temp.similarity) < Math.abs(obj.similarity)) {
							kSimilarUserList.remove(0);
							kSimilarUserList.add(0, obj);
						}
						
						else if (Math.abs(temp.similarity) == Math.abs(obj.similarity)) {
							kSimilarUserList.add(obj);
						}
						Collections.sort(kSimilarUserList, new AbsoluteSimililarityComparator());
					}	
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("Size:"+kSimilarUserList.size());
		return kSimilarUserList;
	}
	
	
	
	/**
	 * This function calculates movie rating for a input movie.
	 * 
	 * @param userid
	 * @param inputMovieid
	 * @param kValue
	 * @return
	 */
	public static final double calculateMovieRating(Matrix inputMatrix, List<UserSimilarityObject> inputSimilarityObjList, int userid, int inputMovieid, int kValue) {
		List<UserSimilarityObject> kSimilarityObjList = new ArrayList<UserSimilarityObject>();
		/* Remove users with object id = 0 */
		for (int i = 0; i < inputSimilarityObjList.size(); i++) {
			if (inputSimilarityObjList.get(i).trainingUserid != 0 && Math.abs(inputSimilarityObjList.get(i).similarity) > 0.0) {
				kSimilarityObjList.add(inputSimilarityObjList.get(i));
			}
		}

		/* Calculate average rating for the given user */
		Double avgRatingForInputUser = inputMatrix.calculateAverageRatingForUser(userid,inputMovieid);
		Double finalRating = 0.0;
		Double numerator = 0.0;
		Double denominator = 0.0;
		Double meanMovieRating = inputMatrix.meanMovieRatingMap.get(inputMovieid);		
		
		for (int i = 0; i < kSimilarityObjList.size(); i++) {
			/* Get similar object from list of k similar objects */
			UserSimilarityObject similarObject = kSimilarityObjList.get(i);
			
			/* Retrieve user id of similar user from k similar user list */
			int trainingUserid = similarObject.getTrainingUserid();
			Double avgRating = inputMatrix.trainingAverageRatingMap.get(trainingUserid);
			if (inputMatrix.matrix[trainingUserid - 1][inputMovieid - 1] > 0) {
				double weight = inputMatrix.matrix[trainingUserid - 1][inputMovieid - 1] - avgRating;
				numerator = numerator + ((similarObject.similarity) * (weight));
				denominator = denominator + Math.abs(similarObject.similarity);
			}
		}

		if (denominator > 0.0) {
			finalRating = avgRatingForInputUser + (numerator / denominator);		
		} else if (meanMovieRating > 0 && meanMovieRating <= 5) {
			finalRating = meanMovieRating;
		} else {
			finalRating = avgRatingForInputUser;
		}
		
		if ((int) Math.rint(finalRating) >= 5) {
			finalRating = 5.0;
		} else if ((int) Math.rint(finalRating) <= 0.0) {
			finalRating = 1.0;
		}
		
		if (finalRating > 5.0 || finalRating <= 0.0) {
			System.out.println("Error Rating:"+finalRating);
			System.exit(0);
		}
		
		//return (int) Math.rint(finalRating);
		return finalRating;
	}
	
}
