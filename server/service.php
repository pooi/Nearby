<?php

	function replaceSqlString($s1){

		$s1 = str_replace("\"", "\\\\\"", $s1);
		$s1 = str_replace("'", "\\'", $s1);
		$s1 = str_replace("\\n", "\\\\n", $s1);

		return $s1;
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

		}else if($service == 'login_user'){



		}else if($service == 'getPatientList'){

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

		}else if($service == "getPatientMedicineList"){

			$patient_id = $_POST['patient_id'];

			$page = $_POST['page'];
			$page = $page*30;

			$sql = "select * from patient_medicine WHERE patient_id = '$patient_id' ORDER BY id DESC LIMIT $page, 30;";

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
				echo json_encode(array('status'=>'fail1', 'message'=>"save fail"));
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

			if($ret == '1' && $i == $num_of_detail){
				echo json_encode(array('status'=>'success', 'message'=>"save success"));
			}else{
				echo json_encode(array('status'=>'fail1', 'message'=>"save fail"));
			}


		}

	}



	mysqli_close($con);
?>
