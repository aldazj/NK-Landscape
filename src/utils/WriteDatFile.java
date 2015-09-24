package utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by aldazj on 24.09.15.
 */
public class WriteDatFile {
    private String filename;
    private BufferedWriter bw;
    private String folder = "src/data";

    public WriteDatFile(String filename) {
        this.filename = folder+"/"+filename+".dat";
    }

    public void writeResults(double[] results){
        try {
            File file = new File(filename);
            if(!file.exists()){
                file.createNewFile();
            }
            bw = new BufferedWriter(new FileWriter(file));
            for (int i = 0; i < results.length; i++) {
                bw.write(Double.toString(results[i]));
                bw.write("\n");
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
