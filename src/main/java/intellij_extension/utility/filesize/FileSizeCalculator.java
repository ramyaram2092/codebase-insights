package intellij_extension.utility.filesize;

import intellij_extension.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.*;
import java.util.stream.Stream;


public class FileSizeCalculator {
    private FileSizeCalculator() {
        //This is a utility class
    }


    /**
     * Unused
     * Gets the line count of a file at the given filePath
     */
    /*public static long getLineCount(String filePath) {
        Stream stream = null;
        try
        {
            // This technique would return the lines of the file in the latest version of the repository
            stream = Files.lines(Paths.get(filePath));
            return stream.count();
        }
        catch (IOException e) {
            System.err.println("There was an error reading file "+filePath);
            stream.close();
            return 0;
        }
    }*/

    /**
     * @param in an InputStream with a File open. This method closes the InputStream when it is done.
     * @return the number of lines in a file or -1 if there was an IOException.
     */
    public static long computeLineCount(InputStream in)
    {
        try
        {
            //Compute number of lines
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            long lineCount = 0;
            while (reader.readLine() != null)
                lineCount++;
            reader.close();

            return lineCount;
        }
        catch (IOException ex)
        {
            Constants.LOG.error(ex);
            return -1;
        }
    }


    /**
     * Unused
     * Counts the number of semi-colons in a file
     * @param in an InputStream with a File open. This method closes the InputStream when it is done.
     * @return the number of semi-colons in a file or -1 if there was an IOException.
     */
    /*public static long computeStatementCount(InputStream in)
    {
        final char CHAR_TO_SEARCH_FOR = ';';
        try
        {
            //Compute number of semi-colons
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            long charCount = 0;
            String line;
            while ((line = reader.readLine()) != null)
            {
                for (int i = 0; i < line.length(); i++)
                {
                    if (line.charAt(i) == CHAR_TO_SEARCH_FOR)
                        charCount++;
                }
            }
            reader.close();

            return charCount;
        }
        catch (IOException ex)
        {
            Constants.LOG.error(ex);
            return -1;
        }
    }*/
}
