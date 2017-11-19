<?php

	function replaceSqlString($s1){

		$s1 = str_replace("\"", "\\\\\"", $s1);
		$s1 = str_replace("'", "\\'", $s1);
		$s1 = str_replace("\\n", "\\\\n", $s1);

		return $s1;
	}

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

	ini_set('max_execution_time','600');
	ini_set('max_input_time','600');
	ini_set('memory_limit','100');
	ini_set('post_max_size','10M');
	ini_set('upload_max_filesize','10M');

	require_once('dbConnect.php');

    $service = $_POST['service'];

	if($service == null){
		echo "error";
	}else{

		if($service == 'login_employee'){

			$login_id = $_POST['login_id'];
			$login_pw = $_POST['login_pw'];

			// $sql = "SELECT * FROM employee WHERE id like '$login_id' and password like '$login_pw';";
			$sql = "SELECT A.*, B.name as location_name, B.pic as location_pic, B.director as location_director, B.capacity as location_capacity, B.major as location_major, B.construction_year as location_construction_year, B.phone as location_phone, B.url as location_url
					FROM employee as A LEFT OUTER JOIN (
					SELECT *
					FROM location
					GROUP BY id) as B
					ON (B.id = A.location_id)
					WHERE A.login_id like '$login_id' and A.password like '$login_pw'
					GROUP BY A.id;";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$login_id = $row['login_id'];
				$email = $row['email'];
				$first_name = $row['first_name'];
				$last_name = $row['last_name'];
				$role = $row['role'];
				$license = $row['license'];
				$gender = $row['gender'];
				$address = $row['address'];
				$zip = $row['zip'];
				$phone = $row['phone'];
				$pic = $row['pic'];
				$date_of_birth = $row['date_of_birth'];
				$major = $row['major'];
				$start_date = $row['start_date'];
				$description = $row['description'];
				$registered_date = $row['registered_date'];

				$location_id = $row['location_id'];
				$location_name = $row['location_name'];
				$location_pic = $row['location_pic'];
				$location_director = $row['location_director'];
				$location_capacity = $row['location_capacity'];
				$location_major = $row['location_major'];
				$location_construction_year = $row['location_construction_year'];
				$location_phone = $row['location_phone'];
				$location_url = $row['location_url'];

				echo "{\"id\":\"$id\",
				\"login_id\":\"$login_id\",
				\"email\":\"$email\",
				\"first_name\":\"$first_name\",
				\"last_name\":\"$last_name\",
				\"role\":\"$role\",
				\"license\":\"$license\",
				\"gender\":\"$gender\",
				\"address\":\"$address\",
				\"zip\":\"$zip\",
				\"phone\":\"$phone\",
				\"pic\":\"$pic\",
				\"date_of_birth\":\"$date_of_birth\",
				\"major\":\"$major\",
				\"start_date\":\"$start_date\",
				\"description\":\"$description\",
				\"registered_date\":\"$registered_date\",
				\"location_id\":\"$location_id\",
				\"location_name\":\"$location_name\",
				\"location_pic\":\"$location_pic\",
				\"location_director\":\"$location_director\",
				\"location_capacity\":\"$location_capacity\",
				\"location_major\":\"$location_major\",
				\"location_construction_year\":\"$location_construction_year\",
				\"location_phone\":\"$location_phone\",
				\"location_url\":\"$location_url\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == 'getEmployeeInfo'){
			
			$employee_id = $_POST['employee_id'];

			// $sql = "SELECT * FROM employee WHERE id like '$login_id' and password like '$login_pw';";
			$sql = "SELECT A.*, B.name as location_name, B.pic as location_pic, B.director as location_director, B.capacity as location_capacity, B.major as location_major, B.construction_year as location_construction_year, B.phone as location_phone, B.url as location_url
					FROM employee as A LEFT OUTER JOIN (
					SELECT *
					FROM location
					GROUP BY id) as B
					ON (B.id = A.location_id)
					WHERE A.id = '$employee_id' 
					GROUP BY A.id;";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$login_id = $row['login_id'];
				$email = $row['email'];
				$first_name = $row['first_name'];
				$last_name = $row['last_name'];
				$role = $row['role'];
				$license = $row['license'];
				$gender = $row['gender'];
				$address = $row['address'];
				$zip = $row['zip'];
				$phone = $row['phone'];
				$pic = $row['pic'];
				$date_of_birth = $row['date_of_birth'];
				$major = $row['major'];
				$start_date = $row['start_date'];
				$description = $row['description'];
				$registered_date = $row['registered_date'];

				$location_id = $row['location_id'];
				$location_name = $row['location_name'];
				$location_pic = $row['location_pic'];
				$location_director = $row['location_director'];
				$location_capacity = $row['location_capacity'];
				$location_major = $row['location_major'];
				$location_construction_year = $row['location_construction_year'];
				$location_phone = $row['location_phone'];
				$location_url = $row['location_url'];

				echo "{\"id\":\"$id\",
				\"login_id\":\"$login_id\",
				\"email\":\"$email\",
				\"first_name\":\"$first_name\",
				\"last_name\":\"$last_name\",
				\"role\":\"$role\",
				\"license\":\"$license\",
				\"gender\":\"$gender\",
				\"address\":\"$address\",
				\"zip\":\"$zip\",
				\"phone\":\"$phone\",
				\"pic\":\"$pic\",
				\"date_of_birth\":\"$date_of_birth\",
				\"major\":\"$major\",
				\"start_date\":\"$start_date\",
				\"description\":\"$description\",
				\"registered_date\":\"$registered_date\",
				\"location_id\":\"$location_id\",
				\"location_name\":\"$location_name\",
				\"location_pic\":\"$location_pic\",
				\"location_director\":\"$location_director\",
				\"location_capacity\":\"$location_capacity\",
				\"location_major\":\"$location_major\",
				\"location_construction_year\":\"$location_construction_year\",
				\"location_phone\":\"$location_phone\",
				\"location_url\":\"$location_url\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}
		else if($service == "login_supporter"){

			$login_id = $_POST['login_id'];
			$login_pw = $_POST['login_pw'];

			$sql = "SELECT * FROM user WHERE login_id='$login_id' AND password='$login_pw';";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$login_id = $row['login_id'];
				$email = $row['email'];
				$first_name = $row['first_name'];
				$last_name = $row['last_name'];
				$gender = $row['gender'];
				$address = $row['address'];
				$zip = $row['zip'];
				$phone = $row['phone'];
				$pic = $row['pic'];
				$date_of_birth = $row['date_of_birth'];
				$registered_date = $row['registered_date'];

				echo "{\"id\":\"$id\",
				\"login_id\":\"$login_id\",
				\"email\":\"$email\",
				\"first_name\":\"$first_name\",
				\"last_name\":\"$last_name\",
				\"gender\":\"$gender\",
				\"address\":\"$address\",
				\"zip\":\"$zip\",
				\"phone\":\"$phone\",
				\"pic\":\"$pic\",
				\"date_of_birth\":\"$date_of_birth\",
				\"registered_date\":\"$registered_date\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}
		else if($service == 'registerSupporter'){

			$supporter_id = $_POST['supporter_id'];
			$supporter_password = $_POST['supporter_password'];
			$supporter_email = $_POST['supporter_email'];
			$supporter_fn = $_POST['supporter_fn'];
			$supporter_ln = $_POST['supporter_ln'];
			$supporter_gender = $_POST['supporter_gender'];
			$supporter_role = $_POST['supporter_role'];
			$dob = $_POST['dob'];
			// echo $dob;
			$supporter_zip = $_POST['supporter_zip'];
			$register_date = $_POST['register_date'];
			$supporter_address = $_POST['supporter_address'];
			$supporter_phone = $_POST['supporter_phone'];
			$supporter_description = $_POST['supporter_description'];
			// echo '<br>';
			$current_time = time() * 1000;
			// echo 'timestamp : ' . $timestamp;
			// echo '<br>';

			$sql = "INSERT INTO
					user(login_id,password, email, first_name, last_name, role, gender, address, zip, phone, date_of_birth, registered_date)
					VALUES('$supporter_id','$supporter_password','$supporter_email','$supporter_fn', '$supporter_ln','$supporter_role','$supporter_gender', '$supporter_address', '$supporter_zip', '$supporter_phone', '$dob', '$register_date');";
			echo $sql;
			// echo $sql;
			$ret = mysqli_query($con, $sql);
			if($ret == '1'){
				echo json_encode(array('status'=>'success', 'message'=>"save success"));
			}else{
				echo json_encode(array('status'=>'fail', 'message'=>"save fail"));
			}

		}
		else if($service == 'registerPatient'){

			$patient_fn = $_POST['patient_fn'];
			$patient_ln = $_POST['patient_ln'];
			$patient_gender = $_POST['patient_gender'];
			$dob = $_POST['dob'];
			$patient_hegiht = $_POST['patient_height'];
			$patient_bla = (double)$_POST['patient_bla'];
			$location_id = $_POST['location_id'];
			// echo $dob;
			$patient_zip = $_POST['patient_zip'];
			$start_date = $_POST['start_date'];
			$register_date = $_POST['register_date'];
			$patient_address = $_POST['patient_address'];
			$patient_phone = $_POST['patient_phone'];
			$patient_license = $_POST['patient_license'];
			$patient_major = $_POST['patient_major'];
			$start_date = 1;
			$patient_description = $_POST['patient_description'];
			// echo '<br>';
			$current_time = time() * 1000;
			// echo 'timestamp : ' . $timestamp;
			// echo '<br>';

			$sql = "INSERT INTO
					patient(first_name, last_name, gender, address, zip, phone, date_of_birth, height, basic_living_allowance, start_date, description, registered_date, location_id)
					VALUES('$patient_fn', '$patient_ln', '$patient_gender', '$patient_address', '$patient_zip', '$patient_phone', '$dob', '$patient_hegiht', '$patient_bla', '$start_date', '$patient_description', '$register_date', '$location_id');";
			echo $sql;
			// echo $sql;
			$ret = mysqli_query($con, $sql);
			if($ret == '1'){
				echo json_encode(array('status'=>'success', 'message'=>"save success"));
			}else{
				echo json_encode(array('status'=>'fail', 'message'=>"save fail"));
			}

		}
		else if($service == 'ValidateInfo'){
			$supporter_id = $_POST['supporter_id'];
			$sql = "SELECT * FROM user WHERE user.patient_id='$supporter_id';";
			echo $sql;
			// echo $sql;
			$ret = mysqli_query($con, $sql);
			if($ret == '1'){
				echo json_encode(array('status'=>'success', 'message'=>"cannot"));
			}else{
				echo json_encode(array('status'=>'fail', 'message'=>"can"));
			}
		}
		else if($service == 'ModifyPatientRegisterInfo'){
			$patient_id = $_POST['patient_id'];
			$patient_fn = $_POST['patient_fn'];
			$patient_ln = $_POST['patient_ln'];
			$patient_gender = $_POST['patient_gender'];
			$dob = $_POST['dob'];
			$patient_hegiht = $_POST['patient_height'];
			$patient_bla = (double)$_POST['patient_bla'];
			$location_id = $_POST['location_id'];
			// echo $dob;
			$patient_zip = $_POST['patient_zip'];
			$start_date = $_POST['start_date'];
			$patient_address = $_POST['patient_address'];
			$patient_phone = $_POST['patient_phone'];
			$patient_license = $_POST['patient_license'];
			$patient_major = $_POST['patient_major'];
			$start_date = $_POST['start_date'];
			$patient_description = $_POST['patient_description'];
			// echo '<br>';
			$current_time = time() * 1000;
			// echo 'timestamp : ' . $timestamp;
			// echo '<br>';

			$sql = "UPDATE patient SET first_name='$patient_fn', last_name='$patient_ln', gender='$patient_gender', address='$patient_address', zip='$patient_zip', phone='$patient_phone', date_of_birth='$dob', height='$patient_hegiht', basic_living_allowance='$patient_bla', start_date='$start_date', description='$patient_description' WHERE patient.id='$patient_id';";
			// echo $sql;
			// echo $sql;
			$ret = mysqli_query($con, $sql);
			if($ret == '1'){
				echo json_encode(array('status'=>'success', 'message'=>"save success"));
			}else{
				echo json_encode(array('status'=>'fail', 'message'=>"save fail"));
			}

		}
		else if($service == 'ModifyNurseInfo'){
		  $nurse_id = $_POST['nurse_id'];
		  $nurse_fn = $_POST['nurse_fn'];
		  $nurse_ln = $_POST['nurse_ln'];
		  $nurse_gender = $_POST['nurse_gender'];
		  $dob = $_POST['dob'];
		  $location_id = $_POST['location_id'];
		  // echo $dob;
		  $nurse_zip = $_POST['nurse_zip'];
		  $start_date = $_POST['start_date'];
		  $nurse_address = $_POST['nurse_address'];
		  $nurse_phone = $_POST['nurse_phone'];
		  $nurse_license = $_POST['nurse_license'];
		  $nurse_major = $_POST['nurse_major'];
		  $nurse_email = $_POST['nurse_email'];
		  // echo '<br>';
		  $current_time = time() * 1000;
		  // echo 'timestamp : ' . $timestamp;
		  // echo '<br>';

		  $sql = "UPDATE employee SET first_name='$nurse_fn', last_name='$nurse_ln', gender='$nurse_gender', email='$nurse_email', address='$nurse_address', zip='$nurse_zip', phone='$nurse_phone', date_of_birth='$dob', start_date='$start_date', major='$nurse_major', license='$nurse_license' WHERE employee.id='$nurse_id';";
		  // echo $sql;
		  // echo $sql;
		  $ret = mysqli_query($con, $sql);
		  if($ret == '1'){
		    echo json_encode(array('status'=>'success', 'message'=>"save success"));
		  }else{
		    echo json_encode(array('status'=>'fail', 'message'=>"save fail"));
		  }

		}
		else if($service == 'getPatientList'){

			$name = $_POST['name'];
			$location_id = $_POST['location_id'];

			$page = $_POST['page'];
			$page = $page*30;

			//$sql = "SELECT * FROM Patient WHERE location_id='$location_id'";
			if($name == null || $name == ''){
				$sql = "SELECT A.*, B.name as location_name, B.pic as location_pic, B.director as location_director, B.capacity as location_capacity, B.major as location_major, B.construction_year as location_construction_year, B.phone as location_phone, B.url as location_url
				FROM patient as A LEFT OUTER JOIN (
				SELECT *
				FROM location
				GROUP BY id) as B
				ON (B.id = A.location_id)
				WHERE A.location_id like '$location_id'
				GROUP BY A.id";
			}else{
				$sql = "SELECT A.*, B.name as location_name, B.pic as location_pic, B.director as location_director, B.capacity as location_capacity, B.major as location_major, B.construction_year as location_construction_year, B.phone as location_phone, B.url as location_url
				FROM patient as A LEFT OUTER JOIN (
				SELECT *
				FROM location
				GROUP BY id) as B
				ON (B.id = A.location_id)
				WHERE A.location_id like '$location_id' and (A.first_name like '%$name%' or A.last_name like '%$name%' or CONCAT(A.first_name, A.last_name) like '%$name%' or CONCAT(A.last_name, A.first_name) like '%$name%')
				GROUP BY A.id";
			}

			$sql = $sql." ORDER BY id DESC LIMIT $page, 30;";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$first_name = $row['first_name'];
				$last_name = $row['last_name'];
				$gender = $row['gender'];
				$address = $row['address'];
				$zip = $row['zip'];
				$phone = $row['phone'];
				$pic = $row['pic'];
				$date_of_birth = $row['date_of_birth'];
				$height = $row['height'];
				$bla = $row['basic_living_allowance'];
				$start_date = $row['start_date'];
				$description = $row['description'];
				$registered_date = $row['registered_date'];

				$location_id = $row['location_id'];
				$location_name = $row['location_name'];
				$location_pic = $row['location_pic'];
				$location_director = $row['location_director'];
				$location_capacity = $row['location_capacity'];
				$location_major = $row['location_major'];
				$location_construction_year = $row['location_construction_year'];
				$location_phone = $row['location_phone'];
				$location_url = $row['location_url'];

				echo "{\"id\":\"$id\",
				\"first_name\":\"$first_name\",
				\"last_name\":\"$last_name\",
				\"gender\":\"$gender\",
				\"address\":\"$address\",
				\"zip\":\"$zip\",
				\"phone\":\"$phone\",
				\"pic\":\"$pic\",
				\"date_of_birth\":\"$date_of_birth\",
				\"height\":\"$height\",
				\"basic_living_allowance\":\"$bla\",
				\"start_date\":\"$start_date\",
				\"description\":\"$description\",
				\"registered_date\":\"$registered_date\",
				\"location_id\":\"$location_id\",
				\"location_name\":\"$location_name\",
				\"location_pic\":\"$location_pic\",
				\"location_director\":\"$location_director\",
				\"location_capacity\":\"$location_capacity\",
				\"location_major\":\"$location_major\",
				\"location_construction_year\":\"$location_construction_year\",
				\"location_phone\":\"$location_phone\",
				\"location_url\":\"$location_url\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == "getPatientMedicineList"){

			$patient_id = $_POST['patient_id'];
			$isAvailable = $_POST['isAvailable'];
			$current_time = time() * 1000;

			$page = $_POST['page'];
			$page = $page*30;

			$sql = "select * from patient_medicine WHERE patient_id = '$patient_id' ";
			if($isAvailable == '1'){
				$sql = $sql."and finish_date >= '$current_time' ";
			}
			$sql = $sql."ORDER BY id DESC LIMIT $page, 30;";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$title = $row['title'];
				$start_date = $row['start_date'];
				$finish_date = $row['finish_date'];
				$patient_id = $row['patient_id'];
				$registered_date = $row['registered_date'];

				echo "{\"id\":\"$id\",
				\"title\":\"$title\",
				\"start_date\":\"$start_date\",
				\"finish_date\":\"$finish_date\",
				\"patient_id\":\"$patient_id\",
				\"registered_date\":\"$registered_date\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == "getPatientMedicineListWithID"){

			$patient_medicine_id = $_POST['patient_medicine_id'];

			$sql = "select * from patient_medicine WHERE id = '$patient_medicine_id';";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$title = $row['title'];
				$start_date = $row['start_date'];
				$finish_date = $row['finish_date'];
				$patient_id = $row['patient_id'];
				$registered_date = $row['registered_date'];

				echo "{\"id\":\"$id\",
				\"title\":\"$title\",
				\"start_date\":\"$start_date\",
				\"finish_date\":\"$finish_date\",
				\"patient_id\":\"$patient_id\",
				\"registered_date\":\"$registered_date\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == "getPatientMedicineDetailList"){

			$patient_medicine_id = $_POST['patient_medicine_id'];

			$sql = "SELECT A.*, B.id as medicine_id, B.type as medicine_type, B.code as medicine_code, B.name as medicine_name, B.company as medicine_company, B.standard as medicine_standard, B.unit as medicine_unit
					FROM patient_medicine_detail as A LEFT OUTER JOIN (
					SELECT *
					FROM medicine
					GROUP BY id) as B
					ON (B.id = A.medicine_id)
					WHERE A.patient_medicine_id like '$patient_medicine_id'
					GROUP BY A.id;";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$patient_medicine_id = $row['patient_medicine_id'];
				$medicine_id = $row['medicine_id'];
				$sd = $row['single_dose'];
				$ndd = $row['num_of_daily_dose'];
				$tdd = $row['total_dosing_days'];
				$description = $row['description'];
				$time = $row['time'];
				$medicine_type = $row['medicine_type'];
				$medicine_code = $row['medicine_code'];
				$medicine_name = $row['medicine_name'];
				$medicine_company = $row['medicine_company'];
				$medicine_standard = $row['medicine_standard'];
				$medicine_unit = $row['medicine_unit'];

				echo "{\"id\":\"$id\",
				\"patient_medicine_id\":\"$patient_medicine_id\",
				\"sd\":\"$sd\",
				\"ndd\":\"$ndd\",
				\"tdd\":\"$tdd\",
				\"description\":\"$description\",
				\"time\":\"$time\",
				\"medicine_id\":\"$medicine_id\",
				\"medicine_type\":\"$medicine_type\",
				\"medicine_code\":\"$medicine_code\",
				\"medicine_name\":\"$medicine_name\",
				\"medicine_company\":\"$medicine_company\",
				\"medicine_standard\":\"$medicine_standard\",
				\"medicine_unit\":\"$medicine_unit\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == "getMedicineList"){

			$name = $_POST['name'];

			$page = $_POST['page'];
			$page = $page*30;

			if($name == null || $name == ""){
				$sql = "SELECT * FROM medicine  ORDER BY name ASC LIMIT $page, 30;";
			}else{
				$sql = "SELECT * FROM medicine WHERE name like '%$name%' or code like '%$name%' ORDER BY name ASC LIMIT $page, 30;";
			}

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$type = $row['type'];
				$code = $row['code'];
				$name = $row['name'];
				$company = $row['company'];
				$standard = $row['standard'];
				$unit = $row['unit'];

				echo "{\"id\":\"$id\",
				\"type\":\"$type\",
				\"code\":\"$code\",
				\"name\":\"$name\",
				\"company\":\"$company\",
				\"standard\":\"$standard\",
				\"unit\":\"$unit\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == "save_patient_medicine"){

			$title = $_POST['title'];
			$start_date = $_POST['start_date'];
			$finish_date = $_POST['finish_date'];
			$patient_id = $_POST['patient_id'];
			$num_of_detail = $_POST['count'];
			$registered_date = time() * 1000;

			$sql = "INSERT INTO patient_medicine(title, start_date, finish_date, patient_id, registered_date)
					VALUES('$title', '$start_date', '$finish_date', '$patient_id', '$registered_date');";

			$ret = mysqli_query($con, $sql);
			$patient_medicine_id = mysqli_insert_id($con);

			$i = 0;
			for($i=0; $i<$num_of_detail; $i++){

				$sd = $_POST['sd'.$i];
				$ndd = $_POST['ndd'.$i];
				$tdd = $_POST['tdd'.$i];
				$description = $_POST['description'.$i];
				$time = $_POST['time'.$i];
				$medicine_id = $_POST['medicine_id'.$i];

				$sql2 = "INSERT INTO patient_medicine_detail(patient_medicine_id, medicine_id, single_dose, num_of_daily_dose, total_dosing_days, description, time)
						VALUES('$patient_medicine_id', '$medicine_id', '$sd', '$ndd', '$tdd', '$description', '$time');";
				$ret2 = mysqli_query($con, $sql2);
				if($ret2 != 1){
					break;
				}

			}

			if($ret == '1' && $i == $num_of_detail){
				echo json_encode(array('status'=>'success', 'message'=>"save success"));
			}else{
				echo json_encode(array('status'=>'fail', 'message'=>"save fail"));
			}


		}else if($service == "getPatientSymptomList"){

			$patient_id = $_POST['patient_id'];

			$page = $_POST['page'];
			$page = $page*30;

			$sql = "select * from symptom_history WHERE patient_id = '$patient_id' ORDER BY id DESC LIMIT $page, 30;";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$patient_id = $row['patient_id'];
				$description = $row['description'];
				$start_date = $row['start_date'];
				$finish_date = $row['finish_date'];
				$registered_date = $row['registered_date'];

				echo "{\"id\":\"$id\",
				\"patient_id\":\"$patient_id\",
				\"start_date\":\"$start_date\",
				\"finish_date\":\"$finish_date\",
				\"description\":\"$description\",
				\"registered_date\":\"$registered_date\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == "save_patient_symptom"){

			$patient_id = $_POST['patient_id'];
			$start_date = $_POST['start_date'];
			$description = $_POST['description'];
			$registered_date = time() * 1000;

			$sql = "INSERT INTO symptom_history(patient_id, description, start_date, finish_date, registered_date)
					VALUES('$patient_id', '$description', '$start_date', '0', '$registered_date');";

			$ret = mysqli_query($con, $sql);

			if($ret == '1'){
				echo json_encode(array('status'=>'success', 'message'=>"save success"));
			}else{
				echo json_encode(array('status'=>'fail', 'message'=>"save fail"));
			}


		}else if($service == "getPatientWeightList"){

			$patient_id = $_POST['patient_id'];

			$sql = "select * from weight_history WHERE patient_id = '$patient_id' ORDER BY id ASC;";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$patient_id = $row['patient_id'];
				$weight = $row['weight'];
				$registered_date = $row['registered_date'];

				echo "{\"id\":\"$id\",
				\"patient_id\":\"$patient_id\",
				\"weight\":\"$weight\",
				\"registered_date\":\"$registered_date\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == "savePatientWeight"){

			$patient_id = $_POST['patient_id'];
			$weight = $_POST['weight'];
			$registered_date = time() * 1000;

			$sql = "INSERT INTO weight_history(patient_id, weight, registered_date) VALUES('$patient_id', '$weight', '$registered_date');";

			$ret = mysqli_query($con, $sql);

			if($ret == '1'){
				echo json_encode(array('status'=>'success', 'message'=>"save success"));
			}else{
				echo json_encode(array('status'=>'fail', 'message'=>"save fail"));
			}

		}else if($service == "getSymptomList"){

			$page = $_POST['page'];
			$page = $page*30;

			$sql = "SELECT * FROM symptom LIMIT $page, 30;";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$description = $row['description'];

				echo "{\"id\":\"$id\",
				\"description\":\"$description\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}
        else if($service == 'getPatientListByLocationId'){

            $name = $_POST['name'];
            $location_id = $_POST['location_id'];

            $page = $_POST['page'];
            $page = $page*30;

            //$sql = "SELECT * FROM Patient WHERE location_id='$location_id'";
            if($name == null || $name == ''){
                $sql = "SELECT A.*, B.name as location_name, B.pic as location_pic, B.director as location_director, B.capacity as location_capacity, B.major as location_major, B.construction_year as location_construction_year, B.phone as location_phone, B.url as location_url
				FROM patient as A LEFT OUTER JOIN (
				SELECT *
				FROM location
				GROUP BY id) as B
				ON (B.id = A.location_id)
				WHERE A.location_id like '$location_id'
				GROUP BY A.id";
            }else{
                $sql = "SELECT A.*, B.name as location_name, B.pic as location_pic, B.director as location_director, B.capacity as location_capacity, B.major as location_major, B.construction_year as location_construction_year, B.phone as location_phone, B.url as location_url
				FROM patient as A LEFT OUTER JOIN (
				SELECT *
				FROM location
				GROUP BY id) as B
				ON (B.id = A.location_id)
				WHERE A.location_id like '$location_id' and (A.first_name like '%$name%' or A.last_name like '%$name%')
				GROUP BY A.id";
            }

            $sql = $sql." ORDER BY id DESC LIMIT $page, 30;";

            $ret = mysqli_query($con, $sql);
            if($ret){
                $count = mysqli_num_rows($ret);
            }else{
                exit();
            }

            echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

            $i=0;

            while($row = mysqli_fetch_array($ret)){

                $id = $row['id'];
                $first_name = $row['first_name'];
                $last_name = $row['last_name'];
                $gender = $row['gender'];
                $address = $row['address'];
                $zip = $row['zip'];
                $phone = $row['phone'];
                $pic = $row['pic'];
                $date_of_birth = $row['date_of_birth'];
                $height = $row['height'];
                $bla = $row['basic_living_allowance'];
                $start_date = $row['start_date'];
                $description = $row['description'];
                $registered_date = $row['registered_date'];

                $location_id = $row['location_id'];
                $location_name = $row['location_name'];
                $location_pic = $row['location_pic'];
                $location_director = $row['location_director'];
                $location_capacity = $row['location_capacity'];
                $location_major = $row['location_major'];
                $location_construction_year = $row['location_construction_year'];
                $location_phone = $row['location_phone'];
                $location_url = $row['location_url'];

                echo "{\"id\":\"$id\",
				\"first_name\":\"$first_name\",
				\"last_name\":\"$last_name\",
				\"gender\":\"$gender\",
				\"address\":\"$address\",
				\"zip\":\"$zip\",
				\"phone\":\"$phone\",
				\"pic\":\"$pic\",
				\"date_of_birth\":\"$date_of_birth\",
				\"height\":\"$height\",
				\"basic_living_allowance\":\"$bla\",
				\"start_date\":\"$start_date\",
				\"description\":\"$description\",
				\"registered_date\":\"$registered_date\",
				\"location_id\":\"$location_id\",
				\"location_name\":\"$location_name\",
				\"location_pic\":\"$location_pic\",
				\"location_director\":\"$location_director\",
				\"location_capacity\":\"$location_capacity\",
				\"location_major\":\"$location_major\",
				\"location_construction_year\":\"$location_construction_year\",
				\"location_phone\":\"$location_phone\",
				\"location_url\":\"$location_url\"
				}";

                if($i<$count-1){
                    echo ",";
                }

                $i++;

            }

            echo "]}";

        }

        else if($service == 'ManagePatientRegisterInfo'){

            $patient_fn = $_POST['patient_fn'];
						$location_id = $_POST['location_id'];
						$patient_ln = $_POST['patient_ln'];
						$patient_dob = $_POST['patient_dob'];

            // $sql = "SELECT * FROM employee WHERE id like '$login_id' and password like '$login_pw';";
            $sql = "SELECT * FROM patient as A WHERE A.first_name like '$patient_fn'";

            $ret = mysqli_query($con, $sql);
            if($ret){
                $count = mysqli_num_rows($ret);
            }else{
                exit();
            }

            echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

            $i=0;

            while($row = mysqli_fetch_array($ret)){

                $id = $row['id'];
                $login_id = $row['login_id'];
                $first_name = $row['first_name'];
                $last_name = $row['last_name'];
                $gender = $row['gender'];
                $address = $row['address'];
                $zip = $row['zip'];
                $phone = $row['phone'];
                $pic = $row['pic'];
								$height = $row['height'];
                $date_of_birth = $row['date_of_birth'];
                $start_date = $row['start_date'];
                $description = $row['description'];
                $registered_date = $row['registered_date'];
                $location_id = $row['location_id'];


                echo "{\"id\":\"$id\",
				\"first_name\":\"$first_name\",
				\"last_name\":\"$last_name\",
				\"gender\":\"$gender\",
				\"address\":\"$address\",
				\"zip\":\"$zip\",
				\"phone\":\"$phone\",
				\"pic\":\"$pic\",
				\"date_of_birth\":\"$date_of_birth\",
				\"height\":\"$height\",
				\"start_date\":\"$start_date\",
				\"description\":\"$description\",
				\"registered_date\":\"$registered_date\",
				\"location_id\":\"$location_id\"
				}";

                if($i<$count-1){
                    echo ",";
                }

                $i++;

            }

            echo "]}";
        }
		else if($service == "updateSymptomFinishDate"){

            $symptom_history_id = $_POST['symptom_history_id'];
			$date = $_POST['date'];

            $sql = "UPDATE symptom_history SET finish_date='$date' WHERE id='$symptom_history_id';";

            $ret = mysqli_query($con, $sql);

            if($ret == '1'){
                echo json_encode(array('status'=>'success', 'message'=>"update success"));
            }else{
                echo json_encode(array('status'=>'fail', 'message'=>"update fail"));
            }

		}else if($service == "updateMedicineFinishDate"){

            $patient_medicine_id = $_POST['patient_medicine_id'];
			$date = $_POST['date'];

            $sql = "UPDATE patient_medicine SET finish_date='$date' WHERE id='$patient_medicine_id';";

            $ret = mysqli_query($con, $sql);

            if($ret == '1'){
                echo json_encode(array('status'=>'success', 'message'=>"update success"));
            }else{
                echo json_encode(array('status'=>'fail', 'message'=>"update fail"));
            }

		}else if($service == "addLog"){
			$location_id = $_POST['location_id'];
			$employee_id = $_POST['employee_id'];
			$patient_id = $_POST['patient_id'];
			$type = $_POST['type'];
			$msg = $_POST['msg'];
			$registered_date = time() * 1000;

			$sql = "INSERT INTO log(location_id, employee_id, patient_id, type, msg, registered_date) VALUES('$location_id', '$employee_id', '$patient_id', '$type', '$msg', '$registered_date');";

			$ret = mysqli_query($con, $sql);

            if($ret == '1'){
                echo json_encode(array('status'=>'success', 'message'=>"save success"));
            }else{
                echo json_encode(array('status'=>'fail', 'message'=>"save fail"));
            }

		}else if($service == "recordData"){

			$patient_id = $_POST['patient_id'];
			$employee_id = $_POST['employee_id'];
			$location_id = $_POST['location_id'];
			$registered_date = time() * 1000;

			$sql = "INSERT INTO main_record(patient_id, employee_id, registered_date, location_id)
			VALUES('$patient_id', '$employee_id', '$registered_date', '$location_id');";


			$isFail = "";
			$ret = mysqli_query($con, $sql);

			if($ret == '1'){
				$main_record_id = mysqli_insert_id($con);


				$have_meal = $_POST['have_meal'];
				if($have_meal == "1"){
					$have_meal_type = $_POST['have_meal_type'];
					$have_meal_description = $_POST['have_meal_description'];

					$sql_haveMeal = "INSERT INTO have_meal(main_record_id, patient_id, type, description, registered_date)
					VALUES('$main_record_id', '$patient_id', '$have_meal_type', '$have_meal_description', '$registered_date');";

					$ret_haveMeal = mysqli_query($con, $sql_haveMeal);
					if($ret_haveMeal != '1'){
						$isFail = $isFail."/have meal".$sql_haveMeal;
					}
				}

				$vital_sign = $_POST['vital_sign'];
				if($vital_sign == "1"){
					$vital_sign_bp_max = $_POST['vital_sign_bp_max'];
					$vital_sign_bp_min = $_POST['vital_sign_bp_min'];
					$vital_sign_pulse = $_POST['vital_sign_pulse'];
					$vital_sign_temperature = $_POST['vital_sign_temperature'];

					$sql_vitalSign = "INSERT INTO vital_sign(main_record_id, patient_id, blood_pressure_max, blood_pressure_min, pulse, temperature, registered_date)
					VALUES('$main_record_id', '$patient_id', '$vital_sign_bp_max', '$vital_sign_bp_min', '$vital_sign_pulse', '$vital_sign_temperature', '$registered_date');";

					$ret_vitalSign = mysqli_query($con, $sql_vitalSign);
					if($ret_vitalSign != '1'){
						$isFail = $isFail."/vital sign".$sql_vitalSign;
					}
				}

				$remarks = $_POST['remarks'];
				if($remarks == "1"){
					$remarksSize = $_POST['remarks_size'];
					for($k=0; $k<$remarksSize; $k++){
						$remarks_description = $_POST['remarks_description'.$k];

						$sql_remarks = "INSERT INTO remarks(main_record_id, patient_id, symptom_id, description, registered_date)
						VALUES('$main_record_id', '$patient_id', null, '$remarks_description', '$registered_date');";

						$ret_remarks = mysqli_query($con, $sql_remarks);
						if($ret_remarks != '1'){
							$isFail = $isFail."/remarks".$k.$sql_remarks;
						}
					}
				}

				$take_medicines = $_POST['take_medicines'];
				if($take_medicines == "1"){
					$takeMedicinesSize = $_POST['take_medicines_size'];
					for($k=0; $k<$takeMedicinesSize; $k++){
						$take_medicines_patient_medicine_id = $_POST['take_medicines_patient_medicine_id'.$k];
						$medicine_id = $_POST['medicine_id'.$k];

						if($take_medicines_patient_medicine_id != ""){
							$sql_take_medicines = "INSERT INTO take_medicine(main_record_id, patient_id, patient_medicine_id, registered_date)
							VALUES('$main_record_id', '$patient_id', '$take_medicines_patient_medicine_id', '$registered_date');";
						}else{
							$sql_take_medicines = "INSERT INTO take_medicine(main_record_id, patient_id, medicine_id, registered_date)
							VALUES('$main_record_id', '$patient_id', '$medicine_id', '$registered_date');";
						}

						$ret_take_medicines = mysqli_query($con, $sql_take_medicines);
						if($ret_take_medicines != '1'){
							$isFail = $isFail."/take medicines".$k.$sql_take_medicines;
						}
					}
				}

				$save_photo = $_POST['save_photo'];
				if($save_photo == "1"){

					$target_path = dirname(__FILE__).'/pic/';

					$imageString = "image";

					if (isset($_FILES[$imageString]['name'])) {

						$path = $_FILES[$imageString]['name'];
						$ext = pathInfo($path, PATHINFO_EXTENSION);

						$path = "record_" . $patient_id . "" . $main_record_id . "" . $registered_date . "." . $ext;

						$target_path2 = $target_path.$path;

						try{

							if (move_uploaded_file($_FILES[$imageString]['tmp_name'], $target_path2)) {
								// File successfully uploaded
								$picture = "http://nearby.cf/pic/" . $path;

								$sql_photo = "INSERT INTO patient_photo(main_record_id, patient_id, url, registered_date)
											VALUES('$main_record_id', '$patient_id', '$picture', '$registered_date');";

								$ret_photo = mysqli_query($con, $sql_photo);
								if($ret_photo != '1'){
									$isFail = $isFail."/photo".$sql_photo;
								}

							}else{
								// File upload failed
								$isFail = $isFail."/photo file upload failed";
								//echo json_encode(array('status'=>'fail', 'message'=>$target_path));
							}

						}catch(Exception $e){
							$isFail = $isFail."/photo " .  $e->getMessage();
							//echo json_encode(array('status'=>'fail', 'message'=>$e->getMessage()));
						}

					} else {
						// File parameter is missing
						$isFail = $isFail."/photo Not received any file";
						//echo json_encode(array('status'=>'fail', 'message'=>'Not received any file'));
					}

				}

				if($isFail == ""){
					echo json_encode(array('status'=>'success', 'message'=>"record success"));
				}else{
					echo json_encode(array('status'=>'fail', 'message'=>$isFail));
				}

			}else{
				echo json_encode(array('status'=>'fail', 'message'=>"Main record save failed"));
			}


		}else if($service == "getSupporterList"){

			$patient_id = $_POST['patient_id'];

			$sql = "SELECT A.*, B.login_id as user_login_id, B.email as user_email, B.first_name as user_first_name, B.last_name as user_last_name, B.phone as user_phone
					FROM user_patient as A LEFT OUTER JOIN (
					SELECT *
					FROM user
					GROUP BY id) as B
					ON (B.id = A.user_id)
					WHERE A.patient_id='$patient_id'
					GROUP BY A.id;";

			$ret = mysqli_query($con, $sql);
            if($ret){
                $count = mysqli_num_rows($ret);
            }else{
                exit();
            }

            echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

            $i=0;

            while($row = mysqli_fetch_array($ret)){

                $id = $row['id'];
                $patient_id = $row['patient_id'];
                $relationship = $row['relationship'];
				$registered_date = $row['registered_date'];

                $user_id = $row['user_id'];
				$user_login_id = $row['user_login_id'];
				$user_email = $row['user_email'];
				$user_first_name = $row['user_first_name'];
				$user_last_name = $row['user_last_name'];
				$user_phone = $row['user_phone'];



                echo "{\"id\":\"$id\",
				\"patient_id\":\"$patient_id\",
				\"relationship\":\"$relationship\",
				\"user_id\":\"$user_id\",
				\"user_login_id\":\"$user_login_id\",
				\"user_email\":\"$user_email\",
				\"user_first_name\":\"$user_first_name\",
				\"user_last_name\":\"$user_last_name\",
				\"user_phone\":\"$user_phone\",
				\"registered_date\":\"$registered_date\"
				}";

                if($i<$count-1){
                    echo ",";
                }

                $i++;

            }

            echo "]}";

		}else if($service == "searchSupporter"){

			$search = $_POST['search'];
			$patient_id = $_POST['patient_id'];

			$page = $_POST['page'];
			$page = $page*30;

			$sql = "SELECT * FROM user WHERE login_id like '%$search%' or (concat(last_name, first_name) like '%$search%' or last_name like '%$search%' or first_name like '%$search%') and (id not in (SELECT user_id FROM user_patient WHERE patient_id = '$patient_id')) LIMIT $page, 30;";
			$ret = mysqli_query($con, $sql);
            if($ret){
                $count = mysqli_num_rows($ret);
            }else{
                exit();
            }

            echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

            $i=0;

            while($row = mysqli_fetch_array($ret)){

                $id = $row['id'];
				$login_id = $row['login_id'];
				$email = $row['email'];
				$first_name = $row['first_name'];
				$last_name = $row['last_name'];
				$role = $row['role'];
				$gender = $row['gender'];
				$address = $row['address'];
				$zip = $row['zip'];
				$phone = $row['phone'];
				$pic = $row['pic'];
				$date_of_birth = $row['date_of_birth'];
				$registered_date = $row['registered_date'];



                echo "{\"id\":\"$id\",
				\"login_id\":\"$login_id\",
				\"email\":\"$email\",
				\"first_name\":\"$first_name\",
				\"last_name\":\"$last_name\",
				\"role\":\"$role\",
				\"gender\":\"$gender\",
				\"address\":\"$address\",
				\"zip\":\"$zip\",
				\"phone\":\"$phone\",
				\"pic\":\"$pic\",
				\"date_of_birth\":\"$date_of_birth\",
				\"registered_date\":\"$registered_date\"
				}";

                if($i<$count-1){
                    echo ",";
                }

                $i++;

            }

            echo "]}";


		}else if($service == "registerPatientSupporter"){

			$patient_id = $_POST['patient_id'];
			$user_id = $_POST['user_id'];
			$relationship = $_POST['relationship'];
			$registered_date = time() * 1000;

			$sql = "INSERT INTO user_patient(patient_id, user_id, relationship, registered_date) VALUES('$patient_id', '$user_id', '$relationship', '$registered_date');";

            $ret = mysqli_query($con, $sql);

			if($ret == '1'){
				echo json_encode(array('status'=>'success', 'message'=>"update success"));
			}else{
				echo json_encode(array('status'=>'fail', 'message'=>"update fail"));
			}

		}else if($service == "removePatientSupporter"){

			$patient_id = $_POST['patient_id'];
			$user_id = $_POST['user_id'];

			$sql = "DELETE FROM user_patient WHERE patient_id='$patient_id' and user_id='$user_id';";

            $ret = mysqli_query($con, $sql);

			if($ret == '1'){
				echo json_encode(array('status'=>'success', 'message'=>"update success"));
			}else{
				echo json_encode(array('status'=>'fail', 'message'=>"update fail"));
			}

		}else if($service == "inquiryPatientMedicine"){

			$patient_id = $_POST['patient_id'];
			$isDate = $_POST['isDate'];
			$start_date = $_POST['start_date'];
			$finish_date = $_POST['finish_date'];

			$page = $_POST['page'];
			$page = $page*30;

			if($isDate == "1"){

				$sql = "SELECT A.*, B.title as title, C.code as medicine_code, C.name as medicine_name, C.type as medicine_type, C.company as medicine_company, C.standard as medicine_standard, C.unit as medicine_unit
						FROM take_medicine as A
						LEFT OUTER JOIN (
						SELECT *
						FROM patient_medicine
						GROUP BY id) as B
						ON (B.id = A.patient_medicine_id)
						LEFT OUTER JOIN (
						SELECT *
						FROM medicine
						GROUP BY id) as C
						ON (C.id = A.medicine_id)
						WHERE A.patient_id='$patient_id' AND ('$start_date' <= A.registered_date AND A.registered_date <= '$finish_date')
						GROUP BY A.id";

			}else{

				$sql = "SELECT A.*, B.title as title, C.code as medicine_code, C.name as medicine_name, C.type as medicine_type, C.company as medicine_company, C.standard as medicine_standard, C.unit as medicine_unit
						FROM take_medicine as A
						LEFT OUTER JOIN (
						SELECT *
						FROM patient_medicine
						GROUP BY id) as B
						ON (B.id = A.patient_medicine_id)
						LEFT OUTER JOIN (
						SELECT *
						FROM medicine
						GROUP BY id) as C
						ON (C.id = A.medicine_id)
						WHERE A.patient_id='$patient_id'
						GROUP BY A.id";

				$sql = $sql." LIMIT $page, 30;";

			}

			$ret = mysqli_query($con, $sql);
            if($ret){
                $count = mysqli_num_rows($ret);
            }else{
                exit();
            }

            echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

            $i=0;

            while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$main_record_id = $row['main_record_id'];
				$patient_medicine_id = $row['patient_medicine_id'];
				$medicine_id = $row['medicine_id'];
				$medicine_code = $row['medicine_code'];
				$medicine_name = $row['medicine_name'];
				$medicine_type = $row['medicine_type'];
				$medicine_company = $row['medicine_company'];
				$medicine_standard = $row['medicine_standard'];
				$medicine_unit = $row['medicine_unit'];
				$registered_date = $row['registered_date'];
				$title = $row['title'];



                echo "{\"id\":\"$id\",
				\"main_record_id\":\"$main_record_id\",
				\"patient_medicine_id\":\"$patient_medicine_id\",
				\"medicine_id\":\"$medicine_id\",
				\"medicine_code\":\"$medicine_code\",
				\"medicine_name\":\"$medicine_name\",
				\"medicine_type\":\"$medicine_type\",
				\"medicine_company\":\"$medicine_company\",
				\"medicine_standard\":\"$medicine_standard\",
				\"medicine_unit\":\"$medicine_unit\",
				\"title\":\"$title\",
				\"registered_date\":\"$registered_date\"
				}";

                if($i<$count-1){
                    echo ",";
                }

                $i++;

            }

            echo "]}";

		}else if($service == "inquiryPatientMeal"){

			$patient_id = $_POST['patient_id'];
			$isDate = $_POST['isDate'];
			$start_date = $_POST['start_date'];
			$finish_date = $_POST['finish_date'];

			$page = $_POST['page'];
			$page = $page*30;

			if($isDate == '1'){
				$sql = "SELECT * FROM have_meal WHERE patient_id='$patient_id' AND ('$start_date' <= registered_date AND registered_date <= '$finish_date');";
			}else{
				$sql = "SELECT * FROM have_meal WHERE patient_id='$patient_id'";

				$sql = $sql." LIMIT $page, 30;";
			}


			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$main_record_id = $row['main_record_id'];
				$type = $row['type'];
				$description = $row['description'];
				$registered_date = $row['registered_date'];


				echo "{\"id\":\"$id\",
				\"main_record_id\":\"$main_record_id\",
				\"type\":\"$type\",
				\"description\":\"$description\",
				\"registered_date\":\"$registered_date\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == "inquiryPatientRemarks"){

			$patient_id = $_POST['patient_id'];
			$isDate = $_POST['isDate'];
			$start_date = $_POST['start_date'];
			$finish_date = $_POST['finish_date'];

			$page = $_POST['page'];
			$page = $page*30;

			if($isDate == '1'){
				$sql = "SELECT * FROM remarks WHERE patient_id='$patient_id' AND ('$start_date' <= registered_date AND registered_date <= '$finish_date');";
			}else{
				$sql = "SELECT * FROM remarks WHERE patient_id='$patient_id'";

				$sql = $sql." LIMIT $page, 30;";
			}


			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$main_record_id = $row['main_record_id'];
				$symptom_id = $row['symptom_id'];
				$description = $row['description'];
				$registered_date = $row['registered_date'];


				echo "{\"id\":\"$id\",
				\"main_record_id\":\"$main_record_id\",
				\"symptom_id\":\"$symptom_id\",
				\"description\":\"$description\",
				\"registered_date\":\"$registered_date\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == "inquiryPatientVitalSign"){

			$patient_id = $_POST['patient_id'];
			$type = $_POST['type'];
			$isDate = $_POST['isDate'];
			$start_date = $_POST['start_date'];
			$finish_date = $_POST['finish_date'];

			$page = $_POST['page'];
			$page = $page*30;

			if($isDate == '1'){
				$sql = "SELECT id, main_record_id, patient_id, pulse, registered_date FROM vital_sign WHERE patient_id='$patient_id' AND ('$start_date' <= registered_date AND registered_date <= '$finish_date');";
				// if($type == 'pulse'){
				// 	$sql = "SELECT id, main_record_id, patient_id, pulse, registered_date FROM vital_sign WHERE patient_id='$patient_id' AND pulse != '0' AND ('$start_date' <= registered_date AND registered_date <= '$finish_date');";
				// }else if($type == 'temperature'){
				// 	$sql = "SELECT id, main_record_id, patient_id, temperature, registered_date FROM vital_sign WHERE patient_id='$patient_id' AND temperature != '0' AND ('$start_date' <= registered_date AND registered_date <= '$finish_date');";
				// }else if($type == 'bp'){
				// 	$sql = "SELECT id, main_record_id, patient_id, blood_pressure_max, blood_pressure_min, registered_date FROM vital_sign WHERE patient_id='$patient_id' AND (blood_pressure_min != '0' AND blood_pressure_max != '0') AND ('$start_date' <= registered_date AND registered_date <= '$finish_date');";
				// }
			}else{
				if($type == 'pulse'){
					$sql = "SELECT id, main_record_id, patient_id, pulse, registered_date FROM vital_sign WHERE patient_id='$patient_id' AND pulse != '0'";
				}else if($type == 'temperature'){
					$sql = "SELECT id, main_record_id, patient_id, temperature, registered_date FROM vital_sign WHERE patient_id='$patient_id' AND temperature != '0'";
				}else if($type == 'bp'){
					$sql = "SELECT id, main_record_id, patient_id, blood_pressure_max, blood_pressure_min, registered_date FROM vital_sign WHERE patient_id='$patient_id' AND (blood_pressure_min != '0' AND blood_pressure_max != '0')";
				}

				$sql = $sql." LIMIT $page, 30;";
			}


			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$main_record_id = $row['main_record_id'];
				$blood_pressure_max = $row['blood_pressure_max'];
				$blood_pressure_min = $row['blood_pressure_min'];
				$pulse = $row['pulse'];
				$temperature = $row['temperature'];
				$registered_date = $row['registered_date'];


				echo "{\"id\":\"$id\",
				\"main_record_id\":\"$main_record_id\",
				\"blood_pressure_max\":\"$blood_pressure_max\",
				\"blood_pressure_min\":\"$blood_pressure_min\",
				\"pulse\":\"$pulse\",
				\"temperature\":\"$temperature\",
				\"registered_date\":\"$registered_date\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == "inquiryPatientPhoto"){

			$patient_id = $_POST['patient_id'];
			$isDate = $_POST['isDate'];
			$start_date = $_POST['start_date'];
			$finish_date = $_POST['finish_date'];

			$page = $_POST['page'];
			$page = $page*30;

			if($isDate == '1'){
				$sql = "SELECT * FROM patient_photo WHERE patient_id='$patient_id' AND ('$start_date' <= registered_date AND registered_date <= '$finish_date');";
			}else{
				$sql = "SELECT * FROM patient_photo WHERE patient_id='$patient_id'";

				$sql = $sql." LIMIT $page, 30;";
			}


			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$main_record_id = $row['main_record_id'];
				$url = $row['url'];
				$registered_date = $row['registered_date'];


				echo "{\"id\":\"$id\",
				\"main_record_id\":\"$main_record_id\",
				\"url\":\"$url\",
				\"registered_date\":\"$registered_date\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == "inquiryPatientRelatedNurse"){

			$patient_id = $_POST['patient_id'];

			$sql = "SELECT A.*, B.name as location_name, B.pic as location_pic, B.director as location_director, B.capacity as location_capacity, B.major as location_major, B.construction_year as location_construction_year, B.phone as location_phone, B.url as location_url
					FROM employee as A LEFT OUTER JOIN (
					SELECT *
					FROM location
					GROUP BY id) as B
					ON (B.id = A.location_id)
					WHERE A.role='nurse' AND A.id IN (SELECT DISTINCT employee_id FROM main_record WHERE patient_id='$patient_id')
					GROUP BY A.id";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$login_id = $row['login_id'];
				$email = $row['email'];
				$first_name = $row['first_name'];
				$last_name = $row['last_name'];
				$role = $row['role'];
				$license = $row['license'];
				$gender = $row['gender'];
				$address = $row['address'];
				$zip = $row['zip'];
				$phone = $row['phone'];
				$pic = $row['pic'];
				$date_of_birth = $row['date_of_birth'];
				$major = $row['major'];
				$start_date = $row['start_date'];
				$description = $row['description'];
				$registered_date = $row['registered_date'];

				$location_id = $row['location_id'];
				$location_name = $row['location_name'];
				$location_pic = $row['location_pic'];
				$location_director = $row['location_director'];
				$location_capacity = $row['location_capacity'];
				$location_major = $row['location_major'];
				$location_construction_year = $row['location_construction_year'];
				$location_phone = $row['location_phone'];
				$location_url = $row['location_url'];

				echo "{\"id\":\"$id\",
				\"login_id\":\"$login_id\",
				\"email\":\"$email\",
				\"first_name\":\"$first_name\",
				\"last_name\":\"$last_name\",
				\"role\":\"$role\",
				\"license\":\"$license\",
				\"gender\":\"$gender\",
				\"address\":\"$address\",
				\"zip\":\"$zip\",
				\"phone\":\"$phone\",
				\"pic\":\"$pic\",
				\"date_of_birth\":\"$date_of_birth\",
				\"major\":\"$major\",
				\"start_date\":\"$start_date\",
				\"description\":\"$description\",
				\"registered_date\":\"$registered_date\",
				\"location_id\":\"$location_id\",
				\"location_name\":\"$location_name\",
				\"location_pic\":\"$location_pic\",
				\"location_director\":\"$location_director\",
				\"location_capacity\":\"$location_capacity\",
				\"location_major\":\"$location_major\",
				\"location_construction_year\":\"$location_construction_year\",
				\"location_phone\":\"$location_phone\",
				\"location_url\":\"$location_url\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == "getMainRecordList"){

			$patient_id = $_POST['patient_id'];

			$page = $_POST['page'];
			$page = $page*30;

			$sql = "SELECT * FROM main_record WHERE patient_id='$patient_id'";

			$sql = $sql." LIMIT $page, 30;";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$employee_id = $row['employee_id'];
				$registered_date = $row['registered_date'];


				echo "{\"id\":\"$id\",
				\"employee_id\":\"$employee_id\",
				\"registered_date\":\"$registered_date\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == 'getSupporterPatientList'){

			$supporter_id = $_POST['supporter_id'];

			$page = $_POST['page'];
			$page = $page*30;

			$sql = "SELECT A.*, B.name as location_name, B.pic as location_pic, B.director as location_director, B.capacity as location_capacity, B.major as location_major, B.construction_year as location_construction_year, B.phone as location_phone, B.url as location_url
					FROM patient as A LEFT OUTER JOIN (
					SELECT *
					FROM location
					GROUP BY id) as B
					ON (B.id = A.location_id)
					WHERE A.id in (SELECT patient_id FROM user_patient WHERE user_id='$supporter_id')
					GROUP BY A.id";

			$sql = $sql." ORDER BY id DESC LIMIT $page, 30;";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$first_name = $row['first_name'];
				$last_name = $row['last_name'];
				$gender = $row['gender'];
				$address = $row['address'];
				$zip = $row['zip'];
				$phone = $row['phone'];
				$pic = $row['pic'];
				$date_of_birth = $row['date_of_birth'];
				$height = $row['height'];
				$bla = $row['basic_living_allowance'];
				$start_date = $row['start_date'];
				$description = $row['description'];
				$registered_date = $row['registered_date'];

				$location_id = $row['location_id'];
				$location_name = $row['location_name'];
				$location_pic = $row['location_pic'];
				$location_director = $row['location_director'];
				$location_capacity = $row['location_capacity'];
				$location_major = $row['location_major'];
				$location_construction_year = $row['location_construction_year'];
				$location_phone = $row['location_phone'];
				$location_url = $row['location_url'];

				echo "{\"id\":\"$id\",
				\"first_name\":\"$first_name\",
				\"last_name\":\"$last_name\",
				\"gender\":\"$gender\",
				\"address\":\"$address\",
				\"zip\":\"$zip\",
				\"phone\":\"$phone\",
				\"pic\":\"$pic\",
				\"date_of_birth\":\"$date_of_birth\",
				\"height\":\"$height\",
				\"basic_living_allowance\":\"$bla\",
				\"start_date\":\"$start_date\",
				\"description\":\"$description\",
				\"registered_date\":\"$registered_date\",
				\"location_id\":\"$location_id\",
				\"location_name\":\"$location_name\",
				\"location_pic\":\"$location_pic\",
				\"location_director\":\"$location_director\",
				\"location_capacity\":\"$location_capacity\",
				\"location_major\":\"$location_major\",
				\"location_construction_year\":\"$location_construction_year\",
				\"location_phone\":\"$location_phone\",
				\"location_url\":\"$location_url\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == "getMedicineDetail"){

			$code = $_POST['code'];

			$sql = "SELECT * FROM medicine_info WHERE code='$code';";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$code = $row['code'];
				$url = $row['url'];
				$ingredient = $row['ingredient'];
				$classification = $row['classification'];
				$usage_capacity = $row['usage_capacity'];

				echo "{\"code\":\"$code\",
				\"url\":\"$url\",
				\"ingredient\":\"$ingredient\",
				\"classification\":\"$classification\",
				\"usage_capacity\":\"$usage_capacity\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}else if($service == "inquiryLog"){

			$type = $_POST['type'];
			$location_id = $_POST['location_id'];

			$isDate = $_POST['isDate'];
			$start_date = $_POST['start_date'];
			$finish_date = $_POST['finish_date'];

			$page = $_POST['page'];
			$page = $page*30;

			$sql = "SELECT * FROM log WHERE type='$type' AND location_id='$location_id' AND (('$start_date' <= registered_date AND registered_date <= '$finish_date') OR '1' <> '$isDate') ";

			$sql = $sql." ORDER BY id DESC LIMIT $page, 30;";

			$ret = mysqli_query($con, $sql);
			if($ret){
				$count = mysqli_num_rows($ret);
			}else{
				exit();
			}

			echo "{\"status\":\"OK\",\"num_result\":\"$count\",\"db_version\":\"1\",\"result\":[";

			$i=0;

			while($row = mysqli_fetch_array($ret)){

				$id = $row['id'];
				$location_id = $row['location_id'];
				$employee_id = $row['employee_id'];
				$patient_id = $row['patient_id'];
				$type = $row['type'];
				$msg = $row['msg'];
				$registered_date = $row['registered_date'];

				echo "{\"id\":\"$id\",
				\"location_id\":\"$location_id\",
				\"employee_id\":\"$employee_id\",
				\"patient_id\":\"$patient_id\",
				\"type\":\"$type\",
				\"msg\":\"$msg\",
				\"registered_date\":\"$registered_date\"
				}";

				if($i<$count-1){
					echo ",";
				}

				$i++;

			}

			echo "]}";

		}

	}



	mysqli_close($con);
?>
