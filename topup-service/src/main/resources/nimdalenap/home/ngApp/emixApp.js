(function () {
    'use strict';
    var version = siteConfig.resourceVersion;

    angular.module('emixApp')
        .config(['$routeProvider', function ($routeProvider) {
            $routeProvider
                .when('/operations/mobilecharge', {
                    controller: 'mobilechargeandinternet_index',
                    templateUrl: '/nimdalenap/home/ngApp/pages/application/monitoring/operations/mobile/index.html' + version
                })
                .when('/operations/internetpackage', {
                    controller: 'mobilechargeandinternet_index',
                    templateUrl: '/nimdalenap/home/ngApp/pages/application/monitoring/operations/mobile/index.html' + version
                });
        }]);
}());