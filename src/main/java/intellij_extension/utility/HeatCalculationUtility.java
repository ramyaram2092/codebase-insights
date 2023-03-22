package intellij_extension.utility;

import intellij_extension.Constants;
import intellij_extension.models.redesign.*;
import intellij_extension.utility.filesize.FileSizeCalculator;
import javafx.scene.paint.Color;

import java.util.*;

/**
 * Modifies the Codebase so that every HeatObject within it is given a heat level
 * according to the specified metric.
 * assignHeatLevels() is the entry point of this class, and it is capable of giving every
 * HeatObject within a Codebase heat levels according to the given metric.
 */
public class HeatCalculationUtility
{
    private HeatCalculationUtility() {
        //This is a utility class
    }


    /**
     * Converts the input heat level to a color.
     * Higher heat levels are indicated by higher intensities of red.
     * @param heatLevel a number from 1 to 10
     * @return a Color between blue (for 1) and red (for 10)
     */
    public static Color colorOfHeat(int heatLevel) {
        // Get percentage
        float heatPercentage = heatLevel / ((float) Constants.HEAT_MAX);

        // Get color based on percentage 0 = completely BLUE 1 = Completely RED
        Color heatColor = Constants.HEAT_MIN_COLOR.interpolate(Constants.HEAT_MAX_COLOR, heatPercentage);

        return heatColor;
    }


    /**
     * Converts the input heat level to a color.
     * Higher heat levels are indicated by higher intensities of red.
     * @param heatLevel a number from 1 to 10
     * @return a hexadecimal String of the form "FFFFFF" representing a color
     */
    public static String colorOfHeatAsHex(int heatLevel) {
        //Convert heat level to Color
        Color fileHeatColor = colorOfHeat(heatLevel);

        //Convert Color to hex
        String colorString = fileHeatColor.toString();
        return colorString.substring(colorString.indexOf("x") + 1);
    }



