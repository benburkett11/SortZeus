package htmlUnit;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.CookieManager;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

public class PageThread implements Runnable{

	private Date todaysDate;
	private SimpleDateFormat sdf;
	private final String TICKET_URL = "Your address here";
	private Ticket ticket;
	private WebClient wb;
	private HtmlPage ticketPage;
	private HtmlTable tNotes;
	private CookieManager cookie;
	private HtmlSpan span;
	private String pageXML;
	private String numOfNotes;
	
	
	public PageThread(Ticket t, CookieManager cm)
	{
		ticket = t;
		cookie = cm;
	}
	
	private void getNotes()
	{
		//check if the page has notes or not.
		if (pageXML.contains("ctl00_mc_t3_ctl00_UcNotes1_lblNoteCount"))
		{
			//Get the number of tickets there are. Use htmlspan to grab the label for the "displaying notes" tag
			span = ticketPage.getHtmlElementById("ctl00_mc_t3_ctl00_UcNotes1_lblNoteCount");
			numOfNotes = span.asText();
			numOfNotes = numOfNotes.substring(numOfNotes.indexOf("of"), numOfNotes.length());
			numOfNotes = numOfNotes.replace('.', ' ');
			numOfNotes = numOfNotes.replace("of", " ");
			numOfNotes = numOfNotes.trim();
			
			//grab the notes
			tNotes = ticketPage.getHtmlElementById("ctl00_mc_t3_ctl00_UcNotes1_dgNotes");
			//we dont need the webclient anymore, we are done with it at this point
			wb.closeAllWindows();
			
			//grab the notes and separate them
			String rawNotes = tNotes.asText();
			String[] notesSeperated = rawNotes.split("\n");
			
			List<String> noteData = new ArrayList<String>();
			for (int x = 0; x < notesSeperated.length; x++)
			{
				notesSeperated[x] = notesSeperated[x].trim();
				if (notesSeperated[x] != null && !notesSeperated[x].isEmpty())
					noteData.add(notesSeperated[x]);
	      	}
			noteData.remove(0);
			ticket.setNoteInfo(noteData, Integer.parseInt(numOfNotes));
		}	
		else
		{
			ticket.setNoteInfo("No recent notes", 0);
	    }
	}
	
	public void getTicketXML()
	{
		pageXML = ticketPage.asXml();
	}
	
	private void getTicketPage()
	{
		try {
			ticketPage = wb.getPage(TICKET_URL + String.valueOf(ticket.getID()));
			pageXML = ticketPage.asXml();
			
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
	
	private void initWebClient()
	{
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		wb = new WebClient(BrowserVersion.FIREFOX_24);
	    wb.getOptions().setJavaScriptEnabled(false);
	    wb.getOptions().setThrowExceptionOnScriptError(false);
	    wb.setCookieManager(cookie);
	}
	
	private void getTodaysDate()
	{
		todaysDate = new Date();
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	}
	
	@Override
	public void run() {
		getTodaysDate();
		initWebClient();
	    getTicketPage();
		getNotes();
	}

}
