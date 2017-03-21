/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nisira.core;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import com.nisira.utils.nisiracore.Constantes;
import com.thoughtworks.xstream.XStream;

public final class CoreUtil {

	public static final int ARCHIVO = 0;
	public static final int UNICA = 1;
	public static final int MULTIPLE = 2;

	public static int TIPO_CONEXION = ARCHIVO;

	public static Map<String, EConexion> conexiones = new HashMap<String, EConexion>();

	public static EConexion getValoresBD(int t) {
		if(t==1){
			return conexiones.get("default");
		}else{
			return conexiones.get("sync");
		}
		
	}
	
    public static String serialize_object_to_xml(String clase,Object objecto) throws ClassNotFoundException{
        Class oClase =  Class.forName(clase);
        String xml="<?xml version='1.0' encoding='ISO-8859-1' ?>";
        XStream xStream= new XStream();
        xStream.processAnnotations(oClase);
        return xml + xStream.toXML(objecto);
    }
    public static List<Object> deserialize_xml_to_arraylist(String xml){
    	List<Object> lista=null;
    	XStream xStream= new XStream();
    	lista = (List<Object>) xStream.fromXML(xml);
    	Constantes.log.info(lista);
    	return lista;
    }
    public void crearXML(String clase,Object objecto, String ruta) throws FileNotFoundException, ClassNotFoundException{
        Class oClase =  Class.forName(clase);
        String xml="<?xml version='1.0' encoding='ISO-8859-1' ?>";
        XStream xStream= new XStream();
        xStream.processAnnotations(oClase);
        xStream.toXML(objecto, new FileOutputStream(ruta));
    }
    public static String fechaEspaniol(String fecha){
        fecha=fecha.substring(0,10);
        String anio=fecha.substring(0,4);
        String dia=fecha.substring(8,10);
        return dia+'-'+fecha.substring(5,7)+'-'+anio;
    }
    public static String fechaConvert112(String fecha){
        fecha=fecha.substring(0,10);
        String anio=fecha.substring(0,4);
        String mes=fecha.substring(5,7);
        String dia=fecha.substring(8,10);
        return anio+mes+dia;
    }
}
