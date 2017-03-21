package com.nisira.dao;

import com.nisira.core.BaseDao;
import com.nisira.entidad.Empresa;

public class EmpresaDao extends BaseDao<Empresa> {
	public EmpresaDao() {
		super(Empresa.class);
	}
}