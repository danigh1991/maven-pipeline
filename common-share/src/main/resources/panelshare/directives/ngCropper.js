﻿/**
 * A generic confirmation for risky actions.
 * Usage: Add attributes:
 * * ng-confirm-message="Are you sure?"
 * * ng-confirm-click="takeAction()" function
 * * ng-confirm-condition="mustBeEvaluatedToTrueForTheConfirmBoxBeShown" expression
 */
angular.module('emixApp.directives').directive('ngCropper', ['$q', '$parse', function($q, $parse) {
    return {
        restrict: 'A',
        scope: {
            events: '=ngCropperEvents',
            options: '=ngCropperOptions',
            showEvent: '=ngCropperShow',
            hideEvent: '=ngCropperHide',
            proxy: '=?ngCropperProxy', // Optional.
        },
        link: function(scope, element, atts) {
            var shown = false;

            var cropperInstance;

            angular.extend(scope.events, {
                changeZoom: function(zoom){
                    cropperInstance.zoom(zoom);
                },
                rotate: function(rotate){
                    cropperInstance.rotate(rotate);
                },
                scaleX: function(scaleX){
                    cropperInstance.scaleX(scaleX);
                },
                scaleY: function(scaleY){
                    cropperInstance.scaleY(scaleY);
                },
                reset: function(){
                    cropperInstance.reset();
                },
                destroy: function(){
                    cropperInstance.destroy();
                },
                getCroppedCanvas: function(options){
                    return cropperInstance.getCroppedCanvas(options);
                },
                setData: function(data){
                    cropperInstance.setData(data);
                }
            });

            scope.$on(scope.showEvent, function() {
                if (shown) return;
                shown = true;

                preprocess(scope.options, element[0])
                    .then(function(options) {
                        cropperInstance= new Cropper(element[0], options);
                        setProxy();
                    })
            });

            function setProxy() {
                if (!scope.proxy) return;
                $parse(scope.proxy).assign(scope.$parent, function(action) {
                    cropperInstance[action]();
                });
            }

            scope.$on(scope.hideEvent, function() {
                if (!shown) return;
                shown = false;
                cropperInstance.destroy();
            });

            scope.$watch('options.disabled', function(disabled) {
                if (!shown) return;
                if (disabled) cropperInstance.disable();
                if (!disabled) cropperInstance.enable();
            });

            scope.$watch('options.dragMode', function(dragMode) {
                if (!shown) return;
                cropperInstance.setDragMode(dragMode);
            });


        }
    };

    function preprocess(options, img) {
        options = options || {};
        var result = $q.when(options); // No changes.
        if (options.maximize) {
            result = maximizeSelection(options, img);
        }
        return result;
    }

    /**
     * Change options to make selection maximum for the image.
     * fengyuanchen/cropper calculates valid selection's height & width
     * with respect to `aspectRatio`.
     */
    function maximizeSelection(options, img) {
        return getRealSize(img).then(function(size) {
            options.data = size;
            return options;
        });
    }

    /**
     * Returns real image size (without changes by css, attributes).
     */
    function getRealSize(img) {
        var defer = $q.defer();
        var size = {height: null, width: null};
        var image = new Image();

        image.onload = function() {
            defer.resolve({width: image.width, height: image.height});
        }

        image.src = img.src;
        return defer.promise;
    }
}]);
angular.module('emixApp.services').service('Cropper', ['$q', function($q) {

    this.encode = function(blob) {
        var defer = $q.defer();
        var reader = new FileReader();
        reader.onload = function(e) {
            defer.resolve(e.target.result);
        };
        reader.readAsDataURL(blob);
        return defer.promise;
    };

    this.decode = function(dataUrl) {
        var meta = dataUrl.split(';')[0];
        var type = meta.split(':')[1];
        var binary = atob(dataUrl.split(',')[1]);
        var array = new Uint8Array(binary.length);
        for (var i = 0; i < binary.length; i++) {
            array[i] = binary.charCodeAt(i);
        }
        return new Blob([array], {type: type});
    };

    this.crop = function(file, data) {
        var _decodeBlob = this.decode;
        return this.encode(file).then(_createImage).then(function(image) {
            var canvas = createCanvas(data);
            var context = canvas.getContext('2d');

            context.drawImage(image, data.x, data.y, data.width, data.height, 0, 0, data.width, data.height);

            var encoded = canvas.toDataURL(file.type);
            removeElement(canvas);

            return _decodeBlob(encoded);
        });
    };

    this.scale = function(file, data) {
        var _decodeBlob = this.decode;
        return this.encode(file).then(_createImage).then(function(image) {
            var heightOrig = image.height;
            var widthOrig = image.width;
            var ratio, height, width;

            if (angular.isNumber(data)) {
                ratio = data;
                height = heightOrig * ratio;
                width = widthOrig * ratio;
            }

            if (angular.isObject(data)) {
                ratio = widthOrig / heightOrig;
                height = data.height;
                width = data.width;

                if (height && !width)
                    width = height * ratio;
                else if (width && !height)
                    height = width / ratio;
            }

            var canvas = createCanvas(data);
            var context = canvas.getContext('2d');

            canvas.height = height;
            canvas.width = width;

            context.drawImage(image, 0, 0, widthOrig, heightOrig, 0, 0, width, height);

            var encoded = canvas.toDataURL(file.type);
            removeElement(canvas);

            return _decodeBlob(encoded);
        });
    };


    function _createImage(source) {
        var defer = $q.defer();
        var image = new Image();
        image.onload = function(e) { defer.resolve(e.target); };
        image.src = source;
        return defer.promise;
    }

    function createCanvas(data) {
        var canvas = document.createElement('canvas');
        canvas.width = data.width;
        canvas.height = data.height;
        canvas.style.display = 'none';
        document.body.appendChild(canvas);
        return canvas;
    }

    function removeElement(el) {
        el.parentElement.removeChild(el);
    }

}]);
