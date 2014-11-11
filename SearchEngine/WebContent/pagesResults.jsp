<%@ page import ="java.util.*,annotation.SearchData" %>

<%Map<SearchData,ArrayList<SearchData>> pages = (Map<SearchData,ArrayList<SearchData>>) request.getAttribute("pages"); %>

<div id="results">
	<div class="container">
		<div class="list-group">
			<%
			Set<SearchData> listKeys = pages.keySet();  // Obtenir la liste des SearchData principaux (les clés)
			Iterator<SearchData> it = listKeys.iterator();
			
			// Parcourir les cles et afficher les entrees de chaque cle;
			while(it.hasNext())
			{
				SearchData aPage = it.next();
				ArrayList<SearchData> similaryPages = pages.get(aPage);
			%>
			<div class="list-group-item">

				<div class="row">
					<div class="col-md-8 ">
						<a href="http://en.wikipedia.org/wiki/Barack_Obama">
							<h3 class="list-group-item-heading"><% out.print(aPage.title); %></h3>
						</a>
						<p class="by-author"><% out.print(aPage.url); %></p>
						<p class="list-group-item-text"><% out.print(aPage.description); %> ...
						</p>
					</div>
				</div>
				<br />
				<div class="row">
					<div class="col-md-12">
						<div class="carousel slide">

							<!-- Carousel items -->
							<div class="carousel-inner carousel-inner2">
								<div class="item active">
									<div class="row">
										<%for(SearchData sp:similaryPages){ %>
										<span class="col-md-3">
											<div>
												<a href="https://twitter.com/BarackObama"><h4><%out.print(sp.title); %></h4></a>
												<p class="by-author"><%out.print(sp.url); %></p>
											</div>
										</span> 
										<%} %>
									</div>
									<!--.row-->
								</div>
								<!--.item-->

							</div>
							<!--.carousel-inner-->
							<a data-slide="prev" href="#Carousel"
								class="left carousel-control">‹</a> <a data-slide="next"
								href="#Carousel" class="right carousel-control">›</a>
						</div>
						<!--.Carousel-->
					</div>
				</div>
			</div>
		<%} %>
		</div>
	</div>
</div>
