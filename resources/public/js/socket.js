var address = location.hostname + ':' + location.port;
//console.log(address);
var webSocket = new WebSocket('wss://' + address + '/websocket/');

webSocket.onopen = function () {
  console.log("connected")
};

webSocket.onclose = function () {
  location.reload(true);
};

webSocket.onmessage = function (msg) {
  if (msg.data === "update") {
    location.reload(true);
  }
};



