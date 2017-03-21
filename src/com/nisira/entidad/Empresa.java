package com.nisira.entidad;

import com.nisira.annotation.ClavePrimaria;
import com.nisira.annotation.Columna;
import com.nisira.annotation.Tabla;

@Tabla(nombre = "Empresa")
public class Empresa {
	@ClavePrimaria
	@Columna
	private String Id;
	@Columna
	private String Direccion;
	@Columna
	private String RazonSocial;
	@Columna
	private String RUC;
	@Columna
	private String RUTA_EXPORTAR;
	@Columna
	private String RUTA_REPORTES;



	/* Sets & Gets */
	public void setId(String Id) {
		this.Id = Id;
	}

	public String getId() {
		return this.Id;
	}

	public void setDireccion(String Direccion) {
		this.Direccion = Direccion;
	}

	public String getDireccion() {
		return this.Direccion;
	}

	public void setRazonSocial(String RazonSocial) {
		this.RazonSocial = RazonSocial;
	}

	public String getRazonSocial() {
		return this.RazonSocial;
	}

	public void setRUC(String RUC) {
		this.RUC = RUC;
	}

	public String getRUC() {
		return this.RUC;
	}

	public void setRUTA_EXPORTAR(String RUTA_EXPORTAR) {
		this.RUTA_EXPORTAR = RUTA_EXPORTAR;
	}

	public String getRUTA_EXPORTAR() {
		return this.RUTA_EXPORTAR;
	}

	public void setRUTA_REPORTES(String RUTA_REPORTES) {
		this.RUTA_REPORTES = RUTA_REPORTES;
	}

	public String getRUTA_REPORTES() {
		return this.RUTA_REPORTES;
	}



	/* Sets & Gets FK*/

}