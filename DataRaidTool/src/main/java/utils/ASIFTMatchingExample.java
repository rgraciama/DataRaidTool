/**
 * Copyright (c) 2011, The University of Southampton and the individual contributors.
 * All rights reserved.
 * <p>
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * <p>
 * * 	Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <p>
 * *	Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p>
 * *	Neither the name of the University of Southampton nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * <p>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package utils;

import com.google.gson.GsonBuilder;
import domain.ChampionKeypoints;
import org.json.simple.parser.ParseException;
import org.openimaj.feature.local.list.LocalFeatureList;
import org.openimaj.feature.local.matcher.FastBasicKeypointMatcher;
import org.openimaj.feature.local.matcher.LocalFeatureMatcher;
import org.openimaj.feature.local.matcher.consistent.ConsistentLocalFeatureMatcher2d;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.feature.local.engine.asift.ASIFTEngine;
import org.openimaj.image.feature.local.keypoints.Keypoint;
import org.openimaj.math.geometry.transforms.HomographyRefinement;
import org.openimaj.math.geometry.transforms.estimation.RobustHomographyEstimator;
import org.openimaj.math.model.fit.RANSAC;
import org.openimaj.util.pair.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Example showing how to extract ASIFT features and match them.
 *
 * @author Jonathon Hare (jsh2@ecs.soton.ac.uk)
 * @author Sina Samangooei (ss@ecs.soton.ac.uk)
 *
 */
public class ASIFTMatchingExample {
    /**
     * Main method
     *
     * @param args
     *            ignored
     * @throws IOException
     *             if the image can't be read
     */
    public static void main(String[] args) throws IOException, ParseException {


        //generate dat files
//        mainGenerateKeypointsSerializable();


        //generate hashmap with deserializer
//        Map<String, ChampionKeypoints> hashChampionKeysPoints = getHashKeypointsDeserializer();
//        System.out.println();
    }

    private static void mainGenerateKeypointsSerializable() {
        //Generate file json with image keypoints
        String[] championNames = getChampionList("images");

        Map<String, ChampionKeypoints> hashChampionKeysPoints = ASIFTMatchingExample.generateChampionsKeyPoints(championNames);
        //generate keystone
        generateSerializableImage(hashChampionKeysPoints);
    }

    public static void mainGenerateKeypoints() throws IOException, ParseException {
        //Generate file json with image keypoints
        String[] championNames = getChampionList("images");

        Map<String, ChampionKeypoints> hashChampionKeysPoints = ASIFTMatchingExample.generateChampionsKeyPoints(championNames);
        //generate keystone
        generateImageJson(hashChampionKeysPoints);
    }

    public static void mainHashmap() throws IOException, ParseException {
        getHashKeypoints();
    }

    public static void generateImageJson(Map<String, ChampionKeypoints> hashChampionKeysPoints) throws IOException, ParseException {
        for (Map.Entry<String, ChampionKeypoints> entry : hashChampionKeysPoints.entrySet()) {
            String imagesJson = new GsonBuilder().setPrettyPrinting().create().toJson(entry.getValue()).trim();
            //files
            String championName = entry.getKey().substring(0, entry.getKey().lastIndexOf('.'));

            FileWriter writer = new FileWriter("src/main/resources/keypoints/"+championName+".json");
            BufferedWriter bw=new BufferedWriter(writer);
            bw.write(imagesJson);
            bw.close();
            writer.close();
        }
    }