    private static void assignHeatLevelsFileSize(Codebase codebase)
    {
        System.out.println("Calculating heat based on file size...");
        final int REQUIRED_NUM_COMMITS_WITHOUT_CHANGING = 5; //the number of consecutive commits where no increase in a file's size is recorded needed in order to reduce the accumulated heat level.
        final int REQUIRED_SIZE_CHANGE = 200;
        final int SIZE_INCREASE_HEAT_CONSEQUENCE = 2; //how much the heat increases when the file size increases
        final int SIZE_DECREASE_HEAT_CONSEQUENCE = -1; //how much the heat decreases when the file size decreases
        final int SIZE_NO_CHANGE_HEAT_CONSEQUENCE = -1; //how much the heat decreases if the file size stays the same for long enough

        Set<FileObject> fileObjectSet = codebase.getActiveFileObjects();
        for (FileObject fileObject : fileObjectSet)
        {
            //System.out.println("\n---------- "+fileObject.getFilename()+" ----------");
            //The oldest commits are at the front of the LinkedHashMap
            LinkedHashMap<String, HeatObject> commitHashToHeatObjectMap = fileObject.getCommitHashToHeatObjectMap();

            HeatObject lastHeatObject = null;
            int numberOfConsecutiveCommitsWithNoSizeIncrease = 0;

            for (Map.Entry<String, HeatObject> commitToHeatObjectEntry : commitHashToHeatObjectMap.entrySet())
            {
                HeatObject newerHeatObject = commitToHeatObjectEntry.getValue();
                if (lastHeatObject != null)
                {
                    //Compute the heat based on how much the size has changed over time
                    int accumulatedHeatLevel = lastHeatObject.getHeatLevel(); //use previous heat, then modify

                    //If the file size increased at all, incur 2 heat
                    long oldFileSize = lastHeatObject.getFileSize();
                    long newFileSize = newerHeatObject.getFileSize();

/*if (fileObject.getFilename().equals("HeatMapPane.java"))
{
    System.out.println("New Hash: "+commitToHeatObjectEntry.getKey());
    System.out.println("oldFileSize: "+oldFileSize);
    System.out.println("newFileSize: "+newFileSize);
}*/

                    //File size increase -> gain heat
                    if (newFileSize > oldFileSize)
                    {
                        accumulatedHeatLevel += SIZE_INCREASE_HEAT_CONSEQUENCE;
                        numberOfConsecutiveCommitsWithNoSizeIncrease = 0;
                    }
                    //File size decrease -> lose heat
                    else if (oldFileSize - newFileSize >= REQUIRED_SIZE_CHANGE)
                    {
                        accumulatedHeatLevel += SIZE_DECREASE_HEAT_CONSEQUENCE;
                        numberOfConsecutiveCommitsWithNoSizeIncrease++;
                    }
                    //File size stayed equal
                    else
                    {
                        numberOfConsecutiveCommitsWithNoSizeIncrease++;

                        //If file went unchanged for long enough, the heat improved
                        if (numberOfConsecutiveCommitsWithNoSizeIncrease >= REQUIRED_NUM_COMMITS_WITHOUT_CHANGING)
                        {
                            accumulatedHeatLevel += SIZE_NO_CHANGE_HEAT_CONSEQUENCE;
                            numberOfConsecutiveCommitsWithNoSizeIncrease = 0;
                        }
                    }

                    //Now compute the heat based on the changes in size, current file size, and line count.
                    //Then average all measurements.
                    double heatLevel = MathUtility.average(
                            accumulatedHeatLevel,
                            computeFileSizeHeat(newerHeatObject.getFileSize()),
                            computeLineCountHeat(newerHeatObject.getLineCount()));

                    //Store the heat
                    newerHeatObject.setHeatLevel((int) Math.round( heatLevel ));
                }
                else
                {
                    //No in/decreases in file size yet:
                    //Compute the heat based on the current file size & line count, then average all measurements
                    double heatLevel = MathUtility.average(
                            computeFileSizeHeat(newerHeatObject.getFileSize()),
                            computeLineCountHeat(newerHeatObject.getLineCount()));

                    //Store the heat
                    newerHeatObject.setHeatLevel((int) Math.round( heatLevel ));
                }
//if (fileObject.getFilename().equals("HeatMapPane.java"))
//System.out.println("Heat: "+newerHeatObject.getHeatLevel()+"\n");

                lastHeatObject = newerHeatObject;
            }
        }
        System.out.println("Finished calculating heat based on file size.");
    }

    private static int computeFileSizeHeat(long fileSize)
    {
        /*/For every N characters, gain 1 heat
        final int CHARACTER_HEAT_RATIO = 1800;

        //Handle case of file size being too large
        final int CHARACTERS_FOR_MAX_HEAT = CHARACTER_HEAT_RATIO * Constants.HEAT_MAX;
        if (fileSize > CHARACTERS_FOR_MAX_HEAT)
            return Constants.HEAT_MAX;

        System.out.println(""+fileSize+" chars yields "+(fileSize / CHARACTER_HEAT_RATIO)+" heat");

        return (int) (fileSize / CHARACTER_HEAT_RATIO);*/


        if (fileSize < 0)
            throw new IllegalArgumentException("computeFileSizeHeat() refuses to handle negative line count input `" + fileSize + "`");

        //Use exponential function to calculate heat
        final int CHARACTERS_FOR_MAX_HEAT = 10000;
        double fixedPart = Math.pow(Constants.HEAT_MAX, 1.0 / (CHARACTERS_FOR_MAX_HEAT - 1));
        int heatLevel = (int)((1.0 / fixedPart) * Math.pow(fixedPart, fileSize));

        //System.out.println(""+fileSize+" chars yields "+heatLevel+" heat");

        //If the result is too large, constrain it
        if (heatLevel > Constants.HEAT_MAX)
            return Constants.HEAT_MAX;

        return heatLevel;
    }

