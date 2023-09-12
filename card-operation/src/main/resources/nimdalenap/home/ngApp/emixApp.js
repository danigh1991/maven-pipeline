(function () {
    'use strict';
    var version = siteConfig.resourceVersion;

    angular.module('emixApp')
        .config(['$routeProvider', function ($routeProvider) {
            $routeProvider
                .when('/operations/cardoperations', {
                    controller: 'cardoperations_index',
                    templateUrl: '/nimdalenap/home/ngApp/pages/application/monitoring/operations/card/index.html' + version
                });
        }]);
}());