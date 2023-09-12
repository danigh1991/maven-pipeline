(function () {
    'use strict';
    var version = siteConfig.resourceVersion;

    angular.module('emixApp')
        .config(['$routeProvider', function ($routeProvider) {
            $routeProvider
                .when('/customer/customerlist', {
                    controller: 'customerlist_index',
                    templateUrl: '/nimdalenap/home/ngApp/pages/application/accounting/merchant/customer/index.html' + version
                })
                //#region user credit account
                .when('/accounting/useraccountcreditlist', {
                    controller: 'useraccountlist_index',
                    templateUrl: '/nimdalenap/home/ngApp/pages/application/accounting/user_accounts/index.html' + version
                })
                .when('/accounting/accountcreditdetails/:accountTypeId/:accountId', {
                    controller: 'accountcreditdetails_index',
                    templateUrl: '/nimdalenap/home/ngApp/pages/application/accounting/user_account_credit/index.html' + version
                })
                .when('/accounting/accountcreditdetail/:id?', {
                    controller: 'addaccountcreditdetail_index',
                    templateUrl: '/nimdalenap/home/ngApp/pages/application/accounting/user_account_credit/add.html' + version
                })
                //#endregion

                //#region user credit account policy
                .when('/accounting/accountcreditdetailpolicy/:id', {
                    controller: 'useraccountpolicycreditdetail_index',
                    templateUrl: '/nimdalenap/home/ngApp/pages/application/accounting/user_account_credit/user-account-policy.html' + version
                })
                //#endregion

                //#region user credit account policy
                .when('/accounting/accountprofilepolicy', {
                    controller: 'accountpolicyprofilelist_index',
                    templateUrl: '/nimdalenap/home/ngApp/pages/application/accounting/account-policy-profile/index.html' + version
                })
                //#endregion

                //#region account merchant limit
                .when('/accounting/accountmerchantlimit/:id', {
                    controller: 'useraccountpolicycreditdetail_index',
                    templateUrl: '/nimdalenap/home/ngApp/pages/application/accounting/user_account_credit/user-account-policy.html' + version
                })
                //#endregion

                // #region account credit merchant limit
                .when('/accounting/accountcreditmerchantlimit/:id', {
                    controller: 'useraccountpolicycreditdetail_index',
                    templateUrl: '/nimdalenap/home/ngApp/pages/application/accounting/user_account_credit/user-account-policy.html' + version
                });
                //#endregion
        }]);
}());