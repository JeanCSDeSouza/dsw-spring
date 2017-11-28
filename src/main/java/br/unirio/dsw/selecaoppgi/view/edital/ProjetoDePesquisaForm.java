package br.unirio.dsw.selecaoppgi.view.edital;

import java.util.ArrayList;
import java.util.List;

import br.unirio.dsw.selecaoppgi.model.edital.ProvaEscrita;
import br.unirio.dsw.selecaoppgi.model.usuario.Usuario;
import lombok.Data;

public @Data class ProjetoDePesquisaForm {
	private int idEdital = -1;
	private String codigoOriginal = "";
	private String codigo = "";
	private String nome = "";
	private boolean exigeProvaOral = false;
	private List<Usuario> listaProfessor = new ArrayList<>();
	private List<ProvaEscrita> provas = new ArrayList<>();
	//atributo ligado ao retorno do add professor 
	private String selectProfessor;
	//Atributo que carrega os professores para o select
	private List<Usuario> selectProfessores;
	//atributo ligado ao retorno de add prova 
	private String selectProva;
	//atributo que carrega as provas para o select de provas
	private List<ProvaEscrita> selectProvas;
	
	List<String> professoresIds = new ArrayList<>();
	 List<String> provasCodigos = new ArrayList<>();
	/*
	 * Lista de Provas
	 * 
	 * Lista de Professores
	 * 
	 * */	
}
