package utils;

import domain.Champion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class FileManagement {
    public static List<Champion> generateChampionList() {

        String filePath = "src/main/resources/data/champions.csv";

        List<Champion> championList = new ArrayList<>();
        File archivo = null;
        FileReader fr = null;
        BufferedReader br = null;

        try {
            // Apertura del fichero y creacion de BufferedReader para poder
            // hacer una lectura comoda (disponer del metodo readLine()).
            archivo = new File(filePath);
            fr = new FileReader(archivo);
            br = new BufferedReader(fr);

            // Lectura del fichero
            String linea;
            while ((linea = br.readLine()) != null) {
                Champion p = new Champion();
                p.setName(linea);
                championList.add(p);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta
            // una excepcion.
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }

        return championList;
    }


    public static void ReadCSVExample2() {

        String line = "";
        String splitBy = ",";
        String filePath = "src/main/resources/data/data.csv";
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null)   //returns a Boolean value
            {
                String[] champion = line.split(splitBy);    // use comma as separator
                System.out.println("Employee [First Name=" + champion[0] + ", Last Name=" + champion[1] + ", Designation=" + champion[2] + ", Contact=" + champion[3] + ", Salary= " + champion[4] + ", City= " + champion[5] + "]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
