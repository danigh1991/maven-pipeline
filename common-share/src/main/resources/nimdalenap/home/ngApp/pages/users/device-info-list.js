angular.module('emixApp.controllers').controller('deviceinfolist_index',
    ['$scope', 'httpServices', 'passObject', '$modalInstance', '$rootScope', 'shareFunc', 'sharedInfo', '$translate',
        function ($scope, httpServices, passObject, $modalInstance, $rootScope, shareFunc, sharedInfo, $translate) {
            'use strict';
            $scope.showError = false;
            $scope.showBreadcrump = !passObject;

            function getAllDeviceByUserId() {

                NProgress.start();
                $.blockUI();

                httpServices.getAllDeviceByUserId(passObject.userId)
                    .then(function (response) {
                        var data = response.data;
                        $scope.dtData = data.returnObject;
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            getAllDeviceByUserId();

            $scope.removeDevice = function (deviceMetadataId) {
                NProgress.start();
                $.blockUI();

                httpServices.removeDevice(deviceMetadataId)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullyDeletedMessage'));
                        getAllDeviceByUserId();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            //#region Role Table
            $scope.dtOptions = {
                id: 'dtUserDeviceList',
                saveState: false,
                allowFilter: true,
                additionalSortFieldName: 'id'
            }
            $scope.dtCols = [
                {
                    name: 'id',
                    title: 'common.id',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center'
                },
                {
                    name: 'deviceDetail',
                    title: 'common.details',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-3'
                },
                {
                    name: 'ip',
                    title: 'common.ip',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2'
                },
                {
                    name: 'location',
                    title: 'common.location',
                    allowSorting: true,
                    headerClass: 'text-center col-md-2',
                    cellClass: 'text-center',
                },
                {
                    name: 'lastLoggedDate',
                    title: 'common.time',
                    allowSorting: true,
                    headerClass: 'text-center col-md-2',
                    cellClass: 'text-center',
                },
                {
                    name: '',
                    title: '',
                    allowSorting: false,
                    headerClass: 'text-center col-md-2',
                    cellClass: 'tdcommand',
                    cellTemplate: '<button class="btn btn-danger" deletebutton \n' +
                        '               ng-click="events.delete(data);" button-type="delete">' +
                        '          </button>'
                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {
            }
            $scope.dtEvents = {
                delete: function (deviceMetadata) {
                    $scope.removeDevice(deviceMetadata.id);
                }
            }
            //#endregion

        }]);
