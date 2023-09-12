angular.module('emixApp.directives').directive('ngImageEditor', ['$http', '$q', '$timeout', function ($http, $q, $timeout) {
    'use strict';
    return {
        restrict: 'A',
        scope: {
            //applyCallback: "&",            
            options: '=?'
        },
        link: function (scope, element, attrs) {
            var elementId = attrs.id;//element.attr('id');
            var imageSrc = $(element).attr('src');
            //var Editable = scope.ngEditable || true;
                    
            //angular.extend(scope.options, {
            if(!scope.options.applyImage)
                scope.options.applyImage = function () {
                    //scope.$apply(function () {
                    var outputdata = $('#avpw_canvas_element')[0].toDataURL("image/jpeg");//$(element).attr('src')
                    scope.options.imageAfterEdit = outputdata;
                    resetEditor();
                    scope.$apply();
                    //});
                }

            scope.options.ngEditable = scope.options.ngEditable || true;

            scope.options.imageAfterEdit = scope.options.imageAfterEdit || null;
            scope.options.afterEditImageFunc = scope.options.afterEditImageFunc || 'afterImageEdit';
            scope.options.aspectRatio = scope.options.aspectRatio || '';

            $(element).dblclick(function () {
                if (scope.options.ngEditable) {                    
                    imageSrc = $(element).attr('src');
                    if (imageSrc.startsWith('data:image')) {                        
                        lunchEditor();
                    } else {
                        imageSrcToBase64(imageSrc).then(function (base64) {
                            imageSrc = base64;
                            $(element).unbind('load error');
                            $(element).attr('src', base64);                            
                            //scope.$apply(function () {
                            $timeout(function () {
                                lunchEditor();
                            });
                            
                        }, function () {

                        });
                    }

                }
            });

            var resetEditor = function () {                
                $("#avpw_holder").remove();
            }

            var lunchEditor = function () {
                if (scope.options.imageAfterEdit == null || true) {
                    featherEditor.launch({
                        image: elementId,
                        url: imageSrc
                    });
                    $('#avpw_save_button').hide();
                    $('#avpw_holder').css('direction', 'ltr !important');
                    $("#avpw_save_button").after('<b class="avpw_button avpw_primary_button" id="avpwsubmit" onClick="angular.element(document.getElementById(\'' + elementId + '\')).scope().' + scope.options.afterEditImageFunc + '();$(\'#avpw_fullscreen_bg\').hide();$(\'#avpw_controls\').hide();">Save</b>');
                    if (scope.options.aspectRatio != '') {
                        setTimeout(function () {
                            $("#avpw_main_crop").click();
                            setTimeout(function () {
                                $("div[data-crop]").hide();
                                $("div[data-crop='" + scope.options.aspectRatio + "']").show();
                                $("div[data-crop='" + scope.options.aspectRatio + "']").click();
                            },500);
                        }, 100);
                    }

                } else {
                    $('#avpw_fullscreen_bg').show();
                    $('#avpw_controls').show();
                }
                return false;
            };
            var outputdata = null;
            //scope.getData = function() {
            //    outputdata = $('#avpw_canvas_element')[0].toDataURL("image/jpeg");
            //    //$('#imagres').attr('src', outputdata);                
            //    $('#avpw_fullscreen_bg').hide();
            //    $('#avpw_controls').hide();
            //    scope.options.applyCallback(outputdata);

            //}
            var encryptUrl = function (url) {
                var iv = CryptoJS.lib.WordArray.random(128 / 8).toString(CryptoJS.enc.Hex);
                var salt = CryptoJS.lib.WordArray.random(128 / 8).toString(CryptoJS.enc.Hex);
                var cryptKey = "TJ@@r123Key";
                var aesUtil = new AesUtil(128, 1000);
                var ciphertext = aesUtil.encrypt(salt, iv, cryptKey, url);

                var aesCrypt = (iv + "::" + salt + "::" + ciphertext);
                var retValue = btoa(aesCrypt);
                return retValue;
            }
            var imageSrcToBase64 = function (imageUrl) {
                var deferred = $q.defer();
                var downloadUrl = Emix.downloadFromWebUrl + "?url=" + encodeURIComponent(encryptUrl(imageUrl));
                //contentType: "image/png", dataType: "text",
                $http.get(downloadUrl, { responseType: 'blob' }).then(function (response) {
                    //var bin = new $window.Blob([response.data]);                    
                    //deferred.resolve("data:image/JPEG;base64," + btoa(response.data));
                    var fileReader = new FileReader();
                    fileReader.onloadend = function () {
                        deferred.resolve(fileReader.result);
                    }
                    fileReader.readAsDataURL(response.data);

                }, function (response) {
                    var err = "بازیابی تصویر برای ادرس ارسالی با خطا مواجه شد.لطفاً تصویر را دانلود و از روی کامپیوتر خود آپلود کنید.";//response;
                    toastr.error(err);
                })['finally'](function () {
                });
                return deferred.promise;

                /*var deferred = $q.defer();
                toDataUrl(imageUrl, function(base64Img){
                    deferred.resolve(fileReader.result);
                })
                return deferred.promise;*/
            }

            function toDataUrl(src, callback, outputFormat) {
                // Create an Image object
                var img = new Image();
                // Add CORS approval to prevent a tainted canvas
                img.crossOrigin = 'Anonymous';
                img.onload = function() {
                    // Create an html canvas element
                    var canvas = document.createElement('CANVAS');
                    // Create a 2d context
                    var ctx = canvas.getContext('2d');
                    var dataURL;
                    // Resize the canavas to the original image dimensions
                    canvas.height = this.naturalHeight;
                    canvas.width = this.naturalWidth;
                    // Draw the image to a canvas
                    ctx.drawImage(this, 0, 0);
                    // Convert the canvas to a data url
                    dataURL = canvas.toDataURL(outputFormat);
                    // Return the data url via callback
                    callback(dataURL);
                    // Mark the canvas to be ready for garbage
                    // collection
                    canvas = null;
                };
                img.onerror = function(){
                    var err = "بازیابی تصویر برای ادرس ارسالی با خطا مواجه شد.";//response;
                    toastr.error(err);
                };
                // Load the image
                img.src = src;
                // make sure the load event fires for cached images too
                if (img.complete || img.complete === undefined) {
                    // Flush cache
                    img.src = 'data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///ywAAAAAAQABAAACAUwAOw==';
                    // Try again
                    img.src = src;
                }
            }

        }
    };
}]);