    /**
     * Returns a heat level that measures how hot (i.e. how large) the lineCount is.
     * The function scales exponentially to guarantee the following:
     * 1 point of heat for 98 lines or fewer (sorry it couldn't be 100 exactly; I'm bad at math).
     * 10 heat (or heat max) for 550 lines or more.
     */
    private static int computeLineCountHeat(long lineCount)
    {
        //Linear technique -- commented out
        /*//For every 100 lines, gain 1 heat
        final int LINE_HEAT_RATIO = 80;

        //Handle case of file size being too large
        final int CHARACTERS_FOR_MAX_HEAT = CHARACTERS_PER_HEAT * Constants.HEAT_MAX;
        if (fileSize > CHARACTERS_FOR_MAX_HEAT)
            return CHARACTERS_FOR_MAX_HEAT;

        return (int) (fileSize / CHARACTERS_PER_HEAT);*/

        if (lineCount < 0)
            throw new IllegalArgumentException("computeLineCountHeat() refuses to handle negative line count input `" + lineCount + "`");

        //Use exponential function to calculate heat
        final int LINES_FOR_MAX_HEAT = 550;
        double fixedPart = Math.pow(Constants.HEAT_MAX, 1.0 / (LINES_FOR_MAX_HEAT - 1));
        int heatLevel = (int)((1.0 / fixedPart) * Math.pow(fixedPart, lineCount));

        //System.out.println(""+lineCount+" lines yields "+heatLevel+" heat");

        //If the result is too large, constrain it
        if (heatLevel > Constants.HEAT_MAX)
            return Constants.HEAT_MAX;

        return heatLevel;
    }



    private static void assignHeatLevelsNumberOfCommits(Codebase codebase)
    {
        System.out.println("Calculating heat based on number of commits...");
        final int REQUIRED_NUM_COMMITS_WITHOUT_CHANGING = 5; //the number of consecutive commits where the file is not modified in order to reduce the accumulated heat level.
        final int COMMIT_HEAT_CONSEQUENCE = 2; //how much the heat increases when the file is modified
        final int COMMIT_ABSENCE_HEAT_CONSEQUENCE = -1; //how much the heat decreases if the file is not modified for enough consecutive commits

        Set<FileObject> fileObjectSet = codebase.getActiveFileObjects();
        for (FileObject fileObject : fileObjectSet)
        {
            //The oldest commits are at the front of the LinkedHashMap
            LinkedHashMap<String, HeatObject> commitHashToHeatObjectMap = fileObject.getCommitHashToHeatObjectMap();

            HeatObject lastHeatObject = null;
            int numberOfConsecutiveCommitsWithNoModify = 0;

            for (Map.Entry<String, HeatObject> commitToHeatObjectEntry : commitHashToHeatObjectMap.entrySet())
            {
                HeatObject newerHeatObject = commitToHeatObjectEntry.getValue();
                if (lastHeatObject != null)
                {
                    newerHeatObject.setHeatLevel(lastHeatObject.getHeatLevel()); //use previous heat, then modify

/*if (fileObject.getFilename().equals("HeatMapPane.java"))
{
    System.out.println("New Hash: "+commitToHeatObjectEntry.getKey());
    System.out.println("oldFileSize: "+oldFileSize);
    System.out.println("newFileSize: "+newFileSize);
}*/

                    //If the file was committed to, incur heat
                    if (newerHeatObject.getNumberOfCommits() > lastHeatObject.getNumberOfCommits())
                    {
                        newerHeatObject.setHeatLevel(newerHeatObject.getHeatLevel() + COMMIT_HEAT_CONSEQUENCE);
                        numberOfConsecutiveCommitsWithNoModify = 0;
                    }
                    //File was not touched in the commit ↓
                    else
                    {
                        numberOfConsecutiveCommitsWithNoModify++;

                        //If file went unchanged for long enough, the heat improved
                        if (numberOfConsecutiveCommitsWithNoModify >= REQUIRED_NUM_COMMITS_WITHOUT_CHANGING)
                        {
                            newerHeatObject.setHeatLevel(newerHeatObject.getHeatLevel() + COMMIT_ABSENCE_HEAT_CONSEQUENCE);
                            numberOfConsecutiveCommitsWithNoModify = 0;
                        }
                    }
                }
                else
                {
                    newerHeatObject.setHeatLevel(Constants.HEAT_MIN); //No commits to the file yet
                }
//if (fileObject.getFilename().equals("HeatMapPane.java"))
                //System.out.println("Heat: "+newerHeatObject.getHeatLevel()+"\n");

                lastHeatObject = newerHeatObject;
            }
        }
        System.out.println("Finished calculating heat based on number of commits.");
    }



