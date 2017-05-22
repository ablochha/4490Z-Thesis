package utility;

/**
 * Created by Bloch-Hansen on 2017-05-11.
 */
public class Pair implements Comparable<Pair> {

    public final double index;
    public final double value;

    public Pair(double index, double value) {

        this.index = index;
        this.value = value;

    } //end Pair

    @Override
    public int compareTo(Pair other) {

        //multiplied to -1 as the author need descending sort order
        return -1 * Double.valueOf(this.value).compareTo(other.value);

    } //end compareTo

} //end Pair
