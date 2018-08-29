<?php
 
/*
 * Following code will apply a tag
 * All details are read from HTTP Post Request
 * PHP Script designed and implemented by Joshua(eeu67d).
 * MySQL statements designed by Edward (eeu675).
 */
 
// array for JSON response
$response = array();

 // import database connection variables
require_once __DIR__ . '/db_config.php';
 
// check for required fields
if (isset($_POST['docID']) && isset($_POST['tagID'])) {
 
    $docID = $_POST['docID'];
	$tagID = $_POST['tagID'];
 
    // connecting to db
    $db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
 
    // mysql inserting a new row
    $result = $db->query("INSERT INTO TagApplication VALUES('$tagID', '$docID')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Tag successfully applied.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
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