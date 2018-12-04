package querying;

import indexing.SearchableObject;

import java.util.Comparator;

public class SearchableObjectComparator implements Comparator<SearchableObject> {

    private SearchableObject queryObject;

    public SearchableObjectComparator(SearchableObject queryObject) {
        this.queryObject = queryObject;
    }

    public int compare(SearchableObject so1, SearchableObject so2) {
        double distanceDifference =  so1.distanceTo(this.queryObject) - so2.distanceTo(this.queryObject);
        if (distanceDifference < 0) {
            return -1;
        } else if (distanceDifference > 0) {
            return 1;
        } else {
            return 0;
        }
    }

}
