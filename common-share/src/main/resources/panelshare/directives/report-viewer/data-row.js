angular.module('emixApp.directives').directive('ngReportViewerRow', ['$compile', '$rootScope', '$http', 'httpServices', '$templateRequest', 'shareFunc', '$timeout', 'sharedInfo',
    function ($compile, $rootScope, $http, httpServices, $templateRequest, shareFunc, $timeout, sharedInfo) {
    'use strict';
    var DEFAULT_ISCOMMANDROW = false;
    var DEFAULT_ROWTYPE = 'simple';

    return {
        scope: {
            rowOptions: '=',
            rgData: '=',
            options: '=',
            additionalData: '=',
            events: '=',
            isCommandRow: '='
        },
        link: function (scope, element, attrs) {

            var init = function () {
                scope.serverSideMode = scope.options.apiUrl.length > 0;

                scope.rowOptions.rowType = scope.rowOptions.rowType || DEFAULT_ROWTYPE;

                scope.isCommandRow = shareFunc.getBoolDefaultValue(scope.isCommandRow, DEFAULT_ISCOMMANDROW);

                //#region Assign Template
                if(scope.rowOptions.rowTemplate) {
                    var $html = $(scope.rowOptions.rowTemplate);
                    $html.attr('data-colname', scope.rowOptions.name);
                    var cellElement = $compile($html.prop('outerHTML'))(scope);
                    $(element).append(cellElement);
                }
                else {
                    $templateRequest("/panelshare/directives/report-viewer/data-row-template.html" + $rootScope.version).then(function(html){
                        var template = angular.element(html);
                        element.append(template);
                        $compile(template)(scope);
                    });
                    /*var html = scope.rowOptions.title;
                    $(element).append(html);*/
                }
                //#endregion
            }

            $timeout(init);

            //#region Utils

            //#endregion

            //#region Events
            scope.export = function () {
                scope.events.export();
            };
            //#endregion
        }
    };
}]);