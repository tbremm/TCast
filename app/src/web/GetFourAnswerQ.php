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
		//$row1 = mysqli_fetch_assoc($result);
		//$row2 = mysqli_fetch_assoc($result);
		
        $row1["QUESTION"] = "What's Gabe's name?";
		$row1["A1"] = "Gabe";
		$row1["A2"] = "Tim";
		$row1["A3"] = "Regina";
		$row1["A4"] = "Paper Towels";
		$row1["ANSWER"] = "Gabe";
		
        $row2["QUESTION"] = "What's Tim's name?";
		$row2["A1"] = "Tom";
		$row2["A2"] = "Tin";
		$row2["A3"] = "Time";
		$row2["A4"] = "Tim";
		$row2["ANSWER"] = "Tim";
				
		$choose = rand(0,1);

		if ($choose == 0) {
			echo $row1["QUESTION"]."|"
					.$row1["A1"]."|"
					.$row1["A2"]."|"
					.$row1["A3"]."|"
					.$row1["A4"]."|"
					.$row1["ANSWER"];
	    } else if ($choose == 1) {
			echo $row2["QUESTION"]."|"
					.$row2["A1"]."|"
					.$row2["A2"]."|"
					.$row2["A3"]."|"
					.$row2["A4"]."|"
					.$row2["ANSWER"];
	    }
		
		//mysqli_free_result($result);
	} else {
		//echo "0 results";
	}
	$conn->close();
?>
	