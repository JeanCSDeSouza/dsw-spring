package br.unirio.dsw.selecaoppgi.model.edital;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import br.unirio.dsw.selecaoppgi.model.usuario.Usuario;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa um edital
 * 
 * @author Marcio Barros
 */
public class Edital
{
	private @Getter @Setter int id;
	private @Getter @Setter String nome;
	private @Getter @Setter DateTime dataInicio;
	private @Getter @Setter DateTime dataTermino;
	private @Getter @Setter StatusEdital status;
	private @Getter @Setter int notaMinimaAlinhamento;
	private List<Usuario> comissaoSelecao;
	private List<Usuario> comissaoRecurso;
	private List<ProvaEscrita> provasEscritas;
	private List<ProjetoPesquisa> projetosPesquisa;
	private List<CriterioAlinhamento> criteriosAlinhamento;

	/**
	 * Inicializa o edital
	 */
	public Edital()
	{
		this.id = -1;
		this.nome = "";
		this.dataInicio = null;
		this.dataTermino = null;
		this.status = StatusEdital.Preparacao;
		this.notaMinimaAlinhamento = 70;
		this.comissaoSelecao = new ArrayList<Usuario>();
		this.comissaoRecurso = new ArrayList<Usuario>();
		this.provasEscritas = new ArrayList<ProvaEscrita>();
		this.projetosPesquisa = new ArrayList<ProjetoPesquisa>();
		this.criteriosAlinhamento = new ArrayList<CriterioAlinhamento>();
	}
	
	/**
	 * Retorna uma prova escrita, dado seu código
	 */
	public ProvaEscrita pegaProvaEscritaCodigo(String codigo)
	{
		for (ProvaEscrita prova : provasEscritas)
			if (prova.getCodigo().compareToIgnoreCase(codigo) == 0)
				return prova;
		
		return null;
	}

	/**
	 * Retorna a composição da comissão de seleção
	 */
	public Iterable<Usuario> getComissaoSelecao()
	{
		return comissaoSelecao;
	}
	/**
	 * Retorna um List contendo todos os professores 
	 * da comissão de seleção
	 */
	public List<Usuario> getListComissaoSelecao(){
		return comissaoSelecao;
	}
	/**
	 * Substitui a lista de comissao de seleção pela passada
	 * se size for maior que zero
	 * @param comissaoSelecao
	 */
	public void setListComissaoSelecao(List<Usuario> comissaoSelecao) {
		if(comissaoSelecao.size() > 0)
			this.comissaoSelecao = comissaoSelecao;
	}
	/**
	 * Adiciona um membro na comissão de seleção
	 */
	public void adicionaComissaoSelecao(Usuario professor)
	{
		this.comissaoSelecao.add(professor);
	}
	/**
	 * Remove um professor da comissão de seleçãoo
	 * @param professor
	 * @return true se o professor estiver na comissão
	 */
	public boolean removeComissaoSelecao(Usuario professor) {
		if( this.comissaoSelecao.contains(professor)) {
			this.comissaoSelecao.remove(professor);
			return true;
		}else {
			return false;
		}
	}
	/**
	 * Retorna a composição da comissão de recursos
	 */
	public Iterable<Usuario> getComissaoRecursos()
	{
		return comissaoRecurso;
	}
	/**
	 * Retorna um List contendo todos os professores 
	 * da comissão de recursos 
	 */
	public List<Usuario> getListComissaoRecursos(){
		return comissaoRecurso;
	}
	public void setListComissaoRecursos(List<Usuario> comissaoRecurso) {
		if(comissaoRecurso.size() > 0)
			this.comissaoRecurso = comissaoRecurso;
	}
	/**
	 * Adiciona um membro na comissão de recursos
	 */
	public void adicionaComissaoRecurso(Usuario professor)
	{
		this.comissaoRecurso.add(professor);
	}
	public boolean removeComissaoRecurso(Usuario professor) {
		if(this.comissaoRecurso.contains(professor)) {
			this.comissaoRecurso.remove(professor);
			return true;
		}else 
			return false;
	}
	/**
	 * Retorna a lista de provas escritas
	 */
	public Iterable<ProvaEscrita> getProvasEscritas()
	{
		return provasEscritas;
	}
	/**
	 * Retorna a lista de provas escritas
	 */
	public List<ProvaEscrita> getListProvasEscritas()
	{
		return provasEscritas;
	}
	/**
	 * Adiciona uma prova escrita no edital
	 */
	public void adicionaProvaEscrita(ProvaEscrita prova)
	{
		this.provasEscritas.add(prova);
	}

