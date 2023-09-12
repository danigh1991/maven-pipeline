angular.module('emixApp.controllers').controller('useraccountlist_index',
    ['$scope', 'httpServices', 'PagerService', '$rootScope', 'shareFunc', 'sharedInfo', '$translate', '$route', '$timeout',
        function ($scope, httpServices, PagerService, $rootScope, shareFunc, sharedInfo, $translate, $route, $timeout) {
            'use strict';

            //#region props
            $scope.isAdminPanel = httpServices.getPanelType() === 'admin';
            //#endregion

            //#region accountType
            $scope.accountTypeIsCredit = $route.current.$$route.originalPath.includes('credit');
            $scope.accountTypeFilter = function (item) {
                var accountType = parseInt($scope.accountType, 10);
                return item.accountTypeId === accountType;
            }

            $scope.accountTypeChange = function (){
                if($scope.accountTypeIsCredit){
                    $scope.getAccountWrappersByUserNameAndTypeId();
                }else {
                    $scope.dtData = ($scope.result || []).filter(function (account) {
                        var accountType = parseInt($scope.accountType, 10);
                        return account.accountTypeId === accountType;
                    });
                }
            }

            $scope.getAllActiveAccountTypes = function (searchAction) {
                searchAction=searchAction===true;

                if(!searchAction) {
                    NProgress.start();
                    $.blockUI();
                }

                httpServices.getAllActiveAccountTypes()
                    .then(function (response) {
                        var data = response.data;
                        $scope.allActiveAccountTypes = data.returnObject;
                        if($scope.accountTypeIsCredit){
                            $scope.allActiveAccountTypes = $scope.allActiveAccountTypes.filter(function (accountType){
                                return accountType.name === 'CREDIT_ORGANIZATION';
                            });
                        }
                        $scope.accountType = $scope.allActiveAccountTypes[0].id;
                        if(searchAction) {
                            if ($scope.isAdminPanel)
                                $scope.search();
                            else
                                $scope.getMyAccountWrappersByTypeId();
                        }

                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        if(searchAction) {
                            NProgress.done();
                            $.unblockUI();
                        }
                    })
                    .finally(function () {
                        if(!searchAction) {
                            NProgress.done();
                            $.unblockUI();
                        }
                    });
            }

            $scope.getAllActiveAccountTypes();
            //#endregion

            //#region user dropbox
            /*$scope.userInputOption = {
            }*/
            $scope.userEvents = {
                onSelected : function (item){
                    $scope.selectedUser = item;
                    $scope.search();
                }
            }
            $scope.userOptions = {
                id: 'dbxUserAccount',
                dataKeyField: 'username',
                saveState: true,
                saveStateKey: 'dbxUserAccountListUser'
            };
            $scope.selectedUserName = '';
            $scope.selectedUser = '';
            //#endregion

            //#region Utils
            $scope.afterRestoreSavedState = function (){
               /* $timeout(function () {
                    $scope.search();
                });*/
            }
            $scope.search = function (){
                $timeout(function () {
                    if ($scope.accountTypeIsCredit) {
                        $scope.getAccountWrappersByUserNameAndTypeId();
                    } else {
                        $scope.getAccountWrappersByUserName();
                    }
                }, 300);
            }

            $scope.addEdit = function (accountCredit) {
                var passObject = {
                    accountTypeId: $scope.accountType
                };

                if(accountCredit){
                    passObject.id = accountCredit.id;
                }else{
                    passObject.selectedUserName = angular.copy($scope.selectedUserName);
                    passObject.selectedUser = angular.copy($scope.selectedUser);
                }

                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/accounting/user_accounts/add.html', 'addaccountcredit_index', 'x-lg',
                    function (result) {
                        $scope.search();
                    }, function () {
                        //dismiss Dialog
                        if(sharedInfo.getNeedRefresh())
                            $scope.search();
                    });
            }
            $scope.disableAccount = function (data) {
                NProgress.start();
                $.blockUI();
                httpServices.disableAccount(data.id)
                    .then(function (response) {
                        toastr.success($translate.instant('common.successfullyDeletedMessage'));
                        $scope.search();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }
            //#endregion

            $scope.getAccountWrappersByUserName = function () {

                if(!$scope.selectedUserName){
                    //toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('common.userName')}));
                    $scope.dtData = [];
                    return;
                }

                //$scope.accountType = 1;
                $scope.accountType = $scope.allActiveAccountTypes && $scope.allActiveAccountTypes[0] ? $scope.allActiveAccountTypes[0].id : null;

                NProgress.start();
                $.blockUI();

                httpServices.getAccountWrappersByUserName($scope.selectedUserName)
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

            $scope.getAccountWrappersByUserNameAndTypeId = function () {
                //if(!$scope.userInputOption.validate){
                if(!$scope.selectedUserName){
                    //toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('common.userName')}));
                    $scope.dtData = [];
                    return;
                }

                NProgress.start();
                $.blockUI();

                if(!$scope.accountType) {
                    $scope.getAllActiveAccountTypes(true);
                    return;
                }

                //httpServices.getAccountWrappersByUserNameAndTypeId($scope.userInputOption.userName, $scope.accountType)
                httpServices.getAccountWrappersByUserNameAndTypeId($scope.selectedUserName, $scope.accountType)
                    .then(function (response) {
                        var data = response.data;
                        $scope.dtData = data.returnObject;
                        //$scope.accountTypeChange();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.getMyAccountWrappersByTypeId = function () {

                NProgress.start();
                $.blockUI();

                if(!$scope.accountType) {
                    $scope.getAllActiveAccountTypes(true);
                    return;
                }

                httpServices.getMyAccountWrappersByTypeId($scope.accountType)
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

            if(!$scope.isAdminPanel){//For UserPanel
                $scope.getMyAccountWrappersByTypeId();
            }

            $scope.manualTransactionRequestList = function (accountWrapper) {
                var passObject = {accountId: accountWrapper.id};
                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/accounting/manual-transaction-request/index.html', 'manualtransactionsrequest_index', '2x-lg',
                    function (result) {
                    }, function () {
                    });
            }

            $scope.accountStatement = function (accountWrapper) {
                var passObject = {accountId: accountWrapper.id};
                shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/accounting/user_accounts/statements.html', 'accountstatements_index', '2x-lg',
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
                    headerClass: 'text-center tdmini'
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
                    headerClass: 'text-center col-md-2',
                    cellTemplate: '<span>{{data.availableBalance | currency:"":additionalData.shopFractionSize}} <small>{{additionalData.shopCurrency}}</small></span>'
                },
                {
                    name: 'block',
                    title: 'accounting.blocked',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                    cellTemplate: '<span>{{data.block | currency:"":additionalData.shopFractionSize}} <small>{{additionalData.shopCurrency}}</small></span>'
                },
                {
                    name: 'createDate',
                    title: 'common.createDate',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-2',
                },
                /*{
                    name: 'statusDesc',
                    title: 'common.status',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1',
                    cellTemplate: '<div class="margin-bottom-sm">{{data.statusDesc}}</div>' +
                        '<span class="label label-success" ng-show="data.main">{{"accounting.accountType.personal" | translate}}</span>'
                },*/
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
                    headerClass: 'text-center tdmini',
                    cellClass: 'tdcommand',
                    cellTemplate:
                        '    <div class="btn-group" ng-if="additionalData.accountTypeIsCredit">\n' +
                        '        <button type="button" class="btn btn-info btn-rounded dropdown-toggle" data-toggle="dropdown" aria-expanded="false">{{\'common.action\' | translate}} <span class="caret"></span></button>\n' +
                        '        <div class="dropdown-menu panel-dropdown action-list" role="menu">\n' +
                        '           <button class="btn btn-warning" ng-if="additionalData.isAdminPanel" ng-click="events.edit(data);"\n' +
                        '              button-type="edit">\n' +
                        '           </button>\n' +
                        '            <button class="btn btn-warning" ng-if="additionalData.isAdminPanel" ng-click="events.manualTransactionRequestList(data);"\n' +
                        '               button-text="accounting.manualTransactionRequestList">\n' +
                        '            </button>\n' +
                        '           <a class="btn" ng-href="{{additionalData.accountCreditDetails + \'/\' + data.accountTypeId + \'/\' + data.id}}"\n' +
                        '               button-text="accounting.accountCreditList">\n' +
                        '           </a>' +
                        '            <a class="btn" \n' +
                        '               ng-href="{{additionalData.accountMerchantLimit + \'/\' + data.id}}" button-text="common.merchants"></a>\n' +
                        '            <button class="btn btn-danger" ng-if="additionalData.isAdminPanel" ng-click="events.delete(data);"\n' +
                        '               button-type="delete">\n' +
                        '            </button>\n' +
                        '        </div>\n' +
                        '    </div>\n' +
                        '    <button class="btn btn-warning" ng-click="events.accountStatement(data);"\n' +
                        '         button-text="accounting.statement">\n' +
                        '    </button>'
                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {
                shopCurrency: $rootScope.shopCurrency,
                shopFractionSize: $rootScope.shopFractionSize,
                accountTypeIsCredit: $scope.accountTypeIsCredit,
                accountCreditDetails: Emix.Pages.accountCreditDetails,
                accountMerchantLimit: Emix.Pages.accountMerchantLimit,
                isAdminPanel: $scope.isAdminPanel
            }
            $scope.dtEvents = {
                accountStatement: function (operationType) {
                    $scope.accountStatement(operationType);
                },
                delete: function (data) {
                    $scope.disableAccount(data);
                },
                edit: function (data) {
                    $scope.addEdit(data);
                },
                manualTransactionRequestList: function (data) {
                    $scope.manualTransactionRequestList(data);
                }
            }
            //#endregion

        }]);
