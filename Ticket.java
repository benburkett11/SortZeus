package htmlUnit;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Ticket implements Comparable<Ticket>{
	
	private String service;
	private int ID;
	private String shortDes;
	private String dateAdded;
	private String firstNote;
	private List<String> notesAdded;
	private int numOfNotes;
	private Date firstNoteDate;
	
	
	public Ticket(String s, String id, String sD, String dA)
	{
		service = s;
		ID = Integer.parseInt(id);
		shortDes = sD;
		dateAdded = dA;
	}
	
	public void setInfo(String s, String id, String sD, String dA)
	{
		service = s;
		ID = Integer.parseInt(id);
		shortDes = sD;
		dateAdded = dA;
	}
	
	public void setNoteInfo(List<String> s, int n)
	{
		notesAdded = new ArrayList<String>(s);
		numOfNotes = n;
		setFirstNoteDate();
		setFirstNote();
	}
	
	public void setNoteInfo(String s, int n)
	{
		notesAdded = new ArrayList<String>();
		notesAdded.add(s);
		numOfNotes = n;
		setFirstNoteDate();
		setFirstNote();
	}
	
	private void setFirstNoteDate()
	{
		String temp;
		DateFormat formatter;
		
		if (notesAdded.get(0).equals("No recent notes"))
			temp = dateAdded;
		else
			temp = notesAdded.get(0).substring(notesAdded.get(0).indexOf("- ") + 2);
		
		formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm aa", Locale.ENGLISH);
		try {
			firstNoteDate = formatter.parse(temp);
		} catch (ParseException e) {
			e.printStackTrace();
			firstNoteDate = null;
		}
	}
	
	public int getID()
	{
		return ID;
	}
	
	public int getNumNotes()
	{
		return numOfNotes;
	}
	
	public String getService()
	{
		return service;
	}
	
	public String getDescription()
	{
		return shortDes;
	}
	
	public void setService(String s)
	{
		service = s;
	}
	
	public void setID(int i)
	{
		ID = i;
	}
	
	public void setShortDes(String s)
	{
		shortDes = s;
	}
	
	public void dateAdded(String s)
	{
		dateAdded = s;
	}
	public String getFirstNote()
	{
		return firstNote;
	}
	public void setFirstNote()
	{
		boolean gettingNote = true;
		int index = 4;
		if (!notesAdded.get(0).equals("No recent notes"))
		{
			while(gettingNote)
			{
				if (index == 4)
				{
					firstNote = notesAdded.get(index);
				}
				else
				{
					if(notesAdded.get(index).matches("^(\\d{1,2})"))
					{
						gettingNote = false;
						break;
					}
					else
					{
						if(notesAdded.get(index).contains(" - "))
						{
							String temp = notesAdded.get(index).substring(notesAdded.get(index).indexOf("- ") + 2);
							if (temp.matches("^(\\d{1,2}/?\\d{1,2}/?\\d{4})"));
							{
								gettingNote = false;
								break;
							}
						}
						else
						{
							firstNote += "\n" + notesAdded.get(index);
						}
					}
				}
				index++;
			}
		}
		else
			firstNote = notesAdded.get(0);
			
		
	}
	
	public void getNotes()
	{
		for (String s : notesAdded)
			System.out.println(s);
	}
	
	public Date getFirstDate()
	{
		return firstNoteDate;
	}
	public String toString()
	{
		return service + ", " + ID + ", " + shortDes + ", " + dateAdded + ", " + firstNoteDate;
	}

	@Override
	public int compareTo(Ticket t) {
		return getFirstDate().compareTo(t.getFirstDate());
	}
}
