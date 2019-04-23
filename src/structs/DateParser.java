package structs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateParser {

	public static String parseLogDate(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("[yyyy-mm-dd_hh:mm:ss]");
		return dateFormat.format(date);
	}
}
