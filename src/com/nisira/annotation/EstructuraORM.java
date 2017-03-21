package com.nisira.annotation;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

public class EstructuraORM {
	private String nombre;
	@SuppressWarnings("rawtypes")
	private Class entityClass;
	public int maxRegsForSentence;
	protected int maxParams = 2000;
	private List<Pair<Field, EColumna>> campos;
	private List<Pair<Field, EColumna>> listaClavePrimaria;
	//private List<Triplet<Field, ECampoRelacionado, PropiedadesTabla<?>>> listaClaveForanea;
	private List<Pair<Field, ECampoRelacionado>> listaClaveForanea;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public EstructuraORM(Class entityClass) {
		this.entityClass = entityClass;
		init();
		if (entityClass.isAnnotationPresent(Tabla.class)) {
			Tabla t = (Tabla) entityClass.getAnnotation(Tabla.class);
			nombre = t.nombre();
			this.nombre = nombre.isEmpty() ? entityClass.getSimpleName() : nombre;
		}

		maxRegsForSentence = (int) Math.floor(maxParams / (campos.size() + 0.0));

	}

	private void init() {
		campos = new ArrayList<Pair<Field, EColumna>>();
		listaClaveForanea = new ArrayList<Pair<Field, ECampoRelacionado>>();
		// listaClaveForanea = new ArrayList<Triplet<Field, ECampoRelacionado,
		// PropiedadesTabla<?>>>();
		listaClavePrimaria = new ArrayList<Pair<Field, EColumna>>();

		for (Field field : entityClass.getDeclaredFields()) {
			boolean esPK, esFK;
			esPK = field.isAnnotationPresent(ClavePrimaria.class);
			esFK = field.isAnnotationPresent(CampoRelacionado.class);

			// Si es clave foranea
			if (esFK){// && this.primerNivel) {
				CampoRelacionado cr = field.getAnnotation(CampoRelacionado.class);
//				Class cc = field.getType();
				ECampoRelacionado ec = new ECampoRelacionado("campo", cr);

//				PropiedadesTabla<?> psub = new PropiedadesTabla(field.getType(), false);
				
				listaClaveForanea.add(Pair.with(field, ec));
			}

			if (!field.isAnnotationPresent(Columna.class)) {
				continue;
			}

			field.setAccessible(true);

			EColumna eCol = new EColumna(field.getAnnotation(Columna.class));

			eCol.setNombre(eCol.getNombre().isEmpty() ? field.getName() : eCol.getNombre());

			if (esPK) {
				eCol.setPKMetaData(field.getAnnotation(ClavePrimaria.class));
				listaClavePrimaria.add(Pair.with(field, eCol));
			}

			campos.add(Pair.with(field, eCol));

		}
	}

	/**
	 * @return Sentencia Insert para la tabla
	 */

	public String sentenciaInsert(int numeroRegistros) {
		String sql, campo = "", params = "";

		for (Pair<Field, EColumna> c : campos) {
			if (!c.getValue1().isNoInsertar()) {
				campo = campo.concat(campo.isEmpty() ? "" : ", ").concat(c.getValue1().getNombre());
				params = params.concat(params.isEmpty() ? "?" : ", ?");
			}
		}

		params = "select ".concat(params);

		sql = "insert into ".concat(nombre).concat("(").concat(campo).concat(") ");// .concat("
																					// ");
		for (int i = 0; i < numeroRegistros; i++) {
			if (i == 0) {
				sql = sql.concat(params);
			} else {
				sql = sql.concat(" union ").concat(params);
			}
		}

		return sql;
	}

	/**
	 * 
	 * @return Sentencia Update con los parametros en Where según campos de PK
	 */
	public String sentenciaUpdate() {
		String sql, campo = "", where = "";

		for (Pair<Field, EColumna> c : campos) {
			campo = campo.concat(campo.isEmpty() ? "" : ", ").concat(c.getValue1().getNombre()).concat("=?");
			if (c.getValue1().isEsPK()) {
				where = where.concat(where.isEmpty() ? "" : " and ").concat(c.getValue1().getNombre()).concat("=?");
			}
		}

		sql = "update ".concat(nombre).concat(" set ").concat(campo).concat(" where ").concat(where);
		return sql;
	}

