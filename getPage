package htmlUnit;
/*
 * 
 * E-mail me at benburkett11@gmail.com if you have any questions
 */
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import jxl.write.WriteException;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebAssert;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

public class GetPage {
	
	private Date todaysDate;
	private SimpleDateFormat sdf;
	private final int MAX_NUM_THREADS = 10;
	private WebClient webClient;
	
	public GetPage()
	{
		getTodaysDate();
		long startTime = System.currentTimeMillis();
		System.out.println("Starting the application...");
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		webClient = new WebClient(BrowserVersion.FIREFOX_24);
	    webClient.getOptions().setJavaScriptEnabled(false);
	    webClient.getOptions().setThrowExceptionOnScriptError(false);
	    
	    System.out.println("---Logging in to Zeus now---");
	    HtmlPage page;
		try {
			
			page = webClient.getPage("Your address here");
			HtmlForm form = (HtmlForm) page.getElementById("Form1"); 
			form.getInputByName("txtUsername").setValueAttribute("****");
			form.getInputByName("txtPassword").setValueAttribute("*****");
			
			List<String> data = new ArrayList<String>();
			List<Ticket> tickets = new ArrayList<Ticket>();
			List<Thread> threads = new ArrayList<Thread>();
			
			HtmlPage homePage =  (HtmlPage) form.getInputByName("btnLogin").click();
	        WebAssert.assertTitleEquals(homePage, "Zeus");
	        
	        HtmlTable incidentTable = homePage.getHtmlElementById("ctl00_mc_wrkOpn_gvQueue");
	        
	        System.out.println("Getting the incidents...");
	        String stringTable = incidentTable.asText();
	        data = getIncidents(stringTable);
	        
	        createTickets(tickets, data);
	        
	        System.out.println("Creating the threads...");
	       for (int x = 0; x < tickets.size(); x++)
	        {
	        	threads.add(new Thread(new PageThread(tickets.get(x), webClient.getCookieManager())));
	        }
	        
	        System.out.println("Getting the notes...");
	        startThreads(tickets, threads);
	        
	        System.out.println("Sorting the tickets...");
	        Collections.sort(tickets, new Comparator<Ticket>() {
	            public int compare(Ticket t1, Ticket t2) {
	                return t1.getFirstDate().compareTo(t2.getFirstDate());
	            }
	        });
	        
	        HtmlPage logOut = webClient.getPage("https://zeus.aisengineering.com/zeus/EndUserSession.aspx?mode=logout");
	        webClient.closeAllWindows();
	        
	        System.out.println("Creating the excel file...");
	        String userFileName= "IncidentReport.xls";
		    WriteToExcel excel;
			try {
				excel = new WriteToExcel(userFileName, tickets);
			    excel.write();
			} catch (WriteException e) {
				System.err.println(sdf.format(todaysDate));
				e.printStackTrace();
			}

		    
		    long endTime = System.currentTimeMillis();
			System.out.println("Scraped zeus in " + (endTime - startTime)/1000 + " seconds!");
		    
		} catch (FailingHttpStatusCodeException e) {
			System.err.println(sdf.format(todaysDate));
			e.printStackTrace();
		} catch (MalformedURLException e) {
			System.err.println(sdf.format(todaysDate));
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println(sdf.format(todaysDate));
			e.printStackTrace();
		}
	}
	
	private void getTodaysDate()
	{
		todaysDate = new Date();
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	}
	
	public void startThreads(List<Ticket> tickets, List<Thread> threads)
	{
		for (int i = 0; i < tickets.size(); i++)
        {
	        threads.get(i).start();
	        if((i) % MAX_NUM_THREADS == 0 && i > 0)
	        {
	        	for (int x = (i - MAX_NUM_THREADS); x <= i; x++)
					try {
						threads.get(x).join();
					} catch (InterruptedException e) {
						System.err.println(sdf.format(todaysDate));
						e.printStackTrace();
					}
	        }
	        else if (i == (threads.size() - 1))
	        {
	        	//take care of the tail end
	        	for ( int x = i - (i % MAX_NUM_THREADS); x <= i; x++)
	        		try {
						threads.get(x).join();
					} catch (InterruptedException e) {
						System.err.println(sdf.format(todaysDate));
						e.printStackTrace();
					}
	        }
        }     //close out the for loop
	}	      //close out the method
	
	public void createTickets(List<Ticket> tickets, List<String> data)
	{
	    Boolean newTicket = false;
        String[] wantedInfo = new String[4];
        
		for (int x = 0; x < data.size(); x++)
        {
			if(data.get(x).equals("Actions"))
				newTicket = true;
			if(newTicket)
			{
				if(data.get(x).equals("Actions") && data.get(x+1).matches("\\d{2,5}"))
				{
					wantedInfo[0] = "N/A";
					wantedInfo[1] = data.get(x+1);
					wantedInfo[2] = data.get(x+2);
					wantedInfo[3] = data.get(x+3);
				}
				else
				{
					wantedInfo[0] = data.get(x+1);
					wantedInfo[1] = data.get(x+2);
					wantedInfo[2] = data.get(x+3);
					wantedInfo[3] = data.get(x+4);
				}
				newTicket = false;
				tickets.add(new Ticket(wantedInfo[0],wantedInfo[1],wantedInfo[2],wantedInfo[3]));
			}
        }
	}
	
	public List<String> getIncidents(String incidents)
	{
		String[] stuff = incidents.split("\t");
        List<String> rawData = new ArrayList<String>();
      
        for (int x = 0; x < stuff.length; x++)
        {
    	    stuff[x] = stuff[x].trim();
    	    if (stuff[x] != null && !stuff[x].isEmpty())
    	    	rawData.add(stuff[x]);
        }
      
        for (int x = 0; x < rawData.size(); x++)
        {
            if (rawData.get(x).contains(",\r\n"))
        	  rawData.set(x, rawData.get(x).replace(",\r\n", ", "));
            //check to see if a string contains two lines, checking the EoL for 2 white space characters
            //and 7 word characters(Actions)
            if (rawData.get(x).matches(".*\\s{2}\\S{7}"))
            {
            	String[] temp = rawData.get(x).split("\\s{2}");
            	for (int i = 0; i < temp.length; i++)
            	{
            		rawData.set(x + i, temp[i].trim());
            	}
            }
        }
        
        for (int x = 0; x <= 15; x++)
        {
        	if (rawData.get(0).equals("Actions"))
        		break;
        	else
        		rawData.remove(0);
        }
        //return the cleaned up version
        return rawData;
	}
	
	public static void main(String[] args)
	{
		new GetPage();
	}
}
