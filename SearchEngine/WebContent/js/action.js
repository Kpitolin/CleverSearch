$(document).ready(function(){

	$('form').submit(function(e){
		e.preventDefault();
		var input = $("#search-form").val();

		//modification de la page
		$('#logo').slideUp(400);
		$('#logo-min').show().addClass('logo-header');
		$('#search-wrap').css('padding-top',20).removeClass('container');
		$('#search-box').removeClass().addClass('col-md-5 search-header');
		$('.results').show();
		$('#loading').append("<img src='ajax-loader.gif'> Chargement...");

		//envoi de la requete ajax
		$.post("LaunchSearchServlet",{action:"search",q:input,label:""})
				.done(function(dataHtml){
					$('#results').append(dataHtml);
					$.post("GenerateGraphServlet")
						.done(function(jsonData){
							$('#loading').hide();
							var nodes = jsonData.nodes;
							var links = jsonData.links;
							similarityGraph(nodes,links);
						});
				});
	});
});

function similarityGraph(nodes,links){
	var color = d3.scale.category20();

	var width = 370,
		height = 500;

	var svg = d3.select("#viz").append("svg")
		.attr("width", width)
		.attr("height", height);

	var force = d3.layout.force()
		  .size([width, height])
		  .charge(-120)
		  .linkDistance(40)
		  .nodes(nodes)
		  .links(links)
		  .start();

	  var link = svg.selectAll(".link")
		  .data(links)
		  .enter().append("line")
		  .attr("class", "link");

	  var node = svg.selectAll(".node")
		  .data(nodes)
		  .enter().append("circle")
		  .attr("class", "node")
		  .attr("r", 5)
		  .style("fill", function(d) { return color(1); })
		  .call(force.drag);

	  node.append("title")
		  .text(function(d) { return d.name; });

	  force.on("tick", function() {
		link.attr("x1", function(d) {return d.source.x; })
			.attr("y1", function(d) { return d.source.y; })
			.attr("x2", function(d) { return d.target.x; })
			.attr("y2", function(d) { return d.target.y; });

		node.attr("cx", function(d) { return d.x; })
			.attr("cy", function(d) { return d.y; });
	  });
}

/*FORCE DIAGRAPH*/
function goToTheMiddle(){
	var width = 370,
		height = 500,
		nodes = [];

	var svg = d3.select("#viz").append("svg")
		.attr("width", width)
		.attr("height", height);

	var force = d3.layout.force()
		.charge(-30)
		.size([width, height])
		.nodes(nodes)
		.links([]);

	force.on("tick", function(e) {
		  svg.selectAll("path")
		  .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; });
	});

	setInterval(function(){
	  // Add a new random shape.
	  nodes.push({
		type: d3.svg.symbolTypes[~~(Math.random() * d3.svg.symbolTypes.length)],
		size: Math.random() * 300 + 100
	  });

	  // Restart the layout.
	  force.start();

	  svg.selectAll("path")
		  .data(nodes)
		  .enter().append("path")
		  .attr("transform", function(d) { return "translate(" + d.x + "," + d.y + ")"; })
		  .attr("d", d3.svg.symbol()
		  .size(function(d) { return d.size; })
		  .type(function(d) { return d.type; }))
		  .style("fill", "steelblue")
		  .style("stroke", "white")
		  .style("stroke-width", "1.5px")
		  .call(force.drag);

	}, 1000);
}