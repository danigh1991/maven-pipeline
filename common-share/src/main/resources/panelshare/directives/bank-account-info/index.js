angular.module('emixApp.directives').directive('ngProductFilter', ['sharedInfo', 'httpServices', '$rootScope', 'shareFunc', '$compile', '$timeout', '$translate', function (sharedInfo, httpServices, $rootScope, shareFunc, $compile, $timeout, $translate) {
    'use strict';
    var DEFAULT_SELECTABLE = true;

    return {
        scope: {
            options: '=',
            events: '='
        },
        templateUrl: '/panelshare/directives/bank-account-info/template.html' + $rootScope.version,
        link: function (scope, element, attrs) {
            /*#region Init Options*/
            scope.options.selectable = shareFunc.getBoolDefaultValue(scope.options.selectable, DEFAULT_SELECTABLE);
            /*#endregion*/

            /*#region Utils*/

            /*#endregion*/

            /*#region LoadData*/

            /*#endregion*/

            /*#region Local Events*/
            /*#endregion*/

            /*#region Events*/
            scope.events.getMyBankAccoutInfo = function(showLoader){
                showLoader = firstCall || false;

            };
            /*#endregion*/

        }
    }

}]);

