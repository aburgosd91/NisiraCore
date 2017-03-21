package com.nisira.generator;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

public class FrmGeneraEntidades extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private ConexionGen c;
	private List<Tabla> lista;
	private DefaultListModel<Tabla> listModel;

	private JRadioButton rbEntidad;
	private JRadioButton rbDao;
	private JRadioButton rbAmbos;

	/*
	 * Launch the application.
	 */

	// public static void main(String[] args) {
	// EventQueue.invokeLater(new Runnable() {
	// public void run() {
	// try {
	// FrmGeneraEntidades frame = new FrmGeneraEntidades();
	// frame.setVisible(true);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// });
	// }

	/**
	 * Create the frame.
	 */
	public FrmGeneraEntidades(String gestor) {
		setTitle("Lista de Tablas");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 524, 420);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		listModel = new DefaultListModel<Tabla>();
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 498, 0 };
		gbl_contentPane.rowHeights = new int[] { 35, 303, 33, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 1.0, 1.0, 0.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JPanel pnlOpciones = new JPanel();
		GridBagConstraints gbc_pnlOpciones = new GridBagConstraints();
		gbc_pnlOpciones.insets = new Insets(0, 0, 5, 0);
		gbc_pnlOpciones.fill = GridBagConstraints.BOTH;
		gbc_pnlOpciones.gridx = 0;
		gbc_pnlOpciones.gridy = 0;
		contentPane.add(pnlOpciones, gbc_pnlOpciones);

		rbEntidad = new JRadioButton("Crear entidades");
		rbEntidad.setSelected(true);
		pnlOpciones.add(rbEntidad);

		rbDao = new JRadioButton("Crear DAO");
		pnlOpciones.add(rbDao);

		rbAmbos = new JRadioButton("Crear entidad y DAO");
		pnlOpciones.add(rbAmbos);

		ButtonGroup bg = new ButtonGroup();
		bg.add(rbEntidad);
		bg.add(rbDao);
		bg.add(rbAmbos);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 1;
		contentPane.add(scrollPane, gbc_scrollPane);

		JList<Tabla> list = new JList<Tabla>();
		scrollPane.setViewportView(list);
		list.setModel(listModel);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.SOUTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		contentPane.add(panel, gbc_panel);

		c = new ConexionGen(gestor);

		JButton btnGenerar = new JButton("Generar");
		btnGenerar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				int resp = JOptionPane.showConfirmDialog(null, "Desea iniciar el proceso?", "Nisira",
						JOptionPane.YES_NO_OPTION);

				if (resp == JOptionPane.YES_OPTION) {
					int[] datos = list.getSelectedIndices();
					for (int i : datos) {
						if (FrmGeneraEntidades.this.rbEntidad.isSelected()) {
							genEntidad(listModel.get(i));
						}

						if (FrmGeneraEntidades.this.rbDao.isSelected()) {
							genDao(listModel.get(i));
						}

						if (FrmGeneraEntidades.this.rbAmbos.isSelected()) {
							genEntidad(listModel.get(i));
							genDao(listModel.get(i));
						}
					}

					JOptionPane.showMessageDialog(null, "Operación realizada correctamente");
				}
			}
		});
		panel.add(btnGenerar);

		JButton btnSalir = new JButton("Salir");
		btnSalir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				FrmGeneraEntidades.this.dispose();
			}
		});
		panel.add(btnSalir);

		try {
			lista = c.retornaEstructura(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		listModel.clear();

		for (Tabla t : lista) {
			listModel.addElement(t);
		}

	}

	private void genDao(Tabla tabla) {
		String nombre = tabla.getNombre().toLowerCase();
		
		nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1).concat("Dao");

		String subnombre = tabla.getNombre().toLowerCase().substring(0, 1).toUpperCase() + tabla.getNombre().toLowerCase().substring(1);

		File file = new File(ConexionGen.RUTADAO + nombre + ".java");

		List<String> codigoPersiste = new ArrayList<String>();

		try {
			if (file.exists()) {
				// Buscar código persistente

				FileReader fr = new FileReader(file);
				BufferedReader br = null;
				br = new BufferedReader(fr);
				boolean inicio = false, fin = false;
				String linea;

				String codigo = "";
				while ((linea = br.readLine()) != null) {
					if (linea.trim().equalsIgnoreCase("/*-Inicio-*/")) {
						inicio = true;
						codigo = "";
					}

					if (linea.trim().equalsIgnoreCase("/*-Fin-*/")) {
						inicio = false;
						fin = true;
					}

					if (inicio) {
						codigo = codigo.concat(linea).concat("\n");
					}

					if (fin) {
						codigo = codigo.concat(linea).concat("\n");

						codigoPersiste.add(codigo);
						fin = false;
					}

				}
				br.close();

				file.delete();
			}

			FileWriter w;
			w = new FileWriter(file);

			BufferedWriter bw = new BufferedWriter(w);

			PrintWriter wr = new PrintWriter(bw);

			String paquete, imports, cabecera, buscarpor_pk = "";
			
			paquete = "package ".concat(ConexionGen.PAQUETEDAO).concat(";\n");
			imports = "import com.nisira.core.BaseDao;\nimport "+ConexionGen.PAQUETEENTIDAD+".".concat(subnombre).concat(";\n");
			boolean usaUtilList = false;

			cabecera = "public class " + nombre + " extends BaseDao<" + subnombre + "> {\n";

			cabecera += "\tpublic " + nombre + "() {\n";
			cabecera += "\t\tsuper(" + subnombre + ".class);\n";
			cabecera += "\t}\n";

			cabecera += "\tpublic " + nombre + "(boolean usaCnBase) throws NisiraORMException {\n";
			cabecera += "\t\tsuper(" + subnombre + ".class, usaCnBase);\n";
			cabecera += "\t}\n";

			if (tabla.getClavePrimaria() != null) {
				usaUtilList = true;
				buscarpor_pk = "\tpublic " + subnombre + " getPorClavePrimaria(";

				boolean primer = true;
				String aux1, aux2;
				aux1 = "";
				aux2 = "";

				for (String pk : tabla.getClavePrimaria().getCampos()) {
					for (Columna c : tabla.getColumnas()) {
						if (c.getNombre().equalsIgnoreCase(pk)) {
							String tipo = getJavaType(c.getTipo());
							buscarpor_pk += (primer ? "" : ", ") + tipo + " " + c.getNombre();
							aux1 += (primer ? "" : " and ") + "t0." + c.getNombre() + " = ?";
							aux2 += (primer ? "" : ", ") + c.getNombre();
							primer = false;
						}
					}
				}
				buscarpor_pk += ") throws NisiraORMException {\n";

				buscarpor_pk += "\t\tList<" + subnombre + "> l = listar(\"" + aux1 + " " + "\", " + aux2 + ");\n";
				buscarpor_pk += "\t\tif (l.isEmpty()) {\n";
				buscarpor_pk += "\t\t\treturn null;\n";
				buscarpor_pk += "\t\t} else {\n";
				buscarpor_pk += "\t\t\treturn l.get(0);\n";
				buscarpor_pk += "\t\t}\n";

				buscarpor_pk += "\t}\n";
			}
			// if (usaSqlException) {
			//imports += "import java.sql.SQLException;\n";
			imports += "import com.nisira.core.NisiraORMException;\n";
			
			// }

			if (usaUtilList) {
				imports += "import java.util.List;\n";
			}
			
			wr.append(paquete);
			wr.append("\n");
			wr.append(imports);
			wr.append("\n");
			wr.append(cabecera);
			wr.append("\n");
			wr.append(buscarpor_pk);
			for (String cod : codigoPersiste) {
				wr.append(cod);
			}
			wr.append("}");

			wr.close();

			bw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String getJavaType(String sqlType) {
		String tipo = null;

		switch (sqlType) {
		case "date":
		case "datetime":
			tipo = "Date";
			break;
		// case "datetime":
		// usaTimestamp = true;
		// tipo = "Timestamp";
		// break;
		case "varchar":
		case "char":
		case "text":
		case "nvarchar":
		case "nchar":
		case "ntext":
		case "xml":
			tipo = "String";
			break;

		case "int":
		case "bigint":
		case "smallint":
			tipo = "Integer";
			break;

		case "float":
		case "numeric":
		case "decimal":
			tipo = "Float";
			break;

		case "double":
			tipo = "Double";
			break;
		case "bit":
			tipo = "Short";
			break;
		}
		return tipo;
	}

	private void genEntidad(Tabla tabla) {

		String nombre = tabla.getNombre().toLowerCase();
		nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1);

		File file = new File(ConexionGen.RUTAENTIDAD + nombre + ".java");

		List<String> codigoPersiste = new ArrayList<String>();

		try {

			if (file.exists()) {
				// Buscar código persistente

				FileReader fr = new FileReader(file);
				BufferedReader br = null;
				br = new BufferedReader(fr);
				boolean inicio = false, fin = false;
				String linea;

				String codigo = "";
				while ((linea = br.readLine()) != null) {
					if (linea.trim().equalsIgnoreCase("/*-Inicio-*/")) {
						inicio = true;
						codigo = "";
					}

					if (linea.trim().equalsIgnoreCase("/*-Fin-*/")) {
						inicio = false;
						fin = true;
					}

					if (inicio) {
						codigo = codigo.concat(linea).concat("\n");
					}

					if (fin) {
						codigo = codigo.concat(linea).concat("\n");

						codigoPersiste.add(codigo);
						fin = false;
					}

				}
				br.close();

				file.delete();

			}

			FileWriter w = new FileWriter(file);

			BufferedWriter bw = new BufferedWriter(w);

			PrintWriter wr = new PrintWriter(bw);

			String paquete, imports, cabecera, campos = "", set = "\n\t/* Sets & Gets */\n";
			String camposRel = "", setRel = "\n\t/* Sets & Gets FK*/\n";

			boolean usaTimestamp = false, usaDate = false, usaReferencia = false;

			paquete = "package "+ConexionGen.PAQUETEENTIDAD+";\n";
			imports = "import com.nisira.annotation.ClavePrimaria;\nimport com.nisira.annotation.Columna;\nimport com.nisira.annotation.Tabla;\n";

			cabecera = "@Tabla(nombre = \"" + tabla.getNombre() + "\")\n" + "public class " + nombre + " {\n";

			for (Columna c : tabla.getColumnas()) {
				String tipo = "", post = c.getNombre().toLowerCase();

				if (c.getNombre().length() > 1) {
					boolean m0, m1, may = true;
					m0 = Character.isUpperCase(post.charAt(0));
					m1 = Character.isUpperCase(post.charAt(1));

					if (!m0 && m1)
						may = false;

					post = ((!may) ? post.substring(0, 1).toLowerCase() : post.substring(0, 1).toUpperCase())
							.concat(post.substring(1));

				} else {
					post = c.getNombre().toUpperCase();
				}

				tipo = getJavaType(c.getTipo());

				if (tipo.equalsIgnoreCase("Date")) {
					usaDate = true;
				}

				if (!c.getTipo().equalsIgnoreCase("time")) {
					// Buscar si es clave Primaria
					boolean esPK = false;
					if (tabla.getClavePrimaria() != null)
						for (String clave : tabla.getClavePrimaria().getCampos()) {
							if (clave.equalsIgnoreCase(c.getNombre())) {
								esPK = true;
								break;
							}
						}
					if (esPK) {
						campos += "\t@ClavePrimaria\n";
					}
					campos += "\t@Columna\n\tprivate " + tipo + " " + c.getNombre().toLowerCase() + ";\n";

					set += "\tpublic void set" + post + "(" + tipo + " " + c.getNombre().toLowerCase() + ") {\n";
					set += "\t\tthis." + c.getNombre().toLowerCase() + " = " + c.getNombre().toLowerCase() + ";\n";
					set += "\t}\n\n";

					set += "\tpublic " + tipo + " get" + post + "() {\n";
					set += "\t\treturn this." + c.getNombre().toLowerCase() + ";\n";
					set += "\t}\n\n";

				}
			}

			// campos relacionados

			for (ClaveForanea fk : tabla.getClavesForaneas()) {
				usaReferencia = true;
				String clase = fk.getTablaForanea().toLowerCase(), instancia, relacion = "", nomCampo;
				
				clase = clase.substring(0, 1).toUpperCase().concat(clase.substring(1));
				
				instancia = fk.getTablaForanea().concat("_").concat(fk.getNombre()).toLowerCase();

				nomCampo = instancia.substring(0, 1).toUpperCase() + instancia.substring(1);
				
//				for (int i = 0 ; i < fk.getCampos().size(); i++) {
//					String[] r = fk.getCampos().get(i);
//					relacion += "{@RelacionTabla(campo=\"".concat(r[0]).concat("\"").concat(",")
//							.concat("campoRelacionado=\"").concat(r[1]).concat("\"").concat(")}");
//				}
				
				for (String[] r : fk.getCampos()) {
					relacion += (relacion.isEmpty()?"":", ") + "@RelacionTabla(campo=\"".concat(r[0]).concat("\"").concat(",")
							.concat("campoRelacionado=\"").concat(r[1]).concat("\"").concat(")");
				}

				camposRel += "\t@CampoRelacionado({" + relacion + "})\n";
				camposRel += "\tprivate ".concat(clase).concat(" ").concat(instancia.toLowerCase()).concat(";\n");

				setRel += "\tpublic void set".concat(nomCampo).concat("(").concat(clase).concat(" ").concat(instancia)
						.concat(") {\n");
				setRel += "\t\tthis.".concat(instancia).concat(" = ").concat(instancia).concat(";\n");
				setRel += "\t}\n\n";

				setRel += "\tpublic " + clase + " get".concat(nomCampo).concat("() {\n");
				setRel += "\t\treturn this.".concat(instancia).concat(";\n");
				setRel += "\t}\n\n";
			}

			if (usaTimestamp) {
				imports += "import java.sql.Timestamp;\n";
			}

			if (usaDate) {
				imports += "import java.util.Date;\n";
			}

			if (usaReferencia) {
				imports += "import com.nisira.annotation.RelacionTabla;\n";
				imports += "import com.nisira.annotation.CampoRelacionado;\n";
			}

			wr.append(paquete);
			wr.append("\n");
			wr.append(imports);
			wr.append("\n");
			wr.append(cabecera);
			wr.append(campos);
			wr.append("\n");
			wr.append(camposRel);
			wr.append("\n");
			wr.append(set);
			wr.append("\n");
			wr.append(setRel);
			wr.append("\n");
			for (String cod : codigoPersiste) {
				wr.append(cod);
			}
			wr.append("}");

			wr.close();

			bw.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
