<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<%@include file="/WEB-INF/views/helper/template.jsp" %>

<div id="contents">

    <!-- Campos básicos -->
	<form:form action="${pageContext.request.contextPath}/edital/projeto/save" commandName="form" method="POST" enctype="utf8" role="form">
		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		<form:hidden path="idEdital"/>
		<form:hidden path="codigoOriginal"/>
	   	
	   	<div class="mdl-grid">
	        <div class="mdl-cell mdl-cell--1-col">
	        </div>
	        
	        <div class="mdl-cell mdl-cell--10-col">
			    <div class="wide-card mdl-card mdl-shadow--2dp min-spacer-up">
			        <!-- Form header -->
			        <div class="mdl-card__title">
			            <h2 class="mdl-card__title-text">
		            			<spring:message code="edital.form.titulo.projeto.edicao"/>
			            </h2>
			        </div>
			
			        <!-- Form body -->
				    <div class="mdl-card__supporting-text">
						<div class="mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
						    <form:input path="codigo" id="projeto-codigo" class="mdl-textfield__input" type="text" value="${form.codigo}" maxlength="4"/>
			                <form:errors id="error-codigo" path="codigo" cssClass="error-block"/>				            
						    <label class="mdl-textfield__label" for="projeto-codigo"><spring:message code="edital.form.label.codigo"/>:</label>
						</div>
	              
						<div class="wide mdl-textfield mdl-js-textfield mdl-textfield--floating-label">
						    <form:input path="nome" id="projeto-nome" class="mdl-textfield__input" type="text" value="${form.nome}" maxlength="120"/>
			                <form:errors id="error-nome" path="nome" cssClass="error-block"/>				            
						    <label class="mdl-textfield__label" for="projeto-nome"><spring:message code="edital.form.label.nome"/>:</label>
						</div>
						
						<label class="mdl-checkbox mdl-js-checkbox mdl-js-ripple-effect" for="exige-prova-oral">
                            <form:checkbox path="exigeProvaOral" id="exige-prova-oral" class="mdl-checkbox__input"/>
                            <span class="mdl-checkbox__label"><spring:message code="edital.form.coluna.prova.oral"/></span>
                        </label>
                        
                        <!-- PROFESSORES -->
                        
                        <div style="margin-top: 32px;">
                            <div class="left">
                                <h4 class="list-title">
                                    <spring:message code="edital.form.projeto.professores.titulo"/>
                                </h4>
                            </div>
                            <div class="right">
  
								<form:select path="selectProfessor">
								  <c:forEach var="professor" items="${form.selectProfessores}">
						                	<form:option value="${professor.id}|${professor.nome}">${professor.nome}	
						                	</form:option>
						          </c:forEach>
								</form:select>

								<button name="addProfessor" id="addProfessor" class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" type="button" onclick="javascript:adicionaProfessor()" >
                                    <spring:message code="edital.form.projeto.professores.adicionar"/>
                                </button>
                            
                            </div>
                        </div>
                        
                        <table id="tbProfessores" class="wide mdl-data-table mdl-js-data-table mdl-shadow--2dp">
                        <tbody>
                        <c:forEach var="professor" varStatus="status" items="${form.listaProfessor}">
                        <tr>
                            <td class="mdl-data-table__cell--non-numeric">
                                <input type="hidden" value="${professor.id}" id="professoresIds" name="professoresIds"/>
                                ${professor.nome}
                            </td>
                            <td>
                               <!--<c:if test="${form.listaProfessor.size() > 1}">-->
                                    <button type="button" class="mdl-button mdl-js-button mdl-button--icon" onclick="javascript:removeProfessor(event)">
                                        <i class="material-icons">delete</i>
                                    </button>
                                <!--</c:if>-->
                            </td>
                        </tr>
                        </c:forEach>
                        </tbody>
                        </table>
                    
                    
                    
                   
                    
                    <!-- PROVAS -->
                    
                    <div style="margin-top: 32px;">
                            <div class="left">
                                <h4 class="list-title">
                                    <spring:message code="edital.form.projeto.provas.titulo"/>
                                </h4>
                            </div>
                            <div class="right">
								<form:select varStatus="status" path="selectProva">
								  <c:forEach var="prova" items="${form.selectProvas}">
						                	<form:option value="${prova.codigo}|${prova.nome}">${prova.nome}	
						                	</form:option>
						          </c:forEach>
								</form:select>                               
                                
                                <button id="addProva"class="mdl-button mdl-button--colored mdl-js-button mdl-js-ripple-effect" type="button" onclick="javascript:adicionaProva(event)">
                                    <spring:message code="edital.form.projeto.provas.adicionar"/>
                                </button>
                            </div>
                        </div>
                        
                        <table id="tbProvas" class="wide mdl-data-table mdl-js-data-table mdl-shadow--2dp">
                        <tbody>
                        <c:forEach var="prova" items="${form.provas}">
                        <tr>
                            <td class="mdl-data-table__cell--non-numeric">
                                <input id="provasCodigos" name="provasCodigos" type="hidden" value="${prova.codigo}"/>
                            	${prova.nome}
                            </td>
                            <td>
                                <!--<c:if test="${form.provas.size() > 1}">-->
                                    <button type="button" class="mdl-button mdl-js-button mdl-button--icon" onclick="javascript:removeProva(event)">
                                        <i class="material-icons">delete</i>
                                    </button>
                                <!--</c:if>-->
                            </td>
                        </tr>
                        </c:forEach>
                        </tbody>
                        </table>
                    </div>
	                   </div>
	                  
			        <!-- Form buttons -->
			        <div class="mdl-card__actions mdl-card--border">
			       	 	<div class="left">
			            </div>
			            <div class="right">
							<button type="submit" name="salva" class="mdl-button mdl-js-button mdl-button--colored mdl-button--raised mdl-js-ripple-effect">
								<spring:message code="edital.form.botao.salva"/>
							</button>
			            		<a href="${pageContext.request.contextPath}/edital/edit/${form.idEdital}">
								<button type="button" class="mdl-button mdl-js-button mdl-button--colored mdl-button--raised mdl-js-ripple-effect">
									<spring:message code="edital.form.botao.cancela"/>
								</button>
							</a>
					    </div>
			        </div>
				</div>
			</div>

	        <div class="mdl-cell mdl-cell--1-col">
	        </div>	        
	</form:form>
