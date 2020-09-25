package dsproject1task2;
/*
 * @author Shreya Bhide
 *
 * This is the Servlet of the MVC model
 * This is the starting point for the application and the mediator between the model and the view.
 *
 * The servlet is acting as the controller.
 * There are two views - prompt.jsp and result.jsp.
 */
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@WebServlet(name = "CovidServlet",
        urlPatterns = {"/getCovidServlet"})
public class CovidServlet extends HttpServlet {
    CovidModel cm=null;
    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
        cm = new CovidModel();
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
    // This servlet will reply to HTTP GET requests via this doGet method
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String ua = request.getHeader("User-Agent");
        boolean mobile;
        // prepare the appropriate DOCTYPE for the view pages based on the device type
        if (ua != null && ((ua.indexOf("Android") != -1) || (ua.indexOf("iPhone") != -1))) {
            mobile = true;
            request.setAttribute("doctype", "<!DOCTYPE html PUBLIC \"-//WAPFORUM//DTD XHTML Mobile 1.2//EN\" \"http://www.openmobilealliance.org/tech/DTD/xhtml-mobile12.dtd\">");
        } else {
            mobile = false;
            request.setAttribute("doctype", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
        }
        // get the state input
        String state_whole = request.getParameter("states");
        // get the state code: like pa, tn, ny etc
        String state=state_whole.split(",")[0];
        // get the full name of the state
        String state_name=state_whole.split(",")[1];
        // get the statistic requested by the user
        String stat=request.getParameter("statname");

        // get the start date and manipulate to get the correct format for URL
        String start=request.getParameter("startdate");
        String end=request.getParameter("enddate");
//        System.out.println("START DATE:"+start);
//        System.out.println("START DATE:"+end);
        // remove the '-' and append to a string builder.
        String[] start_arr=start.split("-");
        StringBuilder sb= new StringBuilder();
        sb.append(start_arr[0]);
        sb.append(start_arr[1]);
        sb.append(start_arr[2]);
        // finally construct the date to be appended to the URL.
        String start_date=sb.toString();
        //System.out.println("START DATE:"+start_date);

        // Repeat the process for the end-date
        String[] end_arr=end.split("-");
        StringBuilder sb2= new StringBuilder();
        sb2.append(end_arr[0]);
        sb2.append(end_arr[1]);
        sb2.append(end_arr[2]);
        // Server side code to validate the dates entered.
        String end_date=sb2.toString();
        //System.out.println("END_DATE:"+end_date);
        String dateError= validate(Integer.valueOf(start_arr[0]),Integer.valueOf(start_arr[1]),Integer.valueOf(start_arr[2]),Integer.valueOf(end_arr[0]),Integer.valueOf(end_arr[1]),Integer.valueOf(end_arr[2]));
        // Final constructed URLs
        String url_start= "https://api.covidtracking.com/v1/states/"+state+"/"+start_date+".csv";
        String url_end= "https://api.covidtracking.com/v1/states/"+state+"/"+end_date+".csv";
        String [] ans=cm.process(url_start,url_end,stat);
        // The url of the state flag returned by the scrape function in the model class.
        String flag= cm.scrape(state_name);
        String flagURL=flag.substring(4,flag.length());
        //System.out.println("Flag URL:"+flagURL);
        String nextView;
//        System.out.println("Start Stat:"+ans[0]);
//        System.out.println("End Stat:"+ans[1]);
//        System.out.println("Stat name:"+stat);
        String statistic_name=new String();
        // procesisng for displaying the statistic name as per format
        if(stat.contains("urrently")){
            statistic_name= "Currently Hospitalized";
        }
        else if(stat.contains("umulative")){
            statistic_name= "Cumulative Hospitalized";
        }
        else if(stat.contains("positive")){
            statistic_name= "Positive Cases";
        }
        else if(stat.contains("positive")){
            statistic_name= "Negative Cases";
        }

        // Display date error if all validity checks are not passed
        if(!dateError.equalsIgnoreCase("good")){
            nextView="prompt.jsp";
            request.setAttribute("errorMessage",dateError);
        }
        // if the dates are valid forward the nextview and set attributes of the request.
        else {
            request.setAttribute("statistic",statistic_name);
            request.setAttribute("stateName",state_name);
            request.setAttribute("stateURL",flagURL);
            request.setAttribute("start_stat",ans[0]);
            request.setAttribute("end_stat",ans[1]);
            nextView = "result.jsp";
        }
        // Forward the nextView to the RequestDistpatcher
        RequestDispatcher view = request.getRequestDispatcher(nextView);
        view.forward(request, response);

    }
    /*
    A utility function to validate the dates entered by the user and return error message accordingly.
    @param start date and end date broken down to integers
    @return String status of date validation process
     */
    public String validate(int y1, int m1, int d1, int y2, int m2, int d2){
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        String current_date=formatter.format(date);
        String [] temp=current_date.split(" ");
        String[] curr_date= temp[0].split("/");
        int curr_y=Integer.valueOf(curr_date[2]);
        int curr_m=Integer.valueOf(curr_date[1]);
        int curr_d=Integer.valueOf(curr_date[0]);
        String message=new String("good");
        if(y1>curr_y || y2>curr_y){
            message="The start and end date have to be lesser than the current date.";
        }
        else if((y1==curr_y&&m1>curr_m)||(y2==curr_y&&m2>curr_m)){
            message="The start and end date have to be lesser than the current date.";
        }
        else if((m1==curr_m&&d1>curr_d)||(m2==curr_m&&d2>curr_d)){
            message="The start and end date have to be lesser than the current date.";
        }
        // Making sure that the year is not before 2020
        if(y1<2020){
            message="Do not enter dates before the year 2020!";
            if(m1<3){
                if(d1<1){
                    message="Enter start date before 1st March 2020!";
                }
            }
        }
        // Making sure that the year is not before 2020
        if(y2<2020){
            message="Do not enter dates before the year 2020!";
            if(m2<3){
                if(d2<1){
                    message="Enter end date before 1st March 2020!";
                }
            }
        }
        // End date has to be after start date
        if(m1>m2){
            message="End date has to be after start date!";
        }
        if(m1==m2 && d1>d2){
            message="End date has to be after start date!";
        }
        return message;
    }
}
