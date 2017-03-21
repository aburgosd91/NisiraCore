package com.nisira.core;

import static com.nisira.utils.nisiracore.Constantes.DEBUG;
import static com.nisira.utils.nisiracore.Constantes.log;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.javatuples.Quintet;
import com.nisira.annotation.EColumna;
import com.nisira.annotation.EstructuraORM;

public class Consulta {
	@SuppressWarnings("rawtypes")
	private List<Quintet<String, Class, EstructuraORM, String, String>> tablas;
	private String where = "";
	private String orderBy = "";
	private Object[] paramsWhere;

	private Connection iConnection;
	// El campo 0 es para la entidad principal

	@SuppressWarnings("rawtypes")
	public Consulta(Class entityClass, String alias) {
		tablas = new ArrayList<Quintet<String, Class, EstructuraORM, String, String>>();
		EstructuraORM es = new EstructuraORM(entityClass);

		tablas.add(Quintet.with(alias, entityClass, es, "", ""));

	}
	
	public EstructuraORM getMainEstructuraORM () {
		Quintet<String, Class, EstructuraORM, String, String> t = tablas.get(0);
		return t.getValue2();
	}

	@SuppressWarnings("rawtypes")
	public Consulta join(String typeJoin, Class entityClass, String alias, String on) {
		EstructuraORM es = new EstructuraORM(entityClass);

		tablas.add(Quintet.with(alias, entityClass, es, on, typeJoin));

		return this;
	}

	public Consulta where(String where, Object... params) {
		this.where = where;
		this.paramsWhere = params;
		return this;
	}

	public Consulta orderBy(String orderBy) {
		this.orderBy = orderBy;
		return this;
	}

	@SuppressWarnings("rawtypes")
	public String getSelectSentence() {
		String campos = "", from = "";

		for (int i = 0; i < tablas.size(); i++) {
			Quintet<String, Class, EstructuraORM, String, String> t = tablas.get(i);
			String alias = t.getValue0();
			String onJoin = t.getValue3();
			String typeJoin = t.getValue4();

			EstructuraORM estructura = t.getValue2();
			campos = campos + (campos.isEmpty() ? "" : "\n,") + estructura.concatenaCampos(alias);
			if (from.isEmpty()) {
				from = estructura.getNombreAlias(alias);
			} else {
				from = from + "\n" + typeJoin + " join " + estructura.getNombreAlias(alias);
			}
			if (!onJoin.isEmpty()) {
				from = from + " on " + onJoin;
			}
		}
		if (!this.where.isEmpty()) {
			from = from + "\nwhere " + this.where;
		}
		if (!this.orderBy.isEmpty()) {
			from = from + "\norder by " + this.orderBy;
		}

		return "select\n" + campos + "\nfrom " + from + ";";
	}
	
	public void mezclar(Connection connection, Object entidad) throws NisiraORMException {
		EstructuraORM est = getMainEstructuraORM ();

		// Mezclar se hace bajo la estructura de la tabla que crea

		String sql = est.sentenciaInsertUpdate();

		if (DEBUG) {
			log.info(sql);
		}

		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new NisiraORMException("Select Exec: " + e.getMessage());
		}

		int insertados = 0;

