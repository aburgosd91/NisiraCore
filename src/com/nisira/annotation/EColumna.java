package com.nisira.annotation;

public class EColumna {
	private String nombre;
	private boolean esPK;
	private boolean noInsertar;
	private String generador;

	public EColumna(Columna columna) {
		this.nombre = columna.nombre();
		this.noInsertar = columna.noInsertar();
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setPKMetaData(ClavePrimaria pk) {
		this.esPK = (pk != null);
		if (this.esPK) {
			generador = pk.generador().trim();
		}
	}

	public boolean isEsPK() {
		return esPK;
	}

	public void setEsPK(boolean esPK) {
		this.esPK = esPK;
	}

	public String getGenerador() {
		return generador;
	}

	public void setGenerador(String generador) {
		this.generador = generador;
	}

	public boolean isNoInsertar() {
		return noInsertar;
	}

	public void setNoInsertar(boolean noInsertar) {
		this.noInsertar = noInsertar;
	}

}
