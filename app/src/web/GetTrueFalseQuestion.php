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
	$sql = "SELECT * FROM `TABLE 2` ORDER BY RAND() LIMIT 2";
	$result = $conn->query($sql);

	if ($result->num_rows > 0) {
	
		$row1 = $result->fetch_assoc();
        $row2 = $result->fetch_assoc();

        $doTrue = rand(0,1);

		echo $row1["COL 2"].":";
		if (doTrue) {
		    echo $row1["COL 3"].":";
	    } else {
	        echo $row2["COL 3"].":";
	    }
	    echo $doTrue;
	} else {
		//echo "0 results";
	}
	$conn->close();
?>
	