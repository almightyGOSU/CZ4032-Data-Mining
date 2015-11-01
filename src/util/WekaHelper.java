package util;

import java.util.ArrayList;

import weka.attrEval.ClassificationModels;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.NominalPrediction;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import classes.RemovedAttribute;

public class WekaHelper {

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
	
	public static RemovedAttribute calcAccuracy(Instances dataInstances) {

		RemovedAttribute attr = new RemovedAttribute("None");
		
		// Set last attribute as class index
		dataInstances.setClassIndex(
				dataInstances.numAttributes() - 1);

		calcAccuracies(dataInstances, attr);
		return attr;
	}
	
	public static void removeAttrAndCalc(Instances dataInstances,
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
	
	public static int[] rankTechniques(RemovedAttribute baseline) {
		
		int numTechniques = Const.MODELS.length;
		int[] ranks = new int[numTechniques];
		
		for(int i = 0; i < numTechniques; i++) {
			
			int rank = 1;
			for(int j = 0; j < numTechniques; j++) {
				
				double accuracy1 = baseline.getAccuracy(Const.MODELS[i]);
				double accuracy2 = baseline.getAccuracy(Const.MODELS[j]);
				
				if(accuracy1 > accuracy2)
					rank++;
			}
			
			ranks[i] = (numTechniques - rank + 1);
		}
		
		return ranks;
	}
	
	public static double[] rankToWeight(int[] ranks) {
		
		int numTechniques = Const.MODELS.length;
		double[] weights = new double[ranks.length];
		
		for(int i = 0; i < weights.length; i++) {
			weights[i] = 1.0 + ((numTechniques - ranks[i]) * 0.1);
		}
		
		return weights;
	}
	
	public static String getColumnHeaders() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("%n%-18s", "Removed Attribute")).append(" | ");
		for(String model : Const.MODELS) {
			
			sb.append(String.format("%-13s", model));
			sb.append(" | ");
		}
		sb.append(String.format("%-13s", "Gain/Loss"));
		
		return sb.toString();
	}
	
	public static void printLine(StringBuilder sb, String contents) {
		
		sb.append(contents).append("\n");
		System.out.println(contents);
	}
	
	public static String[] concat(String[] a, String[] b) {
		int aLen = a.length;
		int bLen = b.length;
		String[] c = new String[aLen + bLen];
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);
		return c;
	}
}
