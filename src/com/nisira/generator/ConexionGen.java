package com.nisira.generator;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.nisira.core.Conexion;
import com.nisira.utils.nisiracore.Constantes;

public class ConexionGen extends Conexion {
	public static String PAQUETEENTIDAD = "com.nisira.entidad";
	public static String PAQUETEDAO = "com.nisira.dao";
	public static String RUTAENTIDAD = "src/com/nisira/entidad/";
	public static String RUTADAO = "src/com/nisira/dao/";
	
	static String estructuraPSG, primarykeysPSG, foreignkeysPSG;

	static {
		estructuraPSG = "select table_name as name_table, column_name as name_column, "
				+ "ordinal_position as colid, data_type as name_type, " + "character_maximum_length as length, "
				+ "numeric_precision as xprec, numeric_scale as xscale, " + "is_nullable as isnullable, "
				+ "'' as text, '' as identidad from INFORMATION_SCHEMA.COLUMNS " + "where table_schema = 'public' ";

		primarykeysPSG = "select tc.table_name as tabla, tc.constraint_name as indice, c.column_name as campo, "
				+ "c.data_type, c.ordinal_position as keyno FROM information_schema.table_constraints tc "
				+ "JOIN information_schema.constraint_column_usage AS ccu USING (constraint_schema, constraint_name) "
				+ "JOIN information_schema.columns AS c ON c.table_schema = tc.constraint_schema AND "
				+ "tc.table_name = c.table_name AND ccu.column_name = c.column_name "
				+ "where constraint_type = 'PRIMARY KEY';";

		foreignkeysPSG = "select c.constraint_name as relacion , x.table_name as ftabla " + ", x.column_name as ftabla "
				+ ", y.table_name as ftabla , y.column_name as fcampo "
				+ "from information_schema.referential_constraints c join information_schema.key_column_usage x "
				+ "on x.constraint_name = c.constraint_name join information_schema.key_column_usage y "
				+ "on y.ordinal_position = x.position_in_unique_constraint "
				+ "and y.constraint_name = c.unique_constraint_name WHERE UPPER(x.table_name) = ? "
				+ "order by c.constraint_name, x.ordinal_position";
	}

	private String gestor;

	public ConexionGen(String gestor) {
		this.gestor = gestor;
	}

	public List<Tabla> retornaEstructura(int con) throws SQLException {

		System.out.println("Obteniendo Estructura de Base de datos actual");
		List<Tabla> lst = new ArrayList<Tabla>();

		Connection cn = obtenerConexion(con);
		CallableStatement cl;

		try {
			if (gestor.equalsIgnoreCase(Constantes.POSTGRESQL)) {
				cl = cn.prepareCall(estructuraPSG);
			} else {
				cl = cn.prepareCall("{CALL returnEstructura (?)}");
				cl.setInt(1, 1);
			}

			ResultSet rs = cl.executeQuery();

			String aTabla = "";
			Tabla tabla = null;

			while (rs.next()) {

				String nom_tabla, nom_columna, tipo, porDefecto;
				int longitud, precision, escala;
				boolean esNulo;

				nom_tabla = rs.getString("name_table");
				nom_columna = rs.getString("name_column");
				tipo = rs.getString("name_type");

				porDefecto = rs.getString("text");
				longitud = rs.getInt("length");
				precision = rs.getInt("xprec");
				escala = rs.getInt("xscale");
				esNulo = (rs.getInt("xscale") == 1) ? true : false;

				if (aTabla.isEmpty() || !aTabla.equalsIgnoreCase(nom_tabla)) {
					tabla = new Tabla();
					lst.add(tabla);
					tabla.setNombre(nom_tabla);
					tabla.setColumnas(new ArrayList<Columna>());
				}
				Columna columna;
				columna = new Columna();
				columna.setNombre(nom_columna);
				columna.setTipo(tipo);
				columna.setEsNulo(esNulo);
				columna.setLongitud(longitud);
				columna.setPrecision(precision);
				columna.setEscala(escala);
				columna.setPorDefecto(porDefecto);

				tabla.getColumnas().add(columna);
				aTabla = nom_tabla;
			}

			// LLenar PKs en PK
			if (gestor.equalsIgnoreCase(Constantes.POSTGRESQL)) {
				cl = cn.prepareCall(primarykeysPSG);
			} else {
				cl = cn.prepareCall("{CALL returnEstructura (?)}");
				cl.setInt(1, 2);
			}

			rs = cl.executeQuery();

			aTabla = "";

			while (rs.next()) {

				String nom_tabla, indice, campo;

				nom_tabla = rs.getString("tabla");
				indice = rs.getString("indice");
				campo = rs.getString("campo");

				if (aTabla.isEmpty() || !aTabla.equalsIgnoreCase(nom_tabla)) {

					for (Tabla t : lst) {
						if (t.getNombre().equalsIgnoreCase(nom_tabla)) {
							t.setClavePrimaria(new ClavePrimaria());
							t.getClavePrimaria().setNombre(indice);
							t.getClavePrimaria().setCampos(new ArrayList<String>());

							tabla = t;
						}

					}
				}
				tabla.getClavePrimaria().getCampos().add(campo);
				aTabla = nom_tabla;
			}

			for (Tabla tab : lst) {
				if (gestor.equalsIgnoreCase(Constantes.POSTGRESQL)) {
					cl = cn.prepareCall(foreignkeysPSG);
					cl.setString(1, tab.getNombre().toUpperCase());
				} else {
					cl = cn.prepareCall("{CALL returnEstructura (?, ?)}");
					cl.setInt(1, 3);
					cl.setString(2, tab.getNombre());
				}

				rs = cl.executeQuery();

				String aFK = "";
				tab.setClavesForaneas(new ArrayList<ClaveForanea>());

				ClaveForanea clave = null;

				while (rs.next()) {
					String fcampo, rtabla, rcampo, relacion;

					relacion = rs.getString("relacion");
					rtabla = rs.getString("rtabla");
					fcampo = rs.getString("fcampo");
					rcampo = rs.getString("rcampo");

					if (aFK.isEmpty() || !aFK.equalsIgnoreCase(relacion)) {
						clave = new ClaveForanea();
						clave.setNombre(relacion);
						clave.setTablaForanea(rtabla);
						clave.setCampos(new ArrayList<String[]>());

						tab.getClavesForaneas().add(clave);
					}
					clave.getCampos().add(new String[] { fcampo, rcampo });

					aFK = relacion;
				}
			}
			System.out.println("Se cargó estructura de Base de datos actual");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lst;
	}

	public void ejecutar(String cadena,int con) {
		if (!cadena.isEmpty()) {
			try {
				Connection cn = obtenerConexion(con);
				PreparedStatement ps;
				ps = cn.prepareStatement(cadena);
				ps.execute();
			} catch (SQLException e) {
				System.out.println(cadena);
				System.out.println(e.getMessage());
				// Main.listaErrores.add(new String [] {cadena,
				// e.getMessage()});
			}
		}
	}
}
