<?php
 
/*
 * PHP Script designed and implemented by Joshua(eeu67d).
 * MySQL statements designed by Edward (eeu675).
 */
 
// array for JSON response
$response = array();

 // import database connection variables
require_once __DIR__ . '/db_config.php';
 
// check for required fields
if (isset($_POST['pageNo']) && isset($_POST['docID'])) {
    $pageNo = $_POST['pageNo'];
	$docID = $_POST['docID'];
 
    // connecting to db
    $db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
 
    // mysql update row with matched pid
    if(!$db->query("DELETE FROM ImagePage WHERE docID = $docID AND pageNo = $pageNo")){
		$response["success"] = 0;
        $response["message"] = $db->error;
	}else{
		if($db->affected_rows > 0){
			
			if(!$db->query("UPDATE ImagePage SET pageNo = pageNo - 1 WHERE pageNo > $pageNo AND docID = $docID")){
				$response["success"] = 1;
				$response["message"] = "Failed update";
			}else{
				// success
				$response["success"] = 1;
				$response["message"] = "Success";

				// echoing JSON response
				echo json_encode($response);
			}
		}else{
			$response["success"] = 0;
			$response["message"] = "No affected rows";
		}
	}
	
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>