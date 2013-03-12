import java.util.List;

/**
 * This class stores movie id, rating and user id in different lists.
 * @author siddharthdalya
 *
 */
public class MovieList {
	 List<Integer> movieList;
	 List<Double> ratingList;
	 List<Integer> useridList;
	 public List<Integer> getUseridList() {
		return useridList;
	}

	public void setUseridList(List<Integer> useridList) {
		this.useridList = useridList;
	}

	public List<Integer> getMovieList() {
		return movieList;
	}

	public void setMovieList(List<Integer> movieList) {
		this.movieList = movieList;
	}

	public List<Double> getRatingList() {
		return ratingList;
	}

	public void setRatingList(List<Double> ratingList) {
		this.ratingList = ratingList;
	}

	
	
	public MovieList(List<Integer> inputMovieList, List<Double> inputRatingList) {
		movieList = inputMovieList;
		ratingList =  inputRatingList;
	}
	
	public MovieList(List<Integer> inputMovieList, List<Double> inputRatingList, List<Integer> inputUseridList) {
		movieList = inputMovieList;
		ratingList =  inputRatingList;
		useridList = inputUseridList;
	}
}
