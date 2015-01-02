<title> TriviaCast! </title>

	<head>
		<link rel="stylesheet" type="text/css" href="TriviaCast.css" media="screen" />
	<!--
	<script src="TriviaCast.js"> </script>
	-->
		<script src="jquery-1.11.1.js"></script>
		<script language="javascript" type="text/javascript" src="Trivia.js"></script>
		<script language="javascript" type="text/javascript" src="Cast.js"></script>
		<script type="text/javascript" src="//www.gstatic.com/cast/sdk/libs/receiver/2.0.0/cast_receiver.js"></script>
		<script type="text/javascript" src="Cast.js"></script>

	</head>
	<!--
	<body onload="init()">
		<div align="center">
		<canvas id="canvas" width="500" height="600" style="background-color: #ACACAC">
			HTML5 canvas not supported. shit.
		</canvas>
		</div>
	</body>
	-->
	<body>
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
