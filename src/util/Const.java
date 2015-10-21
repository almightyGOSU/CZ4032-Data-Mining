package util;

public class Const {

	public static final String MATH_BINARY_FILENAME =
			"data\\student_math_binary.arff";
	public static final String MATH_GRADE_FILENAME = 
			"data\\student_math_grade.arff";
	
	public static final String RESULTS_FILENAME =
			"data\\student_math_attrEvalResults.txt";
	
	public static final String GOOD_BAD_ATTR_FILENAME =
			"data\\student_math_goodBadAttr.txt";
	
	public static final int TOP_N = 5;
	
	public static final int CROSS_VALIDATION_FOLDS = 10;
	
	public static final String[] MODELS = {
		"Naive Bayes",
		"Decision Tree",
		"Random Forest",
		"NN (MLP)",
		"SVM",
		"k-NN (k = 7)",
		"k-NN (k = 9)",
		"k-NN (k = 11)",
		"k-NN (k = 13)"
	};
}
