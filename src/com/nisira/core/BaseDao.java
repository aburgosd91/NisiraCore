/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nisira.core;

import java.lang.reflect.Field;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;

import com.nisira.annotation.ECampoRelacionado;
import com.nisira.annotation.EstructuraORM;
import com.nisira.annotation.PropiedadesTabla;

import static com.nisira.utils.nisiracore.Constantes.log;
import static com.nisira.utils.nisiracore.Constantes.DEBUG;

/**
 *
 * @author jpretel
 * @param <E>
 */

public abstract class BaseDao<E> extends Conexion {

	protected Class<E> entityClass;
	protected Connection cnBase;
	protected boolean usaCnBase;
	protected PropiedadesTabla<E> propiedades;
	
	public Connection getConnection() {
		return cnBase;
	}

	public BaseDao(Class<E> entityClass) {
		this.entityClass = entityClass;
		propiedades = new PropiedadesTabla<E>(this.entityClass);
	}

	public BaseDao(int xs,Class<E> entityClass, boolean usaCnBase) throws NisiraORMException {
		this(entityClass);
		if (usaCnBase) {
			try {
				iniciarConexion(xs);
			} catch (SQLException e) {
				throw new NisiraORMException("Iniciar: " + e.getMessage());
			}
		}
	}

	public Consulta getInstancia(int xs) throws NisiraORMException {
		Consulta c = new Consulta(this.entityClass, "t0");
		if (usaCnBase) {
			c.setiConnection(cnBase);
		} else {
			try {
				c.setiConnection(obtenerConexion(xs));
			} catch (SQLException e) {
				throw new NisiraORMException("GetInstance: " + e.getMessage());
			}
		}

		return c;
	}

	@Override
	protected void finalize() throws Throwable {
		if (usaCnBase) {
			usaCnBase = false;
			cnBase.close();
		}
	};

	public void actualizar(int xs,Map<String, Object> campos, String where, Object... params) throws NisiraORMException {
		Connection cn = null;
		Consulta c = new Consulta(this.entityClass, "t0");

		if (usaCnBase) {
			c.actualizar(cnBase, campos, where, params);
		} else {
			try {
				cn = obtenerConexion(xs);
			} catch (SQLException e) {
				throw new NisiraORMException("Error al crear conexión : " + e.getMessage());
			}
			c.actualizar(cn, campos, where, params);
			try {
				cn.close();
			} catch (SQLException e) {
				throw new NisiraORMException("Error Close: " + e.getMessage());
			}
		}

	}

	public void borrar(int xs,E entidad) throws NisiraORMException {

		Connection cn = null;
		Consulta c = new Consulta(this.entityClass, "t0");

		if (usaCnBase) {
			c.borrar(cnBase, entidad);

		} else {
			try {
				cn = obtenerConexion(xs);
			} catch (SQLException e) {
				throw new NisiraORMException("Error al crear conexión : " + e.getMessage());
			}
			c.borrar(cnBase, entidad);
			try {
				cn.close();
			} catch (SQLException e) {
				throw new NisiraORMException("Error Close: " + e.getMessage());
			}
		}
	}

	public void actualizar(int xs,E entidad) throws NisiraORMException {
		Connection cn = null;
		Consulta c = new Consulta(this.entityClass, "t0");

		if (usaCnBase) {
			c.actualizar(cnBase, entidad);
		} else {
			try {
				cn = obtenerConexion(xs);
			} catch (SQLException e) {
				throw new NisiraORMException("Error al crear conexión : " + e.getMessage());
			}
			c.actualizar(cn, entidad);
			try {
				cn.close();
			} catch (SQLException e) {
				throw new NisiraORMException("Error Close: " + e.getMessage());
			}
		}
	}

