package br.unirio.dsw.selecaoppgi.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import br.unirio.dsw.selecaoppgi.model.edital.Edital;
import br.unirio.dsw.selecaoppgi.model.edital.ProjetoPesquisa;
import br.unirio.dsw.selecaoppgi.model.edital.ProvaEscrita;
import br.unirio.dsw.selecaoppgi.model.usuario.Usuario;
import br.unirio.dsw.selecaoppgi.service.dao.EditalDAO;
import br.unirio.dsw.selecaoppgi.service.dao.UsuarioDAO;
import br.unirio.dsw.selecaoppgi.service.message.ExposedResourceMessageBundleSource;
import br.unirio.dsw.selecaoppgi.utils.EditalComissoesConstants;
import br.unirio.dsw.selecaoppgi.utils.JsonUtils;
import br.unirio.dsw.selecaoppgi.view.edital.EditalForm;
import br.unirio.dsw.selecaoppgi.view.edital.ProjetoDePesquisaForm;
import br.unirio.dsw.selecaoppgi.view.edital.ProvaEscritaForm;

/**
 * Controller responsável pelo gerenciamento de editais
 * 
 * @author marciobarros
 */
@Controller
public class EditalController
{
    @Autowired
    private ExposedResourceMessageBundleSource messageSource;
    
	@Autowired
	private UsuarioDAO userDAO; 

	@Autowired
	private EditalDAO editalDAO; 

	/**
	 * Ação que redireciona o usuário para a lista de editais
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/list", method = RequestMethod.GET)
	public ModelAndView mostraPaginaLista()
	{
		return new ModelAndView("edital/list");
	}

	/**
	 * Ação AJAX que lista todos os editais
	 */
	@ResponseBody
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital", method = RequestMethod.GET, produces = "application/json")
	public String lista(@ModelAttribute("page") int pagina, @ModelAttribute("size") int tamanho, @ModelAttribute("nome") String filtroNome)
	{
		List<Edital> editais = editalDAO.lista(pagina, tamanho, filtroNome);
		int total = editalDAO.conta(filtroNome);
		
		Gson gson = new Gson();
		JsonArray jsonEditais = new JsonArray();
		
		for (Edital edital : editais)
			jsonEditais.add(gson.toJsonTree(edital));
		
		JsonObject root = new JsonObject();
		root.addProperty("Result", "OK");
		root.addProperty("TotalRecordCount", total);
		root.add("Records", jsonEditais);
		return root.toString();
	}

	/**
	 * Ação AJAX que retorna o resumo dos editais
	 */
	@ResponseBody
	@RequestMapping(value = "/edital/summary", method = RequestMethod.GET, produces = "application/json")
	public String geraResumos()
	{
		List<Edital> editais = editalDAO.lista(0, 100000, "");
		JsonArray jsonEditais = new JsonArray();
		
		for (Edital edital : editais)
		{
			JsonObject jsonEdital = new JsonObject();
			jsonEdital.addProperty("id", edital.getId());
			jsonEdital.addProperty("nome", edital.getNome());
			jsonEditais.add(jsonEdital);
		}
		
		return JsonUtils.ajaxSuccess(jsonEditais);
	}
	
	/**
	 * Ação AJAX que troca a senha do usuário logado
	 */
	@ResponseBody
	@RequestMapping(value = "/edital/muda/{id}", method = RequestMethod.POST)
	public String mudaSelecionado(@PathVariable("id") int id, Locale locale)
	{
		Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		if (usuario == null)
			return JsonUtils.ajaxError(messageSource.getMessage("edital.muda.edital.selecionado.erro.usuario.nao.encontrado", null, locale));

		usuario.setIdEdital(id);
        userDAO.mudaEditalSelecionado(usuario.getId(), id);
		return JsonUtils.ajaxSuccess();
	}

