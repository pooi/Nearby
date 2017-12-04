<?php

    session_start();
    $locationId = $_SESSION['location_id'];
    $locationName = $_SESSION['location_name'];

    if($locationId == null || $locationId == ''){
        echo "<script>alert(\"유효하지 않은 접근입니다.\");</script>";
        header("Location: http://nearby.cf/admin");
    }

    function logout(){
        session_start();
        session_destroy();
        header("Location: http://nearby.cf/admin");
    }
    // echo "<script>alert(\"$locationId and $locationName\");</script>";

?>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>nearby - 요양원 전자정보 시스템</title>

    <!-- Bootstrap core CSS -->
    <link href="vendor/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="vendor/bootstrap/css/bootstrap-grid.min.css" rel="stylesheet">
    <link href="vendor/bootstrap/css/bootstrap-reboot.min.css" rel="stylesheet">

    <!-- 달력 -->
    <!-- <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/3.0.0/css/bootstrap-datetimepicker.min.css" rel="stylesheet" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.8.2/moment-with-locales.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-datetimepicker/3.0.0/js/bootstrap-datetimepicker.min.js"></script> -->


    <link rel="stylesheet" href="/bs-datepicker/css/datepicker.css">
    <script src="/bs-datepicker/js/bootstrap-datepicker.js"></script>


    <!-- Custom styles for this template -->
    <link href="css/full-slider.css" rel="stylesheet">

    <style type="text/css">
        .container-table {
            display: table;
        }
        
        .vertical-center-row {
            display: table-cell;
            vertical-align: middle;
        }
        
        .outer {
            display: table;
            position: absolute;
            height: 100%;
            width: 100%;
            margin-top: -50px;
        }
        
        .middle {
            display: table-cell;
            vertical-align: middle;
        }
        
        .inner {
            margin-left: auto;
            margin-right: auto;
        }
    </style>

    <script>
        function move() {
            location.replace("index.php");
        }

        function resetAll() {
            document.frm.reset();
        }
    </script>

</head>

