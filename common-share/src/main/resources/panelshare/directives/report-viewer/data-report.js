angular.module('emixApp.directives').directive('ngReportViewerReport',
    ['$compile', '$rootScope', '$sce', '$filter', 'httpServices', '$http', 'orderByFilter', 'sharedInfo', 'shareFunc', '$templateRequest', '$templateCache', '$timeout', '$translate',
        function ($compile, $rootScope, $sce, $filter, httpServices, $http, orderBy, sharedInfo, shareFunc, $templateRequest, $templateCache, $timeout, $translate) {
        'use strict';
        var DEFAULT_ID = 'ng-report';
        var DEFAULT_CSSClass = 'col-xs-12';
        var DEFAULT_ALLOWEXPORT = true;
        var DEFAULT_TEXTNORESULTS = 'common.noRecordFound';
        var DEFAULT_DATATEMPLATE = '/panelshare/directives/report-viewer/index-template.html';
        var DEFAULT_CUSTOMTEMPLATE = 'custom-template.html';

        return {
            scope: {
                options: '=',
                rgTemplate: '=',
                rgData: '=',
                rgRows: '=',
                additionalData: '=',
                events: '='
            },
            link: function (scope, element, attrs) {
                scope.dataTemplate = DEFAULT_DATATEMPLATE + $rootScope.version;

                var elementHtml = element.html().trim();

                if (!elementHtml || elementHtml.length === 0)
                    elementHtml = scope.rgTemplate;

                if (elementHtml && elementHtml.length > 0) {
                    var customTemplateUrl = (scope.options.id || DEFAULT_ID) + "-" + DEFAULT_CUSTOMTEMPLATE + $rootScope.version;
                    var customTemplateHtml = '<div ng-if="item" class="' + scope.options.cssClass + '" ng-class="{' + scope.options.ngCssClass + '}">\n' +
                        elementHtml +
                        '</div>\n' +
                        '<div class="info-box notfoundbx" ng-class="{\'tblldngmd\': loadingMode}" ng-if="items === null">\n' +
                        '   {{ loadingMode ? (\'loading\' | translate) : (\'common.noRecordFound\' | translate)}}\n' +
                        '</div>';
                    $templateCache.put(customTemplateUrl, customTemplateHtml);
                    scope.dataTemplate = customTemplateUrl;
                    element.empty();
                }
                //#region Assign Template
                $templateRequest("/panelshare/directives/report-viewer/index-template.html" + $rootScope.version).then(function (html) {
                    var template = angular.element(html);
                    element.append(template);
                    $compile(template)(scope);
                });
                //#endregion

                scope.loadingMode = false;
                var init = function () {
                    scope.options.id = scope.options.id || DEFAULT_ID;
                    scope.options.cssClass = scope.options.cssClass || DEFAULT_CSSClass;
                    scope.options.allowExport = shareFunc.getBoolDefaultValue(scope.options.allowExport, DEFAULT_ALLOWEXPORT);
                    scope.options.textNoResults = scope.options.textNoResults || DEFAULT_TEXTNORESULTS;

                    scope.rgData = scope.rgData || [];
                    scope.rgRows = scope.rgRows || [];

                    //#region Server Side Mode
                    scope.options.apiUrl = scope.options.apiUrl || '';
                    scope.options.apiParams = scope.options.apiParams || {};

                    scope.serverSideMode = scope.options.apiUrl.length > 0;
                    if(scope.serverSideMode) {
                        scope.events.dataBind();
                    }
                    //#endregion
                }

                //#region Utils
                var getApiParams = function (){
                    const apiParams = {
                    };
                    let key;

                    for (key in scope.options.apiParams) {
                        var filterText = (scope.options.apiParams[key] || '');
                        if(scope.options.apiParams.hasOwnProperty(key) && filterText !== 'undefined' && typeof filterText !== 'undefined' && (typeof filterText !=='string' || filterText.length > 0)){
                            apiParams[key] = scope.options.apiParams[key];
                        }
                    }

                    return apiParams;
                }
                //#endregion

                //#region Events
                scope.$watch('rgData', function (newval, oldVal) {
                    if(!scope.serverSideMode) {
                        scope.item = scope.rgData || [];
                    }
                });


                scope.events.export = function () {
                    var args = {
                        fileName: 'ExportExcel'
                    };
                    //Export Html or pdf
                }

                scope.events.dataBind = function (){
                    scope.getData();
                }

                //#endregion

                //#region data
                var getHttpService = function (){
                    return $http({
                        method: "GET",
                        url: scope.options.apiUrl,
                        params: getApiParams(),
                        cache: false,
                        headers: httpServices.getHeaders()
                    });
                }

                scope.getData = function () {
                    if(scope.serverSideMode) {
                        NProgress.start();
                        $.blockUI();
                        getHttpService().then(function (response) {
                            var data = response.data;
                            scope.item = data.returnObject;
                        }, function (response) {
                            httpServices.handleError(response.data, response.status, response.headers, response.config);
                        })
                            .finally(function () {
                                NProgress.done();
                                $.unblockUI();
                            });
                    } else {
                        scope.item = rgData;
                    }
                }
                //#endregion
                $timeout(init);
            }
            /*compile: function (tElement) {
                console.log('adsdsa');
            }*/
        };
    }]);

