package dsproject1task2;
/*
 * @author Shreya Bhide
 * This is the Model of the MVC model
 * This is the underlying logic where the covid API is called and the web scraping is done to find the image.
 */
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class CovidModel
{
    /*
    A function to call the covid api and get the statistics
    @param URL for Start date, URL for end date, String statistic
    @ String[] the results for the statistic
     */
    public String[] process(String url_start, String url_end, String stat)
    {
        String[] arr=new String[2];
        try {

            // get the statistic for the first URL
            URL url_1 = new URL(url_start);
            Scanner s = new Scanner(url_1.openStream());
            String labels_raw_1=s.nextLine();
            String data_raw_1=s.nextLine();
            System.out.println("In Model!!!!!!");
            System.out.println("Labels:"+labels_raw_1);
            System.out.println("Data:"+data_raw_1);
            String[] labels_1=labels_raw_1.split(",");
            String [] data_1=data_raw_1.split(",");
            CovidData cd=new CovidData(labels_1,data_1);
            String result_1=cd.getItem(stat);
            System.out.println(result_1);
            // start date statistic
            arr[0]=result_1;

            // get the statistic for the second URL
            URL url_2 = new URL(url_end);
            Scanner s2 = new Scanner(url_2.openStream());
            String labels_raw_2=s2.nextLine();
            String data_raw_2=s2.nextLine();
            System.out.println("In Model!!!!!!");
            System.out.println("Labels:"+labels_raw_2);
            System.out.println("Data:"+data_raw_2);
            String[] labels_2=labels_raw_2.split(",");
            String [] data_2=data_raw_2.split(",");
            CovidData cd2=new CovidData(labels_2,data_2);
            String result_2=cd2.getItem(stat);
            System.out.println(result_2);
            // end date statistic
            arr[1]=result_2;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return arr;
    }

    /*
    A function to scrape the states symbol web page and return the URL of the appropriate flag.
    @param Name of the State
    @return String URL of the flag
    */
    public String scrape(String State){
        String url="https://statesymbolsusa.org/categories/state-flag";
        String response=fetch(url);
        int idx=response.indexOf(">Flag of "+State);
        // System.out.println(response.substring(idx-500,idx));
        System.out.println("Idx:"+idx);
        String temp=response.substring(idx-700,idx);
        int idx2=temp.indexOf("src=");
        int idx3=temp.indexOf("width=");
        System.out.println("Idx2:"+idx2);
        System.out.println("Idx3:"+idx3);
        String flagURL=temp.substring(idx2,idx3);
        System.out.println("URL of Flag:"+flagURL);

        return flagURL;
    }
    /*
    A function to fetch the entire HTML of the web page to be available for web scraping.
    @param String URL of the web page to scrape
    @return The entire HTMl returned as a string
     */
    public String fetch(String urlString){
        String response = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                // str is one line of text readLine() strips newline characters
                response += str;
            }
            in.close();
        } catch (IOException e) {
            System.out.println("Eeek, an exception");
        }
        return response;
    }
}
