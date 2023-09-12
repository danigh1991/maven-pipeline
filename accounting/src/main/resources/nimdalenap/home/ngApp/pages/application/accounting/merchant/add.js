angular.module('emixApp.controllers').controller('addmerchant_index',
    ['$scope', 'httpServices', 'passObject', '$rootScope', '$filter', 'shareFunc', 'sharedInfo', '$modalInstance', '$translate', '$timeout', '$location',
        function ($scope, httpServices, passObject, $rootScope, $filter, shareFunc, sharedInfo, $modalInstance, $translate, $timeout, $location) {
            'use strict';

            $scope.showError = false;
            $scope.showBreadcrump = !passObject;

            //#region utils
            $scope.financeConfig = sharedInfo.getFinanceConfig();
            $scope.getMerchantViewPolicies = function () {

                NProgress.start();
                $.blockUI();

                httpServices.getMerchantViewPolicies()
                    .then(function (response) {
                        var data = response.data;
                        $scope.allMerchantViewPolicies = data.returnObject;
                        $scope.getAllMerchantCategories();
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }

            $scope.getAllMerchantCategories = function () {

                httpServices.getAllMerchantCategories()
                    .then(function (response) {
                        var data = response.data;
                        $scope.allMerchantCategories = data.returnObject;

                        $scope.allMerchantCategories.unshift({ id: null, name: '-------' });

                        if($scope.editMode)
                            $scope.getMerchantInfo();
                        else
                            $scope.getCityList(true, false);

                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }
            //#endregion

            // #region adress
            $scope.mapZoom = sharedInfo.getDefaultMapZoom();
            $scope.center = {lat: sharedInfo.getDefaultPosLat(), lng: sharedInfo.getDefaultPosLan(), zoom: $scope.mapZoom};

            //#region custom select
            $scope.getCityList = function (firstCall, showLoader) {
                firstCall = firstCall || false;
                showLoader = showLoader !== false;
                if(showLoader) {
                    NProgress.start();
                    $.blockUI();
                }
                httpServices.getCityList()
                    .then(function (response) {
                        var data = response.data;
                        $scope.cities = data.returnObject;
                        if(!$scope.editMode)
                            $scope.cityOptions.onSelect($scope.cities[0]);
                        else{
                            if(firstCall) {
                                var cityList = $scope.cities.filter(function (city) {
                                    return city.id === $scope.result.cityId;
                                });
                                var selectedCity = cityList.length > 0 ? cityList[0] : $scope.cities[0];
                                $scope.cityOptions.onSelect(selectedCity, firstCall, showLoader);
                            }
                        }
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        if(!showLoader) {
                            NProgress.done();
                            $.unblockUI();
                        }
                    })
                    .finally(function () {
                        if(showLoader) {
                            NProgress.done();
                            $.unblockUI();
                        }
                    });
            }
            $scope.cityOptions = {
                captionText: 'common.city',
                displayText: '',
                onSelect: function (item, firstCall, showLoader) {
                    $scope.selectedCity = item;
                    $scope.selectedRegion = null;
                    $scope.getCityRegionList(firstCall, showLoader);
                }
            };
            

            $scope.getCityRegionList = function (firstCall, showLoader) {
                firstCall = firstCall || false;
                showLoader = showLoader !== false;
                if(showLoader) {
                    NProgress.start();
                    $.blockUI();
                }
                if($scope.selectedCity) {
                    httpServices.getCityRegionList($scope.selectedCity.id)
                        .then(function (response) {
                            var data = response.data;
                            $scope.cityRegions = data.returnObject;

                            if($scope.editMode && firstCall){
                                var regionList = $scope.cityRegions.filter(function (region) {
                                    return region.id === $scope.result.regionId;
                                });
                                var selectedRegion = regionList.length > 0 ? regionList[0] : null;
                                $scope.regionOptions.onSelect(selectedRegion, firstCall);
                            }

                        }, function (response) {
                            httpServices.handleError(response.data, response.status, response.headers, response.config);
                            if(!showLoader) {
                                NProgress.done();
                                $.unblockUI();
                            }
                        })
                        .finally(function () {
                            if(showLoader) {
                                NProgress.done();
                                $.unblockUI();
                            }
                        });
                }else {
                    $scope.cityRegions = [];
                }
            }
            $scope.regionOptions = {
                captionText: 'common.region',
                displayText: '',
                onSelect: function (item, firstCall) {
                    firstCall = firstCall || false;
                    $scope.selectedRegion = item;
                    if(!firstCall && $scope.editMode)
                        changeMarker({lat: item.lat, lng: item.lan});
                }
            };
            //#endregion
            /*#region Leaflet*/
            $scope.markers = [];

            $scope.$on("leafletDirectiveMap.click", function(event, args){
                changeMarker(args.leafletEvent.latlng);
            });
            $scope.$on("leafletDirectiveMarker.dragend", function(event, args){
                changeMarker(args.leafletObject._latlng);
            });
            var changeMarker = function(latlng){
                $scope.address = '';
                $scope.setMarker(latlng.lat, latlng.lng);
            }
            $scope.setMarker = function(_lat, _lng){
                $scope.center = {lat: _lat, lng: _lng, zoom: $scope.mapZoom};
                $scope.markers[0]= {
                    lat: _lat,
                    lng: _lng,
                    //message: "",
                    draggable: true
                };
            }
            /*#endregion*/
            //#endregion

            //#region user dropbox
            $scope.dbxUserOptions = {
                id: 'dbxAddEditMerchantUser',
                dataKeyField: 'id',
                dataCodeField: 'username',
                dataTextField: 'firstName, lastName',
                label: 'common.user',
                additionalSortFieldName: 'id',
                apiUrl: Emix.Api.Users.getUsers,
                required: true,
                disabled: $scope.editMode,
                apiParams: {}
            }
            $scope.dbxUserCols = sharedInfo.getDbxUserCols();
            $scope.dbxUserEvents = {
                onSelected : function (item){
                    $scope.selectedUserName = item !== null ? item.username : null;
                    $scope.selectedUserId = item !== null ? item.id : null;
                    $scope.selectedUser = item;

                    if($scope.selectedUser != null && !$scope.editMode){
                        $scope.getMerchantInfoByUserId($scope.selectedUserId);
                    }
                }
            }
            //#endregion

            var resetResult = function (){
                $scope.result = {
                    id: null,
                    userId: null,
                    name: '',
                    description: '',
                    address: '',
                    lat: null,
                    lan: null,
                    postalCode: '',
                    email: '',
                    mobileNumber: '',
                    phoneNumber: '',
                    cityId: null,
                    regionId: null,
                    merchantCategoryId: null,
                    otherMerchantViewPolicy: 1,
                    wallet: false,
                    card: false,
                    cardNumber: null
                }
            }
            var bindResult = function (){
                $scope.result = {
                    id: $scope.merchantInfo.id,
                    userId: $scope.merchantInfo.userId,
                    name: $scope.merchantInfo.name,
                    description: $scope.merchantInfo.description,
                    address: $scope.merchantInfo.address,
                    lat: $scope.merchantInfo.lat,
                    lan: $scope.merchantInfo.lan,
                    postalCode: $scope.merchantInfo.postalCode,
                    email: $scope.merchantInfo.email,
                    mobileNumber: $scope.merchantInfo.mobileNumber,
                    phoneNumber: $scope.merchantInfo.phoneNumber,
                    cityId: $scope.merchantInfo.cityId,
                    regionId: $scope.merchantInfo.regionId,
                    active: $scope.merchantInfo.active,
                    merchantCategoryId: $scope.merchantInfo.merchantCategoryId,
                    otherMerchantViewPolicy: $scope.merchantInfo.otherMerchantViewPolicy,
                    wallet: $scope.merchantInfo.wallet,
                    card: $scope.merchantInfo.card,
                    cardNumber: $scope.merchantInfo.cardNumber,
                }

                $scope.selectedUserId = $scope.merchantInfo.userId;

                $scope.setMarker($scope.merchantInfo.lat, $scope.merchantInfo.lan);

                $scope.getCityList(true, false);

                $scope.dbxUserOptions.disabled = true;
            }

            $scope.getMerchantInfo = function () {

                httpServices.getMerchantInfo(passObject.id)
                    .then(function (response) {
                        var data = response.data;
                        $scope.merchantInfo = data.returnObject;

                        bindResult();

                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }

            $scope.getMerchantInfoByUserId = function (userId) {

                httpServices.getMerchantInfoByUserId(userId)
                    .then(function (response) {
                        var data = response.data;
                        $scope.merchantInfo = data.returnObject;

                        $scope.editMode = true;
                        bindResult();

                    }, function (response) {
                        if(response.status !== 400 && response.status !== 404) {
                            httpServices.handleError(response.data, response.status, response.headers, response.config);
                        }

                        NProgress.done();
                        $.unblockUI();
                    })
                    .finally(function () {

                    });
            }

            if(passObject && passObject.id){
                $scope.editMode = true;
            }
            else{
                resetResult();
            }
            $scope.getMerchantViewPolicies();

            $scope.ok = function () {
                $scope.addEditMerchant();
            }

            $scope.addEditMerchant = function () {

                $scope.showError = true;

                if (!$scope.myForm.$valid || $scope.selectedCity === null || $scope.selectedRegion === null) {
                    toastr.error($translate.instant('common.completeFormCarefully'));
                    return;
                }

                if (!$scope.result.card && !$scope.result.wallet) {
                    toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('wallet.allowedPaymentGateways')}));
                    return;
                }

                if ($scope.result.card && !$scope.result.cardNumber) {
                    toastr.error($translate.instant('validate.requiredMessage', {'element': $translate.instant('accounting.cardNumber')}));
                    return;
                }
                if(!$scope.result.card)
                    $scope.result.cardNumber = null;

                $scope.result.lat = $scope.center.lat;
                $scope.result.lan = $scope.center.lng;
                $scope.result.userId = $scope.selectedUserId;
                $scope.result.cityId = $scope.selectedCity.id;
                $scope.result.regionId = $scope.selectedRegion.id;

                NProgress.start();
                $.blockUI();
                httpServices.addEditMerchant($scope.result)
                    .then(function (response) {
                        var data = response.data;
                        sharedInfo.setNeedRefresh(true);
                        $scope.editMode = true;
                        $scope.merchantInfo = data.returnObject;
                        setTimeout(function (){ bindResult(); },0);
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.changeMerchantState = function (){
                NProgress.start();
                $.blockUI();
                httpServices.changeMerchantState($scope.result.id)
                    .then(function (response) {
                        var data = response.data;
                        sharedInfo.setNeedRefresh(true);
                        $scope.result.active = data.returnObject;
                        toastr.success($translate.instant('common.successfullySavedMessage'));
                    }, function (response) {
                        httpServices.handleError(response.data, response.status, response.headers, response.config);
                    })
                    .finally(function () {
                        NProgress.done();
                        $.unblockUI();
                    });
            }

            $scope.closeModal = function() {
                $modalInstance.dismiss('cancel');
            }

        }]);
