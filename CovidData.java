// For Project 1
// Author: Marty Barrett
//
package dsproject1task2;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// class CovidData
// This class doesn't really have anything to do with Covid-19 or data;
//    it simply stores a HashMap of label-value pairs, both as Strings.
//    The constructor takes two String[ ] arrays and uses them to construct
//    the HashMap map with one simple check: use the smaller of the two
//    arrays' lengths. This does not guarantee anything about the actual
//    matchup between label and data, but it seemed good enough for Project 1
public class CovidData {
    // HashMap of label-value pairs
    private Map<String, String> map;

    // Default constructor
    public CovidData() {}

    // Overloaded constructor
    //    Use the smaller array's length, put( ) the label-value pairs into map
    //    No error checking
    public CovidData(String[] labels, String[] values) {
        int max = labels.length < values.length ? labels.length:values.length;
        map = new HashMap<>();
        for (int i = 0; i < max; i++) {
            map.put(labels[i],values[i]);
        }
    }

    // Getter method: returns value associated with label key
    //    No error checking
    public String getItem(String key) {
        String item = map.get(key);
        return item;
    }

}
