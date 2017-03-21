package com.nisira.annotation;

import java.util.ArrayList;
import java.util.List;

import org.javatuples.Pair;

public class ECampoRelacionado {
	private String tabla;
	private List<Pair<String, String>> referencia;
	
	public ECampoRelacionado(String tabla, CampoRelacionado relacion) {
		this.tabla = tabla;
		
		RelacionTabla[] relaciones = relacion.value();
		
		referencia = new ArrayList<Pair<String,String>>();
		
		for (RelacionTabla r : relaciones) {
			referencia.add(Pair.with(r.campo(), r.campoRelacionado()));
		}
	}
	
	public String getTabla() {
		return tabla;
	}
	
	public void setTabla(String tabla) {
		this.tabla = tabla;
	}

	public List<Pair<String, String>> getReferencia() {
		return referencia;
	}

	public void setReferencia(List<Pair<String, String>> referencia) {
		this.referencia = referencia;
	}
}
