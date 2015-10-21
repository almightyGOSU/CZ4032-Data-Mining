package weka.attrEval;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;

public class ClassificationModels {

	private static ClassificationModels _instance = null;
	private static Classifier[] _models = null;
	
	// Classifiers
	private NaiveBayes _naiveBayes = null;
	private J48 _decisionTree = null;
	private RandomForest _randomForest = null;
	private MultilayerPerceptron _neuralNetwork = null;
	private SMO _svm = null;
	private IBk _7NN = null;
	private IBk _9NN = null;
	private IBk _11NN = null;
	private IBk _13NN = null;
	
	private ClassificationModels() {
		
		_naiveBayes = new NaiveBayes();
		/*_naiveBayes.setUseSupervisedDiscretization(true);*/
		
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
		_neuralNetwork.setLearningRate(0.05);
		
		_svm = new SMO();
		
		_7NN = new IBk();
		_7NN.setKNN(7);
		
		_9NN = new IBk();
		_9NN.setKNN(9);
		
		_11NN = new IBk();
		_11NN.setKNN(11);
		
		_13NN = new IBk();
		_13NN.setKNN(13);
		
		_models = new Classifier[] {
				_naiveBayes,
				_decisionTree,
				_randomForest,
				_neuralNetwork,
				_svm,
				_7NN,
				_9NN,
				_11NN,
				_13NN
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
