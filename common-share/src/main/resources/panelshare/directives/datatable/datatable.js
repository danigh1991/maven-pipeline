angular.module('emixApp.directives').directive('ngDataTable',
    ['$compile', '$rootScope', '$sce', '$filter', 'PagerService', 'httpServices', '$http', 'orderByFilter', 'sharedInfo', 'shareFunc', '$templateRequest', '$templateCache', '$timeout', '$translate',
        function ($compile, $rootScope, $sce, $filter, PagerService, httpServices, $http, orderBy, sharedInfo, shareFunc, $templateRequest, $templateCache, $timeout, $translate) {
        'use strict';
        var DEFAULT_ID = 'dtable';
        var DEFAULT_PROPERTYNAME = null;
        var DEFAULT_REVERSE = false;
        var DEFAULT_SAVESTATE = false;
        var DEFAULT_ALLOWSEARCH = true;
        var DEFAULT_MOBILE_SEARCHACTIVE = false;
        var DEFAULT_ALLOWEXPORT = true;
        var DEFAULT_PAGESIZE = 10;
        var DEFAULT_PAGENUM = 1;
        var DEFAULT_TEXTSEARCHING = 'common.searching';
        var DEFAULT_TEXTNORESULTS = 'common.noRecordFound';
        var DEFAULT_DATATEMPLATE = '/panelshare/directives/datatable/table-template.html';
        var DEFAULT_CUSTOMTEMPLATE = 'custom-template.html';
        var DEFAULT_PANELHEIGHT = 300;

        return {
            scope: {
                options: '=',
                dtData: '=',
                dtCols: '=',
                additionalData: '=',
                events: '='
            },
            //templateUrl: '/panelshare/directives/datatable/template.html' + $rootScope.version,
            link: function (scope, element, attrs) {
                //$compile(element.contents())(scope);
                scope.dataTemplate = DEFAULT_DATATEMPLATE + $rootScope.version;

                var elementHtml = element.html().trim();
                if (elementHtml && elementHtml.length > 0) {
                    var customTemplateUrl = (scope.options.id || DEFAULT_ID) + "-" + DEFAULT_CUSTOMTEMPLATE + $rootScope.version;
                    var customTemplateHtml = '<div ng-if="items.length >= 1" ng-repeat="data in items" class="' + scope.options.rowClass + '" ng-class="{' + scope.options.rowNgClass + '}">\n' +
                        elementHtml +
                        '</div>\n' +
                        '<div class="info-box notfoundbx" ng-class="{\'tblldngmd\': loadingMode}" ng-if="items.length === 0">\n' +
                        '   {{ loadingMode ? (\'loading\' | translate) : (\'common.noRecordFound\' | translate)}}\n' +
                        '</div>';
                    $templateCache.put(customTemplateUrl, customTemplateHtml);
                    scope.dataTemplate = customTemplateUrl;
                    element.empty();
                }
                //#region Assign Template
                $templateRequest("/panelshare/directives/datatable/template.html" + $rootScope.version).then(function (html) {
                    var template = angular.element(html);
                    element.append(template);
                    $compile(template)(scope);
                });
                //#endregion

                scope.loadingMode = false;
                var init = function () {
                    scope.options.id = scope.options.id || DEFAULT_ID;
                    scope.options.propertyName = scope.options.propertyName || DEFAULT_PROPERTYNAME;
                    scope.options.additionalSortFieldName = scope.options.additionalSortFieldName || null;
                    scope.options.reverse = shareFunc.getBoolDefaultValue(scope.options.reverse, DEFAULT_REVERSE);
                    scope.options.saveState = shareFunc.getBoolDefaultValue(scope.options.saveState, DEFAULT_SAVESTATE);
                    scope.options.allowFilter = shareFunc.getBoolDefaultValue(scope.options.allowFilter, DEFAULT_ALLOWSEARCH);
                    scope.options.mobileSearchActive = shareFunc.getBoolDefaultValue(scope.options.mobileSearchActive, DEFAULT_MOBILE_SEARCHACTIVE);
                    scope.options.allowExport = shareFunc.getBoolDefaultValue(scope.options.allowExport, DEFAULT_ALLOWEXPORT);
                    scope.options.pageSize = scope.options.pageSize || DEFAULT_PAGESIZE;
                    scope.options.pageNum = scope.options.pageNum || DEFAULT_PAGENUM;
                    scope.options.panelHeight = scope.options.panelHeight || DEFAULT_PANELHEIGHT;
                    scope.options.textSearching = scope.options.textSearching || DEFAULT_TEXTSEARCHING;
                    scope.options.textNoResults = scope.options.textNoResults || DEFAULT_TEXTNORESULTS;
                    scope.options.clearSort = function () {
                        scope.clearSort();
                    }
                    scope.dtData = scope.dtData || [];
                    scope.dtCols = scope.dtCols || [];

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
                var getColCaption = function (col) {
                    //return $compile(col.headerTemplate)(scope)[0].outerHTML;
                    var colCaptionElement = $('#' + scope.options.id).find('[data-colname="' + col.name + '"]');
                    if (colCaptionElement.length === 1)
                        return colCaptionElement.text();
                    return col.name;
                }
                var getExportCols = function () {
                    var exportCols = [];
                    scope.dtCols.forEach(function (col, index) {
                        var allowExport = (col.allowExport !== false) && (col.name && col.name.length > 0);
                        if (allowExport) {
                            var caption = col.title ? $translate.instant(col.title)
                                : col.headerTemplate ? getColCaption(col)
                                    : '';
                            var key = col.name;
                            exportCols.push({caption: caption, key: key});
                        }
                    });
                    return exportCols;
                }
                var getPagingApiParams = function (pageNum){
                    if(pageNum)
                        return {start: (pageNum - 1) * scope.options.pageSize, count: scope.options.pageSize};
                    return {};
                }
                var getFilterOprator = function (col){
                    switch (col.dataType){
                        case sharedInfo.dataType.int:
                        case sharedInfo.dataType.bool:
                        case sharedInfo.dataType.list:
                        case sharedInfo.dataType.date:
                            return ' = ';
                        case sharedInfo.dataType.string:
                            return ' like ';
                        default:
                            return ' = ';
                    }
                }

                var getFilterText = function (col){
                    let filterText = (typeof col.filterText !== 'undefined' ? col.filterText : '').toString().trim();
                    if(col.unFormatFilterText)
                        filterText = col.unFormatFilterText(filterText);
                    return filterText;
                }

                var getClientFilterParams = function () {
                    var filterParam = {};
                    for(var i = 0; i < (scope.dtCols || []).length; i++){
                        var col = scope.dtCols[i];
                        var filterText = getFilterText(col);
                        if(col.filterable && filterText.length > 0 && filterText !== 'undefined'){
                            filterParam[col.keyName] = filterText;
                        }
                    }
                    return filterParam;
                }

                var getFilterParams = function () {
                    var filterParamStr = '';
                    for(var i = 0; i < (scope.dtCols || []).length; i++){
                        var col = scope.dtCols[i];
                        var filterText = getFilterText(col);
                        if(col.filterable && !col.isGlobalFilterParam && filterText.length > 0 && filterText !== 'undefined'){
                            if(filterParamStr.length > 0) filterParamStr += ',';
                            filterParamStr += col.keyName + getFilterOprator(col) + filterText;
                        }
                    }
                    return filterParamStr;
                }

                var getGlobalFilterParams = function () {
                    var globalFilterParam = [];
                    for(var i = 0; i < (scope.dtCols || []).length; i++){
                        var col = scope.dtCols[i];
                        var filterText = (col.filterText || '');
                        if(col.filterable && col.isGlobalFilterParam && filterText.length > 0 && filterText !== 'undefined'){
                            globalFilterParam[col.keyName] = col.filterText;
                        }
                    }
                    return globalFilterParam;
                }

                var getApiSortParam = function (){
                    return scope.options.propertyName ? (scope.options.propertyName + '=' + (scope.options.reverse ? 'desc' : 'asc')) : null;
                }
                var getApiParams = function (pageNum){
                    const apiParams = {
                        resultCount: !!pageNum,
                        sortOptions: getApiSortParam()
                    };
                    let key;

                    var pagingApiParams = getPagingApiParams(pageNum);
                    for (key in pagingApiParams) {
                        if(pagingApiParams.hasOwnProperty(key)){
                            apiParams[key] = pagingApiParams[key];
                        }
                    }

                    for (key in scope.options.apiParams) {
                        var filterText = (scope.options.apiParams[key] || '');
                        if(scope.options.apiParams.hasOwnProperty(key) && filterText !== 'undefined' && typeof filterText !== 'undefined' && (typeof filterText !=='string' || filterText.length > 0)){
                            apiParams[key] = scope.options.apiParams[key];
                        }
                    }

                    var filterParamStr = getFilterParams();
                    if(filterParamStr.length > 0)
                        apiParams['prm'] = filterParamStr;

                    var globalFilterParam = getGlobalFilterParams();
                    for (key in globalFilterParam) {
                        apiParams[key] = globalFilterParam[key];
                    }

                    return apiParams;
                }
                //#endregion

                //#region Events
                scope.$watch('dtData', function (newval, oldVal) {
                    //init();
                    if(!scope.serverSideMode) {
                        scope.dummyItems = newval || [];
                        scope.dummyItems = $filter('filter')(scope.dummyItems, scope.bzquery);//added for keep search data after change dtData
                        if ((oldVal || []).length === 0) {
                            scope.restoreState();
                        } else {
                            /*scope.loadingMode = false;
                            console.log('loadingMode = false;');*/
                        }
                        scope.setPage(scope.options.pageNum);
                    }
                });

                scope.events.setPage = function (page) {
                    scope.setPage(page);
                }

                scope.events.export = function () {
                    var args = {
                        headers: getExportCols(),
                        fileName: 'ExportExcel'
                    };
                    if(scope.serverSideMode) {
                        NProgress.start();
                        $.blockUI();
                        getHttpService().then(function (response) {
                            var data = response.data;
                            args.data = data.returnObject.result;
                            shareFunc.downloadXLS(args);

                        }, function (response) {
                            httpServices.handleError(response.data, response.status, response.headers, response.config);
                        })
                            .finally(function () {
                                NProgress.done();
                                $.unblockUI();
                            });
                    }
                    else{
                        var exportData = $filter('filter')(scope.dummyItems, scope.bzquery);
                        args.data = orderBy(exportData, getSortPropertyName(), scope.options.reverse);
                        shareFunc.downloadXLS(args);
                    }
                    //shareFunc.downloadXLSAsHtmlTable(args);
                }

                scope.events.filter = function () {
                    scope.events.dataBind();
                }

                scope.events.dataBind = function (){
                    scope.setPage(1);
                }

                scope.onRowClick = function (item){
                    scope.events.onRowClick && scope.events.onRowClick(item);
                }
                scope.onRowDblClick = function (item){
                    scope.events.onRowDblClick && scope.events.onRowDblClick(item);
                }
                //#endregion

                //#region state
                scope.storeState = function () {
                    if (scope.options.saveState) {
                        var opt = {}
                        opt.propertyName = scope.options.propertyName;
                        opt.reverse = scope.options.reverse || DEFAULT_REVERSE;
                        opt.pageSize = scope.options.pageSize || DEFAULT_PAGESIZE;
                        opt.pageNum = scope.options.pageNum;
                        sharedInfo.setState(scope.options.id, opt);
                    }
                }
                scope.restoreState = function () {
                    if (scope.options.saveState) {
                        var opt = sharedInfo.getState(scope.options.id);
                        if (opt) {
                            scope.options.propertyName = opt.propertyName;
                            scope.options.reverse = opt.reverse || DEFAULT_REVERSE;
                            scope.options.pageSize = opt.pageSize || DEFAULT_PAGESIZE;
                            scope.options.pageNum = opt.pageNum;
                        }
                    }
                }
                //#endregion

                //#region Sort
                var getSortPropertyName = function () {
                    var sortParams = [];
                    if (scope.options.propertyName) {
                        sortParams.push(scope.options.propertyName);
                        if (scope.options.additionalSortFieldName)
                            sortParams.push(scope.options.additionalSortFieldName);
                    }
                    return sortParams;
                }

                scope.clearSort = function () {
                    scope.options.reverse = false;
                    scope.options.propertyName = null;
                    scope.setPage(1);
                }
                //#endregion

                //#region Search
                var applyFilter = function () {
                    scope.dummyItems = scope.dtData;
                    //scope.dummyItems = $filter('filter')(scope.dummyItems, scope.bzquery);
                    var clientFilterParams = getClientFilterParams();
                    if(!jQuery.isEmptyObject(clientFilterParams))
                        scope.dummyItems = $filter('filter')(scope.dummyItems, clientFilterParams);
                }
                scope.search = function () {
                    scope.setPage(1);
                }
                //#endregion

                //#region Pager
                scope.pageSizeList = [
                    5,
                    10,
                    15,
                    20,
                    25,
                    30,
                    50
                ];

                scope.pageSizeChange = function () {
                    scope.setPage(1);
                }

                var getHttpService = function (page){
                    return $http({
                        method: "GET",
                        url: scope.options.apiUrl,
                        params: getApiParams(page),
                        cache: false,
                        headers: httpServices.getHeaders()
                    });
                }

                scope.setPage = function (page) {
                    if(scope.serverSideMode) {
                        /*NProgress.start();
                        $.blockUI();*/
                        scope.loadingMode = true;
                        getHttpService(page).then(function (response) {
                            var data = response.data;
                            var dataList = data.returnObject.result;
                            var totalCount = data.returnObject.resultCount;
                            var pageCount = totalCount > 0 ? Math.ceil(totalCount / scope.options.pageSize) : 0;

                            scope.options.pageNum = page > pageCount && pageCount > 0 ? pageCount : page;
                            scope.pager = PagerService.GetPager(totalCount, scope.options.pageNum, scope.options.pageSize);

                            scope.dummyItems = dataList;
                            scope.items = dataList;

                        }, function (response) {
                            httpServices.handleError(response.data, response.status, response.headers, response.config);
                        })
                        .finally(function () {
                            /*NProgress.done();
                            $.unblockUI();*/
                            scope.loadingMode = false;
                        });

                    } else {
                        applyFilter();
                        var pageCount = scope.dummyItems.length > 0 ? Math.ceil(scope.dummyItems.length / scope.options.pageSize) : 0;
                        scope.options.pageNum = page > pageCount && pageCount > 0 ? pageCount : page;
                        //if (page < 1 || page > $scope.pager.totalPages) {
                        //    return;
                        //}
                        // get pager object from service
                        scope.pager = PagerService.GetPager(scope.dummyItems.length, scope.options.pageNum, scope.options.pageSize);

                        scope.dummyItems = orderBy(scope.dummyItems, getSortPropertyName(), scope.options.reverse);

                        scope.items = scope.dummyItems.slice(scope.pager.startIndex, scope.pager.endIndex + 1);
                    }
                    scope.storeState();
                }
                //#endregion
                $timeout(init);
            }
            /*compile: function (tElement) {
                console.log('adsdsa');
            }*/
        };
    }]);

