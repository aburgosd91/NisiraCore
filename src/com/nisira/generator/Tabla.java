package com.nisira.generator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tabla extends ObjetoSQL implements Serializable {

	private static final long serialVersionUID = 5019753327326725743L;
	
	private List<Columna> columnas;
	private List<ClaveForanea> clavesForaneas;
	private ClavePrimaria clavePrimaria;
	
	public Tabla() {
		clavesForaneas = new ArrayList<ClaveForanea>();
	}

	public List<Columna> getColumnas() {
		return columnas;
	}

	public void setColumnas(List<Columna> columnas) {
		this.columnas = columnas;
	}

	public ClavePrimaria getClavePrimaria() {
		return clavePrimaria;
	}

	public void setClavePrimaria(ClavePrimaria clavePrimaria) {
		this.clavePrimaria = clavePrimaria;
	}

	public List<ClaveForanea> getClavesForaneas() {
		return clavesForaneas;
	}

	public void setClavesForaneas(List<ClaveForanea> clavesForaneas) {
		this.clavesForaneas = clavesForaneas;
	}

	public String queryCrear(String gestor) {

		String qy = "Create Table " + getNombre() + "(";
		for (int i = 0; i < columnas.size(); i++) {
			// for (Columna c : columnas) {
			Columna c = columnas.get(i);
			qy += c.getDefinicio(gestor) + ((i == columnas.size() - 1) ? "" : ",");
		}
		qy += ") ";

		return qy;
	}

	public String queryCreaClavePrimaria() {
		String qy = "";
		if (clavePrimaria != null) {
			qy += "\n alter table " + getNombre() + " add constraint " + getClavePrimaria().getNombre()
					+ " primary key (";

			for (int i = 0; i < clavePrimaria.getCampos().size(); i++) {
				if (i == clavePrimaria.getCampos().size() - 1) {
					qy += clavePrimaria.getCampos().get(i);
				} else {
					qy += clavePrimaria.getCampos().get(i) + ",";
				}
			}
			qy += ");";

		}
		return qy;
	}

	public String queryCreaClaveForaneas() {
		String qy = "";
		if (getClavesForaneas() != null) {

			for (ClaveForanea fk : clavesForaneas) {
				qy += queryCreaClaveForanea(fk);
			}
		}
		return qy;
	}

	public String queryCreaClaveForanea(ClaveForanea fk) {
		String qy = "alter table " + getNombre() + " add constraint " + fk.getNombre() + " foreign key (";
		String campos = "";
		String rcampos = "";

		if (fk != null) {
			for (int i = 0; i < fk.getCampos().size(); i++) {
				if (i == fk.getCampos().size() - 1) {
					campos += fk.getCampos().get(i)[0];
					rcampos += fk.getCampos().get(i)[1];
				} else {
					campos += fk.getCampos().get(i)[0] + ",";
					rcampos += fk.getCampos().get(i)[1] + ",";
				}
			}
			qy += campos + ") references " + fk.getTablaForanea() + "(" + rcampos + "); \n";
		}
		return qy;
	}

	public String queryCreaColumna(String gestor, Columna col) {
		String qy = "alter table " + getNombre() + " add " + col.getDefinicioModificar(gestor);
		return qy;
	}

	public String queryModificaColumna(String gestor, Columna col) {
		String qy = "alter table " + getNombre() + " alter column " + col.getSubDefinicio(gestor, true);
		return qy;
	}
	
	@Override
	public String toString() {
		return super.getNombre();
	}
}
