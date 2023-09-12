angular.module('emixApp.directives').directive('qrcodeReader', ['$translate', '$rootScope', '$timeout', function ($translate, $rootScope, $timeout) {
    'use strict';
    var DEFAULT_ID = 'qr-reader';
    var DEFAULT_CSS_CLASS = 'qrreaderbx';
    var DEFAULT_CAMERA_WIDTH = 400;
    return {
        scope: {
            options: '=',
            events: '='
        },
        templateUrl: '/panelshare/directives/qrcode-reader/template.html' + $rootScope.version,
        link: function (scope, element, attrs) {
            /*#region options*/
            scope.options.id = scope.options.id || DEFAULT_ID;
            scope.options.cssClass = scope.options.cssClass || DEFAULT_CSS_CLASS;
            scope.options.cameraWidth = scope.options.cameraWidth || DEFAULT_CAMERA_WIDTH;
            //var resultContainer = document.getElementById(scope.options.id + '-results');
            var timeoutForClearLastResult;
            /*#endregion*/

            /*#region events*/
            var lastResult;
            scope.onScanSuccess = function (decodedText) {
                if (decodedText && decodedText.length > 0) {
                    if(decodedText === lastResult){
                        scope.events.onScanDuplicate && scope.events.onScanDuplicate(decodedText);
                        timeoutForClearLastResult && clearTimeout(timeoutForClearLastResult);
                        timeoutForClearLastResult = setTimeout(function () {
                            lastResult = '';
                        }, 2000);
                        return;
                    }
                    console.log(`Scan result ${decodedText}`);
                    lastResult = decodedText;
                    // Handle on success condition with the decoded message.
                    scope.events.onScanSuccess && scope.events.onScanSuccess(decodedText);
                }else {
                }
            }
            /*#endregion*/

            $timeout(function () {
                /*#region variables*/
                var video = document.createElement("video");
                var containerElement = document.getElementById(scope.options.id);
                var containerElementWidth = $(containerElement).width();
                scope.options.cameraWidth = Math.min(scope.options.cameraWidth, containerElementWidth) - 10;
                var diodElement = document.getElementById(scope.options.id + "-diod");
                var canvasElement = document.getElementById(scope.options.id + "-canvas");
                var canvas = canvasElement.getContext("2d");
                var loadingImage = document.getElementById(scope.options.id + "-loadingImage");
                /*#endregion*/

                /*#region utils*/
                function prepreUserMedia() {
                    // Older browsers might not implement mediaDevices at all, so we set an empty object first
                    if (navigator.mediaDevices === undefined) {
                        navigator.mediaDevices = {};
                    }

                    // Some browsers partially implement mediaDevices. We can't just assign an object
                    // with getUserMedia as it would overwrite existing properties.
                    // Here, we will just add the getUserMedia property if it's missing.
                    if (navigator.mediaDevices.getUserMedia === undefined) {
                        navigator.mediaDevices.getUserMedia = function (constraints) {

                            // First get ahold of the legacy getUserMedia, if present
                            var getUserMedia = navigator.webkitGetUserMedia || navigator.mozGetUserMedia;

                            // Some browsers just don't implement it - return a rejected promise with an error
                            // to keep a consistent interface
                            if (!getUserMedia) {
                                return Promise.reject(new Error('getUserMedia is not implemented in this browser'));
                            }

                            // Otherwise, wrap the call to the old navigator.getUserMedia with a Promise
                            return new Promise(function (resolve, reject) {
                                getUserMedia.call(navigator, constraints, resolve, reject);
                            });
                        }
                    }
                }

                function drawLine(begin, end, color) {
                    canvas.beginPath();
                    canvas.moveTo(begin.x, begin.y);
                    canvas.lineTo(end.x, end.y);
                    canvas.lineWidth = 4;
                    canvas.strokeStyle = color;
                    canvas.stroke();
                }
                /*#endregion*/

                /*#region methods*/
                // Use facingMode: environment to attemt to get the front camera on phones
                prepreUserMedia();
                navigator.mediaDevices.getUserMedia({video: {facingMode: "environment"}})
                    .then(function (stream) {
                        video.srcObject = stream;
                        video.setAttribute("playsinline", true); // required to tell iOS safari we don't want fullscreen
                        video.play();
                        requestAnimationFrame(tick);
                    }, function (err) {
                        console.log(err);
                    });

                function tick() {
                    if (video.readyState === video.HAVE_ENOUGH_DATA) {
                        loadingImage.hidden = true;
                        canvasElement.hidden = false;
                        diodElement.hidden = false;

                        scope.cameraHeight = scope.options.cameraWidth * video.videoHeight / video.videoWidth;
                        canvasElement.width = scope.options.cameraWidth;
                        diodElement.width = scope.options.cameraWidth;
                        canvasElement.height = scope.cameraHeight;
                        var docStyle = document.documentElement.style;
                        docStyle.setProperty('--canvas-element-height', scope.cameraHeight + 'px');

                        canvas.drawImage(video, 0, 0, canvasElement.width, canvasElement.height);
                        var imageData = canvas.getImageData(0, 0, canvasElement.width, canvasElement.height);
                        var code = jsQR(imageData.data, imageData.width, imageData.height, {
                            inversionAttempts: "dontInvert",
                        });
                        if (code) {
                            drawLine(code.location.topLeftCorner, code.location.topRightCorner, "#FF3B58");
                            drawLine(code.location.topRightCorner, code.location.bottomRightCorner, "#FF3B58");
                            drawLine(code.location.bottomRightCorner, code.location.bottomLeftCorner, "#FF3B58");
                            drawLine(code.location.bottomLeftCorner, code.location.topLeftCorner, "#FF3B58");
                            scope.onScanSuccess(code.data);
                        } else {
                        }
                    }
                    requestAnimationFrame(tick);
                }
                /*#endregion*/
            }, 0);
        }
    };
}]);