package br.unirio.dsw.selecaoppgi.utils;

public enum EditalComissoesConstants {
	COMISSAO_DE_SELECAO("selecao"),
	COMISSAO_DE_RECURSO("recursos"),
	RETURN_PREFIX("STATUS"),
	BAD_REQUEST("BAD_REQUEST"),
	OK_REQUEST("OK");
	
	private String message;
	EditalComissoesConstants(String message){
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
