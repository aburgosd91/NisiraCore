package com.nisira.generator;

import java.io.Serializable;
import java.util.List;


public class ClavePrimaria extends ObjetoSQL implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -749012959481363123L;
	
	private List<String> campos;

	public List<String> getCampos() {
		return campos;
	}

	public void setCampos(List<String> campos) {
		this.campos = campos;
	}
}

