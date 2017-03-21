package com.nisira.generator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.nisira.utils.nisiracore.Constantes;

public class Columna extends ObjetoSQL implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7079843160316349467L;

	private String tipo;
	private int longitud;
	private int precision;
	private int escala;
	private boolean esNulo;
	private String porDefecto;
	private String identidad;

	private static Map<String, String> postgresDataType = new HashMap<String, String>();

	private static Map<String, String> mssqlDataType = new HashMap<String, String>();

	private static Map<String, String> lPresescala = new HashMap<String, String>();
	private static Map<String, String> lLongitud = new HashMap<String, String>();

	static {
		postgresDataType.put("int", "integer");
		postgresDataType.put("nvarchar", "varchar");
		postgresDataType.put("nchar", "char");
		postgresDataType.put("ntext", "text");
		postgresDataType.put("datetime", "timestamp");

		lPresescala.put("numeric", "1");

		lLongitud.put("nvarchar", "1");
		lLongitud.put("varchar", "1");
		lLongitud.put("nchar", "1");
		lLongitud.put("char", "1");
	}

	public static String getDataType(String gestor, String tipo) {
		String r = null;

		if (gestor.equalsIgnoreCase(Constantes.POSTGRESQL)) {
			r = postgresDataType.get(tipo);
		}

		if (gestor.equalsIgnoreCase(Constantes.MSSQL)) {
			r = mssqlDataType.get(tipo);
		}

		if (r == null) {
			r = tipo;
		}

		return r;
	}

	public Columna() {
		this.porDefecto = "";
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public int getLongitud() {
		return longitud;
	}

	public void setLongitud(int longitud) {
		this.longitud = longitud;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public int getEscala() {
		return escala;
	}

	public void setEscala(int escala) {
		this.escala = escala;
	}

	public boolean isEsNulo() {
		return esNulo;
	}

	public void setEsNulo(boolean esNulo) {
		this.esNulo = esNulo;
	}

	public String getPorDefecto() {
		return porDefecto;
	}

	public void setPorDefecto(String porDefecto) {
		this.porDefecto = porDefecto;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getIdentidad() {
		return identidad;
	}

	public void setIdentidad(String identidad) {
		this.identidad = identidad;
	}

	public String getDefinicio(String gestor) {
		this.porDefecto = (this.porDefecto == null) ? "" : this.porDefecto;
		this.identidad = (this.identidad == null) ? "" : this.identidad;
		return getSubDefinicio(gestor, false) + (this.isEsNulo() ? " NULL " : " NOT NULL ")
				+ (this.identidad.isEmpty() ? "" : " identity (" + this.identidad + ")")
				+ (this.porDefecto.isEmpty() ? "" : "Default " + this.porDefecto);
	}

	// public String getDefinicioAlter(String gestor) {
	// this.porDefecto = (this.porDefecto == null) ? "" : this.porDefecto;
	// return getSubDefinicio(gestor);
	// }

	public String getDefinicioModificar(String gestor) {
		this.porDefecto = (this.porDefecto == null) ? "" : this.porDefecto;
		return getSubDefinicio(gestor, true) + ((this.porDefecto.isEmpty()) ? ""
				: (this.isEsNulo() ? " NULL " : " NOT NULL ") + "Default " + this.porDefecto);
	}

	public String getSubDefinicio(String gestor, boolean modifica) {

		String tipogestor = getDataType(gestor, tipo);
		String union = " ";

		if (modifica && gestor.equalsIgnoreCase(Constantes.POSTGRESQL)) {
			union = " type ";
		}

		if (lPresescala.get(tipogestor) != null) {
			return getNombre() + union + tipogestor + "(" + precision + "," + escala + ")";
		}

		if (lLongitud.get(tipogestor) != null) {
			return getNombre() + union + tipogestor + "(" + ((longitud == -1) ? "max" : longitud) + ")";
		}

		return getNombre() + union + tipogestor;

	}

	public boolean igualEstructura(Columna c) {

		if (this.getNombre().equalsIgnoreCase(c.getNombre()) && this.esNulo == c.isEsNulo()
				&& this.longitud == c.getLongitud() && this.precision == c.getPrecision()
				&& this.escala == c.getEscala()) {
			return true;
		}

		return false;
	}

}
