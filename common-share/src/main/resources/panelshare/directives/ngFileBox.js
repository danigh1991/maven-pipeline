angular.module('emixApp.directives').directive('ngFileBox', ['$modal', function ($modal) {
    'use strict';
    var DEFAULT_TARGET_TYPE = 'site';
    var DEFAULT_TARGET_ID = 0;
    return {
        restrict: 'AE'
        , replace: true
        , transclude: true
        ,scope: {
            events: '=',
            imgSrc: '=ngModel',
            fbDisabled: '=?disabled',
            fbRequired: '=?fbRequired',
            option: '=?'
        }
        , template: function (element, attrs) {
            var html = '';
            html += '<div class="ngfileboxcontainer' + (attrs.containerclass ? ' ' + attrs.containerclass : '') + '">';
            html += ' <div class="input-group has-float-label" style="display: flex;width: 100%">'
            html += '  <input type="input" placeholder=" " ng-disabled="'+ (attrs.fbDisabled === 'true') +'"';
            html += '     class="form-control txtlefttoright"';// required="'+ (attrs.fbRequired || false) +'"';
            html +=       attrs.inputId ? ' id="' + attrs.inputId + '"' : '';
            html +=       attrs.inputName ? ' name="' + attrs.inputName + '"' : '';
            html +=       attrs.ngModel ? ' ng-model="imgSrc"' : '';
            html += '  />';
            html += '  <label for="' + attrs.inputId + '">' + attrs.inputTitle + '</label>';
            html += ' <button ng-click="selectFile()" title="' + attrs.buttonTitle + '" ng-disabled="'+ (attrs.fbDisabled === 'true') +'" class="btn btn-sm btn-success btn-ngselect-file"><i class="fa fa-plus"></i></button>'
            html += ' </div>';
            html += '</div>';
            return html;
        }
        ,link: function(scope, element, attributes) {
            scope.option = scope.option || {};
            var init = function() {
                scope.option.targetType = scope.option.targetType || DEFAULT_TARGET_TYPE;
                scope.option.targetId = scope.option.targetId || DEFAULT_TARGET_ID;
            }
            init();

            scope.fbDisabled = scope.fbDisabled || false;
            scope.selectFile = function () {
                browseFileManager();
            }
            var getFileManagerBaseUrl = function () {
                return '/' + scope.option.targetType + '/' + scope.option.targetId;
            }
            var browseFileManager = function () {

                /*sharedInfo.setFileManageBaseUrl('/article/' + $scope.result.id);
                sharedInfo.setEnableCrop(true);
                sharedInfo.setFileManagerImageType(1);//Main Image
                sharedInfo.setCropRatio('5:4');*/
                var passedObj = {
                    id: 0,
                    title: ''
                }

                var passObject = {
                    object: passedObj,
                    fileManageBaseUrl: getFileManagerBaseUrl(),
                    enableCrop: false,
                    cropRatio: '',
                    fileManagerImageType: 2
                }
                var dialogInst = $modal.open({
                    templateUrl: '/nimdalenap/home/ngApp/pages/modal/file-manager.html',
                    controller: 'filemanage_index',
                    size: 'lg',
                    backdrop: 'static',
                    keyboard: false,//true
                    resolve: {
                        passObject: function () {
                            return passObject;
                        }
                    }
                });
                dialogInst.result.then(function (result) {
                    //console.log(result);
                    scope.imgSrc = result.fileUrl;
                    if(attributes.imageName){
                        scope.imgSrc = scope.imgSrc.replace('img0', attributes.imageName);
                    }
                    angular.element('body>#context-menu').remove();
                }, function () {
                    //MehdiB
                    angular.element('body>#context-menu').remove();
                });

            }
        }
    }
}]);

