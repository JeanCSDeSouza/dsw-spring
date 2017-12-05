package br.unirio.dsw.selecaoppgi.report;

import javax.servlet.http.HttpServletResponse;

import br.unirio.dsw.selecaoppgi.model.edital.Edital;
import br.unirio.dsw.selecaoppgi.service.dao.InscricaoDAO;

public interface Report {
	public void generatePdf(Edital edital, InscricaoDAO inscricaoDAO, HttpServletResponse response);
}
