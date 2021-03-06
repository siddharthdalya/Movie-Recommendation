import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author siddharthdalya
 *
 */
public class CalculateCustom {
	/**
	 * This function returns the list of top k Cosine Similarity. 
	 * @param userid - input user id
	 * @param kValue - value of k
	 * @return
	 */
	public static final List<UserSimilarityObject> calculateCustomSimilarity(Matrix inputMatrix, int userid, int inputMovieid,int kValue) {
		List<UserSimilarityObject> kSimilarUserList = new ArrayList<UserSimilarityObject>();

		/* Initialize a list of k elements */
		for (int i = 0; i < kValue; i++) {
			UserSimilarityObject temp = new UserSimilarityObject(0, 0);
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
				
				Boolean similarRating = false;
				/* Scan through list of movies rated by input user */
				for (int columnid = 0; columnid < movieList.size(); columnid++) {
					int movieid = movieList.get(columnid);
					if (ratingList.get(columnid) != 0 && inputMatrix.matrix[rowid][movieid-1] != 0) {
						/*Get average rating for training user from hash map*/
						numerator = numerator + (ratingList.get(columnid))*(inputMatrix.matrix[rowid][movieid-1]);
						denom1 = denom1 + Math.pow(ratingList.get(columnid), 2);
						denom2 = denom2 + Math.pow(inputMatrix.matrix[rowid][movieid-1], 2);
						commonMovieCount++;
					}
					 
					if ((ratingList.get(columnid) != 0 && inputMatrix.matrix[rowid][movieid-1] != 0) &&
							(ratingList.get(columnid) == inputMatrix.matrix[rowid][movieid-1])){
						similarRating = true;
					}
				}
				
				if (commonMovieCount > 1 || (similarRating)) {
					double denom = (Math.sqrt(denom1)*Math.sqrt(denom2));
					if (denom > 0.0) {
						double similarity = numerator/ denom;
						//similarity = inputMatrix.penalize(similarity, commonMovieCount);
						similarity = (commonMovieCount / (commonMovieCount + 2.0)) * similarity;
						
						similarity = similarity * Math.pow(Math.abs(similarity), 1.5);
						/* Add actual user id by adding 1 to rowid */
						UserSimilarityObject obj = new UserSimilarityObject(rowid+1, similarity);
						UserSimilarityObject temp = kSimilarUserList.get(0);
						if (Math.abs(temp.similarity) < Math.abs(obj.similarity)) {
							kSimilarUserList.remove(0);
							kSimilarUserList.add(0, obj);
						}
						Collections.sort(kSimilarUserList, new AbsoluteSimililarityComparator());
					}	
				}
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return kSimilarUserList;
	}
	
	/**
	 * This function calculates movie rating for a input movie. This function is simple 
	 * variation of basic movie prediction.
	 * @param userid
	 * @param inputMovieid
	 * @param kValue
	 * @return
	 */
	public static final int calculateMovieRating(Matrix inputMatrix,List<UserSimilarityObject> tempKSimilarityObjList, int userid, int inputMovieid, int kValue) {
		List<UserSimilarityObject> kSimilarityObjList = new ArrayList<UserSimilarityObject>();		
			
		/* Remove users with object id = 0 */
		for (int i = 0; i < tempKSimilarityObjList.size(); i++) {
			if (tempKSimilarityObjList.get(i).trainingUserid != 0) {
				kSimilarityObjList.add(tempKSimilarityObjList.get(i));
			}
		}
	
		double meanMovieRating = inputMatrix.meanMovieRatingMap.get(inputMovieid);
		
		/* Calculate average rating for the given user */
		double avgRatingForInputUser = inputMatrix.calculateAverageRatingForUser(userid,inputMovieid);
		double finalRating = 0.0;
		double numerator = 0.0;
		double denominator = 0.0;

		for (int i = 0; i < kSimilarityObjList.size(); i++) {
			/* Get similar object from list of k similar objects */
			UserSimilarityObject similarObject = kSimilarityObjList.get(i);
			/* Find user id of similar user */
			int trainingUserid = similarObject.getTrainingUserid();
			if (inputMatrix.matrix[trainingUserid - 1][inputMovieid - 1] > 0) {
				double rating = inputMatrix.matrix[trainingUserid - 1][inputMovieid - 1];
				numerator = numerator + ((kSimilarityObjList.get(i).similarity) * (rating - avgRatingForInputUser));
				denominator = denominator + (kSimilarityObjList.get(i).similarity - avgRatingForInputUser);
			} 
		}
		
		if (denominator > 0.0) {
			finalRating = numerator / denominator;
		} else  if (meanMovieRating > 0){
			finalRating = meanMovieRating;
		} else {
			finalRating = avgRatingForInputUser;
		}
		
		if ((int) Math.rint(finalRating) > 5 || (int) Math.rint(finalRating) <= 0) {
			System.out.println("Error:"+(int) Math.rint(finalRating));
			System.exit(0);
		}
		
		int result = (int) Math.rint(finalRating);
		return result;
	}
}