<body>

    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
        <div class="container">
            <a class="navbar-brand" href="#">nearby</a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
            <div class="collapse navbar-collapse" id="navbarResponsive">
                <ul class="navbar-nav ml-auto">
                    <li class="nav-item">
                        <a class="nav-link" href="home.php">Home</a>
                    </li>
                    <li class="nav-item active">
                        <a class="nav-link" href="#" onclick="javascript:move();">로그아웃<span class="sr-only">(current)</span></a>
                    </li>
                    <!-- <li class="nav-item">
              <a class="nav-link" href="#">Services</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#">Contact</a>
            </li> -->
                </ul>
            </div>
        </div>
    </nav>

    <div class="container" style="padding-top:100px; padding-bottom:100px;">



        <div class="row">
            <div class="col-md-12">
                <h1>간호사 등록</h1>
                <hr>
                <br>

                <form accept-charset="UTF-8" role="form" action="registration_nurse.php" method="post" class="form-horizontal" name="frm">

                    <div class="col-md-11 offset-md-1">
                        <h2>기본 신상정보 입력</h2>
                        <hr>
                        <div class="form-group">
                            <label for="nurse_id">*로그인 아이디:</label>
                            <input type="text" class="form-control" id="nurse_id" name="nurse_id" required>
                            <button style="margin-top:10px;" class="btn btn-primary" type="button">중복확인</button>
                        </div>
                        <div class="form-group">
                            <label for="nurse_pw">*비밀번호:</label>
                            <input type="password" class="form-control" id="nurse_pw" name="nurse_pw" required>
                        </div>
                        <div class="form-group">
                            <label for="nurse_email">이메일:</label>
                            <input type="text" class="form-control" id="nurse_email" name="nurse_email">
                        </div>
                        <div class="form-group">
                            <label for="nurse_fn">*성:</label>
                            <input type="text" class="form-control" id="nurse_fn" name="nurse_fn" required>
                        </div>
                        <div class="form-group">
                            <label for="nurse_ln">*이름:</label>
                            <input type="text" class="form-control" id="nurse_ln" name="nurse_ln" required>
                        </div>
                        <div class="form-group">
                            <label for="nurse_gender">*성별:</label>
                            <label class="radio-inline"><input type="radio" name="nurse_gender" value="male" required>남자</label>
                            <label class="radio-inline"><input type="radio" name="nurse_gender" value="female" required>여자</label>
                        </div>
                        <div class="form-group">
                            <div class="modal-alerts"></div>
                            <div id="datepicker" role="form" class="form-horizontal"></div>
                            <label for="nurse_dob">생년월일:</label>
                            <script language="javascript">
                                var today = new Date();
                                var toyear = parseInt(today.getFullYear());
                                var start = toyear
                                var end = 1900;

                                document.write("<font size=2><select name=\"nurse_dob1\">");
                                document.write("<option value='0' selected>");
                                for (i = start; i >= end; i--) document.write("<option value='" + i + "'>" + i + "</option>");
                                document.write("</select>년  ");

                                document.write("<select name=\"nurse_dob2\">");
                                document.write("<option value='0' selected>");
                                for (i = 1; i <= 12; i++) document.write("<option value='" + i + "'>" + i + "</option>");
                                document.write("</select>월  ");

                                document.write("<select name=\"nurse_dob3\">");
                                document.write("<option value='0' selected>");
                                for (i = 1; i <= 31; i++) document.write("<option value='" + i + "'>" + i + "</option>");
                                document.write("</select>일  </font>");
                            </script>
                        </div>
                        <div class="form-group">
                            <label for="nurse_zip">우편번호:</label>
                            <input type="text" class="form-control" id="nurse_zip" name="nurse_zip" type="number">
                        </div>
                        <div class="form-group">
                            <label for="nurse_address">집주소:</label>
                            <input type="text" class="form-control" id="nurse_address" name="nurse_address">
                        </div>
                        <div class="form-group">
                            <label for="nurse_phone">전화번호:</label>
                            <input type="text" class="form-control" id="nurse_phone" name="nurse_phone">
                        </div>
                        <br><br>

                        <h2>간호 경력 입력</h2>
                        <hr>
                        <div class="form-group">
                            <label for="nurse_license">자격증 번호:</label>
                            <input type="text" class="form-control" id="nurse_license" name="nurse_license">
                        </div>
                        <div class="form-group">
                            <label for="nurse_major">전공:</label>
                            <input type="text" class="form-control" id="nurse_major" name="nurse_major">
                        </div>
                        <div class="form-group">
                            <div class="modal-alerts"></div>
                            <div id="datepicker" role="form" class="form-horizontal"></div>
                            <label for="nurse_start_date">경력 시작일:</label>
                            <script language="javascript">
                                var today = new Date();
                                var toyear = parseInt(today.getFullYear());
                                var start = toyear
                                var end = 1900;

                                document.write("<font size=2><select name=\"nurse_start_date1\">");
                                document.write("<option value='0' selected>");
                                for (i = start; i >= end; i--) document.write("<option value='" + i + "'>" + i + "</option>");
                                document.write("</select>년  ");

                                document.write("<select name=\"nurse_start_date2\">");
                                document.write("<option value='0' selected>");
                                for (i = 1; i <= 12; i++) document.write("<option value='" + i + "'>" + i + "</option>");
                                document.write("</select>월  ");

                                document.write("<select name=\"nurse_start_date3\">");
                                document.write("<option value='0' selected>");
                                for (i = 1; i <= 31; i++) document.write("<option value='" + i + "'>" + i + "</option>");
                                document.write("</select>일  </font>");
                            </script>
                        </div>
                        <br><br>

                        <h2>기타</h2>
                        <hr>
                        <div class="form-group">
                            <label for="nurse_description">특이사항:</label>
                            <textarea class="form-control" rows="5" id="nurse_description" name="nurse_description"></textarea>
                        </div>

                        <br><br>

                        <hr>
                        <input class="btn btn-success" type="submit" value="등록">
                        <input class="btn btn-danger" type="button" value="취소" onclick="resetAll()">


                    </div>
                </form>
            </div>


        </div>

        <!-- Bootstrap core JavaScript -->
        <script src="vendor/jquery/jquery.min.js"></script>
        <script src="vendor/popper/popper.min.js"></script>

        <script src="vendor/bootstrap/js/bootstrap.min.js"></script>

</body>

</html>