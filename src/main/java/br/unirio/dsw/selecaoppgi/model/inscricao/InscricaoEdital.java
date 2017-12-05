package br.unirio.dsw.selecaoppgi.model.inscricao;

import java.util.ArrayList;
import java.util.List;

import br.unirio.dsw.selecaoppgi.model.edital.Edital;
import br.unirio.dsw.selecaoppgi.model.edital.ProjetoPesquisa;
import br.unirio.dsw.selecaoppgi.model.edital.ProvaEscrita;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe que representa uma inscrição em um edital
 * 
 * @author Marcio Barros
 */
public class InscricaoEdital
{
	private @Getter @Setter int id;
	private @Getter @Setter int idCandidato;
	private @Getter @Setter String nomeCandidato;
	private @Getter Edital edital;
	private @Getter @Setter boolean cotaNegros;
	private @Getter @Setter boolean cotaDeficientes;
	private @Getter @Setter Boolean homologadoOriginal;
	private @Getter @Setter String justificativaHomologacaoOriginal;
	private @Getter @Setter Boolean homologadoRecurso;
	private @Getter @Setter String justificativaHomologacaoRecurso;
	private @Getter @Setter Boolean dispensadoProvaOriginal;
	private @Getter @Setter String justificativaDispensaOriginal;
	private @Getter @Setter Boolean dispensadoProvaRecurso;
	private @Getter @Setter String justificativaDispensaRecurso;
	private List<InscricaoProjetoPesquisa> projetosPesquisa;
	private List<AvaliacaoProvaEscrita> provasEscritas;
	private @Getter @Setter Boolean homologado;
	
	/**
	 * Inicializa uma inscrição em um edital
	 */
	public InscricaoEdital(Edital edital)
	{
		this.id = -1;
		this.idCandidato = -1;
		this.nomeCandidato = "";
		this.edital = edital;
		this.cotaNegros = false;
		this.cotaDeficientes = false;
		this.homologadoOriginal = null;
		this.justificativaHomologacaoOriginal = "";
		this.homologadoRecurso = null;
		this.justificativaHomologacaoRecurso = "";
		this.dispensadoProvaOriginal = null;
		this.justificativaDispensaOriginal = "";
		this.dispensadoProvaRecurso = null;
		this.justificativaDispensaRecurso = "";
		this.projetosPesquisa = new ArrayList<InscricaoProjetoPesquisa>();
		this.provasEscritas = new ArrayList<AvaliacaoProvaEscrita>();
	}
	
	/**
	 * Conta o número de inscrições em projetos de pesquisa
	 */
	public int contaInscricoesProjetoPesquisa()
	{
		return projetosPesquisa.size();
	}
	
	/**
	 * Retorna a inscrição em um projeto de pesquisa, dado seu índice
	 */
	public InscricaoProjetoPesquisa pegaInscricaoProjetoPesquisa(int indice)
	{
		return projetosPesquisa.get(indice);
	}
	
	/**
	 * Retorna todas as inscrições em projetos de pesquisa
	 */
	public Iterable<InscricaoProjetoPesquisa> getInscricoesProjetoPesquisa()
	{
		return projetosPesquisa;
	}

	/**
	 * Pega a inscrição em um projeto de pesquisa
	 */
	public InscricaoProjetoPesquisa pegaInscricaoProjetoPesquisa(ProjetoPesquisa projeto)
	{
		for (InscricaoProjetoPesquisa inscricaoProjeto : projetosPesquisa)
			if (inscricaoProjeto.getProjetoPesquisa() == projeto)
				return inscricaoProjeto;
		
		return null;
	}
	
	/**
	 * Adiciona a inscrição em um projeto de pesquisa
	 */
	public InscricaoProjetoPesquisa adicionaInscricaoProjetoPesquisa(ProjetoPesquisa projeto, String intencoes)
	{
		InscricaoProjetoPesquisa inscricaoProjeto = new InscricaoProjetoPesquisa(projeto, edital);
		inscricaoProjeto.setIntencoes(intencoes);
		this.projetosPesquisa.add(inscricaoProjeto);
		
		for (ProvaEscrita prova : projeto.getProvasEscritas())
			if (pegaAvaliacaoProvaEscrita(prova) == null)
				provasEscritas.add(new AvaliacaoProvaEscrita(prova));
		
		
		return inscricaoProjeto;
	}

	/**
	 * Remove a inscrição em um projeto de pesquisa
	 */
	public void removeInscricaoProjetoPesquisa(int indice)
	{
		this.projetosPesquisa.remove(indice);
		
		List<ProvaEscrita> provasExigidas = pegaProvasEscritasExigidasProjetosPesquisa();
		
		for (int i = provasEscritas.size()-1; i >= 0; i--)
		{
			AvaliacaoProvaEscrita avaliacao = provasEscritas.get(i);
			
			if (!provasExigidas.contains(avaliacao.getProvaEscrita()))
				provasEscritas.remove(i);
		}
	}

	/**
	 * Retorna a lista de provas escritas exigidas pelos projetos de pesquisa
	 */
	private List<ProvaEscrita> pegaProvasEscritasExigidasProjetosPesquisa()
	{
		List<ProvaEscrita> provasEscritas = new ArrayList<ProvaEscrita>();
		
		for (InscricaoProjetoPesquisa inscricao : projetosPesquisa)
		{
			for (ProvaEscrita prova : inscricao.getProjetoPesquisa().getProvasEscritas())
				if (!provasEscritas.contains(prova))
					provasEscritas.add(prova);
		}
		return provasEscritas;
	}
	
	/**
	 * Conta o número de avaliações de provas escritas
	 */
	public int contaAvaliacoesProvaEscrita()
	{
		return provasEscritas.size();
	}
	
	/**
	 * Retorna a avaliação de uma prova escrita, dado seu índice
	 */
	public AvaliacaoProvaEscrita pegaAvaliacaoProvaEscrita(int indice)
	{
		return provasEscritas.get(indice);
	}

	/**
	 * Retorna a avaliação de uma prova escrita
	 */
	public AvaliacaoProvaEscrita pegaAvaliacaoProvaEscrita(ProvaEscrita prova)
	{
		for (AvaliacaoProvaEscrita avaliacaoProva : provasEscritas)
			if (avaliacaoProva.getProvaEscrita() == prova)
				return avaliacaoProva;
		
		return null;
	}
	
	/**
	 * Retorna todas as avaliações de provas escritas
	 */
	public Iterable<AvaliacaoProvaEscrita> getAvaliacoesProvasEscritas()
	{
		return provasEscritas;
	}

	/**
	 * Verifica se a inscrição exige prova oral
	 */
	public boolean exigeProvaOral()
	{		
		for (InscricaoProjetoPesquisa inscricao : projetosPesquisa)
			if (inscricao.getProjetoPesquisa().isExigeProvaOral())
				return true;

		return false;
	}
}