let oldXHROpen = window.XMLHttpRequest.prototype.open;
let oldXHRSend = window.XMLHttpRequest.prototype.send;
let redirectURL = ""; //replace with the redirectURI provided to the plugin
var recordedURL;

window.XMLHttpRequest.prototype.open = (method, url, async, user, password) => {
  if (method === "POST") {
    recordedURL = url;
  }
  oldXHROpen(method, url, async, user, password);
};

window.XMLHttpRequest.prototype.send = (body) => {
  if (body && recordedURL) {
    window.requestInterceptor.onRequest(this.recordedURL, body);
  } else {
    oldXHRSend(body);
  }
};