	public void mezclar(int xs,E entidad) throws NisiraORMException {

		Connection cn = null;
		Consulta c = new Consulta(this.entityClass, "t0");

		if (usaCnBase) {
			c.mezclar(cnBase, entidad);
		} else {
			try {
				cn = obtenerConexion(xs);
			} catch (SQLException e) {
				throw new NisiraORMException("Error al crear conexión : " + e.getMessage());
			}
			c.mezclar(cn, entidad);
			try {
				cn.close();
			} catch (SQLException e) {
				throw new NisiraORMException("Error Close: " + e.getMessage());
			}
		}
	}

	public void insertar(int xs,E entidad) throws NisiraORMException {

		Connection cn = null;
		Consulta c = new Consulta(this.entityClass, "t0");

		if (usaCnBase) {
			c.insertar(cnBase, entidad);
		} else {
			try {
				cn = obtenerConexion(xs);
			} catch (SQLException e) {
				throw new NisiraORMException("Error al crear conexión : " + e.getMessage());
			}
			c.insertar(cn, entidad);
			try {
				cn.close();
			} catch (SQLException e) {
				throw new NisiraORMException("Error Close: " + e.getMessage());
			}
		}

	}

	public void insertar(int xs,List<E> entidades) throws NisiraORMException {
		Connection cn = null;
		Consulta c = new Consulta(this.entityClass, "t0");

		if (usaCnBase) {
			c.insertar(cnBase, entidades);
		} else {
			try {
				cn = obtenerConexion(xs);
			} catch (SQLException e) {
				throw new NisiraORMException("Error al crear conexión : " + e.getMessage());
			}
			c.insertar(cn, entidades);
			try {
				cn.close();
			} catch (SQLException e) {
				throw new NisiraORMException("Error Close: " + e.getMessage());
			}
		}
	}
	public void insertarTest(int xs,List<E> entidades) throws NisiraORMException {
		Connection cn = null;
		Consulta c = new Consulta(this.entityClass, "t0");

		if (usaCnBase) {
			c.insertarTest(cnBase, entidades);
		} else {
			try {
				cn = obtenerConexion(xs);
			} catch (SQLException e) {
				throw new NisiraORMException("Error al crear conexión : " + e.getMessage());
			}
			c.insertarTest(cn, entidades);
			try {
				cn.close();
			} catch (SQLException e) {
				throw new NisiraORMException("Error Close: " + e.getMessage());
			}
		}
	}
	public void iniciarConexion(int xs) throws SQLException {
		cnBase = obtenerConexion(xs);
		usaCnBase = true;
	}

	public void finalizarConexion() throws SQLException {
		usaCnBase = false;
		cnBase.close();
	}

	public void borrar(int xs,String cwhere, Object... params) throws NisiraORMException {
		Connection cn = null;
		Consulta c = new Consulta(this.entityClass, "t0");

		if (usaCnBase) {
			c.borrar(cnBase, cwhere, params);
		} else {
			try {
				cn = obtenerConexion(xs);
			} catch (SQLException e) {
				throw new NisiraORMException("Error al crear conexión : " + e.getMessage());
			}
			c.borrar(cn, cwhere, params);

			try {
				cn.close();
			} catch (SQLException e) {
				throw new NisiraORMException("Error Close: " + e.getMessage());
			}
		}
	}
	
