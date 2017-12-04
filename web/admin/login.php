<!DOCTYPE html>
<meta charset="utf-8" />
<?php
    session_start();
    $loginId = $_POST['login_id'];
    $password = $_POST['password'];

    require_once('../dbConnect.php');

    $sql = "SELECT * FROM employee WHERE login_id='$loginId' and password='$password';";
    
    $ret = mysqli_query($con, $sql);
    if($ret){
        $count = mysqli_num_rows($ret);
    }else{
        exit();
    }

    if($count > 0){

        $row = mysqli_fetch_array($ret);
        $locationId = $row['location_id'];

        $sql2 = "SELECT * FROM location WHERE id='$locationId';";
        $ret2 = mysqli_query($con, $sql2);
        $row2 = mysqli_fetch_array($ret2);

        $locationName = $row2['name'];

        // $params = array ('location_id' => $locationId, 'location_name' => $locationName);
        
        // $query = http_build_query ($params);
        
        // // Create Http context details
        // $contextData = array ( 
        //             'method' => 'POST',
        //             'header' => "Connection: close\r\n".
        //                         "Content-Length: ".strlen($query)."\r\n",
        //             'content'=> $query );
        
        // // Create context resource for our request
        // $context = stream_context_create (array ( 'http' => $contextData ));
        
        // // Read page rendered as result of your POST request
        // $result =  file_get_contents (
        //               'http://nearby.cf/admin/home.php',  // page url
        //               false,
        //               $context);
        
        // // Server response is now stored in $result variable so you can process it
        // echo($result);

        $_SESSION['location_id'] = $locationId;
        $_SESSION['location_name'] = $locationName;

        header("Location: http://nearby.cf/admin/home.php");
        die();
    }else{
        // echo("Failed!");
        header("Location: http://nearby.cf/admin/index.html");
        die();
    }

?>