package com.nisira.utils.nisiracore;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nisira.core.Conexion;
import com.nisira.core.CoreUtil;
import com.nisira.core.EConexion;

public class Utilitarios {
	private static Conexion utlCon = new Conexion();
	
	public static Connection utlCnn;
	
	protected final static int RND_MAX_SIZE = 1048576;
	
	public static void getInstance (int xs) {
		EConexion valores = CoreUtil.getValoresBD(xs);
		getInstance(valores);
	}
	
	public static void getInstance (EConexion valores) {
		try {
			utlCnn = utlCon.obtenerConexion(valores);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static int getID(String tabla) {

		int id = 0;

		tabla = tabla.toUpperCase();

		
		try {
			
			String cadena;

			cadena = " if not exists (select 1 from NsrSequenceID where Tabla = '" + tabla + "') ";
			cadena += " insert into NsrSequenceID (Tabla, Numero) values ('" + tabla + "', 1) ";
			cadena += " else update NsrSequenceID set Numero = Numero + 1 where Tabla = '" + tabla + "'";
			cadena += " select * from NsrSequenceID where tabla = '" + tabla + "'";

			utlCnn.setAutoCommit(false);

			ResultSet rs = utlCnn.prepareStatement(cadena).executeQuery();

			while (rs.next()) {
				id = rs.getInt("Numero");
			}
			utlCnn.commit();
			
			return id;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return id;
	}
}