    private static void assignHeatLevelsNumberOfAuthors(Codebase codebase)
    {
        /*
        ---- General description of this method ----
         By default, every author implicitly has 0 "points" to represent how active they are.
         Every time an author joins, they are given 10 points.
         (So, an author who just pushes 1 commit will be considered absent from the file after 10 commits)
         For every commit after that, they incur 1 point.
         At the same time, every other author loses 1 point because they did not touch the file.
         Once the author's score reaches 0, they are considered "absent" from the file.
         An absent author can re-gain the 10-point penalty upon rejoining.

         The more a person modifies a file, the more they "own" the file...so it takes longer for them to be considered inactive.
         */

        System.out.println("Calculating heat based on number of authors...");
        final int SCORE_PENALTY_FOR_NEW_AUTHOR = 10; //how many consecutive commits another author must make to a file before a particular author can be considered absent
        final int SCORE_PENALTY_FOR_RETURNING = 1; //how many points an author is given when they return

        //Determine total number of authors
        Set<FileObject> fileObjectSet = codebase.getActiveFileObjects();
        final int totalAuthorCount = countTotalNumberOfAuthors(codebase);

        //Assign heat level to every HeatObject based on number of authors
        for (FileObject fileObject : fileObjectSet)
        {
//System.out.println("\n---------------"+fileObject.getFilename()+"---------------\n");
            HashMap<String, Integer> activeAuthors = new HashMap<>(); //the emails of which authors have been committing to the file recently
            //...and their integer score, which increases based on how many commits they have pushed recently

            //The oldest commits are at the front of the LinkedHashMap
            LinkedHashMap<String, HeatObject> commitHashToHeatObjectMap = fileObject.getCommitHashToHeatObjectMap();
            HeatObject lastHeatObject = null;
            int numberOfActiveAuthors = 0;

            //Look at every commit that the file changed in
            for (Map.Entry<String, HeatObject> commitToHeatObjectEntry : commitHashToHeatObjectMap.entrySet())
            {
                HeatObject newerHeatObject = commitToHeatObjectEntry.getValue();

                //Get the author of the commit
                String commitHash = commitToHeatObjectEntry.getKey();
                String authorEmail = codebase.getCommitFromCommitHash(commitHash).getAuthorEmail();

                //Reuse previous heat value for every HeatObject except the first
                if (lastHeatObject != null)
                {
                    //Ensure the file was a part of the commit
                    if (newerHeatObject.getNumberOfCommits() > lastHeatObject.getNumberOfCommits())
                    {
//System.out.println("Author of "+commitHash+": "+authorEmail);
//System.out.println("activeAuthors = "+activeAuthors);

                        //Returning author
                        if (activeAuthors.containsKey(authorEmail) && activeAuthors.get(authorEmail) > 0)
                        {
                            //Add the following: 1 to mark this current commit
                            //..and 1 to reverse the subtraction step below that affects all authors, including this one.
                            int score = activeAuthors.get(authorEmail) + 1 + SCORE_PENALTY_FOR_RETURNING;
                            activeAuthors.put(authorEmail, score);
                        }
                        //New author -> incur a hefty penalty
                        else
                        {
                            //Add the following: REQUIRED_NUM_COMMITS_WITH_AUTHOR_ABSENCE to mark this current commit
                            //..and 1 to reverse the subtraction step below that affects all authors, including this one.
                            int score = SCORE_PENALTY_FOR_NEW_AUTHOR + 1;
                            activeAuthors.put(authorEmail, score);
                            numberOfActiveAuthors++;
                        }

                        for (Map.Entry<String, Integer> authorEntry : activeAuthors.entrySet())
                        {
                            //Decrement the score of every author to indicate that they have not modified the file in this commit
                            int score = authorEntry.getValue();
                            if (score > 0) //if author is active
                            {
                                score--;

                                //If the value is now 0 (the author is sufficiently inactive) and reduce the heat
                                if (score == 0) {
                                    numberOfActiveAuthors--;
                                }
                            }

                            activeAuthors.put(authorEntry.getKey(), score);
                        }
                    }
                }
                //Account for the first commit
                else if (newerHeatObject.getNumberOfCommits() == 1) {
//System.out.println("First author in "+commitHash+": "+authorEmail);
                    activeAuthors.put(authorEmail, SCORE_PENALTY_FOR_NEW_AUTHOR);
                    numberOfActiveAuthors = 1; //there is 1 active author on the first commit
                }


                //Store the new heat level
                //lastHeatBeforeTransformation = heatLevel;
                int heatLevel = activeAuthorsToHeatLevel(numberOfActiveAuthors, totalAuthorCount);
                newerHeatObject.setHeatLevel(heatLevel);
//System.out.println("Heat level: "+heatLevel);
                lastHeatObject = newerHeatObject;
            }
        }
        System.out.println("Finished calculating heat based on number of authors.");
    }


