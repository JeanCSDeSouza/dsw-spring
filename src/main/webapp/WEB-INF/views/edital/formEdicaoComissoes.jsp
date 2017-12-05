<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@include file="/WEB-INF/views/helper/template.jsp" %>
<script>
var id = "${id}";
</script>

<div id="contents">

    <!-- Campos básicos -->
	<form:form action="${pageContext.request.contextPath}/edital/${id}/${comissao}/atualiza" commandName="form" method="POST" enctype="utf8" role="form">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		<form:hidden path="id"/>
	   	
	   	<div class="mdl-grid">
	        <div class="mdl-cell mdl-cell--1-col">
	        </div>
	        
	        <div class="mdl-cell mdl-cell--10-col">
			    <div class="wide-card mdl-card mdl-shadow--2dp min-spacer-up">
			        <!-- Form header -->
			        <div class="mdl-card__title">
			            <h2 class="mdl-card__title-text">
		            			<spring:message code="edital.form.titulo.edicao"/>
			            </h2>
			        </div>
			
			        <!-- Form body -->
				    <div class="mdl-card__supporting-text">
						<div class="wide mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
						    <form:input path="nome" id="edital-nome" class="mdl-textfield__input" type="text" value="${form.nome}" readonly="true"/>
			                <form:errors id="error-nome" path="nome" cssClass="error-block"/>				            
						    <label class="mdl-textfield__label" for="edital-nome"><spring:message code="edital.form.label.nome"/>:</label>
						</div>
	
						<div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
						    <form:input path="notaMinimaAlinhamento" id="edital-notaMinima" class="mdl-textfield__input" type="number" pattern="[0-9]+" readonly="true"/>
		                    <form:errors id="error-notaMinima" path="notaMinimaAlinhamento" cssClass="error-block"/>
						    <label class="mdl-textfield__label" for="edital-notaMinima"><spring:message code="edital.form.label.nota.minima.alinhamento"/>:</label>
						</div>
					</div>
					
			        <!-- Form buttons -->
			        <div class="mdl-card__actions mdl-card--border">
			       	 	<div class="left">
			            </div>
			            <div class="right">
							<button type="submit" class="mdl-button mdl-js-button mdl-button--colored mdl-button--raised mdl-js-ripple-effect">
								<spring:message code="edital.form.botao.salva"/>
							</button>
			            		<a href="${pageContext.request.contextPath}/edital/list">
								<button type="button" class="mdl-button mdl-js-button mdl-button--colored mdl-button--raised mdl-js-ripple-effect">
									<spring:message code="edital.form.botao.retorna"/>
								</button>
							</a>
					    </div>
			        </div>
				</div>
			</div>

	        <div class="mdl-cell mdl-cell--1-col">
	        </div>	        
		</div>
	

				
	<!-- Comissão edit -->
   	<div class="mdl-grid">
        <div class="mdl-cell mdl-cell--1-col">
        </div>
        
        <div class="mdl-cell mdl-cell--10-col">
			<div>
				<div class="left">
					<h4 class="list-title">
						<c:if test="${comissao eq 'selecao' }">
							<spring:message code="edital.form.titulo.comissao.selecao"/>
						</c:if>
						<c:if test="${comissao eq 'recursos' }">
							<spring:message code="edital.form.titulo.comissao.recursos"/>
						</c:if>
					</h4>
				</div>
				<div class="right">
					<form:select path="selectProfessores" multiple="false">
						<c:forEach var="professor" items="${form.selectProfessores}">
							<form:option value="${professor.id}|${professor.nome}">
								${professor.nome}	
						    </form:option>
						</c:forEach>
					</form:select>
					<button name="addComissao" id="addComissao" class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" type="button" onclick="javascript:adicionaProfessor()">
                    	<spring:message code="edital.form.botao.novo.integrante"/>
                    </button>
				</div>
				<div class="clear">
				</div>
			</div>
			
			<table id="tbProfessores" class="wide mdl-data-table mdl-js-data-table mdl-shadow--2dp">
			<thead>
			<tr>
				<th class="mdl-data-table__cell--non-numeric"><spring:message code="edital.form.coluna.nome"/></th>
				<th></th>
			</tr>
			</thead>
			<tbody>
			<c:if test="${comissao eq 'selecao'}">
			<c:forEach var="professor" items="${edital.comissaoSelecao}">
			<tr>
				<td class="mdl-data-table__cell--non-numeric">
					<input type="hidden" value="${professor.id}" id="professoresIds" name="professoresIds"/>
					${professor.nome}
				</td>
				<td>
					<button type="button" class="mdl-button mdl-js-button mdl-button--icon" onclick="javascript:removeProfessor(event)">
                    	<i class="material-icons">delete</i>
                	</button>
				</td>
			</tr>
			</c:forEach>
			</c:if>
			<c:if test="${comissao eq 'recursos'}">
			<c:forEach var="professor" items="${edital.comissaoRecursos}">
			<tr>
				<td class="mdl-data-table__cell--non-numeric">
					<input type="hidden" value="${professor.id}" id="professoresIds" name="professoresIds"/>
					${professor.nome}
				</td>
				<td>
					<button type="button" class="mdl-button mdl-js-button mdl-button--icon" onclick="javascript:removeProfessor(event)">
                    	<i class="material-icons">delete</i>
                	</button>
				</td>
			</tr>
			</c:forEach>
			</c:if>
			</tbody>
			</table>
		</div>

        <div class="mdl-cell mdl-cell--1-col">
        </div>
	</div>
	</form:form>
