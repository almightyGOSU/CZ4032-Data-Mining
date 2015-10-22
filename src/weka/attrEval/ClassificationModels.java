package weka.attrEval;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.RBFClassifier;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.SelectedTag;

public class ClassificationModels {

	private static ClassificationModels _instance = null;
	private static Classifier[] _models = null;
	
	// Classifiers
	private NaiveBayes _naiveBayes = null;
	private J48 _decisionTree = null;
	private RandomForest _randomForest = null;
	private MultilayerPerceptron _neuralNetwork = null;
	private SMO _svm = null;
	private IBk _kNN = null;
	
	private ClassificationModels() {
		
		_naiveBayes = new NaiveBayes();
		_naiveBayes.setUseSupervisedDiscretization(true);
		
		_decisionTree = new J48();
		_randomForest = new RandomForest();
		
		/* Default: 'a'
		 *  'a' = (number of attributes + number of classes) / 2
		 *  'i' = number of attributes
		 *  'o' = number of classes
		 *  't' = number of attributes + number of classes
		 */
		_neuralNetwork = new MultilayerPerceptron();
		_neuralNetwork.setHiddenLayers("5");
		_neuralNetwork.setLearningRate(0.09);
		
		_svm = new SMO();
		
		// Using k-NN with k-value of 9, and weight neighbours using 1/distance
		_kNN = new IBk();
		_kNN.setKNN(9);
		_kNN.setDistanceWeighting(
				new SelectedTag(IBk.WEIGHT_INVERSE, IBk.TAGS_WEIGHTING));
		
		_models = new Classifier[] {
				_naiveBayes,
				_decisionTree,
				_randomForest,
				_neuralNetwork,
				_svm,
				_kNN
		};
	}
	
	public static ClassificationModels getInstance() {
		
		if(_instance == null) {
			_instance = new ClassificationModels();
		}
		
		return _instance;
	}
	
	public Classifier[] getModels() {
		
		return _models;
	}
}
