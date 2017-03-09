function changetype() {
  var r1 = document.getElementById("radio01").checked;
  var r2 = document.getElementById("radio02").checked;
  var urlinput = document.getElementById("id-url");
  var btnChange = document.getElementById("btnChange");
  if (r1 == true) {
    urlinput.disabled = true;
    btnChange.disabled = false;
  }
  if (r2 == true) {
    urlinput.disabled = false;
    btnChange.disabled = false;
  }
}

function changexml() {
  var r1 = document.getElementById("radio01").checked;
  var r2 = document.getElementById("radio02").checked;
  var urlinput = document.getElementById("id-url");
  if (r1 == true) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        alert("reset xml");
      }
    };
    xhttp.open("GET", "resetxml", true);
    xhttp.send();
  }
  if (r2 == true) {
    var newpath = urlinput.value;
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
      if (this.readyState == 4 && this.status == 200) {
        alert("change xml: " + newpath);
      }
    };
    xhttp.open("GET", "changexml?xmlpath=" + newpath, true);
    xhttp.send();
  }
}

function changesource() {
  var r1 = document.getElementById("radioLocal").checked;
  var r2 = document.getElementById("radioRemote").checked;
  var sourceinput = document.getElementById("id-service");
  if (r1 == true) {
    sourceinput.disabled = true;
  }
  if (r2 == true) {
    sourceinput.disabled = false;
  }
}