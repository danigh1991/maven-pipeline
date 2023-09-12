angular.module('emixApp.controllers').controller('addrefund_index',
    ['$scope', 'httpServices', '$rootScope', 'sharedInfo', 'shareFunc', '$filter', '$translate', '$injector', 'passObject',
        function ($scope, httpServices, $rootScope, sharedInfo, shareFunc, $filter, $translate, $injector, passObject) {
            'use strict';

            //#region properties
            //if($injector.has('passObject')) {
            //    var passObject = $injector.get('passObject');
            $scope.accountId = passObject.accountId;
            //}else{
            //    $scope.accountId = 0;
            //}
            $scope.showError = false;
            //#endregion

            $scope.params = {};

            //#region Finance Config
            $scope.financeConfig = sharedInfo.getFinanceConfig();
            $scope.selectedBankAccount;

            $scope.getActiveFinanceDestNumbers = function () {
                NProgress.start();
                $.blockUI();
                httpServices.getActiveFinanceDestNumbers()
                    .then(function (response) {
                        var data = response.data;
                        $scope.activeFinanceDestNumbers = data.returnObject;
                        angular.forEach($scope.activeFinanceDestNumbers, function (activeFinanceDestNumber) {
                            switch (activeFinanceDestNumber.name) {
                                case 'account_number':
                                    $scope.financeConfig.showAccountNumber = true;
                                    break;
                                case 'cart_number':
                                    $scope.financeConfig.showCartNumber = true;
                                    if(activeFinanceDestNumber.mask)
                                        $scope.financeConfig.cartNumberMask = activeFinanceDestNumber.mask;
                                    break;
                                case 'iban_number':
                                    $scope.financeConfig.showIbanNumber = true;
                                    if(activeFinanceDestNumber.mask)
                                        $scope.financeConfig.ibanNumberMask = activeFinanceDestNumber.mask;
                                    break;
                                case 'swift_number':
                                    $scope.financeConfig.showSwiftNumber = true;
                                    if(activeFinanceDestNumber.mask)
                                        $scope.financeConfig.swiftNumberMask = activeFinanceDestNumber.mask;
                                    break;
                                case 'paypal_account':
                                    $scope.financeConfig.showPaypalAccount = true;
                                    break;
                            }
                        })
                        getAccountWrapperInfo();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }
            $scope.getActiveFinanceDestNumbers();
            //#endregion

            function getMyBankAccountInfo() {

                httpServices.getMyBankAccountInfo()
                    .then(function (response) {
                        var data = response.data;

                        $scope.myBankAccountInfo = data.returnObject;
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            function getAccountWrapperInfo() {

                httpServices.getAccountWrapperInfo($scope.accountId)
                    .then(function (response) {
                        var data = response.data;
                        $scope.balance = data.returnObject.balance;
                        $scope.block = data.returnObject.block;

                        $scope.params.accounts = [data.returnObject];

                        getMyBankAccountInfo();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }


            $scope.createUserRequestRefundMoney = function () {
                $scope.showError = true;

                if (!$scope.myForm.$valid) {
                    toastr.error($translate.instant('common.completeFormCarefully'));
                    return;
                }

                if(!$scope.selectedBankAccount){
                    toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('accounting.selectRefundBankAccount')}));
                    return;
                }

                if(!$scope.myBankAccountInfo[$scope.selectedBankAccount]){
                    toastr.error($translate.instant('validate.requiredMessage', {'element': $scope.selectedBankAccount}));
                    return;
                }

                NProgress.start();
                $.blockUI();

                $scope.cRequestRefundMoney = {
                    accountId: $scope.accountId,
                    reqAmount: $scope.reqAmount,
                    reqDesc: $scope.reqDesc,

                    bankAccountId: $scope.myBankAccountInfo.id,
                    financeDestName: $scope.selectedBankAccount,
                    financeDestValue: $scope.myBankAccountInfo[$scope.selectedBankAccount]
                }

                httpServices.createUserRequestRefundMoney($scope.cRequestRefundMoney)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullySavedMessage'));

                        if ($injector.has('$modalStack')) {
                            var modalStack = $injector.get('$modalStack')
                            var modalInstance = modalStack.getTop();
                            if(modalInstance && modalInstance.key){
                                modalInstance.key.dismiss('cancel');
                            }
                        }
                        httpServices.redirect(Emix.Pages.refundList);

                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            };

        }]);
