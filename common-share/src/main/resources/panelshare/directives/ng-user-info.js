angular.module('emixApp.directives')
    .directive('ngUserInfo', ['$compile', '$rootScope', 'httpServices', '$translate',
        function ($compile, $rootScope, httpServices, $translate) {
    'use strict';
    return {
        restrict: 'AE',
        replace: true,
        transclude: true,
        scope: {
            params: '='
        },
        template: function (element, attrs) {
                var html = '';
                html += '<button ng-click="showUserInfo()" class="btn btn-info' + (attrs.containerclass ? ' ' + attrs.containerclass : '') + '">{{params.title | translate}}</button>';
                return html;
        },
        link: function (scope, element, attrs) {

            //$compile(element.contents())(scope);

            //#region Properties
            scope.params.title = scope.params.title == undefined ? 'common.userInfo' : scope.params.title;
            /*#endregion*/

            /*#region Utilities*/
            /*#endregion*/

            /*#region event*/
            scope.showUserInfo = function () {
                /*NProgress.start();
                $.blockUI();

                httpServices.getUserInfo()
                    .then(function (response) {
                        var data = response.data;
                        scope.result = data.returnObject;
                        console.log(scope.result);
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });*/
                if(scope.params.userId)
                    httpServices.redirect(Emix.Pages.userProfile + '/' + scope.params.userId, true);
                else
                    toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('common.user')}));
            }
            /*#endregion*/

        }
    };
}]);
