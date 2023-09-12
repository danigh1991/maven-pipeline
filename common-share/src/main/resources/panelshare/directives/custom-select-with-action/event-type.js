angular.module('emixApp.directives')
    .directive('ngEventType', ['$compile', '$rootScope', 'httpServices', '$timeout', 'shareFunc', 'sharedInfo', '$translate',
        function ($compile, $rootScope, httpServices, $timeout, shareFunc, sharedInfo, $translate) {
            'use strict';

            var DEFAULT_SAVESTATE = true;
            var DEFAULT_ALLOWNUL = true;
            var DEFAULT_DISABLED = false;
            var DEFAULT_SHOW_BUTTON = true;
            var DEFAULT_STORESTATENAME = 'evt_storeStateName';
            var DEFAULT_DROPDOWN_CLASS = 'input-group margin-bottom-sm col-md-4 col-xs-12';
            var DEFAULT_BUTTON_LIST_CLASS = 'col-md-4 col-xs-12';

            return {
                scope: {
                    options: '=',
                    events: '='
                },
                templateUrl: '/panelshare/directives/custom-select-with-action/event-type.html' + $rootScope.version,
                link: function (scope, element, attrs) {

                    var init = function() {
                        scope.options.allowNull = shareFunc.getBoolDefaultValue(scope.options.allowNull, DEFAULT_ALLOWNUL);
                        scope.options.saveState = shareFunc.getBoolDefaultValue(scope.options.saveState, DEFAULT_SAVESTATE);
                        /*scope.options.storeStateName = scope.options.saveState ? scope.options.storeStateName || DEFAULT_STORESTATENAME : null;*/
                        scope.options.dropDownClass = scope.options.dropDownClass || DEFAULT_DROPDOWN_CLASS;
                        scope.options.buttonListClass = scope.options.buttonListClass || DEFAULT_BUTTON_LIST_CLASS;
                        scope.options.disabled = shareFunc.getBoolDefaultValue(scope.options.disabled, DEFAULT_DISABLED);
                        scope.options.showButton = shareFunc.getBoolDefaultValue(scope.options.showButton, DEFAULT_SHOW_BUTTON);
                        scope.eventTypes = [];
                    }
                    init();

                    //#region Utils

                    //#endregion

                    //#region Events
                    scope.getAllEventTypesInfo = function () {
                        NProgress.start();
                        $.blockUI();
                        httpServices.getAllEventTypesInfo()
                            .then(function (response) {
                                var data = response.data;
                                scope.eventTypes = data.returnObject;
                                if(scope.eventTypes.length > 1)
                                    scope.eventTypes.unshift({"id":0,"caption":'common.all'});
                                else
                                    scope.selectedEventType = scope.eventTypes[0];

                                scope.events.afterLoadData && scope.events.afterLoadData(scope.eventTypes);
                            }, function (response) {
                                httpServices.handleError(response.data, response.status, response.headers, response.config);
                            })
                            .finally(function () {
                                NProgress.done();
                                $.unblockUI();
                            });
                    }
                    scope.getAllEventTypesInfo();
                    scope.eventTypesOptions = {
                        onSelect: function (item) {
                        }
                    };
                    scope.addEventType = function () {
                        var passObject = {};
                        shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/event/add-type.html', 'addeventtype_index', null,
                            function (result) {
                                if (result)
                                    scope.getAllEventTypesInfo();
                            }, function () {
                                //dismiss Dialog
                                if (sharedInfo.getNeedRefresh())
                                    scope.getAllEventTypesInfo();
                            });
                    }
                    scope.editEventType = function () {
                        var passObject = {
                            eventTypeId: scope.selectedEventType.id,
                            eventTypeTitle: scope.selectedEventType.caption
                        };
                        shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/event/add-type.html', 'addeventtype_index', null,
                            function (result) {
                                if (result)
                                    scope.getAllEventTypesInfo();
                            }, function () {
                                //dismiss Dialog
                                if (sharedInfo.getNeedRefresh())
                                    scope.getAllEventTypesInfo();
                            });
                    }
                    scope.deleteEventType = function () {
                        NProgress.start();
                        $.blockUI();

                        httpServices.deleteEventType(scope.selectedEventType.id)
                            .then(function (response) {
                                var data = response.data;
                                toastr.success($translate.instant('common.successfullyDeletedMessage'));
                                scope.getAllEventTypesInfo();
                                scope.selectedEventType = scope.eventTypes.length > 0 ? scope.eventTypes[0] : null;
                            }, function (response) {
                                httpServices.handleError(response.data, response.status, response.headers, response.config);
                            })
                            .finally(function () {
                                NProgress.done();
                                $.unblockUI();
                            });
                    }

                    scope.$watch('selectedEventType', function (newval, oldVal) {
                        if((oldVal ? oldVal.id : -1) != (newval ? newval.id : -1))
                            scope.events.onSelectedChange && scope.events.onSelectedChange(newval);

                    });
                    //#endregion
                    //#region Events
                    scope.events.getSelectedEventType = function () {
                        return scope.selectedEventType;
                    }
                    scope.events.setSelectedEventType = function (selectedPageGroupId) {
                        var find = false;
                        angular.forEach(scope.eventTypes, function (eventType) {
                            if(!find && selectedPageGroupId == eventType.id) {
                                scope.selectedEventType = eventType;
                                find = true;
                            }
                        })
                    }
                    //#endregion
                }
            };
        }]);
