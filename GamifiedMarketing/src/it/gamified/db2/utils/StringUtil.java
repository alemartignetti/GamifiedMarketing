package it.gamified.db2.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringUtil {

	public static Date handleFormat(String date) {
		
		System.out.println("date:"+date);
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dateProduct = (Date) sdf.parse(date);
			return dateProduct;
		}
		catch (NullPointerException | ParseException pe) {
			
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-dd-MM");
			Date dateProduct = (Date) sdf.parse(date);
			return dateProduct;
		}
		catch (NullPointerException | ParseException pe) {
			
		}
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			Date dateProduct = (Date) sdf.parse(date);
			return dateProduct;
		}
		catch (NullPointerException | ParseException pe) {
			return null;
		}
	}
}
