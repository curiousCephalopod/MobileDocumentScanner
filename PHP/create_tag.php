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
if (isset($_POST["tag"])) {
    $tag = $_POST['tag'];
 
    // mysql inserting a new row
    $db->multi_query("INSERT INTO Tag VALUES(DEFAULT, '$tag'); SELECT LAST_INSERT_ID();");
	$result = $db->store_result();
 
    if (!empty($result)) {
        // check for empty result
		
        if ($result->num_rows > 0) {
 
            $result = $result->fetch_assoc();
			
			$tagID = $result["LAST_INSERT_ID()"];
			
            // success
            $response["success"] = 1;
 
            // user node
            $response["tagID"] = $tagID;
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no cover found
            $response["success"] = 0;
            $response["message"] = "Failed to retrieve tag ID";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no cover found
        $response["success"] = 0;
        $response["message"] = "Failed to create tag";
 
        // echo no users JSON
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