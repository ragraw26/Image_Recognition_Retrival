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
</head>
<body>
	<div class="container gallery-container">
		<h1>Image Recognition & Retrieval (IRAR)</h1>
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
			<div class="row">
				<div class="container">
					<center>
						<img height=300px; width=500px;
							src="${pageContext.request.contextPath}/resources/css/802012.jpg" />
					</center>
					<form:form action="addImage.htm" commandName="image" method="post"
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

								<p></p>
								<div style="text-align: center;">
									<input class="btn btn-danger" type="submit"
										value=" Upload Image" />
								</div>
							</div>
							<center>
								<img id='img-upload' />
							</center>
						</div>
					</form:form>
				</div>
			</div>
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

	</div>
</body>
</html>