angular.module('emixApp.controllers').controller('siteusers_index',
    ['$rootScope', '$scope', 'httpServices', 'shareFunc', 'sharedInfo', function ($rootScope, $scope, httpServices, shareFunc, sharedInfo) {
        'use strict';

        $scope.avatarUrl = Emix.Api.Account.image;// + '/u/';
        $scope.userProfileUrl = Emix.Pages.userProfile;
        function getAllUser() {
            NProgress.start();
            $.blockUI();

            httpServices.getAllUser()
             .then(function (response) {
                 var data = response.data;
                 $scope.dtData = data.returnObject;
             }, function (response) {
                 httpServices.handleError(response.data, response.status, response.headers, response.config);
            })
            .finally(function () {
                NProgress.done();
                $.unblockUI();
            });
        }

        getAllUser();

        $scope.userDeviceList = function (userId) {
            var passObject = {userId: userId};
            shareFunc.openModal(passObject, '/nimdalenap/home/ngApp/pages/users/device-info-list.html', 'deviceinfolist_index', 'x-lg',
                function (result) {
                }, function () {
                    //dismiss Dialog
                });
        }

        //#region OperationType Table
        $scope.dtOptions = {
            id: 'dtUserList',
            saveState: true,
            allowFilter: true,
            additionalSortFieldName: 'id',
            apiUrl: Emix.Api.Users.getUsers,
        }

        $scope.dtCols = [
            {
                name: 'id',
                title: 'common.id',
                allowSorting: true,
                cellClass: 'text-center',
                headerClass: 'text-center tdmini'
            },
            {
                name: 'logoPath',
                title: 'common.image',
                allowSorting: false,
                cellClass: 'text-center',
                headerClass: 'text-center col-md-1',
                filterable: false,
                cellTemplate: '<img ng-src="{{additionalData.avatarUrl + data.logoPath }}" width="70" height="70" class="imglogo userimage img-circle" alt="User Avatar"\n' +
                    '                             id="userAvatar"/>'
            },
            {
                name: 'username',
                title: 'common.userName',
                allowSorting: true,
                cellClass: 'text-center',
                headerClass: 'text-center col-md-1',
            },
            {
                name: 'firstName',
                title: 'common.name',
                allowSorting: true,
                cellClass: 'text-center',
                headerClass: 'text-center col-md-2',
                cellTemplate: '',
                formatValue: function (value){
                    return value || '--';
                },
                unFormatFilterText: function (value){
                    return value.replaceAll('--', '');
                }
            },
            {
                name: 'lastName',
                title: 'common.family',
                allowSorting: true,
                cellClass: 'text-center',
                headerClass: 'text-center col-md-2',
                cellTemplate: '',
                formatValue: function (value){
                    return value || '--';
                },
                unFormatFilterText: function (value){
                    return value.replaceAll('--', '');
                }
            },
            {
                name: 'mobileNumber',
                title: 'common.mobileNumber',
                allowSorting: true,
                cellClass: 'text-center',
                headerClass: 'text-center col-md-1',
            },
            {
                name: 'email',
                title: 'common.email',
                allowSorting: true,
                cellClass: 'text-center',
                headerClass: 'text-center col-md-2',
            },
            {
                name: 'genderCaption',
                keyName: 'gender',
                title: 'common.gender',
                allowSorting: true,
                cellClass: 'text-center',
                headerClass: 'text-center col-md-1',
                dataType: sharedInfo.dataType.list,
                filterList: sharedInfo.getGendersList(),
                filterListKey: 'id',
                filterListValue: 'name',
            },
            {
                name: 'createDate',
                title: 'common.saveDate',
                allowSorting: true,
                cellClass: 'text-center',
                headerClass: 'text-center col-md-2',
                dataType: sharedInfo.dataType.date
            },
            {
                name: '',
                title: '',
                allowSorting: false,
                headerClass: 'text-center tdmini',
                cellClass: 'tdcommand',
                cellTemplate:
                    '<a class="btn btn-success" ng-href="{{additionalData.userProfileUrl + \'/\' + data.id}}" button-type="edit"></a>\n'+
                    '<button class="btn btn-warning" ng-click="events.userDeviceList(data.id);"\n' +
                    '    button-text="common.deviceInfoList">\n' +
                    '</button>'
            }
        ];

        $scope.dtData = [];

        $scope.dtAdditionalData = {
            avatarUrl: $scope.avatarUrl,
            userProfileUrl: $scope.userProfileUrl
        }

        $scope.dtEvents = {
            userDeviceList: function (userId) {
                $scope.userDeviceList(userId);
            },
        }
        //#endregion
      
    }]);