</div>


<script>
	function adicionaProfessor() {
		var input = document.querySelector('#selectProfessor').value;
		var split = input.split("|");
		var str1 = "<input type=\"hidden\"  value=\"";
		var str3 = "\" id=\"professoresIds\"";
		var str4 = " name=\"professoresIds\""
		var str = " autocomplete=\"off\" >";

		if (!(isNaN(parseInt(split[0])))) {
			var rows = document.getElementById('tbProfessores')
					.getElementsByTagName('tbody')[0]
					.getElementsByTagName('tr').length;

			var select = document.getElementById('selectProfessor');
			select.remove(select.selectedIndex);

			str1 = str1.concat(split[0]);
			str1 = str1.concat(str3);
			//str1 = str1.concat(rows);
			str1 = str1.concat(str4);
			//str1 = str1.concat(rows);
			var res = str1.concat(str);

			var td1 = angular.element(
					'<td class="mdl-data-table__cell--non-numeric">').append(
					res);
			td1.append(split[1])

			var button = angular
					.element('<button type="button" class="mdl-button mdl-js-button mdl-button--icon" onclick="javascript:removeProfessor(event)"><i class="material-icons">delete</i></button>');

			var td2 = angular.element('<td>').append(button);

			var tr = angular.element('<tr>').append(td1).append(td2);

			angular.element(document.querySelector('#tbProfessores > tbody'))
					.append(tr);
			var tamanho = document.getElementById("selectProfessor").length;
			if(tamanho == 0){
				 document.getElementById('selectProfessor').style.visibility = 'hidden';
				 document.getElementById('addProfessor').disabled = true;
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
			var select = document.getElementById('selectProfessor');

			var opt0 = document.createElement("option");
			var where = document.getElementById("selectProfessor").length;
			value = value.concat('|');
			value = value.concat(name);
			opt0.value = value;
			opt0.text = name;
			select.add(opt0, select.options[where]);

			target.parent().parent().parent().remove();
			var tamanho = document.getElementById("selectProfessor").length;
			if(tamanho > 0){
				 document.getElementById('selectProfessor').style.visibility = 'visible';
				 document.getElementById('addProfessor').disabled = false;
			}
		}
	}
	function adicionaProva() {
		var input = document.querySelector('#selectProva').value;
		var split = input.split("|");
		var str1 = "<input type=\"hidden\"  value=\"";
	    var str3 = "\" id=\"provasCodigos\"";
	    var str4 = " name=\"provasCodigos\""
	    var str = " autocomplete=\"off\" >";
		var codigo = split[0];
		if (!(codigo === "")) {
			var rows = document.getElementById('tbProvas')
					.getElementsByTagName('tbody')[0]
					.getElementsByTagName('tr').length;

			var select = document.getElementById('selectProva');
			select.remove(select.selectedIndex);

			
			str1 = str1.concat(split[0]);
			str1 = str1.concat(str3);
			//str1 = str1.concat(rows);
			str1 = str1.concat(str4);
			//str1 = str1.concat(rows);
			var res = str1.concat(str);

			var td1 = angular.element(
					'<td class="mdl-data-table__cell--non-numeric">').append(
					res);
			td1.append(split[1])

			var button = angular
					.element('<button type="button" class="mdl-button mdl-js-button mdl-button--icon" onclick="javascript:removeProva(event)"><i class="material-icons">delete</i></button>');

			var td2 = angular.element('<td>').append(button);

			var tr = angular.element('<tr>').append(td1).append(td2);

			angular.element(document.querySelector('#tbProvas > tbody'))
					.append(tr);
			var tamanho = document.getElementById("selectProva").length;
			if(tamanho == 0){
				 document.getElementById('selectProva').style.visibility = 'hidden';
				 document.getElementById('addProva').disabled = true;
			}
		}
	}
	function removeProva(event) {
		var target = angular.element(event.target);
		var teste = event.target.parentElement.parentElement.parentElement;
		var name = teste.firstElementChild.innerText;
		var value = teste.firstElementChild.firstElementChild.value;//

		if (value == 'undefined') {
			value = teste.firstElementChild.firstChild.value;
		}
		codigo = value;
		if (!(codigo === "")) {
			var select = document.getElementById('selectProva');

			var opt0 = document.createElement("option");
			var where = document.getElementById("selectProva").length;
			value = value.concat('|');
			value = value.concat(name);
			opt0.value = value;
			opt0.text = name;
			select.add(opt0, select.options[where]);

			target.parent().parent().parent().remove();
			var tamanho = document.getElementById("selectProva").length;
			if(tamanho > 0){
				 document.getElementById('selectProva').style.visibility = 'visible';
				 document.getElementById('addProva').disabled = false;
			}
		}
	}
</script>