/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nisira.core;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.nisira.utils.nisiracore.Constantes;
import static com.nisira.utils.NisiraUtils.isNull;

/**
 *
 */
public class Conexion {

	//public EConexion valoresBD2 = 
	
	
	public Connection obtenerConexion(int y) throws SQLException {
		
		EConexion valores = CoreUtil.getValoresBD(y);
		return obtenerConexion(valores);
	}

	public Connection obtenerConexion(EConexion valores) throws SQLException {
		
		Connection con = null;
		
		switch (valores.TIPO) {
		case Constantes.POSTGRESQL:
			try {
				Class.forName("org.postgresql.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			System.out.println(valores.BASE_DATOS);
			con = DriverManager.getConnection("jdbc:postgresql://" + valores.SERVIDOR + "/" + valores.BASE_DATOS,
					valores.USUARIO, valores.CLAVE);

			break;

		case Constantes.MSSQL:
			try {
				Class.forName("net.sourceforge.jtds.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (isNull(valores.INSTANCIA, "").equalsIgnoreCase("")) {
				con = DriverManager.getConnection("jdbc:jtds:sqlserver://" + valores.SERVIDOR + ":1433/"
						+ valores.BASE_DATOS + ";user=" + valores.USUARIO + ";password=" + valores.CLAVE + ";");

			} else {
				String url = "jdbc:jtds:sqlserver://" + valores.SERVIDOR + ";instance=" + valores.INSTANCIA
						+ ";DatabaseName=" + valores.BASE_DATOS;
				con = DriverManager.getConnection(url, valores.USUARIO, valores.CLAVE);
			}
			break;
		case Constantes.SQLITE:
			try {
				Class.forName("org.sqlite.JDBC");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		
			con = DriverManager.getConnection("jdbc:sqlite:" + valores.SERVIDOR);
			break;
		}

		return con;
	}

	protected void cerrar(Connection connection) throws Exception {
		if (connection != null) {
			connection.close();
			connection = null;
		}
	}

	protected void cerrar(Connection connection, PreparedStatement preparedStatement) throws Exception {
		if (connection != null) {
			connection.close();
			connection = null;
		}
		if (preparedStatement != null) {
			preparedStatement.close();
			preparedStatement = null;
		}
	}

	protected void cerrar(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet)
			throws Exception {
		if (connection != null) {
			connection.close();
			connection = null;
		}
		if (preparedStatement != null) {
			preparedStatement.close();
			preparedStatement = null;
		}
		if (resultSet != null) {
			resultSet.close();
			resultSet = null;
		}
	}

	protected void cerrar(Connection connection, CallableStatement callableStatement) throws Exception {
		if (connection != null) {
			connection.close();
			connection = null;
		}
		if (callableStatement != null) {
			callableStatement.close();
			callableStatement = null;
		}

	}

	protected void cerrar(Connection connection, CallableStatement callableStatement, ResultSet resultSet)
			throws Exception {
		if (connection != null) {
			connection.close();
			connection = null;
		}
		if (callableStatement != null) {
			callableStatement.close();
			callableStatement = null;
		}
		if (resultSet != null) {
			resultSet.close();
			resultSet = null;
		}
	}

	protected void cerrar(ResultSet resultSet) throws Exception {
		if (resultSet != null) {
			resultSet.close();
			resultSet = null;
		}
	}

	protected void cerrar(PreparedStatement preparedStatement) throws Exception {
		if (preparedStatement != null) {
			preparedStatement.close();
			preparedStatement = null;
		}
	}

	protected void cerrar(CallableStatement callableStatement) throws Exception {
		if (callableStatement != null) {
			callableStatement.close();
			callableStatement = null;
		}
	}

	protected void rollback(Connection cn) {
		try {
			cn.rollback();
		} catch (Exception e) {
		}
	}
}