	/**
	 * Ação que apresenta o formulário de criação de um novo edital
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/create", method = RequestMethod.GET)
	public ModelAndView mostraPaginaCriacao()
	{
		ModelAndView model = new ModelAndView("edital/formCriacao");
		model.getModel().put("form", new EditalForm());
		return model;
	}

	/**
	 * Ação que salva os dados de um novo um edital
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/create", method = RequestMethod.POST)
	public String salvaNovo(@ModelAttribute("form") EditalForm form, BindingResult result, Locale locale)
	{
		if (form.getNome().length() == 0)
	    		result.addError(new FieldError("form", "nome", messageSource.getMessage("edital.form.nome.vazio", null, locale)));
		
		if (form.getNome().length() > 80)
			result.addError(new FieldError("form", "nome", messageSource.getMessage("edital.form.nome.maior.80.caracteres", null, locale)));
		
		Edital editalMesmoNome = editalDAO.carregaEditalNome(form.getNome(), userDAO);
		
		if (editalMesmoNome != null)
			result.addError(new FieldError("form", "nome", messageSource.getMessage("edital.form.nome.duplicado", null, locale)));
		
		if (form.getNotaMinimaAlinhamento() <= 0)
	    		result.addError(new FieldError("form", "notaMinima", messageSource.getMessage("edital.form.nota.minima.menor.igual.zero", null, locale)));
		
		if (form.getNotaMinimaAlinhamento() >= 100)
			result.addError(new FieldError("form", "notaMinima", messageSource.getMessage("edital.form.nota.minima.maior.igual.cem", null, locale)));
		
        if (result.hasErrors())
            return "edital/formCriacao";
        
		Edital edital = new Edital();
        edital.setNome(form.getNome());
        edital.setNotaMinimaAlinhamento(form.getNotaMinimaAlinhamento());
		editalDAO.atualiza(edital);
		return "redirect:/edital/list?message=edital.form.criado";
	}

	/**
	 * Ação que apresenta o formulário de edição de um edital
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/edit/{id}", method = RequestMethod.GET)
	public ModelAndView mostraPaginaEdicao(@PathVariable("id") int id, Locale locale)
	{
		ModelAndView model = new ModelAndView("edital/formEdicao");
		Edital edital = editalDAO.carregaEditalId(id, userDAO);
		
		if (edital == null)
		{
			model.setViewName("redirect:/edital/list?message=edital.form.edital.nao.encontrado");
	        return model;
		}
		
		EditalForm form = new EditalForm();
		form.setId(edital.getId());
		form.setNome(edital.getNome());
		form.setNotaMinimaAlinhamento(edital.getNotaMinimaAlinhamento());
		
		model.getModel().put("form", form);
		model.getModel().put("edital", edital);
		return model;
	}

	/**
	 * Ação que atualiza um edital sendo editado
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/edit", method = RequestMethod.POST)
	public String salvaEdicao(@ModelAttribute EditalForm form, BindingResult result, Locale locale)
	{
		if (form.getNome().length() == 0)
	    		result.addError(new FieldError("form", "nome", messageSource.getMessage("edital.form.nome.vazio", null, locale)));
		
		if (form.getNome().length() > 80)
			result.addError(new FieldError("form", "nome", messageSource.getMessage("edital.form.nome.maior.80.caracteres", null, locale)));
		
		Edital edital = editalDAO.carregaEditalId(form.getId(), userDAO);
		
		if (edital == null)
			result.addError(new FieldError("form", "nome", messageSource.getMessage("edital.form.edital.nao.encontrado", null, locale)));
		
		Edital editalMesmoNome = editalDAO.carregaEditalNome(form.getNome(), userDAO);
		
		if (editalMesmoNome != null && editalMesmoNome.getId() != edital.getId())
			result.addError(new FieldError("form", "nome", messageSource.getMessage("edital.form.nome.duplicado", null, locale)));
		
		if (form.getNotaMinimaAlinhamento() <= 0)
	    		result.addError(new FieldError("form", "notaMinima", messageSource.getMessage("edital.form.nota.minima.menor.igual.zero", null, locale)));
		
		if (form.getNotaMinimaAlinhamento() >= 100)
			result.addError(new FieldError("form", "notaMinima", messageSource.getMessage("edital.form.nota.minima.maior.igual.cem", null, locale)));
		
        if (result.hasErrors())
            return "edital/formEdicao";
        
        edital.setNome(form.getNome());
        edital.setNotaMinimaAlinhamento(form.getNotaMinimaAlinhamento());
		editalDAO.atualiza(edital);
		return "redirect:/edital/list?message=edital.form.atualizado";
	}

	/**
	 * Ação AJAX que remove um edital
	 */
	@ResponseBody
	@RequestMapping(value = "/edital/{id}", method = RequestMethod.DELETE)
	public String remove(@PathVariable("id") int id, Locale locale)
	{
		Edital edital = editalDAO.carregaEditalId(id, userDAO);
		
		if (edital == null)
			return JsonUtils.ajaxError(messageSource.getMessage("edital.lista.remocao.nao.encontrado", null, locale));

		editalDAO.remove(id);
		return JsonUtils.ajaxSuccess();
	}

