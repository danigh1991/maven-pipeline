angular.module('emixApp.directives')
    .directive('ngPageGroup', ['$compile', '$rootScope', 'httpServices', '$timeout', 'shareFunc', 'sharedInfo', '$translate',
        function ($compile, $rootScope, httpServices, $timeout, shareFunc, sharedInfo, $translate) {
            'use strict';

            var DEFAULT_SAVESTATE = true;
            var DEFAULT_ALLOWNUL = true;
            var DEFAULT_DISABLED = false;
            var DEFAULT_SHOW_BUTTON = true;
            var DEFAULT_STORESTATENAME = 'pggrp_storeStateName';
            var DEFAULT_DROPDOWN_CLASS = 'input-group margin-bottom-sm col-md-4 col-xs-12';
            var DEFAULT_BUTTON_LIST_CLASS = 'col-md-4 col-xs-12';

            return {
                scope: {
                    options: '=',
                    events: '='
                },
                templateUrl: '/panelshare/directives/custom-select-with-action/page-group.html' + $rootScope.version,
                link: function (scope, element, attrs) {

                    var init = function() {
                        scope.options.allowNull = shareFunc.getBoolDefaultValue(scope.options.allowNull, DEFAULT_ALLOWNUL);
                        scope.options.saveState = shareFunc.getBoolDefaultValue(scope.options.saveState, DEFAULT_SAVESTATE);
                        /*scope.options.storeStateName = scope.options.saveState ? scope.options.storeStateName || DEFAULT_STORESTATENAME : null;*/
                        scope.options.dropDownClass = scope.options.dropDownClass || DEFAULT_DROPDOWN_CLASS;
                        scope.options.buttonListClass = scope.options.buttonListClass || DEFAULT_BUTTON_LIST_CLASS;
                        scope.options.disabled = shareFunc.getBoolDefaultValue(scope.options.disabled, DEFAULT_DISABLED);
                        scope.options.showButton = shareFunc.getBoolDefaultValue(scope.options.showButton, DEFAULT_SHOW_BUTTON);
                        scope.pageGroups = [];
                    }
                    init();

                    //#region Utils

                    //#endregion

                    //#region PageGroups
                    scope.getPageGroups = function () {
                        NProgress.start();
                        $.blockUI();
                        httpServices.getPageGroups()
                            .then(function (response) {
                                var data = response.data;
                                scope.pageGroups = data.returnObject;
                                if(scope.pageGroups.length > 1)
                                    scope.pageGroups.unshift({"id":0,"title":$translate.instant('common.all')});
                                else
                                    scope.selectedPageGroup = scope.pageGroups[0];

                                scope.events.afterLoadData && scope.events.afterLoadData(scope.pageGroups);
                            }, function (response) {
                                httpServices.handleError(response.data, response.status, response.headers, response.config);
                            })
                            .finally(function () {
                                NProgress.done();
                                $.unblockUI();
                            });
                    }
                    scope.getPageGroups();
                    scope.pageGroupsOptions = {
                        onSelect: function (item) {
                        }
                    };
                    scope.addPageGroup = function () {
                        var passObject = {};
                        shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/common/page/add-group.html', 'addpagegroup_index', null,
                            function (result) {
                                if (result)
                                    scope.getPageGroups();
                            }, function () {
                                //dismiss Dialog
                                if (sharedInfo.getNeedRefresh())
                                    scope.getPageGroups();
                            });
                    }
                    scope.editPageGroup = function () {
                        var passObject = {
                            pageGroupId: scope.selectedPageGroup.id,
                            pageGroupTitle: scope.selectedPageGroup.title
                        };
                        shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/common/page/add-group.html', 'addpagegroup_index', null,
                            function (result) {
                                if (result)
                                    scope.getPageGroups();
                            }, function () {
                                //dismiss Dialog
                                if (sharedInfo.getNeedRefresh())
                                    scope.getPageGroups();
                            });
                    }
                    scope.deletePageGroup = function () {
                        NProgress.start();
                        $.blockUI();

                        httpServices.deletePageGroup(scope.selectedPageGroup.id)
                            .then(function (response) {
                                var data = response.data;
                                toastr.success($translate.instant('common.successfullyDeletedMessage'));
                                scope.getPageGroups();
                                scope.selectedPageGroup = scope.pageGroups.length > 0 ? scope.pageGroups[0] : null;
                            }, function (response) {
                                httpServices.handleError(response.data, response.status, response.headers, response.config);
                            })
                            .finally(function () {
                                NProgress.done();
                                $.unblockUI();
                            });
                    }

                    scope.$watch('selectedPageGroup', function (newval, oldVal) {
                        if((oldVal ? oldVal.id : -1) != (newval ? newval.id : -1))
                            scope.events.onSelectedChange && scope.events.onSelectedChange(newval);

                    });
                    //#endregion
                    //#region Events
                    scope.events.getSelectedPageGroup = function () {
                        return scope.selectedPageGroup;
                    }
                    scope.events.setSelectedPageGroup = function (selectedPageGroupId) {
                        var find = false;
                        angular.forEach(scope.pageGroups, function (pageGroup) {
                            if(!find && selectedPageGroupId == pageGroup.id) {
                                scope.selectedPageGroup = pageGroup;
                                find = true;
                            }
                        })
                    }
                    //#endregion
                }
            };
        }]);