    /**
     * Transforms numberOfActiveAuthors into a heat level.
     * Currently, this scales linearly based on how close numberOfActiveAuthors is to 4.
     * That is, 4 authors is too many and yields max heat.
     */
    private static int activeAuthorsToHeatLevel(int numberOfActiveAuthors, int totalAuthorCount)
    {
        //Special case for only 1 author in the codebase:
        if (totalAuthorCount == 1)
            return 1; //every file should be the same heat for them

        //Heat should grow exponentially as the numberOfActiveAuthors approaches totalAuthorCount
        /*final int n = totalAuthorCount;
        double base = Math.pow(Constants.HEAT_MAX, 1.0 / (n - 1));
        return (int)((1.0 / base) * Math.pow(base, numberOfActiveAuthors));*/

        final double NUM_AUTHORS_FOR_MAX_HEAT = 4.0; //if a file has this many authors or more, it should have max heat

        if (numberOfActiveAuthors <= 1)
            return 1;
        else if (numberOfActiveAuthors >= NUM_AUTHORS_FOR_MAX_HEAT)
            return Constants.HEAT_MAX;

        return (int)((numberOfActiveAuthors / NUM_AUTHORS_FOR_MAX_HEAT) * Constants.HEAT_MAX);
    }

    public static int countTotalNumberOfAuthors(Codebase codebase)
    {
        Set<FileObject> fileObjectSet = codebase.getActiveFileObjects();
        Set<String> allAuthorSet = new LinkedHashSet<>();
        for (FileObject fileObject : fileObjectSet)
        {
            allAuthorSet.addAll(fileObject.getUniqueAuthors());
        }
        return allAuthorSet.size();
    }


