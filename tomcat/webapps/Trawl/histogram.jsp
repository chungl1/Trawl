<%@ page import="java.util.*, data.Record, model.TrawlExpert, search.BST, search.BasicSearchResult" %>
<head>
  <!-- Plotly.js -->
  <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
</head>

<body>
  
  <div id="histogram"><!-- Plotly chart will be drawn inside this DIV --></div>
  <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
	<script>

	<%

		TrawlExpert te = (TrawlExpert)request.getServletContext().getAttribute("trawl");
		BasicSearchResult result = te.rangeSearch(2, 1960, 2016);

		BST<Integer, Integer> histogram = result.histogram();
		Iterable<Integer> results = histogram.keys();
		out.print("var y=[];");
		out.print("var x=[];");
		for (Integer year: results){
			out.print("\ty.push("+ histogram.get(year) +");\n");
			out.print("\tx.push('"+ year +"');\n");
		}
		
	%>

	var data = [
  	{ 	x: x,
    	y: y,
    	type: 'bar',
     	marker: {
    	color: 'blue',
    	},
  	}
	];

	var layout = {
		title: 'Individual count vs Year',
    	xaxis:{title: 'Year',
    			type: 'category',
    		titlefont: {
      			family: 'Courier New, monospace',
      			size: 18,
      			color: '#7f7f7f'
    			}
    	},
    	yaxis:{title: 'Individual count',
    		titlefont: {
      			family: 'Courier New, monospace',
      			size: 18,
      			color: '#7f7f7f'
    			}
    		}
	}
	Plotly.newPlot('histogram', data,layout);  
	</script>
</body>