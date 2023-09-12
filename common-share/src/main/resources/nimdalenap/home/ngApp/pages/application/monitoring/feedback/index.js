angular.module('emixApp.controllers').controller('feedbacklist_index',
    ['$scope', 'httpServices', '$filter', '$modal', 'shareFunc', 'sharedInfo', '$translate',
        function ($scope, httpServices, $filter, $modal, shareFunc, sharedInfo, $translate) {
            'use strict';

            function getAllFeedbackList() {
                NProgress.start();
                $.blockUI();
                httpServices.getAllFeedbackList()
                    .then(function (response) {
                        var data = response.data;
                        $scope.dtData = data.returnObject.result;
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            getAllFeedbackList();

            $scope.removeFeedback = function (id) {
                NProgress.start();
                $.blockUI();

                httpServices.removeFeedback(id)
                    .then(function (response) {
                        var data = response.data;
                        toastr.success($translate.instant('common.successfullyDeletedMessage'));
                        getAllFeedbackList();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            //#region Feedbacks Table
            $scope.dtOptions = {
                id: 'dtFeedbacks',
                saveState: true,
                allowFilter: true,
                additionalSortFieldName: 'id'
            }
            $scope.dtCols = [
                {
                    name: 'id',
                    title: 'common.id',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-1'
                },
                {
                    name: 'comment',
                    title: 'common.feedback',
                    allowSorting: true,
                    cellClass: 'text-center',
                    headerClass: 'text-center col-md-9'
                },
                {
                    name: '',
                    title: '',
                    allowSorting: false,
                    headerClass: 'text-center col-md-2',
                    cellClass: 'tdcommand',
                    cellTemplate:
                        '   <button class="btn btn-danger" deletebutton \n' +
                        '      ng-click="events.removeFeedback(data.id);" button-type="delete">\n' +
                        '   </button>\n'
                }
            ];
            $scope.dtData = [];
            $scope.dtAdditionalData = {}
            $scope.dtEvents = {
                removeFeedback: function (id) {
                    $scope.removeFeedback(id);
                }
            }
            //#endregion

            $scope.closeModal = function () {
                $modalInstance.dismiss('cancel');
            }

        }]);
