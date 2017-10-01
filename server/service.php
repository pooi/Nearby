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



		}

	}



	mysqli_close($con);
?>
