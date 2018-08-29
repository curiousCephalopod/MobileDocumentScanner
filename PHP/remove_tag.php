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
if (isset($_POST['tagID']) && isset($_POST['docID'])) {
    $tagID = $_POST['tagID'];
	$docID = $_POST['docID'];
 
 
    // connecting to db
    $db = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
 
    // mysql update row with matched pid
    $result = $db->query("DELETE FROM TagApplication WHERE tagID = $tagID AND docID = $docID");
 
    // check if row deleted or not
    if ($db->affected_rows > 0) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Tag successfully removed";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No tag found";
 
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