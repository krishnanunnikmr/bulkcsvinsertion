<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Bulk Upload</title>
<link href="<c:url value='/css/bootstrap.css' />" rel="stylesheet"></link>
<link href="<c:url value='/css/app.css' />" rel="stylesheet"></link>
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.2/jquery.min.js"></script>
</head>

<body style="background-color: #ecf2e7;">

	<div class="container">

		<div class="panel panel-default" style="background-color: #ecf2e7;">

			<!-- 	<div class="panel-heading">
				<span class="lead" style="align-content: center">Clustered Data WareHouse</span>
				<span class="lead" style="text-align: center;">Clustered Data WareHouse</span>
			</div> -->
			<div class="well well-sm" style="background-color: #708291;"></div>
			<div class="">
				<h1 style="text-align: center;">Clustered Data WareHouse</h1>

			</div>
			<div class="well well-sm" style="background-color: #708291;"></div>
			<div class="uploadcontainer" style="margin-top: 25px;">
				<form:form method="POST" modelAttribute="bulkUpload" action="bulkUpload"
					enctype="multipart/form-data" class="form-horizontal">

					<div class="row">
						<div id="fileTable" style="margin-left: 300px;">
							<div class="row">
								<div class="col-md-3">
									<label style="margin-left: 10px; margin-top: 5px;">Upload
										a document(.csv)</label>
								</div>
								<div class="col-md-6">
									<input type="file" name="file" id="file"
										class="form-control input-sm" />
								</div>
								<div class="has-error">
									<form:errors path="file" class="help-inline" />
								</div>
							</div>
							<!-- //////// -->
							<div>
								<br>
							</div>
							<div class="row">
								<div class="col-md-1" style="margin-left: 225px;">
									<input type="submit" value="Upload"
										class="btn btn-primary btn-sm">
								</div>

								<div class="col-md-4">
									<c:forEach var="message" items="${messages}">
										<span style="color: Green;">${message.value}</span>
									</c:forEach>
								</div>
							</div>


							<div class="col-md-2"></div>

						</div>

					</div>
					
						<div class="row">
						<c:if test="${not empty duration}">
						<div class="col-md-12">
					<p><h2>Upload Summery </h2></p>
				</hr>
					
										<h4><span style="color: Green;">${duration}</span></h4>
										<h4><span style="color: Green;">${validDeals}</span></h4>
										<h4><span style="color: Green;">${invalidDeals}</span></h4>
									
						</div>
						</c:if>
						</div>
						
			</form:form>
		</div>
	</div>
	</div>
</body>
</html>