		try {
			insertados = est.setPrepareStamentParams(ps, 1, entidad, false);
			insertados += est.setPrepareStamentParamsPK(ps, insertados + 1, entidad);
			insertados += est.setPrepareStamentParams(ps, insertados + 1, entidad, false);
			insertados += est.setPrepareStamentParamsPK(ps, insertados + 1, entidad);

		} catch (SQLException e) {
			throw new NisiraORMException("Params: " + e.getMessage());
		}
		try {
			ps.execute();
		} catch (SQLException e) {
			throw new NisiraORMException("Exec: " + e.getMessage());
		}

	}
	
	public void insertar(Connection connection, Object entidad) throws NisiraORMException {
		try {
			EstructuraORM est = getMainEstructuraORM ();

			String sql;
			// Generador de PK
			connection.setAutoCommit(false);
			for (Pair<Field, EColumna> pks : est.getListaClavePrimaria()) {
				Field field = pks.getValue0();
				field.setAccessible(true);

				// if
				// (pks.getValue1().getGenerador().equalsIgnoreCase("i_correlativo"))
				// {
				// String sqlCol = propiedades.sentenciaMaxID(pks.getValue1());
				//
				// PreparedStatement ps = cn.prepareStatement(sqlCol);
				// ResultSet rs = ps.executeQuery();
				// int id = 0;
				// if (rs.next()) {
				// id = rs.getInt(1);
				// }
				// try {
				// field.set(entidad, id + 1);
				// } catch (IllegalArgumentException e) {
				// e.printStackTrace();
				// } catch (IllegalAccessException e) {
				// e.printStackTrace();
				// }
				//
				// }
			}
			sql = est.sentenciaInsert(1);
			if (DEBUG) {
				log.info(sql);
			}

			if (DEBUG) {
				log.info(sql);
			}
			PreparedStatement ps = connection.prepareStatement(sql);
			est.setPrepareStamentParams(ps, 1, entidad, false);

			ps.execute();
			connection.commit();
		} catch (SQLException e) {
			try {
				connection.rollback();
				connection.close();
			} catch (SQLException e1) {
				throw new NisiraORMException(e1.getMessage());
			}
			throw new NisiraORMException(e.getMessage());
		}
	}
	public void insertarTest(Connection connection, List entidades) throws NisiraORMException {
		EstructuraORM est = getMainEstructuraORM ();
		try {
			String sql;
			if (!entidades.isEmpty()) {
				int size = entidades.size();
				int max = est.maxRegsForSentence;
				if (size > est.maxRegsForSentence) {
					List<List> test = new ArrayList<List>();
					for(int i=0;i<size;){
						test.add(entidades.subList(i,max));
						i=max;
						max=max+est.maxRegsForSentence;
						if(max>size){
							max=size;
						}
					}
					for(List l : test){
						insertarTest(connection,l);
					}
				}else {
					sql = est.sentenciaInsert(entidades.size());
					PreparedStatement ps;
					System.out.println("Tamaño Insertar :"+entidades.size());
					ps = connection.prepareStatement(sql);

					int i = 1, insertados = 0;
					for (Object entidad : entidades) {
						insertados = est.setPrepareStamentParams(ps, i, entidad, false);
						i = i + insertados;
					}
					
					ps.execute();
				}
			} 
		} catch (SQLException e) {
			throw new NisiraORMException("Insertar Lista: " + e.getMessage());
		}
	}
	public void insertar(Connection connection, List entidades) throws NisiraORMException {
		EstructuraORM est = getMainEstructuraORM ();
		try {
			String sql;
			if (!entidades.isEmpty()) {
				int size = entidades.size();
				if (size > est.maxRegsForSentence) {
//					int total = (int) Math.ceil(size / (est.maxRegsForSentence * 1.0));/*PARTICIONAR*/
					int total = (int)(size / (est.maxRegsForSentence * 1.0));/*PARTICIONAR*/
					int resto = size-(total*est.maxRegsForSentence);
					System.out.println("Size: "+size);
					System.out.println("Total: "+total);
					System.out.println("resto: "+resto);
					System.out.println("est.maxRegsForSentence: "+est.maxRegsForSentence);
					int subinicio;
					int subfin;
					for (int i = 0; i <=total; i++) {
						subinicio = (i==0?0:i-1) * est.maxRegsForSentence;
						subfin = subinicio + est.maxRegsForSentence;
						System.out.println("["+subinicio+","+subfin+"]");
						insertar(connection,entidades.subList(subinicio, subfin));
//						insertar(connection, entidades.subList(var,
//								(i == total - 1) ? entidades.size() : (i + 1) * est.maxRegsForSentence));
					}
					if(resto>0){
						subinicio=total*est.maxRegsForSentence;
						subfin = subinicio + resto;
						System.out.println("Restante["+subinicio+","+subfin+"]");
						insertar(connection,entidades.subList(subinicio, subfin));
					}
						
				} else {
					sql = est.sentenciaInsert(entidades.size());
					PreparedStatement ps;
					System.out.println("Tamaño Insertar :"+entidades.size());
					ps = connection.prepareStatement(sql);

					int i = 1, insertados = 0;
					for (Object entidad : entidades) {
						insertados = est.setPrepareStamentParams(ps, i, entidad, false);
						i = i + insertados;
					}
					
					ps.execute();
				}
			}
		} catch (SQLException e) {
			throw new NisiraORMException("Insertar Lista: " + e.getMessage());
		}
	}
	
	public void actualizar(Connection connection, Map<String, Object> campos, String where, Object... params)
			throws NisiraORMException {
		EstructuraORM est = getMainEstructuraORM ();

		String sql = "update " + est.getNombre() + " set ";

		Object[] par = new Object[campos.keySet().size() + params.length];

		int c = -1;
		for (String key : campos.keySet()) {
			sql = sql + " " + key + " = ?";
			c++;
			par[c] = campos.get(key);
		}

		for (Object o : params) {
			c++;
			par[c] = o;
		}

		sql = sql.concat(" where ").concat(where);

		PreparedStatement ps;
		try {

			ps = connection.prepareStatement(sql);

			for (int i = 0; i < par.length; i++) {
				ps.setObject(i + 1, par[i]);
			}

			ps.executeUpdate();
		} catch (SQLException e) {
			throw new NisiraORMException("Actualizar: " + e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	public List<EntityTuple> execSelect(Connection connection) throws NisiraORMException {
		List<EntityTuple> resultado = new ArrayList<EntityTuple>();

		String select = getSelectSentence();
		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(select);
		} catch (SQLException e) {
			throw new NisiraORMException("Select Exec: " + e.getMessage());
		}

		if (!this.where.isEmpty()) {
			int indicadorParametro = 0;
			for (Object v : this.paramsWhere) {
				indicadorParametro++;
				try {
					if (v == null) {
						ps.setObject(indicadorParametro, null);
					} else {
						if (v instanceof java.util.Date) {
							ps.setTimestamp(indicadorParametro, new java.sql.Timestamp(((Date) v).getTime()));
						} else {

							ps.setObject(indicadorParametro, v);
						}
					}
				} catch (SQLException e) {
					throw new NisiraORMException("Var asign Select: " + e.getMessage());
				}
			}
		}

		ResultSet rs;
		try {
			rs = ps.executeQuery();
		} catch (SQLException e) {
			throw new NisiraORMException("Excecute Query: " + e.getMessage());
		}
		try {
			while (rs.next()) {
				int column = 0;

				EntityTuple et = new EntityTuple();

				for (int i = 0; i < tablas.size(); i++) {
					column++;

					Quintet<String, Class, EstructuraORM, String, String> tabla = tablas.get(i);
					Class entityClass = tabla.getValue1();
					EstructuraORM estructura = tabla.getValue2();
					Object entidad;
					try {
						entidad = entityClass.newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						throw new NisiraORMException("Create entity: " + e.getMessage());
					}

					column = poblarCampos(estructura, rs, entidad, column);

					et.addEntity(tabla.getValue0(), entidad);
				}

				resultado.add(et);
			}
		} catch (SQLException e) {
			throw new NisiraORMException("Read ResultSet: " + e.getMessage());
		}

		return resultado;
	}

	@SuppressWarnings("rawtypes")
	public List<EntityTuple> execSelect() throws NisiraORMException {
		List<EntityTuple> resultado = new ArrayList<EntityTuple>();

		String select = getSelectSentence();
		PreparedStatement ps;
		try {
			ps = iConnection.prepareStatement(select);
		} catch (SQLException e) {
			throw new NisiraORMException("Select Exec: " + e.getMessage());
		}

		if (!this.where.isEmpty()) {
			int indicadorParametro = 0;
			for (Object v : this.paramsWhere) {
				indicadorParametro++;
				try {
					if (v == null) {
						ps.setObject(indicadorParametro, null);
					} else {
						if (v instanceof java.util.Date) {
							ps.setTimestamp(indicadorParametro, new java.sql.Timestamp(((Date) v).getTime()));
						} else {

							ps.setObject(indicadorParametro, v);
						}
					}
				} catch (SQLException e) {
					throw new NisiraORMException("Var asign Select: " + e.getMessage());
				}
			}
		}

		ResultSet rs;
		try {
			rs = ps.executeQuery();
		} catch (SQLException e) {
			throw new NisiraORMException("Excecute Query: " + e.getMessage());
		}
		try {
			while (rs.next()) {
				int column = 0;

				EntityTuple et = new EntityTuple();

				for (int i = 0; i < tablas.size(); i++) {
					column++;

					Quintet<String, Class, EstructuraORM, String, String> tabla = tablas.get(i);
					Class entityClass = tabla.getValue1();
					EstructuraORM estructura = tabla.getValue2();
					Object entidad;
					try {
						entidad = entityClass.newInstance();
					} catch (InstantiationException | IllegalAccessException e) {
						throw new NisiraORMException("Create entity: " + e.getMessage());
					}

					column = poblarCampos(estructura, rs, entidad, column);

					et.addEntity(tabla.getValue0(), entidad);
				}

				resultado.add(et);
			}
		} catch (SQLException e) {
			throw new NisiraORMException("Read ResultSet: " + e.getMessage());
		}

		return resultado;
	}

	public int poblarCampos(EstructuraORM estructura, ResultSet rs, Object efk, int inicio) throws NisiraORMException {

		try {
			for (int i = inicio; i < inicio + estructura.getCampos().size(); i++) {

				Pair<Field, EColumna> c = estructura.getCampos().get(i - inicio);

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
					Object o = rs.getObject(i);
					if (o == null) {
						f.set(efk, null);						
					} else {
						f.set(efk, new Date(rs.getTimestamp(i).getTime()));
					}
					// f.set(efk, rs.getDate(i));
					continue;
				}
				f.set(efk, rs.getObject(i));
			}
			return inicio + estructura.getCampos().size() - 1;
		} catch (IllegalArgumentException | IllegalAccessException | SQLException e) {
			throw new NisiraORMException("Object asgin " + e.getMessage());
		}
	}

	public void borrar(Connection connection, Object entidad) throws NisiraORMException {
		EstructuraORM est = getMainEstructuraORM();
		
		String sql, where = "";

		for (Pair<Field, EColumna> c : est.getCampos()) {
			if (c.getValue1().isEsPK()) {
				where = where.concat(where.isEmpty() ? "" : " and ").concat(c.getValue1().getNombre()).concat("=?");
			}
		}

		sql = "delete from ".concat(est.getNombre()).concat(" where ").concat(where);

		PreparedStatement ps;
		try {
			ps = connection.prepareStatement(sql);

			int i = -1;

			try {

				for (Pair<Field, EColumna> c : est.getCampos()) {

					Field f = c.getValue0();

					if (c.getValue1().isEsPK()) {
						i++;
						if (f.getType() == int.class || f.getType() == Integer.class) {
							ps.setInt(i + 1, f.getInt(entidad));
							continue;
						}

						if (f.getType() == short.class) {
							ps.setShort(i + 1, f.getShort(entidad));
							continue;
						}

						if (f.getType() == float.class || f.getType() == Float.class) {
							ps.setFloat(i + 1, f.getFloat(entidad));
							continue;
						}

						if (f.getType() == double.class || f.getType() == Double.class) {
							ps.setDouble(i + 1, f.getDouble(entidad));
							continue;
						}

						if (f.getType() == String.class) {
							ps.setString(i + 1, String.valueOf(f.get(entidad)));
							continue;
						}

						if (f.getType() == Timestamp.class) {
							ps.setTimestamp(i + 1, (Timestamp) f.get(entidad));
							continue;
						}

						ps.setObject(i + 1, f.get(entidad));
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}

			ps.execute();

		} catch (SQLException e) {
			throw new NisiraORMException("Borrar: " + e.getMessage());
		}
	}
	
	public void actualizar(Connection connection, Object entidad) throws NisiraORMException {
		EstructuraORM est = getMainEstructuraORM ();

		String sql = est.sentenciaUpdate();

		try {
			PreparedStatement ps;
			ps = connection.prepareStatement(sql);

			int ipk = est.getCampos().size() + 1;
			try {
				for (int i = 0; i < est.getCampos().size(); i++) {
					Pair<Field, EColumna> c = est.getCampos().get(i);
					Field f = c.getValue0();

					if (f.getType() == short.class) {
						ps.setShort(i + 1, f.getShort(entidad));
						if (c.getValue1().isEsPK()) {
							ps.setShort(i + 1, f.getShort(entidad));
							ipk++;
						}
						continue;
					}

					if (f.getType() == String.class) {
						ps.setString(i + 1, String.valueOf(f.get(entidad)));
						if (c.getValue1().isEsPK()) {
							ps.setString(i + 1, String.valueOf(f.get(entidad)));
							ipk++;
						}
						continue;
					}

					if (f.getType() == Timestamp.class) {
						ps.setTimestamp(i + 1, (Timestamp) f.get(entidad));
						if (c.getValue1().isEsPK()) {
							ps.setTimestamp(ipk, (Timestamp) f.get(entidad));
							ipk++;
						}
						continue;
					}
					if (f.getType() == Date.class) {
						Object o = f.get(entidad);
						Timestamp d = null;
						if (o != null) {
							d = new Timestamp(((Date) o).getTime());
						}
						ps.setTimestamp(i + 1, d);
						if (c.getValue1().isEsPK()) {
							ps.setTimestamp(ipk, d);
							ipk++;
						}
						continue;
					}
					
					ps.setObject(i + 1, f.get(entidad));
					if (c.getValue1().isEsPK()) {
						ps.setObject(ipk, f.get(entidad));
						ipk++;
					}
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			ps.execute();

		} catch (SQLException e) {
			throw new NisiraORMException("Actualiza: " + e.getMessage());
		}
	}

	public void borrar(Connection connection, String cwhere, Object... params) throws NisiraORMException {
		try {
			EstructuraORM est = getMainEstructuraORM ();

			int indicadorParametro = 0;
			String sql = "delete from ".concat(est.getNombre());

			sql = sql.concat(" where ".concat(cwhere));

			if (DEBUG) {
				log.info(sql);
			}
			PreparedStatement ps;

			ps = connection.prepareStatement(sql);

			for (Object v : params) {
				indicadorParametro++;
				if (v instanceof java.util.Date) {
					ps.setTimestamp(indicadorParametro, new java.sql.Timestamp(((Date) v).getTime()));
				} else {
					ps.setObject(indicadorParametro, v);
				}
			}

			ps.executeUpdate();

		} catch (SQLException e) {
			throw new NisiraORMException("Borrar String: " + e.getMessage());
		}

	}

	public Connection getiConnection() {
		return iConnection;
	}

	public void setiConnection(Connection iConnection) {
		this.iConnection = iConnection;
	}
	public static void main(String[] args) {
		List<String> lista =new ArrayList<>();
		lista.add("a");//0
		lista.add("e");//1
		lista.add("i");//2
		lista.add("o");//3
		lista.add("u");//4
		
		System.out.println("->"+lista.subList(0, 2).toString());
		System.out.println("->"+lista.subList(2, 4).toString());
		
	}
}
