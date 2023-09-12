angular.module('emixApp.controllers').controller('commentlist_index',
    ['$scope', 'httpServices', 'sharedInfo', 'PagerService', '$filter', 'shareFunc', '$translate',
        function ($scope, httpServices, sharedInfo, PagerService, $filter, shareFunc, $translate) {
        'use strict';

        $scope.dsc_id = 0;
        $scope.pager = {};
        $scope.setPage = setPage;
        $scope.dummyItems = '';
        $scope.items = '';
        $scope.dummyItemstmp = '';

        $scope.stateList = [
            { id: 0, name: 'common.pending' },
            { id: 1, name: 'common.verify' },
            { id: 2, name: 'common.notVerify' }
        ];
        $scope.selectedState = $scope.stateList[0];

        $scope.targetTypeList = httpServices.getTargetTypeList();

        $scope.selectedTargetType = $scope.targetTypeList[0];

        $scope.getAllCommentsForCheck = function () {
            NProgress.start();
            $.blockUI();

            httpServices.getAllCommentsForCheck($scope.selectedTargetType.id, $scope.selectedState.id)
             .then(function (response) {
                 var data = response.data;
                 $scope.dummyItemstmp = $scope.dummyItems = data.returnObject;
                 $scope.setPage(1);

             }, function (response) {
                 httpServices.handleError(response.data, response.status, response.headers, response.config);
            })
            .finally(function () {
                NProgress.done();
                $.unblockUI();
            });
        }

        $scope.approvedOrRejectComment = function (_commentId,_approve) {

            NProgress.start();
            $.blockUI();

            httpServices.approvedOrRejectComment(_commentId, _approve)
                .then(function (response) {
                    var data = response.data;
                    toastr.success($translate.instant('common.successfullySavedMessage'));
                    $scope.getAllCommentsForCheck();
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
        }

        /*#region Modal*/
        $scope.editComment = function (sender, _cmnt_id, _cmnt_txt, _cmnt_aprv) {
            var passObject = {
                cmnt_id : _cmnt_id,
                cmnt_txt: _cmnt_txt,
                cmnt_aprv : _cmnt_aprv,
            };
            shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/application/monitoring/comment/edit.html', 'editcomment_index', null,
                function (result) {
                    if(result)
                        $scope.getAllCommentsForCheck();
                }, function () {
                    //dismiss Dialog
                    if(sharedInfo.getNeedRefresh())
                        $scope.getAllCommentsForCheck();
                });
        }

        $scope.deleteComment = function (sender, _commentId) {
            NProgress.start();
            $.blockUI();
            httpServices.deleteComment(_commentId)
                .then(function (response) {
                    toastr.success($translate.instant('common.successfullyDeletedMessage'));
                    $scope.getAllCommentsForCheck();
                }, function (response) {
                    httpServices.handleError(response.data, response.status, response.headers, response.config);
                })
                .finally(function () {
                    NProgress.done();
                    $.unblockUI();
                });
        }
        /*#endregion*/

        //#region Filter
        $scope.getCityList = function () {
            NProgress.start();
            $.blockUI();
            httpServices.getCityList()
             .then(function (response) {
                 var data = response.data;
                 $scope.cities = data.returnObject;
                 $scope.cityOptions.onSelect($scope.cities[0]);
                 //$scope.cities.unshift({ id: 0, name: 'همه شهر ها' });

                 $scope.getAllCommentsForCheck();
             }, function (response) {
                 httpServices.handleError(response.data, response.status, response.headers, response.config);
            })
            .finally(function () {
                NProgress.done();
                $.unblockUI();
            });
        }

        $scope.getCityList();

        $scope.cityOptions = {
            onSelect: function (item) {
                $scope.selectedCity = item;
                $scope.selectedRegion = null;
                $scope.citiesChange();
            }
        };
        $scope.getCityRegionList = function (cityId) {
            NProgress.start();
            $.blockUI();
            httpServices.getCityRegionList(cityId)
             .then(function (response) {
                 var data = response.data;
                 $scope.cityRegions = data.returnObject;
                 $scope.cityRegions.unshift({ id: undefined, name: 'همه محله ها' });
             }, function (response) {
                 httpServices.handleError(response.data, response.status, response.headers, response.config);
            })
            .finally(function () {
                NProgress.done();
                $.unblockUI();
            });
        }

        $scope.regionOptions = {
            onSelect: function (item) {
                $scope.center = [$scope.selectedRegion.lat, $scope.selectedRegion.lan];
                if ($scope.map != undefined)
                    $scope.map.setZoom(sharedInfo.getDefaultMapZoom());
            }
        };
        $scope.cityRegions = [];
        $scope.citiesChange = function () {
            $scope.getCityRegionList($scope.selectedCity.id);
        }
        //#endregion

        $scope.search = function () {
            $scope.dummyItems = $scope.dummyItemstmp;
            $scope.pager.totalPages = 1;
            $scope.dummyItems = $filter('filter')($scope.dummyItems, $scope.bzquery);
            setPage(1);
        }

        //#region Pager
        function setPage(page) {
            //if (page < 1 || page > $scope.pager.totalPages) {
            //    return;
            //}

            // get pager object from service
            $scope.pager = PagerService.GetPager($scope.dummyItems.length, page, 5);

            // get current page of items
            $scope.items = $scope.dummyItems.slice($scope.pager.startIndex, $scope.pager.endIndex + 1);
        }
        //#endregion

    }]);
