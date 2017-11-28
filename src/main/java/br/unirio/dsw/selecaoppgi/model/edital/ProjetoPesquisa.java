package br.unirio.dsw.selecaoppgi.model.edital;

import java.util.ArrayList;
import java.util.List;

import br.unirio.dsw.selecaoppgi.model.usuario.Usuario;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa um projeto de pesquisa
 * 
 * @author Marcio Barros
 */
public class ProjetoPesquisa
{
	private @Getter @Setter String codigo;
	private @Getter @Setter String nome;
	private @Getter @Setter boolean exigeProvaOral;
	private List<Usuario> professores;
	private List<ProvaEscrita> provasEscritas;
	
	/**
	 * Inicializa um projeto de pesquisa em um edital
	 */
	public ProjetoPesquisa()
	{
		this.codigo = "";
		this.nome = "";
		this.exigeProvaOral = false;
		this.professores = new ArrayList<Usuario>();
		this.provasEscritas = new ArrayList<ProvaEscrita>();
	}

	/**
	 * Retorna a lista de professores do projeto de pesquisa
	 */
	public Iterable<Usuario> getProfessores()
	{
		return professores;
	}

	/**
	 * Adiciona um professor no projeto de pesquisa
	 */
	public void adicionaProfessor(Usuario professor)
	{
		this.professores.add(professor);
	}
	/**
	 * substitui a lista de professores pela lista passada
	 * @param professores
	 */
	public void setProfessores(List<Usuario> professores) {
		this.professores = professores;
	}
	/**
	 * Retorna um list contendo os professores
	 * @return
	 */
	public List<Usuario> retornaListaDeProfessores(){
		return this.professores;
	}
	/**
	 * Retorna a lista de provas escritas do projeto de pesquisa
	 */
	public Iterable<ProvaEscrita> getProvasEscritas()
	{
		return provasEscritas;
	}

	/**
	 * Adiciona uma prova escrita no projeto de pesquisa
	 */
	public void adicionaProvaEscrita(ProvaEscrita prova)
	{
		this.provasEscritas.add(prova);
	}
	/*
	 * Substitui a lista de provas pela passada
	 */
	public void setProvas(List<ProvaEscrita> provas) {
		this.provasEscritas = provas;
	}	
 }