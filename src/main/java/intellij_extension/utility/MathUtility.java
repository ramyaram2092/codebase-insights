package intellij_extension.utility;

public class MathUtility
{
    /**
     * Takes the average of any given sequence of integers.
     * That's it.
     */
    public static double average(int... numbersToAverage)
    {
        if (numbersToAverage.length <= 0)
            throw new IllegalArgumentException("average(...) requires at least one integer argument");

        double sum = 0;
        for (int arg : numbersToAverage) {
            sum += arg;
        }
        return sum / numbersToAverage.length;
    }
}