</div>
<script>
function adicionaProfessor() {
	var input = document.querySelector('#selectProfessores').value;
	var split = input.split("|");
	var str1 = "<input type=\"hidden\"  value=\"";
	var str3 = "\" id=\"professoresIds\"";
	var str4 = " name=\"professoresIds\""
	var str = " autocomplete=\"off\" >";

	if (!(isNaN(parseInt(split[0])))) {
		var rows = document.getElementById('tbProfessores')
				.getElementsByTagName('tbody')[0]
				.getElementsByTagName('tr').length;

		var select = document.getElementById('selectProfessores');
		select.remove(select.selectedIndex);

		str1 = str1.concat(split[0]);
		str1 = str1.concat(str3);
		//str1 = str1.concat(rows);
		str1 = str1.concat(str4);
		//str1 = str1.concat(rows);
		var res = str1.concat(str);

		var td1 = angular.element('<td class="mdl-data-table__cell--non-numeric">').append(res);
		td1.append(split[1])

		var button = angular
				.element('<button type="button" class="mdl-button mdl-js-button mdl-button--icon" onclick="javascript:removeProfessor(event)"><i class="material-icons">delete</i></button>');

		var td2 = angular.element('<td>').append(button);

		var tr = angular.element('<tr>').append(td1).append(td2);

		angular.element(document.querySelector('#tbProfessores > tbody'))
				.append(tr);
		var tamanho = document.getElementById("selectProfessores").length;
		if(tamanho == 0){
			 document.getElementById('selectProfessores').style.visibility = 'hidden';
			 document.getElementById('addComissao').disabled = true;
		}
	}
}

function removeProfessor(event) {
	var target = angular.element(event.target);
	var teste = event.target.parentElement.parentElement.parentElement;
	var name = teste.firstElementChild.innerText;
	var value = teste.firstElementChild.firstElementChild.value;//
	if (value == 'undefined') {
		value = teste.firstElementChild.firstChild.value;
	}
	if (!(isNaN(parseInt(value)))) {
		var select = document.getElementById('selectProfessores');

		var opt0 = document.createElement("option");
		var where = document.getElementById("selectProfessores").length;
		value = value.concat('|');
		value = value.concat(name);
		opt0.value = value;
		opt0.text = name;
		select.add(opt0, select.options[where]);

		target.parent().parent().parent().remove();
		var tamanho = document.getElementById("selectProfessores").length;
		if(tamanho > 0){
			 document.getElementById('selectProfessores').style.visibility = 'visible';
			 document.getElementById('addComissao').disabled = false;
		}
	}
}
</script>