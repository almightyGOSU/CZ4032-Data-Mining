import java.io.File;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;


public class CSV2Arff {
  
  public static void main(String[] args) throws Exception {
    
    // load CSV
    CSVLoader loader = new CSVLoader();
    loader.setSource(new File("E:/NTU/kat/CZ4032/src/dataset/converted.csv"));
    Instances data = loader.getDataSet();//get instances object

    // save ARFF
    ArffSaver saver = new ArffSaver();
    saver.setInstances(data);//set the dataset we want to convert
    //and save as ARFF
    saver.setFile(new File("E:/NTU/kat/CZ4032/src/dataset/student-mat.arff"));
    saver.writeBatch();
  }
} 