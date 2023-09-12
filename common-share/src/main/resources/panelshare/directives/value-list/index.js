angular.module('emixApp.directives').directive('ngValueList', ['sharedInfo', 'httpServices', '$rootScope', 'shareFunc', '$compile', '$timeout', '$translate', function (sharedInfo, httpServices, $rootScope, shareFunc, $compile, $timeout, $translate) {
    'use strict';

    return {
        scope: {
            options: '=',
            events: '='
        },
        templateUrl: '/panelshare/directives/value-list/template.html' + $rootScope.version,
        link: function (scope, element, attrs) {

            /*#region Init Options*/
            scope.options.valueList = scope.options.valueList || [];
            scope.options.caption = scope.options.caption || '';
            scope.options.blockClass = scope.options.blockClass || 'bzttag';
            scope.value = '';
            /*#endregion*/
 
            /*#region Utils*/
            scope.addValue = function(){
                var index = scope.options.valueList.indexOf(scope.value);
                if(index >= 0) {
                    toastr.error($translate.instant('common.duplicateElement2', {'element': $translate.instant('common.value')}));
                    return;
                }
                scope.value && scope.options.valueList.push(scope.value);
                scope.value = '';
            }
            scope.removeValue = function(item) {
                var index = scope.options.valueList.indexOf(item);
                scope.options.valueList.splice(index, 1);
            }

            /*#region Edit Mode*/
            scope.editMode = false;
            scope.oldValueForEdit = '';
            scope.editValueState = function(node){
                scope.editMode = true;
                scope.oldValueForEdit = scope.value = node;
            }
            scope.cancleEditValue = function(){
                scope.showValueError = false;
                scope.editMode = false;
                scope.value = '';
            }
            scope.editValue = function(){
                if(scope.value == scope.oldValueForEdit){
                    scope.value = '';
                    scope.editMode = false;
                }
                var index = scope.options.valueList.indexOf(scope.value);
                if(index >= 0) {
                    toastr.error("مقدار تکراری می باشد.");
                    return;
                }
                var oldIndex = scope.options.valueList.indexOf(scope.oldValueForEdit);
                if(oldIndex >= 0 && scope.value) {
                    scope.options.valueList[oldIndex] = scope.value;
                    scope.value = '';
                    scope.editMode = false;
                }
            }
            /*#endregion*/
            /*#endregion*/

            /*#region Events*/
            /*#endregion*/
        }
    }

}]);

