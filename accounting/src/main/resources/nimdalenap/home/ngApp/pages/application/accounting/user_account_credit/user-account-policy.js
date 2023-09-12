angular.module('emixApp.controllers').controller('useraccountpolicycreditdetail_index',
    ['$scope', 'httpServices', '$rootScope', '$filter', 'shareFunc', 'sharedInfo', '$translate', '$routeParams', '$route', '$timeout',
        function ($scope, httpServices, $rootScope, $filter, shareFunc, sharedInfo, $translate, $routeParams, $route, $timeout) {
            'use strict';

            //#region props
            $scope.isAdminPanel = httpServices.getPanelType() === 'admin';

            $scope.showError = false;
            $scope.id = $routeParams.id;
            $scope.type = '1';
            $scope.formType =
                $route.current.$$route.originalPath.includes('accountcreditmerchantlimit') ? 'accountcreditmerchantlimit' :
                    $route.current.$$route.originalPath.includes('accountmerchantlimit') ? 'accountmerchantlimit' : 'accountcreditdetailpolicy';

            $scope.pageTitle = '';
            $scope.userType = 'merchant';
            if ($scope.formType === 'accountcreditmerchantlimit')
                $scope.pageTitle = 'محدودیت پذیرندگان الگوی اعتباری';
            else if ($scope.formType === 'accountmerchantlimit')
                $scope.pageTitle = 'محدودیت پذیرندگان اعتبار';
            else {
                $scope.pageTitle = 'اختصاص الگوی اعتباری به کاربران';
                $scope.userType = 'user';
            }
            $scope.isMerchant = $scope.userType === 'merchant';
            //#endregion

            //#region user dropbox
            var getDbxOptions = function (){
                if($scope.userType === 'user'){
                    if($scope.isAdminPanel){
                        return { //all users
                            id: 'dbxAddEditUser',
                            dataKeyField: 'id',
                            dataCodeField: 'username',
                            dataTextField: 'firstName, lastName',
                            label: 'common.user',
                            additionalSortFieldName: 'id',
                            apiUrl: Emix.Api.Users.getUsers,
                            apiParams: {},
                            readOnly: true
                        };
                    }else{ // only merchant customers
                        return {
                            id: 'dbxAddEditUser',
                            dataKeyField: 'customerUserId',
                            dataCodeField: 'maskCustomerUserName',
                            dataTextField: 'fullName',
                            label: 'common.customer',
                            additionalSortFieldName: 'customerUserId',
                            apiUrl: Emix.Api.Application.merchant.getMerchantCustomers,
                            apiParams: {},
                            readOnly: true
                        };
                    }
                }else{//merchant list
                    return {
                        id: 'dbxAddEditUser',
                        dataKeyField: 'userId',
                        dataCodeField:'userName',
                        dataTextField: 'name',
                        label: 'common.merchant',
                        additionalSortFieldName: 'id',
                        apiUrl: Emix.Api.Application.merchant.getAllMerchants,
                        apiParams: {},
                        readOnly: true
                    };
                }
            }
            $scope.dbxUserOptions = getDbxOptions();
            $scope.dbxUserCols = $scope.userType === 'user' ? $scope.isAdminPanel ? sharedInfo.getDbxUserCols() : sharedInfo.getDbxMerchantUserCols() : sharedInfo.getDbxMerchantCols();
            $scope.dbxUserEvents = {
                onSelected: function (item) {
                }
            }
            //#endregion

            //#region User Group Option
            $scope.getAllUserGroupsByUserId = function () {

                $scope.userId = $scope.isAccountCredit ? $scope.masterAccountInfo.account.userId : $scope.masterAccountInfo.userId;

                httpServices.getAllUserGroupsByUserId($scope.userId)
                    .then(function (response) {
                        var data = response.data;
                        $scope.userGroups = data.returnObject;
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.userGroupOptions = {
                captionText: 'group',
                displayText: '',
                onSelect: function (item) {
                    $scope.groupId = item.id;
                }
            };

            //#endregion

            //#region utils
            $scope.isAccountCredit = false;
            $scope.getMasterInfo = function () {
                if ($scope.formType === 'accountcreditmerchantlimit' || $scope.formType === 'accountcreditdetailpolicy') {
                    $scope.isAccountCredit = true;
                    $scope.getAccountCreditDetailInfo();
                } else if ($scope.formType === 'accountmerchantlimit')
                    $scope.getAccountWrapperInfo();

            }

            $scope.getInfo = function () {
                if ($scope.formType === 'accountcreditmerchantlimit')
                    $scope.getAccountCreditMerchantLimitWrappers();
                else if ($scope.formType === 'accountmerchantlimit')
                    $scope.getAccountMerchantLimitWrappers();
                else
                    $scope.getUserAccountPolicyCreditDetailWrappers();
            }
            $scope.add = function () {
                if ($scope.formType === 'accountcreditmerchantlimit')
                    $scope.addAccountCreditMerchantLimit();
                else if ($scope.formType === 'accountmerchantlimit')
                    $scope.addAccountMerchantLimit();
                else
                    $scope.addUserAccountPolicyCreditDetail();
            }
            $scope.delete = function (data) {
                if ($scope.formType === 'accountcreditmerchantlimit')
                    $scope.deleteAccountCreditMerchantLimit(data);
                else if ($scope.formType === 'accountmerchantlimit')
                    $scope.deleteAccountMerchantLimit(data);
                else
                    $scope.deleteUserAccountPolicyCreditDetail(data);
            }
            //#endregion

            $timeout(function () {
                $scope.getMasterInfo();
            });

            //#region Master Info
            $scope.getAccountWrapperInfo = function () {
                NProgress.start();
                $.blockUI();
                httpServices.getAccountWrapperInfo($scope.id)
                    .then(function (response) {
                        var data = response.data;
                        $scope.masterAccountInfo = data.returnObject;
                        $scope.getInfo();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {
                    });
            }

            $scope.getAccountCreditDetailInfo = function () {
                NProgress.start();
                $.blockUI();
                httpServices.getAccountCreditDetailInfo($scope.id)
                    .then(function (response) {
                        var data = response.data;
                        $scope.masterAccountInfo = data.returnObject;
                        $scope.getInfo();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {
                    });
            }
            //#endregion

            //#region UserAccountPolicyCredit
            $scope.getUserAccountPolicyCreditDetailWrappers = function () {
                httpServices.getUserAccountPolicyCreditDetailWrappers($scope.id)
                    .then(function (response) {
                        var data = response.data;
                        $scope.dtData = data.returnObject;
                        $scope.getAllUserGroupsByUserId();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {
                    });
            }

            $scope.getUserAccountPolicyCreditDetailInfo = function () {
                httpServices.getUserAccountPolicyCreditDetailInfo($scope.id)
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

            $scope.addUserAccountPolicyCreditDetail = function () {

                if ($scope.type === '1' && !($scope.selectedUserId && $scope.selectedUserId.toString().length > 0)) {
                    toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('common.userName')}));
                    return;
                }

                if ($scope.type === '2' && !$scope.groupId) {
                    toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('common.group')}));
                    return;
                }

                NProgress.start();
                $.blockUI();

                var obj = {
                    accountCreditDetailId: $scope.id
                };

                if ($scope.type === '1')
                    obj.userId = $scope.selectedUserId;

                if ($scope.type === '2')
                    obj.groupId = $scope.groupId;

                httpServices.addUserAccountPolicyCreditDetail(obj)
                    .then(function (response) {
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                        $scope.groupUserName = '';
                        $scope.dbxUserEvents.erase();
                        $scope.getInfo();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }

            $scope.deleteUserAccountPolicyCreditDetail = function (data) {
                NProgress.start();
                $.blockUI();
                httpServices.deleteUserAccountPolicyCreditDetail($scope.id, data.userId)
                    .then(function (response) {
                        toastr.success($translate.instant('common.successfullyDeletedMessage'));
                        $scope.getInfo();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {
                    });
            }
            //#endregion

            //#region AccountMerchantLimit
            $scope.getAccountMerchantLimitWrappers = function () {

                httpServices.getAccountMerchantLimitWrappers($scope.id)
                    .then(function (response) {
                        var data = response.data;
                        $scope.dtData = data.returnObject;
                        $scope.getAllUserGroupsByUserId();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {
                    });
            }

            $scope.addAccountMerchantLimit = function () {

                if ($scope.type === '1' && !($scope.selectedUserId && $scope.selectedUserId.toString().length > 0)) {
                    toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('common.userName')}));
                    return;
                }

                if ($scope.type === '2' && !$scope.groupId) {
                    toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('common.group')}));
                    return;
                }

                NProgress.start();
                $.blockUI();

                var obj = {
                    sourceId: $scope.id
                };

                if ($scope.type === '1')
                    obj.userId = $scope.selectedUserId;

                if ($scope.type === '2')
                    obj.groupId = $scope.groupId;

                httpServices.addAccountMerchantLimit(obj)
                    .then(function (response) {
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                        $scope.groupUserName = '';
                        $scope.dbxUserEvents.erase();
                        $scope.getInfo();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }

            $scope.deleteAccountMerchantLimit = function (data) {
                NProgress.start();
                $.blockUI();
                httpServices.deleteAccountMerchantLimit(data.id)
                    .then(function (response) {
                        toastr.success($translate.instant('common.successfullyDeletedMessage'));
                        $scope.getInfo();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {
                    });
            }

            //#endregion

            //#region AccountCreditMerchantLimit
            $scope.getAccountCreditMerchantLimitWrappers = function () {

                httpServices.getAccountCreditMerchantLimitWrappers($scope.id)
                    .then(function (response) {
                        var data = response.data;
                        $scope.dtData = data.returnObject;
                        $scope.getAllUserGroupsByUserId();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }

            $scope.getAccountCreditMerchantLimitWrapperInfo = function (data) {

                httpServices.getAccountCreditMerchantLimitWrapperInfo(data.id)
                    .then(function (response) {
                        var data = response.data;
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.addAccountCreditMerchantLimit = function () {

                if ($scope.type === '1' && !($scope.selectedUserId && $scope.selectedUserId.toString().length > 0)) {
                    toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('common.userName')}));
                    return;
                }

                if ($scope.type === '2' && !$scope.groupId) {
                    toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('common.group')}));
                    return;
                }

                NProgress.start();
                $.blockUI();

                var obj = {
                    sourceId: $scope.id
                };

                if ($scope.type === '1')
                    obj.userId = $scope.selectedUserId;

                if ($scope.type === '2')
                    obj.groupId = $scope.groupId;

                httpServices.addAccountCreditMerchantLimit(obj)
                    .then(function (response) {
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                        $scope.groupUserName = '';
                        $scope.dbxUserEvents.erase();
                        $scope.getInfo();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }

            $scope.deleteAccountCreditMerchantLimit = function (data) {
                NProgress.start();
                $.blockUI();
                httpServices.deleteAccountCreditMerchantLimit(data.id)
                    .then(function (response) {
                        toastr.success($translate.instant('common.successfullyDeletedMessage'));
                        $scope.getInfo();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {
                    });
            }
            //#endregion


            //#region Grid Option
            $scope.dtOptions = {
                id: 'dtUserAccountCreditLimitation',
                pageSize: 15,
                saveState: true,
                allowFilter: true,
                additionalSortFieldName: 'id',
                rowClass: 'col-md-3 col-sm-6 col-xs-12 pdr-0 pdl-5',
            }

            $scope.dtData = [];
            $scope.dtAdditionalData = {
                shopCurrency: $rootScope.shopCurrency,
                shopFractionSize: $rootScope.shopFractionSize,
                formType: $scope.formType,
                isMerchant: $scope.isMerchant
            }

            $scope.dtEvents = {
                delete: function (data) {
                    $scope.delete(data);
                },
            }
            //#endregion


        }]);
