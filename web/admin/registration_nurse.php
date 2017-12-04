<?php

    function getMilliseconds($year, $month, $day){
        if(strlen($month) <2){
            $month = "0".$month;
        }
        if(strlen($day) <2){
            $day = "0".$day;
        }
        $dob = strtotime($year.'-'.$month.'-'.$day) * 1000;
        return $dob;
    }

    require_once('../dbConnect.php');

    $nurse_id = $_POST['nurse_id'];
    $nurse_pw = $_POST['nurse_pw'];
    $nurse_email = $_POST['nurse_email'];
    $nurse_fn = $_POST['nurse_fn'];
    $nurse_ln = $_POST['nurse_ln'];
    $nurse_gender = $_POST['nurse_gender'];
    $nurse_dob1 = $_POST['nurse_dob1'];
    $nurse_dob2 = $_POST['nurse_dob2'];
    $nurse_dob3 = $_POST['nurse_dob3'];
    $dob = getMilliseconds($nurse_dob1, $nurse_dob2, $nurse_dob3);
    // echo $dob;
    $nurse_zip = $_POST['nurse_zip'];
    $nurse_address = $_POST['nurse_address'];
    $nurse_phone = $_POST['nurse_phone'];
    $nurse_license = $_POST['nurse_license'];
    $nurse_major = $_POST['nurse_major'];
    $nurse_start_date1 = $_POST['nurse_start_date1'];
    $nurse_start_date2 = $_POST['nurse_start_date2'];
    $nurse_start_date3 = $_POST['nurse_start_date3'];
    $start_date = getMilliseconds($nurse_start_date1, $nurse_start_date2, $nurse_start_date3);
    $nurse_description = $_POST['nurse_description'];
    // echo '<br>';
    $current_time = time() * 1000;
    // echo 'timestamp : ' . $timestamp;
    // echo '<br>';

    session_start();
    $locationId = $_SESSION['location_id'];
    $locationName = $_SESSION['location_name'];

    if($locationId == null || $locationId == ''){
        echo "<script>alert(\"재로그인이 필요합니다. 로그인 페이지로 이동합니다.\");</script>";
        header("Location: http://nearby.cf/admin");
        exit();
    }

    $sql = "INSERT INTO 
            employee(login_id, password, email, first_name, last_name, role, license, gender, address, zip, phone, date_of_birth, major, start_date, description, registered_date, location_id)
            VALUES('$nurse_id', '$nurse_pw', '$nurse_email', '$nurse_fn', '$nurse_ln', 'nurse', '$nurse_license', '$nurse_gender', '$nurse_address', '$nurse_zip', '$nurse_phone', '$dob', '$nurse_major', '$start_date', '$nurse_description', '$current_time', '$locationId');";

    // echo $sql;
    $ret = mysqli_query($con, $sql);
    if($ret == "1"){
        echo "<script>alert(\"성공적으로 등록하였습니다.\");</script>";
        header("Location: http://nearby.cf/admin/new_nurse.php");
    }else{
        echo "<script>alert(\"간호사를 등록하는데 실패하였습니다.\");</script>";
        header("Location: http://nearby.cf/admin/new_nurse.php");
    }

?>

