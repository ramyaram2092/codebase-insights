package intellij_extension.models.redesign;

import intellij_extension.Constants;

/**
 * Records a file's metrics and its heat level for the state
 * of that file at a particular Git commit.
 * filename uniquely identifies the file.
 */
public class HeatObject {

    private int heatLevel;
    private String filename;
    private long lineCount;
    private long fileSize;
    private int numberOfCommits;
    private int numberOfAuthors;

    public HeatObject() {
        //This allows the metrics to be filled out gradually
        heatLevel = Constants.HEAT_MIN;
        filename = "";
        lineCount = -1;
        fileSize = -1;
        numberOfCommits = -1;
        numberOfAuthors = -1;
    }

    public HeatObject(int heatLevel, String filename, long lineCount, long fileSize, int numberOfCommits, int numberOfAuthors) {
        this.heatLevel = heatLevel;
        constrainHeatLevel();
        this.filename = filename;
        this.lineCount = lineCount;
        this.fileSize = fileSize;
        this.numberOfCommits = numberOfCommits;
        this.numberOfAuthors = numberOfAuthors;
    }

    public int getHeatLevel() {
        return heatLevel;
    }

    public void setHeatLevel(int heatLevel) {
        this.heatLevel = heatLevel;
        constrainHeatLevel();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public long getLineCount() {
        return lineCount;
    }

    public void setLineCount(long lineCount) {
        this.lineCount = lineCount;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getNumberOfCommits() {
        return numberOfCommits;
    }

    public void setNumberOfCommits(int numberOfCommits) {
        this.numberOfCommits = numberOfCommits;
    }

    public int getNumberOfAuthors() {
        return numberOfAuthors;
    }

    public void setNumberOfAuthors(int numberOfAuthors) {
        this.numberOfAuthors = numberOfAuthors;
    }

    public void constrainHeatLevel() {
        if (this.heatLevel < Constants.HEAT_MIN)
            this.heatLevel = Constants.HEAT_MIN;
        else if (this.heatLevel > Constants.HEAT_MAX)
            this.heatLevel = Constants.HEAT_MAX;
    }
}
