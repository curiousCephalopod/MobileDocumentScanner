<?php

/*
 * PHP Script designed and implemented by Joshua(eeu67d).
 * MySQL statements designed by Edward (eeu675).
 */
 
// array for JSON response
$response = array();
 
  // import database connection variables
require_once __DIR__ . '/db_config.php';
 
// connecting to db
$db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
 
$result = $db->query("SELECT * FROM Tag");

// check for empty result
if ($result->num_rows > 0) {
	// looping through all results
	$response["tags"] = array();
 
	while ($row = $result->fetch_assoc()) {
		// temp user array			
		$tag = array();
		$tag['tagID'] = $row['tagID'];
		$tag['tag'] = $row['tag'];
 
		// push single document into final response array
		array_push($response["tags"], $tag);
	}
	// success
	$response["success"] = 1;
 
	// echoing JSON response
	echo json_encode($response);
} else {
	// no documents found
	$response["success"] = 0;
	$response["message"] = "No tags found";
 
	// echo no users JSON
	echo json_encode($response);
}
?>