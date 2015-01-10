<title> TriviaCast! </title>

	<head>
		<link rel="stylesheet" type="text/css" href="TriviaCastCanvas.css" media="screen" />

		<script src="jquery-1.11.1.js"></script>
		<script language="javascript" type="text/javascript" src="Trivia.js"></script>
		<script language="javascript" type="text/javascript" src="CastImitator.js"></script>
		<script language="javascript" type="text/javascript" src="TriviaGraphicsControl.js"></script>
	</head>
	<!--
	<body onload="init()">
		<div id="container">
			<div id="qbox">
				<div id="qboxtext">
				</div>
			</div>
			<div id="abox">
				<div id="aboxtext">
				</div>
				<div id="timer" style="background-color:#199E5C">Timer</div>
			</div>
		</div>
	</body>
	-->
	
	<body onload="init()">
		<div align="center">
			<canvas id="background_canvas" style="background-color: #000000; z-index: 1">
				HTML5 canvas not supported.
			</canvas>
			<canvas id="foreground_canvas" style="z-index: 2"></canvas>
			<canvas id="debug_canvas" style="background-color:#333333; opacity: 0.0; z-index: 3"></canvas>
		</div>
	</body>

	