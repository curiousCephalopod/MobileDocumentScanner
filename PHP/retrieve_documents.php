<?php
 
/*
 * Following code will list all the documents of a user
 * PHP Script designed and implemented by Joshua(eeu67d).
 * MySQL statements designed by Edward (eeu675).
 */
 
 // import database connection variables
require_once __DIR__ . '/db_config.php';
 
 
// array for JSON response
$response = array();

if (isset($_POST['userID'])) {
	
	$userID = $_POST['userID'];
	 
	 
	// connecting to db
	$db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
	 
	// get all documents from documents table
	$result = $db->query("SELECT docID, docTitle, noPages, dateCreated FROM Document WHERE userID = $userID");
	 
	// check for empty result
	if ($result->num_rows > 0) {
		// looping through all results
		// documents node
		$response["documents"] = array();
	 
		while ($row = $result->fetch_assoc()) {
			// temp user array			
			$document = array();
			$document['docID'] = $row['docID'];
			$document['docTitle'] = $row['docTitle'];
			$document['noPages'] = $row['noPages'];
			$document['dateCreated'] = $row['dateCreated'];
	 
			// push single document into final response array
			array_push($response["documents"], $document);
		}
		// success
		$response["success"] = 1;
	 
		// echoing JSON response
		echo json_encode($response);
	} else {
		// no documents found
		$response["success"] = 0;
		$response["message"] = "No documents found";
	 
		// echo no users JSON
		echo json_encode($response);
	}
} else {
	// field missing
	$response["success"] = 0;
	$response["message"] = "Required field(s) is missing";
	
	// Echo response
	echo json_encode($response);
}
?>