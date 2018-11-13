<#--noinspection ALL-->
<!DOCTYPE html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <title>${title}</title>
  <link rel="stylesheet" type="text/css" href="/css/style.css">
  <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.4.2/css/all.css"
        integrity="sha384-/rXc/GQVaYpyDdyxK+ecHPVYJSN9bmVFBvjA/9eOB+pb3F2w2N6fc5qB9Ew5yIns"
        crossorigin="anonymous">
  <script type="text/javascript" src="/js/socket.js"></script>
</head>
<body>
<div class="page">

  <h1>ChillBot</h1>

  <div class="body">
    <h2>Now playing: ${song}</h2>
    <div class="button-bar">
      <form action="./prev" method="POST">
        <button class="btn"><i class="fas fa-backward"></i> Back</button>
      </form>

      <form action="./pause" method="POST">
        <button class="btn">${pause} Play/Pause</button>
      </form>

      <form action="./next" method="POST">
        <button class="btn"><i class="fas fa-forward"></i> Next</button>
      </form>

      <form action="./shuffle" method="POST">
        <button class="btn"><i class="fas fa-random"></i> Shuffle</button>
      </form>
    </div>
    <h3>${playlist}</h3>

    <h2>Enter command:</h2>
    <div class="command">
      <form action="./command" method="GET">
        <input type="text" name="command" autofocus>
        <input type="submit" value="Submit">
      </form>
    </div>
  </div>

</div>
</body>
