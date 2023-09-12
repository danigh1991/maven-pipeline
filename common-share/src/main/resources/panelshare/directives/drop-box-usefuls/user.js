angular.module('emixApp.directives')
    .directive('dbxUser', ['sharedInfo', '$translate',
        function (sharedInfo, $translate) {
            'use strict';
            return {
                restrict: 'E',
                /*replace: false,
                transclude: true,*/
                scope: {
                    selectedValue: '=',
                    options: '=',
                    events: '=?'
                },
                //templateUrl: '/panelshare/directives/drop-box-usefuls/user.html' + $rootScope.version,
                template: function (element, attrs) {
                    return '<div class="row col-xs-12" ng-model="selectedValue" ng-drop-box options="dbxUserOptions" ' +
                        'additional-data="{}" events="events" cols="dbxUserCols"></div>';
                },
                link: function (scope, element, attrs) {

                    //#region Properties
                    scope.dbxUserOptions = {
                        id: scope.options.id || 'dbxUser',
                        dataKeyField: scope.options.dataKeyField || 'id',
                        dataCodeField: 'username',
                        dataTextField: 'firstName, lastName',
                        label: 'common.user',
                        additionalSortFieldName: 'id',
                        apiUrl: Emix.Api.Users.getUsers,
                        required: scope.options.required || false,
                        disabled: scope.options.disabled || false,
                        readOnly: scope.options.readOnly || false,
                        apiParams: {}
                    }
                    scope.dbxUserCols = [
                        {
                            name: 'id',
                            title: 'common.id',
                            allowSorting: true,
                            cellClass: 'text-center',
                            headerClass: 'text-center col-md-1',
                            dataType: sharedInfo.dataType.int
                        },
                        {
                            name: 'username',
                            title: 'common.userName',
                            allowSorting: true,
                            cellClass: 'text-center',
                            headerClass: 'text-center col-md-3'
                        },
                        {
                            name: 'firstName',
                            title: 'common.name',
                            allowSorting: true,
                            cellClass: 'text-center',
                            headerClass: 'text-center col-md-4'
                        },
                        {
                            name: 'lastName',
                            title: 'common.family',
                            allowSorting: true,
                            cellClass: 'text-center',
                            headerClass: 'text-center col-md-4'
                        },
                    ];
                    //#endregion

                    /*#region Utilities*/
                    /*#endregion*/

                    /*#region event*/
                    /*#endregion*/

                }
            };
        }]);
