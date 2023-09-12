angular.module('emixApp.controllers').controller('contactusreply_index',
    ['$scope', 'httpServices', '$routeParams', '$translate',
        function ($scope, httpServices, $routeParams, $translate) {
        'use strict';

        $scope.showError = false;
        $scope.sendSms = false;
        $scope.result = "";

        $scope.messageId = $routeParams.messageId;

        $scope.getContactUsInfo = function () {
            NProgress.start();
            $.blockUI();

            httpServices.getContactUsInfo($scope.messageId)
                .then(function (response) {
                    var data = response.data;
                    $scope.result = data.returnObject;
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
        }
        $scope.getContactUsInfo();

        $scope.replyMessage = function () {

            $scope.showError = true;

            if (!$scope.myForm.$valid) return;

            NProgress.start();
            $.blockUI();

            var replyData = {
                contactUsId: $scope.result.id,
                replyComment: $scope.reply,
                sendEmail: true,//$scope.sendEmail,
                sendSms: $scope.sendSms
            };

            httpServices.replyToContactUs(replyData)
                .then(function (response) {
                    toastr.success($translate.instant('common.successfullySavedMessage'));
                    httpServices.redirectWithDelay(Emix.Pages.contactUslist);                   
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
        };



    }]);
