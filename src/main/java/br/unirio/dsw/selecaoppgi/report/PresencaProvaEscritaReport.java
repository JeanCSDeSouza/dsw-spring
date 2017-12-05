package br.unirio.dsw.selecaoppgi.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import br.unirio.dsw.selecaoppgi.model.edital.Edital;
import br.unirio.dsw.selecaoppgi.model.edital.ProvaEscrita;
import br.unirio.dsw.selecaoppgi.model.inscricao.InscricaoEdital;
import br.unirio.dsw.selecaoppgi.service.dao.InscricaoDAO;

public class PresencaProvaEscritaReport implements Report{

	@Override
	public void generatePdf(Edital edital, InscricaoDAO inscricaoDAO, HttpServletResponse response) {
		List<ProvaEscrita> provasEscritas = (List<ProvaEscrita>) edital.getProvasEscritas();
		List<InscricaoEdital> inscritos = inscricaoDAO.carregaInscricoesEditalAcessoPublico(edital);

		Document document = new Document();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			PdfWriter.getInstance(document, baos);
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}

		document.open();
		document.addTitle("Lista-de-presença-em-prova-escrita");
		
		Font chapterFont = FontFactory.getFont(FontFactory.HELVETICA, 16, Font.BOLD);
		Font paragraphFont = FontFactory.getFont(FontFactory.HELVETICA, 14, Font.BOLD);
		
		final String QUEBRA_DE_LINHA = "\n";
		final String QUEBRA_DE_LINHA_DUPLA = "\n\n";
		final String ESPACO_PARA_ASSINATURA = " _________________________";
		
		Chunk chunk = new Chunk("Lista de presença em prova escrita - " + edital.getNome() + QUEBRA_DE_LINHA_DUPLA, chapterFont);

		int indice = 1;
		for(ProvaEscrita provaEscrita : provasEscritas) {
			Chapter chapter = new Chapter(new Paragraph(chunk), indice++);

			chapter.add(new Paragraph(provaEscrita.getNome().toUpperCase() + QUEBRA_DE_LINHA, paragraphFont));
			inscritos.forEach(inscrito -> {
				inscrito.getAvaliacoesProvasEscritas().forEach(provaDoInscrito -> {
					if(provaEscrita.getCodigo().equals(provaDoInscrito.getProvaEscrita().getCodigo())) {
						chapter.add(new Paragraph(inscrito.getNomeCandidato() + ESPACO_PARA_ASSINATURA));
					}
				});
			});
			
			if(chapter.size()==1) { // só tem o nome da prova(nao tem alunos inscritos)
				indice-=1;
				continue;
			}
				try {
					document.add(chapter);
					document.newPage();
				} catch (DocumentException e) {
					e.printStackTrace();
				}
		}

		document.close();

		response.setHeader("Expires", "0");
		response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		response.setHeader("Pragma", "public");
		response.setContentType("application/pdf");
		response.setContentLength(baos.size());
		OutputStream os;
		try {
			os = response.getOutputStream();
			baos.writeTo(os);
			os.flush();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
