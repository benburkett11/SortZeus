package htmlUnit;
import java.io.File;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;

import jxl.CellView;
import jxl.Workbook;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class WriteToExcel {

	private List<Ticket> tickets;
	private WritableWorkbook workBook;
	private File file;
	private WritableCellFormat timesBoldUnderline;
	private WritableCellFormat times;
	
	public WriteToExcel(String fN, List<Ticket> t) throws WriteException
	{
		//file = new File(fN, "Desktop/TicketReport.xls");
		copyExisting(fN);
		tickets = new ArrayList<Ticket>(t);
		try {
			workBook = Workbook.createWorkbook(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void write() throws WriteException, IOException
	{
		workBook.createSheet("Report", 0);
		WritableSheet excelSheet = workBook.getSheet(0);
		addHeaders(excelSheet);
		addContent(excelSheet);
		
		workBook.write();
		workBook.close();
	}
	
	private void copyExisting(String old)
	{
		Date date;
		file = new File(old);
		if (file.exists())
		{
			date = new Date(file.lastModified());
			Format format = new SimpleDateFormat("yyyy-MM-dd HHmm");
			String sDate = format.format(date);
			File archivedFile = new File("Config/Archive/" + sDate + "_" + old);
			try {
				FileUtils.copyFile(file, archivedFile);
			} catch (IOException e) {
				System.err.println("Error: Unable to copy existing excel file");
				e.printStackTrace();
			}
		}
	}
	
	private void addHeaders(WritableSheet wS) throws WriteException
	{
		WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
	    // Define the cell format
	    times = new WritableCellFormat(times10pt);
	    // Lets automatically wrap the cells
	    times.setWrap(true);

	    // create a bold font with underlines
	    WritableFont times10ptBoldUnderline = new WritableFont(WritableFont.TIMES, 10, WritableFont.BOLD, false,
	        UnderlineStyle.SINGLE);
	    timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
	    // Lets automatically wrap the cells
	    timesBoldUnderline.setWrap(true);

	    CellView cv = new CellView();
	    cv.setFormat(times);
	    cv.setFormat(timesBoldUnderline);
	    cv.setAutosize(true);
	   
	    // Write a few headers
	    Label label;
	    label = new Label(0, 0, "Department", timesBoldUnderline);
	    wS.setColumnView(0, 20);
	    wS.addCell(label);
	    label = new Label(1, 0, "ID", timesBoldUnderline);
	    wS.setColumnView(1, 8);
	    wS.addCell(label);
	    label = new Label(2, 0, "Description", timesBoldUnderline);
	    wS.setColumnView(2, 50);
	    wS.addCell(label);
	    label = new Label(3, 0, "Last Modified on", timesBoldUnderline);
	    wS.setColumnView(3, 30);
	    wS.addCell(label);
	    /*label = new Label(4, 0, "Last Note", timesBoldUnderline);
	    wS.setColumnGroup(4, 8, false);
	    wS.setColumnView(4, 100);
	    wS.addCell(label);*/
	}
	
	private void addContent(WritableSheet wS) throws RowsExceededException, WriteException
	{
		int index = 1;
		for (Ticket t : tickets)
		{
			addLabel(wS, 0, index, t.getService());
			addLabel(wS, 1, index, Integer.toString(t.getID()));
			addLabel(wS, 2, index, t.getDescription());
			addLabel(wS, 3, index, t.getFirstDate().toString());
			//addLabel(wS, 4, index, t.getFirstNote());
			index++;
		}
			
	}
	private void addLabel(WritableSheet sheet, int column, int row, String s) throws RowsExceededException, WriteException
	{
		Label label;
		label = new Label(column, row, s, times);
		sheet.addCell(label);
	}
	
}
