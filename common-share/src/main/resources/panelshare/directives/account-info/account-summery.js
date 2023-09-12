angular.module('emixApp.directives').directive('accountSummery', ['$rootScope', function ($rootScope) {
    'use strict';
    return {
        scope: {
            params: '=',
            events: '='
        },
        templateUrl: '/panelshare/directives/account-info/account-summery.html' + $rootScope.version,
        link: function (scope, element, attrs) {
            scope.shopCurrency = $rootScope.shopCurrency;
            scope.shopFractionSize = $rootScope.shopFractionSize;
            //scope.params.walletAccount = scope.params.accounts[0];

            scope.loadAccountTypeDetail = function (account) {
                scope.events && scope.events.loadAccountTypeDetail && scope.events.loadAccountTypeDetail(account);
            }
        }
    }
}]);

