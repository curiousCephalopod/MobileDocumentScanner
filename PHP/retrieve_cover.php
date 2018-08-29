<?php
 
/*
 * Following code will get cover image
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
if (isset($_POST["docID"])) {
    $docID = $_POST['docID'];
 
    // get a cover from ImageStore
    $result = $db->query("SELECT s.imageID, image, EncryptionKey FROM ImageStore s INNER JOIN ImagePage ON ImagePage.imageID = s.imageID WHERE docID = $docID AND pageNo = 1");
 
    if (!empty($result)) {
        // check for empty result
        if ($result->num_rows > 0) {
 
            $result = $result->fetch_assoc();
 
			$imageID = $result["imageID"];
			$image = $result["image"];
			$encryptionKey = $result["EncryptionKey"];
			
            // success
            $response["success"] = 1;
 
            $response["imageID"] = $imageID;
			$response["image"] = $image;
			$response["encryptionKey"] = $encryptionKey;
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no cover found
            $response["success"] = 0;
            $response["message"] = "No product found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no cover found
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