    private static void assignHeatLevelsOverall(Codebase codebase)
    {
        System.out.println("Calculating overall heat...");
        final float WEIGHT_FILE_SIZE = 0.2f;
        final float WEIGHT_NUM_COMMITS_NUM_OF_COMMITS = 0.4f;
        final float WEIGHT_NUM_OF_AUTHORS = 0.4f;
        /**
         * Create a map that records the sum of all heat levels from every metric.
         * After every call to an assignHeatLevels method, we must call sumHeatLevels(...) to record
         * the latest heat levels in this map.
         */
        HashMap<FileObject, HashMap<String, Float>> fileToCommitToHeatSumMap = new HashMap<>();

        assignHeatLevelsFileSize(codebase);
        sumHeatLevels(codebase, fileToCommitToHeatSumMap, WEIGHT_FILE_SIZE);

        assignHeatLevelsNumberOfCommits(codebase);
        sumHeatLevels(codebase, fileToCommitToHeatSumMap, WEIGHT_NUM_COMMITS_NUM_OF_COMMITS);

        assignHeatLevelsNumberOfAuthors(codebase);
        sumHeatLevels(codebase, fileToCommitToHeatSumMap, WEIGHT_NUM_OF_AUTHORS);

        //Add more metrics here if more are needed in the future...

        //Compute and store overall heat
        Set<FileObject> fileObjectSet = codebase.getActiveFileObjects();
        for (FileObject fileObject : fileObjectSet)
        {
            HashMap<String, Float> commitToHeatSumMap = fileToCommitToHeatSumMap.get(fileObject);

            LinkedHashMap<String, HeatObject> commitHashToHeatObjectMap = fileObject.getCommitHashToHeatObjectMap();
            for (Map.Entry<String, HeatObject> commitToHeatObjectEntry : commitHashToHeatObjectMap.entrySet())
            {
                //Retrieve heat sum
                String commitHash = commitToHeatObjectEntry.getKey();
                int heatSum = Math.round(commitToHeatSumMap.get(commitHash));

                //Store result in the HeatObject
                commitToHeatObjectEntry.getValue().setHeatLevel(heatSum);
            }
        }
        System.out.println("Finished calculating overall heat.");
    }

    /**
     * Helper method for assignHeatLevelsOverall that adds the heat levels from every HeatObject to the float
     * part of the given fileToCommitToHeatSumMap. That way, the HeatObjects can have their heatLevels recalculated
     * by other assignHeat methods.
     */
    private static void sumHeatLevels(Codebase codebase, HashMap<FileObject, HashMap<String, Float>> fileToCommitToHeatSumMap, float weight)
    {
        Set<FileObject> fileObjectSet = codebase.getActiveFileObjects();
        for (FileObject fileObject : fileObjectSet)
        {
            //Get or create map of commit hash to heat sum
            HashMap<String, Float> commitToHeatSumMap = fileToCommitToHeatSumMap.computeIfAbsent(fileObject, k -> new HashMap<>());

            //Add the latest heatLevel to the heat sum, then store the sum in the commitToHeatSumMap
            LinkedHashMap<String, HeatObject> commitHashToHeatObjectMap = fileObject.getCommitHashToHeatObjectMap();
            for (Map.Entry<String, HeatObject> commitToHeatObjectEntry : commitHashToHeatObjectMap.entrySet())
            {
                int heatLevel = commitToHeatObjectEntry.getValue().getHeatLevel();
                float weightedHeat = heatLevel * weight;
                String commitHash = commitToHeatObjectEntry.getKey();
                commitToHeatSumMap.merge(commitHash, weightedHeat, Float::sum);
            }
        }
    }

    public static void assignHeatLevels(Codebase codebase, Constants.HeatMetricOptions heatMetricOption)
    {
        switch (heatMetricOption)
        {
            case OVERALL:
                assignHeatLevelsOverall(codebase);
                break;
            case FILE_SIZE:
                assignHeatLevelsFileSize(codebase);
                break;
            case NUM_OF_COMMITS:
                assignHeatLevelsNumberOfCommits(codebase);
                break;
            case NUM_OF_AUTHORS:
                assignHeatLevelsNumberOfAuthors(codebase);
                break;
            default:
                throw new UnsupportedOperationException("Invalid heat metric selected in HeatCalculationUtility.assignHeatLevels(...)");
        }
    }
}
