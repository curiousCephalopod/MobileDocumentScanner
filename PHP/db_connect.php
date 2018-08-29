<?php
 
/**
 * A class file to connect to database
 * PHP Script designed and implemented by Joshua(eeu67d).
 * MySQL statements designed by Edward (eeu675).
 */
class DB_CONNECT {
 
    // constructor
    function __construct() {
        // connecting to database
        $this->connect();
    }
 
    // destructor
    function __destruct() {
        // closing db connection
        $this->close();
    }
 
    /**
     * Function to connect with database
     */
    function connect() {
        // import database connection variables
        require_once __DIR__ . '/db_config.php';
 
        // Connecting to mysql database
		$con = new mysqli(DB_SERVER, DB_USER, DB_PASSWORD, DB_DATABASE);
		if ($con->connect_errno){
			echo "Failed to connect to MySQL: (" . $mysqli->connect_errno . ") " . $mysqli->connect_error;
		}
	
        // returing connection cursor
        return $con;
    }
 
    /**
     * Function to close db connection
     */
    function close() {
        // closing db connection
        $con->close();
    }
 
}
 
?>