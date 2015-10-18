package weka.attributeEvaluation;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import classes.RemovedAttribute;
import util.Const;
import util.FileIOHelper;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class WekaAttributeEvaluator {
	
	private static RemovedAttribute _binaryBaseline = null;
	private static ArrayList<RemovedAttribute> _binaryAttrList = null;
	
	private static RemovedAttribute _gradeBaseline = null;
	private static ArrayList<RemovedAttribute> _gradeAttrList = null;
	
	private static StringBuilder _sb = new StringBuilder();

	public static void main(String[] args) {
		
		BufferedReader binaryDataFile = FileIOHelper
				.readDataFile(Const.MATH_BINARY_FILENAME);
		Instances binaryDataInstances = null;
		try {
			binaryDataInstances = new Instances(binaryDataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (binaryDataInstances != null) {
			
			System.out.println(
					"Calculating baseline accuracies for binary data....");
			_binaryBaseline = calcBaseline(binaryDataInstances);
			int[] weights = rankTechniques(_binaryBaseline);
			printLine("Weights for binary data: "
					+ Arrays.toString(weights) + "\n");
			
			_binaryAttrList = new ArrayList<>();
			removeAttrAndCalc(binaryDataInstances, _binaryAttrList);
			
			printLine(getColumnHeaders());
			printLine(_binaryBaseline.toString());
			
			// Calculate accuracy gain/loss
			for(RemovedAttribute ra : _binaryAttrList) {
				ra.calcAccuracyGainLoss(_binaryBaseline, weights);
			}
			
			Collections.sort(_binaryAttrList);
			for(RemovedAttribute ra : _binaryAttrList) {
				printLine(ra.toString());
			}
		}
		
		BufferedReader gradeDataFile = FileIOHelper
				.readDataFile(Const.MATH_GRADE_FILENAME);
		Instances gradeDataInstances = null;
		try {
			gradeDataInstances = new Instances(gradeDataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if (gradeDataInstances != null) {
			
			System.out.println(
					"\n\nCalculating baseline accuracies for grade data....");
			_gradeBaseline = calcBaseline(gradeDataInstances);
			int[] weights = rankTechniques(_gradeBaseline);
			printLine("Weights for grade data: "
					+ Arrays.toString(weights) + "\n");
			
			_gradeAttrList = new ArrayList<>();
			removeAttrAndCalc(gradeDataInstances, _gradeAttrList);
			
			printLine(getColumnHeaders());
			printLine(_gradeBaseline.toString());
			
			// Calculate accuracy gain/loss
			for(RemovedAttribute ra : _gradeAttrList) {
				ra.calcAccuracyGainLoss(_gradeBaseline, weights);
			}
			
			Collections.sort(_gradeAttrList);
			for(RemovedAttribute ra : _gradeAttrList) {
				printLine(ra.toString());
			}
		}
		
		FileIOHelper.saveToFile(_sb.toString(), Const.RESULTS_FILENAME, false);
	}
	
	private static Evaluation classify(Classifier model, Instances trainingSet,
			Instances testingSet) {

		Evaluation evaluation = null;
		try {
			evaluation = new Evaluation(trainingSet);
			model.buildClassifier(trainingSet);
			evaluation.evaluateModel(model, testingSet);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return evaluation;
	}

	/**
	 * Returns the accuracy of the predictions<br>
	 * [0, 100]%
	 * 
	 * @param predictions The set of predictions
	 * @return Accuracy of predictions
	 */
	private static double calculateAccuracy(
			ArrayList<NominalPrediction> predictions) {

		double correct = 0;

		for (int i = 0; i < predictions.size(); i++) {
			NominalPrediction np = predictions.get(i);
			if (np.predicted() == np.actual()) {
				correct++;
			}
		}

		return 100 * (correct / predictions.size());
	}

	/**
	 * Splits the data into training set and testing set
	 */
	private static Instances [][] crossValidationSplit(Instances data,
			int numberOfFolds) {
		
		Instances [][] split = new Instances[2][numberOfFolds];

		for (int i = 0; i < numberOfFolds; i++) {
			split[0][i] = data.trainCV(numberOfFolds, i);
			split[1][i] = data.testCV(numberOfFolds, i);
		}

		return split;
	}
	
	private static RemovedAttribute calcBaseline(Instances dataInstances) {

		RemovedAttribute attr = new RemovedAttribute("None");
		
		// Set last attribute as class index
		dataInstances.setClassIndex(
				dataInstances.numAttributes() - 1);

		calcAccuracies(dataInstances, attr);
		return attr;
	}
	
	private static void removeAttrAndCalc(Instances dataInstances,
			ArrayList<RemovedAttribute> attrList) {
		
		int numAttributes = dataInstances.numAttributes();
		Remove removeAttrFilter = new Remove();
		
		for (int attrIndex = 0; attrIndex < numAttributes - 1; attrIndex++) {
			
			String removedAttrName =
					dataInstances.attribute(attrIndex).name();
			System.out.println("Removed attribute '" + removedAttrName +
					"'.. Calculating accuracies after removing attribute....");
			RemovedAttribute removedAttr = new RemovedAttribute(removedAttrName);
			
			// Remove one of the attributes
			removeAttrFilter.setAttributeIndices(
					String.valueOf(attrIndex + 1));
			Instances filteredData = null;
			try {
				removeAttrFilter.setInputFormat(dataInstances);
				filteredData = Filter.useFilter(
						dataInstances, removeAttrFilter);
				filteredData.setClassIndex(filteredData.numAttributes() - 1);
			} catch (Exception e) {
				e.printStackTrace();
			}

			calcAccuracies(filteredData, removedAttr);
			attrList.add(removedAttr);
		}
	}
	
	private static void calcAccuracies(Instances dataInstances, RemovedAttribute attr) {

		// Separate data instances into training and testing splits
		Instances[][] split = crossValidationSplit(dataInstances,
				Const.CROSS_VALIDATION_FOLDS);
		Instances[] trainingSet = split[0];
		Instances[] testingSet = split[1];
		
		Classifier[] models = ClassificationModels.getInstance().getModels();

		// Run for each model
		for (int currModel = 0; currModel < models.length; currModel++) {

			// Collect all the predictions for current model
			ArrayList<NominalPrediction> predictions = new ArrayList<>();

			// For each training-testing split pair,
			// train and test the classifier
			for (int i = 0; i < trainingSet.length; i++) {

				Evaluation validation = classify(
						models[currModel], trainingSet[i], testingSet[i]);
				
				for(Object o : validation.predictions()) {
					if(o instanceof NominalPrediction)
						predictions.add((NominalPrediction) o);
				}
			}

			// Calculate overall accuracy of current classifier on all splits
			double accuracy = calculateAccuracy(predictions);

			// Store accuracy
			attr.addAccuracy(Const.MODELS[currModel], accuracy);
		}
	}
	
	private static int[] rankTechniques(RemovedAttribute baseline) {
		
		HashMap<String, Double> baselineAccuracies = baseline.getAccuracies();
		
		int numTechniques = baselineAccuracies.keySet().size();
		int[] ranks = new int[numTechniques];
		
		ArrayList<String> techniques = new ArrayList<>();
		techniques.addAll(baselineAccuracies.keySet());
		
		for(int i = 0; i < numTechniques; i++) {
			
			int rank = 1;
			for(int j = 0; j < numTechniques; j++) {
				
				double accuracy1 = baseline.getAccuracy(techniques.get(i));
				double accuracy2 = baseline.getAccuracy(techniques.get(j));
				
				if(accuracy1 > accuracy2)
					rank++;
			}
			
			ranks[i] = rank;
		}
		
		return ranks;
	}
	
	private static String getColumnHeaders() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("%n%-18s", "Removed Attribute")).append(" | ");
		for(String model : Const.MODELS) {
			
			sb.append(String.format("%-13s", model));
			sb.append(" | ");
		}
		sb.append(String.format("%-13s", "Gain/Loss"));
		
		return sb.toString();
	}
	
	private static void printLine(String contents) {
		
		_sb.append(contents).append("\n");
		System.out.println(contents);
	}
}