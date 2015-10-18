package weka.attributeEvaluation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import libsvm.svm;
import util.Const;
import util.FileIOHelper;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.rules.DecisionTable;
import weka.classifiers.rules.PART;
import weka.classifiers.trees.DecisionStump;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.FastVector;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

public class WekaAttributeEvaluator {

	public static void main(String[] args) {

		BufferedReader dataFile = FileIOHelper
				.readDataFile(Const.MATH_BINARY_FILENAME);
		Instances dataInstances = null;
		try {
			dataInstances = new Instances(dataFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (dataInstances == null)
			return;

		dataInstances.setClassIndex(dataInstances.numAttributes() - 1);
		Remove filter = new Remove();

		Instances[][] split = crossValidationSplit(dataInstances, 10);
		// Separate split into training and testing arrays
		Instances[] trainingSplits = split[0];
		Instances[] testingSplits = split[1];
		IBk ibk = new IBk();
		ibk.setKNN(7);
		IBk ibk1 = new IBk();
		ibk.setKNN(9);
		IBk ibk2 = new IBk();
		ibk.setKNN(11);

		// Use a set of classifiers
		Classifier[] models = {
				new J48(), // a decision tree
				new PART(),
				new DecisionTable(),// decision table majority classifier
				new DecisionStump(),// one-level decision tree
				new NaiveBayes(), ibk, ibk1, ibk2, new SMO(),
				new MultilayerPerceptron(), new RandomForest()

		};

		// Run for each model
		for (int j = 0; j < models.length; j++) {

			// Collect every group of predictions for current model in a
			// FastVector
			FastVector predictions = new FastVector();

			// For each training-testing split pair, train and test the
			// classifier
			for (int i = 0; i < trainingSplits.length; i++) {

				Evaluation validation = classify(models[j], trainingSplits[i],
						testingSplits[i]);

				predictions.appendElements(validation.predictions());

				// Uncomment to see the summary for each training-testing pair.
				// System.out.println(models[j].toString());
			}

			// Calculate overall accuracy of current classifier on all splits
			double accuracy = calculateAccuracy(predictions);

			// Print current classifier's name and accuracy in a complicated,
			// but nice-looking way.

			System.out.println("Accuracy of "
					+ models[j].getClass().getSimpleName() + ": "
					+ String.format("%.2f%%", accuracy)
					+ "\n---------------------------------");

		}

		// add filter
		for (int count = 0; count < dataInstances.numAttributes() - 1; count++) {
			System.out.println("Count : " + (count + 1));
			System.out.println("Removed attribute : "
					+ dataInstances.attribute(count));
			
			filter.setAttributeIndices(String.valueOf(count + 1));
			try {
				filter.setInputFormat(dataInstances);
			} catch (Exception e) {
				e.printStackTrace();
			}

			Instances filtered_data = null;
			try {
				filtered_data = Filter.useFilter(dataInstances, filter);
			} catch (Exception e) {
				e.printStackTrace();
			}
			filtered_data.setClassIndex(filtered_data.numAttributes() - 1);

			// Do 10-split cross validation
			split = crossValidationSplit(filtered_data, 10);
			// Separate split into training and testing arrays
			trainingSplits = split[0];
			testingSplits = split[1];

			// Run for each model
			for (int j = 0; j < models.length; j++) {

				// Collect every group of predictions for current model in a
				// FastVector
				FastVector predictions = new FastVector();

				// For each training-testing split pair, train and test the
				// classifier
				for (int i = 0; i < trainingSplits.length; i++) {
					Evaluation validation = classify(models[j],
							trainingSplits[i], testingSplits[i]);

					predictions.appendElements(validation.predictions());

					// Uncomment to see the summary for each training-testing
					// pair.
					// System.out.println(models[j].toString());
				}

				// Calculate overall accuracy of current classifier on all
				// splits
				double accuracy = calculateAccuracy(predictions);

				// Print current classifier's name and accuracy in a
				// complicated,
				// but nice-looking way.

				System.out.println("Accuracy of "
						+ models[j].getClass().getSimpleName() + ": "
						+ String.format("%.2f%%", accuracy)
						+ "\n---------------------------------");

			}
			System.out.println("");
		}
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

	private static double calculateAccuracy(FastVector predictions) {
		double correct = 0;

		for (int i = 0; i < predictions.size(); i++) {
			NominalPrediction np = (NominalPrediction) predictions.elementAt(i);
			if (np.predicted() == np.actual()) {
				correct++;
			}
		}

		return 100 * correct / predictions.size();
	}

	private static Instances[][] crossValidationSplit(Instances data,
			int numberOfFolds) {
		Instances[][] split = new Instances[2][numberOfFolds];

		for (int i = 0; i < numberOfFolds; i++) {
			split[0][i] = data.trainCV(numberOfFolds, i);
			split[1][i] = data.testCV(numberOfFolds, i);
		}

		return split;
	}
}