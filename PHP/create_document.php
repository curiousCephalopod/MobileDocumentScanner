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
 
// check for post data
if (isset($_POST["docTitle"]) && isset($_POST["userID"])) {
    $docTitle = $_POST['docTitle'];
	$userID = $_POST['userID'];
 
    // mysql inserting a new row
    $result = $db->query("INSERT INTO Document VALUES(DEFAULT, '$docTitle', 0, now(), $userID)");
	if($db->affected_rows > 0){
		
		$response["success"] = 1;
		$response["message"] = "";
		$response["docID"] = $db->insert_id;
	 
		// echo no users JSON
		echo json_encode($response);
	}else{
		$response["success"] = 0;
		$response["message"] = "";
		
		echo json_encode($response);
	}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>