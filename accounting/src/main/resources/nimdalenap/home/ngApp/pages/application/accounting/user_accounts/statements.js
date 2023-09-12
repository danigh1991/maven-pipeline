angular.module('emixApp.controllers').controller('accountstatements_index',
    ['$scope', 'httpServices', 'passObject', '$rootScope', '$filter', 'shareFunc', 'sharedInfo', '$modalInstance', '$translate', '$modal',
        function ($scope, httpServices, passObject, $rootScope, $filter, shareFunc, sharedInfo, $modalInstance, $translate, $modal) {
            'use strict';

            $scope.showBreadcrump = !passObject;

            $scope.params = {};


            function getAccountStatementWrappersForMerchant() {
                NProgress.start();
                $.blockUI();
                httpServices.getAccountStatementWrappersForMerchant(passObject.accountId, passObject.userCreditId || 0, 0, 1000)
                    .then(function (response) {
                        var data = response.data;
                        $scope.dtData = data.returnObject.result;
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            function getAccountInfo() {
                NProgress.start();
                $.blockUI();
                httpServices.getAccountWrapperInfo(passObject.accountId, 0)
                    .then(function (response) {
                        var data = response.data;
                        $scope.params.accounts = [data.returnObject];
                        getAccountStatementWrappersForMerchant();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {
                    });
            }
            getAccountInfo();

            function getOrderAsHtml(orderId, callback) {
                NProgress.start();
                $.blockUI();
                httpServices.getOrderAsHtml(orderId)
                    .then(function (response) {
                        var data = response.data;
                        callback(data.returnObject);
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }
            $scope.detailOrder = function (orderId) {
                var passObject;
                getOrderAsHtml(orderId, function (returnObject) {

                    passObject = {
                        title: 'فاکتور',
                        message: returnObject.result,
                        displayConfirmButton: false,
                        confirmText: $translate.instant('common.print'),
                        actionToCall: function () {
                            if(typeof bindPrintEvent !== "undefined") bindPrintEvent();
                        }
                    }
                    var dialogInst = $modal.open({
                        templateUrl: '/nimdalenap/home/ngApp/pages/modal/confirm.html',
                        controller: 'confirmmodal_index',
                        size: 'x-lg',
                        backdrop: 'static',
                        keyboard: false,//true
                        resolve: {
                            passObject: function () {
                                return passObject;
                            }
                        }
                    });
                    setTimeout(function() {
                        $('#btnChangeAddressShipment,#btnCancleShipment').hide();
                    },100);
                    setTimeout(function() {
                        $('#btnChangeAddressShipment,#btnCancleShipment').hide();
                    },300);
                    dialogInst.result.then(function (result) {
                        if (result) {
                            bindPrintEvent && bindPrintEvent();
                            $('#btnprint').click();
                        }

                    }, function () {
                        //$log.info('Modal dismissed at: ' + new Date());
                    });
                });
            }

            //#region OperationType Table
            $scope.dtOptions = {
                id: 'dtAccountStatements',
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
                    name: 'operationTypeCaption',
                    //keyName: 'operationTypeId',
                    title: 'common.type',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    /*dataType: sharedInfo.dataType.list,
                    filterList: Emix.Api.Application.OperationType.getAllOperationTypeWrappers,
                    filterListKey: 'id',
                    filterListValue: 'caption',*/
                },
                {
                    name: 'debit',
                    title: 'accounting.statement.debit',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    cellTemplate: '<span>{{data.debit | currency:"":additionalData.shopFractionSize}} <small>{{additionalData.shopCurrency}}</small></span>'
                },
                {
                    name: 'credit',
                    title: 'accounting.statement.credit',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    cellTemplate: '<span>{{data.credit | currency:"":additionalData.shopFractionSize}} <small>{{additionalData.shopCurrency}}</small></span>'
                },
                {
                    name: 'createDate',
                    title: 'common.createDate',
                    allowSorting: true,
                    filterable: false,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    //dataType: sharedInfo.dataType.date
                },
                {
                    name: 'description',
                    title: 'common.description',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-3',
                },
                {
                    name: '',
                    title: '',
                    allowSorting: false,
                    headerClass: 'text-center',
                    cellClass: 'tdcommand',
                    cellTemplate: '<button class="btn btn-info" id="details" ng-click="events.detailOrder(data);" ng-show="data.orderId" button-type="details"></button>'
                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {
                shopCurrency: $rootScope.shopCurrency,
                shopFractionSize: $rootScope.shopFractionSize,
            }
            $scope.dtEvents = {
                detailOrder: function (data) {
                    $scope.detailOrder(data.orderId);
                }
            }
            //#endregion

            $scope.closeModal = function () {
                $modalInstance.dismiss('cancel');
            }

        }]);
