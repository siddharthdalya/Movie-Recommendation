import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author siddharthdalya
 *
 */

public class ItemBasedCollaborativeFiltering {

	public final static List<MovieSimilarityObject> itemBasedCollaborativeFiltering(Matrix inputMatrix, MovieList inputUserRatedList, int inputUserid, int inputMovieid, int kValue) {
		/* List of k similar movies */
		List<MovieSimilarityObject> kSimilarMovies = new ArrayList<MovieSimilarityObject>();
		for (int i = 0; i < kValue; i++) {
			MovieSimilarityObject temp = new MovieSimilarityObject(0, 0);
			kSimilarMovies.add(temp);
		}
		int tempid = inputMovieid - 1;
		
		//MovieList list = inputMatrix.loadMovieAndRatingData(inputUserid);
		List<Integer> ratedMovieList = inputUserRatedList.getMovieList();
		
		for (int i = 0; i < ratedMovieList.size(); i++) {
			int ratedMovieid = ratedMovieList.get(i);
			Boolean similarRating = false;
			Double numerator = 0.0;
			Double denom1 = 0.0;
			Double denom2 = 0.0;
			
			int commonUsercount = 0;
			for (int rowid = 0; rowid < inputMatrix.rows; rowid++) {
				//int userid = rowid+1;
				if (inputMatrix.matrix[rowid][ratedMovieid - 1] !=0 && inputMatrix.matrix[rowid][inputMovieid - 1] != 0) {
					/* Get average rating for training user from hash map */
					//Double trainingUserAvgRating = inputMatrix.trainingAverageRatingMap.get(userid);
					//numerator = numerator + ((inputMatrix.matrix[rowid][ratedMovieid - 1] - trainingUserAvgRating) * (inputMatrix.matrix[rowid][tempid] - trainingUserAvgRating));
					//denom1 = denom1 + Math.pow((inputMatrix.matrix[rowid][ratedMovieid - 1] - trainingUserAvgRating), 2);
					//denom2 = denom2 + Math.pow((inputMatrix.matrix[rowid][tempid] - trainingUserAvgRating), 2);
					numerator = numerator + ((inputMatrix.matrix[rowid][ratedMovieid - 1]) * (inputMatrix.matrix[rowid][tempid]));
					denom1 = denom1 + Math.pow((inputMatrix.matrix[rowid][ratedMovieid - 1]), 2);
					denom2 = denom2 + Math.pow((inputMatrix.matrix[rowid][tempid]), 2);
					commonUsercount++;
				}
			}
			
			
			if (commonUsercount > 1 || (commonUsercount ==1 && similarRating)) {
				if (denom1 > 0.0 && denom2 > 0.0) {
					double denom = (Math.sqrt(denom1) * Math.sqrt(denom2));
					if (denom > 0.0) {
						double similarity = numerator / denom;
						// Penalize similarity
						similarity = (commonUsercount / (commonUsercount + 2.0)) * similarity;

						MovieSimilarityObject obj = new MovieSimilarityObject(ratedMovieid, similarity);
						MovieSimilarityObject temp = kSimilarMovies.get(0);
						if (Math.abs(temp.similarity) < Math.abs(obj.similarity)) {
							kSimilarMovies.remove(0);
							kSimilarMovies.add(0, obj);
						}
						Collections.sort(kSimilarMovies, new MovieSimilarityComparator());
					}
				}
			}
		}
		return kSimilarMovies;
	}
	
	public static final int calculateMovieRating(Matrix inputMatrix, List<MovieSimilarityObject> inputSimilarityList, MovieList inputMovieList, int inputUserid, int inputMovieid, int kValue) {
		//int rating = 0;
		List<MovieSimilarityObject> kSimilarList = new ArrayList<MovieSimilarityObject>();
		double meanMovieRating  = inputMatrix.meanMovieRatingMap.get(inputMovieid);
		double userMeanRating = inputMatrix.calculateAverageRatingForUser(inputUserid, inputMovieid);
		
		for (int i = 0; i < inputSimilarityList.size(); i++) {
			MovieSimilarityObject temp = inputSimilarityList.get(i);
			if (temp.movieid != 0) {
				kSimilarList.add(temp);
			}
		}
		
		///MovieList ratedMovieList = inputMatrix.loadMovieAndRatingData(inputUserid);
		List<Integer> movieList = inputMovieList.getMovieList();
		List<Double> ratingList = inputMovieList.getRatingList();
		
		Double rating = 0.0;
		Double numerator = 0.0;
		Double denominator = 0.0;
		for (int mid = 0; mid < movieList.size(); mid++) {
			int movieid = movieList.get(mid);
			double ratings = ratingList.get(mid);
			for (int listid = 0; listid < kSimilarList.size(); listid++) {
				MovieSimilarityObject movieObj = kSimilarList.get(listid);
				int tempMovieid = movieObj.movieid;
				double similarity  = movieObj.similarity;
				if (tempMovieid == movieid && Math.abs(similarity) > 0.0) {	
					numerator = numerator + (similarity * ratings);
					
					// Not an absolute value
					denominator = denominator + Math.abs(similarity);
				}
			}
		}
		
		if (denominator > 0.0) {
			rating = numerator / denominator;
		} else if ((int) Math.rint(meanMovieRating) > 0 && (int) Math.rint(meanMovieRating) <= 5){
			rating = meanMovieRating;
		} else {
			rating = userMeanRating;
		}
	
		if ((int) Math.rint(rating) <= 0 || (int) Math.rint(rating) > 5)
		{
			System.out.println("Error Rating:"+rating);
			System.exit(0);
		}
		
		return (int) Math.rint(rating);
	}
}