	/**
	 * Adiciona uma prova escrita no edital
	 */
	public ProvaEscrita adicionaProvaEscrita(String codigo, String nome, boolean dispensavel, int notaMinimaAprovacao)
	{
		ProvaEscrita prova = new ProvaEscrita();
		prova.setCodigo(codigo);
		prova.setNome(nome);
		prova.setDispensavel(dispensavel);
		prova.setNotaMinimaAprovacao(notaMinimaAprovacao);
		this.provasEscritas.add(prova);
		return prova;
	}

	/**
	 * Remove uma prova escrita, dado seu código
	 */
	public void removeProvaEscrita(String codigo)
	{
		ProvaEscrita prova = pegaProvaEscritaCodigo(codigo);
		
		if (prova != null)
			this.provasEscritas.remove(prova);
	}

	/**
	 * Conta o número de projetos de pesquisa no edital
	 */
	public int contaProjetosPesquisa()
	{
		return projetosPesquisa.size();
	}

	/**
	 * Retorna a lista de projetos de pesquisa
	 */
	public Iterable<ProjetoPesquisa> getProjetosPesquisa()
	{
		return projetosPesquisa;
	}
	/**
	 * Retorna um list com projetos de pesquisa
	 */
	public List<ProjetoPesquisa> getListProjetosPesquisa()
	{
		return projetosPesquisa;
	}
	/**
	 * Retorna um projeto de pesquisa, dado seu índice
	 */
	public ProjetoPesquisa pegaProjetoPesquisaCodigo(int indice)
	{
		return projetosPesquisa.get(indice);
	}

	/**
	 * Retorna um projeto de pesquisa, dado seu código
	 */
	public ProjetoPesquisa pegaProjetoPesquisaCodigo(String codigo)
	{
		for (ProjetoPesquisa projeto : projetosPesquisa)
			if (projeto.getCodigo().compareToIgnoreCase(codigo) == 0)
				return projeto;
		
		return null;
	}

	/**
	 * Adiciona um projeto de pesquisa no edital
	 */
	public void adicionaProjetoPesquisa(ProjetoPesquisa projeto)
	{
		this.projetosPesquisa.add(projeto);
	}

	/**
	 * Adiciona um projeto de pesquisa no edital
	 * 
	 */
	public ProjetoPesquisa adicionaProjetoPesquisa(String codigo, String nome, boolean exigeProvaOral)
	{
		ProjetoPesquisa projeto = new ProjetoPesquisa();
		projeto.setCodigo(codigo);
		projeto.setNome(nome);
		projeto.setExigeProvaOral(exigeProvaOral);
		this.projetosPesquisa.add(projeto);
		return projeto;
	}

	/**
	 * Retorna a lista de critérios de alinhamento
	 */
	public Iterable<CriterioAlinhamento> getCriteriosAlinhamento()
	{
		return criteriosAlinhamento;
	}

	/**
	 * Retorna um critério de alinhamento de pesquisa, dado seu código
	 */
	public CriterioAlinhamento pegaCriterioAlinhamentoCodigo(String codigo)
	{
		for (CriterioAlinhamento criterio : criteriosAlinhamento)
			if (criterio.getCodigo().compareToIgnoreCase(codigo) == 0)
				return criterio;
		
		return null;
	}

	/**
	 * Adiciona um novo critério de alinhamento
	 */
	public void adicionaCriterioAlinhamento(CriterioAlinhamento criterio)
	{
		this.criteriosAlinhamento.add(criterio);
	}

	/**
	 * Adiciona um novo critério de alinhamento
	 */
	public CriterioAlinhamento adicionaCriterioAlinhamento(String codigo, String nome, int pesoComProvaOral, int pesoSemProvaOral, boolean pertenceProvaOral)
	{
		CriterioAlinhamento criterio = new CriterioAlinhamento();
		criterio.setCodigo(codigo);
		criterio.setNome(nome);
		criterio.setPesoComProvaOral(pesoComProvaOral);
		criterio.setPesoSemProvaOral(pesoSemProvaOral);
		criterio.setPertenceProvaOral(pertenceProvaOral);
		this.criteriosAlinhamento.add(criterio);
		return criterio;
	}
}