	public List<E> listar(int xs,String cwhere, Object... params) throws NisiraORMException {
		List<E> lista = new ArrayList<E>();
		lista = listar(xs,false, cwhere, params);

		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<E> listar(int xs,boolean ref, String cwhere, Object... params) throws NisiraORMException {
		List<E> lista = new ArrayList<E>();
		Connection cn = null;
		Consulta c = new Consulta(this.entityClass, "t0");
		EstructuraORM est = c.getMainEstructuraORM();

		if (ref) {
			int i = 0;
			for (Pair<Field, ECampoRelacionado> foreign : est.getListaClaveForanea()) {
				i++;
				String alias = "t" + String.valueOf(i);
				Field f = foreign.getValue0();

				String onquery = "";
				int ii = 0;
				for (Pair<String, String> refer : foreign.getValue1().getReferencia()) {
					if (ii != 0) {
						onquery = onquery.concat(" and ");
					}
					onquery = onquery.concat(" t0.".concat(refer.getValue0())).concat(" = ").concat(alias).concat(".")
							.concat(refer.getValue1());
					ii++;
				}

				c.join("left", f.getType(), alias, onquery);
			}
		}

		if (!cwhere.isEmpty()) {
			c.where(cwhere, params);
		}
		List<EntityTuple> et = null;
		if (usaCnBase) {
			et = c.execSelect(cnBase);

		} else {
			try {
				cn = obtenerConexion(xs);
			} catch (SQLException e) {
				throw new NisiraORMException("Error al crear conexión : " + e.getMessage());
			}
			et = c.execSelect(cn);

			try {
				cn.close();
			} catch (SQLException e) {
				throw new NisiraORMException("Error al crear conexión : " + e.getMessage());
			}
		}

		for (EntityTuple ent : et) {
			Object entidad = ent.get("t0");
			int i = 0;
			if (ref) {
				for (Pair<Field, ECampoRelacionado> foreign : est.getListaClaveForanea()) {
					i++;
					String alias = "t" + String.valueOf(i);
					Object campoRef = ent.get(alias);
					foreign.getValue0().setAccessible(true);
					try {
						foreign.getValue0().set(entidad, campoRef);
					} catch (IllegalArgumentException | IllegalAccessException e) {
						throw new NisiraORMException("Error Listar: " + e.getMessage());
					}

				}
			}
			lista.add((E) entidad);
		}
		return lista;
	}
	
	public List<E> listar(int xs) throws NisiraORMException {
		return listar(xs,"", new Object[] { null });
	}
	
	public List<E> listar(int xs,boolean ref) throws NisiraORMException {
		return listar(xs,ref, "", new Object[] { null });
	}
	
	protected void execUpdateProcedure(EConexion econ, String procedimiento, Object... params) throws SQLException {
		String consulta = "";
		int tamaño = 0;
		if (params != null) {
			tamaño = params.length;
		}

		Connection connection = obtenerConexion(econ);
		if (tamaño == 0) {
			CallableStatement cls = connection.prepareCall("{CALL " + procedimiento + " }");
			cls.executeUpdate();

			// connection.close();
			// cls.close();
		} else {
			for (int i = 0; i < tamaño; i++) {
				consulta += ((consulta.equalsIgnoreCase("")) ? "?" : ", ?");
			}
			consulta = "{CALL " + procedimiento + " (" + consulta + ")}";
			CallableStatement cls = connection.prepareCall(consulta);
			for (int i = 0; i < tamaño; i++) {
				cls.setObject(i + 1, params[i]);
			}
			cls.executeUpdate();
		}
	}

	protected void execUpdateProcedure(int xs,String procedimiento, Object... params) throws SQLException {
		String consulta = "";
		int tamaño = 0;
		if (params != null) {
			tamaño = params.length;
		}

		Connection connection = obtenerConexion(xs);
		if (tamaño == 0) {
			CallableStatement cls = connection.prepareCall("{CALL " + procedimiento + " }");
			cls.executeUpdate();

			// connection.close();
			// cls.close();
		} else {
			for (int i = 0; i < tamaño; i++) {
				consulta += ((consulta.equalsIgnoreCase("")) ? "?" : ", ?");
			}
			consulta = "{CALL " + procedimiento + " (" + consulta + ")}";
			CallableStatement cls = connection.prepareCall(consulta);
			for (int i = 0; i < tamaño; i++) {
				cls.setObject(i + 1, params[i]);
			}
			cls.executeUpdate();
		}
	}

	protected ResultSet execProcedure(int xs,String procedimiento, Object... params) throws NisiraORMException {
		try {
			String consulta = "";
			int tamaño = 0;
			if (params != null) {
				tamaño = params.length;
			}

			Connection connection = obtenerConexion(xs);
			if (tamaño == 0) {
				CallableStatement cls = connection.prepareCall("{CALL " + procedimiento + " }");
				ResultSet resSet = cls.executeQuery();

				// connection.close();
				// cls.close();
				return resSet;
			} else {
				for (int i = 0; i < tamaño; i++) {
					consulta += ((consulta.equalsIgnoreCase("")) ? "?" : ", ?");
				}
				consulta = "{CALL " + procedimiento + " (" + consulta + ")}";
				CallableStatement cls = connection.prepareCall(consulta);
				for (int i = 0; i < tamaño; i++) {
					cls.setObject(i + 1, params[i]);
				}
				ResultSet resSet = cls.executeQuery();
				// connection.close();
				// cls.close();
				return resSet;
			}
		} catch (SQLException e) {
			throw new NisiraORMException("Exec Proc.: " + e.getMessage());
		}
	}

	public ResultSet execFunction(EConexion econ, String function, Object... params) throws NisiraORMException {
		try {
			String consulta = "";
			int tamaño = 0;
			if (params != null) {
				tamaño = params.length;
			}

			Connection connection = obtenerConexion(econ);
			if (tamaño == 0) {
				CallableStatement cls = connection.prepareCall("Select * From " + function + " ()");
				ResultSet resSet = cls.executeQuery();

				// connection.close();
				// cls.close();
				return resSet;
			} else {
				for (int i = 0; i < tamaño; i++) {
					consulta += ((consulta.equalsIgnoreCase("")) ? "?" : ", ?");
				}
				consulta = "Select * From " + function + " (" + consulta + ")";
				CallableStatement cls = connection.prepareCall(consulta);
				for (int i = 0; i < tamaño; i++) {
					cls.setObject(i + 1, params[i]);
				}
				ResultSet resSet = cls.executeQuery();
				// connection.close();
				// cls.close();
				return resSet;
			}
		} catch (SQLException e) {
			throw new NisiraORMException("Exec Funcion: " + e.getMessage());
		}
	}

	public ResultSet execProcedure(EConexion econ, String procedimiento, Object... params) throws NisiraORMException {
		try {
			String consulta = "";
			int tamaño = 0;
			if (params != null) {
				tamaño = params.length;
			}

			Connection connection;
			connection = obtenerConexion(econ);

			if (tamaño == 0) {
				CallableStatement cls = connection.prepareCall("{CALL " + procedimiento + " }");
				ResultSet resSet = cls.executeQuery();

				// connection.close();
				// cls.close();
				return resSet;
			} else {
				for (int i = 0; i < tamaño; i++) {
					consulta += ((consulta.equalsIgnoreCase("")) ? "?" : ", ?");
				}
				consulta = "{CALL " + procedimiento + " (" + consulta + ")}";
				CallableStatement cls = connection.prepareCall(consulta);
				for (int i = 0; i < tamaño; i++) {
					if (params[i] instanceof java.util.Date && params[i] != null) {
						cls.setTimestamp(i + 1, new java.sql.Timestamp(((Date) params[i]).getTime()));
					} else {
						cls.setObject(i + 1, params[i]);
					}

					// cls.setObject(i + 1, params[i]);
				}
				ResultSet resSet = cls.executeQuery();
				// connection.close();
				// cls.close();
				return resSet;
			}

		} catch (SQLException e) {
			throw new NisiraORMException("Exec Proc.:" + e.getMessage());
		}
	}

	public List<Object[]> rsToObjectArray(ResultSet rs) throws SQLException {
		ResultSetMetaData rm = rs.getMetaData();
		int numCols = rm.getColumnCount();

		List<Object[]> lista = new ArrayList<Object[]>();

		while (rs.next()) {
			Object[] reg = new Object[numCols];

			for (int i = 0; i < numCols; i++) {
				reg[i] = rs.getObject(i + 1);
			}
			lista.add(reg);
		}
		return lista;
	}

}
