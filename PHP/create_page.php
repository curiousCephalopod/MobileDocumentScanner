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
if (isset($_POST["docID"]) && isset($_POST["image"]) && isset($_POST['encryptionKey']) && isset($_POST['pageNo'])) {
    $docID = $_POST['docID'];
	$image = $_POST['image'];
	$key = $_POST['encryptionKey'];
	$pageNo = $_POST['pageNo'];
 
	$result = $db->query("INSERT INTO ImageStore VALUES(DEFAULT, '$image')");
	if($db->affected_rows > 0){
		// success
		$imageID = $db->insert_id;
		
		$result = $db->query("INSERT INTO ImagePage VALUES($docID, $imageID, '$key', $pageNo)");
		if($db->affected_rows > 0){
			// success
			
			$response["success"] = 1;
			$response["messagee"] = "Added";
			
			echo json_encode($response);
		}else{
			$response["success"] = 0;
			$response["messagee"] = "Fail on ImagePage";
			
			echo json_encode($response);
		}
	}else{
		$response["success"] = 0;
		$response["messagee"] = "Fail on ImageStore";
		
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