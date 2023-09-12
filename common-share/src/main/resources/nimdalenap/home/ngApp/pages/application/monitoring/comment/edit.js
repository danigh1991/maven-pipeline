angular.module('emixApp.controllers').controller('editcomment_index',
    ['$scope', 'httpServices', 'sharedInfo', 'shareFunc', '$modalInstance', 'passObject', '$translate',
        function ($scope, httpServices, sharedInfo, shareFunc, $modalInstance, passObject, $translate) {
        'use strict';

        $scope.showBreadcrump = !passObject;
        $scope.showError = false;
        $scope.result = "";

        if(!passObject || !passObject.cmnt_id){
            toastr.error("صفحه به صورت نادرست فراخوانی شده است.");
            return;
        }

        $scope.commentId = passObject.cmnt_id || '';
        $scope.comment = passObject.cmnt_txt || '';
        $scope.commentApproved = passObject.cmnt_aprv || '';

        /*$scope.getCommentInfo = function () {
            NProgress.start();
            $.blockUI();

            httpServices.getCommentInfo($scope.str_id)
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
        $scope.getCommentInfo();*/

        $scope.editComment = function () {

            $scope.showError = true;

            if (!$scope.myForm.$valid) return;

            NProgress.start();
            $.blockUI();

            var commentData = {
                commentId: $scope.commentId,
                comment: $scope.comment,
            };

            httpServices.editComment(commentData)
                .then(function (response) {
                    var data = response.data;
                    toastr.success($translate.instant('common.successfullySavedMessage'));
                    sharedInfo.setNeedRefresh(true);
                    //setTimeout($scope.closeModal(),1000);
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });

        };

        $scope.approvedOrRejectComment = function (_approve) {

            NProgress.start();
            $.blockUI();

            httpServices.approvedOrRejectComment($scope.commentId, _approve)
                .then(function (response) {
                    var data = response.data;
                    toastr.success($translate.instant('common.successfullySavedMessage'));
                    $scope.commentApproved = _approve;
                    sharedInfo.setNeedRefresh(true);
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
        }

        $scope.closeModal = function(){
            $modalInstance.dismiss('cancel');
        }
    }]);

