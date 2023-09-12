(function () {
    'use strict';
    var version = siteConfig.resourceVersion;

    angular.module('emixApp')
        .config(['$routeProvider', function ($routeProvider) {
            $routeProvider
                .when('/operations/accountoperations', {
                    controller: 'accountoperations_index',
                    templateUrl: '/nimdalenap/home/ngApp/pages/application/monitoring/operations/account/index.html' + version
                })
                .when('/customer/customerlist', {
                    controller: 'customerlist_index',
                    templateUrl: '/nimdalenap/home/ngApp/pages/application/accounting/merchant/customer/index.html' + version
                })
                .when('/accounting/dashboard', {
                    controller: 'accountingdashboard_index',
                    templateUrl: '/nimdalenap/home/ngApp/pages/application/accounting/dashboard/index.html' + version
                });
        }]);
}());