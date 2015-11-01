package weka.attrEval;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
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
	
	/** Determines if the default or optimized classification models
	*  will be used **/
	private boolean _bOptimized = true;
	
	private ClassificationModels() {
		
		_naiveBayes = new NaiveBayes();
		if(_bOptimized) {
			// Default: 	No supervised discretization
			// Optimizied:	Supervised discretization
			_naiveBayes.setUseSupervisedDiscretization(true);
		}
		
		_decisionTree = new J48();
		if(_bOptimized) {
			// Default:		Confidence factor of 0.25
			//				Mininum number of objects is 2
			// Optimized:	Confidence factor of 0.175
			//				Mininum number of objects is 13
			_decisionTree.setConfidenceFactor(0.175f);
			_decisionTree.setMinNumObj(13);
		}
		
		_randomForest = new RandomForest();
		if (_bOptimized) {
			// Default:		Number of features is 0 (unlimited)
			//				Maximum depth is 0 (unlimited)
			//				Number of trees is 10
			// Optimized:	Number of features is 10
			//				Maximum depth is 8
			//				Number of trees is 200
			_randomForest.setNumFeatures(10);
			_randomForest.setMaxDepth(8);
			_randomForest.setNumTrees(200);
		}
		
		_neuralNetwork = new MultilayerPerceptron();
		if (_bOptimized) {
			// Default:		1 Hidden layer with [(# of attributes + # of classes)/2] neurons
			//				Learning rate of 0.3
			// Optimized:	1 Hidden layer with 5 neurons
			//				Learning rate of 0.09
			_neuralNetwork.setHiddenLayers("5");
			_neuralNetwork.setLearningRate(0.09);
		}
		
		_svm = new SMO();
		if(_bOptimized) {
			// Default: 	All attributes normalized
			// Optimized:	No normalization/standardization
			_svm.setFilterType(new SelectedTag(
					SMO.FILTER_NONE, SMO.TAGS_FILTER));
		}
		
		// Using k-NN with k-value of 9, and weight neighbours using 1/distance
		_kNN = new IBk();
		if (_bOptimized) {
			// Default:		k value is 1
			//				No distance weighting performed
			// Optimized:	k value is 9
			//				Neighbours are inversely weighted, i.e. 1/distance
			_kNN.setKNN(9);
			_kNN.setDistanceWeighting(new SelectedTag(
					IBk.WEIGHT_INVERSE, IBk.TAGS_WEIGHTING));
		}
		
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