	/**
	 * 
	 * @param gestor
	 * @return String for update sentence
	 */
	public String sentenciaInsertUpdate() {
		String sql = "", where = "", update, insert;

		insert = sentenciaInsert(1);
		update = sentenciaUpdate();

		for (Pair<Field, EColumna> c : getCampos()) {
			if (c.getValue1().isEsPK()) {
				where = where.concat(where.isEmpty() ? "" : " and ").concat(c.getValue1().getNombre()).concat("=?");
			}
		}
		// sql = update.concat((gestor.equalsIgnoreCase(Constantes.SQLITE) ? " "
		// : " ;\n"));
		sql = update.concat(";\n");
		sql = sql.concat(insert);
		sql = sql.concat("\nwhere not exists(select 1 from " + getNombre() + " Where " + where + ")");
		return sql;
	}

	/**
	 * 
	 * @return Campos concatenados
	 */
	public String concatenaCampos(String alias) {
		String sql = "";
		for (Pair<Field, EColumna> c : campos) {

			sql = sql.concat(sql.isEmpty() ? "" : ", ").concat(alias.concat(".")).concat(c.getValue1().getNombre());
		}
		return sql;
	}

	public String getNombreAlias(String alias) {
		return nombre + " as " + alias;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Pair<Field, EColumna>> getCampos() {
		return campos;
	}

	public void setCampos(List<Pair<Field, EColumna>> campos) {
		this.campos = campos;
	}

	public int setPrepareStamentParamsPK(PreparedStatement ps, int inicio, Object entidad) throws SQLException {
		int i = -1;
		try {
			for (Pair<Field, EColumna> c : campos) {

				Field f = c.getValue0();

				if (c.getValue1().isEsPK()) {
					i++;
					if (f.getType() == int.class) {// || f.getType() ==
													// Integer.class) {
						ps.setInt(i + inicio, f.getInt(entidad));
						continue;
					}

					if (f.getType() == short.class) {
						ps.setShort(i + inicio, f.getShort(entidad));
						continue;
					}

					if (f.getType() == float.class || f.getType() == Float.class) {
						ps.setFloat(i + inicio, f.getFloat(entidad));
						continue;
					}

					if (f.getType() == double.class || f.getType() == Double.class) {
						ps.setDouble(i + inicio, f.getDouble(entidad));
						continue;
					}

					if (f.getType() == String.class) {
						ps.setString(i + inicio, String.valueOf(f.get(entidad)));
						continue;
					}

					if (f.getType() == Timestamp.class) {
						ps.setTimestamp(i + inicio, (Timestamp) f.get(entidad));
						continue;
					}

					ps.setObject(i + inicio, f.get(entidad));
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return i + 1;
	}

	public int setPrepareStamentParams(PreparedStatement ps, int inicio, Object entidad, boolean incluirTodos)
			throws SQLException {
		int res = 0;
		try {
			for (int i = 0; i < campos.size(); i++) {

				Pair<Field, EColumna> c = campos.get(i);

				Field f = c.getValue0();

				if (incluirTodos || !c.getValue1().isNoInsertar()) {
					res++;
					if (f.getType() == int.class || f.getType() == Integer.class) {

						ps.setObject(i + inicio, (Integer) f.get(entidad));
						continue;
					}

					if (f.getType() == short.class) {
						ps.setObject(res - 1 + inicio, f.getShort(entidad));
						continue;
					}

					if (f.getType() == float.class || f.getType() == Float.class) {
						ps.setObject(res - 1 + inicio, (Float) f.get(entidad));
						continue;
					}

					if (f.getType() == double.class || f.getType() == Double.class) {
						ps.setObject(res - 1 + inicio, (Double) f.get(entidad));
						continue;
					}

					if (f.getType() == String.class) {
						ps.setObject(res - 1 + inicio, (String) f.get(entidad));
						continue;
					}

					if (f.getType() == Timestamp.class) {
						ps.setTimestamp(res - 1 + inicio, (Timestamp) f.get(entidad));
						continue;
					}

					if (f.getType() == Date.class) {

						Date d = (Date) f.get(entidad);
						if (d == null) {
							ps.setTimestamp(res - 1 + inicio, null);
						} else {
							ps.setTimestamp(res - 1 + inicio, new java.sql.Timestamp((d).getTime()));
						}
						continue;
					}

					ps.setObject(res - 1 + inicio, f.get(entidad));
				}
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return res;
	}

	public List<Pair<Field, EColumna>> getListaClavePrimaria() {
		return listaClavePrimaria;
	}

	public void setListaClavePrimaria(List<Pair<Field, EColumna>> listaClavePrimaria) {
		this.listaClavePrimaria = listaClavePrimaria;
	}

	public List<Pair<Field, ECampoRelacionado>> getListaClaveForanea() {
		return listaClaveForanea;
	}

	public void setListaClaveForanea(List<Pair<Field, ECampoRelacionado>> listaClaveForanea) {
		this.listaClaveForanea = listaClaveForanea;
	}
}
