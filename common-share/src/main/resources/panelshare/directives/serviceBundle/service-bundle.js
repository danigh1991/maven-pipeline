angular.module('emixApp.directives')
    .directive('ngServiceBundle', ['sharedInfo', 'httpServices', '$rootScope', 'shareFunc', '$compile', '$timeout', '$translate',
        function (sharedInfo, httpServices, $rootScope, shareFunc, $compile, $timeout, $translate) {
            'use strict';
            var DEFAULT_VISIBLE = true;
            var DEFAULT_READONLY = false;
            var DEFAULT_SHOW_PRICE_DETAIL = false;
            var DEFAULT_SHOW_PAYABLE_DETAIL = true;
            var DEFAULT_PAYABLE_AMOUNT = 0;
            //var DEFAULT_SELECTED_SERVICE_BUNDLE_ID = 0;

            return {
                scope: {
                    options: '=',
                    events: '='
                },
                templateUrl: '/panelshare/directives/serviceBundle/template.html' + $rootScope.version,
                link: function (scope, element, attrs) {

                    /*#region Init Options*/
                    scope.options.visible = shareFunc.getBoolDefaultValue(scope.options.visible, DEFAULT_VISIBLE);
                    scope.options.readonly = shareFunc.getBoolDefaultValue(scope.options.readonly, DEFAULT_READONLY);
                    scope.options.showPriceDetail = shareFunc.getBoolDefaultValue(scope.options.showPriceDetail, DEFAULT_SHOW_PRICE_DETAIL);
                    scope.options.showPayableDetail = shareFunc.getBoolDefaultValue(scope.options.showPayableDetail, DEFAULT_SHOW_PAYABLE_DETAIL);
                    scope.options.payableAmount = scope.options.payableAmount ? scope.options.payableAmount : DEFAULT_PAYABLE_AMOUNT;
                    //scope.options.selectedServiceBundleId = scope.options.selectedServiceBundleId ? scope.options.selectedServiceBundleId : DEFAULT_SELECTED_SERVICE_BUNDLE_ID;
                    scope.shopCurrency = $rootScope.shopCurrency;
                    scope.shopFractionSize = $rootScope.shopFractionSize;
                    scope.minFinalAmount = 10000;//For Bank Payment Policy
                    scope.bankGetways = sharedInfo.getBankGetways();
                    scope.selectedBank = scope.bankGetways[0].id;
                    scope.serviceBundleList = sharedInfo.getServiceBundleList();
                    scope.options.selectedServiceBundle = scope.serviceBundleList[1];
                    /*#endregion*/

                    /*#region Utils*/
                    scope.calcPayableAmount = function(){
                        if(scope.accountInfo) {
                            scope.options.payableAmount = scope.options.selectedServiceBundle.price - scope.accountInfo.balance;
                            scope.options.payableAmount = scope.options.payableAmount < 0 ? 0 : scope.options.payableAmount;
                            scope.options.payableAmount = scope.options.payableAmount < scope.minFinalAmount && scope.options.payableAmount > 0 ? scope.minFinalAmount : scope.options.payableAmount;
                        }
                    }
                    scope.serviceBundleClick = function(event, force){
                        force = force === true;
                        if(!force && scope.options.readonly) return;

                        var $currentTarget = $(event.currentTarget);
                        var pos = $currentTarget.index() + 2;
                        $(".prc tr").find('td:not(:eq(0))').hide();
                        $('.prc td:nth-child(' + pos + ')').css('display', 'table-cell');
                        $(".prc tr").find('th:not(:eq(0))').hide();
                        $('.prc ul li').removeClass('active');
                        $currentTarget.addClass('active');

                        var serviceBundleId = $currentTarget.data('sbndlid');
                        scope.events.setSelectedServiceBundle(serviceBundleId, true);
                    }
                    var mediaQuery = window.matchMedia('(min-width: 600px)');
                    mediaQuery.addListener(doSomething);

                    function doSomething(mediaQuery) {
                        if (mediaQuery.matches) {
                            $('.prc .sep').attr('colspan', 5);
                            $('.prc td:nth-child(3)').css('display', 'table-cell');
                        } else {
                            $('.prc .sep').attr('colspan', 2);
                        }
                        //if(!scope.options.readonly)
                        scope.serviceBundleClick({currentTarget : $($('.prc ul li')[1])}, true);
                    }
                    /*#endregion*/

                    /*#region events*/

                    /*#region Account*/
                    scope.chargeWallet = function () {

                        var chargeWalletData = {
                            gatewayId: scope.selectedBank,
                            amount: parseFloat(scope.options.payableAmount)
                        };
                        NProgress.start();
                        $.blockUI();
                        httpServices.chargeAccountAsBankPayment(chargeWalletData)
                            .then(function (response) {
                                var data = response.data;
                                httpServices.redirectToIpg(data.returnObject.bankInfo, data.returnObject.message, 'frmBankPayment');
                            }, function (response) {
                                httpServices.handleError(response.data, response.status, response.headers, response.config);
                            })
                            .finally(function () {
                                NProgress.done();
                                $.unblockUI();
                            });
                    }

                    scope.params = {};

                    function getMyMainAccountInfo() {
                        NProgress.start();
                        $.blockUI();
                        httpServices.getMyMainAccountInfo()
                            //.success(function (data, status, headers, config) {
                            .then(function (response) {
                                var data = response.data;
                                scope.accountInfo = data.returnObject;
                                scope.params.accounts = [scope.accountInfo];
                                scope.calcPayableAmount();

                                scope.onAccountInfoLoaded && scope.onAccountInfoLoaded(scope.accountInfo);
                            }, function (response) {
                                httpServices.handleError(response.data, response.status, response.headers, response.config);
                            })
                            .finally(function () {
                                NProgress.done();
                                $.unblockUI();
                            });
                    }
                    /*#endregion*/
                    /*#endregion*/

                    /*#region Events*/
                    /*scope.events.applyFilter = function(firstCall){
                    };*/
                    scope.events.init = function () {
                        //$timeout(function() {
                        doSomething(mediaQuery);
                        //},10);
                    }
                    scope.events.getMyMainAccountInfo = function () {
                        getMyMainAccountInfo();
                    }
                    scope.events.setSelectedServiceBundle = function(serviceBundleId, localCall){
                        localCall = localCall == true;
                        $timeout(function() {
                            var _selectedServiceBundle = scope.serviceBundleList.filter(function (serviceBundle) {
                                return serviceBundle.id == parseInt(serviceBundleId, 10);
                            });
                            if(_selectedServiceBundle.length > 0) {
                                scope.options.selectedServiceBundle = _selectedServiceBundle[0];

                                //angular.element('.prcfooter input[data-sbndlid="' + serviceBundleId + '"]').triggerHandler('click');
                                if(!localCall)
                                    scope.serviceBundleClick({currentTarget : $($('.prc ul li[data-sbndlid="' + serviceBundleId + '"]')[0])}, true);
                                /*$('.prcfooter input[data-sbndlid="' + serviceBundleId + '"]').click();*/
                                scope.calcPayableAmount();
                            }else{
                                toastr.error("پکیج مورد نظر یافت نشد.");
                            }
                        },100);
                    }
                    /*scope.events.getPayableAmount = function () {
                        return scope.options.payableAmount;
                    }*/
                    /*#endregion*/
                }
            }

        }]);

