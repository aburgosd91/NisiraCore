package com.nisira.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Constants {
	public static String fechaActual(){
		Date date = new Date();
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    String strDate = sdf.format(date);
//	    System.out.println("formatted date in dd/MM/yyyy : " + strDate);
	    return strDate;
	}
	public static String timeActual(){
		Date date = new Date();
		DateFormat hourdateFormat = new SimpleDateFormat("HH:mm a");
//		System.out.println("Hora y fecha: "+hourdateFormat.format(date));
		String strDate = hourdateFormat.format(date);
	    return strDate;
	}
	public static void main(String[] args) {
		System.out.println(fechaActual());
		System.out.println(timeActual());
	}
}
