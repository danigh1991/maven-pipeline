angular.module('emixApp.controllers').controller('myaccountlist_index',
    ['$scope', 'httpServices', 'PagerService', '$rootScope', 'shareFunc', 'sharedInfo', '$translate', '$filter',
        function ($scope, httpServices, PagerService, $rootScope, shareFunc, sharedInfo, $translate, $filter) {
            'use strict';

            //#region Utils
            $scope.accountType = 1;
            $scope.accountTypeFilter = function (item) {
                var accountType = parseInt($scope.accountType, 10);
                return item.accountTypeId === accountType;
            };
            $scope.accountTypeChange = function (){
               // $scope.dtData = $scope.result.filter(function (account) {
                $scope.dtData = ($scope.result || []).filter(function (account) {
                    var accountType = parseInt($scope.accountType, 10);
                    return account.accountTypeId === accountType;
                });
            }

            $scope.getAllActiveAccountTypes = function () {
                NProgress.start();
                $.blockUI();

                httpServices.getAllActiveAccountTypes()
                    .then(function (response) {
                        var data = response.data;
                        $scope.allActiveAccountTypes = data.returnObject;
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }
            $scope.getAllActiveAccountTypes();
            //#endregion


            //#endregion

            $scope.getMyAccountWrappers = function () {

                NProgress.start();
                $.blockUI();

                httpServices.getMyAccountWrappers()
                    .then(function (response) {
                        var data = response.data;
                        $scope.result = data.returnObject;
                        $scope.accountTypeChange();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.getMyAccountWrappers();

            $scope.accountStatement = function (accountWrapper) {
                var passObject = {accountId: accountWrapper.id};
                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/accounting/user_accounts/statements.html', 'accountstatements_index', '2x-lg',
                    function (result) {

                    }, function () {
                        //dismiss Dialog

                    });
            }

            $scope.chargeWallet = function (accountWrapper) {
                var passObject = {accountId: accountWrapper.id};
                shareFunc.openModal(passObject, '/panel/home/ngApp/pages/application/accounting/financial/wallet-charge.html', 'wallet_charge_index', 'x-lg',
                    function (result) {

                    }, function () {
                        //dismiss Dialog

                    });
            }

            $scope.refund = function (accountWrapper) {
                var passObject = {accountId: accountWrapper.id};
                shareFunc.openModal(passObject, '/panel/home/ngApp/pages/application/accounting/refund/add.html', 'addrefund_index', '2x-lg',
                    function (result) {

                    }, function () {
                        //dismiss Dialog

                    });
            }

            //#region OperationType Table
            $scope.dtOptions = {
                id: 'dtUserAccounts',
                saveState: true,
                allowFilter: false,
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
                    name: 'name',
                    title: 'common.name',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-3'
                },
                {
                    name: 'availableBalance',
                    title: 'accounting.balance',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    cellTemplate: '<span>{{data.availableBalance | currency:"":additionalData.shopFractionSize}} <small>{{additionalData.shopCurrency}}</small></span>'
                },
                {
                    name: 'block',
                    title: 'accounting.blocked',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                    cellTemplate: '<span>{{data.block | currency:"":additionalData.shopFractionSize}} <small>{{additionalData.shopCurrency}}</small></span>'
                },
                {
                    name: 'createDate',
                    title: 'common.createDate',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                },
                {
                    name: 'statusDesc',
                    title: 'common.status',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center',
                },
                /*{
                    name: 'status',
                    title: 'common.status',
                    allowSorting: true,
                    headerClass: 'text-center col-md-1',
                    cellClass: 'text-center',
                    cellTemplate: ' <span class="label label-{{data.status === 1 ? \'success\' : \'danger\'}}">{{data.status === 1 ? (\'common.active\' | translate) : (\'common.inactive\' | translate)}}</span>'
                },*/
                {
                    name: '',
                    title: '',
                    allowSorting: false,
                    headerClass: 'text-center col-md-1',
                    cellClass: 'tdcommand',
                    cellTemplate:
                        '<div class="btn-group">\n' +
                        '    <button type="button" class="btn btn-info btn-rounded dropdown-toggle" data-toggle="dropdown" aria-expanded="false">{{\'common.action\' | translate}} <span class="caret"></span></button>\n' +
                        '    <div class="dropdown-menu panel-dropdown action-list" role="menu">\n' +
                        '        <button class="btn btn-warning" ng-click="events.accountStatement(data);"\n' +
                        '               button-text="accounting.statement">\n' +
                        '        </button>' +
                        '        <button class="btn btn-default" ng-click="events.chargeWallet(data)" button-text="order.chargeWallet">\n' +
                        '        </button>' +
                        '        <button class="btn btn-default" ng-click="events.refund(data)" button-text="accounting.refund">\n' +
                        '        </button>' +
                        '    </div>\n' +
                        '</div>'
                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {
                shopCurrency: $rootScope.shopCurrency,
                shopFractionSize: $rootScope.shopFractionSize,
                chargeWalletUrl: Emix.Pages.chargewallet,
                refundListUrl: Emix.Pages.refundList

            }
            $scope.dtEvents = {
                accountStatement: function (data) {
                    $scope.accountStatement(data);
                },
                chargeWallet: function (data) {
                    $scope.chargeWallet(data);
                },
                refund: function (data) {
                    $scope.refund(data);
                }
            }
            //#endregion

        }]);
