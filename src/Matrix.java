/**
 * @author siddharthdalya
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Scanner;

class SimililarityComparator implements Comparator<UserSimilarityObject> {
	@Override
	/**
	 * This function compares two similarity objects
	 */
	public int compare(UserSimilarityObject o1, UserSimilarityObject o2) {
		Double similarity1 = o1.similarity;
		Double similarity2 =  o2.similarity;
		return similarity1.compareTo(similarity2);
	}
}

class MovieSimilarityComparator implements Comparator<MovieSimilarityObject> {
	@Override
	/**
	 * This function compares two movie similarity objects
	 */
	public int compare(MovieSimilarityObject o1, MovieSimilarityObject o2) {
		Double similarity1 = Math.abs(o1.similarity);
		Double similarity2 = Math.abs(o2.similarity);
		return similarity1.compareTo(similarity2);
	}
}

class AverageRatingSimilarityComparator implements Comparator<UserAverageRatingObject> {

	@Override
	public int compare(UserAverageRatingObject o1, UserAverageRatingObject o2) {
		Double averageRating1 = o1.averageRating;
		Double averageRating2 =  o2.averageRating;
		return averageRating1.compareTo(averageRating2);
	}
}

class AbsoluteSimililarityComparator implements Comparator<UserSimilarityObject> {
	@Override
	/**
	 * This function compares two similarity objects
	 */
	public int compare(UserSimilarityObject o1, UserSimilarityObject o2) {
		Double similarity1 = Math.abs(o1.similarity);
		Double similarity2 =  Math.abs(o2.similarity);
		return similarity1.compareTo(similarity2);
	}
}

public class Matrix {
	int rows;
	int columns;
	double matrix[][];
	LinkedHashMap<Integer , Double> trainingAverageRatingMap = new LinkedHashMap<Integer, Double>();
	
	/* This list store average rating for each user in a list of Movie Similarity form in sorted form */
	List<UserAverageRatingObject> trainingUsersAverageRatingList = new ArrayList<UserAverageRatingObject>();
	
	/* This map store IUF for each movie */
	LinkedHashMap<Integer, Double> inverseUserFrequencyMap = new LinkedHashMap<Integer, Double>();
	
