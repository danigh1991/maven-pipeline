angular.module('emixApp.directives').directive('ngDropBox',
    ['$compile', '$rootScope', '$sce', '$filter', 'PagerService', 'httpServices', '$http', 'orderByFilter', 'sharedInfo', 'shareFunc', '$templateRequest', '$templateCache', '$translate', '$timeout',
        function ($compile, $rootScope, $sce, $filter, PagerService, httpServices, $http, orderBy, sharedInfo, shareFunc, $templateRequest, $templateCache, $translate, $timeout) {
        'use strict';
        var DEFAULT_ID = 'srdropdown';
        var DEFAULT_DATAKEYFIELD = 'id';
        var DEFAULT_PROPERTYNAME = null;
        var DEFAULT_REVERSE = false;
        var DEFAULT_SAVESTATE = false;
        var DEFAULT_ALLOWSEARCH = true;
        var DEFAULT_ALLOWEXPORT = false;
        var DEFAULT_REQUIRED = false;
        var DEFAULT_DISABLED = false;
        var DEFAULT_READONLY = false;
        var DEFAULT_PAGESIZE = 5;
        var DEFAULT_PAGENUM = 1;
        var DEFAULT_TEXTSEARCHING = 'common.searching';
        var DEFAULT_TEXTNORESULTS = 'common.noRecordFound';

        return {
            require: 'ngModel',//selectedId or selectedValue
            /*replace: true,
            transclude: true,*/
            scope: {
                ngModel: '=',
                options: '=',
                //dbxData: '=',
                cols: '=',
                additionalData: '=',
                events: '='
            },
            link: function (scope, element, attrs) {
                //$compile(element.contents())(scope);
                scope.elementHtml = element.html().trim();

                //#region Assign Template
                $templateRequest("/panelshare/directives/drop-box/template.html" + $rootScope.version).then(function (html) {
                    var template = angular.element(html);
                    element.prepend(template);
                    $compile(template)(scope);
                });
                //#endregion

                var init = function () {
                    scope.options.dataKeyField = scope.options.dataKeyField || DEFAULT_DATAKEYFIELD;
                    scope.options.dataCodeField = scope.options.dataCodeField || scope.options.dataKeyField;
                    scope.options.dataTextField = scope.options.dataTextField || '';
                    scope.options.label = scope.options.label || '';
                    scope.options.modalSize = scope.options.modalSize || 'lg';

                    scope.options.id = scope.options.id || DEFAULT_ID;
                    scope.options.propertyName = scope.options.propertyName || DEFAULT_PROPERTYNAME;//Sort
                    scope.options.additionalSortFieldName = scope.options.additionalSortFieldName || null;
                    scope.options.reverse = shareFunc.getBoolDefaultValue(scope.options.reverse, DEFAULT_REVERSE);

                    scope.options.saveState = shareFunc.getBoolDefaultValue(scope.options.saveState, DEFAULT_SAVESTATE);
                    scope.options.saveStateKey = scope.options.saveStateKey || '';

                    scope.options.allowFilter = shareFunc.getBoolDefaultValue(scope.options.allowFilter, DEFAULT_ALLOWSEARCH);
                    scope.options.allowExport = shareFunc.getBoolDefaultValue(scope.options.allowExport, DEFAULT_ALLOWEXPORT);
                    scope.options.pageSize = scope.options.pageSize || DEFAULT_PAGESIZE;
                    scope.options.pageNum = scope.options.pageNum || DEFAULT_PAGENUM;
                    scope.options.textSearching = scope.options.textSearching || DEFAULT_TEXTSEARCHING;
                    scope.options.textNoResults = scope.options.textNoResults || DEFAULT_TEXTNORESULTS;
                    scope.cols = scope.cols || [];

                    scope.options.required = scope.options.required || DEFAULT_REQUIRED;
                    scope.options.disabled = scope.options.disabled || DEFAULT_DISABLED;
                    scope.options.readOnly = scope.options.readOnly || DEFAULT_READONLY;
                    //#region Server Side Mode
                    scope.options.apiUrl = scope.options.apiUrl || '';
                    scope.options.apiParams = scope.options.apiParams || {};
                    //#endregion
                    $timeout(function () {
                        watchFunction([scope.ngModel, ''], [null, '']);
                    });
                }

                //#region Utils
                var getDataTextField = function (result){
                    var dataTextFieldList = scope.options.dataTextField.split(',');
                    var dataTextList = [];
                    for (var i = 0; i < dataTextFieldList.length; i++){
                        dataTextList.push(result[dataTextFieldList[i].trim()] || '');
                    }
                    return dataTextList.join(' ');
                }
                var needCallApi = true;
                var setNgModel = function (result){
                    if(result !== null) {
                        scope.ngModel = result[scope.options.dataKeyField];
                        scope.ngTextModel = result[scope.options.dataCodeField] + ' - ' + getDataTextField(result);
                        if(angular.isFunction(scope.events.onSelected)) $timeout(function (){scope.events.onSelected(result)}, 0);
                    } else {
                        scope.eraseResult();
                        scope.ngModel = null;
                        scope.ngTextModel = '';
                    }
                    needCallApi = false;
                }

                var getDataCodeValue = function () {
                    return (scope.ngTextModel || '').split(' ')[0];
                }

                var getApiParams = function (){
                    const apiParams = {
                        start: 0,
                        count:2,//For Raise Error On More than one result
                        resultCount: false
                    };
                    var dataCodeValue = getDataCodeValue();
                    if(dataCodeValue.length > 0)
                        apiParams[scope.options.dataCodeField] = getDataCodeValue();
                    else
                        apiParams[scope.options.dataKeyField] = scope.ngModel;

                    return apiParams;
                }

                var getHttpService = function (){
                    return $http({
                        method: "GET",
                        url: scope.options.apiUrl,
                        params: getApiParams(),
                        cache: false,
                        headers: httpServices.getHeaders()
                    });
                }
                var hasValue = function (value){
                    return (typeof value).toString() !== 'undefined' && value !== null && value.toString().length > 0;
                }
                var watchFunction = function (newval, oldval) {
                    var oldNgModel = oldval[0];
                    var oldNgTextModel = oldval[1];

                    var newNgModel = newval[0];
                    var newNgTextModel = newval[1];

                    if( 
                        (!(hasValue(oldNgModel) && hasValue(newNgModel) && newNgModel === oldNgModel && newNgTextModel !== oldNgTextModel) || needCallApi === false) && //after bind success and change text
                        // value set programmatically
                        ((!hasValue(newNgModel) && !hasValue(newNgTextModel)) || //after erase success
                            (hasValue(newNgModel) && hasValue(newNgTextModel)) || //after bind success
                            (newNgModel === oldNgModel && newNgTextModel === oldNgTextModel))//without change
                    ) {
                        needCallApi = true;
                        return;
                    }

                    NProgress.start();
                    $.blockUI();
                    getHttpService().then(function (response) {
                        var data = response.data;
                        var dataList = data.returnObject.result;
                        var totalCount = (dataList || []).length;
                        if(totalCount === 1)
                            setNgModel(dataList[0]);
                        else
                            setNgModel(null);

                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                        .finally(function () {
                            NProgress.done();
                            $.unblockUI();
                        });
                }
                //#endregion

                //#region Events
                scope.inputClicked= function (){
                    if(scope.options.readOnly){
                      scope.openSearchModal();
                    }
                }

                scope.openSearchModal = function (){
                    if(scope.options.disabled) return;
                    var passObject = {};
                    passObject.options = scope.options;
                    passObject.dtCols = scope.cols;
                    passObject.additionalData = scope.additionalData;
                    passObject.events = scope.events;
                    shareFunc.openModal(passObject, '/panelshare/directives/drop-box/modal/index.html', 'dropboxgridmodal_index', scope.options.modalSize,
                        function (result) {
                            setNgModel(result);
                        }, function () {

                        });
                }
                scope.eraseResult = function (){
                    if(scope.options.disabled) return;
                    scope.ngModel = null;
                    scope.ngTextModel = '';
                    if(angular.isFunction(scope.events.onSelected)) $timeout(function (){scope.events.onSelected(null)}, 0);
                }
                scope.$watch('[ngModel, ngTextModel]', watchFunction);

                scope.events.erase = function (){
                    scope.eraseResult();
                }
                //#endregion

                //#region state
                //#endregion

                //#region Sort
                //#endregion

                //#region Search
                //#endregion

                //#region Pager
                //#endregion

                $timeout(init);
            }
        };
    }]);

