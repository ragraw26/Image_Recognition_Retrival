<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ page session="true"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Image Recognition & Retrieval</title>
<script src="//code.jquery.com/jquery-1.11.0.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/font.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/image.js"></script>
<script
	src="//netdna.bootstrapcdn.com/bootstrap/3.1.1/js/bootstrap.min.js"></script>
<link
	href="//netdna.bootstrapcdn.com/bootstrap/3.1.1/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
<link href="https://fonts.googleapis.com/css?family=Droid+Sans:400,700"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/baguettebox.js/1.8.1/baguetteBox.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/css/gallery-grid.css">
<script
	src="${pageContext.servletContext.contextPath}/resources//js/font.js"></script>
<script
	src="${pageContext.servletContext.contextPath}/resources//js/bootstrap.min.js"></script>
<script
	src="${pageContext.servletContext.contextPath}/resources//js/bootstrap.js"></script>
<script>
	function move() {
		var elem = document.getElementById("myBar");
		var width = 1;
		var id = setInterval(frame, 1000000);
		function frame() {
			if (width >= 100) {
				clearInterval(id);
			} else {
				width++;
				elem.style.width = width + '%';
			}
		}
	}
</script>

</head>

<body>

	<div class="container gallery-container">
		<h1 id="text">Image Recognition & Retrieval (IRAR)</h1>
		<p></p>
		<p></p>
		<p></p>
		<p></p>
		<p></p>
		<p></p>
		<nav class="navbar navbar-inverse">
			<div class="container-fluid">
				<!-- Brand and toggle get grouped for better mobile display -->
				<div class="navbar-header">
					<ul class="nav navbar-nav navbar-left">
						<li><a href="/myneu">Home</a></li>
					</ul>
				</div>

				<ul class="nav navbar-nav navbar-right">
					<li><a href="addImage.htm">Upload</a></li>
					<li><a href="features.htm">Features</a></li>
					<li><a href="hashing.htm">Indexing</a></li>
					<li><a href="about.htm">About IRAR</a></li>
				</ul>
			</div>
		</nav>

		<div class="row">
			<c:choose>
				<c:when test="${empty requestScope.userList}">
				<center>
					<div class="row">
						<div class="container">
							<center>
								<img height=300px; width=500px;
									src="${pageContext.request.contextPath}/resources/css/802012.jpg" />
							</center>
							<form:form action="upload.htm" commandName="image" method="post"
								enctype="multipart/form-data">
								<div class="col-md-12">
									<div class="form-group">
										<label>Upload Image</label>
										<div class="input-group">
											<span class="input-group-btn"> <span
												class="btn btn-default btn-file"> Browse Image<input
													type="file" name="image" id="imgInp"
													accept=".jpg,.gif,.png,.jpeg" required="required">
											</span>
											</span> <input type="text" class="form-control" readonly
												required="required">
										</div>
										<div style="text-align: center;"
											class="checkbox checkbox-info">
											<input name="check" id="checkbox4" type="checkbox"
												value="KNearset"> <label for="checkbox4">
												K-Nearest Neighbours </label>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<input name="check" id="checkbox5" type="checkbox"
												value="KMeans"> <label for="checkbox5"> K-
												Means Clustering </label>
											&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
											<input name="check" id="checkbox6" type="checkbox"
												value="Normal"> <label for="checkbox6">
												Normal </label>
										</div>
										<p></p>
										<div style="text-align: center;">
											<input class="btn btn-danger" type="submit"
												value="Search Image" />
										</div>
									</div>
									<center>
										<img id='img-upload' />
									</center>
								</div>
							</form:form>

						</div>
					</div>
				</center>	
				</c:when>
				<c:otherwise>
					<div class="tz-gallery">
						<div class="row">
							<c:if test="${not empty requestScope.acc}">
								<div class="panel panel-primary">
									<label>Accuracy : ${requestScope.acc}</label>
								</div>
							</c:if>
							<div class="col-sm-12 col-md-12">
								<label>Image Seached</label>
								<div class="input-group">
									<span class="input-group-btn"> <span
										class="btn btn-default btn-file"> Image Path:<input
											type="file" name="image" id="imgInp"
											value="${requestScope.path}">
									</span>
									</span> <input type="text" class="form-control"
										value="${requestScope.path}" readonly>
								</div>

								<center>
									<p></p>
									<img id='img-upload' src="${requestScope.actual}" />
									<p></p>
								</center>
							</div>
							<label> Result Images</label>
							<table class="table">
								<c:forEach items="${requestScope.userList}" var="user"
									varStatus="rowCounter">
									<c:if test="${rowCounter.count % 3 == 1}">
										<tr>
									</c:if>
									<td>
										<div class="col-sm-6 col-md-4">
											<a class="lightbox" href="${user.imagePath}"><img
												class="img-rounded" height="320" width="320"
												src="${user.imagePath}" /></a>
										</div>
									</td>
									<c:if test="${rowCounter.count % 3 == 0}">
										</tr>
									</c:if>
								</c:forEach>
							</table>
						</div>
					</div>
				</c:otherwise>
			</c:choose>

		</div>


		<footer id="footer" class="navbar navbar-inverse">
			<div class="container-fluid">
				<div class="navbar-header">
					<ul class="nav navbar-nav navbar-left">
						<li>&copy; Rajat Agrawal. All rights reserved.</a>.
						</li>
					</ul>
				</div>
			</div>
		</footer>
	</div>

	<script
		src="https://cdnjs.cloudflare.com/ajax/libs/baguettebox.js/1.8.1/baguetteBox.min.js"></script>
	<script>
		baguetteBox.run('.tz-gallery');
	</script>
</body>
</html>
