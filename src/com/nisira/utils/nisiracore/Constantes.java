package com.nisira.utils.nisiracore;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Map;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;

public class Constantes {
	public static final String POSTGRESQL = "POSTGRESQL";
	public static final String MSSQL = "MSSQL";
	public static final String SQLITE = "SQLITE";
	public static boolean DEBUG = true;
	public static Logger log = Logger.getLogger(Constantes.class);
	public static void messageLog(String cabecera,String cuerpo){
		System.out.println(cabecera+" : "+cuerpo);
	}
	public static String llenarCerosDigitosTres(Object ob){
		String serie="";
		if(Integer.parseInt(ob.toString())<10)
			serie="00"+ob.toString().trim();
		else if(Integer.parseInt(ob.toString())<100)
			serie="0"+ob.toString().trim();
		else
			serie=ob.toString().trim();
		return serie;
	}
    public static String XmlToString(String clase,Object objecto) throws ClassNotFoundException{
        Class oClase =  Class.forName(clase);
        String xml="<?xml version='1.0' encoding='ISO-8859-1' ?>";
        XStream xStream= new XStream();
        xStream.processAnnotations(oClase);
//        return xml + xStream.toXML(objecto);
        return xml+xStream.toXML(objecto);
    }
    public static void crearXML(String clase,Object objecto, String ruta) throws FileNotFoundException, ClassNotFoundException{
        Class oClase =  Class.forName(clase);
        String xml="<?xml version='1.0' encoding='ISO-8859-1' ?>";
        XStream xStream= new XStream();
        xStream.processAnnotations(oClase);
//        xml = xml+ xStream.toXML(objecto);
        xStream.toXML(objecto, new FileOutputStream(ruta));
    }
    public static Color hex2Rgb(String colorStr) {
    	if(colorStr!=null)
	        return new Color(
	                Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	                Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	                Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
    	else
    		return new Color(255, 255, 255, 80);
    }
    public static String buscarFragmentoTexto(String texto,String charI,String charF,int rep){
    	String fragmento="";
    	String temp=texto;
    	int pos =1,inicio=0,fin=0; 
    	while(pos<rep){
    		/****ANALIZAR******/
    		fin=texto.indexOf(charF);
    		texto=texto.substring(fin+charF.length(), texto.length());
    		pos++;
    	}
    	inicio=texto.indexOf(charI);
		fin=texto.indexOf(charF);
//    	fragmento=texto.replace(charI,"").replace(charF, "");
    	fragmento=texto.substring(inicio+2,fin);
    	System.out.println("Obtenido: "+ fragmento);
    	System.out.println("total: "+ temp);
    	System.out.println("queda : "+ texto);
    	return fragmento;
    }
    public static String claveValorHtml(String clave,String valor){
    	String resp="";
    	resp="<b>"+clave+"</b> : "+valor+"  ";
//    	resp=clave+" = "+valor;
    	return resp;
    }
    public static Object isnull(Object dato,Object propuesto){
    	if(dato==null)
    		return propuesto;
    	return dato;
    }
}
