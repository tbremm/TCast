<?php
	$servername = "localhost";
	$username = "trivia1";
	$password = "trivia2";
	$dbname = "dbTrivia";

	// Create connection
	$conn = new mysqli($servername, $username, $password, $dbname);

	// Check connection
	if ($conn->connect_error) {
		die("Connection failed: " . $conn->connect_error);
	} 

	//$sql = "SELECT * FROM `Basic Trivia` ORDER BY RAND() LIMIT 1";
	$sql = "SELECT * FROM `TABLE 2` ORDER BY RAND() LIMIT 1";
	$result = $conn->query($sql);

	if ($result->num_rows > 0) {
	
		$row = $result->fetch_assoc();
		/*
		echo $row["Question"]."<br>";
		echo "A: ".$row["Answer A"]."<br>";
		echo "B: ".$row["Answer B"]."<br>";
		echo "C: ".$row["Answer C"]."<br>";
		echo "D: ".$row["Answer D"]."<br>";
		*/
		echo $row["COL 2"].":";
		echo $row["COL 3"];
	} else {
		//echo "0 results";
	}
	$conn->close();
?>
	