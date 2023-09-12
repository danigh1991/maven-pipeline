angular.module('emixApp.controllers').controller('selectimage_index',
    ['$rootScope', '$scope', '$timeout', 'Cropper', 'httpServices', '$modal', 'passObject', '$modalInstance', 'FileUploader', 'shareFunc', '$q', '$translate',
        function ($rootScope, $scope, $timeout, Cropper, httpServices, $modal, passObject, $modalInstance, FileUploader, shareFunc, $q, $translate) {
        'use strict';

        //#region Properties
        var mFormData = passObject.formData;
        var _viewMode = passObject.viewMode ? passObject.viewMode : 0;
        $scope.fixRatio = passObject.ratioWidth && passObject.ratioHeight;
        $scope.config = {freeCrop : !$scope.fixRatio};
        $scope.showRatio = passObject.showRatio ? passObject.showRatio : false;
        $scope.config.ratioWidth = $scope.fixRatio ? passObject.ratioWidth : 3;
        $scope.config.ratioHeight = $scope.fixRatio ? passObject.ratioHeight : 2;

        $scope.sendSize = passObject.sendSize ? passObject.sendSize : false;
        //#endregion

        //#region Utils
        $scope.openSelectFile = function (time) {
            time = time || 0;
            $timeout(function () {
                $('#uploaderImageSingle').trigger('click');
            }, time);
        }
        $modalInstance.opened.then(
            function () {
                uploaderImageSingle.clearQueue();
                $scope.openSelectFile();
            });

        var getItrmType = function (item) {
            var itemType = item.type.slice(item.type.lastIndexOf('/') + 1);
            return itemType.toLowerCase();
        }
        //#endregion

        //#region Image Cropper
        var file, data, changeByTextBox = false;;
        $scope.DRAG_MODE_CROP = 'crop';
        $scope.DRAG_MODE_MOVE = 'move';
        $scope.DRAG_MODE_NONE = 'none'; // Events

        $scope.ZOOMIN = 0.1;
        $scope.ZOOMOUT = -0.1;

        $scope.ROTATERIGHT = 45;
        $scope.ROTATELEFT = -45;

        $scope.scaleXDefault = 1;
        $scope.scaleYDefault = 1;

        /*if($scope.dataUrl){
            hideCropper();
            $timeout(showCropper);
        }*/

        /**
         * Method is called every time file input's value changes.
         * Because of Angular has not ng-change for file inputs a hack is needed -
         * call `angular.element(this).scope().onFile(this.files[0])`
         * when input's event is fired.
         */
        /*$scope.onFile = function(blob) {
            console.log('File selected');
            Cropper.encode((file = blob)).then(function(dataUrl) {
                $scope.dataUrl = dataUrl;
                hideCropper();
                $timeout(showCropper);  // wait for $digest to set image's src
            });
        };*/
        /**
         * Croppers container object should be created in controller's scope
         * for updates by directive via prototypal inheritance.
         * Pass a full proxy name to the `ng-cropper-proxy` directive attribute to
         * enable proxing.
         */
        $scope.cropper = {};
        $scope.cropperProxy = 'cropper.first';

        $scope.events = {};

        $scope.ratioWidthChanged = function($event) {
            changeByTextBox = true;
            setTimeout(function() {
                $scope.$apply(function () {
                    //$scope.config.ratioWidth = $event.ratioWidth;
                    $scope.setAspectRation($scope.config.ratioWidth, $scope.config.ratioHeight);
                    hideCropper();
                    showCropper();
                    /*$scope.events.setData({
                        aspectRatio : $scope.config.ratioWidth / $scope.config.ratioHeight
                    });*/
                })
            },10);

        }
        $scope.ratioHeightChanged = function($event) {
            changeByTextBox = true;
            setTimeout(function() {
                $scope.$apply(function () {
                    //$scope.config.ratioHeight = $event.ratioHeight;
                    $scope.setAspectRation($scope.config.ratioWidth, $scope.config.ratioHeight);
                    hideCropper();
                    showCropper();
                    /*$scope.events.setData({
                        aspectRatio : $scope.config.ratioWidth / $scope.config.ratioHeight
                    });*/
                })
            },10);
        }
        $scope.freeCropChanged = function() {
            changeByTextBox = true;
            setTimeout(function() {
                $scope.$apply(function () {
                    $scope.setAspectRation($scope.config.ratioWidth, $scope.config.ratioHeight);
                    hideCropper();
                    showCropper();
                    /*$scope.events.setData({
                        aspectRatio : $scope.config.ratioWidth / $scope.config.ratioHeight
                    });*/
                })
            },10);
        }

        /**
         * Object is used to pass options to initalize a cropper.
         * More on options - https://github.com/fengyuanchen/cropper#options
         */
        var recursiveCall = false;
        $scope.options = {
            viewMode: _viewMode,
            maximize: true,
            autoCropArea: 1,
            zoomOnWheel: false,
            crop: function (dataNew) {
                data = dataNew;
                if (changeByTextBox === false) {
                    $scope.config.ratioWidth = Math.round(data.detail.width);
                    $scope.config.ratioHeight = Math.round(data.detail.height);
                }

                var width = data.detail.width;
                var height = data.detail.height;
                var cropSize = {};
                if (passObject.ratioWidth && width < passObject.ratioWidth)
                    cropSize.width = passObject.ratioWidth;

                if(passObject.ratioHeight && height < passObject.ratioHeight)
                    cropSize.height = passObject.ratioHeight;

                if(cropSize.width || cropSize.height) {
                    if(!recursiveCall) {
                        recursiveCall = true;
                        $scope.events.setData(cropSize);
                    }else
                        recursiveCall = false;
                }

                setTimeout(function() {
                    changeByTextBox = false;
                },1000);
            }
        };

        $scope.setAspectRation = function(ratioWidth, ratioHeight) {
            if ($scope.config.freeCrop) {
                $scope.options.initialAspectRatio = ratioWidth / ratioHeight;
                $scope.options.aspectRatio = NaN;
            }
            else {
                $scope.options.aspectRatio = ratioWidth / ratioHeight;
                $scope.options.initialAspectRatio = NaN;
            }
        }
        $scope.setAspectRation($scope.config.ratioWidth, $scope.config.ratioHeight);
        /**
         * Showing (initializing) and hiding (destroying) of a cropper are started by
         * events. The scope of the `ng-cropper` directive is derived from the scope of
         * the controller. When initializing the `ng-cropper` directive adds two handlers
         * listening to events passed by `ng-cropper-show` & `ng-cropper-hide` attributes.
         * To show or hide a cropper `$broadcast` a proper event.
         */
        $scope.showEvent = 'show';
        $scope.hideEvent = 'hide';

        function showCropper() {
            $scope.$broadcast($scope.showEvent);
            setTimeout(function() {
                $scope.reset();
            },300);
        }

        function hideCropper() {
            $scope.$broadcast($scope.hideEvent);
        }

        //#endregion

        //#region Event
        $scope.options.dragMode = $scope.DRAG_MODE_MOVE;
        $scope.changeDragMode = function (_dragMode) {
            $scope.options.dragMode = _dragMode;
        }
        $scope.changeZoom = function (_zoom) {
            $scope.events.changeZoom(_zoom);
        }
        $scope.rotate = function (_rotate) {
            $scope.events.rotate(_rotate);
        }
        $scope.scaleX = function () {
            $scope.scaleXDefault = -1 * $scope.scaleXDefault;
            $scope.events.scaleX($scope.scaleXDefault);
        }
        $scope.scaleY = function () {
            $scope.scaleYDefault = -1 * $scope.scaleYDefault;
            $scope.events.scaleY($scope.scaleYDefault);
        }
        $scope.reset = function () {
            $scope.events.reset();
        }
        $scope.bgColorSettings = {
            control: 'hue',
            position: 'top left'
        };
        $scope.bgColor = '#fff';
        $scope.getCroppedCanvas = function (item) {
            var _fillColor = $scope.bgColor;
            if(getItrmType(item.file) === 'png')
                _fillColor = 'transparent';

            return $scope.events.getCroppedCanvas({fillColor: _fillColor});
        }
        //#endregion

        $scope.uploadSuccess = function (tmpImage) {
            $modalInstance.close(tmpImage);
        }

        $scope.closeModal = function () {
            $modalInstance.dismiss('cancel');
        }

        $scope.uploadImage = function () {
            if (uploaderImageSingle.queue.length !== 1) {
                toastr.error("هیج فایلی انتخاب نشده");
                return;
            }

            NProgress.start();
            $.blockUI();
            uploaderImageSingle.uploadAll();

        }
        //#region uploaderImageSingle
        var uploaderImageSingle = $scope.uploaderImageSingle = new FileUploader({
            queueLimit: 2,
            url: passObject.uploadUrl
        });
        //#region FILTERS
        uploaderImageSingle.filters.push({
            name: 'imageFilter',
            fn: function (item /*{File|FileLikeObject}*/, options) {
                var type = '|' + getItrmType(item) + '|';
                //return '|jpg|png|jpeg|bmp|gif|'.indexOf(type) !== -1;
                var validExtention = '|jpeg|jpg|png|'.indexOf(type) !== -1;
                if (!validExtention) toastr.error("شما فقط مجاز به انتخاب عکس با پسوند jpeg,jpg,png میباشید.");
                return validExtention;
            }
        });

        uploaderImageSingle.filters.push({
            name: 'imageCountFilter',
            fn: function (item /*{File|FileLikeObject}*/, options) {
                var isFileCountValid = uploaderImageSingle.queue.length <= 1;
                if (!isFileCountValid) toastr.error("حداکثر تعداد عکس مجاز ، 1 عدد می باشد. ");
                return isFileCountValid;
            }
        });
        //#endregion FILTERS

        //#region Image Size Filter
        function getRealSize(imageData) {
            var defer = $q.defer();
            var size = {height: null, width: null};
            var image = new Image();

            image.onload = function() {
                defer.resolve({width: image.width, height: image.height});
            }

            image.src = imageData;
            return defer.promise;
        }
        var checkImageSize = function(event, callback){
            getRealSize(event.target.result).then(function(size) {
                var errorMessage = '';
                if (passObject.ratioWidth && size.width < passObject.ratioWidth)
                    errorMessage = 'عرض تصویر انتخابی ' + size.width + ' میباشد، حداقل عرض تصویر باید ' + passObject.ratioWidth + ' باشد.';

                if(passObject.ratioHeight && size.height < passObject.ratioHeight)
                    errorMessage += (errorMessage.length > 0 ? '<br />' : '') + 'ارتفاع تصویر انتخابی ' + size.height + ' میباشد، حداقل ارتفاع تصویر باید ' + passObject.ratioHeight + ' باشد.';

                if(errorMessage.length > 0){
                    toastr.error(errorMessage);
                    uploaderImageSingle.clearQueue();
                    throw new Error(errorMessage);
                }
                else
                    callback();

            });
        }
        //#endregion

        //#region CALLBACKS
        uploaderImageSingle.onAfterAddingFile = function (fileItem) {
            if (uploaderImageSingle.queue.length > 1) {
                uploaderImageSingle.queue.shift();
            }
            fileItem.croppedImage = '';
            var reader = new FileReader();
            reader.onload = function (event) {
                checkImageSize(event, function () {
                    //$scope.$apply(function () {
                    $scope.dataUrl = event.target.result;
                    hideCropper();
                    $timeout(showCropper);
                    //});
                });
            };
            reader.readAsDataURL(fileItem._file);
        };

        uploaderImageSingle.onBeforeUploadItem = function (item) {
            var croppedCanvas = $scope.getCroppedCanvas(item);
            if (!croppedCanvas) {
                toastr.error("خطا در برش تصویر");
                return;
            }
            /*croppedCanvas.toBlob(function (result) {
                var blob = result;
                item._file = blob;
                item.alias = 'image';
                item.formData.push({ productId: 0, type: 1, isMaster: false});
                item.headers = httpServices.getCSRfToken();
            },'image/jpeg',1);*/
            var contentType = 'image/jpeg';
            if(getItrmType(item.file) === 'png')
                contentType = 'image/png'

            var blob = shareFunc.dataURItoBlob(croppedCanvas.toDataURL(contentType, 1));
            item._file = blob;
            item.alias = 'image';
            //item.formData.push({ productId: 0, type: 1, isMaster: false});
            item.formData = [];
            if($scope.sendSize){
                mFormData.width = $scope.config.ratioWidth;
                mFormData.height = $scope.config.ratioHeight;
            }
            item.formData.push(mFormData);
            //item.headers = httpServices.getCSRfToken();
            item.headers = httpServices.addAuthHeader(item.headers);
        };

        uploaderImageSingle.onSuccessItem = function (fileItem, response, status, headers) {
            NProgress.done();
            $.unblockUI();
            toastr.success($translate.instant('common.successfullySavedMessage'));
            $scope.uploadSuccess(response.returnObject);
        };
        uploaderImageSingle.onErrorItem = function (fileItem, response, status, headers) {
            NProgress.done();
            $.unblockUI();
            fileItem.isUploaded = false;
            var errorMessage = response ? response.errorMessage ? response.errorMessage : 'در حین باگزاری عکس خطایی رخ داده است.لطفاً مجدد تلاش نمایید.' : 'در حین باگزاری عکس خطایی رخ داده است.لطفاً مجدد تلاش نمایید.';
            toastr.error(errorMessage);
        };
        uploaderImageSingle.onCompleteAll = function () {
        };
        //#endregion CALLBACKS
        //#endregion
        $scope.$on("$destroy", function () {
            hideCropper();
        });
    }]);
