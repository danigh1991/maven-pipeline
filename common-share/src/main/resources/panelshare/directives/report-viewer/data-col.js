angular.module('emixApp.directives').directive('ngReportViewerCol', ['$compile', '$rootScope', '$sce', '$templateCache', function ($compile, $rootScope, $sce, $templateCache) {
    'use strict';
    return {
        scope: {
            colOptions: '=',
            rgData: '=',
            additionalData: '=',
            events: '='
        },
        link: function (scope, element, attrs) {
            //#region Utils
            scope.getDataValue = function(data, name){
                let val = data;
                if(!name) return '';
                let nameArr = name.split('.');
                for(var i=0; i < nameArr.length;i++){
                    val = val[nameArr[i]];
                }
                if(scope.colOptions.formatValue){
                    return scope.colOptions.formatValue(val);
                }
                return val;
            }
            //#endregion

            //#region Assign Template
            if(scope.colOptions.colTemplate) {
                let html = scope.colOptions.colTemplate;
                var cellElement = $compile(html)(scope);
                $(element).append(cellElement);
            }
            else {
                let html = scope.getDataValue(scope.data, scope.colOptions.name);
                $(element).append(html);
            }
            //#endregion

            //#region Events
            //#endregion
        }
    };
}]);