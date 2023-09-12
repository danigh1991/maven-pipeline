(function (root, factory) {
    // AMD
    if (typeof define === 'function' && define.amd) define(['angular'], factory);
    // Global
    else factory(angular);
}(this, function (angular) {

    /*(angular.module('ng.ckeditor', ['ng']))*/
    (angular.module('emixApp.directives'))
        .directive('ckeditor', ['$parse', ckeditorDirective]);

    // Polyfill setImmediate function.
    var setImmediate = window && window.setImmediate ? window.setImmediate : function (fn) {
        setTimeout(fn, 0);
    };

    /**
     * CKEditor directive.
     *
     * @example
     * <div ckeditor="options" ng-model="content" ready="onReady()"></div>
     */

    function ckeditorDirective($parse) {
        return {
            restrict: 'A',
            require: ['ckeditor', 'ngModel'],
            controller: [
                '$scope',
                '$element',
                '$attrs',
                '$parse',
                '$q',
                '$timeout',
                ckeditorController
            ],
            link: function (scope, element, attrs, ctrls) {
                // get needed controllers
                var controller = ctrls[0]; // our own, see below
                var ngModelController = ctrls[1];

                // Initialize the editor content when it is ready.
                controller.ready().then(function initialize() {
                    // Sync view on specific events.
                    ['dataReady', 'change', 'blur', 'saveSnapshot'].forEach(function (event) {
                        controller.onCKEvent(event, function syncView() {
                            ngModelController.$setViewValue(controller.instance.getData() || '');
                        });
                    });

                    var readOnlyBool = (attrs.ngDisabled || '').toLowerCase() === 'true';
                    controller.instance.setReadOnly(readOnlyBool);
                    attrs.$observe('ngDisabled', function (readonly) {
                        var readOnlyBool = (readonly || '').toLowerCase() === 'true';
                        controller.instance.setReadOnly(readOnlyBool);
                    });

                    // Defer the ready handler calling to ensure that the editor is
                    // completely ready and populated with data.
                    setImmediate(function () {
                        $parse(attrs.ready)(scope);
                    });
                });

                // Set editor data when view data change.
                ngModelController.$render = function syncEditor() {
                    controller.ready().then(function () {
                        // "noSnapshot" prevent recording an undo snapshot
                        controller.instance.setData(ngModelController.$viewValue || '', {
                            noSnapshot: true,
                            callback: function () {
                                // Amends the top of the undo stack with the current DOM changes
                                // ie: merge snapshot with the first empty one
                                // http://docs.ckeditor.com/#!/api/CKEDITOR.editor-event-updateSnapshot
                                controller.instance.fire('updateSnapshot');
                            }
                        });
                    });
                };
            }
        };
    }

    /**
     * CKEditor controller.
     */

    function ckeditorController($scope, $element, $attrs, $parse, $q, $timeout) {

        var config = $parse($attrs.ckeditor)($scope) || {
            //language: 'fa',
            allowedContent: true,
            entities: false,
            removePlugins: 'iframe,flash,smiley,form',
            skin: 'moono-lisa'
        };
        if ($attrs.removePlugins != undefined) {
            config.removePlugins = $attrs.removePlugins;
        }
        if ($attrs.skin != undefined) {
            config.skin = $attrs.skin;
        }
        if ($attrs.width != undefined) {
            config.width = $attrs.width;
        }
        if ($attrs.height != undefined) {
            config.height = $attrs.height;
        }
        if ($attrs.resizeEnabled != undefined) {
            config.resize_enabled = ($attrs.resizeEnabled == "false") ? false : true;
        }

        var editorElement = $element[0];
        var instance;
        var readyDeferred = $q.defer(); // a deferred to be resolved when the editor is ready

        // Create editor instance.
        if (editorElement.hasAttribute('contenteditable') &&
            editorElement.getAttribute('contenteditable').toLowerCase() == 'true') {

            instance = this.instance = CKEDITOR.inline(editorElement, config);
        }
        else {
            instance = this.instance = CKEDITOR.replace(editorElement, config);
        }

        /**
         * Listen on events of a given type.
         * This make all event asynchronous and wrapped in $scope.$apply.
         *
         * @param {String} event
         * @param {Function} listener
         * @returns {Function} Deregistration function for this listener.
         */

        this.onCKEvent = function (event, listener) {
            instance.on(event, asyncListener);

            function asyncListener() {
                var args = arguments;
                setImmediate(function () {
                    applyListener.apply(null, args);
                });
            }

            function applyListener() {
                var args = arguments;
                $scope.$apply(function () {
                    listener.apply(null, args);
                });
            }

            // Return the deregistration function
            return function $off() {
                instance.removeListener(event, applyListener);
            };
        };

        this.onCKEvent('instanceReady', function() {
            readyDeferred.resolve(true);
        });

        //#region Mehdi process Image
        /*this.onCKEvent('widgetDefinition', function(evt) {

            var widgetData = evt.data;
            if (widgetData.name != "image" || widgetData.dialog != "image2") return;

            //Override of upcast
            if (!widgetData.stdUpcast) {
                widgetData.stdUpcast = widgetData.upcast;

                widgetData.upcast = function (el, data) {
                    var el = widgetData.stdUpcast(el, data);

                    if (!el) return el;

                    var attrsHolder = el.name == 'a' ? el.getFirst() : el;
                    var attrs = attrsHolder.attributes;

                    if (el && el.name == "img") {
                        if (el.styles) {
                            attrs.width = (el.styles.width + "").replace('px', '');
                            attrs.height = (el.styles.height + "").replace('px', '');

                            delete el.styles.width;
                            delete el.styles.height;

                            attrs.style = CKEDITOR.tools.writeCssText(el.styles);
                        }
                    }

                    return el;
                }
            }

            //Override of downcast
            function getImageDimension(url){
                var r = $.Deferred();

                $('<img/>').attr('src', url).load(function(){
                    var s = {w:this.width, h:this.height};
                    r.resolve(s)
                });
                return r;
            }
            if (!widgetData.stdDowncast) {
                widgetData.stdDowncast = widgetData.downcast;

                widgetData.downcast = function (el) {

                    el = this.stdDowncast(el);
                    var images = el.find('img');
                    $.each(images, function (index, imageEle) {
                        if ((!imageEle.attributes.width || !imageEle.attributes.height) && imageEle.attributes.src) {
                            //var $imageEle = $(imageEle);
                            getImageDimension(imageEle.attributes.src).done(function(mageDimension){
                                $timeout(function () {
                                    var ckframe = $('iframe.cke_wysiwyg_frame');
                                    var docImageEle = $("img [src='" + imageEle.attributes.src + "']", ckframe.contents());
                                    if (docImageEle.length) {
                                        docImageEle[0].attributes.width = mageDimension.w;
                                        docImageEle[0].attributes.height = mageDimension.h;
                                    }
                                },1000);
                            });

                        }
                    });

                    /!*el = this.stdDowncast(el);

                    var attrsHolder = el.name == 'a' ? el.getFirst() : el;
                    var attrs = attrsHolder.attributes;

                    var realWidth, realHeight;

                    var widgets = instance.widgets.instances;
                    for (widget in widgets) {

                        if (widgets[widget].name != "image" || widgets[widget].dialog != "image2") {
                            continue;
                        }

                        realWidth = $(widgets[widget].element.$).width();
                        realHeight = $(widgets[widget].element.$).height();
                    }

                    var style = CKEDITOR.tools.parseCssText(attrs.style)

                    if (attrs.width) {
                        style.width = realWidth + "px";
                        delete attrs.width;
                    }
                    if (attrs.height) {
                        style.height = realHeight + "px";
                        delete attrs.height;
                    }

                    attrs.style = CKEDITOR.tools.writeCssText(style);*!/

                    return el;
                }
            }
        });*/
        //#endregion

        /**
         * Check if the editor if ready.
         *
         * @returns {Promise}
         */
        this.ready = function ready() {
            return readyDeferred.promise;
        };

        // Destroy editor when the scope is destroyed.
        $scope.$on('$destroy', function onDestroy() {
            // do not delete too fast or pending events will throw errors
            readyDeferred.promise.then(function() {
                instance.destroy(false);
            });
        });
    }
}));