
var exec = require('cordova/exec'),
    utils = require('cordova/utils');

module.exports = function(successCallback, errorCallback, message) {
    exec(successCallback, errorCallback, "QRReader", 'echo', [message]);
};

module.exports.jw = function(successCallback, errorCallback, message) {
    exec(successCallback, errorCallback, "QRReader", "echoAsync", message);
};
