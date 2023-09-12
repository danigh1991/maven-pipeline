angular.module('emixApp.controllers').controller('manualtransactionsrequest_index',
    ['$scope', 'httpServices', 'PagerService', '$rootScope', 'shareFunc', 'sharedInfo', '$translate', 'passObject', '$modalInstance',
        function ($scope, httpServices, PagerService, $rootScope, shareFunc, sharedInfo, $translate, passObject, $modalInstance) {
            'use strict';

            $scope.showBreadcrump = !passObject;
            $scope.accountId = passObject.accountId;

            $scope.addEdit = function (manualTransactionRequest) {
                var passObject = {
                    accountId: $scope.accountId,
                    accountTypeId: $scope.accountTypeId
                };
                if(manualTransactionRequest){
                    passObject.id = manualTransactionRequest.id;
                }
                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/accounting/manual-transaction-request/add.html', 'addmanualtransactionrequest_index', 'x-lg',
                    function (result) {
                        $scope.dtEvents.dataBind();
                    }, function () {
                        //dismiss Dialog
                        if(sharedInfo.getNeedRefresh())
                            $scope.dtEvents.dataBind();
                    });
            }

            $scope.removeManualTransactionRequest = function (data) {
                NProgress.start();
                $.blockUI();
                httpServices.removeManualTransactionRequest(data.id)
                    .then(function (response) {
                        toastr.success($translate.instant('common.successfullyDeletedMessage'));
                        $scope.dtEvents.dataBind();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            //#region ManualTransactionRequest Table
            $scope.dtOptions = {
                id: 'dtManualTransactionRequest',
                apiUrl: Emix.Api.Application.accounting.getManualTransactionRequestWrappers,
                apiParams: {
                    accountId: $scope.accountId
                },
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
                    headerClass: 'text-center tdmini',
                    dataType: sharedInfo.dataType.int,
                },
                {
                    name: 'userName',
                    title: 'common.userName',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1'
                },
                {
                    name: 'accountName',
                    title: 'accounting.accountName',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                },
                {
                    name: 'accountTypeDesc',
                    title: 'accounting.accountTypeName',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                },
                {
                    name: 'reference',
                    title: 'accounting.bankDepositReference',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                },
                {
                    name: 'referenceDate',
                    title: 'accounting.bankDepositDate',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    dataType: sharedInfo.dataType.date
                },
                {
                    name: 'amount',
                    title: 'common.amount',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                    dataType: sharedInfo.dataType.money,
                    cellTemplate: '<span>{{data.amount | currency:"":additionalData.shopFractionSize}} <small>{{additionalData.shopCurrency}}</small></span>'
                },
                {
                    name: 'statusDesc',
                    keyName: 'status',
                    title: 'common.status',
                    allowSorting: false,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                    dataType: sharedInfo.dataType.list,
                    filterList: Emix.Api.Application.accounting.getManualTransactionRequestStatuses,
                    filterListKey: 'id',
                    filterListValue: 'caption',
                },
                {
                    name: '',
                    title: '',
                    allowSorting: false,
                    headerClass: 'text-center',
                    cellClass: 'tdcommand',
                    cellTemplate: '<button class="btn btn-warning" ng-click="events.edit(data);"\n' +
                        '               button-text="common.edit">\n' +
                        '          </button>\n'+
                        '          <button class="btn btn-danger" deletebutton ng-disabled="data.status !== 0" \n' +
                        '               ng-click="events.delete(data);" button-type="delete">' +
                        '          </button>'
                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {
                shopCurrency: $rootScope.shopCurrency,
                shopFractionSize: $rootScope.shopFractionSize,
            }
            $scope.dtEvents = {
                edit: function (data) {
                    $scope.addEdit(data);
                },
                delete: function (data) {
                    $scope.removeManualTransactionRequest(data);
                }
            }
            //#endregion

        }]);
