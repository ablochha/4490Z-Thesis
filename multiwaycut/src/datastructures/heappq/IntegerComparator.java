package datastructures.heappq;

public class IntegerComparator implements Comparator {

    public IntegerComparator(){}
   
    public int compare(Object a, Object b) throws ClassCastException {

	    Integer aComp = (Integer) a;
	    Integer bComp = (Integer) b;
        
	    if ( aComp.intValue() < bComp.intValue() ) {

    	    return(-1);

        } //end if

        else if (aComp.intValue() == bComp.intValue()) {

	        return(0);

        } //end else if

    	else {

    	    return(1);

        } //end else

    } //end compare

} //end IntegerComparator

