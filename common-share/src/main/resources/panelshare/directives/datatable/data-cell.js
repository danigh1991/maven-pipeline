angular.module('emixApp.directives').directive('ngDataTableCell', ['$compile', '$rootScope', '$sce', '$templateCache', function ($compile, $rootScope, $sce, $templateCache) {
    'use strict';
    return {
        scope: {
            cellOptions: '=',
            data: '=',
            additionalData: '=',
            events: '='
        },
        link: function (scope, element, attrs) {
            //#region Utils
            scope.getDataValue = function(data, name){
                var val = data;
                if(!name) return '';
                var nameArr = name.split('.');
                for(var i=0; i < nameArr.length;i++){
                    val = val[nameArr[i]];
                }
                if(scope.cellOptions.formatValue){
                    return scope.cellOptions.formatValue(val);
                }
                return val;
            }
            //#endregion

            //#region Assign Template
            if(scope.cellOptions.cellTemplate) {
                var html = scope.cellOptions.cellTemplate;
                var cellElement = $compile(html)(scope);
                $(element).append(cellElement);
            }
            else {
                var html = scope.getDataValue(scope.data, scope.cellOptions.name);
                $(element).append(html);
            }
            //#endregion

            //#region Events
            //#endregion
        }
    };
}]);