	/* This map stores mean rating for a each movie */
	LinkedHashMap<Integer, Double> meanMovieRatingMap = new LinkedHashMap<Integer, Double>();
	
	
	/**
	 * This function generate matrix from training data
	 * Also initialize average rating for training users
	 */
	public Matrix() {
		try {
			Scanner scan = new Scanner(new File("/Users/siddharthdalya/Desktop/train.txt"));
			/* Determine number of rows and columns*/
			while (scan.hasNextLine()) {				
				columns = 0;
				Scanner matCol = new Scanner(scan.nextLine());
				while (matCol.hasNextInt()) {
					columns++;
					matCol.next();
				}
				rows++;
			}
			matrix = new double[rows][columns];
			
			/* Populate rows and columns*/
			rows = 0;
			columns = 0;
			scan = new Scanner(new File("train.txt"));
			while (scan.hasNextLine()) {
				columns = 0;
				Scanner matCol = new Scanner(scan.nextLine());
				while (matCol.hasNextInt()) {
					matrix[rows][columns] = matCol.nextInt();
					columns++;
				}
				rows++;
			}
			scan.close();
			
			for (int userid = 0; userid < rows; userid++) {
				UserAverageRatingObject tempObject = new UserAverageRatingObject(0, 0.0);
				trainingUsersAverageRatingList.add(tempObject);
			}
			/* Calculate average rating by considering all ratings given by training users */
			for (int rowid =  0; rowid < rows; rowid++) {
				int ratingCount = 0;
				double totalRating = 0.0;
				double averageRating  = 0.0;
				for (int columnid = 0; columnid < columns; columnid++) {
					if (matrix[rowid][columnid] != 0) {
						totalRating = totalRating + (double) matrix[rowid][columnid];
						ratingCount++;
					}
				}
				averageRating = totalRating / ratingCount;
				trainingAverageRatingMap.put(rowid+1, averageRating);
				UserAverageRatingObject newObject = new UserAverageRatingObject(rowid+1, averageRating);
				
				UserAverageRatingObject tempObject = trainingUsersAverageRatingList.get(0);
				if (tempObject.averageRating < newObject.averageRating) {
					trainingUsersAverageRatingList.remove(0);
					trainingUsersAverageRatingList.add(newObject);
				}
				Collections.sort(trainingUsersAverageRatingList, new AverageRatingSimilarityComparator());
			}
		} catch (FileNotFoundException e) {
			System.out
					.println("Exception occured while reading matrix from file.");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Read the file and find out movies for which rating is to be calculated.
	 * @return
	 */
	public final MovieList calculateZeroRatedMovieList() {
		MovieList movieRatingList = null;
		try {
			/* Read test file to generate a movie list and rating list for the given user */
			Scanner scan = new Scanner(new File("test20.txt"));	
			int userId  = 0;
			int movieId = 0;
			Double rating = 0.0;
			List<Integer> movieList = new ArrayList<Integer>();
			List<Double> ratingList = new ArrayList<Double>();
			List<Integer> useridList = new ArrayList<Integer>();
			while (scan.hasNextLine()) {
				/* Read test file line by line*/
				String temp = scan.nextLine();
				String[] tempArr = temp.split(" ");
				userId=  Integer.parseInt(tempArr[0]);
				
				/* If user id equals to user id for whom rating needs to be calculated */
				if (Integer.parseInt(tempArr[2]) == 0) {
					movieId = Integer.parseInt(tempArr[1]);
					rating = Double.parseDouble(tempArr[2]);
					movieList.add(movieId);
					ratingList.add(rating);
					useridList.add(userId);
				}				
			}
			movieRatingList =  new MovieList(movieList, ratingList, useridList);
		} catch (Exception e) {
			System.out.println("Exception Occured in findMoviesRatings." +e.getMessage());
			e.printStackTrace();
		}
		return movieRatingList;	
	}
	
	
	/**
	 * This function determines movie and rating for input test user.
	 * @param inputUserid - user id from test file
	 * @return MovieList - Contains movie list and rating list
	 */
	public final MovieList loadMovieAndRatingData(int inputUserid) {
		MovieList movieRatingList = null;
		try {
			/* Read test file to generate a movie list and rating list for the given user */
			Scanner scan = new Scanner(new File("test20.txt"));	
			int userId  = 0;
			int movieId = 0;
			Double rating = 0.0;
			List<Integer> movieList = new ArrayList<Integer>();
			List<Double> ratingList = new ArrayList<Double>();
			while (scan.hasNextLine()) {
				/* Read test file line by line*/
				String temp = scan.nextLine();
				String[] tempArr = temp.split(" ");
				userId=  Integer.parseInt(tempArr[0]);
				
				/* If user id equals to user id for whom rating needs to be calculated */
				if (userId == inputUserid && Integer.parseInt(tempArr[2]) > 0) {
					movieId = Integer.parseInt(tempArr[1]);
					rating = Double.parseDouble(tempArr[2]);
					movieList.add(movieId);
					ratingList.add(rating);
				}				
			}
			movieRatingList =  new MovieList(movieList, ratingList);
		} catch (Exception e) {
			System.out.println("Exception Occured in findMoviesRatings." +e.getMessage());
			e.printStackTrace();
		}
		return movieRatingList;	
	}
	
	/**
	 * This function calculates average rating for a test user
	 * @param userid - user id for whom average rating is to be calculated
	 * @return
	 */
	public final double calculateAverageRatingForUser(int userid, int inputMovieid) {
		double avgRating = 0.0;
		Double totalRating = 0.0;
		MovieList mlist = loadMovieAndRatingData(userid);
		/* Calculate average Rating */
		List<Integer> movieList = mlist.getMovieList();
		List<Double> ratingList = mlist.getRatingList();
		for (int movieCount = 0; movieCount < movieList.size(); movieCount++) {
			totalRating = totalRating + ratingList.get(movieCount);
		}
		avgRating = (Double)totalRating / movieList.size();
		return avgRating;
	}
	
	/**
	 * This function calculates mean rating for each movie
	 */
	public final void calculateMeanRatingForMovie() {	
		for (int columnid = 0; columnid < columns; columnid++) {
			double meanRating = 0.0;
			int itemRatingCount = 0;
			double totalRating = 0.0;
			for (int rowid = 0; rowid < rows; rowid++) {
				if (matrix[rowid][columnid] != 0.0) {
					totalRating = totalRating + matrix[rowid][columnid];
					itemRatingCount++;
				}
			}
			/* Put mean rating for the movie in the map */
			if (itemRatingCount > 0) {
				meanRating = totalRating / itemRatingCount;
				meanMovieRatingMap.put(columnid+1, meanRating);
			} else {
				meanMovieRatingMap.put(columnid+1, 0.0);
			}
		}
	}
	
	/**
	 * This function penalize users having less movies in common
	 * @param inputSimilarity
	 * @param inputCommonMovieCount
	 * @return
	 */
	public final double penalize(double inputSimilarity, int inputCommonMovieCount) {
		double outputSimilarity = 0.0;
		final double constant = 2.0;
		outputSimilarity = (inputCommonMovieCount / (inputCommonMovieCount + constant)) * inputSimilarity;
		return outputSimilarity;
	}

	/**
	 * This function calculates inverse user frequency for a input movie
	 * @param inputMovieId - movie id for which inverse user frequency is to be  calculated
	 * @return inverse user frequency for a movie
	 */
	public final void calculateInverseUserFrequency() {
		
		/* Number of users who have rated input movie id */
		
		for (int columnid = 0; columnid < columns; columnid++) {
			double invUserFrequency = 0.0;
			int movieid = columnid + 1;
			int numberOfUsers = 0;
			for (int rowid = 0; rowid < rows; rowid++) {
				if (matrix[rowid][columnid] > 0) {
					numberOfUsers++;
				}
			}
			if (numberOfUsers > 0) {
				invUserFrequency = Math.log10(rows / numberOfUsers);
			} else {
				invUserFrequency = 0.0;
			}
				
			inverseUserFrequencyMap.put(movieid, invUserFrequency);
		}	
	}

	public static void main(String[] args) {

			Matrix mat = new Matrix();
			mat.calculateMeanRatingForMovie();
			int kValue = 80;
			mat.calculateInverseUserFrequency();
			MovieList inputList = mat.calculateZeroRatedMovieList();
			
			try{
				  // Create file 
				  FileWriter fstream = new FileWriter("result20.txt", true);
				  BufferedWriter out = new BufferedWriter(fstream);
				  //Close the output stream
				  int lastUserid = 0;
				  
				  // For user based collaborative filtering
				  List<UserSimilarityObject> similarityObjListCosine = null;
				  List<UserSimilarityObject> similarityObjListPearson = null;
				  
				  // For item based collaborative filtering
				  MovieList testUserRatedList = null;
				  List<MovieSimilarityObject> movieSimilarityObjList = null;
				  boolean itemBased = false;
				  
				   for (int i = 0; i < inputList.getUseridList().size(); i++) {
					  int userid = inputList.getUseridList().get(i);
					  int movieid = inputList.getMovieList().get(i);
					  
					  if (lastUserid == 0 || lastUserid != userid) {
						  kValue = 80;
						  
						  similarityObjListCosine = CalculateCosineSimilarity.calculateCosineSimilarity(mat, userid, movieid, kValue);

						  similarityObjListPearson = CalculatePearsonCorrelation.calculatePearsonCorrelation(mat, userid, movieid, kValue);
						  
						  testUserRatedList = mat.loadMovieAndRatingData(userid);
						  lastUserid = userid;
					  }
					 
					  if (itemBased == true) {
						  movieSimilarityObjList = ItemBasedCollaborativeFiltering.itemBasedCollaborativeFiltering(mat, testUserRatedList, userid, movieid, kValue);
					  }
					  /* User based collaborative filtering */
					  double rating1 = CalculateCosineSimilarity.calculateMovieRating(mat, similarityObjListCosine, userid, movieid, kValue);
					  double rating2 = CalculatePearsonCorrelation.calculateMovieRating(mat, similarityObjListPearson, userid, movieid, kValue);
					  int rating = (int) Math.rint((rating1 + rating2)/2.0);
					  
					  /* Item based collaborative filtering */
					 // int rating = ItemBasedCollaborativeFiltering.calculateMovieRating(mat, movieSimilarityObjList, testUserRatedList, userid, movieid, kValue);
					  
					  System.out.println("Userid:" + userid + "\tMovie id:" + movieid
					  + "\t Rating:"
						+ rating);
					  
					  String line = userid + " " +movieid+ " "+rating;
					  out.write(line);
					  out.write("\n");
					
				  }
				  out.close();
			  	}catch (Exception e){
			  		System.err.println("Error: " + e.getMessage());
			  	}
		}
	}