    public static void generateSerializableImage(Map<String, ChampionKeypoints> hashChampionKeysPoints)  {


        for (Map.Entry<String, ChampionKeypoints> entry : hashChampionKeysPoints.entrySet()) {
            String imagesJson = new GsonBuilder().setPrettyPrinting().create().toJson(entry.getValue()).trim();
            //files
            String championName = entry.getKey().substring(0, entry.getKey().lastIndexOf('.'));

            //Se crea el fichero
            FileOutputStream fos = null;
            ObjectOutputStream salida = null;
            try {
                fos = new FileOutputStream("src/main/resources/serializable/"+championName+".dat");
                salida = new ObjectOutputStream(fos);
                salida.writeObject(entry.getValue());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * @return a matcher with a homographic constraint
     */
    private static LocalFeatureMatcher<Keypoint> createConsistentRANSACHomographyMatcher() {
        final ConsistentLocalFeatureMatcher2d<Keypoint> matcher = new ConsistentLocalFeatureMatcher2d<Keypoint>(
                createFastBasicMatcher());
        matcher.setFittingModel(new RobustHomographyEstimator(10.0, 1000, new RANSAC.BestFitStoppingCondition(),
                HomographyRefinement.NONE));

        return matcher;
    }

    /**
     * @return a basic matcher
     */
    private static LocalFeatureMatcher<Keypoint> createFastBasicMatcher() {
        return new FastBasicKeypointMatcher<Keypoint>(8);
    }

    public static String compareClipboardImageWithChampionRepositoryImages() throws IOException {
        String[] championNames = getChampionList("images");
        if (championNames == null) return null;


        Map<String, ChampionKeypoints> hashChampionKeysPoints = ASIFTMatchingExample.generateChampionsKeyPoints(championNames);

        String filePath = "src/main/resources/clip/clip.png";

        LocalFeatureList<Keypoint> clipKeyPoints = getKeyPointsFromImage(filePath);

        String championNameResult = compareAllChampionByPicture(hashChampionKeysPoints, clipKeyPoints);

        return championNameResult;
    }

    public static String compareClipboardImageByChampionRepositoryImages(Map<String, ChampionKeypoints> hashChampionKeysPoints) throws IOException {
        String[] championNames = getChampionList("images");
        if (championNames == null) return null;

        String filePath = "src/main/resources/clip/clip.png";

        LocalFeatureList<Keypoint> clipKeyPoints = getKeyPointsFromImage(filePath);

        String championNameResult = compareAllChampionByPicture(hashChampionKeysPoints, clipKeyPoints);

        return championNameResult;
    }

    public static Map<String, ChampionKeypoints> getHashKeypointsDeserializer() {

        String[] championNames = getChampionList("serializable");
        if (championNames == null) return null;
        // Now do the magic.

        Map<String, ChampionKeypoints> hashChampionImages = new HashMap<>();
        for (String championName : championNames) {

            StringBuilder sb = new StringBuilder();

            FileInputStream fis = null;
            ObjectInputStream entrada = null;
            ChampionKeypoints championKeypoints;

            try {
                fis = new FileInputStream("src/main/resources/serializable/"+championName);
                entrada = new ObjectInputStream(fis);
                championKeypoints = (ChampionKeypoints) entrada.readObject(); //es necesario el casting
                hashChampionImages.put(championName, championKeypoints);
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                    if (entrada != null) {
                        entrada.close();
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }

            System.out.println("-> "+championName);
        }

        return hashChampionImages;
    }


    public static String getHashKeypoints() throws IOException {

        String[] championNames = getChampionList("keypoints");
        if (championNames == null) return null;
        // Now do the magic.

        Map<String, ChampionKeypoints> hashChampionImages = new HashMap<>();
        for (String championName : championNames) {

        // create a reader
//            Reader reader = Files.newBufferedReader(Paths.get("src/main/resources/keypoints/"+championName));

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = Files.newBufferedReader(Paths.get("src/main/resources/keypoints/"+championName))) {
                // read line by line
                String line;
                while ((line = br.readLine()) != null) {
//                    sb.append(line).append("\n");
                    sb.append(line);
                }

            } catch (IOException e) {
                System.err.format("IOException: %s%n", e);
            }

            System.out.println("-> "+championName);

//            Type listKeypoints = new TypeToken<LocalFeatureList<Keypoint>>(){}.getType();
            //TODO NO SE COMO HACER PARA QUE GENERE UN LOCALFEATURELIST
//            List<Keypoint> championKeypoints = new Gson().fromJson(sb.toString(), listKeypoints);


//            hashChampionImages.put(championName, championKeypoints);
            System.out.println("-> "+championName);

        }

        return "";
    }

    private static String compareAllChampionByPicture(Map<String, ChampionKeypoints> mapChampionsKeyPoints, LocalFeatureList<Keypoint> clipKeyPoints) {
        int result = 0;
        String championWinner = null;
        // Prepare the matcher, uncomment this line to use a basic matcher as
        // opposed to one that enforces homographic consistency
        // LocalFeatureMatcher<Keypoint> matcher = createFastBasicMatcher();
        final LocalFeatureMatcher<Keypoint> matcher = createConsistentRANSACHomographyMatcher();

        for (Map.Entry<String, ChampionKeypoints> entry : mapChampionsKeyPoints.entrySet()) {
            // Find features in image 1
            matcher.setModelFeatures(entry.getValue().getKeypoints());
            // ... against image 2
            matcher.findMatches(clipKeyPoints);

            // Get the matches
            final List<Pair<Keypoint>> matches = matcher.getMatches();
            if (result < matches.size()) {
                result = matches.size();
                championWinner = entry.getKey();
            }

        }


        return championWinner;

    }


    public static String[] getChampionList(String keyOrImages) {
        File carpeta = new File("src/main/resources/"+keyOrImages);
        String[] championNames = carpeta.list();
        if (championNames == null || championNames.length == 0) {
            System.out.println("No hay elementos dentro de la carpeta actual");
            return null;
        }
        return championNames;
    }


    public static Map<String, ChampionKeypoints> generateChampionsKeyPoints(String[] championList) {
        Map<String, ChampionKeypoints> mapChampionsKeyPoints = new HashMap<>();
        for (String champion : championList) {
            System.out.println("generateChampionsKeyPoints ->: " + champion);
            try {
                FImage imageChampion = ImageUtilities.readF(ASIFTMatchingExample.class.getResourceAsStream("/images/" + champion));
                final ASIFTEngine engine = new ASIFTEngine(false, 7);
                LocalFeatureList<Keypoint> input1Feats = engine.findKeypoints(imageChampion);
                ChampionKeypoints championKeypoints = new ChampionKeypoints();
                championKeypoints.setKeypoints(input1Feats);
                mapChampionsKeyPoints.put(champion, championKeypoints);
            } catch (Exception e) {
                System.out.println("FALLA");
            }
            System.out.println("generateChampionsKeyPoints <-: " + champion);
        }
        return mapChampionsKeyPoints;
    }

    private static LocalFeatureList<Keypoint> getKeyPointsFromImage(String fileName) throws IOException {
//        final FImage input_1 = ImageUtilities.readF(ASIFTMatchingExample.class.getResourceAsStream(fileName));
        InputStream clipImage = new FileInputStream(new File(fileName));
        final FImage input_1 = ImageUtilities.readF(clipImage);
        // Prepare the engine to the parameters in the IPOL demo
        final ASIFTEngine engine = new ASIFTEngine(false, 7);

        // Extract the keypoints from both images
        final LocalFeatureList<Keypoint> inputFeats = engine.findKeypoints(input_1);
        return inputFeats;
    }

}
