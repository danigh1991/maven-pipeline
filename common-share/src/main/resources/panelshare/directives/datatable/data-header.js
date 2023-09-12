angular.module('emixApp.directives').directive('ngDataTableHeader', ['$compile', '$rootScope', '$http', 'httpServices', '$templateRequest', 'shareFunc', '$timeout', 'sharedInfo',
    function ($compile, $rootScope, $http, httpServices, $templateRequest, shareFunc, $timeout, sharedInfo) {
    'use strict';
    var DEFAULT_ISCOMMANDCOL = false;
    var DEFAULT_FILTERABLE = true;
    var DEFAULT_ISGLOBALFILTERPARAM = false;
    return {
        scope: {
            headerOptions: '=',
            options: '=',
            additionalData: '=',
            events: '=',
            isCommandCol: '=',
            serverSideMode: '='
        },
        link: function (scope, element, attrs) {

            scope.filterListResult = [];
            var init = function () {
                scope.serverSideMode = scope.options.apiUrl.length > 0;

                scope.isMobile = $('body').hasClass('mbdy');

                scope.isCommandCol = shareFunc.getBoolDefaultValue(scope.isCommandCol, DEFAULT_ISCOMMANDCOL);
                var hasCellTemplate = (scope.headerOptions.cellTemplate || '').length > 0;

                scope.headerOptions.filterControlClass = scope.headerOptions.filterControlClass || '';

                scope.headerOptions.filterable = shareFunc.getBoolDefaultValue(scope.headerOptions.filterable, DEFAULT_FILTERABLE && (!hasCellTemplate || scope.serverSideMode));

                //User When Filter Param is Custom Param And Can't Use Like
                scope.headerOptions.isGlobalFilterParam = shareFunc.getBoolDefaultValue(scope.headerOptions.isGlobalFilterParam, DEFAULT_ISGLOBALFILTERPARAM);

                scope.headerOptions.filterListKey = scope.headerOptions.filterListKey || 'id';
                scope.headerOptions.filterListValue = scope.headerOptions.filterListValue || 'name';

                scope.headerOptions.dataType = scope.headerOptions.dataType || sharedInfo.dataType.string;

                //For create column filter
                scope.headerOptions.keyName = scope.headerOptions.keyName || scope.headerOptions.name;
                if(scope.headerOptions.filterable && scope.headerOptions.dataType === sharedInfo.dataType.list){
                    if((typeof scope.headerOptions.filterList) === 'string') {
                        $http({
                            method: "GET",
                            url: scope.headerOptions.filterList,
                            params: {},
                            cache: false,
                            headers: httpServices.getHeaders()
                        }).then(function (response) {
                            var data = response.data;
                            scope.headerOptions.filterList = data.returnObject;
                            prepareFilterList();
                        }, function (response) {
                            httpServices.handleError(response.data, response.status, response.headers, response.config);
                        })
                            .finally(function () {
                            });
                    }else {
                        prepareFilterList();
                    }
                }

                //#region Assign Template
                if(scope.headerOptions.headerTemplate) {
                    var $html = $(scope.headerOptions.headerTemplate);
                    $html.attr('data-colname', scope.headerOptions.name);
                    var cellElement = $compile($html.prop('outerHTML'))(scope);
                    $(element).append(cellElement);
                }
                else {
                    $templateRequest("/panelshare/directives/datatable/header-template.html" + $rootScope.version).then(function(html){
                        var template = angular.element(html);
                        element.append(template);
                        $compile(template)(scope);
                    });
                    /*var html = scope.headerOptions.title;
                    $(element).append(html);*/
                }
                //#endregion
            }

            $timeout(init);

            //#region Utils
            var prepareFilterList = function () {
                var firstItem = {};
                firstItem[scope.headerOptions.filterListKey] = '';
                firstItem[scope.headerOptions.filterListValue] = '----------';
                scope.filterListResult = (scope.headerOptions.filterList || []);
                scope.filterListResult.unshift(firstItem);
                scope.headerOptions.filterText = '';
            }
            //#endregion

            //#region Events
            scope.sortBy = function (propertyName, allowSorting) {
                if(!allowSorting) return;
                if(scope.options.propertyName === propertyName && scope.options.reverse){
                    scope.options.propertyName = null;
                }else {
                    scope.options.reverse = (scope.options.propertyName === propertyName) ? !scope.options.reverse : false;
                    scope.options.propertyName = propertyName;
                }
                scope.events.setPage(1);
            };

            scope.export = function () {
                scope.events.export();
            };

            scope.filter = function () {
                scope.events.filter();
            };

            scope.toggleMobileSearchActive = function () {
                scope.options.mobileSearchActive = !scope.options.mobileSearchActive;
            }
            //#endregion
        }
    };
}]);