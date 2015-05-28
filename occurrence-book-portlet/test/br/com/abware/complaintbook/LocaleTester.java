package br.com.abware.complaintbook;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class LocaleTester {

	public static void main(String[] args) throws ParseException {
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss", new Locale("fr", "FR"));
		 String dateTimeValue = "2013-10-25-00.00.00.000000"; 
		 dateTimeValue = dateTimeValue.substring(0, dateTimeValue.lastIndexOf('.'));
         System.out.println(sdf.parse(dateTimeValue));
	}

}
