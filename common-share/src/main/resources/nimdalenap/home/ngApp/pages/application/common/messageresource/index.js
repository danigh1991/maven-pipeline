angular.module('emixApp.controllers').controller('messageresource_index',
    ['$scope', 'httpServices', 'sharedInfo', '$timeout', '$translate',
        function ($scope, httpServices, sharedInfo, $timeout, $translate) {
            'use strict';

            $scope.messageResourceTypeList = sharedInfo.getMessageResourceTypeList();
            $scope.selecteMessageResourceTypeId = $scope.messageResourceTypeList[0].id;
            $scope.messageResourceTypeChange = function () {
                $scope.dtOptions.clearSort && $scope.dtOptions.clearSort();
                getMessageResources();
            }

            function getMessageResources() {
                NProgress.start();
                $.blockUI();
                httpServices.getMessageResources($scope.selecteMessageResourceTypeId)
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
            $timeout(function() {
                getMessageResources();
            }, 100);

            var editMessageResource = function (messageResource) {

                if(messageResource.content.length == 0){
                    toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('common.text')}));
                    return;
                }

                var pmessageResource = {
                    key: messageResource.key,
                    content: messageResource.content
                };

                NProgress.start();
                $.blockUI();

                httpServices.editMessageResource(pmessageResource)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                        getMessageResources();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            //#region Product Table
            $scope.dtOptions = {
                id: 'dtMessageResource',
                saveState: true,
                allowFilter: true,
                additionalSortFieldName: 'id'
            }
            $scope.dtCols = [
                {
                    name: 'key',
                    title: 'common.id',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-5'
                },
                {
                    name: 'content',
                    title: 'common.text',
                    allowSorting: true,
                    headerClass: 'text-center col-md-6',
                    cellClass: 'text-center',
                    cellTemplate: '<input type="text" class="form-control" placeholder=" "\n' +
                        '                    autofocus data-ng-model="data.content"/>'
                },
                {
                    name: '',
                    title: '',
                    allowSorting: false,
                    headerClass: 'text-center col-md-2',
                    cellClass: 'tdcommand',
                    cellTemplate:
                        '<button class="btn btn-success" button-type="ok" ng-click="events.editMessageResource(data)">' +
                        '</button>'
                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {
            }
            $scope.dtEvents = {
                editMessageResource: function (messageResource) {
                    editMessageResource(messageResource);
                }
            }
            //#endregion

        }]);
