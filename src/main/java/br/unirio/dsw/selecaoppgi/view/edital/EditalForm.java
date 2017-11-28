package br.unirio.dsw.selecaoppgi.view.edital;

import java.util.List;

import br.unirio.dsw.selecaoppgi.model.usuario.Usuario;
import lombok.Data;

/**
 * Classe que representa o formulário para edição de um edital
 * 
 * @author Marcio
 */
public @Data class EditalForm 
{
	private int id = -1;
	private String nome = "";
	private int notaMinimaAlinhamento = 70;
	private List<Usuario> selectProfessores;
	private List<String> professoresIds;
}
