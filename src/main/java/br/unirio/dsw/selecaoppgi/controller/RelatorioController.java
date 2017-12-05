package br.unirio.dsw.selecaoppgi.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.itextpdf.text.DocumentException;

import br.unirio.dsw.selecaoppgi.model.edital.Edital;
import br.unirio.dsw.selecaoppgi.report.PresencaProvaEscritaReport;
import br.unirio.dsw.selecaoppgi.service.dao.EditalDAO;
import br.unirio.dsw.selecaoppgi.service.dao.InscricaoDAO;
import br.unirio.dsw.selecaoppgi.service.dao.UsuarioDAO;

@Controller
public class RelatorioController {
	// /relatorio/homologacao/homologacao/original
	// /relatorio/homologacao/homologacao/recurso
	// /relatorio/homologacao/dispensa/original
	// /relatorio/homologacao/dispensa/recurso
	// /relatorio/escritas/presenca
	// /relatorio/escritas/notas/original
	// /relatorio/escritas/notas/recurso
	// /relatorio/escritas/pendencias
	// /relatorio/alinhamento/presenca
	// /relatorio/alinhamento/notas/original
	// /relatorio/alinhamento/notas/recurso
	// /relatorio/alinhamento/pendencias
	// /relatorio/aprovacao

	@Autowired
	private InscricaoDAO inscricaoDAO;
	
	@Autowired
	private UsuarioDAO userDAO;

	@Autowired
	private EditalDAO editalDAO;

	/**
	 * Gera o relatório de prova escrita contendo todos os alunos elegíveis a fazê-la 
	 * @param id Edital
	 * @param response
	 * @throws DocumentException
	 * @throws IOException
	 */
	@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/edital/{id}/relatorio/escritas/presenca", method = RequestMethod.GET)
	public void presencaProvaEscrita(@PathVariable int id, HttpServletResponse response)
			throws DocumentException, IOException {
		Edital edital = editalDAO.carregaEditalId(id, userDAO);
		PresencaProvaEscritaReport report = new PresencaProvaEscritaReport();
		
		report.generatePdf(edital, inscricaoDAO, response);
	}

}