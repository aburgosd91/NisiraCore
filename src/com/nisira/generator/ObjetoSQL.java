package com.nisira.generator;

import java.io.Serializable;

public class ObjetoSQL implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7932207241302723007L;
	private String nombre;

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
}
