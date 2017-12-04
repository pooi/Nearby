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
        
        .card {
            box-shadow: 0 4px 80px 0 rgba(0, 0, 0, 0.1), 0 6px 40px 0 rgba(0, 0, 0, 0.4);
            text-align: center;
            padding: 1em;
            width: 100%;
            height: 10em;
            display: table;
            border: 1px solid white;
            background-color: transparent;
            color: white;
            /* background: rgba(255, 255, 255, 0.15); */
            background: rgba(0, 0, 0, 0.5);
        }
        
        .card:hover {
            box-shadow: 0 4px 80px 0 rgba(230, 109, 28, 0.6), 0 6px 40px 0 rgba(0, 0, 0, 0.05);
            background: rgba(230, 109, 28, 0.15);
        }
        
        .clear-button {
            display: table-cell;
            vertical-align: middle;
            font-size: 2em;
            margin:10px;
        }
    </style>

    <script>
        function move() {
            location.replace("index.php");
        }
    </script>

</head>

<body>

    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
        <div class="container">
            <a class="navbar-brand" href="#">nearby - <?php echo($locationName)?></a>
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
              <a class="nav-link" href="#">About</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#">Services</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#">Contact</a>
            </li> -->
                </ul>
            </div>
        </div>
    </nav>

    <!-- <div style="width:100%; height:100%; background:rgba(0, 0, 0, 0.7); position:fixed; z-index:1; padding-top:50px;">

        <div class="div-center">
            <center>
                <div class="outer">
                    <div class="middle">
                        <div class="inner">

                            <button type="button">간호사 등록</button>

                        </div>
                    </div>
                </div>


            </center>
        </div>

    </div> -->

    <div style="width:100%; height:100%; background:rgba(0, 0, 0, 0.7);  position:fixed; z-index:1; padding-top:50px;">

        <div>
            <center>
                <div class="outer">
                    <div class="middle">
                        <div class="inner">

                            <div class="container container-table">
                                <div class="row vertical-center-row">
                                    <div class="col-md-12">
                                        <!-- <div class="panel panel-default">
                                            <div class="panel-heading">
                                                <h3 class="panel-title" style="color:white;">로그인 해주세요.</h3>
                                            </div>
                                            <div class="panel-body">
                                                <form accept-charset="UTF-8" role="form" action="login.php" method="post">
                                                    <fieldset>
                                                        <div class="form-group">
                                                            <input class="form-control" placeholder="아이디" name="login_id" type="text">
                                                        </div>
                                                        <div class="form-group">
                                                            <input class="form-control" placeholder="비밀번호" name="password" type="password" value="">
                                                        </div>
                                                        <input class="btn btn-lg btn-success btn-block" type="submit" value="로그인">
                                                    </fieldset>
                                                </form>
                                            </div>
                                        </div> -->
                                        <div class="container">
                                            <div class="row" style="padding-top:2em;">
                                                <div class="col-md-4">
                                                <div class="card" id="registe_nurse" onclick="location.href='new_nurse.php'">
                                                        <p class="clear-button">간호사 등록</p>
                                                    </div>
                                                </div>
                                                <div class="col-md-4">
                                                    <div class="card" id="registe_nurse" onclick="location.href='#'">
                                                        <p class="clear-button">간호사 목록</p>
                                                    </div>
                                                </div>
                                                <div class="col-md-4">
                                                    <div class="card" id="registe_nurse" onclick="location.href='#'">
                                                        <p class="clear-button">환자 목록</p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                        </div>
                    </div>
                </div>


            </center>
        </div>

    </div>

    <header>
        <div id="carouselExampleIndicators" class="carousel slide" data-ride="carousel">
            <ol class="carousel-indicators">
                <li data-target="#carouselExampleIndicators" data-slide-to="0" class="active"></li>
                <li data-target="#carouselExampleIndicators" data-slide-to="1"></li>
                <li data-target="#carouselExampleIndicators" data-slide-to="2"></li>
            </ol>
            <div class="carousel-inner" role="listbox">
                <!-- Slide One - Set the background image for this slide in the line below -->
                <div class="carousel-item active" style="background-image: url('./image/bg01.jpg')">
                    <!-- <div class="carousel-caption d-none d-md-block">
                            <h3>First Slide</h3>
                            <p>This is a description for the first slide.</p>
                        </div> -->
                </div>
                <!-- Slide Two - Set the background image for this slide in the line below -->
                <div class="carousel-item" style="background-image: url('./image/bg02.jpg')">
                    <!-- <div class="carousel-caption d-none d-md-block">
                            <h3>Second Slide</h3>
                            <p>This is a description for the second slide.</p>
                        </div> -->
                </div>
                <!-- Slide Three - Set the background image for this slide in the line below -->
                <div class="carousel-item" style="background-image: url('./image/bg03.jpg')">
                    <!-- <div class="carousel-caption d-none d-md-block">
                            <h3>Third Slide</h3>
                            <p>This is a description for the third slide.</p>
                        </div> -->
                </div>
            </div>
            <!-- <a class="carousel-control-prev" href="#carouselExampleIndicators" role="button" data-slide="prev">
                    <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                    <span class="sr-only">Previous</span>
                </a>
                <a class="carousel-control-next" href="#carouselExampleIndicators" role="button" data-slide="next">
                    <span class="carousel-control-next-icon" aria-hidden="true"></span>
                    <span class="sr-only">Next</span>
                </a> -->
        </div>
    </header>


    <!-- Page Content -->
    <section class="py-5">

    </section>

    <!-- Footer -->
    <!-- <footer class="py-5 bg-dark">
        <div class="container">
            <p class="m-0 text-center text-white">Copyright &copy; nearby 2017</p>
        </div>
    </footer> -->
    <!-- /.container -->

    <!-- Bootstrap core JavaScript -->
    <script src="vendor/jquery/jquery.min.js"></script>
    <script src="vendor/popper/popper.min.js"></script>
    <script src="vendor/bootstrap/js/bootstrap.min.js"></script>

</body>

</html>