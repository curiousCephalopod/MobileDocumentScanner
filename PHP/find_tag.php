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

    $result = $db->query("SELECT t.tagID FROM Tag t WHERE t.tag = '$tag'");
 
    if (!empty($result)) {
        // check for empty result
        if ($result->num_rows > 0) {
 
            $result = $result->fetch_assoc();
 
			$tagID = $result["tagID"];
			
            // success
            $response["success"] = 1;
 
            // user node
            $response["tagID"] = $tagID;
			$response["tag"] = $tag;
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no tag found
            $response["success"] = 0;
            $response["message"] = "No tag found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no tag found
        $response["success"] = 0;
        $response["message"] = "Empty";
 
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