	/**
	 * Ação que apresenta o formulário de criação de uma nova prova escrita
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/{id}/prova/create", method = RequestMethod.GET)
	public ModelAndView novaProvaEscrita(@PathVariable int id, Locale locale)
	{		
		ModelAndView model = new ModelAndView("edital/formProva");
		ProvaEscritaForm form = new ProvaEscritaForm();
		form.setIdEdital(id);
		form.adicionaPesoQuestao(100);
		model.getModel().put("form", form);
		return model;
	}

	/**
	 * Ação que apresenta o formulário de edição de uma prova escrita
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/{id}/prova/edit/{codigo}", method = RequestMethod.GET)
	public ModelAndView editaProvaEscrita(@PathVariable int id, @PathVariable String codigo, Locale locale)
	{
		ModelAndView model = new ModelAndView("edital/formProva");
		Edital edital = editalDAO.carregaEditalId(id, userDAO);
		
		if (edital == null)
		{
			model.setViewName("redirect:/edital/list?message=edital.form.edital.nao.encontrado");
	        return model;
		}
		
		ProvaEscrita prova = edital.pegaProvaEscritaCodigo(codigo);
		
		if (prova == null)
		{
			model.setViewName("redirect:/edital/edit/" + id + "?message=edital.form.prova.nao.encontrada");
	        return model;
		}
		
		ProvaEscritaForm form = new ProvaEscritaForm();
		form.setIdEdital(id);
		form.setCodigoOriginal(codigo);
		form.setCodigo(codigo);
		form.setNome(prova.getNome());
		form.setDispensavel(prova.isDispensavel());
		form.setNotaMinimaAprovacao(prova.getNotaMinimaAprovacao());
		form.adicionaPesosQuestoes(prova.getPesosQuestoes());
		model.getModel().put("form", form);
		return model;
	}

	/**
	 * Ação AJAX que atualiza uma prova escrita em um edital
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/prova/save", method = RequestMethod.POST)
	public String atualizaProva(@ModelAttribute("form") ProvaEscritaForm form, BindingResult result, Locale locale)
	{
		Edital edital = editalDAO.carregaEditalId(form.getIdEdital(), userDAO);
		
		if (edital == null)
			result.addError(new FieldError("form", "codigo", messageSource.getMessage("edital.form.edital.nao.encontrado", null, locale)));
		
		if (form.getCodigo().length() != 3)
	    		result.addError(new FieldError("form", "codigo", messageSource.getMessage("edital.form.prova.form.erro.codigo.invalido", null, locale)));
		
		if (form.getCodigo().compareToIgnoreCase(form.getCodigoOriginal()) != 0)
		{
			ProvaEscrita prova = edital.pegaProvaEscritaCodigo(form.getCodigo());
			
			if (prova != null)
	    			result.addError(new FieldError("form", "codigo", messageSource.getMessage("edital.form.prova.form.erro.codigo.duplicado", null, locale)));
		}
		
		if (form.getNome().length() == 0)
	    		result.addError(new FieldError("form", "nome", messageSource.getMessage("edital.form.prova.form.erro.nome.branco", null, locale)));
		
		if (form.getNome().length() > 80)
			result.addError(new FieldError("form", "nome", messageSource.getMessage("edital.form.prova.form.erro.nome.longo", null, locale)));
		
		if (form.getNotaMinimaAprovacao() <= 0)
	    		result.addError(new FieldError("form", "notaMinimaAprovacao", messageSource.getMessage("edital.form.prova.form.erro.nota.minima.menor.zero", null, locale)));
		
		if (form.getNotaMinimaAprovacao() >= 100)
			result.addError(new FieldError("form", "notaMinimaAprovacao", messageSource.getMessage("edital.form.prova.form.erro.nota.minima.maior.cem", null, locale)));
		
		int soma = 0;
		
		for (int i = 0; i < form.getPesosQuestoes().size(); i++)
		{
			Integer peso = form.getPesosQuestoes().get(i);
			
			if (peso == null || peso <= 0)
		    		result.addError(new FieldError("form", "pesosQuestoes", messageSource.getMessage("edital.form.prova.form.erro.peso.negativo.zero", null, locale)));
			
			else if (peso > 100)
				result.addError(new FieldError("form", "pesosQuestoes", messageSource.getMessage("edital.form.prova.form.erro.peso.maior.cem", null, locale)));
			
			else
				soma += peso;
		}
		
		if (soma != 100)
			result.addError(new FieldError("form", "pesosQuestoes", messageSource.getMessage("edital.form.prova.form.erro.peso.soma.diferente.cem", null, locale)));
	
		ProvaEscrita prova;
		
		if (form.getCodigoOriginal().length() > 0)
		{
			prova = edital.pegaProvaEscritaCodigo(form.getCodigoOriginal());
			
			if (prova == null)
				result.addError(new FieldError("form", "codigo", messageSource.getMessage("edital.form.prova.form.erro.prova.nao.encontrada", null, locale)));
		}
		else
		{
			prova = new ProvaEscrita();
			edital.adicionaProvaEscrita(prova);
		}
		
		if (result.hasErrors())
			return "edital/formProva";

		prova.setCodigo(form.getCodigo());
		prova.setNome(form.getNome());
		prova.setNotaMinimaAprovacao(form.getNotaMinimaAprovacao());
		prova.setDispensavel(form.isDispensavel());
		prova.limpaQuestoes();
		
		for (Integer peso : form.getPesosQuestoes())
			prova.adicionaQuestao(peso);
		
		editalDAO.atualiza(edital);
		return "redirect:/edital/edit/" + form.getIdEdital() + "?message=edital.form.prova.atualizada";
	}

	/**
	 * Ação que remove uma prova escrita
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/{id}/prova/remove/{codigo}", method = RequestMethod.GET)
	public String removeProvaEscrita(@PathVariable int id, @PathVariable String codigo, Locale locale)
	{
		Edital edital = editalDAO.carregaEditalId(id, userDAO);
		
		if (edital == null)
	        return "redirect:/edital/list?message=edital.form.edital.nao.encontrado";
		
		ProvaEscrita prova = edital.pegaProvaEscritaCodigo(codigo);
		if (prova == null)
	        return "redirect:/edital/edit/" + id + "?message=edital.form.prova.nao.encontrada";
		
		edital.removeProvaEscrita(codigo);
		editalDAO.atualiza(edital);
		return "redirect:/edital/edit/" + id + "?message=edital.form.prova.removida.sucesso";
	}
	/**
	 * Ação que cria um novo projeto de pesquisa no edital cujo id foi passado
	 * Se o id não retornar um edital retorna pra edição de editais com erro
	 * De outra forma leva para a criação de editais
	 * @return ModelAndView com o redirect para criação de projeto de pesquisa 
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/{id}/projeto/create", method = RequestMethod.GET)
	public ModelAndView novoProjetoDePesquisa(@PathVariable int id, @ModelAttribute("form") ProjetoDePesquisaForm form, 
			BindingResult result, Locale locale)
	{	
		ModelAndView model = new ModelAndView("edital/formProjeto");
		//EditalForm formEdital = new EditalForm();
		
		Edital edital = editalDAO.carregaEditalId(id, userDAO);
		if (edital == null)
		{
			model.setViewName("redirect:/edital/list?message=edital.form.edital.nao.encontrado");
			return model;
		}
		form.setSelectProfessores(userDAO.listaProfessores( ));
		form.setSelectProvas( edital.getListProvasEscritas() );
		
		form.setIdEdital(id);
		model.getModel().put("form", form);

		return model;
	}
	/**
	 * Ação que remove um projeto de pesquisa dado o cógido e o id do passado edital. 
	 * Se código for null ou empty retorna com status de BAD_REQUEST
	 * Se o id de edital passado não existir, retorna com BAD_REQUEST
	 * Se a busca pelo codigo passado dentro do edital não retornar um projeto de pesquisa 
	 * returna com BAD_REQUEST
	 * Retorna OK caso não enontre nenhum dos padrões de BAD_REQUEST
	 * @param id Identificador único de edital
	 * @param codigo Identificador do projeto de pesquisa 
	 * @return String identificando sucesso ou insucesso da requisição
	 */
	@ResponseBody
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "edital/{id}/projeto/remove/{codigo}", method = RequestMethod.GET, produces = "application/json")
	public String removeProjetoDePesquisa(@PathVariable int id, @PathVariable String codigo) {
		JsonObject root = new JsonObject();
		if(codigo == null || codigo.equals("")) {
			root.addProperty("STATUS", "BAD_REQUEST");
			String status = root.toString();
			return status;
		}	
		Edital edital = editalDAO.carregaEditalId(id, userDAO);
		if(edital == null) {
			root.addProperty("STATUS", "BAD_REQUEST");
			return root.toString();
		}	
		Optional<ProjetoPesquisa> jaExiste = edital.getListProjetosPesquisa().stream()
				.filter(projetoFlag -> projetoFlag.getCodigo().equalsIgnoreCase(codigo) ).findFirst();
		if(jaExiste.isPresent()) {
			edital.getListProjetosPesquisa().remove(jaExiste.get());
			editalDAO.atualiza(edital);
		}else {
			root.addProperty("STATUS", "BAD_REQUEST");
			return root.toString();
		}
		root.addProperty("STATUS", "OK");
		return root.toString();
	}
	/**
	 * Ação que salva que salva um projeto de pesquisa no edital e então o atualiza.
	 * Se o id passado nçao retornar um edital, retorna com erro. 
	 * Se alguma das ids da lista lista de professores não retornar um usuario cadastrado no banco, 
	 * retorna com erro.
	 * Se algum dos códigos da lista de códigos de provas não retornar uma prova no edital, retorna
	 * com erro.
	 * Se as listas de professores ou de provas estiverem vazias, retorna com erro.
	 * Se os campos do projeto estiverem fora do critério estipulado, retorna com erro.  
	 * De outa forma ele testa se a requisição possui um codigo original, se houver, trata como atualização,
	 * se não somente insere o projeto no edital e o atualiza. 
	 * @return ModelAndView com o redirect para a edição de editais
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/projeto/save", method = RequestMethod.POST)
	public ModelAndView atualizaProjeto(@ModelAttribute("form") ProjetoDePesquisaForm form, BindingResult result,
			Locale locale,
			HttpServletRequest req) 
	{
		ModelAndView model = new ModelAndView("edital/formProjeto");
		Edital edital = editalDAO.carregaEditalId(form.getIdEdital(), userDAO);
		if (edital == null)
		{
			model.setViewName("redirect:/edital/list?message=edital.form.edital.nao.encontrado");
			return model;
		}
		ProjetoPesquisa projeto = new ProjetoPesquisa();
		projeto.setCodigo(form.getCodigo());
		projeto.setNome(form.getNome());
		projeto.setExigeProvaOral(form.isExigeProvaOral());
		List<Usuario> professores = new ArrayList<Usuario>();
		List<ProvaEscrita> provas = new ArrayList<ProvaEscrita>();
		int idAdicionado = -1;
		for(String parameter : form.getProfessoresIds()) {
				try {
					idAdicionado = Integer.parseInt(parameter);
				}catch(NumberFormatException nfe) {
					model.setViewName("redirect:/edital/" + form.getIdEdital() + "/projeto/edit/" + form.getCodigoOriginal() +"?message=edital.form.projeto.form.erro.professor.id.nao.inteiro");
					return model;
				}
				if(idAdicionado >= 0) {
					professores.add(userDAO.carregaUsuarioId(idAdicionado));
					form.setListaProfessor(professores);
				}
		}
		for(String parameter : form.getProvasCodigos()) {
			if(parameter.length() > 2 && parameter.equalsIgnoreCase("")) {
				model.getModel().put("form", form);	
				model.setViewName("redirect:/edital/" + form.getIdEdital() + "/projeto/create?message=edital.form.projeto.form.erro.prova.nao.encontrada");
			}else {
				Optional<ProvaEscrita> jaExiste = edital.getListProvasEscritas().stream().filter( prova -> prova.getCodigo().equalsIgnoreCase(parameter)).findFirst();
				if(jaExiste.isPresent()) {
					provas.add(jaExiste.get());
					form.getProvas().add(jaExiste.get());
				}else {
					model.getModel().put("form", form);	
					model.setViewName("redirect:/edital/" + form.getIdEdital() + "/projeto/create?message=edital.form.projeto.form.erro.prova.não.encontrada");
					return model;
				}		
			}			
		}
		if( form.getProvasCodigos().size() == 0) {
			model.getModel().put("form", form);	
			if (form.getCodigoOriginal().length() == 4) {
				model.setViewName("redirect:/edital/" + form.getIdEdital() + "/projeto/edit/" + form.getCodigoOriginal()
						+ "?message=edital.form.projeto.form.erro.prova.nao.existe.prova.no.projeto");
			}else
				model.setViewName("redirect:/edital/" + form.getIdEdital() + "/projeto/create?message=edital.form.projeto.form.erro.prova.nao.existe.prova.no.projeto");
			return model;
		}
		if( form.getProfessoresIds().size() == 0 ) {
			model.getModel().put("form", form);	
			if (form.getCodigoOriginal().length() == 4) {
				model.setViewName("redirect:/edital/" + form.getIdEdital() + "/projeto/edit/" + form.getCodigoOriginal()
						+ "?message=edital.form.projeto.form.erro.prova.nao.existe.professor.no.projeto");
			}else
				model.setViewName("redirect:/edital/" + form.getIdEdital() + "/projeto/create?message=edital.form.projeto.form.erro.prova.nao.existe.professor.no.projeto");

			return model;
		}
		projeto.setProfessores(professores);
		projeto.setProvas(provas);
			if( projeto.getCodigo().length() != 4 ) {
				result.addError(new FieldError("form", "codigo", messageSource.getMessage("edital.form.projeto.form.erro.codigo.invalido", null, locale)));
			}
			if(projeto.getNome().isEmpty()) {
				result.addError(new FieldError("form", "nome", messageSource.getMessage("edital.form.projeto.form.erro.nome.branco", null, locale)));
			}
			if(projeto.getNome().length() > 80) {
				result.addError( new FieldError( "form", "nome", messageSource.getMessage("edital.form.projeto.form.erro.nome.longo", null, locale)));
			}
			if( result.hasErrors( ) ) {
				form.setSelectProfessores( userDAO.listaProfessores( ) );
				form.setSelectProvas( edital.getListProvasEscritas( ) );
				form.getSelectProfessores().removeAll(form.getListaProfessor());//retira da seleção as provas que já estão adicionadas
				form.getSelectProvas().removeAll(form.getProvas());// retira da seleção as provas que já estão adiconadas
				model.getModel().put("form", form);	
				model.setViewName("edital/formProjeto");
				return model;
			}
			if(form.getCodigoOriginal().isEmpty()) {
				edital.adicionaProjetoPesquisa(projeto);
			}else {
				Optional<ProjetoPesquisa> jaExiste = edital.getListProjetosPesquisa().stream()
						.filter(projetoFlag -> projetoFlag.getCodigo().equalsIgnoreCase(form.getCodigoOriginal()) ).findFirst();
				if(jaExiste.isPresent()) {
					int index = edital.getListProjetosPesquisa().indexOf(jaExiste.get());
					edital.getListProjetosPesquisa().set(index, projeto);
				}
				
			}
		editalDAO.atualiza(edital);
		model.setViewName("redirect:/edital/edit/" + form.getIdEdital() + "?message=edital.form.projeto.atualizado");
		return model;
	}
	/**
	 * Ação que edita projeto de pesquisa pelo codigo passado no edital do id passado.
	 * Se id passado não retornar um edital retorna com erro.
	 * Se o codigo passado não retornar um projeto de pesquisa retorna com erro.
	 * De outra forma, leva para a página de edição de edital.
	 * @return
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/{id}/projeto/edit/{codigo}", method = RequestMethod.GET)
	public ModelAndView editaProjetoPesquisa(@PathVariable int id, @PathVariable String codigo, 
			@ModelAttribute("form") ProjetoDePesquisaForm form, BindingResult result, Locale locale)
	{
		ModelAndView model = new ModelAndView("edital/formProjeto");
		Edital edital = editalDAO.carregaEditalId(id, userDAO);
		EditalForm formEdital = new EditalForm();
		if (edital == null)
		{
			model.setViewName("redirect:/edital/list?message=edital.form.edital.nao.encontrado");
			return model;
		}
		
		form.setSelectProvas( edital.getListProvasEscritas( ) );// coloca a lista de provas no select da página
		form.setSelectProfessores( userDAO.listaProfessores( ) );//coloca lista de professores no select da página
		
		formEdital.setId(edital.getId());
		
		ProjetoPesquisa projeto = edital.pegaProjetoPesquisaCodigo(codigo);
		if (projeto == null)
		{
			model.setViewName("redirect:/edital/edit/" + id + "?message=edital.form.projeto.form.erro.projeto.nao.encontrada");
			return model;
		}
		form.setIdEdital(id);
		form.setCodigoOriginal(codigo);
		form.setCodigo(codigo);
		form.setNome(projeto.getNome());
		form.setExigeProvaOral(projeto.isExigeProvaOral());
		form.setListaProfessor((List<Usuario>) projeto.getProfessores());
		form.getSelectProfessores().removeAll(form.getListaProfessor());//retira da seleção as provas que já estão adicionadas
		form.setProvas((List<ProvaEscrita>) projeto.getProvasEscritas());
		form.getSelectProvas().removeAll(form.getProvas());// retira da seleção as provas que já estão adiconadas
		model.getModel().put("form", form);
		return model;
	}
	/**
	 * ação que remove um professor da comissão de recursos ou seleção dado o parâmetro passado
	 */
	@ResponseBody
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/{id}/{comissao:selecao|recursos}/remove/{professorId}", method = RequestMethod.GET, produces = "application/json")
	public String removeProfessorDeComissao(@PathVariable int id,@PathVariable String comissao ,@PathVariable int professorId) {
		JsonObject root = new JsonObject();
		Edital edital = editalDAO.carregaEditalId(id, userDAO);
		if(edital == null) {
			root.addProperty(EditalComissoesConstants.RETURN_PREFIX.getMessage(),
					EditalComissoesConstants.BAD_REQUEST.getMessage());
			return root.toString();
		}	
		edital.getComissaoRecursos().forEach(action -> System.out.println(action.getNome()));
		Usuario professorRemovido = userDAO.carregaUsuarioId(professorId);
		if(professorRemovido == null) {
			root.addProperty(EditalComissoesConstants.RETURN_PREFIX.getMessage(),
					EditalComissoesConstants.BAD_REQUEST.getMessage());
			return root.toString();
		}
		if( ( comissao == null ) || ( comissao.isEmpty() ) || 
				( ( !comissao.equalsIgnoreCase(EditalComissoesConstants.COMISSAO_DE_SELECAO.getMessage() ) ) &&
				( !comissao.equalsIgnoreCase( EditalComissoesConstants.COMISSAO_DE_RECURSO.getMessage() ) ) ) ) 
		{
			root.addProperty(EditalComissoesConstants.RETURN_PREFIX.getMessage(),
					EditalComissoesConstants.BAD_REQUEST.getMessage());
			return root.toString();
		}
		if(comissao.equalsIgnoreCase(EditalComissoesConstants.COMISSAO_DE_SELECAO.getMessage())) {
			if (edital.removeComissaoSelecao(professorRemovido)) {
				editalDAO.atualiza(edital);
				root.addProperty(EditalComissoesConstants.RETURN_PREFIX.getMessage(),
						EditalComissoesConstants.OK_REQUEST.getMessage());
				return root.toString();
			}
		}else
			if(comissao.equalsIgnoreCase(EditalComissoesConstants.COMISSAO_DE_RECURSO.getMessage())){
				if (edital.removeComissaoRecurso(professorRemovido)) {
					editalDAO.atualiza(edital);
					root.addProperty(EditalComissoesConstants.RETURN_PREFIX.getMessage(),
							EditalComissoesConstants.OK_REQUEST.getMessage());
					return root.toString();
				}
			}
		root.addProperty(EditalComissoesConstants.RETURN_PREFIX.getMessage(),
				EditalComissoesConstants.BAD_REQUEST.getMessage());
		return root.toString();
	}
	/**
	 * Ação que edita comissão do edital. A comissao a ser editada é determinada pela url. 
	 * @param id Integer identidicador de um edital
	 * @param comissao String identificadora da comissao 
	 * @return retorn um ModelAndView com o redirect para a página de edição de comição
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/{id}/{comissao:selecao|recursos}/edita", method = RequestMethod.GET)
	public ModelAndView editaProfessoresDeComissao(@PathVariable int id,@PathVariable String comissao) {
		ModelAndView model = new ModelAndView("edital/formEdicaoComissoes");
		Edital edital = editalDAO.carregaEditalId(id, userDAO);
		if (edital == null)
		{
			model.setViewName("redirect:/edital/list?message=edital.form.edital.nao.encontrado");
	        return model;
		}
		
		EditalForm form = new EditalForm();
		List<Usuario> professores = userDAO.listaProfessores();
		if(comissao.equals(EditalComissoesConstants.COMISSAO_DE_SELECAO.getMessage()))
			if(edital.getComissaoSelecao().iterator().hasNext()) {
				edital.getComissaoSelecao().forEach( professor -> {
					if(professores.contains(professor))
						professores.remove(professor);
				});
			}
			else 
				if(comissao.equals(EditalComissoesConstants.COMISSAO_DE_RECURSO.getMessage()))
					if(edital.getComissaoRecursos().iterator().hasNext()) {
						edital.getComissaoRecursos().forEach( professor -> {
							if(professores.contains(professor))
								professores.remove(professor);
						});
					}
		form.setSelectProfessores(professores);
		form.setId(edital.getId());
		form.setNome(edital.getNome());
		form.setNotaMinimaAlinhamento(edital.getNotaMinimaAlinhamento());
		
		model.getModel().put("comissao", comissao);
		model.getModel().put("form", form);
		model.getModel().put("edital", edital);
		return model;
	}
	/**
	 * Ação que atualiza um edital através do id de edital passado e do tipo de comissão
	 * @param id Identificador de edital
	 * @param comissao Tipo de comissão: selecao | recursos
	 * @return
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/{id}/{comissao:selecao|recursos}/atualiza", method = RequestMethod.POST)
	public ModelAndView atualizaProfessoresDeComissao(@PathVariable int id,@PathVariable String comissao,
			@ModelAttribute("form") EditalForm form, BindingResult result, Locale locale,
			HttpServletRequest req) {
		ModelAndView model = new ModelAndView("edital/formEdicao");
		Edital edital = editalDAO.carregaEditalId(id, userDAO);
		if (edital == null)
		{
			model.setViewName("redirect:/edital/list?message=edital.form.edital.nao.encontrado");
	        return model;
		}
		if(form.getProfessoresIds() == null || form.getProfessoresIds().isEmpty() || form.getProfessoresIds().size() == 0){
			model.getModel().put("form", form);	
			model.setViewName("redirect:/edital/edit/"+ form.getId() +"?message=edital.form.comissao.erro.nao.existe.professor.na.comissao");
	        return model;
		}
		List<Usuario> professores = new ArrayList<Usuario>();
		int idAdicionado = -1;
		for(String parameter : form.getProfessoresIds()) {
				try {
					idAdicionado = Integer.parseInt(parameter);
				}catch(NumberFormatException nfe) {
					model.getModel().put("form", form);	
					model.setViewName("redirect:/edital/edit/" + form.getId() +"?message=edital.form.comissao.erro.professor.id.nao.inteiro");
					return model;
				}
				if(idAdicionado >= 0) {
					professores.add(userDAO.carregaUsuarioId(idAdicionado));
				}
		}
		if(comissao.equals(EditalComissoesConstants.COMISSAO_DE_SELECAO.getMessage())) 
			edital.setListComissaoSelecao(professores);
		else 
			if(comissao.equals(EditalComissoesConstants.COMISSAO_DE_RECURSO.getMessage()))
				edital.setListComissaoRecursos(professores);;
			
		editalDAO.atualiza(edital);
		model.setViewName("redirect:/edital/edit/" + form.getId() + "?message=edital.form.comissao.atualizada");
		return model;
	}
	//	/edital/abertura
//	/edital/inscricao/encerramento
}