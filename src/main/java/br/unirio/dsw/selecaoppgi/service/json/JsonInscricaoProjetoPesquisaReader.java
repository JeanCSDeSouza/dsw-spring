package br.unirio.dsw.selecaoppgi.service.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import br.unirio.dsw.selecaoppgi.model.edital.Edital;
import br.unirio.dsw.selecaoppgi.model.edital.ProjetoPesquisa;
import br.unirio.dsw.selecaoppgi.model.inscricao.InscricaoEdital;

/**
 * Classe que carrega os dados das inscrições em projeto de pesquisa de uma inscrição do formato JSON
 * 
 * @author Marcio Barros
 */
public class JsonInscricaoProjetoPesquisaReader
{
	/**
	 * Gera a representação JSON
	 */
	public void execute(JsonArray jsonInscricaoProjetos, Edital edital, InscricaoEdital inscricao)
	{
		for (int i = 0; i < jsonInscricaoProjetos.size(); i++)
		{
			JsonObject jsonInscricao = jsonInscricaoProjetos.get(i).getAsJsonObject();
			String codigo = jsonInscricao.get("codigo").getAsString();
			String intencoes = jsonInscricao.get("intencoes").getAsString();
			
			ProjetoPesquisa projeto = edital.pegaProjetoPesquisaCodigo(codigo);
			
			if (projeto != null)
				inscricao.adicionaInscricaoProjetoPesquisa(projeto, intencoes);
		}
	}
}