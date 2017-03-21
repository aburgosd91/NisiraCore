package com.nisira.annotation;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.javatuples.Pair;
import org.javatuples.Triplet;

public class PropiedadesTabla<E> {
	private String nombre;
	protected Class<E> entityClass;
	private boolean primerNivel;
	private List<Pair<Field, EColumna>> campos;
	private List<Pair<Field, EColumna>> listaClavePrimaria;
	private List<Triplet<Field, ECampoRelacionado, PropiedadesTabla<?>>> listaClaveForanea;

	public PropiedadesTabla(Class<E> entityClass) {
		this.entityClass = entityClass;
		primerNivel = true;
		init();
		if (entityClass.isAnnotationPresent(Tabla.class)) {

			nombre = entityClass.getAnnotation(Tabla.class).nombre();
			this.nombre = nombre.isEmpty() ? entityClass.getSimpleName() : nombre;
		}
	}

	public PropiedadesTabla(Class<E> entityClass, boolean primerNivel) {
		this.entityClass = entityClass;
		this.primerNivel = primerNivel;
		init();
		nombre = entityClass.getAnnotation(Tabla.class).nombre();
		this.nombre = nombre.isEmpty() ? entityClass.getSimpleName() : nombre;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void init() {

		campos = new ArrayList<Pair<Field, EColumna>>();
		listaClaveForanea = new ArrayList<Triplet<Field, ECampoRelacionado, PropiedadesTabla<?>>>();
		listaClavePrimaria = new ArrayList<Pair<Field, EColumna>>();

		for (Field field : entityClass.getDeclaredFields()) {
			boolean esPK, esFK;
			esPK = field.isAnnotationPresent(ClavePrimaria.class);
			esFK = field.isAnnotationPresent(CampoRelacionado.class);

			// Si es clave foranea
			if (esFK && this.primerNivel) {
				CampoRelacionado cr = field.getAnnotation(CampoRelacionado.class);

				PropiedadesTabla<?> psub = new PropiedadesTabla(field.getType(), false);
				ECampoRelacionado ec = new ECampoRelacionado(psub.getNombre(), cr);

				listaClaveForanea.add(Triplet.with(field, ec, psub));
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

	public String sentenciaMaxID(EColumna ecolumna) {
		String sql;
		sql = "select max(".concat(ecolumna.getNombre()).concat(") as maxid from ").concat(nombre);
		return sql;
	}

	public String concatenaCampos(String alias) {
		String sql = "";

		for (Pair<Field, EColumna> c : getCampos()) {

			sql = sql.concat(sql.isEmpty() ? "" : ", ").concat(alias.concat(".")).concat(c.getValue1().getNombre());
		}
		return sql;
	}

	public void poblarCampos(ResultSet rs, Object efk, int inicio) throws SQLException {

		try {
			for (int i = inicio; i < inicio + campos.size(); i++) {

				Pair<Field, EColumna> c = getCampos().get(i - inicio);

				Field f = c.getValue0();

				if (f.getType() == int.class || f.getType() == Integer.class) {
					f.set(efk, rs.getInt(i));
					continue;
				}

				if (f.getType() == short.class || f.getType() == Short.class) {
					short vShort = rs.getShort(i);
					f.set(efk, vShort);
					continue;
				}

				if (f.getType() == float.class || f.getType() == Float.class) {
					f.set(efk, rs.getFloat(i));
					continue;
				}

				if (f.getType() == double.class || f.getType() == Double.class) {
					f.set(efk, rs.getDouble(i));
					continue;
				}

				if (f.getType() == String.class) {
					f.set(efk, rs.getString(i));
					continue;
				}

				if (f.getType() == Timestamp.class) {
					f.set(efk, rs.getTimestamp(i));
					continue;
				}

				if (f.getType() == java.util.Date.class) {
					f.set(efk, rs.getTimestamp(i));
					//f.set(efk, rs.getDate(i));
					continue;
				}
				f.set(efk, rs.getObject(i));
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

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
																					// values
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

	public int setPrepareStamentParamsPK(PreparedStatement ps, int inicio, E entidad) throws SQLException {
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

	public int setPrepareStamentParams(PreparedStatement ps, int inicio, E entidad, boolean incluirTodos)
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

	/**
	 * 
	 * @param retornaRef
	 *            : true=retorna la clase y sus referencias (FK), false sólo
	 *            retorna los campos de la clase.
	 * @return Sentencia SQL con o sin campos de referencia según retornaRef
	 */
	public String sentenciaSelect(boolean retornaRef) {
		String sql = concatenaCampos("t0");
		String joins = " ";

		int ia = 0;
		if (retornaRef) {
			for (Triplet<Field, ECampoRelacionado, PropiedadesTabla<?>> fk : getListaClaveForanea()) {
				ia++;
				String alias = " t" + ia;
				String camposref = fk.getValue2().concatenaCampos(alias);
				sql = sql.concat(", ").concat(camposref);

				joins = joins.concat("\nleft join ").concat(fk.getValue2().getNombre()).concat(" ").concat(alias)
						.concat(" on ");
				int ii = 0;
				for (Pair<String, String> refer : fk.getValue1().getReferencia()) {
					if (ii != 0) {
						joins = joins.concat(" and ");
					}
					joins = joins.concat(" t0.".concat(refer.getValue0())).concat(" = ").concat(alias).concat(".")
							.concat(refer.getValue1());
					ii++;
				}
			}
		}

		sql = "select ".concat(sql.concat(" from ").concat(getNombre()).concat(" as t0")).concat(joins);

		return sql;
	}

	/**
	 * 
	 * @return Sentencia Update con los parametros en Where según campos de PK
	 */
	public String sentenciaUpdate() {
		String sql, campo = "", where = "";

		for (Pair<Field, EColumna> c : getCampos()) {
			campo = campo.concat(campo.isEmpty() ? "" : ", ").concat(c.getValue1().getNombre()).concat("=?");
			if (c.getValue1().isEsPK()) {
				where = where.concat(where.isEmpty() ? "" : " and ").concat(c.getValue1().getNombre()).concat("=?");
			}
		}

		sql = "update ".concat(getNombre()).concat(" set ").concat(campo).concat(" where ").concat(where);
		return sql;
	}

	/**
	 * 
	 * @return Sentencia Insert/Update con los parametros en Where según campos
	 *         de PK
	 * 
	 *         public String sentenciaInsertUpdate() { String sql = "", where =
	 *         "", update, insert;
	 * 
	 *         insert = sentenciaInsert(1); update = sentenciaUpdate();
	 * 
	 *         for (Pair<Field, EColumna> c : getCampos()) { if
	 *         (c.getValue1().isEsPK()) { where = where.concat(where.isEmpty() ?
	 *         "" : " and ").concat(c.getValue1().getNombre()).concat("=?"); } }
	 * 
	 *         sql = "if exists (Select 1 From " + getNombre() + " Where " +
	 *         where + ")\n"; sql = sql.concat(update); sql =
	 *         sql.concat("\nelse\n"); sql = sql.concat(insert); return sql; }
	 */
	public String sentenciaInsertUpdate(String gestor) {
		String sql = "", where = "", update, insert;

		insert = sentenciaInsert(1);
		update = sentenciaUpdate();

		for (Pair<Field, EColumna> c : getCampos()) {
			if (c.getValue1().isEsPK()) {
				where = where.concat(where.isEmpty() ? "" : " and ").concat(c.getValue1().getNombre()).concat("=?");
			}
		}
		//sql = update.concat((gestor.equalsIgnoreCase(Constantes.SQLITE) ? " " : " ;\n"));
		sql = update.concat(";\n");
		sql = sql.concat(insert);
		sql = sql.concat("\nwhere not exists(select 1 from " + getNombre() + " Where " + where + ")");
		return sql;
	}
	
	public boolean existeCampo(String busqueda) {

		for (Pair<Field, EColumna> p : campos) {
			if (p.getValue0().getName().equals(busqueda)) {
				return true;
			}
		}
		return false;
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

	public boolean isPrimerNivel() {
		return primerNivel;
	}

	public void setPrimerNivel(boolean primerNivel) {
		this.primerNivel = primerNivel;
	}

	public void setCampos(List<Pair<Field, EColumna>> campos) {
		this.campos = campos;
	}

	public List<Triplet<Field, ECampoRelacionado, PropiedadesTabla<?>>> getListaClaveForanea() {
		return listaClaveForanea;
	}

	public void setListaClaveForanea(List<Triplet<Field, ECampoRelacionado, PropiedadesTabla<?>>> listaClaveForanea) {
		this.listaClaveForanea = listaClaveForanea;
	}

	public List<Pair<Field, EColumna>> getListaClavePrimaria() {
		return listaClavePrimaria;
	}

	public void setListaClavePrimaria(List<Pair<Field, EColumna>> listaClavePrimaria) {
		this.listaClavePrimaria = listaClavePrimaria;
	}

	public Class<E> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<E> entityClass) {
		this.entityClass = entityClass;
	}
}