angular.module('emixApp.controllers').controller('logout_index',
    ['$scope', 'httpServices', '$location', '$timeout', 'authService', function ($scope, httpServices, $location, $timeout, authService) {
        'use strict';        

        $scope.logout = function () {
            NProgress.start();
            $.blockUI();

            httpServices.logout()
                .then(function (response) {
                    var data = response.data;
                    authService.clearAllToken();
                    var siteLoginPanel = $('div.navbar-login-box');
                    if (siteLoginPanel.length > 0 && httpServices.getProjectType() === 'advertise') {
                        var cityName = $location.absUrl().split('/')[3];
                        httpServices.redirect('/' + cityName + Emix.Pages.sitePanelPage);
                    }
                    else
                    httpServices.redirect(Emix.Pages.login);

                    if(httpServices.getPanelType() === 'panel')
                        $timeout(function() {
                            window.location.reload();
                        },500);

                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
        };

        $scope.logout();

    }]);
